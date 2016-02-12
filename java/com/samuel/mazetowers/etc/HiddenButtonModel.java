package com.samuel.mazetowers.etc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector4f;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockHiddenPressurePlateWeighted;
import com.samuel.mazetowers.blocks.BlockHiddenButton;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ModelLoader.UVLock;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.property.IExtendedBlockState;

/**
 * Created from CamouflageISmartBlockModelFactory by TheGreyGhost on 19/04/2015.
 */
public class HiddenButtonModel implements ISmartBlockModel {

  @SuppressWarnings("deprecation")
  
  private IRetexturableModel baseModel;
  private IFlexibleBakedModel baseBakedModel = null;
  private IFlexibleBakedModel copyBakedModel = null;
  private Map<IBlockState, IFlexibleBakedModel> modelCache; 
  
  public HiddenButtonModel(IRetexturableModel baseModelIn, IBakedModel baseBakedModelIn)
  {
	  baseModel = baseModelIn;
	  baseBakedModel = (IFlexibleBakedModel) baseBakedModelIn;
	  modelCache = new HashMap<IBlockState, IFlexibleBakedModel>();
  }

  public static final ModelResourceLocation modelResourceLocationU
	= new ModelResourceLocation("mazetowers:quartz_button_u");
  public static final ModelResourceLocation modelResourceLocationD
	= new ModelResourceLocation("mazetowers:quartz_button_d");
  public static final ModelResourceLocation modelResourceLocationE
	= new ModelResourceLocation("mazetowers:quartz_button_e");
  public static final ModelResourceLocation modelResourceLocationW
	= new ModelResourceLocation("mazetowers:quartz_button_w");
  public static final ModelResourceLocation modelResourceLocationS
	= new ModelResourceLocation("mazetowers:quartz_button_s");
  public static final ModelResourceLocation modelResourceLocationN
	= new ModelResourceLocation("mazetowers:quartz_button_n");
  public static final ModelResourceLocation modelResourceLocationUPressed
	= new ModelResourceLocation("mazetowers:quartz_button_u_pressed");
  public static final ModelResourceLocation modelResourceLocationDPressed
	= new ModelResourceLocation("mazetowers:quartz_button_d_pressed");
	public static final ModelResourceLocation modelResourceLocationEPressed
		= new ModelResourceLocation("mazetowers:quartz_button_e_pressed");
	public static final ModelResourceLocation modelResourceLocationWPressed
		= new ModelResourceLocation("mazetowers:quartz_button_w_pressed");
	public static final ModelResourceLocation modelResourceLocationSPressed
		= new ModelResourceLocation("mazetowers:quartz_button_s_pressed");
	public static final ModelResourceLocation modelResourceLocationNPressed
		= new ModelResourceLocation("mazetowers:quartz_button_n_pressed");

  @Override
  public IBakedModel handleBlockState(IBlockState iBlockState)
  {
    IFlexibleBakedModel retval = (IFlexibleBakedModel) baseBakedModel;
    IBlockState baseState = Blocks.stone_button.getStateFromMeta(
    	MazeTowers.BlockHiddenButton.getMetaFromState(iBlockState));

    if (iBlockState instanceof IExtendedBlockState) {
      IExtendedBlockState iExtendedBlockState = (IExtendedBlockState) iBlockState;
      IBlockState copiedBlockIBlockState =
    	iExtendedBlockState.getValue(BlockHiddenButton.COPIEDBLOCK);
      if (copiedBlockIBlockState != baseState) {
    	  /*if (modelCache.containsKey(copiedBlockIBlockState))
    		  retval = modelCache.get(copiedBlockIBlockState);
    	  else {*/
    	  if (true) {
	        Minecraft mc = Minecraft.getMinecraft();
	        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
	        BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
	        IBakedModel copiedBlockModel = blockModelShapes.getModelForState(copiedBlockIBlockState);
	        IFlexibleBakedModel blockModel = (IFlexibleBakedModel) blockModelShapes.getModelForState(baseState);
	        IResourceManager resourceManager = mc.getResourceManager();
	        if (blockModel instanceof ISmartBlockModel) {
	        	TextureAtlasSprite texture = copiedBlockModel.getTexture();
	        	String path = texture.getIconName();
	        	String newPath;
	        	//IPerspectiveAwareModel.MapWrapper.handlePerspective(blockModel, baseModel.getDefaultState(), TransformType.FIRST_PERSON);
	        	//copyBakedModel.handlePerspective(TransformType.THIRD_PERSON).setValue(ForgeHooksClient.getMatrix(ModelRotation.X180_Y90));
	        	if (path != "missingno") {
	
		        	newPath = path.substring(0, path.indexOf(":") + 1) + "textures/" + path.substring(path.indexOf(":") + 1);
					try {
						if (resourceManager.getResource(new ResourceLocation(newPath + "_top.png")) != null)
							path += "_top";
					} catch (IOException e) {
						if (path.indexOf("_") > -1) {
							try {
								if (resourceManager.getResource(new ResourceLocation(newPath.substring(0,
									newPath.lastIndexOf("_") + 1) + "top.png")) != null) {
									path = path.substring(0, path.lastIndexOf("_") + 1) + "top";
								}
							} catch (IOException e_) {
							}	
						}
					}
		        	Map<String, String> texturesMap = new HashMap<String, String>();
		        	texturesMap.put("texture", path);
		        	ImmutableMap<String, String> textures = ImmutableMap.copyOf(texturesMap);
		        	IModel texturedModel = baseModel.retexture(textures);
		        	IModelState modelState = new UVLock(texturedModel.getDefaultState());
		        	final TextureMap textureMap = mc.getTextureMapBlocks();
					copyBakedModel = blockModel = texturedModel.bake(texturedModel.getDefaultState(),
						Attributes.DEFAULT_BAKED_FORMAT,
						new Function<ResourceLocation, TextureAtlasSprite>()
						{
						    @Override
						    public TextureAtlasSprite apply(ResourceLocation location)
						    {
						        if (location == null)
						            return null;
						        return textureMap.getAtlasSprite(location.toString());
						    }
						});
		          }
	        }
	        	
	        retval = blockModel;
	        modelCache.put(copiedBlockIBlockState, retval);
    	  }
      }
    }
    
    return retval;
  }

  @Override
  public TextureAtlasSprite getTexture() {
    return (copyBakedModel == null || copyBakedModel.getTexture().getIconName().equals("missingno")) ?
    	baseBakedModel.getTexture() : copyBakedModel.getTexture();
  }

  @Override
  public List getFaceQuads(EnumFacing p_177551_1_) {
	  throw new UnsupportedOperationException();
  }

  @Override
  public List getGeneralQuads() {
	  throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAmbientOcclusion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isGui3d() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isBuiltInRenderer() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    throw new UnsupportedOperationException();
  }
}