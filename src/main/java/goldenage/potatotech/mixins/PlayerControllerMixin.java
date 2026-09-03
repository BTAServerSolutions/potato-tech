package goldenage.potatotech.mixins;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.blocks.BlockLogicPipe;
import goldenage.potatotech.networks.server.DropPipeItemsMessage;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.helper.EnvironmentHelper;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin {
	@Inject(method = "useOrPlaceItemStackOnTile", at = @At("HEAD"), cancellable = true)
	private void requestPipeItemDrop(Player player, World world, ItemStack itemStack, TilePosc tilePos, Side side, double xHit, double yHit, CallbackInfoReturnable<Boolean> cir) {
		if (itemStack != null || !player.isSneaking()) {
			return;
		}

		int blockId = world.getBlockId(tilePos.x(), tilePos.y(), tilePos.z());
		if (blockId == PTBlocks.pipe.id() || blockId == PTBlocks.pipeGold.id() || blockId == PTBlocks.pipeDiamond.id() || blockId == PTBlocks.pipeSteel.id()) {
			if (EnvironmentHelper.isSinglePlayer()) {
				BlockLogic pipe = world.getBlockLogic(tilePos.x(), tilePos.y(), tilePos.z(), BlockLogicPipe.class);
				if (pipe == null || !pipe.onBlockRightClicked(world, tilePos.x(), tilePos.y(), tilePos.z(), player, side, xHit, yHit)) {
					return;
				}
			} else {
				NetworkHandler.sendToServer(new DropPipeItemsMessage(tilePos.x(), tilePos.y(), tilePos.z()));
			}
			cir.setReturnValue(true);
		}
	}
}
