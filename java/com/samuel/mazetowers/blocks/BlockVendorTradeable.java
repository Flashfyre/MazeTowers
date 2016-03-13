package com.samuel.mazetowers.blocks;

import com.samuel.mazetowers.etc.IVendorTradeable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public abstract class BlockVendorTradeable extends Block implements IVendorTradeable {
	
	final int professionId, minTradeLevel, maxTradeLevel, minTradeChance, maxTradeChance, tradeLevelDiff;
	
	public BlockVendorTradeable(Material materialIn, MapColor mapColor, int professionId, int minTradeLevel,
		int maxTradeLevel, int minTradeChance, int maxTradeChance) {
		super(materialIn);
		this.professionId = professionId;
		this.minTradeLevel = minTradeLevel;
		this.maxTradeLevel = maxTradeLevel;
		this.minTradeChance = minTradeChance;
		this.maxTradeChance = maxTradeChance;
		tradeLevelDiff = maxTradeLevel - minTradeLevel;
	}
	
	public BlockVendorTradeable(Material materialIn, int professionId, int minTradeLevel,
		int maxTradeLevel, int minTradeChance, int maxTradeChance) {
		this(materialIn, materialIn.getMaterialMapColor(), professionId, minTradeLevel,
			maxTradeLevel, minTradeChance, maxTradeChance);
	}
	
	public BlockVendorTradeable(Material materialIn, int professionId, int minTradeChance,
		int maxTradeChance) {
		this(materialIn, materialIn.getMaterialMapColor(), professionId, 0, 9, minTradeChance, maxTradeChance);
	}
	
	public BlockVendorTradeable(Material materialIn, MapColor mapColor, int professionId,
		int minTradeChance, int maxTradeChance) {
		this(materialIn, mapColor, professionId, 0, 9, minTradeChance, maxTradeChance);
	}
	
	public BlockVendorTradeable(Material materialIn, MapColor mapColor, int professionId) {
		this(materialIn, mapColor, professionId, 0, 9, 1000, 1000);
	}
	
	public BlockVendorTradeable(Material materialIn, int professionId) {
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
