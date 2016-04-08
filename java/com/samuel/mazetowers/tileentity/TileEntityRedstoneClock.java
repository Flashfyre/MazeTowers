package com.samuel.mazetowers.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.blocks.BlockRedstoneClock;

public class TileEntityRedstoneClock extends TileEntity implements ITickable {
	
	public TileEntityRedstoneClock() { }
	
	@Override
	/**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        if (this.worldObj != null && !this.worldObj.isRemote && this.worldObj.getTotalWorldTime() % 20L == 0L)  {
            if ((this.blockType = this.getBlockType()) instanceof BlockRedstoneClock)
                ((BlockRedstoneClock)this.blockType).updatePower(this.worldObj, this.pos);
        }
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
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
        return new net.minecraft.util.math.AxisAlignedBB(getPos(), getPos().add(1.0D, 0.125D, 1.0D));
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