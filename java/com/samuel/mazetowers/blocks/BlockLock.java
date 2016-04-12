package com.samuel.mazetowers.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.IMetaBlockName;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.init.ModSounds;
import com.samuel.mazetowers.tileentity.TileEntityLock;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

public class BlockLock extends Block implements IMetaBlockName,
	ITileEntityProvider {
	
	public static final PropertyDirection FACING = PropertyDirection
		.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE =
		PropertyEnum.<BlockDoor.EnumHingePosition>create("hinge",
		BlockDoor.EnumHingePosition.class);
	public static final SoundType LOCK = new SoundType(0.2F, 1.5F,
		SoundEvents.block_stone_break, SoundEvents.block_stone_step,
		SoundEvents.block_anvil_place, SoundEvents.block_anvil_hit,
		SoundEvents.block_anvil_fall);
	protected static final AxisAlignedBB AABB_EAST =
		new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);
	protected static final AxisAlignedBB AABB_WEST =
		new AxisAlignedBB(0.875F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	protected static final AxisAlignedBB AABB_SOUTH =
		new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);
	protected static final AxisAlignedBB AABB_NORTH =
		new AxisAlignedBB(0.0F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0F);
	private int[] colors;
	
	public BlockLock() {
		super(Material.iron);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING,
			EnumFacing.NORTH).withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT));
		EnumDyeColor[][] dyeColors = EnumTowerType.getAllBeaconColors();
		colors = new int[dyeColors.length];
		for (int t = 0; t < dyeColors.length; t++) {
			float[] rgbMix = new float[3];
			for (int c = 0; c < dyeColors[t].length; c++) {
        		float[] rgb = EntitySheep.getDyeRgb(dyeColors[t][c]);
        		rgbMix[0] += rgb[0] / dyeColors[t].length;
        		rgbMix[1] += rgb[1] / dyeColors[t].length;
        		rgbMix[2] += rgb[2] / dyeColors[t].length;
			}
			colors[t] = MTUtils.RGBToInt(rgbMix[0], rgbMix[1], rgbMix[2]);
		}
		this.setStepSound(LOCK);
		this.setBlockUnbreakable();
		this.setCreativeTab(MazeTowers.TabExtra);
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
	
	public int[] getColors() {
		return colors;
	}
	
	@Override
	 /**
    * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
    */
	public EnumBlockRenderType getRenderType(IBlockState state) {
       return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side,
		float hitX, float hitY, float hitZ)  {
		if (!worldIn.isRemote) {
    		final boolean holdingKey = heldItem != null &&
    			(ItemStack.areItemStacksEqual(heldItem, new ItemStack(ModItems.key_colored,
    			1, ((TileEntityLock) worldIn.getTileEntity(pos)).getTypeIndex())) ||
    			heldItem.getItem() == ModItems.key_spectrite ||
    			ItemStack.areItemStacksEqual(heldItem, new ItemStack(ModItems.spectrite_key_sword)));
    		if (!holdingKey) {
    			worldIn.playSound(hitX, hitY, hitZ, ModSounds.door_locked,
    				SoundCategory.BLOCKS, 1.0F, 1.0F, true);
    		} else {
    			worldIn.playSound(null, pos, ModSounds.door_unlock,
        			SoundCategory.BLOCKS, 1.0F, 1.0F);
    			if (!MTUtils.getIsMazeTowerPos(worldIn.provider.getDimension(), pos))
    				this.dropBlockAsItem(worldIn, pos, state, 0);
    			worldIn.setBlockToAir(pos);
    			return true;
    		}
		}
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
			state.getValue(FACING).getOpposite())) {
			if (this.checkForDrop(worldIn, pos, state))
				this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private boolean checkForDrop(World worldIn,
		BlockPos pos, IBlockState state) {
		return !MTUtils.getIsMazeTowerPos(worldIn.provider.getDimension(), pos) &&
			this.canPlaceBlockAt(worldIn, pos);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
	    switch (state.getValue(FACING)) {
	        case NORTH:
	            return AABB_NORTH;
	        case SOUTH:
	            return AABB_SOUTH;
	        case WEST:
	            return AABB_WEST;
	        case EAST:
	            return AABB_EAST;
		default:
			return null;
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
        for (int i = 0; i < count; i++) {
            Item item = this.getItemDropped(state, rand, fortune);
            if (item != null)
                ret.add(new ItemStack(item, 1, getDamageValue((World) world, pos)));
        }
        return ret;
    }

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

		switch (state.getValue(FACING)) {
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
	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(FACING).add(HINGE).build();
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
	public String getSpecialName(ItemStack stack) {
		return String.valueOf(stack.getItemDamage());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityLock();
	}
}
