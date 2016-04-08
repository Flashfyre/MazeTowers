package com.samuel.mazetowers.eventhandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.samuel.mazetowers.etc.IMazeTowerCapability;
import com.samuel.mazetowers.etc.MazeTowerGuiProvider;
import com.samuel.mazetowers.etc.PlayerMazeTower;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;

public class MazeTowersGuiEventHandler {

	private Minecraft mc;
	private String towerName = "", floorString = "",
		difficultyString = "", rarityString = "";
	private boolean isUnderground;
	private float partialTicksCache = 0;
	private int[] propOffsets = new int[] { 0, 0, 0 };
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onAttachEntityCapabilities(AttachCapabilitiesEvent.Entity e) {
		if (!e.getEntity().hasCapability(MazeTowerGuiProvider.gui, null) &&
			e.getEntity() instanceof EntityPlayer)
			e.addCapability(new ResourceLocation("MazeTowers:MazeTowerGui"),
				new MazeTowerGuiProvider(new PlayerMazeTower.DefaultImpl((EntityPlayer) e.getEntity(), 0, false)));
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEntityConstructing(
		EntityConstructing event) {
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onPlayerClone (
		PlayerEvent.Clone event) {
		IMazeTowerCapability mtc = event.getOriginal().getCapability(MazeTowerGuiProvider.gui, null);
		EntityPlayer entityPlayer = event.getEntityPlayer();
		if (entityPlayer.hasCapability(MazeTowerGuiProvider.gui, null) && event.isWasDeath()) {
			entityPlayer.getCapability(MazeTowerGuiProvider.gui, null).setEnabled(false);
			entityPlayer.getCapability(MazeTowerGuiProvider.gui, null).setFloor(mtc.getFloor());
			entityPlayer.getCapability(MazeTowerGuiProvider.gui, null).setIsUnderground(mtc.getIsUnderground());
			entityPlayer.getCapability(MazeTowerGuiProvider.gui, null).setTowerData(mtc.getTowerData());
			entityPlayer.getCapability(MazeTowerGuiProvider.gui, null).setTowerName(mtc.getTowerName());
			entityPlayer.getCapability(MazeTowerGuiProvider.gui, null).setSpawnPos(mtc.getSpawnPos());
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onRenderOverlay(RenderGameOverlayEvent e) {
		if (e.getType().name() == "TEXT") {
			if (mc == null)
				mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.thePlayer;
			IMazeTowerCapability props = player.getCapability(MazeTowerGuiProvider.gui, null);
			if (props != null && props.getEnabled()) {
				final int floor;
				if (e.getPartialTicks() != partialTicksCache
					&& (int) ((partialTicksCache = e.getPartialTicks()) * 5000) % 10 == 0) {
					final int[] towerData = props.getTowerData();
					final int posY;
					final boolean isTowerArea = (player.chunkCoordX == towerData[0]
						|| player.chunkCoordX == towerData[0] - 1 || player.chunkCoordX == towerData[0] + 1)
						&& (player.chunkCoordZ == towerData[2]
							|| player.chunkCoordZ == towerData[2] - 1 || player.chunkCoordZ == towerData[2] + 1), isTowerChunk = isTowerArea
						&& player.chunkCoordX == towerData[0]
						&& player.chunkCoordZ == towerData[2];
					if (isTowerChunk) {
						final String[] propNames = new String[] {
							I18n
								.translateToLocal("towerinfo.floor"),
							I18n
								.translateToLocal("towerinfo.difficulty"),
							I18n
								.translateToLocal("towerinfo.rarity") };
						propOffsets = new int[] {
							Integer
								.valueOf(I18n
									.translateToLocal("towerinfo.floor.offset")),
							Integer
								.valueOf(I18n
									.translateToLocal("towerinfo.difficulty.offset")),
							Integer
								.valueOf(I18n.translateToLocal("towerinfo.rarity.offset")) };
						final int minFloor = !isUnderground ? 1
							: -(towerData[3] - 1), maxFloor = !isUnderground ? towerData[3] + 1
							: 1;
						isUnderground = props
							.getIsUnderground();
						floor = Math.max(Math.min((int) Math
							.floor((((int) player.posY)
							- towerData[1] - (!isUnderground ? 0
							: 3)) / 6) + 1,
							maxFloor), minFloor);
						props.setFloor(floor);
						floorString = propNames[0]
							+ ": "
							+ (floor > 0 ? floor : "B"
								+ -(floor - 1)) + "F";
						difficultyString = propNames[1]
							+ ": "
							+ MazeTowerBase.EnumLevel
								.getStringFromLevel(props
									.getDifficulty(), false);
						rarityString = propNames[2]
							+ ": "
							+ MazeTowerBase.EnumLevel
								.getStringFromLevel(props
									.getRarity(), false);
						towerName = props.getTowerName();
					} else if (!isTowerArea) {
						props.setEnabled(false);
						return;
					} else
						floor = props.getFloor();
					// mc.thePlayer.addChatMessage(new
					// TextComponentString(String.valueOf(partialTicksCache)));
				} else
					floor = props.getFloor();

				mc.fontRendererObj.drawStringWithShadow(
					towerName, 4, 4, 0xffFFFFFF);
				mc.fontRendererObj.drawStringWithShadow(
					floorString, 4 + propOffsets[0], 19,
					0xffFFFFFF);
				mc.fontRendererObj.drawStringWithShadow(
					difficultyString, 4 + propOffsets[1],
					29, 0xffFFFFFF);
				mc.fontRendererObj.drawStringWithShadow(
					rarityString, 4 + propOffsets[2], 39,
					0xffFFFFFF);
			}
		}
	}
}
