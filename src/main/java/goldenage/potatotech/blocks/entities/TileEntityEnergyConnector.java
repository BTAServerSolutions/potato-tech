package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PTItems;
import goldenage.potatotech.PotatoTech;
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
		PotatoTech.LOGGER.info("Added connection on: " + xi + " " + yi + " " + zi);

		return true;
	}

	public void removeConnection(int xi, int yi, int zi) {
		int i = 0;
		for (Connection c: connections) {
			if (c.x == xi && c.y == yi && c.z == zi) {
				break;
			}
			i++;
		}
		if (i < connections.size()) connections.remove(i);
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
				RecipeEntryFurnace recipeEntryBase = (RecipeEntryFurnace)iter.next();
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
		int side = worldObj.getBlockMetadata(x, y, z);
		Direction connectionDir = Direction.getDirectionById(side).getOpposite();
		TileEntity te = worldObj.getTileEntity(x + connectionDir.getOffsetX(), y + connectionDir.getOffsetY(), z + connectionDir.getOffsetZ());
		if (te instanceof TileEntityStirlingEngine) {
			TileEntityStirlingEngine engine = (TileEntityStirlingEngine) te;
			energy = Math.min(energy + engine.power, energyCapacity);
		}

        for (Connection conn : connections) {
			TileEntity te2 = worldObj.getTileEntity(conn.x, conn.y, conn.z);
			if (te2 instanceof TileEntityEnergyConnector) {
				TileEntityEnergyConnector teConn = (TileEntityEnergyConnector) te2;
				if (teConn.energy < energy && teConn.energy < energyCapacity) {
					int amountToTransfer = Math.min(1 + (energy - teConn.energy) / 2, energy);
					energy -= amountToTransfer;
					teConn.energy += amountToTransfer;
				}
			}

			if (te2 instanceof TileEntityFurnace && energy > 0) {
				TileEntityFurnace furnace = (TileEntityFurnace) te2;
				//if (furnaceCanSmelt(furnace, te2 instanceof TileEntityFurnaceBlast)) {
					energy -= 1;
					furnace.currentBurnTime = 50;
				//}
			}
			if (te2 instanceof TileEntityTrommel && energy > 0) {
				TileEntityTrommel trommel = (TileEntityTrommel) te2;
				trommel.burnTime = 50;
				if (trommel.currentItemBurnTime > 0) {
					energy -= 1;
				}
			}
        }
	}
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}
