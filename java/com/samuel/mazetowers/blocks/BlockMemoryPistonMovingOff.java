package com.samuel.mazetowers.blocks;

import java.util.Random;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.tileentities.TileEntityMemoryPiston;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMemoryPistonMovingOff extends BlockMemoryPistonMoving {
	
	public static final PropertyDirection FACING = BlockMemoryPistonExtension.FACING;

    public BlockMemoryPistonMovingOff(String unlocalizedName)
    {
        super(unlocalizedName);
    }
}
