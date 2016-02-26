package com.samuel.mazetowers.init;

import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.tileentities.TileEntityBlockProtect;
import com.samuel.mazetowers.tileentities.TileEntityChaoticSludgeToxin;
import com.samuel.mazetowers.tileentities.TileEntityCircuitBreaker;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityMazeTowerThreshold;
import com.samuel.mazetowers.tileentities.TileEntityMemoryPiston;
import com.samuel.mazetowers.tileentities.TileEntityMemoryPistonMemory;
import com.samuel.mazetowers.tileentities.TileEntityMineralChest;
import com.samuel.mazetowers.tileentities.TileEntityWebSpiderSpawner;

public class ModTileEntities {

	public static Class block_protect;
	public static Class chaotic_sludge_toxin;
	public static Class circuit_breaker;
	public static Class item_scanner;
	public static Class maze_tower_threshold;
	public static Class memory_piston;
	public static Class memory_piston_memory;
	public static Class mineral_chest;
	public static Class web_spider_spawner;

	public static void initTileEntities() {
		GameRegistry.registerTileEntity(
			block_protect = TileEntityBlockProtect.class,
			"block_protect");
		GameRegistry
			.registerTileEntity(
				chaotic_sludge_toxin = TileEntityChaoticSludgeToxin.class,
				"chaotic_sludge_toxin");
		GameRegistry
			.registerTileEntity(
				circuit_breaker = TileEntityCircuitBreaker.class,
				"circuit_breaker");
		GameRegistry.registerTileEntity(
			item_scanner = TileEntityItemScanner.class,
			"item_scanner");
		GameRegistry
			.registerTileEntity(
				maze_tower_threshold = TileEntityMazeTowerThreshold.class,
				"maze_tower_threshold");
		GameRegistry.registerTileEntity(
			memory_piston = TileEntityMemoryPiston.class,
			"memory_piston");
		GameRegistry
			.registerTileEntity(
				memory_piston_memory = TileEntityMemoryPistonMemory.class,
				"memory_piston_memory");
		GameRegistry.registerTileEntity(
			mineral_chest = TileEntityMineralChest.class,
			"mineral_chest");
		GameRegistry
			.registerTileEntity(
				web_spider_spawner = TileEntityWebSpiderSpawner.class,
				"web_spider_spawner");
	}
}