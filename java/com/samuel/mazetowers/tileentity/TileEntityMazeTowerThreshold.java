package com.samuel.mazetowers.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.MTHelper;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;

public class TileEntityMazeTowerThreshold extends
	TileEntity implements ITickable {

	private MazeTowerBase tower;
	private boolean hasTower;

	public TileEntityMazeTowerThreshold() {
		hasTower = true;
	}

	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			float posX = pos.getX(), posY = pos.getY(), posZ = pos
				.getZ();
			if (hasTower && tower == null) {
				final int chunkX = (int) posX >> 4,
					chunkZ = (int) posZ >> 4;
				tower = MazeTowers.mazeTowers
					.getTowerBesideCoords(worldObj,
						chunkX, chunkZ);
				if (tower == null)
					hasTower = false;
				else if ((tower.chunkX != chunkX ||
					tower.chunkZ != chunkZ) &&
					MTHelper.getMiniTowerAtPos(tower.getMiniTowers(), pos) == null) {
					hasTower = false;
					tower = null;
				}
			}
			List list = worldObj.getEntitiesWithinAABB(
				EntityPlayer.class, new AxisAlignedBB
					(posX - 0.5D, posY,
					posZ - 0.5D, posX + 0.5D,
					posY + 1.0D, posZ + 0.5D));
			for (int i = 0; i < list.size(); i++) {
				EntityPlayer player = (EntityPlayer) list
					.get(i);
				MazeTowers.mazeTowers.initGui(player, tower, pos);
			}
		}
	}
}
