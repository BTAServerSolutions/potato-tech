package goldenage.potatotech.mixin;


import goldenage.potatotech.IPotatoGui;
import goldenage.potatotech.gui.GuiBlockCrusher;
import goldenage.potatotech.gui.GuiFilter;
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
		this.mc.displayGuiScreen(new GuiFilter(this.inventory, tileInventory));
	}


	public void diplayBlockCrusherGui(IInventory tileInventory) {
		this.mc.displayGuiScreen(new GuiBlockCrusher(this.inventory, tileInventory));
	}
}
