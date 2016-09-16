package com.samuel.mazetowers.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.tileentity.TileEntityChaoticSludgeToxin;

public class BlockChaoticSludge extends BlockFluidClassic
	implements ITileEntityProvider {
	
	DamageSource damageSource = new DamageSource("chaoticSludge") {
		@Override
		/**
	     * Gets the death message that is displayed when the player dies
	     */
	    public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn)
	    {
	        EntityLivingBase entitylivingbase = entityLivingBaseIn.getAttackingEntity();
	        String s = "death.attack." + this.damageType;
	        String s1 = s + ".player";
	        return entitylivingbase != null && I18n.canTranslate(s1) ? new TextComponentTranslation(s1, new Object[] {entityLivingBaseIn.getDisplayName(), entitylivingbase.getDisplayName()}): new TextComponentTranslation(s, new Object[] {entityLivingBaseIn.getDisplayName()});
	    }
	};

	public BlockChaoticSludge(Fluid fluid,
		String unlocalizedName) {
		super(fluid, Material.WATER);
		this.setHardness(100f);
		this.setResistance(500f);
		this.setUnlocalizedName(unlocalizedName);
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn,
		BlockPos pos) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn,
		int meta) {
		return new TileEntityChaoticSludgeToxin();
	}
	
	@Override
	/**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
		if (!worldIn.isRemote) {
	    	if (state.getBlock() == MazeTowers.BlockChaoticSludge) {
	    		if (entityIn instanceof EntityLivingBase) {
	    			if (((EntityLivingBase) entityIn).attackEntityFrom(damageSource, (float) (16.0F - state.getValue(BlockChaoticSludge.LEVEL)))) {
	    				entityIn.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, worldIn.rand.nextFloat() * 0.4F);
	    			}
	    		} else if (entityIn instanceof EntityItem) {
	    			entityIn.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, worldIn.rand.nextFloat() * 0.4F);
	    			entityIn.attackEntityFrom(damageSource, 4.0F);
	    			entityIn.setFire(15);
	    		}
	    	}
		}
    }
}
