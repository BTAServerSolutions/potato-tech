package goldenage.potatotech.gui;

import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import net.minecraft.client.gui.GuiCrafting;
import net.minecraft.client.gui.GuiContainer;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.ContainerWorkbench;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;

public class GuiCrafter extends GuiContainer {
    public GuiCrafter(InventoryPlayer inventoryPlayer, IInventory tile) {
        super(new ContainerCrafter(inventoryPlayer, (TileEntityCrafter) tile));
        this.ySize = 191;
    }

    @Override
    public void onClosed() {
        super.onClosed();
        this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
    }

    @Override
    protected void drawGuiContainerForegroundLayer() {
        this.fontRenderer.drawString("Auto Crafting", 28, 6, 0x404040);
        this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f) {
        int i = this.mc.renderEngine.getTexture("/assets/potatotech/textures/gui/crafter.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(i);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
    }
}