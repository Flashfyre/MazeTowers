package com.samuel.mazetowers.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import com.samuel.mazetowers.etc.IVendorTradable;

public abstract class ItemBlockVendorTradable extends ItemBlock implements IVendorTradable {
	
	final int professionId, minTradeLevel, maxTradeLevel, minTradeChance, maxTradeChance,
		minPrice, maxPrice, tradeLevelDiff;

	public ItemBlockVendorTradable(Block block, int professionId, int minTradeLevel, int maxTradeLevel, int minTradeChance,
		int maxTradeChance, int minPrice, int maxPrice) {
		super(block);
		this.professionId = professionId;
		this.minTradeLevel = minTradeLevel;
		this.maxTradeLevel = maxTradeLevel;
		this.minTradeChance = minTradeChance;
		this.maxTradeChance = maxTradeChance;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		tradeLevelDiff = maxTradeLevel - minTradeLevel;
	}
	
	public ItemBlockVendorTradable(Block block, int professionId, int minTradeChance,
		int maxTradeChance, int minPrice, int maxPrice) {
		this(block, professionId, 0, 9, minTradeChance, maxTradeChance, minPrice, maxPrice);
	}
	
	public ItemBlockVendorTradable(Block block, int professionId) {
		this(block, professionId, 0, 9, 1000, 1000, 1, 1);
	}
	
	@Override
	public int getVendorProfessionId() {
		return professionId;
	}

	@Override
	public int getVendorTradeChance(int difficulty) {
		if (difficulty >= minTradeLevel)
			return difficulty < maxTradeLevel ? minTradeChance +
				(((maxTradeChance - minTradeChance) / tradeLevelDiff) *
				(difficulty - minTradeLevel)) : maxTradeChance;
		else
			return 0;
	}
}
