package com.samuel.mazetowers.render.tileentities;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBase;
import com.samuel.mazetowers.blocks.BlockMemoryPistonExtension;
import com.samuel.mazetowers.tileentities.TileEntityMemoryPiston;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityMemoryPistonRenderer extends TileEntitySpecialRenderer
{
    private final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
    
    private static final ResourceLocation texture = new ResourceLocation("mazetowers:textures/blocks/memory_piston_top.png");

    public void func_178461_a(TileEntityMemoryPiston p_178461_1_, double p_178461_2_, double p_178461_4_, double p_178461_6_, float p_178461_8_, int p_178461_9_)
    {
        BlockPos BlockPos = p_178461_1_.getPos();
        IBlockState iblockstate = p_178461_1_.getPistonState();
        Block block = iblockstate.getBlock();

        if (block.getMaterial() != Material.air && p_178461_1_.getProgress(p_178461_8_) < 1.0F)
        {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            this.bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GlStateManager.shadeModel(7425);
            }
            else
            {
                GlStateManager.shadeModel(7424);
            }

            worldrenderer.func_181668_a(7, DefaultVertexFormats.BLOCK);
            worldrenderer.setTranslation((double)((float)p_178461_2_ - (float)BlockPos.getX() +
            	p_178461_1_.getOffsetX(p_178461_8_)), (double)((float)p_178461_4_ - (float)BlockPos.getY() +
            	p_178461_1_.getOffsetY(p_178461_8_)), (double)((float)p_178461_6_ - (float)BlockPos.getZ() +
            	p_178461_1_.getOffsetZ(p_178461_8_)));
            World world = this.getWorld();

            if (block == MazeTowers.BlockMemoryPistonHead && p_178461_1_.getProgress(p_178461_8_) < 0.5F)
            {
                iblockstate = iblockstate.withProperty(BlockMemoryPistonExtension.SHORT, Boolean.valueOf(true));
                this.blockRenderer.getBlockModelRenderer().renderModel(world, this.blockRenderer.getModelFromBlockState(iblockstate, world, BlockPos), iblockstate, BlockPos, worldrenderer, true);
            }
            else if (p_178461_1_.shouldPistonHeadBeRendered() && !p_178461_1_.isExtending())
            {
                IBlockState iblockstate1 = MazeTowers.BlockMemoryPistonHead.getDefaultState().withProperty(BlockMemoryPistonExtension.FACING, iblockstate.getValue(BlockMemoryPistonBase.FACING));
                iblockstate1 = iblockstate1.withProperty(BlockMemoryPistonExtension.SHORT, Boolean.valueOf(p_178461_1_.getProgress(p_178461_8_) >= 0.5F));
                this.blockRenderer.getBlockModelRenderer().renderModel(world, this.blockRenderer.getModelFromBlockState(iblockstate1, world, BlockPos), iblockstate1, BlockPos, worldrenderer, true);
                worldrenderer.setTranslation((double)((float)p_178461_2_ - (float)BlockPos.getX()), (double)((float)p_178461_4_ - (float)BlockPos.getY()), (double)((float)p_178461_6_ - (float)BlockPos.getZ()));
                iblockstate.withProperty(BlockMemoryPistonBase.EXTENDED, Boolean.valueOf(true));
                this.blockRenderer.getBlockModelRenderer().renderModel(world, this.blockRenderer.getModelFromBlockState(iblockstate, world, BlockPos), iblockstate, BlockPos, worldrenderer, true);
            }
            else
            {
                this.blockRenderer.getBlockModelRenderer().renderModel(world, this.blockRenderer.getModelFromBlockState(iblockstate, world, BlockPos), iblockstate, BlockPos, worldrenderer, false);
            }

            worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public void renderTileEntityAt(TileEntity p_180535_1_, double posX, double posZ, double p_180535_6_, float p_180535_8_, int p_180535_9_)
    {
        this.func_178461_a((TileEntityMemoryPiston)p_180535_1_, posX, posZ, p_180535_6_, p_180535_8_, p_180535_9_);
    }
}