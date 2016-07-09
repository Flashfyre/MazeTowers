package com.samuel.mazetowers.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.entity.EntityExplosiveArrow;

public class ModDispenserBehavior {

	public static void initDispenserBehavior() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
			MazeTowers.ItemExplosiveArrow,
			new BehaviorProjectileDispense() {
				@Override
				protected IProjectile getProjectileEntity(World worldIn,
						IPosition position, ItemStack stack) {
					EntityExplosiveArrow entityarrow = new EntityExplosiveArrow(
						worldIn, position.getX(), position
						.getY(), position.getZ());
					entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
					return entityarrow;
				}
			});
	}
}
