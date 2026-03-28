package goldenage.potatotech.screens;

import goldenage.potatotech.blocks.entities.TileEntitySequencer;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.lwjgl.opengl.GL11;

public class ScreenSequencer extends ScreenContainerAbstract {
	public ScreenSequencer(ContainerInventory playerInventory, TileEntitySequencer sequencer) {
		super(new MenuSequencer(playerInventory, sequencer));
		this.ySize = 165;
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		this.font.drawString(I18n.getInstance().translateKey("container.sequencer.name"), 8, 4, 0x404040);
		this.font.drawString("Inventory", 8, this.ySize - 90, 0x404040);
	}

	@Override
	public void render(int mx, int my, float partialTick) {
		super.render(mx, my, partialTick);

		/*
		Texture texture = this.mc.textureManager.loadTexture("/assets/potatotech/textures/gui/sequencer.png");
		this.mc.textureManager.bindTexture(texture);
		int buttonX = 176;
		int buttonY = 186;
		this.drawTexturedModalRect(mx, my, buttonX, 0, 10, 10);
		 */
	}

	@Override
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my) {
		super.keyPressed(eventCharacter, eventKey, mx, my);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f) {
		Texture texture = this.mc.textureManager.loadTexture("/assets/potatotech/textures/gui/sequencer.png");
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.textureManager.bindTexture(texture);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}
}

