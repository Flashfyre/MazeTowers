package com.samuel.mazetowers.tileentity;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

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
				EntityPlayer.class, new AxisAlignedBB(
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
						/*MazeTowers.network.sendTo(
							new PacketDebugMessage(
								"Added effect: "
									+ this.toString()),
							(EntityPlayerMP) entity);*/
						if (!((EntityPlayer) entity).capabilities.isCreativeMode)
							continue;
					}

					if (!entity
						.isPotionActive(MobEffects.moveSlowdown))
						entity
							.addPotionEffect(new PotionEffect(
								MobEffects.moveSlowdown, 250, 0));
					else if (!entity
						.isPotionActive(MobEffects.weakness))
						entity
							.addPotionEffect(new PotionEffect(
								MobEffects.weakness, 250, 0));
					else if (!entity
						.isPotionActive(MobEffects.poison))
						entity
							.addPotionEffect(new PotionEffect(
								MobEffects.poison, 250, 0));
					else if (!entity
						.isPotionActive(MobEffects.wither))
						entity
							.addPotionEffect(new PotionEffect(
								MobEffects.wither, 250, 0));
					else {
						int amplifier = 0;
						if (entity
							.isPotionActive(MobEffects.blindness)) {
							amplifier = 1;
							entity.removePotionEffect(MobEffects.blindness);
						}
						entity
							.addPotionEffect(new PotionEffect(
								MobEffects.blindness, 250, amplifier));
					}
				}
			}
		}
	}
}