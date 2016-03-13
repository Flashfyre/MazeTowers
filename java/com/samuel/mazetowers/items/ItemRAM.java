package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRAM extends ItemVendorTradeable {
	
    public ItemRAM() {
    	super(1, 3, 6, 250, 1000);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabMaterials);
    }
}
