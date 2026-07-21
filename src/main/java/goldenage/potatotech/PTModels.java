package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import goldenage.potatotech.blocks.models.BlockModelChute;
import goldenage.potatotech.blocks.models.BlockModelConnector;
import goldenage.potatotech.blocks.models.BlockModelPipe;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelRotatable;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.block.model.BlockModelTransparent;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import static goldenage.potatotech.PotatoTechClient.LOGGER;

@Environment(EnvType.CLIENT)
public class PTModels implements ModelEntrypoint {
	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		LOGGER.info("Initializing block models...");

		dispatcher.addDispatch(new BlockModelStandard<>(PTBlocks.testAreaMaker).setTex("potatotech:block/potato", Side.sides)
		);
		dispatcher.addDispatch(
			new BlockModelStandard<>(PTBlocks.clayIron).setAllTextures("potatotech:block/clay_iron_block")
		);
		dispatcher.addDispatch(
			new BlockModelStandard<>(PTBlocks.clayGold).setAllTextures("potatotech:block/clay_gold_block")
		);
		dispatcher.addDispatch(
			new BlockModelStandard<>(PTBlocks.filter).setAllTextures("potatotech:block/filter")
		);

		dispatcher.addDispatch(new BlockModelPipe<>(PTBlocks.pipe)
			.setAllTextures("potatotech:block/pipe")
		);
		dispatcher.addDispatch(new BlockModelPipe<>(PTBlocks.pipeGold)
			.setAllTextures("potatotech:block/gold_pipe")
		);
		dispatcher.addDispatch(new BlockModelPipe<>(PTBlocks.pipeDiamond)
			.setAllTextures("potatotech:block/diamond_pipe")
		);

		dispatcher.addDispatch(new BlockModelStandard<>(PTBlocks.chute)
			.setTex("potatotech:block/coil_sides", Side.NORTH, Side.SOUTH, Side.EAST, Side.WEST)
			.setTex("potatotech:block/coil_top", Side.TOP, Side.BOTTOM)
		);

		dispatcher.addDispatch(new BlockModelChute<>(PTBlocks.chute)
			.setTex("potatotech:block/chute_sides", Side.NORTH, Side.SOUTH, Side.EAST, Side.WEST)
			.setTex("potatotech:block/chute_bottom", Side.TOP, Side.BOTTOM)
		);
		dispatcher.addDispatch(
			new BlockModelTransparent<>(PTBlocks.pipeStack, true).setAllTextures("potatotech:block/pipe_stack")
		);

		dispatcher.addDispatch(
			new BlockModelConnector<>(PTBlocks.energyConnector)
				.setAllTextures("potatotech:block/energy_connector")
		);

		dispatcher.addDispatch(
			new BlockModelRotatable<>(PTBlocks.crafter)
				.setTex("potatotech:block/auto_crafter_front", Side.TOP)
				.setTex("potatotech:block/iron_chasing_details0", Side.BOTTOM, Side.EAST, Side.WEST)
				.setTex("potatotech:block/auto_crafter_top", Side.NORTH)
				.setTex("potatotech:block/iron_casing_plain", Side.SOUTH)
		);
		dispatcher.addDispatch(
			new BlockModelRotatable<>(PTBlocks.stirlingEngine)
				.setTex("potatotech:block/stirling_engine_front", Side.TOP)
				.setTex("potatotech:block/stirling_engine_back", Side.BOTTOM)
				.setTex("potatotech:block/stirling_engine_hot_side", Side.WEST)
				.setTex("potatotech:block/stirling_engine_cold_side", Side.EAST)
				.setTex("potatotech:block/stirling_engine_top", Side.NORTH)
				.setTex("potatotech:block/iron_chasing_details1", Side.SOUTH)
		);
		dispatcher.addDispatch(
			new BlockModelRotatable<>(PTBlocks.sequencer)
				.setTex("potatotech:block/sequencer_front", Side.TOP)
				.setTex("potatotech:block/iron_machine_out", Side.NORTH)
				.setTex("potatotech:block/iron_machine_side", Side.SOUTH, Side.EAST, Side.WEST, Side.BOTTOM)
		);
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {
		LOGGER.info("Initializing item models...");
		PTItems.itemTextures.forEach((item,texture)->{
			dispatcher.addDispatch(new ItemModelStandard(item, null).setIcon(PotatoTech.MOD_ID +" :item/" + texture));
		});
	}

	@Override
	public void initEntityModels(EntityRendererDispatcher dispatcher) {
		LOGGER.info("Initializing entity models...");
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		LOGGER.info("Initializing tile entity renderers...");
		dispatcher.assignRenderer(TileEntityPipe.class, new TileEntityRendererPipe());
		dispatcher.assignRenderer(TileEntityEnergyConnector.class, new TileEntityRendererEnergyConnector());
	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {

	}
}
