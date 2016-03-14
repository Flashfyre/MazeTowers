package com.samuel.mazetowers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.blocks.*;
import com.samuel.mazetowers.client.renderer.texture.TextureRedstoneClock;
import com.samuel.mazetowers.etc.CommandMazeTowers;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.eventhandlers.MazeTowersChunkEventHandler;
import com.samuel.mazetowers.eventhandlers.MazeTowersGeneralEventHandler;
import com.samuel.mazetowers.eventhandlers.MazeTowersGuiEventHandler;
import com.samuel.mazetowers.eventhandlers.TextureStitchEventHandler;
import com.samuel.mazetowers.init.ModEntities;
import com.samuel.mazetowers.items.*;
import com.samuel.mazetowers.packets.*;
import com.samuel.mazetowers.proxy.CommonProxy;
import com.samuel.mazetowers.tileentities.*;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers;

@Mod(modid = MazeTowers.MODID, name = MazeTowers.MODNAME, version = MazeTowers.VERSION, guiFactory = "com.samuel."
	+ MazeTowers.MODID + ".client.gui.GUIFactoryMazeTowers")
public class MazeTowers {
	public static final String MODNAME = "mazetowers";
	public static final String MODID = "mazetowers";
	public static final String VERSION = "0.6.1";

	@Mod.Instance
	public static MazeTowers instance = new MazeTowers();
	public static CreativeTabs tabExtra;
	public static WorldGenMazeTowers mazeTowers = null;
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
	public static ItemKey ItemKey;
	public static ItemRAM ItemRAM;
	public static ItemExplosiveArrow ItemExplosiveArrow;
	public static ItemExplosiveBow ItemExplosiveBow;
	public static ItemExplosiveCreeperSkull ItemExplosiveCreeperSkull;
	public static ItemChaoticSludgeBucket ItemChaoticSludgeBucket;
	public static Fluid FluidChaoticSludge;
	public static MaterialLogicSolid solidCircuits;
	public static ArmorMaterial EXPLOSIVE_CREEPER_HEAD = new EnumHelper()
		.addArmorMaterial("explosive_creeper_head", "mazetowers:explosive_creeper_head",
		0, new int[]{2, 0, 0, 0}, 25);
	public static TextureRedstoneClock TextureRedstoneClock;
	public static TextureRedstoneClock TextureRedstoneClockInverted;
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
		MinecraftForge.EVENT_BUS
			.register(new TextureStitchEventHandler());
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
			StatCollector.translateToLocal("gui.spawn_maze_towers.name"), true,
			StatCollector.translateToLocal("gui.spawn_maze_towers.desc"));
		Property blockProtection = config.get(
			Configuration.CATEGORY_GENERAL, StatCollector.translateToLocal(
			"gui.block_protection.name"), true,
			StatCollector.translateToLocal("gui.block_protection.desc"));
		this.enableMazeTowers = spawnMazeTowers
			.getBoolean(true);
		this.enableMazeTowers = blockProtection
			.getBoolean(true);
		config.save();
	}
}