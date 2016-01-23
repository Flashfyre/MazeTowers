package com.samuel.mazetowers;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.netty.buffer.ByteBuf;

import com.samuel.mazetowers.etc.CommandMazeTowers;
import com.samuel.mazetowers.etc.MaterialLogicSolid;
import com.samuel.mazetowers.eventhandlers.MazeTowersChunkEventHandler;
import com.samuel.mazetowers.eventhandlers.MazeTowersGeneralEventHandler;
import com.samuel.mazetowers.init.ModEntities;
import com.samuel.mazetowers.packets.*;
import com.samuel.mazetowers.proxy.CommonProxy;
import com.samuel.mazetowers.tileentities.*;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTower;
import com.samuel.mazetowers.worldgen.biomes.BiomeGenMazeTowerLv1;
import com.samuel.mazetowers.worldgen.biomes.BiomeGenMazeTowerLv7;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = MazeTowers.MODID, name = MazeTowers.MODNAME, version = MazeTowers.VERSION,
guiFactory = "com.samuel." + MazeTowers.MODID + ".GUIFactoryMazeTowers")
public class MazeTowers {
    public static final String MODNAME = "mazetowers";
    public static final String MODID = "mazetowers";
    public static final String VERSION = "0.2.0";
    
    @Mod.Instance
	public static MazeTowers instance = new MazeTowers();
    public static WorldGenMazeTowers mazeTowers = null;
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
    public static Block BlockMemoryPiston;
    public static Block BlockMemoryPistonOff;
    public static Block BlockMemoryPistonHead;
    public static Block BlockMemoryPistonHeadOff;
	public static Block BlockMemoryPistonExtension;
	public static Block BlockMemoryPistonExtensionOff;
	public static Block BlockIronChest;
	public static Block BlockGoldChest;
	public static Block BlockDiamondChest;
	public static Block BlockEndStoneDoor;
	public static Block BlockQuartzDoor;
	public static Block BlockObsidianDoor;
	public static Block BlockBedrockDoor;
	public static Item ItemEndStoneDoor;
	public static Item ItemQuartzDoor;
	public static Item ItemObsidianDoor;
	public static Item ItemBedrockDoor;
	public static MaterialLogicSolid solidCircuits;
	public static BiomeGenMazeTowerLv1 biomeGenMazeTowerLv1;
	public static BiomeGenMazeTowerLv7 biomeGenMazeTowerLv7;
    public static boolean enableMazeTowers = true;

	@SidedProxy(clientSide="com.samuel.mazetowers.proxy.ClientProxy", serverSide="com.samuel.mazeTowers.proxy.ServerProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper network;
	public static Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("MazeTowers");
		//network.registerMessage(PacketBGMClient.Handler.class, PacketBGMClient.class, 0, Side.CLIENT);
		network.registerMessage(PacketActivateItemScanner.Handler.class,
			PacketActivateItemScanner.class, 0, Side.SERVER);
		network.registerMessage(PacketDebugMessage.Handler.class,
			PacketDebugMessage.class, 1, Side.CLIENT);
		config = new Configuration(e.getSuggestedConfigurationFile());
	    config.load();
	    saveConfig();
	    MinecraftForge.EVENT_BUS.register(new MazeTowersChunkEventHandler());
	    MinecraftForge.EVENT_BUS.register(new MazeTowersGeneralEventHandler());
		//MinecraftForge.EVENT_BUS.register(new ChaosLabyrinthBGMEventHandler());
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
		event.registerServerCommand(new CommandMazeTowers());
		//event.registerServerCommand(new CommandItemScanner());
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onMobDrop(LivingDropsEvent event) {
		/*Random rand = event.entity.worldObj.rand;
		if (event.entity instanceof EntityChaoticWitherSkeleton) {
			float dropChanceMultiplier = 1F;
			for (int d = 0; d < event.drops.size(); d++) {
				if (((EntityItem) event.drops.get(d)).getEntityItem().getItem() instanceof ItemChaosBattleKey ||
					((EntityItem) event.drops.get(d)).getEntityItem().getItem() instanceof ItemChaosArmor) {
					if (((EntityItem) event.drops.get(d)).getEntityItem().getItem() instanceof ItemChaosArmor)
						dropChanceMultiplier += 0.5F;
					event.drops.remove(d);
					d--;
				}
			}
	
			int keyDamage = ((EntityChaoticWitherSkeleton) event.entity).getHeldItem().getItemDamage();
			
			if (event.entity.worldObj.rand.nextInt(100) / dropChanceMultiplier == 0) {
				if (event.entity.worldObj.rand.nextInt(2 + keyDamage) != 0)
        			event.entity.dropItem(ModItems.chaos_Key, 1);
        		else
        			event.entity.entityDropItem(new ItemStack(ModItems.chaos_Battle_Key, 1, keyDamage), 0.0F);
        	}
			
			List<Item> armor = ((EntityChaoticWitherSkeleton) event.entity).getArmor();
			
			for (int a = 0; a < armor.size(); a++)
	    		if (event.entity.worldObj.rand.nextFloat() <= 0.04 * dropChanceMultiplier)
	    			event.entity.dropItem(armor.get(a), 1);
		}*/
    }
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	@SideOnly(Side.CLIENT)
    public void onAttackEntity(AttackEntityEvent event) {
		/*if (event.entityPlayer instanceof EntityPlayerSP && event.entityPlayer.dimension == 77 && event.target instanceof EntityGiantChaoticSlime) {
			event = (AttackEntityEvent) event;
			MazeTowers.network.sendToServer(new PacketAttackGiantChaoticSlime());
			//event.target.attackEntityFrom(event.entityPlayer, amount)
		}*/
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	@SideOnly(Side.CLIENT)
    public void onGUIOpen(GuiOpenEvent event) {
		/*if (event.gui instanceof GuiDownloadTerrain) {
			Field f_NetHandler = findObfuscatedField(GuiDownloadTerrain.class, "netHandlerPlayClient", "field_146594_a");
			Field f_WorldClient = findObfuscatedField(NetHandlerPlayClient.class, "clientWorldController", "field_147300_g");
            f_NetHandler.setAccessible(true);
            f_NetHandler.setAccessible(true);
    		try {
    			NetHandlerPlayClient netHandler = (NetHandlerPlayClient) f_NetHandler.get(event.gui);
				WorldClient clientWorldController = (WorldClient) f_WorldClient.get(netHandler);
				
				if (clientWorldController.provider instanceof WorldProviderChaos) {
					GuiGenerateChaosDimension chaosGUI = (GuiGenerateChaosDimension) (event.gui = new GuiGenerateChaosDimension(netHandler));
					chaosGUI.initGui();
					//event.gui.mc.displayGuiScreen(chaosGUI);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}*/
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onItemToss(ItemTossEvent event) {
		/*ItemStack item = event.entityItem.getEntityItem();
		boolean isArmor;
		boolean isKeySword = false;
		if (((isArmor = (item.getItem() instanceof ItemChaosArmor)) || (isKeySword = (item.getItem() == itemChaosBattleKey)) ||
			item.getItem() == itemChaosBow) && item.hasTagCompound() && item.getTagCompound().hasKey("owner")) {
			int slotIndex = event.player.inventory.getFirstEmptyStack();
			event.player.inventory.setItemStack(null);
			event.player.inventory.addItemStackToInventory(item);
			if (isArmor)
				((ItemChaosArmor) item.getItem()).onLockedDroppedByPlayer(item, event.player, slotIndex);
			else {
				if (isKeySword)
					((ItemChaosBattleKey) item.getItem()).onLockedDroppedByPlayer(item, event.player, slotIndex);
				else
					((ItemChaosBow) item.getItem()).onLockedDroppedByPlayer(item, event.player, slotIndex);
			}
			event.setCanceled(true);
		}*/
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onItemCrafted(PlayerEvent.ItemCraftedEvent e) {
		/*boolean isArmor;
		if ((isArmor = e.crafting.getItem() instanceof ItemChaosArmor) || e.crafting.getItem() == MazeTowers.itemChaosBattleKey) {
			for (int i = 0; i < 9; i++) {
				ItemStack curStack = e.craftMatrix.getStackInSlot(i);
				if (curStack != null && ((isArmor && curStack.getItem() instanceof ItemChaosArmor &&
					((ItemChaosArmor) curStack.getItem()).armorType == ((ItemChaosArmor) e.crafting.getItem()).armorType) ||
					(!isArmor && curStack.getItem() == MazeTowers.itemChaosBattleKey))) {
					if (curStack.isItemEnchanted())
						e.crafting.setTagInfo("ench", curStack.getEnchantmentTagList());
					break;
				}
			}
		}*/
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onFOVUpdateEvent(FOVUpdateEvent e) {
		/*if (e.entity.isUsingItem() && e.entity.getHeldItem().getItem() == MazeTowers.itemChaosBow) {
			EntityPlayer player = e.entity;
			int i = player.getItemInUseDuration();
            float f1 = (float)i / 20.0F;

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 *= f1;
            }

            e.newfov = e.fov * (1.0F - f1 * 0.15F);
		}*/
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEntityAttack(LivingAttackEvent e) {
		/*if (e.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.entityLiving;
			ItemStack item = player.getHeldItem();
			int itemDamage = item.getItemDamage();
			if (item.getItem() == MazeTowers.itemChaosBattleKey &&
				(item.getItemDamage() == 6 || (itemDamage == 5 && player.worldObj.rand.nextFloat() < 0.32F)) && player.isUsingItem()) {
	            float damage = e.ammount * ((float) 25 - player.getTotalArmorValue());
	            damage = damage / 25.0F;
				float healAmount = damage * 0.25f;
				player.heal(healAmount);
				if (!player.worldObj.isRemote) {
					EnumParticleTypes particle = EnumParticleTypes.SPELL_INSTANT;
					((WorldServer) player.worldObj).spawnParticle(particle,
						particle.getShouldIgnoreRange(), player.posX,
						player.getEntityBoundingBox().maxY, player.posZ, 1,
						0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
				}
			}
		}*/
	}
	
	private static Field findObfuscatedField(Class<?> clazz, String... names) {
        return ReflectionHelper.findField(clazz, ObfuscationReflectionHelper.remapFieldNames(clazz.getName(), names));
    }
	
	public void saveConfig() {
		Property spawnMazeTowers = config.get(Configuration.CATEGORY_GENERAL, "Spawn Maze Towers", true);
		/*Property generateChaosLabyrinthProtrusions = config.get(Configuration.CATEGORY_GENERAL, "Generate Chaos Labyrinth Protrusions", true);
		Property enableDisplayLockedItemOwnerNames = config.get(Configuration.CATEGORY_GENERAL, "Enable Display Locked Item Owner Names", true);
		Property volatileExplosionChainLimit = config.get("Volatile Explosion Chain Limit", Configuration.CATEGORY_GENERAL, 0);
	    spawnMazeTowers.comment = "Disable this to prevent the maze towers from " +
		    "spawning in worlds. If enabled, maze towers will spawn in any world " +
		    "where it has not been created. Existing maze towers will not be affected by this setting.";*/
	    /*generateChaosLabyrinthProtrusions.comment = "Disable this to prevent the generation " +
		    "of chaos block protrusions out of the Chaos Labyrinth, and thereby  shortening " +
	    	"the structure's generation time. This will not affect the protrusions of the entrance. " +
	    	"An existing Chaos Labyrinth will not be affected by this setting.";
	    enableDisplayLockedItemOwnerNames.comment = "Disable this to prevent locked items (rare items that, once used, disappear " +
	    	"upon being dropped) from displaying the owner's name in the tooltip window.";
	    volatileExplosionChainLimit.comment = "The amount of volatile/unstable chaos block explosions to allow before a block won't explode when triggered. (0 is unlimited)";*/
	    this.enableMazeTowers = spawnMazeTowers.getBoolean(true);
	    //this.enableChaosLabyrinthProtrusions = generateChaosLabyrinthProtrusions.getBoolean(true);
	    //this.enableDisplayLockedItemOwnerNames = enableDisplayLockedItemOwnerNames.getBoolean(true);
	    //his.volatileExplosionChainLimit = volatileExplosionChainLimit.getInt(0);
	    config.save();
	}
}