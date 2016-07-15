package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.samuel.mazetowers.MazeTowers;

public class ItemSpectriteSwordSpecial extends ItemSpectriteSword {

	public ItemSpectriteSwordSpecial() {
		super(MazeTowers.SPECTRITE_TOOL);
	}
	
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean Adva){
    	
    }

    @Override
    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack) {
        return 90000;
    }
}