package com.samuel.mazetowers.items;

import java.util.List;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemKey extends ItemVendorTradeable {
	
	private int[] colors;
	
	public ItemKey() {
		super(3);
		EnumDyeColor[][] dyeColors = EnumTowerType.getAllBeaconColors();
		colors = new int[dyeColors.length];
		for (int t = 0; t < dyeColors.length; t++) {
			float[] rgbMix = new float[3];
			for (int c = 0; c < dyeColors[t].length; c++) {
        		float[] rgb = EntitySheep.func_175513_a(dyeColors[t][c]);
        		rgbMix[0] += rgb[0] / dyeColors[t].length;
        		rgbMix[1] += rgb[1] / dyeColors[t].length;
        		rgbMix[2] += rgb[2] / dyeColors[t].length;
			}
			colors[t] = MTUtils.RGBToInt(rgbMix[0], rgbMix[1], rgbMix[2]);
		}
		setCreativeTab(MazeTowers.tabExtra);
		setHasSubtypes(true);
		setMaxStackSize(1);
		setMaxDamage(0);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
    	return colors[stack.getItemDamage()];
    }
	 
    @Override
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *  
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        for (int i = 0; i < colors.length; ++i)
            subItems.add(new ItemStack(itemIn, 1, i));
    }

    @Override
    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage) {
        return damage;
    }
	
	@Override
	/**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.getUnlocalizedName() + "_" + stack.getItemDamage();
    }
}
