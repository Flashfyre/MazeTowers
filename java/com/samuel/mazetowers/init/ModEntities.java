package com.samuel.mazetowers.init;

import net.minecraftforge.fml.common.registry.EntityRegistry;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.entity.EntityExplosiveArrow;
import com.samuel.mazetowers.entity.EntityExplosiveCreeper;
import com.samuel.mazetowers.entity.EntityVillagerVendor;

public class ModEntities {

	private static int entityID = 0;

	public static void initEntities(MazeTowers mod) {
		EntityRegistry.registerModEntity(
			EntityExplosiveArrow.class, "ExplosiveArrow",
			entityID++, mod, 128, 1, true);
		EntityRegistry.registerModEntity(
			EntityExplosiveCreeper.class,
			"ExplosiveCreeper", entityID++, mod,
			128, 1, true, 8388608, 16736256);
		EntityRegistry.registerModEntity(
			EntityVillagerVendor.class,
			"SpecialVillager", entityID++, mod,
			128, 1, true);
	}
}
