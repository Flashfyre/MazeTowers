package com.samuel.mazetowers.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModBlocks;

public class BlockRenderRegister {
	public static String modid = MazeTowers.MODID;

	public static void registerBlockRenderer() {
		reg(ModBlocks.hiddenButton, 0);
		reg(ModBlocks.hiddenPressurePlateWeighted, 0);
		reg(ModBlocks.itemScanner, 0);
		reg(ModBlocks.itemScannerGold, 0);
		reg(ModBlocks.memoryPiston, 0);
		reg(ModBlocks.memoryPistonOff, 0);
		reg(ModBlocks.memoryPistonHead, 0);
		reg(ModBlocks.memoryPistonHeadOff, 0);
		reg(ModBlocks.memoryPistonExtension, 0);
		reg(ModBlocks.memoryPistonExtensionOff, 0);
		reg(ModBlocks.mineralChestIron, 0);
		reg(ModBlocks.mineralChestGold, 0);
		reg(ModBlocks.mineralChestDiamond, 0);
		reg(ModBlocks.mineralChestIronTrapped, 0);
		reg(ModBlocks.mineralChestGoldTrapped, 0);
		reg(ModBlocks.mineralChestDiamondTrapped, 0);
		reg(ModBlocks.packedIceStairs, 0);
		reg(ModBlocks.prismarineBrickStairs, 0);
		reg(ModBlocks.endStoneStairs, 0);
		reg(ModBlocks.obsidianStairs, 0);
		reg(ModBlocks.bedrockStairs, 0);
		reg(ModBlocks.sandstoneWall, 0);
		reg(ModBlocks.redSandstoneWall, 0);
		reg(ModBlocks.mossyStoneBrickWall, 0);
		reg(ModBlocks.stoneBrickWall, 0);
		reg(ModBlocks.packedIceWall, 0);
		reg(ModBlocks.prismarineBrickWall, 0);
		reg(ModBlocks.quartzWall, 0);
		reg(ModBlocks.endStoneWall, 0);
		reg(ModBlocks.purpurWall, 0);
		reg(ModBlocks.obsidianWall, 0);
		reg(ModBlocks.bedrockWall, 0);
		reg(ModBlocks.resistantDoorPrismarine, 0);
		reg(ModBlocks.resistantDoorQuartz, 0);
		reg(ModBlocks.resistantDoorEndStone, 0);
		reg(ModBlocks.resistantDoorObsidian, 0);
		reg(ModBlocks.resistantDoorBedrock, 0);
		for (int c = 0; c < 20; c++)
			reg(ModBlocks.lock, c );
		reg(ModBlocks.redstone_clock, 0);
		reg(ModBlocks.redstone_clock_inverted, 0);
		reg(ModBlocks.explosiveCreeperSkull, 0);
		reg(ModBlocks.specialMobSpawner, 0);
		reg(ModBlocks.vendorSpawner, 0);
	}

	public static void reg(Block block, int meta) {
		Minecraft.getMinecraft().getRenderItem()
			.getItemModelMesher().register(
				Item.getItemFromBlock(block),
				meta,
				new ModelResourceLocation(modid
					+ ":"
					+ block.getUnlocalizedName().substring(
						5), "inventory"));
	}

	public static void reg(Block block, int meta,
		String name) {
		Minecraft.getMinecraft().getRenderItem()
			.getItemModelMesher().register(
				Item.getItemFromBlock(block),
				meta,
				new ModelResourceLocation(modid + ":"
					+ name, "inventory"));
	}
}
