package com.samuel.mazetowers.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;

public class BlockExtraDoor extends BlockDoor {

	public final int type;

	public BlockExtraDoor(String unlocalizedName,
		Material material, float hardness,
		float resistance, int type) {
		super(material);
		this.setUnlocalizedName(unlocalizedName);
		this.setHardness(hardness);
		this.setResistance(resistance);
		this.type = type;
	}

	public BlockExtraDoor(String unlocalizedName,
		float hardness, float resistance, int type) {
		this(unlocalizedName, Material.rock, hardness,
			resistance, type);
	}

	@Override
	public int getLightValue(IBlockAccess world,
		BlockPos pos) {

		Block block = world.getBlockState(pos).getBlock();
		if (block != this) {
			return block.getLightValue(world, pos);
		}

		IBlockState state = world.getBlockState(pos);
		return type < 5 ? 0 : 10;
	}

	@Override
	public Item getItemDropped(IBlockState state,
		Random rand, int fortune) {
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null
			: this.getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return (this.type == 0) ? MazeTowers.ItemEndStoneDoor
			: this.type == 1 ? MazeTowers.ItemQuartzDoor
				: this.type == 2 ? MazeTowers.ItemObsidianDoor
					: MazeTowers.ItemBedrockDoor;
	}

	private Item getItem() {
		return (this.type == 0) ? MazeTowers.ItemEndStoneDoor
			: this.type == 1 ? MazeTowers.ItemQuartzDoor
				: this.type == 2 ? MazeTowers.ItemObsidianDoor
					: MazeTowers.ItemBedrockDoor;
	}

	@Override
	public boolean onBlockActivated(World worldIn,
		BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumFacing side, float hitX,
		float hitY, float hitZ) {
		if (worldIn.isRemote)
			return false;

		worldIn.playSoundAtEntity(playerIn,
			"mazetowers:door_locked", 1.0F, 1.0F);

		return false;
	}

	public void activateDoor(World worldIn, BlockPos pos,
		IBlockState state, EntityPlayer playerIn) {
		BlockPos blockpos1 = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos
			: pos.down();
		IBlockState iblockstate1 = pos.equals(blockpos1) ? state
			: worldIn.getBlockState(blockpos1);

		state = iblockstate1.cycleProperty(OPEN);
		worldIn.setBlockState(blockpos1, state, 2);
		worldIn.markBlockRangeForRenderUpdate(blockpos1,
			pos);
		worldIn.playSoundAtEntity(playerIn,
			!((Boolean) state.getValue(OPEN))
				.booleanValue() ? "random.door_close"
				: "random.door_open", 1.0F, 1.0F);
		if (((Boolean) state.getValue(OPEN)).booleanValue())
			worldIn.playSoundAtEntity(playerIn,
				"mazetowers:door_unlock", 1.0F, 1.0F);
	}

	/**
	 * Called when a neighboring block changes.
	 */
	@Override
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
		if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
			BlockPos blockpos = pos.down();
			IBlockState iblockstate = worldIn
				.getBlockState(blockpos);

			if (iblockstate.getBlock() != this) {
				worldIn.setBlockToAir(pos);
			} else if (neighborBlock != this) {
				this.onNeighborBlockChange(worldIn,
					blockpos, iblockstate, neighborBlock);
			}
		} else {
			boolean flag1 = false;
			BlockPos blockpos1 = pos.up();
			IBlockState iblockstate1 = worldIn
				.getBlockState(blockpos1);

			if (iblockstate1.getBlock() != this) {
				worldIn.setBlockToAir(pos);
				flag1 = true;
			}

			if (!World.doesBlockHaveSolidTopSurface(
				worldIn, pos.down())) {
				worldIn.setBlockToAir(pos);
				flag1 = true;

				if (iblockstate1.getBlock() == this) {
					worldIn.setBlockToAir(blockpos1);
				}
			}

			if (flag1) {
				if (!worldIn.isRemote) {
					this.dropBlockAsItem(worldIn, pos,
						state, 0);
				}
			} else {
				boolean flag = worldIn.isBlockPowered(pos)
					|| worldIn.isBlockPowered(blockpos1);
				IBlockState scannerFrontState = getScannerFrontState(
					worldIn, pos, state
						.getValue(BlockExtraDoor.FACING),
					true);
				IBlockState scannerBackState = getScannerBackState(
					worldIn, pos, state
						.getValue(BlockExtraDoor.FACING),
					true);
				EnumFacing enumfacing = (EnumFacing) state
					.getValue(FACING);
				boolean hasScannerFront = scannerFrontState
					.getBlock() instanceof BlockItemScanner;
				boolean hasScannerBack = scannerBackState
					.getBlock() instanceof BlockItemScanner;
				int scannerFrontStateId = hasScannerFront ? scannerFrontState
					.getValue(BlockItemScanner.STATE)
					: -1;
				int scannerBackStateId = hasScannerBack ? scannerBackState
					.getValue(BlockItemScanner.STATE)
					: -1;
				boolean isScannerPowered = scannerFrontStateId == 3
					|| scannerBackStateId == 3;
				boolean useScanner = false;
				if (((!flag || ((useScanner = neighborBlock instanceof BlockItemScanner)
					|| !hasScannerFront || !hasScannerBack)
					&& neighborBlock.canProvidePower()))
					&& neighborBlock != this) {
					boolean isBackPowered = getIsPowered(
						worldIn, pos, enumfacing,
						hasScannerBack,
						scannerBackStateId == 3, true);
					boolean isFrontPowered = getIsPowered(
						worldIn, pos, enumfacing
							.getOpposite(),
						hasScannerFront,
						scannerFrontStateId == 3, true);
					boolean isPowered = isFrontPowered
						|| isBackPowered;
					boolean isOpen = ((Boolean) state
						.getValue(OPEN)).booleanValue();

					worldIn
						.setBlockState(
							blockpos1,
							iblockstate1
								.withProperty(
									POWERED,
									(isFrontPowered || isBackPowered)
										&& flag), 2);

					if (((isOpen && (!flag || !isPowered)) || (!isOpen
						&& (isScannerPowered
							|| (isFrontPowered && !hasScannerFront) || (isBackPowered && !hasScannerBack)) && flag))) {
						worldIn
							.setBlockState(
								pos,
								state
									.withProperty(
										OPEN,
										Boolean
											.valueOf((isScannerPowered || isPowered)
												&& flag)),
								2);
						worldIn
							.markBlockRangeForRenderUpdate(
								pos, pos);
						worldIn.playAuxSFXAtEntity(
							(EntityPlayer) null,
							isScannerPowered
								|| isFrontPowered
								|| isBackPowered ? 1003
								: 1006, pos, 0);
					}
				}
			}
		}
	}

	private boolean getIsPowered(World worldIn,
		BlockPos bottomPos, EnumFacing facing,
		boolean hasScanner, boolean scannerActivated,
		boolean isBack) {
		boolean isPowered = false;
		BlockPos pos;
		for (int c = 0; c < (isBack ? 2 : 1) && !isPowered; c++) {
			pos = bottomPos.offset(facing, c);
			isPowered = (worldIn.getRedstonePower(pos,
				facing) != 0
				|| worldIn.getRedstonePower(pos.down(),
					facing) != 0
				|| worldIn.getRedstonePower(pos
					.offset(facing.rotateY()), facing) != 0
				|| worldIn.getRedstonePower(pos
					.offset(facing.rotateYCCW()), facing) != 0
				|| worldIn.getRedstonePower(pos = pos.up(),
					facing) != 0
				|| worldIn.getRedstonePower(pos.up(),
					facing) != 0
				|| worldIn.getRedstonePower(pos
					.offset(facing.rotateY()), facing) != 0 || worldIn
				.getRedstonePower(pos.offset(facing
					.rotateYCCW()), facing) != 0)
				&& (!hasScanner || scannerActivated) ? true
				: false;
		}
		return isPowered;
	}

	private IBlockState getScannerBackState(World worldIn,
		BlockPos pos, EnumFacing facing, boolean isBottom) {
		BlockPos backPos = pos.offset(facing);
		IBlockState state;
		boolean hasScanner = (state = worldIn
			.getBlockState(backPos)).getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(backPos))
				.getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(backPos
				.offset(facing.rotateY()))).getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(backPos
				.offset(facing.rotateYCCW()))).getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(backPos
				.offset(isBottom ? EnumFacing.DOWN
					: EnumFacing.UP))).getBlock() instanceof BlockItemScanner
			|| (state = worldIn
				.getBlockState(pos.offset(facing.rotateY())
					.offset(
						isBottom ? EnumFacing.DOWN
							: EnumFacing.UP))).getBlock() instanceof BlockItemScanner
			|| (state = worldIn
				.getBlockState(pos.offset(
					facing.rotateYCCW()).offset(
					isBottom ? EnumFacing.DOWN
						: EnumFacing.UP))).getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(pos
				.offset(facing.rotateY()))).getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(pos
				.offset(facing.rotateYCCW()))).getBlock() instanceof BlockItemScanner;
		return !hasScanner && isBottom ? getScannerBackState(
			worldIn, pos.up(), facing, false)
			: state;
	}

	private IBlockState getScannerFrontState(World worldIn,
		BlockPos pos, EnumFacing facing, boolean isBottom) {
		BlockPos frontPos = pos
			.offset(facing.getOpposite());
		IBlockState state;
		boolean hasScanner = ((state = worldIn
			.getBlockState(frontPos)).getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(frontPos))
				.getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(frontPos
				.offset(facing.rotateY()))).getBlock() instanceof BlockItemScanner
			|| (state = worldIn.getBlockState(frontPos
				.offset(facing.rotateYCCW()))).getBlock() instanceof BlockItemScanner || (state = worldIn
			.getBlockState(frontPos
				.offset(isBottom ? EnumFacing.DOWN
					: EnumFacing.UP))).getBlock() instanceof BlockItemScanner);
		return !hasScanner && isBottom ? getScannerFrontState(
			worldIn, pos.up(), facing, false)
			: state;
	}

}