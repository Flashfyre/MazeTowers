package com.samuel.mazetowers.blocks;

import java.util.List;
import java.util.Random;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMemoryPistonExtension extends Block {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool SHORT = PropertyBool.create("short");
	
	public BlockMemoryPistonExtension(String unlocalizedName) {
		super(Material.piston);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
			.withProperty(SHORT, Boolean.valueOf(false)));
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
        setUnlocalizedName(unlocalizedName);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

            if (enumfacing != null)
            {
                BlockPos blockpos1 = pos.offset(enumfacing.getOpposite());
                Block block = worldIn.getBlockState(blockpos1).getBlock();

                if (block instanceof BlockMemoryPistonBase)
                {
                    worldIn.setBlockToAir(blockpos1);
                }
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

	@Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).getOpposite();
        pos = pos.offset(enumfacing);
        IBlockState iblockstate1 = worldIn.getBlockState(pos);

        if (iblockstate1.getBlock() instanceof BlockMemoryPistonBase &&
        	((Boolean)iblockstate1.getValue(BlockMemoryPistonBase.EXTENDED)).booleanValue())
        {
            iblockstate1.getBlock().dropBlockAsItem(worldIn, pos, iblockstate1, 0);
            worldIn.setBlockToAir(pos);
        }
    }
	
	@Override
	public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
    public boolean isFullCube()
    {
        return false;
    }

	@Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return false;
    }

	@Override
    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return false;
    }

	@Override
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }
	
	@Override
    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     *  
     * @param collidingEntity the Entity colliding with this Block
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
    {
        this.applyHeadBounds(state);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.applyCoreBounds(state);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    private void applyCoreBounds(IBlockState state)
    {
        float f = 0.25F;
        float f1 = 0.375F;
        float f2 = 0.625F;
        float f3 = 0.25F;
        float f4 = 0.75F;

        switch ((EnumFacing)state.getValue(FACING))
        {
            case DOWN:
                this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                break;
            case UP:
                this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                break;
            case NORTH:
                this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                break;
            case SOUTH:
                this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                break;
            case WEST:
                this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                break;
            case EAST:
                this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        this.applyHeadBounds(worldIn.getBlockState(pos));
    }

    public void applyHeadBounds(IBlockState state)
    {
        /*float f = 0.25F;
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

        if (enumfacing != null)
        {
            switch (enumfacing)
            {
                case DOWN:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                    break;
                case UP:
                    this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case NORTH:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                    break;
                case SOUTH:
                    this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                    break;
                case WEST:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                    break;
                case EAST:
                    this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }*/
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
    }
	
	@Override
	/**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos1 = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

        if (!(iblockstate1.getBlock() instanceof BlockMemoryPistonBase))
        {
            worldIn.setBlockToAir(pos);
        }
        else
        {
            iblockstate1.getBlock().onNeighborBlockChange(worldIn, blockpos1, iblockstate1, neighborBlock);
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }
	
	public static EnumFacing getFacing(int meta)
    {
        int j = meta & 7;
        return j > 5 ? null : EnumFacing.getFront(j);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos)
    {
        return MazeTowers.BlockMemoryPistonOff.getItem(worldIn, pos);
    }
	
	@Override
	/**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta));
    }

	@Override
    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        byte b0 = 0;
        int i = b0 | ((EnumFacing)state.getValue(FACING)).getIndex();

        return i;
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, SHORT});
    }
}