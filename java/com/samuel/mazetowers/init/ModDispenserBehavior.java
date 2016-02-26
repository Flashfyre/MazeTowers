package com.samuel.mazetowers.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.entities.EntityExplosiveArrow;

public class ModDispenserBehavior {

	public static void initDispenserBehavior() {
		BlockDispenser.dispenseBehaviorRegistry.putObject(
			MazeTowers.ItemExplosiveArrow,
			new BehaviorProjectileDispense() {

				@Override
				/**
				 * Return the projectile entity spawned by this dispense behavior.
				 */
				protected IProjectile getProjectileEntity(
					World worldIn, IPosition position) {
					EntityExplosiveArrow entityarrow = new EntityExplosiveArrow(
						worldIn, position.getX(), position
							.getY(), position.getZ());
					entityarrow.canBePickedUp = 0;
					return entityarrow;
				}
			});
	}
}
