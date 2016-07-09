package com.samuel.mazetowers.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModSounds {
	
	public static SoundEvent door_locked, door_unlock, correct, incorrect, primed, charge, explosion, fatality;
	public static final ResourceLocation door_locked_rl = new ResourceLocation("mazetowers:door_locked"),
	door_unlock_rl = new ResourceLocation("mazetowers:door_unlock"),
	correct_rl = new ResourceLocation("mazetowers:correct"),
	incorrect_rl = new ResourceLocation("mazetowers:incorrect"),
	primed_rl = new ResourceLocation("mazetowers:primed"),
	charge_rl = new ResourceLocation("mazetowers:charge"),
	explosion_rl = new ResourceLocation("mazetowers:explosion"),
	fatality_rl = new ResourceLocation("mazetowers:fatality");
	
	
	private ModSounds() { }
	
	public static void initSounds() {
		GameRegistry.register(door_locked = new SoundEvent(door_locked_rl).setRegistryName(door_locked_rl));
		GameRegistry.register(door_unlock = new SoundEvent(door_unlock_rl).setRegistryName(door_unlock_rl));
		GameRegistry.register(correct = new SoundEvent(correct_rl).setRegistryName(correct_rl));
		GameRegistry.register(incorrect = new SoundEvent(incorrect_rl).setRegistryName(incorrect_rl));
		GameRegistry.register(primed = new SoundEvent(primed_rl).setRegistryName(primed_rl));
		GameRegistry.register(charge = new SoundEvent(charge_rl).setRegistryName(charge_rl));
		GameRegistry.register(explosion = new SoundEvent(explosion_rl).setRegistryName(explosion_rl));
		GameRegistry.register(fatality = new SoundEvent(fatality_rl).setRegistryName(fatality_rl));
	}
}
