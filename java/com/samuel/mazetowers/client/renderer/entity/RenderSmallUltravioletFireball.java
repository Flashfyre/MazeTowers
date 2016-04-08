package com.samuel.mazetowers.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSmallUltravioletFireball extends
	RenderFireball {

	public RenderSmallUltravioletFireball(
		RenderManager renderManagerIn, float scaleIn) {
		super(renderManagerIn, scaleIn);
	}
}
