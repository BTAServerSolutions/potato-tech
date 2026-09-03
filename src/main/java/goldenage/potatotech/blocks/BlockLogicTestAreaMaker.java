package goldenage.potatotech.blocks;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;

import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockLogicTestAreaMaker extends BlockLogic {
	public BlockLogicTestAreaMaker(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onPlacedByMob(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, @NotNull Mob mob, double xPlaced, double yPlaced) {
		super.onPlacedByMob(world, tilePos, side, mob, xPlaced, yPlaced);

		int startX = world.getChunk(tilePos).pos.x * 16;
		int startZ = world.getChunk(tilePos).pos.z * 16;
		int endX = startX + 15;
		int endZ = startZ + 15;

		for (int yi = tilePos.y() + 1; yi < 256; yi++) {
			for (int zi = startZ; zi <= endZ; zi++) {
				for (int xi = startX; xi <= endX; xi++) {
					world.setBlockTypeNotify(new TilePos(xi, yi, zi), Blocks.AIR);
				}
			}
		}

		for (int yi = 0; yi <= tilePos.y(); yi++) {
			for (int zi = startZ; zi <= endZ; zi++) {
				for (int xi = startX; xi <= endX; xi++) {
					int id = (zi & 1) == (xi & 1) ? Blocks.BASALT_POLISHED.id() : Blocks.STONE_POLISHED.id();
					if (xi == 0 || yi == 0 || xi == endX || zi == endZ) {
						id = (zi & 1) == (xi & 1) ? Blocks.GRANITE_POLISHED.id() : Blocks.LIMESTONE_POLISHED.id();
					}
					world.setBlockTypeNotify(new TilePos(xi, yi, zi), Blocks.getBlock(id));
				}
			}
		}
	}
}
