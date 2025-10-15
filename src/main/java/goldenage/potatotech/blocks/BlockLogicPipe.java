package goldenage.potatotech.blocks;

import goldenage.potatotech.IKeybindings;
import goldenage.potatotech.PTItems;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.NetworkHelper;

import static net.minecraft.client.render.colorizer.Colorizers.mc;

public class BlockLogicPipe extends BlockLogic {
	public BlockLogicPipe(Block<?> block, Material material) {
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
		if (EnvironmentHelper.isClientWorld()) {
			return false;
		}

		TileEntityPipe te = (TileEntityPipe)world.getTileEntity(x, y, z);
		ItemStack heldItem = player.getHeldItem();

		int sideId = player.isSneaking() ? side.getOpposite().getId() : side.getId();

		if (heldItem == null) {
			if (player.isSneaking()) te.dropItems();
		} else if (heldItem.itemID == PTItems.wrench.id) {
			int mode = te.modeBySide[sideId];
			mode = (mode + 1) % 4;
			te.modeBySide[sideId] = (short)mode;

			world.markBlockNeedsUpdate(x, y, z);
			world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "random.click", 0.3f, mode % 2 == 0 ? 0.5f : 0.6f);
			return true;
		} else if (heldItem.itemID == Items.DYE.id) {
			te.colorBySide[sideId] = (short) (heldItem.getMetadata() + 1);
			world.markBlockNeedsUpdate(x, y, z);
			return true;
		} else if (heldItem.getItem().getClass().getName().equals("goocraft4evr.nonamedyes.item.ItemModDye")) {
			// NoNameDyes support
			te.colorBySide[sideId] = (short) (heldItem.getMetadata() + 17);
			world.markBlockNeedsUpdate(x, y, z);
			return true;
		} else if (heldItem.itemID == Items.PAPER.id) {
			te.colorBySide[sideId] = 0;
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
		if (EnvironmentHelper.isServerEnvironment()) {
			world.markBlockNeedsUpdate(x, y, z);
		}
	}
}
