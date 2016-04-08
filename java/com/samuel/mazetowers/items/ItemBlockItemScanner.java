package com.samuel.mazetowers.items;

import net.minecraft.block.Block;

import com.samuel.mazetowers.blocks.BlockItemScannerGold;

public class ItemBlockItemScanner extends ItemBlockVendorTradable {
	
	public ItemBlockItemScanner(Block block) {
		super(block, 1, 5, 7, !(block instanceof BlockItemScannerGold) ? 100 : 500,
			!(block instanceof BlockItemScannerGold) ? 25 : 250,
			!(block instanceof BlockItemScannerGold) ? 3 : 5,
			!(block instanceof BlockItemScannerGold) ? 7 : 10);
	}
}
