package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TileEntityRendererPipe extends TileEntityRenderer<TileEntityPipe> {
    private static final ItemEntityRenderer itemEntityRenderer = new ItemEntityRenderer();
    private EntityItem itemEntity = null;

	@Override
    public void onWorldChanged(World world) {
        itemEntityRenderer.setRenderDispatcher(EntityRenderDispatcher.instance);
        super.onWorldChanged(world);
    }

    @Override
    public void doRender(Tessellator tessellator, TileEntityPipe tileEntity, double x, double y, double z, float g) {
        if (itemEntity == null)  {
            itemEntity = new EntityItem(tileEntity.worldObj, 0, 0, 0, new ItemStack(PotatoTech.blockTestAreaMaker));
            //itemEntity.setRot(0, 0);
            itemEntity.entityBrightness = 1;
            itemEntity.age = 1;
            itemEntity.initialRotation = 0;
        }

        List<float[]> blockPos = tileEntity.getStacksInPipePosition();
        List<ItemStack> stacks = tileEntity.getStacksInPipe();

        for (int i = 0; i < blockPos.size(); i++) {
            ItemStack stack = stacks.get(i);
			if (stack == null) continue;

			float[] pos = blockPos.get(i);
            itemEntity.item = stack;

            double yOffset = stack.itemID < Block.blocksList.length ? 0.1 : 0.3;
            if (stack.itemID == Block.saplingOak.id
                    || stack.itemID == Block.saplingPine.id
                    || stack.itemID == Block.saplingBirch.id
                    || stack.itemID == Block.saplingCherry.id
                    || stack.itemID == Block.saplingOakRetro.id
                    || stack.itemID == Block.saplingEucalyptus.id
            ) {
                yOffset = 0.3;
            }

            itemEntityRenderer.doRender(tessellator, itemEntity, x + pos[0], y + pos[1] - yOffset, z + pos[2], 0, 0);
        }
    }
}
