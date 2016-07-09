package com.samuel.mazetowers.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.tileentity.TileEntityVendorSpawner;

public class BlockVendorSpawner extends Block implements ITileEntityProvider {
	
	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	public static final PropertyDirection FACING = PropertyDirection
		.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool VISIBLE = PropertyBool
		.create("visible");

	public BlockVendorSpawner() {
		super(Material.CIRCUITS);
	}
	
    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
    	return AABB;
    }
	
	/*@Override
	public int getDamageValue(World worldIn, BlockPos pos) {
        return ((TileEntityVendorSpawner) worldIn.getTileEntity(pos)).getType().ordinal();
    }*/
	
	@Override
	public boolean onBlockActivated(World worldIn,
		BlockPos pos, IBlockState state,
		EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side,
		float hitX, float hitY, float hitZ) {
		boolean isVisible = state.getValue(VISIBLE);
		worldIn.playSound(
			pos.getX() + 0.5D, pos.getY() + 0.5D,
			pos.getZ() + 0.5D, isVisible ?
			SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_OFF :
			SoundEvents.BLOCK_METAL_PRESSPLATE_CLICK_ON,
			SoundCategory.BLOCKS, 0.3F, 0.5F, true);
		worldIn.setBlockState(pos, state.withProperty(VISIBLE, !isVisible));
		return true;
	}
	
	@Override
	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

	@Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return !this.blockMaterial.blocksMovement();
    }

	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
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
			enumfacing).withProperty(VISIBLE, meta >> 2 == 0);
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
		
		if (!state.getValue(VISIBLE))
			i |= 4;

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return (new BlockStateContainer.Builder(this)).add(FACING).add(VISIBLE).build();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityVendorSpawner();
	}
}
