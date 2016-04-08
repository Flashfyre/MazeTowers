package com.samuel.mazetowers.blocks;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.etc.UnlistedPropertyCopiedBlock;

/**
 * Edited from Camouflage Block by TheGreyGhost on 19/04/2015.
 */
public class BlockHiddenPressurePlateWeighted extends BlockPressurePlateWeighted {

	public static final UnlistedPropertyCopiedBlock COPIEDBLOCK = new UnlistedPropertyCopiedBlock();
	private final int field_150068_a;

	public BlockHiddenPressurePlateWeighted() {
		super(MazeTowers.solidCircuits, 150);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setTickRandomly(true);
		this.field_150068_a = 150;
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
	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!this.canBePlacedOn(worldIn, pos.down())) {
			if (!MTUtils.getIsMazeTowerPos(worldIn.provider.getDimension(), pos))
				this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private boolean canBePlacedOn(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).isFullyOpaque() ||
        	worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
    }
	
	@Override
	protected int computeRedstoneStrength(World worldIn, BlockPos pos)
    {
		final boolean isInTower = MTUtils.getIsMazeTowerPos(
			worldIn.provider.getDimension(), pos);
		final int i, ix = pos.getX() >> 4 << 4, iz = pos.getZ() >> 4 << 4;
		final AxisAlignedBB towerChunkBounds = isInTower ?
			new AxisAlignedBB(ix, pos.getY() - 1, iz, ix + 15, pos.getY() + 4, iz + 15) : null;
			if (isInTower) {
				Iterator iterator = worldIn.getEntitiesWithinAABB(EntityPlayer.class,
					towerChunkBounds).iterator();
				boolean foundNonSpectator = false;
				while (!foundNonSpectator && iterator.hasNext()) {
					if (!((EntityPlayer) iterator.next()).isSpectator())
						foundNonSpectator = true;
				}
				if (!foundNonSpectator)
					return 0;
			}
			
			i = Math.min(worldIn.getEntitiesWithinAABB(Entity.class,
				PRESSURE_AABB.offset(pos)).size(), this.field_150068_a);

        if (i > 0) {
            float f = (float)Math.min(this.field_150068_a, i) / (float)this.field_150068_a;
            return MathHelper.ceiling_float_int(f * 15.0F);
        } else
            return 0;
    }

	@Override
	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(POWER).add(COPIEDBLOCK).build();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state,
		IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState retval = (IExtendedBlockState) state;
			IBlockState belowState = getBelowState(world,
				pos);
			retval = retval.withProperty(COPIEDBLOCK,
				belowState);
			return retval;
		}
		return state;
	}

	@Override
	public IBlockState getActualState(IBlockState state,
		IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	private static IBlockState getBelowState(IBlockAccess world,
		BlockPos blockPos) {
		final IBlockState normal = Blocks.quartz_block
			.getDefaultState();
		IBlockState belowState = null;

		if (blockPos.getY() == 0
			|| (belowState = world.getBlockState(blockPos
				.down())) == Blocks.air.getDefaultState())
			return normal;
		return belowState;
	}
}
