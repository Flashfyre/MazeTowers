package com.samuel.mazetowers.items;

import com.samuel.mazetowers.etc.IVendorTradeable;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlockRedstoneClock extends ItemBlock implements IVendorTradeable {
	
	private final int professionId, minTradeLevel, maxTradeLevel, minTradeChance,
	maxTradeChance, tradeLevelDiff;

	public ItemBlockRedstoneClock(Block block) {
		super(block);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		professionId = 1;
		minTradeLevel = 6;
		maxTradeLevel = 9;
		minTradeChance = 20;
		maxTradeChance = 200;
		tradeLevelDiff = maxTradeLevel - minTradeLevel;
		this.setCreativeTab(CreativeTabs.tabRedstone);
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

	@Override
	public int getVendorProfessionId() {
		return professionId;
	}
}
