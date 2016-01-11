package com.samuel.mazetowers.blocks;

import java.util.Random;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockResistantDoor extends BlockDoor {
    
    public final int type;
    
    public BlockResistantDoor(String unlocalizedName, Material material, float hardness, float resistance, int type) {
        super(material);
        this.setUnlocalizedName(unlocalizedName);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.type = type;
    }
    
    public BlockResistantDoor(String unlocalizedName, float hardness, float resistance, int type) {
        this(unlocalizedName, Material.rock, hardness, resistance, type);
    }
    
    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos){

        Block block = world.getBlockState(pos).getBlock();
        if (block != this)
        {
            return block.getLightValue(world, pos);
        }
        
        IBlockState state = world.getBlockState(pos);
        return type < 5 ? 0 : 10;
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.getItem();
    }

    
    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World worldIn, BlockPos pos)
    {
        return (this.type == 0) ? MazeTowers.ItemEndStoneDoor :
        	this.type == 1 ? MazeTowers.ItemObsidianDoor :
        	MazeTowers.ItemBedrockDoor;
    }
    
    private Item getItem() {
    	return (this.type == 0) ? MazeTowers.ItemEndStoneDoor :
        	this.type == 1 ? MazeTowers.ItemObsidianDoor :
        	MazeTowers.ItemBedrockDoor;
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (worldIn.isRemote)
    		return false;
    	
    	/*boolean holdingKey = (playerIn.getHeldItem() != null &&
    		playerIn.getHeldItem().getItem() == ChaosBlock.itemChaosKey ||
    		playerIn.getHeldItem().getItem() == ChaosBlock.itemChaosBattleKey);
    	int keyDamage = (holdingKey) ? playerIn.getHeldItem().getItemDamage() : -1;
    	boolean canBeUnlocked = (holdingKey) ? this.type <= keyDamage : false;
    	boolean powered = false;
    	
    	BlockPos blockpos1 = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
        IBlockState iblockstate1 = pos.equals(blockpos1) ? state : worldIn.getBlockState(blockpos1);
    	
    	if (canBeUnlocked) {
            if (iblockstate1.getBlock() != this)
            {
                return false;
            }
            else
          {
            	powered = (((Boolean) state.getValue(POWERED)).booleanValue());
                if (!(((Boolean) state.getValue(OPEN)).booleanValue()) || !powered) {
	            	
                }
            }
    	} else if (!((Boolean) state.getValue(OPEN)).booleanValue())  */
    	worldIn.playSoundAtEntity(playerIn, "mazetowers:door_locked", 1.0F, 1.0F);
    	
    	return false;//canBeUnlocked && !powered;
    }
    
    public void activateDoor(World worldIn, BlockPos pos, IBlockState state,
    	EntityPlayer playerIn) {
    	BlockPos blockpos1 = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ?
    		pos : pos.down();
        IBlockState iblockstate1 = pos.equals(blockpos1) ? state :
        	worldIn.getBlockState(blockpos1);
        
    	state = iblockstate1.cycleProperty(OPEN);
        worldIn.setBlockState(blockpos1, state, 2);
        worldIn.markBlockRangeForRenderUpdate(blockpos1, pos);
        worldIn.playSoundAtEntity(playerIn, !((Boolean)state.getValue(OPEN)).booleanValue() ? "random.door_close" : "random.door_open", 1.0F, 1.0F);
        if (((Boolean) state.getValue(OPEN)).booleanValue())
        	worldIn.playSoundAtEntity(playerIn, "mazetowers:door_unlock", 1.0F, 1.0F);
    }
    
    /**
     * Called when a neighboring block changes.
     */
    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
    	if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
        {
            BlockPos blockpos = pos.down();
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
            else if (neighborBlock != this)
            {
                this.onNeighborBlockChange(worldIn, blockpos, iblockstate, neighborBlock);
            }
        }
        else
        {
            boolean flag1 = false;
            BlockPos blockpos1 = pos.up();
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

            if (iblockstate1.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
            {
                worldIn.setBlockToAir(pos);
                flag1 = true;

                if (iblockstate1.getBlock() == this)
                {
                    worldIn.setBlockToAir(blockpos1);
                }
            }

            if (flag1)
            {
                if (!worldIn.isRemote)
                {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                }
            }
            else
            {
                boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(blockpos1);
                boolean hasScanner =
                	getHasScanner(worldIn, pos, state.getValue(BlockResistantDoor.FACING), true);
                if (((!flag || (!hasScanner || neighborBlock instanceof BlockItemScanner) &&
                	neighborBlock.canProvidePower())) && neighborBlock != this &&
                	flag != ((Boolean)iblockstate1.getValue(POWERED)).booleanValue())
                {
                    worldIn.setBlockState(blockpos1, iblockstate1.withProperty(POWERED, Boolean.valueOf(flag)), 2);

                    if (flag != ((Boolean)state.getValue(OPEN)).booleanValue())
                    {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(flag)), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        worldIn.playAuxSFXAtEntity((EntityPlayer)null, flag ? 1003 : 1006, pos, 0);
                    }
                }
            }
        }
    }

	private boolean getHasScanner(World worldIn, BlockPos pos, EnumFacing facing, boolean isBottom) {
		BlockPos frontPos = pos.offset(facing.getOpposite());
		BlockPos backPos = pos.offset(facing);
		boolean hasScanner = (worldIn.getBlockState(frontPos).getBlock() instanceof BlockItemScanner ||
			worldIn.getBlockState(backPos).getBlock() instanceof BlockItemScanner ||
			worldIn.getBlockState(frontPos.offset(facing.rotateY())).getBlock() instanceof
			BlockItemScanner || worldIn.getBlockState(frontPos.offset(facing.rotateYCCW()))
			.getBlock() instanceof BlockItemScanner ||
			worldIn.getBlockState(backPos.offset(facing.rotateY())).getBlock() instanceof
			BlockItemScanner || worldIn.getBlockState(backPos.offset(facing.rotateYCCW()))
			.getBlock() instanceof BlockItemScanner ||
			worldIn.getBlockState(pos.offset(facing.rotateY())).getBlock() instanceof
			BlockItemScanner || worldIn.getBlockState(pos.offset(facing.rotateYCCW()))
			.getBlock() instanceof BlockItemScanner);
		return !hasScanner && isBottom ? getHasScanner(worldIn, pos.up(), facing, false) : hasScanner;
	}
    
}