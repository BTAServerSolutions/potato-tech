package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.ListTag;
import goldenage.potatotech.PipeStack;
import goldenage.potatotech.Util;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.Packet140TileEntityData;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Direction;

import java.util.*;

public class TileEntityPipe extends TileEntity {
    public PipeStack[] stacks;
    public int stackTimer = 10;
    public TileEntityPipe() {
		stacks = new PipeStack[7];
    }

    public void dropItems() {
        for (PipeStack stack : stacks) {
            if (stack != null) worldObj.dropItem(x, y, z, stack.stack);
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
				if (i > 0) {
					Direction dir = Direction.getDirectionById(i - 1);
					float xof = dir.getOffsetX();
					float yof = dir.getOffsetY();
					float zof = dir.getOffsetZ();

					pos[0] = 0.5f + xof * 0.325f;
					pos[1] = 0.5f + yof * 0.325f;
					pos[2] = 0.5f + zof * 0.325f;
				} else {
					pos[0] = 0.5f;
					pos[1] = 0.5f;
					pos[2] = 0.5f;
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

    public boolean isDirectional() {
        int meta = worldObj.getBlockMetadata(x, y, z);
        return (meta & (1 << 2)) != 0;
    }

    public Direction getDirection() {
        int meta = worldObj.getBlockMetadata(x, y, z);
        return Direction.getDirectionById(meta >> 3);
    }
    public boolean isPointingTo(int x, int y, int z) {
        int meta = worldObj.getBlockMetadata(this.x, this.y, this.z);
        Direction dir = Direction.getDirectionById(meta >> 3);
        boolean sameX = dir.getOffsetX() == this.x - x;
        boolean sameY = dir.getOffsetY() == this.y - y;
        boolean sameZ = dir.getOffsetZ() == this.z - z;
        return sameX && sameY && sameZ;
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
        //nbttagcompound.putInt("visualConnections", visualConnections);
        //nbttagcompound.putInt("visualColor", visualColor);
    }

    @Override
    public void tick() {
		int meta = worldObj.getBlockMetadata(x, y, z);
		int type = meta & 3;

		// Input

		stackTimer--;
		if (stackTimer <= 0) {
			stackTimer = 5;
			if (type == 1) {
				for (Direction dir : Direction.values()) {
					PipeStack stack = stacks[dir.getId() + 1];
					if (stack == null) {
						stack = Util.getItemFromInventory(worldObj, x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ(), dir, 0);
						stacks[dir.getId() + 1] = stack;
					}
				}
			}
		}

		// Output
		for (Direction dir : Direction.values()) {
			PipeStack stack = stacks[dir.getId() + 1];
			if (stack != null && stack.direction == dir.getOpposite()) {
				TileEntity te = worldObj.getBlockTileEntity(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
				if (te instanceof IInventory) {
					if (type == 2) {
						boolean inserted = Util.insertOnInventory((IInventory) te, stack.stack, dir);
						if (inserted) stacks[dir.getId() + 1] = null;
					}
				} else if (te instanceof TileEntityPipe) {
					TileEntityPipe p = (TileEntityPipe) te;
					if (p.stacks[dir.getOpposite().getId() + 1] == null) {
						p.stacks[dir.getOpposite().getId() + 1] = stack;
						stacks[dir.getId() + 1] = null;
					}
				}
			}
		}

		// Move
		{
			PipeStack stack = stacks[0];
			if (stack != null) {
				List<Direction> freeDir = new ArrayList<>();
				for (int i = 0; i < 6; i++) {
					PipeStack stack2 = stacks[i + 1];
					Direction dir = Direction.getDirectionById(i);
					TileEntity te = worldObj.getBlockTileEntity(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
					if (stack2 == null && (te instanceof IInventory || te instanceof TileEntityPipe) && i != stack.direction.getId()) {
						freeDir.add(Direction.getDirectionById(i));
					}
				}

				if (!freeDir.isEmpty()) {
					int selected = (int) (Math.random() * freeDir.size());
					Direction dir = freeDir.get(selected);
					stack.direction = dir.getOpposite();
					stacks[dir.getId() + 1] = stack;
					stacks[0] = null;
				}
			}
		}

		for (int i = 0; i < 6; i++) {
			PipeStack stack = stacks[i + 1];
			if (stack != null && stack.direction == Direction.getDirectionById(i)) {
				if (stacks[0] == null) {
					stacks[0] = stack;
					stacks[i + 1] = null;
				}
			}
		}
	}

    @Override
    public Packet getDescriptionPacket() {
        return new Packet140TileEntityData(this);
    }
}
