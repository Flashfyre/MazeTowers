package com.samuel.mazetowers.client.renderer.entity.layers;

import java.util.Map;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Maps;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.items.ItemSpectriteArmor;

@SideOnly(Side.CLIENT)
public class LayerSpectriteArmor extends LayerBipedArmor {
	
	private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.<String, ResourceLocation>newHashMap();
	
	@SideOnly(Side.CLIENT)
    public LayerSpectriteArmor(RenderLivingBase<?> rendererIn)
    {
        super(rendererIn);
    }
	
	@Override
	/**
     * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
     *
     * @param entity Entity wearing the armor
     * @param stack ItemStack for the armor
     * @param slot Slot ID that the item is in
     * @param type Subtype, can be null or "overlay"
     * @return ResourceLocation pointing at the armor's texture
     */
    public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EntityEquipmentSlot slot, String type)
    {
		if (stack.getItem() instanceof ItemSpectriteArmor) {
	        ItemSpectriteArmor item = (ItemSpectriteArmor)stack.getItem();
	        String texture = item.getArmorMaterial().getName();
	        String domain = "mazetowers";
	        int idx = texture.indexOf(':');
	        if (idx != -1)
	        {
	            domain = texture.substring(0, idx);
	            texture = texture.substring(idx + 1);
	        }
	        int curFrame = MTUtils.getCurrentSpectriteFrame(entity.getEntityWorld());
	        String s1 = String.format("%s:textures/models/armor/%s/layer_%d%s/%d.png", domain, texture,
	        	(isLegSlot(slot) ? 2 : 1), type == null ? "" : String.format("_%s", type), curFrame);
	
	        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
	        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);
	
	        if (resourcelocation == null)
	        {
	            resourcelocation = new ResourceLocation(s1);
	            ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
	        }
	
	        return resourcelocation;
		}
		
		return super.getArmorResource(entity, stack, slot, type);
    }
	
	private boolean isLegSlot(EntityEquipmentSlot p_188363_1_)
    {
        return p_188363_1_ == EntityEquipmentSlot.LEGS;
    }
}
