package goldenage.potatotech.mixins;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.blocks.BlockLogicPipe;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, remap = false)
public class ItemPaperMixin {
	@Inject(method = "onUseOnBlock", at = @At("HEAD"), cancellable = true)
	private void clearPipeColor(@NotNull ItemStack itemStack, World world, Player player, TilePosc tilePos, Side side, double xHit, double yHit, CallbackInfoReturnable<Boolean> cir) {
		if (itemStack.getItem() != Items.PAPER) {
			return;
		}

		int blockId = world.getBlockId(tilePos.x(), tilePos.y(), tilePos.z());
		if (blockId != PTBlocks.pipe.id() && blockId != PTBlocks.pipeGold.id() && blockId != PTBlocks.pipeDiamond.id() && blockId != PTBlocks.pipeSteel.id()) {
			return;
		}

		BlockLogic pipe = world.getBlockLogic(tilePos.x(), tilePos.y(), tilePos.z(), BlockLogicPipe.class);
		if (pipe != null && pipe.onBlockRightClicked(world, tilePos.x(), tilePos.y(), tilePos.z(), player, side, xHit, yHit)) {
			cir.setReturnValue(true);
		}
	}
}
