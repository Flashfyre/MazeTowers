package com.samuel.mazetowers.init;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {
	
	public static Item resistant_door_end_stone =
			MazeTowers.ItemEndStoneDoor;
	public static Item resistant_door_obsidian =
			MazeTowers.ItemObsidianDoor;
	public static Item resistant_door_bedrock =
			MazeTowers.ItemBedrockDoor;
	
	public static void createItems() {
		GameRegistry.registerItem(resistant_door_end_stone, "end_stone_door_item");
		GameRegistry.registerItem(resistant_door_obsidian, "obsidian_door_item");
		GameRegistry.registerItem(resistant_door_bedrock, "bedrock_door_item");
	}
}
