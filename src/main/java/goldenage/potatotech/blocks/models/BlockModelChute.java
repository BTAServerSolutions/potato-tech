package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.BlockLogicChute;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

public class BlockModelChute<T extends BlockLogic> extends BlockModelGeneric<T> {
	private final StaticBlockModel fill;

	public BlockModelChute(Block<T> block) {
		super(block, BlockModelDispatcher.loadDataModel("potatotech:block/chute").asModel());
		this.fill = BlockModelDispatcher.loadDataModel("potatotech:block/chute_fill").asModel();
	}

	@Override
	public boolean renderAttached(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos, boolean cullFaces, @Nullable IconCoordinate overrideTexture) {
		boolean rendered = super.renderAttached(tessellator, worldSource, tilePos, cullFaces, overrideTexture);
		int fillLevel = ((BlockLogicChute) block.getLogic()).getFillLevel(worldSource, tilePos);
		if (fillLevel > 0) {
			rendered |= fill.renderAttached(this, tessellator, worldSource, tilePos, 0, 0, 0, 0, fillLevel / 16.0, 0, false, cullFaces, overrideTexture);
		}
		return rendered;
	}
}
