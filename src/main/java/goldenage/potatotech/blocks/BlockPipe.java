package goldenage.potatotech.blocks;

import goldenage.potatotech.PTItems;
import goldenage.potatotech.PotatoTech;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockPipe extends BlockLogic {
	public BlockPipe(Block<?> block, Material material) {
		super(block, material);
		this.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlockOnCondition(WorldSource world, int x, int y, int z) {
		return false;
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		TileEntityPipe te = (TileEntityPipe)world.getTileEntity(x, y, z);
		if (te != null) {
			te.dropItems();
		}
		super.onBlockRemoved(world, x, y, z, data);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		TileEntityPipe te = (TileEntityPipe)world.getTileEntity(x, y, z);
		ItemStack heldItem = player.getHeldItem();
		PotatoTech.LOGGER.info("Item Key:" + heldItem.getItemKey());
		if (heldItem == null) {
			te.dropItems();
		} else if (heldItem.itemID == PTItems.wrench.id) {
			int mode = te.modeBySide[side.getId()];
			mode = (mode + 1) % 4;
			te.modeBySide[side.getId()] = (short)mode;

			world.markBlockNeedsUpdate(x, y, z);
			world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "random.click", 0.3f, mode % 2 == 0 ? 0.5f : 0.6f);
			return true;
		} else if (heldItem.getItemKey() == "item.dye") {
			te.colorBySide[side.getId()] = (short) (heldItem.getMetadata() + 1);
			world.markBlockNeedsUpdate(x, y, z);
			return true;
		} else if (heldItem.getItem().getClass().getName().equals("goocraft4evr.nonamedyes.item.ItemModDye")) {
			// NoNameDyes support
			te.colorBySide[side.getId()] = (short) (heldItem.getMetadata() + 17);
			world.markBlockNeedsUpdate(x, y, z);
			return true;
		} else if (heldItem.getItemKey() == "item.paper") {
			te.colorBySide[side.getId()] = 0;
			world.markBlockNeedsUpdate(x, y, z);
			return true;
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		boolean flag = world.hasDirectSignal(x, y, z) || world.hasNeighborSignal(x, y, z);
		if (flag) {
			 world.scheduleBlockUpdate(x, y, z, this.id(), 0);
		}
	}
}
