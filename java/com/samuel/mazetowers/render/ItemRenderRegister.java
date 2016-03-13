package com.samuel.mazetowers.render;

import net.minecraft.client.resources.model.ModelResourceLocation;
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
		for (int k = 0; k < 19; k++)
			reg((Item) ModItems.key, 0);
		reg(ModItems.ram);
		reg(ModItems.explosive_arrow);
		reg(ModItems.explosive_bow);
		reg(ModItems.explosive_creeper_skull);
	}

	public static void reg(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0,
			new ModelResourceLocation(modid + ":"
				+ item.getUnlocalizedName().substring(5),
				"inventory"));
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