package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.*;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.BlockChest;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;
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

		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory) {
			IInventory inventory = (IInventory) te;
			String inventoryName = inventory.getInvName();

			boolean isFromIronChests = Objects.equals(inventoryName, "Iron Chest")
				|| Objects.equals(inventoryName, "Gold Chest")
				|| Objects.equals(inventoryName, "Diamond Chest")
				|| Objects.equals(inventoryName, "Steel Chest")
				|| Objects.equals(inventoryName, "Big Chest");

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
			} else */{
				if (Objects.equals(inventoryName, "Chest")) {
					inventory = BlockChest.getInventory(world, x, y ,z);
				}

				if (Objects.equals(inventoryName, "Chest")
					|| Objects.equals(inventoryName, "Trap")
					|| Objects.equals(inventoryName, "Filter")
					|| Objects.equals(inventoryName, "BlockCrusher")
					|| Objects.equals(inventoryName, "BlockPlacer")
					|| Objects.equals(inventoryName, "Large Chest")
					|| isFromIronChests
				) {
					int inventorySize = inventory.getSizeInventory();
					ItemStack stack = null;
					int j = 0;

					if (!inventoryName.equals("Filter")) {
						for (; stack == null && j < inventorySize; j++) stack = inventory.getStackInSlot(j);
					} else {
						for (; stack == null && j < inventorySize; j++) {
							stack = inventory.getStackInSlot(j);
							if (stack != null && stack.stackSize <= 1) stack = null;
						}
					}

					if (stack != null && j > 0) {
						short color = 0;
						if (Objects.equals(inventoryName, "Filter")) {
							TileEntityFilter filter = (TileEntityFilter) inventory;
							color = filter.getColorInSlot(j - 1);
						}

						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer, color);
						if (stack.stackSize <= 0) stack = null;
						inventory.setInventorySlotContents(j - 1, stack);
						return returnStack;
					}

				} else if (Objects.equals(inventoryName, "Trommel")) {
					int inventorySize = 4;
					ItemStack stack = null;
					int j = 0;
					for (; stack == null && j < inventorySize; j++) stack = inventory.getStackInSlot(j);

					if (stack != null && j > 0) {
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setInventorySlotContents(j - 1, stack);
						return returnStack;
					}
				} else if (Objects.equals(inventoryName, "Auto Crafter")) {
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
				} else if (te instanceof TileEntityFlag){
					ItemStack stack = inventory.getStackInSlot(36);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setInventorySlotContents(36, stack);
					}
				} else if (inventory.getSizeInventory() > 0) {
					ItemStack stack = inventory.getStackInSlot(0);
					if (stack != null) {
						returnStack = new PipeStack(removeItemFromStack(stack), dir, stackTimer);
						if (stack.stackSize <= 0) stack = null;
						inventory.setInventorySlotContents(0, stack);
					}
				}
			}
		} /*else if (te instanceof TileEntityChute && dir == Direction.UP) {
			ItemStack stack = ((TileEntityChute)te).removeOneItem();
			if (stack != null) {
				returnStack = new PipeStack(stack, dir, stackTimer);
			}
		}
		*/

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




	public static boolean insertOnInventoryNoCatch(IInventory inventory, ItemStack stack, Direction direction) {
		boolean hasInserted = false;
		if (inventory == null) {
			System.out.println(Arrays.toString(new NullPointerException("Null Pointer in insertOnInventory!!").getStackTrace()));
			StringBuilder builder = new StringBuilder("Error something is null when it shouldn't be!! | Inventory: ");
			System.out.println(builder);
			PotatoTech.LOGGER.info(builder.toString());
			return false;
		}
		int inventorySize = inventory.getSizeInventory();
		String inventoryName = inventory.getInvName();

		if (Objects.equals(inventoryName, "Furnace") || Objects.equals(inventoryName, "Blast Furnace") || Objects.equals(inventoryName, "Trommel")) {
			int fuelSlot = 1;
			int inputSlot = 0;

			if (Objects.equals(inventoryName, "Trommel")) {
				fuelSlot = 4;
				for (; inputSlot < 3; inputSlot++) {
					ItemStack s = inventory.getStackInSlot(inputSlot);
					int maxStackSize = inventory.getInventoryStackLimit() != 64 ? inventory.getInventoryStackLimit() : s.getMaxStackSize();
					if (s == null || s.canStackWith(stack) && s.stackSize < maxStackSize) break;
				}
			}

			int targetSlot = direction == Direction.UP ? fuelSlot : inputSlot;

			ItemStack furnaceStack = inventory.getStackInSlot(targetSlot);

			if (furnaceStack == null) {
				inventory.setInventorySlotContents(targetSlot, stack);
				hasInserted = true;
			} else {
				int maxStackSize = inventory.getInventoryStackLimit() != 64 ? inventory.getInventoryStackLimit() : furnaceStack.getMaxStackSize();
				if (furnaceStack.canStackWith(stack) && furnaceStack.stackSize < maxStackSize) {
					furnaceStack.stackSize++;
					inventory.setInventorySlotContents(targetSlot, furnaceStack);
					hasInserted = true;
				}
			}
		} else if (inventory instanceof TileEntityFlag) {
            int targetSlot = 36;
			ItemStack flagStack = inventory.getStackInSlot(targetSlot);

			if (flagStack == null) {
				inventory.setInventorySlotContents(targetSlot, stack);
				hasInserted = true;
			} else {
				int maxStackSize = inventory.getInventoryStackLimit() != 64 ? inventory.getInventoryStackLimit() : flagStack.getMaxStackSize();
				if (flagStack.canStackWith(stack) && flagStack.stackSize < maxStackSize) {
					flagStack.stackSize++;
					inventory.setInventorySlotContents(targetSlot, flagStack);
					hasInserted = true;
				}
			}
		} else {
		    if (Objects.equals(inventoryName, "Auto Crafter") && inventory instanceof TileEntityCrafter) {
                TileEntityCrafter ac = (TileEntityCrafter) inventory;
                hasInserted = ac.insertItem(stack);
                } else {
    		    // May be a chest or other mass-storage device
    			int j = 0;
    			ItemStack chestStack;
    			while (j < inventorySize) {
    				chestStack = inventory.getStackInSlot(j);

    				if (chestStack == null) {
    					if (!inventoryName.equals("Filter") && !inventoryName.equals("Auto Crafter")) {
    						inventory.setInventorySlotContents(j, stack);
    						hasInserted = true;
    					}
    					break;
    				}

    				int maxStackSize = inventory.getInventoryStackLimit() != 64 ? inventory.getInventoryStackLimit() : chestStack.getMaxStackSize();
    				if (chestStack.canStackWith(stack) && chestStack.stackSize < maxStackSize) {
    					chestStack.stackSize++;
    					inventory.setInventorySlotContents(j, chestStack);

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
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te == null) {
			return false;
		}

		if (!(te instanceof IInventory)) {
			return false;
		}

		if (te instanceof TileEntityFilter) {
			return ((TileEntityFilter) te).canInsertItem(item);
		}

		IInventory inventory = (IInventory)te;
		int inventorySize = inventory.getSizeInventory();
		String inventoryName = inventory.getInvName();

		if (Objects.equals(inventoryName, "Chest")
			|| Objects.equals(inventoryName, "Large Chest")
			|| Objects.equals(inventoryName, "Trap")
			|| Objects.equals(inventoryName, "BlockCrusher")
			|| Objects.equals(inventoryName, "BlockPlacer")
			|| Objects.equals(inventoryName, "Filter")
			|| Objects.equals(inventoryName, "Auto Crafter")
		) {
 			int j = 0;
			ItemStack chestStack;
			while (j < inventorySize) {
				chestStack = inventory.getStackInSlot(j);
				if (chestStack == null) {
					return true;
				}
				int maxStackSize = inventory.getInventoryStackLimit() != 64 ? inventory.getInventoryStackLimit() : chestStack.getMaxStackSize();
				if (chestStack.canStackWith(item) && chestStack.stackSize < maxStackSize) {
					return true;
				}
				j++;
			}
		} else if (inventory instanceof TileEntityFlag) {
			int targetSlot = 36;
			ItemStack flagStack = inventory.getStackInSlot(targetSlot);

			if (flagStack == null) {
				return true;
			} else {
                return flagStack.canStackWith(item);
			}
		} else {
			int fuelSlot = 1;
			int inputSlot = 0;

			if (Objects.equals(inventoryName, "Trommel")) {
				fuelSlot = 4;
				for (; inputSlot < 3; inputSlot++) {
					ItemStack s = inventory.getStackInSlot(inputSlot);
					int maxStackSize = inventory.getInventoryStackLimit() != 64 ? inventory.getInventoryStackLimit() : s.getMaxStackSize();
					if (s == null || s.canStackWith(item) && s.stackSize < maxStackSize) break;
				}
			}

			int targetSlot = dir == Direction.UP ? fuelSlot : inputSlot;

			ItemStack furnaceStack = inventory.getStackInSlot(targetSlot);

			if (furnaceStack == null) {
				return true;
			} else {
                return furnaceStack.canStackWith(item);
			}
		}

		return false;
	}

	public static boolean insertOnInventory(IInventory inventory, ItemStack stack, Direction direction) {
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
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory) {
			IInventory inventory = (IInventory) te;
			String inventoryName = inventory.getInvName();

			boolean isFromIronChests = inventoryName.equals("Iron Chest")
				|| inventoryName.equals("Gold Chest")
				|| inventoryName.equals("Diamond Chest")
				|| inventoryName.equals("Steel Chest")
				|| inventoryName.equals("Big Chest");

			if (inventoryName.equals("Chest")) {
				inventory = BlockChest.getInventory(world, x, y ,z);
			}

			if (inventoryName.equals("Chest")
				|| inventoryName.equals("Trap")
				|| inventoryName.equals("Filter")
				|| inventoryName.equals("BlockCrusher")
				|| inventoryName.equals("BlockPlacer")
				|| inventoryName.equals("Large Chest")
				|| isFromIronChests
			) {
				int inventorySize = inventory.getSizeInventory();
				ItemStack stack = null;
				int j = 0;

				if (!inventoryName.equals("Filter")) {
					while (stack == null && j < inventorySize) stack = inventory.getStackInSlot(j++);
					if (stack != null && stack.stackSize <= 1) {
						returnStack = null;
					}
				} else {
					while (stack == null && j < inventorySize) {
						stack = inventory.getStackInSlot(j++);
						if (stack != null && stack.stackSize++ <= 1) {
							returnStack = null;
						}
					}
				}

				if (stack != null && j > 0) {
					returnStack = new PipeStack(stack.copy(), dir, stackTimer);
					return returnStack;
				}
			} else if (inventoryName.equals("Trommel")) {
				int inventorySize = 4;
				ItemStack stack = null;
				int j = 0;
				while (stack == null && j < inventorySize) stack = inventory.getStackInSlot(j++);

				if (stack != null && j > 0) {
					returnStack = new PipeStack(stack, dir, stackTimer);
					return returnStack;
				}
			} else if (inventoryName.equals("Auto Crafter")) {
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
			} else if (inventory.getSizeInventory() > 2){
				ItemStack stack = inventory.getStackInSlot(2).copy();
				if (stack != null) {
					returnStack = new PipeStack(stack, dir, stackTimer);
				}
			}
		}/* else if (te instanceof TileEntityChute && dir == Direction.UP) {
			TileEntityChute.ChuteEntry firstKey = null;
			for (Map.Entry<TileEntityChute.ChuteEntry, Integer> entry : ((TileEntityChute) te).contents.entrySet()) {
				firstKey = entry.getKey();
				break;
			}
			if (firstKey == null) return null;

			ItemStack stack = new ItemStack(firstKey.getItem(), 1, firstKey.metadata);
			returnStack = new PipeStack(stack, dir, stackTimer);
		}*/

		return returnStack;
	}
}
