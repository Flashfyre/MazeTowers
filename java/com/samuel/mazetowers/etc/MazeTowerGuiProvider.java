package com.samuel.mazetowers.etc;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class MazeTowerGuiProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {

	@CapabilityInject(IMazeTowerCapability.class)
	public static Capability<IMazeTowerCapability> gui = null;
	
	private IMazeTowerCapability mtc = null;
	
	public MazeTowerGuiProvider() { }
	
	public MazeTowerGuiProvider(IMazeTowerCapability mtc){
		this.mtc = mtc;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return gui != null && capability == gui;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (gui != null && capability == gui)
			return (T) mtc;
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return (NBTTagCompound) gui.getStorage().writeNBT(gui, mtc, null);
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		gui.getStorage().readNBT(gui, mtc, null, nbt);
	}
}
