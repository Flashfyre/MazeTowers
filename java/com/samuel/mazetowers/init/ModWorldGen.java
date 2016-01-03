package com.samuel.mazetowers.init;

import com.samuel.mazetowers.MazeTowers;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModWorldGen {

	public static void initWorldGen() {
		GameRegistry.registerWorldGenerator(MazeTowers.mazeTowers, 1618);
	}
}