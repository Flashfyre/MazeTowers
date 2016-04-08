package com.samuel.mazetowers.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.tileentity.TileEntityLock;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

public class ItemBlockLock extends ItemBlockVendorTradableMeta {
	
	private int[] colors;

    public ItemBlockLock(Block block) {
        super(block, 3, 0, 9, 500, 100, 2, 4);
        EnumDyeColor[][] dyeColors = EnumTowerType.getAllBeaconColors();
        setCreativeTab(MazeTowers.TabExtra);
		setMaxStackSize(1);
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
    }
    
    @SideOnly(Side.CLIENT)
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
    	return colors[stack.getItemDamage()];
    }

    @Override
    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,  EnumHand hand,
    	EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        if (stack.stackSize == 0)
        {
            return EnumActionResult.FAIL;
        }
        else if (!playerIn.canPlayerEdit(pos, side, stack))
        {
        	return EnumActionResult.FAIL;
        }
        else if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack))
        {
            int i = this.getMetadata(stack.getMetadata());
            IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, side, hitX,
            	hitY, hitZ, i, playerIn);
            	
            if (placeBlockAt(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, iblockstate1))
            {
                worldIn.playSound(pos.getX() + 0.5F,
                	pos.getY() + 0.5F, pos.getZ() + 0.5F,
                	this.block.getStepSound().getPlaceSound(), SoundCategory.BLOCKS, (this.block.getStepSound()
                	.getVolume() + 1.0F) / 2.0F, this.block.getStepSound().getPitch() * 0.8F, true);
                TileEntityLock te = (TileEntityLock) worldIn.getTileEntity(pos);
                te.setTypeIndex(stack.getItemDamage());
                worldIn.setTileEntity(pos, te);
                --stack.stackSize;
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
}