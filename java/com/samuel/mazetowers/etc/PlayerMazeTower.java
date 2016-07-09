package com.samuel.mazetowers.etc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants.NBT;

public class PlayerMazeTower {
	
	public static class DefaultImpl implements IMazeTowerCapability {
		private final EntityPlayer player;
		private int floor;
		private int floors;
		private int chunkX;
		private int chunkZ;
		private int baseY;
		private int difficulty;
		private int rarity;
		private String towerName;
		private boolean isUnderground;
		private boolean enabled;
		private BlockPos spawnPos;
		private int[][] mtBounds;
	
		public DefaultImpl(EntityPlayer player, int floor,
			boolean enabled) {
			World world = player.getEntityWorld();
			this.player = player;
			this.floor = floor;
			this.enabled = enabled;
			isUnderground = false;
			floors = 0;
			chunkX = 0;
			chunkZ = 0;
			baseY = 0;
			difficulty = 0;
			rarity = 0;
			towerName = null;
			mtBounds = new int[0][6];
			
			if (world.provider.getDimension() == 0)
				setSpawnPos(new BlockPos(getPlayerSpawnPos(
					world, player)));
			else
				setSpawnPos(world.getSpawnPoint());
		}
	
		@Override
		public BlockPos getSpawnPos() {
			return spawnPos;
		}
	
		public BlockPos getPlayerSpawnPos(World worldIn,
			EntityPlayer player) {
			return player.playerLocation != null ? player.getBedLocation() : worldIn
				.getSpawnPoint();
		}
	
		@Override
		public boolean getEnabled() {
			return enabled;
		}
	
		@Override
		public int getFloor() {
			return floor;
		}
	
		@Override
		public boolean getIsUnderground() {
			return isUnderground;
		}
	
		@Override
		public int getDifficulty() {
			int floor = Math.min(!isUnderground ? this.floor
				: Math.abs(this.floor - 2), floors);
			int difficulty = this.difficulty
				+ ((int) Math.floor((floor - 1) / 5));
			return difficulty;
		}
	
		@Override
		public int getRarity() {
			int floor = Math.min(!isUnderground ? this.floor
				: Math.abs(this.floor - 2), floors);
			int rarity = this.rarity
				+ ((int) Math.floor((floor - 1) / 5));
			return rarity;
		}
	
		@Override
		public int[] getTowerData() {
			return new int[] { chunkX, baseY, chunkZ, floors,
				difficulty, rarity };
		}
	
		@Override
		public String getTowerName() {
			return towerName;
		}
		
		@Override
		public int[][] getMTBounds() {
			return mtBounds;
		}
	
		@Override
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	
		@Override
		public void setFloor(int floor) {
			this.floor = floor;
		}
	
		@Override
		public void setIsUnderground(boolean isUnderground) {
			this.isUnderground = isUnderground;
		}
		
		@Override
		public void setTowerData(int chunkX, int baseY,
			int chunkZ, int floors, int difficulty, int rarity) {
			this.chunkX = chunkX;
			this.baseY = baseY;
			this.chunkZ = chunkZ;
			this.floors = floors;
			this.difficulty = difficulty;
			this.rarity = rarity;
		}
	
		@Override
		public void setTowerData(int[] towerData) {
			this.chunkX = towerData[0];
			this.baseY = towerData[1];
			this.chunkZ = towerData[2];
			this.floors = towerData[3];
			this.difficulty = towerData[4];
			this.rarity = towerData[5];
		}
		
		@Override
		public void setTowerName(String towerName) {
			this.towerName = towerName;
		}

		@Override
		public void setSpawnPos(BlockPos spawnPos) {
			this.spawnPos = spawnPos;
		}

		@Override
		public void setMTBounds(int[][] mtBounds) {
			this.mtBounds = mtBounds;
		}
		
		public static class Storage implements IStorage<IMazeTowerCapability> {
			
			@Override
			public NBTBase writeNBT(Capability<IMazeTowerCapability> capability,
				IMazeTowerCapability instance, EnumFacing side) {
				NBTTagCompound properties = new NBTTagCompound();
				
				properties.setBoolean("enabled", instance.getEnabled());
				properties.setBoolean("isUnderground",
					instance.getIsUnderground());
				properties.setInteger("floor", instance.getFloor());
				properties.setLong("spawnPos", instance.getSpawnPos().toLong());
				
				int[][] mtBounds = instance.getMTBounds();
				NBTTagList mtBoundsList = new NBTTagList();
				
				for (int[] mtb : mtBounds) {
					mtBoundsList.appendTag(new NBTTagIntArray(mtb));
				}
				
				properties.setTag("mtBounds", mtBoundsList);
				
				return properties;
			}

			@Override
			public void readNBT(Capability<IMazeTowerCapability> capability,
					IMazeTowerCapability instance, EnumFacing side, NBTBase nbt) {
				NBTTagCompound properties = (NBTTagCompound) nbt;
				instance.setEnabled(properties.getBoolean("enabled"));
				instance.setIsUnderground(properties.getBoolean("isUnderground"));
				instance.setFloor(properties.getInteger("floor"));
				instance.setSpawnPos(BlockPos.fromLong(properties.getLong("spawnPos")));
				
				NBTTagList mtBoundsList = properties.getTagList("mtBounds", NBT.TAG_LIST);
				int[][] mtBounds = new int[mtBoundsList.tagCount()][6];
				
				for (int b = 0; b < mtBoundsList.tagCount(); b++) {
					mtBounds[b] = mtBoundsList.getIntArrayAt(b);
				}
				
				instance.setMTBounds(mtBounds);
			}
		}
	}
}