package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.world.WorldGenMazeTowers;

public class MazeTowersData extends WorldSavedData {

	public int[] genCount;
	public BlockPos[][] spawnPos;
	public boolean[][] isGenerated;
	public boolean[][] isUnderground;
	public int[][][] towerData;
	public ArrayList<int[]>[][] towerDataMini;
	public BitSet[][][][] blockBreakabilityData;
	public ArrayList<BitSet[][][]>[][] blockBreakabilityDataMini;

	public MazeTowersData(String tagName) {
		super(tagName);
		WorldGenMazeTowers mt = MazeTowers.mazeTowers;
		genCount = new int[] { mt.getGenCount(-1),
			mt.getGenCount(0), mt.getGenCount(1) };
		spawnPos = new BlockPos[3][];
		isGenerated = new boolean[3][];
		isUnderground = new boolean[3][];
		towerData = new int[3][][];
		towerDataMini = new ArrayList[3][];
		blockBreakabilityData = new BitSet[3][][][];
		blockBreakabilityDataMini = new ArrayList[3][];
		for (int d = 0; d < 3; d++) {
			final int genCountD = genCount[d];
			spawnPos[d] = new BlockPos[genCountD];
			isGenerated[d] = new boolean[genCountD];
			isUnderground[d] = new boolean[genCountD];
			towerData[d] = new int[genCountD][5];
			towerDataMini[d] = new ArrayList[genCountD];
			blockBreakabilityData[d] = new BitSet[genCountD][][];
			blockBreakabilityDataMini[d] = new ArrayList[genCountD];
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		for (int d = 0; d < 3; d++) {
			if (genCount[d] != 0) {
				NBTTagList spawnPosList = (NBTTagList) compound
					.getTagList("spawnPos",
						Constants.NBT.TAG_LIST).get(d);
				NBTTagList isGeneratedList = (NBTTagList) compound
					.getTagList("isGenerated",
						Constants.NBT.TAG_LIST).get(d);
				NBTTagList isUndergroundList = (NBTTagList) compound
					.getTagList("isUnderground",
						Constants.NBT.TAG_LIST).get(d);
				NBTTagList towerDataList = (NBTTagList) compound
					.getTagList("towerData",
						Constants.NBT.TAG_LIST).get(d);
				NBTTagList towerDataMiniList = (NBTTagList) compound
					.getTagList("towerDataMini",
						Constants.NBT.TAG_LIST).get(d);
				NBTTagList bbdList = (NBTTagList) compound
					.getTagList("blockBreakabilityData",
						Constants.NBT.TAG_LIST).get(d);
				NBTTagList bbdmList = (NBTTagList) compound
					.getTagList("blockBreakabilityDataMini",
						Constants.NBT.TAG_LIST).get(d);
				for (int g = 0; g < isGeneratedList
					.tagCount(); g++) {
					isGenerated[d][g] = (((NBTTagInt) isGeneratedList
						.get(g)).getInt()) == 1;
					if (!isGenerated[d][g])
						continue;
					spawnPos[d][g] = BlockPos
						.fromLong((((NBTTagLong) spawnPosList
							.get(g)).getLong()));
					isUnderground[d][g] = (((NBTTagInt) isUndergroundList
						.get(g)).getInt()) == 1;
					towerData[d][g] = (((NBTTagIntArray) towerDataList
						.get(g)).getIntArray());
					NBTTagList towerDataMiniSublist = null;
					try {
						towerDataMiniSublist = ((NBTTagList) towerDataMiniList
							.get(g));
						towerDataMini[d][g] = new ArrayList<int[]>();
						for (int m = 0; m < towerDataMiniSublist
							.tagCount(); m++)
							towerDataMini[d][g]
								.add((((NBTTagIntArray) towerDataMiniSublist
									.get(m)).getIntArray()));
					} catch (ClassCastException e) {
						e.printStackTrace();
					}
					NBTTagList bbdSublist = ((NBTTagList) bbdList
						.get(g));
					int tagCount = bbdSublist
						.tagCount();
					try {
						blockBreakabilityData[d][g] = new BitSet[tagCount][];
						for (int y = 0; y < tagCount; y++) {
							NBTTagList bbdSublist2 = (NBTTagList) bbdSublist
								.get(y);
							int tagSubcount = bbdSublist2
								.tagCount();
							blockBreakabilityData[d][g][y] = new BitSet[tagSubcount];
							for (int z = 0; z < tagSubcount; z++)
								blockBreakabilityData[d][g][y][z] = BitSet
									.valueOf(((NBTTagByteArray) bbdSublist2
										.get(z))
										.getByteArray());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					NBTTagList bbdmSublist = ((NBTTagList) bbdmList
						.get(g));
					tagCount = bbdmSublist.tagCount();
					blockBreakabilityDataMini[d][g] = new ArrayList<BitSet[][][]>(
						tagCount);
					try {
						for (int m = 0; m < tagCount; m++) {
							NBTTagList bbdmSublist2 = (NBTTagList) bbdmSublist
								.get(m);
							int tagSubcount = bbdmSublist2
								.tagCount();
							blockBreakabilityDataMini[d][g]
								.add(
									m,
									new BitSet[tagSubcount][][]);
							for (int s = 0; s < tagSubcount; s++) {
								NBTTagList bbdmSublist3 = (NBTTagList) bbdmSublist2
									.get(s);
								int tagSubcount2 = bbdmSublist3
									.tagCount();
								blockBreakabilityDataMini[d][g]
									.get(m)[s] = new BitSet[tagSubcount2][];
								for (int y = 0; y < tagSubcount2; y++) {
									NBTTagList bbdmSublist4 = (NBTTagList) bbdmSublist3
										.get(y);
									int tagSubcount3 = bbdmSublist4
										.tagCount();
									blockBreakabilityDataMini[d][g]
										.get(m)[s][y] = new BitSet[tagSubcount3];
									for (int z = 0; z < tagSubcount3; z++)
										blockBreakabilityDataMini[d][g]
											.get(m)[s][y][z] = BitSet
											.valueOf(((NBTTagByteArray) bbdmSublist4
												.get(z))
												.getByteArray());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList spawnPosList = new NBTTagList();
		NBTTagList isGeneratedList = new NBTTagList();
		NBTTagList isUndergroundList = new NBTTagList();
		NBTTagList towerDataList = new NBTTagList();
		NBTTagList towerDataMiniList = new NBTTagList();
		NBTTagList bbdList = new NBTTagList();
		NBTTagList bbdmList = new NBTTagList();
		for (int d = 0; d < 3; d++) {
			NBTTagList spawnPosSublist = new NBTTagList();
			NBTTagList isGeneratedSublist = new NBTTagList();
			NBTTagList isUndergroundSublist = new NBTTagList();
			NBTTagList towerDataSublist = new NBTTagList();
			NBTTagList towerDataMiniSublist = new NBTTagList();
			NBTTagList bbdSublist = new NBTTagList();
			NBTTagList bbdmSublist = new NBTTagList();
			if (genCount[d] != 0) {
				for (int g = 0; g < Math.min(genCount[d],
					MazeTowers.mazeTowers
						.getGenCount(d - 1)); g++) {
					if (!MazeTowers.mazeTowers
						.getGenerated(d - 1, g))
						continue;
					NBTTagLong spawnPosTag;
					NBTTagInt isGeneratedTag;
					NBTTagInt isUndergroundTag;
					NBTTagIntArray towerDataTag;
					NBTTagList towerDataMiniTagList;
					NBTTagList bbdTagList;
					NBTTagList bbdmTagList;
					try {
						spawnPosTag = new NBTTagLong(
							spawnPos[d][g].toLong());
						spawnPosSublist
							.appendTag(spawnPosTag);
						isGeneratedTag = new NBTTagInt(
							isGenerated[d][g] ? 1
								: 0);
						isGeneratedSublist
							.appendTag(isGeneratedTag);
						isUndergroundTag = new NBTTagInt(
							(isUnderground[d][g] ? 1 : 0));
						isUndergroundSublist
							.appendTag(isUndergroundTag);
						towerDataTag = new NBTTagIntArray(
							towerData[d][g]);
						towerDataSublist
							.appendTag(towerDataTag);
						towerDataMiniTagList = new NBTTagList();
						if (towerDataMini[d][g] != null) {
							for (int m = 0; m < towerDataMini[d][g]
								.size(); m++) {
								NBTTagIntArray towerDataMiniTag = new NBTTagIntArray(
									towerDataMini[d][g]
										.get(m));
								towerDataMiniTagList
									.appendTag(towerDataMiniTag);
							}
							towerDataMiniSublist
								.appendTag(towerDataMiniTagList);
						}
						bbdTagList = new NBTTagList();
						if (blockBreakabilityData[d][g] != null) {
							for (int y = 0; y < blockBreakabilityData[d][g].length; y++) {
								NBTTagList bbdTagSublist = new NBTTagList();
								for (int z = 0; z < blockBreakabilityData[d][g][y].length; z++) {
									NBTTagByteArray bbdTag = new NBTTagByteArray(
										blockBreakabilityData[d][g][y][z]
											.toByteArray());
									bbdTagSublist
										.appendTag(bbdTag);
								}
								bbdTagList
									.appendTag(bbdTagSublist);
							}
						}
						bbdSublist.appendTag(bbdTagList);
						bbdmTagList = new NBTTagList();
						if (blockBreakabilityDataMini[d][g] != null) {
							for (int m = 0; m < blockBreakabilityDataMini[d][g]
								.size(); m++) {
								NBTTagList bbdmTagSublist = new NBTTagList();
								if (blockBreakabilityDataMini[d][g]
									.get(m) != null) {
									for (int s = 0; s < blockBreakabilityDataMini[d][g]
										.get(m).length; s++) {
										NBTTagList bbdmTagSublist2 = new NBTTagList();
										if (blockBreakabilityDataMini[d][g]
											.get(m)[s] != null) {
											for (int y = 0; y < blockBreakabilityDataMini[d][g]
												.get(m)[s].length; y++) {
												NBTTagList bbdmTagSublist3 = new NBTTagList();
												for (int z = 0; z < blockBreakabilityDataMini[d][g]
													.get(m)[s][y].length; z++) {
													NBTTagByteArray bbdmTag = new NBTTagByteArray(
														blockBreakabilityDataMini[d][g]
															.get(m)[s][y][z]
															.toByteArray());
													bbdmTagSublist3
														.appendTag(bbdmTag);
												}
												bbdmTagSublist2
													.appendTag(bbdmTagSublist3);
											}
										}
										bbdmTagSublist
											.appendTag(bbdmTagSublist2);
									}
								}
								bbdmTagList
									.appendTag(bbdmTagSublist);
							}
						}
						bbdmSublist.appendTag(bbdmTagList);
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}
				spawnPosList.appendTag(spawnPosSublist);
				isGeneratedList
					.appendTag(isGeneratedSublist);
				isUndergroundList
					.appendTag(isUndergroundSublist);
				towerDataList.appendTag(towerDataSublist);
				towerDataMiniList
					.appendTag(towerDataMiniSublist);
				bbdList.appendTag(bbdSublist);
				bbdmList.appendTag(bbdmSublist);
			}
		}

		compound.setTag("spawnPos", spawnPosList);
		compound.setTag("isGenerated", isGeneratedList);
		compound.setTag("isUnderground", isUndergroundList);
		compound.setTag("towerData", towerDataList);
		compound.setTag("towerDataMini", towerDataMiniList);
		compound.setTag("blockBreakabilityData", bbdList);
		compound.setTag("blockBreakabilityDataMini", bbdmList);
		
		return compound;
	}

	public boolean[][] getIsGenerated() {
		return isGenerated;
	}

	public boolean getIsGenerated(int dimId, int index) {
		return isGenerated[dimId][index];
	}

	public BlockPos[][] getSpawnPoint() {
		return spawnPos;
	}

	public BlockPos getSpawnPoint(int dimId, int index) {
		return spawnPos[dimId][index];
	}

	public boolean[][] getIsUnderground() {
		return isUnderground;
	}

	public boolean getIsUnderground(int dimId, int index) {
		return isUnderground[dimId][index];
	}

	public int[][][] getTowerData() {
		return towerData;
	}

	public int[] getTowerData(int dimId, int index) {
		return towerData[dimId][index];
	}

	public List<int[]> getTowerDataMini(int dimId, int index) {
		return towerDataMini[dimId][index];
	}

	public BitSet[][] getBlockBreakabilityData(int dimId,
		int index) {
		return blockBreakabilityData[dimId][index];
	}

	public List<BitSet[][][]> getBlockBreakabilityDataMini(
		int dimId, int index) {
		return blockBreakabilityDataMini[dimId][index];
	}

	public void setSpawnPoint(int spawnX, int spawnY,
		int spawnZ, int dimId, int index) {
		this.spawnPos[dimId][index] = new BlockPos(spawnX,
			spawnY, spawnZ);
		this.markDirty();
	}

	public void setIsGenerated(boolean isGenerated,
		int dimId, int index) {
		this.isGenerated[dimId][index] = isGenerated;
		this.markDirty();
	}

	public void setIsUnderground(boolean isUnderground,
		int dimId, int index) {
		this.isUnderground[dimId][index] = isUnderground;
		this.markDirty();
	}

	public void setTowerData(int chunkX, int baseY,
		int chunkZ, int floors, int type, int dimId, int index) {
		this.towerData[dimId][index] = new int[] { chunkX,
			baseY, chunkZ, floors, type };
		this.markDirty();
	}

	public void setMiniTowerData(int[] bounds, int dimId,
		int index, int indexMini) {
		if (this.towerDataMini[dimId][index] == null)
			this.towerDataMini[dimId][index] = new ArrayList<int[]>();
		if (this.towerDataMini[dimId][index].size() == indexMini)
			this.towerDataMini[dimId][index].add(bounds);
		else
			this.towerDataMini[dimId][index].set(indexMini,
				bounds);
		this.markDirty();
	}

	public void setBlockBreakabilityData(BitSet[][] data,
		int dimId, int index) {
		this.blockBreakabilityData[dimId][index] = data;
		this.markDirty();
	}

	public void setBlockBreakabilityDataMini(
		BitSet[][][] data, int dimId, int index,
		int indexMini) {
		if (this.blockBreakabilityDataMini[dimId][index] == null)
			this.blockBreakabilityDataMini[dimId][index] = new ArrayList<BitSet[][][]>();
		if (this.blockBreakabilityDataMini[dimId][index]
			.size() == indexMini)
			this.blockBreakabilityDataMini[dimId][index]
				.add(indexMini, data);
		else
			this.blockBreakabilityDataMini[dimId][index]
				.set(indexMini, data);
		this.markDirty();
	}
}
