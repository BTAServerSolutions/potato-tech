package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import goldenage.potatotech.networks.client.OpenGuiCrafterClientMessage;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.util.Random;

public class BlockLogicCrafter extends BlockLogicRotatable {
    protected Random crafterRand = new Random();

    public BlockLogicCrafter(Block<?> block, Material material) {
        super(block, material);
    }

    @Override
    public boolean onInteracted(World world, TilePosc tilePos, Player player, Side side, double xHit, double yHit) {
        if (!EnvironmentHelper.isClientWorld()) {
			TileEntityCrafter crafter = (TileEntityCrafter) world.getTileEntity(tilePos);
			new OpenGuiCrafterClientMessage(crafter).sendToPlayer(player);
        }
        return true;
    }


    @Override
    public void onBlockRemoved(World world, int x, int y, int z, int data) {
        if (world.isClientSide) {
            super.onBlockRemoved(world, x, y, z, data);
            return;
        }

        TileEntityCrafter tileEntityCrafter = (TileEntityCrafter) world.getTileEntity(x, y, z);
        if (tileEntityCrafter == null) {
            super.onBlockRemoved(world, x, y, z, data);
            return;
        }

        for (int l = 0; l < tileEntityCrafter.getContainerSize(); ++l) {
            ItemStack itemstack = tileEntityCrafter.getItem(l);
            if (itemstack == null) continue;
            float f = this.crafterRand.nextFloat() * 0.8f + 0.1f;
            float f1 = this.crafterRand.nextFloat() * 0.8f + 0.1f;
            float f2 = this.crafterRand.nextFloat() * 0.8f + 0.1f;
            while (itemstack.stackSize > 0) {
                int i1 = this.crafterRand.nextInt(21) + 10;
                if (i1 > itemstack.stackSize) {
                    i1 = itemstack.stackSize;
                }
                itemstack.stackSize -= i1;
                EntityItem entityitem = new EntityItem(world, (float)x + f, (float)y + f1, (float)z + f2, new ItemStack(itemstack.itemID, i1, itemstack.getMetadata()));
                float f3 = 0.05f;
                entityitem.xd = (float)this.crafterRand.nextGaussian() * f3;
                entityitem.yd = (float)this.crafterRand.nextGaussian() * f3 + 0.2f;
                entityitem.zd = (float)this.crafterRand.nextGaussian() * f3;
                world.entityJoinedWorld(entityitem);
            }
        }
        super.onBlockRemoved(world, x, y, z, data);
    }
}
