package com.samuel.mazetowers.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.samuel.mazetowers.init.ModSounds;

public class BlockItemScannerGold extends BlockItemScanner {

	public BlockItemScannerGold() {
		super(Material.IRON, MapColor.GOLD);
	}

	@Override
	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World worldIn) {
		return 40;
	}

	@Override
	public void setStateBasedOnMatchResult(World worldIn,
		BlockPos pos, IBlockState state, boolean correctItem) {
		worldIn.setBlockState(pos, state.withProperty(
			STATE, correctItem ? 3 : 2));
		this.notifyNeighbors(worldIn, pos,
			state.getValue(FACING));
		if (!worldIn.isRemote)
			worldIn.playSound(null, pos, correctItem ? ModSounds.correct :
				ModSounds.incorrect, SoundCategory.BLOCKS, 0.15F, 1.0F);
		worldIn.markBlockRangeForRenderUpdate(pos, pos);
		worldIn.scheduleUpdate(pos, this, (int) (this
			.tickRate(worldIn) * (correctItem ? 2 : 0.5)));
	}
}