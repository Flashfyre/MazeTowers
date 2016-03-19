package com.samuel.mazetowers.blocks;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.tileentities.TileEntityMazeTowerThreshold;
import com.samuel.mazetowers.tileentities.TileEntityRedstoneClock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneClock extends BlockVendorTradeable implements ITileEntityProvider {
	
	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
	private final boolean inverted;
	
	public BlockRedstoneClock(boolean inverted) {
		super(Material.circuits, 1, 5, 9, 10, 100);
		this.inverted = inverted;
		this.setBlockBounds(0.0675F, 0.0F, 0.0675F, 0.9325F, 0.125F, 0.9325F);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return ((Integer)state.getValue(POWER)).intValue();
    }

    public void updatePower(World worldIn, BlockPos pos)  {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        int i = MazeTowers.TextureRedstoneClock.getRedstonePower();

        if (this.inverted)
            i = 15 - i;

        if (((Integer)iblockstate.getValue(POWER)).intValue() != i)
            worldIn.setBlockState(pos, iblockstate.withProperty(POWER, Integer.valueOf(i)), 3);
    }
    
    @Override
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return true;
    }
    
    @Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!this.canBePlacedOn(worldIn, pos.down())) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private static boolean canBePlacedOn(World worldIn,
		BlockPos pos) {
		return World.doesBlockHaveSolidTopSurface(worldIn, pos);
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
    public boolean isOpaqueCube() {
    	return false;
    }
    
    @Override
    public boolean isFullCube() {
    	return false;
    }
	
	@Override
	 /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType() {
        return 2;
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
        return ((Integer)state.getValue(POWER)).intValue();
    }

	@Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { POWER });
    }
}