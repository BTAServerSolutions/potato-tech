package goldenage.potatotech.screens;

import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class ScreenCrafter extends ScreenContainerAbstract {

    public ScreenCrafter(ContainerInventory playerInventory, TileEntityCrafter crafter) {
        super(new MenuCrafter(playerInventory, crafter));

        this.ySize = 191;
    }

    @Override
    public void removed() {
        super.removed();
        this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
    }


    @Override
    protected void drawGuiContainerForegroundLayer() {
        this.font.drawString("Auto Crafting", 28, 6, 0x404040);
        this.font.drawString("Inventory", 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f) {
        @NotNull Texture i = this.mc.textureManager.loadTexture("/assets/potatotech/textures/gui/crafter.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.textureManager.bindTexture(i);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
    }

}
