package com.samuel.mazetowers.init;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModCrafting {

	public static void initCrafting() {
		GameRegistry.addRecipe(new ItemStack(
			ModItems.explosive_arrow), "X  ", "Y  ", "Z  ",
			'X', new ItemStack(Items.fire_charge), 'Y',
			new ItemStack(Items.arrow), 'Z', new ItemStack(
				Items.gunpowder));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.explosive_arrow), " X ", " Y ", " Z ",
			'X', new ItemStack(Items.fire_charge), 'Y',
			new ItemStack(Items.arrow), 'Z', new ItemStack(
				Items.gunpowder));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.explosive_arrow), "  X", "  Y", "  Z",
			'X', new ItemStack(Items.fire_charge), 'Y',
			new ItemStack(Items.arrow), 'Z', new ItemStack(
				Items.gunpowder));
	}
}
