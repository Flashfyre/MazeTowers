package com.samuel.mazetowers.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockLock;
import com.samuel.mazetowers.etc.IMetaBlockName;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.tileentities.TileEntityLock;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

public class ItemBlockLock extends ItemBlock {
	
	private int[] colors;

    public ItemBlockLock(Block block) {
        super(block);
        if (!(block instanceof IMetaBlockName))
            throw new IllegalArgumentException(String.format("The given Block %s is not an instance of ISpecialBlockName!", block.getUnlocalizedName()));
        EnumDyeColor[][] dyeColors = EnumTowerType.getAllBeaconColors();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
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
    public int getMetadata(int damage)
    {
        return damage;
    }
    
    @Override
    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!playerIn.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack))
        {
            int i = this.getMetadata(stack.getMetadata());
            IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, side, hitX,
            	hitY, hitZ, i, playerIn);
            	
            if (placeBlockAt(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, iblockstate1))
            {
                worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F),
                	(double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F),
                	this.block.stepSound.getPlaceSound(), (this.block.stepSound
                	.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
                TileEntityLock te = (TileEntityLock) worldIn.getTileEntity(pos);
                te.setTypeIndex(stack.getItemDamage());
                worldIn.setTileEntity(pos, te);
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." +
        	((IMetaBlockName)this.block).getSpecialName(stack);
    }
}