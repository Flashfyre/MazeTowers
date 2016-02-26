package com.samuel.mazetowers.etc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.samuel.mazetowers.client.gui.KeyStackSlot;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;

public class ContainerItemScanner extends Container {
	private IInventory scannerInv;
	private IInventory keyInv;
	private int numRows;

	public ContainerItemScanner(IInventory playerInventory,
		IInventory scannerInventory, EntityPlayer player) {
		keyInv = (IInventory) new InventoryBasic("Key",
			true, 1);
		keyInv.setInventorySlotContents(0,
			((TileEntityItemScanner) scannerInventory)
				.getKeyStack());
		scannerInv = scannerInventory;
		this.numRows = scannerInventory.getSizeInventory() / 9;
		scannerInventory.openInventory(player);

		int i = (this.numRows - 4) * 18;

		for (int j = 0; j < this.numRows; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.addSlotToContainer(new Slot(
					scannerInventory, k + j * 9,
					8 + k * 18, 18 + j * 18));
			}
		}

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlotToContainer(new Slot(
					playerInventory, j1 + l * 9 + 9,
					8 + j1 * 18, 103 + l * 18 + i));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlotToContainer(new Slot(
				playerInventory, i1, 8 + i1 * 18, 161 + i));
		}

		this.addSlotToContainer(new KeyStackSlot(keyInv, 0,
			184, 18,
			(TileEntityItemScanner) scannerInventory));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.scannerInv.isUseableByPlayer(playerIn);
	}

	@Override
	/**
	 * Take a stack from the specified inventory slot.
	 */
	public ItemStack transferStackInSlot(
		EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.numRows * 9) {
				if (!this.mergeItemStack(itemstack1,
					this.numRows * 9, this.inventorySlots
						.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0,
				this.numRows * 9, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.scannerInv.closeInventory(playerIn);
	}

	/**
	 * Return this item scanner's inventory.
	 */
	public IInventory getScannerInventory() {
		return this.scannerInv;
	}
}