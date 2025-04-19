package goldenage.potatotech.blocks;

import goldenage.potatotech.PTItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicCrushable extends BlockLogic {
	public String oreOutput;

	public BlockLogicCrushable(Block<?> block, Material material, String oreOutput) {
		super(block, material);
		this.oreOutput = oreOutput;
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		if (EnumDropCause.PISTON_CRUSH == dropCause) {
			ItemStack[] crushDrop = new ItemStack[]{
				new ItemStack(oreOutput == "Iron" ? PTItems.crushedIronOre : PTItems.crushedGoldOre, 4),
				new ItemStack(Items.CLAY, (int) (Math.random() + 1.5))
			};
			return crushDrop;
		}
		return super.getBreakResult(world, dropCause, meta, tileEntity);
	}
}
