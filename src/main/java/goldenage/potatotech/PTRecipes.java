package goldenage.potatotech;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.RecipeEntrypoint;

import static goldenage.potatotech.PotatoTech.LOGGER;

public class PTRecipes implements RecipeEntrypoint {

	public static RecipeNamespace POTATO_TECH = new RecipeNamespace();
	public static RecipeGroup<RecipeEntryCrafting<?,?>> WORKBENCH;
	public static RecipeGroup<RecipeEntryFurnace> FURNACE;
	@Override
	public void onRecipesReady() {
		LOGGER.info("Loading PotatoTech recipes...");
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
			.addInput('C',"minecraft:chests")
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

		RecipeBuilder.Furnace(PotatoTech.MOD_ID)
			.setInput(PTItems.crushedIronOre)
			.create("Ingot Iron", new ItemStack(Items.INGOT_IRON));

		RecipeBuilder.Furnace(PotatoTech.MOD_ID)
			.setInput(PTItems.crushedGoldOre)
			.create("Ingot Gold", new ItemStack(Items.INGOT_GOLD));

	}

	@Override
	public void initNamespaces() {
		LOGGER.info("Loading PotatoTech recipe namespaces...");

		Registries.RECIPES.unregister("potatotech");
		POTATO_TECH = new RecipeNamespace();
		WORKBENCH = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.WORKBENCH)));
		FURNACE = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.FURNACE_STONE_IDLE)));

		POTATO_TECH.register("workbench",WORKBENCH);
		POTATO_TECH.register("furnace",FURNACE);
	}
}
