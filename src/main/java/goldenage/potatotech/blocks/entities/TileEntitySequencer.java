package goldenage.potatotech.blocks.entities;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;

public class TileEntitySequencer extends TileEntity {
	public boolean stillValid(Player player) {
		if (worldObj == null || worldObj.getTileEntity(this.x, this.y, this.z) != this) {
			return false;
		}
		return player.distanceToSqr((double) this.x + 0.5, (double) this.y + 0.5, (double) this.z + 0.5) <= 64.0;
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}

