package goldenage.potatotech.gui;

import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.Container;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.player.inventory.InventoryPlayer;
import net.minecraft.core.player.inventory.slot.Slot;

import java.util.List;

public class ContainerFilter extends Container {
    private final IInventory inventory;
    private final int numberOfRows;

    public ContainerFilter(InventoryPlayer playerInventory, IInventory inventory, IInventory inventory2) {
        this.inventory = inventory;
        numberOfRows = 1;

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 23));
        }

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventory2, i, 8 + i * 18, 52));
		}

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
    public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int i, EntityPlayer entityPlayer) {
        if (slot.id < 9){
            return getSlots(0, 9, false);
        }
        if (slot.id < 36){
            return getSlots(9, 27, false);
        }
        return getSlots(36, 9, false);
    }

    @Override
    public List<Integer> getTargetSlots(InventoryAction inventoryAction, Slot slot, int i, EntityPlayer entityPlayer) {
        int filterSize = this.numberOfRows * 9;
        if (slot.id < filterSize) { // Filter -> Inventory
            List<Integer> listOut = getSlots(filterSize + 27,9, false); // Hotbar first
            listOut.addAll(getSlots(filterSize,27, false)); // Then Inventory
            return listOut;
        }
        return this.getSlots(0, filterSize, false); // Inventory -> Filter
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
        return this.inventory.canInteractWith(entityPlayer);
    }
}
