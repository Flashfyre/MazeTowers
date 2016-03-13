package com.samuel.mazetowers.eventhandlers;

import java.lang.reflect.Field;
import java.util.Map;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.client.renderer.texture.TextureRedstoneClock;
import com.samuel.mazetowers.etc.MTUtils;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TextureStitchEventHandler {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
		try {
            Field f = MTUtils.findObfuscatedField(TextureMap.class, "mapRegisteredSprites", "field_110574_e");
            f.setAccessible(true);
            Map map = (Map) f.get(event.map);
            
            map.put("mazetowers:items/redstone_clock", MazeTowers.TextureRedstoneClock =
            	new TextureRedstoneClock("mazetowers:items/redstone_clock", false));
            map.put("mazetowers:items/redstone_clock_inverted", MazeTowers.TextureRedstoneClockInverted =
            	new TextureRedstoneClock("mazetowers:items/redstone_clock_inverted", true));
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}
