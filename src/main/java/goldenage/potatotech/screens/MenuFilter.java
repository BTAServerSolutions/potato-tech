package goldenage.potatotech.screens;

import goldenage.potatotech.blocks.entities.TileEntityFilter;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.menu.MenuContainer;
import net.minecraft.core.player.inventory.slot.Slot;

import java.util.List;

public class MenuFilter extends MenuAbstract {

	private final TileEntityFilter filter;
	private final int numberOfRows;

	public MenuFilter(Container playerContainer, TileEntityFilter filter) {
		this.filter = filter;
		numberOfRows = 1;

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(filter, i, 8 + i * 18, 23));
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(filter.paintInventory, i, 8 + i * 18, 52));
		}

		for (int yi = 0; yi < 3; ++yi) {
			for (int xi = 0; xi < 9; ++xi) {
				this.addSlot(new Slot(playerContainer, xi + yi * 9 + 9, 8 + xi * 18, 84 + yi * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerContainer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public List<Integer> getMoveSlots(InventoryAction inventoryAction, Slot slot, int i, Player player) {
		if (slot.index < 9){
			return getSlots(0, 9, false);
		}
		if (slot.index < 36){
			return getSlots(9, 27, false);
		}
		return getSlots(36, 9, false);
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction inventoryAction, Slot slot, int i, Player player) {
		int filterSize = this.numberOfRows * 9;
		if (slot.index < filterSize) { // Filter -> Inventory
			List<Integer> listOut = getSlots(filterSize + 27,9, false); // Hotbar first
			listOut.addAll(getSlots(filterSize,27, false)); // Then Inventory
			return listOut;
		}
		return this.getSlots(0, filterSize, false); // Inventory -> Filter
	}

	@Override
	public boolean stillValid(Player player) {
		return this.filter.stillValid(player);
	}

}
