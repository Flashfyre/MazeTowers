package com.samuel.mazetowers.items;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockRedstoneClock extends ItemBlockVendorTradable {
	
	public static int curFrame = 0;

	public ItemBlockRedstoneClock(Block block) {
		super(block, 1, 4, 9, 30, 300, 2, 5);
		this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            double field_185088_a;
            @SideOnly(Side.CLIENT)
            double field_185089_b;
            @SideOnly(Side.CLIENT)
            long field_185090_c;
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn)
            {
                boolean flag = entityIn != null;	
                Entity entity = flag ? entityIn : stack.getItemFrame();
                if (!flag && !(entity instanceof EntityItemFrame))
                	entity = Minecraft.getMinecraft().thePlayer;

                if (entity != null)
                {
                    worldIn = entity.worldObj;
                }

                if (worldIn == null)
                {
                    return 0.0F;
                }
                else
                {
                    double d0;

                    if (worldIn.provider.isSurfaceWorld())
                    {
                        d0 = worldIn.getCelestialAngle(1.0F);
                    }
                    else
                    {
                        d0 = Math.random();
                    }

                    d0 = this.func_185087_a(worldIn, d0);
                    
                    float time = MathHelper.positiveModulo((float)d0, 1.0F);
                    curFrame = getCurrentFrame(time);
                    return time;
                }
            }
            
            @SideOnly(Side.CLIENT)
            private double func_185087_a(World p_185087_1_, double p_185087_2_) {
                if (p_185087_1_.getTotalWorldTime() != this.field_185090_c) {
                    this.field_185090_c = p_185087_1_.getTotalWorldTime();
                    double d0 = p_185087_2_ - this.field_185088_a;

                    if (d0 < -0.5D)
                        ++d0;

                    this.field_185089_b += d0 * 0.1D;
                    this.field_185089_b *= 0.9D;
                    this.field_185088_a += this.field_185089_b;
                }
                
                return this.field_185088_a;
            }
            
            @SideOnly(Side.CLIENT)
            private int getCurrentFrame(float time) {
            	final double halfFrame = 0.0078125D;
            	double t = time - halfFrame;
            	if (t < 0D)
            		t += halfFrame;
            	return (int) Math.floor(t * 64);
            }
        });
	}
	
	public static int getRedstonePower() {
		
		int power = curFrame < 30 ? 15 - (curFrame >> 1) : curFrame > 33 ? ((curFrame - 32) >> 1) : 0;
		if (power > 15)
			power = power % 16;
    	return power;
    }
}
