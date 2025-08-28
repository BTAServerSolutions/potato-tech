package goldenage.potatotech.networks.client;

import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import goldenage.potatotech.screens.MenuCrafter;
import goldenage.potatotech.screens.ScreenCrafter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;

public class OpenGuiCrafterClientMessage extends OpenGuiContainerMessage<TileEntityCrafter> {
	public OpenGuiCrafterClientMessage(TileEntityCrafter container) {
		super(container);
	}

	public OpenGuiCrafterClientMessage() {
		super(new TileEntityCrafter());
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected Screen getScreenInstance(ContainerInventory playerInventory, TileEntityCrafter container) {
		return new ScreenCrafter(playerInventory, container);
	}

	@Override
	protected MenuAbstract getMenuInstance(ContainerInventory playerInventory, TileEntityCrafter container) {
		return new MenuCrafter(playerInventory, container);
	}
}
