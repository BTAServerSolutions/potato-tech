package goldenage.potatotech.mixin;

import goldenage.potatotech.IPotatoGui;
import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.gui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(value = EntityPlayerSP.class,remap = false)
public abstract class EntityPlayerSPMixin extends EntityPlayer implements IPotatoGui {
	@Shadow
	protected Minecraft mc;

	public EntityPlayerSPMixin(World world) {
		super(world);
	}

	public void diplayBlockFilterGui(IInventory tileInventory) {
		this.mc.displayGuiScreen(new GuiFilter(this.inventory, tileInventory, ((TileEntityFilter)tileInventory).paintInventory));
	}

	public void diplayBlockCrusherGui(IInventory tileInventory) {
		this.mc.displayGuiScreen(new GuiBlockCrusher(this.inventory, tileInventory));
	}

	public void diplayBlockCrafterGui(IInventory tileInventory) {
		this.mc.displayGuiScreen(new GuiCrafter(this.inventory, tileInventory));
	}
}
