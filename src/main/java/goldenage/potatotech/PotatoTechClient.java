package goldenage.potatotech;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ClientStartEntrypoint;

@Environment(EnvType.CLIENT)
public class PotatoTechClient implements ClientModInitializer, ClientStartEntrypoint {
	public static final String MOD_ID = "potatotech|client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Potato tech client init");
	}

	@Override
	public void beforeClientStart() {
		LOGGER.info("Potato tech pre init");
	}

	@Override
	public void afterClientStart() {
		LOGGER.info("Potato tech post init");

		IKeybindings gameSettings = (IKeybindings) Minecraft.getMinecraft().gameSettings;

		//OptionsPage optionsPage = new OptionsPage("gui.options.page.potatotech", PTItems.potato.getDefaultStack());
		//OptionsPages.register(optionsPage);

		OptionsCategory category = new OptionsCategory("gui.options.page.controls.category.potatotech");
		category
			.withComponent(new KeyBindingComponent(gameSettings.potatotech$getWrenchMode()));
		OptionsPages.CONTROLS
			.withComponent(category);
	}
}
