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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

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
		return (long) potatotech$connector().getEnergyCapacity() * potatotech$getMultiplier();
	}

	@Override
	public long getMaxReceive() {
		return (long) potatotech$connector().getRemainingBlockTransfer() * potatotech$getMultiplier();
	}

	@Override
	public long getMaxProvide() {
		return (long) potatotech$connector().getRemainingBlockTransfer() * potatotech$getMultiplier();
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
		TileEntityEnergyConnector connector = potatotech$connector();
		if (!canReceive(dir) || amount <= 0 || !connector.canTransferWithBlock(true)) {
			return 0;
		}
		long accepted = Math.min(amount, Math.min(getCapacity() - getEnergy(), getMaxReceive()));
		internalChangeEnergy(accepted);
		long multiplier = potatotech$getMultiplier();
		connector.recordExternalBlockTransfer(true, (int) ((accepted + multiplier - 1) / multiplier));
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
		TileEntityEnergyConnector connector = potatotech$connector();
		Set<IEnergyContainer> visited = Collections.newSetFromMap(new IdentityHashMap<>());
		boolean changed = false;
		for (Direction direction : Direction.values()) {
			TileEntity adjacent = direction.getTileEntity(world, self);
			if (!(adjacent instanceof TileEntityEnergyConductor conductor)) {
				continue;
			}
			for (NetworkPath path : network.getPathData(conductor.getPosition())) {
			if (path.target == (Object) this || !(path.target instanceof IEnergyContainer destination) || !visited.add(destination)) {
				continue;
			}
			long throughput = Long.MAX_VALUE;
			for (NetworkComponentTile component : path.path) {
				if (component instanceof TileEntityEnergyConductor pathConductor) {
					throughput = Math.min(throughput, pathConductor.getMaxThroughput());
				}
			}
			if (connector.canTransferWithBlock(true) && destination.canProvide(path.targetDirection)) {
				long received = Math.min(Math.min(destination.getEnergy(), destination.getMaxProvide()), Math.min(getMaxReceive(), getCapacity() - getEnergy()));
				received = Math.min(received, throughput);
				if (received > 0) {
					destination.internalChangeEnergy(-received);
					internalChangeEnergy(received);
					connector.recordExternalBlockTransfer(true, potatotech$toPE(received));
					changed = true;
				}
			}
			if (connector.canTransferWithBlock(false) && getEnergy() > 0 && destination.canReceive(path.targetDirection)) {
				long sent = Math.min(Math.min(getEnergy(), getMaxProvide()), Math.min(destination.getMaxReceive(), destination.getCapacityRemaining()));
				sent = Math.min(sent, throughput);
				if (sent > 0) {
					internalChangeEnergy(-sent);
					destination.internalChangeEnergy(sent);
					connector.recordExternalBlockTransfer(false, potatotech$toPE(sent));
					changed = true;
				}
			}
		}
		}
		if (changed) {
			self.setChanged();
		}
	}

	@Unique
	private int potatotech$toPE(long catalystEnergy) {
		long multiplier = potatotech$getMultiplier();
		return (int) ((catalystEnergy + multiplier - 1) / multiplier);
	}

	@Unique
	private TileEntity potatotech$self() {
		return (TileEntity) (Object) this;
	}

	@Unique
	private TileEntityEnergyConnector potatotech$connector() {
		return (TileEntityEnergyConnector) (Object) this;
	}
}
