package goldenage.potatotech;

import goldenage.potatotech.blocks.BlockLogicTestAreaMaker;
import goldenage.potatotech.blocks.BlockPipe;
import goldenage.potatotech.blocks.TileEntityPipe;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.util.BlockInitEntrypoint;

import static goldenage.potatotech.PotatoTech.MOD_ID;
import static goldenage.potatotech.PotatoTech.config;

public class PTBlocks implements BlockInitEntrypoint {

	static Block<?> testAreaMaker;
	static Block<?> pipe;

	@Override
	public void afterBlockInit() {
		int id = config.getInt("starting_block_id");

		testAreaMaker = new BlockBuilder(MOD_ID)
			.build("test_area_maker", "test_area_maker", id++, block -> new BlockLogicTestAreaMaker(block, Material.stone));

		pipe = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityPipe::new)
			.build("pipe", "pipe", id++, block -> new BlockPipe(block, Material.metal));
	}
}
