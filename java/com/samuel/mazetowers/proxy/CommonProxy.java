package com.samuel.mazetowers.proxy;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockChaoticSludge;
import com.samuel.mazetowers.blocks.BlockExplosiveCreeperSkull;
import com.samuel.mazetowers.blocks.BlockExtraDoor;
import com.samuel.mazetowers.blocks.BlockExtraDoubleSlab;
import com.samuel.mazetowers.blocks.BlockExtraHalfSlab;
import com.samuel.mazetowers.blocks.BlockExtraStairs;
import com.samuel.mazetowers.blocks.BlockExtraWall;
import com.samuel.mazetowers.blocks.BlockHiddenButton;
import com.samuel.mazetowers.blocks.BlockHiddenPressurePlateWeighted;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.blocks.BlockItemScannerGold;
import com.samuel.mazetowers.blocks.BlockLock;
import com.samuel.mazetowers.blocks.BlockMazeTowerThreshold;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBase;
import com.samuel.mazetowers.blocks.BlockMemoryPistonBaseOff;
import com.samuel.mazetowers.blocks.BlockMemoryPistonExtension;
import com.samuel.mazetowers.blocks.BlockMemoryPistonExtensionOff;
import com.samuel.mazetowers.blocks.BlockMemoryPistonMoving;
import com.samuel.mazetowers.blocks.BlockMemoryPistonMovingOff;
import com.samuel.mazetowers.blocks.BlockMineralChest;
import com.samuel.mazetowers.blocks.BlockMineralChest.Type;
import com.samuel.mazetowers.blocks.BlockRedstoneClock;
import com.samuel.mazetowers.blocks.BlockSpecialMobSpawner;
import com.samuel.mazetowers.blocks.BlockSpectrite;
import com.samuel.mazetowers.blocks.BlockSpectriteOre;
import com.samuel.mazetowers.blocks.BlockVendorSpawner;
import com.samuel.mazetowers.client.gui.GuiHandlerItemScanner;
import com.samuel.mazetowers.etc.IMazeTowerCapability;
import com.samuel.mazetowers.etc.ItemExtraTab;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.etc.PlayerMazeTower;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.init.ModCrafting;
import com.samuel.mazetowers.init.ModDispenserBehavior;
import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.init.ModSounds;
import com.samuel.mazetowers.init.ModTileEntities;
import com.samuel.mazetowers.init.ModWorldGen;
import com.samuel.mazetowers.items.ItemChaoticSludgeBucket;
import com.samuel.mazetowers.items.ItemColoredKey;
import com.samuel.mazetowers.items.ItemDiamondRod;
import com.samuel.mazetowers.items.ItemExplosiveArrow;
import com.samuel.mazetowers.items.ItemExplosiveBow;
import com.samuel.mazetowers.items.ItemExplosiveCreeperSkull;
import com.samuel.mazetowers.items.ItemRAM;
import com.samuel.mazetowers.items.ItemSpectriteArmor;
import com.samuel.mazetowers.items.ItemSpectriteAxe;
import com.samuel.mazetowers.items.ItemSpectriteAxeSpecial;
import com.samuel.mazetowers.items.ItemSpectriteGem;
import com.samuel.mazetowers.items.ItemSpectriteKey;
import com.samuel.mazetowers.items.ItemSpectriteKeySword;
import com.samuel.mazetowers.items.ItemSpectriteKeySwordSpecial;
import com.samuel.mazetowers.items.ItemSpectriteOrb;
import com.samuel.mazetowers.items.ItemSpectritePickaxe;
import com.samuel.mazetowers.items.ItemSpectritePickaxeSpecial;
import com.samuel.mazetowers.items.ItemSpectriteRod;
import com.samuel.mazetowers.items.ItemSpectriteShovel;
import com.samuel.mazetowers.items.ItemSpectriteShovelSpecial;
import com.samuel.mazetowers.items.ItemSpectriteSword;
import com.samuel.mazetowers.items.ItemSpectriteSwordSpecial;
import com.samuel.mazetowers.tileentity.TileEntityCircuitBreaker;
import com.samuel.mazetowers.tileentity.TileEntityExplosiveCreeperSkull;
import com.samuel.mazetowers.tileentity.TileEntityItemScanner;
import com.samuel.mazetowers.tileentity.TileEntityLock;
import com.samuel.mazetowers.tileentity.TileEntityMazeTowerThreshold;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPiston;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPistonMemory;
import com.samuel.mazetowers.tileentity.TileEntityMineralChest;
import com.samuel.mazetowers.tileentity.TileEntitySpecialMobSpawner;
import com.samuel.mazetowers.tileentity.TileEntityWebSpiderSpawner;
import com.samuel.mazetowers.world.WorldGenMazeTowers;
import com.samuel.mazetowers.world.WorldGenSpectrite;

public class CommonProxy {
	
	static
    {
        FluidRegistry.enableUniversalBucket();
    }

	public void preInit(FMLPreInitializationEvent e) {
		MazeTowers.TabExtra = new ItemExtraTab(CreativeTabs.getNextID(), "extraTab");
		MazeTowers.ItemPropertyGetterSpectrite = new IItemPropertyGetter() {
			
			public int curFrame = 0;
			
	        @Override
	        @SideOnly(Side.CLIENT)
	        public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
	            boolean flag = entityIn != null;
	            Entity entity = flag ? entityIn : stack.getItemFrame();
	            float value = 0.0F;
	            
	            if (worldIn == null && entity != null) {
	                worldIn = entity.worldObj;
	            }

	            if (worldIn == null) {
	                return 0.0F;
	            } else {
	            	float time = MathHelper.ceiling_float_int((((worldIn.getTotalWorldTime() >> 1) % 36)
	            		* 0.2777F) * 1000F) / 10000F;
	                curFrame = Math.round(time * 36);
	                return time;
	            }
	        }
	    };
		
	    for (int t = 0; t < MazeTowers.LootTables.length; t++) {
	    	MazeTowers.LootTables[t] = LootTableList.register(new ResourceLocation(
    			MazeTowers.MODID, "maze_tower_treasure_" + t));
	    }
	    
		MazeTowers.FluidChaoticSludge = new Fluid(
			"chaoticsludge", new ResourceLocation(
				"mazetowers:blocks/chaotic_sludge_still"),
			new ResourceLocation(
				"mazetowers:blocks/chaotic_sludge_flowing")).setLuminosity(5).setViscosity(4000);
		FluidRegistry.registerFluid(MazeTowers.FluidChaoticSludge);
		FluidRegistry.addBucketForFluid(MazeTowers.FluidChaoticSludge);
		MazeTowers.solidCircuits = new MaterialLogicSolid(
			MapColor.AIR);
		(MazeTowers.BlockHiddenPressurePlateWeighted = new BlockHiddenPressurePlateWeighted())
			.setUnlocalizedName("hidden_heavy_pressure_plate");
		(MazeTowers.BlockItemScanner = new BlockItemScanner()).setUnlocalizedName("item_scanner");
		(MazeTowers.BlockItemScannerGold = new BlockItemScannerGold()).setUnlocalizedName("item_scanner_gold");
		(MazeTowers.BlockMazeTowerThreshold = new BlockMazeTowerThreshold()).setUnlocalizedName("maze_tower_threshold");
		(MazeTowers.BlockMemoryPiston = new BlockMemoryPistonBase()).setUnlocalizedName("memory_piston");
		(MazeTowers.BlockMemoryPistonOff = new BlockMemoryPistonBaseOff()).setUnlocalizedName("memory_piston_off");
		MazeTowers.BlockMemoryPistonHead = new BlockMemoryPistonExtension(
			"memory_piston_head");
		MazeTowers.BlockMemoryPistonHeadOff = new BlockMemoryPistonExtensionOff(
			"memory_piston_head_off");
		MazeTowers.BlockMemoryPistonExtension = new BlockMemoryPistonMoving(
			"memory_piston_extension");
		MazeTowers.BlockMemoryPistonExtensionOff = new BlockMemoryPistonMovingOff(
			"memory_piston_extension_off");
		(MazeTowers.BlockHiddenButton = new BlockHiddenButton()).setUnlocalizedName("quartz_button");
		(MazeTowers.BlockIronChest = new BlockMineralChest(Type.IRON))
    		.setUnlocalizedName("iron_chest");
    	(MazeTowers.BlockGoldChest = new BlockMineralChest(Type.GOLD))
    		.setUnlocalizedName("gold_chest");
    	(MazeTowers.BlockDiamondChest = new BlockMineralChest(Type.DIAMOND))
    		.setUnlocalizedName("diamond_chest");
    	(MazeTowers.BlockSpectriteChest = new BlockMineralChest(Type.SPECTRITE))
			.setUnlocalizedName("spectrite_chest");
    	(MazeTowers.BlockTrappedIronChest = new BlockMineralChest(Type.IRON_TRAPPED))
        	.setUnlocalizedName("iron_chest_trapped");
        (MazeTowers.BlockTrappedGoldChest = new BlockMineralChest(Type.GOLD_TRAPPED))
        	.setUnlocalizedName("gold_chest_trapped");
        (MazeTowers.BlockTrappedDiamondChest = new BlockMineralChest(Type.DIAMOND_TRAPPED))
        	.setUnlocalizedName("diamond_chest_trapped");
        (MazeTowers.BlockTrappedSpectriteChest = new BlockMineralChest(Type.SPECTRITE_TRAPPED))
    		.setUnlocalizedName("spectrite_chest_trapped");
        (MazeTowers.BlockExtraHalfSlab = new BlockExtraHalfSlab()).setUnlocalizedName("half_slab");
        (MazeTowers.BlockExtraDoubleSlab = new BlockExtraDoubleSlab())
    		.setUnlocalizedName("double_slab");
		(MazeTowers.BlockPackedIceStairs = new BlockExtraStairs(
			Blocks.PACKED_ICE.getDefaultState()))
			.setUnlocalizedName("packed_ice_stairs");
		(MazeTowers.BlockMyceliumStairs = new BlockExtraStairs(
			Blocks.MYCELIUM.getDefaultState()))
			.setUnlocalizedName("mycelium_stairs");
		(MazeTowers.BlockPrismarineBrickStairs = new BlockExtraStairs(
			Blocks.PRISMARINE.getStateFromMeta(1)))
			.setUnlocalizedName("prismarine_brick_stairs");
		(MazeTowers.BlockEndStoneStairs = new BlockExtraStairs(
			Blocks.END_STONE.getDefaultState()))
			.setUnlocalizedName("end_stone_stairs");
		(MazeTowers.BlockObsidianStairs = new BlockExtraStairs(
			Blocks.OBSIDIAN.getDefaultState()))
			.setUnlocalizedName("obsidian_stairs");
		(MazeTowers.BlockBedrockStairs = new BlockExtraStairs(
			Blocks.BEDROCK.getDefaultState()))
			.setUnlocalizedName("bedrock_stairs");
		(MazeTowers.BlockSandstoneWall = new BlockExtraWall(
			Blocks.SANDSTONE))
			.setUnlocalizedName("sandstone_wall");
		(MazeTowers.BlockRedSandstoneWall = new BlockExtraWall(
			Blocks.RED_SANDSTONE))
			.setUnlocalizedName("red_sandstone_wall");
		(MazeTowers.BlockStoneBrickWall = new BlockExtraWall(
			Blocks.STONEBRICK))
			.setUnlocalizedName("stone_brick_wall");
		(MazeTowers.BlockMossyStoneBrickWall = new BlockExtraWall(
			Blocks.STONEBRICK))
			.setUnlocalizedName("mossy_stone_brick_wall");
		(MazeTowers.BlockPackedIceWall = new BlockExtraWall(
			Blocks.PACKED_ICE))
			.setUnlocalizedName("packed_ice_wall");
		(MazeTowers.BlockPrismarineBrickWall = new BlockExtraWall(
			Blocks.PRISMARINE))
			.setUnlocalizedName("prismarine_brick_wall");
		(MazeTowers.BlockQuartzWall = new BlockExtraWall(
			Blocks.QUARTZ_BLOCK))
			.setUnlocalizedName("quartz_wall");
		(MazeTowers.BlockEndStoneWall = new BlockExtraWall(
			Blocks.END_STONE))
			.setUnlocalizedName("end_stone_wall");
		(MazeTowers.BlockPurpurWall = new BlockExtraWall(
			Blocks.PURPUR_BLOCK))
			.setUnlocalizedName("purpur_wall");
		(MazeTowers.BlockObsidianWall = new BlockExtraWall(
			Blocks.OBSIDIAN))
			.setUnlocalizedName("obsidian_wall");
		(MazeTowers.BlockBedrockWall = new BlockExtraWall(
			Blocks.BEDROCK))
			.setUnlocalizedName("bedrock_wall");
		MazeTowers.BlockPrismarineDoor = new BlockExtraDoor(
			"prismarine_brick_door", 1.5F, 10.0F, 0);
		MazeTowers.BlockQuartzDoor = new BlockExtraDoor(
			"quartz_door", 0.8F, 2.4F, 1);
		MazeTowers.BlockEndStoneDoor = new BlockExtraDoor(
			"end_stone_door", 3.0F, 15.0F, 2);
		MazeTowers.BlockPurpurDoor = new BlockExtraDoor(
			"purpur_door", 3.0F, 15.0F, 3);
		MazeTowers.BlockObsidianDoor = new BlockExtraDoor(
			"obsidian_door", 50.0F, 2000.0F, 4);
		MazeTowers.BlockBedrockDoor = new BlockExtraDoor(
			"bedrock_door", -1.0F, 6000000.0F, 5);
		(MazeTowers.BlockLock = new BlockLock()).setUnlocalizedName("lock");
		(MazeTowers.BlockRedstoneClock = new BlockRedstoneClock(false))
			.setUnlocalizedName("redstone_clock");
		(MazeTowers.BlockRedstoneClockInverted = new BlockRedstoneClock(true))
			.setUnlocalizedName("redstone_clock_inverted");
		(MazeTowers.BlockExplosiveCreeperSkull = new BlockExplosiveCreeperSkull())
			.setUnlocalizedName("explosive_creeper_skull");
		(MazeTowers.BlockSpectriteOre = new BlockSpectriteOre())
			.setHardness(6.0F).setResistance(10.0F).setUnlocalizedName("spectrite_ore");
		(MazeTowers.BlockSpectrite = new BlockSpectrite())
			.setHardness(10.0F).setResistance(15.0F).setUnlocalizedName("spectrite_block")
			.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		(MazeTowers.BlockSpecialMobSpawner = new BlockSpecialMobSpawner())
			.setUnlocalizedName("special_mob_spawner");
		(MazeTowers.BlockVendorSpawner = new BlockVendorSpawner())
			.setUnlocalizedName("vendor_spawner");
		MazeTowers.BlockChaoticSludge = new BlockChaoticSludge(
			MazeTowers.FluidChaoticSludge, "chaoticsludge");
		MazeTowers.FluidChaoticSludge
			.setUnlocalizedName(MazeTowers.BlockChaoticSludge
				.getUnlocalizedName());
		(MazeTowers.ItemColoredKey = new ItemColoredKey()).setUnlocalizedName("key");
		(MazeTowers.ItemSpectriteKey = new ItemSpectriteKey()).setUnlocalizedName("spectrite_key");
		(MazeTowers.ItemDiamondRod = new ItemDiamondRod())
			.setUnlocalizedName("diamond_rod")
			.setCreativeTab(CreativeTabs.MATERIALS);
		(MazeTowers.ItemSpectriteRod = new ItemSpectriteRod())
			.setUnlocalizedName("spectrite_rod")
			.setCreativeTab(CreativeTabs.MATERIALS);
		(MazeTowers.ItemRAM = new ItemRAM()).setUnlocalizedName("ram");
		(MazeTowers.ItemPrismarineDoor = new ItemDoor(
			MazeTowers.BlockPrismarineDoor))
			.setUnlocalizedName("prismarine_brick_door_item")
			.setCreativeTab(MazeTowers.TabExtra);
		(MazeTowers.ItemQuartzDoor = new ItemDoor(
			MazeTowers.BlockQuartzDoor))
			.setUnlocalizedName("quartz_door_item")
			.setCreativeTab(MazeTowers.TabExtra);
		(MazeTowers.ItemEndStoneDoor = new ItemDoor(
			MazeTowers.BlockEndStoneDoor))
			.setUnlocalizedName("end_stone_door_item")
			.setCreativeTab(MazeTowers.TabExtra);
		(MazeTowers.ItemPurpurDoor = new ItemDoor(
			MazeTowers.BlockPurpurDoor))
			.setUnlocalizedName("purpur_door_item")
			.setCreativeTab(MazeTowers.TabExtra);
		(MazeTowers.ItemObsidianDoor = new ItemDoor(
			MazeTowers.BlockObsidianDoor))
			.setUnlocalizedName("obsidian_door_item")
			.setCreativeTab(MazeTowers.TabExtra);
		(MazeTowers.ItemBedrockDoor = new ItemDoor(
			MazeTowers.BlockBedrockDoor))
			.setUnlocalizedName("bedrock_door_item")
			.setCreativeTab(MazeTowers.TabExtra);
		(MazeTowers.ItemSpectriteGem = new ItemSpectriteGem())
			.setUnlocalizedName("spectrite_gem")
			.setCreativeTab(CreativeTabs.MATERIALS);
		MazeTowers.SPECTRITE_TOOL.setRepairItem(new ItemStack(MazeTowers.ItemSpectriteGem));
		MazeTowers.SPECTRITE.customCraftingMaterial = MazeTowers.ItemSpectriteGem;
		(MazeTowers.ItemSpectriteOrb = new ItemSpectriteOrb())
			.setUnlocalizedName("spectrite_orb");
		(MazeTowers.ItemExplosiveArrow = new ItemExplosiveArrow())
			.setUnlocalizedName("explosive_arrow");
		(MazeTowers.ItemExplosiveBow = new ItemExplosiveBow())
			.setUnlocalizedName("explosive_bow");
		(MazeTowers.ItemSpectriteShovel = new ItemSpectriteShovel())
			.setUnlocalizedName("spectrite_shovel");
		(MazeTowers.ItemSpectriteShovelSpecial = new ItemSpectriteShovelSpecial())
			.setUnlocalizedName("spectrite_shovel_special");
		(MazeTowers.ItemSpectritePickaxe = new ItemSpectritePickaxe())
			.setUnlocalizedName("spectrite_pickaxe");
		(MazeTowers.ItemSpectritePickaxeSpecial = new ItemSpectritePickaxeSpecial())
			.setUnlocalizedName("spectrite_pickaxe_special");
		(MazeTowers.ItemSpectriteAxe = new ItemSpectriteAxe())
			.setUnlocalizedName("spectrite_axe");
		(MazeTowers.ItemSpectriteAxeSpecial = new ItemSpectriteAxeSpecial())
			.setUnlocalizedName("spectrite_axe_special");
		(MazeTowers.ItemSpectriteSword = new ItemSpectriteSword())
			.setUnlocalizedName("spectrite_sword");
		(MazeTowers.ItemSpectriteKeySword = new ItemSpectriteKeySword())
			.setUnlocalizedName("spectrite_key_sword");
		(MazeTowers.ItemSpectriteSwordSpecial = new ItemSpectriteSwordSpecial())
			.setUnlocalizedName("spectrite_sword_special");
		(MazeTowers.ItemSpectriteKeySwordSpecial = new ItemSpectriteKeySwordSpecial())
			.setUnlocalizedName("spectrite_key_sword_special");
		(MazeTowers.ItemSpectriteHelmet = new ItemSpectriteArmor(EntityEquipmentSlot.HEAD))
			.setUnlocalizedName("spectrite_helmet");
		(MazeTowers.ItemSpectriteChestplate = new ItemSpectriteArmor(EntityEquipmentSlot.CHEST))
			.setUnlocalizedName("spectrite_chestplate");
		(MazeTowers.ItemSpectriteLeggings = new ItemSpectriteArmor(EntityEquipmentSlot.LEGS))
			.setUnlocalizedName("spectrite_leggings");
		(MazeTowers.ItemSpectriteBoots = new ItemSpectriteArmor(EntityEquipmentSlot.FEET))
			.setUnlocalizedName("spectrite_boots");
		(MazeTowers.ItemExplosiveCreeperSkull = new ItemExplosiveCreeperSkull())
			.setUnlocalizedName("explosive_creeper_skull_item");
		(MazeTowers.ItemChaoticSludgeBucket = new ItemChaoticSludgeBucket())
			.setUnlocalizedName("chaotic_sludge_bucket");
		MazeTowers.TileEntityCircuitBreaker = new TileEntityCircuitBreaker();
		MazeTowers.TileEntityExplosiveCreeperSkull = new TileEntityExplosiveCreeperSkull();
		MazeTowers.TileEntityItemScanner = new TileEntityItemScanner();
		MazeTowers.TileEntityLock = new TileEntityLock();
		MazeTowers.TileEntityMazeTowerThreshold = new TileEntityMazeTowerThreshold();
		MazeTowers.TileEntityMemoryPiston = new TileEntityMemoryPiston();
		MazeTowers.TileEntityMemoryPistonMemory = new TileEntityMemoryPistonMemory();
		MazeTowers.TileEntityMineralChest = new TileEntityMineralChest();
		MazeTowers.TileEntitySpecialMobSpawner = new TileEntitySpecialMobSpawner();
		MazeTowers.TileEntityWebSpiderSpawner = new TileEntityWebSpiderSpawner();
		MazeTowers.spectrite = new WorldGenSpectrite();
		MazeTowers.mazeTowers = new WorldGenMazeTowers();
		MazeTowers.BlockLock.initLockColors();
		CapabilityManager.INSTANCE.register(IMazeTowerCapability.class,
			new PlayerMazeTower.DefaultImpl.Storage(),
			PlayerMazeTower.DefaultImpl.class);
		ModSounds.initSounds();
		ModBlocks.createBlocks();
		ModItems.createItems();
		ModTileEntities.initTileEntities();
		ModDispenserBehavior.initDispenserBehavior();
		ModWorldGen.initWorldGen();

		FMLCommonHandler.instance().bus().register(
			MazeTowers.instance);
		MinecraftForge.EVENT_BUS
			.register(MazeTowers.instance);
	}

	public void init(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(
			MazeTowers.instance,
			new GuiHandlerItemScanner());
		ModCrafting.initCrafting();
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
