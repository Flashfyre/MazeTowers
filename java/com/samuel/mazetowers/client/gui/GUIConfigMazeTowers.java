package com.samuel.mazetowers.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

import com.samuel.mazetowers.MazeTowers;

public class GUIConfigMazeTowers extends GuiConfig {
	public GUIConfigMazeTowers(GuiScreen parent) {
		super(parent, new ConfigElement(MazeTowers.config
			.getCategory(Configuration.CATEGORY_GENERAL))
			.getChildElements(), MazeTowers.MODID, false,
			false, "Maze Towers Settings");
		// titleLine2 = MazeTowers.config.getConfigFile().getAbsolutePath();
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY,
		float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		MazeTowers.instance.saveConfig();
	}
}