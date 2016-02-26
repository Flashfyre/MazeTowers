package com.samuel.mazetowers.init;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.MazeTowers;

public class ModItems {

	public static Item resistant_door_end_stone = MazeTowers.ItemEndStoneDoor;
	public static Item resistant_door_quartz = MazeTowers.ItemQuartzDoor;
	public static Item resistant_door_obsidian = MazeTowers.ItemObsidianDoor;
	public static Item resistant_door_bedrock = MazeTowers.ItemBedrockDoor;
	public static Item explosive_arrow = MazeTowers.ItemExplosiveArrow;
	public static Item explosive_bow = MazeTowers.ItemExplosiveBow;

	public static void createItems() {
		GameRegistry.registerItem(resistant_door_end_stone,
			"end_stone_door_item");
		GameRegistry.registerItem(resistant_door_quartz,
			"quartz_door_item");
		GameRegistry.registerItem(resistant_door_obsidian,
			"obsidian_door_item");
		GameRegistry.registerItem(resistant_door_bedrock,
			"bedrock_door_item");
		GameRegistry.registerItem(explosive_arrow,
			"explosive_arrow");
		GameRegistry.registerItem(explosive_bow,
			"explosive_bow");
	}
}
