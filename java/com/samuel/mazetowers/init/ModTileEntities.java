package com.samuel.mazetowers.init;

import com.samuel.mazetowers.tileentities.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {
	
	public static Class circuit_breaker;
	public static Class item_scanner;
	public static Class maze_tower_threshold;
	public static Class memory_piston;
	public static Class memory_piston_memory;
	public static Class mineral_chest;
	public static Class web_spider_spawner;
	
	public static void initTileEntities() {
		GameRegistry.registerTileEntity(circuit_breaker = TileEntityMazeTowerThreshold.class, "circuit_breaker");
		GameRegistry.registerTileEntity(item_scanner = TileEntityItemScanner.class, "item_scanner");
		GameRegistry.registerTileEntity(maze_tower_threshold = TileEntityMazeTowerThreshold.class, "maze_tower_threshold");
		GameRegistry.registerTileEntity(memory_piston = TileEntityMemoryPiston.class, "memory_piston");
		GameRegistry.registerTileEntity(memory_piston_memory = TileEntityMemoryPistonMemory.class, "memory_piston_memory");
		GameRegistry.registerTileEntity(mineral_chest = TileEntityMineralChest.class, "mineral_chest");
		GameRegistry.registerTileEntity(web_spider_spawner = TileEntityWebSpiderSpawner.class, "web_spider_spawner");
	}
}