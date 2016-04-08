package com.samuel.mazetowers.items;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.IKeyItem;

public abstract class ItemKey extends ItemVendorTradable implements IKeyItem {
	
	public ItemKey() {
		this(0, 9, 1000, 1000, 1, 1);
	}
	
	public ItemKey(int minTradeLevel, int maxTradeLevel, int minTradeChance,
		int maxTradeChance, int minPrice, int maxPrice) {
		super(3, minTradeLevel, maxTradeLevel, minTradeChance, maxTradeChance, minPrice, maxPrice);
		setMaxStackSize(1);
		setMaxDamage(0);
		setCreativeTab(MazeTowers.TabExtra);
	}
}
