package goldenage.potatotech;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ClientStartEntrypoint;

@Environment(EnvType.CLIENT)
public class PotatoTechClient implements ClientModInitializer, ClientStartEntrypoint {
	public static final String MOD_ID = "potatotech|client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Potato tech client init");
	}

	@Override
	public void beforeClientStart() {
		LOGGER.info("Potato tech pre init");
	}

	@Override
	public void afterClientStart() {
		LOGGER.info("Potato tech post init");
	}
}
