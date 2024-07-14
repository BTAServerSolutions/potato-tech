package goldenage.potatotech.blocks;

import goldenage.potatotech.IPotatoGui;
import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.mixin.EntityPlayerSPMixin;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.minecraft.core.block.BlockTileEntity;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.IInventory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockFilter extends BlockTileEntity {
    public BlockFilter(String key, int id, Material material) {
        super(key, id, material);
    }

    @Override
    protected TileEntity getNewBlockEntity() {
        return new TileEntityFilter();
    }

    @Override
    public void onBlockRemoved(World world, int x, int y, int z, int data) {
        dropFilterContent(world, x, y, z);
        super.onBlockRemoved(world, x, y, z, data);
    }

    public static void dropFilterContent(World world, int x, int y, int z) {
        TileEntityFilter tileEntityFilter = (TileEntityFilter) world.getBlockTileEntity(x, y, z);
        if (tileEntityFilter == null) {
            System.out.println("Can't drop chest items because tile entity is null at x: " + x + " y:" + y + " z: " + z);
            return;
        }
        for (int i = 0; i < tileEntityFilter.getSizeInventory(); ++i) {
            ItemStack itemStack = tileEntityFilter.getStackInSlot(i);
            if (itemStack == null) continue;
            EntityItem item = world.dropItem(x, y, z, itemStack);
            item.xd *= 0.5;
            item.yd *= 0.5;
            item.zd *= 0.5;
            item.delayBeforeCanPickup = 0;
        }
    }

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, EntityPlayer player, Side side, double xHit, double yHit) {
        if (!world.isClientSide) {
            IInventory inventory = (IInventory) world.getBlockTileEntity(x, y, z);
			((IPotatoGui) player).diplayBlockFilterGui(inventory);
        }
        return true;
    }
}
