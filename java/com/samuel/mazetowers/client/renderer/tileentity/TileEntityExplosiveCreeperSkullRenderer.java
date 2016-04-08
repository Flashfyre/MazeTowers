package com.samuel.mazetowers.client.renderer.tileentity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.authlib.GameProfile;
import com.samuel.mazetowers.tileentity.TileEntityExplosiveCreeperSkull;

@SideOnly(Side.CLIENT)
public class TileEntityExplosiveCreeperSkullRenderer extends
	TileEntitySpecialRenderer<TileEntityExplosiveCreeperSkull> {
	private static final ResourceLocation CREEPER_TEXTURES =
		new ResourceLocation("mazetowers:textures/entities/explosive_creeper/"
			+ "explosive_creeper.png");
    private static TileEntityExplosiveCreeperSkullRenderer instance;
    private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);

    @Override
    public void renderTileEntityAt(TileEntityExplosiveCreeperSkull te,
    	double x, double y, double z, float partialTicks, int destroyStage)
    {
        EnumFacing enumfacing = EnumFacing.getFront(te.getBlockMetadata() & 7);
        this.renderSkull((float)x, (float)y, (float)z, enumfacing,
        	te.getSkullRotation() * 360 / 16.0F, te.getSkullType(),
        	te.getPlayerProfile(), destroyStage);
    }

    @Override
    public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super.setRendererDispatcher(rendererDispatcherIn);
        TileEntityExplosiveCreeperSkullRenderer.instance = this;
    }
    
    public void renderSkull(float p_180543_1_, float p_180543_2_, float p_180543_3_,
    	EnumFacing p_180543_4_, float p_180543_5_, int p_180543_6_,
    	GameProfile p_180543_7_, int p_180543_8_)
    {
        ModelBase modelbase = this.skeletonHead;

        if (p_180543_8_ >= 0)
        {
            this.bindTexture(DESTROY_STAGES[p_180543_8_]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        	this.bindTexture(CREEPER_TEXTURES);

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        if (p_180543_4_ != EnumFacing.UP)
        {
            switch (p_180543_4_)
            {
                case NORTH:
                    GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_ + 0.25F, p_180543_3_ + 0.74F);
                    break;
                case SOUTH:
                    GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_ + 0.25F, p_180543_3_ + 0.26F);
                    p_180543_5_ = 180.0F;
                    break;
                case WEST:
                    GlStateManager.translate(p_180543_1_ + 0.74F, p_180543_2_ + 0.25F, p_180543_3_ + 0.5F);
                    p_180543_5_ = 270.0F;
                    break;
                case EAST:
                default:
                    GlStateManager.translate(p_180543_1_ + 0.26F, p_180543_2_ + 0.25F, p_180543_3_ + 0.5F);
                    p_180543_5_ = 90.0F;
            }
        }
        else
        {
            GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_, p_180543_3_ + 0.5F);
        }

        float f = 0.0625F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        modelbase.render((Entity)null, 0.0F, 0.0F, 0.0F, p_180543_5_, 0.0F, f);
        GlStateManager.popMatrix();

        if (p_180543_8_ >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}
