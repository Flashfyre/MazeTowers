package com.samuel.mazetowers.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.IMazeTowerCapability;
import com.samuel.mazetowers.etc.MazeTowerGuiProvider;
import com.samuel.mazetowers.packets.PacketMazeTowersGui;
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
				tower = MazeTowers.mazeTowers
					.getTowerAtCoords(worldObj,
						(int) posX >> 4, (int) posZ >> 4);
				if (tower == null)
					hasTower = false;
			}
			List list = worldObj.getEntitiesWithinAABB(
				EntityPlayer.class, new AxisAlignedBB
					(posX - 0.5D, posY - 0.5D,
					posZ - 0.5D, posX + 0.5D,
					posY + 0.5D, posZ + 0.5D));
			for (int i = 0; i < list.size(); i++) {
				EntityPlayer player = (EntityPlayer) list
					.get(i);
				if (player.hasCapability(MazeTowerGuiProvider.gui, null)) {
					IMazeTowerCapability props = player.getCapability(MazeTowerGuiProvider.gui, null);
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
						} else if (!props.getEnabled() &&
							(player.getBedLocation(worldObj.provider.getDimension()) == null ||
							!player.getBedLocation().equals(pos.up()))) {
							player.setSpawnPoint(pos.up(), true);
							player.addChatMessage(new TextComponentString(
								I18n.translateToLocal("gui.spawn_point")));
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
}
