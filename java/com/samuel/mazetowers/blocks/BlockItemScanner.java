package com.samuel.mazetowers.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.client.gui.GuiHandlerItemScanner;
import com.samuel.mazetowers.init.ModSounds;
import com.samuel.mazetowers.tileentity.TileEntityItemScanner;

public class BlockItemScanner extends Block implements ITileEntityProvider {

	protected static final AxisAlignedBB AABB_EAST =
		new AxisAlignedBB(0.0F, 0.0625F, 0.125F, 0.25F, 0.9375F, 0.875F);
    protected static final AxisAlignedBB AABB_WEST =
    	new AxisAlignedBB(0.75F, 0.0625F, 0.125F, 1.0F, 0.9375F, 0.875F);
    protected static final AxisAlignedBB AABB_SOUTH =
    	new AxisAlignedBB(0.125F, 0.0625F, 0.0F, 0.875F, 0.9375F, 0.25F);
    protected static final AxisAlignedBB AABB_NORTH =
    	new AxisAlignedBB(0.125F, 0.0625F, 0.75F, 0.875F, 0.9375F, 1.0F);
	public static final PropertyDirection FACING = PropertyDirection
		.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger STATE = PropertyInteger
		.create("state", 0, 3);
	
	public BlockItemScanner(Material material, MapColor mapColor) {
		super(material, mapColor);
		this.setHardness(5.0F);
		this.setTickRandomly(true);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	public BlockItemScanner(Material material) {
		this(material, material.getMaterialMapColor());
	}
	
	public BlockItemScanner() {
		this(Material.iron);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
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
	 * How many world ticks before ticking
	 */
	public int tickRate(World worldIn) {
		return 50;
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

	protected static boolean func_181088_a(
		World p_181088_0_, BlockPos p_181088_1_,
		EnumFacing p_181088_2_) {
		return p_181088_2_.getAxis() != Axis.Y
			&& p_181088_0_.isSideSolid(p_181088_1_
				.offset(p_181088_2_), p_181088_2_
				.getOpposite());
	}

	@Override
	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState onBlockPlaced(World worldIn,
		BlockPos pos, EnumFacing facing, float hitX,
		float hitY, float hitZ, int meta,
		EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING,
			facing).withProperty(STATE, 0);
	}

	@Override
	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place logic
	 */
	public void onBlockPlacedBy(World worldIn,
		BlockPos pos, IBlockState state,
		EntityLivingBase placer, ItemStack stack) {
		((TileEntityItemScanner) worldIn.getTileEntity(pos))
			.setOwnerName(placer.getDisplayName()
				.getUnformattedText());
	}

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void onNeighborBlockChange(World worldIn,
		BlockPos pos, IBlockState state, Block neighborBlock) {
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
	public boolean onBlockActivated(World worldIn,
		BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumHand hand, ItemStack heldItem,
		EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean canScan = state.getValue(STATE) == 0;
		IBlockState scanState = state
			.withProperty(STATE, 1);
		if (canScan) {
			TileEntityItemScanner te;
			if (!(te = (TileEntityItemScanner) worldIn
				.getTileEntity(pos)).getOwnerName().equals(
				playerIn.getDisplayNameString())
				&& !playerIn.capabilities.isCreativeMode) {
				(te = (TileEntityItemScanner) worldIn
					.getTileEntity(pos))
					.setEntityId(playerIn.getEntityId());
				if (te.getKeyStack() != null) {
					worldIn.playSound(pos
						.getX() + 0.5D,
						pos.getY() + 0.5D,
						pos.getZ() + 0.5D,
						SoundEvents.block_metal_pressplate_click_on,
						SoundCategory.BLOCKS, 0.3F, 0.5F, true);
					worldIn.setBlockState(pos, scanState);
					worldIn.markBlockRangeForRenderUpdate(
						pos, pos);
					worldIn.scheduleUpdate(pos, this, this
						.tickRate(worldIn));
				} else if (!worldIn.isRemote)
					setStateBasedOnMatchResult(worldIn,
						pos, state, true);
			} else {
				// ((TileEntityItemScanner)
				// worldIn.getTileEntity(pos)).openInventory(playerIn);
				playerIn
					.openGui(
						MazeTowers.instance,
						GuiHandlerItemScanner.MOD_TILE_ENTITY_GUI,
						worldIn, pos.getX(), pos.getY(),
						pos.getZ());
			}
		}

		return canScan;
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess worldIn,
		BlockPos pos, EnumFacing side) {
		return state.getValue(STATE) == 3 ? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess worldIn,
		BlockPos pos, EnumFacing side) {
		return !(state.getValue(STATE) == 3) ? 0 : (state
			.getValue(FACING) == side ? 15 : 0);
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
	public void updateTick(World worldIn, BlockPos pos,
		IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			int stateValue = state.getValue(STATE);
			if (stateValue == 1) {
				boolean correctItem = ((TileEntityItemScanner) worldIn
					.getTileEntity(pos)).getItemsMatch();
				setStateBasedOnMatchResult(worldIn, pos,
					state, correctItem);
			} else if (stateValue != 0) {
				worldIn.setBlockState(pos, state
					.withProperty(STATE, 0));
				this.notifyNeighbors(worldIn, pos,
					state.getValue(FACING));
				worldIn.playSound(
					pos.getX() + 0.5D,
					pos.getY() + 0.5D,
					pos.getZ() + 0.5D,
					SoundEvents.block_metal_pressplate_click_on,
					SoundCategory.BLOCKS, 0.3F, 0.5F, true);
				worldIn.markBlockRangeForRenderUpdate(pos,
					pos);
			}
		}
	}

	public void setStateBasedOnMatchResult(World worldIn,
		BlockPos pos, IBlockState state, boolean correctItem) {
		worldIn.setBlockState(pos, state.withProperty(
			STATE, correctItem ? 3 : 2));
		this.notifyNeighbors(worldIn, pos,
			state.getValue(FACING));
		if (!worldIn.isRemote)
			worldIn.playSound(null, pos, correctItem ? ModSounds.correct :
				ModSounds.incorrect, SoundCategory.BLOCKS, 0.15F, 1.0F);
		worldIn.markBlockRangeForRenderUpdate(pos, pos);
		worldIn.scheduleUpdate(pos, this, (int) (this
			.tickRate(worldIn) * (correctItem ? 2 : 0.5)));
	}

	protected void notifyNeighbors(World worldIn,
		BlockPos pos, EnumFacing facing) {
		worldIn.notifyNeighborsOfStateChange(pos, this);
		worldIn.notifyNeighborsOfStateChange(pos
			.offset(facing.getOpposite()), this);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos,
		IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof IInventory) {
			InventoryBasic keyStackInv = new InventoryBasic(
				"Key Stack", true, 1);
			keyStackInv.setInventorySlotContents(0,
				((TileEntityItemScanner) tileentity)
					.getKeyStack());
			InventoryHelper.dropInventoryItems(worldIn,
				pos, (IInventory) tileentity);
			InventoryHelper.dropInventoryItems(worldIn,
				pos, keyStackInv);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		TileEntityItemScanner te = new TileEntityItemScanner();
		return te;
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
			enumfacing).withProperty(STATE, meta >> 2);
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

		int stateValue = state.getValue(STATE);

		if (stateValue > 0) {
			i |= stateValue << 2;
		}

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(FACING).add(STATE).build();
	}

	@Override
	/**
	 * Determines if this block is can be destroyed by the specified entities normal behavior.
	 *
	 * @param world The current world
	 * @param pos Block position in world
	 * @return True to allow the ender dragon to destroy this block
	 */
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos,
		Entity entity) {
		super.canEntityDestroy(state, world, pos, entity);
		boolean isPlayerInCreativeMode = (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode);
		boolean isOwner = (((TileEntityItemScanner) world
			.getTileEntity(pos)).getOwnerName()
			.equals(entity.getDisplayName()
				.getUnformattedText()));
		if (!isOwner && !isPlayerInCreativeMode) {
			entity.addChatMessage(new TextComponentString(
				"You may not destroy "
					+ "an Item Scanner that is not yours"));
			return false;
		}
		return true;
	}
}