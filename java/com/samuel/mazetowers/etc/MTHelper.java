package com.samuel.mazetowers.etc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import scala.Char;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MiniTower;

public class MTHelper {

	private static IBlockState air = Blocks.AIR
		.getDefaultState();
	private static final int minYSurface = 49;
	private static final int minYWater = 30;
	private static final int minYNether = 20;

	public static int getSurfaceY(World world, int x,
		int z, int range, boolean isUnderwater) {
		final int dimId = world.provider.getDimension(), minY = dimId != -1 ? !isUnderwater ? minYSurface
			: minYWater
			: minYNether;
		int cy = minY;
		boolean nextY = true;

		for (; cy < (dimId != -1 ? 127 : 100); cy++) {
			nextY = false;
			for (int cz = z - range; cz <= z + range; cz++) {
				for (int cx = x - range; cx <= x + range; cx++) {
					BlockPos cpos = new BlockPos(cx, cy, cz);
					Block cBlock = null;
					IBlockState cstate = world
						.getBlockState(cpos);
					if (cstate != air
						&& (cBlock = cstate.getBlock()) != Blocks.LEAVES
						&& cBlock != Blocks.LEAVES2
						&& cBlock != Blocks.LOG
						&& ((cBlock != Blocks.WATER && isUnderwater) || !isUnderwater)) {
						nextY = true;
						break;
					}
				}

				if (nextY)
					break;
			}

			if (!nextY)
				break;
		}

		return cy;
	}

	public static int getGroundY(World world, int x, int y,
		int z, int range, boolean isUnderwater) {
		final int dimId = world.provider.getDimension(), minY = dimId != -1 ? !isUnderwater ? minYSurface
			: minYWater
			: minYNether;
		int cy = y - 1;
		boolean nextY = cy > 0;

		for (; cy >= minY; cy--) {
			nextY = false;
			for (int cz = z - range; cz <= z + range; cz++) {
				for (int cx = x - range; cx <= x + range; cx++) {
					BlockPos cpos = new BlockPos(cx, cy, cz);
					Block cBlock = null;
					IBlockState cstate = world
						.getBlockState(cpos);
					if (cstate == air
						|| (cBlock = cstate.getBlock()) == Blocks.LEAVES
						|| cBlock == Blocks.LEAVES2
						|| ((cBlock == Blocks.WATER || cBlock == Blocks.LAVA) && isUnderwater)) {
						nextY = true;
						break;
					}
				}

				if (nextY)
					break;
			}

			if (!nextY)
				break;
			else if (dimId == 1 && minY < 1)
				return -1;
		}

		return cy;
	}
	
	public static int getMiniTowerSupportExitY(World world, int x, int y, int z) {
		final int dimId = world.provider.getDimension();
			int cy = y - 1;

			for (; cy >= 4; cy--) {
				BlockPos cpos = new BlockPos(x, cy, z);
				Block cBlock = null;
				IBlockState cstate = world
					.getBlockState(cpos);

				if (cstate == air)
					break;
			}

			return cy > 4 ? cy : y;
	}

	public static boolean getIsMazeTowerPos(World worldIn,
		BlockPos pos) {
		final int dimId = worldIn.provider.getDimension(),
			chunkX = pos.getX() >> 4, chunkZ = pos.getZ() >> 4, y = pos.getY();

		/*
		 * for (MazeTower t : MazeTowers.mazeTowers.getTowers()) { if (chunkX ==
		 * t.chunkX && chunkZ == t.chunkZ && y >= t.baseY && y <= t.baseY +
		 * ((t.floors + 1) * 6)) return true; }
		 */
		MazeTowerBase tower = MazeTowers.mazeTowers.getTowerBesideCoords(worldIn, chunkX, chunkZ);
		return chunkX == tower.chunkX >> 4
			&& chunkZ == tower.chunkZ >> 4
			&& y >= tower.baseY && y <= tower.baseY + ((tower.floors + 1) * 6)
			|| getMiniTowerAtPos(tower.getMiniTowers(), pos) != null;
	}
	
	public static MiniTower getMiniTowerAtPos(List<MiniTower> miniTowers, BlockPos pos) {
		for (MiniTower mt : miniTowers) {
			if (mt.getPosInBounds(pos))
				return mt;
		}
		
		return null;
	}

	public static ItemStack getEnchantmentBookById(
		int index, int level) {
		ItemStack stack = new ItemStack(
			Items.ENCHANTED_BOOK);
		//EnchantmentHelper.addRandomEnchantment(new Random(), stack, level);
		Items.ENCHANTED_BOOK.addEnchantment(stack, new EnchantmentData(Enchantment
			.getEnchantmentByID(index), level));
		return stack;
	}
	
	public static void fillInventoryWithLoot(EntityPlayer player, int rarity) {
		/*ChestGenHooks chestGen = ModChestGen.chestContents[rarity];
		for (int i = 0; i < player.inventory.mainInventory.length; i++)
			player.inventory.mainInventory[i] = chestGen.getOneItem(player.worldObj.rand);*/
	}
	
	public static ArrayList<String> getLootList(Random rand, int rarity) {
		ArrayList<String> lootList = new ArrayList<String>();
		/*final double iterations = 100000d;
		final List<WeightedRandomChestContent> items;
		HashMap<WeightedRandomChestContent, Integer> itemCount =
			new HashMap<WeightedRandomChestContent, Integer>();
		ChestGenHooks chestGen = ModChestGen.chestContents[rarity];
		WeightedRandomChestContent weightedItem;
		WeightedRandomChestContent[] keys;
		Iterator iterator = chestGen.getItems(rand).iterator();
		ItemStack stack;
		int s = 0;
		double totalWeight = 0, totalWeightNoDupe = 0;
		
		final Comparator<Entry<WeightedRandomChestContent, Integer>> chestGenComparator =
			new Comparator<Entry<WeightedRandomChestContent, Integer>>() {

			@Override
			public int compare(
				Entry<WeightedRandomChestContent, Integer> e1,
				Entry<WeightedRandomChestContent, Integer> e2) {
				Integer v1 = (Integer) e1.getValue();
				Integer v2 = (Integer) e2.getValue();
				return v1.compareTo(v2);
			}
		};
		
		while (iterator.hasNext()) {
			boolean keyFound = false;
			weightedItem = (WeightedRandomChestContent) iterator.next();
			keys = itemCount.keySet().toArray(
				new WeightedRandomChestContent[itemCount.size()]);
			for (WeightedRandomChestContent key : keys) {
				stack = weightedItem.theItemId;
				if (stack.areItemsEqual(stack, key.theItemId) &&
					weightedItem.itemWeight == key.itemWeight &&
					weightedItem.minStackSize == key.minStackSize &&
					weightedItem.maxStackSize == key.maxStackSize) {
					keyFound = true;
					break;
				}
			}
			if (!keyFound) {
				totalWeightNoDupe += weightedItem.itemWeight;
				itemCount.put(weightedItem, 0);
			} else
				totalWeight += weightedItem.itemWeight;
		}
		
		totalWeight += totalWeightNoDupe;
		
		items = chestGen.getItems(rand);
		keys = itemCount.keySet().toArray(new WeightedRandomChestContent[itemCount.size()]);
		
		for (int i = 0; i < iterations; i++) {
			weightedItem = WeightedRandom.getRandomItem(rand, items);
			stack = weightedItem.theItemId;
			for (s = 0; s < keys.length; s++) {
				if (
					stack.areItemsEqual(stack, keys[s].theItemId) &&
					weightedItem.itemWeight == keys[s].itemWeight &&
					weightedItem.minStackSize == keys[s].minStackSize &&
					weightedItem.maxStackSize == keys[s].maxStackSize) {
					itemCount.put(keys[s], itemCount.get(keys[s]) + 1);
					break;
				}
			}
		}
		
		String lootListLine = "";
		
		lootList.add(MazeTowerBase.EnumLevel.getStringFromLevel(rarity, false) +
			"--------------------");
		
		List<Entry<WeightedRandomChestContent, Integer>> stackEntries =
			new ArrayList<Entry<WeightedRandomChestContent, Integer>>(itemCount.entrySet());
		Collections.sort(stackEntries, chestGenComparator.reversed());
		LinkedHashMap<WeightedRandomChestContent, Integer> chestGenSorted =
			new LinkedHashMap<WeightedRandomChestContent, Integer>(itemCount.size());
		for (Entry<WeightedRandomChestContent, Integer> entry : stackEntries) {
			chestGenSorted.put(entry.getKey(), entry.getValue());
		}
		
		keys = chestGenSorted.keySet().toArray(new WeightedRandomChestContent[keys.length]);
		
		for (s = 0; s < keys.length; s++) {
			String stackName, countRange;
			final double weight;
			weightedItem = keys[s];
			stack = weightedItem.theItemId;
			stackName = stack.getDisplayName();
			if (stackName.equals("Enchanted Book"))
				stackName = "EB";
			if (weightedItem.minStackSize == weightedItem.maxStackSize)
				countRange = String.valueOf(weightedItem.minStackSize);
			else
				countRange = weightedItem.minStackSize + "-" + weightedItem.maxStackSize;
			weight = itemCount.get(weightedItem) / iterations;
			lootListLine += stackName + "(" + countRange + "): " + (Math.round(
				weight * 100000d) / 1000d) + "%";
			if (s % 2 != 1 && s != keys.length - 1)
				lootListLine += " | ";
			else {
				lootList.add(lootListLine);
				lootListLine = "";
			}
		}
		
		lootList.add(totalWeightNoDupe + " (" + totalWeight + ")");
		*/
		return lootList;
	}

	public static String getEncodedItemName(
		String itemName, Random rand) {
		int charCode;
		StringBuilder encodedName = new StringBuilder();
		Iterator iterator;
		List<Integer> charPos = new ArrayList<Integer>(), charPosTemp, charPosShuffled = new ArrayList<Integer>(), switchableChars = new ArrayList<Integer>();

		itemName = itemName.toLowerCase();

		for (int c = 0; c < itemName.length(); c++) {
			charCode = Char.char2int(itemName.charAt(c));
			if (charCode >= 97 && charCode <= 122) {
				charPos.add(c);
				switchableChars.add(c);
			}
		}

		charPosTemp = new ArrayList<Integer>(charPos);

		while (!charPos.isEmpty()) {
			int charIndex = rand.nextInt(charPos.size());
			charPosShuffled.add(charPos.get(charIndex));
			charPos.remove(charIndex);
		}

		charPos = charPosTemp;

		int charCount = 0;
		int charIndex;
		char lastChar = ' ';

		iterator = switchableChars.iterator();
		while (iterator.hasNext()) {

			while (charPos.get(charCount).intValue() != charCount) {
				charPos.add(charCount, -1);
				charPosShuffled.add(charCount, -1);
				encodedName.append(lastChar = itemName
					.charAt(charCount++));
			}

			char tempChar = itemName
				.charAt(charPosShuffled
					.get((charIndex = (Integer) iterator
						.next())));

			if (lastChar == ' ')
				tempChar = Character.toUpperCase(tempChar);

			encodedName.append(tempChar);
			lastChar = tempChar;
			charCount++;
		}

		return encodedName.toString();
	}

	public static IBlockState[][][] getStairRotatedStairsMap(
		IBlockState[][][] map, EnumFacing fromDir,
		EnumFacing toDir, IBlockState[] stairsBlocks,
		IBlockState[] torch) {
		IBlockState[][][] newMap = map.clone();
		final int[] dirMap = (fromDir.getAxis() == toDir
			.getAxis()) ? new int[] { 1, 2, 3, 0 }
			: (fromDir.getAxisDirection() == toDir
				.getAxisDirection()) ? new int[] { 2, 1, 0,
				3 } : new int[] { 0, 1, 2, 3 };

		if (fromDir != toDir) {
			newMap[0][5][2] = stairsBlocks[dirMap[2]];
			newMap[1][4][2] = stairsBlocks[dirMap[2]];
			newMap[1][6][2] = torch[dirMap[3]];
			newMap[2][3][2] = stairsBlocks[dirMap[2]];
			newMap[3][2][3] = stairsBlocks[dirMap[3]];
			newMap[4][2][4] = stairsBlocks[dirMap[3]];
			newMap[4][2][2] = torch[dirMap[0]];
			newMap[5][2][5] = stairsBlocks[dirMap[3]];
			newMap[6][3][6] = stairsBlocks[dirMap[0]];
			newMap[7][4][6] = stairsBlocks[dirMap[0]];
			newMap[7][2][6] = torch[dirMap[1]];
			newMap[8][5][6] = stairsBlocks[dirMap[0]];
			newMap[9][6][5] = stairsBlocks[dirMap[1]];
			newMap[10][6][4] = stairsBlocks[dirMap[1]];
			newMap[10][6][6] = torch[dirMap[2]];
			newMap[11][6][3] = stairsBlocks[dirMap[1]];
		}

		return newMap;
	}

	public static IBlockState[][][] getRotatedStateMap(
		IBlockState[][][] map, EnumFacing fromDir,
		EnumFacing toDir, boolean reorder) {
		int dirAxis = toDir.getAxis() == Axis.X ? 2 : 1;
		int dirSign = dirAxis % 2 == 0 ? -1 : 1;
		IBlockState[][][] newMap;
		if (fromDir != toDir) {
			int ry, rz, rx, ryMax = map.length - 1, rzMax = map[0].length - 1, rxMax = map[0][0].length - 1;
			if (fromDir.getAxis() == toDir.getAxis()) {
				newMap = new IBlockState[map.length][map[0].length][map[0][0].length];
				for (ry = 0; ry <= ryMax; ry++) {
					for (rz = 0; rz <= rzMax; rz++) {
						for (rx = 0; rx <= rxMax; rx++)
							newMap[ry][reorder
								&& dirAxis == 1 ? rzMax
								- rz : rz][reorder
								&& dirAxis == 2 ? rxMax
								- rx : rx] = map[ry][rz][rx];
					}
				}
			} else {
				newMap = new IBlockState[map.length][map[0][0].length][map[0].length];
				if (fromDir.getAxisDirection() == toDir
					.getAxisDirection()) {
					for (ry = 0; ry <= ryMax; ry++) {
						for (rz = 0; rz <= rzMax; rz++) {
							for (rx = 0; rx <= rxMax; rx++)
								newMap[ry][reorder
									&& dirAxis == 2 ? rxMax
									- rx : rx][reorder
									&& dirAxis == 1 ? rzMax
									- rz : rz] = map[ry][rz][rx];
						}
					}
				} else {
					for (ry = 0; ry <= ryMax; ry++) {
						for (rz = 0; rz <= rzMax; rz++) {
							for (rx = 0; rx <= rxMax; rx++)
								newMap[ry][reorder ? rxMax
									- rx : rx][reorder ? rzMax
									- rz
									: rz] = map[ry][rz][rx];
						}
					}
				}
			}
			/*
			 * if (fromDir.getAxis() == toDir.getAxis()) { newMap = new
			 * IBlockState[map.length][map[0].length][map[0][0].length]; for (ry
			 * = 0; ry <= ryMax; ry++) { for (rz = 0; rz <= rzMax; rz++) { for
			 * (rx = 0; rx <= rxMax; rx++) newMap[ry][reorder && dirAxis == 2 ?
			 * rzMax - rz : rz] [reorder && dirAxis == 1 ? rxMax - rx : rx] =
			 * map[ry][rz][rx]; } } } else { newMap = new
			 * IBlockState[map.length][map[0][0].length][map[0].length]; if
			 * (fromDir.getAxisDirection() == toDir.getAxisDirection()) { for
			 * (ry = 0; ry <= ryMax; ry++) { for (rz = 0; rz <= rzMax; rz++) {
			 * for (rx = 0; rx <= rxMax; rx++) newMap[ry][rx][rz] =
			 * map[ry][rz][rx]; } } } else { for (ry = 0; ry <= ryMax; ry++) {
			 * for (rz = 0; rz <= rzMax; rz++) { for (rx = 0; rx <= rxMax; rx++)
			 * newMap[ry][reorder ? rxMax - rx : rx] [reorder ? rzMax - rz : rz]
			 * = map[ry][rz][rx]; } } } }
			 */
		} else
			newMap = map.clone();
		return newMap;
	}

	public static BitSet[][] getBlockBreakabilityData(
		IBlockState[][][] blockData) {
		BitSet[][] data = new BitSet[blockData.length][];
		for (int y = 0; y < blockData.length; y++) {
			data[y] = new BitSet[16];
			for (int z = 0; z < 16; z++) {
				data[y][z] = new BitSet(16);
				for (int x = 0; x < 16; x++) {
					IBlockState state = blockData[y][z][x];
					Block block;
					data[y][z]
						.set(
							x,
							(state == air || (state != null && ((block = state
								.getBlock()) == Blocks.WATER
								|| block == Blocks.CARPET
								|| block == Blocks.TORCH
								|| block == Blocks.LADDER
								|| block == Blocks.GLASS
								|| block == Blocks.WEB
								|| block == Blocks.GLASS_PANE
								|| block instanceof BlockMobSpawner
								|| block instanceof BlockChest
								|| block == Blocks.LAVA
								|| block == Blocks.TNT
								|| block == MazeTowers.BlockChaoticSludge
								|| block == Blocks.SEA_LANTERN))));
				}
			}
		}
		return data;
	}

	public static BitSet[][] getMTBlockBreakabilityData(
		IBlockState[][][] stateMap) {
		BitSet[][] data = new BitSet[stateMap.length][];
		final int zLen = stateMap[0].length;
		final int xLen = stateMap[0][0].length;
		for (int y = 0; y < stateMap.length; y++) {
			data[y] = new BitSet[zLen];
			for (int z = 0; z < zLen; z++) {
				data[y][z] = new BitSet(xLen);
				for (int x = 0; x < xLen; x++) {
					IBlockState state = stateMap[y][z][x];
					Block block;
					data[y][z]
						.set(
							x,
							(state == null || state == air || (state != null && ((block = state
								.getBlock()) == Blocks.WATER
								|| block == Blocks.CARPET
								|| block == Blocks.TORCH
								|| block == Blocks.GLASS_PANE
								|| block == Blocks.STAINED_GLASS_PANE
								|| block == Blocks.SEA_LANTERN
								|| block instanceof BlockMobSpawner
								|| block instanceof BlockChest
								|| block == Blocks.LEVER
								|| block == Blocks.BOOKSHELF || block == Blocks.FLOWER_POT))));
				}
			}
		}
		return data;
	}
	
	public static int RGBToInt(float red, float green, float blue) {
		int R = Math.round(255 * red);
		int G = Math.round(255 * green);
	    int B = Math.round(255 * blue);

	    R = (R << 16) & 0x00FF0000;
	    G = (G << 8) & 0x0000FF00;
	    B = B & 0x000000FF;

	    return 0xFF000000 | R | G | B;
	}

	/*
	 * public static Block getNewStairsBlock(IBlockState baseState) { Block
	 * block; try { Class<?> stairsClass =
	 * Class.forName("net.minecraft.block.BlockStairs"); Method stairs =
	 * findObfuscatedMethod(BlockStairs.class, "BlockStairs", "func_i45684_");
	 * stairs.setAccessible(true); Object stairsObj = stairsClass.newInstance();
	 * return (Block) stairs.invoke(stairsObj, baseState); } catch (Exception e)
	 * { e = null; }
	 * 
	 * return null; }
	 */

	public static Field findObfuscatedField(Class<?> clazz,
		String... names) {
		return ReflectionHelper.findField(clazz,
			ObfuscationReflectionHelper.remapFieldNames(
				clazz.getName(), names));
	}
	
	public static <E> Method findObfuscatedMethod(Class<? super E> clazz, E instance,
		String[] names, Class<?>... methodTypes) {
		return ReflectionHelper.findMethod(clazz, instance,
			ObfuscationReflectionHelper.remapFieldNames(
				clazz.getName(), names), methodTypes);
	}

	public static int getCurrentSpectriteFrame(World worldIn) {
		if (worldIn == null) {
            return 0;
		} else {
        	float time = MathHelper.ceiling_float_int((((worldIn.getTotalWorldTime() >> 1) % 36)
        		* 0.2777F) * 1000F) / 10000F;
            return Math.round(time * 36);
        }
	}

	public static Integer[] convertToIntegerArray(byte[] input)
	{
	    Integer[] ret = new Integer[input.length];
	    for (int i = 0; i < input.length; i++)
	    {
	        ret[i] = new Integer((int) input[i]);
	    }
	    return ret;
	}

	public static byte[] convertToByteArray(int[] input) {
		 byte[] ret = new byte[input.length];

		 for (int i = 0; i < input.length; i++)
	    {
	        ret[i] = (byte) input[i];
	    }
		 
         return ret;
	}
}