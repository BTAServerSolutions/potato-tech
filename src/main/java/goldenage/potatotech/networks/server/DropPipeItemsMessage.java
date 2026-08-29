package goldenage.potatotech.networks.server;

import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.pos.TilePos;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class DropPipeItemsMessage implements NetworkMessage {
	private int x;
	private int y;
	private int z;

	public DropPipeItemsMessage() {
	}

	public DropPipeItemsMessage(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void encodeToUniversalPacket(UniversalPacket buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public void decodeFromUniversalPacket(UniversalPacket buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void handleServerEnv(NetworkContext context) {
		Player player = context.player;
		if (player == null || player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) > 36.0) {
			return;
		}

		TileEntity tileEntity = player.world.getTileEntity(new TilePos(x, y, z));
		if (tileEntity instanceof TileEntityPipe pipe) {
			pipe.dropItems();
			pipe.setChanged();
		}
	}
}
