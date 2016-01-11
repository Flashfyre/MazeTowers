package com.samuel.mazetowers.eventhandlers;

import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.samuel.mazetowers.MazeTowers;

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
		if (e.world.provider.getDimensionId() == 0 && !e.world.isRemote &&
			MazeTowers.mazeTowers.getChunksGenerated(e.world) <
			MazeTowers.mazeTowers.getGenCount()) {
			for (int t = 0; t < MazeTowers.mazeTowers.getGenCount(); t++) {
				BlockPos spawnPos = MazeTowers.mazeTowers.getSpawnPos(e.world, t);
				boolean usePos = false;
				
				if (e.chunkX == spawnPos.getX() >> 4 &&
					e.chunkZ == spawnPos.getZ() >> 4) {
					if (spawnPos.getY() > 0)
						usePos = MazeTowers.mazeTowers.addTower(e.world,
							e.chunkX, e.chunkZ, true);
					else
						usePos = false;
						
					if (!usePos) {
						usePos = false;
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onChunkProviderEvent(ChunkEvent.Load e) {
		if (e.world.provider.getDimensionId() == 0 && !e.world.isRemote) {
			if (e.world.rand.nextInt(32) == 0) {
				int chunkIndex;
				if ((chunkIndex = MazeTowers.mazeTowers.getSpawnPosLoadedCount()) <
					MazeTowers.mazeTowers.getGenCount(e.world)) {
					Chunk chunk = e.getChunk();
					BlockPos spawnPos = new BlockPos(chunk.xPosition << 4, 49,
						chunk.zPosition << 4);
					MazeTowers.mazeTowers.setSpawnPos(e.world, chunkIndex, spawnPos);
					byte[] biomeArray = e.getChunk().getBiomeArray();
					for (int i = 49; i < 256; i++) {
						biomeArray[i] = (byte) 219;
					}
					e.getChunk().setBiomeArray(biomeArray);
				}
			}
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
