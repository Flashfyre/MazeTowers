package com.samuel.mazetowers.etc;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import org.apache.commons.lang3.StringUtils;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTower;

public class MTStateMaps {
	
	private static final EnumFacing[] dirs = new EnumFacing[] {
		EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH
	};
	private static Map<IBlockState, IBlockState[][][][]> rawMaps;
	private static Map<IBlockState, Map<EnumFacing, IBlockState[][][]>[]> maps;
	private static final IBlockState[] torch = new IBlockState[] {
		Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST),
		Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH),
		Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST),
		Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH)
	};
	
	public static void initStateMaps(MazeTower tower) {
		if (rawMaps != null && rawMaps.containsKey(tower.wallBlock_external)) {
			final EnumDyeColor[] dyeColors = tower.getDyeColors();
			final IBlockState air2 = Blocks.AIR.getDefaultState();
			final IBlockState wall = tower.wallBlock_external;
			final IBlockState wall2 = tower.wallBlock;
			final IBlockState c = tower.floorBlock;
			final IBlockState window = Blocks.GLASS_PANE.getDefaultState();
			final IBlockState[] carpet = new IBlockState[] {
				tower.getColourBlockState(Blocks.CARPET, dyeColors[0]),
				tower.getColourBlockState(Blocks.CARPET, dyeColors[1]),
				tower.getColourBlockState(Blocks.CARPET, dyeColors[2])
			};
			final IBlockState[] g = new IBlockState[] {
				tower.getColourBlockState(Blocks.STAINED_GLASS, dyeColors[0]),
				tower.getColourBlockState(Blocks.STAINED_GLASS, dyeColors[1]),
				tower.getColourBlockState(Blocks.STAINED_GLASS, dyeColors[2])
			};
			final IBlockState lamp = Blocks.REDSTONE_LAMP.getDefaultState();
			final IBlockState wire = Blocks.REDSTONE_WIRE.getDefaultState();
			
			for (EnumFacing dir : dirs) {
				maps.get(tower.wallBlock_external)[EnumStateMap.MINI_TOWER_CEILING_1.ordinal()]
					.put(dir, getCeiling(air2, wall, wall2, c, g[0], lamp));
				maps.get(tower.wallBlock_external)[EnumStateMap.MINI_TOWER_CEILING_2.ordinal()]
					.put(dir, getCeiling(air2, wall, wall2, c, g[1], lamp));
				maps.get(tower.wallBlock_external)[EnumStateMap.MINI_TOWER_CEILING_3.ordinal()]
					.put(dir, getCeiling(air2, wall, wall2, c, g[2], lamp));
				
			}
		} else {
			final IBlockState[][][][] towerRawMaps;
			final Map<EnumFacing, IBlockState[][][]>[] towerMaps;
			
			if (rawMaps == null) {
				rawMaps = new HashMap<IBlockState, IBlockState[][][][]>();
				maps = new HashMap<IBlockState,
					Map<EnumFacing, IBlockState[][][]>[]>();
			}
			towerRawMaps = getRawMapsForTower(tower);
			rawMaps.put(tower.wallBlock_external, towerRawMaps);
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
							towerMaps[mapIndex].put(dir, MTHelper.getRotatedStateMap(
								towerRawMaps[mapIndex], checkDir, dir, true));
						else
							towerMaps[mapIndex].put(dir, MTHelper.getRotatedStateMap(
								MTHelper.getStairRotatedStairsMap(towerRawMaps[mapIndex],
								EnumFacing.SOUTH, dir, tower.stairsBlock, !tower.getIsUnderwater() ? torch : null),
								checkDir, dir, true));
					}
				}
			}
			
			maps.put(tower.wallBlock_external, towerMaps);
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
	
	public static IBlockState[][][][] getStateMaps(EnumFacing dir, IBlockState wallBlock,
		IBlockState wallBlock_external, IBlockState floorBlock, IBlockState ceilBlock,
		IBlockState fenceBlock, IBlockState carpetBlock, IBlockState commonGlass,
		IBlockState topGlass2, IBlockState topGlass3, IBlockState beaconGlass2,
		IBlockState beaconGlass3, IBlockState window, IBlockState mineral,
		int dyeColorIndex, boolean hasShop) {
		final boolean hasBeacon = !hasShop && mineral != null;
		IBlockState[][][][] stateMap = new IBlockState[6][][][];
		EnumFacing pressurePlateDir = null;
		int index = 0;
		for (EnumStateMap s : EnumStateMap.values()) {
			final String mapName = s.name(),
			lastChar = mapName.substring(mapName.length() - 1);
			if (StringUtils.isNumeric(lastChar)) {
				if (Integer.parseInt(lastChar) != dyeColorIndex + 1)
					continue;
			}
			final int mapIndex = EnumStateMap.valueOf(mapName).ordinal();
			final Map<EnumFacing, IBlockState[][][]> map =
				maps.get(wallBlock_external)[mapIndex];
			final IBlockState air = Blocks.AIR.getDefaultState(),
			lever = !hasBeacon ? Blocks.LEVER.getStateFromMeta(7) :
				beaconGlass2 != null ? beaconGlass2 : air,
			glass = Blocks.GLASS.getDefaultState(),
			lamp = !hasBeacon ? Blocks.REDSTONE_LAMP.getDefaultState() : commonGlass,
			wire = !hasBeacon ? Blocks.REDSTONE_WIRE.getDefaultState() : air;
			if (mapName.startsWith("MINI_TOWER_TOP"))
				map.put(dir, !hasShop ? getTopMap(air, wallBlock_external, carpetBlock,
					window, floorBlock, lever, mineral, pressurePlateDir) : getTopMapShop(air,
					wallBlock_external, carpetBlock, window, fenceBlock, mineral));
			else if (mapName.startsWith("MINI_TOWER_ROOF")) {
				if (mapName.equals("MINI_TOWER_ROOF_WIRE"))
					map.put(dir, getRoofWire(air, wallBlock_external, wire, beaconGlass3));
				else
					map.put(dir, getRoof(fenceBlock, glass, commonGlass, topGlass2, topGlass3));
			} else if (mapName.startsWith("MINI_TOWER_CEILING_"))
				map.put(dir, getCeiling(air, wallBlock_external, wallBlock, ceilBlock,
					commonGlass, lamp));
			else if (mapName.equals("MINI_TOWER_BASE")) {
				map.get(dir)[0][4][4] = floorBlock;
				if (floorBlock.getBlock() instanceof BlockTrapDoor) {
					pressurePlateDir = floorBlock.getValue(BlockTrapDoor.FACING);
					floorBlock = MazeTowers.BlockMazeTowerThreshold.getDefaultState();
				}
			}
			stateMap[index++] = map.get(dir);
		}
		
		return stateMap;
	}
	
	public static IBlockState[][][] getStateMap(EnumFacing dir, String mapName,
		IBlockState wallBlock, IBlockState wallBlock_external, IBlockState floorBlock,
		IBlockState ceilBlock, IBlockState fenceBlock, IBlockState carpetBlock,
		IBlockState glass1, IBlockState glass2, IBlockState glass3, IBlockState window,
		IBlockState mineral, int dyeColorIndex) {
		final boolean hasShop = fenceBlock != null;
		final boolean hasBeacon = !hasShop && mineral != null;
		final int mapIndex = EnumStateMap.valueOf(mapName).ordinal();
		final Map<EnumFacing, IBlockState[][][]> map =
			maps.get(wallBlock_external)[mapIndex];
		final IBlockState[][][] stateMap;
		final IBlockState air2 = Blocks.AIR.getDefaultState(),
		lever = !hasBeacon ? Blocks.LEVER.getStateFromMeta(7) :
			glass2 != null ? glass2 : air2,
		glass = Blocks.GLASS.getDefaultState(),
		lamp = !hasBeacon ? Blocks.REDSTONE_LAMP.getDefaultState() : glass1,
		wire = !hasBeacon ? Blocks.REDSTONE_WIRE.getDefaultState() : air2;
		if (mapName.startsWith("MINI_TOWER_TOP")) {
			EnumFacing pressurePlateDir = null;
			if (floorBlock.getBlock() instanceof BlockTrapDoor) {
				pressurePlateDir = floorBlock.getValue(BlockTrapDoor.FACING);
				floorBlock = MazeTowers.BlockMazeTowerThreshold.getDefaultState();
			}
			map.put(dir, !hasShop ? getTopMap(air2, wallBlock_external, carpetBlock,
				window, floorBlock, lever, mineral, pressurePlateDir) : getTopMapShop(air2,
				wallBlock_external, carpetBlock, window, fenceBlock, mineral));
		} else if (mapName.startsWith("MINI_TOWER_ROOF")) {
			if (mapName.equals("MINI_TOWER_ROOF_WIRE"))
				map.put(dir, getRoofWire(air2, wallBlock_external, wire, glass3));
			else
				map.put(dir, getRoof(fenceBlock, glass, glass1, glass2, glass3));
		} else if (mapName.startsWith("MINI_TOWER_CEILING_"))
			map.put(dir, getCeiling(air2, wallBlock_external, wallBlock, ceilBlock,
				glass1, lamp));
		else if (mapName.equals("MINI_TOWER_BASE")) {
			map.get(dir)[0][4][4] = floorBlock;
		}
				
		return map.get(dir);
	}
	
	public static IBlockState[][][][] getRawMapsForTower(MazeTower tower) {
		final EnumDyeColor[] dyeColors = tower.getDyeColors();
		final IBlockState air = tower.air,
			air2 = Blocks.AIR.getDefaultState(),
			wall = tower.wallBlock_external,
			wall2 = tower.wallBlock,
			floor = tower.floorBlock,
			c = tower.ceilBlock,
			fence = tower.fenceBlock,
			floor2 = !(wall.getBlock() instanceof BlockPrismarine) ? floor :
			Blocks.SEA_LANTERN.getDefaultState();
		final IBlockState[] carpet = new IBlockState[] {
			tower.getColourBlockState(Blocks.CARPET, dyeColors[0]),
			tower.getColourBlockState(Blocks.CARPET, dyeColors[1]),
			tower.getColourBlockState(Blocks.CARPET, dyeColors[2])
		},
		g = new IBlockState[] {
			tower.getColourBlockState(Blocks.STAINED_GLASS, dyeColors[0]),
			tower.getColourBlockState(Blocks.STAINED_GLASS, dyeColors[1]),
			tower.getColourBlockState(Blocks.STAINED_GLASS, dyeColors[2])
		},
		window = new IBlockState[] {
			tower.getColourBlockState(Blocks.STAINED_GLASS_PANE, dyeColors[0]),
			tower.getColourBlockState(Blocks.STAINED_GLASS_PANE, dyeColors[1]),
			tower.getColourBlockState(Blocks.STAINED_GLASS_PANE, dyeColors[2])
		};
		final IBlockState lever = Blocks.LEVER.getStateFromMeta(7),
			lamp = Blocks.REDSTONE_LAMP.getDefaultState(),
			wire = Blocks.REDSTONE_WIRE.getDefaultState();
		final IBlockState[] stairs = tower.stairsBlock;
		final IBlockState[][][][] rawMaps = new IBlockState[][][][] {
			{
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, floor, floor, floor, floor, floor, wall, null },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ null, wall, floor, floor, floor, floor, floor, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				}
			},
			{
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, wall, air, air, air, air, wall, null },
					{ wall, wall2, wall, air, air, air, air, wall2, wall },
					{ wall, wall2, wall, air, air, air, air, wall2, wall },
					{ wall, wall2, stairs[2], air, air, air, air, wall2, wall },
					{ null, wall, air, air, air, air, air, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, wall, air, air, air, air, wall, null },
					{ wall, wall2, wall, air, air, air, air, wall2, wall },
					{ wall, wall2, stairs[2], air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ null, wall, torch[3], air, air, air, air, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, floor, floor, floor, floor, floor, wall, null },
					{ wall, wall2, stairs[2], floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, air, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, air, floor, floor, floor, floor, wall2, wall },
					{ null, wall, air, floor, floor, floor, floor, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, air, stairs[3], wall, wall, wall, wall, null },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ null, wall, air, air, air, air, air, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, torch[0], air, stairs[3], wall, wall, wall, null },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ null, wall, air, air, air, air, air, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, air, air, air, stairs[3], floor, wall, null },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ null, wall, floor, floor, floor, floor, floor, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, air, air, air, air, air, wall, null },
					{ wall, wall2, air, air, air, air, stairs[0], wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ null, wall, air, air, air, air, wall, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, air, air, air, air, torch[1], wall, null },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, stairs[0], wall2, wall },
					{ wall, wall2, air, air, air, air, wall, wall2, wall },
					{ null, wall, air, air, air, air, wall, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, floor, floor, floor, floor, air, wall, null },
					{ wall, wall2, floor, floor, floor, floor, air, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, air, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, stairs[0], wall2, wall },
					{ null, wall, floor, floor, floor, floor, floor, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, air, air, air, air, air, wall, null },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ null, wall, wall, wall, wall, stairs[1], air, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, air, air, air, air, air, wall, null },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ wall, wall2, air, air, air, air, air, wall2, wall },
					{ null, wall, wall, wall, stairs[1], air, torch[2], wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				},
				{
					{ null, null, null, wall, wall, wall, null, null, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, wall, floor, floor, floor, floor, floor, wall, null },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor2, floor, floor, wall2, wall },
					{ wall, wall2, floor, floor, floor, floor, floor, wall2, wall },
					{ null, wall, floor, stairs[1], air, air, air, wall, null },
					{ null, null, wall, wall2, wall2, wall2, wall, null, null },
					{ null, null, null, wall, wall, wall, null, null, null }
				}
			},
			getRoofWire(air2, wall, wire, wire),
			getRoof(fence, Blocks.GLASS.getDefaultState(), g[0], g[1], g[2]),
			getTopMap(air2, wall2, carpet[0], floor, window[0], lever, null, null),
			getTopMap(air2, wall2, carpet[1], floor, window[1], lever, null, null),
			getTopMap(air2, wall2, carpet[2], floor, window[2], lever, null, null),
			getCeiling(air2, wall, wall2, c, g[0], lamp),
			getCeiling(air2, wall, wall2, c, g[1], lamp),
			getCeiling(air2, wall, wall2, c, g[2], lamp),/*,
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
		
		rawMaps[EnumStateMap.MINI_TOWER_TOP_2.ordinal()][2] =
			rawMaps[EnumStateMap.MINI_TOWER_TOP_1.ordinal()][2];
		rawMaps[EnumStateMap.MINI_TOWER_TOP_3.ordinal()][2] =
			rawMaps[EnumStateMap.MINI_TOWER_TOP_1.ordinal()][2];
		
		return rawMaps;
	}
	
	private static IBlockState[][][] getTopMap(IBlockState air2, IBlockState wall,
		IBlockState carpet, IBlockState window, IBlockState floor,
		IBlockState lever, IBlockState mineral, EnumFacing pressurePlateDir) {
		if (mineral == null) {
			mineral = carpet;
		} else
			floor = mineral;
		IBlockState[][][] topMap = new IBlockState[][][] {
			new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null },
				new IBlockState[] { null, null, wall, carpet, carpet, carpet, wall, null, null },
				new IBlockState[] { null, wall, carpet, carpet, carpet, carpet, carpet, wall, null },
				new IBlockState[] { wall, carpet, carpet, mineral, mineral, mineral, carpet, carpet, wall },
				new IBlockState[] { wall, carpet, carpet, mineral, floor, mineral, carpet, carpet, wall },
				new IBlockState[] { wall, carpet, carpet, mineral, mineral, mineral, carpet, carpet, wall },
				new IBlockState[] { null, wall, carpet, carpet, carpet, carpet, carpet, wall, null },
				new IBlockState[] { null, null, wall, carpet, carpet, carpet, wall, null, null },
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null }
			}, new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, window, wall, null, null, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { wall, air2, air2, air2, air2, air2, air2, air2, wall },
				new IBlockState[] { window, air2, air2, air2, air2, air2, air2, air2, window },
				new IBlockState[] { wall, air2, air2, air2, air2, air2, air2, air2, wall },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, null, null, wall, window, wall, null, null, null }
			}, new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { wall, air2, air2, air2, air2, air2, air2, air2, wall },
				new IBlockState[] { wall, air2, air2, air2, lever, air2, air2, air2, wall },
				new IBlockState[] { wall, air2, air2, air2, air2, air2, air2, air2, wall },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null }
			}
		};
		
		if (pressurePlateDir != null) {
			IBlockState pressurePlate = MazeTowers.BlockHiddenPressurePlateWeighted.getDefaultState();
			switch (pressurePlateDir.ordinal()) {
				case 5:
					topMap[0][4][5] = pressurePlate;
					break;
				case 3:
					topMap[0][5][4] = pressurePlate;
					break;
				case 4:
					topMap[0][4][3] = pressurePlate;
					break;
				case 2:
					topMap[0][3][4] = pressurePlate;
					break;
			}
		}
		
		return topMap;
	}
	
	private static IBlockState[][][] getTopMapShop(IBlockState air2, IBlockState wall,
		IBlockState carpet, IBlockState window, IBlockState fence, IBlockState spawner) {
		return new IBlockState[][][] {
			new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null },
				new IBlockState[] { null, null, wall, carpet, carpet, carpet, wall, null, null },
				new IBlockState[] { null, wall, carpet, carpet, carpet, carpet, carpet, wall, null },
				new IBlockState[] { wall, carpet, carpet, wall, wall, wall, carpet, carpet, wall },
				new IBlockState[] { wall, carpet, carpet, wall, spawner, wall, carpet, carpet, wall },
				new IBlockState[] { wall, carpet, carpet, wall, wall, wall, carpet, carpet, wall },
				new IBlockState[] { null, wall, carpet, carpet, carpet, carpet, carpet, wall, null },
				new IBlockState[] { null, null, wall, carpet, carpet, carpet, wall, null, null },
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null }
			}, new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, window, wall, null, null, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { wall, air2, air2, wall, wall, wall, air2, air2, wall },
				new IBlockState[] { window, air2, air2, wall, air2, wall, air2, air2, window },
				new IBlockState[] { wall, air2, air2, fence, air2, fence, air2, air2, wall },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, null, null, wall, window, wall, null, null, null }
			}, new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { wall, air2, air2, wall, wall, wall, air2, air2, wall },
				new IBlockState[] { wall, air2, air2, wall, air2, wall, air2, air2, wall },
				new IBlockState[] { wall, air2, air2, fence, fence, fence, air2, air2, wall },
				new IBlockState[] { null, wall, air2, air2, air2, air2, air2, wall, null },
				new IBlockState[] { null, null, wall, air2, air2, air2, wall, null, null },
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null }
			}
		};
	}
	
	private static IBlockState[][][] getCeiling(IBlockState air2, IBlockState wall,
		IBlockState wall2, IBlockState c, IBlockState g, IBlockState lamp) {
		return new IBlockState[][][] {
			new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null },
				new IBlockState[] { null, null, wall, wall2, wall2, wall2, wall, null, null },
				new IBlockState[] { null, wall, c, c, c, c, c, wall, null },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, lamp, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[]{ null, wall, c, c, c, c, c, wall, null },
				new IBlockState[] { null, null, wall, wall2, wall2, wall2, wall, null, null },
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null }
			},
			new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null },
				new IBlockState[] { null, null, wall, wall2, wall2, wall2, wall, null, null },
				new IBlockState[] { null, wall, lamp, c, c, c, lamp, wall, null },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, lamp, g, c, wall2, wall },
				new IBlockState[] { wall, wall2, c, g, g, g, c, wall2, wall },
				new IBlockState[] { null, wall, lamp, c, c, c, lamp, wall, null },
				new IBlockState[] { null, null, wall, wall2, wall2, wall2, wall, null, null },
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null }
			}
		};
	}
	
	private static IBlockState[][][] getRoofWire(IBlockState air2, IBlockState wall,
		IBlockState wire, IBlockState glass3) {
		return new IBlockState[][][] {
			new IBlockState[][] {
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null },
				new IBlockState[] { null, null, wall, null, wall, null, wall, null, null },
				new IBlockState[] { null, wall, wire, wire, wire, wire, wire, wall, null },
				new IBlockState[] { wall, null, wire, null, null, null, wire, null, wall },
				new IBlockState[] { wall, wall, wire, null, glass3, null, wire, wall, wall },
				new IBlockState[] { wall, null, wire, null, null, null, wire, null, wall },
				new IBlockState[] { null, wall, wire, wire, wire, wire, wire, wall, null },
				new IBlockState[] { null, null, wall, null, wall, null, wall, null, null },
				new IBlockState[] { null, null, null, wall, wall, wall, null, null, null }
			}
		};
	}
	
	private static IBlockState[][][] getRoof(IBlockState fence, IBlockState glass,
		IBlockState glass1, IBlockState glass2, IBlockState glass3) {
		return new IBlockState[][][] {
			new IBlockState[][] {
				new IBlockState[] { null, null, null, fence, fence, fence, null, null, null },
				new IBlockState[] { null, null, fence, fence, glass1, fence, fence, null, null },
				new IBlockState[] { null, fence, fence, glass1, glass2, glass1, fence, fence, null },
				new IBlockState[] { fence, fence, glass1, glass2, glass3, glass2, glass1, fence, fence },
				new IBlockState[] { fence, glass1, glass2, glass3, glass, glass3, glass2, glass1, fence },
				new IBlockState[] { fence, fence, glass1, glass2, glass3, glass2, glass1, fence, fence },
				new IBlockState[] { null, fence, fence, glass1, glass2, glass1, fence, fence, null },
				new IBlockState[] { null, null, fence, fence, glass1, fence, fence, null, null },
				new IBlockState[] { null, null, null, fence, fence, fence, null, null, null }
			}
		};
	}
	
	public static enum EnumStateMap implements IStringSerializable
    {
		MINI_TOWER_BASE("mini_tower_base"),
		MINI_TOWER_STAIRS("mini_tower_stairs"),
		MINI_TOWER_ROOF_WIRE("mini_tower_roof_wire"),
		MINI_TOWER_ROOF("mini_tower_roof"),
		MINI_TOWER_TOP_1("mini_tower_top_1"),
		MINI_TOWER_TOP_2("mini_tower_top_2"),
		MINI_TOWER_TOP_3("mini_tower_top_3"),
		MINI_TOWER_CEILING_1("mini_tower_ceiling_1"),
		MINI_TOWER_CEILING_2("mini_tower_ceiling_2"),
		MINI_TOWER_CEILING_3("mini_tower_ceiling_3"),;

        private final String name;

        private EnumStateMap(String name)
        {
            this.name = name;
        }

        @Override
		public String toString()
        {
            return this.name;
        }

        @Override
		public String getName()
        {
            return this.name;
        }
    }
}
