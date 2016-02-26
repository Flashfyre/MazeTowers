package com.samuel.mazetowers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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

import com.samuel.mazetowers.etc.CommandMazeTowers;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.eventhandlers.MazeTowersChunkEventHandler;
import com.samuel.mazetowers.eventhandlers.MazeTowersGeneralEventHandler;
import com.samuel.mazetowers.eventhandlers.MazeTowersGuiEventHandler;
import com.samuel.mazetowers.init.ModEntities;
import com.samuel.mazetowers.packets.PacketActivateItemScanner;
import com.samuel.mazetowers.packets.PacketDebugMessage;
import com.samuel.mazetowers.packets.PacketMazeTowersGui;
import com.samuel.mazetowers.packets.PacketUpdateBlockRange;
import com.samuel.mazetowers.proxy.CommonProxy;
import com.samuel.mazetowers.tileentities.*;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers;

@Mod(modid = MazeTowers.MODID, name = MazeTowers.MODNAME, version = MazeTowers.VERSION, guiFactory = "com.samuel."
	+ MazeTowers.MODID + ".GUIFactoryMazeTowers")
public class MazeTowers {
	public static final String MODNAME = "mazetowers";
	public static final String MODID = "mazetowers";
	public static final String VERSION = "0.5.0";

	@Mod.Instance
	public static MazeTowers instance = new MazeTowers();
	public static WorldGenMazeTowers mazeTowers = null;
	public static TileEntityBlockProtect TileEntityBlockProtect;
	public static TileEntityCircuitBreaker TileEntityCircuitBreaker;
	public static TileEntityItemScanner TileEntityItemScanner;
	public static TileEntityMazeTowerThreshold TileEntityMazeTowerThreshold;
	public static TileEntityMemoryPiston TileEntityMemoryPiston;
	public static TileEntityMemoryPistonMemory TileEntityMemoryPistonMemory;
	public static TileEntityMineralChest TileEntityMineralChest;
	public static TileEntityWebSpiderSpawner TileEntityWebSpiderSpawner;
	public static Block BlockHiddenButton;
	public static Block BlockHiddenPressurePlateWeighted;
	public static Block BlockItemScanner;
	public static Block BlockItemScannerGold;
	public static Block BlockMazeTowerThreshold;
	public static Block BlockMemoryPiston;
	public static Block BlockMemoryPistonOff;
	public static Block BlockMemoryPistonHead;
	public static Block BlockMemoryPistonHeadOff;
	public static Block BlockMemoryPistonExtension;
	public static Block BlockMemoryPistonExtensionOff;
	public static Block BlockIronChest;
	public static Block BlockGoldChest;
	public static Block BlockDiamondChest;
	public static Block BlockPackedIceStairs;
	public static Block BlockPrismarineBrickStairs;
	public static Block BlockEndStoneStairs;
	public static Block BlockObsidianStairs;
	public static Block BlockBedrockStairs;
	public static Block BlockSandstoneWall;
	public static Block BlockRedSandstoneWall;
	public static Block BlockStoneBrickWall;
	public static Block BlockPackedIceWall;
	public static Block BlockPrismarineBrickWall;
	public static Block BlockQuartzWall;
	public static Block BlockEndStoneWall;
	public static Block BlockObsidianWall;
	public static Block BlockBedrockWall;
	public static Block BlockEndStoneDoor;
	public static Block BlockQuartzDoor;
	public static Block BlockObsidianDoor;
	public static Block BlockBedrockDoor;
	public static Block BlockChaoticSludge;
	public static Item ItemEndStoneDoor;
	public static Item ItemQuartzDoor;
	public static Item ItemObsidianDoor;
	public static Item ItemBedrockDoor;
	public static Item ItemExplosiveArrow;
	public static Item ItemExplosiveBow;
	public static Fluid FluidChaoticSludge;
	public static MaterialLogicSolid solidCircuits;
	public static boolean enableMazeTowers = true;

	@SidedProxy(clientSide = "com.samuel.mazetowers.proxy.ClientProxy", serverSide = "com.samuel.mazetowers.proxy.ServerProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper network;
	public static Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		network = NetworkRegistry.INSTANCE
			.newSimpleChannel("MazeTowers");
		network
			.registerMessage(
				PacketActivateItemScanner.Handler.class,
				PacketActivateItemScanner.class, 0,
				Side.SERVER);
		network.registerMessage(
			PacketMazeTowersGui.Handler.class,
			PacketMazeTowersGui.class, 1, Side.CLIENT);
		network.registerMessage(
			PacketUpdateBlockRange.Handler.class,
			PacketUpdateBlockRange.class, 2, Side.CLIENT);
		network.registerMessage(
			PacketDebugMessage.Handler.class,
			PacketDebugMessage.class, 3, Side.CLIENT);
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
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
		ModEntities.initEntities(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event
			.registerServerCommand(new CommandMazeTowers());
		// event.registerServerCommand(new CommandItemScanner());
	}
	
	public void saveConfig() {
		Property spawnMazeTowers = config.get(
			Configuration.CATEGORY_GENERAL,
			"Spawn Maze Towers", true);
		this.enableMazeTowers = spawnMazeTowers
			.getBoolean(true);
		config.save();
	}
}