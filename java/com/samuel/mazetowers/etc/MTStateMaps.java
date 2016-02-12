package com.samuel.mazetowers.etc;

import java.util.HashMap;
import java.util.Map;

import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTower;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.IStringSerializable;

public class MTStateMaps {
	
	private static /*final*/ EnumFacing[] dirs = new EnumFacing[] {
		EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH
	};
	private static Map<IBlockState, IBlockState[][][][]> rawMaps;
	private static Map<IBlockState, Map<EnumFacing, IBlockState[][][]>[]> maps;
	private static final IBlockState[] torch = new IBlockState[] {
		Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST),
		Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH),
		Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST),
		Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH)
	};
	
	public static void initStateMaps(MazeTower tower) {
		if (rawMaps != null && rawMaps.containsKey(tower.wallBlock)) {
			final IBlockState air2 = Blocks.air.getDefaultState();
			final IBlockState wall = tower.wallBlock_external;
			final IBlockState wall2 = tower.wallBlock;
			final IBlockState c = tower.floorBlock;
			final IBlockState window = Blocks.glass_pane.getDefaultState();
			final IBlockState carpet = tower.getCarpetState();
			final IBlockState g = tower.getGlassState();
			final IBlockState lamp = Blocks.redstone_lamp.getDefaultState();
			final IBlockState wire = Blocks.redstone_wire.getDefaultState();
			try {
			for (EnumFacing dir : dirs) {
				maps.get(tower.wallBlock)[3].put(dir,
					getRoof(air2, wall, wall2, c, g, lamp));
				maps.get(tower.wallBlock)[4].put(dir, getRoofWire(air2, wall, wire));
			}
			} catch (Exception e) {
				e = null;
			}
		} else {
			final IBlockState[][][][] towerRawMaps;
			final Map<EnumFacing, IBlockState[][][]>[] towerMaps;
			
			if (rawMaps == null) {
				rawMaps = new HashMap<IBlockState, IBlockState[][][][]>();
				maps = new HashMap<IBlockState, Map<EnumFacing, IBlockState[][][]>[]>();
			}
			towerRawMaps = getRawMapsForTower(tower);
			rawMaps.put(tower.wallBlock, towerRawMaps);
			towerMaps = new Map[EnumStateMap.values().length];
			
			for (EnumStateMap map : EnumStateMap.values()) {
				final int mapIndex = map.ordinal();
				towerMaps[mapIndex] = new HashMap<EnumFacing, IBlockState[][][]>();
				for (EnumFacing dir : dirs) {
					final EnumFacing checkDir = EnumFacing.NORTH;
					if (dir == EnumFacing.SOUTH)
						towerMaps[mapIndex].put(dir, towerRawMaps[mapIndex]);
					else {
						if (mapIndex != 1)
							towerMaps[mapIndex].put(dir, MTUtils.getRotatedStateMap(
								towerRawMaps[mapIndex], checkDir, dir, true));
						else
							towerMaps[mapIndex].put(dir, MTUtils.getRotatedStateMap(
								MTUtils.getStairRotatedStairsMap(towerRawMaps[mapIndex],
								EnumFacing.SOUTH, dir, tower.stairsBlock, torch),
								checkDir, dir, true));
					}
				}
			}
			
			maps.put(tower.wallBlock, towerMaps);
		}
	}
	
	public static int[][] getTopStaircaseCoords(IBlockState[][] map) {
		final int[][] coords = (map[3][2].getBlock() instanceof BlockStairs) ?
			new int[][] { { 3, 2 }, { 4, 2 }, { 5, 2 }, { 6, 2 } } :
			(map[2][5].getBlock() instanceof BlockStairs) ?
			new int[][] { { 2, 5 }, { 2, 4 }, { 2, 3 }, { 2, 2 } } :
			(map[5][6].getBlock() instanceof BlockStairs) ?
			new int[][] { { 5, 6 }, { 4, 6 }, { 3, 6 }, { 2, 6 } } :
			(map[6][3].getBlock() instanceof BlockStairs) ?
			new int[][] { { 6, 3 }, { 6, 4 }, { 6, 5 }, { 6, 6 } } :
			(map[2][3].getBlock() instanceof BlockStairs) ?
			new int[][] { { 2, 3 }, { 2, 4 }, { 2, 5 }, { 2, 6 } } :
			(map[5][2].getBlock() instanceof BlockStairs) ?
			new int[][] { { 5, 2 }, { 4, 2 }, { 3, 2 }, { 2, 2 } } :
			(map[6][5].getBlock() instanceof BlockStairs) ?
			new int[][] { { 6, 5 }, { 6, 4 }, { 6, 3 }, { 6, 2 } } :
			(map[3][6].getBlock() instanceof BlockStairs) ?
			new int[][] { { 3, 6 }, { 4, 6 }, { 5, 6 }, { 6, 6 } } : null;
			
		return coords;
	}
	
	public static EnumFacing getTopStairDir(IBlockState[][] map) {
		final int[] coords = getTopStaircaseCoords(map)[0];
		final EnumFacing dir = map[coords[0]][coords[1]].getValue(BlockStairs.FACING);
			
		return dir;
	}
	
	public static IBlockState[][][] getStateMap(EnumFacing dir, String mapName,
		IBlockState wallBlock, IBlockState wallBlock_external,
		IBlockState cBlock, IBlockState carpetBlock) {
		
		final int mapIndex = EnumStateMap.valueOf(mapName).ordinal();
		final IBlockState[][][] map = maps.get(wallBlock)[mapIndex].get(dir);
		final IBlockState air2 = Blocks.air.getDefaultState();
		final IBlockState wall = wallBlock_external;
		final IBlockState wall2 = wallBlock;
		final IBlockState c = cBlock;
		final IBlockState window = Blocks.glass_pane.getDefaultState();
		final IBlockState carpet = carpetBlock;
		maps.get(wallBlock)[2].get(dir)[0] =
			getTopCarpet(air2, wall, carpet, wall2);
		
		return map;
	}
	
	public static IBlockState[][][][] getRawMapsForTower(MazeTower tower) {
		final IBlockState air = tower.air;
		final IBlockState air2 = Blocks.air.getDefaultState();
		final IBlockState wall = tower.wallBlock_external;
		final IBlockState wall2 = tower.wallBlock;
		final IBlockState floor = tower.floorBlock;
		final IBlockState c = tower.ceilBlock;
		final IBlockState floor2 = /*!tower.getIsUnderwater()*/
			!(wall.getBlock() instanceof BlockPrismarine) ? floor :
			Blocks.sea_lantern.getDefaultState();
		final IBlockState window = Blocks.glass_pane.getDefaultState();
		final IBlockState carpet = tower.getCarpetState();
		final IBlockState g = tower.getGlassState();
		final IBlockState lever = Blocks.lever.getStateFromMeta(7);
		final IBlockState lamp = Blocks.redstone_lamp.getDefaultState();
		final IBlockState wire = Blocks.redstone_wire.getDefaultState();
		final IBlockState[] stairs = tower.stairsBlock;
		final IBlockState[][][][] rawMaps = new IBlockState[][][][] {
			{
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, floor, floor, floor, floor, floor, wall, air2 },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ air2, wall, floor, floor, floor, floor, floor, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				}
			},
			{
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, wall, air, air, air, air, wall, air2 },
					{ wall, wall2, wall, air, air, air, air, wall2, wall },
					{ wall, wall2, wall, air, air, air, air, wall2, wall },
					{ wall, wall2, stairs[2], air, air, air, air, wall2, wall },
					{ air2, wall, air, air, air, air, air, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, wall, air, air, air, air, wall, air2 },
					{ wall, wall2, wall, air, air, air, air, wall2, wall },
					{ wall, wall2, stairs[2], air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ air2, wall, torch[3], air, air, air, air, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, floor, floor, floor, floor, floor, wall, air2 },
					{ wall, wall2, stairs[2], floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, air, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, air, floor, floor, floor, floor, wall2, wall },
					{ air2, wall, air, floor, floor, floor, floor, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, air, stairs[3], wall, wall, wall, wall, air2 },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ air2, wall, air, air, air, air, wall, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, torch[0], air, stairs[3], wall, wall, wall, air2 },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ air2, wall, air, air, air, air, wall, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, air, air, air, stairs[3], floor, wall, air2 },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ air2, wall, floor, floor, floor, floor, floor, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, air, air, air, air, air, wall, air2 },
					{ wall, wall2, air, air, air, air, stairs[0], wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ air2, wall, air, air, air, air, wall, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, air, air, air, air, torch[1], wall, air2 },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, stairs[0], wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ air2, wall, air, air, air, air, wall, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, floor, floor, floor, floor, air, wall, air2 },
					{ wall, wall2, floor, floor, floor, floor, air, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, air, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, stairs[0], wall2, wall },
					{ air2, wall, floor, floor, floor, floor, floor, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, air, air, air, air, air, wall, air2 },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ air2, wall, wall, wall, wall, stairs[1], air, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, air, air, air, air, air, wall, air2 },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ air2, wall, wall, wall, stairs[1], air, torch[2], wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, wall, floor, floor, floor, floor, floor, wall, air2 },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ air2, wall, floor, stairs[1], air, air, air, wall, air2 },
					{ air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				}
			},
			{
				getTopCarpet(air2, wall, carpet, wall2),
				{
					{ air2, air2, air2, wall, window, wall, air2, air2, air2 },
					{ air2, air2, wall, air2, air2, air2, wall, air2, air2 },
					{ air2, wall, air2, air2, air2, air2, air2, wall, air2 },
					{ wall, air2, air2, air2, air2, air2, air2, air2, wall },
					{ window, air2, air2, air2, air2, air2, air2, air2, window },
					{ wall, air2, air2, air2, air2, air2, air2, air2, wall },
					{ air2, wall, air2, air2, air2, air2, air2, wall, air2 },
					{ air2, air2, wall, air2, air2, air2, wall, air2, air2 },
					{ air2, air2, air2, wall, window, wall, air2, air2, air2 }
				},
				{
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 },
					{ air2, air2, wall, air2, air2, air2, wall, air2, air2 },
					{ air2, wall, air2, air2, air2, air2, air2, wall, air2 },
					{ wall, air2, air2, air2, air2, air2, air2, air2, wall },
					{ wall, air2, air2, air2, lever, air2, air2, air2, wall },
					{ wall, air2, air2, air2, air2, air2, air2, air2, wall },
					{ air2, wall, air2, air2, air2, air2, air2, wall, air2 },
					{ air2, air2, wall, air2, air2, air2, wall, air2, air2 },
					{ air2, air2, air2, wall, wall, wall, air2, air2, air2 }
				}
			},
			getRoof(air2, wall, wall2, c, g, lamp),
			getRoofWire(air2, wall, wire)/*,
			{
			  {
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c }
			  }, {
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c },
				  { c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c }
				}
			}*/
		};
		
		return rawMaps;
	}
	
	private static IBlockState[][] getTopCarpet(IBlockState air2, IBlockState wall,
		IBlockState carpet, IBlockState wall2) {
		return new IBlockState[][] {
			new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 },
			new IBlockState[] { air2, air2, wall, carpet, carpet, carpet, wall, air2, air2 },
			new IBlockState[] { air2, wall, carpet, carpet, carpet, carpet, carpet, wall, air2 },
			new IBlockState[] { wall, carpet, carpet, carpet, carpet, carpet, carpet, carpet, wall },
			new IBlockState[] { wall, carpet, carpet, carpet, wall2, carpet, carpet, carpet, wall },
			new IBlockState[] { wall, carpet, carpet, carpet, carpet, carpet, carpet, carpet, wall },
			new IBlockState[] { air2, wall, carpet, carpet, carpet, carpet, carpet, wall, air2 },
			new IBlockState[] { air2, air2, wall, carpet, carpet, carpet, wall, air2, air2 },
			new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 }
		};
	}
	
	private static IBlockState[][][] getRoof(IBlockState air2, IBlockState wall,
		IBlockState wall2, IBlockState c, IBlockState g, IBlockState lamp) {
		return new IBlockState[][][] {
			new IBlockState[][] {
				new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 },
				new IBlockState[] { air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
				new IBlockState[] { air2, wall, c, c, c, c, c, wall, air2 },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, lamp, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[]{ air2, wall, c, c, c, c, c, wall, air2 },
				new IBlockState[] { air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
				new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 }
			},
			new IBlockState[][] {
				new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 },
				new IBlockState[] { air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
				new IBlockState[] { air2, wall, lamp, c, c, c, lamp, wall, air2 },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, lamp, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[] { air2, wall, lamp, c, c, c, lamp, wall, air2 },
				new IBlockState[] { air2, air2, wall, wall2, wall2, wall2, wall, air2, air2 },
				new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 }
			}
		};
	}
	
	private static IBlockState[][][] getRoofWire(IBlockState air2, IBlockState wall,
		IBlockState wire) {
		return new IBlockState[][][] {
			new IBlockState[][] {
				new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 },
				new IBlockState[] { air2, air2, wall, air2, wall, air2, wall, air2, air2 },
				new IBlockState[] { air2, wall, wire, wire, wire, wire, wire, wall, air2 },
				new IBlockState[] { wall, air2, wire, air2, air2, air2, wire, air2, wall },
				new IBlockState[] { wall, wall, wire, air2, wire, air2, wire, wall, wall },
				new IBlockState[] { wall, air2, wire, air2, air2, air2, wire, air2, wall },
				new IBlockState[] { air2, wall, wire, wire, wire, wire, wire, wall, air2 },
				new IBlockState[] { air2, air2, wall, air2, wall, air2, wall, air2, air2 },
				new IBlockState[] { air2, air2, air2, wall, wall, wall, air2, air2, air2 }
			}
		};
	}
	
	public static enum EnumStateMap implements IStringSerializable
    {
		MINI_TOWER_BASE("mini_tower_base"),
		MINI_TOWER_STAIRS("mini_tower_stairs"),
		MINI_TOWER_TOP("mini_tower_top"),
		MINI_TOWER_ROOF("mini_tower_roof"),
		MINI_TOWER_ROOF_WIRE("mini_tower_roof_wire");

        private final String name;

        private EnumStateMap(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
