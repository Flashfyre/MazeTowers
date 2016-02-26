package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.samuel.mazetowers.entities.EntityExplosiveArrow;
import com.samuel.mazetowers.init.ModItems;

public class ItemExplosiveBow extends ItemBow {

	public ItemExplosiveBow() {
		super();
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}

	@Override
	/**
	 * Player, Render pass, and item usage sensitive version of getIconIndex.
	 *
	 * @param stack The item stack to get the icon for.
	 * @param player The player holding the item
	 * @param useRemaining The ticks remaining for the active item.
	 * @return Null to use default model, or a custom ModelResourceLocation for the stage of use.
	 */
	public ModelResourceLocation getModel(ItemStack stack,
		EntityPlayer player, int useRemaining) {
		if (player.isUsingItem()) {
			long ticksSinceLastUse = player
				.getItemInUseDuration();
			if (ticksSinceLastUse >= 18) {
				return new ModelResourceLocation(
					"mazetowers:explosive_bow_pulling_2",
					"inventory");
			} else if (ticksSinceLastUse > 13) {
				return new ModelResourceLocation(
					"mazetowers:explosive_bow_pulling_1",
					"inventory");
			} else if (ticksSinceLastUse > 0) {
				return new ModelResourceLocation(
					"mazetowers:explosive_bow_pulling_0",
					"inventory");
			}
		}
		return null;
	}

	@Override
	public void addInformation(ItemStack stack,
		EntityPlayer player, List list, boolean Adva) {
		int lineCount = 0;
		boolean isLastLine = false;
		String curLine;
		while (!isLastLine) {
			isLastLine = (curLine = StatCollector
				.translateToLocal(("iteminfo.explosive_bow.l" + ++lineCount)))
				.endsWith("@");
			list.add(!isLastLine ? curLine : curLine
				.substring(0, curLine.length() - 1));
		}
	}

	@Override
	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	public ItemStack onItemUseFinish(ItemStack stack,
		World worldIn, EntityPlayer playerIn) {
		return stack;
	}

	@Override
	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	/**
	 * Called when the player stops using an Item (stops holding the right mouse button).
	 *  
	 * @param timeLeft The amount of ticks left before the using would have been complete
	 */
	public void onPlayerStoppedUsing(ItemStack stack,
		World worldIn, EntityPlayer playerIn, int timeLeft) {
		int j = this.getMaxItemUseDuration(stack)
			- timeLeft;
		net.minecraftforge.event.entity.player.ArrowLooseEvent event = new net.minecraftforge.event.entity.player.ArrowLooseEvent(
			playerIn, stack, j);
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS
			.post(event))
			return;
		j = event.charge;

		boolean flag = playerIn.capabilities.isCreativeMode
			|| EnchantmentHelper.getEnchantmentLevel(
				Enchantment.infinity.effectId, stack) > 0;
		if (playerIn.inventory
			.hasItem(ModItems.explosive_arrow)
			|| flag) {
			float f = (float) j / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((double) f < 0.1D) {
				return;
			}

			if (f > 1.0F) {
				f = 1.0F;
			}

			EntityExplosiveArrow entityarrow = null;
			entityarrow = new EntityExplosiveArrow(worldIn,
				playerIn, f * 2.0F);

			if (f == 1.0F) {
				entityarrow.setIsCritical(true);
			}

			int k = EnchantmentHelper.getEnchantmentLevel(
				Enchantment.power.effectId, stack);

			if (k > 0) {
				entityarrow.setDamage(entityarrow
					.getDamage()
					+ (double) k * 0.5D + 0.5D);
			}

			int l = EnchantmentHelper.getEnchantmentLevel(
				Enchantment.punch.effectId, stack);

			if (l > 0) {
				entityarrow
					.setKnockbackStrength(entityarrow
						.getKnockbackStrength() + 1);
			}

			if (EnchantmentHelper.getEnchantmentLevel(
				Enchantment.flame.effectId, stack) > 0) {
				entityarrow.setFire(100);
			}

			worldIn.playSoundAtEntity(playerIn,
				"random.bow", 1.0F, 1.0F
					/ (itemRand.nextFloat() * 0.4F + 1.2F)
					+ f * 0.5F);

			if (flag) {
				entityarrow.canBePickedUp = 2;
			} else {
				playerIn.inventory
					.consumeInventoryItem(ModItems.explosive_arrow);
			}

			playerIn
				.triggerAchievement(StatList.objectUseStats[Item
					.getIdFromItem(this)]);

			if (!worldIn.isRemote) {
				worldIn.spawnEntityInWorld(entityarrow);
			}
		}
	}

	@Override
	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(
		ItemStack itemStackIn, World worldIn,
		EntityPlayer playerIn) {
		net.minecraftforge.event.entity.player.ArrowNockEvent event = new net.minecraftforge.event.entity.player.ArrowNockEvent(
			playerIn, itemStackIn);
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS
			.post(event))
			return event.result;

		if (!playerIn.isUsingItem()
			&& (playerIn.capabilities.isCreativeMode
				|| playerIn.inventory.hasItem(Items.arrow) || playerIn.inventory
					.hasItem(ModItems.explosive_arrow)))
			playerIn.setItemInUse(itemStackIn, this
				.getMaxItemUseDuration(itemStackIn));

		return itemStackIn;
	}
}