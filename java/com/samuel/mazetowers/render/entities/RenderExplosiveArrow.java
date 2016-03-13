package com.samuel.mazetowers.render.entities;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderExplosiveArrow extends RenderArrow {
	private static final ResourceLocation arrowTextures = new ResourceLocation(
		"mazetowers:textures/entities/explosive_arrow.png");

	public RenderExplosiveArrow(RenderManager p_i46193_1_) {
		super(p_i46193_1_);
	}

	@Override
	public void doRender(EntityArrow entity, double x,
		double y, double z, float entityYaw,
		float partialTicks) {
		this.bindEntityTexture(entity);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y,
			(float) z);
		GlStateManager.rotate(entity.prevRotationYaw
			+ (entity.rotationYaw - entity.prevRotationYaw)
			* partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager
			.rotate(
				entity.prevRotationPitch
					+ (entity.rotationPitch - entity.prevRotationPitch)
					* partialTicks, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator
			.getWorldRenderer();
		int i = 0;
		float f = 0.0F;
		float f1 = 0.5F;
		float f2 = (float) (0 + i * 10) / 32.0F;
		float f3 = (float) (5 + i * 10) / 32.0F;
		float f4 = 0.0F;
		float f5 = 0.15625F;
		float f6 = (float) (5 + i * 10) / 32.0F;
		float f7 = (float) (10 + i * 10) / 32.0F;
		float f8 = 0.05625F;
		GlStateManager.enableRescaleNormal();
		float f9 = (float) entity.arrowShake - partialTicks;

		if (f9 > 0.0F) {
			float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
			GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
		}

		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(f8, f8, f8);
		GlStateManager.translate(-4.0F, 0.0F, 0.0F);
		GL11.glNormal3f(f8, 0.0F, 0.0F);
		worldrenderer.func_181668_a(7,
			DefaultVertexFormats.field_181707_g);
		worldrenderer.func_181662_b(-7.0D, -2.0D, -2.0D)
			.func_181673_a((double) f4, (double) f6)
			.func_181675_d();
		worldrenderer.func_181662_b(-7.0D, -2.0D, 2.0D)
			.func_181673_a((double) f5, (double) f6)
			.func_181675_d();
		worldrenderer.func_181662_b(-7.0D, 2.0D, 2.0D)
			.func_181673_a((double) f5, (double) f7)
			.func_181675_d();
		worldrenderer.func_181662_b(-7.0D, 2.0D, -2.0D)
			.func_181673_a((double) f4, (double) f7)
			.func_181675_d();
		tessellator.draw();
		GL11.glNormal3f(-f8, 0.0F, 0.0F);
		worldrenderer.func_181668_a(7,
			DefaultVertexFormats.field_181707_g);
		worldrenderer.func_181662_b(-7.0D, 2.0D, -2.0D)
			.func_181673_a((double) f4, (double) f6)
			.func_181675_d();
		worldrenderer.func_181662_b(-7.0D, 2.0D, 2.0D)
			.func_181673_a((double) f5, (double) f6)
			.func_181675_d();
		worldrenderer.func_181662_b(-7.0D, -2.0D, 2.0D)
			.func_181673_a((double) f5, (double) f7)
			.func_181675_d();
		worldrenderer.func_181662_b(-7.0D, -2.0D, -2.0D)
			.func_181673_a((double) f4, (double) f7)
			.func_181675_d();
		tessellator.draw();

		for (int j = 0; j < 4; ++j) {
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, f8);
			worldrenderer.func_181668_a(7,
				DefaultVertexFormats.field_181707_g);
			worldrenderer.func_181662_b(-8.0D, -2.0D, 0.0D)
				.func_181673_a((double) f, (double) f2)
				.func_181675_d();
			worldrenderer.func_181662_b(8.0D, -2.0D, 0.0D)
				.func_181673_a((double) f1, (double) f2)
				.func_181675_d();
			worldrenderer.func_181662_b(8.0D, 2.0D, 0.0D)
				.func_181673_a((double) f1, (double) f3)
				.func_181675_d();
			worldrenderer.func_181662_b(-8.0D, 2.0D, 0.0D)
				.func_181673_a((double) f, (double) f3)
				.func_181675_d();
			tessellator.draw();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw,
			partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(
		EntityArrow entity) {
		return arrowTextures;
	}
}
