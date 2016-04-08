package com.samuel.mazetowers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.samuel.mazetowers.blocks.BlockChaoticSludge;
import com.samuel.mazetowers.blocks.BlockExplosiveCreeperSkull;
import com.samuel.mazetowers.blocks.BlockExtraDoor;
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
import com.samuel.mazetowers.blocks.BlockRedstoneClock;
import com.samuel.mazetowers.blocks.BlockSpecialMobSpawner;
import com.samuel.mazetowers.blocks.BlockSpectrite;
import com.samuel.mazetowers.blocks.BlockSpectriteOre;
import com.samuel.mazetowers.blocks.BlockVendorSpawner;
import com.samuel.mazetowers.etc.CommandMazeTowers;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.eventhandlers.MazeTowersChunkEventHandler;
import com.samuel.mazetowers.eventhandlers.MazeTowersGeneralEventHandler;
import com.samuel.mazetowers.eventhandlers.MazeTowersGuiEventHandler;
import com.samuel.mazetowers.init.ModEntities;
import com.samuel.mazetowers.items.ItemChaoticSludgeBucket;
import com.samuel.mazetowers.items.ItemColoredKey;
import com.samuel.mazetowers.items.ItemDiamondRod;
import com.samuel.mazetowers.items.ItemExplosiveArrow;
import com.samuel.mazetowers.items.ItemExplosiveBow;
import com.samuel.mazetowers.items.ItemExplosiveCreeperSkull;
import com.samuel.mazetowers.items.ItemRAM;
import com.samuel.mazetowers.items.ItemSpectriteArmor;
import com.samuel.mazetowers.items.ItemSpectriteGem;
import com.samuel.mazetowers.items.ItemSpectriteKey;
import com.samuel.mazetowers.items.ItemSpectriteKeySword;
import com.samuel.mazetowers.items.ItemSpectriteOrb;
import com.samuel.mazetowers.items.ItemSpectritePickaxe;
import com.samuel.mazetowers.items.ItemSpectriteSword;
import com.samuel.mazetowers.packets.PacketActivateItemScanner;
import com.samuel.mazetowers.packets.PacketDebugMessage;
import com.samuel.mazetowers.packets.PacketMazeTowersGui;
import com.samuel.mazetowers.proxy.CommonProxy;
import com.samuel.mazetowers.tileentity.TileEntityCircuitBreaker;
import com.samuel.mazetowers.tileentity.TileEntityExplosiveCreeperSkull;
import com.samuel.mazetowers.tileentity.TileEntityItemScanner;
import com.samuel.mazetowers.tileentity.TileEntityLock;
import com.samuel.mazetowers.tileentity.TileEntityMazeTowerThreshold;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPiston;
import com.samuel.mazetowers.tileentity.TileEntityMemoryPistonMemory;
import com.samuel.mazetowers.tileentity.TileEntityMineralChest;
import com.samuel.mazetowers.tileentity.TileEntitySpecialMobSpawner;
import com.samuel.mazetowers.tileentity.TileEntityVendorSpawner;
import com.samuel.mazetowers.tileentity.TileEntityWebSpiderSpawner;
import com.samuel.mazetowers.world.WorldGenMazeTowers;
import com.samuel.mazetowers.world.WorldGenSpectrite;

@Mod(modid = MazeTowers.MODID, name = MazeTowers.MODNAME, version = MazeTowers.VERSION, guiFactory = "com.samuel."
	+ MazeTowers.MODID + ".client.gui.GUIFactoryMazeTowers")
public class MazeTowers {
	public static final String MODNAME = "mazetowers";
	public static final String MODID = "mazetowers";
	public static final String VERSION = "0.7.0";

	@Mod.Instance
	public static MazeTowers instance = new MazeTowers();
	public static CreativeTabs TabExtra;
	public static WorldGenMazeTowers mazeTowers = null;
	public static WorldGenSpectrite spectrite = null;
	public static TileEntityCircuitBreaker TileEntityCircuitBreaker;
	public static TileEntityExplosiveCreeperSkull TileEntityExplosiveCreeperSkull;
	public static TileEntityItemScanner TileEntityItemScanner;
	public static TileEntityLock TileEntityLock;
	public static TileEntityMazeTowerThreshold TileEntityMazeTowerThreshold;
	public static TileEntityMemoryPiston TileEntityMemoryPiston;
	public static TileEntityMemoryPistonMemory TileEntityMemoryPistonMemory;
	public static TileEntityMineralChest TileEntityMineralChest;
	public static TileEntitySpecialMobSpawner TileEntitySpecialMobSpawner;
	public static TileEntityVendorSpawner TileEntityVendorSpawner;
	public static TileEntityWebSpiderSpawner TileEntityWebSpiderSpawner;
	public static BlockHiddenButton BlockHiddenButton;
	public static BlockHiddenPressurePlateWeighted BlockHiddenPressurePlateWeighted;
	public static BlockItemScanner BlockItemScanner;
	public static BlockItemScannerGold BlockItemScannerGold;
	public static BlockMazeTowerThreshold BlockMazeTowerThreshold;
	public static BlockMemoryPistonBase BlockMemoryPiston;
	public static BlockMemoryPistonBaseOff BlockMemoryPistonOff;
	public static BlockMemoryPistonExtension BlockMemoryPistonHead;
	public static BlockMemoryPistonExtensionOff BlockMemoryPistonHeadOff;
	public static BlockMemoryPistonMoving BlockMemoryPistonExtension;
	public static BlockMemoryPistonMovingOff BlockMemoryPistonExtensionOff;
	public static BlockMineralChest BlockIronChest;
	public static BlockMineralChest BlockGoldChest;
	public static BlockMineralChest BlockDiamondChest;
	public static BlockMineralChest BlockSpectriteChest;
	public static BlockMineralChest BlockTrappedIronChest;
	public static BlockMineralChest BlockTrappedGoldChest;
	public static BlockMineralChest BlockTrappedDiamondChest;
	public static BlockMineralChest BlockTrappedSpectriteChest;
	public static BlockExtraStairs BlockPackedIceStairs;
	public static BlockExtraStairs BlockPrismarineBrickStairs;
	public static BlockExtraStairs BlockEndStoneStairs;
	public static BlockExtraStairs BlockObsidianStairs;
	public static BlockExtraStairs BlockBedrockStairs;
	public static BlockExtraWall BlockSandstoneWall;
	public static BlockExtraWall BlockRedSandstoneWall;
	public static BlockExtraWall BlockStoneBrickWall;
	public static BlockExtraWall BlockMossyStoneBrickWall;
	public static BlockExtraWall BlockPackedIceWall;
	public static BlockExtraWall BlockPrismarineBrickWall;
	public static BlockExtraWall BlockQuartzWall;
	public static BlockExtraWall BlockEndStoneWall;
	public static BlockExtraWall BlockPurpurWall;
	public static BlockExtraWall BlockObsidianWall;
	public static BlockExtraWall BlockBedrockWall;
	public static BlockExtraDoor BlockPrismarineDoor;
	public static BlockExtraDoor BlockQuartzDoor;
	public static BlockExtraDoor BlockEndStoneDoor;
	public static BlockExtraDoor BlockPurpurDoor;
	public static BlockExtraDoor BlockObsidianDoor;
	public static BlockExtraDoor BlockBedrockDoor;
	public static BlockLock BlockLock;
	public static BlockRedstoneClock BlockRedstoneClock;
	public static BlockRedstoneClock BlockRedstoneClockInverted;
	public static BlockSpectriteOre BlockSpectriteOre;
	public static BlockSpectrite BlockSpectrite;
	public static BlockExplosiveCreeperSkull BlockExplosiveCreeperSkull;
	public static BlockSpecialMobSpawner BlockSpecialMobSpawner;
	public static BlockVendorSpawner BlockVendorSpawner;
	public static BlockChaoticSludge BlockChaoticSludge;
	public static ItemDoor ItemPrismarineDoor;
	public static ItemDoor ItemQuartzDoor;
	public static ItemDoor ItemEndStoneDoor;
	public static ItemDoor ItemPurpurDoor;
	public static ItemDoor ItemObsidianDoor;
	public static ItemDoor ItemBedrockDoor;
	public static ItemColoredKey ItemColoredKey;
	public static ItemSpectriteKey ItemSpectriteKey;
	public static ItemDiamondRod ItemDiamondRod;
	public static ItemRAM ItemRAM;
	public static ItemSpectriteGem ItemSpectriteGem;
	public static ItemSpectriteOrb ItemSpectriteOrb;
	public static ItemExplosiveArrow ItemExplosiveArrow;
	public static ItemExplosiveBow ItemExplosiveBow;
	public static ItemSpectritePickaxe ItemSpectritePickaxe;
	public static ItemSpectriteSword ItemSpectriteSword;
	public static ItemSpectriteKeySword ItemSpectriteKeySword;
	public static ItemSpectriteArmor ItemSpectriteHelmet;
	public static ItemSpectriteArmor ItemSpectriteChestplate;
	public static ItemSpectriteArmor ItemSpectriteLeggings;
	public static ItemSpectriteArmor ItemSpectriteBoots;
	public static ItemExplosiveCreeperSkull ItemExplosiveCreeperSkull;
	public static ItemChaoticSludgeBucket ItemChaoticSludgeBucket;
	public static Fluid FluidChaoticSludge;
	public static MaterialLogicSolid solidCircuits;
	public static ArmorMaterial EXPLOSIVE_CREEPER_HEAD = new EnumHelper()
		.addArmorMaterial("explosive_creeper_head", "mazetowers:explosive_creeper_head",
		0, new int[]{2, 0, 0, 0}, 25, SoundEvents.item_armor_equip_leather);
	public static ArmorMaterial SPECTRITE = new EnumHelper()
		.addArmorMaterial("spectrite", "mazetowers:spectrite_armor",
		0, new int[]{3, 6, 8, 3}, 25, SoundEvents.item_armor_equip_diamond);
	public static ToolMaterial SPECTRITE_TOOL = new EnumHelper()
		.addToolMaterial("spectrite_tool", 3, 4900, 12.0F, 4.0F, 21);
	public static IItemPropertyGetter ItemPropertyGetterSpectrite;
	public static boolean enableMazeTowers = true;
	public static boolean blockProtection = true;

	@SidedProxy(clientSide = "com.samuel.mazetowers.proxy.ClientProxy",
		serverSide = "com.samuel.mazetowers.proxy.ServerProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper network;
	public static Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		network = NetworkRegistry.INSTANCE
			.newSimpleChannel("MazeTowers");
		network.registerMessage(
			PacketActivateItemScanner.Handler.class,
			PacketActivateItemScanner.class, 0, Side.SERVER);
		network.registerMessage(
			PacketMazeTowersGui.Handler.class,
			PacketMazeTowersGui.class, 1, Side.CLIENT);
		network.registerMessage(
			PacketDebugMessage.Handler.class,
			PacketDebugMessage.class, 2, Side.CLIENT);
		config = new Configuration(e
			.getSuggestedConfigurationFile());
		config.load();
		saveConfig();
		MinecraftForge.EVENT_BUS
			.register(new MazeTowersChunkEventHandler());
		MinecraftForge.EVENT_BUS
			.register(new MazeTowersGeneralEventHandler());
		MinecraftForge.EVENT_BUS
			.register(new MazeTowersGuiEventHandler());
		ModEntities.initEntities(this);
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandMazeTowers());
	}
	
	public void saveConfig() {
		Property spawnMazeTowers = config.get(
			Configuration.CATEGORY_GENERAL,
			I18n.translateToLocal("gui.spawn_maze_towers.name"), true,
			I18n.translateToLocal("gui.spawn_maze_towers.desc"));
		Property blockProtection = config.get(
			Configuration.CATEGORY_GENERAL, I18n.translateToLocal(
			"gui.block_protection.name"), true,
			I18n.translateToLocal("gui.block_protection.desc"));
		MazeTowers.enableMazeTowers = spawnMazeTowers
			.getBoolean(true);
		MazeTowers.enableMazeTowers = blockProtection
			.getBoolean(true);
		config.save();
	}
}