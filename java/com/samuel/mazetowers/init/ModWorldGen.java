package com.samuel.mazetowers.init;

import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.MazeTowers;

public class ModWorldGen {

	public static void initWorldGen() {
		GameRegistry.registerWorldGenerator(
			MazeTowers.mazeTowers, 1618);
	}
}