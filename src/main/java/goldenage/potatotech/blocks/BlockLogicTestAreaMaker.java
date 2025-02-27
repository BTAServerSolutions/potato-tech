package goldenage.potatotech.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
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

		int startX = world.getChunkFromBlockCoords(x, z).xPosition * 16;
		int startZ = world.getChunkFromBlockCoords(x, z).zPosition * 16;
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
					world.setBlockWithNotify(xi, yi, zi, (zi & 1) == (xi & 1) ? 1 : 2);
				}
			}
		}
	}
}
