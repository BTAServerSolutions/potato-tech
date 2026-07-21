package goldenage.potatotech.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockLogicTestAreaMaker extends BlockLogic {
	public BlockLogicTestAreaMaker(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
		super.onBlockPlacedByMob(world, x, y, z, side, mob, xPlaced, yPlaced);

		int startX = world.getChunkFromBlockCoords(x, z).pos.x * 16;
		int startZ = world.getChunkFromBlockCoords(x, z).pos.z * 16;
		int endX = startX + 15;
		int endZ = startZ + 15;

		for (int yi = y + 1; yi < 256; yi++) {
			for (int zi = startZ; zi <= endZ; zi++) {
				for (int xi = startX; xi <= endX; xi++) {
					world.setBlockWithNotify(xi, yi, zi, 0);
				}
			}
		}

		for (int yi = 0; yi <= y; yi++) {
			for (int zi = startZ; zi <= endZ; zi++) {
				for (int xi = startX; xi <= endX; xi++) {
					int id = (zi & 1) == (xi & 1) ? Blocks.BASALT_POLISHED.id() : Blocks.STONE_POLISHED.id();
					if (xi == 0 || yi == 0 || xi == endX || zi == endZ) {
						id = (zi & 1) == (xi & 1) ? Blocks.GRANITE_POLISHED.id() : Blocks.LIMESTONE_POLISHED.id();
					}
					world.setBlockWithNotify(xi, yi, zi, id);
				}
			}
		}
	}
}
