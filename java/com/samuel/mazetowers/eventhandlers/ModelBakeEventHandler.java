package com.samuel.mazetowers.eventhandlers;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.samuel.mazetowers.etc.HiddenButtonModel;
import com.samuel.mazetowers.etc.HiddenPressurePlateWeightedModel;

/**
 * Created from Camouflage Block ModelBakeEventHandler created by TheGreyGhost
 * on 19/04/2015.
 */
public class ModelBakeEventHandler {
	public static final ModelBakeEventHandler instance = new ModelBakeEventHandler();

	private ModelBakeEventHandler() {
	};

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		Object plateObj = event.getModelRegistry()
			.getObject(HiddenPressurePlateWeightedModel.modelResourceLocationUp);
		Object buttonObj = event.getModelRegistry()
			.getObject(HiddenPressurePlateWeightedModel.modelResourceLocationUp);
		IBakedModel existingModel;
		if (plateObj instanceof IBakedModel) {
			IRetexturableModel modelPlateUp = null;
			IRetexturableModel modelPlateDown = null;
			existingModel = (IBakedModel) plateObj;
			try {
				modelPlateUp = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation("block/heavy_pressure_plate_up"));
				modelPlateDown = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation("block/heavy_pressure_plate_down"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			HiddenPressurePlateWeightedModel customModelPlate = new HiddenPressurePlateWeightedModel(
				modelPlateUp, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenPressurePlateWeightedModel.modelResourceLocationUp,
					customModelPlate);
			customModelPlate = new HiddenPressurePlateWeightedModel(
				modelPlateDown, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenPressurePlateWeightedModel.modelResourceLocationDown,
					customModelPlate);
		}
		if (buttonObj instanceof IBakedModel) {
			IRetexturableModel modelButtonU = null;
			IRetexturableModel modelButtonD = null;
			IRetexturableModel modelButtonE = null;
			IRetexturableModel modelButtonW = null;
			IRetexturableModel modelButtonS = null;
			IRetexturableModel modelButtonN = null;
			IRetexturableModel modelButtonUPressed = null;
			IRetexturableModel modelButtonDPressed = null;
			IRetexturableModel modelButtonEPressed = null;
			IRetexturableModel modelButtonWPressed = null;
			IRetexturableModel modelButtonSPressed = null;
			IRetexturableModel modelButtonNPressed = null;
			existingModel = (IBakedModel) buttonObj;
			try {
				modelButtonU = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_u"));
				modelButtonD = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_d"));
				modelButtonE = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_e"));
				modelButtonW = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_w"));
				modelButtonS = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_s"));
				modelButtonN = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_n"));
				modelButtonUPressed = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_u_pressed"));
				modelButtonDPressed = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_d_pressed"));
				modelButtonEPressed = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_e_pressed"));
				modelButtonWPressed = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_w_pressed"));
				modelButtonSPressed = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_s_pressed"));
				modelButtonNPressed = (IRetexturableModel) ModelLoaderRegistry
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_n_pressed"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			HiddenButtonModel customModelButton = new HiddenButtonModel(
				modelButtonU, existingModel);
			event.getModelRegistry().putObject(
				HiddenButtonModel.modelResourceLocationU,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonD, existingModel);
			event.getModelRegistry().putObject(
				HiddenButtonModel.modelResourceLocationD,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonW, existingModel);
			event.getModelRegistry().putObject(
				HiddenButtonModel.modelResourceLocationW,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonE, existingModel);
			event.getModelRegistry().putObject(
				HiddenButtonModel.modelResourceLocationE,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonS, existingModel);
			event.getModelRegistry().putObject(
				HiddenButtonModel.modelResourceLocationS,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonN, existingModel);
			event.getModelRegistry().putObject(
				HiddenButtonModel.modelResourceLocationN,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonUPressed, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenButtonModel.modelResourceLocationUPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonDPressed, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenButtonModel.modelResourceLocationDPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonEPressed, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenButtonModel.modelResourceLocationEPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonWPressed, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenButtonModel.modelResourceLocationWPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonSPressed, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenButtonModel.modelResourceLocationSPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonNPressed, existingModel);
			event.getModelRegistry()
				.putObject(
					HiddenButtonModel.modelResourceLocationNPressed,
					customModelButton);
		}
	}
}