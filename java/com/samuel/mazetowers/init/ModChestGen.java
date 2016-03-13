package com.samuel.mazetowers.init;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import com.samuel.mazetowers.etc.MTUtils;

public class ModChestGen {

	public static ChestGenHooks[] chestContents;
	public static int[] totalWeights;

	public static void initChestGen(Random rand, boolean refresh) {
		chestContents = new ChestGenHooks[10];
		for (int i = 1; i <= 10; i++) {
			final int addToLimit = (int) Math.floor((i + 1) / 3);
			chestContents[i - 1] = ChestGenHooks.getInfo("MazeTowerChest" + i);
			if (refresh) {
				for (WeightedRandomChestContent w : chestContents[i - 1].getItems(rand))
					chestContents[i - 1].removeItem(w.theItemId);
			}
			chestContents[i - 1].setMin(1 + (int) Math.floor(addToLimit / 2)); // inclusive
			chestContents[i - 1].setMax(4 + addToLimit); // exclusive
		}
		if (chestContents[0].getItems(rand).isEmpty() || refresh) {
    		// TOOLS
    		// D: 10%
    		addItem(0, new ItemStack(Items.wooden_pickaxe, 1), 1,
    			1, 25);
    		addItem(0, new ItemStack(Items.wooden_axe, 1), 1,
    			1, 20);
    		addItem(0, new ItemStack(Items.wooden_shovel, 1), 1,
    			1, 20);
    		addItem(0, new ItemStack(Items.wooden_hoe, 1), 1,
    			1, 20);
    		addItem(0, new ItemStack(Items.wooden_sword, 1), 1,
    			1, 15);
    		// D+: 7.5%
    		addItem(1, new ItemStack(Items.wooden_pickaxe, 1), 1,
    			1, 10);
    		addItem(1, new ItemStack(Items.wooden_axe, 1), 1,
    			1, 8);
    		addItem(1, new ItemStack(Items.wooden_shovel, 1), 1,
    			1, 8);
    		addItem(1, new ItemStack(Items.wooden_hoe, 1), 1,
    			1, 8);
    		addItem(1, new ItemStack(Items.wooden_sword, 1), 1,
    			1, 5);
    		addItem(1, new ItemStack(Items.stone_pickaxe, 1), 1,
    			1, 10);
    		addItem(1, new ItemStack(Items.stone_axe, 1), 1,
    			1, 7);
    		addItem(1, new ItemStack(Items.stone_shovel, 1), 1,
    			1, 7);
    		addItem(1, new ItemStack(Items.stone_hoe, 1), 1,
    			1, 7);
    		addItem(1, new ItemStack(Items.stone_sword, 1), 1,
    			1, 5);
    		// C: 7.5%
    		addItem(2, new ItemStack(Items.stone_pickaxe, 1), 1,
    			1, 20);
    		addItem(2, new ItemStack(Items.stone_axe, 1), 1,
    			1, 15);
    		addItem(2, new ItemStack(Items.stone_shovel, 1), 1,
    			1, 15);
    		addItem(2, new ItemStack(Items.stone_hoe, 1), 1,
    			1, 15);
    		addItem(2, new ItemStack(Items.stone_sword, 1), 1,
    			1, 10);
    		// C+: 5%
    		addItem(3, new ItemStack(Items.stone_pickaxe, 1), 1,
    			1, 8);
    		addItem(3, new ItemStack(Items.stone_axe, 1), 1,
    			1, 5);
    		addItem(3, new ItemStack(Items.stone_shovel, 1), 1,
    			1, 5);
    		addItem(3, new ItemStack(Items.stone_hoe, 1), 1,
    			1, 5);
    		addItem(3, new ItemStack(Items.stone_sword, 1), 1,
    			1, 3);
    		addItem(3, new ItemStack(Items.golden_pickaxe, 1), 1,
    			1, 7);
    		addItem(3, new ItemStack(Items.golden_axe, 1), 1,
    			1, 5);
    		addItem(3, new ItemStack(Items.golden_shovel, 1), 1,
    			1, 5);
    		addItem(3, new ItemStack(Items.golden_hoe, 1), 1,
    			1, 5);
    		addItem(3, new ItemStack(Items.golden_sword, 1), 1,
    			1, 2);
    		//B: 5%
    		addItem(4, new ItemStack(Items.golden_pickaxe, 1), 1,
    			1, 15);
    		addItem(4, new ItemStack(Items.golden_axe, 1), 1,
    			1, 10);
    		addItem(4, new ItemStack(Items.golden_shovel, 1), 1,
    			1, 10);
    		addItem(4, new ItemStack(Items.golden_hoe, 1), 1,
    			1, 10);
    		addItem(4, new ItemStack(Items.golden_sword, 1), 1,
    			1, 5);
    		//B+: 5%
    		addItem(5, new ItemStack(Items.golden_pickaxe, 1), 1,
    			1, 8);
    		addItem(5, new ItemStack(Items.golden_axe, 1), 1,
    			1, 5);
    		addItem(5, new ItemStack(Items.golden_shovel, 1), 1,
    			1, 5);
    		addItem(5, new ItemStack(Items.golden_hoe, 1), 1,
    			1, 5);
    		addItem(5, new ItemStack(Items.golden_sword, 1), 1,
    			1, 3);
    		addItem(5, new ItemStack(Items.iron_pickaxe, 1), 1,
    			1, 7);
    		addItem(5, new ItemStack(Items.iron_axe, 1), 1,
    			1, 5);
    		addItem(5, new ItemStack(Items.iron_shovel, 1), 1,
    			1, 5);
    		addItem(5, new ItemStack(Items.iron_hoe, 1), 1,
    			1, 5);
    		addItem(5, new ItemStack(Items.iron_sword, 1), 1,
    			1, 2);
    		// A: 5%
    		addItem(6, new ItemStack(Items.iron_pickaxe, 1), 1,
    			1, 15);
    		addItem(6, new ItemStack(Items.iron_axe, 1), 1,
    			1, 10);
    		addItem(6, new ItemStack(Items.iron_shovel, 1), 1,
    			1, 10);
    		addItem(6, new ItemStack(Items.iron_hoe, 1), 1,
    			1, 10);
    		addItem(6, new ItemStack(Items.iron_sword, 1), 1,
    			1, 5);
    		// A+: 7.5%
    		addItem(7, new ItemStack(Items.iron_pickaxe, 1), 1,
    			1, 10);
    		addItem(7, new ItemStack(Items.iron_axe, 1), 1,
    			1, 8);
    		addItem(7, new ItemStack(Items.iron_shovel, 1), 1,
    			1, 8);
    		addItem(7, new ItemStack(Items.iron_hoe, 1), 1,
    			1, 8);
    		addItem(7, new ItemStack(Items.iron_sword, 1), 1,
    			1, 5);
    		addItem(7, new ItemStack(Items.diamond_pickaxe, 1), 1,
    			1, 10);
    		addItem(7, new ItemStack(Items.diamond_axe, 1), 1,
    			1, 7);
    		addItem(7, new ItemStack(Items.diamond_shovel, 1), 1,
    			1, 7);
    		addItem(7, new ItemStack(Items.diamond_hoe, 1), 1,
    			1, 7);
    		addItem(7, new ItemStack(Items.diamond_sword, 1), 1,
    			1, 5);
    		// S: 7.5%
    		addItem(8, new ItemStack(Items.diamond_pickaxe, 1), 1,
    			1, 20);
    		addItem(8, new ItemStack(Items.diamond_axe, 1), 1,
    			1, 15);
    		addItem(8, new ItemStack(Items.diamond_shovel, 1), 1,
    			1, 15);
    		addItem(8, new ItemStack(Items.diamond_hoe, 1), 1,
    			1, 15);
    		addItem(8, new ItemStack(Items.diamond_sword, 1), 1,
    			1, 10);
    		// S+: 10%
    		addItem(9, new ItemStack(Items.diamond_pickaxe, 1), 1,
    			1, 25);
    		addItem(9, new ItemStack(Items.diamond_axe, 1), 1,
    			1, 20);
    		addItem(9, new ItemStack(Items.diamond_shovel, 1), 1,
    			1, 20);
    		addItem(9, new ItemStack(Items.diamond_hoe, 1), 1,
    			1, 20);
    		addItem(9, new ItemStack(Items.diamond_sword, 1), 1,
    			1, 15);
    		// FOOD
    		// D: 25%
    		addItem(0, new ItemStack(Items.rotten_flesh, 1), 1,
    			2, 175);
    		addItem(0, new ItemStack(Items.spider_eye, 1), 1,
    			2, 75);
    		// D+: 22.5%
    		addItem(1, new ItemStack(Items.rotten_flesh, 1), 1,
    			4, 150);
    		addItem(1, new ItemStack(Items.spider_eye, 1), 1,
    			3, 50);
    		addItem(1, new ItemStack(Items.poisonous_potato, 1), 1,
    			1, 25);
    		// C: 20%
    		addItem(2, new ItemStack(Items.rotten_flesh, 1), 1,
    			8, 100);
    		addItem(2, new ItemStack(Items.poisonous_potato, 1), 1,
    			2, 50);
    		addItem(2, new ItemStack(Items.spider_eye, 1), 2,
    			4, 25);
    		addItem(2, new ItemStack(Items.potato, 1), 1,
    			1, 25);
    		// C+: 17.5%
    		addItem(3, new ItemStack(Items.poisonous_potato, 1), 1,
    			4, 75);
    		addItem(3, new ItemStack(Items.potato, 1), 1,
    			2, 50);
    		addItem(3, new ItemStack(Items.melon, 1), 1,
    			1, 25);
    		addItem(3, new ItemStack(Items.rotten_flesh, 1), 1,
    			16, 25);
    		// B: 15%
    		addItem(4, new ItemStack(Items.potato, 1), 1,
    			3, 75);
    		addItem(4, new ItemStack(Items.melon, 1), 1,
    			2, 50);
    		addItem(4, new ItemStack(Items.carrot, 1), 1,
    			1, 25);
    		// B+: 12.5%
    		addItem(5, new ItemStack(Items.melon, 1), 1,
    			3, 50);
    		addItem(5, new ItemStack(Items.carrot, 1), 1,
    			2, 38);
    		addItem(5, new ItemStack(Items.apple, 1), 1,
    			1, 12);
    		// A: 10%
    		addItem(6, new ItemStack(Items.carrot, 1), 1,
    			3, 50);
    		addItem(6, new ItemStack(Items.apple, 1), 1,
    			2, 38);
    		addItem(6, new ItemStack(Items.bread, 1), 1,
    			1, 12);
    		// A+: 7.5%
    		addItem(7, new ItemStack(Items.apple, 1), 1,
    			3, 45);
    		addItem(7, new ItemStack(Items.bread, 1), 1,
    			2, 23);
    		addItem(7, new ItemStack(Items.golden_apple, 1), 1,
    			2, 7);
    		// S: 5%
    		addItem(8, new ItemStack(Items.bread, 1), 1,
    			3, 38);
    		addItem(8, new ItemStack(Items.golden_apple, 1), 1,
    			2, 12);
    		// S+: 5%
    		addItem(9, new ItemStack(Items.bread, 1), 1,
    			3, 25);
    		addItem(9, new ItemStack(Items.golden_apple, 1), 1,
    			3, 18);
    		addItem(9, new ItemStack(Items.golden_apple, 1, 1), 1,
    			1, 7);
    		// MISC
    		// D: 65%
    		addItem(0, new ItemStack(Items.stick, 1), 1, 2, 175);
    		addItem(0, new ItemStack(Items.feather, 1), 1, 2, 125);
    		addItem(0, new ItemStack(Items.string, 1), 1, 2, 125);
    		addItem(0, new ItemStack(Items.bone, 1), 1, 2, 75);
    		addItem(0, new ItemStack(Items.paper, 1), 1, 2, 75);
    		addItem(0, new ItemStack(Items.flint, 1), 1, 2, 75);
    		// D+: 65.125% 
    		addItem(1, new ItemStack(Items.stick, 1), 2, 4, 100);
    		addItem(1, new ItemStack(Items.feather, 1), 2, 4, 40);
    		addItem(1, new ItemStack(Items.string, 1), 2, 4, 40);
    		addItem(1, new ItemStack(Items.bone, 1), 2, 4, 25);
    		addItem(1, new ItemStack(Items.paper, 1), 2, 4, 25);
    		addItem(1, new ItemStack(Items.flint, 1), 2, 4, 25);
    		addItem(1, new ItemStack(Items.gunpowder, 1), 1, 2,
    			125);
    		addItem(1, new ItemStack(Items.leather, 1), 1, 2,
    			125);
    		addItem(1, new ItemStack(Items.coal, 1, 1), 1, 2, 60);
    		addItem(1, new ItemStack(Items.coal, 1), 1, 2, 20);
    		addItem(1, new ItemStack(Item
    			.getItemFromBlock(Blocks.glass_pane), 1), 1, 2,
    			35);
    		addItem(1, new ItemStack(Item
    			.getItemFromBlock(Blocks.glass), 1), 1, 1, 30);
    		// C: 65%
    		addItem(2, new ItemStack(Items.arrow, 1), 1, 2, 175);
    		addItem(2, new ItemStack(Items.leather, 1), 2, 4,
    			100);
    		addItem(2, new ItemStack(Items.gunpowder, 1), 2, 4,
    			75);
    		addItem(2, new ItemStack(Items.stick, 1), 3, 9, 75);
    		addItem(2, new ItemStack(Items.feather, 1), 3, 9, 10);
    		addItem(2, new ItemStack(Items.string, 1), 3, 9, 10);
    		addItem(2, new ItemStack(Items.bone, 1), 3, 9, 5);
    		addItem(2, new ItemStack(Items.paper, 1), 3, 9, 5);
    		addItem(2, new ItemStack(Items.flint, 1), 3, 9, 5);
    		addItem(2, new ItemStack(Item
    			.getItemFromBlock(Blocks.glass_pane), 1), 2, 8, 70);
    		addItem(2, new ItemStack(Item
    			.getItemFromBlock(Blocks.glass), 1), 2, 4, 45);
    		addItem(2, new ItemStack(Items.coal, 1, 1), 2, 4, 65);
    		addItem(2, new ItemStack(Items.coal, 1), 2, 4, 65);
    		addItem(2, new ItemStack(Items.wheat, 1), 1, 2,
    			30);
    		addItem(2, new ItemStack(Items.sugar, 1), 1, 2,
    			20);
    		addItem(2, new ItemStack(Items.bowl, 1), 1, 1,
    			20);
    		addItem(2, new ItemStack(Items.egg, 1), 1, 2,
    			10);
    		// C+: 67.5%
    		addItem(3, new ItemStack(Items.arrow, 1), 2, 4, 150);
    		addItem(3, new ItemStack(Items.stick, 1), 3, 9,
    			50);
    		addItem(3, new ItemStack(Items.leather, 1), 3, 9,
    			50);
    		addItem(3, new ItemStack(Item
    			.getItemFromBlock(Blocks.glass_pane), 1), 3, 9, 45);
    		addItem(3, new ItemStack(Item
    			.getItemFromBlock(Blocks.glass), 1), 3, 9, 45);
    		addItem(3, new ItemStack(Items.gunpowder, 1), 3, 9,
    			35);
    		addItem(3, new ItemStack(Items.glass_bottle, 1), 1, 4, 25);
    		addItem(3, new ItemStack(Items.wheat, 1), 2, 4,
    			50);
    		addItem(3, new ItemStack(Items.sugar, 1), 2, 4,
    			35);
    		addItem(3, new ItemStack(Items.bowl, 1), 1, 2,
    			25);
    		addItem(3, new ItemStack(Items.egg, 1), 2, 4,
    			15);
    		addItem(3, new ItemStack(Items.coal, 1), 3, 9, 30);
    		addItem(3, new ItemStack(Items.coal, 1, 1), 3, 9, 10);
    		// B: 66%
    		addItem(4, new ItemStack(Items.arrow, 1), 4, 16, 125);
    		addItem(4, new ItemStack(Items.wheat, 1), 3, 9,
    			30);
    		addItem(4, new ItemStack(Items.sugar, 1), 3, 9,
    			20);
    		addItem(4, new ItemStack(Items.egg, 1), 3, 9,
    			10);
    		addItem(4, new ItemStack(Items.bowl, 1), 1, 3,
    			20);
    		// B+: 67.5%
    		// A: 68%
    		// A+: 66.25%
    		// S: 62.5%
    		// S+ 41%
    		// RARE
    		// D+: 1.125%
    		addItem(1, new ItemStack(Items.ender_pearl, 1), 1,
    			1, 12);
    		// C: 3.5%
    		addItem(2, new ItemStack(Items.ender_pearl, 1), 1,
    			2, 28);
    		addItem(2, new ItemStack(Items.experience_bottle, 1), 1,
    			1, 7);
    		// C+ 7%
    		addItem(3, new ItemStack(Items.ender_pearl, 1), 1,
    			2, 35);
    		addItem(3, new ItemStack(Items.experience_bottle, 1), 1,
    			2, 25);
    		addItem(3, new ItemStack(Items.redstone, 1), 1,
    			2, 10);
    		// B: 10.5%
    		addItem(4, new ItemStack(Items.ender_pearl, 1), 2,
    			4, 34);
    		addItem(4, new ItemStack(Items.experience_bottle, 1), 1,
    			3, 25);
    		addItem(4, new ItemStack(Items.redstone, 1), 2,
    			4, 23);
    		addItem(4, new ItemStack(Items.gold_ingot, 1), 1,
    			1, 18);
    		addItem(4, new ItemStack(Item.getItemFromBlock(Blocks.coal_block), 1), 1,
    			1, 4);
    		addItem(5, new ItemStack(Item.getItemFromBlock(ModBlocks.itemScanner), 1), 1,
    			1, 1);
    		// B+ 10.5%
    		addItem(5, new ItemStack(Items.experience_bottle, 1), 1,
    			3, 25);
    		addItem(5, new ItemStack(Items.redstone, 1), 3,
    			9, 30);
    		addItem(5, new ItemStack(Items.gold_ingot, 1), 2,
    			4, 20);
    		addItem(5, new ItemStack(Items.iron_ingot, 1), 1,
    			2, 10);
    		addItem(5, new ItemStack(Items.ender_pearl, 1), 3,
    			9, 10);
    		addItem(5, new ItemStack(Item.getItemFromBlock(Blocks.coal_block), 1), 1,
    			2, 8);
    		addItem(5, new ItemStack(Items.diamond, 1), 1,
    			1, 1);
    		addItem(6, new ItemStack(Items.ender_eye, 1), 1,
    			1, 1);
    		// A: 10.5%
    		addItem(6, new ItemStack(Items.experience_bottle, 1), 1,
    			4, 25);
    		addItem(6, new ItemStack(Items.gold_ingot, 1), 3,
    			9, 25);
    		addItem(6, new ItemStack(Items.iron_ingot, 1), 2,
    			4, 18);
    		addItem(6, new ItemStack(Item.getItemFromBlock(Blocks.redstone_block)), 1, 1, 15);
    		addItem(6, new ItemStack(Item.getItemFromBlock(Blocks.coal_block), 1), 1, 3, 12);
    		addItem(6, new ItemStack(Items.ender_pearl, 1), 4,
    			16, 6);
    		addItem(5, new ItemStack(Items.diamond, 1), 1,
    			1, 3);
    		addItem(6, new ItemStack(Items.ender_eye, 1), 1,
    			2, 2);
    		addItem(6, new ItemStack(Items.emerald, 1), 1,
    			1, 1);
    		// A+ 7%
    		addItem(7, new ItemStack(Items.experience_bottle, 1), 2,
    			5, 18);
    		addItem(7, new ItemStack(Items.iron_ingot, 1), 3,
    			9, 25);
    		addItem(7, new ItemStack(Item.getItemFromBlock(Blocks.redstone_block)), 1, 2, 9);
    		addItem(7, new ItemStack(Item.getItemFromBlock(Blocks.gold_block)), 1, 1, 6);
    		addItem(7, new ItemStack(Item.getItemFromBlock(Blocks.obsidian)), 1, 2, 4);
    		addItem(7, new ItemStack(Items.diamond, 1), 1, 2, 4);
    		addItem(7, new ItemStack(Items.emerald, 1), 1, 2, 2);
    		addItem(7, new ItemStack(Items.skull, 1, 1), 1, 1, 2);
    		// S: 5.25%
    		addItem(8, new ItemStack(Items.experience_bottle, 1), 3,
    			6, 14);
    		addItem(8, new ItemStack(Item.getItemFromBlock(Blocks.obsidian)), 2, 4, 9);
    		addItem(8, new ItemStack(Item.getItemFromBlock(Blocks.redstone_block)), 1, 3, 6);
    		addItem(8, new ItemStack(Item.getItemFromBlock(Blocks.gold_block)), 1, 2, 4);
    		addItem(8, new ItemStack(Item.getItemFromBlock(ModBlocks.itemScannerGold), 1), 1,
    			1, 4);
    		addItem(8, new ItemStack(Items.diamond), 2, 4, 5);
    		addItem(8, new ItemStack(Items.emerald), 2, 4, 3);
    		addItem(8, new ItemStack(Item.getItemFromBlock(Blocks.iron_block)), 1, 1, 3);
    		addItem(8, new ItemStack(Items.skull, 1, 1), 1, 1, 4);
    		addItem(8, new ItemStack(Items.nether_star, 1), 1,
    			1, 1);
    		// S+: 7%
    		addItem(9, new ItemStack(Items.experience_bottle, 1), 4,
    			7, 12);
    		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.obsidian)), 3, 9, 14);
    		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.redstone_block)), 1, 3, 12);
    		addItem(9, new ItemStack(Items.skull, 1, 1), 1, 1, 6);
    		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.gold_block)), 1, 3, 7);
    		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.iron_block)), 1, 2, 6);
    		addItem(9, new ItemStack(Items.diamond), 3, 9, 5);
    		addItem(9, new ItemStack(Items.emerald), 3, 6, 4);
    		addItem(9, new ItemStack(Items.nether_star, 1), 1,
    			1, 2);
    		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.diamond_block)), 1, 1, 1);
    		addItem(9, new ItemStack(Item
    			.getItemFromBlock(Blocks.beacon), 1), 1, 1, 1);
    		// ARMOUR
    		// D+: 3.75%
    		addItem(1, new ItemStack(Items.leather_boots, 1), 1,
    			1, 12);
    		addItem(1, new ItemStack(Items.leather_helmet, 1), 1,
    			1, 11);
    		addItem(1, new ItemStack(Items.leather_leggings, 1), 1,
    			1, 9);
    		addItem(1, new ItemStack(Items.leather_chestplate, 1), 1,
    			1, 6);
    		// C: 3.75%
    		addItem(2, new ItemStack(Items.leather_boots, 1), 1,
    			1, 6);
    		addItem(2, new ItemStack(Items.leather_helmet, 1), 1,
    			1, 6);
    		addItem(2, new ItemStack(Items.leather_leggings, 1), 1,
    			1, 5);
    		addItem(2, new ItemStack(Items.leather_chestplate, 1), 1,
    			1, 3);
    		addItem(2, new ItemStack(Items.chainmail_boots, 1), 1,
    			1, 6);
    		addItem(2, new ItemStack(Items.chainmail_helmet, 1), 1,
    			1, 5);
    		addItem(2, new ItemStack(Items.chainmail_leggings, 1), 1,
    			1, 4);
    		addItem(2, new ItemStack(Items.chainmail_chestplate, 1), 1,
    			1, 3);
    		// C+: 2.5%
    		addItem(3, new ItemStack(Items.chainmail_boots, 1), 1,
    			1, 9);
    		addItem(3, new ItemStack(Items.chainmail_helmet, 1), 1,
    			1, 8);
    		addItem(3, new ItemStack(Items.chainmail_leggings, 1), 1,
    			1, 5);
    		addItem(3, new ItemStack(Items.chainmail_chestplate, 1), 1,
    			1, 3);
    		// B: 2.5%
    		addItem(4, new ItemStack(Items.chainmail_boots, 1), 1,
    			1, 5);
    		addItem(4, new ItemStack(Items.chainmail_helmet, 1), 1,
    			1, 4);
    		addItem(4, new ItemStack(Items.chainmail_leggings, 1), 1,
    			1, 3);
    		addItem(4, new ItemStack(Items.chainmail_chestplate, 1), 1,
    			1, 2);
    		addItem(4, new ItemStack(Items.golden_boots, 1), 1,
    			1, 4);
    		addItem(4, new ItemStack(Items.golden_helmet, 1), 1,
    			1, 4);
    		addItem(4, new ItemStack(Items.golden_leggings, 1), 1,
    			1, 2);
    		addItem(4, new ItemStack(Items.golden_chestplate, 1), 1,
    			1, 1);
    		// B+: 2.5%
    		addItem(5, new ItemStack(Items.golden_boots, 1), 1,
    			1, 9);
    		addItem(5, new ItemStack(Items.golden_helmet, 1), 1,
    			1, 8);
    		addItem(5, new ItemStack(Items.golden_leggings, 1), 1,
    			1, 5);
    		addItem(5, new ItemStack(Items.golden_chestplate, 1), 1,
    			1, 3);
    		// A: 2.5%
    		addItem(6, new ItemStack(Items.golden_boots, 1), 1,
    			1, 5);
    		addItem(6, new ItemStack(Items.golden_helmet, 1), 1,
    			1, 4);
    		addItem(6, new ItemStack(Items.golden_leggings, 1), 1,
    			1, 3);
    		addItem(6, new ItemStack(Items.golden_chestplate, 1), 1,
    			1, 2);
    		addItem(6, new ItemStack(Items.iron_boots, 1), 1,
    			1, 4);
    		addItem(6, new ItemStack(Items.iron_helmet, 1), 1,
    			1, 4);
    		addItem(6, new ItemStack(Items.iron_leggings, 1), 1,
    			1, 2);
    		addItem(6, new ItemStack(Items.iron_chestplate, 1), 1,
    			1, 1);
    		// A+: 3.75%
    		addItem(7, new ItemStack(Items.iron_boots, 1), 1,
    			1, 12);
    		addItem(7, new ItemStack(Items.iron_helmet, 1), 1,
    			1, 11);
    		addItem(7, new ItemStack(Items.iron_leggings, 1), 1,
    			1, 9);
    		addItem(7, new ItemStack(Items.iron_chestplate, 1), 1,
    			1, 6);
    		// S: 3.75%
    		addItem(8, new ItemStack(Items.iron_boots, 1), 1,
    			1, 6);
    		addItem(8, new ItemStack(Items.iron_helmet, 1), 1,
    			1, 6);
    		addItem(8, new ItemStack(Items.iron_leggings, 1), 1,
    			1, 5);
    		addItem(8, new ItemStack(Items.iron_chestplate, 1), 1,
    			1, 3);
    		addItem(8, new ItemStack(Items.diamond_boots, 1), 1,
    			1, 6);
    		addItem(8, new ItemStack(Items.diamond_helmet, 1), 1,
    			1, 5);
    		addItem(8, new ItemStack(Items.diamond_leggings, 1), 1,
    			1, 4);
    		addItem(8, new ItemStack(Items.diamond_chestplate, 1), 1,
    			1, 3);
    		// S+ 5%
    		addItem(9, new ItemStack(Items.diamond_boots, 1), 1,
    			1, 15);
    		addItem(9, new ItemStack(Items.diamond_helmet, 1), 1,
    			1, 14);
    		addItem(9, new ItemStack(Items.diamond_leggings, 1), 1,
    			1, 11);
    		addItem(9, new ItemStack(Items.diamond_chestplate, 1), 1,
    			1, 10);
    		addItem(2, new ItemStack(Items.enchanted_book, 1), 1, 1, 1);
    		addItem(3, new ItemStack(Items.enchanted_book, 1), 1, 1, 3);
    		addItem(4, new ItemStack(Items.enchanted_book, 1), 1, 1, 5);
    		addItem(5, new ItemStack(Items.enchanted_book, 1), 1, 1, 10);
    		addItem(6, new ItemStack(Items.enchanted_book, 1), 1, 1, 20);
    		addItem(7, new ItemStack(Items.enchanted_book, 1), 1, 1, 40);
    		addItem(8, new ItemStack(Items.enchanted_book, 1), 1, 1, 80);
    		addItem(9, new ItemStack(Items.enchanted_book, 1), 1, 1, 160);
		}
		/*
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.tnt), 1), 1, 4, 15)); // TNT
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.obsidian), 1), 1, 4, 10)); //
		 * Obsidian chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.glowstone), 1), 1, 6, 20)); //
		 * Glowstone chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.iron_bars), 2), 1, 8, 15)); //
		 * Iron Bars chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.melon_block), 1), 1, 6, 25));
		 * // Melon chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.glowstone), 1), 1, 4, 15)); //
		 * Glowstone Lamp chestContents.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.emerald_block), 1), 1, 2, 5));
		 * // Block of Emerald chestContents.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.anvil), 1), 1, 1, 2)); //
		 * Anvil chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.anvil), 1, 1), 1, 1, 3)); //
		 * Anvil (Slightly Damaged) chestContents.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.anvil), 1, 2), 1, 1, 5)); //
		 * Anvil (Very Damaged) chestContents.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.redstone_block), 1), 1, 2,
		 * 4)); // Block of Redstone chestContents.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.slime_block), 1), 1, 2, 6));
		 * // Slime Block chestContents.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.coal_block), 1), 1, 3, 20));
		 * // Block of Coal chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.iron_shovel, 1), 1, 1,
		 * 5)); // Iron Shovel chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.iron_pickaxe, 1), 1,
		 * 1, 6)); // Iron Pickaxe chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.iron_axe, 1), 1, 1,
		 * 5)); // Iron Axe chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.flint_and_steel, 1),
		 * 1, 1, 10)); // Flint and Steel chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.apple, 4), 1, 4, 40));
		 * // Apple chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.bow, 1), 1, 1, 20)); // Bow chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.arrow, 4), 1, 4, 20));
		 * // Arrow chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.coal, 4), 1, 4, 25)); // Coal
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.coal, 4, 1), 1, 4, 50)); // Charcoal
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.diamond, 1), 1, 2, 2)); // Diamond Gem
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.iron_ingot, 1), 1, 4, 20)); // Iron Ingot
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.gold_ingot, 1), 1, 2, 30)); // Gold Ingot
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.iron_sword, 1), 1, 1, 4)); // Iron Sword
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.diamond_sword, 1), 1, 1, 1)); // Diamond Sword
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.diamond_shovel, 1), 1, 1, 1)); // Diamond Shovel
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.diamond_pickaxe, 1), 1, 1, 1)); // Diamond Pickaxe
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.diamond_axe, 1), 1, 1, 1)); // Diamond Axe
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.bowl, 1), 1, 2, 20)); // Bowl
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.golden_sword, 1), 1, 1, 13)); // Gold Sword
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.golden_shovel, 1), 1, 1, 15)); // Gold Shovel
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.golden_pickaxe, 1), 1, 1, 17)); // Gold Pickaxe
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.golden_axe, 1), 1, 1, 15)); // Gold Axe
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.string, 2), 1, 4, 30)); // String
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.feather, 2), 1, 4, 30)); // Feather
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.bread, 1), 1, 4, 40)); // Bread
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.iron_hoe, 1), 1, 1, 5)); // Iron Hoe
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.diamond_hoe, 1), 1, 1, 1)); // Diamond Hoe
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.golden_hoe, 1), 1, 1, 15)); // Gold Hoe
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.chainmail_helmet, 1), 1, 1, 13)); // Chainmail Helmet
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.chainmail_chestplate, 1), 1, 1, 8)); // Chainmail
		 * Chestplate chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.chainmail_leggings, 1), 1, 1, 8)); // Chainmail
		 * Leggings chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.chainmail_boots, 1), 1, 1, 13)); // Chainmail Boots
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.iron_helmet, 1), 1, 1, 6)); // Iron Helmet
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.iron_chestplate, 1), 1, 1, 4)); // Iron Chestplate
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.iron_leggings, 1), 1, 1, 4)); // Iron Leggings
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.iron_boots, 1), 1, 1, 6)); // Iron Boots
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.golden_apple, 1), 1, 1, 2)); // Golden Apple
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.bucket, 1), 1, 1, 10)); // Bucket
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.slime_ball, 2), 1, 4, 20)); // Slime Ball
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.bone, 1), 1, 1, 50)); // Bone
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.rotten_flesh, 1), 1, 8, 50)); // Rotten Flesh
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.emerald, 1), 1, 4, 15)); // Emerald
		 * chestContents.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8193), 1, 1, 1)); // Regeneration
		 * Potion (0:45) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8197),
		 * 1, 1, 1)); // Healing Potion (0:45) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8198),
		 * 1, 1, 10)); // Night Vision Potion (3:00) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8201),
		 * 1, 1, 1)); // Strength Potion (3:00) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8206),
		 * 1, 1, 1)); // Invisibility Potion (3:00) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16385),
		 * 1, 1, 2)); // Regeneration Splash (0:33) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16389),
		 * 1, 1, 2)); // Healing Splash chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16390),
		 * 1, 1, 15)); // Night Vision Splash (2:15) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16393),
		 * 1, 1, 2)); // Strength Splash (2:15) chestContents.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16398),
		 * 1, 1, 2)); // Invisibility Splash (2:15) //chestContents.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(ChaosBlock.itemExtendedNightVisionSplash), 1, 1, 50)); //
		 * Night Vision Splash (16:00)
		 * 
		 * chestContents_High.setMin(12); // inclusive
		 * chestContents_High.setMax(21); // exclusive
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.obsidian), 4), 1, 4, 10)); //
		 * Obsidian chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.noteblock), 1), 1, 4, 10)); //
		 * Note Block chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.sticky_piston), 1), 1, 3, 7));
		 * // Sticky Piston chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.diamond_block), 1), 1, 1, 3));
		 * // Block of Diamond chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.jukebox), 1), 1, 4, 10)); //
		 * Juke Box chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.enchanting_table), 1), 1, 1,
		 * 2)); // Enchantment Table chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.emerald_block), 16), 1, 4,
		 * 2)); // Block of Emerald chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.beacon), 1), 1, 2, 1)); //
		 * Beacon chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.skull), 1, 1), 1, 3, 3)); //
		 * Head Block (Wither) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.sea_lantern), 1), 1, 3, 3));
		 * // Sea Lantern chestContents_High.addItem(new
		 * WeightedRandomChestContent(new
		 * ItemStack(Item.getItemFromBlock(Blocks.coal_block), 4), 1, 5, 20));
		 * // Block of Coal chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond, 2), 1, 3,
		 * 25)); // Diamond Gem chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_sword, 1), 1,
		 * 1, 8)); // Diamond Sword chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_shovel, 1), 1,
		 * 1, 10)); // Diamond Shovel chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_pickaxe, 1),
		 * 1, 1, 12)); // Diamond Pickaxe chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_axe, 1), 1, 1,
		 * 10)); // Diamond Axe chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_hoe, 1), 1, 1,
		 * 10)); // Diamond Hoe chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_helmet, 1), 1,
		 * 1, 8)); // Diamond Helmet chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_chestplate,
		 * 1), 1, 1, 6)); // Diamond Chestplate chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_leggings, 1),
		 * 1, 1, 6)); // Diamond Leggings chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_boots, 1), 1,
		 * 1, 8)); // Diamond Boots chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.golden_apple, 1, 1),
		 * 1, 1, 10)); // Enchanted Golden Apple chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8193),
		 * 1, 1, 5)); // Regeneration Potion (0:45)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8194), 1, 1, 5)); // Swiftness Potion
		 * (3:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8195), 1, 1, 3)); // Fire Resistance
		 * Potion (3:00) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8196),
		 * 1, 1, 3)); // Poison Potion (0:45) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8197),
		 * 1, 1, 5)); // Healing Potion chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8198),
		 * 1, 1, 7)); // Night Vision Potion (3:00)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8200), 1, 1, 5)); // Weakness Potion
		 * (1:30) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8201), 1, 1, 4)); // Strength Potion
		 * (3:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8202), 1, 1, 3)); // Slowness Potion
		 * (1:30) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8204), 1, 1, 3)); // Harming Potion
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8205), 1, 1, 5)); // Water Breathing
		 * Potion (3:00) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8206),
		 * 1, 1, 5)); // Invisibility Potion (3:00)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8225), 1, 1, 3)); // Regeneration
		 * Potion II (0:22) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8226),
		 * 1, 1, 3)); // Swiftness Potion II (1:30)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8228), 1, 1, 1)); // Poison Potion II
		 * (0:22) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8229), 1, 1, 3)); // Healing Potion II
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8233), 1, 1, 3)); // Strength Potion
		 * II (1:30) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8235),
		 * 1, 1, 3)); // Leaping Potion II (1:30) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8236),
		 * 1, 1, 1)); // Harming Potion II chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8257),
		 * 1, 1, 4)); // Regeneration Potion (2:00)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8258), 1, 1, 4)); // Swiftness Potion
		 * (8:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8259), 1, 1, 4)); // Fire Resistance
		 * Potion (8:00) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8260),
		 * 1, 1, 2)); // Poison Potion (2:00) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8262),
		 * 1, 1, 5)); // Night Vision Potion II (8:00)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8264), 1, 1, 2)); // Weakness Potion
		 * (4:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8265), 1, 1, 3)); // Strength Potion
		 * (8:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8266), 1, 1, 2)); // Slowness Potion
		 * (4:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8267), 1, 1, 3)); // Leaping Potion
		 * (3:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8269), 1, 1, 3)); // Water Breathing
		 * Potion (8:00) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8270),
		 * 1, 1, 3)); // Invisibility Potion (8:00)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8289), 1, 1, 1)); // Regeneration
		 * Potion II (1:00) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8290),
		 * 1, 1, 1)); // Swiftness Potion II (4:00)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8292), 1, 1, 1)); // Poison Potion II
		 * (1:00) chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.potionitem, 1, 8297), 1, 1, 1)); // Strength Potion
		 * II (4:00) chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16454),
		 * 1, 1, 5)); // Night Vision Splash (6:00)
		 * chestContents_High.addItem(new WeightedRandomChestContent(new
		 * ItemStack(Items.experience_bottle, 1), 1, 1, 15)); // Bottle of
		 * Enchanting chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.nether_star, 1), 1, 2,
		 * 5)); // Nether Star chestContents_High.addItem(new
		 * WeightedRandomChestContent(new ItemStack(Items.diamond_horse_armor,
		 * 1), 1, 1, 5)); // Diamond Horse Armor
		 */}

	private static void addItem(int index, ItemStack item,
		int minCount, int maxCount, int weightIn) {
		final int dStart = Math.max(index - 1, 0);
		for (int d = dStart; d <= index && d < 10; d++) {
			int weight;
			
			if (d == index)
				weight = weightIn;
			else {
				/*diff = d < index ? (((int) ((1 / ((index - d))) * 4)) / 4) + 1
					: (d - index) + 1;
				maxCount = (int) Math.min(Math.max(
					maxCountIn * (diff * 0.5), 1), item.getMaxStackSize());
				weight = (int) Math.max(weightIn / diff, 1);*/
				weight = (int) (weightIn * 0.125);
			}
			
			if (weight > 0)
    			chestContents[d]
    				.addItem(new WeightedRandomChestContent(
    					item, minCount, maxCount, weight));
		}
	}
}
