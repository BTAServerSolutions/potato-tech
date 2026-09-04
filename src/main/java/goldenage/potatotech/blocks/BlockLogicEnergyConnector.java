package goldenage.potatotech.blocks;

import goldenage.potatotech.PTItems;
import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.ArrayList;

public class BlockLogicEnergyConnector extends BlockLogic {
	private final int energyCapacity;
	private final int blockTransferRate;

	public BlockLogicEnergyConnector(Block<?> block, Material material) {
		this(block, material, TileEntityEnergyConnector.energyCapacity, TileEntityEnergyConnector.LV_BLOCK_TRANSFER_RATE);
	}

	public BlockLogicEnergyConnector(Block<?> block, Material material, int energyCapacity, int blockTransferRate) {
		super(block, material);
		this.energyCapacity = energyCapacity;
		this.blockTransferRate = blockTransferRate;
	}

	public int getEnergyCapacity() {
		return energyCapacity;
	}

	public int getBlockTransferRate() {
		return blockTransferRate;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean isCubeShaped() {
		return false;
	}

	@Override
	public boolean blocksLight() {
		return false;
	}

	@Override
	public void onPlacedOnSide(World world, TilePosc tilePos, @NotNull Side side, double xPlaced, double yPlaced) {
		world.setBlockDataNotify(tilePos, side.direction.id);
		TileEntity te = world.getTileEntity(tilePos);
		if (te instanceof TileEntityEnergyConnector) {
			//((TileEntityEnergyConnector) te).updateMachineConnections(side.getOpposite().getDirection());
		}
		super.onPlacedOnSide(world, tilePos, side, xPlaced, yPlaced);
	}

	@Override
	public void onRemoved(World world, TilePosc tilePos, int data) {
		if (!world.isClientSide) {
			TileEntity te = world.getTileEntity(tilePos);
			if (te instanceof TileEntityEnergyConnector) {
				((TileEntityEnergyConnector) te).getBreakDrops(true);
			}
		}
		super.onRemoved(world, tilePos, data);
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, TilePosc tilePos, int meta, TileEntity tileEntity) {
		// Tooltip mods may query drops without loading the block's tile entity.
		ItemStack connectorDrop = new ItemStack(world.getBlockId(tilePos.x(), tilePos.y(), tilePos.z()) == PTBlocks.energyConnectorMV.id()
			? PTItems.energyConnectorMV
			: PTItems.energyConnector);
		ItemStack[] wireDrops = tileEntity instanceof TileEntityEnergyConnector connector
			? connector.getBreakDrops(false)
			: new ItemStack[0];
		ItemStack[] result = new ItemStack[wireDrops.length + 1];
		result[0] = connectorDrop;
		System.arraycopy(wireDrops, 0, result, 1, wireDrops.length);
		return result;
	}

	@Override
	public AABBdc getBoundsFromState(WorldSource world, TilePosc tilePos) {
		Side side = Side.fromId(world.getBlockData(tilePos) & 7);
		float pixelSize = 1.0f / 16.0f;
		float min = pixelSize * 5;
		float max = 1.0f - pixelSize * 5;
		float len = pixelSize*9;

		if (side == Side.TOP) {
			return new AABBd(min, 0, min, max, len, max);
		} else if (side == Side.BOTTOM) {
			return new AABBd(min, 1 - len, min, max, 1, max);
		} else if (side == Side.NORTH) {
			return new AABBd(min, min, 1 - len, max, max, 1);
		} else if (side == Side.SOUTH) {
			return new AABBd(min, min, 0, max, max, len);
		} else if (side == Side.EAST) {
			return new AABBd(0, min, min, len, max, max);
		} else {
			return new AABBd(1 - len, min, min, 1, max, max);
		}
    }
}
