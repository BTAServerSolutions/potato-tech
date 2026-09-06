package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PipeStack;
import goldenage.potatotech.Util;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.inventory.InventorySorter;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBd;

import java.util.List;
import java.util.Random;

public class TileEntityChute extends TileEntity implements Container {

	public int numUnitsInside = 0;
	public int maxDropTimer = 10;
	public int dropTimer = 0;
	public ItemStack[] contents = new ItemStack[27];

	@Override
	public int getContainerSize() {
		return this.contents.length;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.contents[index];
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack removed = null;
		ItemStack stack = this.contents[index];

		if (stack != null && count > 0) {
			if (stack.stackSize <= count) {
				removed = stack;
				this.contents[index] = null;
			} else {
				removed = stack.splitStack(count);
				if (stack.stackSize <= 0) {
					this.contents[index] = null;
				}
			}
			this.setChanged();
		}

		return removed;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.contents[index] = stack;
		if (stack != null && stack.stackSize > this.getMaxStackSize()) {
			stack.stackSize = this.getMaxStackSize();
		}
		this.setChanged();
	}

	@Override
	public String getNameTranslationKey() {
		return "container.chute.name";
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		this.updateNumUnits();
		if (this.worldObj != null) {
			this.worldObj.notifyBlockChange(this.tilePos, PTBlocks.chute);
		}
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		if (this.worldObj.getTileEntity(tilePos) != this) {
			return false;
		}
		return player.distanceToSqr((double)this.tilePos.x + 0.5, (double)this.tilePos.y + 0.5, (double)this.tilePos.z + 0.5) <= 64.0;
	}

	@Override
	public void sort() {
		InventorySorter.sortInventory(this.contents);
		this.setChanged();
	}

	public void dropAllItems() {
		Random rand = new Random();
		for (int i = 0; i < this.contents.length; i++) {
			if (this.contents[i] != null) {
				this.dropItemStack(rand, this.contents[i]);
				this.contents[i] = null;
			}
		}
		this.setChanged();
	}

	private void updateNumUnits() {
		this.numUnitsInside = 0;
		for (ItemStack stack : this.contents) {
			if (stack != null) {
				this.numUnitsInside += this.getItemSizeUnits(stack.getItem()) * stack.stackSize;
			}
		}
	}

	private int getItemSizeUnits(Item item) {
		return 64 / item.getItemStackLimit(null);
	}

	private void dropItemStack(Random rand, ItemStack itemstack) {
		float f = rand.nextFloat() * 0.8f + 0.1f;
		float f1 = rand.nextFloat() * 0.8f + 0.1f;
		float f2 = rand.nextFloat() * 0.8f + 0.1f;
		EntityItem entityitem = new EntityItem(this.worldObj, (float)this.tilePos.x + f, (float)this.tilePos.y + f1, (float)this.tilePos.z + f2, itemstack);
		entityitem.xd = (float)rand.nextGaussian() * 0.05f;
		entityitem.yd = (float)rand.nextGaussian() * 0.05f + 0.25f;
		entityitem.zd = (float)rand.nextGaussian() * 0.05f;
		this.worldObj.entityJoinedWorld(entityitem);
	}

	public int getNumUnitsInside() {
		return this.numUnitsInside;
	}

	public int getMaxUnits() {
		return 1728;
	}

	public ItemStack removeItems(int count) {
		ItemStack removed = null;

		if (count > 0) {
			for (int i = 0; i < this.contents.length; i++) {
				if (this.contents[i] != null) {
					removed = this.removeItem(i, Math.min(count, this.contents[i].getMaxStackSize()));
					break;
				}
			}
		}

		return removed;
	}

	@Override
	public void readAdditionalData(@NotNull CompoundTag tag) {
		ListTag itemsTag = tag.getList("Items");
		this.contents = new ItemStack[this.getContainerSize()];

		for (int i = 0; i < itemsTag.tagCount(); ++i) {
			CompoundTag itemTag = (CompoundTag)itemsTag.tagAt(i);

			if (itemTag.containsKey("Slot")) {
				int slot = itemTag.getByte("Slot") & 0xFF;
				if (slot < this.contents.length) {
					this.contents[slot] = ItemStack.readItemStackFromNbt(itemTag);
				}
			} else {
				int itemId = itemTag.getShort("id");
				int metadata = itemTag.getShort("Damage");
				CompoundTag data = itemTag.getCompound("Data");
				Item item = Item.itemsList[itemId];
				int count = itemTag.getShort("Count");

				for (int slot = 0; slot < this.contents.length && count > 0; slot++) {
					if (this.contents[slot] == null) {
						int stackSize = Math.min(count, item.getItemStackLimit(null));
						this.contents[slot] = new ItemStack(itemId, stackSize, metadata, data);
						count -= stackSize;
					}
				}
			}
		}
		this.updateNumUnits();
	}

	@Override
	public void tick() {
		if (this.worldObj == null || this.worldObj.isClientSide) {
			return;
		}

		AABBd aabb = new AABBd(this.tilePos.x, this.tilePos.y, this.tilePos.z, this.tilePos.x + 1, this.tilePos.y + 2, this.tilePos.z + 1);
		List<EntityItem> entities = this.worldObj.getEntitiesWithinAABB(EntityItem.class, aabb);
		if (!entities.isEmpty()) {
			for (Entity e : entities) {
				EntityItem entity = (EntityItem)e;
				if (entity.basketPickupDelay != 0) {
					if (entity.basketPickupDelay > 0) entity.basketPickupDelay--;
					continue;
				}
				if (entity.item == null || entity.item.stackSize <= 0) continue;
				this.importItemStack(entity.item);
				if (entity.item.stackSize > 0) continue;
				entity.item.stackSize = 0;
				e.outOfWorld();
			}
		}

		TileEntity outTe = this.worldObj.getTileEntity(this.tilePos.x, this.tilePos.y - 1, this.tilePos.z);
		if (outTe instanceof Container) {
			this.dropTimer = 0;
			for (int i = 0; i < this.contents.length; i++) {
				if (this.contents[i] != null) {
					ItemStack itemToInsert = this.contents[i].copy();
					itemToInsert.stackSize = 1;
					if (Util.insertOnInventory(outTe, itemToInsert, Direction.DOWN)) {
						this.removeItem(i, 1);
					}
					break;
				}
			}
		} else if (outTe instanceof TileEntityPipe pipe) {
			this.dropTimer = 0;
			int inputSide = Direction.UP.id;
			if (pipe.modeBySide[inputSide] != 1 && pipe.modeBySide[inputSide] != 3 && pipe.stacks[inputSide + 1] == null) {
				ItemStack itemToRemove = this.removeItems(pipe.maxStackSize);
				if (itemToRemove != null) {
					pipe.stacks[inputSide + 1] = new PipeStack(itemToRemove, Direction.UP, 0);
					pipe.setChanged();
					this.worldObj.markBlockNeedsUpdate(pipe.tilePos);
				}
			}
		} else if (this.worldObj.getBlockType(new TilePos(this.tilePos.x, this.tilePos.y-1, this.tilePos.z)).id() == 0) {
			if (this.numUnitsInside > 0) {
				this.dropTimer++;
				if (this.dropTimer >= this.maxDropTimer) {
					for (int i = 0; i < this.contents.length; i++) {
						if (this.contents[i] != null) {
							ItemStack stack = this.removeItem(i, this.contents[i].stackSize);
							EntityItem droppedItem = this.worldObj.dropItem(this.tilePos.x, (double)this.tilePos.y - 0.625, this.tilePos.z, stack, 0);
							droppedItem.xd = 0;
							droppedItem.yd = 0;
							droppedItem.zd = 0;
							droppedItem.basketPickupDelay = 1;
							this.worldObj.entityJoinedWorld(droppedItem);
							break;
						}
					}
					this.dropTimer = 0;
				}
			} else {
				this.dropTimer = 0;
			}
		} else {
			this.dropTimer = 0;
		}
	}

	public boolean importItemStack(ItemStack stack) {
		boolean inserted = false;
		int maxStackSize = Math.min(this.getMaxStackSize(), stack.getMaxStackSize());

		for (int i = 0; i < this.contents.length && stack.stackSize > 0; i++) {
			ItemStack contentsStack = this.contents[i];
			if (contentsStack != null && contentsStack.canStackWith(stack) && contentsStack.stackSize < maxStackSize) {
				int amount = Math.min(stack.stackSize, maxStackSize - contentsStack.stackSize);
				contentsStack.stackSize += amount;
				stack.stackSize -= amount;
				inserted = true;
			}
		}

		for (int i = 0; i < this.contents.length && stack.stackSize > 0; i++) {
			if (this.contents[i] == null) {
				int amount = Math.min(stack.stackSize, maxStackSize);
				ItemStack insertedStack = stack.copy();
				insertedStack.stackSize = amount;
				this.contents[i] = insertedStack;
				stack.stackSize -= amount;
				inserted = true;
			}
		}

		if (inserted) {
			this.setChanged();
		}
		return inserted;
	}

	@Override
	public void writeAdditionalData(@NotNull CompoundTag tag) {
		ListTag itemsTag = new ListTag();
		for (int i = 0; i < this.contents.length; ++i) {
			if (this.contents[i] != null) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putByte("Slot", (byte)i);
				this.contents[i].writeToNBT(itemTag);
				itemsTag.addTag(itemTag);
			}
		}
		tag.put("Items", itemsTag);
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

}
