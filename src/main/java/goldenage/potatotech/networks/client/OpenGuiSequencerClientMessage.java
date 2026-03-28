package goldenage.potatotech.networks.client;

import goldenage.potatotech.blocks.entities.TileEntitySequencer;
import goldenage.potatotech.screens.MenuSequencer;
import goldenage.potatotech.screens.ScreenSequencer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class OpenGuiSequencerClientMessage extends OpenGuiContainerMessage<TileEntitySequencer> {
	public OpenGuiSequencerClientMessage(TileEntitySequencer container) {
		super(container);
	}

	public OpenGuiSequencerClientMessage() {
		super(new TileEntitySequencer());
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected Screen getScreenInstance(ContainerInventory playerInventory, TileEntitySequencer container) {
		return new ScreenSequencer(playerInventory, container);
	}

	@Override
	protected MenuAbstract getMenuInstance(ContainerInventory playerInventory, TileEntitySequencer container) {
		return new MenuSequencer(playerInventory, container);
	}
}

