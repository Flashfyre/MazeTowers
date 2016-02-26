package com.samuel.mazetowers.init;

import net.minecraftforge.fml.common.registry.EntityRegistry;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.entities.EntityExplosiveArrow;
import com.samuel.mazetowers.entities.EntitySmallUltravioletFireball;
import com.samuel.mazetowers.entities.EntityUltravioletBlaze;

public class ModEntities {

	private static int entityID = 0;

	public static void initEntities(MazeTowers mod) {
		EntityRegistry.registerModEntity(
			EntityExplosiveArrow.class, "ExplosiveArrow",
			entityID++, mod, 128, 1, true);
		EntityRegistry.registerModEntity(
			EntityUltravioletBlaze.class,
			"UltravioletBlaze", entityID++, mod, 128, 1,
			true);
		EntityRegistry.registerModEntity(
			EntitySmallUltravioletFireball.class,
			"SmallUltravioletFireball", entityID++, mod,
			128, 1, true);
	}
}
