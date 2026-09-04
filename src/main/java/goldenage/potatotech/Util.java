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
import net.minecraft.core.world.pos.TilePos;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

	public static PipeStack getItemFromInventoryNoCatch(World world, int x, int y, int z, Direction dir, int stackTimer, int count) {
		PipeStack returnStack = null;

		TileEntity te = world.getTileEntity(x, y, z);
		if (FabricLoader.getInstance().isModLoaded("catalyst-core") && CatalystItemIoCompat.isItemIo(te)) {
			return CatalystItemIoCompat.extract(te, dir, stackTimer, count);
		}
		if (te instanceof Container) {
			Container inventory = (Container) te;
			String inventoryName = inventory.getNameTranslationKey();
			if (isSignalIndustriesStorageContainer(inventory)) {
				ItemStack stack = extractSignalIndustriesStorageItem(inventory, count);
				return stack == null ? null : new PipeStack(stack, dir, stackTimer);
			}

			{
				if (Objects.equals(inventoryName, "container.chest.name")) {
					inventory = BlockLogicChest.getInventory(world, new TilePos(x, y ,z));
				}

				if (Objects.equals(inventoryName, "container.chest.name")
					|| Objects.equals(inventoryName, "container.dispenser.name")
					|| inventoryName.startsWith("container.ironchest")
					|| Objects.equals(inventoryName, "container.filter.name")
				) {
					int inventorySize = inventory.getContainerSize();
					ItemStack stack = null;
					int j = 0;

					if (!inventoryName.equals("container.filter.name")) {
						for (; stack == null && j < inventorySize; j++) stack = inventory.getItem(j);
					} else {
						for (; stack == null && j < inventorySize; j++) {
							stack = inventory.getItem(j);
							if (stack != null && stack.stackSize <= 1) stack = null;
						}
					}

					if (stack != null && j > 0) {
						short color = 0;
						if (Objects.equals(inventoryName, "container.filter.name")) {
							TileEntityFilter filter = (TileEntityFilter) inventory;
							color = filter.getColorInSlot(j - 1);
						}

						returnStack = new PipeStack(removeItemFromStack(stack, count), dir, stackTimer, color);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(j - 1, stack);
						return returnStack;
					}

				} else if (Objects.equals(inventoryName, "container.trommel.name")) {
					int inventorySize = 4;
					ItemStack stack = null;
					int j = 0;
					for (; stack == null && j < inventorySize; j++) stack = inventory.getItem(j);

					if (stack != null && j > 0) {
						returnStack = new PipeStack(removeItemFromStack(stack, count), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(j - 1, stack);
						return returnStack;
					}
				} else if (Objects.equals(inventoryName, "container.crafter.name")) {
					TileEntityCrafter ac = (TileEntityCrafter) te;
					ItemStack stack = ac.removeOneResult();
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack, count), dir, stackTimer);
					} else {
						ItemStack extra = ac.extraOutputs.getItem(0);
						if (extra != null) {
							ItemStack r = removeItemFromStack(extra, count);
							if (extra.stackSize <= 0) {
								ac.extraOutputs.setItem(0, null);
							}
							returnStack = new PipeStack(r, dir, stackTimer);
						}
					}
				} else if (te instanceof TileEntityFlag) {
					ItemStack stack = inventory.getItem(36);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack, count), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(36, stack);
					}
				} else if (te instanceof TileEntityFurnace || te instanceof TileEntityFurnaceBlast) {
					int outputSlot = te instanceof TileEntityFurnaceBlast ? 3 : 2;
					ItemStack stack = inventory.getItem(outputSlot);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack, count), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(outputSlot, stack);
					}
				} else if (inventory.getContainerSize() > 0) {
					ItemStack stack = inventory.getItem(0);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack, count), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(0, stack);
					}
				}
			}
		} else if (te instanceof TileEntityChute && dir == Direction.UP) {
			ItemStack stack = ((TileEntityChute)te).removeItems(count);
			if (stack != null) {
				returnStack = new PipeStack(stack, dir, stackTimer);
			}
		}

		return returnStack;
	}

	public static PipeStack getItemFromInventory(World world, int x, int y, int z, Direction dir, int stackTimer, int count) {
		PipeStack result = null;
		try {
			result = getItemFromInventoryNoCatch(world, x, y, z, dir, stackTimer, count);

		} catch(Exception e) {
			PotatoTech.LOGGER.error(e.getMessage());
		}

		return result;
	}

	public static void stackPipeStack(PipeStack pipeStack, ItemStack stack) {
		if (stack.canStackWith(pipeStack.stack)) {
			int remainder = stack.getMaxStackSize() - stack.stackSize;
			int count = Math.min(pipeStack.stack.stackSize, remainder);
			pipeStack.stack.stackSize -= count;
			stack.stackSize += count;
		}
	}

	public static boolean canStackPipeStack(PipeStack pipeStack, ItemStack stack) {
		if (stack.canStackWith(pipeStack.stack)) {
			int remainder = stack.getMaxStackSize() - stack.stackSize;
			return remainder >= pipeStack.stack.stackSize;
		}
		return false;
	}

	public static boolean insertOnInventoryNoCatch(Container inventory, ItemStack stack, Direction direction) {
		boolean hasInserted = false;
		if (inventory == null) {
			System.out.println(Arrays.toString(new NullPointerException("Null Pointer in insertOnInventory!!").getStackTrace()));
			StringBuilder builder = new StringBuilder("Error something is null when it shouldn't be!! | Inventory: ");
			System.out.println(builder);
			PotatoTech.LOGGER.info(builder.toString());
			return false;
		}
		int inventorySize = inventory.getContainerSize();
		String inventoryName = inventory.getNameTranslationKey();

		if (inventory instanceof TileEntityFurnace || inventory instanceof TileEntityFurnaceBlast || Objects.equals(inventoryName, "container.trommel.name")) {
			int fuelSlot = 1;
			int inputSlot = 0;

			if (Objects.equals(inventoryName, "container.trommel.name")) {
				fuelSlot = 4;
				for (; inputSlot < 3; inputSlot++) {
					ItemStack s = inventory.getItem(inputSlot);
					if (s == null) break;
					if (s.canStackWith(stack) && s.stackSize < s.getMaxStackSize()) break;
				}
			}

			if (inventory instanceof TileEntityFurnaceBlast) {
				fuelSlot = 2;
				if (direction == Direction.DOWN) {
					inputSlot = 1;
				}
			}

			int targetSlot = direction == Direction.UP ? fuelSlot : inputSlot;
			ItemStack furnaceStack = inventory.getItem(targetSlot);

			if (furnaceStack == null) {
				inventory.setItem(targetSlot, stack);
				hasInserted = true;
			} else {
				int maxStackSize = 8;
				if (furnaceStack.canStackWith(stack) && furnaceStack.stackSize < maxStackSize) {
					furnaceStack.stackSize++;
					inventory.setItem(targetSlot, furnaceStack);
					hasInserted = true;
				}
			}
		} else if (inventory instanceof TileEntityFlag) {
			int targetSlot = 36;
			ItemStack flagStack = inventory.getItem(targetSlot);

			if (flagStack == null) {
				inventory.setItem(targetSlot, stack);
				hasInserted = true;
			} else {
				int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : flagStack.getMaxStackSize();
				if (flagStack.canStackWith(stack) && flagStack.stackSize < maxStackSize) {
					flagStack.stackSize++;
					inventory.setItem(targetSlot, flagStack);
					hasInserted = true;
				}
			}
		} else if (Objects.equals(inventoryName, "container.crafter.name")) {
			 TileEntityCrafter ac = (TileEntityCrafter) inventory;
			 hasInserted = ac.insertItem(stack);
		} else {
			 ItemStack chestStack;
			 for (int j = 0; j < inventorySize; j++) {
				  if (inventoryName.equals("container.activator.name")) {
					  TileEntityActivator activator = (TileEntityActivator) inventory;
					  if (activator.locked(j)) {
						  continue;
					  }
				  }

				 chestStack = inventory.getItem(j);

				  if (chestStack == null) {
					  if (!inventoryName.equals("container.filter.name") && !inventoryName.equals("container.crafter.name")) {
						   inventory.setItem(j, stack);
						   hasInserted = true;
					  }
					  break;
				  }

				  int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : chestStack.getMaxStackSize();
				  if (chestStack.canStackWith(stack) && chestStack.stackSize < maxStackSize) {
					  chestStack.stackSize++;
					  inventory.setItem(j, chestStack);

					  hasInserted = true;
					  break;
				  }
			 }
		}

		return hasInserted;
	}

	public static boolean canInsertOnInventory(World world, int x, int y, int z, Direction dir, ItemStack item) {
		TileEntity te = world.getTileEntity(x, y, z);

		if (te == null) {
			return false;
		}

		if (!(te instanceof Container)) {
			return false;
		}

		if (te instanceof TileEntityFilter) {
			return ((TileEntityFilter) te).canInsertItem(item);
		}

		Container inventory = (Container)te;
		int inventorySize = inventory.getContainerSize();
		String inventoryName = inventory.getNameTranslationKey();

		if (Objects.equals(inventoryName, "container.chest.name")
			|| Objects.equals(inventoryName, "container.dispenser.name")
			|| inventoryName.startsWith("container.ironchest")
			|| Objects.equals(inventoryName, "container.filter.name")
			|| Objects.equals(inventoryName, "container.crafter.name")
			|| inventoryName.equals("container.activator.name")
		) {
			ItemStack chestStack;
			for (int j = 0; j < inventorySize; j++) {
				if (inventoryName.equals("container.activator.name")) {
					TileEntityActivator activator = (TileEntityActivator) inventory;
					if (activator.locked(j)) {
						continue;
					}
				}

				chestStack = inventory.getItem(j);
				if (chestStack == null) {
					return !inventoryName.equals("container.filter.name");
				}
				int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : chestStack.getMaxStackSize();
				if (chestStack.canStackWith(item) && chestStack.stackSize < maxStackSize) {
					return true;
				}
			}
		} else if (inventory instanceof TileEntityFlag) {
			int targetSlot = 36;
			ItemStack flagStack = inventory.getItem(targetSlot);

			if (flagStack == null) {
				return true;
			} else {
				return flagStack.canStackWith(item);
			}
		} else {
			int fuelSlot = 1;
			int inputSlot = 0;

			if (Objects.equals(inventoryName, "container.trommel.name")) {
				fuelSlot = 4;
				for (; inputSlot < 3; inputSlot++) {
					ItemStack s = inventory.getItem(inputSlot);
					if (s == null) break;
					int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : s.getMaxStackSize();
					if (s.canStackWith(item) && s.stackSize < maxStackSize) break;
				}
			}

			int targetSlot = dir == Direction.UP ? fuelSlot : inputSlot;

			ItemStack furnaceStack = inventory.getItem(targetSlot);

			if (furnaceStack == null) {
				return true;
			} else {
				return furnaceStack.canStackWith(item) && furnaceStack.stackSize < Math.min(8, item.getMaxStackSize());
			}
		}

		return false;
	}

	public static boolean insertPipeStackOnInventory(Container inventory, PipeStack pipeStack, Direction direction) {
		if (inventory == null || pipeStack == null || pipeStack.stack == null || pipeStack.stack.stackSize <= 0) {
			return false;
		}
		if (isSignalIndustriesStorageContainer(inventory)) {
			return insertSignalIndustriesStorageItem(inventory, pipeStack.stack);
		}

		String inventoryName = inventory.getNameTranslationKey();
		if (inventory instanceof TileEntityFurnace || inventory instanceof TileEntityFurnaceBlast || Objects.equals(inventoryName, "container.trommel.name")) {
			int targetSlot = getMachineInputSlot(inventory, inventoryName, direction, pipeStack.stack);
			return insertIntoSlot(inventory, targetSlot, pipeStack, 8);
		}
		if (inventory instanceof TileEntityFlag) {
			return insertIntoSlot(inventory, 36, pipeStack, inventory.getMaxStackSize());
		}
		if (Objects.equals(inventoryName, "container.crafter.name")) {
			TileEntityCrafter crafter = (TileEntityCrafter) inventory;
			boolean inserted = false;
			while (pipeStack.stack.stackSize > 0) {
				ItemStack oneItem = pipeStack.stack.copy();
				oneItem.stackSize = 1;
				if (!crafter.insertItem(oneItem)) {
					break;
				}
				pipeStack.stack.stackSize--;
				inserted = true;
			}
			return inserted;
		}

		boolean inserted = false;
		for (int slot = 0; slot < inventory.getContainerSize() && pipeStack.stack.stackSize > 0; slot++) {
			if (inventoryName.equals("container.activator.name") && ((TileEntityActivator) inventory).locked(slot)) {
				continue;
			}
			ItemStack current = inventory.getItem(slot);
			if (current != null) {
				inserted |= insertIntoSlot(inventory, slot, pipeStack, getSlotCapacity(inventory, current));
			}
		}
		if (Objects.equals(inventoryName, "container.filter.name")) {
			return inserted;
		}
		for (int slot = 0; slot < inventory.getContainerSize() && pipeStack.stack.stackSize > 0; slot++) {
			if (inventoryName.equals("container.activator.name") && ((TileEntityActivator) inventory).locked(slot)) {
				continue;
			}
			if (inventory.getItem(slot) == null) {
				inserted |= insertIntoSlot(inventory, slot, pipeStack, getSlotCapacity(inventory, pipeStack.stack));
			}
		}
		return inserted;
	}

	private static int getMachineInputSlot(Container inventory, String inventoryName, Direction direction, ItemStack stack) {
		if (Objects.equals(inventoryName, "container.trommel.name")) {
			for (int slot = 0; slot < 3; slot++) {
				ItemStack current = inventory.getItem(slot);
				if (current == null || (current.canStackWith(stack) && current.stackSize < 8)) {
					return slot;
				}
			}
			return 0;
		}
		if (inventory instanceof TileEntityFurnaceBlast) {
			if (direction == Direction.UP) return 2;
			return direction == Direction.DOWN ? 1 : 0;
		}
		return direction == Direction.UP ? 1 : 0;
	}

	private static boolean insertIntoSlot(Container inventory, int slot, PipeStack pipeStack, int capacity) {
		ItemStack current = inventory.getItem(slot);
		if (current == null) {
			int amount = Math.min(pipeStack.stack.stackSize, capacity);
			if (amount <= 0) return false;
			ItemStack inserted = pipeStack.stack.copy();
			inserted.stackSize = amount;
			pipeStack.stack.stackSize -= amount;
			inventory.setItem(slot, inserted);
			return true;
		}
		if (!current.canStackWith(pipeStack.stack)) return false;
		int amount = Math.min(pipeStack.stack.stackSize, capacity - current.stackSize);
		if (amount <= 0) return false;
		current.stackSize += amount;
		pipeStack.stack.stackSize -= amount;
		inventory.setItem(slot, current);
		return true;
	}

	private static int getSlotCapacity(Container inventory, ItemStack stack) {
		return Math.min(inventory.getMaxStackSize(), stack.getMaxStackSize());
	}

	private static boolean isSignalIndustriesStorageContainer(Container inventory) {
		return Objects.equals(inventory.getNameTranslationKey(), "container.signalindustries.storageContainer");
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

	public static boolean insertOnInventory(Container inventory, ItemStack stack, Direction direction) {
		boolean result = false;
		try {
			result = insertOnInventoryNoCatch(inventory, stack, direction);
		} catch (Exception e) {
			PotatoTech.LOGGER.error(e.getMessage());
		}
		return result;
	}

}
