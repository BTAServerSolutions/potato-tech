package goldenage.potatotech;

import net.minecraft.core.player.inventory.IInventory;

public interface IPotatoGui {
	void diplayBlockFilterGui(IInventory tileInventory);
	void diplayBlockCrusherGui(IInventory tileInventory);
	void diplayBlockCrafterGui(IInventory tileInventory);
}
