package com.samuel.mazetowers.eventhandlers;

import java.io.IOException;

import com.samuel.mazetowers.etc.HiddenPressurePlateWeightedISmartBlockModelFactory;

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
    // Find the existing mapping for CamouflageISmartBlockModelFactory - it will have been added automatically because
    //  we registered a custom BlockStateMapper for it (using ModelLoader.setCustomStateMapper)
    // Replace the mapping with our ISmartBlockModel.
    Object object = event.modelRegistry.getObject(HiddenPressurePlateWeightedISmartBlockModelFactory.modelResourceLocationUp);
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
      HiddenPressurePlateWeightedISmartBlockModelFactory customModel = new HiddenPressurePlateWeightedISmartBlockModelFactory(modelUp, existingModel);
      event.modelRegistry.putObject(HiddenPressurePlateWeightedISmartBlockModelFactory.modelResourceLocationUp, customModel);
      customModel = new HiddenPressurePlateWeightedISmartBlockModelFactory(modelDown, existingModel);
      event.modelRegistry.putObject(HiddenPressurePlateWeightedISmartBlockModelFactory.modelResourceLocationDown, customModel);
    }
  }
}