package goldenage.potatotech.blocks.entities;

import goldenage.potatotech.PotatoTech;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Direction;

public class TileEntityStirlingEngine extends TileEntity {

	public int power = 0;

	@Override
	public void tick() {
		Direction direction = BlockLogicRotatable.getDirectionFromMeta(worldObj.getBlockMetadata(x, y, z));
		Direction directionCold = Direction.EAST;
		Direction directionHot = Direction.WEST;
		if (direction == Direction.WEST) {
			directionCold = Direction.SOUTH;
			directionHot = Direction.NORTH;
		} else if (direction == Direction.EAST) {
			directionHot = Direction.SOUTH;
			directionCold = Direction.NORTH;
		}

		int coldBlock = worldObj.getBlockId(x + directionCold.getOffsetX(), y + directionCold.getOffsetY(), z + directionCold.getOffsetZ());
		int hotBlock = worldObj.getBlockId(x + directionHot.getOffsetX(), y + directionHot.getOffsetY(), z + directionHot.getOffsetZ());

		PotatoTech.LOGGER.info("cold id is: " + coldBlock + " | hot id is: " + hotBlock);
		power = 0;
		if (hotBlock == Blocks.FLUID_LAVA_STILL.id() && coldBlock == Blocks.FLUID_WATER_STILL.id())  {
			power = 1;
		}

		super.tick();
	}
}
