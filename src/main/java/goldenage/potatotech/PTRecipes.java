package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.helper.RecipeBuilder;

import static goldenage.potatotech.PotatoTech.LOGGER;

public class PTRecipes {

	public static RecipeNamespace POTATO_TECH = new RecipeNamespace();
	public static RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH;
	public static RecipeGroup<RecipeEntryFurnace> FURNACE;
	public static RecipeGroup<RecipeEntryTrommel> TROMMEL;

	public static void onRecipesReady() {
		LOGGER.info("Loading PotatoTech recipes...");
		resetGroups();
		registerNamespaces();
		load();
	}

	public static void initNamespaces() {
		LOGGER.info("Loading PotatoTech recipe namespaces...");
		resetGroups();

		registerNamespaces();
	}

	public static void registerNamespaces() {
		POTATO_TECH.register("workbench", WORKBENCH);
		POTATO_TECH.register("furnace", FURNACE);
		POTATO_TECH.register("trommel", TROMMEL);
		Registries.RECIPES.register("potatotech", POTATO_TECH);
	}

	public static void resetGroups() {
		WORKBENCH = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.WORKBENCH)));
		FURNACE = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.FURNACE_STONE_IDLE)));
		TROMMEL = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.TROMMEL_IDLE)));
		Registries.RECIPES.unregister("potatotech");
	}

	public static void load() {
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("CI", "IC", "  ")
			.addInput('C', Items.CLAY)
			.addInput('I', Items.ORE_RAW_IRON)
			.create("Clay Iron", PTBlocks.clayIron);

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("CI", "IC")
			.addInput('C', Items.CLAY)
			.addInput('I', Items.ORE_RAW_GOLD)
			.create("Clay Iron", PTBlocks.clayGold);

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("S S", "SCS", " S ")
			.addInput('S', "minecraft:stones")
			.addInput('C', "minecraft:chests")
			.create("Chute", PTBlocks.chute);

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("  I", " II", "I  ")
			.addInput('I', Items.INGOT_IRON)
			.create("Wrench", PTItems.wrench);

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("IGI")
			.addInput('I', Items.INGOT_IRON)
			.addInput('G', Blocks.GLASS)
			.create("Pipe", new ItemStack(PTBlocks.pipe, 16));
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("IGI")
			.addInput('I', Items.INGOT_GOLD)
			.addInput('G', Blocks.GLASS)
			.create("Gold Pipe", new ItemStack(PTBlocks.pipeGold, 16));
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("DGD")
			.addInput('D', Items.DIAMOND)
			.addInput('G', Blocks.GLASS)
			.create("Diamond Pipe", new ItemStack(PTBlocks.pipeDiamond, 8));
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("IGI")
			.addInput('I', Items.INGOT_STEEL)
			.addInput('G', Blocks.GLASS)
			.create("Steel Pipe", new ItemStack(PTBlocks.pipeSteel, 16));

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("WRW", "RMR", "WRW")
			.addInput('W', "minecraft:planks")
			.addInput('R', Items.DUST_REDSTONE)
			.addInput('M', Blocks.MESH_GOLD)
			.create("Filter", new ItemStack(PTBlocks.filter, 1));

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("WRW", "RMR", "WRW")
			.addInput('W', Items.INGOT_IRON)
			.addInput('R', Items.DUST_REDSTONE)
			.addInput('M', Blocks.WORKBENCH)
			.create("Crafter", new ItemStack(PTBlocks.crafter, 1));

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("IRI", "ACA", "III")
			.addInput('I', Items.INGOT_IRON)
			.addInput('R', Items.DUST_REDSTONE)
			.addInput('C', PTBlocks.coil)
			.addInput('A', PTItems.redstoneAlloy)
			.create("Stirling Engine", new ItemStack(PTBlocks.stirlingEngine, 1));

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape(" R ", "RSR", " R ")
			.addInput('R', PTItems.redstoneAlloy)
			.addInput('S', Items.STICK)
			.create("Wire Spool", new ItemStack(PTItems.wireSpool, 8));

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape(" I ", "BIB", "BIB")
			.addInput('I', Items.INGOT_IRON)
			.addInput('B', Items.BRICK_CLAY)
			.create("Energy Connector", new ItemStack(PTItems.energyConnector, 4));

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape(" R ", "RSR", " R ")
			.addInput('R', Items.INGOT_IRON)
			.addInput('S', PTBlocks.coil)
			.create("Crafter", new ItemStack(PTItems.electricHeatingUnit, 1));

		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape("CCC", "CSC", "CCC")
			.addInput('C', PTItems.wireSpool)
			.addInput('S', Items.INGOT_IRON)
			.create("Crafter", new ItemStack(PTBlocks.coil, 1));

		RecipeBuilder.Shapeless(PotatoTech.MOD_ID)
			.addInput(Items.INGOT_IRON)
			.addInput(new ItemStack(Items.DUST_REDSTONE))
			.addInput(new ItemStack(Items.DUST_REDSTONE))
			.addInput(new ItemStack(Items.DUST_REDSTONE))
			.create("Redstone Iron Mix", new ItemStack(PTItems.redstoneIronMix, 1));

		RecipeBuilder.Furnace(PotatoTech.MOD_ID)
			.setInput(PTItems.crushedIronOre)
			.create("Ingot Iron", new ItemStack(Items.INGOT_IRON));

		RecipeBuilder.Furnace(PotatoTech.MOD_ID)
			.setInput(PTItems.crushedGoldOre)
			.create("Ingot Gold", new ItemStack(Items.INGOT_GOLD));

		RecipeBuilder.Furnace(PotatoTech.MOD_ID)
			.setInput(PTItems.redstoneIronMix)
			.create("Ingot Iron", new ItemStack(PTItems.redstoneAlloy));

		RecipeBuilder.Trommel(PotatoTech.MOD_ID)
			.setInput(PTItems.bedrockDust)
			.addEntry(new WeightedRandomLootObject(new ItemStack(Items.COAL), 1, 2), 70)
			.addEntry(new WeightedRandomLootObject(new ItemStack(Items.DIAMOND), 1), 10)
			.addEntry(new WeightedRandomLootObject(new ItemStack(Items.DUST_REDSTONE), 3, 6), 64)
			.addEntry(new WeightedRandomLootObject(new ItemStack(Items.DYE, 1, 4), 1, 3), 8)
			.addEntry(new WeightedRandomLootObject(new ItemStack(Items.ORE_RAW_IRON), 1, 2), 64)
			.addEntry(new WeightedRandomLootObject(new ItemStack(Items.ORE_RAW_GOLD), 1), 16)
			.create("bedrock_dust");

		TileEntityCrafter.updateRecipeEntriesCache();
	}
}
