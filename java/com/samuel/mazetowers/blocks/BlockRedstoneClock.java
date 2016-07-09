package com.samuel.mazetowers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.samuel.mazetowers.items.ItemBlockRedstoneClock;
import com.samuel.mazetowers.tileentity.TileEntityRedstoneClock;

public class BlockRedstoneClock extends Block implements ITileEntityProvider {
	
	public static AxisAlignedBB AABB =
		new AxisAlignedBB(0.0675F, 0.0F, 0.0675F, 0.9325F, 0.125F, 0.9325F);
	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
	private final boolean inverted;
	
	public BlockRedstoneClock(boolean inverted) {
		super(Material.CIRCUITS);
		this.inverted = inverted;
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}
	
	@Override
	public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return state.getValue(POWER).intValue();
    }
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    public void updatePower(World worldIn, BlockPos pos)  {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        int i = ItemBlockRedstoneClock.getRedstonePower();

        if (this.inverted)
            i = 15 - i;

        if (iblockstate.getValue(POWER).intValue() != i)
            worldIn.setBlockState(pos, iblockstate.withProperty(POWER, Integer.valueOf(i)), 3);
    }
    
    @Override
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower(IBlockState state) {
        return true;
    }
    
    @Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void neighborChanged(IBlockState state,
		World worldIn, BlockPos pos, Block neighborBlock) {
		if (!this.canBePlacedOn(worldIn, pos.down())) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

    private boolean canBePlacedOn(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).isFullyOpaque();
    }
    
    public boolean getInverted() {
		return inverted;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityRedstoneClock();
	}

    @Override
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state) {
    	return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
    	return false;
    }
	
	@Override
	 /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
	
	@Override
	/**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWER, Integer.valueOf(meta));
    }

	@Override
    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER).intValue();
    }

	@Override
    protected BlockStateContainer createBlockState() {
        return (new BlockStateContainer.Builder(this)).add(POWER).build();
    }
}