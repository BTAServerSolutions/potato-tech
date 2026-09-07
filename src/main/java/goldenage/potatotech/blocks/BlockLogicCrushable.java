package goldenage.potatotech.blocks;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PTItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicCrushable extends BlockLogic {
	public String oreOutput;

	public BlockLogicCrushable(Block<?> block, Material material, String oreOutput) {
		super(block, material);
		this.oreOutput = oreOutput;
	}

	@Override
	public ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int meta, TileEntity tileEntity) {
		if (EnumDropCause.PISTON_CRUSH == dropCause) {
			return switch (oreOutput) {
				case "Gold", "Iron" -> new ItemStack[]{
					new ItemStack(oreOutput == "Iron" ? PTItems.crushedIronOre : PTItems.crushedGoldOre, 4),
					new ItemStack(Items.CLAY, (int) (Math.random() + 1.5))
				};
				case "OreRichSand" -> new ItemStack[]{new ItemStack(PTBlocks.oreRichSand, 1)};
				default -> super.getBreakResult(world, dropCause, meta, tileEntity);
			};
		}
		return super.getBreakResult(world, dropCause, meta, tileEntity);
	}
}
