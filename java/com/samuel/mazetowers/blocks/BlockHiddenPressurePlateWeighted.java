package com.samuel.mazetowers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.IVendorTradeable;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.etc.UnlistedPropertyCopiedBlock;

/**
 * Edited from Camouflage Block by TheGreyGhost on 19/04/2015.
 */
public class BlockHiddenPressurePlateWeighted extends BlockPressurePlateWeighted implements IVendorTradeable {

	public static final UnlistedPropertyCopiedBlock COPIEDBLOCK = new UnlistedPropertyCopiedBlock();
	private final int field_150068_a, professionId, minTradeLevel, maxTradeLevel, minTradeChance,
		maxTradeChance, tradeLevelDiff;

	public BlockHiddenPressurePlateWeighted() {
		super(MazeTowers.solidCircuits, 150);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F,
			0.9375F, 0.0625F, 0.9375F);
		this.setTickRandomly(true);
		this.field_150068_a = 150;
		professionId = 1;
		minTradeLevel = 7;
		maxTradeLevel = 9;
		minTradeChance = 15;
		maxTradeChance = 150;
		tradeLevelDiff = maxTradeLevel - minTradeLevel;
	}

	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.SOLID;
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
	public int getRenderType() {
		return 3;
	}

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!this.canBePlacedOn(worldIn, pos.down())) {
			if (!isMTPFallTrap(worldIn, pos))
				this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private static boolean canBePlacedOn(World worldIn,
		BlockPos pos) {
		return World.doesBlockHaveSolidTopSurface(worldIn,
			pos) || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
	}

	@Override
	protected int computeRedstoneStrength(World worldIn,
		BlockPos pos) {
		final boolean isInTower = MTUtils.getIsMazeTowerPos(
			worldIn.provider.getDimensionId(), pos);
		final int i, ix = pos.getX() >> 4 << 4, iz = pos.getZ() >> 4 << 4;
		final AxisAlignedBB towerChunkBounds = isInTower ?
			new AxisAlignedBB(ix, pos.getY() - 1, iz, ix + 15, pos.getY() + 4,
				iz + 15) : null;
		
		i = !isInTower || worldIn.getEntitiesWithinAABB(EntityPlayer.class,
			towerChunkBounds).size() != 0 ? Math.min(worldIn.getEntitiesWithinAABB(
			(/*isInTower && isMTPFallTrap(worldIn, pos) ? EntityLivingBase.class
			:*/ Entity.class), this.getSensitiveAABB(pos)).size(),
			this.field_150068_a) : 0;

		if (i > 0) {
			float f = (float) Math.min(this.field_150068_a,
				i)
				/ (float) this.field_150068_a;
			return MathHelper.ceiling_float_int(f * 15.0F);
		} else
			return 0;
	}

	private static IBlockState getMTPFallTrapPistonState(
		World worldIn, BlockPos pos) {
		BlockPos belowPos = pos.down();
		IBlockState pistonState;
		return ((pistonState = worldIn
			.getBlockState(belowPos)).getBlock() instanceof BlockMemoryPistonBase
			|| (pistonState = worldIn
				.getBlockState(belowPos.east())).getBlock() instanceof BlockMemoryPistonBase
			|| (pistonState = worldIn
				.getBlockState(belowPos.south()))
				.getBlock() instanceof BlockMemoryPistonBase
			|| (pistonState = worldIn
				.getBlockState(belowPos.west())).getBlock() instanceof BlockMemoryPistonBase
			|| (pistonState = worldIn
				.getBlockState(belowPos.north()))
				.getBlock() instanceof BlockMemoryPistonBase ? pistonState
			: null);
	}

	private static boolean isMTPFallTrap(World worldIn,
		BlockPos pos) {
		IBlockState state = getMTPFallTrapPistonState(
			worldIn, pos);
		return state != null;
	}

	@Override
	public void setBlockBoundsBasedOnState(
		IBlockAccess worldIn, BlockPos pos) {
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F,
			(worldIn.getBlockState(pos)
				.getValue(this.POWER) == 0) ? 0.0625F
				: 0.03125F, 0.9375F);
	}
	
	@Override
	public int getVendorProfessionId() {
		return professionId;
	}

	@Override
	public int getVendorTradeChance(int difficulty) {
		if (difficulty >= minTradeLevel)
			return difficulty < maxTradeLevel ? minTradeChance +
				(((maxTradeChance - minTradeChance) / tradeLevelDiff) *
				(difficulty - minTradeLevel)) : maxTradeChance;
		else
			return 0;
	}

	@Override
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[] { POWER };
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { COPIEDBLOCK };
		return new ExtendedBlockState(this,
			listedProperties, unlistedProperties);
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
