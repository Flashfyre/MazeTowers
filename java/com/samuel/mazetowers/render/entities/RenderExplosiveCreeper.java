package com.samuel.mazetowers.render.entities;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderExplosiveCreeper extends RenderCreeper {
	
	private static final ResourceLocation creeperTextures =
		new ResourceLocation("mazetowers:textures/entities/explosive_creeper/"
			+ "explosive_creeper.png");

    public RenderExplosiveCreeper(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.removeLayer(layerRenderers.get(layerRenderers.size() - 1));
        this.addLayer(new LayerExplosiveCreeperCharge(this));
    }

    @Override
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCreeper entity)
    {
        return creeperTextures;
    }
}
