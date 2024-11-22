package goldenage.potatotech;

import com.mojang.nbt.CompoundTag;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;

public class PipeStack {
    public ItemStack stack;
    public Direction direction;
	public short color;
    public int timer = 0;

    public PipeStack(ItemStack stack, Direction direction, int timer) {
        this.stack = stack;
        this.direction = direction;
        this.timer = timer;
		this.color = 0;
    }

	public PipeStack(ItemStack stack, Direction direction, int timer, short color) {
		this.stack = stack;
		this.direction = direction;
		this.timer = timer;
		this.color = color;
	}

    public PipeStack() {
    }

    public void writeToNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putShort("direction", (short) this.direction.getId());
        nbttagcompound.putShort("timer", (short) this.timer);
		nbttagcompound.putShort("color", (short) this.color);
		if (stack != null) stack.writeToNBT(nbttagcompound);
    }

    public void readFromNBT(CompoundTag nbttagcompound) {
        this.direction = Direction.getDirectionById(nbttagcompound.getShort("direction"));
        this.timer = nbttagcompound.getShort("timer");
		this.color = nbttagcompound.getShort("color");
        this.stack = ItemStack.readItemStackFromNbt(nbttagcompound);
    }

    public static PipeStack readPipeStackFromNbt(CompoundTag nbt) {
        if (nbt == null) {
            return null;
        }
        PipeStack stack = new PipeStack();
        stack.readFromNBT(nbt);
        return stack;
    }
}
