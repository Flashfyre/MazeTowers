package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.UniversalBucket;

public class ItemChaoticSludgeBucket extends UniversalBucket {
	
    public ItemChaoticSludgeBucket()
    {
    	super();
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabMisc);
    }
}
