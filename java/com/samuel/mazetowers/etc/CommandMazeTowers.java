package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.List;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.*;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandMazeTowers implements ICommand, Comparable<ICommand> {
	
	private List aliases;
	public CommandMazeTowers()
	{
		this.aliases = new ArrayList();
		this.aliases.add("mt");
		this.aliases.add("mazetowers");
		this.aliases.add("MazeTowers");
		this.aliases.add("mazeTowers");
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "mt";
	}
	
	@Override
	public List getCommandAliases() {
		return this.aliases;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
	String[] astring, BlockPos pos)
	{
		return null;
	}
	
	@Override
	public boolean isUsernameIndex(String[] astring, int i)
	{
		return false;
	}
	
	@Override
	public int compareTo(ICommand arg0)
	{
		return 0;
	}

	@Override
	public String getCommandName() {
		return "mt";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if (!MazeTowers.enableMazeTowers) {
			sender.addChatMessage(new ChatComponentText("Error: Maze Towers are disabled"));
			return;
		} else {
			if (astring[0].equals("build") || astring[0].equals("spawn")) {
				MazeTowers.mazeTowers.rebuild(sender.getEntityWorld(), 0);
				sender.addChatMessage(new ChatComponentText("Tower built at " +
					MazeTowers.mazeTowers.getSpawnPos(sender.getEntityWorld(), 0).toString()));
			} else if (astring[0].equals("recreate") || astring[0].equals("rebuild") || astring[0].equals("respawn")) {
				MazeTowers.mazeTowers.recreate(sender.getEntityWorld(), true);
				sender.addChatMessage(new ChatComponentText("Towers have been recreated and rebuilt"));
			}
			/*if (astring[0].equals("tp") || astring[0].equals("warp")) {
			
				BlockPos compassPos = ChaosBlock.chaosLabyrinth.getCompassPos(sender.getEntityWorld());
				
				if (compassPos.getY() < 1) {
					sender.addChatMessage(new ChatComponentText("Invalid spawn position"));
					return;
				}
					
				if (sender instanceof EntityPlayerMP)
		        {
		            ((Entity)sender).mountEntity((Entity)null);
		            ((EntityPlayerMP)sender).playerNetServerHandler.setPlayerLocation(compassPos.getX(), compassPos.getY(), compassPos.getZ(), ((EntityPlayerMP) sender).getRotationYawHead(), ((EntityPlayerMP) sender).rotationPitch);
		            sender.addChatMessage(new ChatComponentText("Teleported to Chaos Labyrinth"));
		        }
			} else if (astring[0].equals("volatile")) {
				boolean undo = astring.length == 2 && astring[1].equals("undo");
				if (!undo)
					sender.addChatMessage(new ChatComponentText("Converting Chaos Labyrinth blocks to volatile chaos blocks..."));
				else
					sender.addChatMessage(new ChatComponentText("Converting Chaos Labyrinth blocks back to unbreakable chaos blocks..."));
				ChaosBlock.chaosLabyrinth.convertToFromVolatile(sender.getEntityWorld(), ((EntityPlayerMP)sender), undo);
			} else if (astring[0].equals("deathtrap")) {
				sender.addChatMessage(new ChatComponentText("Chaos Labyrinth is now a deathtrap - please be aware that this cannot be reversed"));
				ChaosBlock.chaosLabyrinth.convertToDeathtrap(sender.getEntityWorld());
			}*/
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(2, this.getCommandName());
	}
}