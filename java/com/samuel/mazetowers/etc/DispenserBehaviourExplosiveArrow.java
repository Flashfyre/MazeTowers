package com.samuel.mazetowers.etc;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.samuel.mazetowers.entity.EntityExplosiveArrow;

final class DispenserBehaviorExplosiveArrow extends
	BehaviorProjectileDispense {
	/**
	 * Return the projectile entity spawned by this dispense behavior.
	 */
	@Override
	protected IProjectile getProjectileEntity(
		World par1World, IPosition par2IPosition, ItemStack stack) {
		return new EntityExplosiveArrow(par1World,
			par2IPosition.getX(), par2IPosition.getY(),
			par2IPosition.getZ());
	}
}
