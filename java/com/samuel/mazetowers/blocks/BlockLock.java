package com.samuel.mazetowers.blocks;

import java.util.List;
import java.util.Random;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.IMetaBlockName;
import com.samuel.mazetowers.etc.IVendorTradeable;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityLock;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLock extends BlockVendorTradeable implements IMetaBlockName, ITileEntityProvider {
	
	public static final PropertyDirection FACING = PropertyDirection
		.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE =
		PropertyEnum.<BlockDoor.EnumHingePosition>create("hinge",
		BlockDoor.EnumHingePosition.class);

	public static final Block.SoundType soundTypeLock =
		new Block.SoundType("lock", 0.2F, 1.5F)  {
		
		@Override
        /**
         * Get the breaking sound for the Block
         */
        public String getStepSound()
        {
            return "dig.stone";
        }
		
		@Override
        /**
         * Get the breaking sound for the Block
         */
        public String getBreakSound()
        {
            return "dig.stone";
        }
		
		@Override
        public String getPlaceSound()
        {
            return "random.anvil_land";
        }
    };
	private int[] colors;
	
	public BlockLock() {
		super(Material.iron, 3, 500, 100);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING,
			EnumFacing.NORTH).withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT));
		EnumDyeColor[][] dyeColors = EnumTowerType.getAllBeaconColors();
		colors = new int[dyeColors.length];
		for (int t = 0; t < dyeColors.length; t++) {
			float[] rgbMix = new float[3];
			for (int c = 0; c < dyeColors[t].length; c++) {
        		float[] rgb = EntitySheep.func_175513_a(dyeColors[t][c]);
        		rgbMix[0] += rgb[0] / dyeColors[t].length;
        		rgbMix[1] += rgb[1] / dyeColors[t].length;
        		rgbMix[2] += rgb[2] / dyeColors[t].length;
			}
			colors[t] = MTUtils.RGBToInt(rgbMix[0], rgbMix[1], rgbMix[2]);
		}
		this.setStepSound(soundTypeLock);
		this.setBlockUnbreakable();
		this.setCreativeTab(MazeTowers.tabExtra);
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
       return 3;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (!worldIn.isRemote) {
    		final boolean holdingKey = playerIn.getHeldItem() != null &&
    			playerIn.getHeldItem().areItemStacksEqual(playerIn.getHeldItem(),
    			new ItemStack((Item) ModItems.key, 1, ((TileEntityLock) worldIn.getTileEntity(pos))
    			.getTypeIndex()));
    		if (!holdingKey) {
    			worldIn.playSoundAtEntity(playerIn, "mazetowers:door_locked", 1.0F, 1.0F);
    			return false;
    		} else {
    			worldIn.playSoundAtEntity(playerIn, "mazetowers:door_unlock", 1.0F, 1.0F);
    			// Consume key if in a Maze Tower
    			if (MTUtils.getIsMazeTowerPos(worldIn.provider.getDimensionId(), pos))
    				playerIn.setCurrentItemOrArmor(0, null);
    			this.dropBlockAsItem(worldIn, pos, state, 0);
    			worldIn.setBlockToAir(pos);
    			return true;
    		}
		} else
			return false;
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
			if (func_181088_a(worldIn, pos, enumfacing))
				return true;
		}

		return false;
	}

	protected static boolean func_181088_a(
		World p_181088_0_, BlockPos p_181088_1_,
		EnumFacing p_181088_2_) {
		final IBlockState connState =
			p_181088_0_.getBlockState(p_181088_1_.offset(p_181088_2_));
		final Block connBlock = connState.getBlock();
		final EnumFacing doorDir = connBlock instanceof BlockDoor ?
			p_181088_0_.getBlockState(connState.getValue(BlockDoor.HALF) ==
			BlockDoor.EnumDoorHalf.UPPER ? p_181088_1_.offset(p_181088_2_).down() :
			p_181088_1_.offset(p_181088_2_)).getValue(BlockDoor.FACING) : null;
		return p_181088_2_.getAxis() != Axis.Y &&
			doorDir != null && doorDir == p_181088_2_ &&
			connState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER &&
			!connState.getValue(BlockDoor.POWERED) && !connState.getValue(BlockDoor.OPEN);
	}

	@Override
	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState onBlockPlaced(World worldIn,
		BlockPos pos, EnumFacing facing, float hitX,
		float hitY, float hitZ, int meta, EntityLivingBase placer) {
		final IBlockState connState =
			worldIn.getBlockState(pos.offset(facing.getOpposite()));
		return getDefaultState().withProperty(FACING, facing)
			.withProperty(HINGE, connState.getValue(BlockDoor.HINGE));
	}

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!func_181088_a(worldIn, pos,
			((EnumFacing) state.getValue(FACING)).getOpposite())) {
			if (this.checkForDrop(worldIn, pos, state))
				this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private boolean checkForDrop(World worldIn,
		BlockPos pos, IBlockState state) {
		return !MTUtils.getIsMazeTowerPos(worldIn.provider.getDimensionId(), pos) &&
			this.canPlaceBlockAt(worldIn, pos);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(
		IBlockAccess worldIn, BlockPos pos) {
		this.updateBlockBounds(worldIn.getBlockState(pos));
	}

	private void updateBlockBounds(IBlockState state) {
		EnumFacing enumfacing = (EnumFacing) state
			.getValue(FACING);
		float f = 0.125F;

		switch (enumfacing) {
		case EAST:
			this.setBlockBounds(0.0F, 0.0F, 0.0F, f,
				1.0F, 1.0F);
			break;
		case WEST:
			this.setBlockBounds(1.0F - f, 0.0F, 0.0F,
				1.0F, 1.0F, 1.0F);
			break;
		case SOUTH:
			this.setBlockBounds(0.0F, 0.0F, 0.0F,
				1.0F, 1.0F, f);
			break;
		default:
			this.setBlockBounds(0.0F, 0.0F, 1.0F - f,
				1.0F, 1.0F, 1.0F);
		}
	}
	
	@Override
	/**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param pos Block position in world
     * @param state Current state
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        Random rand = world instanceof World ? ((World)world).rand : RANDOM;

        int count = quantityDropped(state, fortune, rand);
        for(int i = 0; i < count; i++)
        {
            Item item = this.getItemDropped(state, rand, fortune);
            if (item != null)
            {
                ret.add(new ItemStack(item, 1, getDamageValue((World) world, pos)));
            }
        }
        return ret;
    }
	
	@Override
	public int getDamageValue(World worldIn, BlockPos pos) {
        return ((TileEntityLock) worldIn.getTileEntity(pos)).getTypeIndex();
    }
	
	@Override
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing;

		switch (meta & 5) {
		case 0:
			enumfacing = EnumFacing.EAST;
			break;
		case 1:
			enumfacing = EnumFacing.WEST;
			break;
		case 2:
			enumfacing = EnumFacing.SOUTH;
			break;
		default:
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING,
			enumfacing).withProperty(HINGE, meta >> 2 == 0 ?
			BlockDoor.EnumHingePosition.LEFT :
			BlockDoor.EnumHingePosition.RIGHT);
	}

	@Override
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		int i;

		switch ((EnumFacing) state.getValue(FACING)) {
		case EAST:
			i = 0;
			break;
		case WEST:
			i = 1;
			break;
		case SOUTH:
			i = 2;
			break;
		default:
			i = 3;
		}
		
		int hingeValue = state.getValue(HINGE) == BlockDoor.EnumHingePosition.LEFT ?
			0 : 1;

		if (hingeValue == 1)
			i |= 4;

		return i;
	}

	@Override
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[] { FACING, HINGE };
		return new BlockState(this, listedProperties);
	}
	
	/**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
    	EnumTowerType[] types = EnumTowerType.values();

        for (int j = 0; j < types.length; ++j)
        {
            EnumTowerType type = types[j];
            list.add(new ItemStack(itemIn, 1, type.ordinal()));
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getRenderColor(IBlockState state)
    {
		return colors[14];
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
		int typeIndex = 14;
		if (worldIn.getTileEntity(pos) != null) {
    		TileEntityLock te = (TileEntityLock) worldIn.getTileEntity(pos);
    		typeIndex = te.getTypeIndex();
		}
        return colors[typeIndex];
    }

	@Override
	public String getSpecialName(ItemStack stack) {
		return String.valueOf(stack.getItemDamage());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityLock();
	}
}
