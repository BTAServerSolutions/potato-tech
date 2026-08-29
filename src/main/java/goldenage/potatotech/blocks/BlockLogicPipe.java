package goldenage.potatotech.blocks;

import goldenage.potatotech.IKeybindings;
import goldenage.potatotech.PTItems;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
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
	public boolean isCubeShaped() {
		return false;
	}

	@Override
	public boolean blocksLight() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlockOnCondition(WorldSource world, int x, int y, int z) {
		return false;
	}

	@Override
	public AABBdc getCollisionAABB(WorldSource world, TilePosc tilePos) {
		return new AABBd(tilePos.x() + 0.25, tilePos.y() + 0.25, tilePos.z() + 0.25, tilePos.x() + 0.75, tilePos.y() + 0.75, tilePos.z() + 0.75);
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		if (!world.isClientSide) {
			TileEntityPipe te = (TileEntityPipe)world.getTileEntity(x, y, z);
			if (te != null) {
				te.dropItems();
			}
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

		int sideId = player.isSneaking() ? side.opposite().id : side.id;

		if (heldItem == null) {
			if (player.isSneaking()) {
				te.dropItems();
				te.setChanged();
				return true;
			}
		} else if (heldItem.itemID == PTItems.wrench.id) {
			int mode = te.modeBySide[sideId];
			mode = (mode + 1) % 4;
			te.modeBySide[sideId] = (short)mode;

			updateConnectionVisuals(world, x, y, z);
			world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "random.click", 0.3f, mode % 2 == 0 ? 0.5f : 0.6f);
			return true;
		} else if (heldItem.itemID == Items.DYE.id) {
			te.colorBySide[sideId] = (short) (heldItem.getMetadata() + 1);
			te.requestConnectionRenderUpdate();
			return true;
		} else if (heldItem.getItem().getClass().getName().equals("goocraft4evr.nonamedyes.item.ItemModDye")) {
			// NoNameDyes support
			te.colorBySide[sideId] = (short) (heldItem.getMetadata() + 17);
			te.requestConnectionRenderUpdate();
			return true;
		} else if (heldItem.itemID == Items.PAPER.id) {
			te.colorBySide[sideId] = 0;
			te.requestConnectionRenderUpdate();
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
			updateConnectionVisuals(world, x, y, z);
		}
	}

	private static void updateConnectionVisuals(World world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(x, y, z);
		TileEntityPipe pipe = entity instanceof TileEntityPipe ? (TileEntityPipe) entity : null;
		if (pipe != null) {
			pipe.requestConnectionRenderUpdate();
		}
		for (Direction direction : Direction.ID_MAP) {
			TileEntity neighborEntity = world.getTileEntity(x + direction.offsetX(), y + direction.offsetY(), z + direction.offsetZ());
			TileEntityPipe neighbor = neighborEntity instanceof TileEntityPipe ? (TileEntityPipe) neighborEntity : null;
			if (neighbor != null) {
				neighbor.requestConnectionRenderUpdate();
			}
		}
	}
}
