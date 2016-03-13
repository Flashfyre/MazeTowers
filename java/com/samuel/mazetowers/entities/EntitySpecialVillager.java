package com.samuel.mazetowers.entities;

import java.lang.reflect.Field;

import com.samuel.mazetowers.blocks.BlockVendorTradeable;
import com.samuel.mazetowers.etc.IVendorTradeable;
import com.samuel.mazetowers.etc.MTUtils;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTowerBase.EnumTowerType;

import net.minecraft.block.Block;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySpecialVillager extends EntityVillager {

	private int difficulty;
	private EnumTowerType towerType;
	private boolean needsInitialization;
	private static IVendorTradeable[][] sellList = new IVendorTradeable[][] {
    	{ },
    	{ ModItems.ram, ModBlocks.memoryPistonOff, ModBlocks.itemScanner, ModBlocks.itemScannerGold,
    	ModBlocks.redstone_clock, ModBlocks.hiddenButton,
    	ModBlocks.hiddenPressurePlateWeighted },
    	{ },
    	{ ModItems.key, ModBlocks.lock }
    };
	
	private static IVendorTradeable[][] buyList = new IVendorTradeable[][] {
    	{ },
    	{ },
    	{ },
    	{ }
    };
    
    public EntitySpecialVillager(World worldIn) {
    	this(worldIn, 0, EnumTowerType.STONE_BRICK, EnumTowerType.STONE_BRICK.getBaseDifficulty());
    }

	public EntitySpecialVillager(World worldIn, int professionId,
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
   public IChatComponent getDisplayName()
   {
       String s = this.getCustomNameTag();

       if (s != null && s.length() > 0)
       {
           ChatComponentText chatcomponenttext = new ChatComponentText(s);
           chatcomponenttext.getChatStyle().setChatHoverEvent(this.getHoverEvent());
           chatcomponenttext.getChatStyle().setInsertion(this.getUniqueID().toString());
           return chatcomponenttext;
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
               ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(
            	   "entity.mazetowers.Villager." + s1, new Object[0]);
               chatcomponenttranslation.getChatStyle().setChatHoverEvent(this.getHoverEvent());
               chatcomponenttranslation.getChatStyle().setInsertion(this.getUniqueID().toString());
               return chatcomponenttranslation;
           }
           else
           {
               return super.getDisplayName();
           }
       }
   }
	
	private void populateBuyingList() {
		Field buyingList = MTUtils.findObfuscatedField(EntityVillager.class,
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
		MerchantRecipeList recipes = new MerchantRecipeList();
		final int professionId = getProfession();
		final int itemCount = sellList[professionId].length;
		for (int i = 0; i < itemCount; i++) {
			IVendorTradeable tradeableItem = sellList[professionId][i];
			Item item = tradeableItem instanceof Block ?
				Item.getItemFromBlock((Block) tradeableItem) : (Item) tradeableItem;
			int tradeChance = tradeableItem.getVendorTradeChance(difficulty);
			if (tradeChance == 1000 || rand.nextInt(1000) < tradeChance)
				recipes.add(new MerchantRecipe(getTradeStack(),
					new ItemStack(item, 1, professionId != 3 ? 0 : towerType.ordinal())));
		}
		return recipes;
	}
	
	private ItemStack getTradeStack() {
		return new ItemStack(Items.emerald, 1);
	}
	
	public EnumTowerType getTowerType() {
		return towerType;
	}
	
	/**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("towerType", towerType.ordinal());
        tagCompound.setInteger("difficulty", difficulty);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        super.readEntityFromNBT(tagCompound);
        towerType = EnumTowerType.values()[tagCompound.getInteger("towerType")];
        difficulty = tagCompound.getInteger("difficulty");
    }
}
