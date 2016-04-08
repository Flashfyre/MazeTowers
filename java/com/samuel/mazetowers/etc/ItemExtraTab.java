package com.samuel.mazetowers.etc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;

public final class ItemExtraTab extends CreativeTabs {
	
    public ItemExtraTab(int par1, String par2Str) {
        super(par1, par2Str);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return Item.getItemFromBlock(MazeTowers.BlockStoneBrickWall);
    }
}