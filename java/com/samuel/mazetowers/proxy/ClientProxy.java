package com.samuel.mazetowers.proxy;

import java.lang.reflect.Field;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockChaoticSludge;
import com.samuel.mazetowers.blocks.BlockHiddenPressurePlateWeighted;
import com.samuel.mazetowers.entities.*;
import com.samuel.mazetowers.etc.HiddenButtonModel;
import com.samuel.mazetowers.etc.HiddenPressurePlateWeightedModel;
import com.samuel.mazetowers.eventhandlers.ModelBakeEventHandler;
import com.samuel.mazetowers.render.BlockRenderRegister;
import com.samuel.mazetowers.render.EntityRenderRegister;
import com.samuel.mazetowers.render.ItemRenderRegister;
import com.samuel.mazetowers.render.entities.*;
import com.samuel.mazetowers.render.tileentities.*;
import com.samuel.mazetowers.tileentities.*;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(
				IBlockState state) {
				ModelResourceLocation mrl;
				if (state.getBlock() == MazeTowers.BlockHiddenPressurePlateWeighted) {
					return state
						.getValue(BlockHiddenPressurePlateWeighted.POWER) == 0 ? HiddenPressurePlateWeightedModel.modelResourceLocationUp
						: HiddenPressurePlateWeightedModel.modelResourceLocationDown;
				} else {
					switch (state.getBlock()
						.getMetaFromState(state)) {
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
		ModelLoader.setCustomStateMapper(
			MazeTowers.BlockHiddenPressurePlateWeighted, ignoreState);
		ModelLoader.setCustomStateMapper(
			MazeTowers.BlockHiddenButton, ignoreState);
		final ModelResourceLocation chaoticSludgeModelResourceLocation = new ModelResourceLocation(
			"mazetowers:chaotic_sludge",
			MazeTowers.BlockChaoticSludge.getFluid().getName());
		ModelLoader.setCustomStateMapper(
			MazeTowers.BlockChaoticSludge,
			(new StateMap.Builder()).ignore(
				BlockFluidBase.LEVEL).build());
		ModelLoader.setBucketModelDefinition(MazeTowers.ItemChaoticSludgeBucket);
		EntityRenderRegister.registerEntityRenderer();
		ItemRenderRegister.registerItemRenderer();
		for (int k = 0; k < 20; k++) {
			ModelLoader.setCustomModelResourceLocation(MazeTowers.ItemKey, k,
				new ModelResourceLocation("mazetowers:key_" + k));
			ModelLoader.setCustomModelResourceLocation(Item
				.getItemFromBlock(MazeTowers.BlockLock), k,
				new ModelResourceLocation("mazetowers:lock"));
		}

		MinecraftForge.EVENT_BUS
			.register(ModelBakeEventHandler.instance);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		BlockRenderRegister.registerBlockRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(
			TileEntityExplosiveCreeperSkull.class,
			new TileEntityExplosiveCreeperSkullRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(
			TileEntityMineralChest.class,
			new TileEntityMineralChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(
			TileEntityRedstoneClock.class,
			new TileEntityRedstoneClockRenderer());
		ModelBakery.addVariantName(
			MazeTowers.ItemExplosiveBow,
			"mazetowers:explosive_bow");
		ModelBakery.addVariantName(
			MazeTowers.ItemExplosiveBow,
			"mazetowers:explosive_bow_pulling_0");
		ModelBakery.addVariantName(
			MazeTowers.ItemExplosiveBow,
			"mazetowers:explosive_bow_pulling_1");
		ModelBakery.addVariantName(
			MazeTowers.ItemExplosiveBow,
			"mazetowers:explosive_bow_pulling_2");
		ModelBakery.registerItemVariants(
			Item.getItemFromBlock(MazeTowers.BlockLock),
			new ResourceLocation("mazetowers:lock"));
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}
}
