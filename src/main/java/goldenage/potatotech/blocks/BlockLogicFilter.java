package goldenage.potatotech.blocks;

import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.networks.client.OpenGuiFilterClientMessage;
import goldenage.potatotech.screens.ScreenFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import turniplabs.halplibe.helper.EnvironmentHelper;

public class BlockLogicFilter extends BlockLogic {
	public BlockLogicFilter(Block<?> block, Material material) {
		super(block, material);
	}

	@Override
	public void onBlockRemoved(World world, int x, int y, int z, int data) {
		if (!world.isClientSide) {
			dropFilterContent(world, x, y, z);
		}
		super.onBlockRemoved(world, x, y, z, data);
	}

	public static void dropFilterContent(World world, int x, int y, int z) {
		TileEntityFilter tileEntityFilter = (TileEntityFilter) world.getTileEntity(x, y, z);
		if (tileEntityFilter == null) {
			System.out.println("Can't drop chest items because tile entity is null at x: " + x + " y:" + y + " z: " + z);
			return;
		}
		for (int i = 0; i < tileEntityFilter.getContainerSize(); ++i) {
			ItemStack itemStack = tileEntityFilter.getItem(i);
			if (itemStack == null) continue;
			EntityItem item = world.dropItem(x, y, z, itemStack);
			item.xd *= 0.5;
			item.yd *= 0.5;
			item.zd *= 0.5;
			item.pickupDelay = 0;
		}
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
		if (!EnvironmentHelper.isClientWorld()) {
			TileEntityFilter filter = (TileEntityFilter) world.getTileEntity(x, y, z);
			new OpenGuiFilterClientMessage(filter).sendToPlayer(player);
		}
		return true;
	}
}
