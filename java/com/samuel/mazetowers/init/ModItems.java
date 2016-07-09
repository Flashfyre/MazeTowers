package com.samuel.mazetowers.init;

import net.minecraft.item.ItemDoor;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.items.*;

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
	public static ItemSpectriteRod spectrite_rod = MazeTowers.ItemSpectriteRod;
	public static ItemRAM ram = MazeTowers.ItemRAM;
	public static ItemSpectriteGem spectrite_gem = MazeTowers.ItemSpectriteGem;
	public static ItemSpectriteOrb spectrite_orb = MazeTowers.ItemSpectriteOrb;
	public static ItemExplosiveArrow explosive_arrow = MazeTowers.ItemExplosiveArrow;
	public static ItemExplosiveBow explosive_bow = MazeTowers.ItemExplosiveBow;
	public static ItemSpectriteShovel spectrite_shovel = MazeTowers.ItemSpectriteShovel;
	public static ItemSpectriteShovelSpecial spectrite_shovel_special = MazeTowers.ItemSpectriteShovelSpecial;
	public static ItemSpectritePickaxe spectrite_pickaxe = MazeTowers.ItemSpectritePickaxe;
	public static ItemSpectritePickaxeSpecial spectrite_pickaxe_special = MazeTowers.ItemSpectritePickaxeSpecial;
	public static ItemSpectriteAxe spectrite_axe = MazeTowers.ItemSpectriteAxe;
	public static ItemSpectriteAxeSpecial spectrite_axe_special = MazeTowers.ItemSpectriteAxeSpecial;
	public static ItemSpectriteSword spectrite_sword = MazeTowers.ItemSpectriteSword;
	public static ItemSpectriteKeySword spectrite_key_sword = MazeTowers.ItemSpectriteKeySword;
	public static ItemSpectriteSwordSpecial spectrite_sword_special = MazeTowers.ItemSpectriteSwordSpecial;
	public static ItemSpectriteKeySwordSpecial spectrite_key_sword_special = MazeTowers.ItemSpectriteKeySwordSpecial;
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
		GameRegistry.registerItem(diamond_rod, "diamond_rod");
		GameRegistry.registerItem(spectrite_rod, "spectrite_rod");
		GameRegistry.registerItem(spectrite_gem,
			"spectrite_gem");
		GameRegistry.registerItem(spectrite_orb,
			"spectrite_orb");
		GameRegistry.registerItem(explosive_arrow,
			"explosive_arrow");
		GameRegistry.registerItem(explosive_bow,
			"explosive_bow");
		GameRegistry.registerItem(spectrite_shovel,
			"spectrite_shovel");
		GameRegistry.registerItem(spectrite_shovel_special,
			"spectrite_shovel_special");
		GameRegistry.registerItem(spectrite_pickaxe,
			"spectrite_pickaxe");
		GameRegistry.registerItem(spectrite_pickaxe_special,
			"spectrite_pickaxe_special");
		GameRegistry.registerItem(spectrite_axe,
			"spectrite_axe");
		GameRegistry.registerItem(spectrite_axe_special,
			"spectrite_axe_special");
		GameRegistry.registerItem(spectrite_sword,
			"spectrite_sword");
		GameRegistry.registerItem(spectrite_key_sword,
			"spectrite_key_sword");
		GameRegistry.registerItem(spectrite_sword_special,
			"spectrite_sword_special");
		GameRegistry.registerItem(spectrite_key_sword_special,
			"spectrite_key_sword_special");
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
