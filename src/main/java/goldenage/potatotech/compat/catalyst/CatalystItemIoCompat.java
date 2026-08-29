package goldenage.potatotech.compat.catalyst;

import goldenage.potatotech.PipeStack;
import goldenage.potatotech.Util;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import sunsetsatellite.catalyst.core.util.Connection;
import sunsetsatellite.catalyst.core.util.io.IItemIO;

public final class CatalystItemIoCompat {
	private CatalystItemIoCompat() {
	}

	public static boolean isItemIo(TileEntity tileEntity) {
		return tileEntity instanceof Container && getItemIo(tileEntity) != null;
	}

	public static boolean hasConfiguredSide(TileEntity tileEntity, Direction direction) {
		IItemIO itemIo = getItemIo(tileEntity);
		if (itemIo == null) {
			return false;
		}
		return itemIo.getItemIOForSide(toCatalystDirection(direction.opposite())) != Connection.NONE;
	}

	public static boolean canInsert(TileEntity tileEntity, Direction direction, ItemStack stack) {
		if (!isItemIo(tileEntity)) {
			return false;
		}
		IItemIO itemIo = getItemIo(tileEntity);
		Connection connection = itemIo.getItemIOForSide(toCatalystDirection(direction.opposite()));
		if (connection != Connection.INPUT && connection != Connection.BOTH) {
			return false;
		}
		Container container = (Container) tileEntity;
		int slot = itemIo.getActiveItemSlotForSide(toCatalystDirection(direction.opposite()), stack);
		if (slot < 0 || slot >= container.getContainerSize()) {
			return false;
		}
		ItemStack current = container.getItem(slot);
		return current == null || (current.canStackWith(stack) && current.stackSize < Math.min(container.getMaxStackSize(), current.getMaxStackSize()));
	}

	public static boolean insert(TileEntity tileEntity, Direction direction, ItemStack stack) {
		if (!canInsert(tileEntity, direction, stack)) {
			return false;
		}
		IItemIO itemIo = getItemIo(tileEntity);
		Container container = (Container) tileEntity;
		int slot = itemIo.getActiveItemSlotForSide(toCatalystDirection(direction.opposite()), stack);
		ItemStack current = container.getItem(slot);
		if (current == null) {
			container.setItem(slot, stack);
		} else {
			current.stackSize += stack.stackSize;
			container.setItem(slot, current);
		}
		return true;
	}

	public static PipeStack extract(TileEntity tileEntity, Direction direction, int stackTimer) {
		if (!isItemIo(tileEntity)) {
			return null;
		}
		IItemIO itemIo = getItemIo(tileEntity);
		sunsetsatellite.catalyst.core.util.Direction targetSide = toCatalystDirection(direction.opposite());
		Connection connection = itemIo.getItemIOForSide(targetSide);
		if (connection != Connection.OUTPUT && connection != Connection.BOTH) {
			return null;
		}
		Container container = (Container) tileEntity;
		int slot = itemIo.getActiveItemSlotForSide(targetSide);
		if (slot < 0 || slot >= container.getContainerSize()) {
			return null;
		}
		ItemStack stack = container.getItem(slot);
		if (stack == null) {
			return null;
		}
		ItemStack extracted = Util.removeItemFromStack(stack);
		container.setItem(slot, stack.stackSize > 0 ? stack : null);
		return new PipeStack(extracted, direction, stackTimer);
	}

	private static sunsetsatellite.catalyst.core.util.Direction toCatalystDirection(Direction direction) {
		return sunsetsatellite.catalyst.core.util.Direction.getDirectionFromSide(direction.id);
	}

	private static IItemIO getItemIo(TileEntity tileEntity) {
		if (tileEntity instanceof IItemIO itemIo) {
			return itemIo;
		}
		return tileEntity != null && tileEntity.getBlock() != null && tileEntity.getBlock().getLogic() instanceof IItemIO itemIo ? itemIo : null;
	}
}
