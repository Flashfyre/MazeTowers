package com.samuel.mazetowers.client.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import com.samuel.mazetowers.client.renderer.entity.RenderExplosiveArrow;
import com.samuel.mazetowers.client.renderer.entity.RenderExplosiveCreeper;
import com.samuel.mazetowers.entity.EntityExplosiveArrow;
import com.samuel.mazetowers.entity.EntityExplosiveCreeper;
import com.samuel.mazetowers.entity.EntityVillagerVendor;

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
		RenderingRegistry.registerEntityRenderingHandler(EntityVillagerVendor.class,
			new IRenderFactory<EntityVillagerVendor>() {
            @Override
            public Render<? super EntityVillagerVendor> createRenderFor (RenderManager manager) {
            	return new RenderVillager(manager);
            }
		});
	}
}
