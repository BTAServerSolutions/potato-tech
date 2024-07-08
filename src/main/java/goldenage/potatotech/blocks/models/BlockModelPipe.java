package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.BlockPipe;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.IInventory;

public class BlockModelPipe<T extends BlockPipe> extends BlockModelStandard<T> {
	public BlockModelPipe(Block block) {
		super(block);
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		this.block.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);

		int meta = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
		int type = meta & 3;

		float r = 1.0f;
		float g = 1.0f;
		float b = 1.0f;
		if (type == 1) {
			g = 0.5f;
			b = 0.0f;
		} else if (type == 2) {
			r = 0.28f;
			g = 0.5f;
		}

		this.renderStandardBlock(tessellator, block, x, y, z, r, g, b);

		int[][] offsets = {
			{ 0, -1,  0},
			{ 0,  1,  0},
			{ 0,  0, -1},
			{ 0,  0,  1},
			{-1,  0,  0},
			{ 1,  0,  0},
		};

		float[][] coords = {
			{0.26f, 0.00f, 0.26f, 0.74f, 0.25f, 0.74f},
			{0.26f, 0.74f, 0.26f, 0.74f, 1.00f, 0.74f},

			{0.26f, 0.26f, 0.00f, 0.74f, 0.74f, 0.25f},
			{0.26f, 0.26f, 0.74f, 0.74f, 0.74f, 1.00f},

			{0.00f, 0.26f, 0.26f, 0.25f, 0.74f, 0.74f},
			{0.74f, 0.26f, 0.26f, 1.00f, 0.74f, 0.74f},
		};

		for (int i = 0; i < offsets.length; i++) {
			float[] coord = coords[i];

            TileEntity te = renderBlocks.blockAccess.getBlockTileEntity(x + offsets[i][0], y + offsets[i][1], z +offsets[i][2]);
            if(te instanceof TileEntityPipe || te instanceof IInventory && type != 0) {
				this.block.setBlockBounds(coord[0], coord[1], coord[2], coord[3], coord[4], coord[5]);

				if (te instanceof TileEntityPipe) {
					this.renderStandardBlock(tessellator, block, x, y, z, 1, 1, 1);
				} else {
					this.renderStandardBlock(tessellator, block, x, y, z, r, g, b);
				}
            }
		}

		this.block.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		return true;
	}
}
