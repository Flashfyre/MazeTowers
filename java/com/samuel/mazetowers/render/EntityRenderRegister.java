package com.samuel.mazetowers.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import com.samuel.mazetowers.entities.*;
import com.samuel.mazetowers.render.entities.*;

public class EntityRenderRegister {
	
	public static void registerEntityRenderer() {
		RenderingRegistry.registerEntityRenderingHandler(EntityExplosiveArrow.class,
			new IRenderFactory<EntityExplosiveArrow>() {
            @Override
            public Render<? super EntityExplosiveArrow> createRenderFor (RenderManager manager) {
            	return new RenderExplosiveArrow(manager);
            }
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityExplosiveCreeper.class,
			new IRenderFactory<EntityExplosiveCreeper>() {
            @Override
            public Render<? super EntityExplosiveCreeper> createRenderFor (RenderManager manager) {
            	return new RenderExplosiveCreeper(manager);
            }
		});
		RenderingRegistry.registerEntityRenderingHandler(EntitySpecialVillager.class,
			new IRenderFactory<EntitySpecialVillager>() {
            @Override
            public Render<? super EntitySpecialVillager> createRenderFor (RenderManager manager) {
            	return new RenderVillager(manager);
            }
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityUltravioletBlaze.class,
			new IRenderFactory<EntityUltravioletBlaze>() {
            @Override
            public Render<? super EntityUltravioletBlaze> createRenderFor (RenderManager manager) {
            	return new RenderUltravioletBlaze(manager);
            }
		});
	}
}
