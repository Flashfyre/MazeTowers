package com.samuel.mazetowers.eventhandlers;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.client.renderer.entity.layers.LayerSpectriteArmor;
import com.samuel.mazetowers.etc.MTUtils;

public class MazeTowersRenderEventHandler {
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onRenderPlayer(RenderPlayerEvent.Pre e) {
		EntityLivingBase entity = e.getEntityLiving();
    	Field layerRenderers = MTUtils.findObfuscatedField(RenderLivingBase.class,
    		"layerRenderers", "field_177097_h");
    	layerRenderers.setAccessible(true);
    	try {
	    	if (((List) layerRenderers.get(e.getRenderer())).get(0).getClass() == LayerBipedArmor.class)
				((List) layerRenderers.get(e.getRenderer())).set(0, new LayerSpectriteArmor(e.getRenderer()));
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
	}
}
