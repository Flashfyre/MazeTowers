package com.samuel.mazetowers.init;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModCrafting {

	public static void initCrafting() {
		GameRegistry.addRecipe(new ItemStack(
			ModItems.explosive_arrow), "X", "Y", "Z",
			'X', new ItemStack(Items.FIRE_CHARGE), 'Y',
			new ItemStack(Items.ARROW), 'Z', new ItemStack(
				Items.GUNPOWDER));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestIron), "XXX", "X X", "XXX",
			'X', new ItemStack(Items.IRON_INGOT));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestGold), "XXX", "X X", "XXX",
			'X', new ItemStack(Items.GOLD_INGOT));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestDiamond), "XXX", "X X", "XXX",
			'X', new ItemStack(Items.DIAMOND));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.mineralChestSpectrite), "XXX", "X X", "XXX",
			'X', new ItemStack(ModItems.spectrite_gem));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestIronTrapped), new ItemStack(
			ModBlocks.mineralChestIron), new ItemStack(Blocks.TRIPWIRE_HOOK));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestGoldTrapped), new ItemStack(
			ModBlocks.mineralChestGold), new ItemStack(Blocks.TRIPWIRE_HOOK));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestDiamondTrapped), new ItemStack(
			ModBlocks.mineralChestDiamond), new ItemStack(Blocks.TRIPWIRE_HOOK));
		GameRegistry.addShapelessRecipe(new ItemStack(
			ModBlocks.mineralChestSpectriteTrapped), new ItemStack(
			ModBlocks.mineralChestSpectrite), new ItemStack(Blocks.TRIPWIRE_HOOK));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.diamond_rod), "X", "X",
			'X', new ItemStack(Items.DIAMOND));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_rod), "X", "X",
			'X', new ItemStack(MazeTowers.ItemSpectriteGem));
		GameRegistry.addRecipe(new ItemStack(
			ModBlocks.spectriteBlock), "XXX", "XXX", "XXX",
			'X', new ItemStack(ModItems.spectrite_gem));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_shovel), "X", "Y", "Y",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_shovel), "  X", " Y ", "Y  ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_shovel_special), "X", "Y", "Y",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.spectrite_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_shovel_special), "  X", " Y ", "Y  ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.spectrite_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_axe), "XX ", "XY ", " Y ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_axe), " XX", "XY ", "Y  ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_axe_special), "XX ", "XY ", " Y ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.spectrite_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_axe_special), " XX", "XY ", "Y  ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.spectrite_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_pickaxe), "XXX", " Y ", " Y ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_pickaxe), " XX", " YX", "Y  ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.diamond_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_pickaxe_special), "XXX", " Y ", " Y ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.spectrite_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_pickaxe_special), " XX", " YX", "Y  ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(ModItems.spectrite_rod));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_sword), " X ", " X ", "YYY",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(Items.DIAMOND));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_sword), "  X", "YX ", "YY ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(Items.DIAMOND));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_key_sword), " XY", " X ", "ZZZ", 'X', new ItemStack(ModItems.spectrite_gem),
			'Y', new ItemStack(ModItems.key_spectrite), 'Z', new ItemStack(Items.DIAMOND));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_key_sword), "  X", "YXZ", "YY ",
			'X', new ItemStack(ModItems.spectrite_gem), 'Y', new ItemStack(Items.DIAMOND),
			'Z', new ItemStack(ModItems.key_spectrite));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_sword_special), " X ", " X ", "XXX",
			'X', new ItemStack(ModItems.spectrite_gem));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_sword_special), "  X", "XX ", "XX ",
			'X', new ItemStack(ModItems.spectrite_gem));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_key_sword_special), " XX", " X ", "XXX",
			'X', new ItemStack(ModItems.spectrite_gem));
		GameRegistry.addRecipe(new ItemStack(
			ModItems.spectrite_key_sword_special), "  X", "XXX", "XX ",
			'X', new ItemStack(ModItems.spectrite_gem));
	}
}
