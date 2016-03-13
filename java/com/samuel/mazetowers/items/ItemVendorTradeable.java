package com.samuel.mazetowers.items;

import com.samuel.mazetowers.etc.IVendorTradeable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public abstract class ItemVendorTradeable extends Item implements IVendorTradeable {
	
final int professionId, minTradeLevel, maxTradeLevel, minTradeChance, maxTradeChance, tradeLevelDiff;

	public ItemVendorTradeable(int professionId, int minTradeLevel, int maxTradeLevel, int minTradeChance,
		int maxTradeChance) {
		super();
		this.professionId = professionId;
		this.minTradeLevel = minTradeLevel;
		this.maxTradeLevel = maxTradeLevel;
		this.minTradeChance = minTradeChance;
		this.maxTradeChance = maxTradeChance;
		tradeLevelDiff = maxTradeLevel - minTradeLevel;
	}
	
	public ItemVendorTradeable(int professionId, int minTradeChance,
		int maxTradeChance) {
		this(professionId, 0, 9, minTradeChance, maxTradeChance);
	}
	
	public ItemVendorTradeable(int professionId) {
		this(professionId, 0, 9, 1000, 1000);
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
