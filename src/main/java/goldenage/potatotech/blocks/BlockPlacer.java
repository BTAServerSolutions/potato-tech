package goldenage.potatotech.blocks;


import goldenage.potatotech.PipeStack;
import goldenage.potatotech.Util;
import goldenage.potatotech.blocks.entities.TileEntityCrusher;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockDispenser;
import net.minecraft.core.block.BlockRotatable;
import net.minecraft.core.block.BlockTileEntityRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispenser;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemPlaceable;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.EntityPlayerMP;


public class BlockPlacer extends BlockTileEntityRotatable {
	public BlockPlacer(String key, int id, Material material) {
		super(key, id, material);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		this.checkIfAction(world, x, y, z);
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction dir = entity.getPlacementDirection(side);
		if (dir == Direction.UP || dir == Direction.DOWN) dir = dir.getOpposite();
		if (!entity.isSneaking()) dir = dir.getOpposite();
		world.setBlockMetadataWithNotify(x, y, z, dir.getId());
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		this.setDefaultDirection(world, i, j, k);
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new TileEntityCrusher();
	}

	public static boolean isPowered(int data) {
		return (data & 8) != 0;
	}

	private void checkIfAction(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		Direction dir = Direction.getDirectionById(BlockRotatable.getOrientation(meta & 7));
		boolean hasNeighborSignal = this.getNeighborSignal(world, x, y, z, dir.getId());

		if (hasNeighborSignal) {
			if (!isPowered(meta)) world.triggerEvent(x, y, z, 0, dir.getId());
			meta = dir.getId();
			meta |= 8;
		} else {
			meta = dir.getId();
		}
		world.setBlockMetadata(x, y, z, meta);
	}

	private boolean getNeighborSignal(World world, int x, int y, int z, int direction) {
		if (direction != 0 && world.isBlockIndirectlyProvidingPowerTo(x, y - 1, z, 0)) {
			return true;
		}
		if (direction != 1 && world.isBlockIndirectlyProvidingPowerTo(x, y + 1, z, 1)) {
			return true;
		}
		if (direction != 2 && world.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2)) {
			return true;
		}
		if (direction != 3 && world.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3)) {
			return true;
		}
		if (direction != 5 && world.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5)) {
			return true;
		}
		if (direction != 4 && world.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4)) {
			return true;
		}
		if (world.isBlockIndirectlyProvidingPowerTo(x, y, z, 0)) {
			return true;
		}
		if (world.isBlockIndirectlyProvidingPowerTo(x, y + 2, z, 1)) {
			return true;
		}
		if (world.isBlockIndirectlyProvidingPowerTo(x, y + 1, z - 1, 2)) {
			return true;
		}
		if (world.isBlockIndirectlyProvidingPowerTo(x, y + 1, z + 1, 3)) {
			return true;
		}
		if (world.isBlockIndirectlyProvidingPowerTo(x - 1, y + 1, z, 4)) {
			return true;
		}
		return world.isBlockIndirectlyProvidingPowerTo(x + 1, y + 1, z, 5);
	}

	@Override
	public void triggerEvent(World world, int x, int y, int z, int index, int meta) {
		meta = world.getBlockMetadata(x, y, z);
		Direction dir = Direction.getDirectionById(BlockRotatable.getOrientation(meta)).getOpposite();
		if (dir != Direction.UP && dir != Direction.DOWN) dir = dir.getOpposite();

		int tx = x + dir.getOffsetX();
		int ty = y + dir.getOffsetY();
		int tz = z + dir.getOffsetZ();

		TileEntityCrusher te = (TileEntityCrusher) world.getBlockTileEntity(x, y, z);

		ItemStack blockToPlace = te.getRandomStackFromInventory();

		if (blockToPlace == null) return;

		world.playSoundEffect(2000, tx, ty, tz, blockToPlace.itemID);
		boolean placed = world.setBlockWithNotify(tx, ty, tz, blockToPlace.itemID);
	}

	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xPlaced, double yPlaced) {
        if (!world.isClientSide) {
            //TileEntityDispenser tileentitydispenser = (TileEntityDispenser) world.getBlockTileEntity(x, y, z);
            //player.displayGUIDispenser(tileentitydispenser);
        }
        return true;
    }
}
