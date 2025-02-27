package goldenage.potatotech;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
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

		ModelHelper.setBlockModel(PTBlocks.testAreaMaker, () -> new BlockModelStandard<>(PTBlocks.testAreaMaker)
			.setTex(0, "potatotech:block/potato", Side.sides)
		);
	}

	@Override
	public void initEntityModels(EntityRenderDispatcher dispatcher) {
		LOGGER.info("Initializing entity models...");
	}

	@Override
	public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		LOGGER.info("Initializing tile entity renderers...");
	}

	@Override
	public void initBlockColors(BlockColorDispatcher dispatcher) {

	}
}
