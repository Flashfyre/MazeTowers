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
		reg(ModBlocks.hiddenButton, 0);
		reg(ModBlocks.hiddenPressurePlateWeighted, 0);
		reg(ModBlocks.itemScanner, 0);
		reg(ModBlocks.itemScannerGold, 0);
		reg(ModBlocks.memoryPiston, 0);
		reg(ModBlocks.memoryPistonOff, 0);
		reg(ModBlocks.memoryPistonHead, 0);
		reg(ModBlocks.memoryPistonHeadOff, 0);
		reg(ModBlocks.memoryPistonExtension, 0);
		reg(ModBlocks.memoryPistonExtensionOff, 0);
		reg(ModBlocks.mineralChestIron, 0);
		reg(ModBlocks.mineralChestGold, 0);
		reg(ModBlocks.mineralChestDiamond, 0);
		reg(ModBlocks.resistantDoorEndStone, 0);
		reg(ModBlocks.resistantDoorQuartz, 0);
		reg(ModBlocks.resistantDoorObsidian, 0);
		reg(ModBlocks.resistantDoorBedrock, 0);
	}

	public static void reg(Block block, int meta) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(modid + ":" + block.getUnlocalizedName().substring(5), "inventory"));
	}
    
	public static void reg(Block block, int meta, String name) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(modid + ":" + name, "inventory"));
	}
}
