# Potato Logistics Development Guide

## Project

- Mod ID: `potatotech`
- Display name: Potato Logistics
- Game/API: Better than Adventure! `8.0.1`
- Loader: Fabric Loader with HalpLibe
- Java: 17
- Build system: Gradle Kotlin DSL
- Main entrypoint: `src/main/java/goldenage/potatotech/PotatoTech.java`
- Block registration: `src/main/java/goldenage/potatotech/PTBlocks.java`
- Item registration: `src/main/java/goldenage/potatotech/PTItems.java`
- Recipes: `src/main/java/goldenage/potatotech/PTRecipes.java`

## Build And Run

Use the Gradle wrapper from the repository root:

```powershell
.\gradlew.bat --no-daemon compileJava
.\gradlew.bat --no-daemon processResources
.\gradlew.bat --no-daemon runClient
```

Optional integrations are compile-only and must not become required runtime dependencies.

```powershell
.\gradlew.bat --no-daemon -PrunWithCatalyst=true runClient
.\gradlew.bat --no-daemon -PrunWithBTWaila=true runClient
```

Run a client launch after adding block models or other client bootstrap assets. Compilation alone cannot validate Dragonfly JSON models.

## Block And Item IDs

`starting_block_id` and `starting_item_id` are configurable but worlds persist the resulting numeric IDs.

- Never insert a new block registration between existing registrations in `PTBlocks.init()`.
- Always append new block registrations at the end of `PTBlocks.init()`.
- Never reorder existing block or item registrations.
- New blocks require a translation in `src/main/resources/assets/potatotech/lang/en_US/potatotech.lang`.
- New tile entities require a `TileEntityDispatcher.addMapping(...)` entry in `PotatoTech.onInitialize()`.

## Block Assets And Models

- Block textures: `src/main/resources/assets/potatotech/textures/block/`
- Item textures: `src/main/resources/assets/potatotech/textures/item/`
- Block models: `src/main/resources/assets/potatotech/models/block/`
- Pipe variants need four models: `core.json`, `arm.json`, `arm_insert.json`, and `arm_extract.json`.
- Validate every added JSON file. An incomplete model causes startup failure during `PTModels.initBlockModels()`.
- Register new block models in `PTModels.initBlockModels()`.
- If a block needs a custom item model, register it in `PTModels.initItemModels()`.

## Boilerplates

### Simple Block

1. Declare `public static Block<? extends BlockLogic> exampleBlock;` in `PTBlocks`.
2. Append its registration to the end of `PTBlocks.init()` only:

```java
exampleBlock = new BlockBuilder(MOD_ID)
	.setHardness(1.0f)
	.setResistance(3.0f)
	.addTags(BlockTags.MINEABLE_BY_PICKAXE)
	.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
	.build("example_block", "example_block", id++, block -> new BlockLogic(block, Materials.METAL));
```

3. Add `tile.potatotech.example_block.name` and `.desc` translations.
4. Register a `BlockModelStandard` in `PTModels.initBlockModels()` when the default model is insufficient.
5. Add recipes in `PTRecipes.load()`.

### Block With Tile Entity

1. Create `blocks/entities/TileEntityExample.java` extending `TileEntity`.
2. Persist all server state in `readAdditionalData` and `writeAdditionalData`.
3. Return `new PacketTileEntityData(this)` from `getDescriptionPacket()` for client-visible state.
4. Append the dispatcher mapping in `PotatoTech.onInitialize()`:

```java
TileEntityDispatcher.addMapping(TileEntityExample.class, id("tile.example"));
```

5. Append the block registration with `.setTileEntity(TileEntityExample::new)`:

```java
exampleBlock = new BlockBuilder(MOD_ID)
	.setTileEntity(TileEntityExample::new)
	.setHardness(1.0f)
	.setResistance(3.0f)
	.addTags(BlockTags.MINEABLE_BY_PICKAXE)
	.setCreativeInventoryPlacement(new CreativeInventoryPlacement.Category(CreativeInventoryCategory.MISCELLANEOUS))
	.build("example", "example", id++, block -> new BlockLogicExample(block, Materials.METAL));
```

6. Never mutate server state from client rendering, BTWaila providers, or client-side interaction code.

### Item

1. Declare the item field in `PTItems`.
2. Append its registration to the end of `PTItems.init()`; do not reorder registrations.
3. Use `simpleItem` for ordinary items:

```java
exampleItem = simpleItem("example_item", "example_item", "example_item", id++);
```

4. Use `customItem` for an `Item` subclass:

```java
exampleItem = customItem(new ItemExample("example_item", new NamespaceID(MOD_ID, "example_item"), id++), "example_item");
```

5. Add `textures/item/example_item.png` and `item.potatotech.example_item.name` / `.desc` translations.
6. `PTModels.initItemModels()` automatically registers textures recorded by `simpleItem` and `customItem`.

### Placeable Item

Use `ItemPlaceable` only when the block does not already supply its own block item:

```java
exampleItem = customItem(
	new ItemPlaceable(new NamespaceID(MOD_ID, "example_item"), "example_item", id++, PTBlocks.exampleBlock),
	"example_item"
);
```

### Crafting Recipe

Add recipes in `PTRecipes.load()` after the output block/item is registered:

```java
RecipeBuilder.Shaped(PotatoTech.MOD_ID)
	.setShape("IGI")
	.addInput('I', Items.INGOT_IRON)
	.addInput('G', Blocks.GLASS)
	.create("Example", new ItemStack(PTBlocks.exampleBlock, 16));
```

- Use `RecipeBuilder.Shapeless(PotatoTech.MOD_ID)` for order-independent inputs.
- Use `new ItemStack(output, amount)` when the recipe produces more than one item.
- After recipe changes, run `processResources` and verify the recipe count during a client launch.

### Pipe Variant

1. Create a `TileEntityPipe` subclass. Change only the intended limits/timers:

```java
public class TileEntityExamplePipe extends TileEntityPipe {
	public TileEntityExamplePipe() {
		super();
		maxStackSize = 16;
	}
}
```

2. Append the block and tile dispatcher registrations.
3. Add the four required pipe model JSON files under `models/block/example_pipe/`.
4. Register `BlockModelPipe` in `PTModels.initBlockModels()`.
5. Add block texture, translation, recipe, and all pipe interaction checks listed in the Pipes section.
6. Launch the client to validate the four Dragonfly models.

### GUI And Interaction

- Implement block interaction with BTA 8's `onInteracted(...)`, not obsolete right-click APIs.
- Perform state mutation only server-side. Use `EnvironmentHelper.isClientWorld()` or the world's client flag as appropriate.
- Register all custom network messages in `PotatoTech.onInitialize()`.
- Ensure blocks that store items drop or otherwise preserve their contents in their removal lifecycle method.

### Optional Mod Integration

1. Add the dependency as `compileOnly` in `build.gradle.kts` and as `suggests` in `fabric.mod.json`.
2. Keep integration classes under `compat/<mod>/`.
3. Gate runtime calls with `FabricLoader.getInstance().isModLoaded("modid")` where necessary.
4. Use Fabric entrypoints for plugin-style integrations such as BTWaila.
5. Add an opt-in `runWith...` local runtime flag for development and verify both with and without the dependency.

## Pipes

`TileEntityPipe` is the base pipe implementation. Its defaults define the iron/base pipe behavior:

- `maxStackSize = 1`
- `maxInputTimer = 12`
- `maxPipeStackTimer = 6`

Variants should subclass `TileEntityPipe` and change only their intended capacity/timing values. For example, the steel pipe keeps base timing and sets `maxStackSize = 16`.

Pipe transfer rules:

- Extraction may take up to the source pipe's `maxStackSize`, clamped by the available source stack.
- Insertion must be partial: insert only the destination capacity and retain the remainder in the pipe.
- Pipe-to-pipe transfers must be partial: clamp the moved amount to the receiving pipe's `maxStackSize`.
- A pipe stack is removed only after its `ItemStack.stackSize` reaches zero.
- Preserve `PipeStack.direction`, `color`, and reset the moved segment's timer when splitting a pipe-to-pipe transfer.
- Keep Signal Industries storage-container access on its dedicated reflective compatibility path. Calling that container's generic `getItem(0)` enables its enlarged stack size and breaks its shift-extract behavior.
- Catalyst item IO must respect the contacted machine face, `Connection`, and configured active slot.

When adding a pipe variant, update all interaction checks so wrench use, dyeing, paper color clearing, and empty-hand sneak interaction work on it:

- `items/ItemWrench.java`
- `mixins/ItemDyeMixin.java`
- `mixins/ItemPaperMixin.java`
- `mixins/PlayerControllerMixin.java`

## Optional Integrations

### Catalyst

- `catalyst-core` and `catalyst-energy` are optional.
- Keep Catalyst references isolated in `compat/catalyst/` and optional mixins.
- `PotatoTechMixinPlugin` skips Catalyst energy mixins when Catalyst Energy is absent.
- Do not add direct Catalyst references to always-loaded classes unless the dependency setup guarantees safe loading.
- Energy is displayed to players as `PE`; rates use `PE/t`.

### BTWaila

- BTWaila is optional and registered through the `btwaila` Fabric entrypoint in `fabric.mod.json`.
- Plugin: `compat/btwaila/PotatoTechBTWailaPlugin.java`.
- Providers use `TileTooltip<T>` and register through `TooltipRegistry`.
- Tooltip rendering runs client-side against synced tile-entity data. Do not mutate world or inventory state from a tooltip.
- Use a fixed `ProgressBarOptions` width of `152`; the default zero width can overrun the HUD panel.
- `BlockLogicEnergyConnector.getBreakResult(...)` may be called by BTWaila with a null tile entity. It must remain null-safe and read-only. Connection cleanup belongs in `onRemoved()`.

### Signal Industries

- Signal Industries provides its own BTWaila integration. Do not duplicate its block tooltips.
- Reference source is available at `C:\Users\samuel\AppData\Local\Temp\opencode\signalindustries` during this workspace session.
- Its crusher output is slot `1`; item IO routing must use Catalyst's configured active slot rather than assuming machine slots.

## Style And Safety

- Prefer minimal changes that preserve existing behavior.
- Do not remove or overwrite user changes in a dirty worktree.
- Use `apply_patch` for source and resource edits.
- Keep edited text ASCII unless the target file already needs another character set.
- Avoid logging every transfer/tick. Pipe logic runs frequently.
- Preserve optional-mod behavior: the game must start without Catalyst, BTWaila, or Signal Industries installed.
- After changing recipes, resources, or `fabric.mod.json`, run `processResources` in addition to compilation.
