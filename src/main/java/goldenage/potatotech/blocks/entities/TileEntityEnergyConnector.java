package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PTItems;
import goldenage.potatotech.PotatoTech;
import net.minecraft.core.block.BlockLogicFurnace;
import net.minecraft.core.block.BlockLogicFurnaceBlast;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.block.entity.TileEntityFurnaceBlast;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.util.helper.Direction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntityEnergyConnector extends TileEntity {
	public static class Connection {
		public int x;
		public int y;
		public int z;

		public Connection(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public void writeToNBT(CompoundTag nbttagcompound) {
			nbttagcompound.putInt("x", x);
			nbttagcompound.putInt("y", y);
			nbttagcompound.putInt("z", z);
		}

		public void readFromNBT(CompoundTag nbttagcompound) {
			this.x = nbttagcompound.getInteger("x");
			this.y = nbttagcompound.getInteger("y");
			this.z = nbttagcompound.getInteger("z");
		}
		public static Connection readConnectionFromNBT(CompoundTag nbt) {
			if (nbt == null) {
				return null;
			}
			Connection con = new Connection(0, 0,0 );
			con.readFromNBT(nbt);
			return con;
		}
	}

	static public final int energyCapacity = 32;
	public int energy = 0;

	public TileEntityEnergyConnector() {

	}

	public ArrayList<Connection> connections = new ArrayList<>();
	@Override
	public void readFromNBT(CompoundTag nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		ListTag nbttaglist = nbttagcompound.getList("connections");
		this.connections = new ArrayList<>();
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			this.connections.add(Connection.readConnectionFromNBT(nbttagcompound1));
		}
		energy = nbttagcompound.getInteger("energy");
	}

	@Override
	public void writeToNBT(CompoundTag nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		ListTag nbttaglist = new ListTag();
		for (Connection connection : this.connections) {
			if (connection == null) continue;
			CompoundTag nbttagcompound1 = new CompoundTag();
			connection.writeToNBT(nbttagcompound1);
			nbttaglist.addTag(nbttagcompound1);
		}
		nbttagcompound.put("connections", nbttaglist);
		nbttagcompound.putInt("energy", energy);
	}

	public boolean addConnection(int xi, int yi, int zi)  {
		if (worldObj == null || worldObj.isClientSide) {
			return false;
		}
		TileEntity te = worldObj.getTileEntity(xi, yi, zi);

		if (!(te instanceof TileEntityEnergyConnector)) return false;

		boolean hasConnection = false;
		for (Connection c: connections) {
			if (c.x == xi && c.y == yi && c.z == zi) {
				hasConnection = true;
				break;
			}
		}
		if (hasConnection) return false;

		for (Connection c: ((TileEntityEnergyConnector) te).connections) {
			if (c.x == this.x && c.y == this.y && c.z == this.z) {
				hasConnection = true;
				break;
			}
		}

		if (hasConnection) return false;

		connections.add(new Connection(xi, yi, zi));
		((TileEntityEnergyConnector) te).connections.add(new Connection(this.x, this.y, this.z));
		this.setChanged();
		((TileEntityEnergyConnector) te).setChanged();
		worldObj.markBlockNeedsUpdate(this.x, this.y, this.z);
		worldObj.markBlockNeedsUpdate(xi, yi, zi);
		PotatoTech.LOGGER.info("Added connection on: " + xi + " " + yi + " " + zi);

		return true;
	}

	public void removeConnection(int xi, int yi, int zi) {
		if (worldObj == null || worldObj.isClientSide) {
			return;
		}
		int i = 0;
		for (Connection c: connections) {
			if (c.x == xi && c.y == yi && c.z == zi) {
				break;
			}
			i++;
		}
		if (i < connections.size()) {
			connections.remove(i);
			this.setChanged();
			worldObj.markBlockNeedsUpdate(this.x, this.y, this.z);
		}
	}

	public ItemStack getBreakDrops(boolean removeConnection) {
		ItemStack result = new ItemStack(PTItems.wireSpool, 0);

		ArrayList<Connection> connectionsCopy = (ArrayList<Connection>) connections.clone();
		for (Connection c: connectionsCopy) {
            TileEntity te = worldObj.getTileEntity(c.x, c.y, c.z);
			if (te instanceof TileEntityEnergyConnector && removeConnection) {
				((TileEntityEnergyConnector) te).removeConnection(x, y, z);
			}
			result.stackSize++;
		}

		PotatoTech.LOGGER.info("break results is: " + result);

		if (result.stackSize < 1){
			return null;
		}

		return result;
	}


	private static boolean furnaceCanSmelt(TileEntityFurnace furnace, boolean isBlast) {
		if (furnace.getItem(0) == null) {
			return false;
		}
		List<RecipeEntryFurnace> listF = Registries.RECIPES.getAllFurnaceRecipes();
		List<RecipeEntryBlastFurnace> listB = Registries.RECIPES.getAllBlastFurnaceRecipes();

		ItemStack itemstack = null;
		if (isBlast) {
			Iterator iter = listB.iterator();
			while(iter.hasNext()) {
				RecipeEntryBlastFurnace recipeEntryBase = (RecipeEntryBlastFurnace)iter.next();
				if (recipeEntryBase != null && recipeEntryBase.matches(furnace.getItem(0))) {
					itemstack = recipeEntryBase.getOutput();
				}
			}
		} else {
			Iterator iter = listF.iterator();
			while(iter.hasNext()) {
				RecipeEntryFurnace recipeEntryBase = (RecipeEntryFurnace)iter.next();
				if (recipeEntryBase != null && recipeEntryBase.matches(furnace.getItem(0))) {
					itemstack = recipeEntryBase.getOutput();
				}
			}
		}

		if (itemstack == null) {
			return false;
		}
		if (furnace.getItem(2) == null) {
			return true;
		}
		if (!furnace.getItem(2).isItemEqual(itemstack)) {
			return false;
		}
		if (furnace.getItem(2).stackSize < furnace.getMaxStackSize() && furnace.getItem(2).stackSize < furnace.getItem(2).getMaxStackSize()) {
			return true;
		}
		return furnace.getItem(2).stackSize < itemstack.getMaxStackSize();
	}

	@Override
	public void tick() {
		if (worldObj == null || worldObj.isClientSide) {
			return;
		}

		int previousEnergy = energy;
		int side = worldObj.getBlockMetadata(x, y, z) & 7;
		Direction connectionDir = Direction.getDirectionById(side).getOpposite();
		TileEntity te = worldObj.getTileEntity(x + connectionDir.getOffsetX(), y + connectionDir.getOffsetY(), z + connectionDir.getOffsetZ());
		if (te instanceof TileEntityStirlingEngine) {
			TileEntityStirlingEngine engine = (TileEntityStirlingEngine) te;
			energy = Math.min(energy + engine.power, energyCapacity);
		} else if (te instanceof TileEntityFurnace) {
			TileEntityFurnace furnace = (TileEntityFurnace) te;
			boolean isBlast = te instanceof TileEntityFurnaceBlast;
			ItemStack fuel = furnace.getItem(1);
			if (furnaceCanSmelt(furnace, isBlast) && energy > 1 && fuel != null && fuel.itemID == PTItems.electricHeatingUnit.id) {
				if (furnace.currentBurnTime == 0) {
					if (isBlast) {
						BlockLogicFurnaceBlast.updateFurnaceBlockState(true, worldObj, x + connectionDir.getOffsetX(), y + connectionDir.getOffsetY(), z + connectionDir.getOffsetZ());
					} else {
						BlockLogicFurnace.updateFurnaceBlockState(true, worldObj, x + connectionDir.getOffsetX(), y + connectionDir.getOffsetY(), z + connectionDir.getOffsetZ());
					}
				}
				energy -= isBlast ? 3 : 2;
				furnace.currentBurnTime = 10;
				furnace.maxBurnTime = 10;
			}
		} else if (te instanceof TileEntityTrommel) {
			TileEntityTrommel trommel = (TileEntityTrommel) te;
			ItemStack fuel = trommel.getItem(4);
			if (energy > 0 && fuel != null && fuel.itemID == PTItems.electricHeatingUnit.id) {
				trommel.burnTime = 10;
				if (trommel.currentItemBurnTime > 0) {
					energy -= 2;
				}
			}
		} else if (te instanceof TileEntityCrafter) {
			TileEntityCrafter crafter = (TileEntityCrafter) te;
			if (energy > 0) {
				int sent = crafter.addEnergy(1);
				if (sent > 0) {
					energy -= sent;
					worldObj.markBlockNeedsUpdate(crafter.x, crafter.y, crafter.z);
				}
			}
		}

        for (Connection conn : connections) {
			TileEntity te2 = worldObj.getTileEntity(conn.x, conn.y, conn.z);
			if (te2 instanceof TileEntityEnergyConnector) {
				TileEntityEnergyConnector teConn = (TileEntityEnergyConnector) te2;
				if (teConn.energy < energy && teConn.energy < energyCapacity) {
					int amountToTransfer = Math.min(1 + (energy - teConn.energy) / 2, energy);
					energy -= amountToTransfer;
					teConn.energy += amountToTransfer;
					teConn.setChanged();
					worldObj.markBlockNeedsUpdate(teConn.x, teConn.y, teConn.z);
				}
			}

        }

		if (energy != previousEnergy) {
			this.setChanged();
			worldObj.markBlockNeedsUpdate(this.x, this.y, this.z);
		}
	}
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}
