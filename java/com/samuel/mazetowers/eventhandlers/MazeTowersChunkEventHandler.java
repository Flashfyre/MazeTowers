package com.samuel.mazetowers.eventhandlers;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.world.WorldGenMazeTowers;

public class MazeTowersChunkEventHandler {

	@SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
	public void onPopulateChunkEvent(PopulateChunkEvent.Post e) {
	}

	@SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
	public void onChunkProviderEvent(
		PopulateChunkEvent.Pre e) {
		// MazeTowers.network.sendToDimension(new
		// PacketDebugMessage("Chunk populated at (" +
		// e.chunkX + ", " + e.chunkZ + ")"),
		// e.world.provider.getDimension());
		World world = e.getWorld();
		int dimId = world.provider.getDimension(),
		towerChance = e.getRand().nextInt(dimId == 0 ? 32
			: dimId == -1 ? 64 : 128);
		if ((Math.abs(e.getChunkX()) % 8 == 0 && (Math.abs(e.getChunkZ()) % 8 == 0) &&
			!e.isHasVillageGenerated()) && (dimId != 1 || e.getChunkX() != 0 || e.getChunkZ() != 0)) {
			WorldGenMazeTowers mt = MazeTowers.mazeTowers;
			int chunkIndex;
			if ((chunkIndex = mt.getSpawnPosLoadedCount(world)) <
				mt.getGenCount(dimId) && mt.getIsValidChunkCoord(world,
				e.getChunkX(), e.getChunkZ())) {
				BlockPos spawnPos = new BlockPos(
					e.getChunkX() << 4, 49, e.getChunkZ() << 4);
				mt.setSpawnPos(world, chunkIndex, spawnPos);
				/*
				 * byte[] biomeArray = e.getChunk().getBiomeArray(); for (int i
				 * = 49; i < 256; i++) { biomeArray[i] = (byte) 219; }
				 * e.getChunk().setBiomeArray(biomeArray);
				 */
			}
		}
	}
}
