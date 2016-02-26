package com.samuel.mazetowers.etc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerMazeTower implements
	IExtendedEntityProperties {

	public final static String EXT_PROP_NAME = "PlayerMazeTower";

	private final EntityPlayer player;
	private int floor;
	private int floors;
	private int chunkX;
	private int chunkZ;
	private int baseY;
	private int difficulty;
	private int rarity;
	private String towerName;
	private boolean isUnderground;
	private boolean enabled;
	private BlockPos spawnPos;

	// private BlockPos playerSpawnPos;
	// private BlockPos spawnPos;

	public PlayerMazeTower(EntityPlayer player, int floor,
		boolean enabled) {
		this.player = player;
		this.floor = floor;
		this.enabled = enabled;
		isUnderground = false;
		floors = 0;
		chunkX = 0;
		chunkZ = 0;
		baseY = 0;
		difficulty = 0;
		rarity = 0;
		towerName = null;
	}

	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(
			PlayerMazeTower.EXT_PROP_NAME,
			new PlayerMazeTower(player, 0, false));
	}

	public static final PlayerMazeTower get(
		EntityPlayer player) {
		return (PlayerMazeTower) player
			.getExtendedProperties(EXT_PROP_NAME);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();

		properties.setBoolean("enabled", this.enabled);
		properties.setBoolean("isUnderground",
			isUnderground);
		properties.setInteger("floor", this.floor);
		properties.setLong("spawnPos", this.spawnPos
			.toLong());

		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound
			.getTag(EXT_PROP_NAME);

		this.enabled = properties.getBoolean("enabled");
		this.isUnderground = properties
			.getBoolean("enabled");
		this.floor = properties.getInteger("floor");
		this.spawnPos = BlockPos.fromLong(properties
			.getLong("spawnPos"));
	}

	@Override
	public void init(Entity entity, World world) {
		if (world.provider.getDimensionId() == 0)
			this.spawnPos = new BlockPos(getPlayerSpawnPos(
				world, (EntityPlayer) entity));
		else
			this.spawnPos = world.getSpawnPoint();
	}

	public BlockPos getSpawnPos() {
		return spawnPos;
	}

	public BlockPos getPlayerSpawnPos(World worldIn,
		EntityPlayer player) {
		BlockPos bedLoc = player.getBedLocation();
		return bedLoc != null ? bedLoc : worldIn
			.getSpawnPoint();
	}

	public boolean getEnabled() {
		return enabled;
	}

	public int getFloor() {
		return floor;
	}

	public boolean getIsUnderground() {
		return isUnderground;
	}

	public int getDifficulty() {
		int floor = Math.min(!isUnderground ? this.floor
			: Math.abs(this.floor - 2), floors);
		int difficulty = this.difficulty
			+ ((int) Math.floor((floor - 1) / 5));
		return difficulty;
	}

	public int getRarity() {
		int floor = Math.min(!isUnderground ? this.floor
			: Math.abs(this.floor - 2), floors);
		int rarity = this.rarity
			+ ((int) Math.floor((floor - 1) / 5));
		return rarity;
	}

	public int[] getTowerData() {
		return new int[] { chunkX, baseY, chunkZ, floors,
			difficulty, rarity };
	}

	public String getTowerName() {
		return towerName;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public void setIsUnderground(boolean isUnderground) {
		this.isUnderground = isUnderground;
	}

	public void setTowerData(int chunkX, int baseY,
		int chunkZ, int floors, int difficulty, int rarity) {
		this.chunkX = chunkX;
		this.baseY = baseY;
		this.chunkZ = chunkZ;
		this.floors = floors;
		this.difficulty = difficulty;
		this.rarity = rarity;
	}

	public void setTowerName(String towerName) {
		this.towerName = towerName;
	}
}