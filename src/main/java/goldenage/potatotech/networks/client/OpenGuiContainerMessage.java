package goldenage.potatotech.networks.client;

import goldenage.potatotech.mixins.PlayerServerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.server.entity.player.PlayerServer;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import javax.annotation.Nonnull;

abstract class OpenGuiContainerMessage<A> implements NetworkMessage {
	final protected A container;
	private int windowId = 0;

	public OpenGuiContainerMessage(A container) {
		this.container = container;
	}

	public void sendToPlayer(Player player) {
		if (EnvironmentHelper.isServerEnvironment()) {
			serverSetWindow(player);
		}
		NetworkHandler.sendToPlayer(player, this);
		if (EnvironmentHelper.isServerEnvironment()) {
			this.serverSetWindow2(player);
		}
	}

	@Environment(EnvType.SERVER)
	private void serverSetWindow(Player player) {
		if (player instanceof PlayerServer) {
			((PlayerServerAccessor) player).invokeGetNextWindowId();
			this.windowId = ((PlayerServerAccessor) player).getCurrentWindowId();
		}
	}

	abstract protected MenuAbstract getMenuInstance(ContainerInventory playerInventory, A container);

	@Environment(EnvType.SERVER)
	protected void serverSetWindow2(Player player) {
		if (player instanceof PlayerServer) {
			player.craftingInventory.onCraftGuiClosed(player);
			player.craftingInventory = getMenuInstance(player.inventory, container);
			player.craftingInventory.containerId = this.windowId;
			player.craftingInventory.addSlotListener((PlayerServer) player);
		}
	}

	@Override
	public void encodeToUniversalPacket(@Nonnull UniversalPacket buf) {
		buf.writeInt(windowId);
	}

	@Override
	public void decodeFromUniversalPacket(@Nonnull UniversalPacket buf) {
		windowId = buf.readInt();
	}

	@Environment(EnvType.CLIENT)
	abstract protected Screen getScreenInstance(ContainerInventory playerInventory, A container);

	@Override
	public void handle(NetworkContext context) {
		if (EnvironmentHelper.isSinglePlayer()) {
			doSinglePlayer();
			return;
		}
		if (EnvironmentHelper.isClientWorld()) {
			doClient();
		}
	}

	@Environment(EnvType.CLIENT)
	private void doClient() {
		Minecraft.getMinecraft().displayScreen(getScreenInstance(Minecraft.getMinecraft().thePlayer.inventory, this.container));
		Minecraft.getMinecraft().thePlayer.craftingInventory.containerId = windowId;
	}

	@Environment(EnvType.CLIENT)
	private void doSinglePlayer() {
		Minecraft.getMinecraft().displayScreen(getScreenInstance(Minecraft.getMinecraft().thePlayer.inventory, this.container));
	}
}
