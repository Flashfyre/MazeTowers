package com.samuel.mazetowers.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.entity.EntityExplosiveArrow;
import com.samuel.mazetowers.init.ModItems;

public class ItemExplosiveBow extends ItemBow {

	public ItemExplosiveBow() {
		super();
		this.setCreativeTab(CreativeTabs.COMBAT);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
        {
			@Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn)
            {
                if (entityIn == null)
                {
                    return 0.0F;
                }
                else
                {
                    ItemStack itemstack = entityIn.getActiveItemStack();
                    return itemstack != null && itemstack.getItem() == ModItems.explosive_bow ?
                    	(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
                }
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter()
        {
        	@Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn)
            {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
	}

	@Override
	public void addInformation(ItemStack stack,
		EntityPlayer player, List list, boolean Adva) {
		int lineCount = 0;
		boolean isLastLine = false;
		String curLine;
		while (!isLastLine) {
			isLastLine = (curLine = I18n
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
		World worldIn, EntityLivingBase playerIn) {
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
	
    private ItemStack findAmmo(EntityPlayer player)
    {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return null;
        }
    }

	
	@Override
	protected boolean isArrow(@Nullable ItemStack stack)
    {
        return stack != null &&
        	(stack.getItem() instanceof ItemArrow ||
        	stack.getItem() instanceof ItemExplosiveArrow);
    }
	
	private static ItemStack getFirstArrowStack(ItemStack[] inventory) {
		ItemStack arrowStack;
		for (int i = 0; i < inventory.length; ++i)
        {
			final Item arrowItem = inventory[i] == null ? null : inventory[i].getItem();
            if (arrowItem == Items.ARROW || arrowItem == ModItems.explosive_arrow)
            {
                return inventory[i];
            }
        }

        return null;
	}

	@Override
	/**
	 * Called when the player stops using an Item (stops holding the right mouse button).
	 *  
	 * @param timeLeft The amount of ticks left before the using would have been complete
	 */
	public void onPlayerStoppedUsing(ItemStack stack,
		World worldIn, EntityLivingBase playerIn, int timeLeft) {
		if (playerIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)playerIn;
            boolean flag = entityplayer.capabilities.isCreativeMode ||
            	EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemStack =  this.findAmmo(entityplayer);
            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer)playerIn, i, itemStack != null || flag);
            if (i < 0) return;
            
            final ItemStack helmetStack = playerIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			final Item helmetItem = helmetStack == null ? null : helmetStack.getItem();
	
			boolean isExplosiveArrow = (itemStack != null &&
				itemStack.getItem() == ModItems.explosive_arrow) ||
				helmetItem == ModItems.explosive_creeper_skull;
            if (itemStack != null || flag)
            {
            	ItemStack arrowStack;
                if (itemStack == null)
                   	itemStack = arrowStack = new ItemStack(isExplosiveArrow ?
                    	ModItems.explosive_arrow : Items.ARROW);
                else if (isExplosiveArrow && itemStack.getItem() != ModItems.explosive_arrow)
                	arrowStack = new ItemStack(ModItems.explosive_arrow);
                else
                	arrowStack = itemStack;
                float f = getArrowVelocity(i);

                if (f >= 0.1D)
                {
                    boolean flag1 = flag &&
                    	(arrowStack.getItem() instanceof ItemArrow ||
                    	arrowStack.getItem() == ModItems.explosive_arrow); //Forge: Fix consuming custom arrows.

                    if (!worldIn.isRemote)
                    {
                    	EntityArrow entityarrow;
                    	if (isExplosiveArrow)
                    		entityarrow = new EntityExplosiveArrow(worldIn,
                				playerIn);
                    	else {
                    		ItemArrow itemarrow = ((ItemArrow)(arrowStack.getItem() instanceof ItemArrow ?
                    			arrowStack.getItem() : Items.ARROW));
                    		entityarrow = itemarrow.createArrow(worldIn, arrowStack, entityplayer);
                    	}
                    	entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);

                        if (f == 1.0F)
                        {
                            entityarrow.setIsCritical(true);
                        }

                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                        if (j > 0)
                        {
                            entityarrow.setDamage(entityarrow.getDamage() + j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                        if (k > 0)
                        {
                            entityarrow.setKnockbackStrength(k);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
                        {
                            entityarrow.setFire(100);
                        }

                        stack.damageItem(1, entityplayer);

                        if (flag1)
                        {
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }

                        worldIn.spawnEntityInWorld(entityarrow);
                    }

                    worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                    if (!flag1)
                    {
                        --itemStack.stackSize;

                        if (itemStack.stackSize == 0)
                        {
                            entityplayer.inventory.deleteStack(itemStack);
                        }
                    }

                    entityplayer.addStat(StatList.getObjectUseStats(this));
                }
            }
        }
	}

	@Override
	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	public ActionResult<ItemStack> onItemRightClick(
		ItemStack itemStackIn, World worldIn,
		EntityPlayer playerIn, EnumHand hand) {
		
		boolean flag = this.findAmmo(playerIn) != null;

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory
        	.onArrowNock(itemStackIn, worldIn, playerIn, hand, flag);
        if (ret != null) return ret;

        if (!playerIn.capabilities.isCreativeMode && !flag)
        {
            return !flag ? new ActionResult(EnumActionResult.FAIL, itemStackIn) :
            	new ActionResult(EnumActionResult.PASS, itemStackIn);
        }
        else
        {
            playerIn.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        }
	}
}