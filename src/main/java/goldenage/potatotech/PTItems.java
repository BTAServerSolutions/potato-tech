package goldenage.potatotech;

import goldenage.potatotech.items.ItemWireSpool;
import goldenage.potatotech.items.ItemWrench;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemPlaceable;
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
	public static Item energyConnector;
	public static Item wireSpool;
	public static Item electricHeatingUnit;
	public static Item redstoneIronMix;
	public static Item redstoneAlloy;


	@Override
	public void afterItemInit() {
		LOGGER.info("Initializing items...");

		int id = config.getInt("starting_item_id");

		potato = simpleItem("potato", "potato", "potato", id++);
		wrench = customItem(new ItemWrench("wrench", NamespaceID.getPermanent(MOD_ID, "wrench"), id++), "wrench", "wrench");
		crushedIronOre = simpleItem("crushed iron ore", "crushed_iron_ore", "crushed_iron_ore", id++);
		crushedGoldOre = simpleItem("crushed gold ore", "crushed_gold_ore", "crushed_gold_ore", id++);
		energyConnector = customItem(new ItemPlaceable("Energy Connector", MOD_ID+":"+"item_energy_connector", id++, PTBlocks.energyConnector), "energy_connector", "item_connector");
		wireSpool = customItem(new ItemWireSpool("wire_spool", NamespaceID.getPermanent(MOD_ID, "wire_spool"), id++), "wire_spool", "wire_spool");
		electricHeatingUnit = simpleItem("Electric Heating Unit", "electric_heating_unit", "electric_heating_unit", id++);
		redstoneIronMix = simpleItem("Redstone IronMix", "redstone_iron_mix", "redstone_iron_mix", id++);
		redstoneAlloy = simpleItem("Redstone Allow", "redstone_alloy", "redstone_alloy", id++);
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
