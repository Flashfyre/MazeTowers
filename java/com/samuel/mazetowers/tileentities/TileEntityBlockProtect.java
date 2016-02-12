package com.samuel.mazetowers.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityBlockProtect extends TileEntity implements ITickable {
	
	public TileEntityBlockProtect() { }

	@Override
	/**
     * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world Current world
     * @param pos Tile's world position
     * @param oldID The old ID of the block
     * @param newID The new ID of the block (May be the same)
     * @return True to remove the old tile entity, false to keep it in tact {and create a new one if the new values specify to}
     */
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
		final boolean shouldRefresh = super.shouldRefresh(world, pos, oldState, newState);
		/*if (shouldRefresh && oldState != Blocks.air.getDefaultState())
			world.setBlockState(pos, oldState);*/
        return false;
    }



	@Override
	public void update() {
		if (this.worldObj.getBlockState(this.pos) == Blocks.air.getDefaultState()) {
			//worldObj.setBlockState(pos, Blocks.stone.getDefaultState());
		}
	}
}
