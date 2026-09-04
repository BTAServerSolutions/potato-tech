package goldenage.potatotech.items;

import goldenage.potatotech.PTBlocks;
import goldenage.potatotech.PotatoTech;
import goldenage.potatotech.blocks.BlockLogicPipe;
import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import goldenage.potatotech.blocks.entities.TileEntityBedrockExtractor;
import goldenage.potatotech.blocks.entities.TileEntityStirlingEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemDye;
import net.minecraft.core.item.ItemPaintBrush;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemWrench extends Item {
	public ItemWrench(String translationKey, @NotNull NamespaceID namespaceId, int id) {
		super(translationKey, String.valueOf(namespaceId), id);
	}

	@Override
	public int getDamageVsEntity(@NotNull ItemStack stack, @NotNull Entity entity) {
		return 4 + ToolMaterial.stone.getDamage() * 2;
	}

	@Override
	public boolean onUseOnBlock(@NotNull ItemStack selfStack, @NotNull World world, @Nullable Player player, @NotNull TilePosc blockPos, @NotNull Side side, double xHit, double yHit) {
		int blockX = blockPos.x();
		int blockY = blockPos.y();
		int blockZ = blockPos.z();

		int blockId = world.getBlockId(blockX, blockY, blockZ);
		if (blockId == PTBlocks.pipe.id() || blockId == PTBlocks.pipeDiamond.id() || blockId == PTBlocks.pipeGold.id() || blockId == PTBlocks.pipeSteel.id()) {
			BlockLogic pipeBlock = world.getBlockLogic(blockX, blockY, blockZ, BlockLogicPipe.class);
			if (pipeBlock != null) {
				pipeBlock.onBlockRightClicked(world, blockX, blockY, blockZ, player, side, xHit, yHit);
				return true;
			}
		}

		if (!world.isClientSide) {
			if (blockId == PTBlocks.bedrockExtractor.id()) {
				TileEntityBedrockExtractor extractor = (TileEntityBedrockExtractor) world.getTileEntity(blockX, blockY, blockZ);
				if (player != null) {
					if (extractor == null || !extractor.hasValidDrillAssembly()) {
						player.sendMessage("Invalid Bedrock Extractor position: place a Bedrock Drill directly on top of bedrock below it.");
					} else {
						player.sendMessage("Bedrock Extractor charge: " + extractor.energy + "/" + TileEntityBedrockExtractor.getEnergyCapacity() + " PE");
					}
				}
				return true;
			}
			if (blockId == PTBlocks.energyConnector.id() || blockId == PTBlocks.energyConnectorMV.id()) {
				TileEntityEnergyConnector conn = (TileEntityEnergyConnector) world.getTileEntity(blockX, blockY, blockZ);
				player.sendMessage("energy amount = " + conn.energy);
			}
			if (blockId == PTBlocks.stirlingEngine.id()) {
				TileEntityStirlingEngine engine = (TileEntityStirlingEngine) world.getTileEntity(blockX, blockY, blockZ);
				player.sendMessage("power = " + engine.power);
			}
			if (blockId == Blocks.FURNACE_STONE_IDLE.id() || blockId == Blocks.FURNACE_STONE_ACTIVE.id()) {
				TileEntityFurnace engine = (TileEntityFurnace) world.getTileEntity(blockX, blockY, blockZ);
				player.sendMessage("burn time = " + engine.currentBurnTime);
			}
		}
		return false;
	}
}
