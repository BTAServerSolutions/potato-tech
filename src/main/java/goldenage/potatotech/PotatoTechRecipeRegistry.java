package goldenage.potatotech;

import net.minecraft.core.block.Block;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeRegistry;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import turniplabs.halplibe.helper.RecipeBuilder;

public class PotatoTechRecipeRegistry extends RecipeRegistry {

	public static final RecipeNamespace POTATOTECH = new RecipeNamespace();

	public static void InitRecipes() {
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape(
				"   ",
				"IGI",
				"   ")
			.addInput('I', Item.ingotIron)
			.addInput('G', Block.glass)
			.create("Pipe", new ItemStack(PotatoTech.blockPipe, 8));
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape(
				"   ",
				"IGI",
				"   ")
			.addInput('I', Item.ingotGold)
			.addInput('G', Block.glass)
			.create("GoldPipe", new ItemStack(PotatoTech.blockPipeGold, 8));
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape(
				"   ",
				"IGI",
				"   ")
			.addInput('I', Item.diamond)
			.addInput('G', Block.glass)
			.create("DiamondPipe", new ItemStack(PotatoTech.blockPipeDiamond, 8));
		RecipeBuilder.Shaped(PotatoTech.MOD_ID)
			.setShape(
				"WRW",
				"RMR",
				"WRW")
			.addInput('W',"minecraft:planks")
			.addInput('R', Item.dustRedstone)
			.addInput('M', Block.meshGold)
			.create("Filter", new ItemStack(PotatoTech.blockFilter, 1));
	}

	public static void InitNameSpaces() {

		// So, you can use this template to add more groups if needed.
		// Constant for a group of crafting recipes related to the workbench.
		final RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH =

			// Create a new RecipeGroup instance.
			new RecipeGroup<>(

				// Create a RecipeSymbol for the workbench.
				new RecipeSymbol(

					// ItemStack representing the workbench block.
					new ItemStack(Block.workbench)
				)
			);

		// Add the recipe group - id: yourGroupId
		POTATOTECH.register("workbench", WORKBENCH);


		// If you have furnace recipes
		// final RecipeGroup<RecipeEntryFurnace> FURNACE = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Block.furnaceStoneActive)));
		// PARAGLIDER.register("furnace", FURNACE);

		// Register all
		Registries.RECIPES.register(PotatoTech.MOD_ID, POTATOTECH);

	}
}
