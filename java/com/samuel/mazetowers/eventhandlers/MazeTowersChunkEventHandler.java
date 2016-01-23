package com.samuel.mazetowers.eventhandlers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.primitives.Bytes;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.packets.PacketDebugMessage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MazeTowersChunkEventHandler {
	
	public MazeTowersChunkEventHandler() {
	}

	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onPopulateChunkEvent(PopulateChunkEvent.Post e) {
		int genCount;
		if (e.world.provider.getDimensionId() == 0 &&
			MazeTowers.mazeTowers.getChunksGenerated(e.world) <
			(genCount = MazeTowers.mazeTowers.getGenCount())) {
			for (int t = 0; t < genCount; t++) {
				if (MazeTowers.mazeTowers.getGenerated(t) ||
					!MazeTowers.mazeTowers.getSpawnPosLoaded(t))
					continue;
				BlockPos spawnPos = MazeTowers.mazeTowers.getSpawnPos(t);
				boolean usePos = false;
				
				if (e.chunkX == spawnPos.getX() >> 4 &&
					e.chunkZ == spawnPos.getZ() >> 4) {
					if (spawnPos.getY() > 0)
						usePos = MazeTowers.mazeTowers.addTower(e.world,
							e.chunkX, e.chunkZ, true);
					else
						usePos = false;
				}
			}
			e.chunkProvider.provideChunk(e.chunkX, e.chunkZ).setModified(true);
		}
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onChunkProviderEvent(PopulateChunkEvent.Pre e) {
		//MazeTowers.network.sendToDimension(new PacketDebugMessage("Chunk populated at (" +
			//e.chunkX + ", " + e.chunkZ + ")"), e.world.provider.getDimensionId());
		if (e.world.provider.getDimensionId() == 0 &&
			new Random().nextInt(256) == 0 && !e.hasVillageGenerated) {
			int chunkIndex;
			if ((chunkIndex = MazeTowers.mazeTowers.getSpawnPosLoadedCount(e.world)) <
				MazeTowers.mazeTowers.getGenCount() && MazeTowers.mazeTowers
				.getIsValidChunkCoord(e.chunkX, e.chunkZ)) {
				BlockPos spawnPos = new BlockPos(e.chunkX << 4, 49, e.chunkZ << 4);
				MazeTowers.mazeTowers.setSpawnPos(e.world, chunkIndex, spawnPos);
				/*byte[] biomeArray = e.getChunk().getBiomeArray();
				for (int i = 49; i < 256; i++) {
					biomeArray[i] = (byte) 219;
				}
				e.getChunk().setBiomeArray(biomeArray);*/
			}
			e.chunkProvider.provideChunk(e.chunkX, e.chunkZ).setModified(true);
		}
		
		//BlockPos spawnPos = ChaosBlock.chaosLabyrinth.getSpawnPos(e.world);
		//Chunk chunk = e.getChunk();
		/*if (e.world.provider.getDimensionId() == 0 && !spawnPos.equals(new BlockPos(-1, 1, -1)) && spawnPos.getY() > 0 && 
			(chunk.xPosition == spawnPos.getX() >> 4 ||
			chunk.xPosition == (spawnPos.getX() >> 4) + 1) &&
			(chunk.zPosition == spawnPos.getZ() >> 4 ||
			chunk.zPosition == (spawnPos.getZ() >> 4) + 1)) {*/
			/*byte chaosBiomeIndex = 86;
			byte[] biomeArray = new byte[256];
			Arrays.fill(biomeArray, chaosBiomeIndex);
			e.getChunk().setBiomeArray(biomeArray);*/
		//}
	}
}
