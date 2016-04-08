package com.samuel.mazetowers.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModCrafting {

	public static void initCrafting() {
		GameRegistry.addRecipe(new ItemStack(
			ModItems.explosive_arrow), "X", "Y", "Z",
			'X', new ItemStack(Items.fire_charge), 'Y',
			new ItemStack(Items.arrow), 'Z', new ItemStack(
				Items.gunpowder));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestIron), "XXX", "X X", "XXX",
			'X', new ItemStack(Items.iron_ingot));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestGold), "XXX", "X X", "XXX",
			'X', new ItemStack(Items.gold_ingot));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestDiamond), "XXX", "X X", "XXX",
			'X', new ItemStack(Items.diamond));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestSpectrite), "XXX", "X X", "XXX",
			'X', new ItemStack(ModItems.spectrite_gem));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestIronTrapped), new ItemStack(
			ModBlocks.mineralChestIron), new ItemStack(Blocks.tripwire_hook));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestGoldTrapped), new ItemStack(
			ModBlocks.mineralChestGold), new ItemStack(Blocks.tripwire_hook));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestDiamondTrapped), new ItemStack(
			ModBlocks.mineralChestDiamond), new ItemStack(Blocks.tripwire_hook));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestSpectriteTrapped), new ItemStack(
			ModBlocks.mineralChestSpectrite), new ItemStack(Blocks.tripwire_hook));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.diamond_rod), "X", "X",
			'X', new ItemStack(Items.diamond));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.spectriteBlock), "XXX", "XXX", "XXX",
			'X', new ItemStack(ModItems.spectrite_gem));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_pickaxe), "XXX", " Y ", " Y ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_sword), "X", "X", "Y",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_sword), " XY", " X ", " Z ", 'X', new ItemStack(ModItems.spectrite_gem),
			'Y', new ItemStack(ModItems.key_spectrite), 'Z', new ItemStack(ModItems.diamond_rod));
	}
}
