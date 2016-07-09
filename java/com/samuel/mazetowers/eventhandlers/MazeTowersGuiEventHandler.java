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

import com.samuel.mazetowers.MazeTowers;
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
	private int cachedMTIndex = 0;
	private int timeout = 0;
	
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onAttachEntityCapabilities(AttachCapabilitiesEvent.Entity e) {
		if (!e.getEntity().hasCapability(MazeTowerGuiProvider.gui, null) &&
			e.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntity();
			e.addCapability(new ResourceLocation("MazeTowers:MazeTowerGui"),
				new MazeTowerGuiProvider(new PlayerMazeTower.DefaultImpl(player, 0, false)));
			MazeTowerBase tower = MazeTowers.mazeTowers.getTowerBesideCoords(player.getEntityWorld(),
				player.chunkCoordX, player.chunkCoordZ);
			if (tower != null) {
				MazeTowers.mazeTowers.initGui(player, tower, null);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEntityConstructing(
		EntityConstructing e) {
		if (e.getEntity().hasCapability(MazeTowerGuiProvider.gui, null) &&
			e.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getEntity();
			MazeTowerBase tower = MazeTowers.mazeTowers.getTowerBesideCoords(player.getEntityWorld(),
				player.chunkCoordX, player.chunkCoordZ);
			if (tower != null) {
				MazeTowers.mazeTowers.initGui(player, tower, null);
			}
		}
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
			entityPlayer.getCapability(MazeTowerGuiProvider.gui, null).setMTBounds(mtc.getMTBounds());
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onRenderOverlay(RenderGameOverlayEvent e) {
		if (e.getType().name() == "TEXT") {
			if (mc == null)
				mc = Minecraft.getMinecraft();
			int[] textColour = new int[] { 0xffFFFFFF, 0xbbFFFFFF, 0x99FFFFFF, 0x66FFFFFF, 0x33FFFFFF };
			EntityPlayer player = mc.thePlayer;
			IMazeTowerCapability props = player.getCapability(MazeTowerGuiProvider.gui, null);
			if (props != null && props.getEnabled()) {
				final int floor;
				if (e.getPartialTicks() != partialTicksCache
					&& (int) ((partialTicksCache = e.getPartialTicks()) * 6400) % 8 == 0) {
					final int[] towerData = props.getTowerData();
					final int posY;
					final boolean isTowerArea = (player.chunkCoordX == towerData[0]
						|| player.chunkCoordX == towerData[0] - 1 || player.chunkCoordX == towerData[0] + 1)
						&& (player.chunkCoordZ == towerData[2]
							|| player.chunkCoordZ == towerData[2] - 1 || player.chunkCoordZ == towerData[2] + 1),
						isTowerChunk = isTowerArea
						&& player.chunkCoordX == towerData[0]
						&& player.chunkCoordZ == towerData[2];
					if (isTowerArea) {
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
						isUnderground = props
							.getIsUnderground();
						final int minFloor = !isUnderground ? 1
							: -(towerData[3] - 1), maxFloor = !isUnderground ? towerData[3] + 1
							: 1;
						if (isTowerChunk) {
							floor = Math.max(Math.min((int) Math
								.floor((((int) player.posY)
								- towerData[1] - (!isUnderground ? 0
								: 3)) / 6) + 1,
								maxFloor), minFloor);
							props.setFloor(floor);
						} else {
							final int[][] mtBounds = props.getMTBounds();
							final int iy = towerData[1];
							int mtIndex = -1;
							if (mtBounds.length != 0) {
								final int ix = towerData[0] << 4,
									iz = towerData[2] << 4;
								if (mtBounds.length > cachedMTIndex &&
									ix + mtBounds[cachedMTIndex][0] <= player.posX &&
									ix + mtBounds[cachedMTIndex][3] >= player.posX &&
									iz + mtBounds[cachedMTIndex][2] <= player.posZ &&
									iz + mtBounds[cachedMTIndex][5] >= player.posZ &&
									mtBounds[cachedMTIndex][1] <= player.posY &&
									mtBounds[cachedMTIndex][4] >= player.posY)
									mtIndex = cachedMTIndex;
								else {
									for (int m = 0; m < mtBounds.length; m++) {
										if (m != cachedMTIndex) {
											if (ix + mtBounds[m][0] <= player.posX &&
												ix + mtBounds[m][3] >= player.posX &&
												iz + mtBounds[m][2] <= player.posZ &&
												iz + mtBounds[m][5] >= player.posZ &&
												mtBounds[m][1] <= player.posY &&
												mtBounds[m][4] >= player.posY) {
												mtIndex = cachedMTIndex = m;
												break;
											}
										}
									}
								}
							}
							
							if (mtIndex != -1)
								floor = (int) Math.floor((mtBounds[mtIndex][1] - iy) / 6) + 1;
							else
								floor = props.getFloor();
						}
						
						if (timeout != 0) {
							timeout = 0;
						}
						
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
					} else {
						if (++timeout >= 5) {
							props.setEnabled(false);
							timeout = 0;
						}
						return;
					}
					// mc.thePlayer.addChatMessage(new
					// TextComponentString(String.valueOf(partialTicksCache)));
				} else
					floor = props.getFloor();

				mc.fontRendererObj.drawStringWithShadow(
					towerName, 4, 4, textColour[timeout]);
				mc.fontRendererObj.drawStringWithShadow(
					floorString, 4 + propOffsets[0], 19,
					textColour[timeout]);
				mc.fontRendererObj.drawStringWithShadow(
					difficultyString, 4 + propOffsets[1],
					29, textColour[timeout]);
				mc.fontRendererObj.drawStringWithShadow(
					rarityString, 4 + propOffsets[2], 39,
					textColour[timeout]);
			}
		}
	}
}
