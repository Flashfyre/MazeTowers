package com.samuel.mazetowers.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockItemScannerGold extends BlockItemScanner {
    
	public BlockItemScannerGold() {
		super(Material.iron, MapColor.goldColor);
	}
	
	@Override
	/**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 60;
    }
    
	
	@Override
	public void setStateBasedOnMatchResult(World worldIn, BlockPos pos,
    	IBlockState state, boolean correctItem) {
    	worldIn.setBlockState(pos, state.withProperty(STATE, correctItem ? 3 : 2));
    	this.notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
        worldIn.playSoundEffect((double)pos.getX() + 0.5D,
         	(double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D,
         	(correctItem ? "mazetowers:correct" : "mazetowers:incorrect"),
         	0.15F, 1.0F);
        worldIn.markBlockRangeForRenderUpdate(pos, pos);
        worldIn.scheduleUpdate(pos, this, (int) (this.tickRate(worldIn) *
        	(correctItem ? 2 : 0.5)));
    }
}