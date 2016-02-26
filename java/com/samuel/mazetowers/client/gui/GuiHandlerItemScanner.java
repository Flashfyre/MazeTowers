package com.samuel.mazetowers.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.samuel.mazetowers.etc.ContainerItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;

public class GuiHandlerItemScanner implements IGuiHandler {

	public static final int MOD_TILE_ENTITY_GUI = 0;

	@Override
	public Object getServerGuiElement(int ID,
		EntityPlayer player, World world, int x, int y,
		int z) {
		if (ID == MOD_TILE_ENTITY_GUI)
			return new ContainerItemScanner(
				player.inventory,
				(TileEntityItemScanner) world
					.getTileEntity(new BlockPos(x, y, z)),
				player);

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID,
		EntityPlayer player, World world, int x, int y,
		int z) {
		if (ID == MOD_TILE_ENTITY_GUI)
			return new GuiItemScanner(player.inventory,
				((TileEntityItemScanner) world
					.getTileEntity(new BlockPos(x, y, z))));

		return null;
	}
}
