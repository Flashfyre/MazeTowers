package com.samuel.mazetowers.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;
import com.samuel.mazetowers.blocks.BlockMemoryPistonMoving;

public class TileEntityMemoryPiston extends TileEntity
	implements ITickable {
	private IBlockState pistonState;
	private EnumFacing pistonFacing;
	/** if this piston is extending or not */
	private boolean extending;
	private boolean shouldHeadBeRendered;
	private float progress;
	/** the progress in (de)extending */
	private float lastProgress;
	private List<Entity> field_174933_k = Lists
		.<Entity> newArrayList();

	public TileEntityMemoryPiston() {
	}

	public TileEntityMemoryPiston(
		IBlockState pistonStateIn,
		EnumFacing pistonFacingIn, boolean extendingIn,
		boolean shouldHeadBeRenderedIn) {
		this.pistonState = pistonStateIn;
		this.pistonFacing = pistonFacingIn;
		this.extending = extendingIn;
		this.shouldHeadBeRendered = shouldHeadBeRenderedIn;
	}

	public IBlockState getPistonState() {
		return this.pistonState;
	}

	@Override
	public int getBlockMetadata() {
		return 0;
	}

	/**
	 * Returns true if a piston is extending
	 */
	public boolean isExtending() {
		return this.extending;
	}

	public EnumFacing getFacing() {
		return this.pistonFacing;
	}
	
	private float func_184320_e(float p_184320_1_)
    {
        return this.extending ? p_184320_1_ - 1.0F : 1.0F - p_184320_1_;
    }

	@SideOnly(Side.CLIENT)
	public boolean shouldPistonHeadBeRendered() {
		return this.shouldHeadBeRendered;
	}
	
	private void func_184322_i()
    {
		/*if (this.extending) {
			p_145863_1_ = 1.0F - p_145863_1_;
		} else {
			--p_145863_1_;
		}

		AxisAlignedBB axisalignedbb = ((BlockMemoryPistonMoving) MazeTowers.BlockMemoryPistonExtension)
			.getBoundingBox(this.worldObj, this.pos,
				this.pistonState, p_145863_1_,
				this.pistonFacing);

		if (axisalignedbb != null) {
			List<Entity> list = this.worldObj
				.getEntitiesWithinAABBExcludingEntity(
					(Entity) null, axisalignedbb);

			if (!list.isEmpty()) {
				this.field_174933_k.addAll(list);

				for (Entity entity : this.field_174933_k) {
					if (this.pistonState.getBlock() == Blocks.slime_block
						&& this.extending) {
						switch (this.pistonFacing.getAxis()) {
						case X:
							entity.motionX = (double) this.pistonFacing
								.getFrontOffsetX();
							break;
						case Y:
							entity.motionY = (double) this.pistonFacing
								.getFrontOffsetY();
							break;
						case Z:
							entity.motionZ = (double) this.pistonFacing
								.getFrontOffsetZ();
						}
					} else {
						entity
							.moveEntity(
								(double) (p_145863_2_ * (float) this.pistonFacing
									.getFrontOffsetX()),
								(double) (p_145863_2_ * (float) this.pistonFacing
									.getFrontOffsetY()),
								(double) (p_145863_2_ * (float) this.pistonFacing
									.getFrontOffsetZ()));
					}
				}

				this.field_174933_k.clear();
			}
		}*/
        AxisAlignedBB axisalignedbb = this.func_184321_a(this.worldObj, this.pos).offset(this.pos);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);

        if (!list.isEmpty())
        {
            EnumFacing enumfacing = this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();

            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity = list.get(i);

                if (entity.getPushReaction() != EnumPushReaction.IGNORE)
                {
                    if (this.pistonState.getBlock() == Blocks.slime_block)
                    {
                        switch (enumfacing.getAxis())
                        {
                            case X:
                                entity.motionX = enumfacing.getFrontOffsetX();
                                break;
                            case Y:
                                entity.motionY = enumfacing.getFrontOffsetY();
                                break;
                            case Z:
                                entity.motionZ = enumfacing.getFrontOffsetZ();
                        }
                    }

                    double d0 = 0.0D;
                    double d1 = 0.0D;
                    double d2 = 0.0D;
                    AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();

                    switch (enumfacing.getAxis())
                    {
                        case X:

                            if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                            {
                                d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                            }
                            else
                            {
                                d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                            }

                            d0 = d0 + 0.01D;
                            break;
                        case Y:

                            if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                            {
                                d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                            }
                            else
                            {
                                d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                            }

                            d1 = d1 + 0.01D;
                            break;
                        case Z:

                            if (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                            {
                                d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                            }
                            else
                            {
                                d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                            }

                            d2 = d2 + 0.01D;
                    }

                    entity.moveEntity(d0 * enumfacing.getFrontOffsetX(), d1 * enumfacing.getFrontOffsetY(), d2 * enumfacing.getFrontOffsetZ());
                }
            }
        }
    }
	
	/**
     * Get interpolated progress value (between lastProgress and progress) given the fractional time between ticks as an
     * argument
     */
    @SideOnly(Side.CLIENT)
    public float getProgress(float ticks)
    {
        if (ticks > 1.0F)
        {
            ticks = 1.0F;
        }

        return this.lastProgress + (this.progress - this.lastProgress) * ticks;
    }

    public AxisAlignedBB func_184321_a(IBlockAccess p_184321_1_, BlockPos p_184321_2_)
    {
        return this.func_184319_a(p_184321_1_, p_184321_2_, this.progress).union(this.func_184319_a(p_184321_1_, p_184321_2_, this.lastProgress));
    }

    public AxisAlignedBB func_184319_a(IBlockAccess p_184319_1_, BlockPos p_184319_2_, float p_184319_3_)
    {
        p_184319_3_ = this.func_184320_e(p_184319_3_);
        return this.pistonState.getBoundingBox(p_184319_1_, p_184319_2_).offset(p_184319_3_ * this.pistonFacing.getFrontOffsetX(), p_184319_3_ * this.pistonFacing.getFrontOffsetY(), p_184319_3_ * this.pistonFacing.getFrontOffsetZ());
    }

	@SideOnly(Side.CLIENT)
	public float getOffsetX(float ticks) {
		return this.extending ? (this.getProgress(ticks) - 1.0F)
			* this.pistonFacing.getFrontOffsetX()
			: (1.0F - this.getProgress(ticks))
				* this.pistonFacing
					.getFrontOffsetX();
	}

	@SideOnly(Side.CLIENT)
	public float getOffsetY(float ticks) {
		return this.extending ? (this.getProgress(ticks) - 1.0F)
			* this.pistonFacing.getFrontOffsetY()
			: (1.0F - this.getProgress(ticks))
				* this.pistonFacing
					.getFrontOffsetY();
	}

	@SideOnly(Side.CLIENT)
	public float getOffsetZ(float ticks) {
		return this.extending ? (this.getProgress(ticks) - 1.0F)
			* this.pistonFacing.getFrontOffsetZ()
			: (1.0F - this.getProgress(ticks))
				* this.pistonFacing
					.getFrontOffsetZ();
	}

	/**
	 * removes a piston's tile entity (and if the piston is moving, stops it)
	 */
	public void clearPistonTileEntity() {
		if (this.lastProgress < 1.0F
			&& this.worldObj != null) {
			this.lastProgress = this.progress = 1.0F;
			this.worldObj.removeTileEntity(this.pos);
			this.invalidate();

			if (this.worldObj.getBlockState(this.pos)
				.getBlock() instanceof BlockMemoryPistonMoving) {
				this.worldObj.setBlockState(this.pos,
					this.pistonState, 3);
				if (!net.minecraftforge.event.ForgeEventFactory
					.onNeighborNotify(
						worldObj,
						pos,
						worldObj.getBlockState(pos),
						java.util.EnumSet
							.noneOf(EnumFacing.class))
					.isCanceled())
					this.worldObj.notifyBlockOfStateChange(
						this.pos, this.pistonState
							.getBlock());
			}
		}
	}

	//@Override
	/**
	 * Like the old updateEntity(), except more generic.
	 */
	/*public void update() {
		this.lastProgress = this.progress;

		if (this.lastProgress >= 1.0F) {
			this.launchWithSlimeBlock(1.0F, 0.25F);
			this.worldObj.removeTileEntity(this.pos);
			this.invalidate();

			if (this.worldObj.getBlockState(this.pos)
				.getBlock() instanceof BlockMemoryPistonMoving) {
				this.worldObj.setBlockState(this.pos,
					this.pistonState, 3);
				if (!net.minecraftforge.event.ForgeEventFactory
					.onNeighborNotify(
						worldObj,
						pos,
						worldObj.getBlockState(pos),
						java.util.EnumSet
							.noneOf(EnumFacing.class))
					.isCanceled())
					this.worldObj.notifyBlockOfStateChange(
						this.pos, this.pistonState
							.getBlock());
			}
		} else {
			this.progress += 0.5F;

			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}

			if (this.extending) {
				this.launchWithSlimeBlock(this.progress,
					this.progress - this.lastProgress
						+ 0.0625F);
			}
		}
	}*/
	
	@Override
	/**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        this.lastProgress = this.progress;

        if (this.lastProgress >= 1.0F)
        {
            this.func_184322_i();
            this.worldObj.removeTileEntity(this.pos);
            this.invalidate();

            if (this.worldObj.getBlockState(this.pos).getBlock() instanceof BlockMemoryPistonMoving)
            {
                this.worldObj.setBlockState(this.pos, this.pistonState, 3);
                if(!net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(worldObj, pos, worldObj.getBlockState(pos), java.util.EnumSet.noneOf(EnumFacing.class)).isCanceled())
                    this.worldObj.notifyBlockOfStateChange(this.pos, this.pistonState.getBlock());
            }
        }
        else
        {
            this.progress += 0.5F;

            if (this.progress >= 1.0F)
            {
                this.progress = 1.0F;
            }

            this.func_184322_i();
        }
    }

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.pistonState = Block.getBlockById(
			compound.getInteger("blockId"))
			.getStateFromMeta(
				compound.getInteger("blockData"));
		this.pistonFacing = EnumFacing.getFront(compound
			.getInteger("facing"));
		this.lastProgress = this.progress = compound
			.getFloat("progress");
		this.extending = compound.getBoolean("extending");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("blockId", Block
			.getIdFromBlock(this.pistonState.getBlock()));
		compound.setInteger("blockData", this.pistonState
			.getBlock().getMetaFromState(this.pistonState));
		compound.setInteger("facing", this.pistonFacing
			.getIndex());
		compound.setFloat("progress", this.lastProgress);
		compound.setBoolean("extending", this.extending);
	}
}
