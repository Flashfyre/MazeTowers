package com.samuel.mazetowers.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class GUIMazeTowerFloor extends Gui {
	 
	public GUIMazeTowerFloor(Minecraft mc)
	{
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
	
		mc.fontRendererObj.drawStringWithShadow("Hello World", 4, 4, 0xffFFFFFF);
	}
}
