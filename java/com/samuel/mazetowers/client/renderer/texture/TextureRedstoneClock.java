package com.samuel.mazetowers.client.renderer.texture;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureRedstoneClock extends TextureAtlasSprite
{
	private final boolean inverted;
    private double field_94239_h, field_94240_i;

    public TextureRedstoneClock(String iconName, boolean inverted)
    {
        super(iconName);
        this.inverted = inverted;
    }

    @Override
    public void updateAnimation()
    {
        if (!inverted && !this.framesTextureData.isEmpty())
        {
            Minecraft minecraft = Minecraft.getMinecraft();
            double d0 = 0.0D;

            if (minecraft.theWorld != null && minecraft.thePlayer != null)
            {
                d0 = (double)minecraft.theWorld.getCelestialAngle(1.0F);

                if (!minecraft.theWorld.provider.isSurfaceWorld())
                {
                    d0 = Math.random();
                }
            }

            double d1;

            for (d1 = d0 - this.field_94239_h; d1 < -0.5D; ++d1)
            {
                ;
            }

            while (d1 >= 0.5D)
            {
                --d1;
            }

            d1 = MathHelper.clamp_double(d1, -1.0D, 1.0D);
            this.field_94240_i += d1 * 0.1D;
            this.field_94240_i *= 0.8D;
            this.field_94239_h += this.field_94240_i;
            int i;

            for (i = (int)((this.field_94239_h + 1.0D) * (double)this.framesTextureData.size()) % this.framesTextureData.size();
            	i < 0; i = (i + this.framesTextureData.size()) % this.framesTextureData.size())
            {
                ;
            }

            if (i != this.frameCounter)
            {
                this.frameCounter = i;
                TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(this.frameCounter),
                	this.width, this.height, this.originX, this.originY, false, false);
            }
            
            MazeTowers.TextureRedstoneClockInverted.frameCounter = frameCounter;
        } else
        	TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(this.frameCounter),
            	this.width, this.height, this.originX, this.originY, false, false);
    }
    
    public int getRedstonePower() {
    	return frameCounter < 30 ? 15 - (frameCounter >> 1) : frameCounter > 33 ? ((frameCounter - 32) >> 1) : 0;
    }
}