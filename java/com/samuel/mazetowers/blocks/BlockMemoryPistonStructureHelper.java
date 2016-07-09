package com.samuel.mazetowers.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPiston;

public class BlockMemoryPistonStructureHelper {
	private final World world;
	private final BlockPos pistonPos;
	private final BlockPos blockToMove;
	private final EnumFacing moveDirection;
	private final List<BlockPos> toMove = Lists
		.<BlockPos> newArrayList();
	private final List<BlockPos> toDestroy = Lists
		.<BlockPos> newArrayList();

	public BlockMemoryPistonStructureHelper(World worldIn,
		BlockPos posIn, EnumFacing pistonFacing,
		boolean extending) {
		this.world = worldIn;
		this.pistonPos = posIn;

		if (extending) {
			this.moveDirection = pistonFacing;
			this.blockToMove = posIn.offset(pistonFacing);
		} else {
			this.moveDirection = pistonFacing.getOpposite();
			this.blockToMove = posIn
				.offset(pistonFacing, 2);
		}
	}

	public boolean canMove(int pushCount) {
		this.toMove.clear();
        this.toDestroy.clear();
        IBlockState iblockstate = this.world.getBlockState(this.blockToMove);

        if (!BlockMemoryPistonBase.func_185646_a(iblockstate, this.world, this.blockToMove, this.moveDirection, false))
        {
            if (iblockstate.getMobilityFlag() != EnumPushReaction.DESTROY)
            {
                return false;
            }
            else
            {
                this.toDestroy.add(this.blockToMove);
                return true;
            }
		} else if (!this.func_177251_a(this.blockToMove,
			pushCount)) {
			return false;
		} else {
			for (int i = 0; i < this.toMove.size(); ++i) {
				BlockPos blockpos = this.toMove
					.get(i);

				if (this.world.getBlockState(blockpos)
					.getBlock() == Blocks.SLIME_BLOCK
					&& !this.func_177250_b(blockpos,
						pushCount)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean func_177251_a(BlockPos origin,
		int pushCount) {
		
		IBlockState iblockstate = this.world.getBlockState(origin);
		Block block = iblockstate.getBlock();

		if (block.isAir(iblockstate, world, origin)) {
			return true;
		} else if (!BlockMemoryPistonBase.func_185646_a(iblockstate,
			this.world, origin, this.moveDirection, false)) {
			return true;
		} else if (origin.equals(this.pistonPos)) {
			return true;
		} else if (this.toMove.contains(origin)) {
			return true;
		} else {
			int i = 1;

			if (i + this.toMove.size() > 12) {
				return false;
			} else {
				BlockPos blockpos;
				TileEntity te = world.getTileEntity(origin
					.offset(this.moveDirection, 2));
				TileEntityMemoryPiston tep = te != null
					&& te instanceof TileEntityMemoryPiston ? (TileEntityMemoryPiston) te
					: null;

				while (i < pushCount
					&& !(block = (iblockstate = this.world.getBlockState(
						blockpos = origin.offset(
							this.moveDirection
								.getOpposite(), i))
						).getBlock()).isAir(iblockstate, world, blockpos)
					&& (BlockMemoryPistonBase.func_185646_a(
						iblockstate, this.world, blockpos,
						this.moveDirection, false) || ((TileEntityMemoryPiston) world
						.getTileEntity(this.pistonPos))
						.isExtending())
					&& !blockpos.equals(this.pistonPos)) {

					++i;

					if (tep.isExtending()
						&& i + this.toMove.size() > 12) {
						return false;
					}
				}

				if (tep != null && pushCount == 0)
					i--;

				int i1 = 0;

				for (int j = i - 1; j >= 0; --j) {
					this.toMove.add(origin
						.offset(this.moveDirection
							.getOpposite(), j));
					++i1;
				}

				int j1 = 1;

				while (true) {
					BlockPos blockpos1 = origin.offset(
						this.moveDirection, j1);
					int k = this.toMove.indexOf(blockpos1);

					if (k > -1) {
						this.func_177255_a(i1, k);

						for (int l = 0; l <= k + i1; ++l) {
							BlockPos blockpos2 = this.toMove
								.get(l);

							if (this.world.getBlockState(
								blockpos2).getBlock() == Blocks.SLIME_BLOCK
								&& !this.func_177250_b(
									blockpos2, pushCount)) {
								return false;
							}
						}

						return true;
					}

					iblockstate = this.world.getBlockState(blockpos1);
					block = iblockstate.getBlock();

					if (block.isAir(iblockstate, world, blockpos1)) {
						return true;
					}

					if (!BlockMemoryPistonBase.func_185646_a(
						iblockstate, this.world, blockpos1,
						this.moveDirection, true)
						|| blockpos1.equals(this.pistonPos)) {
						return false;
					}

					if (iblockstate.getMobilityFlag() == EnumPushReaction.DESTROY) {
						this.toDestroy.add(blockpos1);
						return true;
					}

					if (this.toMove.size() >= 12) {
						return false;
					}

					this.toMove.add(blockpos1);
					++i1;
					++j1;
				}
			}
		}
	}

	private void func_177255_a(int p_177255_1_,
		int p_177255_2_) {
		List<BlockPos> list = Lists
			.<BlockPos> newArrayList();
		List<BlockPos> list1 = Lists
			.<BlockPos> newArrayList();
		List<BlockPos> list2 = Lists
			.<BlockPos> newArrayList();
		list.addAll(this.toMove.subList(0, p_177255_2_));
		list1.addAll(this.toMove.subList(this.toMove.size()
			- p_177255_1_, this.toMove.size()));
		list2.addAll(this.toMove.subList(p_177255_2_,
			this.toMove.size() - p_177255_1_));
		this.toMove.clear();
		this.toMove.addAll(list);
		this.toMove.addAll(list1);
		this.toMove.addAll(list2);
	}

	private boolean func_177250_b(BlockPos p_177250_1_,
		int pushCount) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (enumfacing.getAxis() != this.moveDirection
				.getAxis()
				&& !this.func_177251_a(p_177250_1_.offset(
					enumfacing, pushCount), pushCount)) {
				return false;
			}
		}

		return true;
	}

	public List<BlockPos> getBlocksToMove() {
		return this.toMove;
	}

	public List<BlockPos> getBlocksToDestroy() {
		return this.toDestroy;
	}
}