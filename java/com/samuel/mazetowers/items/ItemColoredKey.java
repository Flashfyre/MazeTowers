package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.blocks.BlockExtraDoor;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.init.ModSounds;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

public class ItemColoredKey extends ItemKey {
	
	private int[] colors;
	
	public ItemColoredKey() {
		super();
		EnumDyeColor[][] dyeColors = EnumTowerType.getAllBeaconColors();
		colors = new int[dyeColors.length];
		for (int t = 0; t < dyeColors.length; t++) {
			float[] rgbMix = new float[3];
			for (int c = 0; c < dyeColors[t].length; c++) {
        		float[] rgb = EntitySheep.getDyeRgb(dyeColors[t][c]);
        		rgbMix[0] += rgb[0] / dyeColors[t].length;
        		rgbMix[1] += rgb[1] / dyeColors[t].length;
        		rgbMix[2] += rgb[2] / dyeColors[t].length;
			}
			colors[t] = MTUtils.RGBToInt(rgbMix[0], rgbMix[1], rgbMix[2]);
		}
		setHasSubtypes(true);
	}
	
	public int[] getColors() {
		return colors;
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
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
	    World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			IBlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof BlockExtraDoor &&
				block == EnumTowerType.values()[stack.getItemDamage()].getDoorBlock()) {
				boolean isOpen = (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER ?
					worldIn.getBlockState(pos.down()) : state).getValue(BlockDoor.OPEN);
				((BlockDoor) block).toggleDoor(worldIn, pos, !isOpen);
				worldIn.playSound(null, pos, ModSounds.door_unlock, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return EnumActionResult.SUCCESS;
			}
		}
		
		return EnumActionResult.PASS;
	}
}
