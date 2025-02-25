package goldenage.potatotech;

import turniplabs.halplibe.util.RecipeEntrypoint;

import static goldenage.potatotech.PotatoTech.LOGGER;

public class PTRecipes implements RecipeEntrypoint {
	@Override
	public void onRecipesReady() {
		LOGGER.info("Loading PotatoTech recipes...");
	}

	@Override
	public void initNamespaces() {
		LOGGER.info("Loading PotatoTech recipe namespaces...");
	}
}
