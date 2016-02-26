package com.samuel.mazetowers.render.entities;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderBlaze;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderUltravioletBlaze extends RenderBlaze {

	// private static final ResourceLocation blazeTextures = new
	// ResourceLocation("textures/entity/blaze.png");
	private static final ResourceLocation shadowTextures = new ResourceLocation(
		"textures/misc/shadow.png");
	private static final ResourceLocation blazeTextures = new ResourceLocation(
		"mazetowers:textures/entities/ultravioletblaze.png");

	public RenderUltravioletBlaze(
		RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(
		EntityBlaze entity) {
		return blazeTextures;
	}

	@Override
	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doe
	 *  
	 * @param entityYaw The yaw rotation of the passed entity
	 */
	public void doRender(EntityBlaze entity, double x,
		double y, double z, float entityYaw,
		float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw,
			partialTicks);
	}

	@Override
	/**
	 * Renders the entity's shadow and fire (if its on fire). Args: entity, x, y, z, yaw, partialTickTime
	 */
	public void doRenderShadowAndFire(Entity entityIn,
		double x, double y, double z, float yaw,
		float partialTicks) {
		if (this.renderManager.options != null) {
			if (this.renderManager.options.field_181151_V
				&& this.shadowSize > 0.0F
				&& !entityIn.isInvisible()
				&& this.renderManager.isRenderShadow()) {
				double d0 = this.renderManager
					.getDistanceToCamera(entityIn.posX,
						entityIn.posY, entityIn.posZ);
				float f = (float) ((1.0D - d0 / 256.0D) * (double) this.shadowOpaque);

				if (f > 0.0F) {
					this.renderShadow(entityIn, x, y, z, f,
						partialTicks);
				}
			}

			if (entityIn.canRenderOnFire()
				&& (!(entityIn instanceof EntityPlayer) || !((EntityPlayer) entityIn)
					.isSpectator())) {
				this.renderEntityOnFire(entityIn, x, y, z,
					partialTicks);
			}
		}
	}

	/**
	 * Renders fire on top of the entity. Args: entity, x, y, z, partialTickTime
	 */
	private void renderEntityOnFire(Entity entity,
		double x, double y, double z, float partialTicks) {
		GlStateManager.disableLighting();
		TextureMap texturemap = Minecraft.getMinecraft()
			.getTextureMapBlocks();
		TextureAtlasSprite textureatlassprite = texturemap
			.getAtlasSprite("minecraft:blocks/fire_layer_0");
		TextureAtlasSprite textureatlassprite1 = texturemap
			.getAtlasSprite("minecraft:blocks/fire_layer_1");
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y,
			(float) z);
		float f = entity.width * 1.4F;
		GlStateManager.scale(f, f, f);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator
			.getWorldRenderer();
		float f1 = 0.5F;
		float f2 = 0.0F;
		float f3 = entity.height / f;
		float f4 = (float) (entity.posY - entity
			.getEntityBoundingBox().minY);
		GlStateManager.rotate(
			-this.renderManager.playerViewY, 0.0F, 1.0F,
			0.0F);
		GlStateManager.translate(0.0F, 0.0F, -0.3F
			+ (float) ((int) f3) * 0.02F);
		GlStateManager.color(0.5F, 0.0F, 1.0F, 1.0F);
		float f5 = 0.0F;
		int i = 0;
		worldrenderer.func_181668_a(7,
			DefaultVertexFormats.field_181707_g);

		while (f3 > 0.0F) {
			TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite
				: textureatlassprite1;
			this.bindTexture(TextureMap.locationBlocksTexture);
			float f6 = textureatlassprite2.getMinU();
			float f7 = textureatlassprite2.getMinV();
			float f8 = textureatlassprite2.getMaxU();
			float f9 = textureatlassprite2.getMaxV();

			if (i / 2 % 2 == 0) {
				float f10 = f8;
				f8 = f6;
				f6 = f10;
			}

			worldrenderer.func_181662_b((double) (f1 - f2),
				(double) (0.0F - f4), (double) f5)
				.func_181673_a((double) f8, (double) f9)
				.func_181675_d();
			worldrenderer.func_181662_b(
				(double) (-f1 - f2), (double) (0.0F - f4),
				(double) f5).func_181673_a((double) f6,
				(double) f9).func_181675_d();
			worldrenderer.func_181662_b(
				(double) (-f1 - f2), (double) (1.4F - f4),
				(double) f5).func_181673_a((double) f6,
				(double) f7).func_181675_d();
			worldrenderer.func_181662_b((double) (f1 - f2),
				(double) (1.4F - f4), (double) f5)
				.func_181673_a((double) f8, (double) f7)
				.func_181675_d();
			f3 -= 0.45F;
			f4 -= 0.45F;
			f1 *= 0.9F;
			f5 += 0.03F;
			++i;
		}

		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
	}

	/**
	 * Renders the entity shadows at the position, shadow alpha and
	 * partialTickTime. Args: entity, x, y, z, shadowAlpha, partialTickTime
	 */
	private void renderShadow(Entity entityIn, double x,
		double y, double z, float shadowAlpha,
		float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(770, 771);
		this.renderManager.renderEngine
			.bindTexture(shadowTextures);
		World world = this.getWorldFromRenderManager();
		GlStateManager.depthMask(false);
		float f = this.shadowSize;

		if (entityIn instanceof EntityLiving) {
			EntityLiving entityliving = (EntityLiving) entityIn;
			f *= entityliving.getRenderSizeModifier();

			if (entityliving.isChild()) {
				f *= 0.5F;
			}
		}

		double d5 = entityIn.lastTickPosX
			+ (entityIn.posX - entityIn.lastTickPosX)
			* (double) partialTicks;
		double d0 = entityIn.lastTickPosY
			+ (entityIn.posY - entityIn.lastTickPosY)
			* (double) partialTicks;
		double d1 = entityIn.lastTickPosZ
			+ (entityIn.posZ - entityIn.lastTickPosZ)
			* (double) partialTicks;
		int i = MathHelper.floor_double(d5 - (double) f);
		int j = MathHelper.floor_double(d5 + (double) f);
		int k = MathHelper.floor_double(d0 - (double) f);
		int l = MathHelper.floor_double(d0);
		int i1 = MathHelper.floor_double(d1 - (double) f);
		int j1 = MathHelper.floor_double(d1 + (double) f);
		double d2 = x - d5;
		double d3 = y - d0;
		double d4 = z - d1;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator
			.getWorldRenderer();
		worldrenderer.func_181668_a(7,
			DefaultVertexFormats.field_181709_i);

		for (BlockPos blockpos : BlockPos
			.getAllInBoxMutable(new BlockPos(i, k, i1),
				new BlockPos(j, l, j1))) {
			Block block = world.getBlockState(
				blockpos.down()).getBlock();

			if (block.getRenderType() != -1
				&& world.getLightFromNeighbors(blockpos) > 3) {
				this.func_180549_a(block, x, y, z,
					blockpos, shadowAlpha, f, d2, d3, d4);
			}
		}

		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	private void func_180549_a(Block blockIn,
		double p_180549_2_, double p_180549_4_,
		double p_180549_6_, BlockPos pos,
		float p_180549_9_, float p_180549_10_,
		double p_180549_11_, double p_180549_13_,
		double p_180549_15_) {
		if (blockIn.isFullCube()) {
			Tessellator tessellator = Tessellator
				.getInstance();
			WorldRenderer worldrenderer = tessellator
				.getWorldRenderer();
			double d0 = ((double) p_180549_9_ - (p_180549_4_ - ((double) pos
				.getY() + p_180549_13_)) / 2.0D)
				* 0.5D
				* (double) this.getWorldFromRenderManager()
					.getLightBrightness(pos);

			if (d0 >= 0.0D) {
				if (d0 > 1.0D) {
					d0 = 1.0D;
				}

				double d1 = (double) pos.getX()
					+ blockIn.getBlockBoundsMinX()
					+ p_180549_11_;
				double d2 = (double) pos.getX()
					+ blockIn.getBlockBoundsMaxX()
					+ p_180549_11_;
				double d3 = (double) pos.getY()
					+ blockIn.getBlockBoundsMinY()
					+ p_180549_13_ + 0.015625D;
				double d4 = (double) pos.getZ()
					+ blockIn.getBlockBoundsMinZ()
					+ p_180549_15_;
				double d5 = (double) pos.getZ()
					+ blockIn.getBlockBoundsMaxZ()
					+ p_180549_15_;
				float f = (float) ((p_180549_2_ - d1)
					/ 2.0D / (double) p_180549_10_ + 0.5D);
				float f1 = (float) ((p_180549_2_ - d2)
					/ 2.0D / (double) p_180549_10_ + 0.5D);
				float f2 = (float) ((p_180549_6_ - d4)
					/ 2.0D / (double) p_180549_10_ + 0.5D);
				float f3 = (float) ((p_180549_6_ - d5)
					/ 2.0D / (double) p_180549_10_ + 0.5D);
				worldrenderer.func_181662_b(d1, d3, d4)
					.func_181673_a((double) f, (double) f2)
					.func_181666_a(1.0F, 1.0F, 1.0F,
						(float) d0).func_181675_d();
				worldrenderer.func_181662_b(d1, d3, d5)
					.func_181673_a((double) f, (double) f3)
					.func_181666_a(1.0F, 1.0F, 1.0F,
						(float) d0).func_181675_d();
				worldrenderer
					.func_181662_b(d2, d3, d5)
					.func_181673_a((double) f1, (double) f3)
					.func_181666_a(1.0F, 1.0F, 1.0F,
						(float) d0).func_181675_d();
				worldrenderer
					.func_181662_b(d2, d3, d4)
					.func_181673_a((double) f1, (double) f2)
					.func_181666_a(1.0F, 1.0F, 1.0F,
						(float) d0).func_181675_d();
			}
		}
	}

	@Override
	/**
	 * Renders the model in RenderLiving
	 */
	protected void renderModel(
		EntityBlaze entitylivingbaseIn, float p_77036_2_,
		float p_77036_3_, float p_77036_4_,
		float p_77036_5_, float p_77036_6_, float p_77036_7_) {
		boolean flag = !entitylivingbaseIn.isInvisible();
		boolean flag1 = !flag
			&& !entitylivingbaseIn
				.isInvisibleToPlayer(Minecraft
					.getMinecraft().thePlayer);

		if (flag || flag1) {
			if (!this.bindEntityTexture(entitylivingbaseIn)) {
				return;
			}

			if (flag1) {
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F,
					0.15F);
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(770, 771);
				GlStateManager.alphaFunc(516, 0.003921569F);
			}

			this.mainModel.render(entitylivingbaseIn,
				p_77036_2_, p_77036_3_, p_77036_4_,
				p_77036_5_, p_77036_6_, p_77036_7_);

			if (flag1) {
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				GlStateManager.popMatrix();
				GlStateManager.depthMask(true);
			}
		}
	}

	/**
	 * Returns the render manager's world object
	 */
	private World getWorldFromRenderManager() {
		return this.renderManager.worldObj;
	}
}
