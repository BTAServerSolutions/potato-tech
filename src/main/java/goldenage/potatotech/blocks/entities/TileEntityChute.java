package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.Util;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.primitives.AABBdc;

import java.util.*;

public class TileEntityChute extends TileEntity {

	public int numUnitsInside = 0;
	public final Map<ChuteEntry, Integer> contents = new HashMap<>();

	public void dropAllItems() {
		Random rand = new Random();
		for (Map.Entry<ChuteEntry, Integer> entry : this.contents.entrySet()) {
			int stackSize;
			ChuteEntry be = entry.getKey();
			for (int numItems = entry.getValue(); numItems > 0; numItems -= stackSize) {
				int maxStackSize;
				stackSize = maxStackSize = be.getItem().getItemStackLimit(null);
				int remainingItems = numItems - maxStackSize;
				if (remainingItems < 0) {
					stackSize = numItems;
				}
				this.dropItemStack(rand, new ItemStack(be.id, stackSize, be.metadata, be.tag));
			}
		}
		this.contents.clear();
		TilePos chutePos = new TilePos(this.tilePos.x, this.tilePos.y, this.tilePos.z);
		this.worldObj.notifyBlockChange(chutePos, PTBlocks.chute);
		this.updateNumUnits();
	}

	private void updateNumUnits() {
		this.numUnitsInside = 0;
		for (Map.Entry<ChuteEntry, Integer> entry : this.contents.entrySet()) {
			ChuteEntry be = entry.getKey();
			int numItems = entry.getValue();
			int unitsPerItem = this.getItemSizeUnits(be.getItem());
			this.numUnitsInside += unitsPerItem * numItems;
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
		float f3 = 0.05f;
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

	public void givePlayerAllItems(World world, Player player) {
		List<ChuteEntry> toRemove = new ArrayList<>();
		Iterator var4 = this.contents.entrySet().iterator();

		while(var4.hasNext()) {
			Map.Entry entry = (Map.Entry)var4.next();
			ChuteEntry basketEntry = (ChuteEntry)entry.getKey();
			ItemStack basketEntryStack = new ItemStack(basketEntry.id, (Integer)entry.getValue(), basketEntry.metadata, basketEntry.tag);
			player.inventory.insertItem(basketEntryStack, true);
			this.contents.put(basketEntry, basketEntryStack.stackSize);
			if (basketEntryStack.stackSize <= 0) {
				toRemove.add(basketEntry);
			}
		}

		var4 = toRemove.iterator();

		while(var4.hasNext()) {
			ChuteEntry entry = (ChuteEntry)var4.next();
			this.contents.remove(entry);
		}

		this.updateNumUnits();
		TilePos allItemsPos = new TilePos(this.tilePos.x, this.tilePos.y, this.tilePos.z);
		this.worldObj.notifyBlockChange(allItemsPos, PTBlocks.chute);
	}
	public ItemStack removeOneItem() {
		ChuteEntry firstKey = null;
		int itemCount = 0;
		for (Map.Entry<ChuteEntry, Integer> entry : this.contents.entrySet()) {
			firstKey = entry.getKey();
			itemCount = entry.getValue();
			break;
		}

		if (firstKey == null || itemCount == 0) return null;

		ItemStack itemStack = new ItemStack(firstKey.getItem(), 1, firstKey.metadata);

		itemCount--;

		if (itemCount == 0) {
			this.contents.remove(firstKey);
		} else {
			this.contents.put(firstKey, itemCount);
		}

		TilePos remove1pos = new TilePos(this.tilePos.x, this.tilePos.y, this.tilePos.z);
		this.worldObj.notifyBlockChange(remove1pos, PTBlocks.chute);
		this.updateNumUnits();

		return itemStack;
	}

	@Override
	public void readAdditionalData(CompoundTag tag) {
		super.readFromNBT(tag);
		ListTag itemsTag = tag.getList("Items");
		this.contents.clear();
		for (int i = 0; i < itemsTag.tagCount(); ++i) {
			CompoundTag itemTag = (CompoundTag)itemsTag.tagAt(i);
			ChuteEntry entry = ChuteEntry.read(itemTag);
			short count = itemTag.getShort("Count");
			this.contents.put(entry, (int) count);
		}
		this.updateNumUnits();
	}

	@Override
	public void tick() {
		if (this.worldObj == null || this.worldObj.isClientSide) {
			return;
		}
		AABBdc aabb = AABB.fromPool(this.tilePos.x, this.tilePos.y, this.tilePos.z, this.tilePos.x + 1, this.tilePos.y + 2, this.tilePos.z + 1).asJomlAABB();
		List<EntityItem> entities = this.worldObj.getEntitiesWithinAABB(EntityItem.class, aabb);
		boolean shouldUpdate = false;
		if (!entities.isEmpty()) {
			for (Entity e : entities) {
				EntityItem entity = (EntityItem)e;
				if (entity.item == null || entity.item.stackSize <= 0 || entity.basketPickupDelay != 0) continue;
				shouldUpdate = this.importItemStack(entity.item);
				if (entity.item.stackSize > 0) continue;
				entity.item.stackSize = 0;
				e.outOfWorld();
			}
		}
		if (shouldUpdate) {
			TilePos updatePos = new TilePos(this.tilePos.x, this.tilePos.y, this.tilePos.z);
			this.worldObj.notifyBlockChange(updatePos, PTBlocks.chute);
			this.updateNumUnits();
		}

		TileEntity outTe = worldObj.getTileEntity(tilePos.x, tilePos.y-1, tilePos.z) ;
		if (outTe instanceof Container) {
			ItemStack itemToRemove = this.removeOneItem();

			if (itemToRemove != null) {
				boolean hasInserted = false;

				Container inventory;
				if (outTe instanceof TileEntityChest) {
					TilePos invPos = new TilePos(tilePos.x, tilePos.y - 1, tilePos.z);
					inventory = BlockLogicChest.getInventory(worldObj, invPos);
				} else {
					inventory = (Container) outTe;
				}

				if (inventory != null) {
					hasInserted = Util.insertOnInventory(inventory, itemToRemove, Direction.DOWN);
				}
				if (!hasInserted) {
					importItemStack(itemToRemove);
				}
			}
		} else if (outTe instanceof TileEntityChute) {
			ItemStack itemToRemove = this.removeOneItem();
			if (itemToRemove != null) {
				if (!((TileEntityChute)outTe).importItemStack(itemToRemove)) {
					this.importItemStack(itemToRemove);
				}
			}
		}
	}

	public boolean importItemStack(ItemStack stack) {
		ChuteEntry entry = new ChuteEntry(stack.itemID, stack.getMetadata(), stack.getData());
		int sizeUnits = this.getItemSizeUnits(stack.getItem());
		int freeUnits = this.getMaxUnits() - this.numUnitsInside;
		int itemsToTake = Math.min(freeUnits / sizeUnits, stack.stackSize);
		if (itemsToTake <= 0) {
			return false;
		}
		stack.stackSize -= itemsToTake;
		int currentItemsInBE = this.contents.getOrDefault(entry, 0);
		this.contents.put(entry, currentItemsInBE += itemsToTake);
		return true;
	}

	@Override
	public void writeAdditionalData(CompoundTag tag) {
		super.writeToNBT(tag);
		ListTag itemsTag = new ListTag();
		for (Map.Entry<ChuteEntry, Integer> entry : this.contents.entrySet()) {
			CompoundTag itemTag = new CompoundTag();
			itemTag.putShort("Count", (short)entry.getValue().intValue());
			ChuteEntry.write(itemTag, entry.getKey());
			itemsTag.addTag(itemTag);
		}
		tag.put("Items", itemsTag);
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

	public static final class ChuteEntry {
		public final int id;
		public final int metadata;
		public final CompoundTag tag;

		public ChuteEntry(int id, int metadata, CompoundTag tag) {
			this.id = id;
			this.metadata = metadata;
			this.tag = tag;
		}

		public static ChuteEntry read(CompoundTag tag) {
			short id = tag.getShort("id");
			short damage = tag.getShort("Damage");
			CompoundTag data = tag.getCompound("Data");
			return new ChuteEntry(id, damage, data);
		}

		public static void write(CompoundTag tag, ChuteEntry entry) {
			tag.putShort("id", (short)entry.id);
			tag.putShort("Damage", (short)entry.metadata);
			tag.putCompound("Data", entry.tag);
		}

		public Item getItem() {
			return Item.itemsList[this.id];
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof ChuteEntry)) {
				return false;
			}
			ChuteEntry other = (ChuteEntry)obj;
			if (this.id != other.id || this.metadata != other.metadata) {
				return false;
			}
			return this.tag.getValues().size() <= 2 && other.tag.getValues().size() <= 2;
		}

		public int hashCode() {
			if (this.tag.getValues().size() <= 2) {
				return Objects.hash(this.id, this.metadata);
			}
			return Objects.hash(this.id, this.metadata, this.tag);
		}
	}
}
