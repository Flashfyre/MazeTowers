package com.samuel.mazetowers.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.google.common.base.Predicate;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModBlocks;

public class WorldGenSpectrite implements IWorldGenerator {
	
	private final WorldGenerator spectriteMinable;
	
	public WorldGenSpectrite() {
		super();
		spectriteMinable = new WorldGenSpectriteMinable();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
		IChunkProvider chunkProvider) {
		
		switch(world.provider.getDimension()) {
			case 0:
				generateSurface(world, random, chunkX, chunkZ);
				break;
			case -1:
				generateNether(world, random, chunkX, chunkZ);
				break;
			case 1:
				generateEnd(world, random, chunkX, chunkZ);
				break;
			default:
		}	
		
	}
	
	private void generateOre(Block block, World world, Random rand, int chunkX, int chunkZ,
		int chancesToSpawn, int minHeight, int maxHeight) {
		if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
	        throw new IllegalArgumentException("Illegal Height Arguments for WorldGenSpectrite");

	    int heightDiff = maxHeight - minHeight + 1;
	    for (int i = 0; i < chancesToSpawn; i ++) {
	        int x = chunkX * 16 + rand.nextInt(16);
	        int y = minHeight + rand.nextInt(heightDiff);
	        int z = chunkZ * 16 + rand.nextInt(16);
	        spectriteMinable.generate(world, rand, new BlockPos(x, y, z));
	    }
	}
	
	private void generateEnd(World world, Random random, int chunkX, int chunkZ) {
		generateOre(ModBlocks.spectriteOre, world, random, chunkX, chunkZ, 2, 4, 55);
	}

	private void generateSurface(World world, Random random, int chunkX, int chunkZ) {
		generateOre(ModBlocks.spectriteOre, world, random, chunkX, chunkZ, 1, 0, 16);
	}

	private void generateNether(World world, Random random, int chunkX, int chunkZ) {
		generateOre(ModBlocks.spectriteOre, world, random, chunkX, chunkZ, 2, 0, 127);
	}
	
	public class WorldGenSpectriteMinable extends WorldGenerator {

		private final IBlockState stateSurface = MazeTowers.BlockSpectriteOre.getDefaultState(),
		stateNether = MazeTowers.BlockSpectriteOre.getStateFromMeta(1),
		stateEnd = MazeTowers.BlockSpectriteOre.getStateFromMeta(2);
	    private final Predicate<IBlockState> targetSurface = BlockStateMatcher.forBlock(Blocks.STONE),
	    targetNether = BlockStateMatcher.forBlock(Blocks.NETHERRACK),
	    targetEnd = BlockStateMatcher.forBlock(Blocks.END_STONE);

	    public WorldGenSpectriteMinable() { }

	    @Override
	    public boolean generate(World world, Random rand, BlockPos pos) {
	    	final boolean isSurface = world.provider.getDimension() == 0,
	    	isNether = world.provider.getDimension() == -1,
	    	isEnd = world.provider.getDimension() == 1;
	    	final Block matchBlock = isSurface ? Blocks.STONE : isNether ? Blocks.NETHERRACK : Blocks.END_STONE;
	    	final IBlockState oreState = isSurface ? stateSurface : isNether ? stateNether : stateEnd;
    		final int veinSize = rand.nextInt(rand.nextInt(rand.nextInt(isSurface ? 4 : isNether ? 6 : 10) + 1) + 1) + 1;
    		new WorldGenMinable(oreState, veinSize, BlockMatcher.forBlock(matchBlock)).generate(world, rand, pos);
    	    return true;
	    }
	}
}