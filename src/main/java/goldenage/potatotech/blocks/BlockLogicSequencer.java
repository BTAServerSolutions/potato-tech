package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntitySequencer;
import goldenage.potatotech.networks.client.OpenGuiSequencerClientMessage;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import turniplabs.halplibe.helper.EnvironmentHelper;

public class BlockLogicSequencer extends BlockLogicRotatable {
	public BlockLogicSequencer(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean onInteracted(World world, TilePosc tilePos, Player player, Side side, double xHit, double yHit) {
		if (!EnvironmentHelper.isClientWorld()) {
			TileEntitySequencer sequencer = (TileEntitySequencer) world.getTileEntity(tilePos);
			if (sequencer != null) {
				new OpenGuiSequencerClientMessage(sequencer).sendToPlayer(player);
			}
		}
		return true;
	}
}
