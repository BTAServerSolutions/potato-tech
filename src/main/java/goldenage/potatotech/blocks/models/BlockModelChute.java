package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.BlockLogicChute;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBdc;

public class BlockModelChute<T extends BlockLogic> extends BlockModelStandard<T> {
	public BlockModelChute(Block<T> block) {
		super(block);
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		float onepix = 0.0625f;
		float fourPix = onepix * 4;
		float twoPix = onepix * 2;

		float basketHeight = 1.0f;
		AABBdc bounds = block.getBounds();
		bounds.set(onepix, fourPix, onepix, 0.9375f, fourPix + onepix, 0.9375f);
		this.renderBlockWithBounds(tessellator, bounds, x, y, z);

		bounds.set(0.0f, fourPix, 0.0f, 1.0f, 1.0f, onepix);
		this.renderStandardBlock(tessellator, bounds, x, y, z);
		bounds.set(0.0f, fourPix, 0.9375f, 1.0f, 1.0f, 1.0f);
		this.renderStandardBlock(tessellator, bounds, x, y, z);
		bounds.set(0.0f, fourPix, onepix, onepix, 1.0f, 0.9375f);
		this.renderStandardBlock(tessellator, bounds, x, y, z);
		bounds.set(0.9375f, fourPix, onepix, 1.0f, 1.0f, 0.9375f);
		this.renderStandardBlock(tessellator, bounds, x, y, z);

		bounds.set(twoPix, 0, twoPix, 1.0f - twoPix, fourPix, 1.0f-twoPix);
		this.renderStandardBlock(tessellator, bounds, x, y, z);


		BlockLogicChute block = renderBlocks.blockAccess.getBlockLogic(x, y, z, BlockLogicChute.class);

		int height = block.getFillLevel(renderBlocks.blockAccess, x, y, z);
		if (height > 0) {
			this.renderTopFace(tessellator, bounds, x, (float)(y) + 0.0625F + 0.0625F * (float)height, (double)z, this.fillTexture);
		}

		return true;
	}

	@Override
	public void renderBlockOnInventory(Tessellator tessellator, int metadata, float brightness, @Nullable Integer lightmapCoordinate) {
		float onepix = 0.0625f;
		float fourPix = onepix * 4;
		float twoPix = onepix * 2;

		float basketHeight = 1.0f;
		AABB bounds = block.getBounds();
		bounds.set(onepix, fourPix, onepix, 0.9375f, fourPix + onepix, 0.9375f);
		super.renderBlockWithBounds(tessellator, bounds, metadata, brightness, 1.0f, lightmapCoordinate);

		bounds.set(0.0f, fourPix, 0.0f, 1.0f, 1.0f, onepix);
		super.renderBlockWithBounds(tessellator, bounds, metadata, brightness, 1.0f, lightmapCoordinate);

		bounds.set(0.0f, fourPix, 0.9375f, 1.0f, 1.0f, 1.0f);
		super.renderBlockWithBounds(tessellator, bounds, metadata, brightness, 1.0f, lightmapCoordinate);

		bounds.set(0.0f, fourPix, onepix, onepix, 1.0f, 0.9375f);
		super.renderBlockWithBounds(tessellator, bounds, metadata, brightness, 1.0f, lightmapCoordinate);

		bounds.set(0.9375f, fourPix, onepix, 1.0f, 1.0f, 0.9375f);
		super.renderBlockWithBounds(tessellator, bounds, metadata, brightness, 1.0f, lightmapCoordinate);

		bounds.set(twoPix, 0, twoPix, 1.0f - twoPix, fourPix, 1.0f-twoPix);
		super.renderBlockWithBounds(tessellator, bounds, metadata, brightness, 1.0f, lightmapCoordinate);

		bounds.set(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		super.renderBlockWithBounds(tessellator, bounds, metadata, brightness, 1.0f, lightmapCoordinate);
	}

	protected IconCoordinate fillTexture = TextureRegistry.getTexture("minecraft:block/basket/fill");

	@Override
	public boolean shouldItemRender3d() {
		return true;
	}
}
