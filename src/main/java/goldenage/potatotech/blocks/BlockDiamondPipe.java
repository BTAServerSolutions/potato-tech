package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntityDiamondPipe;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;

public class BlockDiamondPipe extends BlockPipe {
	public BlockDiamondPipe(String key, int id, Material material) {
		super(key, id, material);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityDiamondPipe();
	}
}
