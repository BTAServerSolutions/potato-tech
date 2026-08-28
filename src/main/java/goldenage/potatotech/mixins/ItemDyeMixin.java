package goldenage.potatotech.mixins;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.blocks.BlockLogicPipe;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemDye;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemDye.class, remap = false)
public class ItemDyeMixin {

	@Inject(method = "onUseOnBlock", at = @At("HEAD"))
	public void onUseOnBlock(ItemStack itemstack, World world, Player player, net.minecraft.core.world.pos.TilePosc tilePos, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		int blockX = tilePos.x();
		int blockY = tilePos.y();
		int blockZ = tilePos.z();
		int blockId = world.getBlockId(blockX, blockY, blockZ);
		if (blockId == PTBlocks.pipe.id() || blockId == PTBlocks.pipeDiamond.id() || blockId == PTBlocks.pipeGold.id()) {
			BlockLogic pipeBlock = world.getBlockLogic(blockX, blockY, blockZ, BlockLogicPipe.class);
			if (pipeBlock != null) {
				pipeBlock.onBlockRightClicked(world, blockX, blockY, blockZ, player, side, xPlaced, yPlaced);
			}
		}
	}
}
