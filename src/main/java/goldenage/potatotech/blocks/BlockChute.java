package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntityChute;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockChute extends BlockLogic {
	public BlockChute(Block<?> block, Material material) {
		super(block, material);
	}


	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		TileEntityChute te = (TileEntityChute)world.getTileEntity(x, y, z);
		if (te.getNumUnitsInside() > 0) {
			te.givePlayerAllItems(world, player);
			return true;
		}
		return false;
	}

	@Override
	public boolean renderAsNormalBlockOnCondition(WorldSource world, int x, int y, int z) {
		return false;
	}

	public int getFillLevel(WorldSource world, int x, int y, int z) {
		TileEntityChute te = (TileEntityChute)world.getTileEntity(x, y, z);
		float fill = (float)te.getNumUnitsInside() / (float)te.getMaxUnits();
		return (int)Math.ceil(10.0f * fill);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		TileEntityChute te = (TileEntityChute)world.getTileEntity(x, y, z);
		world.removeBlockTileEntity(x, y, z);
		if (world.isClientSide) {
			return;
		}
		te.dropAllItems();
	}


	@Override
	public boolean isSignalSource() {
		return true;
	}

	@Override
	public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
		TileEntityChute basketTileEntity = (TileEntityChute) worldSource.getTileEntity(x, y, z);
		if (basketTileEntity != null) {
			return basketTileEntity.getNumUnitsInside() == basketTileEntity.getMaxUnits();
		}
		return false;
	}

	@Override
	public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
		TileEntityChute basketTileEntity = (TileEntityChute) world.getTileEntity(x, y, z);
		if (basketTileEntity != null) {
			return basketTileEntity.getNumUnitsInside() == basketTileEntity.getMaxUnits();
		}
		return false;
	}

}
