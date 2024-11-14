package goldenage.potatotech.gui;

import net.minecraft.client.gui.GuiContainer;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class GuiBlockCrusher extends GuiContainer {

	public GuiBlockCrusher(InventoryPlayer inventoryplayer, IInventory tileentity) {
		super(new ContainerBlockCrusher(inventoryplayer, tileentity));
	}

	protected void drawGuiContainerForegroundLayer() {
		//this.fontRenderer.drawString(I18n.getInstance().translateKey("gui.dispenser.label.dispenser"), 60, 6, 4210752);
		//this.fontRenderer.drawString(I18n.getInstance().translateKey("gui.dispenser.label.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float f) {
		int i = this.mc.renderEngine.getTexture("/assets/minecraft/textures/gui/trap.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
	}
}
