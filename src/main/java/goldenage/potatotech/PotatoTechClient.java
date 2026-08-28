package goldenage.potatotech;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

@Environment(EnvType.CLIENT)
public class PotatoTechClient implements ClientModInitializer {
	public static final String MOD_ID = "potatotech|client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Potato tech client init");

		ClientEvents.BLOCK_MODEL_RELOAD.listen(Key.of(PotatoTech.MOD_ID), PTModels::initBlockModels);
		ClientEvents.ITEM_MODEL_RELOAD.listen(Key.of(PotatoTech.MOD_ID), PTModels::initItemModels);
		ClientEvents.TILE_ENTITY_RENDERER_RELOAD.listen(Key.of(PotatoTech.MOD_ID), PTModels::initTileEntityModels);
		ClientEvents.ENTITY_RENDERER_RELOAD.listen(Key.of(PotatoTech.MOD_ID), PTModels::initEntityModels);
	}
}
