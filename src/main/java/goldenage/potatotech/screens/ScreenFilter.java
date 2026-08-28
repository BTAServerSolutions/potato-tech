package goldenage.potatotech.screens;

import goldenage.potatotech.blocks.entities.TileEntityFilter;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicWorkbench;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.Container;

public class ScreenFilter extends ScreenContainerAbstract {
	private final Container filterInventory;
	private final int inventoryRows;

	public ScreenFilter(Container playerInventory, TileEntityFilter filter) {
		super(new MenuFilter(playerInventory, filter));

		this.filterInventory = filter;
		this.inventoryRows = 1;
		this.ySize = 191;
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		this.drawStringNoShadow(this.fontRenderer, I18n.getInstance().translateKey(this.filterInventory.getNameTranslationKey()), 8, 6, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f) {
		Texture i = this.mc.textureManager.loadTexture("/assets/potatotech/textures/gui/filter.png");
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.textureManager.bindTexture(i);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);
	}
}
