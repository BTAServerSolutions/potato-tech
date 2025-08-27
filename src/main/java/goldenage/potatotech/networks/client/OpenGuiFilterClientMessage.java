package goldenage.potatotech.networks.client;

import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.screens.MenuFilter;
import goldenage.potatotech.screens.ScreenFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class OpenGuiFilterClientMessage extends OpenGuiContainerMessage<TileEntityFilter> {
	public OpenGuiFilterClientMessage(TileEntityFilter container) {
		super(container);
	}

	public OpenGuiFilterClientMessage() {
		super(new TileEntityFilter());
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected Screen getScreenInstance(ContainerInventory playerInventory, TileEntityFilter container) {
		return new ScreenFilter(playerInventory, container);
	}

	@Override
	protected MenuAbstract getMenuInstance(Container playerInventory, TileEntityFilter container) {
		return new MenuFilter(playerInventory, container);
	}
}
