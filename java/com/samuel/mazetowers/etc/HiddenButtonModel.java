package com.samuel.mazetowers.etc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockHiddenButton;

/**
 * Created from CamouflageISmartBlockModelFactory by TheGreyGhost on 19/04/2015.
 */
public class HiddenButtonModel implements IBakedModel {

	@SuppressWarnings("deprecation")
	private IRetexturableModel baseModel;
	private IBakedModel baseBakedModel = null;
	private IBakedModel copyBakedModel = null;
	private Map<IBlockState, IBakedModel> modelCache;

	public HiddenButtonModel(
		IRetexturableModel baseModelIn,
		IBakedModel baseBakedModelIn) {
		baseModel = baseModelIn;
		baseBakedModel = baseBakedModelIn;
		modelCache = new HashMap<IBlockState, IBakedModel>();
	}

	public static final ModelResourceLocation modelResourceLocationU = new ModelResourceLocation(
		"mazetowers:quartz_button_u");
	public static final ModelResourceLocation modelResourceLocationD = new ModelResourceLocation(
		"mazetowers:quartz_button_d");
	public static final ModelResourceLocation modelResourceLocationE = new ModelResourceLocation(
		"mazetowers:quartz_button_e");
	public static final ModelResourceLocation modelResourceLocationW = new ModelResourceLocation(
		"mazetowers:quartz_button_w");
	public static final ModelResourceLocation modelResourceLocationS = new ModelResourceLocation(
		"mazetowers:quartz_button_s");
	public static final ModelResourceLocation modelResourceLocationN = new ModelResourceLocation(
		"mazetowers:quartz_button_n");
	public static final ModelResourceLocation modelResourceLocationUPressed = new ModelResourceLocation(
		"mazetowers:quartz_button_u_pressed");
	public static final ModelResourceLocation modelResourceLocationDPressed = new ModelResourceLocation(
		"mazetowers:quartz_button_d_pressed");
	public static final ModelResourceLocation modelResourceLocationEPressed = new ModelResourceLocation(
		"mazetowers:quartz_button_e_pressed");
	public static final ModelResourceLocation modelResourceLocationWPressed = new ModelResourceLocation(
		"mazetowers:quartz_button_w_pressed");
	public static final ModelResourceLocation modelResourceLocationSPressed = new ModelResourceLocation(
		"mazetowers:quartz_button_s_pressed");
	public static final ModelResourceLocation modelResourceLocationNPressed = new ModelResourceLocation(
		"mazetowers:quartz_button_n_pressed");

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return (copyBakedModel == null || copyBakedModel
			.getParticleTexture().getIconName().equals("missingno")) ? baseBakedModel
			.getParticleTexture()
			: copyBakedModel.getParticleTexture();
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState iBlockState, EnumFacing side, long rand) {
		IBakedModel retval = baseBakedModel;
		IBlockState baseState = Blocks.stone_button
			.getStateFromMeta(MazeTowers.BlockHiddenButton
				.getMetaFromState(iBlockState));

		if (iBlockState instanceof IExtendedBlockState) {
			IExtendedBlockState iExtendedBlockState = (IExtendedBlockState) iBlockState;
			IBlockState copiedBlockIBlockState = iExtendedBlockState
				.getValue(BlockHiddenButton.COPIEDBLOCK);
			if (copiedBlockIBlockState != baseState) {
				/*
				 * if (modelCache.containsKey(copiedBlockIBlockState)) retval =
				 * modelCache.get(copiedBlockIBlockState); else {
				 */
				if (true) {
					Minecraft mc = Minecraft.getMinecraft();
					BlockRendererDispatcher blockRendererDispatcher = mc
						.getBlockRendererDispatcher();
					BlockModelShapes blockModelShapes = blockRendererDispatcher
						.getBlockModelShapes();
					IBakedModel copiedBlockModel = blockModelShapes
						.getModelForState(copiedBlockIBlockState);
					IBakedModel blockModel = blockModelShapes
						.getModelForState(baseState);
					IResourceManager resourceManager = mc
						.getResourceManager();
					TextureAtlasSprite texture = copiedBlockModel
						.getParticleTexture();
					String path = texture.getIconName();
					String newPath;
					// IPerspectiveAwareModel.MapWrapper.handlePerspective(blockModel,
					// baseModel.getDefaultState(),
					// TransformType.FIRST_PERSON);
					// copyBakedModel.handlePerspective(TransformType.THIRD_PERSON).setValue(ForgeHooksClient.getMatrix(ModelRotation.X180_Y90));
					if (path != "missingno") {

						newPath = path.substring(0,
							path.indexOf(":") + 1)
							+ "textures/"
							+ path.substring(path
								.indexOf(":") + 1);
						try {
							if (resourceManager
								.getResource(new ResourceLocation(
									newPath
										+ "_top.png")) != null)
								path += "_top";
						} catch (IOException e) {
							if (path.indexOf("_") > -1) {
								try {
									if (resourceManager
										.getResource(new ResourceLocation(
											newPath
												.substring(
													0,
													newPath
														.lastIndexOf("_") + 1)
												+ "top.png")) != null) {
										path = path
											.substring(
												0,
												path.lastIndexOf("_") + 1)
											+ "top";
									}
								} catch (IOException e_) {
								}
							}
						}
						Map<String, String> texturesMap = new HashMap<String, String>();
						texturesMap
							.put("texture", path);
						ImmutableMap<String, String> textures = ImmutableMap
							.copyOf(texturesMap);
						IModel texturedModel = baseModel
							.retexture(textures);
						final TextureMap textureMap = mc
							.getTextureMapBlocks();
						copyBakedModel = blockModel = texturedModel.bake(
							texturedModel
								.getDefaultState(),
							Attributes.DEFAULT_BAKED_FORMAT,
							new Function<ResourceLocation, TextureAtlasSprite>() {
								@Override
								public TextureAtlasSprite apply(
									ResourceLocation location) {
									if (location == null)
										return null;
									return textureMap
										.getAtlasSprite(location
											.toString());
								}
							});
					}

					retval = blockModel;
					modelCache.put(copiedBlockIBlockState,
						retval);
				}
			}
		}

		return retval.getQuads(iBlockState, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return null;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return null;
	}
}