package com.samuel.mazetowers.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.tileentities.TileEntityMemoryPiston;

public class BlockMemoryPistonBaseOff extends
	BlockMemoryPistonBase implements ITileEntityProvider {

	public BlockMemoryPistonBaseOff() {
		super();
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	/**
	 * Called on both Client and Server when World#addBlockEvent is called
	 */
	public boolean onBlockEventReceived(World worldIn,
		BlockPos pos, IBlockState state, int eventID,
		int eventParam) {
		EnumFacing enumfacing = (EnumFacing) state
			.getValue(FACING);

		if (!worldIn.isRemote) {
			boolean flag = this.shouldBeExtended(worldIn,
				pos, enumfacing);

			if (flag && eventID == 1) {
				worldIn.setBlockState(pos, state
					.withProperty(EXTENDED, Boolean
						.valueOf(true)), 2);
				return false;
			}

			if (!flag && eventID == 0) {
				return false;
			}
		}

		if (eventID == 0) {
			if (!this
				.doMove(worldIn, pos, enumfacing, true)) {
				return false;
			}

			int pushCount = 0;
			TileEntity te;

			if ((te = worldIn.getTileEntity(pos)) != null) {
				if (te.getTileData().hasKey("pushCount"))
					pushCount = te.getTileData()
						.getInteger("pushCount");
			}
			worldIn.setBlockState(pos, state.withProperty(
				EXTENDED, Boolean.valueOf(true)), 2);
			worldIn.getTileEntity(pos).getTileData()
				.setInteger("pushCount", pushCount);
			worldIn.playSoundEffect(
				(double) pos.getX() + 0.5D, (double) pos
					.getY() + 0.5D,
				(double) pos.getZ() + 0.5D,
				"tile.piston.out", 0.5F, worldIn.rand
					.nextFloat() * 0.25F + 0.6F);
		} else if (eventID == 1) {
			TileEntity tileentity1 = worldIn
				.getTileEntity(pos.offset(enumfacing));

			if (tileentity1 instanceof TileEntityMemoryPiston) {
				((TileEntityMemoryPiston) tileentity1)
					.clearPistonTileEntity();
			}

			int pushCount = 0;
			tileentity1 = worldIn.getTileEntity(pos);
			if (tileentity1.getTileData().hasKey(
				"pushCount"))
				pushCount = tileentity1.getTileData()
					.getInteger("pushCount");
			tileentity1 = BlockMemoryPistonMovingOff
				.newTileEntity(this
					.getStateFromMeta(eventParam),
					enumfacing, false, true);
			tileentity1.getTileData().setInteger(
				"pushCount", pushCount);

			worldIn.setBlockState(pos,
				MazeTowers.BlockMemoryPistonExtensionOff
					.getDefaultState().withProperty(
						BlockMemoryPistonMovingOff.FACING,
						enumfacing), 3);
			worldIn.setTileEntity(pos, tileentity1);

			worldIn.setBlockToAir(pos.offset(enumfacing));

			worldIn.playSoundEffect(
				(double) pos.getX() + 0.5D, (double) pos
					.getY() + 0.5D,
				(double) pos.getZ() + 0.5D,
				"tile.piston.in", 0.5F, worldIn.rand
					.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	@Override
	public boolean onBlockActivated(World worldIn,
		BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumFacing side, float hitX,
		float hitY, float hitZ) {
		boolean isExtended;
		EnumFacing facing;
		IBlockState onState = MazeTowers.BlockMemoryPiston
			.getDefaultState()
			.withProperty(
				BlockMemoryPistonBase.FACING,
				facing = state
					.getValue(BlockMemoryPistonBase.FACING))
			.withProperty(
				BlockMemoryPistonBase.EXTENDED,
				isExtended = state
					.getValue(BlockMemoryPistonBase.EXTENDED));
		if (!isExtended) {
			worldIn.playSoundEffect(
				(double) pos.getX() + 0.5D, (double) pos
					.getY() + 0.5D,
				(double) pos.getZ() + 0.5D, "random.click",
				0.3F, 0.6F);
			worldIn.setBlockState(pos, onState);
		}
		/*
		 * if (isExtended) { BlockPos headPos = pos.offset(facing); IBlockState
		 * onStateHead = worldIn.getBlockState(headPos); if
		 * (onStateHead.getBlock() == MazeTowers.BlockMemoryPistonHeadOff) {
		 * onStateHead = MazeTowers.BlockMemoryPistonHead.getDefaultState()
		 * .withProperty(BlockMemoryPistonExtension.FACING,
		 * onStateHead.getValue(BlockMemoryPistonExtension.FACING))
		 * .withProperty(BlockMemoryPistonExtension.SHORT,
		 * onStateHead.getValue(BlockMemoryPistonExtension.SHORT));
		 * worldIn.setBlockState(headPos, onStateHead); } }
		 */
		return !isExtended;
	}
}
