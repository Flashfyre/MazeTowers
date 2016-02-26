package com.samuel.mazetowers.tileentities;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ITickable;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.PlayerMazeTower;
import com.samuel.mazetowers.packets.PacketMazeTowersGui;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTowerBase;

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
				tower = MazeTowers.mazeTowers
					.getTowerAtCoords(worldObj,
						(int) posX >> 4, (int) posZ >> 4);
				if (tower == null)
					hasTower = false;
			}
			List list = worldObj.getEntitiesWithinAABB(
				EntityPlayer.class, AxisAlignedBB
					.fromBounds(posX - 0.5D, posY - 0.5D,
						posZ - 0.5D, posX + 0.5D,
						posY + 0.5D, posZ + 0.5D));
			for (int i = 0; i < list.size(); i++) {
				EntityPlayer player = (EntityPlayer) list
					.get(i);
				PlayerMazeTower props = PlayerMazeTower
					.get(player);
				if (props != null) {
					int chunkX = 0, baseY = 0, chunkZ = 0, floors = 0, difficulty = 0, rarity = 0;
					boolean isUnderground = false;
					String towerName = "";
					if (hasTower) {
						chunkX = tower.chunkX;
						baseY = tower.baseY;
						chunkZ = tower.chunkZ;
						isUnderground = tower.isUnderground;
						floors = tower.floors;
						difficulty = tower.difficulty;
						rarity = tower.rarity;
						towerName = tower.towerTypeName
							+ " Maze Tower"
							+ (isUnderground ? " (Underground)"
								: "");
					}
					MazeTowers.network.sendTo(
						new PacketMazeTowersGui(chunkX,
							baseY, chunkZ, floors,
							difficulty, rarity,
							isUnderground, towerName),
						(EntityPlayerMP) player);
				}
			}
		}
	}
}
