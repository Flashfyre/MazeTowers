package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.packets.PacketDebugMessage;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MiniTower;

public class MTDebugHelper {
	
	public static void validateMiniTowerBounds(List<MiniTower> miniTowers) {
		List<int[]> mtBoundsList = new ArrayList<int[]>();
		int size = miniTowers.size();
		
		for (int m = 0; m < size; m++) {
			MiniTower mt = miniTowers.get(m);
			for (int b = m + 1; b < size; b++) {
				MiniTower mt2 = miniTowers.get(b);
				int[] bounds = mt2.getActualBounds();
				BlockPos[] cornerPos = new BlockPos[] { new BlockPos(bounds[0], bounds[1], bounds[2]),
					new BlockPos(bounds[3], bounds[1], bounds[2]), new BlockPos(bounds[0], bounds[4], bounds[2]),
					new BlockPos(bounds[0], bounds[4], bounds[5]), new BlockPos(bounds[3], bounds[4], bounds[2]),
					new BlockPos(bounds[3], bounds[1], bounds[5]), new BlockPos(bounds[0], bounds[4], bounds[5]),
					new BlockPos(bounds[3], bounds[4], bounds[5]) };
				
				for (BlockPos c : cornerPos) {
					if (mt.getPosInBounds(c)) {
						MazeTowers.network.sendToAll(new PacketDebugMessage("Found Mini Tower overlap at " +
							mtBoundsList.get(m).toString() + " and " + bounds.toString()));
						break;
					}
				}
			}
		}
	}
}
