package com.samuel.mazetowers.packets;

import com.samuel.mazetowers.client.gui.GUIMazeTowerFloor;
import com.samuel.mazetowers.etc.PlayerMazeTower;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMazeTowersGui implements IMessage {
    
	public boolean nChunkX;
	public boolean nChunkZ;
	public boolean isUnderground;
	public int chunkX;
	public int baseY;
	public int chunkZ;
	public int floors;
	public int difficulty;
	public String towerName;

    public PacketMazeTowersGui() { }

    public PacketMazeTowersGui(int chunkX, int baseY, int chunkZ, int floors, int difficulty,
    	boolean isUnderground, String towerName) {
        this.chunkX = chunkX;
        this.baseY = baseY;
        this.chunkZ = chunkZ;
        nChunkX = chunkX < 0;
        nChunkZ = chunkZ < 0;
        this.isUnderground = isUnderground;
        this.floors = floors;
        this.difficulty = difficulty;
        this.towerName = towerName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	nChunkX = buf.readBoolean();
    	nChunkZ = buf.readBoolean();
    	chunkX = ByteBufUtils.readVarShort(buf);
    	if (nChunkX)
    		chunkX = ((~chunkX & 0xff) * -1) - 1;
    	baseY =  ByteBufUtils.readVarInt(buf, 2);
    	chunkZ = ByteBufUtils.readVarShort(buf);
    	if (nChunkZ)
    		chunkZ = ((~chunkZ & 0xff) * -1) - 1;
    	isUnderground = buf.readBoolean();
        floors = ByteBufUtils.readVarInt(buf, 1);
        difficulty = ByteBufUtils.readVarInt(buf, 1);
        towerName = ByteBufUtils.readUTF8String(buf);
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
    	ByteBufUtils.writeUTF8String(buf, towerName);
    }

    public static class Handler implements IMessageHandler<PacketMazeTowersGui, IMessage> {
        @Override
        public IMessage onMessage(final PacketMazeTowersGui message, final MessageContext ctx) {
            IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                	PlayerMazeTower props = PlayerMazeTower
                		.get(Minecraft.getMinecraft().thePlayer);
                	if (props != null) {
                		boolean enabled = props.getEnabled();
	                	if (message.floors != 0) {
	                		if (!enabled) {
	                			props.setEnabled(true);
	                			props.setIsUnderground(message.isUnderground);
	                			props.setTowerData(message.chunkX, message.baseY,
	                				message.chunkZ, message.floors, message.difficulty);
	                			props.setTowerName(message.towerName);
	                			props.setFloor(1);
	                		}
	                	} else if (enabled) {
	                		props.setEnabled(false);
	                		//Minecraft.getMinecraft().setIngameFocus();
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