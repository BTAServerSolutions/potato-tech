package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

public class TileEntitySequencer extends TileEntity {
	public boolean stillValid(Player player) {
		TilePos validPos = new TilePos(this.tilePos.x, this.tilePos.y, this.tilePos.z);
		if (worldObj == null || worldObj.getTileEntity(validPos) != this) {
			return false;
		}
		return player.distanceToSqr((double) this.tilePos.x + 0.5, (double) this.tilePos.y + 0.5, (double) this.tilePos.z + 0.5) <= 64.0;
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

