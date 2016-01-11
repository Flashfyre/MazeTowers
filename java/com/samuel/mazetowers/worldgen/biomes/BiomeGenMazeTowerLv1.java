package com.samuel.mazetowers.worldgen.biomes;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeGenMazeTowerLv1 extends BiomeGenBase
{
    public BiomeGenMazeTowerLv1(int p_i1990_1_)
    {
        super(p_i1990_1_);
        this.spawnableMonsterList.clear();
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 15, 1, 3));
    }
}
