package com.samuel.mazetowers.eventhandlers;

import net.minecraft.util.BlockPos;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers;

public class MazeTowersChunkEventHandler {

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onPopulateChunkEvent(
		PopulateChunkEvent.Post e) {
		int genCount, dimId = e.world.provider
			.getDimensionId();
		if (MazeTowers.mazeTowers
			.getChunksGenerated(e.world) < (genCount = MazeTowers.mazeTowers
			.getGenCount(dimId))) {
			for (int t = 0; t < genCount; t++) {
				WorldGenMazeTowers mt = MazeTowers.mazeTowers;
				if (mt.getGenerated(dimId, t)
					|| !mt.getSpawnPosLoaded(dimId, t))
					continue;
				BlockPos spawnPos = MazeTowers.mazeTowers
					.getSpawnPos(dimId, t);
				boolean usePos = false;

				if (e.chunkX == spawnPos.getX() >> 4
					&& e.chunkZ == spawnPos.getZ() >> 4) {
					if (spawnPos.getY() > 0)
						usePos = MazeTowers.mazeTowers
							.addTower(e.world, e.chunkX,
								e.chunkZ, true);
					else
						usePos = false;
				}
			}
			e.chunkProvider
				.provideChunk(e.chunkX, e.chunkZ)
				.setModified(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onChunkProviderEvent(
		PopulateChunkEvent.Pre e) {
		// MazeTowers.network.sendToDimension(new
		// PacketDebugMessage("Chunk populated at (" +
		// e.chunkX + ", " + e.chunkZ + ")"),
		// e.world.provider.getDimensionId());
		int dimId = e.world.provider.getDimensionId();
		if (e.rand.nextInt(dimId == 0 ? 256
			: dimId == -1 ? 64 : 32) == 0
			&& !e.hasVillageGenerated) {
			WorldGenMazeTowers mt = MazeTowers.mazeTowers;
			int chunkIndex;
			if ((chunkIndex = mt
				.getSpawnPosLoadedCount(e.world)) < mt
				.getGenCount(dimId)
				&& mt.getIsValidChunkCoord(e.chunkX,
					e.chunkZ, dimId)) {
				BlockPos spawnPos = new BlockPos(
					e.chunkX << 4, 49, e.chunkZ << 4);
				mt.setSpawnPos(e.world, chunkIndex,
					spawnPos);
				/*
				 * byte[] biomeArray = e.getChunk().getBiomeArray(); for (int i
				 * = 49; i < 256; i++) { biomeArray[i] = (byte) 219; }
				 * e.getChunk().setBiomeArray(biomeArray);
				 */
			}
			e.chunkProvider
				.provideChunk(e.chunkX, e.chunkZ)
				.setModified(true);
		}
	}
}
