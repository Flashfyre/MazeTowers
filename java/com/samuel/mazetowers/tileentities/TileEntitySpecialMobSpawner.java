package com.samuel.mazetowers.tileentities;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntitySpecialMobSpawner extends TileEntityMobSpawner {
	
	private boolean shouldSpawnAbove = false;
	
	private final MobSpawnerSpecialBaseLogic spawnerLogic = new MobSpawnerSpecialBaseLogic()
    {
		@Override
        public void func_98267_a(int id)
        {
            TileEntitySpecialMobSpawner.this.worldObj
            	.addBlockEvent(TileEntitySpecialMobSpawner.this.pos,
            	MazeTowers.BlockSpecialMobSpawner, id, 0);
        }
        
        @Override
        public World getSpawnerWorld()
        {
            return TileEntitySpecialMobSpawner.this.worldObj;
        }
        
        @Override
        public BlockPos getSpawnerPosition()
        {
            return TileEntitySpecialMobSpawner.this.pos;
        }
        
        @Override
        public void setRandomEntity(WeightedRandomMinecart p_98277_1_)
        {
            super.setRandomEntity(p_98277_1_);

            if (this.getSpawnerWorld() != null)
            {
                this.getSpawnerWorld()
                	.markBlockForUpdate(TileEntitySpecialMobSpawner.this.pos);
            }
        }
    };
    
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.spawnerLogic.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.spawnerLogic.writeToNBT(compound);
    }

    @Override
    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        this.spawnerLogic.updateSpawner();
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        return this.spawnerLogic.setDelayToMin(id) ? true : super.receiveClientEvent(id, type);
    }

    @Override
    public MobSpawnerBaseLogic getSpawnerBaseLogic()
    {
        return this.spawnerLogic;
    }

	public void setShouldSpawnAbove(boolean shouldSpawnAbove) {
		this.shouldSpawnAbove = shouldSpawnAbove;
		this.spawnerLogic.setShouldSpawnAbove(shouldSpawnAbove);
	}
}
