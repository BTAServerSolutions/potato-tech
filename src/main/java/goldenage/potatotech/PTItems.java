package goldenage.potatotech;

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
		wrench = simpleItem("wrench", "wrench", "wrench", id++);
		crushedIronOre = simpleItem("crushed iron ore", "crushed_iron_ore", "crushed_iron_ore", id++);
		crushedGoldOre = simpleItem("crushed gold ore", "crushed_gold_ore", "crushed_gold_ore", id++);
	}


	public static Item simpleItem(String name, String lang, String texture, int id) {
		Item item = new Item(NamespaceID.getPermanent(MOD_ID, name), id);
		item.setKey(lang);
		itemTextures.put(item, texture);
		LOGGER.info("Registering item '" + item.namespaceID.toString() + "' with texture 'potatotech:item/" + texture + "'.");
		return new ItemBuilder(MOD_ID).build(item);
	}
}
