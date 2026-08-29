package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntityChute;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;

public class BlockLogicChute extends BlockLogic {
	public BlockLogicChute(Block<?> block, Material material) {
		super(block, material);
	}


	@Override
	public boolean onInteracted(World world, TilePosc tilePos, Player player, Side side, double xHit, double yHit) {
		if (world.getTileEntity(tilePos) instanceof TileEntityChute te && te.getNumUnitsInside() > 0) {
			te.givePlayerAllItems(world, player);
			return true;
		}
		return false;
	}

	@Override
	public boolean renderAsNormalBlockOnCondition(WorldSource world, int x, int y, int z) {
		return false;
	}

	public int getFillLevel(WorldSource world, TilePosc tilePos) {
		if (world.getTileEntity(tilePos) instanceof TileEntityChute te) {
			float fill = Math.min(1.0f, (float) te.getNumUnitsInside() / te.getMaxUnits());
			return (int) Math.ceil(10.0f * fill);
		}
		return 0;
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
	public boolean isEmittingSignal(WorldSource world, TilePosc tilePos, Side side) {
		return world.getTileEntity(tilePos) instanceof TileEntityChute chute
			&& chute.getNumUnitsInside() >= chute.getMaxUnits();
	}

	@Override
	public boolean isEmittingDirectSignal(World world, TilePosc tilePos, Side side) {
		return isEmittingSignal(world, tilePos, side);
	}

}
