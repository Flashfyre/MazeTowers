package com.samuel.mazetowers.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.MazeTowers;

public class ModBlocks {

	public static Block hiddenPressurePlateWeighted;
	public static Block itemScanner;
	public static Block itemScannerGold;
	public static Block mazeTowerThreshold;
	public static Block memoryPiston;
	public static Block memoryPistonOff;
	public static Block memoryPistonHead;
	public static Block memoryPistonHeadOff;
	public static Block memoryPistonExtension;
	public static Block memoryPistonExtensionOff;
	public static Block mineralChestIron;
	public static Block mineralChestGold;
	public static Block mineralChestDiamond;
	public static Block packedIceStairs;
	public static Block prismarineBrickStairs;
	public static Block endStoneStairs;
	public static Block obsidianStairs;
	public static Block bedrockStairs;
	public static Block sandstoneWall;
	public static Block redSandstoneWall;
	public static Block stoneBrickWall;
	public static Block packedIceWall;
	public static Block prismarineBrickWall;
	public static Block quartzWall;
	public static Block endStoneWall;
	public static Block obsidianWall;
	public static Block bedrockWall;
	public static Block hiddenButton;
	public static Block resistantDoorEndStone;
	public static Block resistantDoorQuartz;
	public static Block resistantDoorObsidian;
	public static Block resistantDoorBedrock;
	public static Block chaoticSludge;

	public static void createBlocks() {
		GameRegistry
			.registerBlock(
				hiddenPressurePlateWeighted = MazeTowers.BlockHiddenPressurePlateWeighted,
				"hidden_heavy_pressure_plate");
		GameRegistry.registerBlock(
			itemScanner = MazeTowers.BlockItemScanner,
			"item_scanner");
		GameRegistry
			.registerBlock(
				itemScannerGold = MazeTowers.BlockItemScannerGold,
				"item_scanner_gold");
		GameRegistry
			.registerBlock(
				mazeTowerThreshold = MazeTowers.BlockMazeTowerThreshold,
				"maze_tower_threshold");
		GameRegistry.registerBlock(
			memoryPiston = MazeTowers.BlockMemoryPiston,
			"memory_piston");
		GameRegistry
			.registerBlock(
				memoryPistonOff = MazeTowers.BlockMemoryPistonOff,
				"memory_piston_off");
		GameRegistry
			.registerBlock(
				packedIceStairs = MazeTowers.BlockPackedIceStairs,
				"packed_ice_stairs");
		GameRegistry
			.registerBlock(
				prismarineBrickStairs = MazeTowers.BlockPrismarineBrickStairs,
				"prismarine_brick_stairs");
		GameRegistry
			.registerBlock(
				endStoneStairs = MazeTowers.BlockEndStoneStairs,
				"end_stone_stairs");
		GameRegistry
			.registerBlock(
				obsidianStairs = MazeTowers.BlockObsidianStairs,
				"obsidian_stairs");
		GameRegistry.registerBlock(
			bedrockStairs = MazeTowers.BlockBedrockStairs,
			"bedrock_stairs");
		GameRegistry.registerBlock(
			sandstoneWall = MazeTowers.BlockSandstoneWall,
			"sandstone_wall");
		GameRegistry
			.registerBlock(
				redSandstoneWall = MazeTowers.BlockRedSandstoneWall,
				"red_sandstone_wall");
		GameRegistry
			.registerBlock(
				stoneBrickWall = MazeTowers.BlockStoneBrickWall,
				"stone_brick_wall");
		GameRegistry.registerBlock(
			packedIceWall = MazeTowers.BlockPackedIceWall,
			"packed_ice_wall");
		GameRegistry
			.registerBlock(
				prismarineBrickWall = MazeTowers.BlockPrismarineBrickWall,
				"prismarine_brick_wall");
		GameRegistry.registerBlock(
			quartzWall = MazeTowers.BlockQuartzWall,
			"quartz_wall");
		GameRegistry.registerBlock(
			endStoneWall = MazeTowers.BlockEndStoneWall,
			"end_stone_wall");
		GameRegistry.registerBlock(
			obsidianWall = MazeTowers.BlockObsidianWall,
			"obsidian_wall");
		GameRegistry.registerBlock(
			bedrockWall = MazeTowers.BlockBedrockWall,
			"bedrock_wall");
		GameRegistry
			.registerBlock(
				memoryPistonHead = MazeTowers.BlockMemoryPistonHead,
				"memory_piston_head");
		GameRegistry
			.registerBlock(
				memoryPistonHeadOff = MazeTowers.BlockMemoryPistonHeadOff,
				"memory_piston_head_off");
		GameRegistry
			.registerBlock(
				memoryPistonExtension = MazeTowers.BlockMemoryPistonExtension,
				"memory_piston_extension");
		GameRegistry
			.registerBlock(
				memoryPistonExtensionOff = MazeTowers.BlockMemoryPistonExtensionOff,
				"memory_piston_extension_off");
		GameRegistry.registerBlock(
			mineralChestIron = MazeTowers.BlockIronChest,
			"iron_chest");
		GameRegistry.registerBlock(
			mineralChestGold = MazeTowers.BlockGoldChest,
			"gold_chest");
		GameRegistry
			.registerBlock(
				mineralChestDiamond = MazeTowers.BlockDiamondChest,
				"diamond_chest");
		GameRegistry.registerBlock(
			hiddenButton = MazeTowers.BlockHiddenButton,
			"quartz_button");
		GameRegistry
			.registerBlock(
				resistantDoorEndStone = MazeTowers.BlockEndStoneDoor,
				"end_stone_door");
		GameRegistry
			.registerBlock(
				resistantDoorQuartz = MazeTowers.BlockQuartzDoor,
				"quartz_door");
		GameRegistry
			.registerBlock(
				resistantDoorObsidian = MazeTowers.BlockObsidianDoor,
				"obsidian_door");
		GameRegistry
			.registerBlock(
				resistantDoorBedrock = MazeTowers.BlockBedrockDoor,
				"bedrock_door");
		GameRegistry.registerBlock(
			chaoticSludge = MazeTowers.BlockChaoticSludge,
			"chaotic_sludge");
	}
}