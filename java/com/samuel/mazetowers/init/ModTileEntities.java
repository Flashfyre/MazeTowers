package com.samuel.mazetowers.init;

import com.samuel.mazetowers.tileentities.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {
	
	public static Class maze_tower_threshold;
	public static Class memory_piston;
	public static Class circuit_breaker;
	
	public static void initTileEntities() {
		GameRegistry.registerTileEntity(maze_tower_threshold = TileEntityMazeTowerThreshold.class, "maze_tower_threshold");
		GameRegistry.registerTileEntity(memory_piston = TileEntityMemoryPiston.class, "memory_piston");
		GameRegistry.registerTileEntity(circuit_breaker = TileEntityMazeTowerThreshold.class, "circuit_breaker");
	}
}