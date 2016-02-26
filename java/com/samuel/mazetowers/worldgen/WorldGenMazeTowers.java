package com.samuel.mazetowers.worldgen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.IWorldGenerator;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Multimap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockHiddenButton;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBase;
import com.samuel.mazetowers.etc.MTStateMaps;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.etc.MazeTowersData;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModChestGen;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.packets.PacketDebugMessage;
import com.samuel.mazetowers.tileentities.TileEntityCircuitBreaker;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityWebSpiderSpawner;

public class WorldGenMazeTowers implements IWorldGenerator {

	private Map<Integer, Map<Integer, IBlockState>> chunkData = null;
	private Multimap<Integer, ArrayDeque<Integer>> chunkChest = null;
	private List<MazeTowerBase>[] towers = new List[3];
	private final int[] genCount = new int[] { 64, 128, 0 };
	private int[] chunksGenerated = new int[] { 0, 0, 0 };
	private int[] spawnPosLoadedCount = new int[] { 0, 0, 0 };
	private World curWorld;
	public BlockPos[][] spawnPos = new BlockPos[3][];
	private boolean[][] spawnPosLoaded = new boolean[3][];
	private boolean[][] generated = new boolean[3][];
	private Map<BlockPos, IBlockState>[][] data = new Map[3][];
	private Map<ChunkCoordIntPair, ChunkCoordIntPair>[] chunkGroupTowerCoords = new Map[3];
	private MazeTowersData MazeTowerData;
	private final ChestGenHooks[] chestInfo;
	private final Map<Block, IBlockState[]> colourBlockStates;
	private static final Random rand = new Random();
	private static final Comparator<Entry<BlockPos, Tuple<Integer, Integer>>> scannerPosComparator = new Comparator<Entry<BlockPos, Tuple<Integer, Integer>>>() {

		@Override
		public int compare(
			Entry<BlockPos, Tuple<Integer, Integer>> e1,
			Entry<BlockPos, Tuple<Integer, Integer>> e2) {
			Integer v1 = (Integer) e1.getValue()
				.getSecond();
			Integer v2 = (Integer) e2.getValue()
				.getSecond();
			return v1.compareTo(v2);
		}
	};
	private static final Comparator<Entry<ItemStack, Integer>> chestItemsComparator = new Comparator<Entry<ItemStack, Integer>>() {

		@Override
		public int compare(Entry<ItemStack, Integer> e1,
			Entry<ItemStack, Integer> e2) {
			Integer v1 = e1.getValue();
			Integer v2 = e2.getValue();
			return v1.compareTo(v2);
		}
	};

	public WorldGenMazeTowers() {
		final Block[] colourBlocks = new Block[] {
			Blocks.stained_glass,
			Blocks.stained_glass_pane, Blocks.carpet };
		final PropertyEnum[] colourBlockColours = new PropertyEnum[] {
			BlockStainedGlass.COLOR,
			BlockStainedGlassPane.COLOR, BlockCarpet.COLOR };
		for (int d = 0; d < 3; d++) {
			towers[d] = new ArrayList<MazeTowerBase>();
			spawnPos[d] = new BlockPos[genCount[d]];
			spawnPosLoaded[d] = new boolean[genCount[d]];
			generated[d] = new boolean[genCount[d]];
			data[d] = new Map[genCount[d]];
			chunkGroupTowerCoords[d] = new HashMap<ChunkCoordIntPair, ChunkCoordIntPair>();
		}
		colourBlockStates = new HashMap<Block, IBlockState[]>();
		for (int b = 0; b < colourBlocks.length; b++) {
			final Block block = colourBlocks[b];
			final IBlockState[] blockStates = new IBlockState[EnumDyeColor
				.values().length];
			for (int c = 0; c < blockStates.length; c++)
				blockStates[c] = block.getDefaultState()
					.withProperty(colourBlockColours[b],
						EnumDyeColor.byDyeDamage(c));
			colourBlockStates.put(block, blockStates);
		}

		chestInfo = new ChestGenHooks[11];
		for (int i = 1; i <= 11; i++)
			chestInfo[i - 1] = ChestGenHooks
				.getInfo("MazeTowerChest" + i);
	}

	@Override
	public void generate(Random random, int chunkX,
		int chunkZ, World world,
		IChunkProvider chunkGenerator,
		IChunkProvider chunkProvider) { }

	public int getGenCount(int dimId) {
		return genCount[dimId + 1];
	}

	public int getChunksGenerated(int dimId) {
		return chunksGenerated[dimId + 1];
	}

	public int getChunksGenerated(World worldIn) {
		loadOrCreateData(worldIn);
		return chunksGenerated[worldIn.provider
			.getDimensionId() + 1];
	}

	public int getSpawnPosLoadedCount(World worldIn) {
		loadOrCreateData(worldIn);
		return spawnPosLoadedCount[worldIn.provider
			.getDimensionId() + 1];
	}

	public boolean getIsValidChunkCoord(int chunkX,
		int chunkZ, int dimId) {
		return chunkGroupTowerCoords[dimId + 1]
			.get(new ChunkCoordIntPair(chunkX >> 4,
				chunkZ >> 4)) == null;
	}

	public boolean getGenerated(int dimId, int index) {
		return generated[dimId + 1][index];
	}

	public BlockPos[] getSpawnPos(int dimId) {
		return spawnPos[dimId + 1];
	}

	public BlockPos getSpawnPos(int dimId, int index) {
		return spawnPos[dimId + 1][index];
	}

	public boolean getSpawnPosLoaded(int dimId, int index) {
		return spawnPosLoaded[dimId + 1][index];
	}

	public List<MazeTowerBase> getTowers(int dimId) {
		return towers[dimId + 1];
	}

	public MazeTowersData getData() {
		return MazeTowerData;
	}

	public MazeTowerBase getTowerAtCoords(World worldIn,
		int chunkX, int chunkZ) {
		loadOrCreateData(worldIn);
		for (MazeTowerBase tower : towers[worldIn.provider
			.getDimensionId() + 1]) {
			if (tower.chunkX == chunkX
				&& tower.chunkZ == chunkZ)
				return tower;
		}
		return null;
	}

	public MazeTowerBase getTowerBesideCoords(
		World worldIn, int chunkX, int chunkZ) {
		loadOrCreateData(worldIn);
		for (MazeTowerBase tower : towers[worldIn.provider
			.getDimensionId() + 1]) {
			if ((tower.chunkX == chunkX - 1
				|| tower.chunkX == chunkX || tower.chunkX == chunkX + 1)
				&& (tower.chunkZ == chunkZ - 1
					|| tower.chunkZ == chunkZ || tower.chunkZ == chunkZ + 1))
				return tower;
		}
		return null;
	}

	public void setSpawnPos(World worldIn, int chunkIndex,
		BlockPos pos) {
		int dimId = worldIn.provider.getDimensionId() + 1;
		loadOrCreateData(worldIn);
		spawnPos[dimId][chunkIndex] = pos;
		MazeTowerData.setSpawnPoint(pos.getX(), pos.getY(),
			pos.getZ(), dimId, chunkIndex);
		switch (dimId) {
		case 0:
			chunkGroupTowerCoords[dimId].put(
				new ChunkCoordIntPair(pos.getX() >> 7, pos
					.getZ() >> 7), new ChunkCoordIntPair(
					pos.getX() >> 4, pos.getZ() >> 4));
			break;
		case 1:
			chunkGroupTowerCoords[dimId].put(
				new ChunkCoordIntPair(pos.getX() >> 8, pos
					.getZ() >> 8), new ChunkCoordIntPair(
					pos.getX() >> 4, pos.getZ() >> 4));
			break;
		case 2:
			chunkGroupTowerCoords[dimId].put(
				new ChunkCoordIntPair(pos.getX() >> 6, pos
					.getZ() >> 6), new ChunkCoordIntPair(
					pos.getX() >> 4, pos.getZ() >> 4));
			break;
		}
		spawnPosLoaded[dimId][chunkIndex] = true;
		spawnPosLoadedCount[dimId]++;
	}

	public void setIsGenerated(World worldIn,
		int chunkIndex, boolean isGenerated) {
		int dimId = worldIn.provider.getDimensionId() + 1;
		loadOrCreateData(worldIn);
		generated[dimId][chunkIndex] = isGenerated;
		MazeTowerData.setIsGenerated(isGenerated, dimId,
			chunkIndex);
	}

	protected final void loadOrCreateData(World world) {
		boolean dataIsNull = this.MazeTowerData == null;
		final int dimId = world.provider.getDimensionId();
		if (dataIsNull
			|| curWorld == null
			|| (world.getWorldInfo().getSeed() != 0 && curWorld
				.getWorldInfo().getSeed() != world
				.getWorldInfo().getSeed())) {
			int d = 0, g;
			if (curWorld != world || dataIsNull)
				this.MazeTowerData = (MazeTowersData) world
					.loadItemData(MazeTowersData.class,
						"MazeTowers");
			dataIsNull = this.MazeTowerData == null;
			curWorld = world;
			this.chunkGroupTowerCoords = new HashMap[3];
			this.towers = new List[3];
			if (dataIsNull) {
				for (; d < 3; d++) {
					this.chunkGroupTowerCoords[d] = new HashMap<ChunkCoordIntPair, ChunkCoordIntPair>();
					this.towers[d] = new ArrayList<MazeTowerBase>();
					this.generated[d] = new boolean[genCount[d]];
					this.spawnPosLoaded[d] = new boolean[genCount[d]];
					this.spawnPosLoadedCount[d] = 0;
					this.spawnPos[d] = new BlockPos[genCount[d]];
					for (g = 0; g < genCount[d]; g++)
						this.spawnPos[d][g] = new BlockPos(
							-1, -1, -1);
					this.chunksGenerated[d] = 0;
				}
				if (this.MazeTowerData == null) {
					this.MazeTowerData = new MazeTowersData(
						"MazeTowers");
					world.setItemData("MazeTowers",
						MazeTowerData);
				}
			} else {
				int[][][] towerData = MazeTowerData
					.getTowerData();
				boolean[][] isUnderground = MazeTowerData
					.getIsUnderground();
				String[][] towerTypeName = MazeTowerData
					.getTowerTypeName();
				this.generated = MazeTowerData
					.getIsGenerated();
				this.spawnPos = MazeTowerData
					.getSpawnPoint();
				for (; d < 3; d++) {
					spawnPosLoadedCount[d] = 0;
					this.towers[d] = new ArrayList<MazeTowerBase>();
					for (g = 0; g < genCount[d]; g++) {
						BitSet[][] blockBreakabilityData = MazeTowerData
							.getBlockBreakabilityData(d, g);

						if (this.generated[d][g])
							chunksGenerated[d]++;
						if (this.spawnPos[d][g] != null) {
							int chunkX = towerData[d][g][0];
							int chunkZ = towerData[d][g][2];
							int ix = chunkX << 4;
							int iz = chunkZ << 4;
							List<MiniTower> miniTowers = new ArrayList<MiniTower>();
							List<int[]> mtDataList = MazeTowerData
								.getTowerDataMini(d, g);
							List<BitSet[][][]> mtBlockBreakabilityDataList = MazeTowerData
								.getBlockBreakabilityDataMini(
									d, g);
							MazeTowerBase tower;
							spawnPosLoaded[d][g] = true;
							spawnPosLoadedCount[d]++;
							chunkGroupTowerCoords[d].put(
								new ChunkCoordIntPair(
									chunkX >> 4,
									chunkZ >> 4),
								new ChunkCoordIntPair(
									chunkX, chunkZ));
							towers[d].add(g,
								tower = new MazeTowerBase(
									towerData[d][g][0],
									towerData[d][g][1],
									towerData[d][g][2],
									towerData[d][g][3],
									towerData[d][g][4],
									towerData[d][g][5],
									isUnderground[d][g],
									towerTypeName[d][g],
									blockBreakabilityData,
									miniTowers, d, g));
							for (int m = 0; m < mtDataList
								.size(); m++) {
								try {
									int[] mtData = mtDataList
										.get(m);
									BitSet[][][] mtbbd = mtBlockBreakabilityDataList
										.get(m);
									miniTowers
										.add(new MiniTower(
											tower,
											ix,
											iz,
											new int[] {
												mtData[0],
												mtData[1],
												mtData[2],
												mtData[3],
												mtData[4],
												mtData[5] },
											new int[] {
												mtData[6],
												mtData[7],
												mtData[8],
												mtData[9],
												mtData[10],
												mtData[11] },
											new int[] {
												mtData[12],
												mtData[13],
												mtData[14],
												mtData[15],
												mtData[16],
												mtData[17] },
											mtbbd[0],
											mtbbd[1],
											mtbbd[2], m));
								} catch (NullPointerException e) {
									e = null;
								}
							}
						}
					}
				}
			}
		}
	}

	public void rebuild(World worldIn, int index) {
		loadOrCreateData(worldIn);
		((MazeTower) towers[worldIn.provider
			.getDimensionId() + 1].get(index)).build(
			worldIn, false);
	}

	public void spawn(World world, int ix, int iy, int iz,
		int chunk) {
	}

	public boolean addTower(World worldIn, int x, int z,
		boolean build) {
		loadOrCreateData(worldIn);
		final int dimId = worldIn.provider.getDimensionId();
		final BiomeGenBase biome;
		String biomeName;
		final String towerTypeName;
		final BlockPos pos;
		Block doorBlock = Blocks.iron_door;
		final Block trapDoorBlock = Blocks.iron_trapdoor;
		IBlockState ceilBlock = null, wallBlock = null, wallBlock_external = null, floorBlock = null, fenceBlock = null;
		IBlockState[] stairsBlock = new IBlockState[4];
		final EnumDyeColor[] dyeColorList;
		final EnumDyeColor[] beaconGlassColorList;
		final List<MazeTowerBase> towersList = towers[dimId + 1];
		boolean isUnderground = false;
		final boolean isUnderwater;
		final boolean hasXEntrance = rand.nextBoolean();
		int px = (x << 4) + (hasXEntrance ? 0 : 8), pz = (z << 4)
			+ (hasXEntrance ? 8 : 0), py, difficulty, rarity;
		biome = worldIn.getBiomeGenForCoords(new BlockPos(
			px, 64, pz));
		biomeName = biome.biomeName;
		isUnderwater = biomeName.equals("Deep Ocean");
		py = MTUtils.getSurfaceY(worldIn, px, pz, 3,
			isUnderwater || dimId == -1) - 1;
		pos = new BlockPos(px, py, pz);

		int typeChance = !isUnderwater ? rand.nextInt(128)
			: 32;
		if (dimId == -1)
			typeChance = (typeChance % 6) + 90;
		else if (dimId == 1)
			typeChance = (typeChance % 6) + 58;
		if (typeChance < 32) {
			if (dimId == -1)
				typeChance = (typeChance % 6) + 18;
			else if (dimId == 1)
				typeChance = (typeChance % 6) + 30;
			if (typeChance < 16) {
				ceilBlock = Blocks.stonebrick
					.getStateFromMeta(3);
				wallBlock = Blocks.stonebrick
					.getDefaultState();
				floorBlock = Blocks.stonebrick
					.getStateFromMeta(3);
				stairsBlock = new IBlockState[] {
					Blocks.stone_brick_stairs
						.getStateFromMeta(0),
					Blocks.stone_brick_stairs
						.getStateFromMeta(2),
					Blocks.stone_brick_stairs
						.getStateFromMeta(1),
					Blocks.stone_brick_stairs
						.getDefaultState() };
				fenceBlock = MazeTowers.BlockStoneBrickWall
					.getDefaultState();
				difficulty = 3;
				rarity = 3;
				dyeColorList = new EnumDyeColor[] {
					EnumDyeColor.GRAY, EnumDyeColor.BLUE,
					EnumDyeColor.RED };
				beaconGlassColorList = new EnumDyeColor[] { EnumDyeColor.GRAY };
				isUnderground = rand.nextInt(3) == 0;
				towerTypeName = "Stone Brick";
			} else if (typeChance < 27) {
				ceilBlock = Blocks.quartz_block
					.getDefaultState();
				wallBlock = Blocks.quartz_block
					.getStateFromMeta(2);
				floorBlock = Blocks.quartz_block
					.getStateFromMeta(1);
				doorBlock = MazeTowers.BlockQuartzDoor;
				stairsBlock = new IBlockState[] {
					Blocks.quartz_stairs
						.getStateFromMeta(0),
					Blocks.quartz_stairs
						.getStateFromMeta(2),
					Blocks.quartz_stairs
						.getStateFromMeta(1),
					Blocks.quartz_stairs.getDefaultState() };
				fenceBlock = MazeTowers.BlockQuartzWall
					.getDefaultState();
				difficulty = 4;
				rarity = 4;
				dyeColorList = new EnumDyeColor[] {
					EnumDyeColor.WHITE,
					EnumDyeColor.LIGHT_BLUE,
					EnumDyeColor.LIME };
				beaconGlassColorList = new EnumDyeColor[] { EnumDyeColor.WHITE };
				isUnderground = rand.nextInt(3) == 0;
				towerTypeName = "Quartz";
			} else if (typeChance < 31) {
				ceilBlock = Blocks.obsidian
					.getDefaultState();
				wallBlock = Blocks.obsidian
					.getDefaultState();
				floorBlock = Blocks.obsidian
					.getDefaultState();
				doorBlock = MazeTowers.BlockObsidianDoor;
				stairsBlock = new IBlockState[] {
					MazeTowers.BlockObsidianStairs
						.getStateFromMeta(0),
					MazeTowers.BlockObsidianStairs
						.getStateFromMeta(2),
					MazeTowers.BlockObsidianStairs
						.getStateFromMeta(1),
					MazeTowers.BlockObsidianStairs
						.getDefaultState() };
				fenceBlock = MazeTowers.BlockObsidianWall
					.getDefaultState();
				difficulty = 5;
				rarity = 5;
				dyeColorList = new EnumDyeColor[] {
					EnumDyeColor.BLACK,
					EnumDyeColor.PURPLE, EnumDyeColor.RED };
				beaconGlassColorList = new EnumDyeColor[] {
					EnumDyeColor.PURPLE,
					EnumDyeColor.BLACK, EnumDyeColor.BLACK };
				isUnderground = rand.nextInt(3) != 0;
				towerTypeName = "Obsidian";
			} else {
				ceilBlock = Blocks.bedrock
					.getDefaultState();
				wallBlock = Blocks.bedrock
					.getDefaultState();
				floorBlock = Blocks.bedrock
					.getDefaultState();
				doorBlock = MazeTowers.BlockBedrockDoor;
				stairsBlock = new IBlockState[] {
					MazeTowers.BlockBedrockStairs
						.getStateFromMeta(0),
					MazeTowers.BlockBedrockStairs
						.getStateFromMeta(2),
					MazeTowers.BlockBedrockStairs
						.getStateFromMeta(1),
					MazeTowers.BlockBedrockStairs
						.getDefaultState() };
				fenceBlock = MazeTowers.BlockBedrockWall
					.getDefaultState();
				difficulty = 6;
				rarity = 6;
				dyeColorList = new EnumDyeColor[] {
					EnumDyeColor.BLACK, EnumDyeColor.RED,
					EnumDyeColor.PURPLE };
				beaconGlassColorList = new EnumDyeColor[] {
					EnumDyeColor.GRAY, EnumDyeColor.BLACK };
				isUnderground = rand.nextInt(3) != 0;
				towerTypeName = "Bedrock";
			}
		} else {
			if (dimId == 0) {
				if (biomeName.contains("Taiga")
					|| biomeName.startsWith("Extreme")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(1);
					wallBlock = Blocks.log
						.getStateFromMeta(1);
					floorBlock = Blocks.planks
						.getStateFromMeta(1);
					doorBlock = Blocks.spruce_door;
					stairsBlock = new IBlockState[] {
						Blocks.spruce_stairs
							.getStateFromMeta(0),
						Blocks.spruce_stairs
							.getStateFromMeta(2),
						Blocks.spruce_stairs
							.getStateFromMeta(1),
						Blocks.spruce_stairs
							.getDefaultState() };
					fenceBlock = Blocks.spruce_fence
						.getDefaultState();
					difficulty = 1;
					rarity = 1;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.BROWN,
						EnumDyeColor.SILVER,
						EnumDyeColor.GREEN };
					beaconGlassColorList = new EnumDyeColor[] { EnumDyeColor.BROWN };
					towerTypeName = "Spruce";
				} else if (biomeName.startsWith("Birch")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(2);
					wallBlock = Blocks.log
						.getStateFromMeta(2);
					floorBlock = Blocks.planks
						.getStateFromMeta(2);
					doorBlock = Blocks.birch_door;
					stairsBlock = new IBlockState[] {
						Blocks.birch_stairs
							.getStateFromMeta(0),
						Blocks.birch_stairs
							.getStateFromMeta(2),
						Blocks.birch_stairs
							.getStateFromMeta(1),
						Blocks.birch_stairs
							.getDefaultState() };
					fenceBlock = Blocks.birch_fence
						.getDefaultState();
					difficulty = 1;
					rarity = 1;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.ORANGE,
						EnumDyeColor.SILVER,
						EnumDyeColor.GREEN };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.SILVER,
						EnumDyeColor.ORANGE };
					towerTypeName = "Birch";
				} else if (biomeName.startsWith("Jungle")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(3);
					wallBlock = Blocks.log
						.getStateFromMeta(3);
					floorBlock = Blocks.planks
						.getStateFromMeta(3);
					doorBlock = Blocks.jungle_door;
					stairsBlock = new IBlockState[] {
						Blocks.jungle_stairs
							.getStateFromMeta(0),
						Blocks.jungle_stairs
							.getStateFromMeta(2),
						Blocks.jungle_stairs
							.getStateFromMeta(1),
						Blocks.jungle_stairs
							.getDefaultState() };
					fenceBlock = Blocks.jungle_fence
						.getDefaultState();
					difficulty = 1;
					rarity = 1;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.BROWN,
						EnumDyeColor.SILVER,
						EnumDyeColor.GREEN };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.ORANGE,
						EnumDyeColor.BROWN };
					towerTypeName = "Jungle";
				} else if (biomeName.startsWith("Savanna")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(4);
					wallBlock = Blocks.log2
						.getStateFromMeta(0);
					floorBlock = Blocks.planks
						.getStateFromMeta(4);
					doorBlock = Blocks.acacia_door;
					stairsBlock = new IBlockState[] {
						Blocks.acacia_stairs
							.getStateFromMeta(0),
						Blocks.acacia_stairs
							.getStateFromMeta(2),
						Blocks.acacia_stairs
							.getStateFromMeta(1),
						Blocks.acacia_stairs
							.getDefaultState() };
					fenceBlock = Blocks.acacia_fence
						.getDefaultState();
					difficulty = 1;
					rarity = 1;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.ORANGE,
						EnumDyeColor.SILVER,
						EnumDyeColor.GREEN };
					beaconGlassColorList = new EnumDyeColor[] { EnumDyeColor.ORANGE };
					towerTypeName = "Acacia";
				} else if (biomeName.startsWith("Roofed")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(5);
					wallBlock = Blocks.log2
						.getStateFromMeta(1);
					floorBlock = Blocks.planks
						.getStateFromMeta(5);
					doorBlock = Blocks.dark_oak_door;
					stairsBlock = new IBlockState[] {
						Blocks.dark_oak_stairs
							.getStateFromMeta(0),
						Blocks.dark_oak_stairs
							.getStateFromMeta(2),
						Blocks.dark_oak_stairs
							.getStateFromMeta(1),
						Blocks.dark_oak_stairs
							.getDefaultState() };
					fenceBlock = Blocks.dark_oak_fence
						.getDefaultState();
					difficulty = 2;
					rarity = 2;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.BROWN,
						EnumDyeColor.SILVER,
						EnumDyeColor.GREEN };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.BLACK,
						EnumDyeColor.BROWN };
					towerTypeName = "Dark Oak";
				} else if (biomeName.startsWith("Desert")) {
					ceilBlock = Blocks.sandstone
						.getStateFromMeta(1);
					wallBlock = Blocks.sandstone
						.getDefaultState();
					wallBlock_external = Blocks.sandstone
						.getStateFromMeta(2);
					floorBlock = Blocks.sandstone
						.getStateFromMeta(2);
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.sandstone_stairs
							.getStateFromMeta(0),
						Blocks.sandstone_stairs
							.getStateFromMeta(2),
						Blocks.sandstone_stairs
							.getStateFromMeta(1),
						Blocks.sandstone_stairs
							.getDefaultState() };
					fenceBlock = MazeTowers.BlockSandstoneWall
						.getDefaultState();
					difficulty = 2;
					rarity = 2;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.ORANGE,
						EnumDyeColor.BROWN,
						EnumDyeColor.RED };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.YELLOW,
						EnumDyeColor.ORANGE,
						EnumDyeColor.WHITE };
					isUnderground = rand.nextInt(3) == 0;
					towerTypeName = "Sandstone";
				} else if (biomeName.startsWith("Mesa")) {
					ceilBlock = Blocks.red_sandstone
						.getStateFromMeta(1);
					wallBlock = Blocks.red_sandstone
						.getDefaultState();
					wallBlock_external = Blocks.red_sandstone
						.getStateFromMeta(2);
					floorBlock = Blocks.red_sandstone
						.getStateFromMeta(2);
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.red_sandstone_stairs
							.getStateFromMeta(0),
						Blocks.red_sandstone_stairs
							.getStateFromMeta(2),
						Blocks.red_sandstone_stairs
							.getStateFromMeta(1),
						Blocks.red_sandstone_stairs
							.getDefaultState() };
					fenceBlock = MazeTowers.BlockRedSandstoneWall
						.getDefaultState();
					difficulty = 2;
					rarity = 3;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.ORANGE,
						EnumDyeColor.YELLOW,
						EnumDyeColor.RED };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.RED,
						EnumDyeColor.ORANGE };
					isUnderground = rand.nextInt(4) == 0;
					towerTypeName = "Mesa";
				} else if (biomeName.startsWith("Ice")
					|| biomeName.startsWith("Frozen")
					|| biomeName == "Cold Beach") {
					ceilBlock = Blocks.packed_ice
						.getDefaultState();
					wallBlock = Blocks.packed_ice
						.getDefaultState();
					wallBlock_external = Blocks.packed_ice
						.getDefaultState();
					floorBlock = Blocks.packed_ice
						.getDefaultState();
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs
							.getStateFromMeta(0),
						Blocks.stone_brick_stairs
							.getStateFromMeta(2),
						Blocks.stone_brick_stairs
							.getStateFromMeta(1),
						Blocks.stone_brick_stairs
							.getDefaultState() };
					fenceBlock = MazeTowers.BlockPackedIceWall
						.getDefaultState();
					difficulty = 3;
					rarity = 5;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.LIGHT_BLUE,
						EnumDyeColor.BLUE,
						EnumDyeColor.CYAN };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.WHITE,
						EnumDyeColor.LIGHT_BLUE };
					isUnderground = rand.nextInt(3) == 0;
					towerTypeName = "Ice";
				} else if (((biomeName.contains("Forest") && rand
					.nextBoolean()))
					|| biomeName.startsWith("Swampland")
					|| biomeName.indexOf("Plains") > -1) {
					ceilBlock = Blocks.planks
						.getDefaultState();
					wallBlock = Blocks.log
						.getDefaultState();
					floorBlock = Blocks.planks
						.getDefaultState();
					doorBlock = Blocks.oak_door;
					stairsBlock = new IBlockState[] {
						Blocks.oak_stairs
							.getStateFromMeta(0),
						Blocks.oak_stairs
							.getStateFromMeta(2),
						Blocks.oak_stairs
							.getStateFromMeta(1),
						Blocks.oak_stairs.getDefaultState() };
					fenceBlock = Blocks.oak_fence
						.getDefaultState();
					difficulty = 0;
					rarity = 0;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.ORANGE,
						EnumDyeColor.SILVER,
						EnumDyeColor.GREEN };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.ORANGE,
						EnumDyeColor.ORANGE,
						EnumDyeColor.BROWN };
					towerTypeName = "Oak";
				} else if (biomeName.startsWith("Mushroom")) {
					final boolean isRed = rand
						.nextBoolean();
					final EnumDyeColor mushroomColor = isRed ? EnumDyeColor.RED
						: EnumDyeColor.BROWN;
					final Block mushroomBlock = isRed ? Blocks.red_mushroom_block
						: Blocks.brown_mushroom_block;
					ceilBlock = Blocks.mycelium
						.getDefaultState();
					wallBlock = mushroomBlock
						.getDefaultState();
					wallBlock_external = mushroomBlock
						.getStateFromMeta(15);
					floorBlock = Blocks.mycelium
						.getDefaultState();
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs
							.getStateFromMeta(0),
						Blocks.stone_brick_stairs
							.getStateFromMeta(2),
						Blocks.stone_brick_stairs
							.getStateFromMeta(1),
						Blocks.stone_brick_stairs
							.getDefaultState() };
					fenceBlock = wallBlock;
					difficulty = 3;
					rarity = 5;
					dyeColorList = new EnumDyeColor[] {
						mushroomColor, EnumDyeColor.WHITE,
						EnumDyeColor.PINK };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.WHITE, mushroomColor,
						mushroomColor };
					towerTypeName = (isRed ? "Red"
						: "Brown")
						+ " Mushroom";
				} else if (isUnderwater) {
					ceilBlock = Blocks.prismarine
						.getStateFromMeta(2);
					wallBlock = Blocks.prismarine
						.getDefaultState();
					wallBlock_external = Blocks.prismarine
						.getStateFromMeta(1);
					floorBlock = wallBlock_external;
					doorBlock = MazeTowers.BlockObsidianDoor;
					stairsBlock = new IBlockState[] {
						MazeTowers.BlockPrismarineBrickStairs
							.getStateFromMeta(0),
						MazeTowers.BlockPrismarineBrickStairs
							.getStateFromMeta(2),
						MazeTowers.BlockPrismarineBrickStairs
							.getStateFromMeta(1),
						MazeTowers.BlockPrismarineBrickStairs
							.getDefaultState() };
					fenceBlock = MazeTowers.BlockPrismarineBrickWall
						.getDefaultState();
					difficulty = 4;
					rarity = 5;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.CYAN,
						EnumDyeColor.GREEN,
						EnumDyeColor.PURPLE };
					beaconGlassColorList = new EnumDyeColor[] {
						EnumDyeColor.GREEN,
						EnumDyeColor.CYAN,
						EnumDyeColor.SILVER };
					isUnderground = rand.nextBoolean();
					towerTypeName = "Prismarine";
				} else {
					ceilBlock = Blocks.stonebrick
						.getStateFromMeta(3);
					wallBlock = Blocks.stonebrick
						.getDefaultState();
					if (biomeName.indexOf("Taiga") > -1)
						wallBlock_external = Blocks.stonebrick
							.getStateFromMeta(1);
					floorBlock = Blocks.stonebrick
						.getStateFromMeta(3);
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs
							.getStateFromMeta(0),
						Blocks.stone_brick_stairs
							.getStateFromMeta(2),
						Blocks.stone_brick_stairs
							.getStateFromMeta(1),
						Blocks.stone_brick_stairs
							.getDefaultState() };
					fenceBlock = MazeTowers.BlockStoneBrickWall
						.getDefaultState();
					difficulty = 3;
					rarity = 3;
					dyeColorList = new EnumDyeColor[] {
						EnumDyeColor.SILVER,
						EnumDyeColor.BLUE, EnumDyeColor.RED };
					beaconGlassColorList = new EnumDyeColor[] { EnumDyeColor.SILVER };
					isUnderground = rand.nextInt(3) == 0;
					towerTypeName = "Stone Brick";
				}
			} else if (dimId == -1) {
				ceilBlock = Blocks.nether_brick
					.getDefaultState();
				wallBlock = Blocks.nether_brick
					.getDefaultState();
				floorBlock = Blocks.netherrack
					.getDefaultState();
				stairsBlock = new IBlockState[] {
					Blocks.nether_brick_stairs
						.getStateFromMeta(0),
					Blocks.nether_brick_stairs
						.getStateFromMeta(2),
					Blocks.nether_brick_stairs
						.getStateFromMeta(1),
					Blocks.nether_brick_stairs
						.getDefaultState() };
				fenceBlock = Blocks.nether_brick_fence
					.getDefaultState();
				difficulty = 5;
				rarity = 4;
				dyeColorList = new EnumDyeColor[] {
					EnumDyeColor.RED, EnumDyeColor.BLACK,
					EnumDyeColor.PURPLE };
				beaconGlassColorList = new EnumDyeColor[] {
					EnumDyeColor.BLACK, EnumDyeColor.RED,
					EnumDyeColor.GRAY };
				towerTypeName = "Nether Brick";
			} else {
				ceilBlock = Blocks.end_stone
					.getDefaultState();
				wallBlock = Blocks.end_stone
					.getDefaultState();
				floorBlock = Blocks.end_stone
					.getDefaultState();
				doorBlock = MazeTowers.BlockEndStoneDoor;
				stairsBlock = new IBlockState[] {
					MazeTowers.BlockEndStoneStairs
						.getStateFromMeta(0),
					MazeTowers.BlockEndStoneStairs
						.getStateFromMeta(2),
					MazeTowers.BlockEndStoneStairs
						.getStateFromMeta(1),
					MazeTowers.BlockEndStoneStairs
						.getDefaultState() };
				fenceBlock = MazeTowers.BlockEndStoneWall
					.getDefaultState();
				difficulty = 6;
				rarity = 5;
				dyeColorList = new EnumDyeColor[] {
					EnumDyeColor.YELLOW,
					EnumDyeColor.BLACK, EnumDyeColor.PURPLE };
				beaconGlassColorList = new EnumDyeColor[] {
					EnumDyeColor.WHITE,
					EnumDyeColor.YELLOW, EnumDyeColor.WHITE };
				towerTypeName = "End Stone";
			}
		}
		if (wallBlock_external == null)
			wallBlock_external = wallBlock;
		// isUnderground = true;
		if (isUnderground) {
			difficulty += 2;
			rarity++;
		}
		MazeTower newTower = new MazeTower(x, py, z,
			ceilBlock, wallBlock, wallBlock_external,
			floorBlock, fenceBlock, stairsBlock, doorBlock,
			dyeColorList, beaconGlassColorList,
			isUnderground, hasXEntrance, difficulty,
			rarity, towerTypeName, dimId + 1, towersList
				.size());
		if (!isUnderground) {
			final long startTime = System.nanoTime(), endTime;
			newTower.buildShell(worldIn);
			endTime = (System.nanoTime() - startTime) / 1000000;
			MazeTowers.network.sendToDimension(
				new PacketDebugMessage(
					"Finished tower shell build in "
						+ endTime + " ms"), dimId);

		}
		newTower.initPaths();
		setSpawnPos(worldIn, towersList.size(), pos);
		MTStateMaps.initStateMaps(newTower);
		newTower.addChests();
		// newTower.fillGaps();
		MazeTowerData.setIsUnderground(isUnderground,
			dimId + 1, towersList.size());
		MazeTowerData.setTowerData(x, py, z,
			newTower.floors, difficulty, rarity, dimId + 1,
			towersList.size());
		MazeTowerData.setTowerTypeName(towerTypeName,
			dimId + 1, towersList.size());
		towers[dimId + 1].add(newTower);
		if (build) {
			MazeTowers.network.sendToDimension(
				new PacketDebugMessage("Started building "
					+ towerTypeName + " Maze Tower at "
					+ pos.toString()), dimId);
			final long startTime = System.nanoTime(), endTime;
			newTower.build(worldIn, true);
			endTime = (System.nanoTime() - startTime) / 1000000;
			MazeTowers.network.sendToDimension(
				new PacketDebugMessage(
					"Finished tower build in " + endTime
						+ " ms"), dimId);
			setIsGenerated(worldIn, towersList.size() - 1,
				true);
			chunksGenerated[dimId + 1]++;
		}

		return true;
	}

	public void recreate(World worldIn, boolean build) {
		loadOrCreateData(worldIn);
		int dimId = worldIn.provider.getDimensionId() + 1;
		for (int t = 0; t < towers[dimId].size(); t++) {
			if (towers[dimId].get(t) instanceof MazeTower) {
				MazeTower tower = (MazeTower) towers[dimId]
					.get(t);
				final int x = tower.chunkX, y = tower.baseY, z = tower.chunkZ;

				if (!getGenerated(dimId - 1, t)
					|| worldIn.getEntitiesWithinAABB(
						EntityPlayer.class,
						new AxisAlignedBB((x - 2) << 4, 1,
							(z - 2) << 4, (x + 3) << 4,
							255, (z + 3) << 4)).isEmpty())
					continue;
				if (build && tower.signPos != null) {
					worldIn.setBlockToAir(tower.signPos);
					worldIn.setTileEntity(tower.signPos
						.down(2), null);
				}
				tower.removeMiniTowers(worldIn);
				tower = new MazeTower(x, y, z,
					tower.ceilBlock, tower.wallBlock,
					tower.wallBlock_external,
					tower.floorBlock, tower.fenceBlock,
					tower.stairsBlock, tower.doorBlock,
					tower.dyeColors,
					tower.beaconGlassColors,
					tower.isUnderground,
					rand.nextBoolean(), tower.difficulty,
					tower.rarity, tower.towerTypeName,
					dimId, t);
				tower.initPaths();
				towers[dimId].set(t, tower);
				ModChestGen.initChestGen();
				MTStateMaps.initStateMaps(tower);
				tower.addChests();
				// tower.fillGaps();
				if (build) {
					long startTime;
					long endTime;
					if (!tower.isUnderground) {
						startTime = System.nanoTime();
						tower.buildShell(worldIn);
						endTime = (System.nanoTime() - startTime) / 1000000;
						MazeTowers.network.sendToDimension(
							new PacketDebugMessage(
								"Finished tower shell build in "
									+ endTime + " ms"),
							dimId - 1);
					}
					startTime = System.nanoTime();
					tower.build(worldIn, true);
					endTime = (System.nanoTime() - startTime) / 1000000;
					MazeTowers.network.sendToDimension(
						new PacketDebugMessage(
							"Finished tower build in "
								+ endTime + " ms"),
						dimId - 1);
					MazeTowerData
						.setBlockBreakabilityData(
							tower.blockBreakabilityData = MTUtils
								.getBlockBreakabilityData(tower.blockData),
							dimId, tower.towerIndex);
				}
			}
		}
	}

	public static class MazeTowerBase {
		public final boolean isUnderground;
		public final int chunkX;
		public final int chunkZ;
		public final int baseY;
		public final int floors;
		public final int difficulty;
		public final int rarity;
		public final int towerIndex;
		public final String towerTypeName;
		protected BitSet[][] blockBreakabilityData;
		protected List<MiniTower> miniTowers;
		protected boolean isNetherTower;

		public MazeTowerBase(int x, int y, int z,
			int floors, int difficulty, int rarity,
			boolean isUnderground, String towerTypeName,
			BitSet[][] blockBreakabilityData,
			List<MiniTower> miniTowers, int dimId,
			int towerIndex) {
			final int minBaseY = (floors * 6) + 1;
			chunkX = x;
			chunkZ = z;
			baseY = (!isUnderground || y >= minBaseY) ? y
				: minBaseY;
			this.floors = !(this.isNetherTower = dimId == 0) ? floors
				: Math.min(floors, ((int) Math
					.floor((127 - y) / 6)) - 1);
			this.difficulty = difficulty;
			this.rarity = rarity;
			this.isUnderground = isUnderground;
			this.towerTypeName = towerTypeName;
			if (blockBreakabilityData != null)
				this.blockBreakabilityData = blockBreakabilityData;
			else
				this.blockBreakabilityData = new BitSet[((this.floors * 6) + 1)][16];
			if (miniTowers != null)
				this.miniTowers = miniTowers;
			else
				this.miniTowers = new ArrayList<MiniTower>();
			this.towerIndex = towerIndex;
		}

		public int[] getCoordsFromPos(BlockPos pos) {
			final int x = pos.getX(), y = pos.getY(), z = pos
				.getZ(), ix = chunkX << 4, iz = chunkZ << 4, floorIndex = (int) Math
				.floor((!isUnderground ? y - baseY
					: (baseY + 5) - y) / 6), relY = (!isUnderground ? (y - baseY) % 6
				: 5 - (((baseY + 5) - y) % 6)), yOffset = 6 * floorIndex;
			// floorIndex = (floors - 1) - floorIndex;
			int[] coords = new int[] { relY + yOffset,
				z - iz, x - ix };
			return coords;
		}

		public int getDifficulty(int floor) {
			return difficulty
				+ (int) Math
					.floor((Math.min(floor, floors) - 1) / 5);
		}

		public int getRarity(int floor) {
			return rarity
				+ (int) Math
					.floor((Math.min(floor, floors) - 1) / 5);
		}

		public String getInfoString() {
			String diffStr = EnumLevel.getStringFromLevel(
				difficulty, true), rareStr = EnumLevel
				.getStringFromLevel(rarity, true);
			if (floors > 4) {
				final int addToLevel = (int) Math
					.floor((floors - 1) / 5);
				final String arrow = StringEscapeUtils
					.unescapeJava("\u279C");
				diffStr += arrow
					+ EnumLevel.getStringFromLevel(Math
						.min(difficulty + addToLevel, 9),
						true);
				rareStr += arrow
					+ EnumLevel.getStringFromLevel(Math
						.min(rarity + addToLevel, 9), true);
			}
			return String.format("%1$14s", diffStr)
				+ EnumChatFormatting.RESET + " / "
				+ String.format("%1$-14s", rareStr);
		}

		public Chunk getChunk(World worldIn) {
			return worldIn.getChunkFromChunkCoords(chunkX,
				chunkZ);
		}

		public BitSet[][] getBlockBreakabilityData() {
			return blockBreakabilityData;
		}

		public List<MiniTower> getMiniTowers() {
			return miniTowers;
		}

		public boolean getIsNetherTower() {
			return isNetherTower;
		}

		public static enum EnumLevel implements
			IStringSerializable {
			D("D"), DP("D+"), C("C"), CP("C+"), B("B"), BP(
				"B+"), A("A"), AP("A+"), S("S"), SP("S+");

			private static final EnumChatFormatting[] colorCodes = new EnumChatFormatting[] {
				EnumChatFormatting.BLACK,
				EnumChatFormatting.BLACK,
				EnumChatFormatting.WHITE,
				EnumChatFormatting.YELLOW,
				EnumChatFormatting.AQUA };
			private final String name;

			private EnumLevel(String name) {
				this.name = name;
			}

			public String toString() {
				return this.name;
			}

			public static String getStringFromLevel(
				int level, boolean isBold) {
				EnumChatFormatting colorCode = colorCodes[level >> 1];
				return (isBold ? EnumChatFormatting.BOLD
					+ "" + colorCode
					: colorCode != EnumChatFormatting.BLACK ? colorCode
						: EnumChatFormatting.WHITE)
					+ values()[level].name;
			}

			public String getName() {
				return this.name;
			}
		}
	}

	public class MazeTower extends MazeTowerBase {

		public final int exitX, exitZ;
		public final Block doorBlock;
		public IBlockState air;
		public final IBlockState ceilBlock, wallBlock,
			wallBlock_external, floorBlock, fenceBlock;
		public final IBlockState[] stairsBlock;
		private final EnumFacing entranceDir;
		private final EnumDyeColor[] dyeColors,
			beaconGlassColors;
		private final int entranceMinX, entranceMinZ;
		private final boolean hasXEntrance, hasOddX,
			hasOddZ, isUnderwater, isMushroomTower;
		private int beaconMiniTowerIndex;
		private int[] floorHighestDepthLevel,
			floorHighestMazeDepthLevel;
		private int[][][] pathMap;
		private int[][][] dataMap;
		private int[][][] roomMap;
		private EnumFacing exitDir;
		private BlockPos signPos;
		private IBlockState[][][] blockData;
		private Path[] floorExitPaths;
		private Map<ItemStack, Integer>[] floorChestItems;
		private List<Path> paths;
		private List<Path>[] floorDeadEndPaths;
		private List<MiniTower> miniTowersCheck;
		private List<Room> rooms;

		private MazeTower(int x, int y, int z,
			IBlockState ceilBlock, IBlockState wallBlock,
			IBlockState wallBlock_external,
			IBlockState floorBlock, IBlockState fenceBlock,
			IBlockState[] stairsBlocks, Block doorBlock,
			EnumDyeColor[] dyeColors,
			EnumDyeColor[] beaconGlassColors,
			boolean isUnderground, boolean hasXEntrance,
			int difficulty, int rarity,
			String towerTypeName, int dimId, int towerIndex) {
			super(x, y, z, isUnderground ? 10 : 20,
				difficulty, rarity, isUnderground,
				towerTypeName, null, null, dimId,
				towerIndex);
			final boolean exitChance = rand.nextBoolean();
			floorChestItems = new HashMap[floors + 1];
			floorExitPaths = new Path[floors];
			floorHighestDepthLevel = new int[floors];
			floorHighestMazeDepthLevel = new int[floors];
			this.hasXEntrance = hasXEntrance;
			this.isUnderwater = towerTypeName == "Prismarine";
			this.isMushroomTower = fenceBlock.getBlock() instanceof BlockHugeMushroom;
			this.ceilBlock = ceilBlock;
			this.wallBlock = wallBlock;
			this.wallBlock_external = wallBlock_external;
			this.floorBlock = floorBlock;
			this.fenceBlock = fenceBlock;
			this.stairsBlock = stairsBlocks;
			this.doorBlock = doorBlock;
			this.dyeColors = dyeColors;
			this.beaconGlassColors = beaconGlassColors;
			air = !isUnderwater ? Blocks.air
				.getDefaultState() : Blocks.water
				.getDefaultState();
			pathMap = new int[(floors * 6) + 1][16][16];
			dataMap = new int[(floors * 6) + 1][16][16];
			roomMap = new int[floors][16][16];
			paths = new ArrayList<Path>();
			miniTowersCheck = new ArrayList<MiniTower>();
			rooms = new ArrayList<Room>();
			beaconMiniTowerIndex = -1;
			entranceMinX = hasXEntrance ? 0 : rand
				.nextInt(6) + 5;
			entranceMinZ = hasXEntrance ? rand.nextInt(6) + 5
				: 0;
			if (entranceMinZ < 8) {
				if (entranceMinX < 8)
					entranceDir = (entranceMinX < entranceMinZ) ? EnumFacing.EAST
						: EnumFacing.SOUTH;
				else
					entranceDir = (15 - entranceMinX < entranceMinZ) ? EnumFacing.WEST
						: EnumFacing.SOUTH;
			} else {
				if (entranceMinX < 8)
					entranceDir = (entranceMinX < 15 - entranceMinZ) ? EnumFacing.EAST
						: EnumFacing.NORTH;
				else
					entranceDir = (entranceMinX > entranceMinZ) ? EnumFacing.WEST
						: EnumFacing.NORTH;
			}
			hasOddX = entranceMinX % 2 == 1;
			hasOddZ = entranceMinZ % 2 == 1;
			exitX = entranceMinX + (hasXEntrance ? 1 : 0);
			exitZ = entranceMinZ + (!hasXEntrance ? 1 : 0);
			blockData = getInitialBlockData();
			floorDeadEndPaths = new List[floors];
		}

		private void initPaths() {
			Path entrance;
			entrance = new Path(this, null,
				new ArrayList<Path>(), entranceMinX, 0,
				entranceMinZ, entranceDir);
			for (int f = 1; f <= floors; f++) {
				if (floorExitPaths[f - 1] != null)
					entrance = floorExitPaths[f - 1]
						.newFloor();
				else
					break;
			}
		}

		private void removeMiniTowers(World worldIn) {
			final int ix = chunkX << 4;
			final int iz = chunkZ << 4;
			for (MiniTower mt : miniTowers) {
				int[] bounds = mt.getFullBounds();
				if (mt.isXDir) {
					if (mt.dirSign == 1)
						bounds[0] = 16;
					else
						bounds[3] = -1;
				} else {
					if (mt.dirSign == 1)
						bounds[2] = 16;
					else
						bounds[5] = -1;
				}
				for (int y = bounds[1]; y <= bounds[4]; y++) {
					for (int z = bounds[2]; z <= bounds[5]; z++) {
						for (int x = bounds[0]; x <= bounds[3]; x++) {
							BlockPos pos = new BlockPos(ix
								+ x, y, iz + z);
							worldIn.setBlockToAir(pos);
						}
					}
				}
			}
		}

		public void removeEntities(World worldIn) {
			if (!worldIn.isRemote) {
				float posX = (chunkX << 4), posY = baseY, posZ = (chunkZ << 4);
				List list = worldIn.getEntitiesWithinAABB(
					Entity.class, AxisAlignedBB.fromBounds(
						posX - 32, posY, posZ - 16,
						posX + 16, posY + (floors * 6) + 6,
						posZ + 16));
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity) list.get(i);
					if (!(entity instanceof EntityPlayer))
						entity.setDead();
				}
			}
		}

		private Room getRoom(int id) {
			return rooms.get(id);
		}

		private Room getRoom(int floor, int x, int z) {
			return rooms.get(roomMap[floor][x][z]);
		}

		private IBlockState[][][] getInitialBlockData() {
			final int yLimit = (6 * floors) + 1, yLimitFloor = (int) Math
				.floor(yLimit / floors), xzLimit = 16;
			int y = 0, z = 0, x = 0;
			final EnumFacing dispenserDir = hasXEntrance ? EnumFacing.EAST
				: EnumFacing.SOUTH;
			final IBlockState air = Blocks.air
				.getDefaultState(), carpet1 = getColourBlockState(
				Blocks.carpet, dyeColors[0]), carpet2 = getColourBlockState(
				Blocks.carpet, dyeColors[1]), carpet3 = getColourBlockState(
				Blocks.carpet, dyeColors[2]), redstone = Blocks.redstone_wire
				.getDefaultState(), repeater = Blocks.powered_repeater
				.getDefaultState().withProperty(
					BlockRedstoneRepeater.DELAY, 4), dispenser = Blocks.dispenser
				.getDefaultState().withProperty(
					BlockDispenser.FACING, dispenserDir), piston = Blocks.piston
				.getDefaultState(), button = MazeTowers.BlockHiddenButton
				.getDefaultState().withProperty(
					BlockHiddenButton.FACING, dispenserDir);
			IBlockState glass = getColourBlockState(
				Blocks.stained_glass, dyeColors[0]);
			IBlockState[][][] data = new IBlockState[yLimit + 8][xzLimit][xzLimit];

			for (y = 0; y < yLimit; y++) {
				for (z = 0; z < xzLimit; z++) {
					for (x = 0; x < xzLimit; x++) {
						final boolean isEdge = x == 0
							|| x == 15 || z == 0 || z == 15;
						if (isEdge)
							data[y][z][x] = wallBlock_external;
						else if (y % 6 == 0 || y % 6 == 4
							|| y == yLimit - 1 || x == 0
							|| z == 0 || x == xzLimit - 1
							|| z == xzLimit - 1)
							data[y][z][x] = y % 6 == 0
								|| y == yLimit - 1 ? floorBlock
								: y % 6 == 4 ? ceilBlock
									: wallBlock;
						else if (y % yLimitFloor == 0)
							data[y][z][x] = floorBlock;
						else if (y % yLimitFloor == 5)
							data[y][z][x] = this.air;
					}
				}
			}

			for (; y < data.length; y++) {
				for (z = 0; z < xzLimit; z++) {
					for (x = 0; x < xzLimit; x++) {
						final boolean isEdge = x == 0
							|| x == 15 || z == 0 || z == 15, isEdgeCorner = isEdge
							&& ((x == 0 || x == 15) && (z == 0 || z == 15)), isInnerEdge = ((x == 14 || x == 1)
							&& z != 0 && z != 15)
							|| ((z == 14 || z == 1)
								&& x != 0 && x != 15), isCarpetPos = y == yLimit;
						if (!isUnderground || !isInnerEdge
							|| y <= yLimit + 1
							|| y >= yLimit + 5) {
							if (!isCarpetPos || isEdge) {
								data[y][z][x] = (!isEdge
									&& (!isUnderground && y != yLimit + 5) || (isUnderground && y < yLimit + 5))
									|| (isEdge && ((y != yLimit + 1 && y != yLimit + 3) || isEdgeCorner)) ? isEdge ? isUnderground
									|| y != yLimit + 6 ? wallBlock_external
									: fenceBlock
									: !isMushroomTower
										|| y != yLimit + 6 ? air
										: fenceBlock
									: glass;
							} else {
								final IBlockState carpet = (x < 4 || x > 11)
									|| (z < 4 || z > 11) ? carpet1
									: (x < 7 || x > 8)
										|| (z < 7 || z > 8) ? carpet2
										: carpet3;
								data[y][z][x] = carpet;
							}
						} else {
							if (y != yLimit + 3)
								data[y][z][x] = (y != yLimit + 2 && y != yLimit + 4) ? air
									: wallBlock;
							else {
								final boolean isEntranceSide = (hasXEntrance && x == 1)
									|| (!hasXEntrance && z == 1);
								IBlockState state;
								if (isEntranceSide
									|| ((x <= 2 || x >= 13) && (z <= 2 || z >= 13))) {
									state = redstone;
									if (isEntranceSide) {
										boolean isNegative;
										if (hasXEntrance) {
											if ((isNegative = z == exitZ + 2)
												|| z == exitZ - 1) {
												state = piston
													.withProperty(
														BlockPistonBase.FACING,
														isNegative ? EnumFacing.NORTH
															: EnumFacing.SOUTH);
											} else if (z == exitZ) {
												state = wallBlock;
												data[y - 2][z][x - 1] = wallBlock_external;
												data[y][z][x - 1] = wallBlock_external;
												data[y - 1][z - 2][x + 1] = button;
												data[y - 2][z - 2][x] = dispenser;
												data[y - 3][z - 2][x] = wallBlock;
											} else if (z == exitZ + 1)
												state = air;
										} else {
											if ((isNegative = x == exitX - 2)
												|| x == exitX + 1) {
												state = piston
													.withProperty(
														BlockPistonBase.FACING,
														isNegative ? EnumFacing.EAST
															: EnumFacing.WEST);
											} else if (x == exitX) {
												state = wallBlock;
												data[y - 2][z - 1][x] = wallBlock_external;
												data[y][z - 1][x] = wallBlock_external;
												data[y - 1][z + 1][x + 2] = button;
												data[y - 2][z][x + 2] = dispenser;
												data[y - 3][z][x + 2] = wallBlock;
											} else if (x == exitX + 1)
												state = air;
										}
									}
								} else {
									final EnumFacing repeaterDir = hasXEntrance ? z == 14 ? EnumFacing.EAST
										: x == 14 ? EnumFacing.NORTH
											: EnumFacing.WEST
										: x == 1 ? EnumFacing.SOUTH
											: z == 14 ? EnumFacing.EAST
												: EnumFacing.NORTH;
									state = repeater
										.withProperty(
											BlockRedstoneRepeater.FACING,
											repeaterDir);
								}
								data[y][z][x] = state;
							}
						}
					}
				}
			}

			/*
			 * boolean isEdgeCorner = isEdge && ((x == 0 || x == 15) && (z == 0
			 * || z == 15)); if (y <= yLimit) { if ((state == null ||
			 * (state.getBlock() != Blocks.ladder && isEdge && state != air &&
			 * state.getBlock() != Blocks.torch)) || state == stone) state =
			 * isEdge ? wallBlock_external : y != yLimit ? wallBlock :
			 * floorBlock; } else { boolean isChestPos = y == yLimit + 1 && (x
			 * == 6 || x == 9) && (z == 6 || z == 9); boolean isCarpetPos = y ==
			 * yLimit + 1 && (x != exitX || z != exitZ) && (x != endEntranceX ||
			 * z != endEntranceZ); state = (!isEdge && y != yLimit + 6) ||
			 * (isEdge && ((y != yLimit + 2 && y != yLimit + 4) ||
			 * isEdgeCorner)) ? isEdge ? wallBlock_external : !isChestPos ?
			 * !isCarpetPos ? air : carpet : getChestBlock(floor,
			 * false).getDefaultState() .withProperty(BlockChest.FACING,
			 * exitDir) : glass; }
			 */

			return data;
		}

		public IBlockState[][][] getBlockData() {
			return blockData;
		}

		private Room addRoom(Room room) {
			rooms.add(room);
			return room;
		}

		private Path addPath(Path path) {
			paths.add(path);
			return path;
		}

		private void checkPathDepth(Path path,
			int floorIndex, int depth, int mazeDepth) {
			if (!isUnderground
				|| floorIndex != floors - 1
				|| !(((path.fx == 14 || path.fx == 1)
					&& path.fz != 0 && path.fz != 15) || ((path.fz == 14 || path.fz == 1)
					&& path.fx != 0 && path.fx != 15))) {
				if (depth > floorHighestDepthLevel[floorIndex]
					|| (depth == floorHighestDepthLevel[floorIndex] && rand
						.nextBoolean())) {
					floorExitPaths[floorIndex] = path;
					floorHighestDepthLevel[floorIndex] = depth;
				}

				if (mazeDepth > floorHighestMazeDepthLevel[floorIndex]
					|| (mazeDepth == floorHighestMazeDepthLevel[floorIndex] && rand
						.nextBoolean())) {
					floorHighestMazeDepthLevel[floorIndex] = mazeDepth;
				}
			}
		}

		private boolean getCanAddMiniTower(Path path) {
			return (path.floor > 5);
		}

		private boolean getIsMiniTowerValid(MiniTower mtIn,
			Path path, EnumFacing dir) {
			boolean isValid = true;
			int[] boundsIn = mtIn.getBounds();
			List<MiniTower> mtOOB = new ArrayList<MiniTower>();

			for (MiniTower mt : miniTowersCheck) {
				int[] bounds = mt.getBounds();
				if (boundsIn[1] - 6 > bounds[4])
					mtOOB.add(mt);
				else if (((boundsIn[0] >= bounds[0] && boundsIn[0] <= bounds[3]) || (boundsIn[3] >= bounds[0] && boundsIn[3] <= bounds[3]))
					&& ((boundsIn[2] >= bounds[2] && boundsIn[2] <= bounds[5]) || (boundsIn[5] >= bounds[2] && boundsIn[5] <= bounds[5]))
					&& ((boundsIn[1] >= bounds[1] && boundsIn[1] <= bounds[4]) || (boundsIn[4] >= bounds[1] && boundsIn[4] <= bounds[4]))) {
					isValid = false;
					break;
				} else {
					isValid = true;
				}
			}

			for (MiniTower mtDel : mtOOB)
				miniTowersCheck.remove(mtDel);

			return isValid;
		}

		private int[] getMiniTowerConnData(int minX,
			int minZ, int maxX, int maxZ, EnumFacing dir,
			MiniTower mt) {
			int[] boundsA = new int[] { minX, 0, minZ,
				maxX, 0, maxZ }, boundsB = mt.getBounds(), data = null;
			Axis axis;
			if ((axis = dir.getAxis()) == mt.dir.getAxis()) {
				if (axis == Axis.X) {
					if (boundsA[5] > boundsB[5])
						data = new int[] {
							boundsA[2] - boundsB[5],
							EnumFacing.NORTH.ordinal(), -1 };
					else
						data = new int[] {
							boundsB[2] - boundsA[5],
							EnumFacing.SOUTH.ordinal(), 1 };
				} else {
					if (boundsA[3] > boundsB[3])
						data = new int[] {
							boundsA[0] - boundsB[3],
							EnumFacing.WEST.ordinal(), -1 };
					else
						data = new int[] {
							boundsB[0] - boundsA[3],
							EnumFacing.EAST.ordinal(), 1 };
				}
			}
			return data;
		}

		private void fillGaps() {
			int startX = ((hasXEntrance && hasOddX) || (!hasXEntrance && !hasOddX)) ? 2
				: 1;
			int startZ = ((hasXEntrance && hasOddZ) || (!hasXEntrance && !hasOddZ)) ? 1
				: 2;

			for (int y = 1; y < 4; y += 4) {
				for (int z = startZ; z < 15; z += 2) {
					for (int x = startX; x < 15; x += 2) {
						// if (Path.getPathAt(this, null, x, y, z) == null) {
						if (Path.getStateAt(this, x, y, z) == null) {
							ArrayList<EnumFacing> dirsList = Path
								.getDirsList(false, true,
									true);
							Iterator dirIterator = dirsList
								.iterator();
							EnumFacing dir;
							boolean gapFilled = false;
							int floor = (y / 6) + 1;
							while (dirIterator.hasNext()
								&& (dir = (EnumFacing) dirIterator
									.next()) != null
								&& !gapFilled) {
								Path path;
								int dirIndex;
								int dirAxis;
								int dirSign;
								int dist;
								if ((Path
									.getStateWithOffset(
										this, dir, 2, x,
										y + 1, z)) != null) {
									dirIndex = dir
										.getIndex();
									dirAxis = (int) Math
										.floor(dirIndex * 0.5);
									dirSign = dirIndex % 2 == 0 ? -1
										: 1;
									/*
									 * path.children.add(new Path(this, path,
									 * path.chain, x + (dirAxis == 1 ? dirSign *
									 * 2 : 0), y, z + (dirAxis == 2 ? dirSign *
									 * 2 : 0), dir.getOpposite()));
									 */
									dist = Path
										.getMaxDistance(
											this,
											dir,
											14,
											(dirAxis == 1 ? startZ == 1
												: startX == 1),
											x, y, z);
									if (Path
										.isPosValid(
											this,
											Path.getRelCoordsWithOffset(
												dir,
												dist + 2,
												x, y, z)))
										dist += 2;
									if (dist == 0)
										continue;
									for (int z2 = 0; z2 <= (dirAxis == 1 ? (dist >= 2 ? dist
										: 0)
										: 0); z2++) {
										for (int x2 = 0; x2 <= (dirAxis == 2 ? (dist >= 2 ? dist
											: 0)
											: 0); x2++) {
											for (int y2 = 1; y2 < 4; y2++)
												blockData[y2
													+ ((y - 1) * 6)][z
													+ (z2 * dirSign)][x
													+ (x2 * dirSign)] = air;// Blocks.red_sandstone.getDefaultState();
										}
									}
									gapFilled = true;
								}
							}
						}
					}
				}
			}
		}

		public void build(World worldIn, boolean setData) {
			final int ix = chunkX << 4, iz = chunkZ << 4, yLimit = floors * 6, minXZ = !isUnderground ? 1
				: 0, maxXZ = !isUnderground ? 14 : 15;
			final IBlockState stone = Blocks.stone
				.getDefaultState();
			final IBlockState downButton = ModBlocks.hiddenButton
				.getDefaultState().withProperty(
					BlockButton.FACING, EnumFacing.DOWN);
			Map<BlockPos, Tuple<Integer, Integer>> floorScannerPos = null;
			boolean addCircuitBreaker = false;
			int floor = 0;
			removeEntities(worldIn);
			for (int y = 0; y <= yLimit + 7; y++) {
				if (isUnderwater && y == yLimit)
					air = Blocks.air.getDefaultState();
				else if (y % 6 == 0 && y < yLimit) {
					floor++;
					floorChestItems[floor - 1] = new HashMap<ItemStack, Integer>();
					floorScannerPos = new HashMap<BlockPos, Tuple<Integer, Integer>>();
				}
				for (int z = minXZ; z <= maxXZ; z++) {
					for (int x = minXZ; x <= maxXZ; x++) {
						BlockPos pos = getPosFromCoords(ix,
							iz, x, y, z);
						IBlockState state = blockData[y][z][x];
						final boolean isEdge = isUnderground
							&& x == 0
							|| x == 15
							|| z == 0
							|| z == 15;
						if (y <= yLimit) {
							if ((state == null || (state != air
								&& state.getBlock() != Blocks.ladder
								&& isEdge && state
								.getBlock() != Blocks.torch))
								|| state == stone)
								state = isEdge ? wallBlock_external
									: y != yLimit ? wallBlock
										: floorBlock;
						} else if (isUnderground
							&& y >= yLimit + 6)
							pos = y == yLimit + 6 ? pos
								.up(6) : new BlockPos(ix
								+ x, baseY + 6, iz + z);
						if (state != null) {
							if (state.getBlock() != doorBlock) {
								if (state.getBlock() == Blocks.melon_block) {
									addCircuitBreaker = true;
									state = MTPuzzle.redstone;
								} else if (state.getBlock() == Blocks.pumpkin)
									state = air;
								else if (y % 6 == 5
									&& y < yLimit
									&& state == air)
									state = wallBlock;
								worldIn.setBlockState(pos,
									state, 2);
								if (state.getBlock() == Blocks.dispenser) {
									addDispenserContents(
										worldIn, pos, x, y,
										z, floor - 1,
										y == yLimit + 2);
									if (y == yLimit + 2)
										worldIn
											.setBlockState(
												pos, state,
												2);
								} else if (state.getBlock() instanceof BlockChest) {
									Path path = Path
										.getPathAt(this,
											null, x, y, z);
									Iterator iterator = initChest(
										worldIn, pos, path,
										0).iterator();
									int mazeDepth = path
										.getMazeDepth();
									while (iterator
										.hasNext()) {
										ItemStack stack = (ItemStack) iterator
											.next();
										floorChestItems[floor - 1]
											.put(stack,
												mazeDepth);
									}
								} else if (state.getBlock() == Blocks.mob_spawner) {
									TileEntityMobSpawner spawner = (TileEntityMobSpawner) worldIn
										.getTileEntity(pos);
									try {
										spawner
											.getSpawnerBaseLogic()
											.setEntityName(
												getEntityNameForSpawn(
													floor,
													isUnderwater));
									} catch (NullPointerException e) {
										e = null;
									}
								} else if (state.getBlock() == Blocks.web)
									worldIn
										.setTileEntity(
											pos,
											new TileEntityWebSpiderSpawner(
												difficulty));
								else if (state.getBlock() == MazeTowers.BlockHiddenButton
									&& state
										.getValue(
											BlockHiddenButton.FACING)
										.getAxisDirection() == AxisDirection.NEGATIVE)
									worldIn
										.setBlockState(
											pos.offset(state
												.getValue(
													BlockHiddenButton.FACING)
												.getOpposite()),
											wallBlock, 0);
								else if (state.getBlock() == Blocks.wall_sign)
									worldIn
										.setBlockState(
											pos.offset(state
												.getValue(
													BlockWallSign.FACING)
												.getOpposite()),
											wallBlock, 0);
								else if (addCircuitBreaker) {
									worldIn
										.setTileEntity(
											pos,
											new TileEntityCircuitBreaker());
									addCircuitBreaker = false;
								} else if (state.getBlock() instanceof BlockItemScanner) {
									Path path = Path
										.getPathAt(this,
											null, x, y, z);
									floorScannerPos
										.put(
											pos,
											new Tuple(
												path.pathIndex,
												path.getMazeDepth()));
									worldIn
										.setBlockState(
											pos.offset(state
												.getValue(
													BlockItemScanner.FACING)
												.getOpposite()),
											wallBlock, 0);
								}
							} else {
								if (state
									.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
									ItemDoor
										.placeDoor(
											worldIn,
											pos,
											state
												.getValue(BlockDoor.FACING),
											doorBlock);
							}
						}
						final int groundY;

						if (y == 0
							&& (groundY = MTUtils
								.getGroundY(worldIn,
									ix + x, baseY, iz + z,
									1, isUnderwater) + 1) < baseY) {
							for (int y2 = 0; y2 <= baseY
								- groundY; y2++) {
								try {
									worldIn
										.setBlockState(
											pos.down(y2),
											!isEdge ? floorBlock
												: wallBlock_external,
											0);
								} catch (NullPointerException e) {
									e = null;
								}
							}
						}
					}
				}

				if (y % 6 == 5 && y < yLimit
					&& !floorScannerPos.isEmpty()/*
												  * && ! floorChestItems [ (int)
												  * Math . floor (y / 6)].
												  * isEmpty ()
												  */) {
					List<Entry<BlockPos, Tuple<Integer, Integer>>> floorScannerPosEntries = new ArrayList<Entry<BlockPos, Tuple<Integer, Integer>>>(
						floorScannerPos.entrySet());
					List<Entry<ItemStack, Integer>> floorChestItemsEntries = new ArrayList<Entry<ItemStack, Integer>>(
						floorChestItems[(int) Math
							.floor(y / 6)].entrySet());

					Collections.sort(
						floorScannerPosEntries,
						scannerPosComparator.reversed());
					Collections.sort(
						floorChestItemsEntries,
						chestItemsComparator.reversed());

					LinkedHashMap<BlockPos, Tuple> floorScannerPosSorted = new LinkedHashMap<BlockPos, Tuple>(
						floorScannerPosEntries.size());
					LinkedHashMap<ItemStack, Integer> floorChestItemsSorted = new LinkedHashMap<ItemStack, Integer>(
						floorChestItemsEntries.size());

					for (Entry<BlockPos, Tuple<Integer, Integer>> entry : floorScannerPosEntries) {
						floorScannerPosSorted.put(entry
							.getKey(), entry.getValue());
					}

					for (Entry<ItemStack, Integer> entry : floorChestItemsEntries) {
						floorChestItemsSorted.put(entry
							.getKey(), entry.getValue());
					}

					Iterator iterator = floorScannerPosSorted
						.keySet().iterator();
					ArrayList<ItemStack> stackList = new ArrayList<ItemStack>(
						floorChestItemsSorted.keySet());
					while (iterator.hasNext()) {
						BlockPos pos;
						TileEntityItemScanner te;
						Tuple<Integer, Integer> curVals;
						int mazeDepth;
						pos = (BlockPos) iterator.next();
						curVals = floorScannerPosSorted
							.get(pos);
						mazeDepth = (int) curVals
							.getSecond();
						te = (TileEntityItemScanner) worldIn
							.getTileEntity(pos);
						while (!stackList.isEmpty()
							&& mazeDepth < floorChestItemsSorted
								.get(stackList.get(0)))
							stackList.remove(0);
						if (te != null
							&& te instanceof TileEntityItemScanner) {
							Path scannerPath = paths
								.get((int) curVals
									.getFirst() - 1);
							boolean doesStateMatch;
							doesStateMatch = worldIn
								.getBlockState(
									pos = (new BlockPos(ix
										+ scannerPath.ix,
										pos.getY() + 1,
										iz + scannerPath.iz)
										.offset(scannerPath.dir)))
								.getBlock() == Blocks.wall_sign;
							if (!stackList.isEmpty())
								te.generateRandomKeyStackFromList(stackList);
							if (te.getKeyStack() == null)
								te.setKeyStack(new ItemStack(
									Items.emerald));
							if (doesStateMatch) {
								String keyStackDisplayName = te
									.getKeyStack()
									.getDisplayName();
								((TileEntitySign) worldIn
									.getTileEntity(pos)).signText[2] = new ChatComponentText(
									difficulty < 3 ? keyStackDisplayName
										: MTUtils
											.getEncodedItemName(
												keyStackDisplayName,
												rand));
							}
						} else
							break;
					}
				}
			}
			final Path entrancePath = paths.get(0);
			final EnumFacing entranceDir = entrancePath
				.getDir();
			IBlockState signState = Blocks.wall_sign
				.getDefaultState().withProperty(
					BlockWallSign.FACING,
					entrancePath.getDir().getOpposite());
			TileEntitySign te;
			String infoString = getInfoString();
			BlockPos signPos = new BlockPos(ix
				+ (entrancePath.ix), baseY + 3, iz
				+ (entrancePath.iz)).offset(entrancePath
				.getDir().getOpposite());
			BlockPos thresholdPos;
			this.signPos = signPos;
			worldIn.setBlockState(signPos, signState, 2);
			te = (TileEntitySign) worldIn
				.getTileEntity(signPos);
			te.signText[0] = new ChatComponentText(
				towerTypeName);
			te.signText[1] = new ChatComponentText(
				"Maze Tower");
			if (isUnderground)
				te.signText[2] = new ChatComponentText(
					"(Underground)");
			te.signText[3] = new ChatComponentText(
				infoString);
			if (worldIn.isRemote)
				te.setEditable(false);
			worldIn.setTileEntity(signPos, te);
			worldIn.setBlockState(thresholdPos = signPos
				.down(2),
				MazeTowers.BlockMazeTowerThreshold
					.getDefaultState(), 2);
			worldIn.setBlockState(thresholdPos.offset(
				entranceDir, 2),
				MazeTowers.BlockMazeTowerThreshold
					.getDefaultState(), 2);
			long startTime = System.nanoTime();
			buildStairCase(worldIn, signPos.down(2).offset(
				entranceDir), entranceDir.getOpposite());
			long endTime = (System.nanoTime() - startTime) / 1000000;
			MazeTowers.network.sendToDimension(
				new PacketDebugMessage(
					"Staircase build took " + endTime
						+ " ms"), worldIn.provider
					.getDimensionId());
			signPos = getPosFromCoords(ix, iz, exitX,
				yLimit + 1, exitZ).offset(
				hasXEntrance ? EnumFacing.NORTH
					: EnumFacing.WEST);
			worldIn.setBlockState(signPos,
				signState = Blocks.standing_sign
					.getDefaultState().withProperty(
						BlockStandingSign.ROTATION,
						hasXEntrance ? 12 : 0), 2);
			te = (TileEntitySign) worldIn
				.getTileEntity(signPos);
			try {
				if (!isUnderground) {
					te.signText[1] = new ChatComponentText(
						"Exit");
					te.signText[2] = new ChatComponentText(
						"\u2193\u2193\u2193");
				} else {
					te.signText[2] = new ChatComponentText(
						"Exit");
					te.signText[1] = new ChatComponentText(
						"\u2191\u2191\u2191");
				}
				worldIn.setTileEntity(signPos, te);
			} catch (NullPointerException e) {
				e = null;
			}

			final int dimId = worldIn.provider
				.getDimensionId() + 1, minY = baseY
				+ (!isUnderground ? 0 : -(yLimit + 6)), maxY = baseY
				+ (yLimit + 7);
			/*
			 * MazeTowers.network.sendToDimension(new PacketUpdateBlockRange(
			 * new BlockPos(ix, minY, iz), new BlockPos(ix + 15, maxY, iz +
			 * 15)), worldIn.provider.getDimensionId());
			 */
			if (setData)
				MazeTowerData
					.setBlockBreakabilityData(
						blockBreakabilityData = MTUtils
							.getBlockBreakabilityData(blockData),
						dimId, towerIndex);

			for (MiniTower mt : miniTowers) {
				mt.build(worldIn);
				if (setData) {
					BitSet[][][] bbd = new BitSet[][][] {
						mt.blockBreakabilityData,
						mt.blockBreakabilityDataBridge,
						mt.blockBreakabilityDataBridgeC };
					MazeTowerData.setMiniTowerData(
						new int[] { mt.minX, mt.baseY,
							mt.minZ, mt.maxX, mt.height,
							mt.maxZ, mt.minXBridge,
							mt.baseYBridge, mt.minZBridge,
							mt.maxXBridge, mt.heightBridge,
							mt.maxZBridge, mt.minXBridgeC,
							mt.baseYBridgeC,
							mt.minZBridgeC, mt.maxXBridgeC,
							mt.heightBridgeC,
							mt.maxZBridgeC }, dimId,
						towerIndex, mt.miniTowerIndex);
					MazeTowerData
						.setBlockBreakabilityDataMini(bbd,
							dimId, towerIndex,
							mt.miniTowerIndex);
				}
			}
		}

		public void buildShell(World worldIn) {
			final int ix = chunkX << 4;
			final int iz = chunkZ << 4;
			final int yLimit = floors * 6;
			int floor = floors + 1;
			if (isUnderwater)
				air = Blocks.air.getDefaultState();
			for (int y = yLimit + 7; y >= 0; y--) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						final boolean isEdge = x == 0
							|| x == 15 || z == 0 || z == 15;
						if (isEdge || y == yLimit) {
							IBlockState state = blockData[y][z][x];
							BlockPos pos = getPosFromCoords(
								ix, iz, x, y, z);
							if (isUnderground
								&& y >= yLimit + 6)
								pos = y == yLimit + 6 ? pos
									.up(6) : new BlockPos(
									ix + x, baseY + 6, iz
										+ z);
							worldIn.setBlockState(pos,
								state, 2);
						}
					}
				}

				if (isUnderwater && y == yLimit)
					air = Blocks.water.getDefaultState();
				else if (y % 6 == 0 && y < yLimit) {
					floor--;
				}
			}
		}

		private void buildStairCase(World worldIn,
			BlockPos pos, EnumFacing dir) {
			int maxY = pos.getY();
			final IBlockState air = Blocks.air
				.getDefaultState(), stairState = stairsBlock[dir == EnumFacing.WEST ? 0
				: 1], torch = !isUnderwater ? Blocks.torch
				.getDefaultState() : Blocks.sea_lantern
				.getDefaultState();
			final EnumFacing sideDirR = dir.rotateY(), sideDirL = dir
				.rotateYCCW();
			boolean isFirstStair = true;
			IBlockState state;
			while ((state = worldIn.getBlockState(pos = pos
				.down().offset(dir))) == air
				|| !state.getBlock().isFullBlock()) {
				final int minY = MTUtils.getGroundY(
					worldIn, pos.getX(), --maxY,
					pos.getZ(), 1, isUnderwater
						&& (!isUnderground || baseY < 63));
				BlockPos pos2 = pos.offset(sideDirR, 2);

				for (int s = 0; s < 3; s++) {
					pos2 = pos2.offset(sideDirL);
					for (int y = -1; y <= maxY - minY; y++) {
						if (s != 1)
							worldIn.setBlockState(pos2
								.down(y), wallBlock, 2);
						else if (y != -1)
							worldIn.setBlockState(pos2
								.down(y),
								y != 0 ? floorBlock
									: stairState, 2);
						else if (minY < 0)
							return;
					}
				}

				if (isFirstStair) {
					worldIn.setBlockState(pos2.up(2),
						torch, 2);
					if (this.isUnderwater
						&& this.isUnderground)
						worldIn.setBlockState(pos2.up(2)
							.offset(sideDirR), Blocks.water
							.getDefaultState());
					worldIn.setBlockState(pos2.up(2)
						.offset(sideDirR, 2), torch, 2);
					isFirstStair = false;
				}
			}
		}

		private void addDispenserContents(World worldIn,
			BlockPos pos, int x, int y, int z,
			int floorIndex, boolean isLadderDispenser) {
			TileEntity te;
			if ((te = worldIn.getTileEntity(pos)) != null
				&& te instanceof TileEntityDispenser) {
				TileEntityDispenser ted = (TileEntityDispenser) te;
				LockCode code = new LockCode("fhqwhgads");
				if (!isLadderDispenser) {
					ItemStack[] projectiles;
					int amount = dataMap[y][z][x], stackCount = 1, difficulty = getDifficulty(floorIndex + 1), projectileChance = rand
						.nextInt(10) + 1;
					te = null;
					if (difficulty < 3
						|| (difficulty < 6 && projectileChance < (difficulty - 2) * 3))
						projectiles = new ItemStack[] { new ItemStack(
							Items.arrow, amount) };
					else {
						stackCount = Math.min(amount, 9);
						List<Integer> projectileIndexes = new ArrayList<Integer>();
						ItemStack[] projectileList = new ItemStack[] {
							new ItemStack(Items.potionitem,
								1, 16394), // Slowness
										   // I
										   // (short)
							new ItemStack(Items.potionitem,
								1, 16392), // Weakness
										   // I
										   // (short)
							new ItemStack(Items.potionitem,
								1, 16388), // Poison
										   // I
										   // (short)
							new ItemStack(Items.potionitem,
								1, 16396), // Harming
										   // I
							new ItemStack(Items.potionitem,
								1, 16458), // Slowness
										   // I
										   // (long)
							new ItemStack(Items.potionitem,
								1, 16456), // Weakness
										   // I
										   // (long)
							new ItemStack(Items.potionitem,
								1, 16452), // Poison
										   // I
										   // (long)
							new ItemStack(Items.potionitem,
								1, 16420), // Poison
										   // II
										   // (short)
							new ItemStack(Items.potionitem,
								1, 16484), // Poison
										   // II
										   // (long)
							new ItemStack(Items.potionitem,
								1, 16428), // Harming
										   // II
							new ItemStack(
								MazeTowers.ItemExplosiveArrow) };

						projectiles = new ItemStack[stackCount];

						for (int p = 0; p < stackCount; p++) {
							if (p == 0 || difficulty == 9
								|| p < (difficulty - 5)) {
								int subtractFromRange = Math
									.max(difficulty - 6, 0);
								projectileChance = rand
									.nextInt(4 - subtractFromRange)
									+ (difficulty - subtractFromRange);
								projectileIndexes
									.add(projectileChance);
							} else
								projectileChance = projectileIndexes
									.get(rand
										.nextInt(projectileIndexes
											.size()));
							try {
								projectiles[p] = projectileList[projectileChance];
							} catch (ArrayIndexOutOfBoundsException e) {
								e = null;
							}
						}
					}
					for (int c = 0; c < stackCount; c++)
						ted.setInventorySlotContents(c,
							projectiles[c]);
				} else {
					final ItemStack ladderStack = new ItemStack(
						Blocks.ladder, 64);
					for (int l = 0; l < 9; l++)
						ted.setInventorySlotContents(l,
							ladderStack);
				}
				ted.setLockCode(code);
				worldIn.setTileEntity(pos, ted);
			}

		}

		public void addMobSpawners(int floor) {
			int spawnCount = 0;
			final int spawnY = (floor * 6) - 1;
			final IBlockState spawnerState = Blocks.mob_spawner
				.getDefaultState();
			List<int[]> spawnerCoordsList = new ArrayList<int[]>();
			spawnerCoordsList
				.add(new int[] { spawnY, 4, 4 });
			spawnerCoordsList
				.add(new int[] { spawnY, 4, 11 });
			spawnerCoordsList
				.add(new int[] { spawnY, 11, 4 });
			spawnerCoordsList.add(new int[] { spawnY, 11,
				11 });
			spawnerCoordsList.add(new int[] { spawnY,
				rand.nextBoolean() ? 7 : 8,
				rand.nextBoolean() ? 7 : 8 });

			switch (getDifficulty(floor)) {
			case 1:
				spawnCount = rand.nextInt(2);
				break;
			case 2:
				spawnCount = rand.nextInt(3);
				break;
			case 3:
				spawnCount = 1 + rand.nextInt(2);
				break;
			case 4:
				spawnCount = 1 + rand.nextInt(3);
				break;
			case 5:
				spawnCount = 2 + rand.nextInt(2);
				break;
			case 6:
				spawnCount = 2 + rand.nextInt(3);
				break;
			case 7:
				spawnCount = 3 + rand.nextInt(2);
				break;
			case 8:
				spawnCount = 3 + rand.nextInt(3);
				break;
			case 9:
				spawnCount = 4 + rand.nextInt(2);
				break;
			default:
				spawnCount = 5;
			}

			for (int s = 0; s < spawnCount; s++) {
				final int randIndex = rand
					.nextInt(spawnerCoordsList.size());
				final int[] coords = spawnerCoordsList
					.get(randIndex);
				if (Path.getStateAt(this, coords) == air) {
					boolean isAccessible = (difficulty <= 3 || (rand
						.nextInt(difficulty - 2) == 0));
					blockData[coords[0]][coords[1]][coords[2]] = spawnerState;
					if (isAccessible) {
						blockData[coords[0] - 1][coords[1]][coords[2]] = Blocks.pumpkin
							.getDefaultState();
						if (blockData[coords[0] - 2][coords[1]][coords[2]] == air)
							blockData[coords[0] - 2][coords[1]][coords[2]] = Blocks.pumpkin
								.getDefaultState();
					}
				}
				spawnerCoordsList.remove(randIndex);
			}
		}

		private void addMiniTower(MiniTower mt) {
			miniTowers.add(mt);
			miniTowersCheck.add(mt);
			if (!isUnderground && mt.floor > floors - 3) {
				MiniTower beaconMiniTower;
				if (beaconMiniTowerIndex == -1
					|| (beaconMiniTowerIndex != mt.miniTowerIndex && mt.floor
						+ mt.floors > (beaconMiniTower = miniTowers
						.get(beaconMiniTowerIndex)).floor
						+ beaconMiniTower.floors))
					setBeaconMiniTower(mt);
			}
		}

		public void addChests() {
			int chestCount;
			int floorCount;
			Iterator pathIterator;
			for (int f = 0; f < floors; f++) {
				int highestDepth = floorHighestDepthLevel[f];
				int highestMazeDepth = floorHighestMazeDepthLevel[f];
				if (floorDeadEndPaths[f] != null) {
					pathIterator = floorDeadEndPaths[f]
						.iterator();
					chestCount = 0;
					while (pathIterator.hasNext()) {
						Path path = (Path) pathIterator
							.next();
						if (path.getStateAt(this, path.fx,
							path.fy + 1, path.fz) == air) {
							boolean randBool = true;
							boolean usePath = false;
							if (path.mazeDepth == highestMazeDepth
								&& ((randBool = rand
									.nextBoolean()) || difficulty > 5))
								usePath = true;
							else if (path.depth == highestDepth
								&& ((randBool = rand
									.nextBoolean()) || difficulty > 4))
								usePath = true;
							else {
								int depthDiff = Math.min(
									highestDepth
										- path.depth, 5);
								usePath = true;
								for (int c = 0; c < depthDiff; c++) {
									if (rand.nextBoolean()) {
										usePath = false;
										break;
									}
								}
							}
							if (usePath) {
								IBlockState chestState = randBool ? getChestBlock(
									f + 1, 0)
									.getDefaultState()
									: Blocks.trapped_chest
										.getDefaultState();
								IBlockState[] chestStates = new IBlockState[] {
									chestState
										.withProperty(
											BlockChest.FACING,
											EnumFacing.NORTH),
									chestState
										.withProperty(
											BlockChest.FACING,
											EnumFacing.SOUTH),
									chestState
										.withProperty(
											BlockChest.FACING,
											EnumFacing.WEST),
									chestState
										.withProperty(
											BlockChest.FACING,
											EnumFacing.EAST) };
								path.setStateAt(path.fx,
									path.fy + 1, path.fz,
									chestStates[path
										.getDir()
										.getOpposite()
										.getIndex() - 2]);
								if (!randBool) {
									path.setStateAt(
										path.fx,
										path.fy,
										path.fz,
										Blocks.tnt
											.getDefaultState());
									if (path.floor != 1
										&& path.getStateAt(
											this, path.fx,
											path.fy - 1,
											path.fz) == air)
										path.setStateAt(
											path.fx,
											path.fy,
											path.fz,
											floorBlock);
								}
								chestCount++;
							}
						}
					}
				}
			}
		}

		public List<ItemStack> initChest(World world,
			BlockPos pos, Path path, int addToRarity) {
			if (path != null) {
				TileEntityChest chestTEC = (TileEntityChest) world
					.getTileEntity(pos);
				int rarity = Math.min(path.rarity
					+ addToRarity, 9);
				if (chestTEC != null) {
					ArrayList<ItemStack> items = new ArrayList<ItemStack>();
					WeightedRandomChestContent
						.generateChestContents(world.rand,
							chestInfo[rarity]
								.getItems(world.rand),
							chestTEC, chestInfo[rarity]
								.getCount(world.rand));
					for (int i = 0; i < chestTEC
						.getSizeInventory(); i++) {
						ItemStack curStack;
						if ((curStack = chestTEC
							.getStackInSlot(i)) != null)
							items.add(curStack);
					}
					world.setTileEntity(pos, chestTEC);
					return items;
				}
			}

			return new ArrayList<ItemStack>();
		}

		private String getEntityNameForSpawn(int floor,
			boolean isUnderwater) {
			String entityName;

			final int diffIndex = getDifficulty(floor), indexPart = (int) (diffIndex * 0.375);

			final String[] mobList = !isUnderwater ? !isMushroomTower
				|| rand.nextInt(3) != 0 ? new String[] {
				"Zombie", "Skeleton", "Spider",
				"Silverfish", "Creeper", "CaveSpider",
				"Witch", "Endermite", "Enderman", "Blaze" }
				: new String[] { "MushroomCow" }
				: new String[] { "Guardian" };

			entityName = mobList[Math.min(rand
				.nextInt((diffIndex >> 1) + 1)
				+ (indexPart - (int) Math.min((rand
					.nextGaussian() * 1.5)
					+ rand.nextInt(3) + 1, indexPart))
				+ (diffIndex >> 1), mobList.length - 1)];

			return entityName;
		}

		private Block getChestBlock(int floor,
			int addToRarity) {
			int rareIndex = Math.min(getRarity(floor)
				+ addToRarity, 9);
			if (rareIndex < 4)
				return Blocks.chest;
			else if (rareIndex < 6)
				return MazeTowers.BlockIronChest;
			else if (rareIndex < 8)
				return MazeTowers.BlockGoldChest;
			else
				return MazeTowers.BlockDiamondChest;
		}

		public IBlockState getColourBlockState(Block block,
			EnumDyeColor dyeColor) {
			return colourBlockStates.get(block)[dyeColor
				.getDyeDamage()];
		}

		public BlockPos getPosFromCoords(int ix, int iz,
			int x, int y, int z) {
			int floorIndex = (int) Math.floor(y / 6), relY = y % 6;
			if (isUnderground)
				y = (-6 * floorIndex) + relY;
			return new BlockPos(ix + x, baseY + y, iz + z);
		}

		public EnumDyeColor[] getDyeColors() {
			return dyeColors;
		}

		private ItemStack getRandomItemFrameStack(int floor) {
			int rareIndex = Math.min(getRarity(floor), 9);
			return rareIndex < 4
				|| rand.nextInt(32 - (rareIndex - 4) * 5) != 0 ? chestInfo[rareIndex]
				.getOneItem(rand)
				: new ItemStack(ModItems.explosive_bow);
		}

		private int getRandomDyeColorIndex() {
			int dyeColorChance = rand.nextInt(16);
			return dyeColorChance < 12 ? 0
				: dyeColorChance != 15 ? 1 : 2;
		}

		public EnumDyeColor getRandomDyeColor() {
			return dyeColors[getRandomDyeColorIndex()];
		}

		public void setBeaconMiniTower(MiniTower miniTower) {
			if (beaconMiniTowerIndex != -1)
				miniTowers.get(beaconMiniTowerIndex)
					.setHasBeacon(false);
			miniTower.setHasBeacon(true);
			beaconMiniTowerIndex = miniTower.miniTowerIndex;
		}

		/*
		 * public void buildRoom(World worldIn) { for (int r = 0; r <
		 * rooms.size(); r++) { final Room room = rooms.get(r); final int ix =
		 * chunkX << 4; final int iz = chunkZ << 4; boolean isBaseRoom =
		 * room.minY == 0; for (int y = room.minY; y <= room.maxY; y++) { for
		 * (int z = room.minZ; z <= room.maxZ; z++) { for (int x = room.minX; x
		 * <= room.maxX; x++) { final BlockPos pos = new BlockPos(ix + x, baseY
		 * + y, iz + z); if (y == room.minY || y == room.maxY || x == room.minX
		 * || z == room.minZ || x == room.maxX || z == room.maxZ) {
		 * worldIn.setBlockState(pos, ceilBlock);
		 * 
		 * final int groundY; if (isBaseRoom && y == 0 && (groundY =
		 * MTUtils.getGroundY( worldIn, ix + x, baseY, iz + z, 1) + 1) < baseY,
		 * isUnderwater) { for (int y2 = 0; y2 <= baseY - groundY; y2++)
		 * worldIn.setBlockState(pos.down(y2), floorBlock); } } else
		 * worldIn.setBlockToAir(pos); } } } } }
		 */
	}

	public static class MiniTower {

		private final MazeTowerBase towerBase;
		private final MazeTower tower;
		private final Path path;
		private final EnumFacing dir;
		private EnumFacing dirC;
		private final int miniTowerIndex;
		private final int floor;
		private final int floors;
		private final int dist;
		private final int distC;
		private final int dirSign;
		private final int dirSignC;
		private final int connYDiff;
		private final int ix;
		private final int iz;
		private final int minX;
		private final int minZ;
		private int minXBridge;
		private int minZBridge;
		private int minXBridgeC;
		private int minZBridgeC;
		private final int minXSupport;
		private final int minZSupport;
		private final int maxX;
		private final int maxZ;
		private int maxXBridge;
		private int maxZBridge;
		private int maxXBridgeC;
		private int maxZBridgeC;
		private final int maxXSupport;
		private final int maxZSupport;
		private final int baseY;
		private final int height;
		private int baseYBridge;
		private int heightBridge;
		private int baseYBridgeC;
		private int heightBridgeC;
		private int baseYSupport;
		private int heightSupport;
		private final int[] frameXCoords;
		private final int[] frameZCoords;
		private static final int[][] fenceXCoords = new int[][] {
			new int[] { 1, 2, 2, 3, 4, 5, 6, 6, 7 },
			new int[] { 7, 6, 6, 5, 4, 3, 2, 2, 1 },
			new int[] { 7, 7, 8, 8, 8, 8, 8, 7, 7 },
			new int[] { 1, 1, 0, 0, 0, 0, 0, 1, 1 } };
		private static final int[][] fenceZCoords = new int[][] {
			new int[] { 7, 7, 8, 8, 8, 8, 8, 7, 7 },
			new int[] { 1, 1, 0, 0, 0, 0, 0, 1, 1 },
			new int[] { 1, 2, 2, 3, 4, 5, 6, 6, 7 },
			new int[] { 7, 6, 6, 5, 4, 3, 2, 2, 1 } };
		private static final int[] shelfXCoords = new int[] {
			3, 3, 7, 1 };
		private static final int[] shelfZCoords = new int[] {
			7, 1, 3, 3 };
		private final boolean isXDir;
		private final boolean isReverse;
		private final boolean connLadder;
		private final boolean isConnDownward;
		private final boolean hasItemFrame[];
		private final boolean hasFenceWall[];
		private final boolean hasFenceWallExtended[];
		private final boolean hasBookShelf[][];
		private final boolean hasFlowerPot[][];
		private boolean hasBeacon;
		private int dyeColorIndex;
		private MiniTower conn;
		private IBlockState[][][] stateMap;
		private BitSet[][] blockBreakabilityData;
		private IBlockState[][][] stateMapBridge;
		private BitSet[][] blockBreakabilityDataBridge;
		private IBlockState[][][] stateMapBridgeC;
		private BitSet[][] blockBreakabilityDataBridgeC;
		private IBlockState[][][] stateMapSupport;

		// private BitSet[][] blockBreakabilityDataSupport;

		private MiniTower(MazeTower tower, Path path,
			EnumFacing dir, int miniTowerIndex) {

			final int yMult = !tower.isUnderground ? 1 : -1, fullFenceWallChance = !tower.isMushroomTower ? rand
				.nextInt(36)
				: 4;
			int temp, minXConn = 0, minZConn = 0, maxXConn = 0, maxZConn = 0, distConn = 0;
			int[] connData = null;
			final boolean hasFullFenceWall = fullFenceWallChance < 3, hasFullFenceWallExtended = fullFenceWallChance == 0;
			EnumFacing[] dirs = EnumFacing.values();

			this.towerBase = tower;
			this.tower = tower;
			this.path = path;
			this.dir = dir;
			this.miniTowerIndex = miniTowerIndex;
			dyeColorIndex = tower.getRandomDyeColorIndex();
			floor = path.floor;
			isXDir = dir.getAxis() == Axis.X;
			baseY = tower.baseY + ((floor - 1) * 6) * yMult;
			dirSign = dir.getAxisDirection() == AxisDirection.POSITIVE ? 1
				: -1;
			conn = null;

			for (MiniTower mt : tower.miniTowersCheck) {
				if (mt.conn == null) {
					minXConn = path.fx
						+ (isXDir ? (mt.dist * dirSign)
							- (dirSign == -1 ? 9 : 0) : -4);
					minZConn = path.fz
						+ (!isXDir ? (mt.dist * dirSign)
							- (dirSign == -1 ? 9 : 0) : -4);
					maxXConn = minXConn + 9;
					maxZConn = minZConn + 9;
					if (mt.dir == dir
						&& ((minXConn > mt.maxX || maxXConn < mt.minX) || (minZConn > mt.maxZ || maxZConn < mt.minZ))) {
						mt.conn = this;
						conn = mt;
						distConn = conn.dist;
						connData = tower
							.getMiniTowerConnData(minXConn,
								minZConn, maxXConn,
								maxZConn, dir, mt);
						break;
					}
				}
			}

			dist = conn == null ? rand.nextInt(5) + 4
				: distConn;
			isReverse = rand.nextBoolean();
			floors = rand.nextInt(7) < 3 ? 1 : Math.min(
				rand.nextInt(7) + 3,
				!tower.isNetherTower ? 10 : ((int) Math
					.floor((127 - baseY) / 3)) - 1);
			height = (floors * 3) + 3;
			minXBridge = path.fx + (isXDir ? dirSign : -1);
			minZBridge = path.fz + (!isXDir ? dirSign : -1);
			maxXBridge = minXBridge
				+ (isXDir ? dist * dirSign : 2);
			maxZBridge = minZBridge
				+ (!isXDir ? dist * dirSign : 2);

			if (conn == null) {
				distC = 0;
				dirC = null;
				dirSignC = -1;
				connLadder = false;
				isConnDownward = false;
				connYDiff = 0;
				minX = path.fx
					+ (isXDir ? (dist * dirSign)
						- (dirSign == -1 ? 9 : 0) : -4);
				minZ = path.fz
					+ (!isXDir ? (dist * dirSign)
						- (dirSign == -1 ? 9 : 0) : -4);
				maxX = minX + 9;
				maxZ = minZ + 9;
			} else {
				int y1 = (height - 6) + ((floor - 1) * 6)
					* yMult, y2 = (conn.height - 6)
					+ ((conn.floor - 1) * 6) * yMult;
				// relHeight = conn.height - (3 * (conn.floors - floors));

				/*
				 * if (height > relHeight) { shortHeight = relHeight; tallHeight
				 * = height; } else { shortHeight = height; tallHeight =
				 * relHeight; }
				 */
				distC = connData[0];
				dirSignC = connData[2];
				minX = minXConn;
				minZ = minZConn;
				maxX = maxXConn;
				maxZ = maxZConn;
				isConnDownward = y1 > y2;

				if (isConnDownward) {
					baseYBridgeC = tower.baseY + y2;
					heightBridgeC = (y1 - y2) + 4;
					conn.dirC = EnumFacing.values()[connData[1]];
					dirC = conn.dirC.getOpposite();
					connYDiff = y1 - y2;
					connLadder = distC < connYDiff;
				} else {
					baseYBridgeC = tower.baseY + y1;
					heightBridgeC = (y2 - y1) + 4;
					dirC = EnumFacing.values()[connData[1]];
					conn.dirC = dirC.getOpposite();
					connYDiff = y2 - y1;
					connLadder = distC < connYDiff;
				}

				MazeTowers.network.sendToDimension(
					new PacketDebugMessage("Conn at floor "
						+ floor
						+ " (ladder: "
						+ (connLadder ? "true" : "false")
						+ ", downward: "
						+ (isConnDownward ? "true"
							: "false") + ", dir: "
						+ dirC.getName() + ")"), 0);

				if (dirC.getAxis() == Axis.X) {
					if (dirSignC == -1) {
						minXBridgeC = conn.maxX - 1;
						maxXBridgeC = minX;
					} else {
						minXBridgeC = maxX - 1;
						maxXBridgeC = conn.minX;
					}
					minZBridgeC = minZ + 3;
					maxZBridgeC = minZBridgeC + 2;
				} else {
					if (dirSignC == -1) {
						minZBridgeC = conn.maxZ - 1;
						maxZBridgeC = minZ;
					} else {
						minZBridgeC = maxZ - 1;
						maxZBridgeC = conn.minZ;
					}
					minXBridgeC = minX + 3;
					maxXBridgeC = minXBridgeC + 2;
				}
			}

			if (!tower.isUnderground) {
				minXSupport = this.minX + 3;
				minZSupport = this.minZ + 3;
				maxXSupport = this.maxX - 4;
				maxZSupport = this.maxZ - 4;
			} else {
				minXSupport = 0;
				minZSupport = 0;
				maxXSupport = 0;
				maxZSupport = 0;
			}

			ix = tower.chunkX << 4;
			iz = tower.chunkZ << 4;

			if (isXDir) {
				if (maxXBridge < minXBridge) {
					temp = minXBridge;
					minXBridge = --maxXBridge;
					maxXBridge = temp;
				}
				if (maxZBridgeC < minZBridgeC) {
					temp = minZBridgeC;
					minZBridgeC = --maxZBridgeC;
					maxZBridgeC = temp;
				}
			} else if (!isXDir) {
				if (maxZBridge < minZBridge) {
					temp = minZBridge;
					minZBridge = --maxZBridge;
					maxZBridge = temp;
				}
				if (maxXBridgeC < minXBridgeC) {
					temp = minXBridgeC;
					minXBridgeC = --maxXBridgeC;
					maxXBridgeC = temp;
				}
			}

			hasItemFrame = new boolean[4];
			hasFenceWall = new boolean[4];
			hasFenceWallExtended = new boolean[4];
			hasBookShelf = new boolean[4][3];
			hasFlowerPot = new boolean[4][3];

			frameXCoords = new int[] { minX + 4, minX + 4,
				maxX - 2, minX + 1 };
			frameZCoords = new int[] { maxZ - 2, minZ + 1,
				minZ + 4, minZ + 4 };

			for (int d = 0; d < 4; d++) {
				if (floors != 1 || dirs[d + 2] != dir) {
					final int itemFrameChance = dyeColorIndex == 0 ? 128
						: dyeColorIndex == 1 ? 32 : 8, bookShelfChance = rand
						.nextInt(32), fenceWallChance = !tower.isMushroomTower ? rand
						.nextInt(24) : 4;
					hasItemFrame[d] = rand
						.nextInt(itemFrameChance) == 0;
					hasFenceWall[d] = hasFullFenceWall
						|| fenceWallChance < 3;
					hasFenceWallExtended[d] = hasFullFenceWallExtended
						|| fenceWallChance == 0;
					if (rand.nextInt(4) == 0) {
						int s = 0;
						if (rand.nextInt(4) != 0) {
							for (; s < 3; s++) {
								hasBookShelf[d][s] = rand
									.nextInt(3) == 0;
								hasFlowerPot[d][s] = (hasBookShelf[d][s] && (s != 1 || !hasItemFrame[d])) ? rand
									.nextInt(4) == 0
									: false;
							}
						} else {
							for (; s < 3; s++) {
								hasBookShelf[d][s] = true;
								hasFlowerPot[d][s] = (s != 1 || !hasItemFrame[d]) ? rand
									.nextInt(4) == 0
									: false;
							}
						}
					}
				}
			}
		}

		private MiniTower(MazeTowerBase tower, int ix,
			int iz, int[] mainBounds, int[] bridgeBounds,
			int[] bridgeCBounds,
			BitSet[][] blockBreakabilityData,
			BitSet[][] blockBreakabilityDataBridge,
			BitSet[][] blockBreakabilityDataBridgeC,
			int miniTowerIndex) {
			this.towerBase = tower;
			this.tower = null;
			this.ix = ix;
			this.iz = iz;
			minX = mainBounds[0];
			baseY = mainBounds[1];
			minZ = mainBounds[2];
			maxX = mainBounds[3];
			height = mainBounds[4];
			maxZ = mainBounds[5];
			minXBridge = bridgeBounds[0];
			baseYBridge = bridgeBounds[1];
			minZBridge = bridgeBounds[2];
			maxXBridge = bridgeBounds[3];
			heightBridge = bridgeBounds[4];
			maxZBridge = bridgeBounds[5];
			minXBridge = bridgeCBounds[0];
			baseYBridge = bridgeCBounds[1];
			minZBridge = bridgeCBounds[2];
			maxXBridge = bridgeCBounds[3];
			heightBridge = bridgeCBounds[4];
			maxZBridge = bridgeCBounds[5];
			minXSupport = 0;
			baseYSupport = 0;
			minZSupport = 0;
			maxXSupport = 0;
			heightSupport = 0;
			maxZSupport = 0;
			this.blockBreakabilityData = blockBreakabilityData;
			this.blockBreakabilityDataBridge = blockBreakabilityDataBridge;
			this.blockBreakabilityDataBridgeC = blockBreakabilityDataBridgeC;
			this.miniTowerIndex = miniTowerIndex;
			path = null;
			dir = null;
			dyeColorIndex = 0;
			hasItemFrame = null;
			hasFenceWall = null;
			hasFenceWallExtended = null;
			hasBookShelf = null;
			hasFlowerPot = null;
			frameXCoords = null;
			frameZCoords = null;
			floor = 0;
			floors = 0;
			dist = 0;
			dirSign = 0;
			distC = 0;
			dirC = null;
			dirSignC = -1;
			connYDiff = 0;
			isXDir = false;
			isReverse = false;
			connLadder = false;
			isConnDownward = false;
		}

		public void build(World worldIn) {
			if (hasBeacon)
				setDyeColorIndex(0);
			final boolean hasCornerLamps = rand.nextInt(3) == 0;
			final int ix = tower.chunkX << 4, iz = tower.chunkZ << 4, difficulty = tower
				.getDifficulty(floor), diffIndex = Math
				.min(difficulty + dyeColorIndex, 9);
			final IBlockState air = Blocks.air
				.getDefaultState(), glass = tower
				.getColourBlockState(Blocks.stained_glass,
					tower.dyeColors[dyeColorIndex]), mineral;
			final BlockPos chestPos = new BlockPos(ix + 4
				+ minX, baseY + height - 4, iz + 4 + minZ);

			if (!hasBeacon)
				mineral = null;
			else if (diffIndex > 7)
				mineral = Blocks.diamond_block
					.getDefaultState();
			else if (diffIndex > 5)
				mineral = Blocks.gold_block
					.getDefaultState();
			else
				mineral = Blocks.iron_block
					.getDefaultState();

			stateMap = getStateMap(maxX - minX, height,
				maxZ - minZ, mineral, hasCornerLamps);
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < maxZ - minZ; z++) {
					for (int x = 0; x < maxX - minX; x++) {

						final BlockPos pos = new BlockPos(
							ix + x + minX, baseY + y, iz
								+ z + minZ);
						final Block curBlock;
						IBlockState state = stateMap[y][z][x];

						try {
							if (state != null) {
								curBlock = state.getBlock();

								if (!tower.isUnderwater
									&& y % 3 == 1) {
									EnumFacing facing;
									if (curBlock == Blocks.torch
										&& (facing = state
											.getValue(BlockTorch.FACING))
											.getAxisDirection() == AxisDirection.NEGATIVE)
										worldIn
											.setBlockState(
												pos.offset(facing
													.getOpposite()),
												tower.wallBlock,
												0);
								}

								worldIn.setBlockState(pos,
									state, 2);
							}
						} catch (Exception e) {
							e = null;
						}
					}
				}
			}

			if (!hasBeacon) {
				for (int y = 1; y < height - 5; y += 3) {

					if (rand.nextInt(difficulty) >= 1) {
						final BlockPos spawnerPos = new BlockPos(
							ix + 4 + minX, baseY + y, iz
								+ 4 + minZ);
						worldIn.setBlockState(spawnerPos,
							Blocks.mob_spawner
								.getDefaultState(), 0);
						TileEntityMobSpawner spawner = (TileEntityMobSpawner) worldIn
							.getTileEntity(spawnerPos);
						spawner
							.getSpawnerBaseLogic()
							.setEntityName(
								tower
									.getEntityNameForSpawn(
										floor,
										tower.isUnderwater));
					}
				}

				if (difficulty >= 9) {
					final IBlockState wire = Blocks.redstone_wire
						.getDefaultState();
					final int randDir = rand.nextInt(4);
					worldIn
						.setBlockState(
							chestPos
								.up(4)
								.offset(
									randDir == 0 ? EnumFacing.NORTH
										: randDir == 1 ? EnumFacing.WEST
											: randDir == 2 ? EnumFacing.WEST
												: EnumFacing.EAST,
									3), wire, 2);
				}

				for (int i = 0; i < 4; i++) {

					if (hasItemFrame[i]) {
						final BlockPos pos = new BlockPos(
							ix + frameXCoords[i], baseY
								+ height - 3, iz
								+ frameZCoords[i]);
						EntityItemFrame frame = new EntityItemFrame(
							worldIn, pos, EnumFacing
								.values()[i + 2]);
						frame
							.setDisplayedItem(tower
								.getRandomItemFrameStack(floor));
						worldIn.spawnEntityInWorld(frame);
					}

					if (hasFenceWall[i]) {
						int y = 0;
						BlockPos pos;
						for (; y < 3; y++) {
							for (int f = 0; f < fenceXCoords[i].length; f++) {
								final IBlockState state = (f != 4 && (!hasFenceWallExtended[i] || (f != 3 && f != 5)))
									|| y != 1 ? tower.fenceBlock
									: air;
								pos = new BlockPos(
									ix
										+ minX
										+ fenceXCoords[i][f],
									baseY + height - 5 + y,
									iz
										+ minZ
										+ fenceZCoords[i][f]);
								stateMap[height - 5 + y][fenceZCoords[i][f]][fenceXCoords[i][f]] = state;
								worldIn.setBlockState(pos,
									state, 2);
							}
						}

						for (y = -6; y <= -2; y += 4) {
							for (int xz = 0; xz <= 8; xz += 2) {
								if (xz != 4) {
									/*
									 * stateMap[height + y][fenceZCoords[i][xz]]
									 * [fenceXCoords[i][xz]] =
									 * tower.wallBlock_external;
									 */
									pos = new BlockPos(
										ix
											+ minX
											+ fenceXCoords[i][xz],
										baseY + height + y,
										iz
											+ minZ
											+ fenceZCoords[i][xz]);
									worldIn
										.setBlockState(
											pos,
											tower.wallBlock_external,
											2);
								}
							}
						}
					}
				}

				tower.initChest(worldIn, chestPos, path,
					dyeColorIndex);
			}
			/*
			 * MazeTowers.network.sendToDimension(new PacketUpdateBlockRange(
			 * new BlockPos(ix + minX, baseY, iz + minZ), new BlockPos(ix +
			 * maxX, baseY + (height - 1), iz + maxZ)),
			 * worldIn.provider.getDimensionId());
			 */
			buildBridge(worldIn);
			if (conn != null
				&& conn.miniTowerIndex < miniTowerIndex) {
				long startTime = System.nanoTime();
				buildBridgeC(worldIn);
				long endTime = (System.nanoTime() - startTime) / 1000000;
				MazeTowers.network.sendToDimension(
					new PacketDebugMessage(
						"Conn build took " + endTime
							+ " ms"), worldIn.provider
						.getDimensionId());
			}
			if (!tower.isUnderground)
				buildSupport(worldIn);
			blockBreakabilityData = MTUtils
				.getMTBlockBreakabilityData(stateMap);
			blockBreakabilityDataBridge = MTUtils
				.getMTBlockBreakabilityData(stateMapBridge);
			if (conn != null
				&& conn.miniTowerIndex < miniTowerIndex)
				blockBreakabilityDataBridgeC = MTUtils
					.getMTBlockBreakabilityData(stateMapBridgeC);
			/*
			 * if (!tower.isUnderground) blockBreakabilityDataSupport =
			 * MTUtils.getMTBlockBreakabilityData(stateMapSupport);
			 */
		}

		private void buildBridge(World worldIn) {

			final int ix = tower.chunkX << 4, iz = tower.chunkZ << 4, px = isXDir ? dirSign == 1 ? 0
				: (maxXBridge - minXBridge)
				: 1, pz = isXDir ? 1 : dirSign == 1 ? 0
				: (maxZBridge - minZBridge), sx = isXDir ? dirSign == 1 ? (maxXBridge - minXBridge) - 1
				: 1
				: 1, sz = isXDir ? 1
				: dirSign == 1 ? (maxZBridge - minZBridge) - 1
					: 1, diffIndex = Math.min(tower
				.getDifficulty(floor)
				+ dyeColorIndex, 9);
			final boolean isBarred = conn != null
				&& conn.path.mazeDepth > path.mazeDepth
				&& diffIndex > 3 ? rand.nextInt(Math.max(
				7 - diffIndex, 1)) == 0 : false, hasSensor = !tower.isUnderground
				&& !isBarred
				&& rand.nextInt(Math.max(diffIndex - 2, 1)) != 0;
			final IBlockState air = tower.air, bars = Blocks.iron_bars
				.getDefaultState(), sensor = Blocks.daylight_detector
				.getDefaultState();

			baseYBridge = baseY;
			heightBridge = !hasSensor ? 5 : 7;

			stateMapBridge = new IBlockState[heightBridge][(maxZBridge - minZBridge) + 1][(maxXBridge - minXBridge) + 1];

			for (int y = 0; y <= heightBridge - 1; y++) {
				for (int z = 0; z <= maxZBridge
					- minZBridge; z++) {
					for (int x = 0; x <= maxXBridge
						- minXBridge; x++) {
						final BlockPos pos = new BlockPos(
							ix + x + minXBridge,
							baseYBridge + y
								- (!hasSensor ? 0 : 2), iz
								+ z + minZBridge);
						final boolean isMedian = hasSensor
							&& y == 1;
						final boolean isPistonPos = x == px
							&& z == pz;
						final boolean isSensorPos = x == sx
							&& z == sz;
						if (hasSensor
							&& isMedian
							&& ((isXDir && z == 1 && x != sx
								+ dirSign) || (!isXDir
								&& x == 1 && z != sz
								+ dirSign))) {
							if (isPistonPos) {
								final IBlockState piston = MazeTowers.BlockMemoryPiston
									.getDefaultState()
									.withProperty(
										BlockMemoryPistonBase.FACING,
										EnumFacing.UP);
								worldIn.setBlockState(pos,
									piston, 2);
							} else if (isSensorPos) {
								worldIn.setBlockState(pos,
									sensor, 2);
								stateMapBridge[y][z][x] = sensor;
							} else {
								final IBlockState redstone = Blocks.redstone_wire
									.getDefaultState();
								worldIn.setBlockState(pos,
									redstone, 2);
								stateMapBridge[y][z][x] = redstone;
							}
						} else if (y != 0
							&& y != heightBridge - 1
							&& (!hasSensor || y != 2)
							&& (!isBarred || (x != px || z != pz))) {
							if (((isXDir && z == 1) || (!isXDir && x == 1))
								&& (!isPistonPos || y != 3))
								worldIn.setBlockState(pos,
									air, 2);
							else {
								worldIn
									.setBlockState(
										pos,
										tower.wallBlock_external,
										2);
								stateMapBridge[y][z][x] = tower.wallBlock_external;
							}
						} else {
							if ((isXDir && z == 1)
								|| (!isXDir && x == 1)) {
								final IBlockState state;
								if (!isBarred)
									state = y == 0
										|| (hasSensor && y == 2) ? tower.floorBlock
										: tower.ceilBlock;
								else
									state = y != 2 ? tower.wallBlock
										: bars;
								worldIn.setBlockState(pos,
									state, 2);
								stateMapBridge[y][z][x] = state;
							} else {
								worldIn
									.setBlockState(
										pos,
										tower.wallBlock_external,
										2);
								stateMapBridge[y][z][x] = tower.wallBlock_external;
							}
						}
					}
				}
			}
		}

		private void buildBridgeC(World worldIn) {

			final int ix = tower.chunkX << 4, iz = tower.chunkZ << 4, lx = !isXDir ? (dirSignC == 1 && isConnDownward)
				|| (dirSignC == -1 && !isConnDownward) ? (maxXBridgeC - minXBridgeC) - 1
				: 1
				: 1, lz = !isXDir ? 1
				: (dirSignC == 1 && isConnDownward)
					|| (dirSignC == -1 && !isConnDownward) ? (maxZBridgeC - minZBridgeC) - 1
					: 1, fx = maxXBridgeC - minXBridgeC, fz = maxZBridgeC
				- minZBridgeC;
			int minY = 0;
			final IBlockState air = tower.air, ladder = !connLadder ? null
				: Blocks.ladder.getDefaultState()
					.withProperty(BlockLadder.FACING, dirC), carpetA = tower
				.getColourBlockState(Blocks.carpet,
					tower.dyeColors[dyeColorIndex]), carpetB = tower
				.getColourBlockState(
					Blocks.carpet,
					conn.tower.dyeColors[conn.dyeColorIndex]);

			stateMapBridgeC = new IBlockState[heightBridgeC][(maxZBridgeC - minZBridgeC) + 1][(maxXBridgeC - minXBridgeC) + 1];

			for (int z = 0; z <= fz; z++) {
				for (int x = 0; x <= fx; x++) {
					final int tunnelCoord = isXDir ? z : x, tunnelEndCoord = isXDir ? fz
						: fx;
					final boolean isTunnelStartEnd = (tunnelCoord == 0 || tunnelCoord == tunnelEndCoord), isTunnelEndBelowLadder = isTunnelStartEnd
						&& ((((isConnDownward && dirSignC == 1) || (!isConnDownward && dirSignC == -1)) && tunnelCoord == 0) || (((!isConnDownward && dirSignC == 1) || (isConnDownward && dirSignC == -1)) && tunnelCoord == tunnelEndCoord)), isTunnelEndAboveLadder = isTunnelStartEnd
						&& !isTunnelEndBelowLadder;
					minY = !connLadder && connYDiff != 0 ? (int) Math
						.floor(connYDiff
							* (tunnelCoord / connYDiff))
						: 0;
					if (!connLadder
						&& ((isConnDownward && dirSignC == 1) || (!isConnDownward && dirSignC == -1)))
						minY = connYDiff - minY;
					for (int y = 0; y < heightBridgeC; y++) {
						if (y >= minY) {
							boolean isCarpetAPos = false, isCarpetBPos = false;
							final boolean isCarpetPos = (!connLadder ? isCarpetAPos = y == minY + 1
								: ((isCarpetAPos = y == minY + 1) || (isCarpetBPos = y == heightBridgeC - 4))), isLadder = connLadder
								&& z == lz && x == lx, isBelowLadder = y < 3, isAboveLadder = y > heightBridgeC - 4;
							boolean isEntrancePos;
							final BlockPos pos = new BlockPos(
								ix + x + minXBridgeC,
								baseYBridgeC + y, iz + z
									+ minZBridgeC);
							if (isLadder && !isBelowLadder
								&& !isAboveLadder) {
								worldIn.setBlockState(pos,
									ladder, 2);
								stateMapBridgeC[y][z][x] = ladder;
							} else if (y != minY
								&& y != heightBridgeC - 1
								&& (!connLadder || (connLadder && ((isBelowLadder && !isTunnelEndBelowLadder) || (isAboveLadder && !isTunnelEndAboveLadder))))) {
								if (((isXDir && x == 1) || (!isXDir && z == 1))
									&& (!connLadder
										|| isLadder || (isBelowLadder || isAboveLadder))) {
									final boolean isConnPos = (isCarpetAPos && !isConnDownward)
										|| (isCarpetBPos && isConnDownward);
									worldIn
										.setBlockState(
											pos,
											!isCarpetPos ? air
												: isConnPos ? carpetA
													: carpetB,
											2);
								} else {
									worldIn
										.setBlockState(
											pos,
											tower.wallBlock_external,
											2);
									stateMapBridgeC[y][z][x] = tower.wallBlock_external;
								}
							} else {
								if (!isTunnelStartEnd
									&& ((isXDir && x == 1) || (!isXDir && z == 1))) {
									worldIn
										.setBlockState(
											pos,
											y == minY ? tower.floorBlock
												: tower.ceilBlock,
											2);
									stateMapBridgeC[y][z][x] = tower.ceilBlock;
								} else {
									worldIn
										.setBlockState(
											pos,
											tower.wallBlock_external,
											2);
									stateMapBridgeC[y][z][x] = tower.wallBlock_external;
								}
							}
						}
					}
				}
			}
		}

		private void buildSupport(World worldIn) {

			final int ix = tower.chunkX << 4;
			final int iz = tower.chunkZ << 4;

			baseYSupport = MTUtils.getGroundY(worldIn, ix
				+ (minXSupport + 1), baseY - 1, iz
				+ (minZSupport + 1), 1, true);
			heightSupport = baseY - baseYSupport;
			stateMapSupport = new IBlockState[heightSupport][(maxZSupport - minZSupport) + 1][(maxXSupport - minXSupport) + 1];

			for (int y = heightSupport - 1; y > 0; y--) {
				for (int z = 0; z <= maxZSupport
					- minZSupport; z++) {
					for (int x = 0; x <= maxXSupport
						- minXSupport; x++) {
						final BlockPos pos = new BlockPos(
							ix + x + minXSupport,
							baseYSupport + y, iz + z
								+ minZSupport);
						final boolean isXMid = x == 1;
						final boolean isZMid = z == 1;
						if (isXMid || isZMid) {
							worldIn
								.setBlockState(
									pos,
									tower.wallBlock_external,
									2);
							stateMapSupport[y - 1][z][x] = tower.wallBlock_external;
						}
					}
				}
			}
		}

		private IBlockState[][][] getStateMap(int width,
			int height, int depth, IBlockState mineral,
			boolean hasCornerLamps) {
			final EnumDyeColor dyeColor = tower
				.getDyeColors()[dyeColorIndex];
			final EnumFacing facing = isReverse ? dir
				.getOpposite() : dir;
			final EnumFlowerType[] flowerTypes = EnumFlowerType
				.values();
			final IBlockState[][][] stateMap = new IBlockState[height + 1][depth][width];
			final IBlockState carpet = tower
				.getColourBlockState(Blocks.carpet,
					dyeColor), air = Blocks.air
				.getDefaultState(), wall = tower.wallBlock, wire = Blocks.redstone_wire
				.getDefaultState(), window = tower
				.getColourBlockState(
					Blocks.stained_glass_pane, dyeColor), topGlass = tower
				.getColourBlockState(Blocks.stained_glass,
					dyeColor), glass2 = !hasBeacon
				|| tower.beaconGlassColors.length == 1 ? null
				: tower.getColourBlockState(
					Blocks.stained_glass,
					tower.beaconGlassColors[0]), glass3 = !hasBeacon
				|| tower.beaconGlassColors.length != 3 ? wire
				: tower.getColourBlockState(
					Blocks.stained_glass,
					tower.beaconGlassColors[2]), bookShelf = Blocks.bookshelf
				.getDefaultState(), tnt = Blocks.tnt
				.getDefaultState();
			IBlockState chest = tower.getChestBlock(floor,
				dyeColorIndex).getDefaultState();
			IBlockState flowerPot = Blocks.flower_pot
				.getDefaultState();
			final IBlockState[][][] stairsMap = MTStateMaps
				.getStateMap(facing, "MINI_TOWER_STAIRS",
					tower.wallBlock,
					tower.wallBlock_external,
					tower.floorBlock, tower.ceilBlock,
					carpet, topGlass, glass2, glass3,
					window, mineral, dyeColorIndex), topMap = MTStateMaps
				.getStateMap(facing, "MINI_TOWER_TOP_"
					+ (dyeColorIndex + 1), tower.wallBlock,
					tower.wallBlock_external,
					tower.floorBlock, tower.ceilBlock,
					carpet, topGlass, glass2, glass3,
					window, mineral, dyeColorIndex);
			final IBlockState[][] baseMap = MTStateMaps
				.getStateMap(facing, "MINI_TOWER_BASE",
					tower.wallBlock,
					tower.wallBlock_external,
					tower.floorBlock, tower.ceilBlock,
					carpet, topGlass, glass2, glass3,
					window, mineral, dyeColorIndex)[0], roofMap = MTStateMaps
				.getStateMap(facing, "MINI_TOWER_ROOF_"
					+ (dyeColorIndex + 1), tower.wallBlock,
					tower.wallBlock_external,
					tower.floorBlock, tower.ceilBlock,
					carpet, topGlass, glass2, glass3,
					window, mineral, dyeColorIndex)[!hasCornerLamps ? 0
				: 1], roofWireMap = MTStateMaps
				.getStateMap(facing,
					"MINI_TOWER_ROOF_WIRE",
					tower.wallBlock,
					tower.wallBlock_external,
					tower.floorBlock, tower.ceilBlock,
					carpet, topGlass, glass2, glass3,
					window, mineral, dyeColorIndex)[0];
			EnumFacing entranceDir = dir;
			final int stairsYLimit = height - 5, difficulty = tower
				.getDifficulty(floor);
			int xCoord = 0, zCoord = 0, x = 0, y = 0, z = 0;
			int[][] topStairCoords = null;

			for (; y < height; y++) {
				if (y != 0 && y < stairsYLimit) {
					stateMap[y] = stairsMap[(y - 1)
						% stairsMap.length];
					if (y == stairsYLimit - 1) {
						if (!hasBeacon) {
							chest = chest.withProperty(
								BlockChest.FACING,
								entranceDir = MTStateMaps
									.getTopStairDir(
										stairsMap[y % 12])
									.getOpposite());
							xCoord = 4 + (entranceDir == EnumFacing.WEST ? -1
								: entranceDir == EnumFacing.EAST ? 1
									: 0);
							zCoord = 4 + (entranceDir == EnumFacing.NORTH ? -1
								: entranceDir == EnumFacing.SOUTH ? 1
									: 0);
							roofMap[zCoord][xCoord] = tower.ceilBlock;
							roofWireMap[zCoord][xCoord] = wire;
						} else
							chest = Blocks.beacon
								.getDefaultState();
						if (floors != 1)
							topStairCoords = MTStateMaps
								.getTopStaircaseCoords(stateMap[y]);
					}
				} else if (y != 0 && y < height - 2) {
					for (int i = 0; i < 4; i++) {
						for (int s = 0; s < 3; s++) {
							if (hasBookShelf[i][s]) {
								topMap[y - stairsYLimit][shelfZCoords[i]
									+ (i < 2 ? 0 : s)][shelfXCoords[i]
									+ (i < 2 ? s : 0)] = y != stairsYLimit + 2 ? bookShelf
									: !hasFlowerPot[i][s] ? air
										: flowerPot
											.withProperty(
												BlockFlowerPot.LEGACY_DATA,
												rand.nextInt(16));
							}
						}
					}
					if (y != stairsYLimit)
						stateMap[y] = topMap[y == stairsYLimit + 1 ? 1
							: 2];
					else {
						stateMap[y] = topMap[0];
						if (floors != 1) {
							stateMap[y][topStairCoords[0][0]][topStairCoords[0][1]] = air;
							stateMap[y][topStairCoords[1][0]][topStairCoords[1][1]] = air;
							stateMap[y][topStairCoords[2][0]][topStairCoords[2][1]] = air;
							stateMap[y][topStairCoords[3][0]][topStairCoords[3][1]] = air;
						}
					}

					if (y == stairsYLimit + 1) {
						if (y == 2)
							chest = !hasBeacon ? chest
								.withProperty(
									BlockChest.FACING,
									entranceDir = dir
										.getOpposite())
								: Blocks.beacon
									.getDefaultState();
						stateMap[y][4][4] = chest;
					}
				} else if (y == 0)
					stateMap[y] = baseMap;
				else if (y == height - 2) {
					stateMap[y] = roofMap;
					if (floors == 1) {
						xCoord = 4 + (entranceDir == EnumFacing.WEST ? -1
							: entranceDir == EnumFacing.EAST ? 1
								: 0);
						zCoord = 4 + (entranceDir == EnumFacing.NORTH ? -1
							: entranceDir == EnumFacing.SOUTH ? 1
								: 0);
						stateMap[y][zCoord][xCoord] = tower.ceilBlock;
					}
				} else {
					stateMap[y] = roofWireMap;
					if (!hasBeacon) {
						if (floors == 1)
							stateMap[y][zCoord][xCoord] = wire;
						if ((difficulty > 2 && rand
							.nextInt(24 - (difficulty * 2)) == 0)) {
							if (difficulty != 9) {
								final int repetitions = difficulty < 7 ? 1
									: difficulty == 7 ? 2
										: difficulty == 8 ? 4
											: 16;
								for (int t = 0; t < repetitions; t++) {
									final int tntX;
									final int tntZ;
									if (difficulty == 8) {
										tntX = t % 2 == 0 ? 2
											: 6;
										tntZ = ((int) Math
											.floor(t / 2) == 0) ? 2
											: 6;
									} else {
										tntX = 2 + ((rand
											.nextInt(3)) * 2);
										tntZ = tntX != 4 ? 2 + (rand
											.nextBoolean() ? 0
											: 4)
											: 4;
									}
									stateMap[y][tntX][tntZ] = tnt;
								}
							} else {
								stateMap[y][1][3] = tnt;
								stateMap[y][1][5] = tnt;
								stateMap[y][3][1] = tnt;
								stateMap[y][3][7] = tnt;
								stateMap[y][5][1] = tnt;
								stateMap[y][5][7] = tnt;
								stateMap[y][7][3] = tnt;
								stateMap[y][7][5] = tnt;
							}
						}
					}
				}
			}

			return stateMap;
		}

		public IBlockState[][][][] getStateMaps() {
			return new IBlockState[][][][] { stateMap,
				stateMapBridge, stateMapBridgeC };
		}

		public boolean getPosBreakability(BlockPos pos) {
			int x = pos.getX(), y = pos.getY(), z = pos
				.getZ();
			boolean posInMainBounds = false;
			boolean posInBridgeBounds = false;
			boolean posInBounds = (posInMainBounds = (x >= ix
				+ minX
				&& x <= ix + maxX
				&& z >= iz + minZ
				&& z <= iz + maxZ && y >= baseY && y <= baseY
				+ height))
				|| (posInBridgeBounds = (x >= ix
					+ minXBridge
					&& x <= ix + maxXBridge
					&& z >= iz + minZBridge
					&& z <= iz + maxZBridge
					&& y >= baseYBridge && y <= baseYBridge
					+ heightBridge))
				|| /*
				    * (posInSupportBounds = (x >= ix + minXSupport && x <= ix +
				    * maxXSupport && z >= iz + minZSupport && z <= iz +
				    * maxZSupport && y >= towerBase.baseY + baseYSupport && y <=
				    * towerBase.baseY + baseYSupport + heightSupport) ||
				    */
				(x >= ix + minXBridgeC
					&& x <= ix + maxXBridgeC
					&& z >= iz + minZBridgeC
					&& z <= iz + maxZBridgeC
					&& y >= baseYBridgeC && y <= baseYBridgeC
					+ heightBridgeC);
			if (posInBounds) {
				x -= (ix + (posInMainBounds ? minX
					: posInBridgeBounds ? minXBridge
						: minXBridgeC));
				z -= (iz + (posInMainBounds ? minZ
					: posInBridgeBounds ? minZBridge
						: minZBridgeC));
				y -= (posInMainBounds ? baseY
					: (posInBridgeBounds ? baseYBridge
						+ (5 - heightBridge) : baseYBridgeC));

				BitSet[][] data = posInMainBounds ? blockBreakabilityData
					: posInBridgeBounds ? blockBreakabilityDataBridge
						: blockBreakabilityDataBridgeC;
				try {
					return data[y][z].get(x);
				} catch (ArrayIndexOutOfBoundsException e) {
					e = null;
				}
			}
			return true;
		}

		public IBlockState getStateAtPos(BlockPos pos) {
			int x = pos.getX(), y = pos.getY(), z = pos
				.getZ();
			boolean posInMainBounds = false;
			boolean posInBridgeBounds = false;
			boolean posInBounds = (posInMainBounds = (x >= ix
				+ minX
				&& x <= ix + maxX
				&& z >= iz + minZ
				&& z <= iz + maxZ && y >= baseY && y <= baseY
				+ height))
				|| (posInBridgeBounds = (x >= ix
					+ minXBridge
					&& x <= ix + maxXBridge
					&& z >= iz + minZBridge
					&& z <= iz + maxZBridge
					&& y >= baseYBridge && y <= baseYBridge
					+ heightBridge))
				||
				/*
				 * (posInSupportBounds = (x >= ix + minXSupport && x <= ix +
				 * maxXSupport && z >= iz + minZSupport && z <= iz + maxZSupport
				 * && y >= tower.baseY + baseYSupport && y <= tower.baseY +
				 * baseYSupport + heightSupport) ||
				 */
				(x >= ix + minXBridgeC
					&& x <= ix + maxXBridgeC
					&& z >= iz + minZBridgeC
					&& z <= iz + maxZBridgeC
					&& y >= baseYBridgeC && y <= baseYBridgeC
					+ heightBridgeC);
			if (posInBounds) {
				x -= (ix + minX);
				z -= (iz + minZ);
				y -= (posInMainBounds ? baseY
					: tower.baseY
						+ (posInBridgeBounds ? baseYBridge
							+ (5 - heightBridge)
							: baseYBridgeC));

				IBlockState[][][] stateMap = posInMainBounds ? this.stateMap
					: posInBridgeBounds ? stateMapBridge
						: stateMapBridgeC;
				try {
					IBlockState state = stateMap[y][z][x];
					return state;
				} catch (ArrayIndexOutOfBoundsException e) {
					e = null;
				}

			}
			return null;
		}

		public int[] getBounds() {
			return new int[] { minX, baseY, minZ, maxX,
				baseY + height, maxZ };
		}

		public int[] getFullBounds() {
			int minX = (this.minX < minXBridge) ? this.minX
				: minXBridge;
			int minZ = (this.minZ < minZBridge) ? this.minZ
				: minZBridge;
			int maxX = (this.maxX > maxXBridge) ? this.maxX
				: maxXBridge;
			int maxZ = (this.maxZ > maxZBridge) ? this.maxZ
				: maxZBridge;

			return new int[] { minX, tower.baseY, minZ,
				maxX, baseY + height, maxZ };
		}

		public int getFloors() {
			return floors;
		}

		public void setDyeColorIndex(int dyeColorIndex) {
			this.dyeColorIndex = dyeColorIndex;
		}

		public void setHasBeacon(boolean hasBeacon) {
			this.hasBeacon = hasBeacon;
		}
	}

	private static class Path {

		private final MazeTower tower;
		private final EnumFacing dir;
		private final boolean isFloorEntrance;
		private final boolean isTowerEntrance;
		private final int ix;
		private final int iy;
		private final int iz;
		private final int fx;
		private final int fy;
		private final int fz;
		private final int floor;
		private final int dirIndex;
		private final int dirSign;
		private final int pathIndex;
		private final int difficulty;
		private final int rarity;
		private final BlockPos pos;
		private boolean hasMtp;
		private boolean isDeadEnd;
		private int dirAxis;
		private int maxDistance;
		private int distance;
		private int depth;
		private int mazeDepth;
		private Path parent;
		private ArrayList<MTPuzzle> mtp = null;
		private ArrayList<Path> chain;
		private ArrayList<Path> children;
		private Stack<EnumFacing> reservedDirs;

		private Path(MazeTower tower, Path parentPath,
			ArrayList pathChain, int ix, int iy, int iz,
			EnumFacing facing) {
			this.tower = tower;
			this.parent = parentPath;
			this.chain = pathChain;
			floor = (int) Math.floor(iy / 6) + 1;
			isTowerEntrance = parentPath == null;
			isFloorEntrance = isTowerEntrance
				|| parentPath.floor == floor - 1;
			isDeadEnd = !isFloorEntrance;
			depth = !isTowerEntrance ? parent.depth + 1 : 1;
			mazeDepth = !isTowerEntrance ? parent.mazeDepth
				: 1;
			this.chain.add(this);
			this.ix = ix;
			this.iy = iy;
			this.iz = iz;
			pathIndex = !isTowerEntrance ? tower.paths
				.get(tower.paths.size() - 1).pathIndex + 1
				: 1;
			pos = new BlockPos(ix, iy, iz);
			children = new ArrayList<Path>();
			reservedDirs = new Stack<EnumFacing>();
			hasMtp = !isFloorEntrance && parent.hasMtp;

			if (facing == null)
				dir = getDir();
			else {
				if (isFloorEntrance)
					dir = facing;
				else
					dir = ((maxDistance = getMaxDistance(
						tower, facing, 14, false, ix,
						iy + 1, iz)) >= (!hasMtp ? 2 : 4)) ? facing
						: null;
			}

			if (dir != null) {
				dirIndex = dir.getIndex();
				dirAxis = (int) Math.floor(dirIndex * 0.5);
				dirSign = dirIndex % 2 == 0 ? -1 : 1;
				/*
				 * distance = !isEntrance ? Math.max((int)
				 * Math.floor((Math.min(rand.nextInt(maxDistance) + 1,
				 * rand.nextInt(3) + 3)) * 0.5) * 2, !hasMtp ? 2 : 4) : 3;
				 */
				distance = !isTowerEntrance ? Math
					.max(
						(int) Math
							.floor((rand
								.nextInt(maxDistance) + 1) * 0.5) * 2,
						!hasMtp ? 2 : 4)
					: 3;
				difficulty = tower.getDifficulty(floor);
				rarity = tower.getRarity(floor);
				int[] fcoords = getRelCoordsWithOffset(dir,
					distance);
				fx = fcoords[2];
				fy = fcoords[0];
				fz = fcoords[1];

				tower.addPath(this);

				if (!isTowerEntrance && !parent.isDeadEnd)
					parent.isDeadEnd = false;

				if (hasMtp) {
					mtp = new ArrayList<MTPuzzle>();
					int mtpCount = 1;// (rand.nextInt(tower.floors) < 3 ? 2 :
									 // 3);
					for (int p = 0; p < mtpCount
						&& (mtp.isEmpty() || mtp.get(mtp
							.size() - 1) != null); p++) {
						MTPuzzle newMtp = getMtp();
						if (newMtp != null) {
							mtp.add(newMtp);
							mazeDepth += newMtp
								.getMazeDepthValue();
						}
					}
					hasMtp = false;
				} else if (!isFloorEntrance
					&& rand.nextInt(7) < 2)
					this.hasMtp = true;

				for (int y = 1; y < 4; y++) {
					for (int d = 0; d <= distance; d++)
						setPathWithOffset(d, y);
				}

				// if (true) {
				int dist = rand.nextInt(distance);
				int windowX = ix
					+ (dir.getAxis() == Axis.X ? dist : 0);
				int windowZ = iz
					+ (dir.getAxis() == Axis.Z ? dist : 0);
				EnumFacing sideDir = rand.nextBoolean() ? dir
					.rotateY()
					: dir.rotateYCCW();
				Path checkPath = getPathWithOffset(tower,
					this, sideDir, distance >> 1, windowX,
					iy, windowZ);
				IBlockState checkState = getStateAt(tower,
					windowX, iy + 1, windowZ);
				Block checkBlock;
				boolean isXAxis = dir.getAxis() == Axis.X;
				if (checkPath != null
					&& ((checkPath.depth > depth + 2 && (checkState != null && checkState
						.getBlock() != Blocks.ladder)) || (checkPath = getPathWithOffset(
						tower, this, (sideDir = sideDir
							.getOpposite()), distance >> 1,
						windowX, iy, windowZ)) != null
						&& (checkPath.depth > depth + 2
							&& (checkState = getStateAt(
								tower, windowX, iy + 1,
								windowZ)) != null
							&& (checkBlock = checkState
								.getBlock()) != Blocks.ladder
							&& checkBlock != Blocks.ladder && !(checkBlock instanceof BlockItemScanner)))) {
					BlockPos windowPos = new BlockPos(
						windowX, iy, windowZ)
						.offset(sideDir);
					setStateAt(windowPos.getX(), iy + 2,
						windowPos.getZ(), Blocks.glass_pane
							.getDefaultState());
				}
				// }

				EnumFacing miniTowerDir;
				MiniTower miniTower;
				boolean isMiniTowerValid = false;
				final int mirrorX = fx < 8 ? fx : 15 - fx, mirrorZ = fz < 8 ? fz
					: 15 - fz, addToMiniTowerChance = (int) Math
					.ceil(Math.sqrt(mirrorX * mirrorZ) * 0.5);
				if (rand
					.nextInt((21 + addToMiniTowerChance)
						- floor) == 0
					&& tower.getCanAddMiniTower(this)
					&& (miniTowerDir = getMiniTowerDir()) != null
					&& (isMiniTowerValid = tower
						.getIsMiniTowerValid(
							(miniTower = new MiniTower(
								tower, this, miniTowerDir,
								tower.miniTowers.size())),
							this, miniTowerDir)))
					tower.addMiniTower(miniTower);

				if ((isDeadEnd = (children.size() == 0 && !isMiniTowerValid))) {
					if (tower.floorDeadEndPaths[floor - 1] == null)
						tower.floorDeadEndPaths[floor - 1] = new ArrayList<Path>();
					tower.floorDeadEndPaths[floor - 1]
						.add(this);
				} else if (isFloorEntrance
					&& tower.isUnderground
					&& !isTowerEntrance) {
					setStateWithOffset(0, 4, 0, tower.air);
					setStateWithOffset(0, 5, 0,
						Blocks.pumpkin.getDefaultState());
				}

				tower.checkPathDepth(this, floor - 1,
					depth, mazeDepth);
			} else {
				dirIndex = 0;
				dirAxis = -1;
				dirSign = 0;
				distance = 0;
				difficulty = 0;
				rarity = 0;
				fy = iy;
				fz = iz;
				fx = ix;
				chain.remove(this);
			}
		}

		public Path newFloor() {
			boolean isLastFloor = floor == tower.floors;
			EnumFacing ladderDir, exitDir = tower.hasXEntrance ? EnumFacing.EAST
				: EnumFacing.SOUTH;
			IBlockState ladder = Blocks.ladder
				.getDefaultState().withProperty(
					BlockLadder.FACING,
					ladderDir = dir.getOpposite()), ladderExit = Blocks.ladder
				.getDefaultState().withProperty(
					BlockLadder.FACING, exitDir), button, piston;
			if (isLastFloor) {
				tower.exitDir = ladderDir.getOpposite();
				final int chestY = tower.blockData.length - 7;
				final IBlockState chestState = tower
					.getChestBlock(tower.floors, 0)
					.getDefaultState().withProperty(
						BlockChest.FACING, tower.exitDir);
				setStateAt(6, chestY - 1, 6,
					tower.floorBlock);
				setStateAt(9, chestY - 1, 6,
					tower.floorBlock);
				setStateAt(6, chestY - 1, 9,
					tower.floorBlock);
				setStateAt(9, chestY - 1, 9,
					tower.floorBlock);
				setStateAt(6, chestY, 6, chestState);
				setStateAt(9, chestY, 6, chestState);
				setStateAt(6, chestY, 9, chestState);
				setStateAt(9, chestY, 9, chestState);
			}
			if (tower.isUnderground) {
				final boolean rotatePiston = true, switchRotation = rotatePiston
					&& (dir == EnumFacing.NORTH || dir == EnumFacing.EAST);// !isPosValid(tower,
				// getRelCoordsWithOffset(dir, 1, fx, fy, fz));
				final Path behindPath = getPathWithOffset(
					tower, this, dir.getOpposite(), 1, ix,
					iy, iz);
				EnumFacing pistonDir;
				boolean rotatePistonL = rotatePiston
					&& rand.nextBoolean();
				if (rotatePiston) {
					if (!isPosValid(
						tower,
						getRelCoordsWithOffset(
							pistonDir = ((rotatePistonL && !switchRotation) || (!rotatePistonL && switchRotation)) ? dir
								.rotateYCCW()
								: dir.rotateY(), 1, fx, fy,
							fz)))
						rotatePistonL = !rotatePistonL;
				}
				if (pistonDir == null)
					pistonDir = dir.getOpposite();
				button = MazeTowers.BlockHiddenButton
					.getDefaultState()
					.withProperty(BlockHiddenButton.FACING,
						pistonDir);
				piston = MazeTowers.BlockMemoryPiston
					.getDefaultState().withProperty(
						BlockMemoryPistonBase.FACING,
						pistonDir);
				// setStateWithOffset(0, 2, 0,
				// Blocks.redstone_block.getDefaultState(), true);
				setStateWithOffset(0, 1, 0, button, true);
				setStateWithOffset(!rotatePiston ? 1 : 0,
					0, !rotatePiston ? 0
						: rotatePistonL ? -1 : 1, piston,
					true);
				// setStateWithOffset(0, -1, 0,
				// Blocks.pumpkin.getDefaultState(), true);
				// setStateWithOffset(0, -2, 0,
				// Blocks.pumpkin.getDefaultState(), true);
				if (!rotatePiston) {
					if (behindPath == null
						|| behindPath == this)
						setStateWithOffset(-1, 0, 0,
							tower.air, false);
					else
						behindPath.setStateWithOffset(1, 0,
							0, tower.air, true);
				} else
					setStateWithOffset(0, 0,
						rotatePistonL ? 1 : -1, tower.air,
						true);
			}

			for (int y = 0; y < (!isLastFloor ? 9 : 6); y++) {
				// int[] coords = getRelCoordsWithOffset(dir, distance);
				/*
				 * if (isPosValid(coords[2], coords[0], coords[1]))
				 * setStateWithOffset(distance + 1, y, 0, tower.wallBlock);
				 */
				if (y == 5) {
					// addFloorMedianAccess(coords[2], coords[1], false);
					tower.addMobSpawners(floor);
				}
				if (!tower.isUnderground && y > 0
					&& (y < 7 || isLastFloor))
					setStateWithOffset(0, y, 0, ladder,
						true);
				if (floor != 1 || y > 6) {
					setStateAt(tower.exitX, y
						+ ((floor - 1) * 6), tower.exitZ,
						ladderExit);
					if (floor != 1
						&& y % 6 == 2
						&& getStateWithOffset(
							tower,
							tower.hasXEntrance ? EnumFacing.EAST
								: EnumFacing.SOUTH, 2,
							tower.exitX, y
								+ ((floor - 1) * 6),
							tower.exitZ) == tower.air)
						setStateAt(tower.exitX
							+ (tower.hasXEntrance ? 1 : 0),
							y + ((floor - 1) * 6),
							tower.exitZ
								+ (!tower.hasXEntrance ? 1
									: 0), Blocks.glass_pane
								.getDefaultState());
				} else if ((!tower.isUnderground && y >= 4)
					|| (tower.isUnderground && y == 0)) {
					if (!tower.isUnderground) {
						if (tower.hasXEntrance) {
							if (y == 4) {
								setStateAt(tower.exitX, y
									+ ((floor - 1) * 6),
									tower.exitZ - 1,
									Blocks.air
										.getDefaultState());
								setStateAt(tower.exitX, y
									+ ((floor - 1) * 6),
									tower.exitZ,
									tower.ceilBlock);
								setStateAt(
									tower.exitX,
									y + ((floor - 1) * 6),
									tower.exitZ + 1,
									(piston = MazeTowers.BlockMemoryPiston
										.getDefaultState())
										.withProperty(
											BlockMemoryPistonBase.FACING,
											EnumFacing.NORTH));
							} else if (y == 5) {
								setStateAt(
									tower.exitX,
									y + ((floor - 1) * 6),
									tower.exitZ,
									(button = MazeTowers.BlockHiddenButton
										.getDefaultState())
										.withProperty(
											BlockHiddenButton.FACING,
											EnumFacing.NORTH));
							}
						} else {
							if (y == 4) {
								setStateAt(tower.exitX - 1,
									y + ((floor - 1) * 6),
									tower.exitZ, Blocks.air
										.getDefaultState());
								setStateAt(tower.exitX, y
									+ ((floor - 1) * 6),
									tower.exitZ,
									tower.ceilBlock);
								setStateAt(
									tower.exitX + 1,
									y + ((floor - 1) * 6),
									tower.exitZ,
									(piston = MazeTowers.BlockMemoryPiston
										.getDefaultState())
										.withProperty(
											BlockMemoryPistonBase.FACING,
											EnumFacing.WEST));
							} else if (y == 5) {
								setStateAt(
									tower.exitX,
									y + ((floor - 1) * 6),
									tower.exitZ,
									(button = MazeTowers.BlockHiddenButton
										.getDefaultState())
										.withProperty(
											BlockHiddenButton.FACING,
											EnumFacing.WEST));
							}
						}
					} else
						setStateAt(tower.exitX, y
							+ ((floor - 1) * 6),
							tower.exitZ, ladderExit);
				}
			}
			if (isLastFloor) {
				final IBlockState air = Blocks.air
					.getDefaultState();
				int[] endEntranceCoords = getRelCoordsWithOffset(
					dir, distance, ix, iy + 6, iz);
				if (!tower.isUnderground) {
					setStateAt(endEntranceCoords[2],
						endEntranceCoords[0],
						endEntranceCoords[1], ladder);
					setStateAt(endEntranceCoords[2],
						endEntranceCoords[0] + 1,
						endEntranceCoords[1], air);
					setStateAt(tower.exitX,
						endEntranceCoords[0], tower.exitZ,
						ladderExit);
					setStateAt(tower.exitX,
						endEntranceCoords[0] + 1,
						tower.exitZ, ladderExit);
				} else {
					for (int l = 1; l < 6; l++) {
						if (l != 4)
							setStateAt(tower.exitX,
								endEntranceCoords[0] + l,
								tower.exitZ, ladderExit);
					}
				}
			}
			return !isLastFloor ? new Path(tower, this,
				chain, fx, fy + 6, fz, null) : null;
		}

		private void addFallTrapHole(int x, int z,
			boolean isFatal) {
			int floor = this.floor - 1;
			int y = (floor * 6) - 1;
			Block fluid = (tower.difficulty < 4
				|| (tower.difficulty == 4 && rand
					.nextBoolean()) ? Blocks.water
				: tower.difficulty < 7
					|| rand.nextBoolean() ? Blocks.lava
					: MazeTowers.BlockChaoticSludge);
			final IBlockState air = Blocks.air
				.getDefaultState();

			if (!tower.isUnderground)
				addFloorMedianAccess(x, z, true);

			if (isFatal) {
				if (tower.isUnderground) {
					y += 12;
					setStateAt(x, y, z, fluid
						.getDefaultState());
					setStateAt(x, y - 1, z, fluid
						.getDefaultState());
					setStateAt(x, y - 1, z, air);
					setStateAt(x, y - 2, z, Blocks.glass
						.getDefaultState());
				} else {
					setStateAt(x, y - 1, z, fluid
						.getDefaultState());
					setStateAt(x, y - 2, z, air);
					setStateAt(x, y - 3, z, air);
					setStateAt(x, y - 4, z, air);
				}
			}
		}

		private void addFloorMedianAccess(int x, int z,
			boolean isFallTrap) {
			int floor = this.floor - (isFallTrap ? 1 : 0);
			int y = (floor * 6)
				- (!tower.isUnderground ? 1 : -11);
			addFloorMedianAccess(x, y, z, isFallTrap);
		}

		private void addFloorMedianAccess(int x, int y,
			int z, boolean isFallTrap) {
			// if (getStateAt(tower, x + 1, y, z) == air)
			// setStateAt(x + 1, y, z, tower.wallBlock);
			// if (getStateAt(tower, x - 1, y, z) == air)
			// setStateAt(x - 1, y, z, tower.wallBlock);
			// if (getStateAt(tower, x + 1, y, z + 1) == air)
			// setStateAt(x + 1, y, z + 1, tower.wallBlock);
			// if (getStateAt(tower, x - 1, y, z + 1) == air)
			// setStateAt(x - 1, y, z + 1, tower.wallBlock);
			// if (getStateAt(tower, x + 1, y, z - 1) == air)
			// setStateAt(x + 1, y, z - 1, tower.wallBlock);
			// if (getStateAt(tower, x - 1, y, z - 1) == air)
			// setStateAt(x - 1, y, z - 1, tower.wallBlock);
			// if (getStateAt(tower, x, y, z + 1) == air)
			// setStateAt(x, y, z + 1, tower.wallBlock);
			// if (getStateAt(tower, x, y, z - 1) == air)
			// setStateAt(x, y, z - 1, tower.wallBlock);
			setStateAt(x, y, z, Blocks.air
				.getDefaultState());
		}

		private EnumFacing getDir() {
			if (dir != null)
				return dir;
			else {
				EnumFacing facing = null;
				IBlockState[][][] data = tower.blockData;
				int prevdirAxis = parent.dirAxis;
				int minDist = !hasMtp ? 2 : 4;
				List<EnumFacing> dirs = getDirsList(false,
					prevdirAxis != 1, prevdirAxis != 2);
				Iterator rdIterator = parent.reservedDirs
					.iterator();
				while (rdIterator.hasNext())
					dirs.remove(rdIterator.next());
				Iterator dirsIterator = dirs.iterator();
				while (dirsIterator.hasNext()
					&& ((facing = ((EnumFacing) dirsIterator
						.next())) == null || (maxDistance = getMaxDistance(
						facing, minDist, false)) < minDist))
					;

				return facing;
			}
		}

		private EnumFacing getMiniTowerDir() {
			EnumFacing facing = null;
			EnumFacing entranceDir = tower.paths.get(0)
				.getDir();
			int coordA = tower.hasXEntrance ? fz : fx;
			int coordB = tower.hasXEntrance ? fx : fz;

			if (coordA <= 2)
				facing = tower.hasXEntrance ? EnumFacing.NORTH
					: EnumFacing.WEST;
			else if (coordA >= 13)
				facing = tower.hasXEntrance ? EnumFacing.SOUTH
					: EnumFacing.EAST;
			else if (coordB == 1)
				facing = entranceDir.getOpposite();
			else if (coordB == 13)
				facing = entranceDir;

			return facing;
		}

		private static ArrayList<EnumFacing> getDirsList(
			boolean ud, boolean ns, boolean ew) {
			ArrayList<EnumFacing> dirs = new ArrayList<EnumFacing>();
			if (ew) {
				dirs.add(EnumFacing.EAST);
				dirs.add(rand.nextInt(2), EnumFacing.WEST);
			}
			if (ns) {
				dirs.add(rand.nextInt(dirs.size() + 1),
					EnumFacing.SOUTH);
				dirs.add(rand.nextInt(dirs.size() + 1),
					EnumFacing.NORTH);
			}
			if (ud) {
				dirs.add(EnumFacing.UP);
				dirs.add(rand.nextInt(2)
					+ (dirs.size() - 1), EnumFacing.DOWN);
			}

			dirs.add(null);

			return dirs;
		}

		private EnumFacing getLadderDir() {
			ArrayList<EnumFacing> dirsList = getDirsList(
				false, true, true);
			Iterator dirsIterator;
			EnumFacing facing = null;
			boolean isValidDir = false;
			int[] coords = new int[3];

			dirsList.remove(dir);
			dirsIterator = dirsList.iterator();

			while (!isValidDir
				&& dirsIterator.hasNext()
				&& ((facing = (EnumFacing) dirsIterator
					.next()) == null || !isPosValid(tower,
					coords = getRelCoordsWithOffset(facing,
						1, fx, fy, fz)))) {
				int y = 1;
				for (; y <= 9; y++) {
					if (getStateAt(tower, coords[2],
						coords[0] + y, coords[1]) != tower.wallBlock)
						break;
				}
				if (y > 7)
					isValidDir = true;
			}
			return facing;
		}

		private int getMaxDistance(EnumFacing facing,
			int limit, boolean odd) {
			return getMaxDistance(tower, facing, limit,
				odd, ix, iy + 1, iz);
		}

		private static int getMaxDistance(MazeTower tower,
			EnumFacing facing, int limit, boolean odd,
			int x, int y, int z) {
			boolean validPos;
			int dist = odd ? 1 : 0;
			int facingIndex = (int) Math.floor(facing
				.getIndex() * 0.5);
			int facingSign = facing.getIndex() % 2 == 0 ? -1
				: 1;
			// int xLimit = facingSign == -1 ? x - 1 : ((14 + (odd ? 1 : 0)) -
			// x);
			// int zLimit = facingSign == -1 ? z - 1 : ((14 + (odd ? 1 : 0)) -
			// z);
			// int addDist;
			do {
				dist += 2;// (addDist = (dist != 0 && (dist != 1 || odd)) ? 2 :
						  // 1);
			} while (dist <= limit
				&& (isPosValid(tower,
					getRelCoordsWithOffset(facing, dist, x,
						y, z)) && getStateWithOffset(tower,
					facing, dist, x, y, z) == null));
			/*
			 * if ((tower.hasXEntrance && facing.getAxis() == Axis.X &&
			 * (facingSign == 1 && tower.hasOddX)) || (!tower.hasXEntrance &&
			 * facing.getAxis() == Axis.Z)) dist -= 2;
			 */

			// validPos = isPosValid(getRelCoordsWithOffset(facing, dist - 2, x,
			// y, z));
			return Math.min(dist - 2, limit);
		}

		private boolean isPosAccessible(int x, int y, int z) {
			boolean isAccessible = (getStateAt(tower,
				x + 1, y, z) != null
				|| getStateAt(tower, x - 1, y, z) != null
				|| getStateAt(tower, x, y, z + 1) != null || getStateAt(
				tower, x, y, z - 1) != null);
			return isAccessible;
		}

		private IBlockState getStateWithOffset(
			MazeTower tower, EnumFacing facing, int dist) {
			return getStateWithOffset(tower, facing, dist,
				ix, iy, iz);
		}

		private static IBlockState getStateWithOffset(
			MazeTower tower, EnumFacing facing, int dist,
			int[] coords) {

			return getStateAt(tower, coords);
		}

		private static IBlockState getStateWithOffset(
			MazeTower tower, EnumFacing facing, int dist,
			int x, int y, int z) {
			int[] coords = getRelCoordsWithOffset(facing,
				dist, x, y, z);

			return getStateAt(tower, coords);
		}

		private Path getPathWithOffset(EnumFacing facing,
			int dist) {
			return getPathWithOffset(tower, this, facing,
				dist, ix, iy, iz);
		}

		private static Path getPathWithOffset(
			MazeTower tower, Path path, EnumFacing facing,
			int dist, int[] coords) {

			return getPathAt(tower, path, coords[2],
				coords[0], coords[1]);
		}

		private static Path getPathWithOffset(
			MazeTower tower, Path path, EnumFacing facing,
			int dist, int x, int y, int z) {
			int[] coords = getRelCoordsWithOffset(facing,
				dist, x, y, z);

			return getPathAt(tower, path, coords[2],
				coords[0], coords[1]);
		}

		private static Path getPathAt(MazeTower tower,
			Path path, int[] coords) {
			return getPathAt(tower, path, coords[2],
				coords[0], coords[1]);
		}

		private static Path getPathAt(MazeTower tower,
			Path pathIn, int x, int y, int z) {
			Path path = null;

			if (isPosValid(tower, x, y, z)) {
				int index = 0;
				try {
					index = tower.pathMap[y][z][x] - 1;
				} catch (ArrayIndexOutOfBoundsException e) {
					e = null;
				}
				if (index != -1) {
					if (index != tower.paths.size())
						path = tower.paths.get(index);
					else
						path = pathIn;
				}
			}

			return path;
		}

		private static IBlockState getStateAt(
			MazeTower tower, int[] coords) {
			return getStateAt(tower, coords[2], coords[0],
				coords[1]);
		}

		private static IBlockState getStateAt(
			MazeTower tower, int x, int y, int z) {
			IBlockState state = null;

			if (isPosValid(tower, x, y, z))
				state = tower.blockData[y][z][x];

			return state;
		}

		private int[] getRelCoordsWithOffset(
			EnumFacing facing, int dist) {
			return getRelCoordsWithOffset(facing, dist, ix,
				iy, iz);
		}

		private static int[] getRelCoordsWithOffset(
			EnumFacing facing, int dist, int rx, int ry,
			int rz) {
			int index = facing != null ? (int) Math
				.floor(facing.getIndex() * 0.5) : -1, sign = (facing != null && facing
				.getIndex() % 2 == 0) ? -1 : 1, x = rx
				+ ((index != 2) ? 0 : dist * sign), y = ry
				+ ((index != 0) ? 0 : dist * sign), z = rz
				+ ((index != 1) ? 0 : dist * sign);
			int[] coords = new int[] { y, z, x };

			return coords;
		}

		public int getMazeDepth() {
			return mazeDepth;
		}

		private static boolean isPosValid(MazeTower tower,
			int[] coords) {
			return isPosValid(tower, coords[2], coords[0],
				coords[1]);
		}

		private static boolean isPosValid(MazeTower tower,
			int x, int y, int z) {
			int floor = (int) Math.floor(y / 6) + 1;
			return ((x > 0 && x < 15) && (z > 0 && z < 15)
				&& (y >= 0 && y < floor * 6) && (floor == 1
				|| x != tower.exitX || z != tower.exitZ));
		}

		private void setStateWithOffset(int dist, int addY,
			int sideOffset, IBlockState state) {
			setStateWithOffset(dist, addY, sideOffset,
				state, false);
		}

		private void setStateWithOffset(int dist, int addY,
			int sideOffset, IBlockState state,
			boolean fromEnd) {
			final int x = (!fromEnd ? ix : fx)
				+ (dirAxis != 2 ? dirAxis == 0 ? 0
					: sideOffset : dist * dirSign);
			final int y = (!fromEnd ? iy : fy) + addY
				+ (dirAxis != 0 ? 0 : dist);
			final int z = (!fromEnd ? iz : fz)
				+ (dirAxis != 1 ? dirAxis == 0 ? 0
					: sideOffset : dist * dirSign);
			final IBlockState curState;
			try {
				curState = tower.blockData[y][z][x];
			} catch (ArrayIndexOutOfBoundsException e) {
				return;
			}
			if (curState == null
				|| curState == tower.air
				|| curState.getBlock() == tower.wallBlock
					.getBlock()
				|| curState == tower.floorBlock
				|| state.getBlock() == Blocks.ladder)
				tower.blockData[y][z][x] = state;
			if (tower.pathMap[y][z][x] == 0)
				tower.pathMap[y][z][x] = pathIndex;
			if (dist == distance && addY == 3
				&& sideOffset == 0 && state == tower.air) {
				int pathsChance = rand.nextInt(8);
				int numPaths = pathsChance < 3 ? 1
					: pathsChance != 7 ? 2 : 3;
				/*
				 * Iterator rdIterator = reservedDirs.iterator(); while
				 * (rdIterator.hasNext()) { Path newPath = new Path(tower, this,
				 * chain, x, y - addY, z, (EnumFacing) rdIterator.next()); if
				 * (newPath != null) { children.add(newPath); numPaths--; } }
				 */
				// tower.build(curWorld);
				// if (numPaths != 1) {
				for (int p = 0; p < numPaths; p++) {
					Path newPath = new Path(tower, this,
						chain, x, y - addY, z, null);
					if (newPath != null
						&& newPath.dir != null)
						children.add(newPath);
				}
				// } else {

				// }
			}
		}

		private void setStateAt(int x, int y, int z,
			IBlockState state) {
			IBlockState curState = tower.blockData[y][z][x];
			Block curBlock;
			/*
			 * if (curState != null && curState.getBlock() ==
			 * Blocks.redstone_block) return;
			 */
			if (curState == null
				|| ((curBlock = curState.getBlock()) != Blocks.lever
					&& curBlock != MazeTowers.BlockHiddenButton
					&& curBlock != Blocks.ladder
					&& curBlock != Blocks.redstone_wire
					&& curBlock != Blocks.wall_sign && curBlock != Blocks.glass_pane))
				tower.blockData[y][z][x] = state;
		}

		private void setPathWithOffset(int dist, int addY) {
			IBlockState torch = !tower.isUnderwater ? Blocks.torch
				.getDefaultState().withProperty(
					BlockTorch.FACING, dir.rotateYCCW())
				: Blocks.sea_lantern.getDefaultState();
			// for (int s = -1; s < 2; s++)
			if (!isTowerEntrance
				|| (dist != 0 && dist != 2) || addY != 3) {
				if (!tower.isUnderwater || addY != 2
					|| (dist != 1 && dist != distance - 1)) {
					setStateWithOffset(
						dist,
						addY,
						0,
						(dist != 1 && dist != distance - 1)
							|| addY != 2
							|| (rand
								.nextInt(difficulty + 1) != 0) ? tower.air
							: torch);
				} else if (!tower.isUnderwater) {
					if (rand.nextInt(4) == 3)
						setStateWithOffset(dist, addY + 2,
							0, torch);
				} else
					setStateWithOffset(dist, addY, 0,
						tower.air);

			}
		}

		private MTPuzzle getMtp() {
			MTPuzzle newMtp = null;
			int dIndex = dirAxis != 0 ? dirAxis == 1 ? 2
				: 1 : 0;
			int dist = 2;
			EnumFacing facing = null;
			Path toPath = null;
			List<EnumFacing> dirs = getDirsList(false,
				true, true);
			dirs.remove(this.dir.getOpposite());
			Iterator rdIterator = reservedDirs.iterator();
			while (rdIterator.hasNext())
				dirs.remove(rdIterator.next());
			IBlockState medianState;
			Iterator iterator = dirs.iterator();
			int[] coords = new int[] { 0, 0, 0 };
			/*
			 * while (iterator.hasNext() && ((facing = (EnumFacing)
			 * iterator.next()) == null || ((dist = getMaxDistance(facing, 2,
			 * false, fx, fy, fz) + 2) < 2) || (toPath = getPathAt((mcoords =
			 * getRelCoordsWithOffset(facing, dist, fx, fy, fz))[2], mcoords[0],
			 * mcoords[1])) == null || toPath.pathIndex == pathIndex ||
			 * toPath.dir == dir.getOpposite()));
			 */
			/*
			 * while (iterator.hasNext() && ((facing = (EnumFacing)
			 * iterator.next()) == null || !isPosValid(coords =
			 * getRelCoordsWithOffset(facing, 2, fx, fy, fz)) || (medianState =
			 * getStateWithOffset(facing, 1, fx, fy + 1, fz)) != null /*||
			 * (toPath = getPathWithOffset(facing, dist, parent.fx, parent.fy,
			 * parent.fz)) == null ||
			 */
			/*
			 * || toPath.hasMtp || toPath.pathIndex == pathIndex)
			 */// ));
			   // while (iterator.hasNext() && (facing = (EnumFacing)
			   // iterator.next()) != null && newMtp == null) {
			if (getMaxDistance(tower, this.dir, 4, false,
				ix, iy + 1, iz) == 4) {
				// toPath.hasMtp = true;
				// if (toPath.depth + 1 < this.depth)
				newMtp = getRandomMtp(tower, this.parent,
					this, this.dir, dist, ix, iy + 1, iz, 0);
				if (newMtp != null) {
					if (newMtp.getDir() == null)
						newMtp = null;
					else {
						newMtp.build();
						reservedDirs.add(dir);
					}
				}
				/*
				 * else if (toPath == null)// newMtp = getRandomMtp(tower,
				 * toPath, this, facing, dist, toPath.fx, toPath.fy + 1,
				 * toPath.fz);
				 */
			}
			// }
			return newMtp;
		}

		private MTPuzzle getRandomMtp(MazeTower tower,
			Path fromPath, Path toPath, EnumFacing dir,
			int distance, int x, int y, int z, int failCount) {
			MTPuzzle mtp = null;
			EnumFacing pathDir;
			int mtpChance = rand.nextInt(6);
			int offsetX;
			int offsetZ;
			int[] offsets;

			switch (mtpChance) {
			case 0:
				EnumFacing subDir = toPath.getFreeSpaceDir(
					x, y, z, fromPath);
				if (subDir != null)
					mtp = new MTPWindow(tower, fromPath,
						this, dir, subDir, distance, x, y,
						z);
				break;
			case 1:
				mtp = new MTPArrow(tower, fromPath, this,
					dir, distance, x, y, z);
				break;
			case 2:
				mtp = new MTPArrowGauntlet(tower, fromPath,
					this, dir, distance, x, y, z);
				break;
			case 3:
				if ((!tower.isUnderground && floor != 1)
					|| (tower.isUnderground && floor != tower.floors - 1)
					&& getStateAt(tower, x, y - 2, z) == tower.air)
					mtp = new MTPBounceTrap(tower,
						fromPath, this, toPath.dir,
						distance, x, y, z);
				break;
			case 4:
				if (floor != 1) {
					boolean isNonFatal;
					offsetX = -1;
					offsetZ = 1;// rand.nextInt(toPath.distance - 1) + 1;
					offsets = MTPuzzle.getRotatedOffsets(
						offsetX + 1, offsetZ,
						EnumFacing.SOUTH, dir);

					if ((getStateAt(tower, x + offsets[0],
						y - 2, z + offsets[1]) == tower.air
						&& ((isNonFatal = !tower.isUnderground
							&& getStateAt(tower, x
								+ offsets[0], y - 6, z
								+ offsets[1]) == tower.air) || !(isNonFatal = (!tower.isUnderground && isPosAccessible(
							x + offsets[0], y - 6, z
								+ offsets[1])))) && (isNonFatal || difficulty > 2))
						&& (!tower.isUnderground || floor < tower.floors - 2)) {
						/*
						 * if (!isNonFatal) { setStateAt(x + offsets[0], y, z +
						 * offsets[1], Blocks.redstone_block.getDefaultState());
						 * setStateAt(x + offsets[0], y - 6, z + offsets[1],
						 * Blocks.redstone_block.getDefaultState()); }
						 */
						offsets = MTPuzzle
							.getRotatedOffsets(offsetX,
								offsetZ, EnumFacing.SOUTH,
								dir);
						mtp = new MTPFallTrap(tower,
							fromPath, this, dir, distance,
							x, y, z, offsets[0],
							offsets[1], isNonFatal);
					}
				}
				break;
			case 5:
				if (difficulty > 2) {
					boolean isLeft = rand.nextBoolean();
					boolean switchDir = false;
					boolean useMtp = true;
					offsetX = -1;
					offsetZ = 0;
					offsets = MTPuzzle.getRotatedOffsets(
						offsetX, offsetZ, EnumFacing.SOUTH,
						dir);
					int[] coords = new int[] { y,
						z + offsets[1], x + offsets[0] };
					IBlockState curState = getStateAt(
						tower, coords);
					while (useMtp
						&& ((curState != null && curState
							.getBlock() instanceof BlockItemScanner) || getStateWithOffset(
							tower, isLeft ? dir
								.rotateYCCW() : dir
								.rotateY(), 2, coords) != null)) {
						if (!switchDir) {
							isLeft = !isLeft;
							switchDir = true;
						} else
							useMtp = false;
					}
					if (useMtp && !isLeft)
						mtp = new MTPItem(tower, fromPath,
							this, dir, distance, x, y, z,
							isLeft);
				}
				break;
			case 6:
				mtp = new MTPPiston(tower, fromPath, this,
					dir, distance, x, y, z);
				break;
			case 7:
				break;
			default:
				// mtp = new MTPArrowGauntlet(tower, fromPath, toPath,
				// toPath.dir, distance, x,
				// y, z);
				// mtp = new MTPWindow(tower, fromPath, toPath, dir, distance,
				// x,
				// y, z);
				// mtp = new MTPPiston(tower, fromPath, toPath, dir, distance,
				// x,
				// y, z);

			}
			return (mtp != null && mtp.dir != null)
				|| failCount++ >= 5 ? mtp : getRandomMtp(
				tower, fromPath, toPath, dir, distance, x,
				y, z, failCount);
		}

		private EnumFacing getFreeSpaceDir(int x, int y,
			int z, Path fromPath) {
			ArrayList<EnumFacing> dirsList = new ArrayList<EnumFacing>();
			EnumFacing facing = null;
			Path path = null;
			final boolean usePath = difficulty < 3 ? false
				: difficulty > 5 ? true : rand
					.nextInt(difficulty - 1) != 0;
			dirsList = Path.getDirsList(false, true, true);
			dirsList.remove(dir);
			Iterator dirIterator = dirsList.iterator();
			while (dirIterator.hasNext()
				&& ((facing = (EnumFacing) dirIterator
					.next()) == null
					|| (((path = getPathWithOffset(tower,
						this, facing, 2, x, y, z)) != null && !usePath) || (usePath && (path == null
						|| path.mtp != null
						|| path.isFloorEntrance || path.depth >= depth - 2))) || !isPosValid(
						tower,
						x
							+ (facing.getAxis() == Axis.X ? facing
								.getAxisDirection() == AxisDirection.NEGATIVE ? -2
								: 2
								: 0),
						y,
						z
							+ (facing.getAxis() == Axis.Z ? facing
								.getAxisDirection() == AxisDirection.NEGATIVE ? -2
								: 2
								: 0))))
				;
			/*
			 * (path = getPathWithOffset(tower, this, facing, 2, (x = x +
			 * (facing.getAxis() == Axis.X ? facing.getAxisDirection() ==
			 * AxisDirection.NEGATIVE ? -2 : 2 : 0)), y, (z = z +
			 * (facing.getAxis() == Axis.Z ? facing.getAxisDirection() ==
			 * AxisDirection.NEGATIVE ? -2 : 2 : 0)))) != null && (path.hasMtp
			 * || path.parent == this || path.getMazeDepth() > mazeDepth)) ||
			 * !isPosValid(tower, x, y, z)));
			 */
			return facing;
		}
	}

	private class Room {

		private final MazeTower tower;
		private final boolean isEntrance;
		private final int floor;
		private final int width;
		private final int depth;
		private final int minX;
		private final int minY;
		private final int minZ;
		private final int maxX;
		private final int maxY;
		private final int maxZ;
		private final int height;
		private final int pathDepth;
		private List<RoomConn> conns;

		private Room(MazeTower tower, int floor,
			RoomConn conn) {
			this.tower = tower;
			isEntrance = conn == null;
			this.floor = floor;
			width = !isEntrance ? (int) Math.max((rand
				.nextGaussian() * 2) + 6, 3) : (int) Math
				.max((rand.nextGaussian()) + 3, 2) * 2;
			depth = !isEntrance ? (int) Math.max((rand
				.nextGaussian() * 2) + 6, 3) : (int) Math
				.max((rand.nextGaussian()) + 3, 2) * 2;
			height = (int) Math.max(Math.max((rand
				.nextGaussian() * 1.5), 0),
				(tower.floors - floor)) + 1;

			if (isEntrance) {
				pathDepth = 1;
				minX = tower.entranceMinX;
				minZ = tower.entranceMinZ;
				maxX = minX + width;
				maxZ = minZ + depth;
			} else {
				pathDepth = conn.fromRoom.pathDepth + 1;
				minX = tower.entranceMinX;
				minZ = tower.entranceMinZ;
				maxX = minX + width;
				maxZ = minZ + depth;
			}

			minY = ((floor - 1) * 8);
			maxY = (floor * 8);
		}
	}

	private class RoomConn {

		private final Room fromRoom;
		private final Room toRoom;

		private RoomConn(Room from) {
			fromRoom = from;
			toRoom = new Room(from.tower, getFloor(from),
				getConn(from).get(0));
		}

		private int getFloor(Room from) {
			return from.floor;
		}

		private List<RoomConn> getConn(Room from) {
			return from.conns;
		}
	}

	private static class MTPWindow extends MTPuzzle {

		protected EnumFacing subDir;

		private MTPWindow(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, EnumFacing subDir,
			int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = -2, zOffset = -2, 1);
			this.subDir = subDir;
			IBlockState stone = Blocks.stone
				.getDefaultState();
			IBlockState lever = Blocks.lever
				.getDefaultState()
				.withProperty(
					BlockLever.FACING,
					(toPath.getDir().getAxis() == Axis.Z ? EnumOrientation.DOWN_Z
						: EnumOrientation.DOWN_X));
			IBlockState redstone = Blocks.redstone_wire
				.getDefaultState();
			if ((((subDir.getIndex() - 2) - (dirIndex - 2)) % 4) == 2) {
				// setXOffset(-2);
				// setZOffset(-2);
			}
			if (dir == EnumFacing.NORTH) {
				// setXOffset(2);
				// setZOffset(2);
			}
			stateMap = new IBlockState[][][] {
				{
					{ null, null, null, null, null },
					{ null, null, null, null, null },
					{
						null,
						null,
						doorStates[dir.getOpposite()
							.getIndex() % 4], null, null },
					{ null, null, null, null, null },
					{ null, null, null, null, null } },
				{
					{ null, null, null, null, null },
					{ null, null, null, null, null },
					{
						null,
						null,
						doorStates[(dir.getIndex() % 2) == -1 ? 4
							: 5], null, null },
					{ null, null, null, null, null },
					{ null, null, null, null, null } },
				{ { null, null, null, null, null },
					{ null, null, stone, null, null },
					{ null, null, tower.air, null, null },
					{ null, null, stone, null, null },
					{ null, null, null, null, null } } };
			stateMap = getRotatedStateMap(stateMap,
				EnumFacing.SOUTH, dir, true, true);
			final int sign = subDir.getAxisDirection() == AxisDirection.NEGATIVE ? -1
				: 1, axis = subDir.getAxis() == Axis.X ? 2
				: 1, coordA = sign == dirSign
				&& dirAxis != axis ? 3 : 1, coordB = sign == dirSign
				&& dirAxis != axis ? 4 : 0;
			if (subDir.getAxis() == Axis.Z) {
				stateMap[0][coordA][2] = stone;
				stateMap[0][coordB][2] = stone;
				stateMap[1][coordA][2] = stone;
				stateMap[1][coordB][2] = redstone;
				stateMap[2][coordA][2] = Blocks.glass
					.getDefaultState();
				stateMap[2][coordB][2] = lever;
			} else {
				stateMap[0][2][coordA] = stone;
				stateMap[0][2][coordB] = stone;
				stateMap[1][2][coordA] = stone;
				stateMap[1][2][coordB] = redstone;
				stateMap[2][2][coordA] = Blocks.glass
					.getDefaultState();
				stateMap[2][2][coordB] = lever;
			}
			validateStateMap(stateMap, false);
		}

	}

	private static class MTPPiston extends MTPuzzle {
		private MTPPiston(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, int distance,
			int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = 0, zOffset = 1, 2);
			int[] backCoords;
			boolean canAccessBack = true;/*
										  * Path.getStateWithOffset(tower,
										  * dir.rotateY(), 2, (backCoords = new
										  * int[] { x + xOffset, y + 2, z +
										  * zOffset + 1 })) == air
										  */
			IBlockState stone = Blocks.stone
				.getDefaultState();
			IBlockState piston = MazeTowers.BlockMemoryPiston
				.getDefaultState().withProperty(
					BlockPistonBase.FACING, dir.rotateY());
			IBlockState lever = Blocks.lever
				.getDefaultState()
				.withProperty(
					BlockLever.FACING,
					EnumOrientation
						.byMetadata(6 - (canAccessBack ? dir
							.rotateYCCW()
							: dir.getOpposite()).getIndex()));
			IBlockState button = ModBlocks.hiddenButton
				.getDefaultState().withProperty(
					BlockButton.FACING,
					(canAccessBack ? dir.rotateYCCW() : dir
						.getOpposite()));
			fromPath.setStateAt(x, y + 2, z,
				Blocks.brick_block.getDefaultState());
			stateMap = getRotatedStateMap(
				new IBlockState[][][] {
					{ { stone, piston, null },
						{ null, null, null } },
					{ { stone, piston, null },
						{ null, null, null } },
					{
						{ stone, stone,
							canAccessBack ? lever : null },
						{ null,
							!canAccessBack ? lever : null,
							null } } }, EnumFacing.SOUTH,
				dir, true);

			/*
			 * if (!canAccessBack) { EnumFacing facing = null; List<EnumFacing>
			 * dirsList = Path.getDirsList(false, true, true); Iterator
			 * dirIterator; dirsList.remove(dir.rotateY()); dirIterator =
			 * dirsList.iterator(); while (!canAccessBack &&
			 * dirIterator.hasNext() && (facing = (EnumFacing)
			 * dirIterator.next()) != null) { Path path; if ((path =
			 * Path.getPathWithOffset(tower, null, facing, 1, backCoords)) !=
			 * null && path.mazeDepth <= fromPath.mazeDepth) { canAccessBack =
			 * true; } } }
			 */

			validateStateMap(stateMap, false);
		}
	}

	private static class MTPArrow extends MTPuzzle {
		private MTPArrow(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, int distance,
			int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = -1, zOffset = /*
												  * rand.nextInt(toPath.distance
												  * - 1) +
												  */1, 1);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			boolean isLeft = false;// rand.nextBoolean();
			final IBlockState dispenser = Blocks.dispenser
				.getDefaultState().withProperty(
					BlockDispenser.FACING,
					(isLeft ? dir.rotateY() : dir
						.rotateYCCW())), pressurePlate = MazeTowers.BlockHiddenPressurePlateWeighted
				.getDefaultState();
			stateMap = getRotatedStateMap(
				new IBlockState[][][] { { {
					(isLeft ? null : dispenser),
					pressurePlate,
					(isLeft ? dispenser : null) } } },
				EnumFacing.SOUTH, dir, true);
			validateStateMap(stateMap, false);
		}
	}

	private static class MTPArrowGauntlet extends MTPuzzle {
		private MTPArrowGauntlet(MazeTower tower,
			Path fromPath, Path toPath, EnumFacing dir,
			int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = 0, zOffset = 0, 2);
			int delay = toPath.difficulty < 2 ? 3
				: toPath.difficulty < 5 ? 2 : 1;
			final IBlockState stone = Blocks.stone
				.getDefaultState(), dispenser = Blocks.dispenser
				.getDefaultState().withProperty(
					BlockDispenser.FACING, EnumFacing.DOWN), button = ModBlocks.hiddenButton
				.getDefaultState().withProperty(
					BlockButton.FACING, EnumFacing.DOWN), repeaterA = Blocks.unpowered_repeater
				.getDefaultState().withProperty(
					Blocks.unpowered_repeater.FACING,
					dir.getOpposite()).withProperty(
					Blocks.unpowered_repeater.DELAY, delay), repeaterB = Blocks.unpowered_repeater
				.getDefaultState().withProperty(
					Blocks.unpowered_repeater.FACING, dir)
				.withProperty(
					Blocks.unpowered_repeater.DELAY, 3), repeaterC = Blocks.unpowered_repeater
				.getDefaultState().withProperty(
					Blocks.unpowered_repeater.FACING,
					dirAxis == 2 ? dir.rotateYCCW() : dir
						.rotateY()).withProperty(
					Blocks.unpowered_repeater.DELAY, 3);
			dirSign = dirIndex % 2 == 0 ? -1 : 1;
			// if (dir != EnumFacing.EAST && dir != EnumFacing.NORTH)
			// dirSign *= -1;
			stateMap = new IBlockState[5][3][toPath.distance + 1];
			stateMap[0][0][0] = doorStates[dir
				.getOpposite().getIndex() % 4];
			stateMap[0][0][1] = null;
			stateMap[0][1][0] = null;
			stateMap[0][1][1] = null;
			stateMap[0][2][0] = null;
			stateMap[0][2][1] = null;
			stateMap[1][0][0] = doorStates[(dirIndex % 2) == -1 ? 4
				: 5];
			stateMap[1][0][1] = button;
			stateMap[1][1][0] = null;
			stateMap[1][1][1] = null;
			stateMap[1][2][0] = null;
			stateMap[1][2][1] = null;
			stateMap[2][0][0] = button;
			stateMap[2][0][1] = stone;
			stateMap[2][1][0] = null;
			stateMap[2][1][1] = null;
			stateMap[2][2][0] = null;
			stateMap[2][2][1] = null;
			stateMap[3][0][0] = stone;
			stateMap[3][0][1] = stone;
			stateMap[3][1][0] = null;
			stateMap[3][1][1] = null;
			stateMap[3][2][0] = null;
			stateMap[3][2][1] = null;
			stateMap[4][0][0] = redstone;
			stateMap[4][0][1] = repeaterA;
			stateMap[4][1][0] = redstone;
			stateMap[4][1][1] = null;
			stateMap[4][2][0] = redstone;
			stateMap[4][2][1] = repeaterB;
			int len = 2;
			for (int d = 0; d < toPath.distance - 2; d++) {
				for (int h = 0; h < 5; h++) {
					if (h < 3) {
						stateMap[h][0][len] = null;
						stateMap[h][1][len] = null;
						stateMap[h][2][len] = null;
					} else if (h == 3) {
						stateMap[h][0][len] = dispenser;
						stateMap[h][1][len] = null;
						stateMap[h][2][len] = null;
					} else {
						stateMap[h][0][len] = repeaterA;
						stateMap[h][1][len] = null;
						stateMap[h][2][len] = repeaterB;
					}
				}
				len++;
			}
			stateMap[4][0][2] = Blocks.melon_block
				.getDefaultState();
			stateMap[4][0][len] = redstone;
			stateMap[4][1][len] = repeaterC;
			stateMap[4][2][len] = redstone;
			stateMap = getRotatedStateMap(stateMap,
				EnumFacing.EAST, dir, false);
			validateStateMap(stateMap, true);
		}
	}

	private static class MTPFallTrap extends MTPuzzle {

		protected boolean isFatal;

		private MTPFallTrap(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, int distance,
			int x, int y, int z, int xOffset, int zOffset,
			boolean isNonFatal) {
			super(tower, fromPath, toPath, dir, distance,
				x, y - (isNonFatal ? 3 : 1), z, xOffset,
				zOffset, 1);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			boolean isLeft = rand.nextBoolean();
			IBlockState pistonA = MazeTowers.BlockMemoryPiston
				.getDefaultState().withProperty(
					BlockMemoryPistonBase.FACING,
					(isLeft ? dir.rotateYCCW() : dir
						.rotateY()));
			IBlockState pistonB = ((toPath.difficulty >= 4) ? MazeTowers.BlockMemoryPistonOff
				: MazeTowers.BlockMemoryPiston)
				.getDefaultState().withProperty(
					BlockMemoryPistonBase.FACING,
					(isLeft ? dir.rotateY() : dir
						.rotateYCCW()));
			IBlockState pressurePlate = MazeTowers.BlockHiddenPressurePlateWeighted
				.getDefaultState();
			setXOffset(xOffset);
			setZOffset(zOffset);
			isFatal = !isNonFatal;
			stateMap = getRotatedStateMap(
				isNonFatal ? new IBlockState[][][] {
					{ { isLeft ? pistonA : tower.air,
						tower.ceilBlock,
						isLeft ? tower.air : pistonA } },
					{ { null, pressurePlate, null } },
					{ { isLeft ? tower.air : pistonB, null,
						isLeft ? pistonB : tower.air } },
					{ { null, pressurePlate, null } } }
					: new IBlockState[][][] {
						{ { isLeft ? tower.air : pistonB,
							null,
							isLeft ? pistonB : tower.air } },
						{ { null, pressurePlate, null } } },
				EnumFacing.SOUTH, dir, true, false);
			validateStateMap(stateMap, false);
		}
	}

	private static class MTPBounceTrap extends MTPuzzle {

		private MTPBounceTrap(MazeTower tower,
			Path fromPath, Path toPath, EnumFacing dir,
			int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y - 2, z, xOffset = 0, zOffset = rand
					.nextInt(toPath.distance - 1) + 1, 1);
			dirSign = (dirIndex % 2 == 0 ? -2 : 1);
			IBlockState piston = MazeTowers.BlockMemoryPiston
				.getDefaultState().withProperty(
					BlockMemoryPistonBase.FACING,
					EnumFacing.UP);
			IBlockState pressurePlate = MazeTowers.BlockHiddenPressurePlateWeighted
				.getDefaultState();
			IBlockState web = Blocks.web.getDefaultState();
			stateMap = getRotatedStateMap(
				new IBlockState[][][] {
					{ { piston } },
					{ { null } },
					{ { pressurePlate } },
					{ { null } },
					{ { (rand.nextInt(10)) < toPath.difficulty ? web
						: null } } }, EnumFacing.SOUTH,
				dir, true);
			validateStateMap(stateMap, false);
		}
	}

	public static class MTPItem extends MTPuzzle {

		private MTPItem(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, int distance,
			int x, int y, int z, boolean isLeft) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = -1, zOffset = 1, 3);
			IBlockState stone = Blocks.stone
				.getDefaultState();
			IBlockState scanner = (toPath.difficulty < 5
				|| rand.nextInt((toPath.difficulty) - 3) == 0 ? MazeTowers.BlockItemScanner
				: MazeTowers.BlockItemScannerGold)
				.getDefaultState().withProperty(
					BlockItemScanner.FACING,
					dir.getOpposite());
			IBlockState sign = Blocks.wall_sign
				.getDefaultState()
				.withProperty(BlockWallSign.FACING,
					dir.getOpposite());
			IBlockState button = MazeTowers.BlockHiddenButton
				.getDefaultState().withProperty(
					BlockButton.FACING, dir);

			stateMap = new IBlockState[][][] {
				{
					{ null, null, null },
					{
						null,
						doorStates[dir.getOpposite()
							.getIndex() % 4], null },
					{ null, null, null } },
				{
					{ isLeft ? null : scanner, null,
						isLeft ? scanner : null },
					{
						null,
						doorStates[(dir.getOpposite()
							.getIndex() % 2) == -1 ? 4 : 5],
						null }, { null, null, null } },
				{ { null, sign, null },
					{ null, stone, null },
					{ null, button, null } } };

			/*
			 * IBlockState[][][] s = new IBlockState[][][] { { { stone, button,
			 * scanner }, { sign, Blocks.melon_block.getDefaultState(),
			 * tower.ceilBlock }, { tower.air,
			 * Blocks.redstone_block.getDefaultState(), doorStates[0] } } };
			 * getRotatedStateMap(s, EnumFacing.SOUTH, EnumFacing.SOUTH, true);
			 * getRotatedStateMap(s, EnumFacing.SOUTH, EnumFacing.NORTH, true);
			 * getRotatedStateMap(s, EnumFacing.SOUTH, EnumFacing.EAST, true);
			 * getRotatedStateMap(s, EnumFacing.SOUTH, EnumFacing.WEST, true);
			 */

			/*
			 * if (dirSign == -1) { if (dirAxis == 1) setZOffset(-1); else
			 * setXOffset(1); }
			 */
			stateMap = getRotatedStateMap(stateMap,
				EnumFacing.SOUTH, dir, false);
			validateStateMap(stateMap, false);
		}

	}

	private static class MTPuzzle {
		private final MazeTower tower;
		private final Path fromPath;
		private final Path toPath;
		private final int x;
		private final int y;
		private final int z;
		private final int mazeDepthValue;
		protected final int dirIndex;
		protected final int dirAxis;
		protected final IBlockState[] leverStates;
		protected final IBlockState[] doorStates;
		protected int dirSign;
		protected EnumFacing dir;
		protected IBlockState[][][] stateMap;
		protected static int xOffset;
		protected static int zOffset;
		protected static final IBlockState redstone = Blocks.redstone_wire
			.getDefaultState();
		private int distance;
		private boolean hasRedstone = false;

		private MTPuzzle(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing facing, int distance,
			int x, int y, int z, int xOffset, int zOffset,
			int mazeDepthValue) {
			dir = facing;
			dirIndex = dir.getIndex();
			dirAxis = (int) Math.floor(dirIndex * 0.5);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			this.mazeDepthValue = mazeDepthValue;
			Block lever = Blocks.lever;
			Block door = tower.doorBlock;
			leverStates = new IBlockState[] {
				lever.getDefaultState().withProperty(
					BlockLever.FACING,
					BlockLever.EnumOrientation.NORTH),
				lever.getDefaultState().withProperty(
					BlockLever.FACING,
					BlockLever.EnumOrientation.SOUTH),
				lever.getDefaultState().withProperty(
					BlockLever.FACING,
					BlockLever.EnumOrientation.WEST),
				lever.getDefaultState().withProperty(
					BlockLever.FACING,
					BlockLever.EnumOrientation.EAST),
				lever.getDefaultState().withProperty(
					BlockLever.FACING,
					BlockLever.EnumOrientation.UP_X) };
			doorStates = new IBlockState[] {
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.WEST),
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.EAST),
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.NORTH),
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.SOUTH),
				/*
				 * Blocks.wool.getDefaultState(),
				 * Blocks.emerald_block.getDefaultState(),
				 * Blocks.netherrack.getDefaultState(),
				 * Blocks.stonebrick.getDefaultState(),
				 */
				door.getDefaultState()
					.withProperty(BlockDoor.HINGE,
						EnumHingePosition.LEFT)
					.withProperty(BlockDoor.HALF,
						BlockDoor.EnumDoorHalf.UPPER),
				door.getDefaultState().withProperty(
					BlockDoor.HINGE,
					EnumHingePosition.RIGHT).withProperty(
					BlockDoor.HALF,
					BlockDoor.EnumDoorHalf.UPPER) };
			this.tower = tower;
			this.stateMap = new IBlockState[1][1][1];
			this.distance = distance;
			this.x = x;
			this.y = y;
			this.z = z;
			this.xOffset = xOffset;
			this.zOffset = zOffset;
			this.fromPath = fromPath;
			this.toPath = toPath;
		}

		private EnumFacing getDir() {
			return dir;
		}

		protected int getMazeDepthValue() {
			return mazeDepthValue;
		}

		protected void setDir(EnumFacing dir) {
			this.dir = dir;
		}

		protected static int[] getRotatedOffsets(
			int offsetX, int offsetZ, EnumFacing fromDir,
			EnumFacing toDir) {
			int[] offsets = new int[] { offsetX, offsetZ };
			if (fromDir != toDir) {
				if (fromDir.getAxis() == toDir.getAxis()) {
					offsets[0] *= -1;
					offsets[1] *= -1;
				} else {
					if (fromDir.getAxisDirection() == toDir
						.getAxisDirection()) {
						offsets[0] = offsetZ;
						offsets[1] = offsetX;
					} else {
						offsets[0] = offsetZ * -1;
						offsets[1] = offsetX * -1;
					}
				}
			}

			return offsets;
		}

		protected IBlockState[][][] getRotatedStateMap(
			IBlockState[][][] map, EnumFacing fromDir,
			EnumFacing toDir, boolean reorder) {
			return getRotatedStateMap(map, fromDir, toDir,
				reorder, true);
		}

		protected IBlockState[][][] getRotatedStateMap(
			IBlockState[][][] map, EnumFacing fromDir,
			EnumFacing toDir, boolean reorder,
			boolean rotateOffsets) {
			IBlockState[][][] newMap = MTUtils
				.getRotatedStateMap(map, fromDir, toDir,
					reorder);
			if (rotateOffsets) {
				int[] offsets = getRotatedOffsets(xOffset,
					zOffset, fromDir, dir);
				setXOffset(offsets[0]);
				setZOffset(offsets[1]);
			}
			return newMap;
		}

		protected void setXOffset(int xOffset) {
			this.xOffset = xOffset;
		}

		protected void setZOffset(int zOffset) {
			this.zOffset = zOffset;
		}

		public int getXOffset() {
			return xOffset;
		}

		public int getZOffset() {
			return zOffset;
		}

		protected MTPuzzle build() {
			try {
				int sign = dirSign;
				int randInt = -1;
				for (int y2 = 0; y2 < stateMap.length; y2++) {
					for (int z2 = 0; z2 < stateMap[y2].length; z2++) {
						for (int x2 = 0; x2 < stateMap[y2][z2].length; x2++) {
							IBlockState state = stateMap[y2][z2][x2];
							int xCoord = x + (x2 * sign)
								+ xOffset, yCoord = y + y2, zCoord = z
								+ (z2 * sign) + zOffset;
							if (state != null) {
								Block block = state
									.getBlock();
								if (block == Blocks.dispenser) {
									if (Path.isPosValid(
										tower, xCoord,
										yCoord, zCoord))
										tower.dataMap[yCoord][zCoord][xCoord] = (randInt != -1) ? randInt
											: (randInt = (rand
												.nextInt(9) + 1 + (Math
												.max(
													toPath.difficulty - 3,
													0))));
								} else if (this instanceof MTPWindow
									&& block == Blocks.glass) {
									Path leverPath;
									int[] leverPathCoords = toPath
										.getRelCoordsWithOffset(
											((MTPWindow) this).subDir,
											1, xCoord,
											yCoord - 2,
											zCoord);
									leverPath = toPath
										.getPathWithOffset(
											tower,
											toPath,
											((MTPWindow) this).subDir,
											1, xCoord,
											yCoord - 2,
											zCoord);
									if (leverPath != null) {
										if (leverPath
											.getStateAt(
												tower,
												leverPathCoords[2] - 1,
												leverPathCoords[0],
												leverPathCoords[1]) == tower.air)
											leverPath
												.setStateAt(
													leverPathCoords[2] - 1,
													leverPathCoords[0],
													leverPathCoords[1],
													tower.stairsBlock[0]);
										if (leverPath
											.getStateAt(
												tower,
												leverPathCoords[2] + 1,
												leverPathCoords[0],
												leverPathCoords[1]) == tower.air)
											leverPath
												.setStateAt(
													leverPathCoords[2] + 1,
													leverPathCoords[0],
													leverPathCoords[1],
													tower.stairsBlock[2]);
										if (leverPath
											.getStateAt(
												tower,
												leverPathCoords[2],
												leverPathCoords[0],
												leverPathCoords[1] - 1) == tower.air)
											leverPath
												.setStateAt(
													leverPathCoords[2],
													leverPathCoords[0],
													leverPathCoords[1] - 1,
													tower.stairsBlock[1]);
										if (leverPath
											.getStateAt(
												tower,
												leverPathCoords[2],
												leverPathCoords[0],
												leverPathCoords[1] + 1) == tower.air)
											leverPath
												.setStateAt(
													leverPathCoords[2],
													leverPathCoords[0],
													leverPathCoords[1] + 1,
													tower.stairsBlock[3]);
										state = tower.wallBlock;
									}
									if (toPath.getStateAt(
										tower, xCoord,
										yCoord, zCoord) == tower.air)
										state = Blocks.glowstone
											.getDefaultState();
								} else if ((this instanceof MTPFallTrap || this instanceof MTPBounceTrap)
									&& block == MazeTowers.BlockMemoryPiston) {
									if (tower.isUnderground
										&& toPath
											.isPosValid(
												tower,
												new int[] {
													yCoord + 12,
													zCoord,
													xCoord })) {
										yCoord += 12;
									}
								} else if (this instanceof MTPFallTrap
									&& block == MazeTowers.BlockHiddenPressurePlateWeighted) {
									boolean isFatal = ((MTPFallTrap) this).isFatal;
									if ((isFatal && y2 == 1)
										|| (!isFatal && y2 == 3))
										fromPath
											.addFallTrapHole(
												xCoord,
												zCoord,
												isFatal);
								} else if (block instanceof BlockItemScanner)
									tower.pathMap[yCoord][zCoord][xCoord] = toPath.pathIndex;
								fromPath.setStateAt(xCoord,
									yCoord, zCoord, state);
							}
						}
					}
				}
				// fromPath.setStateAt(x, y + 2, z,
				// Blocks.emerald_block.getDefaultState());
				// fromPath.setStateAt(x + xOffset, y + 2, z + zOffset,
				// Blocks.lapis_block.getDefaultState());
				// fromPath.setStateAt(fromPath.fx, fromPath.iy + 3,
				// fromPath.fz, Blocks.gold_block.getDefaultState());
				// fromPath.setStateAt(toPath.fx, toPath.iy + 3, toPath.fz,
				// Blocks.diamond_block.getDefaultState());
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}

			return this;
		}

		protected void validateStateMap(
			IBlockState[][][] map, boolean checkTopSpace) {
			int sign = dirSign;
			int[] minCoords;
			int[] maxCoords;
			try {
				if (!Path
					.isPosValid(
						tower,
						(maxCoords = new int[] {
							y + (map.length - 1),
							z
								+ ((map[0].length - 1) * dirSign)
								+ zOffset,
							x
								+ ((map[0][0].length - 1) * dirSign)
								+ xOffset }))
					|| !Path.isPosValid(tower,
						(minCoords = new int[] { y,
							z + zOffset, x + xOffset }))
					|| (checkTopSpace && (Path.getStateAt(
						tower, minCoords[2], maxCoords[0],
						minCoords[1]) != tower.air
						|| Path.getStateAt(tower,
							maxCoords[2], maxCoords[0],
							minCoords[1]) != tower.air
						|| Path.getStateAt(tower,
							minCoords[2], maxCoords[0],
							maxCoords[1]) != tower.air || Path
						.getStateAt(tower, maxCoords) != tower.air)))
					setDir(null);
			} catch (ArrayIndexOutOfBoundsException e) {
				e = null;
			}
		}

		private IBlockState getLeverState(int x, int y,
			int z) {
			IBlockState state = null;

			return state;
		}
	}
}