package goldenage.potatotech.screens;

import goldenage.potatotech.blocks.entities.TileEntitySequencer;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;

import java.util.Collections;
import java.util.List;

public class MenuSequencer extends MenuAbstract {
	private final TileEntitySequencer sequencer;

	public MenuSequencer(ContainerInventory playerInventory, TileEntitySequencer sequencer) {
		this.sequencer = sequencer;

		for (int yi = 0; yi < 3; ++yi) {
			for (int xi = 0; xi < 9; ++xi) {
				this.addSlot(new Slot(playerInventory, xi + yi * 9 + 9, 8 + xi * 18, 84 + yi * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
		if (action == InventoryAction.MOVE_SIMILAR) {
			return this.getSlots(0, 36, false);
		}
		if (slot.index < 27) {
			return this.getSlots(0, 27, false);
		}
		if (slot.index < 36) {
			return this.getSlots(27, 9, false);
		}
		return Collections.emptyList();
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
		if (slot.index < 27) {
			return this.getSlots(27, 9, false);
		}
		if (slot.index < 36) {
			return this.getSlots(0, 27, false);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.sequencer == null || this.sequencer.worldObj == null) {
			return true;
		}
		return this.sequencer.stillValid(player);
	}
}

