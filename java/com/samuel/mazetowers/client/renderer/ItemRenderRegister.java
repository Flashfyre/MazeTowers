package com.samuel.mazetowers.client.renderer;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModItems;

public final class ItemRenderRegister {

	public static String modid = MazeTowers.MODID;

	public static void registerItemRenderer() {
		reg(ModItems.resistant_door_prismarine);
		reg(ModItems.resistant_door_quartz);
		reg(ModItems.resistant_door_end_stone);
		reg(ModItems.resistant_door_purpur);
		reg(ModItems.resistant_door_obsidian);
		reg(ModItems.resistant_door_bedrock);
		for (int k = 0; k < 20; k++)
			reg(ModItems.key_colored, 0);
		reg(ModItems.key_spectrite);
		reg(ModItems.diamond_rod);
		reg(ModItems.spectrite_rod);
		reg(ModItems.ram);
		reg(ModItems.spectrite_gem);
		reg(ModItems.spectrite_orb);
		reg(ModItems.explosive_arrow);
		reg(ModItems.explosive_bow);
		reg(ModItems.spectrite_shovel);
		reg(ModItems.spectrite_shovel_special);
		reg(ModItems.spectrite_pickaxe);
		reg(ModItems.spectrite_pickaxe_special);
		reg(ModItems.spectrite_axe);
		reg(ModItems.spectrite_axe_special);
		reg(ModItems.spectrite_sword);
		reg(ModItems.spectrite_key_sword);
		reg(ModItems.spectrite_sword_special);
		reg(ModItems.spectrite_key_sword_special);
		reg(ModItems.spectrite_helmet);
		reg(ModItems.spectrite_chestplate);
		reg(ModItems.spectrite_leggings);
		reg(ModItems.spectrite_boots);
		reg(ModItems.explosive_creeper_skull);
	}

	public static void reg(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0,
			new ModelResourceLocation(modid + ":" +
			item.getUnlocalizedName().substring(5), "inventory"));
	}

	public static void reg(Item item, int meta) {
		ModelLoader.setCustomModelResourceLocation(item,
			meta, new ModelResourceLocation(modid
				+ ":"
				+ item.getUnlocalizedName(
					new ItemStack(item, 1, meta))
					.substring(5), "inventory"));
	}

}