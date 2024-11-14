package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import goldenage.potatotech.PotatoTech;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.IDispensable;
import net.minecraft.core.item.ItemPlaceable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntityCrusher extends TileEntity implements IInventory {

	private ItemStack[] contents = new ItemStack[9];

	private final Random random = new Random();

	public TileEntityCrusher() {
	}

    @Override
	public int getSizeInventory() {
		return 9;
	}

    @Override
	public ItemStack getStackInSlot(int i) {
		return this.contents[i];
	}

    @Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.contents[i] != null) {
			ItemStack itemstack1;
			if (this.contents[i].stackSize <= j) {
				itemstack1 = this.contents[i];
				this.contents[i] = null;
            } else {
				itemstack1 = this.contents[i].splitStack(j);
				if (this.contents[i].stackSize <= 0) {
					this.contents[i] = null;
				}
            }
            this.onInventoryChanged();
            return itemstack1;
        } else {
			return null;
		}
	}

	public ItemStack getRandomStackFromInventory() {
		int i = -1;

		ArrayList<Integer> available_items = new ArrayList<>();

		for(int k = 0; k < this.contents.length; ++k) {
			if (this.contents[k] != null) {
				available_items.add(k);
			}
		}

		if (!available_items.isEmpty()) {
			i = available_items.get(random.nextInt(available_items.size()));
		}

		if (i >= 0 && (this.getStackInSlot(i).getItem().id < Block.blocksList.length || this.getStackInSlot(i).getItem() instanceof ItemPlaceable)) {
			return this.decrStackSize(i, 1);
		} else {
			return null;
		}
	}

    @Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.contents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public String getInvName() {
		return "BlockCrusher";
	}

	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		this.contents = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			if (j < this.contents.length) {
				this.contents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
			}
		}

	}

    @Override
	public void writeToNBT(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ListTag nbttaglist = new ListTag();

		for(int i = 0; i < this.contents.length; ++i) {
			if (this.contents[i] != null) {
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte)i);
				this.contents[i].writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
		}

		nbttagcompound.put("Items", nbttaglist);
	}

    @Override
	public int getInventoryStackLimit() {
		return 64;
	}

    @Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (this.worldObj.getBlockTileEntity(this.x, this.y, this.z) != this) {
			return false;
		} else {
			return entityplayer.distanceToSqr((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
		}
	}

    @Override
	public void sortInventory() {
	}
}
