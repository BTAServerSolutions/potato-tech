package goldenage.potatotech.compat.catalyst;

import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Direction;
import sunsetsatellite.catalyst.energy.simple.api.IEnergyContainer;

public final class CatalystCompat {
	private CatalystCompat() {
	}

	public static boolean isAvailable() {
		return FabricLoader.getInstance().isModLoaded("catalyst-energy");
	}

	public static void transferEnergy(TileEntityEnergyConnector connector, TileEntity target, Direction direction) {
		if (!(connector instanceof IEnergyContainer source) || !(target instanceof IEnergyContainer destination)) {
			return;
		}
		sunsetsatellite.catalyst.core.util.Direction targetSide = toCatalystDirection(direction.opposite());
		if (!destination.canReceive(targetSide) || source.getEnergy() <= 0) {
			return;
		}
		long accepted = destination.receiveEnergy(targetSide, source.getEnergy());
		if (accepted > 0) {
			source.internalChangeEnergy(-accepted);
			connector.setChanged();
		}
	}

	private static sunsetsatellite.catalyst.core.util.Direction toCatalystDirection(Direction direction) {
		return sunsetsatellite.catalyst.core.util.Direction.getDirectionFromSide(direction.id);
	}

}
