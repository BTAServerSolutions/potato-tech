package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.client.gui.ScreenMainMenu;
import net.minecraft.client.gui.container.ScreenContainer;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.menu.MenuContainer;
import org.jetbrains.annotations.Nullable;

class FilterPaintContainer implements Container {
	public ItemStack[] contents = new ItemStack[9];

	@Override
	public int getContainerSize() {
		return 9;
	}

	@Override
	public ItemStack getItem(int i) {
		return contents[i];
	}

	@Override
	public ItemStack removeItem(int i, int j) {
		if (this.contents[i] != null) {
			if (this.contents[i].stackSize <= j) {
				ItemStack itemstack = this.contents[i];
				this.contents[i] = null;
				this.setChanged();
				return itemstack;
			}
			ItemStack itemstack1 = this.contents[i].splitStack(j);
			if (this.contents[i].stackSize <= 0) {
				this.contents[i] = null;
			}
			this.setChanged();
			return itemstack1;
		}
		return null;
	}

	@Override
	public void setItem(int i, ItemStack itemStack) {
		this.contents[i] = itemStack;
		if (itemStack != null && itemStack.stackSize > this.getMaxStackSize()) {
			itemStack.stackSize = this.getMaxStackSize();
		}
		this.setChanged();
	}

	@Override
	public String getNameTranslationKey() {
		return "container.filer_paint";
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setChanged() {

	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void sortContainer() {

	}

	@Override
	public boolean locked(int index) {
		return false;
	}
}

public class TileEntityFilter extends TileEntity implements Container {
	private ItemStack[] filterContents;

	public TileEntityFilter(int size) {
		filterContents = new ItemStack[size];
	}

	public TileEntityFilter() {
		this(9);
	}
	public FilterPaintContainer paintInventory = new FilterPaintContainer();

	@Override
	public int getContainerSize() {
		return filterContents.length;
	}

	@Override
	public ItemStack getItem(int i) {
		return this.filterContents[i];
	}

	public short getColorInSlot(int i) {
		ItemStack stack = paintInventory.getItem(i);
		if (stack == null) {
			return 0;
		} else if (stack.itemID == Items.DYE.id) {
			return (short)(stack.getMetadata() + 1);
		} else if (stack.getItem().getClass().getName().equals("goocraft4evr.nonamedyes.item.ItemModDye")) {
			return (short)(stack.getMetadata() + 17);
		}
		return 0;
	}

	@Override
	public ItemStack removeItem(int i, int j) {
		if (this.filterContents[i] != null) {
			if (this.filterContents[i].stackSize <= j) {
				ItemStack itemstack = this.filterContents[i];
				this.filterContents[i] = null;
				this.setChanged();
				return itemstack;
			}
			ItemStack itemstack1 = this.filterContents[i].splitStack(j);
			if (this.filterContents[i].stackSize <= 0) {
				this.filterContents[i] = null;
			}
			this.setChanged();
			return itemstack1;
		}
		return null;
	}

	public boolean canInsertItem(ItemStack itemStack) {
		for (int i = 0; i < 9; i++)	{
			if (filterContents[i] != null && filterContents[i].canStackWith(itemStack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		this.filterContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getMaxStackSize()) {
			itemstack.stackSize = this.getMaxStackSize();
		}
		this.setChanged();
	}

	@Override
	public String getNameTranslationKey() {
		return "container.filter.name";
	}

	@Override
	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("Items");
		this.filterContents = new ItemStack[this.getContainerSize()];
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag) nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xFF;
			if (j >= this.filterContents.length) continue;
			this.filterContents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
		}

		ListTag nbttaglist2 = nbttagcompound.getList("ItemsC");
		for (int i = 0; i < nbttaglist2.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag) nbttaglist2.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xFF;
			if (j >= this.paintInventory.contents.length) continue;
			this.paintInventory.contents[j] = ItemStack.readItemStackFromNbt(nbttagcompound1);
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ListTag nbttaglist = new ListTag();
		for (int i = 0; i < this.filterContents.length; ++i) {
			if (this.filterContents[i] == null) continue;
			CompoundTag nbttagcompound1 = new CompoundTag();
			nbttagcompound1.putByte("Slot", (byte) i);
			this.filterContents[i].writeToNBT(nbttagcompound1);
			nbttaglist.addTag(nbttagcompound1);
		}
		nbttagcompound.put("Items", nbttaglist);

		ListTag nbttaglist2 = new ListTag();
		for (int i = 0; i < this.paintInventory.contents.length; ++i) {
			if (this.paintInventory.contents[i] == null) continue;
			CompoundTag nbttagcompound1 = new CompoundTag();
			nbttagcompound1.putByte("Slot", (byte) i);
			this.paintInventory.contents[i].writeToNBT(nbttagcompound1);
			nbttaglist2.addTag(nbttagcompound1);
		}
		nbttagcompound.put("ItemsC", nbttaglist2);
	}


	@Override
	public int getMaxStackSize() {
		return 64;
	}


	@Override
	public boolean stillValid(Player player) {
		if (worldObj.getTileEntity(this.x, this.y, this.z) != this) {
			return false;
		}
		return player.distanceToSqr((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) <= 64.0;
	}

	@Override
	public void sortContainer() {

	}
}
