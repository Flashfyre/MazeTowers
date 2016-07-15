package com.samuel.mazetowers.blocks;

import com.samuel.mazetowers.MazeTowers;

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
