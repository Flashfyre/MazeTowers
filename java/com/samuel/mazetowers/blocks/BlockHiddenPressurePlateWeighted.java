package com.samuel.mazetowers.blocks;

import com.samuel.mazetowers.etc.UnlistedPropertyCopiedBlock;

import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Edited from Camouflage Block by TheGreyGhost on 19/04/2015.
 */
public class BlockHiddenPressurePlateWeighted extends BlockPressurePlateWeighted {
	
	public static final UnlistedPropertyCopiedBlock COPIEDBLOCK = new UnlistedPropertyCopiedBlock();

	public BlockHiddenPressurePlateWeighted(String unlocalizedName) {
		super(Material.circuits, 150);
		setUnlocalizedName(unlocalizedName);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
		this.setTickRandomly(true);
	}
	
	 @SideOnly(Side.CLIENT)
	 public EnumWorldBlockLayer getBlockLayer()
	 {
		 return EnumWorldBlockLayer.SOLID;
	 }
	 
	 @Override
	  /**
	   * Used to determine ambient occlusion and culling when rebuilding chunks for render
	   */
	  public boolean isOpaqueCube()
	  {
	      return false;
	  }

	  @Override
	  public boolean isFullCube()
	  {
	      return false;
	  }

	  @Override
	  public int getRenderType() {
	    return 3;
	  }
	  
	  @Override
	  public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	  {
		  setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F,
			(worldIn.getBlockState(pos).getValue(this.POWER) == 0) ? 0.0625F :
			0.03125F, 0.9375F);
	  }

	  @Override
	  protected BlockState createBlockState() {
	    IProperty [] listedProperties = new IProperty[] { POWER };
	    IUnlistedProperty [] unlistedProperties = new IUnlistedProperty[] { COPIEDBLOCK };
	    return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	  }

	  @Override
	  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
	    if (state instanceof IExtendedBlockState) {
	      IExtendedBlockState retval = (IExtendedBlockState)state;
	      IBlockState belowState = getBelowState(world, pos);
	      retval = retval.withProperty(COPIEDBLOCK, belowState);
	      return retval;
	    }
	    return state;
	  }

	  @Override
	  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	  {
	    return state;
	  }

	  private IBlockState getBelowState(IBlockAccess world, BlockPos blockPos)
	  {
	    final IBlockState normal = Blocks.iron_block.getDefaultState();
	    IBlockState belowState = null;

	    if (blockPos.getY() == 0 || (belowState = world.getBlockState(blockPos.down()))
	    	== Blocks.air.getDefaultState())
	    	return normal;
	    return belowState;
	  }
}
