package com.samuel.mazetowers.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import com.samuel.mazetowers.init.ModItems;
import com.samuel.mazetowers.init.ModSounds;

public class EntityExplosiveArrow extends EntityArrow {

	private int ticksInGround = 0;
	
	public EntityExplosiveArrow(World worldIn)
    {
        super(worldIn);
    }

    public EntityExplosiveArrow(World worldIn, EntityLivingBase shooter)
    {
        super(worldIn, shooter);
        if (shooter instanceof EntityPlayer)
			this.canBePickedUp = PickupStatus.CREATIVE_ONLY;
    }

    public EntityExplosiveArrow(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

	@Override
	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();
        if (!this.worldObj.isRemote && this.inGround) {
            ++this.ticksInGround;
            
            if (this.ticksInGround == 1) {
				this.worldObj.playSound(
					null, getPosition(), ModSounds.primed,
					SoundCategory.BLOCKS, 1.0F, 0.9F + (this.worldObj.rand
					.nextFloat() - this.worldObj.rand
					.nextFloat()) * 0.15F);
			} else if (this.ticksInGround >= 25) {
				this.worldObj
					.newExplosion(
						this,
						this.posX,
						this.posY
							+ this.height / 2.0F,
						this.posZ, 2.0F, false, true);
				this.setDead();
			}
       	} else
       		this.ticksInGround = 0;
	}
	
	@Override
	protected void arrowHit(EntityLivingBase living)
    {
		if (!this.worldObj.isRemote) {
			this.worldObj.newExplosion(
			this, this.posX, this.posY + this.height / 2.0F,
			this.posZ, 2.0F, false, true);
		}
		this.setDead();
    }

	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(ModItems.explosive_arrow);
	}
}