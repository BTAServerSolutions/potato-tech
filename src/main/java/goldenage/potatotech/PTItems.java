package goldenage.potatotech;

import goldenage.potatotech.items.ItemWrench;
import net.minecraft.core.item.Item;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.util.ItemInitEntrypoint;

import java.util.HashMap;

import static goldenage.potatotech.PotatoTech.*;

public class PTItems implements ItemInitEntrypoint {

	public static HashMap<Item, String> itemTextures = new HashMap<>();

	public static Item potato;
	public static Item wrench;
	public static Item crushedIronOre;
	public static Item crushedGoldOre;

	@Override
	public void afterItemInit() {
		LOGGER.info("Initializing items...");

		int id = config.getInt("starting_item_id");

		potato = simpleItem("potato", "potato", "potato", id++);
		wrench = customItem(new ItemWrench("wrench", NamespaceID.getPermanent(MOD_ID, "wrench"), id++), "wrench", "wrench");
		crushedIronOre = simpleItem("crushed iron ore", "crushed_iron_ore", "crushed_iron_ore", id++);
		crushedGoldOre = simpleItem("crushed gold ore", "crushed_gold_ore", "crushed_gold_ore", id++);
	}


	public static Item customItem(Item item, String lang, String texture) {
		item.setKey(lang);
		itemTextures.put(item, texture);
		LOGGER.info("Registering Custom item '{}' with texture 'potatotech:item/{}'.", item.namespaceID.toString(), texture);
		return new ItemBuilder(MOD_ID).build(item);
	}

	public static Item simpleItem(String name, String lang, String texture, int id) {
		Item item = new Item(NamespaceID.getPermanent(MOD_ID, name), id);
		item.setKey(lang);
		itemTextures.put(item, texture);
		LOGGER.info("Registering item '{}' with texture 'potatotech:item/{}'.", item.namespaceID.toString(), texture);
		return new ItemBuilder(MOD_ID).build(item);
	}
}
