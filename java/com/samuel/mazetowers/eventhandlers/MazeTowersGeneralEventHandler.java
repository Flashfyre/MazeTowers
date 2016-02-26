package com.samuel.mazetowers.eventhandlers;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTowerBase;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MiniTower;

public class MazeTowersGeneralEventHandler {

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onBreakBlock(BlockEvent.BreakEvent e) {
		if (!e.world.isRemote
			&& !e.getPlayer().capabilities.isCreativeMode) {
			int dimId = e.world.provider.getDimensionId();
			EntityPlayer player = e.getPlayer();
			World world = player.getEntityWorld();
			BlockPos pos = e.pos;
			List<MazeTowerBase> towers = MazeTowers.mazeTowers
				.getTowers(dimId);
			if (towers == null)
				return;
			Iterator towerIterator = towers.iterator();
			Chunk chunk = world
				.getChunkFromBlockCoords(e.pos);
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
						if (coords[0] == 0)
							coords[0] = blockBreakabilityData.length - 1;
						if (coords[0] >= 0
							&& coords[0] < blockBreakabilityData.length
							&& !blockBreakabilityData[coords[0]][coords[1]]
								.get(coords[2]))
							e.setCanceled(true);
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
							return;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void OnHarvestDrops(HarvestDropsEvent e) {
		if (e.harvester != null) {
			final int dimId = e.world.provider
				.getDimensionId() + 1;
			Chunk chunk = e.world
				.getChunkFromBlockCoords(e.pos);
			if (MTUtils.getIsMazeTowerPos(dimId, e.pos)) {
				MazeTowerBase tower = MazeTowers.mazeTowers
					.getTowerAtCoords(e.world,
						chunk.xPosition, chunk.zPosition);
				BitSet[][] blockBreakabilityData = tower
					.getBlockBreakabilityData();
				try {
					int[] coords = tower
						.getCoordsFromPos(e.pos);
					if (coords[0] == -6)
						coords[0] = blockBreakabilityData.length - 1;
					if (coords[0] >= 0
						&& coords[0] < blockBreakabilityData.length
						&& !blockBreakabilityData[coords[0]][coords[1]]
							.get(coords[2]))
						e.drops.clear();
				} catch (ArrayIndexOutOfBoundsException e_) {
					e_ = null;
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onExplosionDetonate(
		ExplosionEvent.Detonate e) {
		if (!e.world.isRemote) {
			Map<Chunk, List<BlockPos>> chunkBlocks = new HashMap<Chunk, List<BlockPos>>();
			WorldGenMazeTowers mazeTowers = MazeTowers.mazeTowers;
			for (BlockPos pos : e.getAffectedBlocks()) {
				Chunk chunk = e.world
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
					.getTowerAtCoords(e.world,
						chunk.xPosition, chunk.zPosition);
				int[] coords = null;
				if (tower != null) {
					BitSet[][] blockBreakabilityData = tower
						.getBlockBreakabilityData();

					for (BlockPos pos : chunkBlocks
						.get(chunk)) {
						try {
							coords = tower
								.getCoordsFromPos(pos);
							if (coords[0] == -6)
								coords[0] = blockBreakabilityData.length - 1;
							if (coords[0] >= 0
								&& coords[0] < blockBreakabilityData.length
								&& !blockBreakabilityData[coords[0]][coords[1]]
									.get(coords[2]))
								toRemove.add(pos);
						} catch (ArrayIndexOutOfBoundsException e1) {
							e1 = null;
						}
					}
				} else if ((tower = mazeTowers
					.getTowerBesideCoords(e.world,
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

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onMobSpawn(LivingSpawnEvent.CheckSpawn e) {
		BlockPos pos = new BlockPos(e.x, e.y, e.z);
		if (!e.world.isRemote
			&& MTUtils.getIsMazeTowerPos(e.world.provider
				.getDimensionId(), pos)) {
			/*
			 * if (e.world.getBiomeGenForCoords(new BlockPos(e.x, e.y, e.z))
			 * instanceof BiomeGenMazeTowerLv7
			 */
			MazeTowerBase tower = MazeTowers.mazeTowers
				.getTowerAtCoords(e.world, pos.getX() >> 4,
					pos.getZ() >> 4);
			if (e.entityLiving instanceof EntitySkeleton) {
				((EntitySkeleton) e.entityLiving)
					.setCurrentItemOrArmor(0,
						new ItemStack(Items.stone_sword));
				((EntitySkeleton) e.entityLiving)
					.setSkeletonType(1);
			}
			try {
				if (pos.getY() - tower.baseY % 6 == 5)// CHANGE TO SPAWNPOS
													  // UPDATE AND GETY
					e.entityLiving.setDead();
			} catch (NullPointerException e_) {
				e_ = null;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onFOVUpdateEvent(FOVUpdateEvent e) {
		if (e.entity != null
			&& e.entity.isUsingItem()
			&& e.entity instanceof EntityPlayer
			&& e.entity.getHeldItem().getItem() == MazeTowers.ItemExplosiveBow) {
			EntityPlayer player = e.entity;
			int i = player.getItemInUseDuration();
			float f1 = (float) i / 20.0F;

			if (f1 > 1.0F) {
				f1 = 1.0F;
			} else {
				f1 *= f1;
			}

			e.newfov = e.fov * (1.0F - f1 * 0.15F);
		}
	}
}
