package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.BlockPipe;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Direction;

public class BlockModelPipe<T extends BlockPipe> extends BlockModelStandard<T> {

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
		{0.26f, 0.75f, 0.26f, 0.74f, 1.00f, 0.74f},

		{0.26f, 0.26f, 0.00f, 0.74f, 0.74f, 0.25f},
		{0.26f, 0.26f, 0.75f, 0.74f, 0.74f, 1.00f},

		{0.00f, 0.26f, 0.26f, 0.25f, 0.74f, 0.74f},
		{0.75f, 0.26f, 0.26f, 1.00f, 0.74f, 0.74f},
	};

	float[][] coordsExtract = {
		{0.1975f, 0.00f,   0.1975f, 0.8025f, 0.25f, 0.8025f},
		{0.1975f, 0.75f,   0.1975f, 0.8025f, 1.00f, 0.8025f},

		{0.1975f, 0.1975f, 0.00f,   0.8025f, 0.8025f, 0.25f},
		{0.1975f, 0.1975f, 0.75f,   0.8025f, 0.8025f, 1.00f},

		{0.00f,   0.1975f, 0.1975f, 0.25f, 0.8025f, 0.8025f},
		{0.75f,   0.1975f, 0.1975f, 1.00f, 0.8025f, 0.8025f},
	};

	float[][] coordsInsert = {
		{0.3225f, 0.00f,   0.3225f, 0.6775f, 0.25f, 0.6775f},
		{0.3225f, 0.75f,   0.3225f, 0.6775f, 1.00f, 0.6775f},

		{0.3225f, 0.3225f, 0.00f,   0.6775f, 0.6775f, 0.25f},
		{0.3225f, 0.3225f, 0.75f,   0.6775f, 0.6775f, 1.00f},

		{0.00f,   0.3225f, 0.3225f, 0.25f, 0.6775f, 0.6775f},
		{0.75f,   0.3225f, 0.3225f, 1.00f, 0.6775f, 0.6775f},
	};

	float[][] colors = {
		{1.0f, 1.0f, 1.0f}, // normal
		{0.05f, 0.05f, 0.05f}, // black
		{1.0f, 0.0f, 0.0f}, // red
		{0.1f, 0.8f, 0.1f}, // green
		{0.4f, 0.2f, 0.1f}, // brown
		{0.0f, 0.0f, 1.0f}, // blue
		{0.7f, 0.0f, 1.0f}, // purple
		{0.0f, 0.8f, 1.0f}, // cyan
		{0.7f, 0.7f, 0.7f}, // silver
		{0.2f, 0.2f, 0.2f}, // gray
		{1.2f, 0.7f, 0.7f}, // pink
		{0.2f, 1.5f, 0.2f}, // lime
		{1.0f, 1.5f, 0.0f}, // yellow
		{0.5f, 0.5f, 1.0f}, // lightblue
		{1.0f, 0.2f, 1.0f}, // magenta
		{1.0f, 0.7f, 0.0f}, // orange
		{5.0f, 5.0f, 5.0f}, // white
		// NND
		{0.79f, 0.19f, 0.29f}, // Crimson
		{0.43f, 0.12f, 0.11f}, // Maroon
		{0.64f, 0.73f, 0.66f}, // Ash Gray
		{0.56f, 0.55f, 0.17f}, // Olive
		{0.78f, 0.52f, 0.25f}, // Ochre
		{1.00f, 0.76f, 0.46f}, // Buff
		{0.38f, 0.88f, 0.76f}, // Verdigris
		{0.92f, 0.89f, 0.56f}, // Light Yellow
		{0.31f, 0.24f, 0.84f}, // Indigo
		{0.82f, 0.99f, 0.17f}, // Xanthic
		{0.76f, 0.41f, 0.20f}, // Cinnamon
		{0.18f, 0.15f, 0.51f}, // Navy Blue
		{0.45f, 0.18f, 0.54f}, // Royal Purple
		{0.24f, 0.54f, 0.44f}  // Viridian
	};

	public BlockModelPipe(Block block) {
		super(block);
	}

	public boolean renderNormal(TileEntityPipe pipe, Tessellator tessellator, int x, int y, int z) {
		this.block.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);

		this.renderStandardBlock(tessellator, block, x, y, z, 1, 1, 1);

		for (int i = 0; i < offsets.length; i++) {
			float[] coord = coords[i];
			float[] coordI = coordsInsert[i];
			float[] coordE = coordsExtract[i];

			TileEntity te = renderBlocks.blockAccess.getBlockTileEntity(x + offsets[i][0], y + offsets[i][1], z +offsets[i][2]);
			if (((te instanceof TileEntityPipe
				&& ((TileEntityPipe)te).modeBySide[Direction.getDirectionById(i).getOpposite().getId()] != 3)
				|| te instanceof IInventory) && pipe.modeBySide[i] != 3)
			{
				float[] color = colors[pipe.colorBySide[i]];

				if (pipe.modeBySide[i] == 1) {
					this.block.setBlockBounds(coordI[0], coordI[1], coordI[2], coordI[3], coordI[4], coordI[5]);
				} else if (pipe.modeBySide[i] == 2) {
					this.block.setBlockBounds(coordE[0], coordE[1], coordE[2], coordE[3], coordE[4], coordE[5]);
				} else {
					this.block.setBlockBounds(coord[0], coord[1], coord[2], coord[3], coord[4], coord[5]);
				}

				this.renderStandardBlock(tessellator, block, x, y, z, color[0], color[1], color[2]);
			}
		}

		this.block.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
		return true;
	}

	@Override
	public boolean render(Tessellator tessellator, int x, int y, int z) {
		TileEntity pipe = renderBlocks.blockAccess.getBlockTileEntity(x, y, z);

		return renderNormal((TileEntityPipe) pipe, tessellator, x, y, z);
	}
}
