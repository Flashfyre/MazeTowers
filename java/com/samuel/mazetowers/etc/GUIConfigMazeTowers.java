package com.samuel.mazetowers.etc;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GUIConfigMazeTowers extends GuiConfig 
{
    public GUIConfigMazeTowers(GuiScreen parent) 
    {
        super(parent,
	        new ConfigElement(MazeTowers.config.getCategory(
	        	Configuration.CATEGORY_GENERAL)).getChildElements(), MazeTowers.MODID,
	        false, false, "Maze Towers Settings");
        //titleLine2 = MazeTowers.config.getConfigFile().getAbsolutePath();
    }
    
    @Override
    public void initGui()
    {
        // You can add buttons and initialize fields here
        super.initGui();
    }

    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // You can process any additional buttons you may have added here
        super.actionPerformed(button);
        MazeTowers.instance.saveConfig();
    }
}