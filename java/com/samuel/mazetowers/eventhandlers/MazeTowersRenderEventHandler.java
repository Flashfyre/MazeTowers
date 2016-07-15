package com.samuel.mazetowers.eventhandlers;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.client.renderer.entity.layers.LayerSpectriteArmor;
import com.samuel.mazetowers.etc.IMazeTowerCapability;
import com.samuel.mazetowers.etc.ISpectriteTool;
import com.samuel.mazetowers.etc.MTHelper;
import com.samuel.mazetowers.etc.MazeTowerGuiProvider;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MiniTower;

public class MazeTowersRenderEventHandler {
	
	public BlockPos cachedSelPos = null;
	public boolean cachedSelPosBreakability = false;
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onRenderLiving(RenderLivingEvent.Pre e) {
		EntityLivingBase entity = e.getEntity();
    	Field layerRenderers = MTHelper.findObfuscatedField(RenderLivingBase.class,
    		"layerRenderers", "field_177097_h");
    	layerRenderers.setAccessible(true);
    	try {
	    	if (!((List) layerRenderers.get(e.getRenderer())).isEmpty()) {
	    		Class rendererClass = ((List) layerRenderers.get(e.getRenderer())).get(0).getClass();
	    		if (rendererClass == LayerBipedArmor.class || rendererClass.getSuperclass() == LayerBipedArmor.class)
	    			((List) layerRenderers.get(e.getRenderer())).set(0, new LayerSpectriteArmor(e.getRenderer()));
	    	}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
		EntityPlayer player = e.getPlayer();
		BlockPos blockpos = e.getTarget().getBlockPos();
		IMazeTowerCapability props = player.getCapability(MazeTowerGuiProvider.gui, null);
		ItemStack playerHeldItem = player.getHeldItemMainhand();
		if (blockpos != null) {
			if (props != null && props.getEnabled()) {
				World world = player.getEntityWorld();
				MazeTowerBase tower = MazeTowers.mazeTowers.getTowerBesideCoords(world, ((int) player.posX) >> 4, ((int) player.posZ) >> 4);
				if (tower != null && blockpos != null) {
					if (!blockpos.equals(cachedSelPos)) {
						int[] coords = tower.getCoordsFromPos(blockpos);
						BitSet[][] blockBreakabilityData = tower.getBlockBreakabilityData();
						if (coords[1] >= 0 && coords[1] < 16 && coords[2] >= 0 && coords[2] < 16 && coords[0] >= 0 && coords[0] < blockBreakabilityData.length) {
							try {
								if (blockBreakabilityData[coords[0]] != null && !blockBreakabilityData[coords[0]][coords[1]].get(coords[2])) {
									cachedSelPosBreakability = false;
								} else {
									cachedSelPosBreakability = true;
								}
							} catch (NullPointerException e1) {
								e1.printStackTrace();
							}
							cachedSelPos = blockpos;
						} else {
							for (MiniTower mt : tower.getMiniTowers()) {
								if (mt.getPosInBounds(blockpos)) {
									if (!mt.getPosBreakability(blockpos)) {
										cachedSelPosBreakability = false;
									} else {
										cachedSelPosBreakability = true;
									}
									cachedSelPos = blockpos;
									break;
								}
							}
						}
					}
					if (!cachedSelPosBreakability)
						drawUnbreakableBlockSelectionBox(player, blockpos, e.getTarget(), e.getPartialTicks());
				}
			}
			
			if (playerHeldItem != null && playerHeldItem.getItem() instanceof ISpectriteTool) {
				List<BlockPos> affectedPosList = ((ISpectriteTool) playerHeldItem.getItem())
					.getPlayerBreakableBlocks(playerHeldItem, blockpos, player);
				Iterator<BlockPos> affectedPosIterator;
				
				affectedPosIterator = affectedPosList.iterator();
				
				while (affectedPosIterator.hasNext()) {
					BlockPos curPos = affectedPosIterator.next();
					if (!curPos.equals(blockpos)) {
						drawBreakableBlockSelectionBox(player, curPos, e.getTarget(), e.getPartialTicks());
					}
				}
			}
		}
	}
	
	private void drawUnbreakableBlockSelectionBox(EntityPlayer player, BlockPos blockpos,
		RayTraceResult rayTraceResult, float partialTicks) {
		drawColoredBlockSelectionBox(player, blockpos, rayTraceResult, partialTicks, 1.0F, 0.0F, 0.0F, 0.4F);
	}

	private void drawBreakableBlockSelectionBox(EntityPlayer player, BlockPos blockpos,
		RayTraceResult rayTraceResult, float partialTicks) {
		drawColoredBlockSelectionBox(player, blockpos, rayTraceResult, partialTicks, 0.0F, 0.0F, 0.0F, 0.2F);
	}
	
	private void drawColoredBlockSelectionBox(EntityPlayer player, BlockPos blockpos,
		RayTraceResult rayTraceResult, float partialTicks,
		float r, float g, float b, float a) {
		World world = player.getEntityWorld();
		GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
        	GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
        	GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        
        IBlockState iblockstate = world.getBlockState(blockpos);

        if (iblockstate.getMaterial() != Material.AIR && world.getWorldBorder().contains(blockpos))
        {
            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            Minecraft.getMinecraft().renderGlobal.func_189697_a(iblockstate.getSelectedBoundingBox(world, blockpos)
            	.offset(-d0, -d1, -d2), r, g, b, a);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}
}
