package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import org.jetbrains.annotations.NotNull;

public class TileEntitySequencer extends TileEntity {
	public boolean stillValid(Player player) {
		if (worldObj == null || worldObj.getTileEntity(tilePos.x, tilePos.y, tilePos.z) != this) {
			return false;
		}
		return player.distanceToSqr((double) tilePos.x + 0.5, (double) tilePos.y + 0.5, (double) tilePos.z + 0.5) <= 64.0;
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

	@Override
	public void readAdditionalData(@NotNull CompoundTag compoundTag) {

	}

	@Override
	public void writeAdditionalData(@NotNull CompoundTag compoundTag) {

	}
}

