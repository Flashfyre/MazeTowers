package com.samuel.mazetowers.tileentities;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.samuel.mazetowers.blocks.BlockItemScannerGold;
import com.samuel.mazetowers.etc.ContainerItemScanner;

public class TileEntityItemScanner extends TileEntity implements IInventory {

	private int entityId;
	private String customName;
	private String ownerName;
	private ItemStack keyStack;
	private ItemStack[] inventory = new ItemStack[18];

	public TileEntityItemScanner() {
		ownerName = "";
		entityId = -1;
		keyStack = null;
	}

	public void generateRandomKeyStack() {
		keyStack = new ItemStack(Items.diamond);
		markDirty();
	}

	public void generateRandomKeyStackFromList(
		List<ItemStack> itemsList) {
		ItemStack listStack = itemsList.get(worldObj.rand
			.nextInt(itemsList.size()));
		keyStack = new ItemStack(listStack.getItem(), 1,
			listStack.getItemDamage());
		markDirty();
	}

	public void setKeyStack(ItemStack stackIn) {
		keyStack = stackIn;
		markDirty();
	}

	public void setOwnerName(String ownerNameIn) {
		ownerName = ownerNameIn;
		markDirty();
	}

	public void setEntityId(int entityIdIn) {
		entityId = entityIdIn;
		markDirty();
	}

	public String getOwnerName() {
		return ownerName;
	}

	public boolean getItemsMatch() {
		if (this.worldObj.isRemote)
			return false;

		boolean itemsMatch = false;

		if (keyStack != null) {
			if (this.worldObj.getEntityByID(entityId) != null) {
				EntityPlayer player = (EntityPlayer) this.worldObj
					.getEntityByID(entityId);
				ItemStack matchStack = player.getHeldItem();
				if (matchStack != null) {
					if (matchStack.isItemEqual(keyStack)
						&& keyStack
							.getDisplayName()
							.equals(
								matchStack.getDisplayName())) {
						if (player.worldObj.getBlockState(
							pos).getBlock() instanceof BlockItemScannerGold) {
							this.addStackToInventory(player
								.getHeldItem());
							player.inventory
								.decrStackSize(
									player.inventory.currentItem,
									1);
						}
						itemsMatch = true;
					}
				}
			}
		} else
			itemsMatch = true;

		return itemsMatch;
	}

	private void addStackToInventory(ItemStack stack) {
		for (int s = 0; s < inventory.length; s++) {
			boolean addToStack = false;
			if (inventory[s] == null
				|| (addToStack = (inventory[s]
					.areItemsEqual(inventory[s], stack) && inventory[s]
					.getMaxStackSize() > inventory[s].stackSize))) {
				if (addToStack)
					inventory[s].stackSize++;
				else
					inventory[s] = stack;
				break;
			}
		}
	}

	@Override
	/**
	 * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity should be re-created when the ID, or Metadata changes.
	 * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
	 *
	 * @param world Current world
	 * @param pos Tile's world position
	 * @param oldID The old ID of the block
	 * @param newID The new ID of the block (May be the same)
	 * @return True to remove the old tile entity, false to keep it in tact {and create a new one if the new values specify to}
	 */
	public boolean shouldRefresh(World world, BlockPos pos,
		IBlockState oldState, IBlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		ownerName = compound.getString("ownerName");
		entityId = compound.getInteger("entityId");
		keyStack = ItemStack.loadItemStackFromNBT(compound
			.getCompoundTag("keyStack"));
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("ownerName", ownerName);
		compound.setInteger("entityId", entityId);
		if (keyStack != null)
			compound.setTag("keyStack", keyStack
				.writeToNBT(new NBTTagCompound()));
	}

	@Override
	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return 18;
	}

	@Override
	/**
	 * Returns the stack in the given slot.
	 *  
	 * @param index The slot to retrieve from.
	 */
	public ItemStack getStackInSlot(int index) {
		return this.inventory[index];
	}

	@Override
	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 *  
	 * @param index The slot to remove from.
	 * @param count The maximum amount of items to remove.
	 */
	public ItemStack decrStackSize(int index, int count) {
		if (this.inventory[index] != null) {
			if (this.inventory[index].stackSize <= count) {
				ItemStack itemstack1 = this.inventory[index];
				this.inventory[index] = null;
				this.markDirty();
				return itemstack1;
			} else {
				ItemStack itemstack = this.inventory[index]
					.splitStack(count);

				if (this.inventory[index].stackSize == 0) {
					this.inventory[index] = null;
				}

				this.markDirty();
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	/**
	 * Removes a stack from the given slot and returns it.
	 *  
	 * @param index The slot to remove a stack from.
	 */
	public ItemStack getStackInSlotOnClosing(int index) {
		if (this.inventory[index] != null) {
			ItemStack itemstack = this.inventory[index];
			this.inventory[index] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(int index,
		ItemStack stack) {
		this.inventory[index] = stack;

		if (stack != null
			&& stack.stackSize > this
				.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getCommandSenderName() {
		return this.hasCustomName() ? this.customName
			: "mazetowers:container.item_scanner";
	}

	public ItemStack getKeyStack() {
		return keyStack;
	}

	@Override
	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName() {
		return this.customName != null
			&& this.customName.length() > 0;
	}

	public void setName(String name) {
		this.customName = name;
	}

	@Override
	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player) {
		TileEntity te;
		return (te = this.worldObj.getTileEntity(this.pos)) != this ? false
			: player.getDistanceSq(
				(double) this.pos.getX() + 0.5D,
				(double) this.pos.getY() + 0.5D,
				(double) this.pos.getZ() + 0.5D) <= 64.0D
				&& (player.capabilities.isCreativeMode || ((TileEntityItemScanner) te)
					.getOwnerName().equals(
						player.getDisplayNameString()));
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	public boolean isItemValidForSlot(int index,
		ItemStack stack) {
		return stack.getItem() != null;
	}

	public String getGuiID() {
		return "mazetowers:item_scanner";
	}

	public Container createContainer(
		InventoryPlayer playerInventory,
		EntityPlayer playerIn) {
		return new ContainerItemScanner(playerInventory,
			this, playerIn);
	}

	@Override
	public IChatComponent getDisplayName() {
		String name = StatCollector
			.translateToLocal((blockType != null ? blockType
				.getUnlocalizedName()
				: "tile.item_scanner")
				+ ".name");
		return new ChatComponentText(name);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.inventory.length; ++i) {
			this.inventory[i] = null;
		}
	}
}
