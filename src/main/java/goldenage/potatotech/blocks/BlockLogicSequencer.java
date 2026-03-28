package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntitySequencer;
import goldenage.potatotech.networks.client.OpenGuiSequencerClientMessage;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import turniplabs.halplibe.helper.EnvironmentHelper;

public class BlockLogicSequencer extends BlockLogicRotatable {
	public BlockLogicSequencer(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		if (!EnvironmentHelper.isClientWorld()) {
			TileEntitySequencer sequencer = (TileEntitySequencer) world.getTileEntity(x, y, z);
			if (sequencer != null) {
				new OpenGuiSequencerClientMessage(sequencer).sendToPlayer(player);
			}
		}
		return true;
	}
}

