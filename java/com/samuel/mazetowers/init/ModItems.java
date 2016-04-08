package com.samuel.mazetowers.init;

import net.minecraft.item.ItemDoor;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.items.ItemChaoticSludgeBucket;
import com.samuel.mazetowers.items.ItemColoredKey;
import com.samuel.mazetowers.items.ItemDiamondRod;
import com.samuel.mazetowers.items.ItemExplosiveArrow;
import com.samuel.mazetowers.items.ItemExplosiveBow;
import com.samuel.mazetowers.items.ItemExplosiveCreeperSkull;
import com.samuel.mazetowers.items.ItemRAM;
import com.samuel.mazetowers.items.ItemSpectriteArmor;
import com.samuel.mazetowers.items.ItemSpectriteGem;
import com.samuel.mazetowers.items.ItemSpectriteKey;
import com.samuel.mazetowers.items.ItemSpectriteKeySword;
import com.samuel.mazetowers.items.ItemSpectriteOrb;
import com.samuel.mazetowers.items.ItemSpectritePickaxe;
import com.samuel.mazetowers.items.ItemSpectriteSword;

public class ModItems {

	public static ItemDoor resistant_door_prismarine = MazeTowers.ItemPrismarineDoor;
	public static ItemDoor resistant_door_quartz = MazeTowers.ItemQuartzDoor;
	public static ItemDoor resistant_door_end_stone = MazeTowers.ItemEndStoneDoor;
	public static ItemDoor resistant_door_purpur = MazeTowers.ItemPurpurDoor;
	public static ItemDoor resistant_door_obsidian = MazeTowers.ItemObsidianDoor;
	public static ItemDoor resistant_door_bedrock = MazeTowers.ItemBedrockDoor;
	public static ItemColoredKey key_colored = MazeTowers.ItemColoredKey;
	public static ItemSpectriteKey key_spectrite = MazeTowers.ItemSpectriteKey;
	public static ItemDiamondRod diamond_rod = MazeTowers.ItemDiamondRod;
	public static ItemRAM ram = MazeTowers.ItemRAM;
	public static ItemSpectriteGem spectrite_gem = MazeTowers.ItemSpectriteGem;
	public static ItemSpectriteOrb spectrite_orb = MazeTowers.ItemSpectriteOrb;
	public static ItemExplosiveArrow explosive_arrow = MazeTowers.ItemExplosiveArrow;
	public static ItemExplosiveBow explosive_bow = MazeTowers.ItemExplosiveBow;
	public static ItemSpectritePickaxe spectrite_pickaxe = MazeTowers.ItemSpectritePickaxe;
	public static ItemSpectriteSword spectrite_sword = MazeTowers.ItemSpectriteSword;
	public static ItemSpectriteKeySword spectrite_key_sword = MazeTowers.ItemSpectriteKeySword;
	public static ItemSpectriteArmor spectrite_helmet = MazeTowers.ItemSpectriteHelmet;
	public static ItemSpectriteArmor spectrite_chestplate = MazeTowers.ItemSpectriteChestplate;
	public static ItemSpectriteArmor spectrite_leggings = MazeTowers.ItemSpectriteLeggings;
	public static ItemSpectriteArmor spectrite_boots = MazeTowers.ItemSpectriteBoots;
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
		GameRegistry.registerItem(key_colored, "key");
		GameRegistry.registerItem(key_spectrite, "spectrite_key");
		GameRegistry.registerItem(ram, "ram");
		GameRegistry.registerItem(spectrite_gem,
			"spectrite_gem");
		GameRegistry.registerItem(spectrite_orb,
			"spectrite_orb");
		GameRegistry.registerItem(explosive_arrow,
			"explosive_arrow");
		GameRegistry.registerItem(explosive_bow,
			"explosive_bow");
		GameRegistry.registerItem(spectrite_pickaxe,
			"spectrite_pickaxe");
		GameRegistry.registerItem(spectrite_sword,
			"spectrite_sword");
		GameRegistry.registerItem(spectrite_key_sword,
			"spectrite_key_sword");
		GameRegistry.registerItem(spectrite_helmet,
			"spectrite_helmet");
		GameRegistry.registerItem(spectrite_chestplate,
			"spectrite_chestplate");
		GameRegistry.registerItem(spectrite_leggings,
			"spectrite_leggings");
		GameRegistry.registerItem(spectrite_boots,
			"spectrite_boots");
		GameRegistry.registerItem(explosive_creeper_skull,
			"explosive_creeper_skull_item");
	}
}
