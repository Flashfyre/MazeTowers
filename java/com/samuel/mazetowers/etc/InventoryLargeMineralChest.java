package com.samuel.mazetowers.etc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class InventoryLargeMineralChest extends InventoryLargeChest {
 
    public InventoryLargeMineralChest(String nameIn, ILockableContainer upperChestIn, ILockableContainer lowerChestIn)
    {
        super(nameIn, upperChestIn, lowerChestIn);
    }
}