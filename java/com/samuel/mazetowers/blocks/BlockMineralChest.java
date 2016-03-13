package com.samuel.mazetowers.blocks;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.InventoryLargeMineralChest;
import com.samuel.mazetowers.tileentities.TileEntityMineralChest;

public class BlockMineralChest extends BlockChest {
	private boolean isLocked;

	public BlockMineralChest(int chestType) {
		super(chestType);
		this.setCreativeTab(MazeTowers.tabExtra);
	}

	@Override
	public void onBlockPlacedBy(World worldIn,
		BlockPos pos, IBlockState state,
		EntityLivingBase placer, ItemStack stack) {
		EnumFacing enumfacing = EnumFacing
			.getHorizontal(
				MathHelper
					.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3)
			.getOpposite();
		state = state.withProperty(FACING, enumfacing);
		BlockPos blockpos1 = pos.north();
		BlockPos blockpos2 = pos.south();
		BlockPos blockpos3 = pos.west();
		BlockPos blockpos4 = pos.east();
		boolean flag = this == worldIn.getBlockState(
			blockpos1).getBlock();
		boolean flag1 = this == worldIn.getBlockState(
			blockpos2).getBlock();
		boolean flag2 = this == worldIn.getBlockState(
			blockpos3).getBlock();
		boolean flag3 = this == worldIn.getBlockState(
			blockpos4).getBlock();

		if (!flag && !flag1 && !flag2 && !flag3) {
			worldIn.setBlockState(pos, state, 3);
		} else if (enumfacing.getAxis() == EnumFacing.Axis.X
			&& (flag || flag1)) {
			if (flag) {
				worldIn.setBlockState(blockpos1, state, 3);
			} else {
				worldIn.setBlockState(blockpos2, state, 3);
			}

			worldIn.setBlockState(pos, state, 3);
		} else if (enumfacing.getAxis() == EnumFacing.Axis.Z
			&& (flag2 || flag3)) {
			if (flag2) {
				worldIn.setBlockState(blockpos3, state, 3);
			} else {
				worldIn.setBlockState(blockpos4, state, 3);
			}

			worldIn.setBlockState(pos, state, 3);
		}

		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn
				.getTileEntity(pos);

			if (tileentity instanceof TileEntityMineralChest) {
				((TileEntityMineralChest) tileentity)
					.setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
		super.onNeighborBlockChange(worldIn, pos, state,
			neighborBlock);
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityMineralChest) {
			tileentity.updateContainingBlockInfo();
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn,
		BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumFacing side, float hitX,
		float hitY, float hitZ) {

		if (worldIn.isRemote) {
			return true;
		} else {
			ILockableContainer ilockablecontainer = this
				.getLockableContainer(worldIn, pos);

			if (ilockablecontainer != null) {
				playerIn
					.displayGUIChest(ilockablecontainer);
			}

			return true;
		}
	}

	@Override
	public ILockableContainer getLockableContainer(
		World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (!(tileentity instanceof TileEntityMineralChest)) {
			return null;
		} else {
			Object object = (TileEntityMineralChest) tileentity;

			if (this.isBlocked(worldIn, pos)) {
				return null;
			} else {
				Iterator iterator = EnumFacing.Plane.HORIZONTAL
					.iterator();

				while (iterator.hasNext()) {
					EnumFacing enumfacing = (EnumFacing) iterator
						.next();
					BlockPos blockpos1 = pos
						.offset(enumfacing);
					Block block = worldIn.getBlockState(
						blockpos1).getBlock();

					if (block == this) {
						if (this.isBlocked(worldIn,
							blockpos1)) {
							return null;
						}

						this.isLocked = true;

						TileEntity tileentity1 = worldIn
							.getTileEntity(blockpos1);

						if (tileentity1 instanceof TileEntityMineralChest) {
							String containerName = "Double "
								+ ((TileEntityMineralChest) tileentity1)
									.getDisplayName()
									.getUnformattedText();
							if (enumfacing != EnumFacing.WEST
								&& enumfacing != EnumFacing.NORTH) {
								object = new InventoryLargeMineralChest(
									containerName,
									(ILockableContainer) object,
									(TileEntityMineralChest) tileentity1);
							} else {
								object = new InventoryLargeMineralChest(
									containerName,
									(TileEntityMineralChest) tileentity1,
									(ILockableContainer) object);
							}
						}
					}
				}

				return (ILockableContainer) object;
			}
		}
	}

	@Override
	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityMineralChest(chestType);
	}

	@Override
	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	public boolean canProvidePower() {
		return false;
	}

	private boolean isBlocked(World worldIn, BlockPos pos) {
		return this.isBelowSolidBlock(worldIn, pos)
			|| this.isOcelotSittingOnChest(worldIn, pos);
	}

	private static boolean isBelowSolidBlock(World worldIn,
		BlockPos pos) {
		return worldIn.isSideSolid(pos.up(),
			EnumFacing.DOWN, false);
	}

	private static boolean isOcelotSittingOnChest(World worldIn,
		BlockPos pos) {
		Iterator iterator = worldIn.getEntitiesWithinAABB(
			EntityOcelot.class,
			new AxisAlignedBB((double) pos.getX(),
				(double) (pos.getY() + 1), (double) pos
					.getZ(), (double) (pos.getX() + 1),
				(double) (pos.getY() + 2), (double) (pos
					.getZ() + 1))).iterator();
		EntityOcelot entityocelot;

		do {
			if (!iterator.hasNext()) {
				return false;
			}

			Entity entity = (Entity) iterator.next();
			entityocelot = (EntityOcelot) entity;
		} while (!entityocelot.isSitting());

		return true;
	}

	@Override
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING,
			enumfacing);
	}

	@Override
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING))
			.getIndex();
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this,
			new IProperty[] { FACING });
	}
}