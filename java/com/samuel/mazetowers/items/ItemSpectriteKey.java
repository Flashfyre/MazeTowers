package com.samuel.mazetowers.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModSounds;

public class ItemSpectriteKey extends ItemKey {
	
	public ItemSpectriteKey() {
		super(5, 9, 10, 100, 20, 50);
		this.addPropertyOverride(new ResourceLocation("time"), MazeTowers.ItemPropertyGetterSpectrite);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
	    World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			IBlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof BlockDoor) {
				boolean isOpen = (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER ?
					worldIn.getBlockState(pos.down()) : state).getValue(BlockDoor.OPEN);
				((BlockDoor) block).toggleDoor(worldIn, pos, !isOpen);
				worldIn.playSound(null, pos, ModSounds.door_unlock, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return EnumActionResult.SUCCESS;
			}
		}
		
		return EnumActionResult.PASS;
	}
}
