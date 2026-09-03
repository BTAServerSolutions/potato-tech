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
	public static Item bedrockDust;


	public static void init() {
		LOGGER.info("Initializing items...");

		int id = config.getInt("starting_item_id");

		potato = simpleItem("potato", "potato", "potato", id++);
		wrench = customItem(new ItemWrench("wrench", new NamespaceID(MOD_ID, "wrench"), id++), "wrench");
		crushedIronOre = simpleItem("crushed_iron_ore", "crushed_iron_ore", "crushed_iron_ore", id++);
		crushedGoldOre = simpleItem("crushed_gold_ore", "crushed_gold_ore", "crushed_gold_ore", id++);
		energyConnector = customItem(new ItemPlaceable(new NamespaceID(MOD_ID, "item_energy_connector"), "energy_connector", id++, PTBlocks.energyConnector), "item_connector");
		wireSpool = customItem(new ItemWireSpool("wire_spool", new NamespaceID(MOD_ID, "wire_spool"), id++), "wire_spool");
		electricHeatingUnit = simpleItem("electric_heating_unit", "electric_heating_unit", "electric_heating_unit", id++).setMaxStackSize(4);
		redstoneIronMix = simpleItem("redstone_iron_mix", "redstone_iron_mix", "redstone_iron_mix", id++);
		redstoneAlloy = simpleItem("redstone_alloy", "redstone_alloy", "redstone_alloy", id++);
		bedrockDust = simpleItem("bedrock_dust", "bedrock_dust", "bedrock_dust", id++);
	}


	public static Item customItem(Item item, String texture) {
		itemTextures.put(item, texture);
		LOGGER.info("Registering Custom item '{}' with texture 'potatotech:item/{}'.", item.namespaceID, texture);
		return new ItemBuilder(MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build(item);
	}

	public static Item simpleItem(String name, String lang, String texture, int id) {
		Item item = new Item(new NamespaceID(MOD_ID, name), lang, id);
		itemTextures.put(item, texture);
		LOGGER.info("Registering item '{}' with texture 'potatotech:item/{}'.", item.namespaceID, texture);
		return new ItemBuilder(MOD_ID)
			.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
			.build(item);
	}
}
