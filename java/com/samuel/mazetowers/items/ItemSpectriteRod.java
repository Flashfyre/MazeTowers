package com.samuel.mazetowers.items;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemSpectriteRod extends Item {
	public ItemSpectriteRod() {
		super();
		this.addPropertyOverride(new ResourceLocation("time"), MazeTowers.ItemPropertyGetterSpectrite);
	}
}
