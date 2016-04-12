package com.samuel.mazetowers.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockExtraSlab;
import com.samuel.mazetowers.blocks.BlockHiddenPressurePlateWeighted;
import com.samuel.mazetowers.client.renderer.BlockRenderRegister;
import com.samuel.mazetowers.client.renderer.EntityRenderRegister;
import com.samuel.mazetowers.client.renderer.ItemRenderRegister;
import com.samuel.mazetowers.client.renderer.tileentity.TileEntityExplosiveCreeperSkullRenderer;
import com.samuel.mazetowers.client.renderer.tileentity.TileEntityMineralChestRenderer;
import com.samuel.mazetowers.client.renderer.tileentity.TileEntityRedstoneClockRenderer;
import com.samuel.mazetowers.etc.HiddenButtonModel;
import com.samuel.mazetowers.etc.HiddenPressurePlateWeightedModel;
import com.samuel.mazetowers.eventhandlers.MazeTowersRenderEventHandler;
import com.samuel.mazetowers.eventhandlers.ModelBakeEventHandler;
import com.samuel.mazetowers.init.ModBlocks;
import com.samuel.mazetowers.tileentity.TileEntityExplosiveCreeperSkull;
import com.samuel.mazetowers.tileentity.TileEntityLock;
import com.samuel.mazetowers.tileentity.TileEntityMineralChest;
import com.samuel.mazetowers.tileentity.TileEntityRedstoneClock;

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
						.getValue(BlockPressurePlateWeighted.POWER) == 0 ?
						HiddenPressurePlateWeightedModel.modelResourceLocationUp
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
			ModelLoader.setCustomModelResourceLocation(MazeTowers.ItemColoredKey, k,
				new ModelResourceLocation("mazetowers:key_" + k));
			ModelLoader.setCustomModelResourceLocation(Item
				.getItemFromBlock(MazeTowers.BlockLock), k,
				new ModelResourceLocation("mazetowers:lock"));
		}
		for (int v = 0; v < BlockExtraSlab.EnumType.values().length; v++) {
			final ModelResourceLocation mrl = new ModelResourceLocation("mazetowers:" +
				MazeTowers.BlockExtraHalfSlab.getUnlocalizedName(v).substring(5));
			ModelLoader.setCustomModelResourceLocation(Item
				.getItemFromBlock(MazeTowers.BlockExtraHalfSlab), v,
				mrl);
		}
		ModelLoader.setCustomModelResourceLocation(Item
			.getItemFromBlock(MazeTowers.BlockSpectriteOre), 0,
			new ModelResourceLocation("mazetowers:spectrite_ore_surface"));
		ModelLoader.setCustomModelResourceLocation(Item
			.getItemFromBlock(MazeTowers.BlockSpectriteOre), 1,
			new ModelResourceLocation("mazetowers:spectrite_ore_nether"));
		ModelLoader.setCustomModelResourceLocation(Item
			.getItemFromBlock(MazeTowers.BlockSpectriteOre), 2,
			new ModelResourceLocation("mazetowers:spectrite_ore_end"));
		ModelLoader.setCustomModelResourceLocation(Item
			.getItemFromBlock(MazeTowers.BlockSpectriteOre), 0,
			new ModelResourceLocation("mazetowers:spectrite_chest"));
		ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.spectriteOre),
			new ResourceLocation("mazetowers:spectrite_ore_surface"),
			new ResourceLocation("mazetowers:spectrite_ore_nether"),
			new ResourceLocation("mazetowers:spectrite_ore_end"));
		MinecraftForge.EVENT_BUS
			.register(new MazeTowersRenderEventHandler());
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
		BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
		blockColors.registerBlockColorHandler(new IBlockColor()
        {
			@Override
			@SideOnly(Side.CLIENT)
		    public int colorMultiplier(IBlockState state, IBlockAccess worldIn,
		    	BlockPos pos, int tintIndex) {
				int typeIndex = 14;
				if (worldIn.getTileEntity(pos) != null) {
		    		TileEntityLock te = (TileEntityLock) worldIn.getTileEntity(pos);
		    		typeIndex = te.getTypeIndex();
				}
		        return MazeTowers.BlockLock.getColors()[typeIndex];
		    }
        }, new Block[] { MazeTowers.BlockLock } );
		itemColors.registerItemColorHandler(new IItemColor()
        {
        	@Override
            @SideOnly(Side.CLIENT)
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            	return MazeTowers.ItemColoredKey.getColors()[stack.getItemDamage()];
            }
        }, MazeTowers.ItemColoredKey, Item.getItemFromBlock(MazeTowers.BlockLock));
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}
}
