package com.samuel.mazetowers.tileentity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;
import com.samuel.mazetowers.etc.MTHelper;

public abstract class MobSpawnerSpecialBaseLogic extends MobSpawnerBaseLogic {
	
	 /** The delay to spawn. */
    private int spawnDelay = 20;
	private String baseMobID = "Pig";
	private String mobID = "Pig";
	private final List<WeightedSpawnerEntity> minecartToSpawn = Lists.<WeightedSpawnerEntity>newArrayList();
	private WeightedSpawnerEntity randomEntity = new WeightedSpawnerEntity();
	 /** The distance from which a player activates the spawner. */
    private int activatingRangeFromPlayer = 16;
    /** The rotation of the mob inside the mob spawner */
    private double mobRotation;
    /** the previous rotation of the mob inside the mob spawner */
    private double prevMobRotation;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 2;
    /** Cached instance of the entity to render inside the spawner. */
    private Entity cachedEntity;
    private int maxNearbyEntities = 6;
    /** The range coefficient for spawning entities around. */
    private int spawnRange = 4;
    private boolean shouldSpawnAbove = false;
	
    /**
    * Gets the entity name that should be spawned.
    */
    private String getEntityNameToSpawn()
    {
       return this.randomEntity.getNbt().getString("id");
   	}
    
    private String getActualEntityName() {
    	return this.mobID;
    }
    
    @Override
    public void setEntityName(String name)
    {
    	super.setEntityName(name);
        mobID = name;
        baseMobID = name != "ChargedCreeper" ? name : "Creeper";
        this.randomEntity.getNbt().setString("id", baseMobID);
    }
	
	/**
     * Returns true if there's a player close enough to this mob spawner to activate it.
     */
    private boolean isActivated()
    {
        BlockPos blockpos = this.getSpawnerPosition();
        return this.getSpawnerWorld().isAnyPlayerWithinRangeAt(
        	blockpos.getX() + 0.5D, blockpos.getY() + 0.5D,
        	blockpos.getZ() + 0.5D, this.activatingRangeFromPlayer);
    }
    
    public void setShouldSpawnAbove(boolean shouldSpawnAbove) {
    	this.shouldSpawnAbove = shouldSpawnAbove;
    }
	
	private Entity spawnNewEntity(Entity entityliving, boolean spawn)
    {
        if (entityliving instanceof EntityLivingBase && entityliving.worldObj != null && spawn)
        {	
            
        }

        return entityliving;
    }
	
	@Override
	public void updateSpawner() {
        if (!this.isActivated())
        {
            this.prevMobRotation = this.mobRotation;
        }
        else
        {
            BlockPos blockpos = this.getSpawnerPosition();

            if (this.getSpawnerWorld().isRemote)
            {
                double d3 = blockpos.getX() + this.getSpawnerWorld().rand.nextFloat();
                double d4 = blockpos.getY() + this.getSpawnerWorld().rand.nextFloat();
                double d5 = blockpos.getZ() + this.getSpawnerWorld().rand.nextFloat();
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0D, 0.0D, 0.0D, new int[0]);
                this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D, new int[0]);

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                }

                this.prevMobRotation = this.mobRotation;
                this.mobRotation = (this.mobRotation + 1000.0F / (this.spawnDelay + 200.0F)) % 360.0D;
            }
            else
            {
                if (this.spawnDelay == -1)
                {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0)
                {
                    --this.spawnDelay;
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i)
                {
                    NBTTagCompound nbttagcompound = this.randomEntity.getNbt();
                    NBTTagList nbttaglist = nbttagcompound.getTagList("Pos", 6);
                    World world = this.getSpawnerWorld();
                    int j = nbttaglist.tagCount();
                    double d0 = j >= 1 ? nbttaglist.getDoubleAt(0) : blockpos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * this.spawnRange + 0.5D;
                    double d1 = j >= 2 ? nbttaglist.getDoubleAt(1) : (double)(blockpos.getY() + world.rand.nextInt(3) - 1);
                    double d2 = j >= 3 ? nbttaglist.getDoubleAt(2) : blockpos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * this.spawnRange + 0.5D;
                    Entity entity = AnvilChunkLoader.readWorldEntityPos(nbttagcompound, world, d0, d1, d2, false);

                    if (entity == null)
                    {
                        return;
                    }

                    int k = world.getEntitiesWithinAABB(entity.getClass(), (new AxisAlignedBB(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() + 1, blockpos.getY() + 1, blockpos.getZ() + 1)).expandXyz(this.spawnRange)).size();

                    if (k >= this.maxNearbyEntities)
                    {
                        this.resetTimer();
                        return;
                    }

                    EntityLiving entityliving = entity instanceof EntityLiving ? (EntityLiving)entity : null;
                    entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, world.rand.nextFloat() * 360.0F, 0.0F);

                    if (entityliving == null || entityliving.getCanSpawnHere() && entityliving.isNotColliding())
                    {
                        if (this.randomEntity.getNbt().getSize() == 1 && this.randomEntity.getNbt().hasKey("id", 8) && entity instanceof EntityLiving)
                            ((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);

                        if (entityliving instanceof EntityLiving)
                            entityliving.onInitialSpawn(entityliving.worldObj.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);

                        if (entityliving.worldObj.provider.getDimension() == -1 &&
                        	entityliving instanceof EntitySkeleton) {
                        	EntitySkeleton skeleton = (EntitySkeleton) entityliving;
                        	Method setSize = MTHelper.findObfuscatedMethod(Entity.class,
                        		(Entity) skeleton, new String[] { "setSize", "func_70105_a" },
                        		float.class, float.class);
                        	setSize.setAccessible(true);
                        	try {
            					setSize.invoke(skeleton, 0.6F, 1.95F);
            				} catch (IllegalAccessException e) {
            					e.printStackTrace();
            				} catch (IllegalArgumentException e) {
            					e.printStackTrace();
            				} catch (InvocationTargetException e) {
            					e.printStackTrace();
            				} finally {
            					entityliving.worldObj.spawnEntityInWorld(skeleton);
            				}
                        } else if (getActualEntityName() == "ChargedCreeper") {
                        	EntityCreeper creeper = (EntityCreeper) entityliving;
                        	Field POWERED = MTHelper.findObfuscatedField(EntityCreeper.class,
                        		"POWERED", "field_184714_b");
                        	POWERED.setAccessible(true);
                        	try {
								creeper.getDataManager().set((DataParameter<Boolean>) POWERED.get(creeper), true);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								entityliving.worldObj.spawnEntityInWorld(creeper);
							}
                        } else
                        	AnvilChunkLoader.spawnEntity(entity, world);
                        
                        world.playBroadcastSound(2004, blockpos, 0);

                        if (entityliving != null)
                        {
                            entityliving.spawnExplosionParticle();
                        }

                        flag = true;
                    }
                }

                if (flag)
                {
                    this.resetTimer();
                }
            }
        }
    }
	
	private void resetTimer()
    {
        if (this.maxSpawnDelay <= this.minSpawnDelay)
        {
            this.spawnDelay = this.minSpawnDelay;
        }
        else
        {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
        }

        if (!this.minecartToSpawn.isEmpty())
        {
            this.setNextSpawnData(WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
        }

        this.broadcastEvent(1);
    }

	@Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.spawnDelay = nbt.getShort("Delay");
        this.minecartToSpawn.clear();

        if (nbt.hasKey("SpawnPotentials", 9))
        {
            NBTTagList nbttaglist = nbt.getTagList("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                this.minecartToSpawn.add(new WeightedSpawnerEntity(nbttaglist.getCompoundTagAt(i)));
            }
        }

        NBTTagCompound nbttagcompound = nbt.getCompoundTag("SpawnData");

        if (!nbttagcompound.hasKey("id", 8))
        {
            nbttagcompound.setString("id", "Pig");
        }
        
        if (!nbttagcompound.hasKey("mobID", 8))
        {
            nbttagcompound.setString("mobID", this.mobID);
        }

        this.setNextSpawnData(new WeightedSpawnerEntity(1, nbttagcompound));

        if (nbt.hasKey("MinSpawnDelay", 99))
        {
            this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
            this.spawnCount = nbt.getShort("SpawnCount");
        }

        if (nbt.hasKey("MaxNearbyEntities", 99))
        {
            this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
            this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
        }

        if (nbt.hasKey("SpawnRange", 99))
        {
            this.spawnRange = nbt.getShort("SpawnRange");
        }
        
        this.shouldSpawnAbove = nbt.getBoolean("ShouldSpawnAbove");

        if (this.getSpawnerWorld() != null)
        {
            this.cachedEntity = null;
        }
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        String s = this.getEntityNameToSpawn();

        if (!StringUtils.isNullOrEmpty(s))
        {
            nbt.setShort("Delay", (short)this.spawnDelay);
            nbt.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
            nbt.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
            nbt.setShort("SpawnCount", (short)this.spawnCount);
            nbt.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
            nbt.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
            nbt.setShort("SpawnRange", (short)this.spawnRange);
            nbt.setString("mobID", this.mobID);
            nbt.setTag("SpawnData", this.randomEntity.getNbt().copy());
            nbt.setBoolean("ShouldSpawnAbove", shouldSpawnAbove);
            NBTTagList nbttaglist = new NBTTagList();

            if (!this.minecartToSpawn.isEmpty())
            {
                for (WeightedSpawnerEntity weightedspawnerentity : this.minecartToSpawn)
                {
                    nbttaglist.appendTag(weightedspawnerentity.toCompoundTag());
                }
            }
            else
            {
                nbttaglist.appendTag(this.randomEntity.toCompoundTag());
            }

            nbt.setTag("SpawnPotentials", nbttaglist);
        }
        
        return nbt;
    }


    @Override
    /**
     * Sets the delay to minDelay if parameter given is 1, else return false.
     */
    public boolean setDelayToMin(int delay)
    {
        if (delay == 1 && this.getSpawnerWorld().isRemote)
        {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Entity getCachedEntity()
    {
        if (this.cachedEntity == null)
        {
            this.cachedEntity = AnvilChunkLoader.readWorldEntity(this.randomEntity.getNbt(), this.getSpawnerWorld(), false);

            if (this.randomEntity.getNbt().getSize() == 1 && this.randomEntity.getNbt().hasKey("id", 8) && this.cachedEntity instanceof EntityLiving)
            {
                ((EntityLiving)this.cachedEntity).onInitialSpawn(this.getSpawnerWorld().getDifficultyForLocation(new BlockPos(this.cachedEntity)), (IEntityLivingData)null);
            }
        }

        return this.cachedEntity;
    }

	private WeightedSpawnerEntity getRandomEntity()
    {
        return this.randomEntity;
    }
    
    @Override
    public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_)
    {
    	super.setNextSpawnData(p_184993_1_);
        this.randomEntity = p_184993_1_;
    }
}
