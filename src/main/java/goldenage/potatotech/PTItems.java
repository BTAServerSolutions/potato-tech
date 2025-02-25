package goldenage.potatotech;

import net.minecraft.core.item.Item;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.util.ItemInitEntrypoint;

import java.util.HashMap;

import static goldenage.potatotech.PotatoTech.LOGGER;
import static goldenage.potatotech.PotatoTech.MOD_ID;

public class PTItems implements ItemInitEntrypoint {

	public static HashMap<Item, String> itemTextures = new HashMap<>();

	boolean isInitialized = false;

	public void init() {
		if (isInitialized) return;
		LOGGER.info("Initializing items...");
		isInitialized = true;
	}

	@Override
	public void afterItemInit() {
		init();
	}


	public static Item simpleItem(String name, String lang, String texture, int id) {
		Item item = new Item(NamespaceID.getPermanent(MOD_ID, name), id);
		item.setKey(lang);
		itemTextures.put(item, texture);
		LOGGER.info("Registering item '" + item.namespaceID.toString() + "' with texture 'potatotech:item/" + texture + "'.");
		return new ItemBuilder(MOD_ID).build(item);
	}
}
