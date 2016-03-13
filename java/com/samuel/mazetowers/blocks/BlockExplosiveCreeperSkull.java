package com.samuel.mazetowers.blocks;

import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.tileentities.TileEntityExplosiveCreeperSkull;

import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockExplosiveCreeperSkull extends BlockSkull {
	
	@Override
	/**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityExplosiveCreeperSkull();
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos)
    {
        return ModItems.explosive_creeper_skull;
    }
	
	@Override
	public int getDamageValue(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityExplosiveCreeperSkull ?
        	((TileEntityExplosiveCreeperSkull)tileentity).getSkullType() :
        	super.getDamageValue(worldIn, pos);
    }
	
	@Override
	/**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
    }
	
	@Override
	public java.util.List<ItemStack> getDrops(IBlockAccess worldIn, BlockPos pos,
		 IBlockState state, int fortune) {
        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        {
            if (!((Boolean)state.getValue(NODROP)).booleanValue())
            {
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity instanceof TileEntityExplosiveCreeperSkull) {
                    TileEntityExplosiveCreeperSkull tileentityskull =
                    	(TileEntityExplosiveCreeperSkull)tileentity;
                    ItemStack itemstack = new ItemStack(ModItems.explosive_creeper_skull,
                    	1, tileentityskull.getSkullType());

                    if (tileentityskull.getSkullType() == 3 && tileentityskull
                    	.getPlayerProfile() != null)
                    {
                        itemstack.setTagCompound(new NBTTagCompound());
                        NBTTagCompound nbttagcompound = new NBTTagCompound();
                        NBTUtil.writeGameProfile(nbttagcompound,
                        	tileentityskull.getPlayerProfile());
                        itemstack.getTagCompound().setTag("SkullOwner", nbttagcompound);
                    }

                    ret.add(itemstack);
                }
            }
        }
        return ret;
    }
}
