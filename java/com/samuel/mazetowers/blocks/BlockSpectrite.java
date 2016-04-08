package com.samuel.mazetowers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockSpectrite extends Block {

	public BlockSpectrite() {
		super(Material.iron, MapColor.purpleColor);
		setStepSound(SoundType.ANVIL);
	}

}
