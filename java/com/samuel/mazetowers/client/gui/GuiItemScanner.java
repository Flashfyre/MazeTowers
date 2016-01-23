package com.samuel.mazetowers.client.gui;

import java.io.IOException;

import com.samuel.mazetowers.MazeTowers;
import com.samuel.mazetowers.blocks.BlockItemScanner;
import com.samuel.mazetowers.blocks.BlockItemScannerGold;
import com.samuel.mazetowers.etc.ContainerItemScanner;
import com.samuel.mazetowers.packets.PacketActivateItemScanner;
import com.samuel.mazetowers.tileentities.TileEntityItemScanner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemScanner extends GuiContainer
{
    /** The ResourceLocation containing the chest GUI texture. */
	private static final ResourceLocation GUI_TEXTURE_NORMAL =
	    	new ResourceLocation("mazetowers:textures/gui/container/item_scanner.png");
	private static final ResourceLocation GUI_TEXTURE_GOLD =
	    	new ResourceLocation("mazetowers:textures/gui/container/item_scanner_gold.png");
    private final boolean isGold;
	private IInventory playerInv;
    private IInventory scannerInv;
    /** window height is calculated with these values; the more rows, the higher */
    private int inventoryRows;

    public GuiItemScanner(IInventory playerInv, IInventory itemScanner)
    {
        super(new ContainerItemScanner(playerInv, itemScanner,
        	Minecraft.getMinecraft().thePlayer));
        this.playerInv = playerInv;
        this.scannerInv = itemScanner;
        this.allowUserInput = false;
        int i = 222;
        int j = i - 108;
        this.inventoryRows = scannerInv.getSizeInventory() / 9;
        this.xSize += 32;
        this.ySize = j + this.inventoryRows * 18;
        TileEntityItemScanner te = (TileEntityItemScanner) itemScanner;
        isGold = (te.getWorld().getBlockState(te.getPos()).getBlock()
        	instanceof BlockItemScannerGold);
    }
    
    @Override
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 49, this.guiTop - 24, 98, 20, "Activate"));
    }

    @Override
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    	this.fontRendererObj.drawString(scannerInv.getDisplayName().getUnformattedText(), 8, 6, 4210752);
    	this.fontRendererObj.drawString("Key", 184, 6, 4210752);
        this.fontRendererObj.drawString(playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(!isGold ? GUI_TEXTURE_NORMAL : GUI_TEXTURE_GOLD);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
    
    @Override
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
        	TileEntityItemScanner te = ((TileEntityItemScanner) this.scannerInv);
            ((BlockItemScanner) MazeTowers.BlockItemScanner)
            .setStateBasedOnMatchResult(te.getWorld(),
            	te.getPos(), te.getWorld().getBlockState(te.getPos()), true);
            MazeTowers.network.sendToServer(
            	new PacketActivateItemScanner(String.valueOf(te.getPos().toLong())));
        }
    }
}