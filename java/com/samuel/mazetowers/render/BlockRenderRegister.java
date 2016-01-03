package com.samuel.mazetowers.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModBlocks;

public class BlockRenderRegister {
	public static String modid = MazeTowers.MODID;
	
	 public static void registerBlockRenderer() {
		reg(ModBlocks.hiddenPressurePlateWeighted, 0);
		reg(ModBlocks.memoryPiston, 0);
		reg(ModBlocks.memoryPistonHead, 0);
		reg(ModBlocks.memoryPistonExtension, 0);
		reg(ModBlocks.quartzButton, 0);
	 }

    public static void reg(Block block, int meta) {
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(modid + ":" + block.getUnlocalizedName().substring(5), "inventory"));
    }
    
    public static void reg(Block block, int meta, String name) {
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(modid + ":" + name, "inventory"));
    }
}
