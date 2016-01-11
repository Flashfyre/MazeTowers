package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.List;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class CommandItemScanner implements ICommand, Comparable<ICommand> {
	
	private List aliases;
	public CommandItemScanner()
	{
		this.aliases = new ArrayList();
		this.aliases.add("IS");
		this.aliases.add("itemscanner");
		this.aliases.add("ItemScanner");
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "is";
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
		return "is";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] astring)
		throws CommandException {
		boolean isCorrectUsage = false;
		if (!(sender instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) sender;
		double maxDistance = 10;//((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		Vec3 lookVec = player.getLook(1.0F);
		Vec3 start = new Vec3(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);
		Vec3 end = start.addVector(lookVec.xCoord * maxDistance, lookVec.yCoord * maxDistance, lookVec.zCoord * maxDistance);
		MovingObjectPosition mop = player.worldObj.rayTraceBlocks(start, end, false, true, false);
		BlockPos lookPos = mop.getBlockPos();
		IBlockState lookState = player.worldObj.getBlockState(lookPos);
		if (lookState.getBlock() instanceof BlockItemScanner &&
			((TileEntityItemScanner) player.worldObj.getTileEntity(lookPos))
			.getOwnerName().equals(player.getDisplayName())) {
		
			if (astring[0].equals("set")) {
				if (astring.length == 2) {
					if (astring[1] == "item") {
						String itemName;
						ItemStack heldItem = player.getHeldItem();
						((TileEntityItemScanner) player.worldObj.getTileEntity(lookPos))
						.setKeyStack(heldItem);
						sender.addChatMessage(new ChatComponentText("Key item set to "
							+ heldItem.getDisplayName()));
					}
				}
				if (!isCorrectUsage)
					throw new WrongUsageException("commands.itemscanner.set", new Object[0]);
			} else if (astring[0].equals("inventory")) {
				
			}
		} else
			sender.addChatMessage(new ChatComponentText(
				"To use this command you must be looking at an Item Scanner and be the owner of that scanner."));
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

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
}
