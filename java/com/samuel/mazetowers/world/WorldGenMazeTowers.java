package com.samuel.mazetowers.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.IWorldGenerator;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Multimap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockExtraDoor;
import com.samuel.mazetowers.blocks.BlockExtraSlab;
import com.samuel.mazetowers.blocks.BlockHiddenButton;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.blocks.BlockLock;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBase;
import com.samuel.mazetowers.blocks.BlockVendorSpawner;
import com.samuel.mazetowers.etc.MTStateMaps;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.etc.MazeTowersData;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModChestGen;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.packets.PacketDebugMessage;
import com.samuel.mazetowers.tileentity.TileEntityCircuitBreaker;
import com.samuel.mazetowers.tileentity.TileEntityItemScanner;
import com.samuel.mazetowers.tileentity.TileEntityLock;
import com.samuel.mazetowers.tileentity.TileEntitySpecialMobSpawner;
import com.samuel.mazetowers.tileentity.TileEntityVendorSpawner;
import com.samuel.mazetowers.tileentity.TileEntityWebSpiderSpawner;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

public class WorldGenMazeTowers implements IWorldGenerator {

	private Map<Integer, Map<Integer, IBlockState>> chunkData = null;
	private Multimap<Integer, ArrayDeque<Integer>> chunkChest = null;
	private List<MazeTowerBase>[] towers = new List[3];
	private final int[] genCount = new int[] { 64, 128, 64 };
	private int[] chunksGenerated = new int[] { 0, 0, 0 };
	private int[] spawnPosLoadedCount = new int[] { 0, 0, 0 };
	private World curWorld;
	public BlockPos[][] spawnPos = new BlockPos[3][];
	private boolean[][] spawnPosLoaded = new boolean[3][];
	private boolean[][] generated = new boolean[3][];
	private Map<BlockPos, IBlockState>[][] data = new Map[3][];
	private Map<ChunkCoordIntPair, ChunkCoordIntPair>[] chunkGroupTowerCoords = new Map[3];
	private MazeTowersData MazeTowerData;
	private final Map<Block, IBlockState[]> colourBlockStates;
	private static final Random rand = new Random();
	private static final Comparator<Entry<BlockPos, Tuple<Integer, Integer>>> scannerPosComparator = new Comparator<Entry<BlockPos, Tuple<Integer, Integer>>>() {

		@Override
		public int compare(
			Entry<BlockPos, Tuple<Integer, Integer>> e1,
			Entry<BlockPos, Tuple<Integer, Integer>> e2) {
			Integer v1 = e1.getValue()
				.getSecond();
			Integer v2 = e2.getValue()
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
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		int genCount, dimId = world.provider
			.getDimension();
		if (getChunksGenerated(world) < (genCount = getGenCount(dimId))) {
			for (int t = 0; t < genCount; t++) {
				if (getGenerated(dimId, t)
					|| !getSpawnPosLoaded(dimId, t))
					continue;
				BlockPos spawnPos = getSpawnPos(dimId, t);
				boolean usePos = false;

				if (chunkX == spawnPos.getX() >> 4
					&& chunkZ == spawnPos.getZ() >> 4) {
					if (spawnPos.getY() > 0) {
						usePos = addTower(world, chunkX, chunkZ, true);
						chunkGenerator.provideChunk(chunkX, chunkZ).setModified(true);
					} else
						usePos = false;
				}
			}
		}
	}

	public int getGenCount(int dimId) {
		return genCount[dimId + 1];
	}

	public int getChunksGenerated(int dimId) {
		return chunksGenerated[dimId + 1];
	}

	public int getChunksGenerated(World worldIn) {
		loadOrCreateData(worldIn);
		return chunksGenerated[worldIn.provider
			.getDimension() + 1];
	}

	public int getSpawnPosLoadedCount(World worldIn) {
		loadOrCreateData(worldIn);
		return spawnPosLoadedCount[worldIn.provider
			.getDimension() + 1];
	}

	public boolean getIsValidChunkCoord(World worldIn, int chunkX, int chunkZ) {
		final int dimId = worldIn.provider.getDimension();
		if (dimId == 1) {
			BlockPos cornerPos = new BlockPos((chunkX - 1) << 4, 56, (chunkZ - 1) << 4);
			if (worldIn.getTopSolidOrLiquidBlock(cornerPos).getY() < 56 ||
				worldIn.getTopSolidOrLiquidBlock(cornerPos.offset(EnumFacing.EAST, 47)
				.offset(EnumFacing.SOUTH, 47)).getY() < 56 ||
				worldIn.getTopSolidOrLiquidBlock(cornerPos.offset(EnumFacing.EAST, 47)).getY() < 56 ||
				worldIn.getTopSolidOrLiquidBlock(cornerPos.offset(EnumFacing.SOUTH, 47)).getY() < 56)
				return false;
		}
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
			.getDimension() + 1]) {
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
			.getDimension() + 1]) {
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
		final int dimId = worldIn.provider.getDimension() + 1;
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
		final int dimId = worldIn.provider.getDimension() + 1;
		loadOrCreateData(worldIn);
		generated[dimId][chunkIndex] = isGenerated;
		MazeTowerData.setIsGenerated(isGenerated, dimId,
			chunkIndex);
	}

	protected final void loadOrCreateData(World world) {
		boolean dataIsNull = this.MazeTowerData == null;
		final int dimId = world.provider.getDimension();
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
			ModChestGen.initChestGen(world.rand, false);
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
				this.generated = MazeTowerData
					.getIsGenerated();
				this.spawnPos = MazeTowerData
					.getSpawnPoint();
				for (; d < 3; d++) {
					spawnPosLoadedCount[d] = 0;
					chunkGroupTowerCoords[d] = new HashMap<ChunkCoordIntPair,
						ChunkCoordIntPair>(genCount[d]);
					towers[d] = new ArrayList<MazeTowerBase>();
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
							Stack<MiniTower> miniTowers = new Stack<MiniTower>();
							List<int[]> mtDataList = MazeTowerData
								.getTowerDataMini(d, g);
							List<BitSet[][][]> mtBlockBreakabilityDataList = MazeTowerData
								.getBlockBreakabilityDataMini(d, g);
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
									isUnderground[d][g],
									EnumTowerType.values()[towerData[d][g][4]],
									blockBreakabilityData,
									miniTowers, d, g));
							if (mtDataList != null) {
    							for (int m = 0; m < mtDataList.size(); m++) {
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
    								} catch (IndexOutOfBoundsException e) {
    									e = null;
    								}
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
			.getDimension() + 1].get(index)).build(
			worldIn, false);
	}

	public void spawn(World world, int ix, int iy, int iz,
		int chunk) {
	}

	public boolean addTower(World worldIn, int x, int z,
		boolean build) {
		loadOrCreateData(worldIn);
		final int dimId = worldIn.provider.getDimension();
		final BiomeGenBase biome;
		String biomeName;
		final EnumTowerType towerType;
		final BlockPos pos;
		final Block trapDoorBlock = Blocks.iron_trapdoor;
		IBlockState ceilBlock = null, wallBlock = null, wallBlock_external = null,
		wallBlock_corner = null, floorBlock = null, slabBlock = null, fenceBlock = null;
		IBlockState[] stairsBlock = new IBlockState[4];
		final EnumDyeColor[] dyeColorList, beaconGlassColorList;
		final List<MazeTowerBase> towersList = towers[dimId + 1];
		boolean isUnderground = false;
		final boolean isUnderwater, hasXEntrance = rand.nextBoolean();
		int px = (x << 4) + (hasXEntrance ? 0 : 8), pz = (z << 4)
			+ (hasXEntrance ? 8 : 0), py, difficulty, rarity;
		biome = worldIn.getBiomeGenForCoords(new BlockPos(
			px, 64, pz));
		biomeName = biome.getBiomeName();
		isUnderwater = biomeName.equals("Deep Ocean");
		py = MTUtils.getSurfaceY(worldIn, px, pz, 3,
			isUnderwater || dimId == -1) - 1;
		pos = new BlockPos(px, py, pz);

		int typeChance = (dimId == 0) ? !isUnderwater ?
			rand.nextInt(128) : 32 : (dimId == -1) ?
			16 + rand.nextInt(64) : 27 + rand.nextInt(32);
		if (typeChance < 32) {
			if (typeChance < 16) {
				ceilBlock = Blocks.stonebrick
					.getStateFromMeta(3);
				wallBlock = Blocks.stonebrick
					.getDefaultState();
				floorBlock = Blocks.stonebrick
					.getStateFromMeta(3);
				slabBlock = Blocks.stone_slab.getStateFromMeta(5);
				stairsBlock = new IBlockState[] {
					Blocks.stone_brick_stairs
						.getStateFromMeta(0),
					Blocks.stone_brick_stairs
						.getStateFromMeta(2),
					Blocks.stone_brick_stairs
						.getStateFromMeta(1),
					Blocks.stone_brick_stairs
						.getDefaultState() };
				if (biomeName.indexOf("Taiga") != -1) {
					wallBlock_external = Blocks.stonebrick
						.getStateFromMeta(1);
					fenceBlock = MazeTowers.BlockMossyStoneBrickWall
						.getDefaultState();
				} else
					fenceBlock = MazeTowers.BlockStoneBrickWall
						.getDefaultState();
				isUnderground = rand.nextInt(3) == 0;
				towerType = EnumTowerType.STONE_BRICK;
			} else if (typeChance < 27) {
				ceilBlock = Blocks.quartz_block
					.getDefaultState();
				wallBlock = Blocks.quartz_block
					.getStateFromMeta(2);
				floorBlock = Blocks.quartz_block
					.getStateFromMeta(1);
				slabBlock = Blocks.stone_slab.getStateFromMeta(7);
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
				isUnderground = rand.nextInt(3) == 0;
				towerType = EnumTowerType.QUARTZ;
			} else if (typeChance < 31) {
				ceilBlock = Blocks.obsidian
					.getDefaultState();
				wallBlock = Blocks.obsidian
					.getDefaultState();
				floorBlock = Blocks.obsidian
					.getDefaultState();
				slabBlock = ModBlocks.extraSlabHalf.getStateFromMeta(BlockExtraSlab.EnumType.OBSIDIAN.ordinal());
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
				isUnderground = rand.nextInt(3) != 0;
				towerType = EnumTowerType.OBSIDIAN;
			} else {
				ceilBlock = Blocks.bedrock
					.getDefaultState();
				wallBlock = Blocks.bedrock
					.getDefaultState();
				floorBlock = Blocks.bedrock
					.getDefaultState();
				slabBlock = ModBlocks.extraSlabHalf.getStateFromMeta(BlockExtraSlab.EnumType.BEDROCK.ordinal());
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
				isUnderground = rand.nextInt(3) != 0;
				towerType = EnumTowerType.BEDROCK;
			}
		} else {
			if (dimId == 0) {
				if ((biomeName.contains("Taiga") && rand.nextInt(3) != 0)
					|| biomeName.startsWith("Extreme")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(1);
					wallBlock = Blocks.log
						.getStateFromMeta(1);
					floorBlock = Blocks.planks
						.getStateFromMeta(1);
					slabBlock = Blocks.wooden_slab.getStateFromMeta(1);
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
					towerType = EnumTowerType.SPRUCE;
				} else if (biomeName.startsWith("Birch")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(2);
					wallBlock = Blocks.log
						.getStateFromMeta(2);
					floorBlock = Blocks.planks
						.getStateFromMeta(2);
					slabBlock = Blocks.wooden_slab.getStateFromMeta(2);
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
					towerType = EnumTowerType.BIRCH;
				} else if (biomeName.startsWith("Jungle")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(3);
					wallBlock = Blocks.log
						.getStateFromMeta(3);
					floorBlock = Blocks.planks
						.getStateFromMeta(3);
					slabBlock = Blocks.wooden_slab.getStateFromMeta(3);
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
					towerType = EnumTowerType.JUNGLE;
				} else if (biomeName.startsWith("Savanna")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(4);
					wallBlock = Blocks.log2
						.getStateFromMeta(0);
					floorBlock = Blocks.planks
						.getStateFromMeta(4);
					slabBlock = Blocks.wooden_slab.getStateFromMeta(4);
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
					towerType = EnumTowerType.ACACIA;
				} else if (biomeName.startsWith("Desert")) {
					ceilBlock = Blocks.sandstone
						.getStateFromMeta(1);
					wallBlock = Blocks.sandstone
						.getDefaultState();
					wallBlock_external = Blocks.sandstone
						.getStateFromMeta(2);
					floorBlock = Blocks.sandstone
						.getStateFromMeta(2);
					slabBlock = Blocks.stone_slab.getStateFromMeta(1);
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
					isUnderground = rand.nextInt(3) == 0;
					towerType = EnumTowerType.SANDSTONE;
				} else if (biomeName.startsWith("Mesa")) {
					ceilBlock = Blocks.red_sandstone
						.getStateFromMeta(1);
					wallBlock = Blocks.red_sandstone
						.getDefaultState();
					wallBlock_external = Blocks.red_sandstone
						.getStateFromMeta(2);
					floorBlock = Blocks.red_sandstone
						.getStateFromMeta(2);
					slabBlock = Blocks.stone_slab2.getDefaultState();
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
					isUnderground = rand.nextInt(4) == 0;
					towerType = EnumTowerType.RED_SANDSTONE;
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
					slabBlock = ModBlocks.extraSlabHalf.getStateFromMeta(BlockExtraSlab.EnumType.PACKED_ICE.ordinal());
					stairsBlock = new IBlockState[] {
						ModBlocks.packedIceStairs
							.getStateFromMeta(0),
						ModBlocks.packedIceStairs
							.getStateFromMeta(2),
						ModBlocks.packedIceStairs
							.getStateFromMeta(1),
						ModBlocks.packedIceStairs
							.getDefaultState() };
					fenceBlock = MazeTowers.BlockPackedIceWall
						.getDefaultState();
					isUnderground = rand.nextInt(3) == 0;
					towerType = EnumTowerType.ICE;
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
					slabBlock = Blocks.wooden_slab.getDefaultState();
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
					towerType = EnumTowerType.OAK;
				} else if (biomeName.startsWith("Mushroom") ||
					(biomeName.startsWith("Roofed") && rand.nextInt(8) == 0)) {
					final boolean isRed = rand
						.nextBoolean();
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
					slabBlock = ModBlocks.extraSlabHalf.getStateFromMeta(BlockExtraSlab.EnumType.MYCELIUM.ordinal());
					stairsBlock = new IBlockState[] {
						ModBlocks.myceliumStairs
							.getStateFromMeta(0),
						ModBlocks.myceliumStairs
							.getStateFromMeta(2),
						ModBlocks.myceliumStairs
							.getStateFromMeta(1),
						ModBlocks.myceliumStairs
							.getDefaultState() };
					fenceBlock = wallBlock;
					towerType = isRed ? EnumTowerType.RED_MUSHROOM :
						EnumTowerType.BROWN_MUSHROOM;
				} else if (biomeName.startsWith("Roofed")) {
					ceilBlock = Blocks.planks
						.getStateFromMeta(5);
					wallBlock = Blocks.log2
						.getStateFromMeta(1);
					floorBlock = Blocks.planks
						.getStateFromMeta(5);
					slabBlock = Blocks.wooden_slab.getStateFromMeta(5);
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
					towerType = EnumTowerType.DARK_OAK;
				} else if (isUnderwater) {
					ceilBlock = Blocks.prismarine
						.getStateFromMeta(2);
					wallBlock = Blocks.prismarine
						.getDefaultState();
					wallBlock_external = Blocks.prismarine
						.getStateFromMeta(1);
					floorBlock = wallBlock_external;
					slabBlock = ModBlocks.extraSlabHalf.getStateFromMeta(BlockExtraSlab.EnumType.PRISMARINE_BRICK.ordinal());
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
					isUnderground = rand.nextBoolean();
					towerType = EnumTowerType.PRISMARINE;
				} else {
					ceilBlock = Blocks.cobblestone
						.getDefaultState();
					wallBlock = Blocks.cobblestone
						.getDefaultState();
					floorBlock = Blocks.cobblestone
						.getDefaultState();
					slabBlock = Blocks.stone_slab.getStateFromMeta(3);
					stairsBlock = new IBlockState[] {
						Blocks.stone_stairs
							.getStateFromMeta(0),
						Blocks.stone_stairs
							.getStateFromMeta(2),
						Blocks.stone_stairs
							.getStateFromMeta(1),
						Blocks.stone_stairs
							.getDefaultState() };
					if (biomeName.indexOf("Taiga") == -1)
						fenceBlock = Blocks.cobblestone_wall
							.getDefaultState();
					else {
						wallBlock_external = Blocks.mossy_cobblestone
							.getDefaultState();
						fenceBlock = Blocks.cobblestone_wall
							.getDefaultState().withProperty(BlockWall.VARIANT,
							BlockWall.EnumType.MOSSY);
					}
					isUnderground = rand.nextInt(3) == 0;
					towerType = EnumTowerType.COBBLESTONE;
				}
			} else if (dimId == -1) {
				ceilBlock = Blocks.nether_brick
					.getDefaultState();
				wallBlock = Blocks.nether_brick
					.getDefaultState();
				floorBlock = Blocks.netherrack
					.getDefaultState();
				slabBlock = Blocks.stone_slab.getStateFromMeta(6);
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
				towerType = EnumTowerType.NETHER_BRICK;
			} else {
				if (rand.nextInt(3) != 0) {
    				ceilBlock = Blocks.end_bricks
    					.getDefaultState();
    				wallBlock = Blocks.end_bricks
    					.getDefaultState();
    				floorBlock = Blocks.end_bricks
    					.getDefaultState();
    				slabBlock = ModBlocks.extraSlabHalf.getStateFromMeta(BlockExtraSlab.EnumType.END_STONE_BRICK.ordinal());
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
    				towerType = EnumTowerType.END_STONE_BRICK;
				} else {
					ceilBlock = Blocks.purpur_block
    					.getDefaultState();
    				wallBlock = Blocks.purpur_pillar
    					.getDefaultState();
    				wallBlock_external = Blocks.purpur_block.getDefaultState();
    				wallBlock_corner = Blocks.purpur_pillar.getDefaultState();
    				floorBlock = Blocks.purpur_block
    					.getDefaultState();
    				slabBlock = Blocks.purpur_slab.getDefaultState();
    				stairsBlock = new IBlockState[] {
    					Blocks.purpur_stairs
    						.getStateFromMeta(0),
    					Blocks.purpur_stairs
    						.getStateFromMeta(2),
    					Blocks.purpur_stairs
    						.getStateFromMeta(1),
    					Blocks.purpur_stairs
    						.getDefaultState() };
    				fenceBlock = MazeTowers.BlockPurpurWall
    					.getDefaultState();
    				towerType = EnumTowerType.PURPUR;
				}
			}
		}
		if (wallBlock_external == null)
			wallBlock_external = wallBlock;
		if (wallBlock_corner == null)
			wallBlock_corner = wallBlock_external;
		difficulty = towerType.getBaseDifficulty();
		rarity = towerType.getBaseRarity();
		
		if (isUnderground) {
			difficulty += 2;
			rarity++;
		}
		long startTime = System.nanoTime(), endTime;
		MazeTower newTower = new MazeTower(x, py, z,
			ceilBlock, wallBlock, wallBlock_external, wallBlock_corner,
			floorBlock, slabBlock, fenceBlock, stairsBlock, isUnderground,
			hasXEntrance, towerType, dimId + 1, towersList.size());
		endTime = (System.nanoTime() - startTime) / 1000000;
		MazeTowers.network.sendToDimension(
			new PacketDebugMessage("Finished tower generation in "
			+ endTime + " ms"), dimId);
		if (!isUnderground) {
			startTime = System.nanoTime();
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
		MazeTowerData.setTowerData(x, py, z, newTower.floors,
			towerType.ordinal(), dimId + 1, towersList.size());
		towers[dimId + 1].add(newTower);
		if (build) {
			MazeTowers.network.sendToDimension(
				new PacketDebugMessage("Started building "
					+ newTower.towerTypeName + " Maze Tower at "
					+ pos.toString()), dimId);
			startTime = System.nanoTime();
			newTower.build(worldIn, true);
			endTime = (System.nanoTime() - startTime) / 1000000;
			MazeTowers.network.sendToDimension(
				new PacketDebugMessage(
					"Finished tower build in " + endTime + " ms"), dimId);
			setIsGenerated(worldIn, towersList.size() - 1,
				true);
			chunksGenerated[dimId + 1]++;
		}

		return true;
	}

	public void recreate(World worldIn, boolean build) {
		loadOrCreateData(worldIn);
		int dimId = worldIn.provider.getDimension() + 1;
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
					tower.wallBlock_external, tower.wallBlock_corner,
					tower.floorBlock, tower.slabBlock, tower.fenceBlock,
					tower.stairsBlock, tower.isUnderground,
					rand.nextBoolean(), tower.towerType, dimId, t);
				tower.initPaths();
				towers[dimId].set(t, tower);
				ModChestGen.initChestGen(worldIn.rand, true);
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
		public final EnumTowerType towerType;
		protected BitSet[][] blockBreakabilityData;
		protected Stack<MiniTower> miniTowers;
		protected boolean isNetherTower;
		protected static final String[] mobList = new String[] {
			"Zombie", "Skeleton", "Spider", "Silverfish", "Creeper",
			"Endermite", "CaveSpider", "Witch", "ChargedCreeper",
			"Blaze", "mazetowers.ExplosiveCreeper" };

		public MazeTowerBase(int x, int y, int z,
			int floors, boolean isUnderground, EnumTowerType towerType,
			BitSet[][] blockBreakabilityData, Stack<MiniTower> miniTowers,
			int dimId, int towerIndex) {
			final int minBaseY = (floors * 6) + 1;
			chunkX = x;
			chunkZ = z;
			baseY = (!isUnderground || y >= minBaseY) ? y
				: minBaseY;
			this.floors = !(this.isNetherTower = dimId == 0) ? floors
				: Math.min(floors, ((int) Math
					.floor((125 - y) / 6)) - 1);
			this.isUnderground = isUnderground;
			this.difficulty = towerType.getBaseDifficulty() + (!isUnderground ? 0 : 2);
			this.rarity = towerType.getBaseRarity() + (!isUnderground ? 0 : 1);
			this.towerTypeName = towerType.getName();
			this.towerType = towerType;
			if (blockBreakabilityData != null)
				this.blockBreakabilityData = blockBreakabilityData;
			else
				this.blockBreakabilityData = new BitSet[((this.floors * 6) + 1)][16];
			if (miniTowers != null)
				this.miniTowers = miniTowers;
			else
				this.miniTowers = new Stack<MiniTower>();
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
			return rarity + (int) Math.floor((Math.min(floor, floors) - 1) / 5);
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
				diffStr += arrow + EnumLevel.getStringFromLevel(Math
					.min(difficulty + addToLevel, 9), true);
				rareStr += arrow + EnumLevel.getStringFromLevel(Math
					.min(rarity + addToLevel, 9), true);
			}
			return String.format(" %1$13s", diffStr)
				+ TextFormatting.RESET + "  "
				+ String.format("%1$-13s ", rareStr);
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

			private static final TextFormatting[] colorCodes = new TextFormatting[] {
				TextFormatting.BLACK,
				TextFormatting.WHITE,
				TextFormatting.YELLOW,
				TextFormatting.AQUA,
				TextFormatting.LIGHT_PURPLE };
			private final String name;

			private EnumLevel(String name) {
				this.name = name;
			}

			@Override
			public String toString() {
				return this.name;
			}

			public static String getStringFromLevel(
				int level, boolean isBold) {
				TextFormatting colorCode = colorCodes[level >> 1];
				return (isBold ? TextFormatting.BOLD
					+ "" + colorCode
					: colorCode != TextFormatting.BLACK ? colorCode
						: TextFormatting.WHITE)
					+ values()[level].name;
			}

			@Override
			public String getName() {
				return this.name;
			}
		}
		
		public static enum EnumTowerType implements IStringSerializable {
			
			OAK("Oak"), SPRUCE("Spruce"), BIRCH("Birch"), JUNGLE("Jungle"),
			ACACIA("Acacia"), DARK_OAK("Dark Oak"), SANDSTONE("Sandstone"),
			RED_SANDSTONE("Red Sandstone"), COBBLESTONE("Cobblestone"),
			STONE_BRICK("Stone Brick"), ICE("Ice"), RED_MUSHROOM("Red Mushroom"),
			BROWN_MUSHROOM("Brown Mushroom"), NETHER_BRICK("Nether Brick"),
			/*BRICK("Brick"), */QUARTZ("Quartz"), PRISMARINE("Prismarine"), END_STONE_BRICK("End Stone Brick"),
			OBSIDIAN("Obsidian"), PURPUR("Purpur"), BEDROCK("Bedrock");

			private final String name;
			private static final int[] baseDifficulty = new int[] {
				0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 4, /*4, */4, 4, 5, 5, 6, 6
			}, baseRarity = new int[] {
				0, 1, 1, 1, 2, 2, 2, 3, 3, 4, 5, 5, 5, 3, /*4, */4, 5, 4, 5, 5, 6
			};
			private static final BlockDoor ironDoor = (BlockDoor) Blocks.iron_door;
			private static final BlockDoor[] doorBlocks = new BlockDoor[] {
				(BlockDoor) Blocks.oak_door, (BlockDoor) Blocks.spruce_door,
				(BlockDoor) Blocks.birch_door, (BlockDoor) Blocks.jungle_door,
				(BlockDoor) Blocks.acacia_door, (BlockDoor) Blocks.dark_oak_door,
				ironDoor, ironDoor, ironDoor, ironDoor, ironDoor, ironDoor, ironDoor,
				ironDoor, /*ironDoor, */MazeTowers.BlockQuartzDoor, MazeTowers.BlockPrismarineDoor,
				MazeTowers.BlockEndStoneDoor, MazeTowers.BlockObsidianDoor,
				MazeTowers.BlockPurpurDoor, MazeTowers.BlockBedrockDoor
			};
			private static final EnumDyeColor[][] dyeColors = new EnumDyeColor[][] {
				{ EnumDyeColor.ORANGE, EnumDyeColor.SILVER, EnumDyeColor.GREEN },
				{ EnumDyeColor.BROWN, EnumDyeColor.SILVER, EnumDyeColor.GREEN },
				{ EnumDyeColor.ORANGE, EnumDyeColor.SILVER, EnumDyeColor.GREEN },
				{ EnumDyeColor.BROWN, EnumDyeColor.SILVER, EnumDyeColor.GREEN },
				{ EnumDyeColor.ORANGE, EnumDyeColor.SILVER, EnumDyeColor.GREEN },
				{ EnumDyeColor.BROWN, EnumDyeColor.SILVER, EnumDyeColor.GREEN },
				{ EnumDyeColor.ORANGE, EnumDyeColor.BROWN, EnumDyeColor.RED },
				{ EnumDyeColor.ORANGE, EnumDyeColor.YELLOW, EnumDyeColor.RED },
				{ EnumDyeColor.GRAY, EnumDyeColor.SILVER, EnumDyeColor.WHITE },
				{ EnumDyeColor.SILVER, EnumDyeColor.BLUE, EnumDyeColor.RED },
				{ EnumDyeColor.LIGHT_BLUE, EnumDyeColor.BLUE, EnumDyeColor.CYAN },
				{ EnumDyeColor.RED, EnumDyeColor.WHITE, EnumDyeColor.PINK },
				{ EnumDyeColor.BROWN, EnumDyeColor.WHITE, EnumDyeColor.PINK },
				{ EnumDyeColor.RED, EnumDyeColor.BLACK, EnumDyeColor.PURPLE },
				{ EnumDyeColor.WHITE, EnumDyeColor.LIGHT_BLUE, EnumDyeColor.LIME },
				{ EnumDyeColor.CYAN, EnumDyeColor.GREEN, EnumDyeColor.PURPLE },
				{ EnumDyeColor.YELLOW, EnumDyeColor.BLACK, EnumDyeColor.PURPLE },
				{ EnumDyeColor.BLACK, EnumDyeColor.PURPLE, EnumDyeColor.RED },
				{ EnumDyeColor.MAGENTA, EnumDyeColor.PURPLE, EnumDyeColor.BLACK },
				{ EnumDyeColor.BLACK, EnumDyeColor.RED, EnumDyeColor.PURPLE }
			}, beaconGlassColors = new EnumDyeColor[][] {
				{ EnumDyeColor.ORANGE, EnumDyeColor.ORANGE, EnumDyeColor.BROWN },
				{ EnumDyeColor.BROWN },
				{ EnumDyeColor.SILVER, EnumDyeColor.ORANGE },
				{ EnumDyeColor.ORANGE, EnumDyeColor.BROWN },
				{ EnumDyeColor.ORANGE },
				{ EnumDyeColor.BLACK, EnumDyeColor.BROWN },
				{ EnumDyeColor.YELLOW, EnumDyeColor.ORANGE, EnumDyeColor.WHITE },
				{ EnumDyeColor.RED, EnumDyeColor.ORANGE },
				{ EnumDyeColor.GRAY },
				{ EnumDyeColor.SILVER },
				{ EnumDyeColor.WHITE, EnumDyeColor.LIGHT_BLUE },
				{ EnumDyeColor.RED },
				{ EnumDyeColor.WHITE, EnumDyeColor.BROWN, EnumDyeColor.BROWN },
				{ EnumDyeColor.BLACK, EnumDyeColor.RED, EnumDyeColor.GRAY },
				{ EnumDyeColor.WHITE },
				{ EnumDyeColor.GREEN, EnumDyeColor.CYAN, EnumDyeColor.SILVER },
				{ EnumDyeColor.WHITE, EnumDyeColor.YELLOW, EnumDyeColor.WHITE },
				{ EnumDyeColor.PURPLE, EnumDyeColor.BLACK, EnumDyeColor.BLACK },
				{ EnumDyeColor.PINK, EnumDyeColor.MAGENTA },
				{ EnumDyeColor.GRAY, EnumDyeColor.BLACK }
			};

			private EnumTowerType(String name) {
				this.name = name;
			}
			
			public int getBaseDifficulty() {
				return baseDifficulty[ordinal()];
			}
			
			public int getBaseRarity() {
				return baseRarity[ordinal()];
			}
			
			public EnumDyeColor[] getDyeColors() {
				return dyeColors[ordinal()];
			}
			
			public EnumDyeColor[] getBeaconColors() {
				return beaconGlassColors[ordinal()];
			}
			
			public static EnumDyeColor[][] getAllBeaconColors() {
				return beaconGlassColors;
			}

			@Override
			public String toString() {
				return this.name;
			}

			@Override
			public String getName() {
				return this.name;
			}

			public Block getDoorBlock() {
				return doorBlocks[ordinal()];
			}
		}

		public int getFloorFromPosY(int posY) {
			final int minFloor = !isUnderground ? 1
				: -(floors - 1), maxFloor = !isUnderground ? floors + 1 : 1;
			return Math.max(Math.min((int) Math.floor((posY	- baseY -
				(!isUnderground ? 0 : 3)) / 6) + 1, floors), minFloor);
		}
		
		protected static int getTowerFloors(EnumTowerType towerType, boolean isUnderground) {
			final int difficulty = towerType.getBaseDifficulty();
			int floors = Math.min((5 + difficulty) + rand.nextInt(difficulty + 11), 20);
			if (isUnderground)
				floors >>= 1;
			return floors;
		}
	}

	public class MazeTower extends MazeTowerBase {

		public final int exitX, exitZ;
		public final Block doorBlock;
		public IBlockState air;
		public final IBlockState ceilBlock, wallBlock, wallBlock_external,
		wallBlock_corner, floorBlock, slabBlock, fenceBlock;
		public final IBlockState[] stairsBlock;
		private final EnumFacing entranceDir;
		private final EnumDyeColor[] dyeColors,
		beaconGlassColors;
		private final int entranceMinX, entranceMinZ;
		private final boolean hasXEntrance, hasOddX,
		hasOddZ, isUnderwater, isMushroomTower;
		private int chestCount,
		beaconMiniTowerIndex,
		locksmithMiniTowerIndex,
		floorHighestDepthLevel[],
		floorHighestMazeDepthLevel[],
		pathMap[][][], dataMap[][][];
		private EnumFacing exitDir;
		private BlockPos signPos, keyChestPos;
		private IBlockState[][][] blockData;
		private Path[] floorExitPaths;
		private Map<ItemStack, Integer>[] floorChestItems;
		private Stack<BlockPos>[] floorChestPos;
		private Stack<Path> paths;
		private List<Path>[] floorDeadEndPaths;
		private Stack<MiniTower> miniTowersCheck;
		private Stack<Tuple<int[], IBlockState>>[] floorAlteredStates;
		
		private MazeTower(int x, int y, int z,
			IBlockState ceilBlock, IBlockState wallBlock,
			IBlockState wallBlock_external, IBlockState wallBlock_corner,
			IBlockState floorBlock, IBlockState slabBlock, IBlockState fenceBlock,
			IBlockState[] stairsBlocks, boolean isUnderground,
			boolean hasXEntrance, EnumTowerType towerType,
			int dimId, int towerIndex) {
			super(x, y, z, getTowerFloors(towerType, isUnderground),
				isUnderground, towerType, null, null, dimId, towerIndex);
			final boolean exitChance = rand.nextBoolean();
			Arrays.fill(new int[10], getTowerFloors(EnumTowerType.OAK, false));
			floorChestItems = new HashMap[floors + 1];
			floorExitPaths = new Path[floors];
			floorHighestDepthLevel = new int[floors];
			floorHighestMazeDepthLevel = new int[floors];
			floorAlteredStates = new Stack[floors];
			floorAlteredStates[0] = new Stack<Tuple<int[], IBlockState>>();
			floorChestItems[0] = new HashMap<ItemStack, Integer>();
			this.hasXEntrance = hasXEntrance;
			this.isUnderwater = towerType == EnumTowerType.PRISMARINE;
			this.isMushroomTower = towerType == EnumTowerType.RED_MUSHROOM ||
				towerType == EnumTowerType.BROWN_MUSHROOM;
			this.ceilBlock = ceilBlock;
			this.wallBlock = wallBlock;
			this.wallBlock_external = wallBlock_external;
			this.wallBlock_corner = wallBlock_corner;
			this.floorBlock = floorBlock;
			this.slabBlock = slabBlock;
			this.fenceBlock = fenceBlock;
			this.stairsBlock = stairsBlocks;
			this.doorBlock = towerType.getDoorBlock();
			this.dyeColors = towerType.getDyeColors();
			this.beaconGlassColors = towerType.getBeaconColors();
			air = !isUnderwater ? Blocks.air
				.getDefaultState() : Blocks.water
				.getDefaultState();
			floorChestPos = new Stack[floors];
			floorChestPos[0] = new Stack<BlockPos>();
			paths = new Stack<Path>();
			miniTowersCheck = new Stack<MiniTower>();
			beaconMiniTowerIndex = -1;
			locksmithMiniTowerIndex = -1;
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
			pathMap = new int[blockData.length][16][16];
			dataMap = new int[blockData.length][16][16];
			floorDeadEndPaths = new List[floors];
		}

		private void initPaths() {
			Path entrance;
			entrance = new Path(this, null,
				entranceMinX, 0, entranceMinZ, entranceDir);
			int rebuildCount = 0, prevBeaconMiniTowerIndex = -1,
				prevLocksmithMiniTowerIndex = -1;
			for (int f = 0; f < floors; f++) {
				final Path prevPath = f != 0 ? floorExitPaths[f - 1] : null;
				while (floorAlteredStates[f].size() < 200 + (10 * getDifficulty(f + 1))) {
					if (beaconMiniTowerIndex != -1 && miniTowers.get(beaconMiniTowerIndex).floor == f + 1 &&
						beaconMiniTowerIndex != prevBeaconMiniTowerIndex) {
						if (prevBeaconMiniTowerIndex != -1)
							miniTowers.get(beaconMiniTowerIndex =
								prevBeaconMiniTowerIndex).setHasBeacon(true);
						else
							beaconMiniTowerIndex = -1;
						locksmithMiniTowerIndex = prevLocksmithMiniTowerIndex;
					}
					unbuildFloor(f + 1);
					entrance = f != 0 ? new Path(this, prevPath,
						prevPath.fx, prevPath.fy + 6, prevPath.fz, null) :
						new Path(this, null, entranceMinX, 0, entranceMinZ, entranceDir);
					rebuildCount++;
				}
				if (floorExitPaths[f] != null) {
					//fillGaps(f);
					prevBeaconMiniTowerIndex = beaconMiniTowerIndex;
					prevLocksmithMiniTowerIndex = locksmithMiniTowerIndex;
					entrance = floorExitPaths[f].newFloor();
				} else
					break;
			}
			
			int floorIndex;
			
			if (chestCount != 0) {
				while (floorChestPos[floorIndex = rand.nextInt(floors)].isEmpty());
				keyChestPos = floorChestPos[floorIndex].get(rand.nextInt(floorChestPos[floorIndex].size()));
			}
			if (beaconMiniTowerIndex != -1 && !isUnderground)
				miniTowers.get(beaconMiniTowerIndex).setHasShop(false);
			if (locksmithMiniTowerIndex == -1) {
				if (beaconMiniTowerIndex != -1)
					locksmithMiniTowerIndex = beaconMiniTowerIndex +
						((miniTowers.size() > beaconMiniTowerIndex + 1) ? 1 : -1);
				else
					locksmithMiniTowerIndex = miniTowers.size() - 1;
			} 
			if (locksmithMiniTowerIndex != -1)
				miniTowers.get(locksmithMiniTowerIndex).setIsLocksmithRequired(true);
			MazeTowers.network.sendToAll(new PacketDebugMessage("Rebuilt floors " +
				rebuildCount + " times"));
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
					Entity.class, new AxisAlignedBB(
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
					BlockDispenser.FACING, dispenserDir), piston =
					MazeTowers.BlockMemoryPistonOff.getDefaultState(),
					button = MazeTowers.BlockHiddenButton
				.getDefaultState().withProperty(
					BlockHiddenButton.FACING, dispenserDir);
			IBlockState glass = getColourBlockState(
				Blocks.stained_glass, dyeColors[0]);
			IBlockState[][][] data = new IBlockState[yLimit + 8][xzLimit][xzLimit];

			for (y = 0; y < yLimit; y++) {
				for (z = 0; z < xzLimit; z++) {
					for (x = 0; x < xzLimit; x++) {
						final boolean isEdge = x == 0 || x == 15 || z == 0 || z == 15,
						isEdgeCorner = isEdge && ((x == 0 || x == 15) && (z == 0 || z == 15));
						if (isEdge)
							data[y][z][x] = !isEdgeCorner ? wallBlock_external : wallBlock_corner;
						else if (y % 6 == 0 || y % 6 == 4
							|| y == yLimit - 1 || x == 0
							|| z == 0 || x == xzLimit - 1
							|| z == xzLimit - 1)
							data[y][z][x] = y % 6 == 0
								|| y == yLimit - 1 ? floorBlock
								: y % 6 == 4 ? ceilBlock : wallBlock;
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
							&& x != 0 && x != 15),
							isInnerEdge2 = ((x == 13 || x == 2)
								&& z >= 2 && z <= 13)
								|| ((z == 13 || z == 2)
								&& x >= 2 && x <= 13), isCarpetPos = y == yLimit;
						if (!isUnderground || !isInnerEdge
							|| y <= yLimit + 1
							|| y >= yLimit + 5) {
							if (!isCarpetPos || isEdge) {
								data[y][z][x] = (!isEdge
									&& (!isUnderground && y != yLimit + 5) || (isUnderground && y < yLimit + 5))
									|| (isEdge && ((y != yLimit + 1 && y != yLimit + 3) ||
									isEdgeCorner)) ? isEdge ? isUnderground
									|| y != yLimit + 6 ? wallBlock_external : fenceBlock : !isMushroomTower ||
									y != yLimit + 6 ? (!isUnderground || !isInnerEdge2 || y != yLimit + 3) ?
									air : wallBlock_external : fenceBlock : glass;
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
														BlockDirectional.FACING,
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
														BlockDirectional.FACING,
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
											BlockHorizontal.FACING,
											repeaterDir);
								}
								data[y][z][x] = state;
							}
						}
					}
				}
			}

			return data;
		}

		public IBlockState[][][] getBlockData() {
			return blockData;
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
			return (path.floor > 5 || (path.tower.isUnderground && path.floor > 2));
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
		
		private void fillGaps(int floor) {
    		final int startX = ((hasXEntrance && hasOddX) ||
    			(!hasXEntrance && !hasOddX)) ? 2 : 1,
    		startZ = ((!hasXEntrance && hasOddZ) ||
    			(hasXEntrance && !hasOddZ)) ? 2 : 1;
			for (int z = startZ; z <= startZ + 12; z += 12) {
				for (int x = startX; x <= startX + 12; x += 12) {
					final int y = floor * 6;
					if (Path.getPathAt(this, null, new int[] { y + 1, z, x }) == null) {
						/*new FillerPath(this, null,
							new ArrayList<FillerPath>(), x, y, z, null);*/
						break;
					}
				}
			}
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
								.next()) != null && !gapFilled) {
								Path path;
								int dirIndex;
								int dirAxis;
								int dirSign;
								int dist;
								if ((Path.getStateWithOffset(
									this, dir, 2, x, y + 1, z)) != null) {
									dirIndex = dir.getIndex();
									dirAxis = (int) Math.floor(dirIndex * 0.5);
									dirSign = dirIndex % 2 == 0 ? -1 : 1;
									/*
									 * path.children.add(new Path(this, path,
									 * path.chain, x + (dirAxis == 1 ? dirSign *
									 * 2 : 0), y, z + (dirAxis == 2 ? dirSign *
									 * 2 : 0), dir.getOpposite()));
									 */
									dist = Path.getMaxDistance(
										this, dir, 14, (dirAxis == 1 ?
										startZ == 1 : startX == 1), x, y, z);
									if (Path
										.isPosValid(this, Path.getRelCoordsWithOffset(
										dir, dist + 2, x, y, z)))
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
			BlockPos lockPos = null;
			final IBlockState stone = Blocks.stone
				.getDefaultState();
			final IBlockState downButton = ModBlocks.hiddenButton
				.getDefaultState().withProperty(
					BlockDirectional.FACING, EnumFacing.DOWN);
			Map<BlockPos, Tuple<Integer, Integer>> floorScannerPos = null;
			boolean addCircuitBreaker = false;
			int floor = 0;
			removeEntities(worldIn);
			for (int y = 0; y <= yLimit + 7; y++) {
				if (isUnderwater && y == yLimit)
					air = Blocks.air.getDefaultState();
				else if (y % 6 == 0 && y < yLimit) {
					floor++;
					floorScannerPos = new HashMap<BlockPos, Tuple<Integer, Integer>>();
				}
				for (int z = minXZ; z <= maxXZ; z++) {
					for (int x = minXZ; x <= maxXZ; x++) {
						BlockPos pos = getPosFromCoords(ix,
							iz, x, y, z);
						IBlockState state = blockData[y][z][x];
						final boolean isEdge = isUnderground && (x == 0 ||
							x == 15 || z == 0 || z == 15);
						if (y <= yLimit) {
							final Block block;
							if ((state == null || (state != air
								&& (block = state.getBlock()) != Blocks.ladder &&
								block != MazeTowers.BlockMemoryPiston
								&& isEdge && block != Blocks.torch))
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
							Block block = state.getBlock();
							if (block != doorBlock) {
								if (block == Blocks.melon_block) {
									addCircuitBreaker = true;
									state = MTPuzzle.redstone;
								} else if (block == Blocks.pumpkin) {
									state = air;
									block = air.getBlock();
								} else if (y % 6 == 5
									&& y < yLimit
									&& state == air) {
									state = wallBlock;
									block = wallBlock.getBlock();
								}
								worldIn.setBlockState(pos, state, 2);
								if (block == Blocks.dispenser) {
									addDispenserContents(
										worldIn, pos, x, y,
										z, floor - 1,
										y == yLimit + 2);
									if (y == yLimit + 2)
										worldIn
											.setBlockState(
												pos, state,
												2);
								} else if (block instanceof BlockChest) {
									Path path = y < yLimit ? Path
										.getPathAt(this, null, x, y, z) :
										paths.get(paths.size() - 1);
									Iterator iterator = initChest(
										worldIn, pos, path, 0).iterator();
									if (y < yLimit) {
    									int mazeDepth = path
    										.getMazeDepth();
    									while (iterator
    										.hasNext()) {
    										ItemStack stack = (ItemStack) iterator.next();
    										floorChestItems[floor - 1]
    											.put(stack, mazeDepth);
    									}
									}
								} else if (block ==
									MazeTowers.BlockSpecialMobSpawner) {
									TileEntitySpecialMobSpawner spawner =
										(TileEntitySpecialMobSpawner) worldIn
										.getTileEntity(pos);
									try {
										spawner.getSpawnerBaseLogic()
											.setEntityName(
											getEntityNameForSpawn(
											floor, isUnderwater));
									} catch (NullPointerException e) {
										e = null;
									}
								} else if (block == Blocks.web)
									worldIn.setTileEntity(pos,
										new TileEntityWebSpiderSpawner(difficulty));
								else if (block == MazeTowers.BlockHiddenButton
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
								else if (block == Blocks.wall_sign)
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
								} else if (block instanceof BlockItemScanner) {
									Path path = Path
										.getPathAt(this,
											null, x, y, z);
									floorScannerPos.put(pos,new Tuple(path.pathIndex, path.getMazeDepth()));
									worldIn.setBlockState(
										pos.offset(state.getValue(BlockItemScanner.FACING)
										.getOpposite()), wallBlock, 0);
								} else if (block == ModBlocks.lock) {
									lockPos = pos;
									TileEntityLock tel =
										(TileEntityLock) worldIn.getTileEntity(pos);
									tel.setTypeIndex(towerType.ordinal());
									worldIn.setTileEntity(pos, tel);
								}
							} else {
								if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
									ItemDoor.placeDoor(worldIn, pos, state
									.getValue(BlockDoor.FACING), doorBlock, false);
							}
						}
						final int groundY;

						if (y == 0 && (groundY = MTUtils.getGroundY(worldIn,
							ix + x, baseY, iz + z, 1, isUnderwater) + 1) < baseY) {
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
						mazeDepth = curVals
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
								.get(curVals
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
							if (!(scannerPath.mtp instanceof MTPKeyDoor)) {
    							if (!stackList.isEmpty())
    								te.generateRandomKeyStackFromList(stackList);
    							if (te.getKeyStack() == null)
    								te.setKeyStack(getDefaultScannerKeyStack(
    									scannerPath.difficulty));
							} else
								te.setKeyStack(new ItemStack(
									MazeTowers.ItemColoredKey, 1, towerType.ordinal()));
							if (doesStateMatch) {
								String keyStackDisplayName = te
									.getKeyStack()
									.getDisplayName();
								((TileEntitySign) worldIn
									.getTileEntity(pos)).signText[2] = new TextComponentString(
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
			addTowerDecorations(worldIn);
			final Path entrancePath = paths.get(0);
			final EnumFacing entranceDir = entrancePath
				.getDir();
			IBlockState signState = Blocks.wall_sign
				.getDefaultState().withProperty(
					BlockWallSign.FACING,
					entrancePath.getDir().getOpposite());
			TileEntitySign te;
			String infoString = getInfoString();
			BlockPos entrancePos = new BlockPos(ix
				+ (entrancePath.ix), baseY + 3, iz
				+ (entrancePath.iz)),
			signPos = entrancePos.offset(entrancePath
				.getDir().getOpposite(), 2);
			BlockPos thresholdPos;
			if (!isUnderground) {
				worldIn.setBlockState(entrancePos.down(),
					blockData[1][entrancePath.iz][entrancePath.ix]);
				worldIn.setBlockState(entrancePos.down(2),
					blockData[2][entrancePath.iz][entrancePath.ix]);
			}
			this.signPos = signPos;
			worldIn.setBlockState(signPos, signState, 2);
			te = (TileEntitySign) worldIn
				.getTileEntity(signPos);
			te.signText[0] = new TextComponentString(
				towerTypeName);
			te.signText[1] = new TextComponentString(
				"Maze Tower");
			if (isUnderground)
				te.signText[2] = new TextComponentString(
					"(Underground)");
			te.signText[3] = new TextComponentString(
				infoString);
			if (worldIn.isRemote)
				te.setEditable(false);
			worldIn.setTileEntity(signPos, te);
			worldIn.setBlockState(thresholdPos = entrancePos.down(2)
				.offset(entranceDir, -1), MazeTowers.BlockMazeTowerThreshold
				.getDefaultState(), 2);
			worldIn.setBlockState(thresholdPos.offset(
				entranceDir, 2),
				MazeTowers.BlockMazeTowerThreshold
				.getDefaultState(), 2);
			long startTime = System.nanoTime();
			buildStairCase(worldIn, entrancePos.down(2), entranceDir.getOpposite());
			long endTime = (System.nanoTime() - startTime) / 1000000;
			MazeTowers.network.sendToDimension(
				new PacketDebugMessage(
					"Staircase build took " + endTime
						+ " ms"), worldIn.provider
					.getDimension());
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
					te.signText[1] = new TextComponentString(
						"Exit");
					te.signText[2] = new TextComponentString(
						"\u2193\u2193\u2193");
				} else {
					te.signText[2] = new TextComponentString(
						"Exit");
					te.signText[1] = new TextComponentString(
						"\u2191\u2191\u2191");
				}
				worldIn.setTileEntity(signPos, te);
			} catch (NullPointerException e) {
				e = null;
			}

			final int dimId = worldIn.provider
				.getDimension() + 1, minY = baseY
				+ (!isUnderground ? 0 : -(yLimit + 6)), maxY = baseY
				+ (yLimit + 7);
			
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
			
			if (keyChestPos != null)
    			MazeTowers.network.sendToDimension(new PacketDebugMessage(
    				"Key in chest at " + keyChestPos.toString()
    				), worldIn.provider.getDimension());
			if (lockPos != null) {
				/*MazeTowers.network.sendToDimension(
					new PacketSyncLockType(lockPos, towerType.ordinal()),
					worldIn.provider.getDimension());*/
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
							final int groundY;

							if (y == 0 && !isUnderground && (groundY = MTUtils
									.getGroundY(worldIn,
										ix + x, baseY, iz + z,
										1, isUnderwater) + 1) < baseY) {
								for (int y2 = 0; y2 <= baseY
									- groundY; y2++) {
									try {
										worldIn.setBlockState(
											pos.down(y2), wallBlock_external, 0);
									} catch (NullPointerException e) {
										e = null;
									}
								}
							}
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
				: 1]/*, torch = !isUnderwater ? Blocks.torch
				.getDefaultState() : Blocks.sea_lantern
				.getDefaultState()*/;
			IBlockState state;
			final EnumFacing sideDirR = dir.rotateY(), sideDirL = dir
				.rotateYCCW();
			boolean isFirstStair = true, hasStairs = (state = worldIn.getBlockState(pos = pos
					.down().offset(dir))) == air || !state.getBlock().isFullBlock(state);
			
			while (isFirstStair || (hasStairs && ((state = worldIn.getBlockState(pos = pos
				.down().offset(dir))) == air || !state.getBlock().isFullBlock(state)))) {
				final int minY = MTUtils.getGroundY(
					worldIn, pos.getX(), --maxY,
					pos.getZ(), 1, isUnderwater
						&& (!isUnderground || (baseY < 63 && worldIn.provider.getDimension() == 0)));
				BlockPos pos2 = pos.offset(sideDirR, 2);

				for (int s = 0; s < 3; s++) {
					pos2 = pos2.offset(sideDirL);
					for (int y = -1; y <= maxY - minY; y++) {
						if (s != 1)
							worldIn.setBlockState(pos2.down(y),
								wallBlock_external, 2);
						else if (y != -1) {
							if (hasStairs)
								worldIn.setBlockState(pos2.down(y),
									y != 0 ? floorBlock : stairState, 2);
						} else if (minY < 0)
							return;
					}
				}

				if (isFirstStair) {
					worldIn.setBlockState(pos2.up(2), fenceBlock, 2);
					if (this.isUnderwater && this.isUnderground)
						worldIn.setBlockState(pos2.up(2)
							.offset(sideDirR), Blocks.water.getDefaultState());
					worldIn.setBlockState(pos2.up(2)
						.offset(sideDirR, 2), fenceBlock, 2);
					worldIn.setBlockState(pos2.up(3),
						fenceBlock, 2);
					worldIn.setBlockState(pos2.up(3).offset(sideDirR), wallBlock_external, 2);
					worldIn.setBlockState(pos2.up(3).offset(sideDirR, 2), fenceBlock, 2);
					isFirstStair = false;
				}
			}
		}
		
		private void addTowerDecorations(World worldIn) {
			if (towerType == EnumTowerType.PURPUR) { 
				buildFloorDecoration(worldIn, new int[] { 7, 8 }, new int[] { 1, 3, 5 },
					stairsBlock[1], true);
				buildFloorDecoration(worldIn, new int[] { -1, 0, 1, 4, 5, 10, 11, 14, 15, 16 }, new int[] { 5 },
					stairsBlock[1].withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), true);
				buildFloorDecoration(worldIn, new int[] { 2, 3, 6, 9, 12, 13 }, new int[] { 5 },
					stairsBlock[1], true);
				buildFloorDecoration(worldIn, new int[] { -1, 16 }, new int[] { !isUnderground ? 6 : 4  },
					Blocks.end_rod.getDefaultState(), false);
			} else {
				final int[] allCoords = new int[] { -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 },
				oddCoords = new int[] { -1, 1, 3, 5, 7, 8, 10, 12, 14, 16 },
				evenCoords = new int[] { 0, 2, 4, 6, 9, 11, 13, 15 },
				midCoords = new int[] { 2, 6, 9, 13 };
				boolean stairChance, stairChance2, stairChance3;
				final int upperY;
				stairChance = rand.nextBoolean();
				stairChance2 = rand.nextBoolean();
				upperY = stairChance2 ? 2 : 3;
				IBlockState stateBottom = stairChance ? stairsBlock[1].withProperty(BlockStairs.HALF,
					BlockStairs.EnumHalf.TOP) : slabBlock, stateTop = stairChance ? stairsBlock[1] :
					slabBlock.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
					
				if (rand.nextBoolean()) {
					buildFloorDecoration(worldIn, allCoords, new int[] { 1 }, stateBottom, stairChance);
					buildFloorDecoration(worldIn, allCoords, new int[] { upperY }, stateTop, stairChance);
				} else {
					stairChance3 = rand.nextBoolean();
					buildFloorDecoration(worldIn, oddCoords, new int[] { 1 }, stateBottom, stairChance);
					buildFloorDecoration(worldIn, evenCoords, new int[] { 1 }, stateTop, stairChance);
					buildFloorDecoration(worldIn, stairChance3 ? evenCoords : oddCoords, new int[] { upperY }, stateBottom, stairChance);
					buildFloorDecoration(worldIn, stairChance3 ? oddCoords : evenCoords, new int[] { upperY }, stateTop, stairChance);
				}
				if (!stairChance2)
					buildFloorDecoration(worldIn, allCoords, new int[] { 2 }, wallBlock_external, false);
				else if (rand.nextBoolean()) {
					if (rand.nextBoolean()) {
						stairChance3 = rand.nextBoolean();
						buildFloorDecoration(worldIn, oddCoords, new int[] { stairChance3 ? rand.nextBoolean() ? 0 : 3 : 0 },
							fenceBlock, stairChance);
						if (stairChance3)
							buildFloorDecoration(worldIn, oddCoords, new int[] { 3 }, fenceBlock, stairChance);
					} else {
						buildFloorDecoration(worldIn, midCoords, new int[] { 0 }, stateBottom, stairChance);
						buildFloorDecoration(worldIn, midCoords, new int[] { 3 }, stateTop, stairChance);
					}
				}
					
				stairChance = rand.nextBoolean();
				buildFloorDecoration(worldIn, allCoords, new int[] { stairChance ? stairChance2 ? 3 : 4 : 5 },
					stairsBlock[1].withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP), true);
				if (!stairChance2)
					buildFloorDecoration(worldIn, allCoords, new int[] { 4 }, wallBlock_external, false);
				buildFloorDecoration(worldIn, allCoords, new int[] { stairChance ? 5 : stairChance2 ? 4 : 3 },
					stairsBlock[1], true);
			}
		}
		
		private void buildFloorDecoration(World worldIn, int[] xzCoords, int[] yCoords,
			IBlockState state, boolean rotateState) {
			int ix = chunkX << 4, iz = chunkZ << 4;
			IBlockState[] states = rotateState ? new IBlockState[] { state,
				state.withRotation(Rotation.CLOCKWISE_180), state.withRotation(Rotation.COUNTERCLOCKWISE_90),
				state.withRotation(Rotation.CLOCKWISE_90)
			} : new IBlockState[] { state, state, state, state };
			for (int f = 0; f <= floors; f++) {
				for (int yCoord : yCoords) {
					final int y = yCoord + f * 6;
					if (y == (6 * floors) + 2 || y == (6 * floors) + 4)
						continue;
					for (int xz : xzCoords) {
						for (EnumFacing dir : EnumFacing.HORIZONTALS) {
							int x, z;
							if (dir == EnumFacing.NORTH) {
								x = xz;
								z = -1;
							} else if (dir == EnumFacing.WEST) {
								x = -1;
								z = xz;
							} else if (dir == EnumFacing.SOUTH) {
								x = xz;
								z = 16;
							} else {
								x = 16;
								z = xz;
							}
							final BlockPos pos = getPosFromCoords(
								ix, iz, x, y, z);
							if (f != 0 || yCoord != 2)
								worldIn.setBlockState(pos, states[dir.ordinal() - 2]);
						}
					}
				}
			}
		}
		
		private void unbuildFloor(int floor) {
			Path path;
			Stack<Tuple<int[], IBlockState>> alteredStates;
			Tuple<int[], IBlockState> alteredState;
			int[] coords;
			IBlockState oldState;
			BlockPos chestPos;
			alteredStates = floorAlteredStates[floor - 1];
			do {
				path = paths.pop();
			} while ((floor != 1 || path.parent != null) && !path.isFloorEntrance);
			this.floorHighestDepthLevel[floor - 1] = path.depth;
			this.floorHighestMazeDepthLevel[floor - 1] = path.mazeDepth;
			for (int c = this.floorChestPos[floor - 1].size() - 1; c >= 0; c--) {
				this.chestCount--;
				this.floorChestPos[floor - 1].remove(c);
			}
			this.floorChestItems[floor - 1].clear();
			this.floorDeadEndPaths[floor - 1].clear();
			this.floorExitPaths[floor - 1] = null;
			do {
				alteredState = alteredStates.pop();
				coords = alteredState.getFirst();
				oldState = alteredState.getSecond();
				this.blockData[coords[0]][coords[1]][coords[2]] = oldState;
				this.dataMap[coords[0]][coords[1]][coords[2]] = 0;
				this.pathMap[coords[0]][coords[1]][coords[2]] = 0;
			} while (!alteredStates.isEmpty());
			while (!miniTowers.isEmpty() && miniTowers.peek().floor == floor && !miniTowers.isEmpty()) {
				miniTowers.pop();
				miniTowersCheck.pop();
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
						Item potionItem = rand.nextInt(10 - difficulty) == 0 ?
							Items.lingering_potion : Items.splash_potion;
						List<Integer> projectileIndexes = new ArrayList<Integer>();
						ItemStack[] projectileList = new ItemStack[] {
							new ItemStack(potionItem), // Slowness I (short)
							new ItemStack(potionItem), // Weakness I (short)
							new ItemStack(potionItem), // Poison I (short)
							new ItemStack(potionItem), // Harming I
							new ItemStack(potionItem), // Slowness I (long)
							new ItemStack(potionItem), // Weakness I (long)
							new ItemStack(potionItem), // Poison I (long)
							new ItemStack(potionItem), // Poison II
							new ItemStack(MazeTowers.ItemExplosiveArrow),
							new ItemStack(potionItem), // Poison II
							new ItemStack(potionItem) // Harming II
						};
						projectileList[0].setTagInfo("Potion", new NBTTagString("minecraft:slowness"));
						projectileList[1].setTagInfo("Potion", new NBTTagString("minecraft:weakness"));
						projectileList[2].setTagInfo("Potion", new NBTTagString("minecraft:poison"));
						projectileList[3].setTagInfo("Potion", new NBTTagString("minecraft:harming"));
						projectileList[4].setTagInfo("Potion", new NBTTagString("minecraft:long_slowness"));
						projectileList[5].setTagInfo("Potion", new NBTTagString("minecraft:long_weakness"));
						projectileList[6].setTagInfo("Potion", new NBTTagString("minecraft:long_poison"));
						projectileList[7].setTagInfo("Potion", new NBTTagString("minecraft:strong_poison"));
						projectileList[9].setTagInfo("Potion", new NBTTagString("minecraft:strong_poison"));
						projectileList[10].setTagInfo("Potion", new NBTTagString("minecraft:strong_harming"));
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
			final IBlockState spawnerState =
				MazeTowers.BlockSpecialMobSpawner.getDefaultState();
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
					floorAlteredStates[floor - 1].push(new Tuple(coords, blockData[coords[0]][coords[1]][coords[2]]));
					if (isAccessible) {
						blockData[coords[0] - 1][coords[1]][coords[2]] = Blocks.pumpkin
							.getDefaultState();
						floorAlteredStates[floor - 1].push(new Tuple(new int[] { coords[0] - 1, coords[1], coords[2] },
							blockData[coords[0] - 1][coords[1]][coords[2]]));
						if (blockData[coords[0] - 2][coords[1]][coords[2]] == air) {
							blockData[coords[0] - 2][coords[1]][coords[2]] = Blocks.pumpkin
								.getDefaultState();
							floorAlteredStates[floor - 1].push(new Tuple(new int[] { coords[0] - 2, coords[1], coords[2] },
								blockData[coords[0] - 2][coords[1]][coords[2]]));
						}
					}
				}
				spawnerCoordsList.remove(randIndex);
			}
		}

		private void addMiniTower(MiniTower mt) {
			miniTowers.add(mt);
			miniTowersCheck.add(mt);
			if (mt.floor > floors - 3) {
				MiniTower beaconMiniTower;
				if (!isUnderground && (beaconMiniTowerIndex == -1
					|| (beaconMiniTowerIndex != mt.miniTowerIndex &&
					mt.floor + mt.floors > (beaconMiniTower = miniTowers
					.get(beaconMiniTowerIndex)).floor + beaconMiniTower.floors)))
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
						if (Path.getStateAt(this, path.fx,
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
									f + 1, 0, false).getDefaultState() : getChestBlock(
									f + 1, 0, true).getDefaultState();
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
										&& Path.getStateAt(
											this, path.fx,
											path.fy - 1,
											path.fz) == air)
										path.setStateAt(
											path.fx,
											path.fy,
											path.fz,
											floorBlock);
								}
								floorChestPos[f].push(getPosFromCoords(
									chunkX << 4, chunkZ << 4, path.fx,
									path.fy + 1, path.fz));
								chestCount++;
							}
						}
					}
				}
			}
		}

		public List<ItemStack> initChest(World world,
			BlockPos pos, Path path, int addToRarity) {
			if (path == null)
				path = paths.get(paths.size() - 1);
			
			TileEntityChest chestTEC = (TileEntityChest) world
				.getTileEntity(pos);
			int rarity = Math.min(path.rarity + addToRarity, 9);
			if (chestTEC != null) {
				ArrayList<ItemStack> items = new ArrayList<ItemStack>();
				chestTEC.setLoot(new ResourceLocation("mazetowers:maze_tower_treasure_" + rarity), 0L);
				/*WeightedRandomChestContent
					.generateChestContents(world.rand,
					chestInfo[rarity].getItems(world.rand),
					chestTEC, chestInfo[rarity]
					.getCount(world.rand));*/
				for (int i = 0; i < chestTEC
					.getSizeInventory(); i++) {
					ItemStack curStack;
					if ((curStack = chestTEC
						.getStackInSlot(i)) != null)
						items.add(curStack);
				}
				if (pos.equals(keyChestPos)) {
					int slotIndex;
					while (chestTEC.getStackInSlot(slotIndex = rand.nextInt(chestTEC.getSizeInventory())) != null);
					chestTEC.setInventorySlotContents(slotIndex,
						new ItemStack(MazeTowers.ItemColoredKey, 1, towerType.ordinal()));
				}
				world.setTileEntity(pos, chestTEC);
				return items;
			}

			return new ArrayList<ItemStack>();
		}

		private String getEntityNameForSpawn(int floor,
			boolean isUnderwater) {
			final boolean useSpecificMob = isUnderwater || ((isNetherTower ||
				isMushroomTower || towerType == EnumTowerType.PURPUR) && rand.nextInt(3) == 0);
			final int diffIndex = getDifficulty(floor), indexPart = (int) (diffIndex * 0.375);
			String entityName;
			
			if (useSpecificMob) {
				if (isUnderwater)
					entityName = "Guardian";
				else if (isNetherTower)
					entityName = "Skeleton";
				else if (towerType == EnumTowerType.PURPUR)
					entityName = "Shulker";
				else
					entityName = "MushroomCow";
			} else {
    			entityName = mobList[Math.min(rand
    				.nextInt((diffIndex >> 1) + 1)
    				+ (indexPart - (int) Math.min((rand
    					.nextGaussian() * 1.5)
    					+ rand.nextInt(3) + 1, indexPart))
    				+ (diffIndex >> 1), mobList.length - 1)];
			}

			return entityName;
		}

		private Block getChestBlock(int floor,
			int addToRarity, boolean isTrapped) {
			int rareIndex = Math.min(getRarity(floor)
				+ addToRarity, 9);
			if (rareIndex < 2)
				return !isTrapped ? Blocks.chest : Blocks.trapped_chest;
			else if (rareIndex < 4)
				return !isTrapped ? MazeTowers.BlockIronChest :
					MazeTowers.BlockTrappedIronChest;
			else if (rareIndex < 6)
				return !isTrapped ? MazeTowers.BlockGoldChest :
					MazeTowers.BlockTrappedGoldChest;
			else if (rareIndex < 8)
				return !isTrapped ? MazeTowers.BlockDiamondChest :
					MazeTowers.BlockTrappedDiamondChest;
			else
				return !isTrapped ? MazeTowers.BlockSpectriteChest :
					MazeTowers.BlockTrappedSpectriteChest;
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
		
		private class InventoryItemFrame implements IInventory {
			
			private ILockableContainer containerItemFrame;

			@Override
			public String getName() {
				return null;
			}

			@Override
			public boolean hasCustomName() {
				return false;
			}

			@Override
			public ITextComponent getDisplayName() {
				return null;
			}

			@Override
			public int getSizeInventory() {
				return 1;
			}

			@Override
			public ItemStack getStackInSlot(int index) {
				return null;
			}

			@Override
			public ItemStack decrStackSize(int index, int count) {
				return null;
			}

			@Override
			public ItemStack removeStackFromSlot(int index) {
				return null;
			}

			@Override
			public void setInventorySlotContents(int index, ItemStack stack) {
				
			}

			@Override
			public int getInventoryStackLimit() {
				return 64;
			}

			@Override
			public void markDirty() {
			}

			@Override
			public boolean isUseableByPlayer(EntityPlayer player) {
				return false;
			}

			@Override
			public void openInventory(EntityPlayer player) {
			}

			@Override
			public void closeInventory(EntityPlayer player) {
			}

			@Override
			public boolean isItemValidForSlot(int index, ItemStack stack) {
				return index == 0;
			}

			@Override
			public int getField(int id) {
				return 0;
			}

			@Override
			public void setField(int id, int value) {
			}

			@Override
			public int getFieldCount() {
				return 0;
			}

			@Override
			public void clear() {
				this.setInventorySlotContents(0, null);
			}
			
		}

		private ItemStack getRandomItemFrameStack(World worldIn, int floor) {
			int rareIndex = Math.min(getRarity(floor), 9);
			if (rareIndex < 4
					|| rand.nextInt(32 - (rareIndex - 4) * 5) != 0) {
				LootTable lootTable = worldIn.getLootTableManager()
					.getLootTableFromLocation(new ResourceLocation("mazetowers:maze_tower_treasure_" + rareIndex));
				List<ItemStack> generatedLoot =
					lootTable.generateLootForPools(worldIn.rand, new LootContext.Builder((WorldServer) worldIn).build());
				ItemStack curStack;
				for (int s = 0; s < generatedLoot.size(); s++) {
					curStack = generatedLoot.get(s);
					if (curStack != null)
						return curStack;
				}
				return null;
			}
			return new ItemStack(ModItems.explosive_bow);
		}

		private int getRandomDyeColorIndex() {
			int dyeColorChance = rand.nextInt(16);
			return dyeColorChance < 12 ? 0
				: dyeColorChance != 15 ? 1 : 2;
		}

		public EnumDyeColor getRandomDyeColor() {
			return dyeColors[getRandomDyeColorIndex()];
		}
		
		private ItemStack getDefaultScannerKeyStack(int difficulty) {
			final ItemStack stack;
			if (difficulty < 6)
				stack = new ItemStack(Items.iron_ingot);
			else if (difficulty < 8)
				stack = new ItemStack(Items.gold_ingot);
			else
				stack = new ItemStack(Items.diamond);
			return stack;
		}

		public void setBeaconMiniTower(MiniTower miniTower) {
			if (beaconMiniTowerIndex != -1) {
				MiniTower prevBeaconMiniTower = miniTowers.get(beaconMiniTowerIndex);
				if (!isUnderground)
					prevBeaconMiniTower.setHasBeacon(false);
				locksmithMiniTowerIndex = miniTower.conn == null ?
					beaconMiniTowerIndex : miniTower.conn.miniTowerIndex;
			}
			if (!isUnderground)
				miniTower.setHasBeacon(true);
			beaconMiniTowerIndex = miniTower.miniTowerIndex;
		}
	}

	public static class MiniTower {

		private final MazeTowerBase towerBase;
		private final MazeTower tower;
		private final Path path;
		private final EnumFacing dir;
		private EnumFacing dirC;
		private final int miniTowerIndex,
		floor, floors, dist, distC, dirSign,
		dirSignC, connYDiff, ix, iz, minX, minZ;
		private int minXBridge, minZBridge,
		minXBridgeC, minZBridgeC;
		private final int minXSupport,
		minZSupport, maxX, maxZ;
		private int maxXBridge, maxZBridge,
		maxXBridgeC, maxZBridgeC;
		private final int maxXSupport,
		maxZSupport, baseY, height;
		private int baseYBridge,
		heightBridge, baseYBridgeC, heightBridgeC,
		baseYSupport, heightSupport;
		private final int[] frameXCoords, frameZCoords;
		private static final int[][] fenceXCoords = new int[][] {
			new int[] { 1, 2, 2, 3, 4, 5, 6, 6, 7 },
			new int[] { 7, 6, 6, 5, 4, 3, 2, 2, 1 },
			new int[] { 7, 7, 8, 8, 8, 8, 8, 7, 7 },
			new int[] { 1, 1, 0, 0, 0, 0, 0, 1, 1 } },
		fenceZCoords = new int[][] {
			new int[] { 7, 7, 8, 8, 8, 8, 8, 7, 7 },
			new int[] { 1, 1, 0, 0, 0, 0, 0, 1, 1 },
			new int[] { 1, 2, 2, 3, 4, 5, 6, 6, 7 },
			new int[] { 7, 6, 6, 5, 4, 3, 2, 2, 1 } };
		private static final int[]
		shelfXCoords = new int[] { 3, 3, 7, 1 },
		shelfZCoords = new int[] { 7, 1, 3, 3 };
		private final boolean isXDir,
		isReverse, connLadder, isConnDownward,
		hasItemFrame[], hasFenceWall[], hasFenceWallExtended[],
		hasBookShelf[][], hasFlowerPot[][];
		private boolean hasBeacon, hasShop, isShopClockInverted, isLocksmithRequired;
		private int dyeColorIndex, shopVendorProfessionId;
		private BlockPos chestPos;
		private MiniTower conn;
		private IBlockState[][][] stateMap;
		private BitSet[][] blockBreakabilityData;
		private IBlockState[][][] stateMapBridge;
		private BitSet[][] blockBreakabilityDataBridge;
		private IBlockState[][][] stateMapBridgeC;
		private BitSet[][] blockBreakabilityDataBridgeC;
		private IBlockState[][][] stateMapSupport;
		
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
			dirSign = dir.getAxisDirection() == AxisDirection.POSITIVE ? 1 : -1;
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
			height = (floors * 3) + 4; // C
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
				int y1 = (height - 7) + ((floor - 1) * 6) // C
					* yMult, y2 = (conn.height - 7) // C
					+ ((conn.floor - 1) * 6) * yMult;
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
					connYDiff = y1 - y2;
				} else {
					baseYBridgeC = tower.baseY + y1;
					heightBridgeC = (y2 - y1) + 4;
					connYDiff = y2 - y1;
				}
				conn.dirC = EnumFacing.values()[connData[1]];
				dirC = conn.dirC.getOpposite();
				connLadder = (baseY + height) != (conn.baseY + conn.height);//distC < connYDiff;

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
			
			if (rand.nextInt(!tower.isNetherTower ? 3 : 7) == 0)
				setHasShop(true);
			
			chestPos = new BlockPos(ix + 4
				+ minX, baseY + height - 5, iz + 4 + minZ); // C
			if (!hasShop) {
				tower.floorChestPos[path.floor - 1].push(chestPos);
				tower.chestCount++;
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
				.min(difficulty + dyeColorIndex, 9), rarity = tower.getRarity(floor),
				rarityIndex = Math.min(rarity + dyeColorIndex, 9);
			final IBlockState air = Blocks.air
				.getDefaultState(), glass = tower
				.getColourBlockState(Blocks.stained_glass,
					tower.dyeColors[dyeColorIndex]), mineral;
			final BlockPos chestPos = new BlockPos(ix + 4
				+ minX, baseY + height - 5, iz + 4 + minZ); // C

			if (!hasBeacon)
				mineral = !hasShop ? null : ModBlocks.vendorSpawner
					.getDefaultState().withProperty(BlockVendorSpawner.VISIBLE, false);
			else if (rarityIndex > 7)
				mineral = ModBlocks.spectriteBlock.getDefaultState();
			else if (rarityIndex > 5)
				mineral = Blocks.diamond_block
					.getDefaultState();
			else if (rarityIndex > 3)
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
										worldIn.setBlockState(
											pos.offset(facing
											.getOpposite()),
											tower.wallBlock, 0);
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
				for (int y = 1; y < height - 6; y += 3) { // C

					if (!hasShop && rand.nextInt(difficulty) >= 1) {
						final BlockPos spawnerPos = new BlockPos(
							ix + 4 + minX, baseY + y, iz
								+ 4 + minZ);
						worldIn.setBlockState(spawnerPos,
							MazeTowers.BlockSpecialMobSpawner
								.getDefaultState());
						TileEntitySpecialMobSpawner spawner =
							(TileEntitySpecialMobSpawner) worldIn
							.getTileEntity(spawnerPos);
						spawner.setShouldSpawnAbove(true);
						spawner.getSpawnerBaseLogic()
							.setEntityName(tower
							.getEntityNameForSpawn(
							floor, tower.isUnderwater));
					}
				}

				if (difficulty >= 9) {
					final IBlockState wire = Blocks.redstone_wire
						.getDefaultState();
					final int randDir = rand.nextInt(4);
					worldIn
						.setBlockState(chestPos.up(4)
							.offset(randDir == 0 ? EnumFacing.NORTH
							: randDir == 1 ? EnumFacing.WEST
							: randDir == 2 ? EnumFacing.WEST
							: EnumFacing.EAST, 3), wire, 2);
				}

				for (int i = 0; i < 4; i++) {

					if (hasItemFrame[i]) {
						final BlockPos pos = new BlockPos(
							ix + frameXCoords[i], baseY
								+ height - 4, iz // C
								+ frameZCoords[i]);
						EntityItemFrame frame = new EntityItemFrame(
							worldIn, pos, EnumFacing
								.values()[i + 2]);
						try {
						frame.setDisplayedItem(tower
							.getRandomItemFrameStack(worldIn, floor));
						} catch (IllegalArgumentException e) {
							e = null;
						}
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
									ix + minX + fenceXCoords[i][f],
									baseY + height - 6 + y, // C
									iz + minZ + fenceZCoords[i][f]);
								stateMap[height - 6 + y][fenceZCoords[i][f]][fenceXCoords[i][f]] = state; // C
								worldIn.setBlockState(pos,
									state, 2);
							}
						}

						for (y = -7; y <= -3; y += 4) { // C
							for (int xz = 0; xz <= 8; xz += 2) {
								if (xz != 4) {
									pos = new BlockPos(
										ix + minX + fenceXCoords[i][xz],
										baseY + height + y,
										iz + minZ + fenceZCoords[i][xz]);
									worldIn.setBlockState(pos,
										tower.wallBlock_external, 2);
								}
							}
						}
					}
				}

				if (!hasShop)
					tower.initChest(worldIn, chestPos, path,
						dyeColorIndex);
				else {
					if (shopVendorProfessionId != 0) {
						try {
						TileEntityVendorSpawner te =
							(TileEntityVendorSpawner) worldIn.getTileEntity(chestPos.down());
						te.setSpecialProfessionId(shopVendorProfessionId);
						te.setDifficulty(diffIndex);
						worldIn.setTileEntity(chestPos.down(), te);
						} catch (Exception e) {
							e = null;
						}
					}
				}
			}
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
						.getDimension());
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

			final int ix = tower.chunkX << 4, iz = tower.chunkZ << 4,
			px = isXDir ? dirSign == 1 ? 0 : (maxXBridge - minXBridge) : 1,
			pz = isXDir ? 1 : dirSign == 1 ? 0 : (maxZBridge - minZBridge),
			sx = isXDir ? dirSign == 1 ? (maxXBridge - minXBridge) - 1 : 1 : 1,
			sz = isXDir ? 1 : dirSign == 1 ? (maxZBridge - minZBridge) - 1 : 1,
			diffIndex = Math.min(tower.getDifficulty(floor) + dyeColorIndex, 9),
			sideWallChance = rand.nextInt(16);
			int shortCoord = 0;
			final boolean isBarred = conn != null
				&& conn.path.mazeDepth > path.mazeDepth
				&& diffIndex > 3 ? rand.nextInt(Math.max(
				7 - diffIndex, 1)) == 0 : false, hasSensor = (!tower.isUnderground || floor != floors) && !isBarred
				&& rand.nextInt(Math.max(diffIndex - 2, 1)) != 0;
			final IBlockState air = tower.air, bars = Blocks.iron_bars
				.getDefaultState(), clock = !hasShop || isShopClockInverted ?
				ModBlocks.redstoneClockInverted.getDefaultState() :
				ModBlocks.redstoneClock.getDefaultState(),
				oppStairs = tower.stairsBlock[dir.getOpposite() == EnumFacing.EAST ? 2 :
					dir.getOpposite().getHorizontalIndex() + 1]
					.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
			IBlockState	sideWallL = tower.wallBlock_external, sideWallR = tower.wallBlock_external;
			if (sideWallChance >= 14) {
				switch (sideWallChance) {
					case 14:
						sideWallL = sideWallR = tower.stairsBlock[dir == EnumFacing.EAST ? 0 :
							dir.getHorizontalIndex() + 1];
						break;
					case 15:
						sideWallL = tower.stairsBlock[dir.getHorizontalIndex()];
						sideWallR = tower.stairsBlock[(dir.getHorizontalIndex() + 2) % 4];
						break;
					default:
				}
				if (rand.nextBoolean()) {
					sideWallL = sideWallL.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
					sideWallR = sideWallR.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
				}
			}

			baseYBridge = baseY;
			heightBridge = !hasSensor ? 5 : 7;

			stateMapBridge = new IBlockState[heightBridge][(maxZBridge - minZBridge) + 1][(maxXBridge - minXBridge) + 1];

			for (int y = 0; y <= heightBridge - 1; y++) {
				for (int z = 0; z <= maxZBridge - minZBridge; z++) {
					for (int x = 0; x <= maxXBridge - minXBridge; x++) {
						final BlockPos pos = new BlockPos(
							ix + x + minXBridge,
							baseYBridge + y
								- (!hasSensor ? 0 : 2), iz
								+ z + minZBridge);
						final boolean isMedian = hasSensor
							&& y == 1,
						isPistonPos = x == px
							&& z == pz,
						isSensorPos = x == sx
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
									clock, 2);
								stateMapBridge[y][z][x] = clock;
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
								IBlockState state = shortCoord != 1 ? shortCoord == 0 ? sideWallL :
									sideWallR : tower.wallBlock_external;
									
								worldIn.setBlockState(pos, state, 2);
								stateMapBridge[y][z][x] = state;
							}
						} else {
							if ((isXDir && z == 1) || (!isXDir && x == 1)) {
								final IBlockState state;
								if (!isBarred)
									state = y == 0 || (hasSensor && y == 2) ?
										!hasSensor && !isPistonPos && y == 2 ?
										tower.floorBlock : oppStairs : tower.ceilBlock;
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
						if (!isXDir)
							shortCoord++;
					}
					if (isXDir)
						shortCoord++;
					else
						shortCoord = 0;
				}
				if (isXDir)
					shortCoord = 0;
			}
		}

		private void buildBridgeC(World worldIn) {

			final int ix = tower.chunkX << 4, iz = tower.chunkZ << 4, lx = !isXDir ? (dirSignC == 1 && isConnDownward)
				|| (dirSignC == -1 && !isConnDownward) ? (maxXBridgeC - minXBridgeC) - 1
				: 1 : 1, lz = !isXDir ? 1 : (dirSignC == 1 && isConnDownward)
				|| (dirSignC == -1 && !isConnDownward) ? (maxZBridgeC - minZBridgeC) - 1
				: 1, fx = maxXBridgeC - minXBridgeC, fz = maxZBridgeC - minZBridgeC;
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
					final boolean isTunnelStartEnd = (tunnelCoord == 0 ||
						tunnelCoord == tunnelEndCoord),
						isTunnelEndBelowLadder = isTunnelStartEnd
						&& ((((isConnDownward && dirSignC == 1) ||
						(!isConnDownward && dirSignC == -1)) && tunnelCoord == 0) ||
						(((!isConnDownward && dirSignC == 1) ||
						(isConnDownward && dirSignC == -1)) &&
						tunnelCoord == tunnelEndCoord)),
						isTunnelEndAboveLadder = isTunnelStartEnd
						&& !isTunnelEndBelowLadder;
					minY = !connLadder && connYDiff != 0 ? (int) Math
						.floor(connYDiff * (tunnelCoord / connYDiff)) : 0;
					if (!connLadder
						&& ((isConnDownward && dirSignC == 1) ||
						(!isConnDownward && dirSignC == -1)))
						minY = connYDiff - minY;
					for (int y = 0; y < heightBridgeC; y++) {
						if (y >= minY) {
							boolean isCarpetAPos = false, isCarpetBPos = false;
							final boolean isCarpetPos = (!connLadder ? isCarpetAPos = y == minY + 1
								: ((isCarpetAPos = y == minY + 1) || (isCarpetBPos = y == heightBridgeC - 3))), isLadder = connLadder
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
								&& (!connLadder || (connLadder && ((isBelowLadder &&
								!isTunnelEndBelowLadder) || (isAboveLadder &&
								!isTunnelEndAboveLadder))))) {
								if (((isXDir && x == 1) || (!isXDir && z == 1))
									&& (!connLadder || isLadder || (isBelowLadder ||
									isAboveLadder))) {
									final boolean isConnPos = (isCarpetAPos && !isConnDownward)
										|| (isCarpetBPos && isConnDownward);
									worldIn.setBlockState(pos, !isCarpetPos ?
										air : isConnPos ? carpetA : carpetB, 2);
								} else {
									worldIn.setBlockState(
										pos, tower.wallBlock_external, 2);
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
							worldIn.setBlockState(
							pos, tower.wallBlock_external, 2);
							stateMapSupport[y - 1][z][x] = tower.wallBlock_external;
						}
					}
				}
			}
		}

		private IBlockState[][][] getStateMap(int width,
			int height, int depth, IBlockState mineral,
			boolean hasCornerLamps) {
			final EnumDyeColor[] dyeColors = tower.getDyeColors();
			final EnumDyeColor dyeColor = dyeColors[dyeColorIndex];
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
				.getColourBlockState(Blocks.stained_glass_pane, dyeColor),
				topGlass = tower.getColourBlockState(Blocks.stained_glass,
				dyeColor), topGlass2 = tower.getColourBlockState(Blocks.stained_glass,
				tower.dyeColors[Math.min(dyeColorIndex + 1, 2)]),
				topGlass3 = tower.getColourBlockState(Blocks.stained_glass,
				tower.dyeColors[Math.min(dyeColorIndex + 2, 2)]), beaconGlass2 = !hasBeacon
				|| tower.beaconGlassColors.length == 1 ? null
				: tower.getColourBlockState(Blocks.stained_glass,
				tower.beaconGlassColors[0]), beaconGlass3 = !hasBeacon
				|| tower.beaconGlassColors.length != 3 ? wire
				: tower.getColourBlockState(Blocks.stained_glass,
				tower.beaconGlassColors[2]), bookShelf = Blocks.bookshelf
				.getDefaultState(), tnt = Blocks.tnt
				.getDefaultState(), fence = tower.fenceBlock;
			IBlockState chest = tower.getChestBlock(floor,
				dyeColorIndex, false).getDefaultState(), flowerPot = Blocks.flower_pot
				.getDefaultState();
			final IBlockState[][][][] stateMaps = MTStateMaps
				.getStateMaps(facing, tower.wallBlock, tower.wallBlock_external,
				tower.floorBlock, tower.ceilBlock, fence,
				carpet, topGlass, topGlass2, topGlass3, beaconGlass2, beaconGlass3,
				window, mineral, dyeColorIndex, hasShop);
			final IBlockState[][][] stairsMap =
				stateMaps[MTStateMaps.EnumStateMap.MINI_TOWER_STAIRS.ordinal()];
			IBlockState[][][] topMap =
				stateMaps[MTStateMaps.EnumStateMap.MINI_TOWER_TOP_1.ordinal()];
			final IBlockState[][] baseMap =
				stateMaps[MTStateMaps.EnumStateMap.MINI_TOWER_BASE.ordinal()][0],
			ceilMap = stateMaps[MTStateMaps.EnumStateMap.MINI_TOWER_CEILING_1.ordinal() - 2]
				[!hasCornerLamps ? 0 : 1],
			roofWireMap = stateMaps[MTStateMaps.EnumStateMap.MINI_TOWER_ROOF_WIRE.ordinal()][0],
			roofMap = stateMaps[MTStateMaps.EnumStateMap.MINI_TOWER_ROOF.ordinal()][0];
			EnumFacing entranceDir;
			final int stairsYLimit = height - 6, difficulty = path.difficulty; // C
			int xCoord = 0, zCoord = 0, x = 0, y = 0, z = 0;
			int[][] topStairCoords = null;
			
			if (floors == 1)
				entranceDir = dir;
			else {
				entranceDir = MTStateMaps.getTopStairDir(
					stairsMap[(stairsYLimit - 1) % 12]).getOpposite();
			}
			if (hasShop) {
				final EnumFacing shopDir = entranceDir.getOpposite();
				topMap = MTUtils.getRotatedStateMap(topMap, EnumFacing.SOUTH,
					shopDir, true);
				topMap[0][4][4] = topMap[0][4][4].withProperty(BlockVendorSpawner
					.FACING, shopDir);
				xCoord = 4 + (shopDir == EnumFacing.WEST ? -1
					: shopDir == EnumFacing.EAST ? 1 : 0);
				zCoord = 4 + (shopDir == EnumFacing.NORTH ? -1
					: shopDir == EnumFacing.SOUTH ? 1 : 0);
				ceilMap[zCoord][xCoord] = tower.fenceBlock;
				roofWireMap[4][4] = tower.ceilBlock;
				roofWireMap[zCoord][xCoord] = !tower.isNetherTower ?
					ModBlocks.memoryPiston.getDefaultState().withProperty(
					BlockMemoryPistonBase.FACING, EnumFacing.DOWN) : air;
				roofWireMap[4 + (shopDir.getAxis() == Axis.X ?
					isReverse ? -3 : 3 : 0)][4 + (shopDir.getAxis() == Axis.Z ?
					isReverse ? -3 : 3 : 0)] = !tower.isNetherTower ?
					this.isLocksmithRequired || rand.nextInt(10) != 0 ?
					ModBlocks.redstoneClockInverted.getDefaultState() :
					ModBlocks.redstoneClock.getDefaultState() : tower.wallBlock_external;
			}

			for (; y < height; y++) {
				if (y != 0 && y < stairsYLimit) {
					stateMap[y] = stairsMap[(y - 1)
						% stairsMap.length];
					if (y == stairsYLimit - 1) { // C
						if (!hasBeacon) {
							if (entranceDir == dir)
								entranceDir = MTStateMaps
									.getTopStairDir(stairsMap[y % 12]).getOpposite();
							chest = chest.withProperty(
								BlockChest.FACING, entranceDir);
							xCoord = 4 + (entranceDir == EnumFacing.WEST ? -1
								: entranceDir == EnumFacing.EAST ? 1 : 0);
							zCoord = 4 + (entranceDir == EnumFacing.NORTH ? -1
								: entranceDir == EnumFacing.SOUTH ? 1 : 0);
							ceilMap[zCoord][xCoord] = tower.ceilBlock;
							roofWireMap[zCoord][xCoord] = wire;
						} else
							chest = Blocks.beacon.getDefaultState();
						if (floors != 1)
							topStairCoords = MTStateMaps
								.getTopStaircaseCoords(stateMap[y]);
					}
				} else if (y != 0 && y < height - 3) { // C
					for (int i = 0; i < 4; i++) {
						for (int s = 0; s < 3; s++) {
							if (hasBookShelf[i][s]) {
								topMap[y - stairsYLimit][shelfZCoords[i]
									+ (i < 2 ? 0 : s)][shelfXCoords[i]
									+ (i < 2 ? s : 0)] = y != stairsYLimit + 2 ? bookShelf
									: !hasFlowerPot[i][s] ? air
									: flowerPot.withProperty(
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

					if (y == stairsYLimit + 1 && !hasShop) {
						if (y == 2) {
							entranceDir =  dir;
							chest = !hasBeacon ? chest
								.withProperty(BlockChest.FACING, dir.getOpposite()) :
								Blocks.beacon.getDefaultState();
						}
						stateMap[y][4][4] = chest;
					}
				} else if (y == 0)
					stateMap[y] = baseMap;
				else if (y == height - 3) { // C
					stateMap[y] = ceilMap;
					if (floors == 1) {
						xCoord = 4 + (entranceDir == EnumFacing.WEST ? -1
							: entranceDir == EnumFacing.EAST ? 1
								: 0);
						zCoord = 4 + (entranceDir == EnumFacing.NORTH ? -1
							: entranceDir == EnumFacing.SOUTH ? 1
								: 0);
						stateMap[y][zCoord][xCoord] = tower.ceilBlock;
					}
				} else if (y == height - 2) { // C
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
										tntX = t % 2 == 0 ? 2 : 6;
										tntZ = ((int) Math
											.floor(t / 2) == 0) ? 2 : 6;
									} else {
										tntX = 2 + ((rand
											.nextInt(3)) * 2);
										tntZ = tntX != 4 ? 2 + (rand
											.nextBoolean() ? 0 : 4) : 4;
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
				} else
					stateMap[y] = roofMap;
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
		
		public void setHasShop(boolean hasShop) {
			this.hasShop = hasShop;
			if (!hasShop)
				this.shopVendorProfessionId = 0;
			else {
				if (!isLocksmithRequired) {
    				int professionChance = rand.nextInt(16);
    				this.shopVendorProfessionId = professionChance < 14 ?
    					0 : professionChance == 14 ? 1 : 3;
				} else
					this.shopVendorProfessionId = 3;
				isShopClockInverted = rand.nextInt(10) != 0;
			}
		}
		
		public void setIsLocksmithRequired(boolean isLocksmithRequired) {
			this.isLocksmithRequired = isLocksmithRequired;
			if (isLocksmithRequired && !hasShop)
				setHasShop(true);
		}
		
		public void setShopVendorProfessionId(int shopVendorProfessionId) {
			this.shopVendorProfessionId = shopVendorProfessionId;
		}
	}

	private static class Path {

		protected final MazeTower tower;
		protected final EnumFacing dir;
		protected final BlockPos pos;
		protected final boolean isFloorEntrance;
		protected final boolean isTowerEntrance;
		protected final int ix;
		protected final int iy;
		protected final int iz;
		protected final int fx;
		protected final int fy;
		protected final int fz;
		protected final int floor;
		protected final int dirIndex;
		protected final int dirSign;
		protected final int pathIndex;
		protected final int difficulty;
		protected final int rarity;
		protected boolean hasMtp;
		protected boolean isDeadEnd;
		protected int dirAxis;
		protected int maxDistance;
		protected int distance;
		protected int depth;
		protected int mazeDepth;
		protected Path parent;
		protected MTPuzzle mtp = null;
		protected ArrayList<Path> children;
		protected Stack<EnumFacing> reservedDirs;

		private Path(MazeTower tower, Path parentPath,
			int ix, int iy, int iz, EnumFacing facing) {
			this.tower = tower;
			this.parent = parentPath;
			floor = (int) Math.floor(iy / 6) + 1;
			isTowerEntrance = parentPath == null;
			isFloorEntrance = isTowerEntrance
				|| parentPath.floor == floor - 1;
			isDeadEnd = !isFloorEntrance;
			depth = !isTowerEntrance ? parent.depth + 1 : 1;
			mazeDepth = !isTowerEntrance ? parent.mazeDepth
				: 1;
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
				dirAxis = (int) Math.floor(dirIndex >> 1);
				dirSign = dirIndex % 2 == 0 ? -1 : 1;
				distance = !isTowerEntrance ? Math.max(((rand
					.nextInt(maxDistance) + 1) >> 1) << 1, !hasMtp ? 2 : 4) : 3;
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
					MTPuzzle newMtp = getMtp();
					if (newMtp != null) {
						mtp = newMtp;
						mazeDepth += newMtp
							.getMazeDepthValue();
					}
					hasMtp = false;
				} else if (!isFloorEntrance
					&& rand.nextInt(7) < 2)
					this.hasMtp = true;
				for (int y = 1; y < 4; y++) {
					for (int d = 0; d <= distance; d++)
						setPathWithOffset(d, y);
				}

				final int dist = rand.nextInt(distance);
				final boolean isWindowWall = dist > 2,
				isWindowPosClose = isWindowWall || rand.nextBoolean();
				final int windowX = ix + (dir.getAxis() == Axis.X ? isWindowPosClose ? 1 : dist - 1 : 0),
				windowZ = iz + (dir.getAxis() == Axis.Z ? isWindowPosClose ? 1 : dist - 1 : 0);
				EnumFacing sideDir = rand.nextBoolean() ? dir.rotateY() : dir.rotateYCCW();
				Path checkPath = getPathWithOffset(tower,
					this, sideDir, distance >> 1, windowX,
					iy + 1, windowZ);
				IBlockState checkState = getStateAt(tower,
					windowX, iy + 1, windowZ);
				Block checkBlock;
				final boolean isXAxis = dir.getAxis() == Axis.X;
				
				if (checkPath != null
					&& ((checkPath.depth > depth + 2 && (checkState != null && checkState
						.getBlock() != Blocks.ladder)) || ((checkPath = getPathWithOffset(
						tower, this, (sideDir = sideDir
							.getOpposite()), distance >> 1,
						windowX, iy + 1, windowZ)) != null
						&& (checkPath.depth > depth + 2
							&& (checkState = getStateAt(
								tower, windowX, iy + 1,
								windowZ)) != null
							&& (checkBlock = checkState
								.getBlock()) != Blocks.ladder &&
								!(checkBlock instanceof BlockItemScanner))))) {
					final int windowCount = !isWindowWall ? 1 : dist - 2;
					for (int wz = 0; wz < (isXAxis ? 0 : windowCount); wz++) {
    					for (int wx = 0; wx < (isXAxis ? windowCount : 0); wx++) {
    						BlockPos windowPos = new BlockPos(windowX + wx, iy, windowZ + wz).offset(sideDir);
    						setStateAt(windowPos.getX(), iy + 2, windowPos.getZ(),
    							Blocks.glass_pane.getDefaultState());
    					}
					}
				}

				EnumFacing miniTowerDir;
				MiniTower miniTower;
				boolean isMiniTowerValid = false;
				final int mirrorX = fx < 8 ? fx : 15 - fx, mirrorZ = fz < 8 ? fz
					: 15 - fz, addToMiniTowerChance = (int) Math
					.ceil(Math.sqrt(mirrorX * mirrorZ) * 0.5);
				if (rand.nextInt((21 + addToMiniTowerChance) - floor) == 0
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
					tower.floorDeadEndPaths[floor - 1].add(this);
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
					.getChestBlock(tower.floors, 0, false)
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
				mtp = new MTPKeyDoor(tower, parent,
					this, dir, distance, ix, iy + 1, iz);
				mtp.build();
			} else {
				tower.floorChestPos[floor] = new Stack<BlockPos>();
				tower.floorChestItems[floor] = new HashMap<ItemStack, Integer>();
				tower.floorDeadEndPaths[floor] = new Stack<Path>();
				tower.floorAlteredStates[floor] = new Stack<Tuple<int[], IBlockState>>();
			}
			if (tower.isUnderground) {
				final boolean rotatePiston = !isPosValid(tower,
					getRelCoordsWithOffset(dir, 1, fx, fy, fz)), switchRotation = rotatePiston
					&& (dir == EnumFacing.NORTH || dir == EnumFacing.EAST);
				final Path behindPath = getPathWithOffset(
					tower, this, dir.getOpposite(), 1, ix,
					iy, iz);
				EnumFacing pistonDir = null;
				boolean rotatePistonL = rotatePiston
					&& rand.nextBoolean();
				if (rotatePiston) {
					if (!isPosValid(
						tower,
						getRelCoordsWithOffset(
							pistonDir = ((rotatePistonL && !switchRotation) || (!rotatePistonL && switchRotation)) ? dir
								.rotateYCCW()
								: dir.rotateY(), 1, fx, fy, fz)))
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
				/*if (rotatePiston)
					setStateWithOffset(0, 2, 0,
						Blocks.redstone_block.getDefaultState(), true);*/
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
						rotatePistonL ? 1 : -1, tower.air, true);
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
					final int yCoord = y + ((floor - 1) * 6);
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
									yCoord,
									tower.exitZ + 1,
									(piston = MazeTowers.BlockMemoryPiston
										.getDefaultState())
										.withProperty(
											BlockMemoryPistonBase.FACING,
											EnumFacing.NORTH));
							} else if (y == 5) {
								setStateAt(tower.exitX, yCoord, tower.exitZ,
									(button = MazeTowers.BlockHiddenButton
										.getDefaultState())
										.withProperty(
											BlockHiddenButton.FACING,
											EnumFacing.NORTH));
							}
						} else {
							if (y == 4) {
								setStateAt(tower.exitX - 1,
									yCoord,
									tower.exitZ, Blocks.air
										.getDefaultState());
								setStateAt(tower.exitX, yCoord,
									tower.exitZ,
									tower.ceilBlock);
								setStateAt(
									tower.exitX + 1, yCoord, tower.exitZ,
									(piston = MazeTowers.BlockMemoryPiston
										.getDefaultState())
										.withProperty(
											BlockMemoryPistonBase.FACING,
											EnumFacing.WEST));
							} else if (y == 5)
								setStateAt(
									tower.exitX, yCoord, tower.exitZ,
									(button = MazeTowers.BlockHiddenButton
										.getDefaultState())
										.withProperty(
											BlockHiddenButton.FACING,
											EnumFacing.WEST));
						}
					} else
						setStateAt(tower.exitX, yCoord,
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
						else
							setStateAt(endEntranceCoords[2], endEntranceCoords[0] + 4,
								endEntranceCoords[1], air);
					}
				}
			}
			return !isLastFloor ? new Path(tower, this,
				fx, fy + 6, fz, null) : null;
		}

		protected void addFallTrapHole(int x, int z,
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

			if (isFatal) {
				if (!tower.isUnderground)
					addFloorMedianAccess(x, z, true);
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
					setStateAt(x, y - 1, z, fluid.getDefaultState());
					setStateAt(x, y - 2, z, fluid.getDefaultState());
					setStateAt(x, y - 3, z, fluid.getDefaultState());
					setStateAt(x, y - 4, z, fluid.getDefaultState());
				}
			}
		}

		protected void addFloorMedianAccess(int x, int z,
			boolean isFallTrap) {
			int floor = this.floor - (isFallTrap ? 1 : 0);
			int y = (floor * 6)
				- (!tower.isUnderground ? 1 : -11);
			addFloorMedianAccess(x, y, z, isFallTrap);
		}

		protected void addFloorMedianAccess(int x, int y,
			int z, boolean isFallTrap) {
			setStateAt(x, y, z, Blocks.pumpkin
				.getDefaultState());
		}

		protected EnumFacing getDir() {
			if (dir != null)
				return dir;
			else {
				EnumFacing facing = null;
				IBlockState[][][] data = tower.blockData;
				int prevdirAxis = parent != null ? parent.dirAxis : 0,
				minDist = !hasMtp ? 2 : 4;
				List<EnumFacing> dirs = getDirsList(false,
					prevdirAxis != 1, prevdirAxis != 2);
				if (parent != null) {
    				Iterator rdIterator = parent.reservedDirs
    					.iterator();
    				while (rdIterator.hasNext())
    					dirs.remove(rdIterator.next());
				}
				Iterator dirsIterator = dirs.iterator();
				while (dirsIterator.hasNext()
					&& ((facing = ((EnumFacing) dirsIterator
						.next())) == null || (maxDistance = getMaxDistance(
						facing, minDist, false)) < minDist));

				return facing;
			}
		}

		protected EnumFacing getMiniTowerDir() {
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

		protected static ArrayList<EnumFacing> getDirsList(
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

		protected EnumFacing getLadderDir() {
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

		protected int getMaxDistance(EnumFacing facing,
			int limit, boolean odd) {
			return getMaxDistance(tower, facing, limit,
				odd, ix, iy + 1, iz);
		}

		protected static int getMaxDistance(MazeTower tower,
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

		protected boolean isPosAccessible(int x, int y, int z) {
			boolean isAccessible = (getStateAt(tower,
				x + 1, y, z) != null
				|| getStateAt(tower, x - 1, y, z) != null
				|| getStateAt(tower, x, y, z + 1) != null || getStateAt(
				tower, x, y, z - 1) != null);
			return isAccessible;
		}

		protected IBlockState getStateWithOffset(
			MazeTower tower, EnumFacing facing, int dist) {
			return getStateWithOffset(tower, facing, dist,
				ix, iy, iz);
		}

		protected static IBlockState getStateWithOffset(
			MazeTower tower, EnumFacing facing, int dist,
			int[] coords) {

			return getStateAt(tower, coords);
		}

		protected static IBlockState getStateWithOffset(
			MazeTower tower, EnumFacing facing, int dist,
			int x, int y, int z) {
			int[] coords = getRelCoordsWithOffset(facing,
				dist, x, y, z);

			return getStateAt(tower, coords);
		}

		protected Path getPathWithOffset(EnumFacing facing,
			int dist) {
			return getPathWithOffset(tower, this, facing,
				dist, ix, iy, iz);
		}

		protected static Path getPathWithOffset(
			MazeTower tower, Path path, EnumFacing facing,
			int dist, int[] coords) {

			return getPathAt(tower, path, coords[2],
				coords[0], coords[1]);
		}

		protected static Path getPathWithOffset(
			MazeTower tower, Path path, EnumFacing facing,
			int dist, int x, int y, int z) {
			int[] coords = getRelCoordsWithOffset(facing,
				dist, x, y, z);

			return getPathAt(tower, path, coords[2],
				coords[0], coords[1]);
		}

		protected static Path getPathAt(MazeTower tower,
			Path path, int[] coords) {
			return getPathAt(tower, path, coords[2],
				coords[0], coords[1]);
		}

		protected static Path getPathAt(MazeTower tower,
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

		protected static IBlockState getStateAt(
			MazeTower tower, int[] coords) {
			return getStateAt(tower, coords[2], coords[0],
				coords[1]);
		}

		protected static IBlockState getStateAt(
			MazeTower tower, int x, int y, int z) {
			IBlockState state = null;

			if (isPosValid(tower, x, y, z))
				state = tower.blockData[y][z][x];

			return state;
		}

		protected int[] getRelCoordsWithOffset(
			EnumFacing facing, int dist) {
			return getRelCoordsWithOffset(facing, dist, ix,
				iy, iz);
		}

		protected static int[] getRelCoordsWithOffset(
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
		
		public int getDepth() {
			return depth;
		}

		public int getMazeDepth() {
			return mazeDepth;
		}

		protected static boolean isPosValid(MazeTower tower,
			int[] coords) {
			return isPosValid(tower, coords[2], coords[0],
				coords[1]);
		}

		protected static boolean isPosValid(MazeTower tower,
			int x, int y, int z) {
			int floor = (int) Math.floor(y / 6) + 1;
			return ((x > 0 && x < 15) && (z > 0 && z < 15)
				&& (y >= 0 && y < floor * 6) && (floor == 1
				|| x != tower.exitX || z != tower.exitZ));
		}

		protected void setStateWithOffset(int dist, int addY,
			int sideOffset, IBlockState state) {
			setStateWithOffset(dist, addY, sideOffset,
				state, false);
		}

		protected void setStateWithOffset(int dist, int addY,
			int sideOffset, IBlockState state,
			boolean fromEnd) {
			final int x = (!fromEnd ? ix : fx)
				+ (dirAxis != 2 ? dirAxis == 0 ? 0
				: sideOffset : dist * dirSign),
			y = (!fromEnd ? iy : fy) + addY
				+ (dirAxis != 0 ? 0 : dist),
			z = (!fromEnd ? iz : fz)
				+ (dirAxis != 1 ? dirAxis == 0 ? 0
				: sideOffset : dist * dirSign),
			coords[] = new int[] { y, z, x };
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
				|| state.getBlock() == Blocks.ladder) {
				tower.blockData[y][z][x] = state;
				tower.floorAlteredStates[floor - 1].push(new Tuple(coords, curState));
			}
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
						x, y - addY, z, null);
					if (newPath != null
						&& newPath.dir != null)
						children.add(newPath);
				}
				// } else {

				// }
			}
		}

		protected void setStateAt(int x, int y, int z,
			IBlockState state) {
			IBlockState curState = tower.blockData[y][z][x];
			Block curBlock;
			int[] coords = new int[] { y, z, x };
			/*
			 * if (curState != null && curState.getBlock() ==
			 * Blocks.redstone_block) return;
			 */
			if (curState == null
				|| ((curBlock = curState.getBlock()) != Blocks.lever
					&& curBlock != MazeTowers.BlockHiddenButton
					&& curBlock != Blocks.ladder
					&& curBlock != Blocks.redstone_wire
					&& curBlock != Blocks.wall_sign && curBlock != Blocks.glass_pane)) {
				tower.floorAlteredStates[floor - 1].push(new Tuple(coords, curState));
				tower.blockData[y][z][x] = state;
			}
		}

		protected void setPathWithOffset(int dist, int addY) {
			IBlockState torch = !tower.isUnderwater ? Blocks.torch
				.getDefaultState().withProperty(
					BlockTorch.FACING, dir.rotateYCCW())
				: Blocks.sea_lantern.getDefaultState();
			// for (int s = -1; s < 2; s++)
			if (!isTowerEntrance
				|| (dist != 0 && dist != 2) || addY != 3) {
				if (!tower.isUnderwater || addY != 2
					|| (dist != 1 && dist != distance - 1)) {
					setStateWithOffset(dist, addY, 0,
						(dist != 1 && dist != distance - 1)
						|| addY != 2 || (rand.nextInt(difficulty + 1) != 0) ?
						tower.air : torch);
				} else if (!tower.isUnderwater) {
					if (rand.nextInt(4) == 3)
						setStateWithOffset(dist, addY + 2,
							0, torch);
				} else
					setStateWithOffset(dist, addY, 0,
						tower.air);

			}
		}

		protected MTPuzzle getMtp() {
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

		protected MTPuzzle getRandomMtp(MazeTower tower,
			Path fromPath, Path toPath, EnumFacing dir,
			int distance, int x, int y, int z, int failCount) {
			MTPuzzle mtp = null;
			EnumFacing pathDir;
			int mtpChance = rand.nextInt(7);
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
				if (difficulty > 3) {
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
							.getBlock() instanceof BlockItemScanner) ||
							getStateWithOffset(tower, isLeft ? dir
							.rotateYCCW() : dir.rotateY(), 2, coords) != null)) {
						if (!switchDir) {
							isLeft = !isLeft;
							switchDir = true;
						} else
							useMtp = false;
					}
					if (useMtp)
						mtp = new MTPItem(tower, fromPath,
							this, dir, distance, x, y, z,
							isLeft);
				}
				break;
			case 6:
				mtp = new MTPDoor(tower, fromPath, this, dir,
					distance, x, y, z);
				break;
			case 7:
				mtp = new MTPPiston(tower, fromPath, this,
					dir, distance, x, y, z);
				break;
			default:

			}
			return (mtp != null && mtp.dir != null)
				|| failCount++ >= 5 ? mtp : getRandomMtp(
				tower, fromPath, toPath, dir, distance, x,
				y, z, failCount);
		}

		protected EnumFacing getFreeSpaceDir(int x, int y,
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
						|| path.mtp != null || path.isFloorEntrance || path.depth >= depth - 2))) ||
						!isPosValid(tower, x + (facing.getAxis() == Axis.X ? facing
						.getAxisDirection() == AxisDirection.NEGATIVE ? -2 : 2 : 0),
						y, z + (facing.getAxis() == Axis.Z ? facing
						.getAxisDirection() == AxisDirection.NEGATIVE ? -2 : 2 : 0))));
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
			IBlockState door = doorStates[dir.getIndex() % 4];
			final IBlockState stone = Blocks.stone.getDefaultState(),
			lever = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING,
				(toPath.getDir().getAxis() == Axis.Z ? EnumOrientation.DOWN_Z
				: EnumOrientation.DOWN_X)),
			redstone = Blocks.redstone_wire.getDefaultState();
			if (door.getValue(BlockDoor.FACING).rotateY() == toPath.parent.dir)
				door = door.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
			/*if ((((subDir.getIndex() - 2) - (dirIndex - 2)) % 4) == 2) {
				setXOffset(-2);
				setZOffset(-2);
			}
			if (dir == EnumFacing.NORTH) {
				setXOffset(2);
				setZOffset(2);
			}*/
			stateMap = new IBlockState[][][] {
				{
					{ null, null, null, null, null },
					{ null, null, null, null, null },
					{ null, null, door, null, null },
					{ null, null, null, null, null },
					{ null, null, null, null, null } },
				{
					{ null, null, null, null, null },
					{ null, null, null, null, null },
					{
						null, null,
						doorStates[(dir.getOpposite().getIndex() % 2) == -1 ? 4 : 5],
						null, null },
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
			boolean canAccessBack = true;
			IBlockState stone = Blocks.stone
				.getDefaultState();
			IBlockState piston = MazeTowers.BlockMemoryPiston
				.getDefaultState().withProperty(
					BlockDirectional.FACING, dir.rotateY());
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
					BlockDirectional.FACING,
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
			validateStateMap(stateMap, false);
		}
	}

	private static class MTPArrow extends MTPuzzle {
		private MTPArrow(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, int distance,
			int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = -1, zOffset = 1, 1);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			boolean isLeft = rand.nextBoolean();
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
					BlockDirectional.FACING, EnumFacing.DOWN), repeaterA = Blocks.unpowered_repeater
				.getDefaultState().withProperty(
					BlockHorizontal.FACING,
					dir.getOpposite()).withProperty(
					BlockRedstoneRepeater.DELAY, delay), repeaterB = Blocks.unpowered_repeater
				.getDefaultState().withProperty(
					BlockHorizontal.FACING, dir)
				.withProperty(
					BlockRedstoneRepeater.DELAY, 3), repeaterC = Blocks.unpowered_repeater
				.getDefaultState().withProperty(
					BlockHorizontal.FACING,
					dirAxis == 2 ? dir.rotateYCCW() : dir
						.rotateY()).withProperty(
					BlockRedstoneRepeater.DELAY, 3);
			dirSign = dirIndex % 2 == 0 ? -1 : 1;
			// if (dir != EnumFacing.EAST && dir != EnumFacing.NORTH)
			// dirSign *= -1;
			stateMap = new IBlockState[5][3][toPath.distance + 1];
			stateMap[0][0][0] = doorStates[dir.getIndex() % 4];
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
			IBlockState pistonA = ((toPath.difficulty >= 4) ? MazeTowers.BlockMemoryPiston :
				MazeTowers.BlockMemoryPistonOff).getDefaultState()
				.withProperty(BlockMemoryPistonBase.FACING, (isLeft ? dir.rotateYCCW() : dir.rotateY()));
			IBlockState pistonB = ((toPath.difficulty >= 4 || toPath.difficulty < 2) ? MazeTowers.BlockMemoryPistonOff
				: MazeTowers.BlockMemoryPiston).getDefaultState().withProperty(
				BlockMemoryPistonBase.FACING, (isLeft ? dir.rotateY() : dir.rotateYCCW()));
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
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
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
					{ { (!tower.isUnderwater &&
						rand.nextInt(10) < toPath.difficulty) ? web
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
			IBlockState scanner = (toPath.difficulty < 5
				|| rand.nextInt((toPath.difficulty) - 3) == 0 ?
				MazeTowers.BlockItemScanner
				: MazeTowers.BlockItemScannerGold)
				.getDefaultState().withProperty(
					BlockItemScanner.FACING,
					dir.getOpposite());
			init(scanner, isLeft);
		}
		
		private MTPItem(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, int distance,
			int x, int y, int z, IBlockState scanner) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = -1, zOffset = 0, 3);
			boolean isLeft = rand.nextBoolean();
			int offsetX, offsetZ;
			int[] offsets;
			offsetX = -1;
			offsetZ = -1;
			offsets = getRotatedOffsets(
				offsetX, offsetZ, EnumFacing.SOUTH, dir);
			int[] coords = new int[] { y,
				z + offsets[1], x + offsets[0] };
			IBlockState curState = Path.getStateAt(
				tower, coords);
			if ((curState != null && curState
				.getBlock() instanceof BlockItemScanner) ||
				Path.getStateWithOffset(tower, isLeft ? dir
				.rotateYCCW() : dir.rotateY(), 2, coords) != null)
				isLeft = !isLeft;
			init(scanner, isLeft);
		}
		
		protected void init(IBlockState scanner, boolean isLeft) {
			IBlockState stone = Blocks.stone
				.getDefaultState();
			IBlockState sign = Blocks.wall_sign
				.getDefaultState()
				.withProperty(BlockWallSign.FACING,
					dir.getOpposite());
			IBlockState button = MazeTowers.BlockHiddenButton
				.getDefaultState().withProperty(
					BlockDirectional.FACING, dir);
			stateMap = new IBlockState[][][] {
				{
					{ null, null, null },
					{
						null, doorStates[dir.getIndex() % 4], null },
					{ null, null, null } },
				{
					{ isLeft ? stone : scanner, null,
						isLeft ? scanner : stone },
					{
						null,
						doorStates[(dir.getIndex() % 2) == -1 ? 4 : 5],
						null }, { null, null, null } },
				{ { null, sign, null },
					{ null, stone, null },
					{ null, button, null } } };
			stateMap = getRotatedStateMap(stateMap,
				EnumFacing.SOUTH, dir, false);
			validateStateMap(stateMap, false);
		}
	}
	
	private static class MTPDoor extends MTPuzzle {

		private MTPDoor(MazeTower tower,
			Path fromPath, Path toPath, EnumFacing dir,
			int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, xOffset = 0, zOffset = rand.nextInt(toPath.distance - 1) + 1, 1);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			final IBlockState pressurePlate = MazeTowers.BlockHiddenPressurePlateWeighted
				.getDefaultState(),
			stone = Blocks.stone.getDefaultState();
			stateMap = getRotatedStateMap(
				new IBlockState[][][] {
					{
						{ pressurePlate },
						{ doorStates[dir.getOpposite().getIndex() % 4] },
						{ pressurePlate }
					},
					{
						{ null },
						{ doorStates[(dir.getIndex() % 2) == -1 ? 4 : 5] },
						{ null }
					},
					{
						{ stone },
						{ null },
						{ null }
					}
				}, EnumFacing.SOUTH, dir, false);
			validateStateMap(stateMap, false);
		}
	}
	
	private static class MTPKeyDoor extends MTPuzzle {
		
		private static final IBlockState[] doorStatesIron = new IBlockState[] {
			Blocks.iron_door.getDefaultState().withProperty(
				BlockDoor.FACING, EnumFacing.EAST),
			Blocks.iron_door.getDefaultState().withProperty(
				BlockDoor.FACING, EnumFacing.WEST),
			Blocks.iron_door.getDefaultState().withProperty(
				BlockDoor.FACING, EnumFacing.SOUTH),
			Blocks.iron_door.getDefaultState().withProperty(
				BlockDoor.FACING, EnumFacing.NORTH),
			Blocks.iron_door.getDefaultState()
				.withProperty(BlockDoor.HINGE,
					EnumHingePosition.LEFT)
				.withProperty(BlockDoor.HALF,
					BlockDoor.EnumDoorHalf.UPPER),
			Blocks.iron_door.getDefaultState().withProperty(
				BlockDoor.HINGE,
				EnumHingePosition.RIGHT).withProperty(
				BlockDoor.HALF,
				BlockDoor.EnumDoorHalf.UPPER) };
		
		private MTPKeyDoor(MazeTower tower, Path fromPath,
			Path toPath, EnumFacing dir, int distance,
			int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance,
				x, y, z, 0, 0, 1);
			final boolean useIronDoor =
				!(this.doorStates[0].getBlock() instanceof BlockExtraDoor);
			final IBlockState lock = ModBlocks.lock.getDefaultState()
				.withProperty(BlockLock.FACING, dir.getOpposite()),
			stone = Blocks.stone.getDefaultState(),
			lever = leverStates[dir.getOpposite().getIndex() - 2];
			stateMap = new IBlockState[][][] {
				{
					{ lever },
					{ useIronDoor ? doorStatesIron[dir.getOpposite().getIndex() % 4] :
						doorStates[dir.getOpposite().getIndex() % 4] },
					{ null }
				}, {
					{ lock },
					{ useIronDoor ? doorStatesIron[(dir.getIndex() % 2) == -1 ? 4 : 5] :
						doorStates[(dir.getIndex() % 2) == -1 ? 4 : 5] },
					{ null }
				}, {
					{ null },
					{ stone },
					{ null }
				}
			};
			stateMap = getRotatedStateMap(stateMap,
				EnumFacing.SOUTH, dir, false);
			validateStateMap(stateMap, false);
		}
	}

	private static abstract class MTPuzzle {
		private final MazeTower tower;
		private final Path fromPath;
		private final Path toPath;
		private final int x;
		private final int y;
		private final int z;
		private final int mazeDepthValue;
		protected final int dirIndex;
		protected final int dirAxis;
		protected static final IBlockState[] leverStates = new IBlockState[] {
			Blocks.lever.getDefaultState().withProperty(
				BlockLever.FACING,
				BlockLever.EnumOrientation.EAST),
			Blocks.lever.getDefaultState().withProperty(
				BlockLever.FACING,
				BlockLever.EnumOrientation.WEST),
			Blocks.lever.getDefaultState().withProperty(
				BlockLever.FACING,
				BlockLever.EnumOrientation.SOUTH),
			Blocks.lever.getDefaultState().withProperty(
				BlockLever.FACING,
				BlockLever.EnumOrientation.NORTH),
			Blocks.lever.getDefaultState().withProperty(
				BlockLever.FACING,
				BlockLever.EnumOrientation.UP_X) };
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
			doorStates = new IBlockState[] {
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.EAST),
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.WEST),
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.SOUTH),
				door.getDefaultState().withProperty(
					BlockDoor.FACING, EnumFacing.NORTH),
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
			MTPuzzle.xOffset = xOffset;
			MTPuzzle.zOffset = zOffset;
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
			MTPuzzle.xOffset = xOffset;
		}

		protected void setZOffset(int zOffset) {
			MTPuzzle.zOffset = zOffset;
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
									if (Path.isPosValid( tower, xCoord, yCoord, zCoord))
										tower.dataMap[yCoord][zCoord][xCoord] = (randInt != -1) ? randInt
											: (randInt = (rand.nextInt(9) + 1 + (Math
											.max(toPath.difficulty - 3, 0))));
								} else if (this instanceof MTPWindow && block == Blocks.glass) {
									Path leverPath;
									int[] leverPathCoords = Path
										.getRelCoordsWithOffset(
											((MTPWindow) this).subDir,
											1, xCoord,
											yCoord - 2,
											zCoord);
									leverPath = Path
										.getPathWithOffset(
											tower,
											toPath,
											((MTPWindow) this).subDir,
											1, xCoord,
											yCoord - 2,
											zCoord);
									if (leverPath != null) {
										if (Path
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
										if (Path
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
										if (Path
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
										if (Path
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
									if (Path.getStateAt(
										tower, xCoord,
										yCoord, zCoord) == tower.air)
										state = Blocks.glowstone
											.getDefaultState();
								} else if ((this instanceof MTPFallTrap || this instanceof MTPBounceTrap)
									&& block == MazeTowers.BlockMemoryPiston) {
									if (tower.isUnderground
										&& Path.isPosValid(tower, new int[] {
										yCoord + 12, zCoord, xCoord })) {
										yCoord += 12;
									}
								} else if (this instanceof MTPFallTrap
									&& block == MazeTowers.BlockHiddenPressurePlateWeighted) {
									boolean isFatal = ((MTPFallTrap) this).isFatal;
									if ((isFatal && y2 == 1) || (!isFatal && y2 == 3))
										fromPath.addFallTrapHole(xCoord, zCoord, isFatal);
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
				if (!Path.isPosValid(tower, (maxCoords = new int[] {
					y + (map.length - 1),
					z + ((map[0].length - 1) * dirSign)
					+ zOffset, x + ((map[0][0].length - 1) * dirSign)
					+ xOffset })) || !Path.isPosValid(tower,
					(minCoords = new int[] { y,
					z + zOffset, x + xOffset }))
					|| (checkTopSpace && (Path.getStateAt(
					tower, minCoords[2], maxCoords[0],
					minCoords[1]) != tower.air ||
					Path.getStateAt(tower, maxCoords[2], maxCoords[0],
					minCoords[1]) != tower.air || Path.getStateAt(tower,
					minCoords[2], maxCoords[0], maxCoords[1]) != tower.air ||
					Path.getStateAt(tower, maxCoords) != tower.air)))
					setDir(null);
			} catch (ArrayIndexOutOfBoundsException e) {
				e = null;
			}
		}
	}
}