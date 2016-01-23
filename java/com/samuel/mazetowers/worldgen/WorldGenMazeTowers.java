package com.samuel.mazetowers.worldgen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockHiddenButton;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBase;
import com.samuel.mazetowers.etc.MazeTowersData;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModChestGen;
import com.samuel.mazetowers.tileentities.TileEntityCircuitBreaker;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityWebSpiderSpawner;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Tuple;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenMazeTowers implements IWorldGenerator {

	private Map<Integer, Map<Integer, IBlockState>> chunkData = null;
	private Multimap<Integer, ArrayDeque<Integer>> chunkChest = null;
	private List<MazeTower> towers;
	private int genCount = 128;
	private int chunksGenerated = 0;
	private int spawnPosLoadedCount = 0;
	private int[] chunkDistanceX = new int[genCount];
	private int[] blockDistanceY = new int[genCount];
	private int[] chunkDistanceZ = new int[genCount];
	private World curWorld;
	public BlockPos[] spawnPos = new BlockPos[genCount];
	private boolean[] spawnPosLoaded = new boolean[genCount];
	private boolean[] generated = new boolean[genCount];
	private Map<BlockPos, IBlockState>[] data = new Map[genCount];
	private Map<ChunkCoordIntPair, ChunkCoordIntPair> chunkGroupTowerCoords;
	private MazeTowersData MazeTowerData;
	private ChestGenHooks[] chestInfo;
	private static final Random rand = new Random();
	private static final Comparator<Entry<BlockPos, Tuple>> scannerPosComparator =
		new Comparator<Entry<BlockPos, Tuple>>() {
		
        @Override
        public int compare(Entry<BlockPos, Tuple> e1, Entry<BlockPos, Tuple> e2) {
            Integer v1 = (Integer) e1.getValue().getSecond();
            Integer v2 = (Integer) e2.getValue().getSecond();
            return v1.compareTo(v2);
        }
    };
    private static final  Comparator<Entry<ItemStack, Integer>> chestItemsComparator =
		new Comparator<Entry<ItemStack, Integer>>() {
        
        @Override
        public int compare(Entry<ItemStack, Integer> e1, Entry<ItemStack, Integer> e2) {
            Integer v1 = e1.getValue();
            Integer v2 = e2.getValue();
            return v1.compareTo(v2);
        }
    };

	public WorldGenMazeTowers() {
		chestInfo = new ChestGenHooks[11];
		for (int i = 1; i <= 11; i++)
			chestInfo[i - 1] = ChestGenHooks.getInfo("MazeTowerChest" + i);
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimensionId() != 0
				|| world.provider.getDimensionId() == 0)
			return;
		loadOrCreateData(world);
		for (int g = 0; g < genCount; g++) {
			if (world.isRemote || !MazeTowers.enableMazeTowers)
				return;

			if (!generated[g]) {
				if (!spawnPosLoaded[g]
						&& (chunkDistanceX[g] == 0 || blockDistanceY[g] <= 0 || chunkDistanceZ[g] == 0)) {
					int failCount = 0;
					MazeTowerData.setIsGenerated(false, g);
					findNewSpawnPos(world, random, 0, g);
					BlockPos oppPos = new BlockPos(
							this.spawnPos[g].getX() + 31,
							this.spawnPos[g].getY() + 45,
							this.spawnPos[g].getZ() + 31);
					while ((world.getBiomeGenForCoords(this.spawnPos[g]).biomeName == "Ocean"
							|| world.getBiomeGenForCoords(this.spawnPos[g]).biomeName == "Deep Ocean"
							|| world.getBiomeGenForCoords(this.spawnPos[g]).biomeName == "Beach"
							|| world.getBiomeGenForCoords(this.spawnPos[g]).biomeName == "Mushroom Island Shore"
							|| world.getBiomeGenForCoords(this.spawnPos[g]).biomeName == "River"
							|| world.getBiomeGenForCoords(oppPos).biomeName == "Ocean"
							|| world.getBiomeGenForCoords(oppPos).biomeName == "Deep Ocean"
							|| world.getBiomeGenForCoords(oppPos).biomeName == "Beach"
							|| world.getBiomeGenForCoords(oppPos).biomeName == "Mushroom Island Shore" || world
							.getBiomeGenForCoords(oppPos).biomeName == "River")
							&& failCount < 200) {
						failCount++;
						findNewSpawnPos(world, random, failCount, g);
						oppPos = new BlockPos(this.spawnPos[g].getX() + 31,
								this.spawnPos[g].getY() + 45,
								this.spawnPos[g].getZ() + 31);
					}
					// ChaosBlock.network.sendToAll(new
					// PacketSyncCLCompassPos(String.valueOf(this.compassPos.toLong())));
				}
			}

			int spawnPosChunkX = spawnPos[g].getX() >> 4;
			int spawnPosChunkZ = spawnPos[g].getZ() >> 4;

			data[g] = new HashMap<BlockPos, IBlockState>();

			if ((chunkX >= spawnPosChunkX - 4 && chunkX <= spawnPosChunkX + 3)
					&& (chunkZ >= spawnPosChunkZ - 4 && chunkZ <= spawnPosChunkZ + 3)) {

				int distance = (int) ((chunkX + chunkZ) * 0.5);

				if ((chunkX == spawnPos[g].getX() >> 4 || chunkX - 1 == spawnPos[g]
						.getX() >> 4)
						&& (chunkZ == spawnPos[g].getZ() >> 4 || chunkZ - 1 == spawnPos[g]
								.getZ() >> 4)
						&& world.provider.getDimensionId() == 0)
					generateSurface(world, random, chunkX * 16, chunkZ * 16,
							true, distance, g);
				else
					generateSurface(world, random, chunkX * 16, chunkZ * 16,
							false, distance, g);
			}
		}
	}

	private void findNewSpawnPos(World world, Random random, int failCount,
			int index) {
		if (index == 0) {
			chunkDistanceX[index] = (world.getSpawnPoint().getX() << 4) + 1;
			chunkDistanceZ[index] = (world.getSpawnPoint().getZ() << 4) + 1;
		} else {
			chunkDistanceX[index] = (int) (random.nextGaussian() * 25 + (failCount < 20 ? 15
					: 20))
					* ((Math.round(Math.random()) == 0) ? -1 : 1);
			chunkDistanceZ[index] = (int) (random.nextGaussian() * 12.5 + (failCount < 20 ? 7.5
					: 10))
					* ((Math.round(Math.random()) == 0) ? -1 : 1) * 2;
		}
		blockDistanceY[index] = 70;
		spawnPos[index] = new BlockPos(
			(((world.getSpawnPoint().getX() >> 4) + (chunkDistanceX[index] + 1)) * 16),
			blockDistanceY[index],
			(((world.getSpawnPoint().getZ() >> 4) + (chunkDistanceZ[index] + 1)) * 16));
		MazeTowerData.setSpawnPoint(spawnPos[index].getX(),
				spawnPos[index].getY(), spawnPos[index].getZ(), index);
	}

	private void generateSurface(World world, Random random, int x, int z,
			boolean isStructure, int distance, int index) {
		if (isStructure) {
			/*
			 * int chunkIndex = (z >> 4 == spawnPos.getZ() >> 4) ? 0 : 2; if (x
			 * >> 4 != spawnPos.getX() >> 4) chunkIndex++; this.spawn(world, x,
			 * spawnPos.getY(), z, chunkIndex);
			 */
		}
	}
	
	public int getGenCount() {
		return genCount;
	}
	
	public int getChunksGenerated() {
		return chunksGenerated;
	}
	
	public int getChunksGenerated(World worldIn) {
		loadOrCreateData(worldIn);
		return chunksGenerated;
	}
	
	public int getSpawnPosLoadedCount(World worldIn) {
		loadOrCreateData(worldIn);
		return spawnPosLoadedCount;
	}
	
	public boolean getIsValidChunkCoord(int chunkX, int chunkZ) {
		return chunkGroupTowerCoords.get(new ChunkCoordIntPair(chunkX >> 4, chunkZ >> 4))
			== null;
	}

	public boolean getGenerated(int index) {
		return generated[index];
	}

	public BlockPos getSpawnPos(int index) {
		return spawnPos[index];
	}
	
	public boolean getSpawnPosLoaded(int index) {
		return spawnPosLoaded[index];
	}

	public List<MazeTower> getTowers() {
		return towers;
	}
	
	public MazeTowersData getData() {
		return MazeTowerData;
	}
	
	public MazeTower getTowerAtCoords(int chunkX, int chunkZ) {
		for (MazeTower tower : towers) {
			if (tower.chunkX == chunkX && tower.chunkZ == chunkZ)
				return tower;
		}
		return null;
	}
	
	public void setSpawnPos(World worldIn, int chunkIndex, BlockPos pos) {
		loadOrCreateData(worldIn);
		spawnPos[chunkIndex] = pos;
		MazeTowerData.setSpawnPoint(pos.getX(), pos.getY(), pos.getZ(), chunkIndex);
		chunkGroupTowerCoords.put(new ChunkCoordIntPair(pos.getX() >> 8, pos.getZ() >> 8),
			new ChunkCoordIntPair(pos.getX() >> 4, pos.getZ() >> 4));
		spawnPosLoaded[chunkIndex] = true;
		spawnPosLoadedCount++;
	}
	
	public void setIsGenerated(World worldIn, int chunkIndex, boolean isGenerated) {
		loadOrCreateData(worldIn);
		generated[chunkIndex] = isGenerated;
		MazeTowerData.setIsGenerated(isGenerated, chunkIndex);
	}

	protected final void loadOrCreateData(World world) {
		boolean dataIsNull = this.MazeTowerData == null;
		if (dataIsNull || curWorld == null
			|| (world.getWorldInfo().getSeed() != 0 && curWorld
			.getWorldInfo().getSeed() != world.getWorldInfo()
			.getSeed())) {
			int g = 0;
			if (curWorld != world || dataIsNull)
				this.MazeTowerData = (MazeTowersData) world.loadItemData(
					MazeTowersData.class, "MazeTowers");
			curWorld = world;
			this.chunkGroupTowerCoords = new HashMap<ChunkCoordIntPair,
					ChunkCoordIntPair>();
			if (this.MazeTowerData == null) {
				this.MazeTowerData = new MazeTowersData("MazeTowers");
				world.setItemData("MazeTowers", MazeTowerData);
				for (; g < genCount; g++) {
					this.generated[g] = false;
					this.spawnPosLoaded[g] = false;
					this.chunkDistanceX[g] = 0;
					this.blockDistanceY[g] = 0;
					this.chunkDistanceZ[g] = 0;
					this.spawnPos[g] = new BlockPos(-1, -1, -1);
				}
				this.chunksGenerated = 0;
				this.spawnPosLoadedCount = 0;
			} else {
				for (; g < genCount; g++) {
					this.generated[g] = MazeTowerData.getIsGenerated(g);
					this.spawnPos[g] = MazeTowerData.getSpawnPoint(g);
					if (this.generated[g])
						chunksGenerated++;
					if (this.spawnPos[g] != null) {
						int chunkX = spawnPos[g].getX() >> 4;
						int chunkZ = spawnPos[g].getZ() >> 4;
						spawnPosLoaded[g] = true;
						chunkGroupTowerCoords.put(new ChunkCoordIntPair(chunkX >> 4,
							chunkZ >> 4), new ChunkCoordIntPair(chunkX, chunkZ));
					}
				}
			}
			this.towers = new ArrayList<MazeTower>();
		}
	}

	public void rebuild(World worldIn, int index) {
		loadOrCreateData(worldIn);
		((MazeTower) towers.get(index)).build(worldIn);
	}

	public void spawn(World world, int ix, int iy, int iz, int chunk) {
	}

	public boolean addTower(World worldIn, int x, int z, boolean build) {
		loadOrCreateData(worldIn);
		BiomeGenBase biome;
		String biomeName;
		String towerTypeName;
		BlockPos pos;
		Block doorBlock = Blocks.iron_door;
		Block trapDoorBlock = Blocks.iron_trapdoor;
		IBlockState ceilBlock = null, wallBlock = null,
			wallBlock_external = null, floorBlock = null;
		IBlockState[] stairsBlock = new IBlockState[4];
		final boolean isUnderwater;
		final boolean hasXEntrance = rand.nextBoolean();
		int px = (x << 4) + (hasXEntrance ? 0 : 8);
		int pz = (z << 4) + (hasXEntrance ? 8 : 0);
		int py;
		int difficulty;
		int dimId = worldIn.provider.getDimensionId();
		biome = worldIn.getBiomeGenForCoords(new BlockPos(px, 64, pz));
		biomeName =  biome.biomeName;
		isUnderwater = biomeName.equals("Deep Ocean");
		//biomeName =  "Deep Ocean";
		py = MTUtils.getSurfaceY(worldIn, px, pz, 3, isUnderwater) - 1;
		pos = new BlockPos(px, py, pz);	
		
		int typeChance = rand.nextInt(128);
		if (dimId == -1)
			typeChance = (typeChance % 6) + 90;
		else if (dimId == 1)
			typeChance = (typeChance % 6) + 58;
		if (!isUnderwater && typeChance < 32) {
			if (dimId == -1)
				typeChance = (typeChance % 6) + 18;
			else if (dimId == 1)
				typeChance = (typeChance % 6) + 30;
			if (typeChance < 16) {
				ceilBlock = Blocks.stonebrick.getStateFromMeta(3);
				wallBlock = Blocks.stonebrick.getDefaultState();
				floorBlock = Blocks.stonebrick.getStateFromMeta(3);
				stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs.getStateFromMeta(0),
						Blocks.stone_brick_stairs.getStateFromMeta(2),
						Blocks.stone_brick_stairs.getStateFromMeta(1),
						Blocks.stone_brick_stairs.getDefaultState()
				};
				difficulty = 4;
				towerTypeName = "Stone Brick";
			} else if (typeChance < 27) {
				ceilBlock = Blocks.quartz_block.getDefaultState();
				wallBlock = Blocks.quartz_block.getStateFromMeta(2);
				floorBlock = Blocks.quartz_block.getStateFromMeta(1);
				doorBlock = MazeTowers.BlockQuartzDoor;
				stairsBlock = new IBlockState[] {
					Blocks.quartz_stairs.getStateFromMeta(0),
					Blocks.quartz_stairs.getStateFromMeta(2),
					Blocks.quartz_stairs.getStateFromMeta(1),
					Blocks.quartz_stairs.getDefaultState()
				};
				difficulty = 5;
				towerTypeName = "Quartz";
			} else if (typeChance < 31) {
				ceilBlock = Blocks.obsidian.getDefaultState();
				wallBlock = Blocks.obsidian.getDefaultState();
				floorBlock = Blocks.obsidian.getDefaultState();
				doorBlock = MazeTowers.BlockObsidianDoor;
				stairsBlock = new IBlockState[] {
					Blocks.nether_brick_stairs.getStateFromMeta(0),
					Blocks.nether_brick_stairs.getStateFromMeta(2),
					Blocks.nether_brick_stairs.getStateFromMeta(1),
					Blocks.nether_brick_stairs.getDefaultState()
				};
				difficulty = 6;
				towerTypeName = "Obsidian";
			} else {
				ceilBlock = Blocks.bedrock.getDefaultState();
				wallBlock = Blocks.bedrock.getDefaultState();
				floorBlock = Blocks.bedrock.getDefaultState();
				doorBlock = MazeTowers.BlockBedrockDoor;
				stairsBlock = new IBlockState[] {
					Blocks.nether_brick_stairs.getStateFromMeta(0),
					Blocks.nether_brick_stairs.getStateFromMeta(2),
					Blocks.nether_brick_stairs.getStateFromMeta(1),
					Blocks.nether_brick_stairs.getDefaultState()
				};
				difficulty = 7;
				towerTypeName = "Bedrock";
			}
		} else {
			if (dimId == 0) {
				if (biomeName.indexOf("Taiga") > -1 ||
					biomeName.startsWith("Extreme")) {
					ceilBlock = Blocks.planks.getStateFromMeta(1);
					wallBlock = Blocks.log.getStateFromMeta(1);
					floorBlock = Blocks.planks.getStateFromMeta(1);
					doorBlock = Blocks.spruce_door;
					stairsBlock = new IBlockState[] {
						Blocks.spruce_stairs.getStateFromMeta(0),
						Blocks.spruce_stairs.getStateFromMeta(2),
						Blocks.spruce_stairs.getStateFromMeta(1),
						Blocks.spruce_stairs.getDefaultState() };
					difficulty = 2;
					towerTypeName = "Spruce";
				} else if (((biomeName == "Forest" || biomeName == "Flower Forest" &&
					rand.nextBoolean())) || biomeName.startsWith("Swampland") ||
					biomeName.indexOf("Plains") > -1) {
					ceilBlock = Blocks.planks.getDefaultState();
					wallBlock = Blocks.log.getDefaultState();
					floorBlock = Blocks.planks.getDefaultState();
					doorBlock = Blocks.oak_door;
					stairsBlock = new IBlockState[] {
						Blocks.oak_stairs.getStateFromMeta(0),
						Blocks.oak_stairs.getStateFromMeta(2),
						Blocks.oak_stairs.getStateFromMeta(1),
						Blocks.oak_stairs.getDefaultState()
					};
					difficulty = 1;
					towerTypeName = "Oak";
				} else if (biomeName.startsWith("Birch")) {
					ceilBlock = Blocks.planks.getStateFromMeta(2);
					wallBlock = Blocks.log.getStateFromMeta(2);
					floorBlock = Blocks.planks.getStateFromMeta(2);
					doorBlock = Blocks.birch_door;
					stairsBlock = new IBlockState[] {
						Blocks.birch_stairs.getStateFromMeta(0),
						Blocks.birch_stairs.getStateFromMeta(2),
						Blocks.birch_stairs.getStateFromMeta(1),
						Blocks.birch_stairs.getDefaultState()
					};
					difficulty = 2;
					towerTypeName = "Birch";
				} else if (biomeName.startsWith("Jungle")) {
					ceilBlock = Blocks.planks.getStateFromMeta(3);
					wallBlock = Blocks.log.getStateFromMeta(3);
					floorBlock = Blocks.planks.getStateFromMeta(3);
					doorBlock = Blocks.jungle_door;
					stairsBlock = new IBlockState[] {
						Blocks.jungle_stairs.getStateFromMeta(0),
						Blocks.jungle_stairs.getStateFromMeta(2),
						Blocks.jungle_stairs.getStateFromMeta(1),
						Blocks.jungle_stairs.getDefaultState()
					};
					difficulty = 2;
					towerTypeName = "Jungle";
				} else if (biomeName.startsWith("Savanna")) {
					ceilBlock = Blocks.planks.getStateFromMeta(4);
					wallBlock = Blocks.log2.getStateFromMeta(0);
					floorBlock = Blocks.planks.getStateFromMeta(4);
					doorBlock = Blocks.acacia_door;
					stairsBlock = new IBlockState[] {
						Blocks.acacia_stairs.getStateFromMeta(0),
						Blocks.acacia_stairs.getStateFromMeta(2),
						Blocks.acacia_stairs.getStateFromMeta(1),
						Blocks.acacia_stairs.getDefaultState()
					};
					difficulty = 2;
					towerTypeName = "Acacia";
				} else if (biomeName.startsWith("Roofed")) {
					ceilBlock = Blocks.planks.getStateFromMeta(5);
					wallBlock = Blocks.log2.getStateFromMeta(1);
					floorBlock = Blocks.planks.getStateFromMeta(5);
					doorBlock = Blocks.dark_oak_door;
					stairsBlock = new IBlockState[] {
						Blocks.dark_oak_stairs.getStateFromMeta(0),
						Blocks.dark_oak_stairs.getStateFromMeta(2),
						Blocks.dark_oak_stairs.getStateFromMeta(1),
						Blocks.dark_oak_stairs.getDefaultState()
					};
					difficulty = 3;
					towerTypeName = "Dark Oak";
				} else if (biomeName.startsWith("Desert")) {
					ceilBlock = Blocks.sandstone.getDefaultState();
					wallBlock = Blocks.sandstone.getDefaultState();
					wallBlock_external = Blocks.sandstone.getDefaultState();
					floorBlock = Blocks.sandstone.getDefaultState();
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.sandstone_stairs.getStateFromMeta(0),
						Blocks.sandstone_stairs.getStateFromMeta(2),
						Blocks.sandstone_stairs.getStateFromMeta(1),
						Blocks.sandstone_stairs.getDefaultState()
					};
					difficulty = 3;
					towerTypeName = "Desert";
				} else if (biomeName.startsWith("Mesa")) {
					ceilBlock = Blocks.red_sandstone.getDefaultState();
					wallBlock = Blocks.sand.getStateFromMeta(1);
					wallBlock_external = Blocks.red_sandstone.getDefaultState();
					floorBlock = Blocks.red_sandstone.getDefaultState();
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.red_sandstone_stairs.getStateFromMeta(0),
						Blocks.red_sandstone_stairs.getStateFromMeta(2),
						Blocks.red_sandstone_stairs.getStateFromMeta(1),
						Blocks.red_sandstone_stairs.getDefaultState()
					};
					difficulty = 3;
					towerTypeName = "Mesa";
				} else if (biomeName.startsWith("Ice")) {
					ceilBlock = Blocks.packed_ice.getDefaultState();
					wallBlock = Blocks.ice.getDefaultState();
					wallBlock_external = Blocks.packed_ice.getDefaultState();
					floorBlock = Blocks.packed_ice.getDefaultState();
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs.getStateFromMeta(0),
						Blocks.stone_brick_stairs.getStateFromMeta(2),
						Blocks.stone_brick_stairs.getStateFromMeta(1),
						Blocks.stone_brick_stairs.getDefaultState()
					};
					difficulty = 3;
					towerTypeName = "Ice";
				} else if (biomeName == "Mushroom Island") {
					ceilBlock = Blocks.mycelium.getDefaultState();
					wallBlock = rand.nextBoolean() ?
						Blocks.red_mushroom_block.getDefaultState() :
						Blocks.brown_mushroom_block.getDefaultState();
					floorBlock = Blocks.mycelium.getDefaultState();
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs.getStateFromMeta(0),
						Blocks.stone_brick_stairs.getStateFromMeta(2),
						Blocks.stone_brick_stairs.getStateFromMeta(1),
						Blocks.stone_brick_stairs.getDefaultState()
					};
					difficulty = 4;
					towerTypeName = "Mushroom";
				} else if (isUnderwater) {
					ceilBlock = Blocks.prismarine.getStateFromMeta(2);
					wallBlock = Blocks.prismarine.getDefaultState();
					wallBlock_external = Blocks.prismarine.getStateFromMeta(1);
					floorBlock = wallBlock_external;
					doorBlock = MazeTowers.BlockObsidianDoor;
					stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs.getStateFromMeta(0),
						Blocks.stone_brick_stairs.getStateFromMeta(2),
						Blocks.stone_brick_stairs.getStateFromMeta(1),
						Blocks.stone_brick_stairs.getDefaultState()
					};
					difficulty = 5;
					towerTypeName = "Prismarine";
				} else {
					ceilBlock = Blocks.stonebrick.getStateFromMeta(3);
					wallBlock = Blocks.stonebrick.getDefaultState();
					floorBlock = Blocks.stonebrick.getStateFromMeta(3);
					doorBlock = Blocks.iron_door;
					stairsBlock = new IBlockState[] {
						Blocks.stone_brick_stairs.getStateFromMeta(0),
						Blocks.stone_brick_stairs.getStateFromMeta(2),
						Blocks.stone_brick_stairs.getStateFromMeta(1),
						Blocks.stone_brick_stairs.getDefaultState()
					};
					difficulty = 4;
					towerTypeName = "Stone Brick";
				}
			} else if (dimId == -1) {
				ceilBlock = Blocks.nether_brick.getDefaultState();
				wallBlock = Blocks.nether_brick.getDefaultState();
				floorBlock = Blocks.netherrack.getDefaultState();
				stairsBlock = new IBlockState[] {
					Blocks.nether_brick_stairs.getStateFromMeta(0),
					Blocks.nether_brick_stairs.getStateFromMeta(2),
					Blocks.nether_brick_stairs.getStateFromMeta(1),
					Blocks.nether_brick_stairs.getDefaultState()
				};
				difficulty = 4;
				towerTypeName = "Nether";
			} else {
				ceilBlock = Blocks.end_stone.getDefaultState();
				wallBlock = Blocks.end_stone.getDefaultState();
				floorBlock = Blocks.end_stone.getDefaultState();
				doorBlock = MazeTowers.BlockEndStoneDoor;
				stairsBlock = new IBlockState[] {
					Blocks.stone_brick_stairs.getStateFromMeta(0),
					Blocks.stone_brick_stairs.getStateFromMeta(2),
					Blocks.stone_brick_stairs.getStateFromMeta(1),
					Blocks.stone_brick_stairs.getDefaultState()
				};
				difficulty = 5;
				towerTypeName = "End";
			}
		}
		if (wallBlock_external == null)
			wallBlock_external = wallBlock;
		MazeTower newTower = new MazeTower(x, py, z, ceilBlock, wallBlock,
			wallBlock_external, floorBlock, stairsBlock, doorBlock, hasXEntrance,
			difficulty, towerTypeName);
		spawnPos[towers.size()] = pos;
		newTower.addChests();
		newTower.fillGaps();
		towers.add(newTower);
		if (build) {
			newTower.build(worldIn);
			generated[towers.size() - 1] = true;
			chunksGenerated++;
		}
		
		return true;
	}

	public void recreate(World worldIn, boolean build) {
		loadOrCreateData(worldIn);
		
		for (int t = 0; t < towers.size(); t++) {
			MazeTower tower = towers.get(t);
			final int x = tower.chunkX;
			final int y = tower.baseY;
			final int z = tower.chunkZ;
			
			if (!getGenerated(t) || !worldIn.isBlockLoaded(getSpawnPos(t)))
				continue;
			
			towers.set(t, (tower = new MazeTower(x, y, z, tower.ceilBlock,
				tower.wallBlock, tower.wallBlock_external, tower.floorBlock,
				tower.stairsBlock, tower.doorBlock, tower.hasXEntrance, tower.difficulty, 
				tower.towerTypeName)));
			
			ModChestGen.initChestGen();
			tower.addChests();
			//tower.fillGaps();
			if (build) {
				if (tower.signPos != null)
					worldIn.setBlockToAir(tower.signPos);
				tower.build(worldIn);
			}
		}
	}

	public class MazeTower {

		public final int chunkX;
		public final int chunkZ;
		public final int baseY;
		public final int floors;
		public final int exitX;
		public final int exitZ;
		public final IBlockState ceilBlock;
		public final IBlockState wallBlock;
		public final IBlockState wallBlock_external;
		public final IBlockState floorBlock;
		public final Block doorBlock;
		private final int difficulty;
		private final int entranceMinX;
		private final int entranceMinZ;
		private final boolean hasXEntrance;
		private final boolean hasOddX;
		private final boolean hasOddZ;
		private final boolean isUnderwater;
		private final String towerTypeName;
		private final IBlockState[] stairsBlock;
		private int[][][] pathMap;
		private int[][][] dataMap;
		private int[][][] roomMap;
		private int[] floorHighestDepthLevel;
		private int[] floorHighestMazeDepthLevel;
		public IBlockState air;
		private EnumFacing exitDir;
		private BlockPos signPos;
		private IBlockState[][][] blockData;
		private Path[] floorExitPaths;
		private Map<ItemStack, Integer>[] floorChestItems;
		private List<Path> paths;
		private List<Path>[] floorDeadEndPaths;
		private List<Room> rooms;

		private MazeTower(int x, int y, int z, IBlockState ceilBlock,
			IBlockState wallBlock, IBlockState wallBlock_external, IBlockState floorBlock,
			IBlockState[] stairsBlocks, Block doorBlock, boolean hasXEntrance,
			int difficulty, String towerTypeName) {
			chunkX = x;
			chunkZ = z;
			baseY = y;
			floors = rand.nextInt(19) + 2;
			floorChestItems = new HashMap[floors + 1];
			floorExitPaths = new Path[floors];
			floorHighestDepthLevel = new int[floors];
			floorHighestMazeDepthLevel = new int[floors];
			this.difficulty = difficulty;
			this.hasXEntrance = hasXEntrance;
			this.isUnderwater = towerTypeName == "Prismarine";
			this.towerTypeName = towerTypeName;
			this.ceilBlock = ceilBlock;
			this.wallBlock = wallBlock;
			this.wallBlock_external = wallBlock_external;
			this.floorBlock = floorBlock;
			this.stairsBlock = stairsBlocks;
			this.doorBlock = doorBlock;
			air = !isUnderwater ? Blocks.air.getDefaultState() :
				Blocks.water.getDefaultState();
			blockData = getInitialBlockData();
			pathMap = new int[(floors * 6) + 1][16][16];
			dataMap = new int[(floors * 6) + 1][16][16];
			roomMap = new int[floors][16][16];
			paths = new ArrayList<Path>();
			rooms = new ArrayList<Room>();

			final boolean exitChance = rand.nextBoolean();
			final EnumFacing entranceDir;
			entranceMinX = hasXEntrance ? 0 : rand.nextInt(6) + 5;
			entranceMinZ = hasXEntrance ? rand.nextInt(6) + 5 : 0;
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
			//hasXEntrance ? hasOddX ? 15 : 1 : exitChance ? 7 : 8;
			exitZ = entranceMinZ + (!hasXEntrance ? 1 : 0);
			//!hasXEntrance ? hasOddZ ? 15 : 1 : exitChance ? 7 : 8;
			// Room entrance;
			// entrance = addRoom(new Room(this, 1, null));
			floorDeadEndPaths = new List[floors];
			Path entrance = new Path(this, null, new ArrayList<Path>(),
					entranceMinX, 0, entranceMinZ, entranceDir);
			for (int f = 1; f <= floors; f++) {
				if (floorExitPaths[f - 1] != null)
					entrance = floorExitPaths[f - 1].newFloor();
				else
					break;
			}
		}

		public void removeEntities(World worldIn) {
			if (!worldIn.isRemote) {
				float posX = (chunkX << 4), posY = baseY, posZ = (chunkZ << 4);
				List list = worldIn.getEntitiesWithinAABB(Entity.class,
					AxisAlignedBB.fromBounds(posX - 32, posY, posZ - 16,
						posX + 16, posY + (floors * 6) + 6, posZ + 16));
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity) list.get(i);
					if (!(entity instanceof EntityPlayer))
						entity.setDead();
				}
			}
		}

		private void generate() {

		}

		private Room getRoom(int id) {
			return rooms.get(id);
		}

		private Room getRoom(int floor, int x, int z) {
			return rooms.get(roomMap[floor][x][z]);
		}

		private IBlockState[][][] getInitialBlockData() {
			final int yLimit = (6 * floors) + 1;
			final int yLimitFloor = (int) Math.floor(yLimit / floors);
			final int xzLimit = 16;
			IBlockState[][][] data = new IBlockState[yLimit][xzLimit][xzLimit];

			for (int y = 0; y < yLimit; y++) {
				for (int z = 0; z < xzLimit; z++) {
					for (int x = 0; x < xzLimit; x++) {
						if (y % 6 == 0 || y % 6 == 4 || y == yLimit - 1 || x == 0 || z == 0
							|| x == xzLimit - 1 || z == xzLimit - 1)
							data[y][z][x] = y % 6 == 0 || y == yLimit - 1 ? floorBlock
								: y % 6 == 4 ? ceilBlock : wallBlock;
						else if (y % yLimitFloor == 0)
							data[y][z][x] = floorBlock;
						else if (y % yLimitFloor == 5)
							data[y][z][x] = air;
					}
				}
			}

			return data;
		}
		
		public IBlockState[][][] getBlockData() {
			return blockData;
		}
		
		public Chunk getChunk(World worldIn) {
			return worldIn.getChunkFromChunkCoords(chunkX, chunkZ);
		}

		private Room addRoom(Room room) {
			rooms.add(room);
			return room;
		}

		private Path addPath(Path path) {
			paths.add(path);
			return path;
		}
		
		private void checkPathDepth(Path path, int floorIndex, int depth, int mazeDepth) {
			if (depth > floorHighestDepthLevel[floorIndex] ||
				(depth == floorHighestDepthLevel[floorIndex] && rand.nextBoolean())) {
				floorExitPaths[floorIndex] = path;
				floorHighestDepthLevel[floorIndex] = depth;
			}
			
			if (mazeDepth > floorHighestMazeDepthLevel[floorIndex] ||
				(mazeDepth == floorHighestMazeDepthLevel[floorIndex] &&
				rand.nextBoolean())) {
				floorHighestMazeDepthLevel[floorIndex] = mazeDepth;
			}
		}

		private void fillGaps() {
			int startX = ((hasXEntrance && hasOddX) || (!hasXEntrance && !hasOddX)) ? 2 : 1;
			int startZ = ((hasXEntrance && hasOddZ) || (!hasXEntrance && !hasOddZ)) ? 1 : 2;
				
    		for (int y = 1; y < 4; y += 4) {
    			for (int z = startZ; z < 15; z += 2) {
    				for (int x = startX; x < 15; x += 2) {
    					//if (Path.getPathAt(this, null, x, y, z) == null) {
    					if (Path.getStateAt(this, x, y, z) == null) {
    						ArrayList<EnumFacing> dirsList = Path.getDirsList(false, true, true);
    						Iterator dirIterator = dirsList.iterator();
    						EnumFacing dir;
    						boolean gapFilled = false;
    						int floor = (y / 6) + 1;
    						while (dirIterator.hasNext() &&
    							(dir = (EnumFacing) dirIterator.next()) != null && !gapFilled) {
    							Path path;
    							int dirIndex;
    							int dirAxis;
    							int dirSign;
    							int dist;
    							if ((Path.getStateWithOffset(this, dir, 2, x, y + 1, z)) != null) {
    								dirIndex = dir.getIndex();
    								dirAxis = (int) Math.floor(dirIndex * 0.5);
    								dirSign = dirIndex % 2 == 0 ? -1 : 1;
    								/*path.children.add(new Path(this, path, path.chain, x + (dirAxis == 1 ? dirSign * 2 : 0),
    									y, z + (dirAxis == 2 ? dirSign * 2 : 0), dir.getOpposite()));*/
    								dist = Path.getMaxDistance(this, dir, 14, (dirAxis == 1 ? startZ == 1 : startX == 1), x, y, z);
    								if (Path.isPosValid(this, Path.getRelCoordsWithOffset(dir, dist + 2, x, y, z)))
    									dist += 2;
    								if (dist == 0)
    									continue;
									for (int z2 = 0; z2 <= (dirAxis == 1 ? (dist >= 2 ? dist : 0) : 0); z2++) {
										for (int x2 = 0; x2 <= (dirAxis == 2 ? (dist >= 2 ? dist : 0) : 0); x2++) {
											for (int y2 = 1; y2 < 4; y2++)
												blockData[y2 + ((y - 1) * 6)][z + (z2 * dirSign)][x + (x2 * dirSign)] = air;//Blocks.red_sandstone.getDefaultState();
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

		public void build(World worldIn) {
			final int ix = chunkX << 4;
			final int iz = chunkZ << 4;
			final int yLimit = floors * 6;
			final IBlockState stone = Blocks.stone.getDefaultState();
			final IBlockState glass = getGlassState();
			final IBlockState downButton = ModBlocks.hiddenButton.getDefaultState()
				.withProperty(BlockButton.FACING, EnumFacing.DOWN);
			Map<BlockPos, Tuple> floorScannerPos = null;
			boolean addCircuitBreaker = false;
			removeEntities(worldIn);
			for (int a = 0; a < 2; a++) {//
				int floor = 0;
				for (int y = 0; y <= yLimit + 6; y++) {
					if (isUnderwater && y % 6 == 5)
						air = Blocks.water.getDefaultState();
					else if (y % 6 == 0 && y < yLimit) {
						floor++;
						floorChestItems[floor - 1] = new HashMap<ItemStack, Integer>();
						floorScannerPos = new HashMap<BlockPos, Tuple>();

					}
					for (int z = 0; z < 16; z++) {
						for (int x = 0; x < 16; x++) {
							final BlockPos pos = new BlockPos(ix + x + (-16 * a), baseY + y, iz + z);
							IBlockState state = y <= yLimit ? blockData[y][z][x] : null;
							boolean isEdge = x == 0 || x == 15 || z == 0 || z == 15;
							if (a == 0) {//
								boolean isEdgeCorner = isEdge && ((x == 0 || x == 15) && (z == 0 || z == 15));
								if (y <= yLimit) {
									if ((state == null || (state.getBlock() != Blocks.ladder &&
										isEdge && state != air && state.getBlock() != Blocks.torch)) ||
										state == stone) state = isEdge ? wallBlock_external :
										y != yLimit ? wallBlock : floorBlock;
								} else {
									boolean isChestPos = y == yLimit + 1 && (x == 6 || x == 9) &&
										(z == 6 || z == 9);
									state = (!isEdge && y != yLimit + 6) ||
										(isEdge && ((y != yLimit + 2 && y != yLimit + 4) || isEdgeCorner)) ?
										isEdge ? wallBlock_external : !isChestPos ? air :
										getChestBlock(floor).getDefaultState()
										.withProperty(BlockChest.FACING, exitDir) : glass;
								}
								
							}//
							else {//
								Block block = state == null ? null : state.getBlock();
								if ((state == air && y % 6 != 5) || (state != null &&
									block == Blocks.torch))//
									state = wallBlock;//
								else if (state == null
									|| block == Blocks.mob_spawner ||
									block == Blocks.lava ||
									(block == wallBlock.getBlock() &&
									(y % 6 != 0 || y == yLimit)))
									state = air;
							}
							if (state != null) {
								if (state.getBlock() != doorBlock) {
									if (state.getBlock() == Blocks.melon_block) {
										addCircuitBreaker = true;
										state = MTPuzzle.redstone;
									} else if (state.getBlock() == Blocks.pumpkin)
										state = air;
									else if (y % 6 == 5 && y < yLimit && state == air)
										state = wallBlock;
									worldIn.setBlockState(pos, state);
									if (a == 0 && state.getBlock() == Blocks.dispenser)
										addDispenserArrows(worldIn, pos);
									else if (a == 0 && (
										state.getBlock() instanceof BlockChest)) {
										Path path = Path.getPathAt(this, null, x, y, z);
										Iterator iterator = initChest(worldIn, pos,
											path).iterator();
										int mazeDepth = path.getMazeDepth();
										while (iterator.hasNext()) {
											ItemStack stack = (ItemStack) iterator.next();
											floorChestItems[floor - 1].put(stack, mazeDepth);
										}
									} else if (a == 0 && state.getBlock() == Blocks.mob_spawner) {
										TileEntityMobSpawner spawner =
											(TileEntityMobSpawner)worldIn.getTileEntity(pos);
										spawner.getSpawnerBaseLogic().setEntityName(getEntityNameForSpawn(floor, isUnderwater));
									} else if (a == 0 && state.getBlock() == Blocks.web)
										worldIn.setTileEntity(pos,
											new TileEntityWebSpiderSpawner(difficulty));
									else if (state.getBlock() == MazeTowers.BlockHiddenButton &&
											state.getValue(BlockHiddenButton.FACING)
											.getAxisDirection() == AxisDirection.NEGATIVE)
										worldIn.setBlockState(pos.offset(
											state.getValue(BlockHiddenButton.FACING).getOpposite()), wallBlock);
									else if (state.getBlock() == Blocks.wall_sign)
										worldIn.setBlockState(pos.offset(state.getValue(
											BlockWallSign.FACING).getOpposite()), wallBlock);
									else if (a == 0 && addCircuitBreaker) {
										worldIn.setTileEntity(pos, new TileEntityCircuitBreaker());
										addCircuitBreaker = false;
									} else if (a == 0 && state.getBlock()
										instanceof BlockItemScanner) {
										Path path = Path.getPathAt(this, null, x, y, z);
										floorScannerPos.put(pos,
											new Tuple(path.pathIndex, path.getMazeDepth()));
										worldIn.setBlockState(pos.offset(state.getValue(
											BlockItemScanner.FACING).getOpposite()), wallBlock);
									}
								} else {
									if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
										ItemDoor.placeDoor(worldIn, pos,
											state.getValue(BlockDoor.FACING),
											doorBlock);
								}
							}
							final int groundY;
							
							if (y == 0
								&& (groundY = MTUtils.getGroundY(worldIn,
								ix + x + (-16 * a), baseY, iz + z, 1, isUnderwater) + 1) < baseY) {
								for (int y2 = 0; y2 <= baseY - groundY; y2++) {
									try {
									worldIn.setBlockState(pos.down(y2), !isEdge ?
										floorBlock : wallBlock_external);
									} catch (NullPointerException e) {
										e = null;
									}
								}
							}
						}
					}
					
					if (a == 0 && y % 6 == 5 && y < yLimit &&
						!floorScannerPos.isEmpty() && !floorChestItems
						[(int) Math.floor(y / 6)].isEmpty()) {
						 List<Entry<BlockPos, Tuple>> floorScannerPosEntries =
							new ArrayList<Entry<BlockPos, Tuple>>(floorScannerPos.entrySet());
						 List<Entry<ItemStack, Integer>> floorChestItemsEntries =
							new ArrayList<Entry<ItemStack, Integer>>
				        	(floorChestItems[(int) Math.floor(y / 6)].entrySet());
				        
						 Collections.sort(floorScannerPosEntries, scannerPosComparator.reversed());
						 Collections.sort(floorChestItemsEntries, chestItemsComparator.reversed());
				        
				        LinkedHashMap<BlockPos, Tuple> floorScannerPosSorted =
					        new LinkedHashMap<BlockPos, Tuple>(floorScannerPosEntries.size());
				        LinkedHashMap<ItemStack, Integer> floorChestItemsSorted =
					        new LinkedHashMap<ItemStack, Integer>(floorChestItemsEntries.size());
				        
				        for (Entry<BlockPos, Tuple> entry : floorScannerPosEntries) {
				        	floorScannerPosSorted.put(entry.getKey(), entry.getValue());
				        }
				        
				        for (Entry<ItemStack, Integer> entry : floorChestItemsEntries) {
				        	floorChestItemsSorted.put(entry.getKey(), entry.getValue());
				        }
						
						Iterator iterator = floorScannerPosSorted.keySet().iterator();
						ArrayList<ItemStack> stackList = new ArrayList<ItemStack>
							(floorChestItemsSorted.keySet());
						//try {
						while (iterator.hasNext()) {
							BlockPos pos;
							TileEntityItemScanner te;
							Tuple curVals;
							int mazeDepth;
							pos = (BlockPos) iterator.next();
							curVals = floorScannerPosSorted.get(pos);
							mazeDepth = (int) curVals.getSecond();
							te = (TileEntityItemScanner) worldIn.getTileEntity(pos);
							while (!stackList.isEmpty() && mazeDepth < floorChestItemsSorted
								.get(stackList.get(0)))
								stackList.remove(0);
							if (!stackList.isEmpty() && te != null &&
								te instanceof TileEntityItemScanner) {
								Path scannerPath = paths.get((int) curVals.getFirst() - 1);
								boolean doesStateMatch;
								doesStateMatch = worldIn.getBlockState(pos = (new BlockPos(
									ix + scannerPath.ix + (-16 * a), baseY + scannerPath.iy + 3,
									iz + scannerPath.iz).offset(scannerPath.dir))).getBlock() == Blocks.wall_sign;
								te.generateRandomKeyStackFromList(stackList);
								if (doesStateMatch) {
									if (te.getKeyStack() != null) {
										String keyStackDisplayName = te.getKeyStack()
											.getDisplayName();
										((TileEntitySign) worldIn
											.getTileEntity(pos)).signText[2] =
											new ChatComponentText(difficulty < 4 ?
											keyStackDisplayName : MTUtils
											.getEncodedItemName(keyStackDisplayName, rand)
										);
									} else
										worldIn.setBlockState(pos, wallBlock);
								}
							} else
								break;
						}
						/*} catch (NullPointerException e) {
							e = null;
						}*/
					}
				}
			}//
			Path entrancePath = paths.get(0);
			IBlockState signState = Blocks.wall_sign.getDefaultState()
				.withProperty(BlockWallSign.FACING, entrancePath.getDir().getOpposite());
			TileEntitySign te;
			String difficultyString = "";
			BlockPos signPos = new BlockPos(
				ix + (entrancePath.ix), baseY + 3, iz + (entrancePath.iz))
				.offset(entrancePath.getDir().getOpposite());
			this.signPos = signPos;
			for (int d = 0; d < difficulty; d++)
				difficultyString += "✪";
			worldIn.setBlockState(signPos, signState);
			te = (TileEntitySign) worldIn.getTileEntity(signPos);
			te.signText[0] = new ChatComponentText(towerTypeName);
			te.signText[1] = new ChatComponentText("Maze Tower");
			te.signText[3] = new ChatComponentText(difficultyString);
			te.setEditable(false);
			worldIn.setTileEntity(signPos, te);
			signPos = new BlockPos(
				ix + exitX, baseY + yLimit + 1, iz + exitZ)
				.offset(hasXEntrance ? EnumFacing.NORTH : EnumFacing.WEST);
			worldIn.setBlockState(signPos, signState =
				Blocks.standing_sign.getDefaultState().withProperty(
				BlockStandingSign.ROTATION, hasXEntrance ? 12 : 0));
			te = (TileEntitySign) worldIn.getTileEntity(signPos);
			te.signText[1] = new ChatComponentText("Exit");
			te.signText[2] = new ChatComponentText("↓");
			te.setEditable(false);
			worldIn.setTileEntity(signPos, te);
		}
		
		private void addDispenserArrows(World worldIn, BlockPos pos) {
			TileEntity te;
			int amount = dataMap[pos.getY() - baseY][pos.getZ() - (chunkZ << 4)][pos.getX() - (chunkX << 4)];
				if ((te = worldIn.getTileEntity(pos)) != null &&
					te instanceof TileEntityDispenser) {
					TileEntityDispenser ted = (TileEntityDispenser) te;
					LockCode code = new LockCode("fhqwhgads");
					ItemStack[] projectiles;
					int stackCount = 1;
					int projectileChance = rand.nextInt(10) + 1;
					te = null;
					if (difficulty < 4 || (difficulty < 7 &&
						projectileChance < (difficulty - 3) * 3))
						projectiles = new ItemStack[] { new ItemStack(Items.arrow, amount) };
					else {
						stackCount = Math.min(amount, 9);
						List<Integer> projectileIndexes = new ArrayList<Integer>();
						ItemStack[] projectileList = new ItemStack[] {
							new ItemStack(Items.potionitem, 1, 16394), //Slowness I (short)
							new ItemStack(Items.potionitem, 1, 16392), //Weakness I (short)
							new ItemStack(Items.potionitem, 1, 16388), //Poison I (short)
							new ItemStack(Items.potionitem, 1, 16396), //Harming I
							new ItemStack(Items.potionitem, 1, 16458), //Slowness I (long)
							new ItemStack(Items.potionitem, 1, 16456), //Weakness I (long)
							new ItemStack(Items.potionitem, 1, 16452), //Poison I (long)
							new ItemStack(Items.potionitem, 1, 16420), //Poison II (short)
							new ItemStack(Items.potionitem, 1, 16484), //Poison II (long)
							new ItemStack(Items.potionitem, 1, 16428) //Harming II
						};
	
						projectiles = new ItemStack[stackCount];
						
						for (int p = 0; p < stackCount; p++) {
							if (p == 0 || difficulty >= 10 || p < (difficulty - 6)) {
								int subtractFromRange = Math.max(difficulty - 8, 0);
								projectileChance = rand.nextInt(4 - subtractFromRange) +
									(difficulty - subtractFromRange);
								projectileIndexes.add(projectileChance);
							} else
								projectileChance =
								projectileIndexes.get(rand.nextInt(projectileIndexes.size()));
							projectiles[p] = projectileList[projectileChance];
						}
					}
					//ted.setLockCode(code);
					for (int c = 0; c < stackCount; c++)
						ted.setInventorySlotContents(c, projectiles[c]);
					worldIn.setTileEntity(pos, ted);
				}
			
		}
		
		public class ValueComparator implements Comparator {
				 
			Map map;
			 
			public ValueComparator(Map map) {
				this.map = map;
			}
			  
			public int compare(Object keyA, Object keyB) {
				Comparable valueA = (Comparable) map.get(keyA);
				Comparable valueB = (Comparable) map.get(keyB);

				return valueA.compareTo(valueB);
			}
		}
		
		public void addChests() {
			int chestCount;
			int floorCount;
			Iterator pathIterator;
			for (int f = 0; f < floors; f++) {
				int highestDepth = floorHighestDepthLevel[f];
				int highestMazeDepth = floorHighestMazeDepthLevel[f];
				pathIterator = floorDeadEndPaths[f].iterator();	
				chestCount = 0;
				while (pathIterator.hasNext()) {
					Path path = (Path) pathIterator.next();
					if (path.getStateAt(this, path.fx, path.fy + 1, path.fz) == air) {
						boolean randBool = true;
						boolean usePath = false;
						if (path.mazeDepth == highestMazeDepth && ((randBool = rand.nextBoolean()) || difficulty > 6))
							usePath = true;
						else if (path.depth == highestDepth && ((randBool = rand.nextBoolean()) || difficulty > 5))
							usePath = true;
						else {
							int depthDiff = Math.min(highestDepth - path.depth, 5);
							usePath = true;
							for (int c = 0; c < depthDiff; c++) {
								if (rand.nextBoolean()) {
									usePath = false;
									break;
								}
							}
						}
						if (usePath) {
							IBlockState chestState = randBool ? getChestBlock(f + 1)
								.getDefaultState() : Blocks.trapped_chest.getDefaultState();
							IBlockState[] chestStates = new IBlockState[] {
								chestState.withProperty(BlockChest.FACING, EnumFacing.NORTH),
								chestState.withProperty(BlockChest.FACING, EnumFacing.SOUTH),
								chestState.withProperty(BlockChest.FACING, EnumFacing.WEST),
								chestState.withProperty(BlockChest.FACING, EnumFacing.EAST)
							};
							path.setStateAt(path.fx, path.fy + 1, path.fz,
								chestStates[path.getDir().getOpposite().getIndex() - 2]);
							if (!randBool) {
								path.setStateAt(path.fx, path.fy, path.fz,
									Blocks.tnt.getDefaultState());
								if (path.floor != 1 &&
									path.getStateAt(this, path.fx, path.fy - 1, path.fz) == air)
									path.setStateAt(path.fx, path.fy, path.fz, floorBlock);
							}
							chestCount++;
						}
					}
				}
			}
		}
		
		public List<ItemStack> initChest(World world, BlockPos pos, Path path) {
			if (path != null) {
				TileEntityChest chestTEC = (TileEntityChest) world.getTileEntity(pos);
				int difficulty = this.difficulty + (int) (Math.floor(path.floor / 5));
				if (chestTEC != null) {
					ArrayList<ItemStack> items = new ArrayList<ItemStack>();
					WeightedRandomChestContent.generateChestContents(world.rand,
						chestInfo[difficulty - 1].getItems(world.rand), chestTEC,
						chestInfo[difficulty - 1].getCount(world.rand));
					for (int i = 0; i < chestTEC.getSizeInventory(); i++) {
						ItemStack curStack;
						if ((curStack = chestTEC.getStackInSlot(i)) != null)
							items.add(curStack);
					}
					world.setTileEntity(pos, chestTEC);
					return items;
				}
			}
			
			return new ArrayList<ItemStack>();
		}
		
		private String getEntityNameForSpawn(int floor, boolean isUnderwater) {
			String entityName;
			
			int diffIndex = (int) (difficulty + Math.floor((floor - 1) / 5));
			
			String[] mobList = !isUnderwater ? new String[] {
				"Zombie", "Skeleton", "Spider", "Creeper", "Silverfish",
				"Slime", "Witch", "Cave Spider", "Enderman", "Endermite", "Magma Cube",
				"Blaze"
			} : new String [] { "Guardian" };
			
			entityName = mobList[Math.min(rand.nextInt(5) + diffIndex, mobList.length - 1)];
			
			return entityName;
		}
		
		private Block getChestBlock(int floor) {
			int addToDifficulty = (int) Math.floor((floor - 1) / 5);
			int difIndex = difficulty + addToDifficulty;
			if (difIndex < 5)
				return Blocks.chest;
			else if (difIndex < 7)
				return MazeTowers.BlockIronChest;
			else if (difIndex < 9)
				return MazeTowers.BlockGoldChest;
			else
				return MazeTowers.BlockDiamondChest;
		}

		private IBlockState getGlassState() {
			IBlockState state;
			Block stainedGlass = Blocks.stained_glass;
			IBlockState[] glassStates = new IBlockState[] {
				Blocks.glass.getDefaultState(),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLUE),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.PURPLE),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.LIME),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.YELLOW),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.ORANGE),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.RED),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.BLACK),
				stainedGlass.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.WHITE)
			};
			return glassStates[(int) Math.floor(floors / 5)];
		}

		/*public void buildRoom(World worldIn) {
			for (int r = 0; r < rooms.size(); r++) {
				final Room room = rooms.get(r);
				final int ix = chunkX << 4;
				final int iz = chunkZ << 4;
				boolean isBaseRoom = room.minY == 0;
				for (int y = room.minY; y <= room.maxY; y++) {
					for (int z = room.minZ; z <= room.maxZ; z++) {
						for (int x = room.minX; x <= room.maxX; x++) {
							final BlockPos pos = new BlockPos(ix + x,
									baseY + y, iz + z);
							if (y == room.minY || y == room.maxY
									|| x == room.minX || z == room.minZ
									|| x == room.maxX || z == room.maxZ) {
								worldIn.setBlockState(pos, ceilBlock);

								final int groundY;
								if (isBaseRoom
										&& y == 0
										&& (groundY = MTUtils.getGroundY(
												worldIn, ix + x, baseY, iz + z,
												1) + 1) < baseY, isUnderwater) {
									for (int y2 = 0; y2 <= baseY - groundY; y2++)
										worldIn.setBlockState(pos.down(y2),
												floorBlock);
								}
							} else
								worldIn.setBlockToAir(pos);
						}
					}
				}
			}
		}*/

		public void addMobSpawners(int floor) {
			int spawnCount = 0;
			final int addToDifficulty = (int) Math.floor(floor / 5);
			final int spawnY = (floor * 6) - 1;
			final IBlockState spawnerState = Blocks.mob_spawner.getDefaultState();
			List<int[]> spawnerCoordsList = new ArrayList<int[]>();
			spawnerCoordsList.add(new int[] { spawnY, 4, 4 });
			spawnerCoordsList.add(new int[] { spawnY, 4, 11 });
			spawnerCoordsList.add(new int[] { spawnY, 11, 4 });
			spawnerCoordsList.add(new int[] { spawnY, 11, 11 });
			spawnerCoordsList.add(new int[] { spawnY, rand.nextBoolean() ? 7 : 8,
				rand.nextBoolean() ? 7 : 8 });
			
			switch (difficulty + addToDifficulty) {
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
				final int randIndex = rand.nextInt(spawnerCoordsList.size());
				final int[] coords = spawnerCoordsList.get(randIndex);
				if (Path.getStateAt(this, coords) == air) {
					boolean isAccessible = (difficulty <= 4 ||
						(rand.nextInt(difficulty - 3) == 0));
					blockData[coords[0]][coords[1]][coords[2]] = spawnerState;
					if (isAccessible) {
						blockData[coords[0] - 1][coords[1]][coords[2]] =
						Blocks.pumpkin.getDefaultState();
						if (blockData[coords[0] - 2][coords[1]][coords[2]] == air)
							blockData[coords[0] - 2][coords[1]][coords[2]] =
							Blocks.pumpkin.getDefaultState();
					}
				}
				spawnerCoordsList.remove(randIndex);
			}
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
		private final BlockPos pos;
		private boolean hasMtp;
		private boolean isDeadEnd;
		private int dirAxis;
		private int maxDistance;
		private int distance;
		private int depth; // Actual depth in paths
		private int mazeDepth; // Depth in locked doors
		private Path parent;
		private ArrayList<MTPuzzle> mtp = null;
		private ArrayList<Path> chain;
		private ArrayList<Path> children;
		private Stack<EnumFacing> reservedDirs;

		private Path(MazeTower tower, Path parentPath, ArrayList pathChain, int ix, int iy, int iz, EnumFacing facing) {
			this.tower = tower;
			this.parent = parentPath;
			this.chain = pathChain;
			floor = (int) Math.floor(iy / 6) + 1;
			isTowerEntrance = parentPath == null;
			isFloorEntrance = isTowerEntrance || parentPath.floor == floor - 1;
			isDeadEnd = !isFloorEntrance;
			depth = !isTowerEntrance ? parent.depth + 1 : 1;
			mazeDepth = !isTowerEntrance ? parent.mazeDepth : 1;
			this.chain.add(this);
			this.ix = ix;
			this.iy = iy;
			this.iz = iz;
			pathIndex = !isTowerEntrance ?
				tower.paths.get(tower.paths.size() - 1).pathIndex + 1 : 1;
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
					dir = ((maxDistance = getMaxDistance(tower, facing, 14,
							false, ix, iy + 1, iz)) >= (!hasMtp ? 2 : 4)) ? facing : null;
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
				distance = !isTowerEntrance ? Math.max((int) Math
					.floor((rand.nextInt(maxDistance) + 1) * 0.5) * 2,
					!hasMtp ? 2 : 4) : 3;
				int[] fcoords = getRelCoordsWithOffset(dir, distance);
				fx = fcoords[2];
				fy = fcoords[0] - 1;
				fz = fcoords[1];

				tower.addPath(this);
				
				if (!isTowerEntrance && !parent.isDeadEnd)
					parent.isDeadEnd = false;

				if (hasMtp) {
					mtp = new ArrayList<MTPuzzle>();
					int mtpCount = 1;// (rand.nextInt(tower.floors) < 3 ? 2 :
										// 3);
					for (int p = 0; p < mtpCount && (mtp.isEmpty() || mtp.get(mtp.size() - 1) != null); p++) {
						MTPuzzle newMtp = getMtp();
						if (newMtp != null) {
							mtp.add(newMtp);
							mazeDepth += newMtp.getMazeDepthValue();
						}
					}
					hasMtp = false;
				} else if (!isFloorEntrance) {
					if (rand.nextInt(7) < 2) {
						this.hasMtp = true;
					}
				}

				for (int y = 1; y < 4; y++) {
					for (int d = 0; d <= distance; d++)
						setPathWithOffset(d, y);
				}
				
				//if (true) {
					int dist = rand.nextInt(distance);
					int windowX = ix + (dir.getAxis() == Axis.X ? dist : 0);
					int windowZ = iz + (dir.getAxis() == Axis.Z ? dist : 0);
					EnumFacing sideDir = rand.nextBoolean() ? dir.rotateY() :
						dir.rotateYCCW();
					Path checkPath = getPathWithOffset(tower, this, sideDir,
						distance >> 1, windowX, iy, windowZ);
					IBlockState checkState = getStateAt(tower, windowX, iy + 1, windowZ);
					boolean isXAxis = dir.getAxis() == Axis.X;
					if (checkPath != null && ((checkPath.depth > depth + 2 &&
						(checkState != null && checkState.getBlock() !=
						Blocks.ladder)) || (checkPath = getPathWithOffset(tower, this,
						(sideDir = sideDir.getOpposite()), distance >> 1, windowX, iy,
						windowZ)) != null && (checkPath.depth > depth + 2 &&
						(checkState = getStateAt(tower, windowX, iy + 1,
						windowZ)) != null && checkState.getBlock() != Blocks.ladder))) {
						BlockPos windowPos = new BlockPos(windowX, iy, windowZ).offset(sideDir);
						setStateAt(windowPos.getX(), iy + 2, windowPos.getZ(),
							Blocks.glass_pane.getDefaultState());
					}
				//}
				
				if ((isDeadEnd = children.size() == 0)) {
					if (tower.floorDeadEndPaths[floor - 1] == null)
						tower.floorDeadEndPaths[floor - 1] = new ArrayList<Path>();
					tower.floorDeadEndPaths[floor - 1].add(this);
				}
				
				tower.checkPathDepth(this, floor - 1, depth, mazeDepth);
			} else {
				dirIndex = 0;
				dirAxis = -1;
				dirSign = 0;
				distance = 0;
				fy = iy;
				fz = iz;
				fx = ix;
				chain.remove(this);
			}
		}

		public Path newFloor() {
			boolean isLastFloor = floor == tower.floors;
			EnumFacing ladderDir;
			EnumFacing exitDir = tower.hasXEntrance ?
				EnumFacing.EAST : EnumFacing.SOUTH;
			IBlockState ladder = Blocks.ladder.getDefaultState()
				.withProperty(BlockLadder.FACING, ladderDir = getLadderDir());
			IBlockState ladderExit = Blocks.ladder.getDefaultState()
				.withProperty(BlockLadder.FACING, exitDir);
			if (isLastFloor)
				tower.exitDir = ladderDir.getOpposite();
			for (int y = 0; y < (!isLastFloor ? 9 : 6); y++) {
				//int[] coords = getRelCoordsWithOffset(dir, distance);
				/*if (isPosValid(coords[2], coords[0], coords[1]))
					setStateWithOffset(distance + 1, y, 0, tower.wallBlock);*/
				if (y == 5) {
					//addFloorMedianAccess(coords[2], coords[1], false);
					tower.addMobSpawners(floor);
				}
				if (y > 0)
					setStateWithOffset(distance, y, 0, ladder);
				if (floor != 1 || (y > 6)) {
					setStateAt(tower.exitX, y + ((floor - 1) * 6),
						tower.exitZ, ladderExit);
					if (floor != 1 && y % 6 == 2 && getStateWithOffset(tower,
						tower.hasXEntrance ? EnumFacing.EAST : EnumFacing.SOUTH, 2,
						tower.exitX, y + ((floor - 1) * 6), tower.exitZ) == tower.air)
						setStateAt(tower.exitX + (tower.hasXEntrance ? 1 : 0),
							y + ((floor - 1) * 6), tower.exitZ + (!tower.hasXEntrance ?
							1 : 0), Blocks.glass_pane.getDefaultState());
				} else if (y >= 4) {
					if (tower.hasXEntrance) {
						if (y == 4) {
							setStateAt(tower.exitX, y + ((floor - 1) * 6),
								tower.exitZ - 1, Blocks.air.getDefaultState());
							setStateAt(tower.exitX, y + ((floor - 1) * 6),
								tower.exitZ, tower.ceilBlock);
							setStateAt(tower.exitX, y + ((floor - 1) * 6),
								tower.exitZ + 1, MazeTowers.BlockMemoryPiston
								.getDefaultState().withProperty(
								BlockMemoryPistonBase.FACING, EnumFacing.NORTH));
						} else if (y == 5) {
							setStateAt(tower.exitX, y + ((floor - 1) * 6),
								tower.exitZ, MazeTowers.BlockHiddenButton
								.getDefaultState().withProperty(BlockHiddenButton.FACING,
								EnumFacing.NORTH));
						}
					} else {
						if (y == 4) {
							setStateAt(tower.exitX - 1, y + ((floor - 1) * 6),
								tower.exitZ, Blocks.air.getDefaultState());
							setStateAt(tower.exitX, y + ((floor - 1) * 6),
								tower.exitZ, tower.ceilBlock);
							setStateAt(tower.exitX + 1, y + ((floor - 1) * 6),
								tower.exitZ, MazeTowers.BlockMemoryPiston
								.getDefaultState().withProperty(
								BlockMemoryPistonBase.FACING, EnumFacing.WEST));
						} else if (y == 5) {
							setStateAt(tower.exitX, y + ((floor - 1) * 6),
								tower.exitZ, MazeTowers.BlockHiddenButton
								.getDefaultState().withProperty(BlockHiddenButton.FACING,
								EnumFacing.WEST));
						}
					}
				}
			}
			if (isLastFloor) {
				setStateWithOffset(distance, 6, 0, ladder);
				setStateAt(tower.exitX, 6 + ((floor - 1) * 6),
					tower.exitZ, ladderExit);
			}
			return !isLastFloor ? new Path(tower, this, chain, fx, fy + 6, fz, null) : null;
		}
		
		private void addFallTrapHole(int x, int z, boolean isFatal) {
			int floor = this.floor - 1;
			int y = (floor * 6) - 1;
			Block fluid = (tower.difficulty < 5 || (tower.difficulty == 5 && rand.nextBoolean()) ?
				Blocks.water : Blocks.lava);
			//addFloorMedianAccess(x, z, true);
			
			if (isFatal) {
				setStateAt(x, y - 1, z, fluid.getDefaultState());
				setStateAt(x, y - 2, z, tower.air);
				setStateAt(x, y - 3, z, tower.air);
				setStateAt(x, y - 4, z, tower.air);
			}
		}
		
		private void addFloorMedianAccess(int x, int z, boolean isFallTrap) {
			int floor = this.floor - (isFallTrap ? 1 : 0);
			int y = (floor * 6) - 1;
			addFloorMedianAccess(x, y, z, isFallTrap);
		}
		
		private void addFloorMedianAccess(int x, int y, int z, boolean isFallTrap) {
			//if (getStateAt(tower, x + 1, y, z) == air)
				setStateAt(x + 1, y, z, tower.wallBlock);
			//if (getStateAt(tower, x - 1, y, z) == air)
				setStateAt(x - 1, y, z, tower.wallBlock);
			//if (getStateAt(tower, x + 1, y, z + 1) == air)
				setStateAt(x + 1, y, z + 1, tower.wallBlock);
			//if (getStateAt(tower, x - 1, y, z + 1) == air)
				setStateAt(x - 1, y, z + 1, tower.wallBlock);
			//if (getStateAt(tower, x + 1, y, z - 1) == air)
				setStateAt(x + 1, y, z - 1, tower.wallBlock);
			//if (getStateAt(tower, x - 1, y, z - 1) == air)
				setStateAt(x - 1, y, z - 1, tower.wallBlock);
			//if (getStateAt(tower, x, y, z + 1) == air)
				setStateAt(x, y, z + 1, tower.wallBlock);
			//if (getStateAt(tower, x, y, z - 1) == air)
				setStateAt(x, y, z - 1, tower.wallBlock);
		}

		private EnumFacing getDir() {
			if (dir != null)
				return dir;
			else {
				EnumFacing facing = null;
				IBlockState[][][] data = tower.blockData;
				int prevdirAxis = parent.dirAxis;
				int minDist = !hasMtp ? 2 : 4;
				List<EnumFacing> dirs = getDirsList(false, prevdirAxis != 1,
						prevdirAxis != 2);
				Iterator rdIterator = parent.reservedDirs.iterator();
				while (rdIterator.hasNext())
					dirs.remove(rdIterator.next());
				Iterator dirsIterator = dirs.iterator();
				while (dirsIterator.hasNext()
					&& ((facing = ((EnumFacing) dirsIterator.next())) == null ||
					(maxDistance = getMaxDistance(facing, minDist, false)) < minDist));

				return facing;
			}
		}

		private static ArrayList<EnumFacing> getDirsList(boolean ud,
				boolean ns, boolean ew) {
			ArrayList<EnumFacing> dirs = new ArrayList<EnumFacing>();
			if (ew) {
				dirs.add(EnumFacing.EAST);
				dirs.add(rand.nextInt(2), EnumFacing.WEST);
			}
			if (ns) {
				dirs.add(rand.nextInt(dirs.size() + 1), EnumFacing.SOUTH);
				dirs.add(rand.nextInt(dirs.size() + 1), EnumFacing.NORTH);
			}
			if (ud) {
				dirs.add(EnumFacing.UP);
				dirs.add(rand.nextInt(2) + (dirs.size() - 1), EnumFacing.DOWN);
			}

			dirs.add(null);

			return dirs;
		}
		
		private EnumFacing getLadderDir() {
			ArrayList<EnumFacing> dirsList = getDirsList(false, true, true);
			Iterator dirsIterator;
			EnumFacing facing = null;
			boolean isValidDir = false;
			int[] coords = new int[3];
			
			dirsList.remove(dir);
			dirsIterator = dirsList.iterator();
			
			while (!isValidDir && dirsIterator.hasNext() && ((facing = (EnumFacing) dirsIterator.next()) == null ||
				!isPosValid(tower, coords = getRelCoordsWithOffset(facing, 1, fx, fy, fz)))) {
				int y = 1;
				for (; y <= 9; y++) {
					if (getStateAt(tower, coords[2], coords[0] + y, coords[1]) != tower.wallBlock)
						break;
				}
				if (y > 7)
					isValidDir = true;
			}
			return facing;
		}

		private int getMaxDistance(EnumFacing facing, int limit, boolean odd) {
			return getMaxDistance(tower, facing, limit, odd, ix, iy, iz);
		}

		private static int getMaxDistance(MazeTower tower, EnumFacing facing, int limit, boolean odd,
				int x, int y, int z) {
			boolean validPos;
			int dist = odd ? 1 : 0;
			int facingIndex = (int) Math.floor(facing.getIndex() * 0.5);
			int facingSign = facing.getIndex() % 2 == 0 ? -1 : 1;
			// int xLimit = facingSign == -1 ? x - 1 : ((14 + (odd ? 1 : 0)) -
			// x);
			// int zLimit = facingSign == -1 ? z - 1 : ((14 + (odd ? 1 : 0)) -
			// z);
			// int addDist;
			do {
				dist += 2;// (addDist = (dist != 0 && (dist != 1 || odd)) ? 2 :
							// 1);
			} while (dist <= limit
					&& (isPosValid(tower, getRelCoordsWithOffset(facing, dist, x, y, z)) && getStateWithOffset(tower,
							facing, dist, x, y, z) == null));
			/*if ((tower.hasXEntrance && facing.getAxis() == Axis.X &&
				(facingSign == 1 && tower.hasOddX)) ||
				(!tower.hasXEntrance && facing.getAxis() == Axis.Z))
				dist -= 2;*/
					
			// validPos = isPosValid(getRelCoordsWithOffset(facing, dist - 2, x,
			// y, z));
			return Math.min(dist - 2, limit);
		}
		
		private boolean isPosAccessible(int x, int y, int z) {
			boolean isAccessible = (getStateAt(tower, x + 1, y, z) != null ||
				getStateAt(tower, x - 1, y, z) != null ||
				getStateAt(tower, x, y, z + 1) != null ||
				getStateAt(tower, x, y, z - 1) != null);
			return isAccessible;
		}

		private IBlockState getStateWithOffset(MazeTower tower, EnumFacing facing, int dist) {
			return getStateWithOffset(tower, facing, dist, ix, iy, iz);
		}
		
		private static IBlockState getStateWithOffset(MazeTower tower, EnumFacing facing, int dist,
			int[] coords) {
			
			return getStateAt(tower, coords);
		}

		private static IBlockState getStateWithOffset(MazeTower tower, EnumFacing facing, int dist,
				int x, int y, int z) {
			int[] coords = getRelCoordsWithOffset(facing, dist, x, y, z);

			return getStateAt(tower, coords);
		}

		private Path getPathWithOffset(EnumFacing facing, int dist) {
			return getPathWithOffset(tower, this, facing, dist, ix, iy, iz);
		}
		
		private static Path getPathWithOffset(MazeTower tower, Path path,
				EnumFacing facing, int dist, int[] coords) {
			
			return getPathAt(tower, path, coords);
		}

		private static Path getPathWithOffset(MazeTower tower, Path path,
				EnumFacing facing, int dist, int x, int y, int z) {
			int[] coords = getRelCoordsWithOffset(facing, dist, x, y, z);

			return getPathAt(tower, path, coords);
		}

		private static Path getPathAt(MazeTower tower, Path path, int[] coords) {
			return getPathAt(tower, path, coords[2], coords[0], coords[1]);
		}

		private static Path getPathAt(MazeTower tower, Path pathIn, int x,
				int y, int z) {
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

		private static IBlockState getStateAt(MazeTower tower, int[] coords) {
			return getStateAt(tower, coords[2], coords[0], coords[1]);
		}

		private static IBlockState getStateAt(MazeTower tower, int x, int y,
				int z) {
			IBlockState state = null;

			if (isPosValid(tower, x, y, z))
				state = tower.blockData[y][z][x];

			return state;
		}

		private int[] getRelCoordsWithOffset(EnumFacing facing, int dist) {
			return getRelCoordsWithOffset(facing, dist, ix, iy, iz);
		}

		private static int[] getRelCoordsWithOffset(EnumFacing facing,
				int dist, int rx, int ry, int rz) {
			int index = facing != null ? (int) Math.floor(facing.getIndex() * 0.5) : -1;
			int sign = (facing != null && facing.getIndex() % 2 == 0) ? -1 : 1;
			int x = rx + ((index != 2) ? 0 : dist * sign);
			int y = ry + ((index != 0) ? 0 : dist * sign) + (ry % 6 == 0 ? 1 : 0);
			int z = rz + ((index != 1) ? 0 : dist * sign);
			return new int[] { y, z, x };
		}
		
		public int getMazeDepth() {
			return mazeDepth;
		}

		private static boolean isPosValid(MazeTower tower, int[] coords) {
			return isPosValid(tower, coords[2], coords[0], coords[1]);
		}

		private static boolean isPosValid(MazeTower tower, int x, int y, int z) {
			int floor = (int) Math.floor(y / 6) + 1;
			return ((x > 0 && x < 15) && (z > 0 && z < 15) && (y >= 0 && y < floor * 6) &&
				(floor == 1 || x != tower.exitX || z != tower.exitZ));
		}

		private void setStateWithOffset(int dist, int addY, int sideOffset,
				IBlockState state) {
			final int x = ix + (dirAxis != 2 ? dirAxis == 0 ? 0 : sideOffset : dist * dirSign);
			final int y = iy + addY + (dirAxis != 0 ? 0 : dist);
			final int z = iz + (dirAxis != 1 ? dirAxis == 0 ? 0 : sideOffset : dist * dirSign);
			final IBlockState curState;
			try {
				curState = tower.blockData[y][z][x];
			} catch(ArrayIndexOutOfBoundsException e) {
				return;
			}
			if (curState == null || curState == tower.air
				|| curState.getBlock() == tower.wallBlock.getBlock() ||
				state.getBlock() == Blocks.ladder)
				tower.blockData[y][z][x] = state;
			if (tower.pathMap[y][z][x] == 0)
				tower.pathMap[y][z][x] = pathIndex;
			if (dist == distance && addY == 3 && sideOffset == 0 && state == tower.air) {
				int pathsChance = rand.nextInt(8);
				int numPaths = pathsChance < 3 ? 1 : pathsChance != 7 ? 2 : 3;
				/*Iterator rdIterator = reservedDirs.iterator();
				while (rdIterator.hasNext()) {
					Path newPath = new Path(tower, this, chain, x, y - addY, z, (EnumFacing) rdIterator.next());
					if (newPath != null) {
						children.add(newPath);
						numPaths--;
					}
				}*/
				// tower.build(curWorld);
				// if (numPaths != 1) {
				for (int p = 0; p < numPaths; p++) {
					Path newPath = new Path(tower, this, chain, x, y - addY, z, null);
					if (newPath != null && newPath.dir != null)
						children.add(newPath);
				}
				// } else {

				// }
			}
		}
		
		private void setStateAt(int x, int y, int z, IBlockState state) {
			IBlockState curState = tower.blockData[y][z][x];
			Block curBlock;
			/*if (curState != null && curState.getBlock() == Blocks.redstone_block)
				return;*/
			if (curState == null ||
				((curBlock = curState.getBlock()) != Blocks.lever &&
				curBlock != MazeTowers.BlockHiddenButton && curBlock != Blocks.ladder &&
				curBlock != Blocks.redstone_wire && curBlock != Blocks.wall_sign &&
				curBlock != Blocks.glass_pane))
				tower.blockData[y][z][x] = state;
		}

		private void setPathWithOffset(int dist, int addY) {
			IBlockState torch = !tower.isUnderwater ?
				Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, dir.rotateYCCW()) :
				Blocks.sea_lantern.getDefaultState();
			// for (int s = -1; s < 2; s++)
			if (!isTowerEntrance || (dist != 0 && dist != 2) || addY != 3) {
				if (!tower.isUnderwater || addY != 2 || (dist != 1 && dist != distance - 1)) {
					setStateWithOffset(dist, addY, 0,
						(dist != 1 && dist != distance - 1) || addY != 2 ||
						(rand.nextInt(tower.difficulty) != 0) ? tower.air : torch);
				} else {
					setStateWithOffset(dist, addY, 0, tower.air);
					if (rand.nextInt(4) == 3)
						setStateWithOffset(dist, addY + 2, 0, torch);
				}
			}
		}

		private MTPuzzle getMtp() {
			MTPuzzle newMtp = null;
			int dIndex = dirAxis != 0 ? dirAxis == 1 ? 2 : 1 : 0;
			int dist = 2;
			EnumFacing facing = null;
			Path toPath = null;
			List<EnumFacing> dirs = getDirsList(false, true, true);
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
			//while (iterator.hasNext() && (facing = (EnumFacing) iterator.next()) != null && newMtp == null) {
			if (getMaxDistance(tower, this.dir, 4, false, ix, iy, iz) == 4) {
				// toPath.hasMtp = true;
				// if (toPath.depth + 1 < this.depth)
				newMtp = getRandomMtp(tower, this.parent, this, this.dir, dist, ix,
					iy + 1, iz, 0);
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
			//}
			return newMtp;
		}

		private MTPuzzle getRandomMtp(MazeTower tower, Path fromPath,
				Path toPath, EnumFacing dir, int distance, int x, int y, int z, int failCount) {
			MTPuzzle mtp = null;
			EnumFacing pathDir;
			int mtpChance = rand.nextInt(6);
			int offsetX;
			int offsetZ;
			int[] offsets;
			
			switch (mtpChance) {
				case 0:
					EnumFacing subDir = getFreeSpaceDir(x, y, z);
					if (subDir != null)
						mtp = new MTPWindow(tower, fromPath, toPath, subDir, distance, x,
							y, z);
					break;
				case 1:
					mtp = new MTPArrow(tower, fromPath, toPath, toPath.dir, distance, x,
						y, z);
					break;
				case 2:
					mtp = new MTPArrowGauntlet(tower, fromPath, toPath, toPath.dir,
						distance, x, y, z);
					break;
				case 3:
					if (floor != 1 && getStateAt(tower, x, y - 2, z) == tower.air)
						mtp = new MTPBounceTrap(tower, fromPath, toPath, toPath.dir,
							distance, x, y, z);
					break;
				case 4:
					if (floor != 1) {
						boolean isNonFatal;
						pathDir = toPath.dir;
						offsetX = -1;
						offsetZ = 1;//rand.nextInt(toPath.distance - 1) + 1;
						offsets = MTPuzzle.getRotatedOffsets(offsetX + 1, offsetZ,
							EnumFacing.SOUTH, pathDir);
						
						if (getStateAt(tower, x + offsets[0], y - 2,
							z + offsets[1]) == tower.air && ((isNonFatal = getStateAt(tower,
							x + offsets[0], y - 6, z + offsets[1]) == tower.air) ||
							!(isNonFatal = isPosAccessible(x + offsets[0], y - 6,
							z + offsets[1]))) && (isNonFatal || tower.difficulty > 3)) {
							/*if (!isNonFatal) {
								setStateAt(x + offsets[0], y, z + offsets[1],
									Blocks.redstone_block.getDefaultState());
								setStateAt(x + offsets[0], y - 6, z + offsets[1],
									Blocks.redstone_block.getDefaultState());
							}*/
							offsets = MTPuzzle.getRotatedOffsets(offsetX, offsetZ,
								EnumFacing.SOUTH, pathDir);
							mtp = new MTPFallTrap(tower, fromPath, toPath, pathDir,
								distance, x, y, z, offsets[0], offsets[1], isNonFatal);
						}
					}
					break;
				case 5:
					boolean isLeft = rand.nextBoolean();
					boolean switchDir = false;
					boolean useMtp = true;
					offsetX = -1;
					offsetZ = 0;
					offsets = MTPuzzle.getRotatedOffsets(offsetX, offsetZ,
						EnumFacing.SOUTH, dir);
					int[] coords = new int[] { y, z + offsets[1], x + offsets[0] };
					IBlockState curState = getStateAt(tower, coords);
					while (useMtp && ((curState != null && curState.getBlock() instanceof BlockItemScanner) ||
						getStateWithOffset(tower, isLeft ? dir.rotateYCCW() : dir.rotateY(), 2,
						coords) != null)) {
						if (!switchDir) {
							isLeft = !isLeft;
							switchDir = true;
						} else
							useMtp = false;
					}
					if (useMtp)
						mtp = new MTPItem(tower, fromPath, toPath, dir, distance, x,
							y, z, isLeft);
					break;
				case 6:
					mtp = new MTPPiston(tower, fromPath, toPath, dir, distance, x,
							y, z);
					break;
				case 7:
					break;
				default:
					//mtp = new MTPArrowGauntlet(tower, fromPath, toPath, toPath.dir, distance, x,
						//	y, z);
					//mtp = new MTPWindow(tower, fromPath, toPath, dir, distance, x,
							//y, z);
					//mtp = new MTPPiston(tower, fromPath, toPath, dir, distance, x,
						//y, z);
					
			}
			return (mtp != null && mtp.dir != null) || failCount++ >= 5 ? mtp :
				getRandomMtp(tower, fromPath, toPath, dir, distance, x, y, z, failCount);
		}
		
		private EnumFacing getFreeSpaceDir(int x, int y, int z) {
			ArrayList<EnumFacing> dirsList = new ArrayList<EnumFacing>();
			//dirsList.add(EnumFacing.WEST);//
			dirsList = Path.getDirsList(false, true, true);
			//dirsList.add(null);
			dirsList.remove(dir);
			Iterator dirIterator = dirsList.iterator();
			EnumFacing facing = null;
			Path path;
			while (dirIterator.hasNext() && ((facing = (EnumFacing) dirIterator.next()) == null ||
				getStateWithOffset(tower, facing, 2, x, y, z) != null));
			
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

		private Room(MazeTower tower, int floor, RoomConn conn) {
			this.tower = tower;
			isEntrance = conn == null;
			this.floor = floor;
			width = !isEntrance ? (int) Math.max((rand.nextGaussian() * 2) + 6,
					3) : (int) Math.max((rand.nextGaussian()) + 3, 2) * 2;
			depth = !isEntrance ? (int) Math.max((rand.nextGaussian() * 2) + 6,
					3) : (int) Math.max((rand.nextGaussian()) + 3, 2) * 2;
			height = (int) Math.max(Math.max((rand.nextGaussian() * 1.5), 0),
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
			toRoom = new Room(from.tower, getFloor(from), getConn(from).get(0));
		}

		private int getFloor(Room from) {
			return from.floor;
		}

		private List<RoomConn> getConn(Room from) {
			return from.conns;
		}
	}

	private static class MTPDoor extends MTPuzzle {

		private MTPDoor(MazeTower tower, Path fromPath, Path toPath,
				EnumFacing dir, int distance, int x, int y, int z) {

			super(tower, fromPath, toPath, dir, distance, x, y, z, 0, 0, 1,
					Blocks.red_sandstone.getDefaultState(), tower.wallBlock,
					Blocks.lapis_block.getDefaultState(), true);
		}

	}

	private static class MTPWindow extends MTPuzzle {

		private MTPWindow(MazeTower tower, Path fromPath, Path toPath,
				EnumFacing dir, int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance, x, y, z,
				xOffset = 2, zOffset = 2, 1, null, null, null, false);
			IBlockState stone = Blocks.stone.getDefaultState();
			IBlockState lever = Blocks.lever.getDefaultState()
				.withProperty(BlockLever.FACING, (dir.getAxis() == Axis.Z ?
				EnumOrientation.DOWN_Z : EnumOrientation.DOWN_X));
			IBlockState redstone = Blocks.redstone_wire.getDefaultState();
			if ((((toPath.getDir().getIndex() - 2) - (dirIndex - 2)) % 4) == 2) {
				setXOffset(-2);
				setZOffset(-2);
			}
			//if (dirAxis == 2)
				stateMap = new IBlockState[][][] {
					{
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, doorStates[toPath.getDir().getOpposite().getIndex() % 4], null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null }
					}, {
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, doorStates[(toPath.getDir().getIndex() % 2) == -1 ? 4 : 5], null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null }
					}, {
						{ null, null, null, null, null },
						{ null, null, stone, null, null },
						{ null, null, tower.air, null, null },
						{ null, null, stone, null, null },
						{ null, null, null, null, null }
					}
				};
			stateMap = getRotatedStateMap(stateMap, EnumFacing.SOUTH, toPath.getDir(), true);
			if (dirAxis == 1) {
				stateMap[0][dirSign == 0 ? 0 : 3][2] = stone;
				stateMap[0][dirSign == 0 ? 1 : 4][2] = stone;
				stateMap[1][dirSign == 0 ? 0 : 3][2] = stone;
				stateMap[1][dirSign == 0 ? 1 : 4][2] = redstone;
				stateMap[2][dirSign == 0 ? 0 : 3][2] = Blocks.glass.getDefaultState();
				stateMap[2][dirSign == 0 ? 1 : 4][2] = lever;
			} else {
				stateMap[0][2][dirSign == 0 ? 0 : 3] = stone;
				stateMap[0][2][dirSign == 0 ? 1 : 4] = stone;
				stateMap[1][2][dirSign == 0 ? 0 : 3] = stone;
				stateMap[1][2][dirSign == 0 ? 1 : 4] = redstone;
				stateMap[2][2][dirSign == 0 ? 0 : 3] = Blocks.glass.getDefaultState();
				stateMap[2][2][dirSign == 0 ? 1 : 4] = lever;
			}
			validateStateMap(stateMap, false);
		}

	}
	
	private static class MTPPiston extends MTPuzzle {
		private MTPPiston(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing dir, int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance, x, y, z,
				xOffset = 0, zOffset = 1, 2, null, null, null, false);
			int[] backCoords;
			boolean canAccessBack = true;/*Path.getStateWithOffset(tower, dir.rotateY(), 2,
				(backCoords = new int[] { x + xOffset, y + 2, z + zOffset + 1 })) == air*/
			IBlockState stone = Blocks.stone.getDefaultState();
			IBlockState piston = MazeTowers.BlockMemoryPiston.getDefaultState()
				.withProperty(BlockPistonBase.FACING, dir.rotateY());
			IBlockState lever = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING,
				EnumOrientation.byMetadata(6 - (canAccessBack ? dir.rotateYCCW() : dir.getOpposite()).getIndex()));
			IBlockState button = ModBlocks.hiddenButton.getDefaultState()
				.withProperty(BlockButton.FACING, (canAccessBack ? dir.rotateYCCW() : dir.getOpposite()));
			fromPath.setStateAt(x, y + 2, z, Blocks.brick_block.getDefaultState());
			stateMap = getRotatedStateMap(new IBlockState[][][] {
				{
					{ stone, piston, null },
					{ null, null, null }
				}, {
					{ stone, piston, null },
					{ null, null, null }
				}, {
					{ stone, stone, canAccessBack ? lever : null },
					{ null, !canAccessBack ? lever : null, null }
				}
			}, EnumFacing.SOUTH, dir, true);
			
			/*if (!canAccessBack) {
				EnumFacing facing = null;
				List<EnumFacing> dirsList = Path.getDirsList(false, true, true);
				Iterator dirIterator;
				dirsList.remove(dir.rotateY());
				dirIterator = dirsList.iterator();
				while (!canAccessBack && dirIterator.hasNext() &&
					(facing = (EnumFacing) dirIterator.next()) != null) {
					Path path;
					if ((path = Path.getPathWithOffset(tower, null, facing,
						1, backCoords)) != null && path.mazeDepth <= fromPath.mazeDepth) {
						canAccessBack = true;
					}
				}
			}*/
			
			validateStateMap(stateMap, false);
		}
	}
	
	private static class MTPArrow extends MTPuzzle {
		private MTPArrow(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing dir, int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance, x, y, z,
				xOffset = -1, zOffset = rand.nextInt(toPath.distance - 1) + 1, 1,
				null, null, null, false);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			boolean isLeft = rand.nextBoolean();
			IBlockState dispenser = Blocks.dispenser.getDefaultState()
				.withProperty(BlockDispenser.FACING, (isLeft ? dir.rotateY() : dir.rotateYCCW()));
			IBlockState pressurePlate = MazeTowers.BlockHiddenPressurePlateWeighted.getDefaultState();
			stateMap = getRotatedStateMap(new IBlockState[][][] {
				{
					{ (isLeft ? null : dispenser), pressurePlate, (isLeft ? dispenser : null) }
				}
			}, EnumFacing.SOUTH, dir, true);
			validateStateMap(stateMap, false);
		}
	}
	
	private static class MTPArrowGauntlet extends MTPuzzle {
		private MTPArrowGauntlet(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing dir, int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance, x, y, z,
				xOffset = 0, zOffset = 0, 2, null, null, null, false);
			int delay = tower.difficulty < 3 ? 3 : tower.difficulty < 6 ? 2 : 1;
			IBlockState stone = Blocks.stone.getDefaultState();
			IBlockState dispenser = Blocks.dispenser.getDefaultState()
				.withProperty(BlockDispenser.FACING, EnumFacing.DOWN);
			IBlockState button = ModBlocks.hiddenButton.getDefaultState()
					.withProperty(BlockButton.FACING, EnumFacing.DOWN);
			IBlockState repeaterA = Blocks.unpowered_repeater.getDefaultState()
					.withProperty(Blocks.unpowered_repeater.FACING, dir.getOpposite())
					.withProperty(Blocks.unpowered_repeater.DELAY, delay);
			IBlockState repeaterB = Blocks.unpowered_repeater.getDefaultState()
					.withProperty(Blocks.unpowered_repeater.FACING, dir)
					.withProperty(Blocks.unpowered_repeater.DELAY, 3);
			IBlockState repeaterC = Blocks.unpowered_repeater.getDefaultState()
				.withProperty(Blocks.unpowered_repeater.FACING,
				dirAxis == 2 ? dir.rotateYCCW() : dir.rotateY())
				.withProperty(Blocks.unpowered_repeater.DELAY, 3);
			dirSign = dirIndex % 2 == 0 ? -1 : 1;
			//if (dir != EnumFacing.EAST && dir != EnumFacing.NORTH)
				//dirSign *= -1;
			stateMap = new IBlockState[5][3][toPath.distance + 1];
			stateMap[0][0][0] = doorStates[dir.getOpposite().getIndex() % 4]; stateMap[0][0][1] = null;
			stateMap[0][1][0] = null; stateMap[0][1][1] = null;
			stateMap[0][2][0] = null; stateMap[0][2][1] = null;
			stateMap[1][0][0] = doorStates[(dirIndex % 2) == -1 ? 4 : 5]; stateMap[1][0][1] = button;
			stateMap[1][1][0] = null; stateMap[1][1][1] = null;
			stateMap[1][2][0] = null; stateMap[1][2][1] = null;
			stateMap[2][0][0] = button; stateMap[2][0][1] = stone;
			stateMap[2][1][0] = null; stateMap[2][1][1] = null;
			stateMap[2][2][0] = null; stateMap[2][2][1] = null;
			stateMap[3][0][0] = stone; stateMap[3][0][1] = stone;
			stateMap[3][1][0] = null; stateMap[3][1][1] = null;
			stateMap[3][2][0] = null; stateMap[3][2][1] = null;
			stateMap[4][0][0] = redstone; stateMap[4][0][1] = repeaterA;
			stateMap[4][1][0] = redstone; stateMap[4][1][1] = null;
			stateMap[4][2][0] = redstone; stateMap[4][2][1] = repeaterB;
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
			stateMap[4][0][2] = Blocks.melon_block.getDefaultState();
			stateMap[4][0][len] = redstone;
			stateMap[4][1][len] = repeaterC;
			stateMap[4][2][len] = redstone;
			stateMap = getRotatedStateMap(stateMap, EnumFacing.EAST, dir, false);
			validateStateMap(stateMap, true);
		}
	}
	
	private static class MTPFallTrap extends MTPuzzle {
		
		protected boolean isFatal;
		
		private MTPFallTrap(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing dir, int distance, int x, int y, int z,
			int xOffset, int zOffset, boolean isNonFatal) {
			super(tower, fromPath, toPath, dir, distance, x, y - (isNonFatal ? 3 : 1), z,
				xOffset, zOffset, 1, null, null, null, false);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			boolean isLeft = rand.nextBoolean();
			IBlockState pistonA = MazeTowers.BlockMemoryPiston.getDefaultState()
				.withProperty(BlockMemoryPistonBase.FACING,
				(isLeft ? dir.rotateYCCW() : dir.rotateY()));
			IBlockState pistonB = ((tower.difficulty >= 5) ?
				MazeTowers.BlockMemoryPistonOff : MazeTowers.BlockMemoryPiston)
				.getDefaultState().withProperty(BlockMemoryPistonBase.FACING,
				(isLeft ? dir.rotateY() : dir.rotateYCCW()));
			IBlockState pressurePlate =
				MazeTowers.BlockHiddenPressurePlateWeighted.getDefaultState();
			setXOffset(xOffset);
			setZOffset(zOffset);
			isFatal = !isNonFatal;
			stateMap = getRotatedStateMap(isNonFatal ? new IBlockState[][][] {
				{
					{ isLeft ? pistonA : tower.air, tower.ceilBlock, isLeft ? tower.air : pistonA }
				},
				{
					{ null, pressurePlate, null }
				},
				{
					{ isLeft ? tower.air : pistonB, null, isLeft ? pistonB : tower.air }
				},
				{
					{ null, pressurePlate, null }
				}
			} : new IBlockState[][][] {
				{
					{ isLeft ? tower.air : pistonB, null, isLeft ? pistonB : tower.air }
				},
				{
					{ null, pressurePlate, null }
				}
			}, EnumFacing.SOUTH, dir, true, false);
			validateStateMap(stateMap, false);
		}
	}
	
	private static class MTPBounceTrap extends MTPuzzle {
		
		private MTPBounceTrap(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing dir, int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance, x, y - 2, z,
				xOffset = 0, zOffset = rand.nextInt(toPath.distance - 1) + 1, 1, null, null, null, false);
			dirSign = (dirIndex % 2 == 0 ? -2 : 1);
			IBlockState piston = MazeTowers.BlockMemoryPiston.getDefaultState()
				.withProperty(BlockMemoryPistonBase.FACING,
				EnumFacing.UP);
			IBlockState pressurePlate = MazeTowers.BlockHiddenPressurePlateWeighted.getDefaultState();
			IBlockState web = Blocks.web.getDefaultState();
			stateMap = getRotatedStateMap(new IBlockState[][][] {
				{
					{ piston }
				},
				{
					{ null }
				},
				{
					{ pressurePlate }
				},
				{
					{ null }
				},
				{
					{ (rand.nextInt(10) + 1) < tower.difficulty  ? web : null }
				}
			}, EnumFacing.SOUTH, dir, true);
			validateStateMap(stateMap, false);
		}
	}
	
	public static class MTPItem extends MTPuzzle {

		private MTPItem(MazeTower tower, Path fromPath, Path toPath,
				EnumFacing dir, int distance, int x, int y, int z, boolean isLeft) {
			super(tower, fromPath, toPath, dir, distance, x, y, z,
				xOffset = -1, zOffset = 1, 3, null, null, null, false);
			int difficulty = tower.difficulty + (int) Math.floor(toPath.floor) / 5;
			IBlockState stone = Blocks.stone.getDefaultState();
			IBlockState scanner = (difficulty  < 6 || rand.nextInt((difficulty) - 4) == 0 ?
				MazeTowers.BlockItemScanner : MazeTowers.BlockItemScannerGold)
				.getDefaultState().withProperty(BlockItemScanner.FACING, dir.getOpposite());
			IBlockState sign = Blocks.wall_sign.getDefaultState()
				.withProperty(BlockWallSign.FACING, dir.getOpposite());
			IBlockState button = MazeTowers.BlockHiddenButton.getDefaultState()
				.withProperty(BlockButton.FACING, dir);
			
			stateMap = new IBlockState[][][] {
				{
					{ null, null, null },
					{ null, doorStates[dir.getOpposite().getIndex() % 4], null },
					{ null, null, null }
				}, {
					{ isLeft ? null : scanner, null, isLeft ? scanner : null },
					{ null, doorStates[(dir.getOpposite().getIndex() % 2) == -1 ? 4 : 5], null },
					{ null, null, null }
				}, {
					{ null, sign, null },
					{ null, stone, null },
					{ null, button, null }
				}
			};
			
			stateMap = getRotatedStateMap(stateMap, EnumFacing.SOUTH, dir, true);
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
		private final int mx;
		private final int mz;
		private final int ox;
		private final int oz;
		private final int mazeDepthValue;
		protected final int dirIndex;
		protected final int dirAxis;
		protected final Path oPath;
		protected final IBlockState[] leverStates;
		protected final IBlockState[] doorStates;
		protected int dirSign;
		protected EnumFacing dir;
		protected IBlockState[][][] stateMap;
		protected static int xOffset;
		protected static int zOffset;
		protected static final IBlockState redstone = Blocks.redstone_wire.getDefaultState();
		private IBlockState obstacleState;
		private IBlockState medianState;
		private IBlockState triggerState;
		private int distance;
		private boolean hasRedstone = false;

		private MTPuzzle(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing facing, int distance, int x, int y, int z, int xOffset, int zOffset,
			int mazeDepthValue, IBlockState obstacle, IBlockState median,
			IBlockState trigger, boolean hasRedstone) {
			dir = facing;
			dirIndex = dir.getIndex();
			dirAxis = (int) Math.floor(dirIndex * 0.5);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
			this.mazeDepthValue = mazeDepthValue;
			Block lever = Blocks.lever;
			Block door = tower.doorBlock;
			leverStates = new IBlockState[] {
				lever.getDefaultState().withProperty(BlockLever.FACING,
				BlockLever.EnumOrientation.NORTH),
				lever.getDefaultState().withProperty(BlockLever.FACING,
				BlockLever.EnumOrientation.SOUTH),
				lever.getDefaultState().withProperty(BlockLever.FACING,
				BlockLever.EnumOrientation.WEST),
				lever.getDefaultState().withProperty(BlockLever.FACING,
				BlockLever.EnumOrientation.EAST),
				lever.getDefaultState().withProperty(BlockLever.FACING,
				BlockLever.EnumOrientation.UP_X) };
			doorStates = new IBlockState[] {
				door.getDefaultState().withProperty(BlockDoor.FACING,
						EnumFacing.WEST),
				door.getDefaultState().withProperty(BlockDoor.FACING,
						EnumFacing.EAST),
				door.getDefaultState().withProperty(BlockDoor.FACING,
						EnumFacing.NORTH),
				door.getDefaultState().withProperty(BlockDoor.FACING,
						EnumFacing.SOUTH),
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
				door.getDefaultState()
						.withProperty(BlockDoor.HINGE,
								EnumHingePosition.RIGHT)
						.withProperty(BlockDoor.HALF,
								BlockDoor.EnumDoorHalf.UPPER) };
			this.tower = tower;
			this.stateMap = new IBlockState[1][1][1];
			this.distance = distance;
			this.x = x;
			this.y = y;
			this.z = z;
			this.xOffset = xOffset;
			this.zOffset = zOffset;
			// Path oPath = fromPath.getPathAt(x, y, z);
			this.fromPath = fromPath;
			this.toPath = toPath;
			this.oPath = fromPath.getPathAt(tower, fromPath, x, y, z);
			mx = x + ((int) dirAxis == 2 ? dirSign : 0);
			mz = z + ((int) dirAxis == 1 ? dirSign : 0);
			ox = x + ((int) dirAxis == 2 ? dirSign * 2 : 0);
			oz = z + ((int) dirAxis == 1 ? dirSign * 2 : 0);
			this.obstacleState = obstacle;
			this.medianState = median;
			this.triggerState = trigger;
			
			if (median != null) {
				tower.blockData[y + 1][mz][mx] = median;
				tower.blockData[y + 2][mz][mx] = tower.wallBlock;
			}

			if (hasRedstone) {
				/*
				 * for (int r = 1; r < distance; r++) {
				 * 
				 * }
				 */
				tower.blockData[y][z][x] = Blocks.redstone_wire
						.getDefaultState();
				/*
				 * tower.blockData[y][z - ((int) fromPath.dirAxis == 1 ? dirSign
				 * : 0)] [x - ((int) fromPath.dirAxis == 2 ? dirSign : 0)] =
				 * Blocks.redstone_wire.getDefaultState();
				 */
				tower.blockData[y][mz][mx] = Blocks.redstone_wire
						.getDefaultState();
			}

			if (trigger != null)
				/*
				 * tower.blockData[y][z + ((dirAxis == 1) ? distance * dirSign :
				 * 0)] [x + ((dirAxis == 2) ? distance * dirSign : 0)] =
				 * trigger;
				 */
				tower.blockData[y][toPath.iz][toPath.ix] = leverStates[4];

			if (obstacle != null) {
				tower.blockData[y][oz][ox] = doorStates[(dir.getIndex()) % 4];
				tower.blockData[y + 1][oz][ox] = doorStates[(oPath.dir.getIndex() % 2) == -1 ? 4 : 5];
				tower.blockData[y + 2][oz][ox] = tower.wallBlock;
			}
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
		
		protected static int[] getRotatedOffsets(int offsetX, int offsetZ,
			EnumFacing fromDir, EnumFacing toDir) {
			int[] offsets = new int[] { offsetX, offsetZ };
			if (fromDir != toDir) {
				if (fromDir.getAxis() == toDir.getAxis()) {
					offsets[0] *= -1;
					offsets[1] *= -1;
				} else {
					if (fromDir.getAxisDirection() == toDir.getAxisDirection()) {
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
		
		protected IBlockState[][][] getRotatedStateMap(IBlockState[][][] map, EnumFacing fromDir,
				EnumFacing toDir, boolean reorder) {
			return getRotatedStateMap(map, fromDir, toDir, reorder, true);
		}
		
		protected IBlockState[][][] getRotatedStateMap(IBlockState[][][] map, EnumFacing fromDir,
			EnumFacing toDir, boolean reorder, boolean rotateOffsets) {
			IBlockState[][][] newMap;
			if (fromDir != toDir) {
				int ry, rz, rx, ryMax = map.length - 1, rzMax = map[0].length - 1, rxMax = map[0][0].length - 1;
				if (fromDir.getAxis() == toDir.getAxis()) {
					newMap = new IBlockState[map.length][map[0].length][map[0][0].length];
					for (ry = 0; ry <= ryMax; ry++) {
						for (rz = 0; rz <= rzMax; rz++) {
							for (rx = 0; rx <= rxMax; rx++)
								newMap[ry][reorder && dirAxis == 2 ? rzMax - rz : rz]
									[reorder && dirAxis == 1 ? rxMax - rx : rx] = map[ry][rz][rx];
						}
					}
					
					if (rotateOffsets) {
						setXOffset(xOffset * -1);
						setZOffset(zOffset * -1);
					}
				} else {
					newMap = new IBlockState[map.length][map[0][0].length][map[0].length];
					if (fromDir.getAxisDirection() == toDir.getAxisDirection()) {
						for (ry = 0; ry <= ryMax; ry++) {
							for (rz = 0; rz <= rzMax; rz++) {
								for (rx = 0; rx <= rxMax; rx++)
									newMap[ry][reorder && dirAxis == 2 ? rxMax - rx : rx]
										[reorder && dirAxis == 1 ? rzMax - rz : rz] = map[ry][rz][rx];
							}
						}
						
						if (rotateOffsets) {
							int temp = xOffset;
							setXOffset(zOffset);
							setZOffset(temp);
						}
					} else {
						for (ry = 0; ry <= ryMax; ry++) {
							for (rz = 0; rz <= rzMax; rz++) {
								for (rx = 0; rx <= rxMax; rx++)
									newMap[ry][reorder && dirAxis == 2 ? rxMax - rx : rx]
										[reorder && dirAxis == 1 ? rzMax - rz : rz] = map[ry][rz][rx];
							}
						}
						
						if (rotateOffsets) {
							int temp = xOffset * -1;
							setXOffset(zOffset * -1);
							setZOffset(temp);
						}
					}
				}
			} else
				newMap = map.clone();
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
							int xCoord = x + (x2 * sign) + xOffset, yCoord = y + y2,
								zCoord = z + (z2 * sign) + zOffset;
							if (state != null) {
								/*if (state.getBlock() == Blocks.lever)
									state = getLeverState(x2, y2, z2);
								else {*/
								Block block = state.getBlock();
								if (block == Blocks.dispenser) {
									if (Path.isPosValid(tower, xCoord, yCoord, zCoord))
										tower.dataMap[yCoord][zCoord][xCoord] = (randInt != -1) ? randInt :
											(randInt = (rand.nextInt(9) + 1 + (Math.max(tower.difficulty - 4, 0))));
								} else if (block ==
									MazeTowers.BlockHiddenPressurePlateWeighted &&
									this instanceof MTPFallTrap) {
									boolean isFatal = ((MTPFallTrap) this).isFatal;
									if ((isFatal && y2 == 1) || (!isFatal && y2 == 3))
										fromPath.addFallTrapHole(xCoord, zCoord, isFatal);
								} else if (block instanceof BlockItemScanner)
									tower.pathMap[yCoord][zCoord][xCoord] = toPath.pathIndex;
								fromPath.setStateAt(xCoord, yCoord, zCoord, state);
								//}
							}
						}
					}
				}
				//fromPath.setStateAt(x, y + 2, z, Blocks.emerald_block.getDefaultState());
				//fromPath.setStateAt(x + xOffset, y + 2, z + zOffset, Blocks.lapis_block.getDefaultState());
				fromPath.setStateAt(fromPath.fx, fromPath.fy + 4, fromPath.fz, Blocks.gold_block.getDefaultState());
				fromPath.setStateAt(toPath.fx, toPath.iy + 4, toPath.fz, Blocks.diamond_block.getDefaultState());
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
			
			return this;
		}
		
		protected void validateStateMap(IBlockState[][][] map, boolean checkTopSpace) {
			int sign = dirSign;
			int[] minCoords;
			int[] maxCoords;
			try {
			if (!Path.isPosValid(tower, (maxCoords = new int[] { y + (map.length - 1),
				z + ((map[0].length - 1) * dirSign) + zOffset,
				x + ((map[0][0].length - 1) * dirSign) + xOffset })) ||
				!Path.isPosValid(tower, (minCoords = new int[] { y, z + zOffset, x + xOffset })) ||
				(checkTopSpace && (Path.getStateAt(tower, minCoords[2], maxCoords[0],
					minCoords[1]) != tower.air || Path.getStateAt(tower, maxCoords[2],
					maxCoords[0], minCoords[1]) != tower.air || Path.getStateAt(tower,
					minCoords[2], maxCoords[0], maxCoords[1]) != tower.air ||
					Path.getStateAt(tower, maxCoords) != tower.air)))
				setDir(null);
			} catch (ArrayIndexOutOfBoundsException e) {
				e = null;
			}
		}

		private IBlockState getLeverState(int x, int y, int z) {
			IBlockState state = null;
			
			return state;
		}
	}
}