package com.samuel.mazetowers.blocks;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.samuel.mazetowers.tileentity.TileEntitySpecialMobSpawner;

public class BlockSpecialMobSpawner extends BlockMobSpawner {
	
	@Override
	/**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySpecialMobSpawner();
    }
}
