package com.samuel.mazetowers.tileentities;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ITickable;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.packets.PacketDebugMessage;

public class TileEntityChaoticSludgeToxin extends
	TileEntity implements ITickable {

	private int tickCounter = 0;

	public TileEntityChaoticSludgeToxin() {
	}

	@Override
	public void update() {
		if (!this.worldObj.isRemote && tickCounter++ == 50) {
			float posX, posY, posZ;
			List list = worldObj.getEntitiesWithinAABB(
				EntityPlayer.class, AxisAlignedBB
					.fromBounds(
						(posX = this.pos.getX()) + 0.375D,
						(posY = this.pos.getY()) + 0.375D,
						(posZ = this.pos.getZ()) + 0.375D,
						posX + 0.625D, posY + 0.625D,
						posZ + 0.625D));
			tickCounter = 0;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) instanceof EntityLivingBase) {
					EntityLivingBase entity = (EntityLivingBase) list
						.get(i);
					if (entity instanceof EntityPlayer) {
						MazeTowers.network.sendTo(
							new PacketDebugMessage(
								"Added effect: "
									+ this.toString()),
							(EntityPlayerMP) entity);
						if (!((EntityPlayer) entity).capabilities.isCreativeMode)
							continue;
					}

					if (!entity
						.isPotionActive(Potion.moveSlowdown))
						entity
							.addPotionEffect(new PotionEffect(
								2, 250, 0));
					else if (!entity
						.isPotionActive(Potion.weakness))
						entity
							.addPotionEffect(new PotionEffect(
								18, 250, 0));
					else if (!entity
						.isPotionActive(Potion.poison))
						entity
							.addPotionEffect(new PotionEffect(
								19, 250, 0));
					else if (!entity
						.isPotionActive(Potion.wither))
						entity
							.addPotionEffect(new PotionEffect(
								20, 250, 0));
					else {
						int amplifier = 0;
						if (entity
							.isPotionActive(Potion.blindness)) {
							amplifier = 1;
							entity
								.removePotionEffect(Potion.blindness
									.getId());
						}
						entity
							.addPotionEffect(new PotionEffect(
								15, 250, amplifier));
					}
				}
			}
		}
	}
}