package com.samuel.mazetowers.items;

import com.samuel.mazetowers.blocks.BlockExtraDoubleSlab;
import com.samuel.mazetowers.blocks.BlockExtraHalfSlab;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockExtraSlab extends ItemSlab {
	
	 private final BlockSlab singleSlab, doubleSlab;
	 
    /**
     * Initializes a new instance of the ItemBlockStainedBrickSlab class.
     * @param block the block behind the item.
     * @param slab the half height slab.
     * @param doubleSlab the full height slab.
     * @param stacked whether or not the block is the stacked version.
     */
    public ItemBlockExtraSlab(
        final Block block,
        final BlockExtraHalfSlab singleSlab,
        final BlockExtraDoubleSlab doubleSlab,
        final Boolean stacked) {
        super(block, singleSlab, doubleSlab);
        this.singleSlab = singleSlab;
        this.doubleSlab = doubleSlab;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        BlockPos blockpos = pos;
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == this.singleSlab)
        {
            boolean flag = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

            if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag))
            {
                return true;
            }
        }

        pos = pos.offset(side);
        IBlockState iblockstate1 = worldIn.getBlockState(pos);
        return iblockstate1.getBlock() == this.singleSlab ? true : super.canPlaceBlockOnSide(worldIn, blockpos, side, player, stack);
    }
    
    private boolean tryPlace(EntityPlayer player, ItemStack stack, World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == singleSlab)
        {
        	IBlockState iblockstate1 = doubleSlab.getStateFromMeta(singleSlab.getMetaFromState(iblockstate));
            AxisAlignedBB axisalignedbb = iblockstate1.getSelectedBoundingBox(worldIn, pos);

            if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) &&
            	worldIn.setBlockState(pos, iblockstate1, 11))
            {
                SoundType soundtype = this.doubleSlab.getStepSound();
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                --stack.stackSize;
            }

            return true;
        }

        return false;
    }
    
    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos.offset(facing), facing, stack))
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (iblockstate.getBlock() == this.singleSlab)
            {
                BlockSlab.EnumBlockHalf blockslab$enumblockhalf = (BlockSlab.EnumBlockHalf)iblockstate.getValue(BlockSlab.HALF);

                if ((facing == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || facing == EnumFacing.DOWN && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP))
                {
                	IBlockState iblockstate1 = doubleSlab.getStateFromMeta(singleSlab.getMetaFromState(iblockstate));
                    AxisAlignedBB axisalignedbb = iblockstate1.getSelectedBoundingBox(worldIn, pos);

                    if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)))
                    {
                    	worldIn.setBlockState(pos, iblockstate1, 11);
                        SoundType soundtype = this.doubleSlab.getStepSound();
                        worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        --stack.stackSize;
                    }

                    return EnumActionResult.SUCCESS;
                }
            }

            return this.tryPlace(playerIn, stack, worldIn, pos.offset(facing)) ? EnumActionResult.SUCCESS : super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
}
