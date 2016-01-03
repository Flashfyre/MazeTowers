package com.samuel.mazetowers.etc;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MTUtils {
	
	private static IBlockState air = Blocks.air.getDefaultState();
	private static final int minY = 49;
	
	public static int getSurfaceY(World world, int x, int z, int range) {
		int cy = minY;
		boolean nextY = true;
		
		for (; cy < 127; cy++) {
			nextY = false;
			for (int cz = z - range; cz <= z + range; cz++) {
				for (int cx = x - range; cx <= x + range; cx++) {
					BlockPos cpos = new BlockPos(cx, cy, cz);
					Block cBlock = null;
					IBlockState cstate = world.getBlockState(cpos);
					if (cstate != air && (cBlock = cstate.getBlock()) != Blocks.leaves && 
						cBlock != Blocks.leaves2 && cBlock != Blocks.log) {
						nextY = true;
						break;
					}
				}
				
				if (nextY)
					break;
			}
			
			if (!nextY)
				break;
		}
		
		return cy;
	}
	
	public static int getGroundY(World world, int x, int y, int z, int range) {
		int cy = y - 1;
		boolean nextY = true;
		for (; cy >= minY; cy--) {
			nextY = false;
			for (int cz = z - range; cz <= z + range; cz++) {
				for (int cx = x - range; cx <= x + range; cx++) {
					BlockPos cpos = new BlockPos(cx, cy, cz);
					Block cBlock = null;
					IBlockState cstate = world.getBlockState(cpos);
					if (cstate == air || (cBlock = cstate.getBlock()) == Blocks.leaves ||
						cBlock == Blocks.leaves2 || cBlock == Blocks.log) {
						nextY = true;
						break;
					}
				}
				
				if (nextY)
					break;
			}
			
			if (!nextY)
				break;
		}
		
		return cy;
	}
}
