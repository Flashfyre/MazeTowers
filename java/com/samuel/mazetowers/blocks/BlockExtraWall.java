package com.samuel.mazetowers.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockExtraWall extends Block {

	public static final PropertyBool UP = PropertyBool
		.create("up"),
		NORTH = PropertyBool.create("north"),
		EAST = PropertyBool.create("east"),
		SOUTH = PropertyBool.create("south"),
		WEST = PropertyBool.create("west");
	private boolean noDrop;

	public BlockExtraWall(Block modelBlock) {
		super(modelBlock.getMaterial());
		this.setDefaultState(this.blockState.getBaseState()
			.withProperty(UP, Boolean.valueOf(false))
			.withProperty(NORTH, Boolean.valueOf(false))
			.withProperty(EAST, Boolean.valueOf(false))
			.withProperty(SOUTH, Boolean.valueOf(false))
			.withProperty(WEST, Boolean.valueOf(false)));
		this.setHardness(modelBlock.getBlockHardness(null,
			null));
		this.setResistance(modelBlock
			.getExplosionResistance(null, null, null, null) / 3.0F);
		this.setStepSound(modelBlock.stepSound);
		this.setCreativeTab(CreativeTabs.tabBlock);
		if (modelBlock == Blocks.packed_ice) {
			this.slipperiness = 0.98F;
			noDrop = true;
		} else
			noDrop = false;
	}

	@Override
	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
		return !noDrop ? super.quantityDropped(random) : 0;
	}

	@Override
	/**
	 * Gets the localized name of this block. Used for the statistics page.
	 */
	public String getLocalizedName() {
		return StatCollector.translateToLocal(this
			.getUnlocalizedName()
			+ "."
			+ BlockWall.EnumType.NORMAL
				.getUnlocalizedName() + ".name");
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn,
		BlockPos pos) {
		return false;
	}

	@Override
	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(
		IBlockAccess worldIn, BlockPos pos) {
		boolean flag = this.canConnectTo(worldIn, pos
			.north());
		boolean flag1 = this.canConnectTo(worldIn, pos
			.south());
		boolean flag2 = this.canConnectTo(worldIn, pos
			.west());
		boolean flag3 = this.canConnectTo(worldIn, pos
			.east());
		float f = 0.25F;
		float f1 = 0.75F;
		float f2 = 0.25F;
		float f3 = 0.75F;
		float f4 = 1.0F;

		if (flag) {
			f2 = 0.0F;
		}

		if (flag1) {
			f3 = 1.0F;
		}

		if (flag2) {
			f = 0.0F;
		}

		if (flag3) {
			f1 = 1.0F;
		}

		if (flag && flag1 && !flag2 && !flag3) {
			f4 = 0.8125F;
			f = 0.3125F;
			f1 = 0.6875F;
		} else if (!flag && !flag1 && flag2 && flag3) {
			f4 = 0.8125F;
			f2 = 0.3125F;
			f3 = 0.6875F;
		}

		this.setBlockBounds(f, 0.0F, f2, f1, f4, f3);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(
		World worldIn, BlockPos pos, IBlockState state) {
		this.setBlockBoundsBasedOnState(worldIn, pos);
		this.maxY = 1.5D;
		return super.getCollisionBoundingBox(worldIn, pos,
			state);
	}

	public boolean canConnectTo(IBlockAccess worldIn,
		BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		return block == Blocks.barrier ? false
			: (block != this
				&& !(block instanceof BlockFenceGate) ? (block
				.getMaterial().isOpaque()
				&& block.isFullCube() ? block.getMaterial() != Material.gourd
				: false)
				: true);
	}

	// @Override
	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	/*
	 * @SideOnly(Side.CLIENT) public void getSubBlocks(Item itemIn, CreativeTabs
	 * tab, List<ItemStack> list) { for (BlockExtraWall.EnumType enumtype :
	 * BlockExtraWall.EnumType.values()) { list.add(new ItemStack(itemIn, 1,
	 * enumtype.getMetadata())); } }
	 */

	@Override
	/**
	 * Get the damage value that this Block should drop
	 */
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	@Override
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	public IBlockState getActualState(IBlockState state,
		IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(UP,
			Boolean.valueOf(!worldIn.isAirBlock(pos.up())))
			.withProperty(
				NORTH,
				Boolean.valueOf(this.canConnectTo(worldIn,
					pos.north()))).withProperty(
				EAST,
				Boolean.valueOf(this.canConnectTo(worldIn,
					pos.east()))).withProperty(
				SOUTH,
				Boolean.valueOf(this.canConnectTo(worldIn,
					pos.south()))).withProperty(
				WEST,
				Boolean.valueOf(this.canConnectTo(worldIn,
					pos.west())));
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { UP,
			NORTH, EAST, WEST, SOUTH });
	}

	public static enum EnumType implements
		IStringSerializable {
		PRISMARINE_BRICK(0, "prismarine_brick",
			"prismarine_brick"), QUARTZ(1, "quartz",
			"quartz"), END_STONE(2, "end_stone",
			"end_stone"), OBSIDIAN(3, "obsidian",
			"obsidian"), BEDROCK(4, "bedrock", "bedrock");

		private static final BlockExtraWall.EnumType[] META_LOOKUP = new BlockExtraWall.EnumType[values().length];
		private final int meta;
		private final String name;
		private String unlocalizedName;

		private EnumType(int meta, String name,
			String unlocalizedName) {
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
		}

		public int getMetadata() {
			return this.meta;
		}

		public String toString() {
			return this.name;
		}

		public static BlockExtraWall.EnumType byMetadata(
			int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		public String getName() {
			return this.name;
		}

		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		static {
			for (BlockExtraWall.EnumType enumtype : values()) {
				META_LOOKUP[enumtype.getMetadata()] = enumtype;
			}
		}
	}

}
