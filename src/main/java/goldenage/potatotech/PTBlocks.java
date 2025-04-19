package goldenage.potatotech;

import goldenage.potatotech.blocks.BlockChute;
import goldenage.potatotech.blocks.BlockLogicCrushable;
import goldenage.potatotech.blocks.BlockLogicTestAreaMaker;
import goldenage.potatotech.blocks.BlockPipe;
import goldenage.potatotech.blocks.entities.TileEntityChute;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.sound.BlockSounds;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.util.BlockInitEntrypoint;

import static goldenage.potatotech.PotatoTech.MOD_ID;
import static goldenage.potatotech.PotatoTech.config;

public class PTBlocks implements BlockInitEntrypoint {

	public static Block<? extends BlockLogic> testAreaMaker;
	public static Block<? extends BlockLogic> pipe;
	public static Block<? extends BlockLogic> clayIron;
	public static Block<? extends BlockLogic> clayGold;
	public static Block<? extends BlockLogic> chute;


	@Override
	public void afterBlockInit() {
		int id = config.getInt("starting_block_id");

		testAreaMaker = new BlockBuilder(MOD_ID)
			.build("test_area_maker", "test_area_maker", id++, block -> new BlockLogicTestAreaMaker(block, Material.stone));

		pipe = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityPipe::new)
			.build("pipe", "pipe", id++, block -> new BlockPipe(block, Material.metal));
		clayIron = new BlockBuilder(MOD_ID)
			.setHardness(3.0f)
			.setBlockSound(BlockSounds.SAND)
			.build("clay_iron", "clay_iron", id++, block ->
				new BlockLogicCrushable(block, Material.clay, "Iron")
			);
		clayGold = new BlockBuilder(MOD_ID)
			.setHardness(3.0f)
			.setBlockSound(BlockSounds.SAND)
			.build("clay_gold", "clay_gold", id++, block ->
				new BlockLogicCrushable(block, Material.clay, "Gold")
			);
		chute = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityChute::new)
			.setHardness(3.0f)
			.setBlockSound(BlockSounds.STONE)
			.build("chute", "chute", id++, block ->
				new BlockChute(block, Material.stone)
			);
	}
}
