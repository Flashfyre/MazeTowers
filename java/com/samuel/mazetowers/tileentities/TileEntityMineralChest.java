package com.samuel.mazetowers.tileentities;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.StatCollector;

import com.samuel.mazetowers.blocks.BlockMineralChest;
import com.samuel.mazetowers.etc.ContainerMineralChest;
import com.samuel.mazetowers.etc.InventoryLargeMineralChest;

public class TileEntityMineralChest extends TileEntityChest
	implements ITickable, IInventory {
	private int ticksSinceSync;
	private int cachedChestType;
	private String customName;

	public TileEntityMineralChest() {
		this.cachedChestType = -1;
	}

	public TileEntityMineralChest(int chestType) {
		this.cachedChestType = chestType;
	}

	@Override
	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getCommandSenderName() {
		return this.hasCustomName() ? this.customName
			: cachedChestType == 0 ? "mazetowers:container.iron_chest"
				: cachedChestType == 1 ? "mazetowers:container.gold_chest"
					: "mazetowers:container.diamond_chest";
	}

	@Override
	/**
	 * Returns true if this thing is named
	 */
	public boolean hasCustomName() {
		return this.customName != null
			&& this.customName.length() > 0;
	}

	public void setCustomName(String name) {
		super.setCustomName(name);
		this.customName = name;
	}

	@Override
	public IChatComponent getDisplayName() {
		String name = StatCollector
			.translateToLocal((blockType != null ? blockType
				.getUnlocalizedName()
				: "tile.")
				+ (cachedChestType == 0 ? "iron"
					: cachedChestType == 1 ? "gold"
						: "diamond") + "_chest.name");
		return new ChatComponentText(name);
	}

	public int getChestType() {
		if (this.cachedChestType == -1) {
			if (this.worldObj == null
				|| !(this.getBlockType() instanceof BlockMineralChest)) {
				return 0;
			}

			this.cachedChestType = ((BlockMineralChest) this
				.getBlockType()).chestType;
		}

		return this.cachedChestType;
	}

	@Override
	protected TileEntityChest getAdjacentChest(
		EnumFacing side) {
		BlockPos blockpos = this.pos.offset(side);

		if (this.isChestAt(blockpos)) {
			TileEntity tileentity = this.worldObj
				.getTileEntity(blockpos);

			if (tileentity instanceof TileEntityChest) {
				TileEntityMineralChest tileentitychest = (TileEntityMineralChest) tileentity;
				tileentitychest.func_174910_a(
					tileentitychest, side);
				return tileentitychest;
			}
		}

		return null;
	}

	@SuppressWarnings("incomplete-switch")
	private void func_174910_a(
		TileEntityMineralChest chestTe, EnumFacing side) {
		if (chestTe.isInvalid()) {
			this.adjacentChestChecked = false;
		} else if (this.adjacentChestChecked) {
			switch (side) {
			case NORTH:

				if (this.adjacentChestZNeg != chestTe) {
					this.adjacentChestChecked = false;
				}

				break;
			case SOUTH:

				if (this.adjacentChestZPos != chestTe) {
					this.adjacentChestChecked = false;
				}

				break;
			case EAST:

				if (this.adjacentChestXPos != chestTe) {
					this.adjacentChestChecked = false;
				}

				break;
			case WEST:

				if (this.adjacentChestXNeg != chestTe) {
					this.adjacentChestChecked = false;
				}
			}
		}
	}

	private boolean isChestAt(BlockPos posIn) {
		if (this.worldObj == null) {
			return false;
		} else {
			Block block = this.worldObj
				.getBlockState(posIn).getBlock();
			return block instanceof BlockMineralChest
				&& ((BlockMineralChest) block).chestType == this
					.getChestType();
		}
	}

	@Override
	/**
	 * Updates the JList with a new model.
	 */
	public void update() {
		this.checkForAdjacentChests();
		int var1 = this.pos.getX();
		int var2 = this.pos.getY();
		int var3 = this.pos.getZ();
		++this.ticksSinceSync;
		float var4;

		if (!this.worldObj.isRemote
			&& this.numPlayersUsing != 0
			&& (this.ticksSinceSync + var1 + var2 + var3) % 200 == 0) {
			this.numPlayersUsing = 0;
			var4 = 5.0F;
			List var5 = this.worldObj
				.getEntitiesWithinAABB(
					EntityPlayer.class,
					new AxisAlignedBB(
						(double) ((float) var1 - var4),
						(double) ((float) var2 - var4),
						(double) ((float) var3 - var4),
						(double) ((float) (var1 + 1) + var4),
						(double) ((float) (var2 + 1) + var4),
						(double) ((float) (var3 + 1) + var4)));
			Iterator var6 = var5.iterator();

			while (var6.hasNext()) {
				EntityPlayer var7 = (EntityPlayer) var6
					.next();

				if (var7.openContainer instanceof ContainerMineralChest) {
					IInventory var8 = ((ContainerMineralChest) var7.openContainer)
						.getLowerChestInventory();

					if (var8 == this
						|| var8 instanceof InventoryLargeMineralChest
						&& ((InventoryLargeMineralChest) var8)
							.isPartOfLargeChest(this)) {
						++this.numPlayersUsing;
					}
				}
			}
		}

		this.prevLidAngle = this.lidAngle;
		var4 = 0.1F;
		double var14;

		if (this.numPlayersUsing > 0
			&& this.lidAngle == 0.0F
			&& this.adjacentChestZNeg == null
			&& this.adjacentChestXNeg == null) {
			double var11 = (double) var1 + 0.5D;
			var14 = (double) var3 + 0.5D;

			if (this.adjacentChestZPos != null) {
				var14 += 0.5D;
			}

			if (this.adjacentChestXPos != null) {
				var11 += 0.5D;
			}

			this.worldObj
				.playSoundEffect(
					var11,
					(double) var2 + 0.5D,
					var14,
					"random.chestopen",
					0.5F,
					this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (this.numPlayersUsing == 0
			&& this.lidAngle > 0.0F
			|| this.numPlayersUsing > 0
			&& this.lidAngle < 1.0F) {
			float var12 = this.lidAngle;

			if (this.numPlayersUsing > 0) {
				this.lidAngle += var4;
			} else {
				this.lidAngle -= var4;
			}

			if (this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			float var13 = 0.5F;

			if (this.lidAngle < var13 && var12 >= var13
				&& this.adjacentChestZNeg == null
				&& this.adjacentChestXNeg == null) {
				var14 = (double) var1 + 0.5D;
				double var9 = (double) var3 + 0.5D;

				if (this.adjacentChestZPos != null) {
					var9 += 0.5D;
				}

				if (this.adjacentChestXPos != null) {
					var14 += 0.5D;
				}

				this.worldObj
					.playSoundEffect(
						var14,
						(double) var2 + 0.5D,
						var9,
						"random.chestclosed",
						0.5F,
						this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}
	}

	@Override
	/**
	 * invalidates a tile entity
	 */
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
		this.checkForAdjacentChests();
	}

	@Override
	public String getGuiID() {
		return "minecraft:chest";
	}

	@Override
	public Container createContainer(
		InventoryPlayer playerInventory,
		EntityPlayer playerIn) {
		return new ContainerMineralChest(playerInventory,
			this, playerIn);
	}
}
