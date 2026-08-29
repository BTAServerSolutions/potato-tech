package goldenage.potatotech.mixins;

import com.mojang.nbt.tags.CompoundTag;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sunsetsatellite.catalyst.core.util.Direction;
import sunsetsatellite.catalyst.energy.simple.api.IEnergyContainer;
import sunsetsatellite.catalyst.energy.simple.impl.TileEntityEnergyConductor;
import sunsetsatellite.catalyst.core.util.network.Network;
import sunsetsatellite.catalyst.core.util.network.NetworkComponentTile;
import sunsetsatellite.catalyst.core.util.network.NetworkManager;
import sunsetsatellite.catalyst.core.util.network.NetworkPath;
import sunsetsatellite.catalyst.core.util.network.NetworkType;
import sunsetsatellite.catalyst.core.util.vector.Vec3i;

@Mixin(value = TileEntityEnergyConnector.class, remap = false)
public class CatalystEnergyConnectorMixin implements IEnergyContainer, NetworkComponentTile {
	@Shadow public int energy;
	@Unique private long potatotech$catalystRemainder;

	@Override
	public boolean canReceive(@NotNull Direction dir) {
		return true;
	}

	@Override
	public boolean canProvide(@NotNull Direction dir) {
		return getEnergy() > 0;
	}

	@Override
	public long getEnergy() {
		return (long) energy * potatotech$getMultiplier() + potatotech$catalystRemainder;
	}

	@Override
	public long getCapacity() {
		return (long) TileEntityEnergyConnector.energyCapacity * potatotech$getMultiplier();
	}

	@Override
	public long getMaxReceive() {
		return getCapacity();
	}

	@Override
	public long getMaxProvide() {
		return getCapacity();
	}

	@Override
	public long internalChangeEnergy(long difference) {
		long previous = getEnergy();
		long total = Math.max(0, Math.min(getCapacity(), previous + difference));
		energy = (int) (total / potatotech$getMultiplier());
		potatotech$catalystRemainder = total % potatotech$getMultiplier();
		return total - previous;
	}

	@Override
	public long receiveEnergy(@NotNull Direction dir, long amount) {
		if (!canReceive(dir) || amount <= 0) {
			return 0;
		}
		long accepted = Math.min(amount, getCapacity() - getEnergy());
		internalChangeEnergy(accepted);
		return accepted;
	}

	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}

	@Override
	public Vec3i getPosition() {
		return new Vec3i(potatotech$self().tilePos);
	}

	@Override
	public boolean isConnected(Direction direction) {
		return false;
	}

	@Override
	public void networkChanged(Network network) {
	}

	@Override
	public void removedFromNetwork(Network network) {
	}

	@Unique
	private int potatotech$getMultiplier() {
		return Math.max(1, PotatoTech.config.getInt("catalyst_energy_multiplier"));
	}

	@Inject(method = "readAdditionalData", at = @At("TAIL"))
	private void potatotech$readCatalystData(CompoundTag tag, CallbackInfo ci) {
		potatotech$catalystRemainder = tag.getLong("catalystEnergyRemainder");
	}

	@Inject(method = "writeAdditionalData", at = @At("TAIL"))
	private void potatotech$writeCatalystData(CompoundTag tag, CallbackInfo ci) {
		 tag.putLong("catalystEnergyRemainder", potatotech$catalystRemainder);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void potatotech$transferCatalystEnergy(CallbackInfo ci) {
		TileEntity self = potatotech$self();
		World world = self.worldObj;
		if (world == null || world.isClientSide) {
			return;
		}
		Network network = NetworkManager.getNet(world, getPosition());
		if (network == null) {
			return;
		}
		for (Direction direction : Direction.values()) {
			TileEntity adjacent = direction.getTileEntity(world, self);
			if (!(adjacent instanceof TileEntityEnergyConductor conductor)) {
				continue;
			}
			for (NetworkPath path : network.getPathData(conductor.getPosition())) {
			if (path.target == (Object) this || !(path.target instanceof IEnergyContainer destination)) {
				continue;
			}
			long throughput = Long.MAX_VALUE;
			for (NetworkComponentTile component : path.path) {
				if (component instanceof TileEntityEnergyConductor pathConductor) {
					throughput = Math.min(throughput, pathConductor.getMaxThroughput());
				}
			}
			if (destination.canProvide(path.targetDirection)) {
				long received = Math.min(Math.min(destination.getEnergy(), destination.getMaxProvide()), Math.min(getMaxReceive(), getCapacity() - getEnergy()));
				received = Math.min(received, throughput);
				if (received > 0) {
					destination.internalChangeEnergy(-received);
					internalChangeEnergy(received);
				}
			}
			if (getEnergy() > 0 && destination.canReceive(path.targetDirection)) {
				long sent = Math.min(Math.min(getEnergy(), getMaxProvide()), Math.min(destination.getMaxReceive(), destination.getCapacityRemaining()));
				sent = Math.min(sent, throughput);
				if (sent > 0) {
					internalChangeEnergy(-sent);
					destination.internalChangeEnergy(sent);
				}
			}
		}
		}
		self.setChanged();
	}

	@Unique
	private TileEntity potatotech$self() {
		return (TileEntity) (Object) this;
	}
}
