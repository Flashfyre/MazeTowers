package com.samuel.mazetowers.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.samuel.mazetowers.tileentities.TileEntityItemScanner;

public class KeyStackSlot extends Slot {

	TileEntityItemScanner scanner;

	public KeyStackSlot(IInventory inventory, int index,
		int xPosition, int yPosition,
		TileEntityItemScanner scanner) {
		super(inventory, index, xPosition, yPosition);
		this.scanner = scanner;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	/**
	 * Helper method to put a stack in the slot.
	 */
	public void putStack(ItemStack stack) {
		this.inventory.setInventorySlotContents(0, stack);
		scanner.setKeyStack(stack);
		this.onSlotChanged();
	}

	@Override
	/**
	 * if par2 has more items than par1, onCrafting(item,countIncrease) is called
	 */
	public void onSlotChange(ItemStack p_75220_1_,
		ItemStack p_75220_2_) {
		scanner.setKeyStack(p_75220_2_);
		if (p_75220_1_ != null && p_75220_2_ != null) {
			if (p_75220_1_.getItem() == p_75220_2_
				.getItem()) {
				int i = p_75220_2_.stackSize
					- p_75220_1_.stackSize;

				if (i > 0) {
					this.onCrafting(p_75220_1_, i);
				}
			}
		}
	}

	@Override
	public void onPickupFromSlot(EntityPlayer playerIn,
		ItemStack stack) {
		scanner.setKeyStack(null);
		this.onSlotChanged();
	}

	@Override
	/**
	 * Return whether this slot's stack can be taken from this slot.
	 */
	public boolean canTakeStack(EntityPlayer playerIn) {
		return true;
	}
}
