package com.samuel.mazetowers.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLock extends ModelBase
{
    /** The lock model */
    public ModelRenderer lock = (new ModelRenderer(this, 0, 0)).setTextureSize(16, 16);

    public ModelLock(boolean isRH) {
    	this.textureHeight = 16;
    	this.textureWidth = 16;
    	if (!isRH) {
            this.lock.addBox(0.0F, 0.0F, 14.0F, 5, 5, 2, 0.0F);
            this.lock.rotationPointX = 2.5F;
    	} else {
    		this.lock.addBox(11.0F, 0.0F, 14.0F, 5, 5, 2, 0.0F);
            this.lock.rotationPointX = 13.5F;
    	}
    	
    	this.lock.rotationPointY = 2.5F;
    	this.lock.rotationPointZ = 15.0F;
    }
}