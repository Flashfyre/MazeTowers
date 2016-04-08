package com.samuel.mazetowers.init;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ModSounds {
	
	public static SoundEvent door_locked, door_unlock, correct, incorrect, primed, charge, fatality;
	
	private ModSounds() { }
	
	public static void initSounds() {
		try {
			 Method m = ReflectionHelper.findMethod(SoundEvent.class, null,
					new String[] { "registerSound", "func_187502_a" }, String.class);
			 m.invoke(null, "mazetowers:door_locked");
			 m.invoke(null, "mazetowers:door_unlock");
			 m.invoke(null, "mazetowers:correct");
			 m.invoke(null, "mazetowers:incorrect");
			 m.invoke(null, "mazetowers:primed");
			 m.invoke(null, "mazetowers:charge");
			 m.invoke(null, "mazetowers:fatality");
		 } catch (InvocationTargetException e) {
			 e.printStackTrace();
		 } catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			door_locked = SoundEvent.soundEventRegistry.getObject(new ResourceLocation("mazetowers:door_locked"));
			door_unlock = SoundEvent.soundEventRegistry.getObject(new ResourceLocation("mazetowers:door_unlock"));
			correct = SoundEvent.soundEventRegistry.getObject(new ResourceLocation("mazetowers:correct"));
			incorrect = SoundEvent.soundEventRegistry.getObject(new ResourceLocation("mazetowers:incorrect"));
			primed = SoundEvent.soundEventRegistry.getObject(new ResourceLocation("mazetowers:primed"));
			charge = SoundEvent.soundEventRegistry.getObject(new ResourceLocation("mazetowers:charge"));
			fatality = SoundEvent.soundEventRegistry.getObject(new ResourceLocation("mazetowers:fatality"));
		}
	}
}
