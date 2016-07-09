package com.samuel.mazetowers.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPiston;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPistonMemory;

public class BlockMemoryPistonBase extends BlockDirectional implements ITileEntityProvider {

	public static final PropertyDirection FACING = PropertyDirection
		.create("facing");
	public static final PropertyBool EXTENDED = PropertyBool
		.create("extended");
	protected static final AxisAlignedBB PISTON_BASE_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_WEST_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D);
    protected static final AxisAlignedBB PISTON_BASE_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_UP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_DOWN_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.0D, 1.0D, 1.0D, 1.0D);

	public BlockMemoryPistonBase() {
		super(Material.PISTON);
		this.setDefaultState(this.blockState.getBaseState()
			.withProperty(FACING, EnumFacing.NORTH)
			.withProperty(EXTENDED, Boolean.valueOf(false)));
		this.setSoundType(SoundType.STONE);
		this.setHardness(0.5F);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            switch ((EnumFacing)state.getValue(FACING))
            {
                case DOWN:
                    return PISTON_BASE_DOWN_AABB;
                case UP:
                default:
                    return PISTON_BASE_UP_AABB;
                case NORTH:
                    return PISTON_BASE_NORTH_AABB;
                case SOUTH:
                    return PISTON_BASE_SOUTH_AABB;
                case WEST:
                    return PISTON_BASE_WEST_AABB;
                case EAST:
                    return PISTON_BASE_EAST_AABB;
            }
        }
        else
        {
            return FULL_BLOCK_AABB;
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
	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place logic
	 */
	public void onBlockPlacedBy(World worldIn,
		BlockPos pos, IBlockState state,
		EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(
			FACING, getFacingFromEntity(worldIn, pos,
				placer)), 2);

		if (!worldIn.isRemote) {
			this.checkForMove(worldIn, pos, state);
		}
	}

	@Override
	/**
	 * Called when a neighboring block changes.
	 */
	public void neighborChanged(IBlockState state,
		World worldIn, BlockPos pos, Block neighborBlock) {
		if (!worldIn.isRemote) {
			this.checkForMove(worldIn, pos, state);
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos,
		IBlockState state) {
		if (!worldIn.isRemote
			&& worldIn.getTileEntity(pos) == null) {
			this.checkForMove(worldIn, pos, state);
		}
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
			getFacingFromEntity(worldIn, pos, placer))
			.withProperty(EXTENDED, Boolean.valueOf(false));
	}

	private void checkForMove(World worldIn, BlockPos pos,
		IBlockState state) {
		EnumFacing enumfacing = state
			.getValue(FACING);
		boolean flag = this.shouldBeExtended(worldIn, pos,
			enumfacing);

		int pushCount = 0;
		TileEntity te;

		if ((te = worldIn.getTileEntity(pos)).getTileData()
			.hasKey("pushCount"))
			pushCount = te.getTileData().getInteger(
				"pushCount");

		if (flag
			&& !state.getValue(EXTENDED)
				.booleanValue()) {
			if ((new BlockMemoryPistonStructureHelper(
				worldIn, pos, enumfacing, true))
				.canMove(pushCount)) {
				worldIn.addBlockEvent(pos, this, 0,
					enumfacing.getIndex());
			}
		} else if (!flag
			&& state.getValue(EXTENDED)
				.booleanValue()) {
			worldIn.setBlockState(pos, state.withProperty(
				EXTENDED, Boolean.valueOf(false)), 2);
			worldIn.getTileEntity(pos).getTileData()
				.setInteger("pushCount", pushCount);
			worldIn.addBlockEvent(pos, this, 1, enumfacing
				.getIndex());
		}
	}

	protected boolean shouldBeExtended(World worldIn,
		BlockPos pos, EnumFacing facing) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (enumfacing != facing
				&& worldIn.isSidePowered(pos
					.offset(enumfacing), enumfacing)) {
				return true;
			}
		}

		if (worldIn.isSidePowered(pos, EnumFacing.DOWN)) {
			return true;
		} else {
			BlockPos blockpos = pos.up();

			for (EnumFacing enumfacing1 : EnumFacing
				.values()) {
				if (enumfacing1 != EnumFacing.DOWN
					&& worldIn.isSidePowered(blockpos
						.offset(enumfacing1), enumfacing1)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	/**
     * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
     * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
     * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
     * changes.
     *  
     * @param state The block state retrieved from the block position prior to this method being invoked
     * @param pos The position of the block event. Can be used to retrieve tile entities.
     */
	public boolean eventReceived(IBlockState state,
		World worldIn, BlockPos pos, int eventID,
		int eventParam) {
		EnumFacing enumfacing;

		try {
			enumfacing = state.getValue(FACING);
		} catch (IllegalArgumentException e) {
			return false;
		}

		if (!worldIn.isRemote) {
			boolean flag = this.shouldBeExtended(worldIn,
				pos, enumfacing);

			if (flag && eventID == 1) {
				worldIn.setBlockState(pos, state
					.withProperty(EXTENDED, Boolean
						.valueOf(true)), 2);
				return false;
			}

			if (!flag && eventID == 0) {
				return false;
			}
		}

		if (eventID == 0) {
			if (!this
				.doMove(worldIn, pos, enumfacing, true)) {
				return false;
			}

			int pushCount = 0;
			TileEntity te;

			if ((te = worldIn.getTileEntity(pos)) != null) {
				if (te.getTileData().hasKey("pushCount"))
					pushCount = te.getTileData()
						.getInteger("pushCount");
			}
			worldIn.setBlockState(pos, state.withProperty(
				EXTENDED, Boolean.valueOf(true)), 2);
			worldIn.getTileEntity(pos).getTileData()
				.setInteger("pushCount", pushCount);
			worldIn.playSound(
				pos.getX() + 0.5D, pos
					.getY() + 0.5D,
				pos.getZ() + 0.5D,
				SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS,
				0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F, true);
		} else if (eventID == 1) {
			TileEntity tileentity1 = worldIn
				.getTileEntity(pos.offset(enumfacing));

			if (tileentity1 instanceof TileEntityMemoryPiston) {
				((TileEntityMemoryPiston) tileentity1)
					.clearPistonTileEntity();
			}

			int pushCount = 0;
			boolean isOff = this instanceof BlockMemoryPistonBaseOff;
			tileentity1 = worldIn.getTileEntity(pos);
			if (tileentity1.getTileData().hasKey(
				"pushCount"))
				pushCount = tileentity1.getTileData()
					.getInteger("pushCount");
			tileentity1 = BlockMemoryPistonMoving
				.newTileEntity(this
					.getStateFromMeta(eventParam),
					enumfacing, false, true);
			tileentity1.getTileData().setInteger(
				"pushCount", pushCount);

			worldIn
				.setBlockState(
					pos,
					(isOff ? MazeTowers.BlockMemoryPistonExtensionOff
						: MazeTowers.BlockMemoryPistonExtension)
						.getDefaultState().withProperty(
							BlockMemoryPistonMoving.FACING,
							enumfacing), 3);
			worldIn.setTileEntity(pos, tileentity1);

			BlockPos blockpos = pos.add(enumfacing
				.getFrontOffsetX() * 2, enumfacing
				.getFrontOffsetY() * 2, enumfacing
				.getFrontOffsetZ() * 2);
			IBlockState iblockstate = worldIn.getBlockState(blockpos);
			Block block = iblockstate.getBlock();
			boolean flag1 = false;

			if (block instanceof BlockMemoryPistonExtension) {
				TileEntity tileentity = worldIn
					.getTileEntity(blockpos);

				if (tileentity instanceof TileEntityMemoryPiston) {
					TileEntityMemoryPiston tileentitypiston = (TileEntityMemoryPiston) tileentity;

					if (tileentitypiston.getFacing() == enumfacing
						&& tileentitypiston.isExtending()) {
						tileentitypiston
							.clearPistonTileEntity();
						flag1 = true;
					}
				}
			}

			if (!flag1
				&& !block.isAir(iblockstate, worldIn, blockpos)
				&& ((func_185646_a(iblockstate, worldIn, blockpos,
					enumfacing.getOpposite(), false)))
				&& (block.getMobilityFlag(iblockstate) == EnumPushReaction.NORMAL || block instanceof BlockMemoryPistonBase)) {
				this.doMove(worldIn, pos, enumfacing, false);
			}

			worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_CONTRACT,
				SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
		}

		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public static EnumFacing getFacing(int meta) {
		int i = meta & 7;
		return i > 5 ? null : EnumFacing.getFront(i);
	}

	public static EnumFacing getFacingFromEntity(
		World worldIn, BlockPos clickedBlock,
		EntityLivingBase entityIn) {
		if (MathHelper.abs((float) entityIn.posX
			- clickedBlock.getX()) < 2.0F
			&& MathHelper.abs((float) entityIn.posZ
				- clickedBlock.getZ()) < 2.0F) {
			double d0 = entityIn.posY
				+ entityIn.getEyeHeight();

			if (d0 - clickedBlock.getY() > 2.0D) {
				return EnumFacing.UP;
			}

			if (clickedBlock.getY() - d0 > 0.0D) {
				return EnumFacing.DOWN;
			}
		}

		return entityIn.getHorizontalFacing().getOpposite();
	}

	public static boolean func_185646_a(IBlockState state, World worldIn, BlockPos pos, EnumFacing direction, boolean p_185646_4_) {
		if (!worldIn.getWorldBorder().contains(pos)) {
			return false;
		} else if (pos.getY() >= 0
			&& (direction != EnumFacing.DOWN || pos.getY() != 0)) {
			if (pos.getY() < worldIn.getHeight()
				&& (direction != EnumFacing.UP || pos
					.getY() != worldIn.getHeight() - 1)) {
				Block block = state.getBlock();
				if (!(block instanceof BlockMemoryPistonBase)
					&& block != Blocks.PISTON
					&& block != Blocks.STICKY_PISTON) {
					if (state.getMobilityFlag() == EnumPushReaction.BLOCK && pos.getY() < 2) {
						return false;
					}
					if (state.getMobilityFlag() == EnumPushReaction.BLOCK && block != Blocks.BEDROCK) {
						return false;
					}
					
					if (state.getMobilityFlag() == EnumPushReaction.DESTROY) {
						return true;
					}
				} else if (worldIn
					.getBlockState(pos).getValue(EXTENDED)
					.booleanValue()) {
					return false;
				} else
					return true;

				return !(block.hasTileEntity(worldIn
					.getBlockState(pos)));
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	protected boolean doMove(World worldIn, BlockPos pos,
		EnumFacing direction, boolean extending) {
		if (!extending) {
			worldIn.setBlockToAir(pos.offset(direction));
		}

		int pushCount = 0;
		boolean isOff = this instanceof BlockMemoryPistonBaseOff;
		BlockMemoryPistonStructureHelper blockpistonstructurehelper = new BlockMemoryPistonStructureHelper(
			worldIn, pos, direction, extending);
		TileEntity te;
		List<BlockPos> list = blockpistonstructurehelper
			.getBlocksToMove();
		List<BlockPos> list1 = blockpistonstructurehelper
			.getBlocksToDestroy();
		if ((te = worldIn.getTileEntity(pos)) != null) {
			if (te.getTileData().hasKey("pushCount"))
				pushCount = te.getTileData().getInteger(
					"pushCount");
		}
		if (!list.isEmpty())
			list = list.subList(0, pushCount);

		if (!blockpistonstructurehelper.canMove(pushCount)) {
			return false;
		} else {
			int i = list.size() + list1.size();
			Block[] ablock = new Block[i];
			EnumFacing enumfacing = extending ? direction
				: direction.getOpposite();

			for (int j = list1.size() - 1; j >= 0; --j) {
				BlockPos blockpos = list1.get(j);
				Block block = worldIn.getBlockState(
					blockpos).getBlock();
				// With our change to how snowballs are dropped this needs to
				// disallow to mimic vanilla behavior.
				float chance = block instanceof BlockSnow ? -1.0f
					: 1.0f;
				block
					.dropBlockAsItemWithChance(worldIn,
						blockpos, worldIn
							.getBlockState(blockpos),
						chance, 0);
				worldIn.setBlockToAir(blockpos);
				--i;
				ablock[i] = block;
			}

			for (int k = list.size() - 1; k >= 0; --k) {
				BlockPos blockpos2 = list.get(k);
				IBlockState iblockstate = worldIn
					.getBlockState(blockpos2);
				Block block1 = iblockstate.getBlock();
				block1.getMetaFromState(iblockstate);
				worldIn.setBlockToAir(blockpos2);
				blockpos2 = blockpos2.offset(enumfacing);
				te = BlockMemoryPistonMoving
					.newTileEntity(iblockstate, direction,
						extending, false);
				worldIn
					.setBlockState(
						blockpos2,
						(isOff ? MazeTowers.BlockMemoryPistonExtensionOff
							: MazeTowers.BlockMemoryPistonExtension)
							.getDefaultState()
							.withProperty(FACING, direction),
						4);
				worldIn.setTileEntity(blockpos2, te);
				--i;
				ablock[i] = block1;
			}

			BlockPos blockpos1 = pos.offset(direction);

			if (extending) {
				IBlockState iblockstate1 = isOff ? MazeTowers.BlockMemoryPistonHeadOff
					.getDefaultState().withProperty(
						BlockDirectional.FACING,
						direction)
					: MazeTowers.BlockMemoryPistonHead
						.getDefaultState()
						.withProperty(
							BlockDirectional.FACING,
							direction);
				IBlockState iblockstate2 = isOff ? MazeTowers.BlockMemoryPistonExtensionOff
					.getDefaultState().withProperty(
						BlockMemoryPistonMoving.FACING,
						direction)
					: MazeTowers.BlockMemoryPistonExtension
						.getDefaultState().withProperty(
							BlockMemoryPistonMoving.FACING,
							direction);
				worldIn.getTileEntity(pos).getTileData()
					.setInteger("pushCount", list.size());
				te = BlockMemoryPistonMoving
					.newTileEntity(iblockstate1, direction,
						true, false);
				worldIn.setBlockState(blockpos1,
					iblockstate2, 4);
				te.getTileData().setInteger("pushCount",
					list.size());
				worldIn.setTileEntity(blockpos1, te);
			}

			for (int l = list1.size() - 1; l >= 0; --l) {
				worldIn.notifyNeighborsOfStateChange(
					list1.get(l), ablock[i++]);
			}

			for (int i1 = list.size() - 1; i1 >= 0; --i1) {
				worldIn.notifyNeighborsOfStateChange(
					list.get(i1), ablock[i++]);
			}

			if (extending) {
				worldIn
					.notifyNeighborsOfStateChange(
						blockpos1,
						isOff ? MazeTowers.BlockMemoryPistonHeadOff
							: MazeTowers.BlockMemoryPistonHead);
				worldIn.notifyNeighborsOfStateChange(pos,
					this);
			}

			return true;
		}
	}

	@Override
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING,
			getFacing(meta)).withProperty(EXTENDED,
			Boolean.valueOf((meta & 8) > 0));
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

		if (state.getValue(EXTENDED)
			.booleanValue()) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(FACING).add(EXTENDED).build();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityMemoryPistonMemory();
	}

	@Override
	public boolean onBlockActivated(World worldIn,
		BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumHand hand, ItemStack heldItem,
		EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean isExtended;
		EnumFacing facing;
		IBlockState offState = MazeTowers.BlockMemoryPistonOff
			.getDefaultState()
			.withProperty(
				BlockMemoryPistonBase.FACING,
				facing = state
					.getValue(BlockMemoryPistonBase.FACING))
			.withProperty(
				BlockMemoryPistonBase.EXTENDED,
				isExtended = state
					.getValue(BlockMemoryPistonBase.EXTENDED));
		if (!isExtended) {
			worldIn.playSound(
				pos.getX() + 0.5D, pos.getY() + 0.5D,
				pos.getZ() + 0.5D, SoundEvents.BLOCK_STONE_PRESSPLATE_CLICK_ON,
				SoundCategory.BLOCKS, 0.3F, 0.5F, true);
			worldIn.setBlockState(pos, offState);
		}
		
		/*
		 * if (isExtended) { BlockPos headPos = pos.offset(facing); IBlockState
		 * offStateHead = worldIn.getBlockState(headPos); if
		 * (offStateHead.getBlock() == MazeTowers.BlockMemoryPistonHead) {
		 * offStateHead = MazeTowers.BlockMemoryPistonHeadOff.getDefaultState()
		 * .withProperty(BlockMemoryPistonExtension.FACING,
		 * offStateHead.getValue(BlockMemoryPistonExtension.FACING))
		 * .withProperty(BlockMemoryPistonExtension.SHORT,
		 * offStateHead.getValue(BlockMemoryPistonExtension.SHORT));
		 * worldIn.setBlockState(headPos, offStateHead, 0); } }
		 */
		return !isExtended;
	}
}
