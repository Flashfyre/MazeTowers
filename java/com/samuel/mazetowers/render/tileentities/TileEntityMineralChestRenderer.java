package com.samuel.mazetowers.render.tileentities;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.blocks.BlockMineralChest;
import com.samuel.mazetowers.tileentities.TileEntityMineralChest;

@SideOnly(Side.CLIENT)
public class TileEntityMineralChestRenderer extends
	TileEntitySpecialRenderer {
	private static final ResourceLocation textureIron = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_iron.png");
	private static final ResourceLocation textureGold = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_gold.png");
	private static final ResourceLocation textureDiamond = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_diamond.png");
	private static final ResourceLocation textureDoubleIron = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_iron_double.png");
	private static final ResourceLocation textureDoubleGold = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_gold_double.png");
	private static final ResourceLocation textureDoubleDiamond = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_diamond_double.png");
	private ModelChest simpleChest = new ModelChest();
	private ModelLargeChest largeChest = new ModelLargeChest();

	public TileEntityMineralChestRenderer() {
	}

	public void render(TileEntityMineralChest tile,
		double x, double y_, double z, float partialTick,
		int breakStage) {
		int var10;

		if (!tile.hasWorldObj()) {
			var10 = 0;
		} else {
			Block var11 = tile.getBlockType();
			var10 = tile.getBlockMetadata();

			if (var11 instanceof BlockMineralChest
				&& var10 == 0) {
				((BlockMineralChest) var11)
					.checkForSurroundingChests(tile
						.getWorld(), tile.getPos(), tile
						.getWorld().getBlockState(
							tile.getPos()));
				var10 = tile.getBlockMetadata();
			}

			tile.checkForAdjacentChests();
		}

		if (tile.adjacentChestZNeg == null
			&& tile.adjacentChestXNeg == null) {
			ModelChest var15;

			if (tile.adjacentChestXPos == null
				&& tile.adjacentChestZPos == null) {
				var15 = this.simpleChest;

				if (breakStage >= 0) {
					this.bindTexture(DESTROY_STAGES[breakStage]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(4.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F,
						0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				} else {
					ResourceLocation texture = tile
						.getChestType() == 0 ? textureIron
						: tile.getChestType() == 1 ? textureGold
							: textureDiamond;
					this.bindTexture(texture);
				}
			} else {
				var15 = this.largeChest;

				ResourceLocation textureDouble = tile
					.getChestType() == 0 ? textureDoubleIron
					: tile.getChestType() == 1 ? textureDoubleGold
						: textureDoubleDiamond;

				if (breakStage >= 0) {
					this.bindTexture(DESTROY_STAGES[breakStage]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(8.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F,
						0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				}
				this.bindTexture(textureDouble);
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();

			if (breakStage < 0) {
				GlStateManager
					.color(1.0F, 1.0F, 1.0F, 1.0F);
			}

			GlStateManager.translate((float) x,
				(float) y_ + 1.0F, (float) z + 1.0F);
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
			short var12 = 0;

			if (var10 == 2) {
				var12 = 180;
			}

			if (var10 == 3) {
				var12 = 0;
			}

			if (var10 == 4) {
				var12 = 90;
			}

			if (var10 == 5) {
				var12 = -90;
			}

			if (var10 == 2
				&& tile.adjacentChestXPos != null) {
				GlStateManager.translate(1.0F, 0.0F, 0.0F);
			}

			if (var10 == 5
				&& tile.adjacentChestZPos != null) {
				GlStateManager.translate(0.0F, 0.0F, -1.0F);
			}

			GlStateManager.rotate((float) var12, 0.0F,
				1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			float var13 = tile.prevLidAngle
				+ (tile.lidAngle - tile.prevLidAngle)
				* partialTick;
			float var14;

			if (tile.adjacentChestZNeg != null) {
				var14 = tile.adjacentChestZNeg.prevLidAngle
					+ (tile.adjacentChestZNeg.lidAngle - tile.adjacentChestZNeg.prevLidAngle)
					* partialTick;

				if (var14 > var13) {
					var13 = var14;
				}
			}

			if (tile.adjacentChestXNeg != null) {
				var14 = tile.adjacentChestXNeg.prevLidAngle
					+ (tile.adjacentChestXNeg.lidAngle - tile.adjacentChestXNeg.prevLidAngle)
					* partialTick;

				if (var14 > var13) {
					var13 = var14;
				}
			}

			var13 = 1.0F - var13;
			var13 = 1.0F - var13 * var13 * var13;
			var15.chestLid.rotateAngleX = -(var13
				* (float) Math.PI / 2.0F);
			var15.renderAll();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (breakStage >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity,
		double x, double y, double z, float partialTick,
		int breakStage) {
		this.render((TileEntityMineralChest) tileentity, x,
			y, z, partialTick, breakStage);
	}
}
