package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.BlockLogicChute;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.block.model.generic.BlockModelGenericBasket;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class BlockModelChute<T extends BlockLogic> extends BlockModelStandard<T> {
	public BlockModelChute(Block<T> block) {
		super(block);
	}


	@Override
	public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
		float onepix = 0.0625f;
		float fourPix = onepix * 4;
		float twoPix = onepix * 2;

		float basketHeight = 1.0f;
		AABBdc bounds;
		bounds = new AABBd(onepix, fourPix, onepix, 0.9375f, fourPix + onepix, 0.9375f);
		super.renderBlockWithBounds(tessellator, bounds, 0, (byte) 0, 0);

		bounds = new AABBd(0.0f, fourPix, 0.0f, 1.0f, 1.0f, onepix);
		super.renderBlockWithBounds(tessellator, bounds, 0, (byte) 0, 0);
		bounds = new AABBd(0.0f, fourPix, 0.9375f, 1.0f, 1.0f, 1.0f);
		super.renderBlockWithBounds(tessellator, bounds, 0, (byte) 0, 0);
		bounds = new AABBd(0.0f, fourPix, onepix, onepix, 1.0f, 0.9375f);
		super.renderBlockWithBounds(tessellator, bounds, 0, (byte) 0, 0);
		bounds = new AABBd(0.9375f, fourPix, onepix, 1.0f, 1.0f, 0.9375f);
		super.renderBlockWithBounds(tessellator, bounds, 0, (byte) 0, 0);

		bounds = new AABBd(twoPix, 0, twoPix, 1.0f - twoPix, fourPix, 1.0f-twoPix);
		super.renderBlockWithBounds(tessellator, bounds, 0, (byte) 0, 0);

		BlockLogicChute block = worldSource.getBlockLogic(tilePos, BlockLogicChute.class);

		int height = block.getFillLevel(worldSource, tilePos.x(), tilePos.y(), tilePos.z());
		if (height > 0) {
			//this.renderTopFace(tessellator, bounds, tilePos.x(), (float)(tilePos.y()) + 0.0625F + 0.0625F * (float)height, (double)tilePos.z(), this.fillTexture);
		}

		return true;
	}

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
