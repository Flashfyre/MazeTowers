package com.samuel.mazetowers.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.samuel.mazetowers.etc.IMazeTowerCapability;
import com.samuel.mazetowers.etc.MTHelper;
import com.samuel.mazetowers.etc.MazeTowerGuiProvider;

public class PacketMazeTowersGui implements IMessage {

	public boolean nChunkX;
	public boolean nChunkZ;
	public boolean isUnderground;
	public int chunkX;
	public int baseY;
	public int chunkZ;
	public int floors;
	public int difficulty;
	public int rarity;
	public String towerName;
	public int[][] mtBounds;

	public PacketMazeTowersGui() {
	}

	public PacketMazeTowersGui(int chunkX, int baseY,
		int chunkZ, int floors, int difficulty, int rarity,
		boolean isUnderground, String towerName, int[][] mtBounds) {
		this.chunkX = chunkX;
		this.baseY = baseY;
		this.chunkZ = chunkZ;
		nChunkX = chunkX < 0;
		nChunkZ = chunkZ < 0;
		this.isUnderground = isUnderground;
		this.floors = floors;
		this.difficulty = difficulty;
		this.rarity = rarity;
		this.towerName = towerName;
		this.mtBounds = mtBounds;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nChunkX = buf.readBoolean();
		nChunkZ = buf.readBoolean();
		chunkX = ByteBufUtils.readVarShort(buf);
		if (nChunkX)
			chunkX = ((~chunkX & 0xff) * -1) - 1;
		baseY = ByteBufUtils.readVarInt(buf, 2);
		chunkZ = ByteBufUtils.readVarShort(buf);
		if (nChunkZ)
			chunkZ = ((~chunkZ & 0xff) * -1) - 1;
		isUnderground = buf.readBoolean();
		floors = ByteBufUtils.readVarInt(buf, 1);
		difficulty = ByteBufUtils.readVarInt(buf, 1);
		rarity = ByteBufUtils.readVarInt(buf, 1);
		towerName = ByteBufUtils.readUTF8String(buf);
		
		List<int[]> mtbList = new ArrayList<int[]>();
		int coord;
		
		while (buf.isReadable(6) && (coord = ByteBufUtils.readVarShort(buf)) != 0) {
			int[] mtb = new int[] { coord - 128, ByteBufUtils.readVarShort(buf),
				ByteBufUtils.readVarShort(buf) - 128, ByteBufUtils.readVarShort(buf) - 128,
				ByteBufUtils.readVarShort(buf), ByteBufUtils.readVarShort(buf) - 128 };
			mtbList.add(mtb);
		}
		
		this.mtBounds = mtbList.toArray(new int[mtbList.size()][6]);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(nChunkX);
		buf.writeBoolean(nChunkZ);
		ByteBufUtils.writeVarShort(buf, chunkX);
		ByteBufUtils.writeVarInt(buf, baseY, 2);
		ByteBufUtils.writeVarShort(buf, chunkZ);
		buf.writeBoolean(isUnderground);
		ByteBufUtils.writeVarInt(buf, floors, 1);
		ByteBufUtils.writeVarInt(buf, difficulty, 1);
		ByteBufUtils.writeVarInt(buf, rarity, 1);
		ByteBufUtils.writeUTF8String(buf, towerName);
		
		for (int[] mtb : mtBounds) {
			for (int i = 0; i < 6; i++) {
				ByteBufUtils.writeVarShort(buf, mtb[i] + (i % 3 == 1 ? 0 : 128));
			}
		}
	}

	public static class Handler implements
		IMessageHandler<PacketMazeTowersGui, IMessage> {
		@Override
		public IMessage onMessage(
			final PacketMazeTowersGui message,
			final MessageContext ctx) {
			IThreadListener mainThread = Minecraft
				.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
					IMazeTowerCapability props =
						player.getCapability(MazeTowerGuiProvider.gui, null);
					if (props != null) {
						boolean enabled = props
							.getEnabled();
						if (message.floors != 0) {
							if (!enabled) {
								props.setEnabled(true);
								props
									.setIsUnderground(message.isUnderground);
								props.setTowerData(
									message.chunkX,
									message.baseY,
									message.chunkZ,
									message.floors,
									message.difficulty,
									message.rarity);
								props
									.setTowerName(message.towerName);
								props.setFloor(1);
								props.setMTBounds(message.mtBounds);
							}
						} else if (enabled) {
							props.setEnabled(false);
						}
					} else {
						props = null;
					}
				}
			});
			return null;
		}
	}
}