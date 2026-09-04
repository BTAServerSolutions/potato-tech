package goldenage.potatotech.blocks.entities;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import goldenage.potatotech.EnergyWireType;
import goldenage.potatotech.PTItems;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.BlockLogicEnergyConnector;
import goldenage.potatotech.compat.catalyst.CatalystCompat;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.core.world.pos.TilePos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TileEntityEnergyConnector extends TileEntity {
	public static class Connection {
		public int x;
		public int y;
		public int z;
		public EnergyWireType wireType;

		public Connection(int x, int y, int z) {
			this(x, y, z, EnergyWireType.LV);
		}

		public Connection(int x, int y, int z, EnergyWireType wireType) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.wireType = wireType;
		}
		public void writeToNBT(CompoundTag nbttagcompound) {
			nbttagcompound.putInt("x", x);
			nbttagcompound.putInt("y", y);
			nbttagcompound.putInt("z", z);
			nbttagcompound.putString("wireType", wireType.getId());
		}

		public void readFromNBT(CompoundTag nbttagcompound) {
			this.x = nbttagcompound.getInteger("x");
			this.y = nbttagcompound.getInteger("y");
			this.z = nbttagcompound.getInteger("z");
			this.wireType = EnergyWireType.fromId(nbttagcompound.getString("wireType"));
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

	public static final int energyCapacity = 128;
	public static final int MV_ENERGY_CAPACITY = 512;
	public static final int LV_BLOCK_TRANSFER_RATE = 16;
	public static final int MV_BLOCK_TRANSFER_RATE = 32;
	public int energy = 0;
	private long lastNetworkTick = Long.MIN_VALUE;
	private long blockBudgetTick = Long.MIN_VALUE;
	private int blockTransferred;
	private int blockDirection;

	public TileEntityEnergyConnector() {

	}

	public int getEnergyCapacity() {
		BlockLogicEnergyConnector blockLogic = getConnectorBlockLogic();
		return blockLogic == null ? energyCapacity : blockLogic.getEnergyCapacity();
	}

	public int getBlockTransferRate() {
		BlockLogicEnergyConnector blockLogic = getConnectorBlockLogic();
		return blockLogic == null ? LV_BLOCK_TRANSFER_RATE : blockLogic.getBlockTransferRate();
	}

	private BlockLogicEnergyConnector getConnectorBlockLogic() {
		return worldObj == null ? null : worldObj.getBlockLogic(tilePos.x, tilePos.y, tilePos.z, BlockLogicEnergyConnector.class);
	}

	public ArrayList<Connection> connections = new ArrayList<>();
	@Override
	public void readAdditionalData(CompoundTag nbttagcompound) {
		ListTag nbttaglist = nbttagcompound.getList("connections");
		this.connections = new ArrayList<>();
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			CompoundTag nbttagcompound1 = (CompoundTag)nbttaglist.tagAt(i);
			this.connections.add(Connection.readConnectionFromNBT(nbttagcompound1));
		}
		energy = Math.max(0, nbttagcompound.getInteger("energy"));
	}

	@Override
	public void writeAdditionalData(CompoundTag nbttagcompound) {
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

	public boolean addConnection(int xi, int yi, int zi) {
		return addConnection(xi, yi, zi, EnergyWireType.LV);
	}

	public boolean addConnection(int xi, int yi, int zi, EnergyWireType wireType)  {
		if (worldObj == null || worldObj.isClientSide) {
			return false;
		}
		TilePos connectPos = new TilePos(xi, yi, zi);

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
			if (c.x == tilePos.x && c.y == tilePos.y && c.z == tilePos.z) {
				hasConnection = true;
				break;
			}
		}

		if (hasConnection) return false;

		connections.add(new Connection(xi, yi, zi, wireType));
		((TileEntityEnergyConnector) te).connections.add(new Connection(tilePos.x, tilePos.y, tilePos.z, wireType));
		this.setChanged();
		((TileEntityEnergyConnector) te).setChanged();
		worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
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
			worldObj.markBlockNeedsUpdate(tilePos.x, tilePos.y, tilePos.z);
		}
	}

	public ItemStack[] getBreakDrops(boolean removeConnection) {
		Map<EnergyWireType, Integer> wireCounts = new HashMap<>();

		ArrayList<Connection> connectionsCopy = new ArrayList<>(connections);
		for (Connection c: connectionsCopy) {
			wireCounts.merge(c.wireType, 1, Integer::sum);
			TileEntity te = worldObj.getTileEntity(c.x, c.y, c.z);
			if (te instanceof TileEntityEnergyConnector && removeConnection) {
				((TileEntityEnergyConnector) te).removeConnection(tilePos.x, tilePos.y, tilePos.z);
			}
		}

		List<ItemStack> drops = new ArrayList<>();
		for (EnergyWireType wireType : EnergyWireType.values()) {
			int remaining = wireCounts.getOrDefault(wireType, 0);
			while (remaining > 0) {
				ItemStack stack = new ItemStack(wireType.getSpoolItem(), 1);
				stack.stackSize = Math.min(remaining, stack.getMaxStackSize());
				remaining -= stack.stackSize;
				drops.add(stack);
			}
		}

		return drops.toArray(new ItemStack[0]);
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
				if (recipeEntryBase != null && recipeEntryBase.matches(furnace.getItem(0), furnace.getItem(1))) {
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
		processNetwork();
	}

	private void processNetwork() {
		long worldTick = worldObj.getWorldTime();
		if (lastNetworkTick == worldTick) {
			return;
		}

		List<TileEntityEnergyConnector> network = discoverNetwork();
		network.sort(TileEntityEnergyConnector::comparePosition);
		Map<TileEntityEnergyConnector, Integer> previousEnergy = new HashMap<>();
		for (TileEntityEnergyConnector connector : network) {
			connector.lastNetworkTick = worldTick;
			connector.energy = Math.min(connector.energy, connector.getEnergyCapacity());
			previousEnergy.put(connector, connector.energy);
			connector.receiveLocalGeneration();
		}

		balanceWires(network);
		for (TileEntityEnergyConnector connector : network) {
			connector.supplyLocalConsumer();
			if (connector.energy != previousEnergy.get(connector)) {
				connector.setChanged();
				if (worldTick % 20 == 0) {
					worldObj.markBlockNeedsUpdate(connector.tilePos.x, connector.tilePos.y, connector.tilePos.z);
				}
			}
		}
	}

	private List<TileEntityEnergyConnector> discoverNetwork() {
		List<TileEntityEnergyConnector> result = new ArrayList<>();
		ArrayDeque<TileEntityEnergyConnector> pending = new ArrayDeque<>();
		Set<ConnectorPos> visited = new HashSet<>();
		pending.add(this);
		visited.add(position());

		while (!pending.isEmpty()) {
			TileEntityEnergyConnector connector = pending.removeFirst();
			result.add(connector);
			for (Connection connection : connector.connections) {
				TileEntity target = worldObj.getTileEntity(connection.x, connection.y, connection.z);
				if (target instanceof TileEntityEnergyConnector targetConnector && visited.add(targetConnector.position())) {
					pending.addLast(targetConnector);
				}
			}
		}
		return result;
	}

	private record ConnectorPos(int x, int y, int z) implements Comparable<ConnectorPos> {
		@Override
		public int compareTo(ConnectorPos other) {
			int xResult = Integer.compare(x, other.x);
			if (xResult != 0) return xResult;
			int yResult = Integer.compare(y, other.y);
			if (yResult != 0) return yResult;
			return Integer.compare(z, other.z);
		}
	}

	private record TargetShare(int connectorIndex, long remainder) {
	}

	private static final class FlowEdge {
		private final int target;
		private final int reverseIndex;
		private long capacity;

		private FlowEdge(int target, int reverseIndex, long capacity) {
			this.target = target;
			this.reverseIndex = reverseIndex;
			this.capacity = capacity;
		}
	}

	private record NetworkWire(int leftIndex, int rightIndex, int transferRate, FlowEdge flowEdge) {
	}

	private static final class FlowNetwork {
		private final List<FlowEdge>[] edges;
		private final int[] levels;
		private final int[] nextEdges;

		@SuppressWarnings("unchecked")
		private FlowNetwork(int nodeCount) {
			edges = new List[nodeCount];
			for (int i = 0; i < nodeCount; i++) {
				edges[i] = new ArrayList<>();
			}
			levels = new int[nodeCount];
			nextEdges = new int[nodeCount];
		}

		private void addDirectedEdge(int source, int target, long capacity) {
			FlowEdge forward = new FlowEdge(target, edges[target].size(), capacity);
			FlowEdge reverse = new FlowEdge(source, edges[source].size(), 0);
			edges[source].add(forward);
			edges[target].add(reverse);
		}

		private FlowEdge addUndirectedEdge(int left, int right, long capacity) {
			FlowEdge forward = new FlowEdge(right, edges[right].size(), capacity);
			FlowEdge reverse = new FlowEdge(left, edges[left].size(), capacity);
			edges[left].add(forward);
			edges[right].add(reverse);
			return forward;
		}

		private long getMaximumFlow(int source, int sink) {
			long total = 0;
			while (buildLevels(source, sink)) {
				Arrays.fill(nextEdges, 0);
				long pushed;
				while ((pushed = pushFlow(source, sink, Long.MAX_VALUE)) > 0) {
					total += pushed;
				}
			}
			return total;
		}

		private boolean buildLevels(int source, int sink) {
			Arrays.fill(levels, -1);
			ArrayDeque<Integer> pending = new ArrayDeque<>();
			levels[source] = 0;
			pending.add(source);
			while (!pending.isEmpty()) {
				int node = pending.removeFirst();
				for (FlowEdge edge : edges[node]) {
					if (edge.capacity > 0 && levels[edge.target] < 0) {
						levels[edge.target] = levels[node] + 1;
						pending.addLast(edge.target);
					}
				}
			}
			return levels[sink] >= 0;
		}

		private long pushFlow(int node, int sink, long available) {
			if (node == sink) {
				return available;
			}
			for (; nextEdges[node] < edges[node].size(); nextEdges[node]++) {
				FlowEdge edge = edges[node].get(nextEdges[node]);
				if (edge.capacity <= 0 || levels[edge.target] != levels[node] + 1) {
					continue;
				}
				long pushed = pushFlow(edge.target, sink, Math.min(available, edge.capacity));
				if (pushed > 0) {
					edge.capacity -= pushed;
					edges[edge.target].get(edge.reverseIndex).capacity += pushed;
					return pushed;
				}
			}
			return 0;
		}
	}

	private record WireEdgeKey(ConnectorPos first, ConnectorPos second) {
		private static WireEdgeKey of(TileEntityEnergyConnector left, TileEntityEnergyConnector right) {
			ConnectorPos leftPos = left.position();
			ConnectorPos rightPos = right.position();
			return leftPos.compareTo(rightPos) <= 0
				? new WireEdgeKey(leftPos, rightPos)
				: new WireEdgeKey(rightPos, leftPos);
		}
	}

	private void balanceWires(List<TileEntityEnergyConnector> network) {
		Set<TileEntityEnergyConnector> members = new HashSet<>();
		Map<TileEntityEnergyConnector, Integer> connectorIndices = new HashMap<>();
		long totalEnergy = 0;
		long totalCapacity = 0;
		for (int i = 0; i < network.size(); i++) {
			TileEntityEnergyConnector connector = network.get(i);
			members.add(connector);
			connectorIndices.put(connector, i);
			totalEnergy += connector.energy;
			totalCapacity += connector.getEnergyCapacity();
		}
		if (network.size() < 2 || totalEnergy <= 0 || totalCapacity <= 0) {
			return;
		}

		int[] targetEnergy = new int[network.size()];
		List<TargetShare> targetRemainders = new ArrayList<>();
		long assignedEnergy = 0;
		for (int i = 0; i < network.size(); i++) {
			long scaledEnergy = totalEnergy * network.get(i).getEnergyCapacity();
			targetEnergy[i] = (int) (scaledEnergy / totalCapacity);
			assignedEnergy += targetEnergy[i];
			targetRemainders.add(new TargetShare(i, scaledEnergy % totalCapacity));
		}
		int rotation = Math.floorMod((int) worldObj.getWorldTime(), network.size());
		targetRemainders.sort(Comparator
			.comparingLong(TargetShare::remainder).reversed()
			.thenComparingInt(share -> Math.floorMod(share.connectorIndex() - rotation, network.size())));
		for (int i = 0; i < totalEnergy - assignedEnergy; i++) {
			targetEnergy[targetRemainders.get(i).connectorIndex()]++;
		}

		int source = network.size();
		int sink = source + 1;
		FlowNetwork flowNetwork = new FlowNetwork(network.size() + 2);
		for (int offset = 0; offset < network.size(); offset++) {
			int index = (rotation + offset) % network.size();
			int difference = network.get(index).energy - targetEnergy[index];
			if (difference > 0) {
				flowNetwork.addDirectedEdge(source, index, difference);
			} else if (difference < 0) {
				flowNetwork.addDirectedEdge(index, sink, -difference);
			}
		}

		List<NetworkWire> wires = new ArrayList<>();
		Set<WireEdgeKey> processedEdges = new HashSet<>();
		for (TileEntityEnergyConnector connector : network) {
			for (Connection connection : connector.connections) {
				TileEntity target = worldObj.getTileEntity(connection.x, connection.y, connection.z);
				if (!(target instanceof TileEntityEnergyConnector other)
					|| !members.contains(other)
					|| !processedEdges.add(WireEdgeKey.of(connector, other))) {
					continue;
				}

				int wireRate = connection.wireType.getTransferRate();
				Connection reverse = other.findConnection(connector.tilePos.x, connector.tilePos.y, connector.tilePos.z);
				if (reverse != null) {
					wireRate = Math.min(wireRate, reverse.wireType.getTransferRate());
				}
				int leftIndex = connectorIndices.get(connector);
				int rightIndex = connectorIndices.get(other);
				FlowEdge flowEdge = flowNetwork.addUndirectedEdge(leftIndex, rightIndex, wireRate);
				wires.add(new NetworkWire(leftIndex, rightIndex, wireRate, flowEdge));
			}
		}

		flowNetwork.getMaximumFlow(source, sink);
		for (NetworkWire wire : wires) {
			long transferred = wire.transferRate() - wire.flowEdge().capacity;
			if (transferred != 0) {
				network.get(wire.leftIndex()).energy -= (int) transferred;
				network.get(wire.rightIndex()).energy += (int) transferred;
			}
		}
	}

	private Connection findConnection(int x, int y, int z) {
		for (Connection connection : connections) {
			if (connection.x == x && connection.y == y && connection.z == z) {
				return connection;
			}
		}
		return null;
	}

	private ConnectorPos position() {
		return new ConnectorPos(tilePos.x, tilePos.y, tilePos.z);
	}

	private static int comparePosition(TileEntityEnergyConnector left, TileEntityEnergyConnector right) {
		int xResult = Integer.compare(left.tilePos.x, right.tilePos.x);
		if (xResult != 0) return xResult;
		int yResult = Integer.compare(left.tilePos.y, right.tilePos.y);
		if (yResult != 0) return yResult;
		return Integer.compare(left.tilePos.z, right.tilePos.z);
	}

	private void resetBlockBudget() {
		long worldTick = worldObj == null ? Long.MIN_VALUE : worldObj.getWorldTime();
		if (blockBudgetTick != worldTick) {
			blockBudgetTick = worldTick;
			blockTransferred = 0;
			blockDirection = 0;
		}
	}

	public int getRemainingBlockTransfer() {
		resetBlockBudget();
		return Math.max(0, getBlockTransferRate() - blockTransferred);
	}

	public boolean canTransferWithBlock(boolean receiving) {
		resetBlockBudget();
		int direction = receiving ? 1 : -1;
		return getRemainingBlockTransfer() > 0 && (blockDirection == 0 || blockDirection == direction);
	}

	public void recordExternalBlockTransfer(boolean receiving, int amount) {
		if (amount <= 0) {
			return;
		}
		resetBlockBudget();
		blockDirection = receiving ? 1 : -1;
		blockTransferred = Math.min(getBlockTransferRate(), blockTransferred + amount);
	}

	private int receiveEnergyFromBlock(int amount) {
		if (amount <= 0 || !canTransferWithBlock(true)) {
			return 0;
		}
		int accepted = Math.min(amount, Math.min(getRemainingBlockTransfer(), getEnergyCapacity() - energy));
		if (accepted > 0) {
			energy += accepted;
			recordExternalBlockTransfer(true, accepted);
		}
		return accepted;
	}

	private boolean consumeEnergyForBlock(int amount) {
		if (amount <= 0 || energy < amount || !canTransferWithBlock(false) || getRemainingBlockTransfer() < amount) {
			return false;
		}
		energy -= amount;
		recordExternalBlockTransfer(false, amount);
		return true;
	}

	private int offerEnergyToBlock(int amount) {
		if (amount <= 0 || !canTransferWithBlock(false)) {
			return 0;
		}
		return Math.min(amount, Math.min(energy, getRemainingBlockTransfer()));
	}

	private void commitEnergyToBlock(int amount) {
		if (amount > 0) {
			energy -= amount;
			recordExternalBlockTransfer(false, amount);
		}
	}

	private TileEntity getAttachedTile(Direction connectionDir) {
		return worldObj.getTileEntity(tilePos.x + connectionDir.offsetX(), tilePos.y + connectionDir.offsetY(), tilePos.z + connectionDir.offsetZ());
	}

	private Direction getAttachedDirection() {
		int side = worldObj.getBlockMetadata(tilePos.x, tilePos.y, tilePos.z) & 7;
		return Direction.fromId(side).opposite();
	}

	private void receiveLocalGeneration() {
		Direction connectionDir = getAttachedDirection();
		TileEntity te = getAttachedTile(connectionDir);
		if (te instanceof TileEntityStirlingEngine engine) {
			receiveEnergyFromBlock(engine.power);
		}
	}

	private void supplyLocalConsumer() {
		Direction connectionDir = getAttachedDirection();
		TileEntity te = getAttachedTile(connectionDir);
		if (te instanceof TileEntityStirlingEngine) {
			return;
		} else if (te instanceof TileEntityFurnace) {
			TileEntityFurnace furnace = (TileEntityFurnace) te;
			boolean isBlast = te instanceof TileEntityFurnaceBlast;
			ItemStack fuel = furnace.getItem(1);
			int powerPerCoil = isBlast ? 3 : 2;
			int coilCount = fuel == null ? 0 : Math.min(4, fuel.stackSize);
			boolean isElectricHeating = coilCount > 0 && fuel.itemID == PTItems.electricHeatingUnit.id;
			if (furnaceCanSmelt(furnace, isBlast) && isElectricHeating && consumeEnergyForBlock(powerPerCoil * coilCount)) {
				if (furnace.currentBurnTime == 0) {
					if (isBlast) {
						BlockLogicFurnaceBlast.updateFurnaceBlockState(worldObj, new TilePos(tilePos).add(connectionDir), true);
					} else {
						BlockLogicFurnace.updateFurnaceBlockState(worldObj, new TilePos(tilePos).add(connectionDir), true);
					}
				}
				// The machine decrements burn time before checking whether to process.
				furnace.currentBurnTime = 2;
				furnace.maxBurnTime = 2;
				furnace.currentCookTime += coilCount - 1;
				if (furnace.currentCookTime >= furnace.maxCookTime) {
					furnace.currentCookTime = 0;
					furnace.smeltItem();
				}
			} else if (isElectricHeating) {
				furnace.currentBurnTime = 0;
				furnace.maxBurnTime = 0;
			}
		} else if (te instanceof TileEntityTrommel) {
			TileEntityTrommel trommel = (TileEntityTrommel) te;
			ItemStack fuel = trommel.getItem(4);
			int coilCount = fuel == null ? 0 : Math.min(4, fuel.stackSize);
			boolean hasIngredient = false;
			for (int slot = 0; slot < 4; slot++) {
				if (trommel.getItem(slot) != null) {
					hasIngredient = true;
					break;
				}
			}
			boolean isElectricHeating = coilCount > 0 && fuel.itemID == PTItems.electricHeatingUnit.id;
			if (hasIngredient && isElectricHeating && consumeEnergyForBlock(2 * coilCount)) {
				trommel.burnTime = 2;
				trommel.itemPopTime += coilCount - 1;
			} else if (isElectricHeating) {
				trommel.burnTime = 0;
			}
		} else if (te instanceof TileEntityCrafter) {
			TileEntityCrafter crafter = (TileEntityCrafter) te;
			int offered = offerEnergyToBlock(getRemainingBlockTransfer());
			if (offered > 0) {
				int sent = crafter.addEnergy(offered);
				if (sent > 0) {
					commitEnergyToBlock(sent);
					worldObj.markBlockNeedsUpdate(crafter.tilePos.x, crafter.tilePos.y, crafter.tilePos.z);
				}
			}
		} else if (te instanceof TileEntityBedrockExtractor) {
			TileEntityBedrockExtractor extractor = (TileEntityBedrockExtractor) te;
			int offered = offerEnergyToBlock(getRemainingBlockTransfer());
			if (offered > 0) {
				int sent = extractor.addEnergy(offered);
				if (sent > 0) {
					commitEnergyToBlock(sent);
					worldObj.markBlockNeedsUpdate(extractor.tilePos.x, extractor.tilePos.y, extractor.tilePos.z);
				}
			}
		}
		if (FabricLoader.getInstance().isModLoaded("catalyst-energy")) {
			CatalystCompat.transferEnergy(this, te, connectionDir);
		}
	}
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}
