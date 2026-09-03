package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import goldenage.potatotech.PipeStack;
import goldenage.potatotech.Util;
import goldenage.potatotech.compat.catalyst.CatalystItemIoCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TileEntityPipe extends TileEntity {
	public PipeStack[] stacks;

	// 0 - normal
	// 1 - insert
	// 2 - extract
	// 3 - disable
	public short[] modeBySide;
	public short[] colorBySide;
	public int maxInputTimer;
	public int inputTimer;
	public int maxPipeStackTimer;
	public int connectionRenderRevision;
	public int maxStackSize = 1;
	private int clientConnectionRenderRevision = -1;

	public TileEntityPipe() {
		stacks = new PipeStack[7];
		modeBySide = new short[6];
		colorBySide = new short[6];

		maxInputTimer = 12;
		inputTimer = maxInputTimer;
		maxPipeStackTimer = 6;
	}

	public void dropItems() {
		for (int i = 0; i < stacks.length; i++) {
			PipeStack stack = stacks[i];
			if (stack != null) {
				worldObj.dropItem(tilePos.x, tilePos.y, tilePos.z, stack.stack);
				stacks[i] = null;
				worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
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
					Direction dir = Direction.fromId(i - 1);

					float x1 = dir.offsetX() * 0.325f;
					float y1 = dir.offsetY() * 0.325f;
					float z1 = dir.offsetZ() * 0.325f;

					float x0 = x1 + stack.direction.offsetX() * 0.325f;
					float y0 = y1 + stack.direction.offsetY() * 0.325f;
					float z0 = z1 + stack.direction.offsetZ() * 0.325f;

					float xof = x0 * (1-t) + x1 * t;
					float yof = y0 * (1-t) + y1 * t;
					float zof = z0 * (1-t) + z1 * t;

					pos[0] = 0.5f + xof;
					pos[1] = 0.5f + yof;
					pos[2] = 0.5f + zof;
				} else {
					float x0 = stack.direction.offsetX() * 0.325f;
					float y0 = stack.direction.offsetY() * 0.325f;
					float z0 = stack.direction.offsetZ() * 0.325f;

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
	public void readAdditionalData(@NotNull CompoundTag nbttagcompound) {
		ListTag nbttaglist = nbttagcompound.getList("Items");
		this.stacks = new PipeStack[7];
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			this.stacks[i] = PipeStack.readPipeStackFromNbt(nbttagcompound1);
			if (this.stacks[i].stack == null) this.stacks[i] = null;
		}

		for (int i = 0; i < modeBySide.length; i++) {
			modeBySide[i] = nbttagcompound.getShort("mode"+i);
		}
		for (int i = 0; i < colorBySide.length; i++) {
			colorBySide[i] = nbttagcompound.getShort("color"+i);
		}
		connectionRenderRevision = nbttagcompound.getInteger("connectionRenderRevision");
		if (worldObj != null && worldObj.isClientSide && clientConnectionRenderRevision != connectionRenderRevision) {
			clientConnectionRenderRevision = connectionRenderRevision;
			worldObj.markBlocksDirty(tilePos.x - 1, tilePos.y - 1, tilePos.z - 1, tilePos.x + 1, tilePos.y + 1, tilePos.z + 1);
		}
	}

	@Override
	public void writeAdditionalData(CompoundTag nbttagcompound) {
		ListTag nbttaglist = new ListTag();
		for (PipeStack stack : this.stacks) {
			if (stack == null) {
				PipeStack s = new PipeStack();
				s.direction = Direction.fromId(0);
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
			nbttagcompound.putShort("mode"+i, modeBySide[i]);
		}
		for (int i = 0; i < colorBySide.length; i++) {
			nbttagcompound.putShort("color"+i, colorBySide[i]);
		}
		nbttagcompound.putInt("connectionRenderRevision", connectionRenderRevision);
	}

	public void requestConnectionRenderUpdate() {
		connectionRenderRevision++;
		setChanged();
		worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
	}

	public void inputItems() {
		for (Direction dir : Direction.ID_MAP) {
			if (modeBySide[dir.id] == 2) {
				PipeStack stack = stacks[dir.id + 1];
				if (stack == null) {
					stack = Util.getItemFromInventory(worldObj, tilePos.x + dir.offsetX(), tilePos.y + dir.offsetY(), tilePos.z + dir.offsetZ(), dir, 0, maxStackSize);
					if (stack != null) {
						stack.timer = 0;
						if (stack.color == 0) {
							stack.color = colorBySide[dir.id];
						}
					}
					stacks[dir.id + 1] = stack;
					worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
				}
			}
		}
	}

	public void outputItems() {
		// Output
		for (Direction dir : Direction.ID_MAP) {
			PipeStack stack = stacks[dir.id + 1];
			if (stack != null && stack.direction == dir.opposite() && stack.timer >= maxPipeStackTimer) {
				int blockId = worldObj.getBlockId(tilePos.x + dir.offsetX(), tilePos.y + dir.offsetY(), tilePos.z + dir.offsetZ());
				TileEntity te = worldObj.getTileEntity(tilePos.x + dir.offsetX(), tilePos.y + dir.offsetY(), tilePos.z + dir.offsetZ());
				if (FabricLoader.getInstance().isModLoaded("catalyst-core") && CatalystItemIoCompat.isItemIo(te)) {
					if (modeBySide[dir.id] <= 1 && CatalystItemIoCompat.insert(te, dir, stack)) {
						if (stack.stack.stackSize <= 0) {
							stacks[dir.id + 1] = null;
						}
						worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
					}
					continue;
				}
				if (blockId != 0 && te instanceof Container && !(te instanceof TileEntityPipe)) {
					if (modeBySide[dir.id] <= 1) {
						Container inventory = (Container) te;
						if (Objects.equals(inventory.getNameTranslationKey(), "container.chest.name")) {
							inventory = BlockLogicChest.getInventory(worldObj, new TilePos(tilePos).add(dir));
						}

						boolean inserted = Util.insertPipeStackOnInventory(inventory, stack, dir);
						if (inserted) {
							if (stack.stack.stackSize <= 0) {
								stacks[dir.id + 1] = null;
							}
							worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
						}
					}
				}
			}
		}
	}

	public void moveItems() {
		for (Direction dir : Direction.ID_MAP) {
			PipeStack stack = stacks[dir.id + 1];
			if (stack != null && stack.direction == dir.opposite() && stack.timer >= maxPipeStackTimer) {
				int blockId = worldObj.getBlockId(tilePos.x + dir.offsetX(), tilePos.y + dir.offsetY(), tilePos.z + dir.offsetZ());
				TileEntity te = worldObj.getTileEntity(tilePos.x + dir.offsetX(), tilePos.y + dir.offsetY(), tilePos.z + dir.offsetZ());
				if (blockId != 0 && te instanceof TileEntityPipe) {
					TileEntityPipe p = (TileEntityPipe) te;
					if (p.stacks[dir.opposite().id + 1] == null) {
						int amount = Math.min(stack.stack.stackSize, p.maxStackSize);
						ItemStack moved = Util.removeItemFromStack(stack.stack, amount);
						p.stacks[dir.opposite().id + 1] = new PipeStack(moved, stack.direction, 0, stack.color);
						if (stack.stack.stackSize <= 0) {
							stacks[dir.id + 1] = null;
						}
						p.setChanged();
						worldObj.markBlockNeedsUpdate(p.tilePos.x, p.tilePos.y, p.tilePos.z);
						worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
					}
				}
			}
		}

		if (stacks[0] != null && stacks[0].timer >= maxPipeStackTimer) {
			List<Direction> freeDir = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				PipeStack stack2 = stacks[i + 1];
				Direction dir = Direction.fromId(i);
				int blockId = worldObj.getBlockId(tilePos.x + dir.offsetX(), tilePos.y + dir.offsetY(), tilePos.z + dir.offsetZ());
				TileEntity te = worldObj.getTileEntity(tilePos.x + dir.offsetX(), tilePos.y + dir.offsetY(), tilePos.z + dir.offsetZ());
				boolean catalystItemIo = FabricLoader.getInstance().isModLoaded("catalyst-core") && CatalystItemIoCompat.isItemIo(te);
				if (stack2 == null && blockId != 0 && (te instanceof Container || te instanceof TileEntityPipe || catalystItemIo) && i != stacks[0].direction.id && modeBySide[i] < 2) {
					if (te instanceof TileEntityPipe) {
						TileEntityPipe pipe = (TileEntityPipe) te;
						int pipeMode = pipe.modeBySide[dir.opposite().id];
						boolean cannotMove = (pipeMode == 3) || (pipeMode == 1);
						//cannotMove |= pipe.stacks[dir.opposite().id] != null;
						if (cannotMove) continue;
					} else if (catalystItemIo) {
						if (!CatalystItemIoCompat.canInsert(te, dir, stacks[0].stack)) {
							continue;
						}
					} else {
						boolean canInsert = modeBySide[i] == 1;
						canInsert &= !Util.canInsertOnInventory(
							worldObj,
							tilePos.x + dir.offsetX(),
							tilePos.y + dir.offsetY(),
							tilePos.z + dir.offsetZ(),
							dir,
							stacks[0].stack);

						if (canInsert) {
							continue;
						}
					}

					if (this.colorBySide[i] > 0) {
						if (this.colorBySide[i] == stacks[0].color) {
							freeDir.clear();
							freeDir.add(Direction.fromId(i));
							break;
						} else {
							continue;
						}
					}

					if (this.modeBySide[i] == 1) {
						freeDir.clear();
						freeDir.add(Direction.fromId(i));
						break;
					}
					freeDir.add(Direction.fromId(i));
				}
			}

			if (!freeDir.isEmpty()) {
				int selected = (int) (Math.random() * freeDir.size());
				Direction dir = freeDir.get(selected);
				stacks[0].direction = dir.opposite();
				stacks[0].timer = 0;
				stacks[dir.id + 1] = stacks[0];
				stacks[0] = null;

				worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
			}
		}

		boolean isPowered = worldObj.hasNeighborSignal(tilePos.x, tilePos.y, tilePos.z) || worldObj.hasDirectSignal(tilePos.x, tilePos.y, tilePos.z);
		if (!isPowered) {
			for (int i = 0; i < 6; i++) {
				PipeStack stack = stacks[i + 1];
				if (stack != null && stack.direction == Direction.fromId(i) && stack.timer >= maxPipeStackTimer) {
					if (stacks[0] == null) {
						stacks[0] = stack;
						stacks[0].timer = 0;
						stacks[i + 1] = null;
						worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
					}
				}
			}
		}
	}

	@Override
	public void tick() {
		if (worldObj == null) {
			return;
		}

		for (PipeStack pipeStack : stacks) if (pipeStack != null) pipeStack.timer++;

		if (worldObj.isClientSide) {
			return;
		}

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
		return new PacketTileEntityData(this);
	}
}
