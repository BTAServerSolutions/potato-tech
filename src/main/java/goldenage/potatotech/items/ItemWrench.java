package goldenage.potatotech.items;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.BlockLogicPipe;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemDye;
import net.minecraft.core.item.ItemPaintBrush;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemWrench extends Item {
	public ItemWrench(String translationKey, @NotNull NamespaceID namespaceId, int id) {
		super(translationKey, String.valueOf(namespaceId), id);
	}

	@Override
	public boolean onUseItemOnBlock(ItemStack itemstack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
		int blockId = world.getBlockId(blockX, blockY, blockZ);
		if (blockId == PTBlocks.pipe.id() || blockId == PTBlocks.pipeDiamond.id() || blockId == PTBlocks.pipeGold.id()) {
			BlockLogic pipeBlock = world.getBlockLogic(blockX, blockY, blockZ, BlockLogicPipe.class);
			if (pipeBlock != null) {
				pipeBlock.onBlockRightClicked(world, blockX, blockY, blockZ, entityplayer, side, xPlaced, yPlaced);
				return true;
			}
		}
		return false;
	}
}
