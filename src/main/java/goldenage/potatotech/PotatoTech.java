package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.*;
import goldenage.potatotech.networks.client.OpenGuiCrafterClientMessage;
import goldenage.potatotech.networks.client.OpenGuiFilterClientMessage;
import goldenage.potatotech.networks.client.OpenGuiSequencerClientMessage;
import goldenage.potatotech.networks.server.DropPipeItemsMessage;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.lang.Language;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.dependency.Key;

import java.util.*;


public class PotatoTech implements ModInitializer {
    public static final String MOD_ID = "potatotech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigHandler config;
	static {
		Properties prop = new Properties();
		prop.setProperty("starting_block_id","1999");
		prop.setProperty("starting_item_id","17900");
		config = new ConfigHandler(MOD_ID, prop);
		config.updateConfig();
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Potato Tech initialized");

		PTBlocks.init();
		PTItems.init();
		Language.Default.INSTANCE.loadNamespace(MOD_ID);

		TileEntityDispatcher.addMapping(TileEntityPipe.class, id("tile.pipe"));
		TileEntityDispatcher.addMapping(TileEntityGoldPipe.class, id("tile.gold_pipe"));
		TileEntityDispatcher.addMapping(TileEntityDiamondPipe.class, id("tile.diamond_pipe"));
		TileEntityDispatcher.addMapping(TileEntityChute.class, id("tile.chute"));
		TileEntityDispatcher.addMapping(TileEntityFilter.class, id("tile.filter"));
		TileEntityDispatcher.addMapping(TileEntityCrafter.class, id("tile.crafter"));
		TileEntityDispatcher.addMapping(TileEntityEnergyConnector.class, id("tile.energy_connector"));
		TileEntityDispatcher.addMapping(TileEntityStirlingEngine.class, id("tile.stirling_engine"));
		TileEntityDispatcher.addMapping(TileEntitySequencer.class, id("tile.sequencer"));

		NetworkHandler.registerNetworkMessage(OpenGuiFilterClientMessage::new);
		NetworkHandler.registerNetworkMessage(OpenGuiCrafterClientMessage::new);
		NetworkHandler.registerNetworkMessage(OpenGuiSequencerClientMessage::new);
		NetworkHandler.registerNetworkMessage(DropPipeItemsMessage::new);

		CommonEvents.RECIPES_NAMESPACE_INIT.listen(Key.of(MOD_ID), PTRecipes::initNamespaces);
		CommonEvents.RECIPES_READY.listen(Key.of(MOD_ID), PTRecipes::onRecipesReady);
	}

	public static NamespaceID id(String id) {
		return NamespaceID.getPermanent(MOD_ID, id);
	}
}
