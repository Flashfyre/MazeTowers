package com.samuel.mazetowers.items;

import net.minecraft.creativetab.CreativeTabs;

public class ItemRAM extends ItemVendorTradable {
	
    public ItemRAM() {
    	super(1, 3, 6, 250, 1000);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MATERIALS);
    }
}
