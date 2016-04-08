package com.samuel.mazetowers.items;

import net.minecraft.item.Item;

import com.samuel.mazetowers.etc.IVendorTradable;

public abstract class ItemVendorTradable extends Item implements IVendorTradable {
	
	final int professionId, minTradeLevel, maxTradeLevel, minTradeChance, maxTradeChance,
		minPrice, maxPrice, tradeLevelDiff;

	public ItemVendorTradable(int professionId, int minTradeLevel, int maxTradeLevel, int minTradeChance,
		int maxTradeChance, int minPrice, int maxPrice) {
		super();
		this.professionId = professionId;
		this.minTradeLevel = minTradeLevel;
		this.maxTradeLevel = maxTradeLevel;
		this.minTradeChance = minTradeChance;
		this.maxTradeChance = maxTradeChance;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		tradeLevelDiff = maxTradeLevel - minTradeLevel;
	}
	
	public ItemVendorTradable(int professionId, int minTradeChance,
		int maxTradeChance, int minPrice, int maxPrice) {
		this(professionId, 0, 9, minTradeChance, maxTradeChance, minPrice, maxPrice);
	}
	
	public ItemVendorTradable(int professionId) {
		this(professionId, 0, 9, 1000, 1000, 1, 1);
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
