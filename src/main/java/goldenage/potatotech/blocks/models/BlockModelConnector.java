package goldenage.potatotech.blocks.models;

import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class BlockModelConnector <T extends BlockLogic> extends BlockModelStandard<T> {
	public BlockModelConnector(Block<T> block) {
		super(block);
	}

	private static float[] getConnectorColor(int i ) {
		float r = 0.41f;
		float g = 0.23f;
		float b = 0.18f;
		if (i % 2 == 1) {
			r *= 1.1f;
			g *= 1.1f;
			b *= 1.1f;
		}
		if (i == 0 || i == 8) {
			r = 0.9f;
			g = 0.9f;
			b = 0.9f;
		}

		return new float[]{r, g, b};
	}

	/*
	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		int meta = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
		Side side = Side.getSideById(meta & 7);
		float pixelSize = 1.0f / 16.0f;

		AABB bounds = block.getBounds();

		if (side == Side.TOP) {
			for (int i = 0; i < 9; i++) {
				float m = (i % 2 == 0) ? pixelSize * 6 : pixelSize * 5;
				bounds.set(m, pixelSize * i, m, 1 - m, pixelSize * (i + 1), 1 - m);

				float[] color = getConnectorColor(i);
				this.renderStandardBlock(tessellator, bounds, x, y, z, color[0], color[1], color[2]);
			}
		} else if (side == Side.BOTTOM) {
			for (int i = 0; i < 9; i++) {
				float m = (i % 2 == 0) ? pixelSize * 6 : pixelSize * 5;
				bounds.set(m, 1 - pixelSize * (i + 1), m, 1 - m, 1 - pixelSize * i, 1 - m);

				float[] color = getConnectorColor(i);
				this.renderStandardBlock(tessellator, bounds, x, y, z, color[0], color[1], color[2]);
			}
		} else if (side == Side.NORTH) {
			for (int i = 0; i < 9; i++) {
				float m = (i % 2 == 0) ? pixelSize * 6 : pixelSize * 5;
				bounds.set(m, m, 1 - pixelSize * (i + 1), 1 - m, 1 - m, 1 - pixelSize * i);

				float[] color = getConnectorColor(i);
				this.renderStandardBlock(tessellator, bounds, x, y, z, color[0], color[1], color[2]);
			}
		} else if (side == Side.SOUTH) {
			for (int i = 0; i < 9; i++) {
				float m = (i % 2 == 0) ? pixelSize * 6 : pixelSize * 5;
				bounds.set(m, m, pixelSize * i, 1 - m, 1 - m, pixelSize * (i + 1));

				float[] color = getConnectorColor(i);
				this.renderStandardBlock(tessellator, bounds, x, y, z, color[0], color[1], color[2]);
			}
		} else if (side == Side.EAST) {
			for (int i = 0; i < 9; i++) {
				float m = (i % 2 == 0) ? pixelSize * 6 : pixelSize * 5;
				bounds.set(pixelSize * i, m, m,  pixelSize * (i + 1), 1 - m, 1 - m);

				float[] color = getConnectorColor(i);
				this.renderStandardBlock(tessellator, bounds, x, y, z, color[0], color[1], color[2]);
			}
		} else {
			for (int i = 0; i < 9; i++) {
				float m = (i % 2 == 0) ? pixelSize * 6 : pixelSize * 5;
				bounds.set(1 - pixelSize * (i + 1), m, m, 1 - pixelSize * i, 1 - m, 1 - m);

				float[] color = getConnectorColor(i);
				this.renderStandardBlock(tessellator, bounds, x, y, z, color[0], color[1], color[2]);
			}
		}

		//block.setBlockBounds(pixelSize * 5, 0, pixelSize * 5, 1 - pixelSize * 5, pixelSize * 9, 1 - pixelSize * 5);
		return true;
	}
	 */
}
