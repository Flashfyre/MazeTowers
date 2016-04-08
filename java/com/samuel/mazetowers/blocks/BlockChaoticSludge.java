package com.samuel.mazetowers.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import com.samuel.mazetowers.tileentity.TileEntityChaoticSludgeToxin;

public class BlockChaoticSludge extends BlockFluidClassic
	implements ITileEntityProvider {

	public BlockChaoticSludge(Fluid fluid,
		String unlocalizedName) {
		super(fluid, Material.water);
		this.setHardness(100f);
		this.setResistance(500f);
		this.setUnlocalizedName(unlocalizedName);
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn,
		BlockPos pos) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityChaoticSludgeToxin();
	}
}
