package com.samuel.mazetowers.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.blocks.BlockRedstoneClock;
import com.samuel.mazetowers.init.ModBlocks;

@SideOnly(Side.CLIENT)
public class TileEntityRedstoneClockRenderer extends TileEntitySpecialRenderer {
	
	private RenderManager renderManager = null;
	private EntityItem entityNormal = null, entityInverted = null;

	public TileEntityRedstoneClockRenderer() {
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double dx, double dy, double dz,
		float partialTick, int blockDamageProgress) {
		if (!(te.getBlockType() instanceof BlockRedstoneClock)) {
			te.invalidate();
			return;
		}
		final boolean inverted = ((BlockRedstoneClock) te.getBlockType()).getInverted();
		final EntityItem entity;
		
		if (renderManager == null) {
			final ItemStack stackNormal = new ItemStack(Item.getItemFromBlock(ModBlocks.redstoneClock)),
			stackInverted = new ItemStack(Item.getItemFromBlock(ModBlocks.redstoneClockInverted));
			renderManager = Minecraft.getMinecraft().getRenderManager();
			entityNormal = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, stackNormal);
			entityInverted = new EntityItem(getWorld(), 0.0D, 0.0D, 0.0D, stackInverted);
		}
		
		entity = inverted ? entityInverted : entityNormal;
		
		entity.hoverStart = 0.0F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(dx, dy, dz);
    	GlStateManager.enableRescaleNormal();
    	GlStateManager.scale(2.0F, 4.0F, 2.0F);
    	GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
    	renderManager.doRenderEntity(entity, 0.25D, -0.25D, -0.0125D, 0.0F, 0.0F, false);
    	GlStateManager.disableRescaleNormal();
    	GlStateManager.popMatrix();
    	if (blockDamageProgress >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	 }
}
