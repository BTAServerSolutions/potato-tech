package goldenage.potatotech.blocks.models;

import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.helper.Sides;

public class BlockModelCrusher<T extends Block> extends BlockModelStandard {
	public BlockModelCrusher(Block block) {
		super(block);
	}

	public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int data) {
		int index = Sides.orientationLookUp[6 * (data & 7) + side.getId()];
		return this.atlasIndices[index];
	}
}
