package com.samuel.mazetowers.items;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

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
}
