package goldenage.potatotech.mixins;

import goldenage.potatotech.blocks.BlockLogicEnergyConnector;
import org.spongepowered.asm.mixin.Mixin;
import sunsetsatellite.catalyst.core.util.network.NetworkComponent;
import sunsetsatellite.catalyst.core.util.network.NetworkType;

@Mixin(value = BlockLogicEnergyConnector.class, remap = false)
public class CatalystEnergyConnectorBlockMixin implements NetworkComponent {
	@Override
	public NetworkType getType() {
		return NetworkType.CATALYST_ENERGY;
	}
}
