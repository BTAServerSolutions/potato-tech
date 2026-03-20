package goldenage.potatotech.blocks.entities;

import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.util.helper.Direction;

public class TileEntityStirlingEngine extends TileEntity {

	public int power = 0;

	@Override
	public void tick() {
		if (worldObj == null || worldObj.isClientSide) {
			return;
		}

		int previousPower = power;

		Direction direction = BlockLogicRotatable.getDirectionFromMeta(worldObj.getBlockMetadata(x, y, z));
		Direction directionCold = Direction.WEST;
		Direction directionHot = Direction.EAST;
		if (direction == Direction.WEST) {
			directionCold = Direction.SOUTH;
			directionHot = Direction.NORTH;
		} else if (direction == Direction.EAST) {
			directionCold = Direction.NORTH;
			directionHot = Direction.SOUTH;
		} else if (direction == Direction.SOUTH) {
			directionCold = Direction.EAST;
			directionHot = Direction.WEST;
		}

		int coldBlock = worldObj.getBlockId(x + directionCold.getOffsetX(), y + directionCold.getOffsetY(), z + directionCold.getOffsetZ());
		int hotBlock = worldObj.getBlockId(x + directionHot.getOffsetX(), y + directionHot.getOffsetY(), z + directionHot.getOffsetZ());

		int coldTemperature = 0;
		int hotTemperature = 0;

		if (coldBlock == Blocks.FLUID_WATER_STILL.id() || coldBlock == Blocks.FLUID_WATER_FLOWING.id()) {
			coldTemperature = -1;
		}

		if (coldBlock == Blocks.PERMAICE.id()) {
			coldTemperature = -2;
		}

		if (coldBlock == Blocks.ICE.id()) {
			coldTemperature = -3;
		}

		if (hotBlock == Blocks.FLUID_LAVA_STILL.id() || hotBlock == Blocks.FLUID_LAVA_FLOWING.id()) {
			hotTemperature = 3;
		}

		if (hotBlock == Blocks.FIRE.id()) {
			hotTemperature = 2;
		}

		power = Math.max(0, hotTemperature - coldTemperature);
		if (power != previousPower) {
			setChanged();
			worldObj.markBlockNeedsUpdate(x, y, z);
		}

		super.tick();
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}
