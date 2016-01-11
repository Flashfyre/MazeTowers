package com.samuel.mazetowers.eventhandlers;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTower;
import com.samuel.mazetowers.worldgen.biomes.BiomeGenMazeTowerLv7;

public class MazeTowersGeneralEventHandler {
	
	public MazeTowersGeneralEventHandler() {
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onBreakBlock(BlockEvent.BreakEvent e) {
		if (!e.getPlayer().capabilities.isCreativeMode) {
			World world = e.getPlayer().getEntityWorld();
			BlockPos pos = e.pos;
			List<MazeTower> towers = MazeTowers.mazeTowers.getTowers();
			Iterator towerIterator = towers.iterator();
			while (towerIterator.hasNext()) {
				MazeTower tower = (MazeTower) towerIterator.next();
				if (e.getPlayer().worldObj.getChunkFromBlockCoords(pos)
						.equals(tower.getChunk(world))) {
					int y = pos.getY();
					int baseY = tower.baseY;
					IBlockState state;
					Block block;
					if (y >= baseY && y <= baseY + ((tower.floors + 1) * 6) &&
						((y > (baseY + (tower.floors * 6)) || (state = tower.getBlockData()[pos.getY() - baseY]
						[(pos.getZ() % 16) + (pos.getZ() > -1 ? 0 : 16)]
						[pos.getX() % 16 + (pos.getX() > -1 ? 0 : 16)]) == null) ||
						(state != Blocks.air && (block = state.getBlock()) != Blocks.torch) &&
						block != Blocks.glass &&
						block != Blocks.web && block != Blocks.chest &&
						block != Blocks.trapped_chest && block != Blocks.mob_spawner))
						e.setCanceled(true);
					break;
				}
			}
			
			/*if (!e.isCanceled() && e.state.getBlock() instanceof BlockItemScanner) {
				TileEntityItemScanner te = (TileEntityItemScanner) e.world.getTileEntity(e.pos);
				if (!te.getOwnerName().equals(e.getPlayer().getDisplayNameString())) {
					e.setCanceled(true);
					e.getPlayer().addChatMessage(new ChatComponentText("You may not destroy "
					+ "an Item Scanner that is not yours"));
				}
			}*/
		}
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onMobSpawn(LivingSpawnEvent.CheckSpawn e) {
		if (!e.world.isRemote &&
			e.entityLiving instanceof EntitySkeleton) { /*&&
			e.world.getBiomeGenForCoords(new BlockPos(e.x, e.y, e.z))
			instanceof BiomeGenMazeTowerLv7*/
			((EntitySkeleton) e.entityLiving).setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
			((EntitySkeleton) e.entityLiving).setSkeletonType(1);
		}
	}
}
