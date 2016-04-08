package com.samuel.mazetowers.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.blocks.BlockMemoryPistonBase;
import com.samuel.mazetowers.blocks.BlockMemoryPistonExtension;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPiston;

@SideOnly(Side.CLIENT)
public class TileEntityMemoryPistonRenderer extends
	TileEntitySpecialRenderer {
	private BlockRendererDispatcher blockRenderer = Minecraft
		.getMinecraft().getBlockRendererDispatcher();

	private static final ResourceLocation texture = new ResourceLocation(
		"mazetowers:textures/blocks/memory_piston_top.png");

	public void renderTileEntityAt(
		TileEntityMemoryPiston te, double x, double y, double z, float partialTicks,
		int p_178461_9_) {
		if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockPos blockpos = te.getPos();
        IBlockState iblockstate = te.getPistonState();
        Block block = iblockstate.getBlock();
        
        if (iblockstate.getMaterial() != Material.air && te.getProgress(partialTicks) < 1.0F)
        {
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            this.bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
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

            vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
            vertexbuffer.setTranslation((float)x - blockpos.getX() + te.getOffsetX(partialTicks),
            	(float)y - blockpos.getY() + te.getOffsetY(partialTicks),
            	(float)z - blockpos.getZ() + te.getOffsetZ(partialTicks));
            World world = this.getWorld();

            if (block == ModBlocks.memoryPistonHead && te.getProgress(partialTicks) < 0.5F)
            {
                iblockstate = iblockstate.withProperty(BlockMemoryPistonExtension.SHORT, Boolean.valueOf(true));
                this.renderStateModel(blockpos, iblockstate, vertexbuffer, world, true);
            }
            else if (te.shouldPistonHeadBeRendered() && !te.isExtending())
            {
                IBlockState iblockstate1 = ModBlocks.memoryPistonHead.getDefaultState().withProperty(BlockDirectional.FACING, iblockstate.getValue(BlockMemoryPistonBase.FACING));
                iblockstate1 = iblockstate1.withProperty(BlockMemoryPistonExtension.SHORT, Boolean.valueOf(te.getProgress(partialTicks) >= 0.5F));
                this.renderStateModel(blockpos, iblockstate1, vertexbuffer, world, true);
                vertexbuffer.setTranslation((float)x - blockpos.getX(), (float)y - blockpos.getY(), (float)z - blockpos.getZ());
                iblockstate = iblockstate.withProperty(BlockMemoryPistonBase.EXTENDED, Boolean.valueOf(true));
                this.renderStateModel(blockpos, iblockstate, vertexbuffer, world, true);
            }
            else
            {
                this.renderStateModel(blockpos, iblockstate, vertexbuffer, world, false);
            }

            vertexbuffer.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
            RenderHelper.enableStandardItemLighting();
        }
	}

	private boolean renderStateModel(BlockPos p_188186_1_, IBlockState p_188186_2_, VertexBuffer p_188186_3_, World p_188186_4_, boolean p_188186_5_)
    {
        return this.blockRenderer.getBlockModelRenderer().renderModel(p_188186_4_, this.blockRenderer.getModelForState(p_188186_2_), p_188186_2_, p_188186_1_, p_188186_3_, p_188186_5_);
    }

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z,
			float partialTicks, int destroyStage) {
		renderTileEntityAt((TileEntityMemoryPiston) te, x, y, z, partialTicks,
			destroyStage);
	}
}