package com.samuel.mazetowers.items;

import java.util.BitSet;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModSounds;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MiniTower;

public class ItemSpectritePickaxe extends ItemPickaxe {
	
	public ItemSpectritePickaxe() {
        super(MazeTowers.SPECTRITE_TOOL);
        this.addPropertyOverride(new ResourceLocation("time"), MazeTowers.ItemPropertyGetterSpectrite);
        attackSpeed = -3.0F;
    }
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		
		String displayName = super.getItemStackDisplayName(stack);
		displayName = TextFormatting.LIGHT_PURPLE + displayName;
		return displayName;
	}
	
	@Override
	/**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState blockIn, BlockPos pos,
    	EntityLivingBase entityLiving) {
		if (blockIn.getBlockHardness(worldIn, pos) != 0.0D) {
			stack.damageItem(1, entityLiving);
		}
		
		if (!worldIn.isRemote) {
			
		}

        return true;
    }
	
	@Override
	/**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param pos Block's position in world
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
    	World worldIn = player.worldObj;
    	if (!worldIn.isRemote) {
			WorldServer worldServer = (WorldServer) worldIn;
			Vec3d lookVec = player.getLookVec();
			EnumFacing facing = EnumFacing.getFacingFromVector((float) lookVec.xCoord,
				(float) lookVec.yCoord, (float) lookVec.zCoord);
			Axis axis = facing.getAxis();
			BlockPos curPos;
			Block curBlock;
			IBlockState curState;
			final int posX = pos.getX(), posY = pos.getY(), posZ = pos.getZ();
			Iterator<BlockPos> targetBlocks;
			
			if (axis != Axis.Y && posY < player.posY)
				axis = Axis.Y;
						
			targetBlocks = BlockPos.getAllInBox(new BlockPos(axis == Axis.X ?
				posX : posX - 1, axis == Axis.Y ? posY : posY - 1, axis == Axis.Z ?
				posZ : posZ - 1), new BlockPos(axis == Axis.X ? posX : posX + 1, axis == Axis.Y ?
				posY : posY + 1, axis == Axis.Z ? posZ : posZ + 1)).iterator();
			
			while (targetBlocks.hasNext()) {
				curPos = targetBlocks.next();
				curState = worldIn.getBlockState(curPos);
				curBlock = curState.getBlock();
				if (curBlock.canHarvestBlock(worldIn, curPos, player)) {
					curBlock.onBlockHarvested(worldIn, curPos, curState, player);
				}
				if (curBlock.getBlockHardness(curState, worldIn, curPos) > 0.0) {
					int chunkX = curPos.getX() >> 4, chunkZ = curPos.getZ() >> 4;
					MazeTowerBase tower = MazeTowers.mazeTowers.getTowerAtCoords(worldIn,
						chunkX, chunkZ);
					if (tower != null) {
						BitSet[][] blockBreakabilityData = tower.getBlockBreakabilityData();
						int[] coords = tower
							.getCoordsFromPos(curPos);
						if (coords[0] == 0)
							coords[0] = blockBreakabilityData.length - 1;
						if (coords[0] >= 0 && coords[0] < blockBreakabilityData.length
							&& !blockBreakabilityData[coords[0]][coords[1]].get(coords[2]))
							continue;
					} else {
						tower = MazeTowers.mazeTowers.getTowerBesideCoords(worldIn, chunkX, chunkZ);
						if (tower != null) {
							boolean isUnbreakable = false;
							for (MiniTower mt : tower.getMiniTowers()) {
	    						if (!mt.getPosBreakability(curPos)) {
	    							isUnbreakable = true;
	    							break;
	    						}
	    							
	    					}
							if (isUnbreakable)
								continue;
						}
					}
					
					worldIn.destroyBlock(curPos, true);
					curBlock.onBlockDestroyedByPlayer(worldIn, curPos, curState);
				}
			}	
			
			worldIn.playSound(null, pos, ModSounds.fatality, SoundCategory.PLAYERS, 1.0F,
				1.0F + (worldIn.rand.nextFloat()) * 0.4F);
			
			worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE,
					EnumParticleTypes.EXPLOSION_LARGE.getShouldIgnoreRange(),
				posX, posY, posZ, 7, worldIn.rand.nextFloat(), worldIn.rand.nextFloat(),
				worldIn.rand.nextFloat(), 0.0D, new int[0]);
    	}
        return false;
    }
}
