package com.samuel.mazetowers.eventhandlers;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.tileentities.TileEntityBlockProtect;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTowerBase;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MiniTower;
import com.samuel.mazetowers.worldgen.biomes.BiomeGenMazeTowerLv7;

public class MazeTowersGeneralEventHandler {
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onBreakBlock(BlockEvent.BreakEvent e) {
		if (!e.world.isRemote && !e.getPlayer().capabilities.isCreativeMode) {
			EntityPlayer player = e.getPlayer();
			World world = player.getEntityWorld();
			BlockPos pos = e.pos;
			List<MazeTowerBase> towers = MazeTowers.mazeTowers.getTowers();
			if (towers == null)
				return;
			Iterator towerIterator = towers.iterator();
			Chunk chunk = world.getChunkFromBlockCoords(e.pos);
			while (towerIterator.hasNext()) {
				final MazeTowerBase tower = (MazeTowerBase) towerIterator.next();
				final Chunk towerChunk = tower.getChunk(world);
				if (chunk.equals(towerChunk)) {
					int y = pos.getY();
					int baseY = tower.baseY;
					boolean isUnbreakable = false;
					IBlockState state;
					Block block;
					BitSet[][] blockBreakabilityData = tower.getBlockBreakabilityData();
					try {
					int[] coords = tower.getCoordsFromPos(pos);
					if (coords[0] >= 0 && coords[0] <= blockBreakabilityData.length &&
						!blockBreakabilityData[coords[0]][coords[1]].get(coords[2])
						/*|| (state =
						tower.getBlockBreakabilityData()[pos.getY() - baseY]
						[(pos.getZ() % 16) + (pos.getZ() > -1 ? 0 : 16)]
						[(pos.getX() % 16) + (pos.getX() > -1 ? 0 : 16)]) == null) ||
						(isUnbreakable = ((block = state.getBlock()) != Blocks.air &&
						block != Blocks.torch) && block != Blocks.glass &&
						block != Blocks.web && block != Blocks.chest &&
						block != Blocks.trapped_chest && block != Blocks.mob_spawner*/)
						e.setCanceled(true);
					} catch (ArrayIndexOutOfBoundsException e_) {
						e_ = null;
					}
					break;
				} else if ((chunk.xPosition == towerChunk.xPosition + 1 ||
					chunk.xPosition == towerChunk.xPosition - 1 ||
					chunk.xPosition == towerChunk.xPosition) &&
					(chunk.zPosition == towerChunk.zPosition + 1 ||
					chunk.zPosition == towerChunk.zPosition - 1 ||
					chunk.zPosition == towerChunk.zPosition)) {
					for (MiniTower mt : tower.getMiniTowers()) {
						if (!mt.getPosBreakability(pos)) {
							e.setCanceled(true);
							return;
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onMobSpawn(LivingSpawnEvent.CheckSpawn e) {
		BlockPos pos = new BlockPos(e.x, e.y, e.z);
		if (!e.world.isRemote && MTUtils.getIsMazeTowerPos(pos)) {
			/*if (e.world.getBiomeGenForCoords(new BlockPos(e.x, e.y, e.z))
				instanceof BiomeGenMazeTowerLv7*/
			MazeTowerBase tower = MazeTowers.mazeTowers.getTowerAtCoords(e.world,
				pos.getX() >> 4, pos.getZ() >> 4);
			if (e.entityLiving instanceof EntitySkeleton) {
				((EntitySkeleton) e.entityLiving).setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
				((EntitySkeleton) e.entityLiving).setSkeletonType(1);
			}
			try {
				if (pos.getY() - tower.baseY % 6 == 5)//CHANGE TO SPAWNPOS UPDATE AND GETY
					e.entityLiving.setDead();
			} catch (NullPointerException e_) {
				e_ = null;
			}
		}
	}
}
