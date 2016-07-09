package com.samuel.mazetowers.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
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
		if (random.nextInt(4) == 0)
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
	    	final int veinTier = rand.nextInt(isSurface ? 7 : isNether ? 5 : 3) == 0 ?
	    		rand.nextInt(isSurface ? 7 : isNether ? 5 : 3) == 0 ? 2 : 1 : 0;
	    	final IBlockState state = world.getBlockState(pos),
	    	oreState = isSurface ? stateSurface : isNether ? stateNether : stateEnd;
	    	if ((isSurface || isNether || isEnd) && state.getBlock()
	    		.isReplaceableOreGen(state, world, pos, isSurface ? targetSurface : isNether ? targetNether : targetEnd)) {
	    		final int veinSize = rand.nextBoolean() ? 1 : rand.nextInt(veinTier == 0 ? 3 : veinTier == 1 ? 7 : 15) + 1;
	    		boolean offsetX = rand.nextBoolean(), offsetY = rand.nextBoolean(), offsetZ = rand.nextBoolean();
	    		if (!offsetX && !offsetZ && !offsetY) {
	    			final int offsetChance = rand.nextInt(3);
	    			if (offsetChance == 0)
	    				offsetX = true;
	    			else if (offsetChance == 1)
	    				offsetY = true;
	    			else
	    				offsetZ = true;
	    		}
	    		world.setBlockState(pos, oreState);
	    		for (int o = 0; o < veinSize - 1; o++) {
	    			BlockPos offsetPos;
	    			IBlockState curState;
	    			do {
	    				offsetPos = (offsetX ? pos.offset(EnumFacing.EAST, (rand.nextInt(3) + 1) *
    	    				(rand.nextBoolean() ? 1 : -1)) : pos).offset(EnumFacing.UP, offsetY ? (rand.nextInt(3) + 1) *
    	    	    		(rand.nextBoolean() ? 1 : -1) : 0).offset(EnumFacing.SOUTH, offsetZ ?
    	    	    		(rand.nextInt(3) + 1) * (rand.nextBoolean() ? 1 : -1) : 0);
	    				curState = world.getBlockState(offsetPos);
	    			} while ((isSurface && offsetPos.getY() <= 0) || (isNether && offsetPos.getY() >= 127));
	    			if (curState.getBlock().isReplaceableOreGen(curState, world, offsetPos,
	    				isSurface ? targetSurface : isNether ? targetNether : targetEnd))
	    				world.setBlockState(offsetPos, oreState);
	    		}
	    	}
    	    return true;
	    }
	}
}