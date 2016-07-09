package com.samuel.mazetowers.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;

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
			if (spectriteCount >= 3 && player.getActivePotionEffect(MobEffects.REGENERATION) == null &&
				player.getFoodStats().getFoodLevel() > 0) {
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 220, spectriteCount - 3));
			}
		}
    }
}
