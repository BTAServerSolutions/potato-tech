package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import goldenage.potatotech.blocks.models.BlockModelChute;
import goldenage.potatotech.blocks.models.BlockModelConnector;
import goldenage.potatotech.blocks.models.BlockModelPipe;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.EntityRenderDispatcher;
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

		ModelHelper.setBlockModel(PTBlocks.testAreaMaker, () -> new BlockModelStandard<>(PTBlocks.testAreaMaker)
			.setTex(0, "potatotech:block/potato", Side.sides)
		);
		ModelHelper.setBlockModel(PTBlocks.clayIron, () ->
			new BlockModelStandard<>(PTBlocks.clayIron).setAllTextures(0, "potatotech:block/clay_iron_block")
		);
		ModelHelper.setBlockModel(PTBlocks.clayGold, () ->
			new BlockModelStandard<>(PTBlocks.clayGold).setAllTextures(0, "potatotech:block/clay_gold_block")
		);
		ModelHelper.setBlockModel(PTBlocks.filter, () ->
			new BlockModelStandard<>(PTBlocks.filter).setAllTextures(0, "potatotech:block/filter")
		);

		ModelHelper.setBlockModel(PTBlocks.pipe, () -> new BlockModelPipe<>(PTBlocks.pipe)
			.setAllTextures(0, "potatotech:block/pipe")
		);
		ModelHelper.setBlockModel(PTBlocks.pipeGold, () -> new BlockModelPipe<>(PTBlocks.pipeGold)
			.setAllTextures(0, "potatotech:block/gold_pipe")
		);
		ModelHelper.setBlockModel(PTBlocks.pipeDiamond, () -> new BlockModelPipe<>(PTBlocks.pipeDiamond)
			.setAllTextures(0, "potatotech:block/diamond_pipe")
		);

		ModelHelper.setBlockModel(PTBlocks.chute, () -> new BlockModelChute<>(PTBlocks.chute)
			.setTex(0, "potatotech:block/chute_sides", Side.NORTH, Side.SOUTH, Side.EAST, Side.WEST)
			.setTex(0, "potatotech:block/chute_bottom", Side.TOP, Side.BOTTOM)
		);
		ModelHelper.setBlockModel(PTBlocks.pipeStack, () ->
			new BlockModelTransparent<>(PTBlocks.pipeStack, true).setAllTextures(0, "potatotech:block/pipe_stack")
		);

		ModelHelper.setBlockModel(PTBlocks.energyConnector, () ->
			new BlockModelConnector<>(PTBlocks.energyConnector)
				.setAllTextures(0, "potatotech:block/energy_connector")
		);

		ModelHelper.setBlockModel(PTBlocks.crafter, () ->
			new BlockModelRotatable<>(PTBlocks.crafter)
				.setTex(0, "potatotech:block/auto_crafter_front", Side.NORTH)
				.setTex(0, "potatotech:block/iron_chasing_details0", Side.SOUTH, Side.EAST, Side.WEST)
				.setTex(0, "potatotech:block/auto_crafter_top", Side.TOP)
				.setTex(0, "potatotech:block/iron_casing_plain", Side.BOTTOM)
		);
		ModelHelper.setBlockModel(PTBlocks.stirlingEngine, () ->
			new BlockModelRotatable<>(PTBlocks.stirlingEngine)
				.setTex(0, "potatotech:block/stirling_engine_front", Side.TOP)
				.setTex(0, "potatotech:block/stirling_engine_back", Side.BOTTOM)
				.setTex(0, "potatotech:block/stirling_engine_hot_side", Side.WEST)
				.setTex(0, "potatotech:block/stirling_engine_cold_side", Side.EAST)
				.setTex(0, "potatotech:block/stirling_engine_top", Side.NORTH)
				.setTex(0, "potatotech:block/iron_chasing_details1", Side.SOUTH)
		);
	}

	@Override
	public void initItemModels(ItemModelDispatcher dispatcher) {
		LOGGER.info("Initializing item models...");
		PTItems.itemTextures.forEach((item,texture)->{
			ModelHelper.setItemModel(item,()->{
				ItemModelStandard model = new ItemModelStandard(item, null);
				model.icon = TextureRegistry.getTexture(NamespaceID.getTemp(PotatoTech.MOD_ID,"item/"+texture));
				return model;
			});
		});
	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {
		LOGGER.info("Initializing entity models...");
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		LOGGER.info("Initializing tile entity renderers...");
		ModelHelper.setTileEntityModel(TileEntityPipe.class, TileEntityRendererPipe::new);
		ModelHelper.setTileEntityModel(TileEntityEnergyConnector.class, TileEntityRendererEnergyConnector::new);
	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {

	}
}
