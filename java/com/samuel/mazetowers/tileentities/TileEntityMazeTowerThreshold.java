package com.samuel.mazetowers.tileentities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3;

public class TileEntityMazeTowerThreshold extends TileEntity implements ITickable {
	
	private EnumFacing dir;
	
	public TileEntityMazeTowerThreshold() {

	}

	public TileEntityMazeTowerThreshold(EnumFacing dir) {
		this.dir = dir;
	}

	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			float posX, posY, posZ;
			List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
				AxisAlignedBB.fromBounds((posX = this.pos.getX()) - 0.5D,
				(posY = this.pos.getY()) - (0.5D), (posZ = this.pos.getZ()) - 0.5D,
				posX + 0.5D, posY + 0.5D, posZ + 0.5D));
			for (int i = 0; i < list.size(); i++) {
				EntityPlayer player = (EntityPlayer) list.get(i);
				
			}
		}
	}
}
