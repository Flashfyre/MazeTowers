package com.samuel.mazetowers.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntityMemoryPistonMemory extends
	TileEntity {

	private int pushCount = 0;

	public TileEntityMemoryPistonMemory() {
	}

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
	public boolean shouldRefresh(World world, BlockPos pos,
		IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.pushCount = compound.getInteger("pushCount");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("pushCount", pushCount);
	}
}
