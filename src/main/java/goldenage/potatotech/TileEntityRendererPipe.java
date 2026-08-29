package goldenage.potatotech;


import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.block.model.BlockModel;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

import java.util.List;

public class TileEntityRendererPipe extends TileEntityRenderer<TileEntityPipe> {
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
	public void doRender(TessellatorGeneral tessellator, TileEntityPipe tileEntity, double x, double y, double z, float g) {
		timer += 1;
		if (timer > 8000) timer = 0;
		float rotAngle = 360 * ((float)timer / 8000.0f);

		if (itemEntity == null)  {
			itemEntity = new EntityItem(tileEntity.worldObj, 0, 0, 0, new ItemStack(PTBlocks.testAreaMaker));
			itemEntity.age = 1;
		}

		List<float[]> blockPos = tileEntity.getStacksInPipePosition();
		List<ItemStack> stacks = tileEntity.getStacksInPipe();
		PipeStack[] pipeStacks = tileEntity.stacks;

		byte lightIndex = tileEntity.worldObj.getLightIndex(tileEntity.tilePos, 0);

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

				if (itemstack.itemID < Blocks.blocksList.length &&
					Blocks.blocksList[itemstack.itemID] != null)
				{
					TextureRegistry.worldAtlas.bind();
					float itemSize = 0.25f;

					for(int j = 0; j < renderCount; ++j) {
						GLRenderer.pushFrame();
						GLRenderer.modelM4f().translate((float) (x + pos[0]), (float) (y + pos[1] - yOffset), (float) (z + pos[2]));
						GLRenderer.modelM4f().scale(itemSize, itemSize, itemSize);

						BlockModel blockModel = BlockModelDispatcher.getInstance().getDispatch(Blocks.blocksList[itemstack.itemID]);
						blockModel.renderStandalone(tessellator, itemstack.getMetadata(), lightIndex);

						GLRenderer.popFrame();
					}
				} else {
					boolean items3D = (Boolean) GameSettings.ITEMS_3D.value;

					GLRenderer.pushFrame();
					if (items3D) {
						GLRenderer.modelM4f().translate((float) (x + pos[0]), (float) (y + pos[1] - yOffset - 0.1), (float) (z + pos[2]));
						GLRenderer.modelM4f().scale(0.75f, 0.75f, 0.75f);
						GLRenderer.modelM4f().rotateY((float) Math.toRadians(rotAngle));
					} else {
						GLRenderer.modelM4f().translate((float) (x + pos[0]), (float) (y + pos[1] - yOffset), (float) (z + pos[2]));
					}

					ItemModelDispatcher.getInstance().getDispatch(item).renderItemEntity(tessellator, itemstack, items3D, renderCount, 0, 1F, lightIndex, (float) 0);

					GLRenderer.popFrame();
				}

				if (pipeStack.color > 0) {
					TextureRegistry.worldAtlas.bind();
					float itemSize = 0.35f;

					GLRenderer.pushFrame();
					GLRenderer.modelM4f().translate((float) (x + pos[0]), (float) (y + pos[1]), (float) (z + pos[2]));
					GLRenderer.modelM4f().scale(itemSize, itemSize, itemSize);

					BlockModelStandard blockModel = (BlockModelStandard) BlockModelDispatcher.getInstance().getDispatch(PTBlocks.pipeStack);
					int color = colors[pipeStack.color];
					GLRenderer.setColor4f(((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, 1.0f);
					blockModel.renderStandalone(tessellator, 0, lightIndex);
					GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);

					GLRenderer.popFrame();
				}
			}
		}
	}
}
