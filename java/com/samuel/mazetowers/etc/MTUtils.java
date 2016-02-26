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
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import scala.Char;

import com.samuel.mazetowers.MazeTowers;

public class MTUtils {

	private static IBlockState air = Blocks.air
		.getDefaultState();
	private static final int minYSurface = 49;
	private static final int minYWater = 30;
	private static final int minYNether = 20;

	public static int getSurfaceY(World world, int x,
		int z, int range, boolean isUnderwater) {
		final int dimId = world.provider.getDimensionId(), minY = dimId != -1 ? !isUnderwater ? minYSurface
			: minYWater
			: minYNether;
		int cy = minY;
		boolean nextY = true;

		for (; cy < 127; cy++) {
			nextY = false;
			for (int cz = z - range; cz <= z + range; cz++) {
				for (int cx = x - range; cx <= x + range; cx++) {
					BlockPos cpos = new BlockPos(cx, cy, cz);
					Block cBlock = null;
					IBlockState cstate = world
						.getBlockState(cpos);
					if (cstate != air
						&& (cBlock = cstate.getBlock()) != Blocks.leaves
						&& cBlock != Blocks.leaves2
						&& cBlock != Blocks.log
						&& ((cBlock != Blocks.water && isUnderwater) || !isUnderwater)) {
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
		final int dimId = world.provider.getDimensionId(), minY = dimId != -1 ? !isUnderwater ? minYSurface
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
						|| (cBlock = cstate.getBlock()) == Blocks.leaves
						|| cBlock == Blocks.leaves2
						|| ((cBlock == Blocks.water || cBlock == Blocks.lava) && isUnderwater)) {
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

	public static boolean getIsMazeTowerPos(int dimId,
		BlockPos pos) {
		int chunkX = pos.getX() >> 4, chunkZ = pos.getZ() >> 4, y = pos
			.getY();
		BlockPos[] towerPos = MazeTowers.mazeTowers
			.getSpawnPos(dimId);

		/*
		 * for (MazeTower t : MazeTowers.mazeTowers.getTowers()) { if (chunkX ==
		 * t.chunkX && chunkZ == t.chunkZ && y >= t.baseY && y <= t.baseY +
		 * ((t.floors + 1) * 6)) return true; }
		 */
		for (int p = 0; p < towerPos.length; p++) {
			if (towerPos[p] != null
				&& chunkX == towerPos[p].getX() >> 4
				&& chunkZ == towerPos[p].getZ() >> 4
				&& y >= towerPos[p].getY())
				return true;
		}

		return false;
	}

	public static ItemStack getEnchantmentBookById(
		int index, int level) {
		ItemStack stack = new ItemStack(
			Items.enchanted_book);
		stack.addEnchantment(Enchantment
			.getEnchantmentById(index), level);
		return stack;
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
			newMap[1][6][3] = torch[dirMap[3]];
			newMap[2][3][2] = stairsBlocks[dirMap[2]];
			newMap[3][2][3] = stairsBlocks[dirMap[3]];
			newMap[4][2][4] = stairsBlocks[dirMap[3]];
			newMap[4][3][2] = torch[dirMap[0]];
			newMap[5][2][5] = stairsBlocks[dirMap[3]];
			newMap[6][3][6] = stairsBlocks[dirMap[0]];
			newMap[7][4][6] = stairsBlocks[dirMap[0]];
			newMap[7][2][5] = torch[dirMap[1]];
			newMap[8][5][6] = stairsBlocks[dirMap[0]];
			newMap[9][6][5] = stairsBlocks[dirMap[1]];
			newMap[10][6][4] = stairsBlocks[dirMap[1]];
			newMap[10][5][6] = torch[dirMap[2]];
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
								.getBlock()) == Blocks.water
								|| block == Blocks.carpet
								|| block == Blocks.torch
								|| block == Blocks.glass
								|| block == Blocks.web
								|| block instanceof BlockChest
								|| block == Blocks.sea_lantern || block == Blocks.mob_spawner))));
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
								.getBlock()) == Blocks.water
								|| block == Blocks.carpet
								|| block == Blocks.torch
								|| block == Blocks.glass_pane
								|| block == Blocks.stained_glass_pane
								|| block == Blocks.sea_lantern
								|| block == Blocks.mob_spawner
								|| block instanceof BlockChest
								|| block == Blocks.lever
								|| block == Blocks.bookshelf || block == Blocks.flower_pot))));
				}
			}
		}
		return data;
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

	public static Method findObfuscatedMethod(
		Class<?> clazz, String... names) {
		return ReflectionHelper.findMethod(clazz, null,
			names);
	}
}