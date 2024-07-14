package goldenage.potatotech.blocks;

import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFence;
import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Random;

public class BlockPipe extends BlockTileEntity {

	public BlockPipe(String key, int id, Material material) {
		super(key, id, material);
		this.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}

    @Override
    protected TileEntity getNewBlockEntity() {
        return new TileEntityPipe();
    }

	@Override
    public boolean isSolidRender() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void onBlockRemoved(World world, int x, int y, int z, int data) {
        TileEntityPipe te = (TileEntityPipe)world.getBlockTileEntity(x, y, z);
        if (te != null) {
            te.dropItems();
        }
        super.onBlockRemoved(world, x, y, z, data);
    }

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xHit, double yHit) {
        TileEntityPipe te = (TileEntityPipe)world.getBlockTileEntity(x, y, z);
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.itemID == PotatoTech.itemWrench.id) {
			int mode = te.modeBySide[side.getId()];
			mode = (mode + 1) % 4;
			te.modeBySide[side.getId()] = mode;

			world.markBlockNeedsUpdate(x, y, z);
			world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, "random.click", 0.3f, mode % 2 == 0 ? 0.5f : 0.6f);
            return true;
        } else if (heldItem == null) {
			te.dropItems();
		}
        return false;
    }

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		if (blockId > 0 && Block.blocksList[blockId].canProvidePower()) {
			boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockGettingPowered(x, y, z);
			if (flag) {
				world.scheduleBlockUpdate(x, y, z, this.id, 0);
			}
		}
	}


}
