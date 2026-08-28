package goldenage.potatotech.blocks.models;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;

public class BlockModelChute<T extends BlockLogic> extends BlockModelGeneric<T> {
	public BlockModelChute(Block<T> block) {
		super(block, BlockModelDispatcher.loadDataModel("potatotech:block/chute").asModel());
	}
}
