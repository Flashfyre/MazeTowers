package com.samuel.mazetowers.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.IVendorTradeable;
import com.samuel.mazetowers.items.*;

public class ModItems {

	public static ItemDoor resistant_door_prismarine = MazeTowers.ItemPrismarineDoor;
	public static ItemDoor resistant_door_quartz = MazeTowers.ItemQuartzDoor;
	public static ItemDoor resistant_door_end_stone = MazeTowers.ItemEndStoneDoor;
	public static ItemDoor resistant_door_purpur = MazeTowers.ItemPurpurDoor;
	public static ItemDoor resistant_door_obsidian = MazeTowers.ItemObsidianDoor;
	public static ItemDoor resistant_door_bedrock = MazeTowers.ItemBedrockDoor;
	public static ItemKey key = MazeTowers.ItemKey;
	public static ItemRAM ram = MazeTowers.ItemRAM;
	public static ItemExplosiveArrow explosive_arrow = MazeTowers.ItemExplosiveArrow;
	public static ItemExplosiveBow explosive_bow = MazeTowers.ItemExplosiveBow;
	public static ItemExplosiveCreeperSkull explosive_creeper_skull = MazeTowers.ItemExplosiveCreeperSkull;
	public static ItemChaoticSludgeBucket chaotic_sludge_bucket = MazeTowers.ItemChaoticSludgeBucket;

	public static void createItems() {
		GameRegistry.registerItem(resistant_door_prismarine,
			"prismarine_brick_door_item");
		GameRegistry.registerItem(resistant_door_quartz,
			"quartz_door_item");
		GameRegistry.registerItem(resistant_door_end_stone,
			"end_stone_door_item");
		GameRegistry.registerItem(resistant_door_purpur,
			"purpur_door_item");
		GameRegistry.registerItem(resistant_door_obsidian,
			"obsidian_door_item");
		GameRegistry.registerItem(resistant_door_bedrock,
			"bedrock_door_item");
		GameRegistry.registerItem(key, "key");
		GameRegistry.registerItem(ram, "ram");
		GameRegistry.registerItem(explosive_arrow,
			"explosive_arrow");
		GameRegistry.registerItem(explosive_bow,
			"explosive_bow");
		GameRegistry.registerItem(explosive_creeper_skull,
			"explosive_creeper_skull_item");
	}
}
