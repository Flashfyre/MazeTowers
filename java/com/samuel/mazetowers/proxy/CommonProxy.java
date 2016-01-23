package com.samuel.mazetowers.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.*;
import com.samuel.mazetowers.client.gui.GuiHandlerItemScanner;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.init.*;
import com.samuel.mazetowers.tileentities.*;
import com.samuel.mazetowers.worldgen.*;
import com.samuel.mazetowers.worldgen.biomes.*;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		MazeTowers.mazeTowers = new WorldGenMazeTowers();
		
		MazeTowers.solidCircuits = new MaterialLogicSolid(MapColor.airColor);
		MazeTowers.BlockHiddenPressurePlateWeighted =
			new BlockHiddenPressurePlateWeighted("hidden_heavy_pressure_plate");
		MazeTowers.BlockItemScanner = new BlockItemScanner().setUnlocalizedName("item_scanner");
		MazeTowers.BlockItemScannerGold = new BlockItemScannerGold().setUnlocalizedName("item_scanner_gold");
		MazeTowers.BlockMemoryPiston = new BlockMemoryPistonBase("memory_piston");
		MazeTowers.BlockMemoryPistonOff = new BlockMemoryPistonBaseOff("memory_piston_off");
		MazeTowers.BlockMemoryPistonHead = new BlockMemoryPistonExtension("memory_piston_head");
		MazeTowers.BlockMemoryPistonHeadOff = new BlockMemoryPistonExtensionOff("memory_piston_head_off");
		MazeTowers.BlockMemoryPistonExtension = new BlockMemoryPistonMoving("memory_piston_extension");
		MazeTowers.BlockMemoryPistonExtensionOff = new BlockMemoryPistonMoving("memory_piston_extension_off");
		MazeTowers.BlockHiddenButton = new BlockHiddenButton("quartz_button");
		MazeTowers.BlockIronChest = new BlockMineralChest(0).setUnlocalizedName("iron_chest");
		MazeTowers.BlockGoldChest = new BlockMineralChest(1).setUnlocalizedName("gold_chest");
		MazeTowers.BlockDiamondChest = new BlockMineralChest(2).setUnlocalizedName("diamond_chest");
		MazeTowers.BlockEndStoneDoor = new BlockResistantDoor("end_stone_door", 5.0F, 15.0F, 0);
		MazeTowers.BlockQuartzDoor = new BlockResistantDoor("quartz_door", 5.0F, 15.0F, 1);
		MazeTowers.BlockObsidianDoor = new BlockResistantDoor("obsidian_door", 50.0F, 2000.0F, 2);
		MazeTowers.BlockBedrockDoor = new BlockResistantDoor("bedrock_door", -1.0F, 6000000.0F, 3);
		MazeTowers.ItemEndStoneDoor = new ItemDoor(MazeTowers.BlockEndStoneDoor).setUnlocalizedName("end_stone_door_item");
		MazeTowers.ItemQuartzDoor = new ItemDoor(MazeTowers.BlockQuartzDoor).setUnlocalizedName("quartz_door_item");
		MazeTowers.ItemObsidianDoor = new ItemDoor(MazeTowers.BlockObsidianDoor).setUnlocalizedName("obsidian_door_item");
		MazeTowers.ItemBedrockDoor = new ItemDoor(MazeTowers.BlockBedrockDoor).setUnlocalizedName("bedrock_door_item");
		MazeTowers.TileEntityCircuitBreaker = new TileEntityCircuitBreaker();
		MazeTowers.TileEntityItemScanner = new TileEntityItemScanner();
		MazeTowers.TileEntityMazeTowerThreshold = new TileEntityMazeTowerThreshold();
		MazeTowers.TileEntityMemoryPiston = new TileEntityMemoryPiston();
		MazeTowers.TileEntityMemoryPistonMemory = new TileEntityMemoryPistonMemory();
		MazeTowers.TileEntityMineralChest = new TileEntityMineralChest();
		MazeTowers.TileEntityWebSpiderSpawner = new TileEntityWebSpiderSpawner();
		(MazeTowers.biomeGenMazeTowerLv1 = new BiomeGenMazeTowerLv1(212)).setBiomeName("Maze Tower Lv1");
		(MazeTowers.biomeGenMazeTowerLv7 = new BiomeGenMazeTowerLv7(219)).setBiomeName("Maze Tower Lv7");
		BiomeDictionary.registerBiomeType(MazeTowers.biomeGenMazeTowerLv1, BiomeDictionary.Type.DENSE);
		BiomeDictionary.registerBiomeType(MazeTowers.biomeGenMazeTowerLv7, BiomeDictionary.Type.DENSE);
		BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeEntry(MazeTowers.biomeGenMazeTowerLv1, 0));
		BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeEntry(MazeTowers.biomeGenMazeTowerLv7, 0));
		
		ModBlocks.createBlocks();
		ModItems.createItems();
		ModTileEntities.initTileEntities();
		ModChestGen.initChestGen();
		ModWorldGen.initWorldGen();
		
		FMLCommonHandler.instance().bus().register(MazeTowers.instance);
		MinecraftForge.EVENT_BUS.register(MazeTowers.instance);
	}

	public void init(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(MazeTowers.instance, new GuiHandlerItemScanner());
		ModCrafting.initCrafting();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
