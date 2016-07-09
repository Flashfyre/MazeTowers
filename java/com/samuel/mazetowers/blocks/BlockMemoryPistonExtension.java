package com.samuel.mazetowers.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.init.ModBlocks;

public class BlockMemoryPistonExtension extends BlockDirectional {

	
	public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE =
		PropertyEnum.<BlockPistonExtension.EnumPistonType>create("type",
		BlockPistonExtension.EnumPistonType.class);
    public static final PropertyBool SHORT = PropertyBool.create("short");
    protected static final AxisAlignedBB PISTON_EXTENSION_EAST_AABB = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
    protected static final AxisAlignedBB PISTON_EXTENSION_UP_AABB = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_DOWN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    protected static final AxisAlignedBB field_185636_C = new AxisAlignedBB(0.375D, -0.25D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB field_185638_D = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.25D, 0.625D);
    protected static final AxisAlignedBB field_185640_E = new AxisAlignedBB(0.375D, 0.375D, -0.25D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB field_185642_F = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.25D);
    protected static final AxisAlignedBB field_185644_G = new AxisAlignedBB(-0.25D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB field_185645_I = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.25D, 0.625D, 0.625D);


	public BlockMemoryPistonExtension(String unlocalizedName) {
		super(Material.PISTON);
		this.setDefaultState(this.blockState.getBaseState()
			.withProperty(FACING, EnumFacing.NORTH)
			.withProperty(SHORT, Boolean.valueOf(false)));
		this.setSoundType(SoundType.STONE);
		this.setHardness(0.5F);
		setUnlocalizedName(unlocalizedName);
	}

	@Override
	public void onBlockHarvested(World worldIn,
		BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			EnumFacing enumfacing = state
				.getValue(FACING);

			if (enumfacing != null) {
				BlockPos blockpos1 = pos.offset(enumfacing
					.getOpposite());
				Block block = worldIn.getBlockState(
					blockpos1).getBlock();

				if (block instanceof BlockMemoryPistonBase) {
					worldIn.setBlockToAir(blockpos1);
				}
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos,
		IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		EnumFacing enumfacing = state
			.getValue(FACING).getOpposite();
		pos = pos.offset(enumfacing);
		IBlockState iblockstate1 = worldIn
			.getBlockState(pos);

		if (iblockstate1.getBlock() instanceof BlockMemoryPistonBase
			&& iblockstate1
				.getValue(BlockMemoryPistonBase.EXTENDED)
				.booleanValue()) {
			iblockstate1.getBlock().dropBlockAsItem(
				worldIn, pos, iblockstate1, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
	    switch (state.getValue(FACING)) {
	        case DOWN:
	        default:
	            return PISTON_EXTENSION_DOWN_AABB;
	        case UP:
	            return PISTON_EXTENSION_UP_AABB;
	        case NORTH:
	            return PISTON_EXTENSION_NORTH_AABB;
	        case SOUTH:
	            return PISTON_EXTENSION_SOUTH_AABB;
	        case WEST:
	            return PISTON_EXTENSION_WEST_AABB;
	        case EAST:
	            return PISTON_EXTENSION_EAST_AABB;
	    }
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos,
		AxisAlignedBB p_185477_4_, List<AxisAlignedBB> p_185477_5_, Entity p_185477_6_) {
        addCollisionBoxToList(pos, p_185477_4_, p_185477_5_, state.getBoundingBox(worldIn, pos));
        addCollisionBoxToList(pos, p_185477_4_, p_185477_5_, this.func_185633_i(state));
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
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
		return 0;
	}
	
	private AxisAlignedBB func_185633_i(IBlockState p_185633_1_)
    {
        /*switch ((EnumFacing)p_185633_1_.getValue(FACING))
        {
            case DOWN:
            default:
                return field_185638_D;
            case UP:
                return field_185636_C;
            case NORTH:
                return field_185642_F;
            case SOUTH:
                return field_185640_E;
            case WEST:
                return field_185645_I;
            case EAST:
                return field_185644_G;
        }*/
		return new AxisAlignedBB(
			0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
    }
	
	@Override
	/**
     * Checks if an IBlockState represents a block that is opaque and a full cube.
     *  
     * @param state The block state to check.
     */
    public boolean isFullyOpaque(IBlockState state)
    {
        return state.getValue(FACING) == EnumFacing.UP;
    }

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void neighborChanged(IBlockState state,
		World worldIn, BlockPos pos, Block neighborBlock) {
		EnumFacing enumfacing = state
			.getValue(FACING);
		BlockPos blockpos1 = pos.offset(enumfacing
			.getOpposite());
		IBlockState iblockstate1 = worldIn
			.getBlockState(blockpos1);

		if (!(iblockstate1.getBlock() instanceof BlockMemoryPistonBase)) {
			worldIn.setBlockToAir(pos);
		} else {
			iblockstate1.getBlock().neighborChanged(
				iblockstate1, worldIn, blockpos1, neighborBlock);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

	public static EnumFacing getFacing(int meta) {
		int j = meta & 7;
		return j > 5 ? null : EnumFacing.getFront(j);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModBlocks.memoryPiston);
    }

	@Override
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING,
			getFacing(meta));
	}

	@Override
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0
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
		return (new BlockStateContainer.Builder(this)).add(FACING).add(SHORT).build();
	}
}