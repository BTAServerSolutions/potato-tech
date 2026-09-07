package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityChute;
import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.compat.catalyst.CatalystItemIoCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.renderer.DrawMode;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.entity.*;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

public class Util {

    public static void draw3dLine(TessellatorGeneral tessellator, double width, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b) {
        Vector3f direction = new Vector3f((float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1)).normalize();
        Vector3f reference = Math.abs(direction.y) < 0.9f ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
        Vector3f right = direction.cross(reference, new Vector3f()).normalize().mul((float) width * 0.5f);
        Vector3f up = right.cross(direction, new Vector3f()).normalize().mul((float) width * 0.5f);

        tessellator.startDrawing(DrawMode.TRIANGLES);
        tessellator.setColor4f(r, g, b, 1.0f);
        tessellator.setLightmapCoord1i(0xFF);

        tessellator.setNormal(-right.x, -right.y, -right.z);
        tessellator.addVertex(x1 - up.x - right.x, y1 - up.y - right.y, z1 - up.z - right.z);
        tessellator.addVertex(x1 + up.x - right.x, y1 + up.y - right.y, z1 + up.z - right.z);
        tessellator.addVertex(x2 + up.x - right.x, y2 + up.y - right.y, z2 + up.z - right.z);
        tessellator.addVertex(x1 - up.x - right.x, y1 - up.y - right.y, z1 - up.z - right.z);
        tessellator.addVertex(x2 + up.x - right.x, y2 + up.y - right.y, z2 + up.z - right.z);
        tessellator.addVertex(x2 - up.x - right.x, y2 - up.y - right.y, z2 - up.z - right.z);

        tessellator.setNormal(right.x, right.y, right.z);
        tessellator.addVertex(x1 - up.x + right.x, y1 - up.y + right.y, z1 - up.z + right.z);
        tessellator.addVertex(x2 - up.x + right.x, y2 - up.y + right.y, z2 - up.z + right.z);
        tessellator.addVertex(x2 + up.x + right.x, y2 + up.y + right.y, z2 + up.z + right.z);
        tessellator.addVertex(x1 - up.x + right.x, y1 - up.y + right.y, z1 - up.z + right.z);
        tessellator.addVertex(x2 + up.x + right.x, y2 + up.y + right.y, z2 + up.z + right.z);
        tessellator.addVertex(x1 + up.x + right.x, y1 + up.y + right.y, z1 + up.z + right.z);

        tessellator.setNormal(up.x, up.y, up.z);
        tessellator.addVertex(x1 - right.x + up.x, y1 - right.y + up.y, z1 - right.z + up.z);
        tessellator.addVertex(x1 + right.x + up.x, y1 + right.y + up.y, z1 + right.z + up.z);
        tessellator.addVertex(x2 + right.x + up.x, y2 + right.y + up.y, z2 + right.z + up.z);
        tessellator.addVertex(x1 - right.x + up.x, y1 - right.y + up.y, z1 - right.z + up.z);
        tessellator.addVertex(x2 + right.x + up.x, y2 + right.y + up.y, z2 + right.z + up.z);
        tessellator.addVertex(x2 - right.x + up.x, y2 - right.y + up.y, z2 - right.z + up.z);

        tessellator.setNormal(-up.x, -up.y, -up.z);
        tessellator.addVertex(x1 - right.x - up.x, y1 - right.y - up.y, z1 - right.z - up.z);
        tessellator.addVertex(x2 - right.x - up.x, y2 - right.y - up.y, z2 - right.z - up.z);
        tessellator.addVertex(x2 + right.x - up.x, y2 + right.y - up.y, z2 + right.z - up.z);
        tessellator.addVertex(x1 - right.x - up.x, y1 - right.y - up.y, z1 - right.z - up.z);
        tessellator.addVertex(x2 + right.x - up.x, y2 + right.y - up.y, z2 - right.z - up.z);
        tessellator.addVertex(x1 + right.x - up.x, y1 + right.y - up.y, z1 + right.z - up.z);

        tessellator.draw();
    }

	/** Draws a connected square tube with shared cross-sections at every joint. */
	public static void draw3dTube(TessellatorGeneral tessellator, double width, List<Vector3f> points, float lightR, float lightG, float lightB, float darkR, float darkG, float darkB) {
		if (points.size() < 2) {
			return;
		}

		float halfWidth = (float) width * 0.5f;
		Vector3f[] right = new Vector3f[points.size()];
		Vector3f[] up = new Vector3f[points.size()];
		for (int i = 0; i < points.size(); i++) {
			Vector3f tangent;
			if (i == 0) {
				tangent = new Vector3f(points.get(1)).sub(points.get(0));
			} else if (i == points.size() - 1) {
				tangent = new Vector3f(points.get(i)).sub(points.get(i - 1));
			} else {
				tangent = new Vector3f(points.get(i + 1)).sub(points.get(i - 1));
			}
			if (tangent.lengthSquared() == 0.0f) {
				return;
			}
			tangent.normalize();
			Vector3f reference = Math.abs(tangent.y) < 0.9f ? new Vector3f(0, 1, 0) : new Vector3f(1, 0, 0);
			right[i] = tangent.cross(reference, new Vector3f()).normalize().mul(halfWidth);
			up[i] = right[i].cross(tangent, new Vector3f()).normalize().mul(halfWidth);
			if (i > 0 && right[i].dot(right[i - 1]) < 0.0f) {
				right[i].negate();
				up[i].negate();
			}
		}

		tessellator.startDrawing(DrawMode.TRIANGLES);
		tessellator.setLightmapCoord1i(0xFF);
		for (int i = 0; i < points.size() - 1; i++) {
			boolean lightSegment = i % 2 == 0;
			tessellator.setColor4f(lightSegment ? lightR : darkR, lightSegment ? lightG : darkG, lightSegment ? lightB : darkB, 1.0f);
			addTubeFace(tessellator, points.get(i), right[i], up[i], points.get(i + 1), right[i + 1], up[i + 1], -1, 0);
			addTubeFace(tessellator, points.get(i), right[i], up[i], points.get(i + 1), right[i + 1], up[i + 1], 1, 0);
			addTubeFace(tessellator, points.get(i), right[i], up[i], points.get(i + 1), right[i + 1], up[i + 1], 0, 1);
			addTubeFace(tessellator, points.get(i), right[i], up[i], points.get(i + 1), right[i + 1], up[i + 1], 0, -1);
		}
		tessellator.setColor4f(lightR, lightG, lightB, 1.0f);
		addTubeCap(tessellator, points.get(0), right[0], up[0], true);
		boolean lightLastSegment = (points.size() - 2) % 2 == 0;
		tessellator.setColor4f(lightLastSegment ? lightR : darkR, lightLastSegment ? lightG : darkG, lightLastSegment ? lightB : darkB, 1.0f);
		addTubeCap(tessellator, points.get(points.size() - 1), right[points.size() - 1], up[points.size() - 1], false);
		tessellator.draw();
	}

	private static void addTubeFace(TessellatorGeneral tessellator, Vector3f start, Vector3f startRight, Vector3f startUp, Vector3f end, Vector3f endRight, Vector3f endUp, int rightSign, int upSign) {
		Vector3f a;
		Vector3f b;
		Vector3f c;
		Vector3f d;
		if (rightSign < 0) {
			a = tubeCorner(start, startRight, startUp, -1, -1);
			b = tubeCorner(start, startRight, startUp, -1, 1);
			c = tubeCorner(end, endRight, endUp, -1, 1);
			d = tubeCorner(end, endRight, endUp, -1, -1);
		} else if (rightSign > 0) {
			a = tubeCorner(start, startRight, startUp, 1, -1);
			b = tubeCorner(end, endRight, endUp, 1, -1);
			c = tubeCorner(end, endRight, endUp, 1, 1);
			d = tubeCorner(start, startRight, startUp, 1, 1);
		} else if (upSign > 0) {
			a = tubeCorner(start, startRight, startUp, -1, 1);
			b = tubeCorner(start, startRight, startUp, 1, 1);
			c = tubeCorner(end, endRight, endUp, 1, 1);
			d = tubeCorner(end, endRight, endUp, -1, 1);
		} else {
			a = tubeCorner(start, startRight, startUp, -1, -1);
			b = tubeCorner(end, endRight, endUp, -1, -1);
			c = tubeCorner(end, endRight, endUp, 1, -1);
			d = tubeCorner(start, startRight, startUp, 1, -1);
		}
		addQuad(tessellator, a, b, c, d);
	}

	private static void addTubeCap(TessellatorGeneral tessellator, Vector3f point, Vector3f right, Vector3f up, boolean start) {
		Vector3f a = tubeCorner(point, right, up, -1, -1);
		Vector3f b = tubeCorner(point, right, up, 1, -1);
		Vector3f c = tubeCorner(point, right, up, 1, 1);
		Vector3f d = tubeCorner(point, right, up, -1, 1);
		if (start) {
			addQuad(tessellator, a, b, c, d);
		} else {
			addQuad(tessellator, a, d, c, b);
		}
	}

	private static Vector3f tubeCorner(Vector3f point, Vector3f right, Vector3f up, int rightSign, int upSign) {
		return new Vector3f(point).fma(rightSign, right).fma(upSign, up);
	}



	private static void addQuad(TessellatorGeneral tessellator, Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
		tessellator.addVertex(a.x, a.y, a.z);
		tessellator.addVertex(b.x, b.y, b.z);
		tessellator.addVertex(c.x, c.y, c.z);
		tessellator.addVertex(a.x, a.y, a.z);
		tessellator.addVertex(c.x, c.y, c.z);
		tessellator.addVertex(d.x, d.y, d.z);
	}

	public record SlotInfo(Container container, int index, int freeCapacity) {}

	public static SlotInfo getContainerSlotInfo(TileEntity entity, Direction dir, ItemStack stackToInsert, short requestedColor) {
		SlotInfo info = new SlotInfo(null, -1, 0);
		boolean isInsertion = stackToInsert != null;

		if (!(entity instanceof Container)) return info;

		Container container = (Container) entity;
		String containerName = container.getNameTranslationKey();

		if (isSignalIndustriesStorageContainer(container)) {
			info = new SlotInfo(container, 0, isInsertion ? stackToInsert.stackSize : 1);
		} else if (FabricLoader.getInstance().isModLoaded("catalyst-core") && CatalystItemIoCompat.isItemIo(entity)) {
			int slot = CatalystItemIoCompat.getActiveSlot(entity, dir, stackToInsert);

			if (slot >= 0 && slot < container.getContainerSize()) {
				ItemStack stack = container.getItem(slot);

				if (isInsertion) {
					int maxStackSize = Math.min(container.getMaxStackSize(), stackToInsert.getMaxStackSize());
					if (stack == null) {
						info = new SlotInfo(container, slot, maxStackSize);
					} else if (stack.canStackWith(stackToInsert) && stack.stackSize < maxStackSize) {
						info = new SlotInfo(container, slot, maxStackSize - stack.stackSize);
					}
				} else if (stack != null) {
					info = new SlotInfo(container, slot, stack.stackSize);
				}
			}
		} else if (entity instanceof TileEntityChute chute) {
			if (isInsertion && dir == Direction.DOWN) {
				int maxStackSize = Math.min(chute.getMaxStackSize(), stackToInsert.getMaxStackSize());
				int emptySlot = -1;

				for (int i = 0; i < chute.getContainerSize(); i++) {
					ItemStack stack = chute.getItem(i);
					if (stack == null) {
						if (emptySlot < 0) emptySlot = i;
					} else if (stack.canStackWith(stackToInsert) && stack.stackSize < maxStackSize) {
						info = new SlotInfo(chute, i, maxStackSize - stack.stackSize);
						break;
					}
				}

				if (info.index() < 0 && emptySlot >= 0) {
					info = new SlotInfo(chute, emptySlot, maxStackSize);
				}
			} else if (!isInsertion && dir == Direction.UP) {
				for (int i = 0; i < chute.getContainerSize(); i++) {
					ItemStack stack = chute.getItem(i);
					if (stack != null) {
						info = new SlotInfo(chute, i, stack.stackSize);
						break;
					}
				}
			}
		} else {
			if (containerName.equals("container.chest.name")) {
				container = BlockLogicChest.getInventory(entity.worldObj, entity.tilePos);
			}

			if (isInsertion) {
				if (entity instanceof TileEntityCrafter crafter) {
					int selectedSlot = -1;
					int selectedCount = Integer.MAX_VALUE;

					for (int i = 0; i < crafter.craftMatrix.getContainerSize(); i++) {
						ItemStack pattern = crafter.pattern.getItem(i);
						if (pattern == null
							|| pattern.itemID != stackToInsert.itemID
							|| pattern.getMetadata() != stackToInsert.getMetadata()) continue;

						ItemStack stack = crafter.craftMatrix.getItem(i);
						if (stack == null) {
							selectedSlot = i + 1;
							selectedCount = 0;
						} else if (selectedCount > 0
							&& stack.itemID == stackToInsert.itemID
							&& stack.getMetadata() == stackToInsert.getMetadata()
							&& stack.stackSize < stack.getMaxStackSize()
							&& stack.stackSize < selectedCount) {
							selectedSlot = i + 1;
							selectedCount = stack.stackSize;
						}
					}

					if (selectedSlot >= 0) {
						info = new SlotInfo(crafter, selectedSlot, 1);
					}
				} else {
					int firstSlot = 0;
					int[] slotsToSkip = null;
					int endSlot = container.getContainerSize();
					int maxStackSize = Math.min(container.getMaxStackSize(), stackToInsert.getMaxStackSize());
					boolean allowEmptySlot = !containerName.equals("container.filter.name");

					if (entity instanceof TileEntityFurnaceBlast) {
						firstSlot = dir == Direction.UP ? 2 : dir == Direction.DOWN ? 1 : 0;
						endSlot = firstSlot + 1;
						maxStackSize = Math.min(maxStackSize, 8);
					} else if (entity instanceof TileEntityFurnace) {
						firstSlot = dir == Direction.UP ? 1 : 0;
						endSlot = firstSlot + 1;
						maxStackSize = Math.min(maxStackSize, 8);
					} else if (containerName.startsWith("tile.tile.ic2.machine")) {
						if (containerName.equals("tile.tile.ic2.machine.induction_furnace.name")) {
							endSlot = 4;
							slotsToSkip = new int[2];
							slotsToSkip[0] = 1;
							slotsToSkip[1] = 2;
						} else {
							endSlot = 1;
						}
					} else if (containerName.equals("container.trommel.name")) {
						firstSlot = dir == Direction.UP ? 4 : 0;
						endSlot = dir == Direction.UP ? 5 : 3;
						maxStackSize = Math.min(maxStackSize, 8);
					} else if (entity instanceof TileEntityFlag) {
						firstSlot = 36;
						endSlot = 37;
					}

					int emptySlot = -1;
					for (int i = firstSlot; i < endSlot; i++) {
						if (slotsToSkip != null) {
							boolean skip = false;
							for (int k : slotsToSkip) {
								if (k == i) {
									skip = true;
									break;
								}
							}
							if (skip) continue;
						}
						if (container.locked(i)) continue;
						ItemStack stack = container.getItem(i);

						if (stack == null) {
							if (allowEmptySlot && emptySlot < 0) emptySlot = i;
						} else if (stack.canStackWith(stackToInsert) && stack.stackSize < maxStackSize) {
							info = new SlotInfo(container, i, maxStackSize - stack.stackSize);
							break;
						}
					}

					if (info.index() < 0 && emptySlot >= 0) {
						info = new SlotInfo(container, emptySlot, maxStackSize);
					}
				}
			} else {
				int firstSlot = 0;
				int endSlot = Math.min(1, container.getContainerSize());
				int reservedItems = 0;
				int slotToSkip = -1;

				if (entity instanceof TileEntityCrafter crafter) {
					if (crafter.getItem(0) != null) {
						container = crafter;
						endSlot = 1;
						reservedItems = Objects.requireNonNull(crafter.getItem(0)).stackSize - 1;
					} else {
						container = crafter.extraOutputs;
						endSlot = 1;
					}
				} else if (entity instanceof TileEntityFurnaceBlast) {
					firstSlot = 3;
					endSlot = 4;
				} else if (entity instanceof TileEntityFurnace) {
					firstSlot = 2;
					endSlot = 3;
				} else if (containerName.equals("container.trommel.name")) {
					endSlot = 4;
				} else if (containerName.startsWith("tile.tile.ic2.machine")) {
					if (containerName.equals("tile.tile.ic2.machine.induction_furnace.name")) {
						firstSlot = 2;
						endSlot = 5;
						slotToSkip = 3;
					} else {
						firstSlot = 2;
						endSlot = 3;
					}
				} else if (entity instanceof TileEntityFlag) {
					firstSlot = 36;
					endSlot = 37;
				} else if (containerName.equals("container.chest.name")
					|| containerName.equals("container.dispenser.name")
					|| containerName.startsWith("container.ironchest")
					|| containerName.equals("container.filter.name"))
				{
					endSlot = container.getContainerSize();
					if (containerName.equals("container.filter.name")) reservedItems = 1;
				}

				for (int i = firstSlot; i < endSlot; i++) {
					if (i == slotToSkip) continue;
					ItemStack stack = container.getItem(i);
					if (stack != null
						&& stack.stackSize > reservedItems
						&& (!(entity instanceof TileEntityFilter filter) || requestedColor == 0 || filter.getColorInSlot(i) == requestedColor)) {
						info = new SlotInfo(container, i, stack.stackSize - reservedItems);
						break;
					}
				}
			}
		}

		return info;
	}

	public static ItemStack removeItemFromStack(ItemStack stack, int count) {
		if (count > 0) {
			count = Math.min(count, stack.stackSize);
			ItemStack newStack = stack.copy();
			newStack.stackSize = count;
			stack.stackSize -= count;
			return newStack;
		}
		return null;
	}

	public static PipeStack getItemFromInventoryNoCatch(World world, int x, int y, int z, Direction dir, int stackTimer, int count, short requestedColor) {
		PipeStack returnStack = null;
		TileEntity te = world.getTileEntity(x, y, z);

		SlotInfo slotInfo = getContainerSlotInfo(te, dir, null, requestedColor);

		if (slotInfo.freeCapacity() > 0) {
			if (isSignalIndustriesStorageContainer(slotInfo.container())) {
				ItemStack stack = extractSignalIndustriesStorageItem(slotInfo.container(), count);
				if (stack != null) returnStack = new PipeStack(stack, dir, stackTimer);
			} else {
				ItemStack stack = slotInfo.container().getItem(slotInfo.index());
				if (stack != null) {
					short color = te instanceof TileEntityFilter filter ? filter.getColorInSlot(slotInfo.index()) : 0;
					ItemStack extracted = removeItemFromStack(stack, Math.min(count, slotInfo.freeCapacity()));
					slotInfo.container().setItem(slotInfo.index(), stack.stackSize > 0 ? stack : null);
					slotInfo.container().setChanged();
					te.setChanged();
					returnStack = new PipeStack(extracted, dir, stackTimer, color);
				}
			}
		}

		return returnStack;
	}

	public static PipeStack getItemFromInventory(World world, int x, int y, int z, Direction dir, int stackTimer, int count, short requestedColor) {
		PipeStack result = null;
		try {
			result = getItemFromInventoryNoCatch(world, x, y, z, dir, stackTimer, count, requestedColor);

		} catch(Exception e) {
			PotatoTech.LOGGER.error(e.getMessage());
		}

		return result;
	}

	public static boolean insertOnInventoryNoCatch(TileEntity entity, ItemStack stack, Direction direction) {
		if (entity == null || stack == null || stack.stackSize <= 0) return false;
		return insertPipeStackOnInventory(entity, new PipeStack(stack, direction, 0), direction);
	}

	public static boolean canInsertOnInventory(World world, int x, int y, int z, Direction dir, ItemStack item) {
		return getContainerSlotInfo(world.getTileEntity(x, y, z), dir, item, (short)0).freeCapacity() > 0;
	}

	public static boolean insertPipeStackOnInventory(TileEntity entity, PipeStack pipeStack, Direction direction) {
		if (entity == null || pipeStack == null || pipeStack.stack == null || pipeStack.stack.stackSize <= 0) {
			return false;
		}

		boolean inserted = false;
		SlotInfo firstSlot = getContainerSlotInfo(entity, direction, pipeStack.stack, (short)0);

		if (firstSlot.freeCapacity() > 0) {
			if (isSignalIndustriesStorageContainer(firstSlot.container())) {
				inserted = insertSignalIndustriesStorageItem(firstSlot.container(), pipeStack.stack);
			} else {
				SlotInfo slotInfo = firstSlot;
				while (slotInfo.freeCapacity() > 0 && pipeStack.stack.stackSize > 0) {
					if (!insertIntoSlot(slotInfo, pipeStack)) break;
					inserted = true;
					slotInfo = getContainerSlotInfo(entity, direction, pipeStack.stack, (short)0);
				}
			}
		}

		if (inserted) entity.setChanged();
		return inserted;
	}

	private static boolean insertIntoSlot(SlotInfo slotInfo, PipeStack pipeStack) {
		Container inventory = slotInfo.container();
		ItemStack current = inventory.getItem(slotInfo.index());
		int amount = Math.min(pipeStack.stack.stackSize, slotInfo.freeCapacity());
		if (amount <= 0) return false;
		if (current == null) {
			ItemStack inserted = pipeStack.stack.copy();
			inserted.stackSize = amount;
			pipeStack.stack.stackSize -= amount;
			inventory.setItem(slotInfo.index(), inserted);
			inventory.setChanged();
			return true;
		}
		current.stackSize += amount;
		pipeStack.stack.stackSize -= amount;
		inventory.setItem(slotInfo.index(), current);
		inventory.setChanged();
		return true;
	}

	private static boolean isSignalIndustriesStorageContainer(Container inventory) {
		return inventory.getNameTranslationKey().equals("container.signalindustries.storageContainer");
	}

	private static boolean insertSignalIndustriesStorageItem(Container inventory, ItemStack stack) {
		try {
			return (boolean) inventory.getClass().getMethod("insertStack", ItemStack.class).invoke(inventory, stack);
		} catch (ReflectiveOperationException e) {
			PotatoTech.LOGGER.error("Unable to insert into Signal Industries storage container", e);
			return false;
		}
	}

	private static ItemStack extractSignalIndustriesStorageItem(Container inventory, int count) {
		try {
			return (ItemStack) inventory.getClass().getMethod("extractStack", int.class).invoke(inventory, count);
		} catch (ReflectiveOperationException e) {
			PotatoTech.LOGGER.error("Unable to extract from Signal Industries storage container", e);
			return null;
		}
	}

	public static boolean insertOnInventory(TileEntity entity, ItemStack stack, Direction direction) {
		boolean result = false;
		try {
			result = insertOnInventoryNoCatch(entity, stack, direction);
		} catch (Exception e) {
			PotatoTech.LOGGER.error(e.getMessage());
		}
		return result;
	}

}
