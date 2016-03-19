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
public class TileEntityMineralChestRenderer extends TileEntitySpecialRenderer {
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
	private static final ResourceLocation textureTrappedIron = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_iron_trapped.png");
	private static final ResourceLocation textureTrappedGold = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_gold_trapped.png");
	private static final ResourceLocation textureTrappedDiamond = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_diamond_trapped.png");
	private static final ResourceLocation textureDoubleTrappedIron = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_iron_trapped_double.png");
	private static final ResourceLocation textureDoubleTrappedGold = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_gold_trapped_double.png");
	private static final ResourceLocation textureDoubleTrappedDiamond = new ResourceLocation(
		"mazetowers:textures/tileentities/chest_diamond_trapped_double.png");
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
			final int chestType = tile.getChestType();
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
					ResourceLocation texture = null;
					switch (chestType) {
						case 2:
							texture = textureIron;
							break;
						case 3:
							texture = textureGold;
							break;
						case 4:
							texture = textureDiamond;
							break;
						case 5:
							texture = textureTrappedIron;
							break;
						case 6:
							texture = textureTrappedGold;
							break;
						case 7:
							texture = textureTrappedDiamond;
							break;
						default:
					}
					this.bindTexture(texture);
				}
			} else {
				var15 = this.largeChest;
				ResourceLocation textureDouble = null;
				switch (chestType) {
    				case 2:
    					textureDouble = textureDoubleIron;
    					break;
    				case 3:
    					textureDouble = textureDoubleGold;
    					break;
    				case 4:
    					textureDouble = textureDoubleDiamond;
    					break;
    				case 5:
    					textureDouble = textureDoubleTrappedIron;
    					break;
    				case 6:
    					textureDouble = textureDoubleTrappedGold;
    					break;
    				case 7:
    					textureDouble = textureDoubleTrappedDiamond;
    					break;
    				default:
    			}

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
