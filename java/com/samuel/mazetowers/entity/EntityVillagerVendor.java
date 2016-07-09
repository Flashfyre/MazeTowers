package com.samuel.mazetowers.entity;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import com.samuel.mazetowers.etc.IVendorTradable;
import com.samuel.mazetowers.etc.MTHelper;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

public class EntityVillagerVendor extends EntityVillager {

	private int difficulty;
	private EnumTowerType towerType;
	private boolean needsInitialization;
	private static IVendorTradable[][] sellList = new IVendorTradable[][] {
    	{ },
    	{ ModItems.ram, (IVendorTradable) Item.getItemFromBlock(ModBlocks.memoryPistonOff),
    	(IVendorTradable) Item.getItemFromBlock(ModBlocks.itemScanner),
    	(IVendorTradable) Item.getItemFromBlock(ModBlocks.itemScannerGold),
    	(IVendorTradable) Item.getItemFromBlock(ModBlocks.redstoneClock),
    	(IVendorTradable) Item.getItemFromBlock(ModBlocks.hiddenButton),
    	(IVendorTradable) Item.getItemFromBlock(ModBlocks.hiddenPressurePlateWeighted) },
    	{ },
    	{ ModItems.key_colored, ModItems.key_spectrite, (IVendorTradable) Item.getItemFromBlock(ModBlocks.lock) }
    };
	
	private static IVendorTradable[][] buyList = new IVendorTradable[][] {
    	{ },
    	{ },
    	{ },
    	{ ModItems.key_colored, ModItems.key_spectrite, (IVendorTradable) Item.getItemFromBlock(ModBlocks.lock) }
    };
    
    public EntityVillagerVendor(World worldIn) {
    	this(worldIn, 0, EnumTowerType.STONE_BRICK, EnumTowerType.STONE_BRICK.getBaseDifficulty());
    }

	public EntityVillagerVendor(World worldIn, int professionId,
		EnumTowerType towerType, int difficulty) {
		super(worldIn, professionId);
		this.difficulty = difficulty;
		this.towerType = towerType;
		this.needsInitialization = true;
	}
	
	@Override
	protected void updateAITasks() {
        super.updateAITasks();
        if (needsInitialization) {
        	populateBuyingList();
        	needsInitialization = false;
        }
    }
	
	@Override
	/**
    * Get the formatted ChatComponent that will be used for the sender's username in chat
    */
   public ITextComponent getDisplayName()
   {
       String s = this.getCustomNameTag();

       if (s != null && s.length() > 0)
       {
           TextComponentString TextComponentString = new TextComponentString(s);
           TextComponentString.getStyle().setHoverEvent(this.getHoverEvent());
           TextComponentString.getStyle().setInsertion(this.getUniqueID().toString());
           return TextComponentString;
       }
       else
       {
           if (needsInitialization)
        	   this.populateBuyingList();

           String s1 = null, customName = null;

           switch (this.getProfession())
           {
               case 1:
                   s1 = "engineer";
                   break;
               case 3:
                   s1 = "locksmith";
                   break;
               default:
           }

           if (s1 != null)
           {
               TextComponentTranslation chatcomponenttranslation = new TextComponentTranslation(
            	   "entity.mazetowers.Villager." + s1, new Object[0]);
               chatcomponenttranslation.getStyle().setHoverEvent(this.getHoverEvent());
               chatcomponenttranslation.getStyle().setInsertion(this.getUniqueID().toString());
               return chatcomponenttranslation;
           }
           else
           {
               return super.getDisplayName();
           }
       }
   }
	
	private void populateBuyingList() {
		Field buyingList = MTHelper.findObfuscatedField(EntityVillager.class,
    		"buyingList", "field_70963_i");
    	buyingList.setAccessible(true);
    	try {
			buyingList.set(this, getNewRecipeList());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    }
	
	private MerchantRecipeList getNewRecipeList() {
		
		final int professionId = getProfession(),
		sellCount = sellList[professionId].length,
		buyCount = buyList[professionId].length;
		MerchantRecipeList recipes = new MerchantRecipeList();
		
		for (int s = 0; s < sellCount; s++) {
			IVendorTradable sellItem = sellList[professionId][s];
			Item item = sellItem instanceof Block ?
				Item.getItemFromBlock((Block) sellItem) : (Item) sellItem;
			ItemStack stack = new ItemStack(item, 1, professionId != 3 ||
				item == ModItems.key_spectrite ? 0 : towerType.ordinal());
			int tradeChance = sellItem.getVendorTradeChance(difficulty);
			if (tradeChance == 1000 || rand.nextInt(1000) < tradeChance)
				recipes.add(new MerchantRecipe(getTradeStack(stack, false), stack));
		}
		for (int b = 0; b < buyCount; b++) {
			IVendorTradable buyItem = buyList[professionId][b];
			Item item = buyItem instanceof Block ?
				Item.getItemFromBlock((Block) buyItem) : (Item) buyItem;
			ItemStack stack = new ItemStack(item, 1, professionId != 3 ||
				item == ModItems.key_spectrite ? 0 : towerType.ordinal());
			int tradeChance = buyItem.getVendorTradeChance(difficulty);
			if (tradeChance == 1000 || (rand.nextInt(16) < 6 &&
				rand.nextInt(1000) < tradeChance))
				recipes.add(new MerchantRecipe(stack, getTradeStack(stack, true)));
		}
		
		return recipes;
	}
	
	private ItemStack getTradeStack(ItemStack item, boolean isBuying) {
		int itemPrice = getRandomPrice((IVendorTradable) item.getItem());
		if (isBuying)
			itemPrice = (int) Math.max(Math.floor(itemPrice * (0.5 + (rand.nextInt(10) * 0.05))), 1);
		return new ItemStack(Items.EMERALD, itemPrice);
	}
	
	private int getRandomPrice(IVendorTradable item) {
		
		return item.minPrice == item.maxPrice ? item.minPrice :
			rand.nextInt(item.maxPrice - item.minPrice) + item.minPrice;
	}
	
	public EnumTowerType getTowerType() {
		return towerType;
	}
	
	/**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
	public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("towerType", towerType.ordinal());
        tagCompound.setInteger("difficulty", difficulty);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	public void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        super.readEntityFromNBT(tagCompound);
        towerType = EnumTowerType.values()[tagCompound.getInteger("towerType")];
        difficulty = tagCompound.getInteger("difficulty");
    }
}
