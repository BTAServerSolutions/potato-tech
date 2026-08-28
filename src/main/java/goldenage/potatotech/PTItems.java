package goldenage.potatotech;

import goldenage.potatotech.items.ItemWireSpool;
import goldenage.potatotech.items.ItemWrench;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemPlaceable;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryCategory;
import turniplabs.halplibe.helper.creativeInventory.CreativeInventoryPlacement;

import java.util.HashMap;

import static goldenage.potatotech.PotatoTech.*;

public class PTItems {

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


	public static void init() {
		LOGGER.info("Initializing items...");

		int id = config.getInt("starting_item_id");

		potato = simpleItem("potato", "potato", "potato", id++, CreativeInventoryCategory.FOOD);
		wrench = customItem(new ItemWrench("wrench", NamespaceID.getPermanent(MOD_ID, "wrench"), id++), "wrench", CreativeInventoryCategory.TOOLS);
		crushedIronOre = simpleItem("crushed iron ore", "crushed_iron_ore", "crushed_iron_ore", id++, CreativeInventoryCategory.ORE_PRODUCTS);
		crushedGoldOre = simpleItem("crushed gold ore", "crushed_gold_ore", "crushed_gold_ore", id++, CreativeInventoryCategory.ORE_PRODUCTS);
		energyConnector = customItem(new ItemPlaceable(NamespaceID.getPermanent(MOD_ID, "item_energy_connector"), "energy_connector", id++, PTBlocks.energyConnector), "item_connector", CreativeInventoryCategory.REDSTONE);
		wireSpool = customItem(new ItemWireSpool("wire_spool", NamespaceID.getPermanent(MOD_ID, "wire_spool"), id++), "wire_spool", CreativeInventoryCategory.REDSTONE);
		electricHeatingUnit = simpleItem("Electric Heating Unit", "electric_heating_unit", "electric_heating_unit", id++, CreativeInventoryCategory.REDSTONE).setMaxStackSize(4);
		redstoneIronMix = simpleItem("Redstone IronMix", "redstone_iron_mix", "redstone_iron_mix", id++, CreativeInventoryCategory.REDSTONE);
		redstoneAlloy = simpleItem("Redstone Allow", "redstone_alloy", "redstone_alloy", id++, CreativeInventoryCategory.REDSTONE);
	}


	public static Item customItem(Item item, String texture, CreativeInventoryCategory category) {
		itemTextures.put(item, texture);
		LOGGER.info("Registering Custom item '{}' with texture 'potatotech:item/{}'.", item.namespaceID.toString(), texture);
		return new ItemBuilder(MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(category))
			.build(item);
	}

	public static Item simpleItem(String name, String lang, String texture, int id, CreativeInventoryCategory category) {
		Item item = new Item(NamespaceID.getPermanent(MOD_ID, name), lang, id);
		itemTextures.put(item, texture);
		LOGGER.info("Registering item '{}' with texture 'potatotech:item/{}'.", item.namespaceID.toString(), texture);
		return new ItemBuilder(MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(category))
			.build(item);
	}
}
