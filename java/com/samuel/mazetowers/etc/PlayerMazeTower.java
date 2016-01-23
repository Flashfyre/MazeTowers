package com.samuel.mazetowers.etc;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.worldgen.WorldGenMazeTowers.MazeTower;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerMazeTower implements IExtendedEntityProperties {
	
	public final static String EXT_PROP_NAME = "PlayerMazeTower";
	
	private final EntityPlayer player;
	private int floor;
	//private int difficulty 
	private boolean enabled;
	private BlockPos spawnPos;
	//private BlockPos playerSpawnPos;
	//private BlockPos spawnPos;
	
	public PlayerMazeTower(EntityPlayer player, int floor, boolean enabled) {
		this.player = player;
		this.floor = floor;
		this.enabled = enabled;
	}
	
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(PlayerMazeTower.EXT_PROP_NAME,
			new PlayerMazeTower(player, -1, false));
	}
	
	public static final PlayerMazeTower get(EntityPlayer player) {
		return (PlayerMazeTower) player.getExtendedProperties(EXT_PROP_NAME);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		
		properties.setBoolean("enabled", this.enabled);
		properties.setInteger("floor", this.floor);
		properties.setLong("spawnPos", this.spawnPos.toLong());
	
		compound.setTag(EXT_PROP_NAME, properties);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		this.floor = properties.getInteger("floor");
	}
	
	@Override
	public void init(Entity entity, World world) {
		this.spawnPos = new BlockPos(getPlayerSpawnPos(world, (EntityPlayer) entity));
	}
	
	public BlockPos getSpawnPos() {
		return spawnPos;
	}
	
	public BlockPos getPlayerSpawnPos(World worldIn, EntityPlayer player) {
		BlockPos bedLoc = player.getBedLocation();
		return bedLoc != null ? bedLoc : worldIn.getSpawnPoint();
	}
	
	/*public void setSpawnPos(int bgmIndexIn) {
		World world = player.getEntityWorld();
		BlockPos labyrinthSpawnPos = ChaosBlock.chaosLabyrinth.getSpawnPos(world);
		switch (bgmIndexIn) {
			case 0:
			case 5:
				this.spawnPos = new BlockPos(this.playerSpawnPos);
				break;
			case 1:
				this.spawnPos = new BlockPos(labyrinthSpawnPos.getX() + 16, labyrinthSpawnPos.getY() + 40,
					labyrinthSpawnPos.getZ() + 8);
				if (this.bgmIndex == 0 && !world.isRemote && (player.getBedLocation(0) == null ||
					(!player.getBedLocation(0).equals(new BlockPos(labyrinthSpawnPos.getX() + 16, labyrinthSpawnPos.getY() + 40, labyrinthSpawnPos.getZ() + 8)) &&
					!player.getBedLocation(0).equals(new BlockPos(labyrinthSpawnPos.getX() + 16, labyrinthSpawnPos.getY() + 7, labyrinthSpawnPos.getZ() + 8)))))
					setSpawnPos(world);
				break;
			case 2:
			case 3:
			case 4:
				this.spawnPos = new BlockPos(labyrinthSpawnPos.getX() + 16, labyrinthSpawnPos.getY() + 7,
					labyrinthSpawnPos.getZ() + 8);
				
		}
		
		if (!world.isRemote) {
			boolean useWorldSpawn = this.spawnPos.equals(this.playerSpawnPos) && this.playerSpawnPos.equals(player.getEntityWorld().getSpawnPoint());
			player.setSpawnPoint((!useWorldSpawn) ? new BlockPos(this.spawnPos) : null, true);
			player.setSpawnChunk((!useWorldSpawn) ? new BlockPos(this.spawnPos) : null, true, 0);
		}
	}*/
}