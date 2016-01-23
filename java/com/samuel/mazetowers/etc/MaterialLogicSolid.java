package com.samuel.mazetowers.etc;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLogic;

public class MaterialLogicSolid extends MaterialLogic {
	
	public MaterialLogicSolid(MapColor mapColor)
    {
        super(mapColor);
    }

	@Override
    /**
     * Returns true if the block is a considered solid. This is true by default.
     */
    public boolean isSolid()
    {
        return true;
    }

	@Override
    /**
     * Returns if this material is considered solid or not
     */
    public boolean blocksMovement()
    {
        return true;
    }
}
