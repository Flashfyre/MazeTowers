package com.samuel.mazetowers.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDebugMessage implements IMessage {

	private String text;

	public PacketDebugMessage() {
	}

	public PacketDebugMessage(String text) {
		this.text = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		text = ByteBufUtils.readUTF8String(buf); // this class is very useful in
												 // general for writing more
												 // complex objects
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, text);
	}

	public static class Handler implements
		IMessageHandler<PacketDebugMessage, IMessage> {
		@Override
		public IMessage onMessage(
			final PacketDebugMessage message,
			final MessageContext ctx) {
			IThreadListener mainThread = Minecraft
				.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Minecraft.getMinecraft().thePlayer
						.addChatMessage(new TextComponentString(
							message.text));
				}
			});
			return null;
		}
	}
}