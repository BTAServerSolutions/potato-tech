package goldenage.potatotech.blocks;

import goldenage.potatotech.PTItems;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.ArrayList;

public class BlockLogicEnergyConnector extends BlockLogic {
	public BlockLogicEnergyConnector(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
		world.setBlockMetadataWithNotify(x, y, z, side.id);
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityEnergyConnector) {
			//((TileEntityEnergyConnector) te).updateMachineConnections(side.getOpposite().getDirection());
		}
		super.onBlockPlacedOnSide(world, x, y, z, side, xPlaced, yPlaced);
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		if (!world.isClientSide) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileEntityEnergyConnector) {
				((TileEntityEnergyConnector) te).getBreakDrops(true);
			}
		}
		super.onBlockRemoved(world, x, y, z, data);
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		PotatoTech.LOGGER.info("Get break result");
		return new ItemStack[]{new ItemStack(PTItems.energyConnector), ((TileEntityEnergyConnector)tileEntity).getBreakDrops(true)};
	}

	@Override
	public AABBdc getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		Side side = Side.fromId(world.getBlockMetadata(x, y, z) & 7);
		float pixelSize = 1.0f / 16.0f;
		float min = pixelSize * 5;
		float max = 1.0f - pixelSize * 5;
		float len = pixelSize*9;

		AABBd aabb = this.getBoundsRaw();
		if (side == Side.TOP) {
			aabb.set(new AABBd(min, 0, min, max, len, max));
		} else if (side == Side.BOTTOM) {
			aabb.set(new AABBd(min, 1 - len, min, max, 1, max));
		} else if (side == Side.NORTH) {
			aabb.set(new AABBd(min, min, 1 - len, max, max, 1));
		} else if (side == Side.SOUTH) {
			aabb.set(new AABBd(min, min, 0, max, max, len));
		} else if (side == Side.EAST) {
			aabb.set(new AABBd(0, min, min, len, max, max));
		} else {
			aabb.set(new AABBd(1 - len, min, min, 1, max, max));
		}
        return aabb;
    }
}
