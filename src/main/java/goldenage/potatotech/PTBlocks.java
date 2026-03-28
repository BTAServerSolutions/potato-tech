package goldenage.potatotech;

import goldenage.potatotech.blocks.*;
import goldenage.potatotech.blocks.entities.*;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
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
	public static Block<? extends BlockLogic> filter;
	public static Block<? extends BlockLogic> pipeStack;
	public static Block<? extends BlockLogic> crafter;
	public static Block<? extends BlockLogic> pipeGold;
	public static Block<? extends BlockLogic> pipeDiamond;
	public static Block<? extends BlockLogic> energyConnector;
	public static Block<? extends BlockLogic> stirlingEngine;
	public static Block<? extends BlockLogic> sequencer;
	public static Block<? extends BlockLogic> coil;


	@Override
	public void afterBlockInit() {
		int id = config.getInt("starting_block_id");

		testAreaMaker = new BlockBuilder(MOD_ID)
			.build("test_area_maker", "test_area_maker", id++, block -> new BlockLogicTestAreaMaker(block, Material.stone));

		pipe = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityPipe::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("pipe", "pipe", id++, block -> new BlockLogicPipe(block, Material.metal));

		clayIron = new BlockBuilder(MOD_ID)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.setBlockSound(BlockSounds.SAND)
			.addTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("clay_iron", "clay_iron", id++, block ->
				new BlockLogicCrushable(block, Material.clay, "Iron")
			);

		clayGold = new BlockBuilder(MOD_ID)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.setBlockSound(BlockSounds.SAND)
			.addTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("clay_gold", "clay_gold", id++, block ->
				new BlockLogicCrushable(block, Material.clay, "Gold")
			);

		chute = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityChute::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.setBlockSound(BlockSounds.STONE)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("chute", "chute", id++, block ->
				new BlockLogicChute(block, Material.stone)
			);

		filter = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityFilter::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.setBlockSound(BlockSounds.WOOD)
			.addTags(BlockTags.MINEABLE_BY_AXE)
			.build("filter", "filter", id++, block ->
				new BlockLogicFilter(block, Material.wood)
			);

		pipeStack = new BlockBuilder(MOD_ID)
			.build("pipe_stack", "pipe_stack", id++, block -> new BlockLogic(block, Material.metal));

		crafter = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityCrafter::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.setBlockSound(BlockSounds.METAL)
			.build("crafter", "crafter", id++, block ->
					new BlockLogicCrafter(block, Material.metal)
			);

		pipeGold = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityGoldPipe::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("gold_pipe", "gold_pipe", id++, block -> new BlockLogicPipe(block, Material.metal));

		pipeDiamond = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityDiamondPipe::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("diamond_pipe", "diamond_pipe", id++, block -> new BlockLogicPipe(block, Material.metal));

		energyConnector = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityEnergyConnector::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("energy_connector", "energy_connector", id++, block -> new BlockLogicEnergyConnector(block, Material.metal));

		stirlingEngine = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntityStirlingEngine::new)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("stirling_engine", "stirling_engine", id++, block -> new BlockLogicStirlingEngine(block, Material.metal));

		coil = new BlockBuilder(MOD_ID)
			.setHardness(1.0f)
			.setResistance(3.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("coil", "coil", id++, block -> new BlockLogic(block, Material.metal));

		sequencer = new BlockBuilder(MOD_ID)
			.setTileEntity(TileEntitySequencer::new)
			.setHardness(0.0f)
			.setResistance(2.0f)
			.addTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("sequencer", "sequencer", id++, block -> new BlockLogicSequencer(block, Material.metal));
	}
}
