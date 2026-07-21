package goldenage.potatotech.blocks.entities;


import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import goldenage.potatotech.screens.MenuCrafter;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.inventory.container.*;
import net.minecraft.core.player.inventory.container.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityCrafter extends TileEntity implements Container {
	public static final int energyCapacity = 1;
	public static final int energyPerCraft = 1;

	public ContainerCrafting craftMatrix;
	public Container pattern;
	public Container extraOutputs;
	public Container craftResult = new ContainerResult();
	public MenuCrafter dummyContainer;
	public int energy = 0;

	private static List<RecipeEntryCrafting<?, ?>> CRAFTING_RECIPE_ENTRIES_CACHE;

	static public void updateRecipeEntriesCache() {
		CRAFTING_RECIPE_ENTRIES_CACHE = Registries.RECIPES.getAllCraftingRecipes();
	}

	public TileEntityCrafter() {
		dummyContainer = new MenuCrafter(null, this);
		craftMatrix = new ContainerCrafting(dummyContainer, 3, 3);
		pattern = new ContainerSimple("pattern", 9);
		extraOutputs = new ContainerSimple("extra outputs", 1);
	}

	@Override
	public int getContainerSize() {
		return 10;
	}

	@Override
	public @Nullable ItemStack getItem(int i) {
		if (i == 0) {
			return craftResult.getItem(0);
		} else if (craftMatrix != null) {
			return craftMatrix.getItem(i-1);
		}
		return null;
	}

	@Override
	public @Nullable ItemStack removeItem(int i, int j) {
		if (i == 0) {
			return craftResult.removeItem(i, j);
		} else if (craftMatrix != null) {
			return craftMatrix.removeItem(i, j);
		}
		return null;
	}

	@Override
	public void setItem(int i, @Nullable ItemStack itemStack) {
		if (i == 0) {
			craftResult.setItem(0, itemStack);
		} else if (craftMatrix != null) {
			craftMatrix.setItem(i - 1, itemStack);
		}
	}

	@Override
	public String getNameTranslationKey() {
		return "container.crafter.name";
	}

	@Override
	public void readAdditionalData(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		energy = nbttagcompound.getInteger("energy");
		{
			ListTag nbttaglist = nbttagcompound.getList("CraftGrid");
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				CompoundTag nbttagcompound1 = (CompoundTag) nbttaglist.tagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 0xFF;
				if (j >= this.craftMatrix.getContainerSize()) continue;
				this.craftMatrix.setItem(j, ItemStack.readItemStackFromNbt(nbttagcompound1));
			}
		}
		{
			ListTag nbttaglist = nbttagcompound.getList("Pattern");
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				CompoundTag nbttagcompound1 = (CompoundTag) nbttaglist.tagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 0xFF;
				if (j >= this.pattern.getContainerSize()) continue;
				this.pattern.setItem(j, ItemStack.readItemStackFromNbt(nbttagcompound1));
			}
		}
		{
			ListTag nbttaglist = nbttagcompound.getList("CraftResult");
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				CompoundTag nbttagcompound1 = (CompoundTag) nbttaglist.tagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 0xFF;
				if (j >= this.craftResult.getContainerSize()) continue;
				this.craftResult.setItem(j, ItemStack.readItemStackFromNbt(nbttagcompound1));
			}
		}
	}

	@Override
	public void writeAdditionalData(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.putInt("energy", energy);
		{
			ListTag nbttaglist = new ListTag();
			for (int i = 0; i < this.craftMatrix.getContainerSize(); ++i) {
				ItemStack stack = this.craftMatrix.getItem(i);
				if (stack == null) continue;
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte) i);
				stack.writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
			nbttagcompound.put("CraftGrid", nbttaglist);
		}
		{
			ListTag nbttaglist = new ListTag();
			for (int i = 0; i < this.pattern.getContainerSize(); ++i) {
				ItemStack stack = this.pattern.getItem(i);
				if (stack == null) continue;
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte) i);
				stack.writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
			nbttagcompound.put("Pattern", nbttaglist);
		}
		{
			ListTag nbttaglist = new ListTag();
			for (int i = 0; i < this.craftResult.getContainerSize(); ++i) {
				ItemStack stack = this.craftResult.getItem(i);
				if (stack == null) continue;
				CompoundTag nbttagcompound1 = new CompoundTag();
				nbttagcompound1.putByte("Slot", (byte) i);
				stack.writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
			nbttagcompound.put("CraftResult", nbttaglist);
		}
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public boolean stillValid(Player player) {
		if (worldObj.getTileEntity(tilePos.x, tilePos.y, tilePos.z) != this) {
			return false;
		}
		return player.distanceToSqr((double) tilePos.x + 0.5, (double) tilePos.y + 0.5, (double) tilePos.z + 0.5) <= 64.0;
	}

	@Override
	public void sort() {

	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}

	public ItemStack removeOneResult() {
		ItemStack stack = craftResult.getItem(0);
		if (stack != null) {
			stack.stackSize--;
			if (stack.stackSize <= 0) {
				craftResult.setItem(0, null);
			} else {
				craftResult.setItem(0, stack.copy());
			}
			stack.stackSize = 1;
			this.setChanged();
			return stack;
		}
		return null;
	}


	public boolean insertItem(ItemStack stackToInsert) {
		boolean inserted = false;

		int slotToInsert = -1;
		int lastSlotCount = 99;

		if (craftMatrix != null) {
			for (int i = 0; i < 9; i++) {
				ItemStack stack = craftMatrix.getItem(i);
				ItemStack stackPattern = pattern.getItem(i);
				if (stackPattern == null) continue;

				if (stack == null) {
					if (stackPattern.itemID == stackToInsert.itemID && stackPattern.getMetadata() == stackToInsert.getMetadata()) {
						slotToInsert = i;
						lastSlotCount = 0;
					}
				} else if (stack.itemID == stackToInsert.itemID
					&& stack.getMetadata() == stackToInsert.getMetadata()
					&& stack.stackSize < stack.getMaxStackSize()
					&& stack.stackSize < lastSlotCount
				) {
					slotToInsert = i;
					lastSlotCount = stack.stackSize;
				}
			}

			if (slotToInsert >= 0) {
				if (lastSlotCount == 0) {
					craftMatrix.setItem(slotToInsert, stackToInsert.copy());
					this.setChanged();
					inserted = true;
				} else {
					ItemStack stack = craftMatrix.getItem(slotToInsert);
					if (stack != null) {
						stack.stackSize++;
						craftMatrix.setItem(slotToInsert, stack);
						this.setChanged();
						inserted = true;
					}
				}
			}
		}

		return inserted;
	}

	public int addEnergy(int amount) {
		if (amount <= 0) {
			return 0;
		}
		int accepted = Math.min(amount, energyCapacity - energy);
		if (accepted > 0) {
			energy += accepted;
			setChanged();
		}
		return accepted;
	}

	@Override
	public void setChanged() {
		super.setChanged();
	}

	int timer = 0;

	@Override
	public void tick() {
		super.tick();

		if (this.craftMatrix != null && !worldObj.isClientSide) {
			boolean canCraft = true;

			for (int i = 0; i < 9; i++) {
				ItemStack i0 = craftMatrix.getItem(i);
				ItemStack i1 = pattern.getItem(i);
				if (i0 != null && i1 == null || i0 == null && i1 != null) {
					canCraft = false;
					break;
				}

				if (i0 != null && i0.itemID != i1.itemID) {
					canCraft = false;
					break;
				}
			}

			if (extraOutputs.getItem(0) != null) {
				canCraft = false;
			}

			RecipeEntryCrafting<?, ?> recipe = null;
			for (RecipeEntryCrafting<?, ?> entry: CRAFTING_RECIPE_ENTRIES_CACHE) {
				if (entry.matches(this.craftMatrix)) {
					recipe = entry;
					break;
				}
			}

			ItemStack craftingResult = null;
			if (recipe != null)  {
				craftingResult = recipe.getCraftingResult(craftMatrix);
			}

			if (craftResult.getItem(0) == null && craftingResult != null && canCraft && energy >= energyPerCraft) {
				if (timer > 10) {
					recipe.onCraftResult(this.craftMatrix);
					craftResult.setItem(0, craftingResult);
					energy -= energyPerCraft;
					this.setChanged();

					int bucketCount = 0;
					for (int i = 0; i < 9; i++) {
						ItemStack s = craftMatrix.getItem(i);
						if (s != null && s.itemID == Items.BUCKET_IRON.id) {
							craftMatrix.setItem(i, null);
							bucketCount += 1;
						}
					}

					if (bucketCount > 0) {
						extraOutputs.setItem(0, new ItemStack(Items.BUCKET_IRON, bucketCount));
					}

					timer = 0;
				}
				timer += 1;
			} else {
				timer = 0;

				//CraftingManager.getInstance().onCraftResult(this.craftMatrix);
				//for (int i = 0; i < 9; i++) this.craftMatrix.decrStackSize(i, 1);
			}
		}
	}
}
