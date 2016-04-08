package com.samuel.mazetowers.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.samuel.mazetowers.tileentity.TileEntityMemoryPiston;

public class BlockMemoryPistonMoving extends BlockContainer {

	public static final PropertyDirection FACING = BlockMemoryPistonExtension.FACING;

	public BlockMemoryPistonMoving(String unlocalizedName) {
		super(Material.piston);
		this.setDefaultState(this.blockState.getBaseState()
			.withProperty(FACING, EnumFacing.NORTH));
		this.setHardness(-1.0F);
		setUnlocalizedName(unlocalizedName);
	}

	@Override
	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return null;
	}

	public static TileEntity newTileEntity(
		IBlockState state, EnumFacing facing,
		boolean extending, boolean renderHead) {
		return new TileEntityMemoryPiston(state, facing,
			extending, renderHead);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos,
		IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityMemoryPiston) {
			((TileEntityMemoryPiston) tileentity)
				.clearPistonTileEntity();
		} else {
			super.breakBlock(worldIn, pos, state);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn,
		BlockPos pos) {
		return false;
	}

	@Override
	/**
	 * Check whether this Block can be placed on the given side
	 */
	public boolean canPlaceBlockOnSide(World worldIn,
		BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	/**
	 * Called when a player destroys this Block
	 */
	public void onBlockDestroyedByPlayer(World worldIn,
		BlockPos pos, IBlockState state) {
		BlockPos blockpos = pos.offset(state
			.getValue(FACING).getOpposite());
		IBlockState iblockstate = worldIn
			.getBlockState(blockpos);

		if (iblockstate.getBlock() instanceof BlockMemoryPistonBase
			&& iblockstate
				.getValue(BlockMemoryPistonBase.EXTENDED)
				.booleanValue()) {
			worldIn.setBlockToAir(blockpos);
		}
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
	public boolean onBlockActivated(World worldIn,
		BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumHand hand, ItemStack stack,
		EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote
			&& worldIn.getTileEntity(pos) == null) {
			worldIn.setBlockToAir(pos);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	/**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        return null;
    }

	@Override
	/**
	 * Get the Item that this Block should drop when harvested.
	 *  
	 * @param fortune the level of the Fortune enchantment on the player's tool
	 */
	public Item getItemDropped(IBlockState state,
		Random rand, int fortune) {
		return null;
	}

	@Override
	/**
	 * Spawns this Block's drops into the World as EntityItems.
	 *  
	 * @param chance The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
	 * @param fortune The player's fortune level
	 */
	public void dropBlockAsItemWithChance(World worldIn,
		BlockPos pos, IBlockState state, float chance,
		int fortune) {
		super.dropBlockAsItemWithChance(worldIn, pos,
			state, chance, fortune);
	}

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!worldIn.isRemote) {
			worldIn.getTileEntity(pos);
		}
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntityMemoryPiston tileentitypiston = this.func_185589_c(worldIn, pos);
        return tileentitypiston == null ? null : tileentitypiston.func_184321_a(worldIn, pos);
    }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TileEntityMemoryPiston tileentitypiston = this.func_185589_c(source, pos);
        return tileentitypiston != null ? tileentitypiston.func_184321_a(source, pos) : FULL_BLOCK_AABB;
    }
	
	private TileEntityMemoryPiston func_185589_c(IBlockAccess p_185589_1_, BlockPos p_185589_2_)
    {
        TileEntity tileentity = p_185589_1_.getTileEntity(p_185589_2_);
        return tileentity instanceof TileEntityMemoryPiston ?
        	(TileEntityMemoryPiston)tileentity : null;
    }

	private static TileEntityMemoryPiston getTileEntity(
		IBlockAccess worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity instanceof TileEntityMemoryPiston ?
			(TileEntityMemoryPiston) tileentity : null;
	}

	@Override
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING,
			BlockMemoryPistonExtension.getFacing(meta));
	}

	@Override
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i
			| state.getValue(FACING)
				.getIndex();

		return i;
	}
	
	@Override
	/**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

	@Override
    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this,
			new IProperty[] { FACING });
	}

	@Override
	public java.util.List<net.minecraft.item.ItemStack> getDrops(
		IBlockAccess world, BlockPos pos,
		IBlockState state, int fortune) {
		TileEntityMemoryPiston tileentitypiston = BlockMemoryPistonMoving
			.getTileEntity(world, pos);
		if (tileentitypiston != null) {
			IBlockState pushed = tileentitypiston
				.getPistonState();
			return pushed.getBlock().getDrops(world, pos,
				pushed, fortune);
		}
		return new java.util.ArrayList();
	}
}
