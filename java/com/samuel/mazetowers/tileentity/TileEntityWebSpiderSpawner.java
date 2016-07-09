package com.samuel.mazetowers.tileentity;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityWebSpiderSpawner extends TileEntity
	implements ITickable {

	private final int difficulty;

	public TileEntityWebSpiderSpawner() {
		difficulty = 1;
	}

	public TileEntityWebSpiderSpawner(int difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			boolean isValid = false;
			boolean isWeb = false;
			IBlockState state;
			if ((state = this.worldObj.getBlockState(pos)) != null
				&& (isWeb = state.getBlock() == Blocks.WEB)) {
				float posX, posY, posZ;
				List list = worldObj.getEntitiesWithinAABB(
					EntityPlayer.class, new AxisAlignedBB(
							(posX = this.pos.getX()),
							(posY = this.pos.getY()) + 0D,
							(posZ = this.pos.getZ()),
							posX + 1.0D, posY + 0.5D,
							posZ + 1.0D));
				if (list.isEmpty())
					isValid = true;
				else {
					EntityPlayer player = (EntityPlayer) list
						.get(0);
					if (player.capabilities.isCreativeMode)
						isValid = true;
					else if ((this.worldObj.rand
						.nextInt(10) + 1) < difficulty) {
						EntitySpider spider = difficulty < 5 ? new EntitySpider(
							this.worldObj)
							: new EntityCaveSpider(
								this.worldObj);
						EnumFacing offsetDir = EnumFacing
							.fromAngle(
								player.getRotationYawHead())
							.getOpposite();
						if (offsetDir.getAxis() == Axis.X) {
							posX += offsetDir
								.getAxisDirection() == AxisDirection.POSITIVE ? 1.5F
								: -0.5F;
							posZ += (posZ < 0 ? 0.5 : -0.5);
						} else {
							posX += (posX < 0 ? 0.5 : -0.5);
							posZ += offsetDir
								.getAxisDirection() == AxisDirection.POSITIVE ? 1.5F
								: -0.5F;
						}
						spider.setLocationAndAngles(posX,
							posY - 2.0F, posZ, player
								.getRotationYawHead(),
							spider.cameraPitch);
						this.worldObj
							.spawnEntityInWorld(spider);
					}
				}
			}
			if (!isValid) {
				this.invalidate();
				this.worldObj.setTileEntity(pos, null);
			}
		}
	}
}