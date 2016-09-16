package com.samuel.mazetowers.items;

import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.MTHelper;

public class ItemSpectriteArmor extends ItemArmor {

	public ItemSpectriteArmor(EntityEquipmentSlot equipmentSlotIn) {
		super(MazeTowers.SPECTRITE, 0, equipmentSlotIn);
		
		this.addPropertyOverride(new ResourceLocation("time"), MazeTowers.ItemPropertyGetterSpectrite);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		
		String displayName = super.getItemStackDisplayName(stack);
		displayName = TextFormatting.LIGHT_PURPLE + displayName;
		return displayName;
	}
	
	@Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (!world.isRemote) {
			int spectriteCount = 0;
			for (int a = 0; a < 4; a++) {
				ItemStack stack = player.inventory.armorInventory[a];
				if (stack != null && stack.getItem() instanceof ItemSpectriteArmor)
					spectriteCount++;
			}
			if (player.getFoodStats().getFoodLevel() > 0) {
				if (player.getActivePotionEffect(MobEffects.ABSORPTION) == null && player.getHealth() == player.getMaxHealth()) {
					float absorptionHealth = player.getHealth();
					Field lastHealthScore = MTHelper.findObfuscatedField(EntityPlayerMP.class,
			    		"lastHealthScore", "field_130068_bO");
					Field ABSORPTION = MTHelper.findObfuscatedField(EntityPlayer.class,
				    		"ABSORPTION", "field_184829_a");
			    	lastHealthScore.setAccessible(true);
			    	ABSORPTION.setAccessible(true);
			    	try {
			    		absorptionHealth = Math.min(((Float) lastHealthScore.get(player)).floatValue() - player.getMaxHealth(), spectriteCount << 1);
			    		if (player.getDataManager().get((DataParameter<Float>) ABSORPTION.get(player)).floatValue() > (float) (spectriteCount << 1)) {
			    			player.getDataManager().set((DataParameter<Float>) ABSORPTION.get(player), (float) (spectriteCount << 1));
			    		}
					} catch (Exception e) {
						e.printStackTrace();
					}
					int amplifier = Math.round(absorptionHealth) >> 2;
					player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 220,
						Math.min(amplifier, Math.min(spectriteCount - 1, 2))));
				}
				if (spectriteCount == 4 && player.getActivePotionEffect(MobEffects.REGENERATION) == null) {
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 220, 0));
				}
			}
		}
    }
}
