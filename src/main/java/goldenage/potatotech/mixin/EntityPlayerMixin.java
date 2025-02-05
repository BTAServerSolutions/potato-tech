package goldenage.potatotech.mixin;

import goldenage.potatotech.IPotatoGui;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EntityPlayer.class,remap = false)
public abstract class EntityPlayerMixin implements IPotatoGui {

	public void displayBlockFilterGui(TileEntity tileEntity)  {
	}

	public void diplayBlockCrusherGui(IInventory tileInventory) {
	}

	public void diplayBlockCrafterGui(IInventory tileInventory) {
	}
}
