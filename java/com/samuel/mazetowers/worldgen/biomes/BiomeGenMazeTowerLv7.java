package com.samuel.mazetowers.worldgen.biomes;

import com.samuel.mazetowers.entities.EntityUltravioletBlaze;

import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenMazeTowerLv7 extends BiomeGenMazeTowerLv1
{
    public BiomeGenMazeTowerLv7(int p_i1990_1_)
    {
        super(p_i1990_1_);
        this.spawnableMonsterList.clear();
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityUltravioletBlaze.class, 15, 1, 3));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 15, 1, 3));
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCaveSpider.class, 15, 1, 3));
        
    }
}
