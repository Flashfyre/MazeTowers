package com.samuel.mazetowers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import com.samuel.mazetowers.etc.IVendorTradable;

public abstract class BlockVendorTradable extends Block implements IVendorTradable {
	
	final int professionId, minTradeLevel, maxTradeLevel, minTradeChance, maxTradeChance,
	minPrice, maxPrice, tradeLevelDiff;
	
	public BlockVendorTradable(Material materialIn, MapColor mapColor, int professionId, int minTradeLevel,
		int maxTradeLevel, int minTradeChance, int maxTradeChance, int minPrice, int maxPrice) {
		super(materialIn);
		this.professionId = professionId;
		this.minTradeLevel = minTradeLevel;
		this.maxTradeLevel = maxTradeLevel;
		this.minTradeChance = minTradeChance;
		this.maxTradeChance = maxTradeChance;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		tradeLevelDiff = maxTradeLevel - minTradeLevel;
	}
	
	public BlockVendorTradable(Material materialIn, int professionId, int minTradeLevel,
		int maxTradeLevel, int minTradeChance, int maxTradeChance, int minPrice, int maxPrice) {
		this(materialIn, materialIn.getMaterialMapColor(), professionId, minTradeLevel,
			maxTradeLevel, minTradeChance, maxTradeChance, minPrice, maxPrice);
	}
	
	public BlockVendorTradable(Material materialIn, int professionId, int minTradeChance,
		int maxTradeChance, int minPrice, int maxPrice) {
		this(materialIn, materialIn.getMaterialMapColor(), professionId, 0, 9, minTradeChance, maxTradeChance,
			minPrice, maxPrice);
	}
	
	public BlockVendorTradable(Material materialIn, MapColor mapColor, int professionId,
		int minTradeChance, int maxTradeChance, int minPrice, int maxPrice) {
		this(materialIn, mapColor, professionId, 0, 9, minTradeChance, maxTradeChance, minPrice, maxPrice);
	}
	
	public BlockVendorTradable(Material materialIn, MapColor mapColor, int professionId) {
		this(materialIn, mapColor, professionId, 0, 9, 1000, 1000, 1, 1);
	}
	
	public BlockVendorTradable(Material materialIn, int professionId) {
		this(materialIn, materialIn.getMaterialMapColor(), professionId, 0, 9, 1000, 1000);
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
