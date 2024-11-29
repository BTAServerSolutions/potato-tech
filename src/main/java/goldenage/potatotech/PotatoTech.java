package goldenage.potatotech;

import goldenage.potatotech.blocks.*;
import goldenage.potatotech.blocks.entities.*;
import goldenage.potatotech.blocks.models.BlockModelBlockPipeStack;
import goldenage.potatotech.blocks.models.BlockModelCrusher;
import goldenage.potatotech.blocks.models.BlockModelPipe;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.color.BlockColorCustom;
import net.minecraft.client.render.block.model.BlockModelTransparent;
import net.minecraft.client.render.stitcher.AtlasStitcher;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockGlass;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PotatoTech implements ModInitializer, GameStartEntrypoint, ClientStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "potatotech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigHandler config;
	static {
		Properties prop = new Properties();
		prop.setProperty("starting_block_id","1999");
		prop.setProperty("starting_item_id","17999");
		config = new ConfigHandler(MOD_ID, prop);
		config.updateConfig();
	}

	public static Item itemWrench;
	public static Block blockTestAreaMaker;
	public static Block blockPipe;

	public static Block blockPipeStack;
	public static Block blockCrusher;
	public static Block blockPlacer;
	public static Block blockFilter;

	public static Block blockPipeGold;
	public static Block blockPipeDiamond;

	@Override
    public void onInitialize() {
        LOGGER.info("ExampleMod initialized.");

		int blockNum = config.getInt("starting_block_id");

		blockTestAreaMaker = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/potato")
			.build(new BlockTestAreaMaker("test_area_maker", blockNum++, Material.metal));

		blockPipe = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/pipe")
			.setBlockModel(BlockModelPipe::new)
			.build(new BlockPipe("pipe", blockNum++, Material.metal));

		blockPipeStack = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/pipe_stack")
			.setBlockModel(BlockModelBlockPipeStack::new)
			.setBlockColor(block -> new BlockColorBlockPipeStack())
			.build(new BlockGlass("pipe_stack", blockNum++, Material.metal));


		blockCrusher = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/block_crusher_side")
			.setTopTexture("potatotech:block/block_crusher_front")
			.setBottomTexture("potatotech:block/block_crusher_back")
			.setBlockModel(BlockModelCrusher::new)
			.build(new BlockCrusher("block_crusher", blockNum++, Material.stone));

		blockPlacer = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/block_placer_side")
			.setTopTexture("potatotech:block/block_placer_front")
			.setBottomTexture("potatotech:block/block_placer_back")
			.setBlockModel(BlockModelCrusher::new)
			.build(new BlockPlacer("block_placer", blockNum++, Material.stone));

		blockFilter = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/block_filter")
			.build(new BlockFilter("filter", blockNum++, Material.wood));

		blockPipeGold = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/gold_pipe")
			.setBlockModel(BlockModelPipe::new)
			.build(new BlockGoldPipe("gold_pipe", blockNum++, Material.metal));

		blockPipeDiamond = new BlockBuilder(MOD_ID)
			.setTextures("potatotech:block/diamond_pipe")
			.setBlockModel(BlockModelPipe::new)
			.build(new BlockDiamondPipe("diamond_pipe", blockNum++, Material.metal));

		int itemNum = config.getInt("starting_item_id");
		itemWrench = new ItemBuilder(MOD_ID).setIcon("potatotech:item/wrench").build(new Item("wrench", itemNum++));

		EntityHelper.createSpecialTileEntity(TileEntityPipe.class, "pipe.tile", TileEntityRendererPipe::new);
		EntityHelper.createSpecialTileEntity(TileEntityGoldPipe.class, "gold_pipe.tile", TileEntityRendererPipe::new);
		EntityHelper.createSpecialTileEntity(TileEntityDiamondPipe.class, "diamond_pipe.tile", TileEntityRendererPipe::new);
		EntityHelper.createTileEntity(TileEntityCrusher.class, "crusher.tile");
		EntityHelper.createTileEntity(TileEntityPlacer.class, "placer.tile");
		EntityHelper.createTileEntity(TileEntityFilter.class, "filter.tile");
	}

	@Override
	public void beforeGameStart() {
	}

	@Override
	public void afterGameStart() {
		BlockCrusher.initCrusherResults();
	}

	@Override
	public void beforeClientStart() {
	}

	@Override
	public void afterClientStart() {
	}

	@Override
	public void onRecipesReady() {
		PotatoTechRecipeRegistry.InitRecipes();
	}

	@Override
	public void initNamespaces() {
		PotatoTechRecipeRegistry.InitNameSpaces();
	}
}
