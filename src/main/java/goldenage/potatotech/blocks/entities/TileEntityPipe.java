package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import goldenage.potatotech.PipeStack;
import goldenage.potatotech.Util;
import net.minecraft.core.block.BlockChest;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.Packet140TileEntityData;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Direction;

import java.util.*;

public class TileEntityPipe extends TileEntity {
    public PipeStack[] stacks;

	// 0 - normal
	// 1 - insert
	// 2 - extract
	// 3 - disable
	public int[] modeBySide;
	public int maxInputTimer = 4;
	public int inputTimer = maxInputTimer;

	public int maxPipeStackTimer = 2;

    public TileEntityPipe() {
		stacks = new PipeStack[7];
		modeBySide = new int[6];
    }

    public void dropItems() {
        for (int i = 0; i < stacks.length; i++) {
			PipeStack stack = stacks[i];
            if (stack != null) {
				worldObj.dropItem(x, y, z, stack.stack);
				stacks[i] = null;
			}
        }
    }

    public List<ItemStack> getStacksInPipe() {
        List<ItemStack> l = new ArrayList<>(this.stacks.length);
        for (PipeStack stack : stacks) {
            l.add(stack != null ? stack.stack : null);
        }
        return l;
    }

    public List<float[]> getStacksInPipePosition() {
        List<float[]> l = new ArrayList<>(this.stacks.length);
		int i = 0;
        for (PipeStack stack : stacks) {
            float[] pos = new float[3];
			if (stack != null) {
				float t = (float)stack.timer / (float) maxPipeStackTimer;
				if (t > 1.0) t = 1.0f;

				if (i > 0) {
					Direction dir = Direction.getDirectionById(i - 1);

					float x1 = dir.getOffsetX() * 0.325f;
					float y1 = dir.getOffsetY() * 0.325f;
					float z1 = dir.getOffsetZ() * 0.325f;

					float x0 = x1 + stack.direction.getOffsetX() * 0.325f;
					float y0 = y1 + stack.direction.getOffsetY() * 0.325f;
					float z0 = z1 + stack.direction.getOffsetZ() * 0.325f;

					float xof = x0 * (1-t) + x1 * t;
					float yof = y0 * (1-t) + y1 * t;
					float zof = z0 * (1-t) + z1 * t;

					pos[0] = 0.5f + xof;
					pos[1] = 0.5f + yof;
					pos[2] = 0.5f + zof;
				} else {
					float x0 = stack.direction.getOffsetX() * 0.325f;
					float y0 = stack.direction.getOffsetY() * 0.325f;
					float z0 = stack.direction.getOffsetZ() * 0.325f;

					float xof = x0 * (1-t);
					float yof = y0 * (1-t);
					float zof = z0 * (1-t);

					pos[0] = 0.5f + xof;
					pos[1] = 0.5f + yof;
					pos[2] = 0.5f + zof;
				}
			} else {
				pos[0] = 0.5f;
				pos[1] = 0.5f;
				pos[2] = 0.5f;
			}
            l.add(pos);
			i++;
        }
        return l;
    }

    @Override
    public void readFromNBT(CompoundTag nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        ListTag nbttaglist = nbttagcompound.getList("Items");
        this.stacks = new PipeStack[7];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
            this.stacks[i] = PipeStack.readPipeStackFromNbt(nbttagcompound1);
			if (this.stacks[i].stack == null) this.stacks[i] = null;
        }

		for (int i = 0; i < modeBySide.length; i++) {
			modeBySide[i] = nbttagcompound.getInteger("mode"+i);
		}

        //visualConnections = nbttagcompound.getInteger("visualConnections");
        //visualColor = nbttagcompound.getInteger("visualColor");
    }

    @Override
    public void writeToNBT(CompoundTag nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        ListTag nbttaglist = new ListTag();
        for (PipeStack stack : this.stacks) {
			if (stack == null) {
				PipeStack s = new PipeStack();
				s.direction = Direction.getDirectionById(0);
				CompoundTag nbttagcompound1 = new CompoundTag();
				s.writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			} else {
				CompoundTag nbttagcompound1 = new CompoundTag();
				stack.writeToNBT(nbttagcompound1);
				nbttaglist.addTag(nbttagcompound1);
			}
        }
        nbttagcompound.put("Items", nbttaglist);

		for (int i = 0; i < modeBySide.length; i++) {
			nbttagcompound.putInt("mode"+i, modeBySide[i]);
		}
    }

	private void inputItems() {
		for (Direction dir : Direction.directions) {
			if (modeBySide[dir.getId()] == 2) {
				PipeStack stack = stacks[dir.getId() + 1];
				if (stack == null) {
					stack = Util.getItemFromInventory(worldObj, x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ(), dir, 0);
					if (stack != null) stack.timer = 0;
					stacks[dir.getId() + 1] = stack;
				}
			}
		}
	}

	private void outputItems() {
		// Output
		for (Direction dir : Direction.directions) {
			PipeStack stack = stacks[dir.getId() + 1];
			if (stack != null && stack.direction == dir.getOpposite() && stack.timer >= maxPipeStackTimer) {
				TileEntity te = worldObj.getBlockTileEntity(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
				if (te instanceof IInventory) {
					if (modeBySide[dir.getId()] <= 1) {
						IInventory inventory = (IInventory) te;
						if (Objects.equals(inventory.getInvName(), "Chest")) {
							inventory = BlockChest.getInventory(worldObj, x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
						}

						boolean inserted = Util.insertOnInventory(inventory, stack.stack, dir);
						if (inserted) stacks[dir.getId() + 1] = null;
					}
				}
			}
		}
	}

	private void moveItems() {
		for (Direction dir : Direction.directions) {
			PipeStack stack = stacks[dir.getId() + 1];
			if (stack != null && stack.direction == dir.getOpposite() && stack.timer >= maxPipeStackTimer) {
				TileEntity te = worldObj.getBlockTileEntity(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
				if (te instanceof TileEntityPipe) {
					TileEntityPipe p = (TileEntityPipe) te;
					if (p.stacks[dir.getOpposite().getId() + 1] == null) {
						stack.timer = 0;
						p.stacks[dir.getOpposite().getId() + 1] = stack;
						stacks[dir.getId() + 1] = null;
					}
				}
			}
		}

		if (stacks[0] != null && stacks[0].timer >= maxPipeStackTimer) {
			List<Direction> freeDir = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				PipeStack stack2 = stacks[i + 1];
				Direction dir = Direction.getDirectionById(i);
				TileEntity te = worldObj.getBlockTileEntity(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
				if (stack2 == null && (te instanceof IInventory || te instanceof TileEntityPipe) && i != stacks[0].direction.getId() && modeBySide[i] < 2) {
					if (te instanceof TileEntityPipe) {
						if (((TileEntityPipe)te).modeBySide[dir.getOpposite().getId()] == 3) continue;
						if (((TileEntityPipe)te).modeBySide[dir.getOpposite().getId()] == 1) continue;
					}
					if (this.modeBySide[i] == 1) {
						freeDir.clear();
						freeDir.add(Direction.getDirectionById(i));
						break;
					}
					freeDir.add(Direction.getDirectionById(i));
				}
			}

			if (!freeDir.isEmpty()) {
				int selected = (int) (Math.random() * freeDir.size());
				Direction dir = freeDir.get(selected);
				stacks[0].direction = dir.getOpposite();
				stacks[0].timer = 0;
				stacks[dir.getId() + 1] = stacks[0];
				stacks[0] = null;
			}
		}

		boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(x, y, z) || worldObj.isBlockGettingPowered(x, y, z);
		if (!isPowered) {
			for (int i = 0; i < 6; i++) {
				PipeStack stack = stacks[i + 1];
				if (stack != null && stack.direction == Direction.getDirectionById(i) && stack.timer >= maxPipeStackTimer) {
					if (stacks[0] == null) {
						stacks[0] = stack;
						stacks[0].timer = 0;
						stacks[i + 1] = null;
					}
				}
			}
		}
	}

    @Override
    public void tick() {
        for (PipeStack pipeStack : stacks) if (pipeStack != null) pipeStack.timer++;

		outputItems();

		inputTimer--;
		if (inputTimer < 0) {
			inputItems();
			inputTimer = maxInputTimer;
		}

		moveItems();
	}

    @Override
    public Packet getDescriptionPacket() {
        return new Packet140TileEntityData(this);
    }
}
