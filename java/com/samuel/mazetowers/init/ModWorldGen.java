package com.samuel.mazetowers.init;

import net.minecraftforge.fml.common.registry.GameRegistry;

import com.samuel.mazetowers.MazeTowers;

public class ModWorldGen {

	public static void initWorldGen() {
		GameRegistry.registerWorldGenerator(
			MazeTowers.spectrite, 618);
		GameRegistry.registerWorldGenerator(
			MazeTowers.mazeTowers, 619);
	
	}
}