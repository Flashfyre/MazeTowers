package com.samuel.mazetowers.etc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModChestGen;
import com.samuel.mazetowers.world.WorldGenMazeTowers.MazeTowerBase;

public class CommandMazeTowers implements ICommand,
	Comparable<ICommand> {

	private List aliases;

	public CommandMazeTowers() {
		this.aliases = new ArrayList();
		this.aliases.add("mazetowers");
		this.aliases.add("MazeTowers");
		this.aliases.add("mazeTowers");
	}

	@Override
	public String getCommandUsage(
		ICommandSender icommandsender) {
		return "mt";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server,
		ICommandSender icommandsender, String[] astring,
		BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "mt";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender,
		String[] astring) {
		if (!MazeTowers.enableMazeTowers) {
			sender.addChatMessage(new TextComponentString(
				"Error: Maze Towers are disabled"));
			return;
		} else {
			int dimId = sender.getEntityWorld().provider
				.getDimension();
			if (astring[0].equals("build")
				|| astring[0].equals("spawn")) {
				for (int g = 0; g < MazeTowers.mazeTowers
					.getGenCount(dimId); g++) {
					BlockPos spawnPos = MazeTowers.mazeTowers
						.getSpawnPos(dimId, g);
					if (sender.getEntityWorld()
						.isAnyPlayerWithinRangeAt(
							spawnPos.getX(),
							spawnPos.getY(),
							spawnPos.getZ(), 50)) {
						MazeTowers.mazeTowers.rebuild(
							sender.getEntityWorld(), g);
						sender
							.addChatMessage(new TextComponentString(
								"Tower #" + g
									+ " built at "
									+ spawnPos.toString()));
					}
				}
			} else if (astring[0].equals("recreate")
				|| astring[0].equals("rebuild")
				|| astring[0].equals("respawn")) {
				MazeTowers.mazeTowers.recreate(sender
					.getEntityWorld(), true);
				// sender.addChatMessage(new
				// TextComponentString("Towers have been recreated and rebuilt"));
			} else if (astring.length == 2 && astring[0].equals("refresh") &&
				astring[1].toLowerCase().equals("chestgen")) {
				ModChestGen.initChestGen(sender.getEntityWorld().rand, true);
				sender.addChatMessage(new TextComponentString(
					"ChestGen items have been refreshed"));
			} else if (astring[0].equals("loot")) {
				MazeTowerBase tower = MazeTowers.mazeTowers.getTowerBesideCoords(
					sender.getEntityWorld(), sender.getPosition().getX() >> 4,
					sender.getPosition().getZ() >> 4);
				if (tower != null) {
					int rarity;
					try {
						if (astring.length >= 2 &&
							(astring.length != 2 ||
							astring[1].equals("inventory")))
							rarity = Integer.parseInt(!astring[1].equals("inventory") ?
								astring[1] : astring[2]);
						else
							rarity = tower.getRarity(tower.getFloorFromPosY(
								sender.getPosition().getY()));
					} catch (NumberFormatException e) {
						rarity = tower.getRarity(tower.getFloorFromPosY(
							sender.getPosition().getY()));
					}
					if (astring.length > 1 &&
						astring[astring.length - 1].equals("inventory") ||
						astring[astring.length - 2].equals("inventory"))
						MTUtils.fillInventoryWithLoot((EntityPlayer) sender, rarity);
    				ArrayList<String> lootList =
    					MTUtils.getLootList(sender.getEntityWorld().rand, rarity);
    				for (String s : lootList)
    					sender.addChatMessage(new TextComponentString(
    						ChatFormatting.YELLOW + s));
				}
			} else if (astring[0].equals("end")) {
				World world = sender.getEntityWorld();
				BlockPos pos = sender.getPosition();
				IBlockState state = world.getBlockState(pos);
				world.setBlockState(pos, Blocks.end_portal.getDefaultState());
			}
			/*
			 * if (astring[0].equals("tp") || astring[0].equals("warp")) {
			 * 
			 * BlockPos compassPos =
			 * ChaosBlock.chaosLabyrinth.getCompassPos(sender.getEntityWorld());
			 * 
			 * if (compassPos.getY() < 1) { sender.addChatMessage(new
			 * TextComponentString("Invalid spawn position")); return; }
			 * 
			 * if (sender instanceof EntityPlayerMP) {
			 * ((Entity)sender).mountEntity((Entity)null);
			 * ((EntityPlayerMP)sender
			 * ).playerNetServerHandler.setPlayerLocation(compassPos.getX(),
			 * compassPos.getY(), compassPos.getZ(), ((EntityPlayerMP)
			 * sender).getRotationYawHead(), ((EntityPlayerMP)
			 * sender).rotationPitch); sender.addChatMessage(new
			 * TextComponentString("Teleported to Chaos Labyrinth")); } } else if
			 * (astring[0].equals("volatile")) { boolean undo = astring.length
			 * == 2 && astring[1].equals("undo"); if (!undo)
			 * sender.addChatMessage(new TextComponentString(
			 * "Converting Chaos Labyrinth blocks to volatile chaos blocks..."
			 * )); else sender.addChatMessage(new TextComponentString(
			 * "Converting Chaos Labyrinth blocks back to unbreakable chaos blocks..."
			 * ));
			 * ChaosBlock.chaosLabyrinth.convertToFromVolatile(sender.getEntityWorld
			 * (), ((EntityPlayerMP)sender), undo); } else if
			 * (astring[0].equals("deathtrap")) { sender.addChatMessage(new
			 * TextComponentString(
			 * "Chaos Labyrinth is now a deathtrap - please be aware that this cannot be reversed"
			 * ));
			 * ChaosBlock.chaosLabyrinth.convertToDeathtrap(sender.getEntityWorld
			 * ()); }
			 */
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canCommandSenderUseCommand(2, this.getCommandName());
	}
}
