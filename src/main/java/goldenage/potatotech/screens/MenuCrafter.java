package goldenage.potatotech.screens;

import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;

import java.util.List;

public class MenuCrafter extends MenuAbstract {
	public static class SlotCrafterOut extends Slot {
		public SlotCrafterOut(Container inventory, int id, int x, int y) {
			super(inventory, id, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack itemstack) {
			return false;
		}
	}

	public static class SlotNoInteract extends Slot {
		public SlotNoInteract(Container inventory, int id, int x, int y) {
			super(inventory, id, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack itemstack) {
			return hasItem() && getItemStack().canStackWith(itemstack);
		}
	}

	public MenuCrafter(ContainerInventory playerInventory, TileEntityCrafter tile) {
		if (playerInventory == null) {
			return;
		}

		this.addSlot(new SlotCrafterOut(tile.craftResult, 0, 124, 35));

		this.addSlot(new SlotNoInteract(tile.extraOutputs, 0, 151, 35));

		for (int yi = 0; yi < 3; ++yi) {
			for (int xi = 0; xi < 3; ++xi) {
				this.addSlot(new Slot(tile.pattern, xi + yi * 3, 30 + xi * 18, 17 + yi * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new SlotNoInteract(tile.craftMatrix, i, 8 + i * 18, 81));
		}

		for (int yi = 0; yi < 3; ++yi) {
			for (int xi = 0; xi < 9; ++xi) {
				this.addSlot(new Slot(playerInventory, xi + yi * 9 + 9, 8 + xi * 18, 109 + yi * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 167));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player player) {
		if (slot.index == 0) {
			return this.getSlots(0, 1, false);
		}
		if (slot.index >= 1 && slot.index < 9) {
			return this.getSlots(1, 9, false);
		}
		if (action == InventoryAction.MOVE_SIMILAR) {
			if (slot.index >= 10 && slot.index <= 45) {
				return this.getSlots(10, 36, false);
			}
		} else {
			if (slot.index >= 10 && slot.index <= 36) {
				return this.getSlots(10, 27, false);
			}
			if (slot.index >= 37 && slot.index <= 45) {
				return this.getSlots(37, 9, false);
			}
		}
		return null;
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player player) {
		if (slot.index >= 10 && slot.index <= 45) {
			if (target == 1) {
				return this.getSlots(1, 9, false);
			}
			if (slot.index <= 36) {
				return this.getSlots(37, 9, false);
			}
			return this.getSlots(10, 27, false);
		} else {
			if (slot.index == 0) {
				return this.getSlots(10, 36, true);
			}
			return this.getSlots(10, 36, false);
		}
	}
}
