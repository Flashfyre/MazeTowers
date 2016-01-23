package com.samuel.mazetowers.blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.UnlistedPropertyCopiedBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHiddenButton extends Block {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	public static final UnlistedPropertyCopiedBlock COPIEDBLOCK = new UnlistedPropertyCopiedBlock();
	/*@SuppressWarnings("unchecked")
	public static final IUnlistedProperty[] properties = new IUnlistedProperty[7];
	private static int cubeSize = 1;
	
	static
    {
        for(EnumFacing f : EnumFacing.values())
        {
            properties[f.ordinal()] = Properties.toUnlisted(PropertyInteger.create(f.getName(), 0, (1 << (cubeSize * cubeSize)) - 1));
        }
        properties[6] = COPIEDBLOCK;
    }*/
	
	public BlockHiddenButton(String unlocalizedName) {
		super(MazeTowers.solidCircuits);
		IExtendedBlockState state = ((IExtendedBlockState) this.blockState.getBaseState())
			.withProperty(COPIEDBLOCK, Blocks.quartz_block.getDefaultState());
		setCreativeTab(CreativeTabs.tabRedstone);
		setUnlocalizedName(unlocalizedName);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING,
			EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)));
	    setTickRandomly(true);
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
	  protected BlockState createBlockState() {
	    IProperty [] listedProperties = new IProperty[] { FACING, POWERED };
	    IUnlistedProperty [] unlistedProperties = { COPIEDBLOCK };
	    return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	  }

	  @Override
	  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
	    if (state instanceof IExtendedBlockState) {
	      IExtendedBlockState model = (IExtendedBlockState)state;
	      IBlockState belowState = getConnectedBlockState(world, pos);
	      model = model.withProperty(COPIEDBLOCK, belowState);
	      return model;
	    }
	    return state;
	  }

	  @Override
	  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	  {
	    return state;
	  }
	  
	  @Override
	  /**
	   * Convert the BlockState into the correct metadata value
	   */
	  public int getMetaFromState(IBlockState state) {
	        int i;

	        switch ((EnumFacing)state.getValue(FACING))
	        {
	            case EAST:
	                i = 2;
	                break;
	            case WEST:
	                i = 3;
	                break;
	            case SOUTH:
	                i = 4;
	                break;
	            case NORTH:
	                i = 5;
	                break;
	            case DOWN:
	            	i = 1;
	            	break;
	            default:
	              i = 0;
	      }

	      if (((Boolean)state.getValue(POWERED)).booleanValue())
	      {
	          i += 6;
	      }

	      return i;
	  }
	  
	  @Override
	  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
	    {
	        return null;
	    }

	  	@Override
	    /**
	     * How many world ticks before ticking
	     */
	    public int tickRate(World worldIn)
	    {
	        return 20;
	    }

	  	@Override
	    /**
	     * Check whether this Block can be placed on the given side
	     */
	    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
	    {
	        return func_181088_a(worldIn, pos, side.getOpposite());
	    }

	  	@Override
	    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	    {
	        for (EnumFacing enumfacing : EnumFacing.values())
	        {
	            if (func_181088_a(worldIn, pos, enumfacing))
	            {
	                return true;
	            }
	        }

	        return false;
	    }

	    protected static boolean func_181088_a(World p_181088_0_, BlockPos p_181088_1_, EnumFacing p_181088_2_)
	    {
	        return p_181088_2_ == EnumFacing.DOWN && World.doesBlockHaveSolidTopSurface(p_181088_0_, p_181088_1_.down()) ? true : p_181088_0_.isSideSolid(p_181088_1_.offset(p_181088_2_), p_181088_2_.getOpposite());
	    }

	    /**
	     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	     * IBlockstate
	     */
	    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	    {
	        return func_181088_a(worldIn, pos, facing.getOpposite()) ? this.getDefaultState().withProperty(FACING, facing).withProperty(POWERED, Boolean.valueOf(false)) : this.getDefaultState().withProperty(FACING, EnumFacing.DOWN).withProperty(POWERED, Boolean.valueOf(false));
	    }

	    @Override
	    /**
	     * Called when a neighboring block changes.
	     */
	    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	    {
	        if (this.checkForDrop(worldIn, pos, state) && !func_181088_a(worldIn, pos, ((EnumFacing)state.getValue(FACING)).getOpposite()))
	        {
	            this.dropBlockAsItem(worldIn, pos, state, 0);
	            worldIn.setBlockToAir(pos);
	        }
	    }

	    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
	    {
	        if (this.canPlaceBlockAt(worldIn, pos))
	        {
	            return true;
	        }
	        else
	        {
	            this.dropBlockAsItem(worldIn, pos, state, 0);
	            worldIn.setBlockToAir(pos);
	            return false;
	        }
	    }

	    @Override
	    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	    {
	        this.updateBlockBounds(worldIn.getBlockState(pos));
	    }

	    private void updateBlockBounds(IBlockState state)
	    {
	        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
	        boolean flag = ((Boolean)state.getValue(POWERED)).booleanValue();
	        float f = 0.25F;
	        float f1 = 0.375F;
	        float f2 = (float)(flag ? 1 : 2) / 16.0F;
	        float f3 = 0.125F;
	        float f4 = 0.1875F;

	        switch (enumfacing)
	        {
	            case EAST:
	                this.setBlockBounds(0.0F, 0.375F, 0.3125F, f2, 0.625F, 0.6875F);
	                break;
	            case WEST:
	                this.setBlockBounds(1.0F - f2, 0.375F, 0.3125F, 1.0F, 0.625F, 0.6875F);
	                break;
	            case SOUTH:
	                this.setBlockBounds(0.3125F, 0.375F, 0.0F, 0.6875F, 0.625F, f2);
	                break;
	            case NORTH:
	                this.setBlockBounds(0.3125F, 0.375F, 1.0F - f2, 0.6875F, 0.625F, 1.0F);
	                break;
	            case UP:
	                this.setBlockBounds(0.3125F, 0.0F, 0.375F, 0.6875F, 0.0F + f2, 0.625F);
	                break;
	            case DOWN:
	                this.setBlockBounds(0.3125F, 1.0F - f2, 0.375F, 0.6875F, 1.0F, 0.625F);
	        }
	    }

	    @Override
	    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	    {
	        if (((Boolean)state.getValue(POWERED)).booleanValue())
	        {
	            return true;
	        }
	        else
	        {
	            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
	            worldIn.markBlockRangeForRenderUpdate(pos, pos);
	            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
	            this.notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
	            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	            return true;
	        }
	    }

	    @Override
	    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	    {
	        if (((Boolean)state.getValue(POWERED)).booleanValue())
	        {
	            this.notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
	        }

	        super.breakBlock(worldIn, pos, state);
	    }

	    @Override
	    public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
	    {
	        return ((Boolean)state.getValue(POWERED)).booleanValue() ? 15 : 0;
	    }

	    @Override
	    public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
	    {
	        return !((Boolean)state.getValue(POWERED)).booleanValue() ? 0 : (state.getValue(FACING) == side ? 15 : 0);
	    }

	    @Override
	    /**
	     * Can this block provide power. Only wire currently seems to have this change based on its state.
	     */
	    public boolean canProvidePower()
	    {
	        return true;
	    }

	    @Override
	    /**
	     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
	     */
	    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
	    {
	    }

	    @Override
	    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	    {
	        if (!worldIn.isRemote)
	        {
	            if (((Boolean)state.getValue(POWERED)).booleanValue())
	            {
                    worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)));
                    this.notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
                    worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, 0.5F);
                    worldIn.markBlockRangeForRenderUpdate(pos, pos);
	            }
	        }
	    }

	    @Override
	    /**
	     * Sets the block's bounds for rendering it as an item
	     */
	    public void setBlockBoundsForItemRender()
	    {
	        float f = 0.1875F;
	        float f1 = 0.125F;
	        float f2 = 0.125F;
	        this.setBlockBounds(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
	    }

	    @Override
	    /**
	     * Called When an Entity Collided with the Block
	     */
	    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	    {
	    }
	    
	    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing)
	    {
	        worldIn.notifyNeighborsOfStateChange(pos, this);
	        worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
	    }
	    
	    @Override
	    /**
	     * Convert the given metadata into a BlockState for this Block
	     */
	    public IBlockState getStateFromMeta(int meta)
	    {
	        EnumFacing enumfacing;

	        switch (meta & 7)
	        {
	            case 0:
	                enumfacing = EnumFacing.DOWN;
	                break;
	            case 1:
	                enumfacing = EnumFacing.EAST;
	                break;
	            case 2:
	                enumfacing = EnumFacing.WEST;
	                break;
	            case 3:
	                enumfacing = EnumFacing.SOUTH;
	                break;
	            case 4:
	                enumfacing = EnumFacing.NORTH;
	                break;
	            case 5:
	            default:
	                enumfacing = EnumFacing.UP;
	        }

	        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
	    }

	  private IBlockState getConnectedBlockState(IBlockAccess world, BlockPos blockPos)
	  {
	    final IBlockState normal = Blocks.quartz_block.getStateFromMeta(3);
	    EnumFacing facing = world.getBlockState(blockPos).getValue(BlockButton.FACING);
	    IBlockState connState = null;

	    if ((connState = world.getBlockState(blockPos.offset(facing.getOpposite())))
	    	== Blocks.air.getDefaultState())
	    	return normal;
	    return connState;
	  }
}