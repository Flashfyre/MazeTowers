package com.samuel.mazetowers.init;

import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.tileentity.TileEntityChaoticSludgeToxin;
import com.samuel.mazetowers.tileentity.TileEntityCircuitBreaker;
import com.samuel.mazetowers.tileentity.TileEntityExplosiveCreeperSkull;
import com.samuel.mazetowers.tileentity.TileEntityItemScanner;
import com.samuel.mazetowers.tileentity.TileEntityLock;
import com.samuel.mazetowers.tileentity.TileEntityMazeTowerThreshold;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPiston;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPistonMemory;
import com.samuel.mazetowers.tileentity.TileEntityMineralChest;
import com.samuel.mazetowers.tileentity.TileEntityRedstoneClock;
import com.samuel.mazetowers.tileentity.TileEntitySpecialMobSpawner;
import com.samuel.mazetowers.tileentity.TileEntityVendorSpawner;
import com.samuel.mazetowers.tileentity.TileEntityWebSpiderSpawner;

public class ModTileEntities {

	public static Class block_protect;
	public static Class chaotic_sludge_toxin;
	public static Class circuit_breaker;
	public static Class explosive_creeper_skull;
	public static Class item_scanner;
	public static Class lock;
	public static Class maze_tower_threshold;
	public static Class memory_piston;
	public static Class memory_piston_memory;
	public static Class mineral_chest;
	public static Class redstone_clock;
	public static Class special_mob_spawner;
	public static Class vendor_spawner;
	public static Class web_spider_spawner;

	public static void initTileEntities() {
		GameRegistry
			.registerTileEntity(
				chaotic_sludge_toxin = TileEntityChaoticSludgeToxin.class,
				"chaotic_sludge_toxin");
		GameRegistry
			.registerTileEntity(
				circuit_breaker = TileEntityCircuitBreaker.class,
				"circuit_breaker");
		GameRegistry.registerTileEntity(
			explosive_creeper_skull = TileEntityExplosiveCreeperSkull.class,
			"explosive_creeper_skull");
		GameRegistry.registerTileEntity(item_scanner = TileEntityItemScanner.class,
			"item_scanner");
		GameRegistry.registerTileEntity(lock = TileEntityLock.class, "lock");
		GameRegistry.registerTileEntity(
				maze_tower_threshold = TileEntityMazeTowerThreshold.class,
				"maze_tower_threshold");
		GameRegistry.registerTileEntity(
			memory_piston = TileEntityMemoryPiston.class,
			"memory_piston");
		GameRegistry.registerTileEntity(
			memory_piston_memory = TileEntityMemoryPistonMemory.class,
			"memory_piston_memory");
		GameRegistry.registerTileEntity(
			mineral_chest = TileEntityMineralChest.class,
			"mineral_chest");
		GameRegistry.registerTileEntity(
			redstone_clock = TileEntityRedstoneClock.class, "redstone_clock");
		GameRegistry
		.registerTileEntity(
			special_mob_spawner = TileEntitySpecialMobSpawner.class,
			"special_mob_spawner");
		GameRegistry.registerTileEntity(vendor_spawner = TileEntityVendorSpawner.class,
			"vendor_spawner");
		GameRegistry
    		.registerTileEntity(
    			web_spider_spawner = TileEntityWebSpiderSpawner.class,
    			"web_spider_spawner");
	}
}