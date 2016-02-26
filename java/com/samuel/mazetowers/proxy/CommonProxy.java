package com.samuel.mazetowers.proxy;

import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockChaoticSludge;
import com.samuel.mazetowers.blocks.BlockExtraDoor;
import com.samuel.mazetowers.blocks.BlockExtraStairs;
import com.samuel.mazetowers.blocks.BlockExtraWall;
import com.samuel.mazetowers.blocks.BlockHiddenButton;
import com.samuel.mazetowers.blocks.BlockHiddenPressurePlateWeighted;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.blocks.BlockItemScannerGold;
import com.samuel.mazetowers.blocks.BlockMazeTowerThreshold;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBase;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBaseOff;
import com.samuel.mazetowers.blocks.BlockMemoryPistonExtension;
import com.samuel.mazetowers.blocks.BlockMemoryPistonExtensionOff;
import com.samuel.mazetowers.blocks.BlockMemoryPistonMoving;
import com.samuel.mazetowers.blocks.BlockMineralChest;
import com.samuel.mazetowers.client.gui.GuiHandlerItemScanner;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModChestGen;
import com.samuel.mazetowers.init.ModCrafting;
import com.samuel.mazetowers.init.ModDispenserBehavior;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.init.ModTileEntities;
import com.samuel.mazetowers.init.ModWorldGen;
import com.samuel.mazetowers.items.ItemExplosiveArrow;
import com.samuel.mazetowers.items.ItemExplosiveBow;
import com.samuel.mazetowers.tileentities.*;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		MazeTowers.mazeTowers = new WorldGenMazeTowers();

		MazeTowers.FluidChaoticSludge = new Fluid(
			"chaoticsludge", new ResourceLocation(
				"mazetowers:blocks/chaotic_sludge_still"),
			new ResourceLocation(
				"mazetowers:blocks/chaotic_sludge_flowing"));
		FluidRegistry
			.registerFluid(MazeTowers.FluidChaoticSludge);
		MazeTowers.solidCircuits = new MaterialLogicSolid(
			MapColor.airColor);
		MazeTowers.BlockHiddenPressurePlateWeighted = new BlockHiddenPressurePlateWeighted(
			"hidden_heavy_pressure_plate");
		MazeTowers.BlockItemScanner = new BlockItemScanner()
			.setUnlocalizedName("item_scanner");
		MazeTowers.BlockItemScannerGold = new BlockItemScannerGold()
			.setUnlocalizedName("item_scanner_gold");
		MazeTowers.BlockMazeTowerThreshold = new BlockMazeTowerThreshold()
			.setUnlocalizedName("maze_tower_threshold");
		MazeTowers.BlockMemoryPiston = new BlockMemoryPistonBase(
			"memory_piston");
		MazeTowers.BlockMemoryPistonOff = new BlockMemoryPistonBaseOff(
			"memory_piston_off");
		MazeTowers.BlockMemoryPistonHead = new BlockMemoryPistonExtension(
			"memory_piston_head");
		MazeTowers.BlockMemoryPistonHeadOff = new BlockMemoryPistonExtensionOff(
			"memory_piston_head_off");
		MazeTowers.BlockMemoryPistonExtension = new BlockMemoryPistonMoving(
			"memory_piston_extension");
		MazeTowers.BlockMemoryPistonExtensionOff = new BlockMemoryPistonMoving(
			"memory_piston_extension_off");
		MazeTowers.BlockHiddenButton = new BlockHiddenButton(
			"quartz_button");
		MazeTowers.BlockIronChest = new BlockMineralChest(0)
			.setUnlocalizedName("iron_chest");
		MazeTowers.BlockGoldChest = new BlockMineralChest(1)
			.setUnlocalizedName("gold_chest");
		MazeTowers.BlockDiamondChest = new BlockMineralChest(
			2).setUnlocalizedName("diamond_chest");
		MazeTowers.BlockPackedIceStairs = new BlockExtraStairs(
			Blocks.packed_ice.getDefaultState())
			.setUnlocalizedName("packed_ice_stairs");
		MazeTowers.BlockPrismarineBrickStairs = new BlockExtraStairs(
			Blocks.prismarine.getStateFromMeta(1))
			.setUnlocalizedName("prismarine_brick_stairs");
		MazeTowers.BlockEndStoneStairs = new BlockExtraStairs(
			Blocks.end_stone.getDefaultState())
			.setUnlocalizedName("end_stone_stairs");
		MazeTowers.BlockObsidianStairs = new BlockExtraStairs(
			Blocks.obsidian.getDefaultState())
			.setUnlocalizedName("obsidian_stairs");
		MazeTowers.BlockBedrockStairs = new BlockExtraStairs(
			Blocks.bedrock.getDefaultState())
			.setUnlocalizedName("bedrock_stairs");
		MazeTowers.BlockSandstoneWall = new BlockExtraWall(
			Blocks.sandstone)
			.setUnlocalizedName("sandstone_wall");
		MazeTowers.BlockRedSandstoneWall = new BlockExtraWall(
			Blocks.red_sandstone)
			.setUnlocalizedName("red_sandstone_wall");
		MazeTowers.BlockStoneBrickWall = new BlockExtraWall(
			Blocks.stonebrick)
			.setUnlocalizedName("stone_brick_wall");
		MazeTowers.BlockPackedIceWall = new BlockExtraWall(
			Blocks.packed_ice)
			.setUnlocalizedName("packed_ice_wall");
		MazeTowers.BlockPrismarineBrickWall = new BlockExtraWall(
			Blocks.prismarine)
			.setUnlocalizedName("prismarine_brick_wall");
		MazeTowers.BlockEndStoneWall = new BlockExtraWall(
			Blocks.end_stone)
			.setUnlocalizedName("end_stone_wall");
		MazeTowers.BlockQuartzWall = new BlockExtraWall(
			Blocks.quartz_block)
			.setUnlocalizedName("quartz_wall");
		MazeTowers.BlockObsidianWall = new BlockExtraWall(
			Blocks.obsidian)
			.setUnlocalizedName("obsidian_wall");
		MazeTowers.BlockBedrockWall = new BlockExtraWall(
			Blocks.bedrock)
			.setUnlocalizedName("bedrock_wall");
		MazeTowers.BlockEndStoneDoor = new BlockExtraDoor(
			"end_stone_door", 5.0F, 15.0F, 0);
		MazeTowers.BlockQuartzDoor = new BlockExtraDoor(
			"quartz_door", 5.0F, 15.0F, 1);
		MazeTowers.BlockObsidianDoor = new BlockExtraDoor(
			"obsidian_door", 50.0F, 2000.0F, 2);
		MazeTowers.BlockBedrockDoor = new BlockExtraDoor(
			"bedrock_door", -1.0F, 6000000.0F, 3);
		MazeTowers.BlockChaoticSludge = new BlockChaoticSludge(
			MazeTowers.FluidChaoticSludge, "chaoticsludge");
		MazeTowers.FluidChaoticSludge
			.setUnlocalizedName(MazeTowers.BlockChaoticSludge
				.getUnlocalizedName());
		MazeTowers.ItemEndStoneDoor = new ItemDoor(
			MazeTowers.BlockEndStoneDoor)
			.setUnlocalizedName("end_stone_door_item");
		MazeTowers.ItemQuartzDoor = new ItemDoor(
			MazeTowers.BlockQuartzDoor)
			.setUnlocalizedName("quartz_door_item");
		MazeTowers.ItemObsidianDoor = new ItemDoor(
			MazeTowers.BlockObsidianDoor)
			.setUnlocalizedName("obsidian_door_item");
		MazeTowers.ItemBedrockDoor = new ItemDoor(
			MazeTowers.BlockBedrockDoor)
			.setUnlocalizedName("bedrock_door_item");
		MazeTowers.ItemExplosiveArrow = new ItemExplosiveArrow()
			.setUnlocalizedName("explosive_arrow");
		MazeTowers.ItemExplosiveBow = new ItemExplosiveBow()
			.setUnlocalizedName("explosive_bow");
		MazeTowers.TileEntityBlockProtect = new TileEntityBlockProtect();
		MazeTowers.TileEntityCircuitBreaker = new TileEntityCircuitBreaker();
		MazeTowers.TileEntityItemScanner = new TileEntityItemScanner();
		MazeTowers.TileEntityMazeTowerThreshold = new TileEntityMazeTowerThreshold();
		MazeTowers.TileEntityMemoryPiston = new TileEntityMemoryPiston();
		MazeTowers.TileEntityMemoryPistonMemory = new TileEntityMemoryPistonMemory();
		MazeTowers.TileEntityMineralChest = new TileEntityMineralChest();
		MazeTowers.TileEntityWebSpiderSpawner = new TileEntityWebSpiderSpawner();

		ModBlocks.createBlocks();
		ModItems.createItems();
		ModTileEntities.initTileEntities();
		ModChestGen.initChestGen();
		ModDispenserBehavior.initDispenserBehavior();
		ModWorldGen.initWorldGen();

		FMLCommonHandler.instance().bus().register(
			MazeTowers.instance);
		MinecraftForge.EVENT_BUS
			.register(MazeTowers.instance);
	}

	public void init(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(
			MazeTowers.instance,
			new GuiHandlerItemScanner());
		ModCrafting.initCrafting();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
