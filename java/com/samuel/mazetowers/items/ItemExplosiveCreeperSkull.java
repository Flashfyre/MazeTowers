package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockExplosiveCreeperSkull;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.tileentity.TileEntityExplosiveCreeperSkull;

public class ItemExplosiveCreeperSkull extends ItemArmor {
	
	public ItemExplosiveCreeperSkull()
    {
		super(MazeTowers.EXPLOSIVE_CREEPER_HEAD, 4, EntityEquipmentSlot.HEAD);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }
	
	@Override
	public void addInformation(ItemStack stack,
		EntityPlayer player, List list, boolean Adva) {
		int lineCount = 0;
		boolean isLastLine = false;
		String curLine;
		while (!isLastLine) {
			isLastLine = (curLine = I18n
				.translateToLocal(("iteminfo.explosive_creeper_skull_item.l" +
				++lineCount))).endsWith("@");
			list.add(!isLastLine ? curLine : curLine
				.substring(0, curLine.length() - 1));
		}
	}

	@Override
    /**
     * Called when a Block is right-clicked with this Item
     *  
     * @param pos The block being right-clicked
     * @param side The side being right-clicked
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn,
    	BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	Block skullBlock = ModBlocks.explosiveCreeperSkull;
    	IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock().isReplaceable(worldIn, pos) && side != EnumFacing.DOWN)
        {
            side = EnumFacing.UP;
            pos = pos.down();
        }
        if (side == EnumFacing.DOWN)
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            Block block = state.getBlock();
            boolean flag = block.isReplaceable(worldIn, pos);

            if (!flag)
            {
                if (!state.getBlock().getMaterial(state).isSolid() &&
                	!worldIn.isSideSolid(pos, side, true))  {
                    return EnumActionResult.FAIL;
                }

                pos = pos.offset(side);
            }

            if (!playerIn.canPlayerEdit(pos, side, stack))
            {
                return EnumActionResult.FAIL;
            }
            else if (!skullBlock.canPlaceBlockAt(worldIn, pos))
            {
                return EnumActionResult.FAIL;
            }
            else
            {
                if (!worldIn.isRemote)
                {
                    if (!skullBlock.canPlaceBlockOnSide(worldIn, pos, side))
                    	return EnumActionResult.FAIL;
                    worldIn.setBlockState(pos, skullBlock.getDefaultState()
                    	.withProperty(BlockSkull.FACING, side), 3);
                    int i = 0;

                    if (side == EnumFacing.UP)
                    {
                        i = MathHelper.floor_double(playerIn.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
                    }

                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntityExplosiveCreeperSkull)
                    {
                        TileEntityExplosiveCreeperSkull TileEntityExplosiveCreeperSkull = (TileEntityExplosiveCreeperSkull)tileentity;
                        TileEntityExplosiveCreeperSkull.setType(4);
                        TileEntityExplosiveCreeperSkull.setSkullRotation(i);
                    }

                    --stack.stackSize;
                }

                return EnumActionResult.SUCCESS;
            }
        }
    }

    @Override
    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return super.getItemStackDisplayName(stack);
    }

    @Override
    /**
     * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
     */
    public boolean updateItemStackNBT(NBTTagCompound nbt) {
    	super.updateItemStackNBT(nbt);

        return false;
    }
    
    @Override
    /**
     * Determines if the specific ItemStack can be placed in the specified armor slot.
     *
     * @param stack The ItemStack
     * @param armorType Armor slot ID: 0: Helmet, 1: Chest, 2: Legs, 3: Boots
     * @param entity The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType,
    	Entity entity) {
    	return armorType == EntityEquipmentSlot.HEAD;
    }
}
