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
	public static Block itemScanner;
	public static Block itemScannerGold;
	public static Block memoryPiston;
	public static Block memoryPistonOff;
	public static Block memoryPistonHead;
	public static Block memoryPistonHeadOff;
	public static Block memoryPistonExtension;
	public static Block memoryPistonExtensionOff;
	public static Block quartzButton;
	public static Block resistantDoorEndStone;
	public static Block resistantDoorObsidian;
	public static Block resistantDoorBedrock;

    public static void createBlocks() {
    	GameRegistry.registerBlock(hiddenPressurePlateWeighted =
    		MazeTowers.BlockHiddenPressurePlateWeighted,
    		"hidden_heavy_pressure_plate");
    	GameRegistry.registerBlock(itemScanner = MazeTowers.BlockItemScanner, "item_scanner");
    	GameRegistry.registerBlock(itemScannerGold = MazeTowers.BlockItemScannerGold, "item_scanner_gold");
    	GameRegistry.registerBlock(memoryPiston = MazeTowers.BlockMemoryPiston, "memory_piston");
    	GameRegistry.registerBlock(memoryPistonOff = MazeTowers.BlockMemoryPistonOff, "memory_piston_off");
    	GameRegistry.registerBlock(memoryPistonHead = MazeTowers.BlockMemoryPistonHead, "memory_piston_head");
    	GameRegistry.registerBlock(memoryPistonHeadOff = MazeTowers.BlockMemoryPistonHeadOff, "memory_piston_head_off");
    	GameRegistry.registerBlock(memoryPistonExtension = MazeTowers.BlockMemoryPistonExtension, "memory_piston_extension");
    	GameRegistry.registerBlock(memoryPistonExtensionOff = MazeTowers.BlockMemoryPistonExtensionOff, "memory_piston_extension_off");
    	GameRegistry.registerBlock(quartzButton = MazeTowers.BlockQuartzButton, "quartz_button");
    	GameRegistry.registerBlock(resistantDoorEndStone = MazeTowers.BlockEndStoneDoor, "end_stone_door");
    	GameRegistry.registerBlock(resistantDoorObsidian = MazeTowers.BlockObsidianDoor, "obsidian_door");
    	GameRegistry.registerBlock(resistantDoorBedrock = MazeTowers.BlockBedrockDoor, "bedrock_door");
    }
}