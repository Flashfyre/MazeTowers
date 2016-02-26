package com.samuel.mazetowers.eventhandlers;

import java.io.IOException;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IRetexturableModel;
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
		Object plateObj = event.modelRegistry
			.getObject(HiddenPressurePlateWeightedModel.modelResourceLocationUp);
		Object buttonObj = event.modelRegistry
			.getObject(HiddenPressurePlateWeightedModel.modelResourceLocationUp);
		IBakedModel existingModel;
		if (plateObj instanceof IBakedModel) {
			IRetexturableModel modelPlateUp = null;
			IRetexturableModel modelPlateDown = null;
			existingModel = (IBakedModel) plateObj;
			try {
				modelPlateUp = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"block/heavy_pressure_plate_up"));
				modelPlateDown = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"block/heavy_pressure_plate_down"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			HiddenPressurePlateWeightedModel customModelPlate = new HiddenPressurePlateWeightedModel(
				modelPlateUp, existingModel);
			event.modelRegistry
				.putObject(
					HiddenPressurePlateWeightedModel.modelResourceLocationUp,
					customModelPlate);
			customModelPlate = new HiddenPressurePlateWeightedModel(
				modelPlateDown, existingModel);
			event.modelRegistry
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
				modelButtonU = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_u"));
				modelButtonD = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_d"));
				modelButtonE = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_e"));
				modelButtonW = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_w"));
				modelButtonS = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_s"));
				modelButtonN = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_n"));
				modelButtonUPressed = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_u_pressed"));
				modelButtonDPressed = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_d_pressed"));
				modelButtonEPressed = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_e_pressed"));
				modelButtonWPressed = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_w_pressed"));
				modelButtonSPressed = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_s_pressed"));
				modelButtonNPressed = (IRetexturableModel) event.modelLoader
					.getModel(new ResourceLocation(
						"mazetowers:block/quartz_button_n_pressed"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			HiddenButtonModel customModelButton = new HiddenButtonModel(
				modelButtonU, existingModel);
			event.modelRegistry.putObject(
				HiddenButtonModel.modelResourceLocationU,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonD, existingModel);
			event.modelRegistry.putObject(
				HiddenButtonModel.modelResourceLocationD,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonW, existingModel);
			event.modelRegistry.putObject(
				HiddenButtonModel.modelResourceLocationW,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonE, existingModel);
			event.modelRegistry.putObject(
				HiddenButtonModel.modelResourceLocationE,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonS, existingModel);
			event.modelRegistry.putObject(
				HiddenButtonModel.modelResourceLocationS,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonN, existingModel);
			event.modelRegistry.putObject(
				HiddenButtonModel.modelResourceLocationN,
				customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonUPressed, existingModel);
			event.modelRegistry
				.putObject(
					HiddenButtonModel.modelResourceLocationUPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonDPressed, existingModel);
			event.modelRegistry
				.putObject(
					HiddenButtonModel.modelResourceLocationDPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonEPressed, existingModel);
			event.modelRegistry
				.putObject(
					HiddenButtonModel.modelResourceLocationEPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonWPressed, existingModel);
			event.modelRegistry
				.putObject(
					HiddenButtonModel.modelResourceLocationWPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonSPressed, existingModel);
			event.modelRegistry
				.putObject(
					HiddenButtonModel.modelResourceLocationSPressed,
					customModelButton);
			customModelButton = new HiddenButtonModel(
				modelButtonNPressed, existingModel);
			event.modelRegistry
				.putObject(
					HiddenButtonModel.modelResourceLocationNPressed,
					customModelButton);
		}
	}
}