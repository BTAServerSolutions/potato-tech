package goldenage.potatotech.networks.client;

import goldenage.potatotech.mixins.PlayerServerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.server.entity.player.PlayerServer;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;


abstract class OpenGuiContainerMessage<A> implements NetworkMessage {
	final protected A container;
	private int windowId = 0;
	private int x;
	private int y;
	private int z;

	public OpenGuiContainerMessage(A container) {
		this.container = container;
		if (container instanceof TileEntity) {
			TileEntity tileEntity = (TileEntity) container;
			x = tileEntity.tilePos.x;
			y = tileEntity.tilePos.y;
			z = tileEntity.tilePos.z;
		}
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
			player.containerMenu.onCraftGuiClosed(player);
			MenuAbstract menu = getMenuInstance(player.inventory, container);
			menu.containerId = this.windowId;
			menu.addSlotListener((PlayerServer) player);
			player.containerMenu = menu;
		}
	}

	@Override
	public void encodeToUniversalPacket(UniversalPacket buf) {
		buf.writeInt(windowId);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public void decodeFromUniversalPacket(UniversalPacket buf) {
		windowId = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
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
		TileEntity tileEntity = Minecraft.getMinecraft().thePlayer.world.getTileEntity(new TilePos(x, y, z));
		if (tileEntity != null && container.getClass().isInstance(tileEntity)) {
			Screen screen = getScreenInstance(Minecraft.getMinecraft().thePlayer.inventory, (A) tileEntity);
			if (screen instanceof ScreenContainerAbstract) {
				MenuAbstract menu = ((ScreenContainerAbstract) screen).inventorySlots;
				menu.containerId = windowId;
				Minecraft.getMinecraft().thePlayer.containerMenu = menu;
			}
			Minecraft.getMinecraft().displayScreen(screen);
		}
	}

	@Environment(EnvType.CLIENT)
	private void doSinglePlayer() {
		Minecraft.getMinecraft().displayScreen(getScreenInstance(Minecraft.getMinecraft().thePlayer.inventory, this.container));
	}
}
