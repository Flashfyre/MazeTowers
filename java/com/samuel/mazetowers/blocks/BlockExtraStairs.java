package com.samuel.mazetowers.blocks;

import java.util.Random;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class BlockExtraStairs extends BlockStairs {

	private boolean noDrop;

	public BlockExtraStairs(IBlockState modelState) {
		super(modelState);
		if (modelState == Blocks.packed_ice
			.getDefaultState())
			this.slipperiness = 0.98F;
		this.setCreativeTab(MazeTowers.tabExtra);
	}

	@Override
	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
		return !noDrop ? super.quantityDropped(random) : 0;
	}
}
