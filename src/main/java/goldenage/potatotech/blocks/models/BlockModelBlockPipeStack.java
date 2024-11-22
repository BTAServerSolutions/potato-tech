package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.BlockPipe;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.WorldSource;

public class BlockModelBlockPipeStack <T extends Block> extends BlockModelStandard<T> {

	public BlockModelBlockPipeStack(Block block) {
		super(block);
	}

	@Override
	public boolean shouldSideBeRendered(WorldSource blockAccess, int x, int y, int z, int side) {
		return true;//super.shouldSideBeRendered(blockAccess, x, y, z, side);
	}
}
