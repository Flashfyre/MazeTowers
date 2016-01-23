package com.samuel.mazetowers.tileentities;

import java.util.List;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTower;

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
	
	private MazeTower tower;
	
	public TileEntityMazeTowerThreshold() {

	}


	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			float posX = pos.getX(), posY = pos.getY(), posZ = pos.getZ();
			if (tower == null)
				tower = MazeTowers.mazeTowers.getTowerAtCoords((int) posX >> 4, (int) posZ >> 4);
			List list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,
				AxisAlignedBB.fromBounds(posX - 0.5D,
				(posY = this.pos.getY()) - (0.5D), posZ - 0.5D,
				posX + 14.5D, posY + 0.5D, posZ + 14.5D));
			for (int i = 0; i < list.size(); i++) {
				EntityPlayer player = (EntityPlayer) list.get(i);
				
			}
		}
	}
}
