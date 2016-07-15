package com.samuel.mazetowers.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockItemScannerGold;

public class PacketActivateItemScanner implements IMessage {

	private String text;

	public PacketActivateItemScanner() {
	}

	public PacketActivateItemScanner(String text) {
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
		IMessageHandler<PacketActivateItemScanner, IMessage> {
		@Override
		public IMessage onMessage(
			final PacketActivateItemScanner message,
			final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx
				.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player = ctx
						.getServerHandler().playerEntity;
					World world = player.worldObj;
					BlockPos pos = BlockPos.fromLong(Long
						.parseLong(message.text));
					boolean isGold = (world.getBlockState(
						pos).getBlock() instanceof BlockItemScannerGold);
					(!isGold ? MazeTowers.BlockItemScanner
						: MazeTowers.BlockItemScannerGold)
						.setStateBasedOnMatchResult(world,
							pos, world.getBlockState(pos), true);
				}
			});
			return null;
		}
	}
}
