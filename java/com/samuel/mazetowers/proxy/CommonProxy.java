package com.samuel.mazetowers.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.*;
import com.samuel.mazetowers.init.*;
import com.samuel.mazetowers.tileentities.*;
import com.samuel.mazetowers.worldgen.*;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		MazeTowers.mazeTowers = new WorldGenMazeTowers();
		
		MazeTowers.BlockHiddenPressurePlateWeighted =
			new BlockHiddenPressurePlateWeighted("hidden_heavy_pressure_plate");
		MazeTowers.BlockMemoryPiston = new BlockMemoryPistonBase("memory_piston");
		MazeTowers.BlockMemoryPistonHead = new BlockMemoryPistonExtension("memory_piston_head");
		MazeTowers.BlockMemoryPistonExtension = new BlockMemoryPistonMoving("memory_piston_extension");
		MazeTowers.BlockQuartzButton = new BlockQuartzButton("quartz_button");
		MazeTowers.tileEntityMazeTowerThreshold = new TileEntityMazeTowerThreshold();
		MazeTowers.tileEntityMemoryPiston = new TileEntityMemoryPiston();
		MazeTowers.tileEntityCircuitBreaker = new TileEntityCircuitBreaker();
		
		ModBlocks.createBlocks();
		ModItems.createItems();
		ModTileEntities.initTileEntities();
		ModChestGen.initChestGen();
		ModWorldGen.initWorldGen();
		
		FMLCommonHandler.instance().bus().register(MazeTowers.instance);
		MinecraftForge.EVENT_BUS.register(MazeTowers.instance);
	}

	public void init(FMLInitializationEvent e) {
		ModCrafting.initCrafting();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
