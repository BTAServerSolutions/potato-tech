package goldenage.potatotech.items;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemWireSpool extends Item {
	private String displayName;

	public ItemWireSpool(String translationKey, @NotNull NamespaceID namespaceId, int id) {
		super(translationKey, String.valueOf(namespaceId), id);
	}

	@Override
	public boolean onUseOnBlock(@NotNull ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
		if (world.isClientSide) {
			return true;
		}

		this.displayName = getStatName();
		int blockX = blockPos.x();
		int blockY = blockPos.y();
		int blockZ = blockPos.z();

		Block block = world.getBlock(blockX, blockY, blockZ);
		if (block != null && block.id() == PTBlocks.energyConnector.id()) {
			boolean connected = selfStack.getData().getBoolean("connected");
			if (connected) {
				TileEntity te = world.getTileEntity(blockX, blockY, blockZ);
				if (te instanceof TileEntityEnergyConnector) {
					selfStack.getData().putBoolean("connected", false);
					int x = selfStack.getData().getInteger("x");
					int y = selfStack.getData().getInteger("y");
					int z = selfStack.getData().getInteger("z");
					boolean connectedSuccessfully = ((TileEntityEnergyConnector) te).addConnection(x, y, z);
					if (connectedSuccessfully) {
						selfStack.consumeItem(player);
					}
					removedConnectionData(selfStack);
				}
			} else {
				selfStack.getData().putBoolean("connected", true);
				selfStack.getData().putInt("x", blockX);
				selfStack.getData().putInt("y", blockY);
				selfStack.getData().putInt("z", blockZ);
				selfStack.setCustomName(this.displayName + " Connected to: " + blockX + " " + blockY + " " + blockZ);
			}

			return true;
		} else if (player.isSneaking()) {
			selfStack.getData().putBoolean("connected", false);
			PotatoTech.LOGGER.info("Clear spool connection");
			selfStack.setCustomName(this.displayName);
			return true;
		}

		return false;
	}

	private void removedConnectionData(ItemStack stack) {
		stack.getData().getValue().remove("connected");
		stack.getData().getValue().remove("x");
		stack.getData().getValue().remove("y");
		stack.getData().getValue().remove("z");
		stack.removeCustomName();
	}
}
