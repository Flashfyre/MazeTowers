package com.samuel.mazetowers.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.samuel.mazetowers.etc.IMetaBlockName;

public class ItemBlockVendorTradableMeta extends ItemBlockVendorTradable {

	public ItemBlockVendorTradableMeta(Block block, int professionId,
			int minTradeLevel, int maxTradeLevel, int minTradeChance,
			int maxTradeChance, int minPrice, int maxPrice) {
		super(block, professionId, minTradeLevel, maxTradeLevel, minTradeChance,
			maxTradeChance, minPrice, maxPrice);
		 if (!(block instanceof IMetaBlockName))
            throw new IllegalArgumentException(String.format("The given Block %s is not an instance of IMetaBlockName!",
            	block.getUnlocalizedName()));
		 this.setMaxDamage(0);
	     this.setHasSubtypes(true);
	}
	
	public ItemBlockVendorTradableMeta(Block block, int professionId, int minTradeChance,
		int maxTradeChance, int minPrice, int maxPrice) {
		this(block, professionId, 0, 9, minTradeChance, maxTradeChance, minPrice, maxPrice);
	}
	
	public ItemBlockVendorTradableMeta(Block block, int professionId) {
		this(block, professionId, 0, 9, 1000, 1000, 1, 1);
	}
	
	@Override
    public int getMetadata(int damage)
    {
        return damage;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" +
        	((IMetaBlockName)this.block).getSpecialName(stack);
    }
}
