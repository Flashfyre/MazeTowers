package com.samuel.mazetowers.etc;

import net.minecraft.util.math.BlockPos;

public interface IMazeTowerCapability {
	
	public BlockPos getSpawnPos();

	public boolean getEnabled();

	public int getFloor();

	public boolean getIsUnderground();

	public int getDifficulty();

	public int getRarity();

	public int[] getTowerData();

	public String getTowerName();
	
	public void setSpawnPos(BlockPos fromLong);

	public void setEnabled(boolean enabled);

	public void setFloor(int floor);

	public void setIsUnderground(boolean isUnderground);

	public void setTowerData(int chunkX, int baseY,
		int chunkZ, int floors, int difficulty, int rarity);
	
	public void setTowerData(int[] towerData);

	public void setTowerName(String towerName);
}
