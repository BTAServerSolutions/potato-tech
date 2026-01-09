package goldenage.potatotech;


import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.ItemRenderer;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.AABB;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

public class TileEntityRendererPipe extends TileEntityRenderer<TileEntityPipe> {
	private static final ItemRenderer itemEntityRenderer = new ItemRenderer(Minecraft.getMinecraft());

	private EntityItem itemEntity = null;
	private long timer = 0;

	int[] colors = {
		0xFF_FF_FF_FF, // Normal
		0xFF_09_09_09, // black
		0xFF_FF_00_00, // red
		0xFF_00_AA_00, // green
		0xFF_80_30_20, // brown
		0xFF_00_00_FF, // blue
		0xFF_A0_00_FF, // purple
		0xFF_00_C0_FF, // cyan
		0xFF_C0_C0_C0, // silver
		0xFF_50_50_50, // gray
		0xFF_FF_A0_A0, // pink
		0xFF_20_FF_20, // lime
		0xFF_C0_FF_20, // yellow
		0xFF_90_90_FF, // lightblue
		0xFF_FF_30_FF, // magenta
		0xFF_FF_90_00, // orange
		0xFF_FF_FF_FF, // White
		// NoNamedYes support
		0xFF_CA_30_4A, // Crimson
		0xFF_6E_1E_1B, // Maroon
		0xFF_A4_B9_A8, // Ash Gray
		0xFF_90_8D_2C, // Olive
		0xFF_C8_84_40, // Ochre
		0xFF_FF_C3_76, // Buff
		0xFF_61_E0_C2, // Verdigris
		0xFF_EA_E2_8F, // Light Yellow
		0xFF_4F_3E_D6, // Indigo
		0xFF_D0_FC_2C, // Xanthic
		0xFF_C1_68_34, // Cinnamon
		0xFF_2F_27_81, // Navy Blue
		0xFF_74_2F_8A, // Royal Purple
		0xFF_3D_8A_6F  // Viridian
	};

	@Override
	public void doRender(Tessellator tessellator, TileEntityPipe tileEntity, double x, double y, double z, float g) {
		timer += 1;
		if (timer > 8000) timer = 0;
		Minecraft mc = Minecraft.getMinecraft();
		float rotAngle = 360 * ((float)timer / 8000.0f);

		if (itemEntity == null)  {
			itemEntity = new EntityItem(tileEntity.worldObj, 0, 0, 0, new ItemStack(PTBlocks.testAreaMaker));
			itemEntity.entityBrightness = 1;
			itemEntity.age = 1;
		}

		List<float[]> blockPos = tileEntity.getStacksInPipePosition();
		List<ItemStack> stacks = tileEntity.getStacksInPipe();
		PipeStack[] pipeStacks = tileEntity.stacks;

		for (int i = 0; i < blockPos.size(); i++) {
			ItemStack stack = stacks.get(i);
			PipeStack pipeStack = pipeStacks[i];
			if (stack == null) continue;

			float[] pos = blockPos.get(i);
			itemEntity.item = stack;

			double yOffset = stack.itemID < Blocks.blocksList.length ? 0.0 : 0.1;
			if (stack.itemID == Blocks.SAPLING_OAK.id()
				|| stack.itemID == Blocks.SAPLING_PINE.id()
				|| stack.itemID == Blocks.SAPLING_BIRCH.id()
				|| stack.itemID == Blocks.SAPLING_CHERRY.id()
				|| stack.itemID == Blocks.SAPLING_OAK_RETRO.id()
				|| stack.itemID == Blocks.SAPLING_EUCALYPTUS.id()
				|| stack.itemID == Blocks.SAPLING_SHRUB.id()
				|| stack.itemID == Blocks.SAPLING_THORN.id()
			) {
				yOffset = 0.1;
			}


			float brightness = 1.0F;

			ItemStack itemstack = itemEntity.item;
			Item item = itemstack.getItem();
			if (item != null) {
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
				BlockModel.setRenderBlocks(itemEntityRenderer.renderBlocksInstance);

				GL11.glPushMatrix();
				if (itemstack.itemID < Blocks.blocksList.length &&
					Blocks.blocksList[itemstack.itemID] != null)
				{
					TextureRegistry.blockAtlas.bind();
					float itemSize = 0.25f;
					GL11.glTranslatef((float) (x + pos[0]) , (float) (y + pos[1] - yOffset) , (float) (z + pos[2]));
					GL11.glScalef(itemSize, itemSize, itemSize);
					//GL11.glRotatef(rotAngle, 0, 1, 0);

					for(int j = 0; j < renderCount; ++j) {
						BlockModel blockModel = BlockModelDispatcher.getInstance().getDispatch(Blocks.blocksList[itemstack.itemID]);
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
				GL11.glPopMatrix();

				if (pipeStack.color > 0) {
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glPushMatrix();
					int color = colors[pipeStack.color];
					TextureRegistry.blockAtlas.bind();
					float itemSize = 0.35f;
					GL11.glTranslatef((float) (x + pos[0]) , (float) (y + pos[1]) , (float) (z + pos[2]));
					GL11.glScalef(itemSize, itemSize, itemSize);
					BlockModelStandard blockModel = (BlockModelStandard) BlockModelDispatcher.getInstance().getDispatch(PTBlocks.pipeStack);
					blockModel.renderBlockOnInventory(tessellator, color, brightness, null);
					{
						if (blockModel.renderBlocks.useInventoryTint) {
							float cr = (float)(color >> 16 & 255) / 255.0F;
							float cg = (float)(color >> 8 & 255) / 255.0F;
							float cb = (float)(color & 255) / 255.0F;
							GL11.glColor4f(cr, cg, cb, 1.0f);
						} else {
							GL11.glColor4f(brightness, brightness, brightness, 1.0f);
						}

						float yOffset2 = 0.5F;
						AABB bounds = blockModel.getBlockBoundsForItemRender();
						GL11.glTranslatef(-0.5F, 0.0F - yOffset2, -0.5F);
						blockModel.renderBlockWithBounds(tessellator, bounds, 0, brightness, 1.0f, null);
						GL11.glTranslatef(0.5F, yOffset2, 0.5F);
					}

					GL11.glPopMatrix();
					GL11.glDisable(GL11.GL_BLEND);
				}

				GL11.glDisable(3042);
				GL11.glDisable(32826);
			}
		}
	}
}
