package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemExplosiveArrow extends Item {

	public ItemExplosiveArrow() {
		super();
		this.setCreativeTab(CreativeTabs.tabCombat);
	}

	@Override
	public void addInformation(ItemStack stack,
		EntityPlayer player, List list, boolean Adva) {
		int lineCount = 0;
		boolean isLastLine = false;
		String curLine;
		while (!isLastLine) {
			isLastLine = (curLine = StatCollector
				.translateToLocal(("iteminfo.explosive_arrow.l" + ++lineCount)))
				.endsWith("@");
			list.add(!isLastLine ? curLine : curLine
				.substring(0, curLine.length() - 1));
		}
	}
}