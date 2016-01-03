package com.samuel.mazetowers.worldgen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.etc.MazeTowersData;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.tileentities.TileEntityCircuitBreaker;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenMazeTowers implements IWorldGenerator {

	private static final Random rand = new Random();
	private static final IBlockState air = Blocks.air.getDefaultState();
	private Map<Integer, Map<Integer, IBlockState>> chunkData = null;
	private Multimap<Integer, ArrayDeque<Integer>> chunkChest = null;
	private Multimap<Integer, BlockPos> specialBlocks = ArrayListMultimap
			.create();
	private List<BlockPos> specialChoices = new ArrayList<BlockPos>();
	private List<MazeTower> towers;
	private int genCount = 1;
	private int chunksGenerated = 0;
	private int[] chunkDistanceX = new int[genCount];
	private int[] blockDistanceY = new int[genCount];
	private int[] chunkDistanceZ = new int[genCount];
	private World curWorld;
	public BlockPos[] spawnPos = new BlockPos[genCount];
	private boolean[] spawnPosLoaded = new boolean[genCount];
	private boolean[] generated = new boolean[genCount];
	private Map<BlockPos, IBlockState>[] data = new Map[genCount];
	private MazeTowersData MazeTowerData;
	private ChestGenHooks info = ChestGenHooks.getInfo("MazeTowerChest");
	private ChestGenHooks info_high = ChestGenHooks.getInfo("MazeTowerChest+");

	public WorldGenMazeTowers() {
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimensionId() != 0
				|| world.provider.getDimensionId() == 0)
			return;
		for (int g = 0; g < genCount; g++) {
			loadOrCreateData(world, g);
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

	public boolean getGenerated(World worldIn, int index) {
		loadOrCreateData(worldIn, index);
		return generated[index];
	}

	public BlockPos getSpawnPos(World worldIn, int index) {
		loadOrCreateData(worldIn, index);
		return spawnPos[index];
	}

	public List<MazeTower> getTowers() {
		return towers;
	}

	protected final void loadOrCreateData(World world, int index) {
		if (this.MazeTowerData == null
				|| curWorld == null
				|| (world.getWorldInfo().getSeed() != 0 && curWorld
						.getWorldInfo().getSeed() != world.getWorldInfo()
						.getSeed())) {
			curWorld = world;
			this.MazeTowerData = (MazeTowersData) world.loadItemData(
					MazeTowersData.class, "MazeTowers");
			if (this.MazeTowerData == null) {
				this.MazeTowerData = new MazeTowersData("MazeTowers");
				world.setItemData("MazeTowers", MazeTowerData);
				this.generated[index] = false;
				this.spawnPosLoaded[index] = false;
				this.chunksGenerated = 0;
				this.chunkDistanceX[index] = 0;
				this.blockDistanceY[index] = 0;
				this.chunkDistanceZ[index] = 0;
				this.spawnPos[index] = new BlockPos(-1, 1, -1);
				this.towers = new ArrayList<MazeTower>();
			} else {
				this.generated[index] = MazeTowerData.getIsGenerated(index);
				this.spawnPos[index] = MazeTowerData.getSpawnPoint(index);
				IBlockState spawnBlock;
				/*
				 * if ((spawnBlock = world.getBlockState(this.spawnPos)) !=
				 * chaos_unbreakable) { this.generated[index] = false;
				 * MazeTowerData.setIsGenerated(false, index);
				 * this.spawnPosLoaded[index] = false; this.chunksGenerated = 0;
				 * this.chunkDistanceX[index] = 0; this.blockDistanceY[index] =
				 * 0; this.chunkDistanceZ[index] = 0; this.specialChoices = new
				 * ArrayList<BlockPos>(); this.specialBlocks =
				 * ArrayListMultimap.create(); } else {
				 */
				chunksGenerated++;
				this.spawnPosLoaded[index] = true;
				// }
			}
		}
	}

	public void rebuild(World worldIn, int index) {
		loadOrCreateData(worldIn, index);
		((MazeTower) towers.get(index)).build(worldIn);
	}

	public MazeTowersData getData() {
		return this.MazeTowerData;
	}

	public void spawn(World world, int ix, int iy, int iz, int chunk) {
	}

	public void addTower(World worldIn, int x, int z, boolean build) {
		loadOrCreateData(worldIn, towers.size());
		int px = (x << 4) + 8;
		int pz = (z << 4) + 8;
		int py = MTUtils.getSurfaceY(worldIn, px, pz, 3) - 1;
		int difficulty;
		BiomeGenBase biome = worldIn.getBiomeGenForCoords(new BlockPos(px, py, pz));
		String biomeName = biome.biomeName;
		IBlockState ceilBlock = null, wallBlock = null,
			wallBlock_external = null, floorBlock = null;
		IBlockState[] stairsBlock = new IBlockState[4];
		Block doorBlock = Blocks.iron_door;
		if (biomeName == "Ocean" || biomeName == "Deep Ocean" ||
			biomeName == "Beach" || biomeName == "Cold Beach" ||
			biomeName == "Stone Beach" || biomeName == "River" ||
			biomeName == "Mushroom Island Shore")
			return;
		else {
			int typeChance = rand.nextInt(36);//rand.nextInt(128);
			if (typeChance < 36) {
				if (typeChance < 18) {
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
				} else if (typeChance < 30) {
					ceilBlock = Blocks.quartz_block.getDefaultState();
					wallBlock = Blocks.quartz_block.getStateFromMeta(2);
					floorBlock = Blocks.quartz_block.getStateFromMeta(1);
					stairsBlock = new IBlockState[] {
						Blocks.quartz_stairs.getStateFromMeta(0),
						Blocks.quartz_stairs.getStateFromMeta(2),
						Blocks.quartz_stairs.getStateFromMeta(1),
						Blocks.quartz_stairs.getDefaultState()
					};
					difficulty = 5;
				} else if (typeChance < 34) {
					ceilBlock = Blocks.obsidian.getDefaultState();
					wallBlock = Blocks.obsidian.getDefaultState();
					floorBlock = Blocks.obsidian.getDefaultState();
					stairsBlock = new IBlockState[] {
						Blocks.nether_brick_stairs.getStateFromMeta(0),
						Blocks.nether_brick_stairs.getStateFromMeta(2),
						Blocks.nether_brick_stairs.getStateFromMeta(1),
						Blocks.nether_brick_stairs.getDefaultState()
					};
					difficulty = 6;
				} else {
					ceilBlock = Blocks.bedrock.getDefaultState();
					wallBlock = Blocks.bedrock.getDefaultState();
					floorBlock = Blocks.bedrock.getDefaultState();
					stairsBlock = new IBlockState[] {
						Blocks.nether_brick_stairs.getStateFromMeta(0),
						Blocks.nether_brick_stairs.getStateFromMeta(2),
						Blocks.nether_brick_stairs.getStateFromMeta(1),
						Blocks.nether_brick_stairs.getDefaultState()
					};
					difficulty = 7;
				}
			} else {
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
				} else if (biomeName.startsWith("Savanna")) {
					ceilBlock = Blocks.planks.getStateFromMeta(4);
					wallBlock = Blocks.log.getStateFromMeta(4);
					floorBlock = Blocks.planks.getStateFromMeta(4);
					doorBlock = Blocks.acacia_door;
					stairsBlock = new IBlockState[] {
						Blocks.acacia_stairs.getStateFromMeta(0),
						Blocks.acacia_stairs.getStateFromMeta(2),
						Blocks.acacia_stairs.getStateFromMeta(1),
						Blocks.acacia_stairs.getDefaultState()
					};
					difficulty = 2;
				} else if (biomeName.startsWith("Roofed")) {
					ceilBlock = Blocks.planks.getStateFromMeta(5);
					wallBlock = Blocks.log.getStateFromMeta(5);
					floorBlock = Blocks.planks.getStateFromMeta(5);
					doorBlock = Blocks.dark_oak_door;
					stairsBlock = new IBlockState[] {
						Blocks.dark_oak_stairs.getStateFromMeta(0),
						Blocks.dark_oak_stairs.getStateFromMeta(2),
						Blocks.dark_oak_stairs.getStateFromMeta(1),
						Blocks.dark_oak_stairs.getDefaultState()
					};
					difficulty = 3;
				} else if (biomeName.startsWith("Desert")) {
					ceilBlock = Blocks.sandstone.getDefaultState();
					wallBlock = Blocks.sand.getDefaultState();
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
				}
			}
			
			if (wallBlock_external == null)
				wallBlock_external = wallBlock;
		}
		MazeTower newTower = new MazeTower(x, py, z, ceilBlock, wallBlock,
			wallBlock_external, floorBlock, stairsBlock, doorBlock, difficulty);
		spawnPos[towers.size()] = new BlockPos(newTower.chunkX << 4,
				newTower.baseY + 1, newTower.chunkZ << 4);
		newTower.fillGaps();
		towers.add(newTower);
		if (build)
			newTower.build(worldIn);
	}

	public void recreate(World worldIn, boolean build) {
		loadOrCreateData(worldIn, towers.size());
		for (int t = 0; t < towers.size(); t++) {
			MazeTower tower = towers.get(t);
			final int x = tower.chunkX;
			final int y = tower.baseY;
			final int z = tower.chunkZ;
			towers.set(t, (tower = new MazeTower(x, y, z, tower.ceilBlock,
				tower.wallBlock, tower.wallBlock_external, tower.floorBlock,
				tower.stairsBlock, tower.doorBlock, tower.difficulty)));
			//tower.fillGaps();
			if (build)
				tower.build(worldIn);
		}
	}

	public void initChest(World world, BlockPos pos, boolean isHighChest,
			int index) {
		/*
		 * if (!isHighChest) { TileEntityChest chestTE = (TileEntityChest)
		 * world.getTileEntity(pos); boolean isDoubleChest =
		 * chestTE.adjacentChestXNeg != null || chestTE.adjacentChestXPos !=
		 * null || chestTE.adjacentChestZNeg != null ||
		 * chestTE.adjacentChestZPos != null;
		 * WeightedRandomChestContent.generateChestContents(world.rand,
		 * info.getItems(world.rand), chestTE, info.getCount(world.rand) *
		 * ((!isDoubleChest) ? 1 : 2)); world.setTileEntity(pos, chestTE); }
		 * else { TileEntityChaosChest chestTEC = (TileEntityChaosChest)
		 * world.getTileEntity(pos); boolean isDoubleChaosChest =
		 * chestTEC.adjacentChestXNeg != null || chestTEC.adjacentChestXPos !=
		 * null || chestTEC.adjacentChestZNeg != null ||
		 * chestTEC.adjacentChestZPos != null;
		 * WeightedRandomChestContent.generateChestContents(world.rand,
		 * info_high.getItems(world.rand), chestTEC,
		 * info_high.getCount(world.rand) * ((!isDoubleChaosChest) ? 1 : 2));
		 * world.setTileEntity(pos, chestTEC); }
		 */
	}

	public class MazeTower {

		public final int baseY;
		public final int floors;
		private final int difficulty;
		private final int chunkX;
		private final int chunkZ;
		private final int entranceMinX;
		private final int entranceMinZ;
		private final boolean hasXEntrance;
		private final boolean hasOddX;
		private final boolean hasOddZ;
		private final IBlockState[] stairsBlock;
		private IBlockState[][][] blockData;
		private Path[] floorExitPaths;
		private List<Path> paths;
		private int[][][] pathMap;
		private int[][][] dataMap;
		private int[][][] roomMap;
		private int[] floorHighestDepthLevel;
		private List<Room> rooms;
		public final IBlockState ceilBlock;
		public final IBlockState wallBlock;
		public final IBlockState wallBlock_external;
		public final IBlockState floorBlock;
		public final Block doorBlock;

		private MazeTower(int x, int y, int z, IBlockState ceilBlock,
			IBlockState wallBlock, IBlockState wallBlock_external, IBlockState floorBlock,
			IBlockState[] stairsBlocks, Block doorBlock, int difficulty) {
			chunkX = x;
			chunkZ = z;
			baseY = y;
			floors = 5;//rand.nextInt(5) + 2;
			floorExitPaths = new Path[floors];
			floorHighestDepthLevel = new int[floors];
			this.difficulty = difficulty;
			this.ceilBlock = ceilBlock;
			this.wallBlock = wallBlock;
			this.wallBlock_external = wallBlock_external;
			this.floorBlock = floorBlock;
			this.stairsBlock = stairsBlocks;
			this.doorBlock = doorBlock;
			blockData = getInitialBlockData();
			pathMap = new int[(floors * 6) + 1][16][16];
			dataMap = new int[(floors * 6) + 1][16][16];
			roomMap = new int[floors][16][16];
			paths = new ArrayList<Path>();
			rooms = new ArrayList<Room>();

			final boolean doorCoordIsX = rand.nextBoolean();
			final EnumFacing entranceDir;
			entranceMinX = doorCoordIsX ? 0 : rand.nextInt(6) + 5;
			entranceMinZ = doorCoordIsX ? rand.nextInt(6) + 5 : 0;
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

			hasXEntrance = Math.floor(entranceDir.getIndex() * 0.5) == 2;
			hasOddX = entranceMinX % 2 == 1;
			hasOddZ = entranceMinZ % 2 == 1;

			// Room entrance;
			// entrance = addRoom(new Room(this, 1, null));
			Path entrance = new Path(this, null, new ArrayList<Path>(),
					entranceMinX, 0, entranceMinZ, entranceDir);
			for (int f = 1; f <= floors; f++) {
				if (floorExitPaths[f - 1] != null)
					entrance = floorExitPaths[f - 1].newFloor();
				else
					break;
			}
		}

		public void removeItemEntities(World worldIn) {
			if (!worldIn.isRemote) {
				float posX = (chunkX >> 4) - 32, posY = baseY, posZ = (chunkZ >> 4) - 16;
				List list = worldIn.getEntitiesWithinAABB(EntityItem.class,
					AxisAlignedBB.fromBounds(posX, posY, posZ,
						posX + 32, posY + (floors * 6) + 6, posZ + 16));
				for (int i = 0; i < list.size(); i++) {
					EntityItem item = (EntityItem) list.get(i);
					item.setDead();
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
		
		private void checkPathDepth(Path path, int floor, int depth) {
			if (depth > floorHighestDepthLevel[floor - 1] ||
				(depth == floorHighestDepthLevel[floor - 1] && rand.nextBoolean())) {
				floorExitPaths[floor - 1] = path;
				floorHighestDepthLevel[floor - 1] = depth;
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
    								if (Path.isPosValid(Path.getRelCoordsWithOffset(dir, dist + 2, x, y, z)))
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
			final IBlockState downButton = ModBlocks.quartzButton.getDefaultState()
				.withProperty(BlockButton.FACING, EnumFacing.DOWN);
			boolean addCircuitBreaker = false;
			removeItemEntities(worldIn);
			for (int a = 0; a < 2; a++) {//
				for (int y = 0; y <= yLimit + 6; y++) {
					for (int z = 0; z < 16; z++) {
						for (int x = 0; x < 16; x++) {
							final BlockPos pos = new BlockPos(ix + x + (-16 * a), baseY + y, iz + z);
							IBlockState state = y <= yLimit ? blockData[y][z][x] : null;
							if (a == 0) {//
								boolean isEdge = x == 0 || x == 15 || z == 0 || z == 15;
								boolean isEdgeCorner = isEdge && ((x == 0 || x == 15) && (z == 0 || z == 15));
								if (y <= yLimit) {
									if ((state == null || (state.getBlock() != Blocks.ladder &&
										isEdge && state != air && state.getBlock() != Blocks.torch)) ||
										state == stone) state = isEdge ? wallBlock_external :
										y != yLimit ? wallBlock : floorBlock;
								} else {
									state = (!isEdge && y != yLimit + 6) ||
										(isEdge && ((y < yLimit + 2 || y > yLimit + 4) || isEdgeCorner)) ?
										isEdge ? wallBlock_external : air : glass;
								}
								
							}//
							else {//
								if ((state == air && y % 6 != 5) || (state != null &&
									state.getBlock() == Blocks.torch))//
									state = wallBlock;//
								else if (state == null
									|| (state.getBlock() == wallBlock.getBlock() &&
									y % 6 != 0))//
									state = air;//
							}
							if (state.getBlock() != doorBlock) {
								if (state.getBlock() == Blocks.melon_block) {
									addCircuitBreaker = true;
									state = MTPuzzle.redstone;
								}
									
								worldIn.setBlockState(pos, state);
								if (a == 0 && state.getBlock() == Blocks.dispenser)
									addDispenserArrows(worldIn, pos);
								else if (state == downButton)
									worldIn.setBlockState(pos.up(), wallBlock);
								else if (a == 0 && addCircuitBreaker) {
									worldIn.setTileEntity(pos, new TileEntityCircuitBreaker());
									addCircuitBreaker = false;
								}
							} else {
								if (state.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER)
									ItemDoor.placeDoor(worldIn, pos,
										state.getValue(BlockDoor.FACING),
										doorBlock);
								// worldIn.setBlockState(pos, state, 0);
							}
							final int groundY;
							if (y == 0
								&& (groundY = MTUtils.getGroundY(worldIn,
								ix + x + (-16 * a), baseY, iz + z, 1) + 1) < baseY) {
								for (int y2 = 0; y2 <= baseY - groundY; y2++)
									worldIn.setBlockState(pos.down(y2), floorBlock);
							}
						}
					}
				}
			}//
		}
		
		private void addDispenserArrows(World worldIn, BlockPos pos) {
			TileEntity te;
			int numArrows = dataMap[pos.getY() - baseY][pos.getZ() - (chunkZ << 4)][pos.getX() - (chunkX << 4)];
			if ((te = worldIn.getTileEntity(pos)) != null &&
				te instanceof TileEntityDispenser) {
				 TileEntityDispenser ted = (TileEntityDispenser) te;
				 te = null;
				 LockCode code = new LockCode("free arrows");
				 ted.setLockCode(code);
				 ted.setInventorySlotContents(0, new ItemStack(Items.arrow, numArrows));
				 worldIn.setTileEntity(pos, ted);
			}
			
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

		public void buildRoom(World worldIn) {
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
												1) + 1) < baseY) {
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
		}
	}

	private static class Path {

		private final MazeTower tower;
		private final EnumFacing dir;
		private final boolean isEntrance;
		private final int ix;
		private final int iy;
		private final int iz;
		private final int fx;
		private final int fy;
		private final int fz;
		private final int floor;
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
			isEntrance = parentPath == null;
			isDeadEnd = true;
			depth = !isEntrance ? parent.depth + 1 : 1;
			mazeDepth = !isEntrance ? parent.mazeDepth : 1;
			this.chain.add(this);
			this.ix = ix;
			this.iy = iy;
			this.iz = iz;
			floor = (int) Math.floor(iy / 6) + 1;
			pathIndex = !isEntrance ? tower.paths.get(tower.paths.size() - 1).pathIndex + 1
					: 1;
			pos = new BlockPos(ix, iy, iz);
			children = new ArrayList<Path>();
			reservedDirs = new Stack<EnumFacing>();
			hasMtp = !isEntrance && parent.hasMtp;

			if (facing == null)
				dir = getDir();
			else {
				if (isEntrance)
					dir = facing;
				else
					dir = ((maxDistance = getMaxDistance(tower, facing, 14,
							false, ix, iy + 1, iz)) >= (!hasMtp ? 2 : 4)) ? facing : null;
			}

			if (dir != null) {
				dirAxis = (int) Math.floor(dir.getIndex() * 0.5);
				dirSign = dir.getIndex() % 2 == 0 ? -1 : 1;
				/*
				 * distance = !isEntrance ? Math.max((int)
				 * Math.floor((Math.min(rand.nextInt(maxDistance) + 1,
				 * rand.nextInt(3) + 3)) * 0.5) * 2, !hasMtp ? 2 : 4) : 3;
				 */
				distance = !isEntrance ? Math.max((int) Math
					.floor((rand.nextInt(maxDistance) + 1) * 0.5) * 2,
					!hasMtp ? 2 : 4) : 3;
				int[] fcoords = getRelCoordsWithOffset(dir, distance);
				fx = fcoords[2];
				fy = fcoords[0] - 1;
				fz = fcoords[1];

				tower.addPath(this);
				
				if (!isEntrance && !parent.isDeadEnd)
					parent.isDeadEnd = false;
				
				tower.checkPathDepth(this, floor, depth);

				if (hasMtp) {
					mtp = new ArrayList<MTPuzzle>();
					int mtpCount = 1;// (rand.nextInt(tower.floors) < 3 ? 2 :
										// 3);
					for (int p = 0; p < mtpCount && (mtp.isEmpty() || mtp.get(mtp.size() - 1) != null); p++) {
						MTPuzzle newMtp = getMtp();
						if (newMtp != null) {
							mtp.add(newMtp);
						}/*
						 * else if (p == 0) hasMtp = false;
						 */
					}
					hasMtp = false;
					mazeDepth++;
				} else if (!isEntrance) {
					if (rand.nextInt(7) < 2) {
						this.hasMtp = true;
					}
				}

				for (int y = 1; y < 4; y++) {
					for (int d = 0; d <= distance; d++)
						setPathWithOffset(d, y);
				}
			} else {
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
			EnumFacing ladderDir;
			IBlockState ladder = Blocks.ladder.getDefaultState()
				.withProperty(BlockLadder.FACING, ladderDir = getLadderDir());
			boolean isLastFloor = floor == tower.floors;
			for (int y = 1; y < (!isLastFloor ? 9 : 6); y++) {
				int[] coords = getRelCoordsWithOffset(dir, distance);
				/*if (isPosValid(coords[2], coords[0], coords[1]))
					setStateWithOffset(distance + 1, y, 0, tower.wallBlock);*/
				if (y == 5) {
					setStateAt(coords[2] + 1, fy + 5, coords[1], tower.wallBlock);
					setStateAt(coords[2] - 1, fy + 5, coords[1], tower.wallBlock);
					setStateAt(coords[2] + 1, fy + 5, coords[1] + 1, tower.wallBlock);
					setStateAt(coords[2] - 1, fy + 5, coords[1] + 1, tower.wallBlock);
					setStateAt(coords[2] + 1, fy + 5, coords[1] - 1, tower.wallBlock);
					setStateAt(coords[2] - 1, fy + 5, coords[1] - 1, tower.wallBlock);
					setStateAt(coords[2], fy + 5, coords[1] + 1, tower.wallBlock);
					setStateAt(coords[2], fy + 5, coords[1] - 1, tower.wallBlock);
					setStateAt(coords[2], fy + 5, coords[1], tower.wallBlock);
				}
				setStateWithOffset(distance, y, 0, ladder);
			}
			setStateWithOffset(distance, 9, 0, Blocks.noteblock.getDefaultState());
			if (isLastFloor)
				setStateWithOffset(distance, 6, 0, ladder);
			return !isLastFloor ? new Path(tower, this, chain, fx, fy + 6, fz, null) : null;
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
				!isPosValid(coords = getRelCoordsWithOffset(facing, 1, fx, fy, fz)))) {
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
					&& (isPosValid(getRelCoordsWithOffset(facing, dist, x, y, z)) && getStateWithOffset(tower,
							facing, dist, x, y, z) == null));
			// validPos = isPosValid(getRelCoordsWithOffset(facing, dist - 2, x,
			// y, z));
			return Math.min(dist - 2, limit);
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

			if (isPosValid(x, y, z)) {
				int index = tower.pathMap[y][z][x] - 1;
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

			if (isPosValid(x, y, z))
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

		private static boolean isPosValid(int[] coords) {
			return isPosValid(coords[2], coords[0], coords[1]);
		}

		private static boolean isPosValid(int x, int y, int z) {
			int floor = (y / 6) + 1;
			return ((x > 0 && x < 15) && (z > 0 && z < 15) && (y >= 0 && y < floor * 6));
		}

		private void setStateWithOffset(int dist, int addY, int sideOffset,
				IBlockState state) {
			final int x = ix
					+ (dirAxis != 2 ? dirAxis == 0 ? 0 : sideOffset : dist
							* dirSign);
			final int y = iy + addY + (dirAxis != 0 ? 0 : dist);
			final int z = iz + (dirAxis != 1 ? dirAxis == 0 ? 0 : sideOffset : dist * dirSign);
			final IBlockState curState;
			try {
				curState = tower.blockData[y][z][x];
			} catch(ArrayIndexOutOfBoundsException e) {
				return;
			}
			if (curState == null || curState == air
				|| curState.getBlock() == tower.wallBlock.getBlock() ||
				state.getBlock() == Blocks.ladder)
				tower.blockData[y][z][x] = state;
			if (tower.pathMap[y][z][x] == 0)
				tower.pathMap[y][z][x] = pathIndex;
			if (dist == distance && addY == 3 && sideOffset == 0 && state == air) {
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
					if (newPath != null)
						children.add(newPath);
				}
				// } else {

				// }
			}
		}
		
		private void setStateAt(int x, int y, int z, IBlockState state) {
			IBlockState curState = tower.blockData[y][z][x];
			if (curState == null ||
				(curState.getBlock() != Blocks.lever && curState.getBlock() != MazeTowers.BlockQuartzButton))//
				tower.blockData[y][z][x] = state;
		}

		private void setPathWithOffset(int dist, int addY) {
			IBlockState torch = Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, dir.rotateYCCW());
			// for (int s = -1; s < 2; s++)
			setStateWithOffset(dist, addY, 0,
				addY != 2 ? air : torch);
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
			//dir = fromPath.dir;
			int mtpChance = rand.nextInt(3);//rand.nextInt(5);
			switch (mtpChance) {
				case 0:
					EnumFacing subDir = getFreeSpaceDir(x, y, z);
					if (subDir != null)
						mtp = new MTPWindow(tower, fromPath, toPath, subDir, distance, x,
							y, z);
					break;
				case 4:
					mtp = new MTPPiston(tower, fromPath, toPath, toPath.dir, distance, x,
						y, z);
					break;
				case 1:
					mtp = new MTPArrow(tower, fromPath, toPath, toPath.dir, distance, x,
						y, z);
					break;
				case 2:
					mtp = new MTPArrowGauntlet(tower, fromPath, toPath, toPath.dir, distance, x,
						y, z);
					break;
				case 3:
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

			super(tower, fromPath, toPath, dir, distance, x, y, z, 0, 0,
					Blocks.red_sandstone.getDefaultState(), tower.wallBlock,
					Blocks.lapis_block.getDefaultState(), true);
		}

	}

	public static class MTPWindow extends MTPuzzle {

		private MTPWindow(MazeTower tower, Path fromPath, Path toPath,
				EnumFacing dir, int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance, x, y, z,
				xOffset = 2, zOffset = 2, null, null, null, false);
			IBlockState stone = Blocks.stone.getDefaultState();
			IBlockState lever = Blocks.lever.getDefaultState()
				.withProperty(BlockLever.FACING, (dir.getAxis() == Axis.Z ? EnumOrientation.DOWN_X : EnumOrientation.DOWN_Z));
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
						{ null, null, air, null, null },
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
	
	public static class MTPPiston extends MTPuzzle {
		private MTPPiston(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing dir, int distance, int x, int y, int z) {
			super(tower, fromPath, toPath, dir, distance, x, y, z,
				xOffset = 0, zOffset = 1, null, null, null, false);
			int[] backCoords;
			boolean canAccessBack = true;/*Path.getStateWithOffset(tower, dir.rotateY(), 2,
				(backCoords = new int[] { x + xOffset, y + 2, z + zOffset + 1 })) == air*/
			IBlockState stone = Blocks.stone.getDefaultState();
			IBlockState piston = MazeTowers.BlockMemoryPiston.getDefaultState()
				.withProperty(BlockPistonBase.FACING, dir.rotateY());
			IBlockState lever = Blocks.lever.getDefaultState().withProperty(BlockLever.FACING,
				EnumOrientation.byMetadata(6 - (canAccessBack ? dir.rotateYCCW() : dir.getOpposite()).getIndex()));
			IBlockState button = ModBlocks.quartzButton.getDefaultState()
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
					{ stone, stone, canAccessBack ? (rand.nextBoolean() ? button : lever) : null },
					{ null, !canAccessBack ? (rand.nextBoolean() ? button : lever) : null, null }
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
				xOffset = -1, zOffset = 1, null, null, null, false);
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
				xOffset = 0, zOffset = 0, null, null, null, false);
			IBlockState stone = Blocks.stone.getDefaultState();
			IBlockState dispenser = Blocks.dispenser.getDefaultState()
				.withProperty(BlockDispenser.FACING, EnumFacing.DOWN);
			IBlockState button = ModBlocks.quartzButton.getDefaultState()
					.withProperty(BlockButton.FACING, EnumFacing.DOWN);
			IBlockState repeaterA = Blocks.unpowered_repeater.getDefaultState()
					.withProperty(Blocks.unpowered_repeater.FACING, dir.getOpposite())
					.withProperty(Blocks.unpowered_repeater.DELAY, rand.nextInt(3) + 1);
			IBlockState repeaterB = Blocks.unpowered_repeater.getDefaultState()
					.withProperty(Blocks.unpowered_repeater.FACING, dir)
					.withProperty(Blocks.unpowered_repeater.DELAY, 3);
			IBlockState repeaterC = Blocks.unpowered_repeater.getDefaultState()
				.withProperty(Blocks.unpowered_repeater.FACING, dir.rotateYCCW())
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
		protected final int dirIndex;
		protected final int dirAxis;
		protected final Path oPath;
		protected final IBlockState[] leverStates;
		protected final IBlockState[] doorStates;
		protected static final IBlockState redstone = Blocks.redstone_wire.getDefaultState();
		protected static int xOffset;
		protected static int zOffset;
		private IBlockState obstacleState;
		private IBlockState medianState;
		private IBlockState triggerState;
		private int distance;
		private boolean hasRedstone = false;
		protected int dirSign;
		protected EnumFacing dir;
		protected IBlockState[][][] stateMap;

		private MTPuzzle(MazeTower tower, Path fromPath, Path toPath,
			EnumFacing facing, int distance, int x, int y, int z, int xOffset, int zOffset,
			IBlockState obstacle, IBlockState median, IBlockState trigger, boolean hasRedstone) {
			dir = facing;
			dirIndex = dir.getIndex();
			dirAxis = (int) Math.floor(dirIndex * 0.5);
			dirSign = (dirIndex % 2 == 0 ? -1 : 1);
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
		
		protected void setDir(EnumFacing dir) {
			this.dir = dir;
		}
		
		protected IBlockState[][][] getRotatedStateMap(IBlockState[][][] map, EnumFacing fromDir,
			EnumFacing toDir, boolean reorder) {
			IBlockState[][][] newMap;
			if (fromDir != toDir) {
				int ry, rz, rx, ryMax = map.length - 1, rzMax = map[0].length - 1, rxMax = map[0][0].length - 1;
				if (fromDir.getAxis() == toDir.getAxis()) {
					newMap = new IBlockState[map.length][map[0].length][map[0][0].length];
					for (ry = 0; ry <= ryMax; ry++) {
						for (rz = 0; rz <= rzMax; rz++) {
							for (rx = 0; rx <= rxMax; rx++)
								newMap[ry][reorder && dirAxis == 1 ? rzMax - rz : rz]
									[reorder && dirAxis == 2 ? rxMax - rx : rx] = map[ry][rz][rx];
						}
					}

					setZOffset(zOffset * -1);
					setXOffset(xOffset * -1);
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
						
						int temp = xOffset;
						setXOffset(zOffset);
						setZOffset(temp);
					} else {
						for (ry = 0; ry <= ryMax; ry++) {
							for (rz = 0; rz <= rzMax; rz++) {
								for (rx = 0; rx <= rxMax; rx++)
									newMap[ry][reorder && dirAxis == 2 ? rxMax - rx : rx]
										[reorder && dirAxis == 1 ? rzMax - rz : rz] = map[ry][rz][rx];
							}
						}
						
						int temp = xOffset * -1;
						setXOffset(zOffset * -1);
						setZOffset(temp);
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
									if (state.getBlock() == Blocks.dispenser) {
										if (Path.isPosValid(xCoord, yCoord, zCoord))
											tower.dataMap[yCoord][zCoord][xCoord] = (randInt != -1) ?
												randInt : (randInt = (rand.nextInt(9) + 2));
									}
									fromPath.setStateAt(xCoord, yCoord, zCoord, state);
								//}
							}
						}
					}
				}
				fromPath.setStateAt(x, y + 2, z, Blocks.emerald_block.getDefaultState());
				fromPath.setStateAt(x + xOffset, y + 2, z + zOffset, Blocks.lapis_block.getDefaultState());
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
			if (!Path.isPosValid((maxCoords = new int[] { y + (map.length - 1),
				z + ((map[0].length - 1) * dirSign) + zOffset,
				x + ((map[0][0].length - 1) * dirSign) + xOffset })) ||
				!Path.isPosValid((minCoords = new int[] { y, z + zOffset, x + xOffset })) ||
				(checkTopSpace && (Path.getStateAt(tower, minCoords[2], maxCoords[0], minCoords[1]) != air ||
				Path.getStateAt(tower, maxCoords[2], maxCoords[0], minCoords[1]) != air ||
				Path.getStateAt(tower, minCoords[2], maxCoords[0], maxCoords[1]) != air ||
				Path.getStateAt(tower, maxCoords) != air)))
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
	
	/*private static class MTTrap {
		
		private final MazeTower tower;
		private final Path path;
		private final int dirIndex;
		private final int dirAxis;
		private final int dirSign;
		private final int x;
		private final int y;
		private final int z;
		protected EnumFacing dir;
		protected IBlockState[][][] stateMap;
		private int distance;
		private int xOffset;
		private int zOffset;
		
		private MTTrap(MazeTower tower, Path path, EnumFacing dir, int x, int y, int z,
			int xOffset, int zOffset) {
			this.tower = tower;
			this.path = path;
			this.dir = dir;
			dirIndex = dir.getIndex();
			dirAxis = (int) Math.floor(dirIndex * 0.5);
			dirSign = (dir.getIndex() % 2 == 0) ? -1 : 1;
			this.x = x;
			this.y = y;
			this.z = z;
			this.xOffset = xOffset;
			this.zOffset = zOffset;
		}
	}
	
	private class MTTArrow extends MTTrap {
		 
		private MTTArrow(MazeTower tower, Path path, EnumFacing dir, int x, int y, int z) {
			super(tower, path, dir, x, y, z, 0, 0);
				
			}
		
		}
	}*/
}
