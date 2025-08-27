package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntityCrafter;
import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.screens.ScreenCrafter;
import goldenage.potatotech.screens.ScreenFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Random;

public class BlockLogicCrafter extends BlockLogicRotatable {
    protected Random crafterRand = new Random();

    public BlockLogicCrafter(Block<?> block, Material material) {
        super(block, material);
    }

    @Override
    public void onBlockPlacedByWorld(World world, int x, int y, int z) {
        super.onBlockPlacedByWorld(world, x, y, z);

        if (!world.isClientSide) {
            int l = world.getBlockId(x, y, z - 1);
            int i1 = world.getBlockId(x, y, z + 1);
            int j1 = world.getBlockId(x - 1, y, z);
            int k1 = world.getBlockId(x + 1, y, z);
            byte byte0 = 3;

            if (Blocks.solid[i1] && !Blocks.solid[l]) {
                byte0 = 2;
            }

            if (Blocks.solid[j1] && !Blocks.solid[k1]) {
                byte0 = 5;
            }

            if (Blocks.solid[k1] && !Blocks.solid[j1]) {
                byte0 = 4;
            }

            world.setBlockMetadataWithNotify(x, y, z, byte0);
        }
    }

    @Override
    public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
        if (!world.isClientSide) {
            TileEntityCrafter crafter = (TileEntityCrafter) world.getTileEntity(x, y, z);
            //TODO: Minecraft.getMinecraft().displayScreen(new ScreenCrafter(player, crafter));
        }
        return true;
    }


    @Override
    public void onBlockRemoved(World world, int x, int y, int z, int data) {
        int blockId = world.getBlockId(x,y,z);
        // TODO
        //if (blockId == PotatoLogisticsMod.blockFurnaceBurner.id || blockId == PotatoLogisticsMod.blockFurnaceBurnerOn.id) {return;}
        TileEntityCrafter tileEntityCrafter = (TileEntityCrafter) world.getTileEntity(x, y, z);
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
