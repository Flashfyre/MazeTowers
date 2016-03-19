package com.samuel.mazetowers.proxy;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.*;
import com.samuel.mazetowers.client.gui.GuiHandlerItemScanner;
import com.samuel.mazetowers.etc.ItemExtraTab;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModChestGen;
import com.samuel.mazetowers.init.ModCrafting;
import com.samuel.mazetowers.init.ModDispenserBehavior;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.init.ModTileEntities;
import com.samuel.mazetowers.init.ModWorldGen;
import com.samuel.mazetowers.items.*;
import com.samuel.mazetowers.tileentities.*;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers;

public class CommonProxy {
	
	static
    {
        FluidRegistry.enableUniversalBucket();
    }

	public void preInit(FMLPreInitializationEvent e) {
		MazeTowers.mazeTowers = new WorldGenMazeTowers();
		MazeTowers.tabExtra = new ItemExtraTab(CreativeTabs.getNextID(), "extraTab");

		MazeTowers.FluidChaoticSludge = new Fluid(
			"chaoticsludge", new ResourceLocation(
				"mazetowers:blocks/chaotic_sludge_still"),
			new ResourceLocation(
				"mazetowers:blocks/chaotic_sludge_flowing")).setLuminosity(5).setViscosity(4000);
		FluidRegistry.registerFluid(MazeTowers.FluidChaoticSludge);
		FluidRegistry.addBucketForFluid(MazeTowers.FluidChaoticSludge);
		MazeTowers.solidCircuits = new MaterialLogicSolid(
			MapColor.airColor);
		(MazeTowers.BlockHiddenPressurePlateWeighted = new BlockHiddenPressurePlateWeighted())
			.setUnlocalizedName("hidden_heavy_pressure_plate");
		(MazeTowers.BlockItemScanner = new BlockItemScanner()).setUnlocalizedName("item_scanner");
		(MazeTowers.BlockItemScannerGold = new BlockItemScannerGold()).setUnlocalizedName("item_scanner_gold");
		(MazeTowers.BlockMazeTowerThreshold = new BlockMazeTowerThreshold()).setUnlocalizedName("maze_tower_threshold");
		(MazeTowers.BlockMemoryPiston = new BlockMemoryPistonBase()).setUnlocalizedName("memory_piston");
		(MazeTowers.BlockMemoryPistonOff = new BlockMemoryPistonBaseOff()).setUnlocalizedName("memory_piston_off");
		MazeTowers.BlockMemoryPistonHead = new BlockMemoryPistonExtension(
			"memory_piston_head");
		MazeTowers.BlockMemoryPistonHeadOff = new BlockMemoryPistonExtensionOff(
			"memory_piston_head_off");
		MazeTowers.BlockMemoryPistonExtension = new BlockMemoryPistonMoving(
			"memory_piston_extension");
		MazeTowers.BlockMemoryPistonExtensionOff = new BlockMemoryPistonMovingOff(
			"memory_piston_extension_off");
		(MazeTowers.BlockHiddenButton = new BlockHiddenButton()).setUnlocalizedName("quartz_button");
		(MazeTowers.BlockIronChest = new BlockMineralChest(2))
    		.setUnlocalizedName("iron_chest");
    	(MazeTowers.BlockGoldChest = new BlockMineralChest(3))
    		.setUnlocalizedName("gold_chest");
    	(MazeTowers.BlockDiamondChest = new BlockMineralChest(4))
    		.setUnlocalizedName("diamond_chest");
    	(MazeTowers.BlockTrappedIronChest = new BlockMineralChest(5))
        	.setUnlocalizedName("iron_chest_trapped");
        (MazeTowers.BlockTrappedGoldChest = new BlockMineralChest(6))
        	.setUnlocalizedName("gold_chest_trapped");
        (MazeTowers.BlockTrappedDiamondChest = new BlockMineralChest(7))
        	.setUnlocalizedName("diamond_chest_trapped");
		(MazeTowers.BlockPackedIceStairs = new BlockExtraStairs(
			Blocks.packed_ice.getDefaultState()))
			.setUnlocalizedName("packed_ice_stairs");
		(MazeTowers.BlockPrismarineBrickStairs = new BlockExtraStairs(
			Blocks.prismarine.getStateFromMeta(1)))
			.setUnlocalizedName("prismarine_brick_stairs");
		(MazeTowers.BlockEndStoneStairs = new BlockExtraStairs(
			Blocks.end_stone.getDefaultState()))
			.setUnlocalizedName("end_stone_stairs");
		(MazeTowers.BlockObsidianStairs = new BlockExtraStairs(
			Blocks.obsidian.getDefaultState()))
			.setUnlocalizedName("obsidian_stairs");
		(MazeTowers.BlockBedrockStairs = new BlockExtraStairs(
			Blocks.bedrock.getDefaultState()))
			.setUnlocalizedName("bedrock_stairs");
		(MazeTowers.BlockSandstoneWall = new BlockExtraWall(
			Blocks.sandstone))
			.setUnlocalizedName("sandstone_wall");
		(MazeTowers.BlockRedSandstoneWall = new BlockExtraWall(
			Blocks.red_sandstone))
			.setUnlocalizedName("red_sandstone_wall");
		(MazeTowers.BlockStoneBrickWall = new BlockExtraWall(
			Blocks.stonebrick))
			.setUnlocalizedName("stone_brick_wall");
		(MazeTowers.BlockMossyStoneBrickWall = new BlockExtraWall(
			Blocks.stonebrick))
			.setUnlocalizedName("mossy_stone_brick_wall");
		(MazeTowers.BlockPackedIceWall = new BlockExtraWall(
			Blocks.packed_ice))
			.setUnlocalizedName("packed_ice_wall");
		(MazeTowers.BlockPrismarineBrickWall = new BlockExtraWall(
			Blocks.prismarine))
			.setUnlocalizedName("prismarine_brick_wall");
		(MazeTowers.BlockQuartzWall = new BlockExtraWall(
			Blocks.quartz_block))
			.setUnlocalizedName("quartz_wall");
		(MazeTowers.BlockEndStoneWall = new BlockExtraWall(
			Blocks.end_stone))
			.setUnlocalizedName("end_stone_wall");
		(MazeTowers.BlockPurpurWall = new BlockExtraWall(
			Blocks.end_stone))
			.setUnlocalizedName("purpur_wall");
		(MazeTowers.BlockObsidianWall = new BlockExtraWall(
			Blocks.obsidian))
			.setUnlocalizedName("obsidian_wall");
		(MazeTowers.BlockBedrockWall = new BlockExtraWall(
			Blocks.bedrock))
			.setUnlocalizedName("bedrock_wall");
		MazeTowers.BlockPrismarineDoor = new BlockExtraDoor(
			"prismarine_brick_door", 1.5F, 10.0F, 0);
		MazeTowers.BlockQuartzDoor = new BlockExtraDoor(
			"quartz_door", 0.8F, 2.4F, 1);
		MazeTowers.BlockEndStoneDoor = new BlockExtraDoor(
			"end_stone_door", 3.0F, 15.0F, 2);
		MazeTowers.BlockPurpurDoor = new BlockExtraDoor(
			"purpur_door", 3.0F, 15.0F, 3);
		MazeTowers.BlockObsidianDoor = new BlockExtraDoor(
			"obsidian_door", 50.0F, 2000.0F, 4);
		MazeTowers.BlockBedrockDoor = new BlockExtraDoor(
			"bedrock_door", -1.0F, 6000000.0F, 5);
		(MazeTowers.BlockLock = new BlockLock()).setUnlocalizedName("lock");
		(MazeTowers.BlockRedstoneClock = new BlockRedstoneClock(false))
			.setUnlocalizedName("redstone_clock");
		(MazeTowers.BlockRedstoneClockInverted = new BlockRedstoneClock(true))
			.setUnlocalizedName("redstone_clock_inverted");
		(MazeTowers.BlockExplosiveCreeperSkull = new BlockExplosiveCreeperSkull())
			.setUnlocalizedName("explosive_creeper_skull");
		(MazeTowers.BlockSpecialMobSpawner = new BlockSpecialMobSpawner())
			.setUnlocalizedName("special_mob_spawner");
		(MazeTowers.BlockVendorSpawner = new BlockVendorSpawner())
			.setUnlocalizedName("vendor_spawner");
		MazeTowers.BlockChaoticSludge = new BlockChaoticSludge(
			MazeTowers.FluidChaoticSludge, "chaoticsludge");
		MazeTowers.FluidChaoticSludge
			.setUnlocalizedName(MazeTowers.BlockChaoticSludge
				.getUnlocalizedName());
		(MazeTowers.ItemKey = new ItemKey()).setUnlocalizedName("key");
		(MazeTowers.ItemRAM = new ItemRAM()).setUnlocalizedName("ram");
		(MazeTowers.ItemPrismarineDoor = new ItemDoor(
			MazeTowers.BlockPrismarineDoor))
			.setUnlocalizedName("prismarine_brick_door_item")
			.setCreativeTab(MazeTowers.tabExtra);
		(MazeTowers.ItemQuartzDoor = new ItemDoor(
			MazeTowers.BlockQuartzDoor))
			.setUnlocalizedName("quartz_door_item")
			.setCreativeTab(MazeTowers.tabExtra);
		(MazeTowers.ItemEndStoneDoor = new ItemDoor(
			MazeTowers.BlockEndStoneDoor))
			.setUnlocalizedName("end_stone_door_item")
			.setCreativeTab(MazeTowers.tabExtra);
		(MazeTowers.ItemPurpurDoor = new ItemDoor(
			MazeTowers.BlockPurpurDoor))
			.setUnlocalizedName("purpur_door_item")
			.setCreativeTab(MazeTowers.tabExtra);
		(MazeTowers.ItemObsidianDoor = new ItemDoor(
			MazeTowers.BlockObsidianDoor))
			.setUnlocalizedName("obsidian_door_item")
			.setCreativeTab(MazeTowers.tabExtra);
		(MazeTowers.ItemBedrockDoor = new ItemDoor(
			MazeTowers.BlockBedrockDoor))
			.setUnlocalizedName("bedrock_door_item")
			.setCreativeTab(MazeTowers.tabExtra);
		(MazeTowers.ItemExplosiveArrow = new ItemExplosiveArrow())
			.setUnlocalizedName("explosive_arrow");
		(MazeTowers.ItemExplosiveBow = new ItemExplosiveBow())
			.setUnlocalizedName("explosive_bow");
		(MazeTowers.ItemExplosiveCreeperSkull = new ItemExplosiveCreeperSkull())
			.setUnlocalizedName("explosive_creeper_skull_item");
		(MazeTowers.ItemChaoticSludgeBucket = new ItemChaoticSludgeBucket())
			.setUnlocalizedName("chaotic_sludge_bucket");
		MazeTowers.TileEntityCircuitBreaker = new TileEntityCircuitBreaker();
		MazeTowers.TileEntityExplosiveCreeperSkull = new TileEntityExplosiveCreeperSkull();
		MazeTowers.TileEntityItemScanner = new TileEntityItemScanner();
		MazeTowers.TileEntityLock = new TileEntityLock();
		MazeTowers.TileEntityMazeTowerThreshold = new TileEntityMazeTowerThreshold();
		MazeTowers.TileEntityMemoryPiston = new TileEntityMemoryPiston();
		MazeTowers.TileEntityMemoryPistonMemory = new TileEntityMemoryPistonMemory();
		MazeTowers.TileEntityMineralChest = new TileEntityMineralChest();
		MazeTowers.TileEntitySpecialMobSpawner = new TileEntitySpecialMobSpawner();
		MazeTowers.TileEntityWebSpiderSpawner = new TileEntityWebSpiderSpawner();
		ModBlocks.createBlocks();
		ModItems.createItems();
		ModTileEntities.initTileEntities();
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
