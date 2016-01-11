package com.samuel.mazetowers.init;

import com.samuel.mazetowers.etc.MTUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class ModChestGen {

	public static ChestGenHooks[] chestContents;

	public static void initChestGen() {
		chestContents = new ChestGenHooks[10];
		for (int i = 1; i <= 10; i++) {
			chestContents[i - 1] = ChestGenHooks.getInfo("MazeTowerChest" + i);
			int addToLimit = (int) Math.floor((i + 1) / 3);
			chestContents[i - 1].setMin(1 + (int) Math.floor(addToLimit / 2)); // inclusive
			chestContents[i - 1].setMax(8 + addToLimit); // exclusive
		}
		addItem(0, new ItemStack(Items.rotten_flesh, 1), 1, 4, 100);
		addItem(0, new ItemStack(Items.stick, 1), 1, 4, 100);
		addItem(0, new ItemStack(Items.feather, 1), 1, 4, 75);
		addItem(0, new ItemStack(Items.string, 1), 1, 4, 75);
		addItem(0, new ItemStack(Items.flint, 1), 1, 4, 75);
		addItem(0, new ItemStack(Items.paper, 1), 1, 4, 75);
		addItem(0, new ItemStack(Items.bone, 1), 1, 4, 50);
		addItem(0, new ItemStack(Items.spider_eye, 1), 1, 4, 25);
		addItem(0, new ItemStack(Items.ender_pearl, 1), 1, 4, 25);
		addItem(1, new ItemStack(Items.gunpowder, 1), 1, 4, 75);
		addItem(1, new ItemStack(Items.leather, 1), 1, 4, 75);
		addItem(1, new ItemStack(Item.getItemFromBlock(Blocks.glass_pane), 1), 2, 8, 50);
		addItem(1, new ItemStack(Item.getItemFromBlock(Blocks.glass), 1), 1, 4, 50);
		addItem(2, new ItemStack(Items.gold_ingot, 1), 1, 4, 20);
		addItem(3, new ItemStack(Item.getItemFromBlock(Blocks.jukebox), 1), 1, 1, 25);
		addItem(3, new ItemStack(Items.emerald, 1), 1, 4, 20);
		addItem(3, MTUtils.getEnchantmentBookById(2, 1), 1, 1, 15); // Feather Falling I
		addItem(3, MTUtils.getEnchantmentBookById(4, 1), 1, 1, 15); // Projectile Protect I
		addItem(4, MTUtils.getEnchantmentBookById(34, 1), 1, 1, 15); // Unbreaking I
		addItem(4, new ItemStack(Items.compass, 1), 1, 1, 25);
		addItem(4, new ItemStack(Items.diamond, 1), 1, 4, 20);
		addItem(4, MTUtils.getEnchantmentBookById(34, 2), 1, 1, 15); // Unbreaking II
		addItem(4, MTUtils.getEnchantmentBookById(2, 2), 1, 1, 15); // Feather Falling II
		addItem(4, MTUtils.getEnchantmentBookById(4, 2), 1, 1, 15); // Projectile Protect II
		addItem(4, new ItemStack(Item.getItemFromBlock(Blocks.gold_block), 1), 1, 1, 10);
		addItem(5, new ItemStack(Items.ghast_tear, 1), 1, 2, 25);
		addItem(5, MTUtils.getEnchantmentBookById(6, 1), 1, 1, 20); // Aqua Affinity I
		addItem(5, MTUtils.getEnchantmentBookById(48, 1), 1, 1, 20); // Power I
		addItem(5, MTUtils.getEnchantmentBookById(2, 3), 1, 1, 15); // Feather Falling III
		addItem(5, MTUtils.getEnchantmentBookById(4, 3), 1, 1, 15); // Projectile Protect III
		addItem(5, MTUtils.getEnchantmentBookById(34, 3), 1, 1, 15); // Unbreaking III
		addItem(5, MTUtils.getEnchantmentBookById(3, 1), 1, 1, 10); // Blast Protection I
		addItem(5, new ItemStack(Item.getItemFromBlock(Blocks.emerald_block), 1), 1, 1, 10);
		addItem(6, new ItemStack(Item.getItemFromBlock(Blocks.diamond_block), 1), 1, 1, 10);
		addItem(6, new ItemStack(Items.nether_star, 1), 1, 1, 10);
		addItem(6, MTUtils.getEnchantmentBookById(48, 2), 1, 1, 15); // Power II
		addItem(6, MTUtils.getEnchantmentBookById(2, 4), 1, 1, 15); // Feather Falling IV
		addItem(6, MTUtils.getEnchantmentBookById(4, 4), 1, 1, 15); // Projectile Protect IV
		addItem(6, MTUtils.getEnchantmentBookById(3, 2), 1, 1, 10); // Blast Protection II
		addItem(7, MTUtils.getEnchantmentBookById(3, 3), 1, 1, 10); // Blast Protection III
		addItem(7, MTUtils.getEnchantmentBookById(21, 1), 1, 1, 10); // Looting I
		addItem(7, MTUtils.getEnchantmentBookById(48, 3), 1, 1, 10); // Power III
		addItem(7, MTUtils.getEnchantmentBookById(48, 3), 1, 1, 5); // Power III
		addItem(8, new ItemStack(Item.getItemFromBlock(Blocks.emerald_block), 2), 1, 2, 10);
		addItem(8, MTUtils.getEnchantmentBookById(49, 1), 1, 1, 15); // Punch I
		addItem(8, MTUtils.getEnchantmentBookById(21, 2), 1, 1, 10); // Looting II
		addItem(8, MTUtils.getEnchantmentBookById(32, 5), 1, 1, 10); // Efficiency IV
		addItem(8, MTUtils.getEnchantmentBookById(50, 1), 1, 1, 10); // Flame I
		addItem(8, MTUtils.getEnchantmentBookById(3, 4), 1, 1, 5); // Blast Protection IV
		addItem(8, MTUtils.getEnchantmentBookById(5, 3), 1, 1, 5); // Respiration III
		addItem(8, MTUtils.getEnchantmentBookById(20, 2), 1, 1, 5); // Fire Aspect II
		addItem(8, MTUtils.getEnchantmentBookById(48, 4), 1, 1, 5); // Power IV
		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.emerald_block), 2), 1, 2, 5);
		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.diamond_block), 2), 1, 2, 10);
		addItem(9, MTUtils.getEnchantmentBookById(3, 4), 1, 1, 3); // Blast Protection IV
		addItem(9, MTUtils.getEnchantmentBookById(5, 3), 1, 1, 3); // Respiration III
		addItem(9, MTUtils.getEnchantmentBookById(20, 2), 1, 1, 3); // Fire Aspect II
		addItem(9, MTUtils.getEnchantmentBookById(48, 4), 1, 1, 3); // Power IV
		addItem(9, MTUtils.getEnchantmentBookById(49, 2), 1, 1, 8); // Punch I
		addItem(9, MTUtils.getEnchantmentBookById(50, 1), 1, 1, 8); // Flame I
		addItem(9, MTUtils.getEnchantmentBookById(21, 2), 1, 1, 5); // Looting II
		addItem(9, MTUtils.getEnchantmentBookById(16, 4), 1, 1, 20); // Sharpness IV
		addItem(9, MTUtils.getEnchantmentBookById(33, 1), 1, 1, 20); // Silk Touch I
		addItem(9, MTUtils.getEnchantmentBookById(35, 2), 1, 1, 20); // Fortune II
		addItem(9, MTUtils.getEnchantmentBookById(51, 1), 1, 1, 20); // Infinity I
		addItem(9, MTUtils.getEnchantmentBookById(0, 4), 1, 1, 15); // Protection IV
		addItem(9, MTUtils.getEnchantmentBookById(1, 4), 1, 1, 15); // Fire Protection IV
		addItem(9, MTUtils.getEnchantmentBookById(49, 2), 1, 1, 15); // Punch II
		addItem(9, new ItemStack(Item.getItemFromBlock(Blocks.beacon), 1), 1, 2, 15);
		addItem(9, MTUtils.getEnchantmentBookById(7, 2), 1, 1, 13); // Thorns II
		addItem(9, MTUtils.getEnchantmentBookById(21, 3), 1, 1, 10); // Looting III
		addItem(9, MTUtils.getEnchantmentBookById(35, 3), 1, 1, 10); // Fortune III
		addItem(9, MTUtils.getEnchantmentBookById(61, 3), 1, 1, 10); // Lure III
		addItem(9, MTUtils.getEnchantmentBookById(62, 3), 1, 1, 8); // Luck of the Sea III
		addItem(9, MTUtils.getEnchantmentBookById(8, 3), 1, 1, 5); // Depth Strider III
		addItem(9, MTUtils.getEnchantmentBookById(17, 5), 1, 1, 5); // Smite V
		addItem(9, MTUtils.getEnchantmentBookById(18, 5), 1, 1, 5); // Bane of Arthropods V
		addItem(9, MTUtils.getEnchantmentBookById(32, 5), 1, 1, 5); // Efficiency V
		addItem(9, MTUtils.getEnchantmentBookById(48, 5), 1, 1, 5); // Power V
		addItem(9, MTUtils.getEnchantmentBookById(16, 5), 1, 1, 3); // Sharpness V
		addItem(9, MTUtils.getEnchantmentBookById(7, 3), 1, 1, 3); // Thorns III
		/*chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.tnt), 1), 1, 4, 15)); // TNT
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.obsidian), 1), 1, 4, 10)); // Obsidian
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.glowstone), 1), 1, 6, 20)); // Glowstone
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.iron_bars), 2), 1, 8, 15)); // Iron Bars
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.melon_block), 1), 1, 6, 25)); // Melon
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.glowstone), 1), 1, 4, 15)); // Glowstone Lamp
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.emerald_block), 1), 1, 2, 5)); // Block of Emerald
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.anvil), 1), 1, 1, 2)); // Anvil
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.anvil), 1, 1), 1, 1, 3)); // Anvil (Slightly Damaged)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.anvil), 1, 2), 1, 1, 5)); // Anvil (Very Damaged)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.redstone_block), 1), 1, 2, 4)); // Block of Redstone
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.slime_block), 1), 1, 2, 6)); // Slime Block
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.coal_block), 1), 1, 3, 20)); // Block of Coal
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_shovel, 1), 1, 1, 5)); // Iron Shovel
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_pickaxe, 1), 1, 1, 6)); // Iron Pickaxe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_axe, 1), 1, 1, 5)); // Iron Axe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.flint_and_steel, 1), 1, 1, 10)); // Flint and Steel
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.apple, 4), 1, 4, 40)); // Apple
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.bow, 1), 1, 1, 20)); // Bow
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.arrow, 4), 1, 4, 20)); // Arrow
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.coal, 4), 1, 4, 25)); // Coal
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.coal, 4, 1), 1, 4, 50)); // Charcoal
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond, 1), 1, 2, 2)); // Diamond Gem
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_ingot, 1), 1, 4, 20)); // Iron Ingot
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.gold_ingot, 1), 1, 2, 30)); // Gold Ingot
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_sword, 1), 1, 1, 4)); // Iron Sword
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_sword, 1), 1, 1, 1)); // Diamond Sword
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_shovel, 1), 1, 1, 1)); // Diamond Shovel
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_pickaxe, 1), 1, 1, 1)); // Diamond Pickaxe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_axe, 1), 1, 1, 1)); // Diamond Axe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.bowl, 1), 1, 2, 20)); // Bowl
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_sword, 1), 1, 1, 13)); // Gold Sword
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_shovel, 1), 1, 1, 15)); // Gold Shovel
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_pickaxe, 1), 1, 1, 17)); // Gold Pickaxe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_axe, 1), 1, 1, 15)); // Gold Axe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.string, 2), 1, 4, 30)); // String
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.feather, 2), 1, 4, 30)); // Feather
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.bread, 1), 1, 4, 40)); // Bread
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_hoe, 1), 1, 1, 5)); // Iron Hoe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_hoe, 1), 1, 1, 1)); // Diamond Hoe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_hoe, 1), 1, 1, 15)); // Gold Hoe
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.chainmail_helmet, 1), 1, 1, 13)); // Chainmail Helmet
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.chainmail_chestplate, 1), 1, 1, 8)); // Chainmail Chestplate
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.chainmail_leggings, 1), 1, 1, 8)); // Chainmail Leggings
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.chainmail_boots, 1), 1, 1, 13)); // Chainmail Boots
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_helmet, 1), 1, 1, 6)); // Iron Helmet
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_chestplate, 1), 1, 1, 4)); // Iron Chestplate
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_leggings, 1), 1, 1, 4)); // Iron Leggings
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.iron_boots, 1), 1, 1, 6)); // Iron Boots
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_apple, 1), 1, 1, 2)); // Golden Apple
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.bucket, 1), 1, 1, 10)); // Bucket
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.slime_ball, 2), 1, 4, 20)); // Slime Ball
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.bone, 1), 1, 1, 50)); // Bone
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.rotten_flesh, 1), 1, 8, 50)); // Rotten Flesh
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.emerald, 1), 1, 4, 15)); // Emerald
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8193), 1, 1, 1)); // Regeneration Potion (0:45)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8197), 1, 1, 1)); // Healing Potion (0:45)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8198), 1, 1, 10)); // Night Vision Potion (3:00)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8201), 1, 1, 1)); // Strength Potion (3:00)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8206), 1, 1, 1)); // Invisibility Potion (3:00)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16385), 1, 1, 2)); // Regeneration Splash (0:33)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16389), 1, 1, 2)); // Healing Splash
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16390), 1, 1, 15)); // Night Vision Splash (2:15)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16393), 1, 1, 2)); // Strength Splash (2:15)
		chestContents.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16398), 1, 1, 2)); // Invisibility Splash (2:15)
		//chestContents.addItem(new WeightedRandomChestContent(new ItemStack(ChaosBlock.itemExtendedNightVisionSplash), 1, 1, 50)); // Night Vision Splash (16:00)
		
		chestContents_High.setMin(12); // inclusive
		chestContents_High.setMax(21); // exclusive
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.obsidian), 4), 1, 4, 10)); // Obsidian
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.noteblock), 1), 1, 4, 10)); // Note Block
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.sticky_piston), 1), 1, 3, 7)); // Sticky Piston
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.diamond_block), 1), 1, 1, 3)); // Block of Diamond
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.jukebox), 1), 1, 4, 10)); // Juke Box
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.enchanting_table), 1), 1, 1, 2)); // Enchantment Table
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.emerald_block), 16), 1, 4, 2)); // Block of Emerald
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.beacon), 1), 1, 2, 1)); // Beacon
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.skull), 1, 1), 1, 3, 3)); // Head Block (Wither)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.sea_lantern), 1), 1, 3, 3)); // Sea Lantern
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Item.getItemFromBlock(Blocks.coal_block), 4), 1, 5, 20)); // Block of Coal
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond, 2), 1, 3, 25)); // Diamond Gem
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_sword, 1), 1, 1, 8)); // Diamond Sword
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_shovel, 1), 1, 1, 10)); // Diamond Shovel
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_pickaxe, 1), 1, 1, 12)); // Diamond Pickaxe
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_axe, 1), 1, 1, 10)); // Diamond Axe
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_hoe, 1), 1, 1, 10)); // Diamond Hoe
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_helmet, 1), 1, 1, 8)); // Diamond Helmet
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_chestplate, 1), 1, 1, 6)); // Diamond Chestplate
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_leggings, 1), 1, 1, 6)); // Diamond Leggings
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_boots, 1), 1, 1, 8)); // Diamond Boots
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.golden_apple, 1, 1), 1, 1, 10)); // Enchanted Golden Apple
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8193), 1, 1, 5)); // Regeneration Potion (0:45)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8194), 1, 1, 5)); // Swiftness Potion (3:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8195), 1, 1, 3)); // Fire Resistance Potion (3:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8196), 1, 1, 3)); // Poison Potion (0:45)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8197), 1, 1, 5)); // Healing Potion
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8198), 1, 1, 7)); // Night Vision Potion (3:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8200), 1, 1, 5)); // Weakness Potion (1:30)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8201), 1, 1, 4)); // Strength Potion (3:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8202), 1, 1, 3)); // Slowness Potion (1:30)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8204), 1, 1, 3)); // Harming Potion
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8205), 1, 1, 5)); // Water Breathing Potion (3:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8206), 1, 1, 5)); // Invisibility Potion (3:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8225), 1, 1, 3)); // Regeneration Potion II (0:22)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8226), 1, 1, 3)); // Swiftness Potion II (1:30)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8228), 1, 1, 1)); // Poison Potion II (0:22)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8229), 1, 1, 3)); // Healing Potion II
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8233), 1, 1, 3)); // Strength Potion II (1:30)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8235), 1, 1, 3)); // Leaping Potion II (1:30)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8236), 1, 1, 1)); // Harming Potion II
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8257), 1, 1, 4)); // Regeneration Potion (2:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8258), 1, 1, 4)); // Swiftness Potion (8:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8259), 1, 1, 4)); // Fire Resistance Potion (8:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8260), 1, 1, 2)); // Poison Potion (2:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8262), 1, 1, 5)); // Night Vision Potion II (8:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8264), 1, 1, 2)); // Weakness Potion (4:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8265), 1, 1, 3)); // Strength Potion (8:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8266), 1, 1, 2)); // Slowness Potion (4:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8267), 1, 1, 3)); // Leaping Potion (3:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8269), 1, 1, 3)); // Water Breathing Potion (8:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8270), 1, 1, 3)); // Invisibility Potion (8:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8289), 1, 1, 1)); // Regeneration Potion II (1:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8290), 1, 1, 1)); // Swiftness Potion II (4:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8292), 1, 1, 1)); // Poison Potion II (1:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 8297), 1, 1, 1)); // Strength Potion II (4:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.potionitem, 1, 16454), 1, 1, 5)); // Night Vision Splash (6:00)
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.experience_bottle, 1), 1, 1, 15)); // Bottle of Enchanting
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.nether_star, 1), 1, 2, 5)); // Nether Star
		chestContents_High.addItem(new WeightedRandomChestContent(new ItemStack(Items.diamond_horse_armor, 1), 1, 1, 5)); // Diamond Horse Armor
	*/}
	
	private static void addItem(int index, ItemStack item, int minCount, int maxCountIn,
		int weightIn) {
		int dStart = Math.max(index - 3, 0);
		for (int d = dStart; d <= index + 3 && d < 10; d++) {
			float diff;
			int maxCount;
			int weight;
			if (d == index) {
				maxCount = maxCountIn;
				weight = weightIn;
			} else {
				diff = d < index ? (((int) ((1 / ((index - d))) * 4)) / 4) + 1 :
					(d - index) + 1;
				maxCount = (int) Math.min(Math.max(maxCountIn * diff, 1),
					item.getMaxStackSize());
				weight = (int) Math.max(weightIn / diff, 1);
			}
			chestContents[index].addItem(new WeightedRandomChestContent(item, minCount,
				maxCount, weight));
		}
	}
}
