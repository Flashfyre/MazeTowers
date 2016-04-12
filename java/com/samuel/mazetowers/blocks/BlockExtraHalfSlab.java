package com.samuel.mazetowers.blocks;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.block.state.IBlockState;

public class BlockExtraHalfSlab extends BlockExtraSlab {

	public BlockExtraHalfSlab() {
		super();
		setCreativeTab(MazeTowers.TabExtra);
	}

	@Override
	public boolean isDouble() {
		return false;
	}
}
