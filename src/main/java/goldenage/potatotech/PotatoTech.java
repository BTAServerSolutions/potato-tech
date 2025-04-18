package goldenage.potatotech;

import goldenage.potatotech.blocks.TileEntityPipe;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.util.*;


public class PotatoTech implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "potatotech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigHandler config;
	static {
		Properties prop = new Properties();
		prop.setProperty("starting_block_id","1999");
		prop.setProperty("starting_item_id","17999");
		config = new ConfigHandler(MOD_ID, prop);
		config.updateConfig();
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Potato Tech initialized");

		EntityHelper.createTileEntity(TileEntityPipe.class, id("tile.pipe"));
	}

	@Override
	public void beforeGameStart() {
		LOGGER.info("init");
	}

	@Override
	public void afterGameStart() {
		LOGGER.info("init");
	}


	public static NamespaceID id(String id) {
		return NamespaceID.getPermanent(MOD_ID, id);
	}
}
