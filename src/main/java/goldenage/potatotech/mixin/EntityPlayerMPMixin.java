package goldenage.potatotech.mixin;

import goldenage.potatotech.IPotatoGui;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.InvocationTargetException;


@Mixin(value = EntityPlayerMP.class,remap = false)
public abstract class EntityPlayerMPMixin extends EntityPlayer implements IPotatoGui {
	private EntityPlayerMPMixin(World world) {
		super(world);
	}

	@Shadow
	protected abstract void getNextWindowId();

	@Shadow
	private int currentWindowId;
	@Shadow
	public NetServerHandler playerNetServerHandler;
	@Unique
	private final EntityPlayerMP thisAs = (EntityPlayerMP)(Object)this;

	//TODO: change display methods to have xyz argument and stack argument

	public void diplayBlockFilterGui(TileEntity tileEntity, String id) {
	}
}
