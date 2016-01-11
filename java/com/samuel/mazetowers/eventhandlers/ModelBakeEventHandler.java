package com.samuel.mazetowers.eventhandlers;

import java.io.IOException;

import com.samuel.mazetowers.etc.HiddenPressurePlateWeightedModel;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by TheGreyGhost on 19/04/2015.
 */
public class ModelBakeEventHandler {
  public static final ModelBakeEventHandler instance = new ModelBakeEventHandler();

  private ModelBakeEventHandler() {};

  // Called after all the other baked block models have been added to the modelRegistry
  // Allows us to manipulate the modelRegistry before BlockModelShapes caches them.
  @SubscribeEvent
  public void onModelBakeEvent(ModelBakeEvent event)
  {
    Object object = event.modelRegistry.getObject(HiddenPressurePlateWeightedModel.modelResourceLocationUp);
    if (object instanceof IBakedModel) {
      IBakedModel existingModel = (IBakedModel)object;
      IRetexturableModel modelUp = null;
      IRetexturableModel modelDown = null;
      try {
    	  modelUp = (IRetexturableModel) event.modelLoader.getModel(new ModelResourceLocation("block/heavy_pressure_plate_up"));
    	  modelDown = (IRetexturableModel) event.modelLoader.getModel(new ModelResourceLocation("block/heavy_pressure_plate_down"));
	} catch (IOException e) {
		e.printStackTrace();
	}
      HiddenPressurePlateWeightedModel customModel = new HiddenPressurePlateWeightedModel(modelUp, existingModel);
      event.modelRegistry.putObject(HiddenPressurePlateWeightedModel.modelResourceLocationUp, customModel);
      customModel = new HiddenPressurePlateWeightedModel(modelDown, existingModel);
      event.modelRegistry.putObject(HiddenPressurePlateWeightedModel.modelResourceLocationDown, customModel);
    }
  }
}