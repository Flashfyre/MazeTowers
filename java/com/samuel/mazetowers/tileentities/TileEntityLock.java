package com.samuel.mazetowers.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLock extends TileEntity {
	
	int typeIndex;
	
	public TileEntityLock() {
		typeIndex = 14;
	}
	
	public int getTypeIndex() {
		return typeIndex;
	}

	public void setTypeIndex(int itemDamage) {
		typeIndex = itemDamage;
		markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
        typeIndex = compound.getInteger("typeIndex");
    }

	@Override
    public void writeToNBT(NBTTagCompound compound) {
    	super.writeToNBT(compound);
    	compound.setInteger("typeIndex", typeIndex);
    }
	
	@Override
	/**
     * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world Current world
     * @param pos Tile's world position
     * @param oldState The old ID of the block
     * @param newState The new ID of the block (May be the same)
     * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
     */
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }
	
	@Override
	/**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket()
    {
        return new net.minecraft.network.play.server.S35PacketUpdateTileEntity(pos, 0, serializeNBT());
    }
	
	@Override
	/**
    * Called when you receive a TileEntityData packet for the location this
    * TileEntity is currently in. On the client, the NetworkManager will always
    * be the remote server. On the server, it will be whomever is responsible for
    * sending the packet.
    *
    * @param net The NetworkManager the packet originated from
    * @param pkt The data packet
    */
   public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.S35PacketUpdateTileEntity pkt)
   {
	   readFromNBT(pkt.getNbtCompound());
	   super.onDataPacket(net, pkt);
   }
	
	@Override
	/**
     * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity}
     * Defaults to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block
     * at this location.
     *
     * @return an appropriate size {@link AxisAlignedBB} for the {@link TileEntity}
     */
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.AxisAlignedBB getRenderBoundingBox() {
        return new net.minecraft.util.AxisAlignedBB(getPos(), getPos().add(1.0D, 1.0D, 1.0D));
    }

    @Override
    /**
     * Checks if this tile entity knows how to render its 'breaking' overlay effect.
     * If this returns true, The TileEntitySpecialRenderer will be called again with break progress set.
     * @return True to re-render tile with breaking effect.
     */
    public boolean canRenderBreaking() {
        return true;
    }
}
