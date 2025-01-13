package goldenage.potatotech.gui;

import net.minecraft.client.gui.GuiContainer;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiFilter extends GuiContainer {
    private final IInventory filterInventory;
    private final int inventoryRows;

    public GuiFilter(InventoryPlayer playerInventory, IInventory iinventory, IInventory inventory2) {
        super(new ContainerFilter(playerInventory, iinventory, inventory2));
        this.filterInventory = iinventory;
        this.allowIngameInput = false;
        this.inventoryRows = 1;
		this.ySize = 191;
    }

    @Override
    protected void drawGuiContainerForegroundLayer() {
        this.fontRenderer.drawString(this.filterInventory.getInvName(), 8, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f) {
        int i = this.mc.renderEngine.getTexture("/assets/potatotech/textures/gui/filter.png");
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(i);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
    }
}
