package com.samuel.mazetowers.render;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

public final class ItemRenderRegister {
	
	public static String modid = MazeTowers.MODID;

    public static void registerItemRenderer() {
    }

    public static void reg(Item item) {
    	ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modid + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
    
    public static void reg(Item item, int meta) {
    	ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modid + ":" + item.getUnlocalizedName(new ItemStack(item, 1, meta)).substring(5), "inventory"));
    }

}