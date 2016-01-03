package com.samuel.mazetowers.init;

import java.util.List;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
	
	public static Block hiddenPressurePlateWeighted;
	public static Block memoryPiston;
	public static Block memoryPistonHead;
	public static Block memoryPistonExtension;
	public static Block quartzButton;

    public static void createBlocks() {
    	GameRegistry.registerBlock(hiddenPressurePlateWeighted =
    		MazeTowers.BlockHiddenPressurePlateWeighted,
    		"hidden_heavy_pressure_plate");
    	GameRegistry.registerBlock(memoryPiston = MazeTowers.BlockMemoryPiston, "memory_piston");
    	GameRegistry.registerBlock(memoryPistonHead = MazeTowers.BlockMemoryPistonHead, "memory_piston_head");
    	GameRegistry.registerBlock(memoryPistonExtension = MazeTowers.BlockMemoryPistonExtension, "memory_piston_extension");
		GameRegistry.registerBlock(quartzButton = MazeTowers.BlockQuartzButton, "quartz_button");
    }
}