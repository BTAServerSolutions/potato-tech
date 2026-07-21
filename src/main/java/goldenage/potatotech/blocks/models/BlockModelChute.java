package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.BlockLogicChute;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBdc;
import org.jspecify.annotations.NonNull;

public class BlockModelChute<T extends BlockLogic> extends BlockModelStandard<T> {
	public BlockModelChute(Block<T> block) {
		super(block);
	}

	@Override
	public boolean render(@NonNull TessellatorGeneral tessellator, @NonNull WorldSource worldSource, @NonNull TilePosc tilePos) {
		float onepix = 0.0625f;
		float fourPix = onepix * 4;
		float twoPix = onepix * 2;

		float basketHeight = 1.0f;
		TilePos renderPos = new TilePos(tilePos.x(), tilePos.y(), tilePos.z());
		AABB bounds = (AABB) block.getBounds();
		bounds.set(onepix, fourPix, onepix, 0.9375f, fourPix + onepix, 0.9375f);
		this.renderStandardBlock(tessellator, bounds, renderPos);

		bounds.set(0.0f, fourPix, 0.0f, 1.0f, 1.0f, onepix);
		this.renderStandardBlock(tessellator, bounds, renderPos);
		bounds.set(0.0f, fourPix, 0.9375f, 1.0f, 1.0f, 1.0f);
		this.renderStandardBlock(tessellator, bounds, renderPos);
		bounds.set(0.0f, fourPix, onepix, onepix, 1.0f, 0.9375f);
		this.renderStandardBlock(tessellator, bounds, renderPos);
		bounds.set(0.9375f, fourPix, onepix, 1.0f, 1.0f, 0.9375f);
		this.renderStandardBlock(tessellator, bounds, renderPos);

		bounds.set(twoPix, 0, twoPix, 1.0f - twoPix, fourPix, 1.0f-twoPix);
		this.renderStandardBlock(tessellator, bounds, renderPos);


		BlockLogicChute block = renderBlocks.blockAccess.getBlockLogic(renderPos, BlockLogicChute.class);

		int height = block.getFillLevel(renderBlocks.blockAccess, renderPos);
		if (height > 0) {
			this.renderTopFace(tessellator, bounds, tilePos.x(), (float)(tilePos.y()) + 0.0625F + 0.0625F * (float)height, (double)tilePos.z(), this.fillTexture);
		}

		return true;
	}

	@Override
	public void renderBlockOnInventory(TessellatorGeneral tessellator, int metadata, byte brightness) {
		float onepix = 0.0625f;
		float fourPix = onepix * 4;
		float twoPix = onepix * 2;

		float basketHeight = 1.0f;
		AABB bounds = (AABB) block.getBounds();
		bounds.set(onepix, fourPix, onepix, 0.9375f, fourPix + onepix, 0.9375f);
		super.renderBlockWithBounds(tessellator, (AABBdc) bounds, metadata, brightness, 1);

		bounds.set(0.0f, fourPix, 0.0f, 1.0f, 1.0f, onepix);
		super.renderBlockWithBounds(tessellator, (AABBdc) bounds, metadata, brightness, 1);

		bounds.set(0.0f, fourPix, 0.9375f, 1.0f, 1.0f, 1.0f);
		super.renderBlockWithBounds(tessellator, (AABBdc) bounds, metadata, brightness, 1);

		bounds.set(0.0f, fourPix, onepix, onepix, 1.0f, 0.9375f);
		super.renderBlockWithBounds(tessellator, (AABBdc) bounds, metadata, brightness, 1);

		bounds.set(0.9375f, fourPix, onepix, 1.0f, 1.0f, 0.9375f);
		super.renderBlockWithBounds(tessellator, (AABBdc) bounds, metadata, brightness, 1);

		bounds.set(twoPix, 0, twoPix, 1.0f - twoPix, fourPix, 1.0f-twoPix);
		super.renderBlockWithBounds(tessellator, (AABBdc) bounds, metadata, brightness, 1);

		bounds.set(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		super.renderBlockWithBounds(tessellator, (AABBdc) bounds, metadata, brightness, 1);
	}

	protected IconCoordinate fillTexture = TextureRegistry.getTexture("minecraft:block/basket/fill");

	@Override
	public boolean shouldItemRender3d() {
		return true;
	}
}
