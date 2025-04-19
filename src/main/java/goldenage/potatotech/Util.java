package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityChute;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.entity.*;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Util {
    public static double[] crossProduct(double[] v0, double[] v1) {
        double[] crossProduct = new double[3];
        crossProduct[0] = v0[1] * v1[2] - v0[2] * v1[1];
        crossProduct[1] = v0[2] * v1[0] - v0[0] * v1[2];
        crossProduct[2] = v0[0] * v1[1] - v0[1] * v1[0];
        return crossProduct;
    }

    public static void normalize(double[] v) {
        double len = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2] * v[2]);
        v[0] /= len;
        v[1] /= len;
        v[2] /= len;
    }

    public static void draw3dLine(double width, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b) {

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Tessellator tessellator = Tessellator.instance;
        double l = Math.sqrt(x2*x2 + y2*y2 + z2*x2);

        Vector3f norm = new Vector3f((float)(x2 - x1), (float) (y2 - y1), (float)(z2 - z1));
        norm.normalise(norm);

        Vector3f perp = new Vector3f(1, 0, 0);
        if (Math.abs(norm.x) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 0.0f;
            perp.z = 1.0f;
        } else if (Math.abs(norm.z) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 1.0f;
            perp.z = 0.0f;
        }

        Vector3f up = new Vector3f(0, 0, 0) ;
        Vector3f.cross(norm, perp, up);
        up.normalise(up);

        Vector3f right = new Vector3f();
        Vector3f.cross(norm, up, right);

        up.x *= (float) (width * 0.5);
        up.y *= (float) (width * 0.5);
        up.z *= (float) (width * 0.5);

        right.x *= (float) (width * 0.5);
        right.y *= (float) (width * 0.5);
        right.z *= (float) (width * 0.5);

        tessellator.startDrawing(GL11.GL_QUADS);

        GL11.glColor4f(r, g, b, 1);

        tessellator.setNormal(-right.x, -right.y, -right.z);
        tessellator.addVertex(x1 - up.x - right.x, y1 - up.y - right.y, z1 - up.z - right.z);
        tessellator.addVertex(x1 + up.x - right.x, y1 + up.y - right.y, z1 + up.z - right.z);
        tessellator.addVertex(x2 + up.x - right.x, y2 + up.y - right.y, z2 + up.z - right.z);
        tessellator.addVertex(x2 - up.x - right.x, y2 - up.y - right.y, z2 - up.z - right.z);

        tessellator.setNormal(right.x, right.y, right.z);
        tessellator.addVertex(x1 - up.x + right.x, y1 - up.y + right.y, z1 - up.z + right.z);
        tessellator.addVertex(x2 - up.x + right.x, y2 - up.y + right.y, z2 - up.z + right.z);
        tessellator.addVertex(x2 + up.x + right.x, y2 + up.y + right.y, z2 + up.z + right.z);
        tessellator.addVertex(x1 + up.x + right.x, y1 + up.y + right.y, z1 + up.z + right.z);

        tessellator.setNormal(up.x, up.y, up.z);
        tessellator.addVertex(x1 - right.x + up.x, y1 - right.y + up.y, z1 - right.z + up.z);
        tessellator.addVertex(x1 + right.x + up.x, y1 + right.y + up.y, z1 + right.z + up.z);
        tessellator.addVertex(x2 + right.x + up.x, y2 + right.y + up.y, z2 + right.z + up.z);
        tessellator.addVertex(x2 - right.x + up.x, y2 - right.y + up.y, z2 - right.z + up.z);

        tessellator.setNormal(-up.x, -up.y, -up.z);
        tessellator.addVertex(x1 - right.x - up.x, y1 - right.y - up.y, z1 - right.z - up.z);
        tessellator.addVertex(x2 - right.x - up.x, y2 - right.y - up.y, z2 - right.z - up.z);
        tessellator.addVertex(x2 + right.x - up.x, y2 + right.y - up.y, z2 + right.z - up.z);
        tessellator.addVertex(x1 + right.x - up.x, y1 + right.y - up.y, z1 + right.z - up.z);

        tessellator.draw();

        GL11.glPopAttrib();
    }
    public static void draw3dLineWithTexture(int textureId, double width, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b) {

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, 1);

        Tessellator tessellator = Tessellator.instance;
        double l = Math.sqrt(x2*x2 + y2*y2 + z2*x2);

        Vector3f norm = new Vector3f((float)(x2 - x1), (float) (y2 - y1), (float)(z2 - z1));
        norm.normalise(norm);

        Vector3f perp = new Vector3f(1, 0, 0);
        if (Math.abs(norm.x) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 0.0f;
            perp.z = 1.0f;
        } else if (Math.abs(norm.z) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 1.0f;
            perp.z = 0.0f;
        }

        Vector3f up = new Vector3f(0, 0, 0) ;
        Vector3f.cross(norm, perp, up);
        up.normalise(up);

        Vector3f right = new Vector3f();
        Vector3f.cross(norm, up, right);

        up.x *= (float) (width * 0.5);
        up.y *= (float) (width * 0.5);
        up.z *= (float) (width * 0.5);

        right.x *= (float) (width * 0.5);
        right.y *= (float) (width * 0.5);
        right.z *= (float) (width * 0.5);

        tessellator.startDrawing(GL11.GL_QUADS);

        tessellator.addVertex(x1 - up.x - right.x, y1 - up.y - right.y, z1 - up.z - right.z);
        tessellator.addVertex(x1 + up.x - right.x, y1 + up.y - right.y, z1 + up.z - right.z);
        tessellator.addVertex(x2 + up.x - right.x, y2 + up.y - right.y, z2 + up.z - right.z);
        tessellator.addVertex(x2 - up.x - right.x, y2 - up.y - right.y, z2 - up.z - right.z);

        tessellator.addVertex(x1 - up.x  + right.x, y1 - up.y + right.y, z1 - up.z + right.z);
        tessellator.addVertex(x2 - up.x  + right.x, y2 - up.y + right.y, z2 - up.z + right.z);
        tessellator.addVertex(x2 + up.x  + right.x, y2 + up.y + right.y, z2 + up.z + right.z);
        tessellator.addVertex(x1 + up.x  + right.x, y1 + up.y + right.y, z1 + up.z + right.z);

        tessellator.addVertex(x1 - right.x + up.x, y1 - right.y + up.y, z1 - right.z + up.z);
        tessellator.addVertex(x1 + right.x + up.x, y1 + right.y + up.y, z1 + right.z + up.z);
        tessellator.addVertex(x2 + right.x + up.x, y2 + right.y + up.y, z2 + right.z + up.z);
        tessellator.addVertex(x2 - right.x + up.x, y2 - right.y + up.y, z2 - right.z + up.z);

        tessellator.addVertex(x1 - right.x - up.x, y1 - right.y - up.y, z1 - right.z - up.z);
        tessellator.addVertex(x2 - right.x - up.x, y2 - right.y - up.y, z2 - right.z - up.z);
        tessellator.addVertex(x2 + right.x - up.x, y2 + right.y - up.y, z2 + right.z - up.z);
        tessellator.addVertex(x1 + right.x - up.x, y1 + right.y - up.y, z1 + right.z - up.z);

        tessellator.draw();

        GL11.glPopAttrib();
    }


	public static ItemStack[] cloneStackArray(ItemStack[] stacks){
		ItemStack[] _result = new ItemStack[stacks.length];
		for (int i = 0; i < _result.length; i++) {
			ItemStack stack = stacks[i];
			_result[i] = new ItemStack(stack.itemID,  stack.stackSize, stack.getMetadata());
		}
		return _result;
	}

	public static ItemStack removeItemFromStack(ItemStack stack) {
		ItemStack newStack = stack.copy();
		newStack.stackSize = 1;
		stack.stackSize--;
		return newStack;
	}


	public static PipeStack getItemFromInventoryNoCatch(World world, int x, int y, int z, Direction dir, int stackTimer) {
		PipeStack returnStack = null;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof Container) {
			Container inventory = (Container) te;
			String inventoryName = inventory.getNameTranslationKey();

			/*if (te instanceof IItemIO && !isFromIronChests) {
				sunsetsatellite.catalyst.core.util.Direction sdir = sunsetsatellite.catalyst.core.util.Direction.getDirectionFromSide(dir.getId()).getOpposite();
				IItemIO itemIo = (IItemIO) te;

				Connection con = itemIo.getItemIOForSide(sdir);
				if (con == Connection.OUTPUT || con == Connection.BOTH) {
					int index = itemIo.getActiveItemSlotForSide(sdir);

					ItemStack stack = inventory.getStackInSlot(index);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setInventorySlotContents(index, stack);
					}
				}
			} else */
			{
				if (Objects.equals(inventoryName, "container.chest.name")) {
					inventory = BlockLogicChest.getInventory(world, x, y ,z);
				}

				if (Objects.equals(inventoryName, "container.chest.name")
					|| Objects.equals(inventoryName, "container.dispenser.name")
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
							/*
							TileEntityFilter filter = (TileEntityFilter) inventory;
							color = filter.getColorInSlot(j - 1);
							 */
						}

						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer, color);
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
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(j - 1, stack);
						return returnStack;
					}
				} else if (Objects.equals(inventoryName, "container.crafter.name")) {
					/*
					TileEntityCrafter ac = (TileEntityCrafter) te;
					ItemStack stack = ac.removeOneResult();
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
					} else {
						ItemStack extra = ac.extraOutputs.getStackInSlot(0);
						if (extra != null) {
							ItemStack r = removeItemFromStack(extra);
							if (extra.stackSize <= 0) {
								ac.extraOutputs.setInventorySlotContents(0, null);
							}
							returnStack = new PipeStack(r, dir, stackTimer);
						}
					}
					 */
				} else if (te instanceof TileEntityFlag) {
					ItemStack stack = inventory.getItem(36);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(36, stack);
					}
				} else if (inventory.getContainerSize() > 0) {
					ItemStack stack = inventory.getItem(0);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setItem(0, stack);
					}
				}
			}
		} else if (te instanceof TileEntityChute && dir == Direction.UP) {
			ItemStack stack = ((TileEntityChute)te).removeOneItem();
			if (stack != null) {
				returnStack = new PipeStack(stack, dir, stackTimer);
			}
		}

		return returnStack;
	}

	public static PipeStack getItemFromInventory(World world, int x, int y, int z, Direction dir, int stackTimer) {
		PipeStack result = null;
		try {
			result = getItemFromInventoryNoCatch(world, x, y, z, dir, stackTimer);

		} catch(Exception e) {
			PotatoTech.LOGGER.error(e.getMessage());
		}

		return result;
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

		if (Objects.equals(inventoryName, "container.furnace.name")
			|| Objects.equals(inventoryName, "container.furnace_blast.name")
			|| Objects.equals(inventoryName, "container.trommel.name"))
		{
			int fuelSlot = 1;
			int inputSlot = 0;

			if (Objects.equals(inventoryName, "container.trommel.name")) {
				fuelSlot = 4;
				for (; inputSlot < 3; inputSlot++) {
					ItemStack s = inventory.getItem(inputSlot);
					int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : s.getMaxStackSize();
					if (s == null || s.canStackWith(stack) && s.stackSize < maxStackSize) break;
				}
			}

			int targetSlot = direction == Direction.UP ? fuelSlot : inputSlot;

			ItemStack furnaceStack = inventory.getItem(targetSlot);

			if (furnaceStack == null) {
				inventory.setItem(targetSlot, stack);
				hasInserted = true;
			} else {
				int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : furnaceStack.getMaxStackSize();
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
		} else {
			if (Objects.equals(inventoryName, "container.crafter.name")) {
				/*
				TileEntityCrafter ac = (TileEntityCrafter) inventory;
				hasInserted = ac.insertItem(stack);

				 */
			} else {
				// May be a chest or other mass-storage device
				int j = 0;
				ItemStack chestStack;
				while (j < inventorySize) {
					chestStack = inventory.getItem(j);

					if (chestStack == null) {
						if (!inventoryName.equals("conatiner.filter.name") && !inventoryName.equals("container.crafter.name")) {
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

					j++;
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

		/*
		if (te instanceof TileEntityFilter) {
			return ((TileEntityFilter) te).canInsertItem(item);
		}
		 */

		Container inventory = (Container)te;
		int inventorySize = inventory.getContainerSize();
		String inventoryName = inventory.getNameTranslationKey();

		if (Objects.equals(inventoryName, "container.chest.name")
			|| Objects.equals(inventoryName, "container.dispenser.name")
			|| Objects.equals(inventoryName, "container.filter.name")
			|| Objects.equals(inventoryName, "container.crafter.name")
		) {
			int j = 0;
			ItemStack chestStack;
			while (j < inventorySize) {
				chestStack = inventory.getItem(j);
				if (chestStack == null) {
					return true;
				}
				int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : chestStack.getMaxStackSize();
				if (chestStack.canStackWith(item) && chestStack.stackSize < maxStackSize) {
					return true;
				}
				j++;
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
					int maxStackSize = inventory.getMaxStackSize() != 64 ? inventory.getMaxStackSize() : s.getMaxStackSize();
					if (s == null || s.canStackWith(item) && s.stackSize < maxStackSize) break;
				}
			}

			int targetSlot = dir == Direction.UP ? fuelSlot : inputSlot;

			ItemStack furnaceStack = inventory.getItem(targetSlot);

			if (furnaceStack == null) {
				return true;
			} else {
				return furnaceStack.canStackWith(item);
			}
		}

		return false;
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

	public static PipeStack peekItemFromInventory(World world, int x, int y, int z, Direction dir, int stackTimer) {
		PipeStack result = null;
		try {
			result = peekItemFromInventoryNoCatch(world, x, y, z, dir, stackTimer);
		} catch (Exception e) {
			PotatoTech.LOGGER.error(e.getMessage());
		}
		return result;
	}

	public static PipeStack peekItemFromInventoryNoCatch(World world, int x, int y, int z, Direction dir, int stackTimer) {
		PipeStack returnStack = null;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof Container) {
			Container inventory = (Container) te;
			String inventoryName = inventory.getNameTranslationKey();

			if (Objects.equals(inventoryName, "container.chest.name")) {
				inventory = BlockLogicChest.getInventory(world, x, y ,z);
			}

			if (Objects.equals(inventoryName, "container.chest.name")
				|| Objects.equals(inventoryName, "container.dispenser.name")
				|| Objects.equals(inventoryName, "container.filter.name")
			) {
				int inventorySize = inventory.getContainerSize();
				ItemStack stack = null;
				int j = 0;

				if (!inventoryName.equals("container.filter.name")) {
					while (stack == null && j < inventorySize) stack = inventory.getItem(j++);
					if (stack != null && stack.stackSize <= 1) {
						returnStack = null;
					}
				} else {
					while (stack == null && j < inventorySize) {
						stack = inventory.getItem(j++);
						if (stack != null && stack.stackSize++ <= 1) {
							returnStack = null;
						}
					}
				}

				if (stack != null && j > 0) {
					returnStack = new PipeStack(stack.copy(), dir, stackTimer);
					return returnStack;
				}
			} else if (inventoryName.equals("container.trommel.name")) {
				int inventorySize = 4;
				ItemStack stack = null;
				int j = 0;
				while (stack == null && j < inventorySize) stack = inventory.getItem(j++);

				if (stack != null && j > 0) {
					returnStack = new PipeStack(stack, dir, stackTimer);
					return returnStack;
				}
			} else if (inventoryName.equals("container.crafter.name")) {
				/*
				TileEntityCrafter ac = (TileEntityCrafter) te;
				ItemStack stack = ac.craftResult.getStackInSlot(0).copy();
				if (stack != null) {
					returnStack = new PipeStack(stack, dir, stackTimer);
				} else {
					ItemStack extra = ac.extraOutputs.getStackInSlot(0).copy();
					if (extra != null) {
						returnStack = new PipeStack(extra, dir, stackTimer);
					}
				}
				 */
			} else if (inventory.getContainerSize() > 2){
				ItemStack stack = inventory.getItem(2);
				if (stack != null) {
					stack = stack.copy();
					returnStack = new PipeStack(stack, dir, stackTimer);
				}
			}
		} else if (te instanceof TileEntityChute && dir == Direction.UP) {
			TileEntityChute.ChuteEntry firstKey = null;
			for (Map.Entry<TileEntityChute.ChuteEntry, Integer> entry : ((TileEntityChute) te).contents.entrySet()) {
				firstKey = entry.getKey();
				break;
			}
			if (firstKey == null) return null;

			ItemStack stack = new ItemStack(firstKey.getItem(), 1, firstKey.metadata);
			returnStack = new PipeStack(stack, dir, stackTimer);
		}

		return returnStack;
	}
}
