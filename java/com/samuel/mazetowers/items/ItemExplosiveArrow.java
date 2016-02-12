package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemExplosiveArrow extends Item {
	
	public ItemExplosiveArrow() {
		super();
		this.setCreativeTab(CreativeTabs.tabCombat);
	}
}