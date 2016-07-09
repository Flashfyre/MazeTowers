package com.samuel.mazetowers.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.samuel.mazetowers.init.ModBlocks;

public class TileEntitySpecialMobSpawner extends TileEntityMobSpawner {
	
	private boolean shouldSpawnAbove = false;
	
	private final MobSpawnerSpecialBaseLogic spawnerLogic = new MobSpawnerSpecialBaseLogic()
    {
		@Override
		public void broadcastEvent(int id)
        {
            TileEntitySpecialMobSpawner.this.worldObj.addBlockEvent(TileEntitySpecialMobSpawner.this.pos, Blocks.MOB_SPAWNER, id, 0);
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
        public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_)
        {
            super.setNextSpawnData(p_184993_1_);

            if (this.getSpawnerWorld() != null)
            {
                IBlockState iblockstate = this.getSpawnerWorld().getBlockState(this
                	.getSpawnerPosition());
                this.getSpawnerWorld().notifyBlockUpdate(TileEntitySpecialMobSpawner
                	.this.pos, iblockstate, iblockstate, 4);
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.spawnerLogic.writeToNBT(compound);
        
        return compound;
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
