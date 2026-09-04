package goldenage.potatotech.compat.btwaila;

import goldenage.potatotech.blocks.entities.TileEntityChute;
import goldenage.potatotech.blocks.entities.TileEntityBedrockExtractor;
import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.blocks.entities.TileEntityStirlingEngine;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.ItemStack;
import org.slf4j.Logger;
import toufoumaster.btwaila.entryplugins.waila.BTWailaCustomTooltipPlugin;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.tooltips.TileTooltip;
import toufoumaster.btwaila.tooltips.TooltipRegistry;
import toufoumaster.btwaila.util.ProgressBarOptions;
import toufoumaster.btwaila.util.TextureOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PotatoTechBTWailaPlugin implements BTWailaCustomTooltipPlugin {
	@Override
	public void initializePlugin(TooltipRegistry tooltipRegistry, Logger logger) {
		logger.info("Loading Potato Logistics tooltips");
		tooltipRegistry.register(new EnergyConnectorTooltip());
		tooltipRegistry.register(new StirlingEngineTooltip());
		tooltipRegistry.register(new FilterTooltip());
		tooltipRegistry.register(new ChuteTooltip());
		tooltipRegistry.register(new CrafterTooltip());
		tooltipRegistry.register(new BedrockExtractorTooltip());
	}
}

abstract class PotatoTechTooltip<T> extends TileTooltip<T> {
	protected void drawBar(AdvancedInfoComponent component, int value, int maximum, String label, int color) {
		ProgressBarOptions options = new ProgressBarOptions()
			.setBoxWidth(152)
			.setForegroundOptions(new TextureOptions(color, TextureRegistry.getTexture("minecraft:block/stone")))
			.setBackgroundOptions(new TextureOptions(0, TextureRegistry.getTexture("minecraft:block/polished_stone_top")))
			.setText(label + ": ");
		component.drawProgressBarTextureWithText(value, maximum, options, 0);
	}
}

class EnergyConnectorTooltip extends PotatoTechTooltip<TileEntityEnergyConnector> {
	@Override
	public void initTooltip() {
		addClass(TileEntityEnergyConnector.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityEnergyConnector connector, AdvancedInfoComponent component) {
		component.drawStringWithShadow("Energy Connector", 0, 0x55D9FF);
		drawBar(component, connector.energy, connector.getEnergyCapacity(), "Buffer (PE)", 0x55D9FF);
		component.drawStringWithShadow(connector.connections.size() + " linked connector" + (connector.connections.size() == 1 ? "" : "s"), 0);
	}
}

class StirlingEngineTooltip extends PotatoTechTooltip<TileEntityStirlingEngine> {
	@Override
	public void initTooltip() {
		addClass(TileEntityStirlingEngine.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityStirlingEngine engine, AdvancedInfoComponent component) {
		int color = engine.power > 0 ? 0xFF9B45 : 0x777777;
		component.drawStringWithShadow(engine.power > 0 ? "Thermal gradient active" : "No thermal gradient", 0, color);
		drawBar(component, engine.power, 8, "Power", color);
	}
}

class FilterTooltip extends PotatoTechTooltip<TileEntityFilter> {
	private static final String[] DYE_NAMES = {
		"Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "Light Gray",
		"Gray", "Pink", "Lime", "Yellow", "Light Blue", "Magenta", "Orange", "White"
	};

	@Override
	public void initTooltip() {
		addClass(TileEntityFilter.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityFilter filter, AdvancedInfoComponent component) {
		List<ItemStack> filters = new ArrayList<>();
		for (int slot = 0; slot < filter.getContainerSize(); slot++) {
			ItemStack stack = filter.getItem(slot);
			if (stack == null) {
				continue;
			}
			filters.add(stack);
			short color = filter.getColorInSlot(slot);
			component.drawStringWithShadow("Slot " + (slot + 1) + ": " + stack.getDisplayName() + colorLabel(color), 0);
		}
		if (filters.isEmpty()) {
			component.drawStringWithShadow("No filter rules", 0, 0xAAAAAA);
			return;
		}
		component.drawStringWithShadow("Filter rules: " + filters.size() + "/" + filter.getContainerSize(), 0, 0x55D9FF);
		component.drawItemList(filters.toArray(new ItemStack[0]), 0);
	}

	private String colorLabel(short color) {
		if (color == 0) {
			return " - any color";
		}
		if (color <= DYE_NAMES.length) {
			return " - " + DYE_NAMES[color - 1];
		}
		return " - dye " + color;
	}
}

class ChuteTooltip extends PotatoTechTooltip<TileEntityChute> {
	@Override
	public void initTooltip() {
		addClass(TileEntityChute.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityChute chute, AdvancedInfoComponent component) {
		component.drawStringWithShadow(chute.contents.isEmpty() ? "Empty" : chute.contents.size() + " item type" + (chute.contents.size() == 1 ? "" : "s"), 0);
		drawBar(component, chute.getNumUnitsInside(), chute.getMaxUnits(), "Capacity", 0xD6A34A);
		List<ItemStack> stacks = new ArrayList<>();
		for (Map.Entry<TileEntityChute.ChuteEntry, Integer> entry : chute.contents.entrySet()) {
			TileEntityChute.ChuteEntry item = entry.getKey();
			stacks.add(new ItemStack(item.id, entry.getValue(), item.metadata, item.tag));
		}
		if (!stacks.isEmpty()) {
			component.drawItemList(stacks.toArray(new ItemStack[0]), 0);
		}
	}
}

class CrafterTooltip extends PotatoTechTooltip<TileEntityCrafter> {
	@Override
	public void initTooltip() {
		addClass(TileEntityCrafter.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityCrafter crafter, AdvancedInfoComponent component) {
		drawBar(component, crafter.energy, TileEntityCrafter.energyCapacity, "Craft energy (PE)", 0x55D9FF);
		ItemStack result = crafter.getItem(0);
		component.drawStringWithShadow(result == null ? "Awaiting a valid recipe" : "Output: " + result.getDisplayName(), 0);
		component.drawInventory(crafter, 0);
	}
}

class BedrockExtractorTooltip extends PotatoTechTooltip<TileEntityBedrockExtractor> {
	@Override
	public void initTooltip() {
		addClass(TileEntityBedrockExtractor.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityBedrockExtractor extractor, AdvancedInfoComponent component) {
		boolean validAssembly = extractor.hasValidDrillAssembly();
		component.drawStringWithShadow("Bedrock Extractor", 0, 0x55D9FF);
		component.drawStringWithShadow(
			validAssembly ? "Drill assembly ready" : "Requires a Bedrock Drill on bedrock below",
			0,
			validAssembly ? 0x55D955 : 0xFF5555
		);
		drawBar(component, extractor.energy, TileEntityBedrockExtractor.getEnergyCapacity(), "Charge (PE)", 0x55D9FF);
	}
}
