package com.samuel.mazetowers.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.entity.EntityExplosiveArrow;

@SideOnly(Side.CLIENT)
public class RenderExplosiveArrow<T extends EntityExplosiveArrow> extends RenderArrow<T> {
	private static final ResourceLocation arrowTextures = new ResourceLocation(
		"mazetowers:textures/entities/explosive_arrow.png");

	public RenderExplosiveArrow(RenderManager p_i46193_1_) {
		super(p_i46193_1_);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(T entity) {
		return arrowTextures;
	}
}
