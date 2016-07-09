package com.samuel.mazetowers.items;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Multimap;
import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.init.ModSounds;

public class ItemSpectriteSword extends ItemSword {
	
	public ItemSpectriteSword(ToolMaterial material) {
		super(material);
		this.setCreativeTab(CreativeTabs.COMBAT);
		this.setMaxStackSize(1);
		this.addPropertyOverride(new ResourceLocation("time"), MazeTowers.ItemPropertyGetterSpectrite);
	}
	
	public ItemSpectriteSword() {
		this(MazeTowers.DIAMOND_SPECTRITE_TOOL);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		
		String displayName = super.getItemStackDisplayName(stack);
		displayName = (stack.getItem() instanceof ItemSpectriteSwordSpecial ? TextFormatting.RED :
			TextFormatting.LIGHT_PURPLE) + displayName;
		return displayName;
	}
	
	@Override
	/**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase playerIn)
    {
        return true;
    }
	
	@Override
	/**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     *  
     * @param target The Entity being hit
     * @param attacker the attacking entity
     */
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
		World world = attacker.worldObj;
		BlockPos pos = target.getPosition();
		if (!world.isRemote) {
			WorldServer worldServer = (WorldServer) world;
			int power = (stack.getItem() instanceof ItemSpectriteSwordSpecial) ? 4 : 3;
			
			List<Entity> surrounding = world.getEntitiesWithinAABBExcludingEntity(attacker,
				new AxisAlignedBB(pos.north(power - 1).west(power - 1).down(power - 1),
				pos.south(power - 1).east(power - 1).up(power - 1)));
		
			if (target.getMaxHealth() >= 200.0F) {
				target.attackEntityFrom(DamageSource.causeThornsDamage(attacker), power == 3 ? 34.0F : 43.0F);
			}
			EnumParticleTypes particle = null;
			switch (power) {
				case 3:
					particle = EnumParticleTypes.EXPLOSION_LARGE;
					
					world.playSound(null, pos, ModSounds.explosion, SoundCategory.PLAYERS, 0.75F,
						1.0F + (world.rand.nextFloat()) * 0.4F);
				break;
				case 4:
					particle = EnumParticleTypes.EXPLOSION_LARGE;
					
					world.playSound(null, pos, ModSounds.explosion, SoundCategory.PLAYERS, 0.75F,
							1.0F + (world.rand.nextFloat()) * 0.4F);
					world.playSound(null, pos, ModSounds.fatality, SoundCategory.PLAYERS, 1.0F,
						1.0F);
					break;
				default:
					particle = EnumParticleTypes.CRIT_MAGIC;
			}
			
			if (target != null && particle != null) {
				worldServer.spawnParticle(particle,
					particle.getShouldIgnoreRange(), target.posX,
					target.getEntityBoundingBox().minY, target.posZ, 7,
					world.rand.nextGaussian(), world.rand.nextGaussian(),
					world.rand.nextGaussian(), 0.0D, new int[0]);
			}
			
			for (int e = 0; e < surrounding.size(); e++) {
				if (surrounding.get(e) instanceof EntityLivingBase &&
					(!((EntityLivingBase) surrounding.get(e)).isOnSameTeam(attacker) &&
					(surrounding.get(e).equals(target) || (attacker instanceof EntityPlayer && !(target instanceof EntityPlayer))))) {
					EntityLivingBase curEntity = ((EntityLivingBase) surrounding.get(e));
					double distance = curEntity.getDistanceToEntity(target);
					curEntity.addPotionEffect(new PotionEffect(!curEntity.isEntityUndead() ? MobEffects.INSTANT_DAMAGE :
						MobEffects.INSTANT_HEALTH, 5,
						(int) Math.floor(power - distance)));
				}
			}
			return true;
		} else {
			world.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.fatality, SoundCategory.PLAYERS, 1.0F,
				0.85F + (world.rand.nextFloat()) * 0.3F, true);
			return false;
		}
    }

    @Override
    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean Adva){
    	
    }
    
    @Override
    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BLOCK;
    }

    @Override
    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
    
    /**
     * Called when the player Left Clicks (attacks) an entity.
     * Processed before damage is done, if return value is true further processing is canceled
     * and the entity is not attacked.
     *
     * @param stack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    @Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }
	
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(),
            	new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -1.8D, 0));
        }

        return multimap;
    }
}
