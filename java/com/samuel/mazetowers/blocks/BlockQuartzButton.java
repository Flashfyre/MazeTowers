package com.samuel.mazetowers.blocks;

import net.minecraft.block.BlockButton;
import net.minecraft.creativetab.CreativeTabs;

public class BlockQuartzButton extends BlockButton {
	
	public BlockQuartzButton(String unlocalizedName) {
		super(false);
		setCreativeTab(CreativeTabs.tabRedstone);
		setUnlocalizedName(unlocalizedName);
	}
}
