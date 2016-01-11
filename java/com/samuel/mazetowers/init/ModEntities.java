package com.samuel.mazetowers.init;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.entities.EntitySmallUltravioletFireball;
import com.samuel.mazetowers.entities.EntityUltravioletBlaze;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModEntities {
	
	private static int entityID = 0;

	public static void initEntities(MazeTowers mod) {
		EntityRegistry.registerModEntity(EntityUltravioletBlaze.class,
			"UltravioletBlaze", entityID++, mod, 128, 1, true);
		EntityRegistry.registerModEntity(EntitySmallUltravioletFireball.class,
			"SmallUltravioletFireball", entityID++, mod, 128, 1, true);
    }
}
