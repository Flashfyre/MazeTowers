package com.samuel.mazetowers.eventhandlers;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.world.WorldGenMazeTowers;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MiniTower;

public class MazeTowersBlockProtectionEventHandler {
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onBreakBlock(BlockEvent.BreakEvent e) {
		if (!e.getWorld().isRemote) {
			if (MazeTowers.blockProtection &&
				!e.getPlayer().capabilities.isCreativeMode) {
    			int dimId = e.getWorld().provider.getDimension();
    			EntityPlayer player = e.getPlayer();
    			World world = player.getEntityWorld();
    			BlockPos pos = e.getPos();
    			List<MazeTowerBase> towers = MazeTowers.mazeTowers
    				.getTowers(dimId);
    			if (towers == null)
    				return;
    			Iterator towerIterator = towers.iterator();
    			Chunk chunk = world
    				.getChunkFromBlockCoords(e.getPos());
    			while (towerIterator.hasNext()) {
    				final MazeTowerBase tower = (MazeTowerBase) towerIterator
    					.next();
    				final Chunk towerChunk = tower
    					.getChunk(world);
    				if (chunk.equals(towerChunk)) {
    					BitSet[][] blockBreakabilityData = tower
    						.getBlockBreakabilityData();
    					try {
    						int[] coords = tower
    							.getCoordsFromPos(pos);
    						if (coords[0] == -6)
    							coords[0] = blockBreakabilityData.length - 1;
    						if (coords[0] >= 0
    							&& coords[0] < blockBreakabilityData.length
    							&& !blockBreakabilityData[coords[0]][coords[1]]
    								.get(coords[2])) {
    								e.setCanceled(true);
    						}
    					} catch (ArrayIndexOutOfBoundsException e_) {
    						e_ = null;
    					}
    					break;
    				} else if ((chunk.xPosition == towerChunk.xPosition + 1
    					|| chunk.xPosition == towerChunk.xPosition - 1 || chunk.xPosition == towerChunk.xPosition)
    					&& (chunk.zPosition == towerChunk.zPosition + 1
    						|| chunk.zPosition == towerChunk.zPosition - 1 || chunk.zPosition == towerChunk.zPosition)) {
    					for (MiniTower mt : tower
    						.getMiniTowers()) {
    						if (!mt.getPosBreakability(pos)) {
    							e.setCanceled(true);
    							break;
    						}
    					}
    				}
    			}
    			if (e.isCanceled()) {
    				BlockPos particlePos = pos.add(Math.min(Math.max((player.posX - pos.getX()) * 0.25d, -1.0d), 1.0d), 0.0d,
    					Math.min(Math.max((player.posZ - pos.getZ()) * 0.25d, -1.0d), 1.0d));
    				((WorldServer) world).spawnParticle(EnumParticleTypes.BARRIER,
    					EnumParticleTypes.BARRIER.getShouldIgnoreRange(),
    					(int) pos.getX() + 0.5D, (int) pos.getY() + 0.5D, (int) pos.getZ() + 0.5D, 1,
    					0.0f, 0.0f, 0.0f, 0.0D, new int[0]);
    			}
    		}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onExplosionDetonate(
		ExplosionEvent.Detonate e) {
		if (!e.getWorld().isRemote && MazeTowers.blockProtection) {
			Map<Chunk, List<BlockPos>> chunkBlocks = new HashMap<Chunk, List<BlockPos>>();
			WorldGenMazeTowers mazeTowers = MazeTowers.mazeTowers;
			for (BlockPos pos : e.getAffectedBlocks()) {
				Chunk chunk = e.getWorld()
					.getChunkFromBlockCoords(pos);
				if (chunkBlocks.containsKey(chunk))
					chunkBlocks.get(chunk).add(pos);
				else {
					List<BlockPos> posList = new ArrayList<BlockPos>();
					posList.add(pos);
					chunkBlocks.put(chunk, posList);
				}
			}
			List<BlockPos> toRemove = new ArrayList<BlockPos>();
			for (Chunk chunk : chunkBlocks.keySet()) {
				MazeTowerBase tower = mazeTowers
					.getTowerAtCoords(e.getWorld(),
						chunk.xPosition, chunk.zPosition);
				int[] coords = null;
				if (tower != null) {
					BitSet[][] blockBreakabilityData = tower
						.getBlockBreakabilityData();
					
					if (blockBreakabilityData[0] == null)
						return;

					for (BlockPos pos : chunkBlocks
						.get(chunk)) {
						try {
							coords = tower
								.getCoordsFromPos(pos);
							if (coords[0] == -6)
								coords[0] = blockBreakabilityData.length - 1;
							if (coords[0] >= 0 && coords[0] < blockBreakabilityData.length
								&& !blockBreakabilityData[coords[0]][coords[1]].get(coords[2]))
								toRemove.add(pos);
						} catch (ArrayIndexOutOfBoundsException e1) {
							e1.printStackTrace();
						} catch (NullPointerException e1) {
							e1.printStackTrace();
						}
					}
				} else if ((tower = mazeTowers
					.getTowerBesideCoords(e.getWorld(),
						chunk.xPosition, chunk.zPosition)) != null) {
					for (BlockPos pos : chunkBlocks
						.get(chunk)) {
						for (MiniTower mt : tower
							.getMiniTowers()) {
							if (!mt.getPosBreakability(pos)) {
								toRemove.add(pos);
								break;
							}
						}
					}
				}
			}

			e.getAffectedBlocks().removeAll(toRemove);
		}
	}
}
