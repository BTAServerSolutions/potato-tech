package goldenage.potatotech;

import goldenage.potatotech.blocks.*;
import goldenage.potatotech.blocks.entities.TileEntityCrusher;
import goldenage.potatotech.blocks.entities.TileEntityFilter;
import goldenage.potatotech.blocks.entities.TileEntityPipe;
import goldenage.potatotech.blocks.models.BlockModelCrusher;
import goldenage.potatotech.blocks.models.BlockModelPipe;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.stitcher.AtlasStitcher;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.util.GameStartEntrypoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PotatoTech implements ModInitializer, GameStartEntrypoint, ClientStartEntrypoint {
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
	public static Block blockCrusher;
	public static Block blockPlacer;
	public static Block blockFilter;

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

		blockNum++;

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

		int itemNum = config.getInt("starting_item_id");
		itemWrench = new ItemBuilder(MOD_ID).setIcon("potatotech:item/wrench").build(new Item("wrench", itemNum++));

		EntityHelper.createSpecialTileEntity(TileEntityPipe.class, "pipe.tile", TileEntityRendererPipe::new);
		EntityHelper.createTileEntity(TileEntityCrusher.class, "crusher.tile");
		EntityHelper.createTileEntity(TileEntityFilter.class, "filter.tile");


	}

	public void loadTextures(AtlasStitcher stitcher){

		// This is awful, but required until 7.2-pre2 comes out
		String id = TextureRegistry.stitcherMap.entrySet().stream().filter((e)->e.getValue() == stitcher).map(Map.Entry::getKey).collect(Collectors.toSet()).stream().findFirst().orElse(null);
		if(id == null){
			throw new RuntimeException("Failed to load textures: invalid atlas provided!");
		}
		LOGGER.info("Loading "+id+" textures...");
		long start = System.currentTimeMillis();

		String path = String.format("%s/%s/%s", "/assets", MOD_ID, stitcher.directoryPath);
		URI uri;
		try {
			LOGGER.info("path: " + path);
			URL url = DataLoader.class.getResource(path);
			LOGGER.info("path: " + url);
			uri = url.toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		FileSystem fileSystem = null;
		Path myPath;
		if (uri.getScheme().equals("jar")) {
			try {
				fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			myPath = fileSystem.getPath(path);
		} else {
			myPath = Paths.get(uri);
		}

		Stream<Path> walk;
		try {
			walk = Files.walk(myPath, Integer.MAX_VALUE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Iterator<Path> it = walk.iterator();

		while (it.hasNext()) {
			Path file = it.next();
			String name = file.getFileName().toString();
			if (name.endsWith(".png")) {
				String path1 = file.toString().replace(file.getFileSystem().getSeparator(), "/");
				String cutPath = path1.split(path)[1];
				cutPath = cutPath.substring(0, cutPath.length() - 4);
				TextureRegistry.getTexture(MOD_ID + ":"+ id + cutPath);
			}
		}

		walk.close();
		if (fileSystem != null) {
			try {
				fileSystem.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			TextureRegistry.initializeAllFiles(MOD_ID, stitcher);
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException("Failed to load textures.", e);
		}
		LOGGER.info(String.format("Loaded "+id+" textures (took %sms).", System.currentTimeMillis() - start));
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
		loadTextures(TextureRegistry.blockAtlas);
		loadTextures(TextureRegistry.itemAtlas);
		Minecraft.getMinecraft(Minecraft.class).renderEngine.refreshTextures(new ArrayList<>());
	}
}
