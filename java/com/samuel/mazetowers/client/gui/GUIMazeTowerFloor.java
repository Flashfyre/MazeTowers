package com.samuel.mazetowers.client.gui;

import com.samuel.mazetowers.MazeTowers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GUIMazeTowerFloor extends Gui {
	
	public GUIMazeTowerFloor(Minecraft mc) {
		super();
	}
	 
	public GUIMazeTowerFloor(Minecraft mc, int floor)
	{
		super();
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
	
		this.drawString(mc.fontRendererObj, floor + "F", 4, 4, 0xFFFFFF);
		//mc.fontRendererObj.drawStringWithShadow("Hello World", 4, 4, 0xffFFFFFF);
	}
}
