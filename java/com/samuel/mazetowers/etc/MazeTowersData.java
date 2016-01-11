package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.samuel.mazetowers.MazeTowers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MazeTowersData extends WorldSavedData {
	
	private World world;
	public BlockPos[] spawnPos;
	public boolean[] isGenerated;
	public int genCount;

	public MazeTowersData(World worldIn, String tagName) {
		super(tagName);
		genCount = MazeTowers.mazeTowers.getGenCount();
		spawnPos = new BlockPos[genCount];
		isGenerated = new boolean[genCount];
		world = worldIn;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		for (int g = 0; g < compound.getTagList("isGenerated", Constants.NBT.TAG_INT).tagCount(); g++) {
			spawnPos[g] = BlockPos.fromLong(((long) ((NBTTagLong) compound.getTagList("spawnPos", Constants.NBT.TAG_LONG).get(g)).getLong()));
			isGenerated[g] = ((int) ((NBTTagInt) compound.getTagList("isGenerated", Constants.NBT.TAG_INT).get(g)).getInt()) == 1;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList spawnPosSublist = new NBTTagList();
		NBTTagList isGeneratedSublist = new NBTTagList();
		for (int g = 0; g < Math.min(genCount, MazeTowers.mazeTowers.getGenCount(world)); g++) {
			NBTTagLong spawnPosTag;
			NBTTagInt isGeneratedTag;
			try {
				spawnPosTag = new NBTTagLong((long) spawnPos[g].toLong());
				spawnPosSublist.appendTag(spawnPosTag);
			} catch (NullPointerException eSpawn) {
				
			}
			try {
				isGeneratedTag = new NBTTagInt((int) (isGenerated[g] ? 1 : 0));
				isGeneratedSublist.appendTag(isGeneratedTag);
			} catch (NullPointerException eSpawn) {
				
			}
		}
	}
	
	public boolean getIsGenerated(int index) {
		return isGenerated[index];
	}
	
	public BlockPos getSpawnPoint(int index) {
		return spawnPos[index];
	}
	
	public void setSpawnPoint(int spawnX, int spawnY, int spawnZ, int index) {
		this.spawnPos[index] = new BlockPos(spawnX, spawnY, spawnZ);
		this.markDirty();
	}

	public void setIsGenerated(boolean isGenerated, int index) {
		this.isGenerated[index] = isGenerated;
		this.markDirty();
	}
}
