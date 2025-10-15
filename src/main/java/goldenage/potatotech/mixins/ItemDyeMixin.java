package goldenage.potatotech.mixins;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.blocks.BlockLogicPipe;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemDye;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemDye.class, remap = false)
public class ItemDyeMixin {

	@Inject(method = "onUseItemOnBlock", at = @At("HEAD"))
	public void onUseItemOnBlock(ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		int blockId = world.getBlockId(blockX, blockY, blockZ);
		if (blockId == PTBlocks.pipe.id() || blockId == PTBlocks.pipeDiamond.id() || blockId == PTBlocks.pipeGold.id()) {
			BlockLogic pipeBlock = world.getBlockLogic(blockX, blockY, blockZ, BlockLogicPipe.class);
			if (pipeBlock != null) {
				pipeBlock.onBlockRightClicked(world, blockX, blockY, blockZ, player, side, xPlaced, yPlaced);
			}
		}
	}
}
