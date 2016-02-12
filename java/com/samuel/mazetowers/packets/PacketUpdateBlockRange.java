package com.samuel.mazetowers.packets;

import java.lang.reflect.Proxy;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateBlockRange implements IMessage {
    
    private BlockPos pos1;
    private BlockPos pos2;

    public PacketUpdateBlockRange() { }

    public PacketUpdateBlockRange(BlockPos pos1, BlockPos pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	pos1 = BlockPos.fromLong(buf.readLong());
    	pos2 = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	 buf.writeLong(pos1.toLong());
    	 buf.writeLong(pos2.toLong());
    }

    public static class Handler implements IMessageHandler<PacketUpdateBlockRange, IMessage> {
        @Override
        public IMessage onMessage(final PacketUpdateBlockRange message, final MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
        			Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(
        				message.pos1, message.pos2);
                }
            });
            return null;
        }
    }
}