package com.samuel.mazetowers.proxy;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.HashBiMap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockHiddenButton;
import com.samuel.mazetowers.blocks.BlockHiddenPressurePlateWeighted;
import com.samuel.mazetowers.entities.*;
import com.samuel.mazetowers.etc.HiddenButtonModel;
import com.samuel.mazetowers.etc.HiddenPressurePlateWeightedModel;
import com.samuel.mazetowers.etc.UltravioletFireModel;
import com.samuel.mazetowers.eventhandlers.ModelBakeEventHandler;
import com.samuel.mazetowers.render.BlockRenderRegister;
import com.samuel.mazetowers.render.ItemRenderRegister;
import com.samuel.mazetowers.render.entities.*;
import com.samuel.mazetowers.render.tileentities.*;
import com.samuel.mazetowers.tileentities.*;

import net.minecraft.block.BlockButton;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				ModelResourceLocation mrl;
				if (state.getBlock() == MazeTowers.BlockHiddenPressurePlateWeighted) {
					return state.getValue(BlockHiddenPressurePlateWeighted.POWER) == 0 ?
					HiddenPressurePlateWeightedModel.modelResourceLocationUp :
					HiddenPressurePlateWeightedModel.modelResourceLocationDown;
				} else {
					switch (state.getBlock().getMetaFromState(state)) {
						case 0:
							mrl = HiddenButtonModel.modelResourceLocationU;
							break;
						case 1:
							mrl = HiddenButtonModel.modelResourceLocationD;
							break;
						case 2:
							mrl = HiddenButtonModel.modelResourceLocationE;
							break;
						case 3:
							mrl = HiddenButtonModel.modelResourceLocationW;
							break;
						case 4:
							mrl = HiddenButtonModel.modelResourceLocationS;
							break;
						case 5:
							mrl = HiddenButtonModel.modelResourceLocationN;
							break;
						case 6:
							mrl = HiddenButtonModel.modelResourceLocationUPressed;
							break;
						case 7:
							mrl = HiddenButtonModel.modelResourceLocationDPressed;
							break;
						case 8:
							mrl = HiddenButtonModel.modelResourceLocationEPressed;
							break;
						case 9:
							mrl = HiddenButtonModel.modelResourceLocationWPressed;
							break;
						case 10:
							mrl = HiddenButtonModel.modelResourceLocationSPressed;
							break;
						default:
							mrl = HiddenButtonModel.modelResourceLocationNPressed;
					}
					return mrl;
				}
			}
	    };
	    ModelLoader.setCustomStateMapper(MazeTowers.BlockHiddenPressurePlateWeighted,
	    	ignoreState);
	    ModelLoader.setCustomStateMapper(MazeTowers.BlockHiddenButton,
		    ignoreState);
		    
		ItemRenderRegister.registerItemRenderer();
		
		MinecraftForge.EVENT_BUS.register(ModelBakeEventHandler.instance);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		BlockRenderRegister.registerBlockRenderer();
		
		RenderingRegistry.registerEntityRenderingHandler(EntityExplosiveArrow.class,
				new RenderExplosiveArrow(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySmallUltravioletFireball.class,
				new RenderSmallUltravioletFireball(Minecraft.getMinecraft().getRenderManager(), 1.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityUltravioletBlaze.class,
			new RenderUltravioletBlaze(Minecraft.getMinecraft().getRenderManager()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMineralChest.class,
			new TileEntityMineralChestRenderer());
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}
	
	private static Field findObfuscatedField(Class<?> clazz, String... names)
    {
        return ReflectionHelper.findField(clazz, ObfuscationReflectionHelper.remapFieldNames(clazz.getName(), names));
    }
}
