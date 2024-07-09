package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelBlock;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.client.util.helper.Textures;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockStone;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

public class TileEntityRendererPipe extends TileEntityRenderer<TileEntityPipe> {
    private static final ItemEntityRenderer itemEntityRenderer = new ItemEntityRenderer();
    private EntityItem itemEntity = null;
	private long timer = 0;

	@Override
    public void onWorldChanged(World world) {
        itemEntityRenderer.setRenderDispatcher(EntityRenderDispatcher.instance);
        super.onWorldChanged(world);
    }

    @Override
    public void doRender(Tessellator tessellator, TileEntityPipe tileEntity, double x, double y, double z, float g) {
		timer += 1;
		if (timer > 8000) timer = 0;
		Minecraft mc = Minecraft.getMinecraft(this);
		float rotAngle = 360 * ((float)timer / 8000.0f);

        if (itemEntity == null)  {
            itemEntity = new EntityItem(tileEntity.worldObj, 0, 0, 0, new ItemStack(PotatoTech.blockTestAreaMaker));
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

            double yOffset = stack.itemID < Block.blocksList.length ? 0.0 : 0.1;
            if (stack.itemID == Block.saplingOak.id
                    || stack.itemID == Block.saplingPine.id
                    || stack.itemID == Block.saplingBirch.id
                    || stack.itemID == Block.saplingCherry.id
                    || stack.itemID == Block.saplingOakRetro.id
                    || stack.itemID == Block.saplingEucalyptus.id
            ) {
                yOffset = 0.1;
            }


			float brightness = 1.0F;

			ItemStack itemstack = itemEntity.item;
            Item item = itemstack.getItem();
            if (item != null) {
                GL11.glPushMatrix();
                byte renderCount = 1;
                if (itemEntity.item.stackSize > 1) {
                    renderCount = 2;
                }

                if (itemEntity.item.stackSize > 5) {
                    renderCount = 3;
                }

                if (itemEntity.item.stackSize > 20) {
                    renderCount = 4;
                }

                GL11.glEnable(32826);
                GL11.glEnable(3042);
                BlockModel.setRenderBlocks(itemEntityRenderer.renderBlocks);

                if (itemstack.itemID < Block.blocksList.length &&
                    Block.blocksList[itemstack.itemID] != null)
                {
                    TextureRegistry.blockAtlas.bindTexture();
                    float itemSize = 0.25f;
					GL11.glTranslatef((float) (x + pos[0]) , (float) (y + pos[1] - yOffset) , (float) (z + pos[2]));
                    GL11.glScalef(itemSize, itemSize, itemSize);
					//GL11.glRotatef(rotAngle, 0, 1, 0);

                    for(int j = 0; j < renderCount; ++j) {
                        BlockModel blockModel = BlockModelDispatcher.getInstance().getDispatch(Block.blocksList[itemstack.itemID]);
                        blockModel.renderBlockOnInventory(tessellator, itemstack.getMetadata(), brightness, null);
                    }
                } else {

					if (mc.gameSettings.items3D.value) {
						GL11.glTranslatef((float) (x + pos[0]) , (float) (y + pos[1] - yOffset - 0.1) , (float) (z + pos[2]));
						GL11.glScalef(0.75f, 0.75f, 0.75f);
						GL11.glRotatef(rotAngle, 0, 1, 0);
					} else {
						GL11.glTranslatef((float) (x + pos[0]) , (float) (y + pos[1] - yOffset) , (float) (z + pos[2]));
					}
                    ItemModelDispatcher.getInstance().getDispatch(item).renderAsItemEntity(Tessellator.instance, itemEntity, new Random(), itemstack, renderCount, 0, brightness, 1);
                }

                GL11.glDisable(3042);
                GL11.glDisable(32826);
                GL11.glPopMatrix();
            }
        }
    }
}
