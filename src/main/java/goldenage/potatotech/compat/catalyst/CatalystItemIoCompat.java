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

	public static boolean canAccessSide(TileEntity tileEntity, Direction direction, boolean insertion) {
		IItemIO itemIo = getItemIo(tileEntity);
		if (itemIo == null) {
			return false;
		}
		Connection connection = itemIo.getItemIOForSide(toCatalystDirection(direction.opposite()));
		return insertion
			? connection == Connection.INPUT || connection == Connection.BOTH
			: connection == Connection.OUTPUT || connection == Connection.BOTH;
	}

	public static boolean canInsert(TileEntity tileEntity, Direction direction, ItemStack stack) {
		return Util.getContainerSlotInfo(tileEntity, direction, stack, (short)0).freeCapacity() > 0;
	}

	public static boolean insert(TileEntity tileEntity, Direction direction, PipeStack pipeStack) {
		return Util.insertPipeStackOnInventory(tileEntity, pipeStack, direction);
	}

	public static PipeStack extract(TileEntity tileEntity, Direction direction, int stackTimer, int count) {
		return Util.getItemFromInventoryNoCatch(
			tileEntity.worldObj,
			tileEntity.tilePos.x,
			tileEntity.tilePos.y,
			tileEntity.tilePos.z,
			direction,
			stackTimer,
			count,
			(short)0
		);
	}

	public static int getActiveSlot(TileEntity tileEntity, Direction direction, ItemStack stackToInsert) {
		int slot = -1;
		IItemIO itemIo = getItemIo(tileEntity);

		if (itemIo != null) {
			sunsetsatellite.catalyst.core.util.Direction targetSide = toCatalystDirection(direction.opposite());
			Connection connection = itemIo.getItemIOForSide(targetSide);

			if (stackToInsert != null && (connection == Connection.INPUT || connection == Connection.BOTH)) {
				slot = itemIo.getActiveItemSlotForSide(targetSide, stackToInsert);
			} else if (stackToInsert == null && (connection == Connection.OUTPUT || connection == Connection.BOTH)) {
				slot = itemIo.getActiveItemSlotForSide(targetSide);
			}
		}

		return slot;
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
