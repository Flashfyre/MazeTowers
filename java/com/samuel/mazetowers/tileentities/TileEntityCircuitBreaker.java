package com.samuel.mazetowers.tileentities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3;

public class TileEntityCircuitBreaker extends TileEntity implements ITickable {
	
	int timer;
	
	public TileEntityCircuitBreaker() {
		timer = 0;
	}

	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			boolean isValid = false;
			boolean isRedstone = false;
			IBlockState state;
			if ((state = this.worldObj.getBlockState(pos)) != null &&
				(isRedstone = state.getBlock() == Blocks.redstone_wire)) {
				if (state.getValue(Blocks.redstone_wire.POWER) != 0) {
					BlockPos belowPos = getPos().down();
					IBlockState belowState = this.worldObj.getBlockState(belowPos);
					TileEntity belowTileEntity;
					if (belowState != null && (belowTileEntity = this.worldObj.getTileEntity(belowPos)) != null) {
						if (belowTileEntity instanceof TileEntityDispenser &&
							((TileEntityDispenser) belowTileEntity).getDispenseSlot() != -1)
							isValid = true;
					}
				} else
					isValid = true;
			}
			if (!isValid) {
				if (isRedstone) {
					this.worldObj.setBlockToAir(pos);
				} else {
					if (timer == 0) {
						this.invalidate();
						this.worldObj.setTileEntity(pos, null);
						this.setWorldObj(null);
					} else if (timer >= 50) {
						timer = 0;
						this.worldObj.setBlockState(pos, Blocks.redstone_wire.getDefaultState());
					}
				}
				timer++;
			}
		}
	}
}
