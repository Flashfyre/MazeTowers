package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.samuel.mazetowers.MazeTowers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MazeTowersData extends WorldSavedData {

	public int genCount;
	public BlockPos[] spawnPos;
	public boolean[] isGenerated;
	public boolean[] isUnderground;
	public int[][] towerData;
	public ArrayList<int[]>[] towerDataMini;
	public BitSet[][][] blockBreakabilityData;
	public ArrayList<BitSet[][][]>[] blockBreakabilityDataMini;
	public String[] towerTypeName;
	
	public MazeTowersData(String tagName) {
		super(tagName);
		genCount = MazeTowers.mazeTowers.getGenCount();
		spawnPos = new BlockPos[genCount];
		isGenerated = new boolean[genCount];
		isUnderground = new boolean[genCount];
		towerData = new int[genCount][5];
		towerDataMini = new ArrayList[genCount];
		blockBreakabilityData = new BitSet[genCount][][];
		blockBreakabilityDataMini = new ArrayList[genCount];
		towerTypeName = new String[genCount];
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList isGeneratedList = compound.getTagList("isGenerated", Constants.NBT.TAG_INT);
		for (int g = 0; g < isGeneratedList.tagCount(); g++) {
			isGenerated[g] = ((int) ((NBTTagInt) isGeneratedList.get(g)).getInt()) == 1;
			try {
			if (!isGenerated[g])
				break;
			isUnderground[g] = ((int) ((NBTTagInt) isGeneratedList.get(g)).getInt()) == 1;
			spawnPos[g] = BlockPos.fromLong(((long) ((NBTTagLong) compound.getTagList("spawnPos", Constants.NBT.TAG_LONG).get(g)).getLong()));
			towerData[g] = ((int[]) ((NBTTagIntArray) compound.getTagList("towerData", Constants.NBT.TAG_INT_ARRAY).get(g)).getIntArray());
			NBTTagList towerDataMiniList = ((NBTTagList) compound.getTagList("towerDataMini", Constants.NBT.TAG_LIST).get(g));
			towerDataMini[g] = new ArrayList<int[]>();
			for (int m = 0; m < towerDataMiniList.tagCount(); m++)
				towerDataMini[g].add((int[]) (((NBTTagIntArray) towerDataMiniList.get(m)).getIntArray()));
			towerTypeName[g] = ((String) ((NBTTagString) compound.getTagList("towerTypeName", Constants.NBT.TAG_STRING).get(g)).getString());
			NBTTagList bbdList = ((NBTTagList) compound.getTagList("blockBreakabilityData", Constants.NBT.TAG_LIST).get(g));
			int tagCount = bbdList.tagCount();
			blockBreakabilityData[g] = new BitSet[tagCount][];
			for (int y = 0; y < tagCount; y++) {
				NBTTagList bbdSublist = (NBTTagList) bbdList.get(y);
				int tagSubcount = bbdSublist.tagCount();
				blockBreakabilityData[g][y] = new BitSet[tagSubcount];
				for (int z = 0; z < tagSubcount; z++)
					blockBreakabilityData[g][y][z] = BitSet.valueOf((byte[]) ((NBTTagByteArray) bbdSublist.get(z)).getByteArray());
			}
			NBTTagList bbdmList = ((NBTTagList) compound.getTagList("blockBreakabilityDataMini", Constants.NBT.TAG_LIST).get(g));
			tagCount = bbdmList.tagCount();
			blockBreakabilityDataMini[g] = new ArrayList<BitSet[][][]>(tagCount);
			for (int m = 0; m < tagCount; m++) {
				NBTTagList bbdmSublist = (NBTTagList) bbdmList.get(m);
				int tagSubcount = bbdmSublist.tagCount();
				blockBreakabilityDataMini[g].add(m, new BitSet[tagSubcount][][]);
				for (int s = 0; s < tagSubcount; s++) {
					NBTTagList bbdmSublist2 = (NBTTagList) bbdmSublist.get(s);
					int tagSubcount2 = bbdmSublist2.tagCount();
					blockBreakabilityDataMini[g].get(m)[s] = new BitSet[tagSubcount2][];
					for (int y = 0; y < tagSubcount2; y++) {
						NBTTagList bbdmSublist3 = (NBTTagList) bbdmSublist2.get(y);
						int tagSubcount3 = bbdmSublist3.tagCount();
						blockBreakabilityDataMini[g].get(m)[s][y] = new BitSet[tagSubcount3];
						for (int z = 0; z < tagSubcount3; z++)
							blockBreakabilityDataMini[g].get(m)[s][y][z] =
								BitSet.valueOf((byte[]) ((NBTTagByteArray) bbdmSublist3
								.get(z)).getByteArray());
					}
				}
			}
			} catch (Exception e) {
				e = null;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList spawnPosSublist = new NBTTagList();
		NBTTagList isGeneratedSublist = new NBTTagList();
		NBTTagList isUndergroundSublist = new NBTTagList();
		NBTTagList towerDataSublist = new NBTTagList();
		NBTTagList towerDataMiniSublist = new NBTTagList();
		NBTTagList towerTypeNameSublist = new NBTTagList();
		NBTTagList bbdSublist = new NBTTagList();
		NBTTagList bbdmSublist = new NBTTagList();
		for (int g = 0; g < Math.min(genCount, MazeTowers.mazeTowers.getGenCount()); g++) {
			if (!MazeTowers.mazeTowers.getGenerated(g))
				break;
			NBTTagLong spawnPosTag;
			NBTTagInt isGeneratedTag;
			NBTTagInt isUndergroundTag;
			NBTTagIntArray towerDataTag;
			NBTTagList towerDataMiniTagList;
			NBTTagString towerTypeNameTag;
			NBTTagList bbdTagList;
			NBTTagList bbdmTagList;
			try {
				spawnPosTag = new NBTTagLong((long) spawnPos[g].toLong());
				spawnPosSublist.appendTag(spawnPosTag);
				isGeneratedTag = new NBTTagInt((int) (isGenerated[g] ? 1 : 0));
				isUndergroundTag = new NBTTagInt((int) (isUnderground[g] ? 1 : 0));
				isGeneratedSublist.appendTag(isGeneratedTag);
				towerDataTag = new NBTTagIntArray(towerData[g]);
				towerDataSublist.appendTag(towerDataTag);
				towerDataMiniTagList = new NBTTagList();
				if (towerDataMini[g] != null) {
					for (int m = 0; m < towerDataMini[g].size(); m++) {
						NBTTagIntArray towerDataMiniTag = new NBTTagIntArray(towerDataMini[g].get(m));
						towerDataMiniTagList.appendTag(towerDataMiniTag);
					}
					towerDataMiniSublist.appendTag(towerDataMiniTagList);
				}
				towerTypeNameTag = new NBTTagString(towerTypeName[g]);
				towerTypeNameSublist.appendTag(towerTypeNameTag);
				bbdTagList = new NBTTagList();
				for (int y = 0; y < blockBreakabilityData[g].length; y++) {
					NBTTagList bbdTagSublist = new NBTTagList();
					for (int z = 0; z < blockBreakabilityData[g][y].length; z++) {
						NBTTagByteArray bbdTag = new NBTTagByteArray(blockBreakabilityData[g][y][z].toByteArray());
						bbdTagSublist.appendTag(bbdTag);
					}	
					bbdTagList.appendTag(bbdTagSublist);
				}
				bbdSublist.appendTag(bbdTagList);
				bbdmTagList = new NBTTagList();
				for (int m = 0; m < blockBreakabilityDataMini[g].size(); m++) {
					NBTTagList bbdmTagSublist = new NBTTagList();
					for (int s = 0; s < blockBreakabilityDataMini[g].get(m).length; s++) {
						NBTTagList bbdmTagSublist2 = new NBTTagList();
						for (int y = 0; y < blockBreakabilityDataMini[g].get(m)[s].length; y++) {
							NBTTagList bbdmTagSublist3 = new NBTTagList();
							for (int z = 0; z < blockBreakabilityDataMini[g].get(m)[s][y].length; z++) {
								NBTTagByteArray bbdmTag = new NBTTagByteArray(
									blockBreakabilityDataMini[g].get(m)[s][y][z].toByteArray());
								bbdmTagSublist3.appendTag(bbdmTag);
							}
							bbdmTagSublist2.appendTag(bbdmTagSublist3);
						}
						bbdmTagSublist.appendTag(bbdmTagSublist2);
					}
					bbdmTagList.appendTag(bbdmTagSublist);
				}
				bbdmSublist.appendTag(bbdmTagList);
			} catch (NullPointerException e) {
				e = null;
			}
		}
		compound.setTag("spawnPos", spawnPosSublist);
		compound.setTag("isGenerated", isGeneratedSublist);
		compound.setTag("towerData", towerDataSublist);
		compound.setTag("towerDataMini", towerDataMiniSublist);
		compound.setTag("towerTypeName", towerTypeNameSublist);
		compound.setTag("blockBreakabilityData", bbdSublist);
		compound.setTag("blockBreakabilityDataMini", bbdmSublist);
	}
	
	public boolean getIsGenerated(int index) {
		return isGenerated[index];
	}
	
	public BlockPos getSpawnPoint(int index) {
		return spawnPos[index];
	}
	
	public boolean getIsUnderground(int index) {
		return isUnderground[index];
	}
	
	public int[] getTowerData(int index) {
		return towerData[index];
	}
	
	public List<int[]> getTowerDataMini(int index) {
		return towerDataMini[index];
	}
	
	public String getTowerTypeName(int index) {
		return towerTypeName[index];
	}
	
	public BitSet[][] getBlockBreakabilityData(int index) {
		return blockBreakabilityData[index];
	}
	
	public List<BitSet[][][]> getBlockBreakabilityDataMini(int index) {
		return blockBreakabilityDataMini[index];
	}
	
	public void setSpawnPoint(int spawnX, int spawnY, int spawnZ, int index) {
		this.spawnPos[index] = new BlockPos(spawnX, spawnY, spawnZ);
		this.markDirty();
	}

	public void setIsGenerated(boolean isGenerated, int index) {
		this.isGenerated[index] = isGenerated;
		this.markDirty();
	}
	
	public void setIsUnderground(boolean isUnderground, int index) {
		this.isUnderground[index] = isUnderground;
		this.markDirty();
	}
	
	public void setTowerData(int chunkX, int baseY, int chunkZ, int floors,
		int difficulty, int index) {
		this.towerData[index] = new int[] { chunkX, baseY, chunkZ, floors, difficulty };
		this.markDirty();
	}
	
	public void setMiniTowerData(int[] bounds, int index, int indexMini) {
		if (this.towerDataMini[index] == null)
			this.towerDataMini[index] = new ArrayList<int[]>();
		if (this.towerDataMini[index].size() == indexMini)
			this.towerDataMini[index].add(indexMini, bounds);
		else
			this.towerDataMini[index].set(indexMini, bounds);
		this.markDirty();
	}
	
	public void setTowerTypeName(String name, int index) {
		this.towerTypeName[index] = name;
		this.markDirty();
	}
	
	public void setBlockBreakabilityData(BitSet[][] data, int index) {
		this.blockBreakabilityData[index] = data;
		this.markDirty();
	}
	
	public void setBlockBreakabilityDataMini(BitSet[][][] data, int index, int indexMini) {
		if (this.blockBreakabilityDataMini[index] == null)
			this.blockBreakabilityDataMini[index] = new ArrayList<BitSet[][][]>();
		if (this.blockBreakabilityDataMini[index].size() == indexMini)
			this.blockBreakabilityDataMini[index].add(indexMini, data);
		else
			this.blockBreakabilityDataMini[index].set(indexMini, data);
		this.markDirty();
	}
}
