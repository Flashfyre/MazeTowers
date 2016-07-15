package com.samuel.mazetowers.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.UnlistedPropertyCopiedBlock;

public class BlockHiddenButton extends Block {

	protected static final AxisAlignedBB AABB_DOWN_OFF = new AxisAlignedBB(0.3125D, 0.875D, 0.375D, 0.6875D, 1.0D, 0.625D);
    protected static final AxisAlignedBB AABB_UP_OFF = new AxisAlignedBB(0.3125D, 0.0D, 0.375D, 0.6875D, 0.125D, 0.625D);
    protected static final AxisAlignedBB AABB_NORTH_OFF = new AxisAlignedBB(0.3125D, 0.375D, 0.875D, 0.6875D, 0.625D, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH_OFF = new AxisAlignedBB(0.3125D, 0.375D, 0.0D, 0.6875D, 0.625D, 0.125D);
    protected static final AxisAlignedBB AABB_WEST_OFF = new AxisAlignedBB(0.875D, 0.375D, 0.3125D, 1.0D, 0.625D, 0.6875D);
    protected static final AxisAlignedBB AABB_EAST_OFF = new AxisAlignedBB(0.0D, 0.375D, 0.3125D, 0.125D, 0.625D, 0.6875D);
    protected static final AxisAlignedBB AABB_DOWN_ON = new AxisAlignedBB(0.3125D, 0.9375D, 0.375D, 0.6875D, 1.0D, 0.625D);
    protected static final AxisAlignedBB AABB_UP_ON = new AxisAlignedBB(0.3125D, 0.0D, 0.375D, 0.6875D, 0.0625D, 0.625D);
    protected static final AxisAlignedBB AABB_NORTH_ON = new AxisAlignedBB(0.3125D, 0.375D, 0.9375D, 0.6875D, 0.625D, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH_ON = new AxisAlignedBB(0.3125D, 0.375D, 0.0D, 0.6875D, 0.625D, 0.0625D);
    protected static final AxisAlignedBB AABB_WEST_ON = new AxisAlignedBB(0.9375D, 0.375D, 0.3125D, 1.0D, 0.625D, 0.6875D);
    protected static final AxisAlignedBB AABB_EAST_ON = new AxisAlignedBB(0.0D, 0.375D, 0.3125D, 0.0625D, 0.625D, 0.6875D);
	public static final PropertyDirection FACING = PropertyDirection
		.create("facing");
	public static final PropertyBool POWERED = PropertyBool
		.create("powered");
	public static final UnlistedPropertyCopiedBlock COPIEDBLOCK = new UnlistedPropertyCopiedBlock();

	/*
	 * @SuppressWarnings("unchecked") public static final IUnlistedProperty[]
	 * properties = new IUnlistedProperty[7]; private static int cubeSize = 1;
	 * 
	 * static { for(EnumFacing f : EnumFacing.values()) {
	 * properties[f.ordinal()] =
	 * Properties.toUnlisted(PropertyInteger.create(f.getName(), 0, (1 <<
	 * (cubeSize * cubeSize)) - 1)); } properties[6] = COPIEDBLOCK; }
	 */

	public BlockHiddenButton() {
		super(MazeTowers.solidCircuits);
		IExtendedBlockState state = ((IExtendedBlockState) this.blockState
			.getBaseState()).withProperty(COPIEDBLOCK,
			Blocks.QUARTZ_BLOCK.getDefaultState());
		setCreativeTab(CreativeTabs.REDSTONE);
		setDefaultState(this.blockState.getBaseState()
			.withProperty(FACING, EnumFacing.NORTH)
			.withProperty(POWERED, Boolean.valueOf(false)));
		setTickRandomly(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(FACING).add(POWERED).add(COPIEDBLOCK).build();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state,
		IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState model = (IExtendedBlockState) state;
			IBlockState belowState = getConnectedBlockState(
				world, pos);
			model = model.withProperty(COPIEDBLOCK,
				belowState);
			return model;
		}
		return state;
	}

	@Override
	public IBlockState getActualState(IBlockState state,
		IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		int i;

		switch (state.getValue(FACING)) {
		case EAST:
			i = 2;
			break;
		case WEST:
			i = 3;
			break;
		case SOUTH:
			i = 4;
			break;
		case NORTH:
			i = 5;
			break;
		case DOWN:
			i = 1;
			break;
		default:
			i = 0;
		}

		if (state.getValue(POWERED)
			.booleanValue()) {
			i += 6;
		}

		return i;
	}

	@Override
	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World worldIn) {
		return 20;
	}

	@Override
	/**
	 * Check whether this Block can be placed on the given side
	 */
	public boolean canPlaceBlockOnSide(World worldIn,
		BlockPos pos, EnumFacing side) {
		return func_181088_a(worldIn, pos, side
			.getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn,
		BlockPos pos) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (func_181088_a(worldIn, pos, enumfacing)) {
				return true;
			}
		}

		return false;
	}
	
	protected static boolean func_181088_a(World p_181088_0_, BlockPos p_181088_1_,
		EnumFacing p_181088_2_) {
        BlockPos blockpos = p_181088_1_.offset(p_181088_2_);
        return p_181088_0_.getBlockState(blockpos).isSideSolid(p_181088_0_, blockpos, p_181088_2_.getOpposite());
    }

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to
	 * allow for adjustments to the IBlockstate
	 */
	@Override
	public IBlockState onBlockPlaced(World worldIn,
		BlockPos pos, EnumFacing facing, float hitX,
		float hitY, float hitZ, int meta,
		EntityLivingBase placer) {
		return func_181088_a(worldIn, pos, facing
			.getOpposite()) ? this.getDefaultState()
			.withProperty(FACING, facing).withProperty(
				POWERED, Boolean.valueOf(false)) : this
			.getDefaultState().withProperty(FACING,
				EnumFacing.DOWN).withProperty(POWERED,
				Boolean.valueOf(false));
	}

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void neighborChanged(IBlockState state,
		World worldIn, BlockPos pos, Block neighborBlock) {
		if (this.checkForDrop(worldIn, pos, state)
			&& !func_181088_a(worldIn, pos,
				state.getValue(FACING)
					.getOpposite())) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private boolean checkForDrop(World worldIn,
		BlockPos pos, IBlockState state) {
		if (this.canPlaceBlockAt(worldIn, pos)) {
			return true;
		} else {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			return false;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        EnumFacing enumfacing = state.getValue(FACING);
        boolean flag = state.getValue(POWERED).booleanValue();

        switch (enumfacing)
        {
            case EAST:
                return flag ? AABB_EAST_ON : AABB_EAST_OFF;
            case WEST:
                return flag ? AABB_WEST_ON : AABB_WEST_OFF;
            case SOUTH:
                return flag ? AABB_SOUTH_ON : AABB_SOUTH_OFF;
            case NORTH:
            default:
                return flag ? AABB_NORTH_ON : AABB_NORTH_OFF;
            case UP:
                return flag ? AABB_UP_ON : AABB_UP_OFF;
            case DOWN:
                return flag ? AABB_DOWN_ON : AABB_DOWN_OFF;
        }
    }

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (state.getValue(POWERED).booleanValue())
        {
            return true;
        }
        else
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            //this.func_185615_a(playerIn, worldIn, pos);
            this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            return true;
        }
    }

	@Override
	public void breakBlock(World worldIn, BlockPos pos,
		IBlockState state) {
		if (state.getValue(POWERED)
			.booleanValue()) {
			this.notifyNeighbors(worldIn, pos,
				state.getValue(FACING));
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess worldIn,
		BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED)
			.booleanValue() ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess worldIn,
		BlockPos pos, EnumFacing side) {
		return !state.getValue(POWERED)
			.booleanValue() ? 0
			: (state.getValue(FACING) == side ? 15 : 0);
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
	 * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
	 */
	public void randomTick(World worldIn, BlockPos pos,
		IBlockState state, Random random) {
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(POWERED).booleanValue())
            {
                worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)));
                this.notifyNeighbors(worldIn, pos, state.getValue(FACING));
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
            }
        }
    }

	@Override
	/**
	 * Called When an Entity Collided with the Block
	 */
	public void onEntityCollidedWithBlock(World worldIn,
		BlockPos pos, IBlockState state, Entity entityIn) {
	}

	private void notifyNeighbors(World worldIn,
		BlockPos pos, EnumFacing facing) {
		worldIn.notifyNeighborsOfStateChange(pos, this);
		worldIn.notifyNeighborsOfStateChange(pos
			.offset(facing.getOpposite()), this);
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
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing;

		switch (meta & 7) {
		case 0:
			enumfacing = EnumFacing.DOWN;
			break;
		case 1:
			enumfacing = EnumFacing.EAST;
			break;
		case 2:
			enumfacing = EnumFacing.WEST;
			break;
		case 3:
			enumfacing = EnumFacing.SOUTH;
			break;
		case 4:
			enumfacing = EnumFacing.NORTH;
			break;
		case 5:
		default:
			enumfacing = EnumFacing.UP;
		}

		return this.getDefaultState().withProperty(FACING,
			enumfacing).withProperty(POWERED,
			Boolean.valueOf((meta & 8) > 0));
	}

	private static IBlockState getConnectedBlockState(
		IBlockAccess world, BlockPos blockPos) {
		final IBlockState normal = Blocks.QUARTZ_BLOCK
			.getStateFromMeta(3);
		EnumFacing facing = world.getBlockState(blockPos)
			.getValue(BlockDirectional.FACING);
		IBlockState connState = null;

		if ((connState = world.getBlockState(blockPos
			.offset(facing.getOpposite()))) == Blocks.AIR
			.getDefaultState())
			return normal;
		return connState;
	}
}