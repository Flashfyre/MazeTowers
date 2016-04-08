package com.samuel.mazetowers.tileentity;

import java.util.ArrayDeque;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockVendorSpawner;
import com.samuel.mazetowers.entity.EntityVillagerVendor;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

public class TileEntityVendorSpawner extends TileEntity implements ITickable {
	
	private boolean enabled;
	private int typeIndex, specialProfessionId, difficulty;
	private String vendorName;
	private BlockPos frontPos;
	private EntityVillager vendor;
	private EnumFacing dir;
	private EnumTowerType type;
	
	public TileEntityVendorSpawner() {
		type = EnumTowerType.STONE_BRICK;
		typeIndex = type.ordinal();
		specialProfessionId = 0;
		difficulty = type.getBaseDifficulty();
		enabled = false;
	}
	
	public void init(boolean isRespawn) {
		final boolean spawnVillager;
		if (!isRespawn) {
    		float posX = pos.getX(), posY = pos.getY(), posZ = pos.getZ();
    		MazeTowerBase tower = MazeTowers.mazeTowers.getTowerBesideCoords(
    			worldObj, (int) Math.floor(posX) >> 4, (int) Math.floor(posZ) >> 4);
    		ArrayDeque<EntityVillager> nearbyEntities =
    			new ArrayDeque<EntityVillager>(worldObj.getEntitiesWithinAABB(
    			EntityVillager.class, new AxisAlignedBB(
    			posX + 2.0D, posY + 1.5D, posZ + 2.0D, posX - 1.0D, posY - 1.5D,
    			posZ - 1.0D)));
    		while (!nearbyEntities.isEmpty() && (((vendor = nearbyEntities.pop())
    			instanceof EntityVillagerVendor && specialProfessionId != vendor.getProfession()) ||
    			(specialProfessionId == 0 && vendor instanceof EntityVillagerVendor)))
    			vendor = null;
    		if (tower != null)
    			setType(tower.towerType);
    		dir = worldObj.getBlockState(pos).getValue(BlockVendorSpawner.FACING);
    		frontPos = pos.offset(dir);
    		spawnVillager = vendor == null;
		} else 
			spawnVillager = true;
		
		if (spawnVillager) {
			vendor = (specialProfessionId != 0) ? new EntityVillagerVendor(worldObj,
				specialProfessionId, type, difficulty) : new EntityVillager(worldObj);
			//vendor.setIsWillingToTrade(true);
		}

		vendor.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
			dir.getHorizontalIndex() * -90F, 0.0F);
		if (!vendor.addedToChunk)
			worldObj.spawnEntityInWorld(vendor);
	}
	
	public EnumTowerType getType() {
		return type;
	}
	
	public int getSpecialProfessionId() {
		return specialProfessionId;
	}
	
	public int getDifficulty() {
		return difficulty;
	}

	public void setType(EnumTowerType type) {
		this.type = type;
		typeIndex = type.ordinal();
		markDirty();
	}
	
	public void setSpecialProfessionId(int specialProfessionId) {
		this.specialProfessionId = specialProfessionId;
		markDirty();
	}
	
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
		markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		typeIndex = compound.getInteger("typeIndex");
		specialProfessionId = compound.getInteger("specialProfessionId");
		difficulty = compound.getInteger("difficulty");
    }

	@Override
    public void writeToNBT(NBTTagCompound compound) {
    	super.writeToNBT(compound);
    	compound.setInteger("type", typeIndex);
    	compound.setInteger("specialProfessionId", specialProfessionId);
    	compound.setInteger("difficulty", difficulty);
    }

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			if (frontPos == null)
				init(false);
    		if ((enabled && (worldObj.isAirBlock(frontPos) || !worldObj.isAirBlock(frontPos.up()))) ||
    			(!enabled && (!worldObj.isAirBlock(frontPos) && worldObj.isAirBlock(frontPos.up())))) {
    			enabled = !enabled;
    			if (enabled && vendor.isDead)
    				init(true);
    				
    			vendor.setInvisible(!enabled);
    			vendor.setSilent(!enabled);
    			//vendor.setIsWillingToTrade(enabled);
    		}
		}	
	}
	
	@Override
	/**
     * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world Current world
     * @param pos Tile's world position
     * @param oldState The old ID of the block
     * @param newState The new ID of the block (May be the same)
     * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
     */
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }
}
