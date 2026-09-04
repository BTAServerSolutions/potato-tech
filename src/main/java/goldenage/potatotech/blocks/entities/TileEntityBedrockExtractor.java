package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PTItems;
import goldenage.potatotech.PipeStack;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.Util;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import org.jetbrains.annotations.NotNull;


public class TileEntityBedrockExtractor extends TileEntity {
	public int energy;

	public static int getEnergyCapacity() {
		return Math.max(1, PotatoTech.config.getInt("bedrock_extractor_energy_required"));
	}

	public int addEnergy(int amount) {
		if (amount <= 0 || !hasValidDrillAssembly()) {
			return 0;
		}
		int accepted = Math.min(amount, getEnergyCapacity() - energy);
		if (accepted > 0) {
			energy += accepted;
			setChanged();
		}
		return accepted;
	}

	public boolean hasValidDrillAssembly() {
		return worldObj != null
			&& worldObj.getBlockId(tilePos.x, tilePos.y - 1, tilePos.z) == PTBlocks.bedrockDrill.id()
			&& worldObj.getBlockId(tilePos.x, tilePos.y - 2, tilePos.z) == Blocks.BEDROCK.id();
	}

	@Override
	public void tick() {
		if (worldObj == null || worldObj.isClientSide || energy < getEnergyCapacity() || !hasValidDrillAssembly()) {
			return;
		}

		ItemStack dust = new ItemStack(PTItems.bedrockDust);
		TileEntity above = worldObj.getTileEntity(tilePos.x, tilePos.y + 1, tilePos.z);
		if (above instanceof Container container) {
			if (Util.insertOnInventoryNoCatch(container, dust, Direction.DOWN)) {
				finishCycle();
			}
			return;
		} else if (above instanceof TileEntityPipe pipe) {
			if (pipe.modeBySide[Direction.DOWN.id] < 2
				&& pipe.stacks[Direction.DOWN.id + 1] == null) {
				pipe.stacks[Direction.DOWN.id + 1] = new PipeStack(dust, Direction.DOWN, 0);
				pipe.setChanged();
				worldObj.markBlockNeedsUpdate(pipe.tilePos.x, pipe.tilePos.y, pipe.tilePos.z);
				finishCycle();
			}
			return;
		} else {
			worldObj.dropItem(tilePos.x, tilePos.y + 1, tilePos.z, dust);
			finishCycle();
		}
	}

	private void finishCycle() {
		energy = 0;
		setChanged();
		worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
	}
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

	@Override
	public void readAdditionalData(@NotNull CompoundTag compoundTag) {
		energy = Math.min(compoundTag.getInteger("energy"), getEnergyCapacity());
	}

	@Override
	public void writeAdditionalData(@NotNull CompoundTag compoundTag) {
		compoundTag.putInt("energy", energy);
	}
}
