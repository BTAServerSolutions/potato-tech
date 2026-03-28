# PotatoTech Agent Guide

## Scope

This repository is a Better than Adventure mod named Potato Logistics (`potatotech`). It targets BTA `7.3_04` and uses Halplibe plus Fabric/Loom tooling. Future agents should treat BTA 7.3 documentation as the primary external reference, especially the community docs at https://better-than-documentation.readthedocs.io/en/latest.

## Build And Runtime Constraints

- Java source compatibility is `8`.
- Gradle should run with JDK `21`.
- Build metadata uses `mod_name=potatologistics` while runtime IDs use `potatotech`; keep both values aligned with their current roles.
- The mod is wired through Fabric entrypoints and Halplibe entrypoints.
- Block and item IDs come from `PotatoTech.config`, not hardcoded scattered values.
- Keep changes aligned with BTA 7.3 APIs and patterns; do not assume modern Minecraft/Fabric behavior.

## Project Map

### Root entry points

- `src/main/java/goldenage/potatotech/PotatoTech.java` is the main mod initializer and game-start hook. It sets up tile entities, network messages, and config-backed ID ranges.
- `src/main/java/goldenage/potatotech/PTBlocks.java` registers blocks during the Halplibe block init phase.
- `src/main/java/goldenage/potatotech/PTItems.java` registers items during the Halplibe item init phase.
- `src/main/java/goldenage/potatotech/PTRecipes.java` registers recipes and recipe namespaces.
- `src/main/java/goldenage/potatotech/PotatoTechClient.java` handles client startup and keybinding/options wiring.
- `src/main/java/goldenage/potatotech/PotatoTechServer.java` handles server-specific initialization.
- `src/main/java/goldenage/potatotech/PTModels.java` handles the `initModels` phase for block/item/tile-entity model registration.

### Core packages

- `blocks/` contains block logic classes such as pipes, chute, filter, crafter, energy connector, stirling engine, and test-area utilities.
- `blocks/entities/` contains the tile entities that back the machine and pipe behavior.
- `blocks/models/` contains custom block model helpers.
- `items/` contains custom tools and utility items such as the wrench and wire spool.
- `screens/` contains GUI screens and container/menu classes for the filter and crafter.
- `networks/client/` contains client message handlers for opening GUIs.
- `mixins/` contains the small set of mixins used by the mod.
- Root utility classes such as `PipeStack`, `BlockColorBlockPipeStack`, `Util`, `PTModels`, and the renderer classes support the systems above.

### Resources

- `src/main/resources/fabric.mod.json` defines the Fabric and Halplibe entrypoints.
- `src/main/resources/potatotech.mixins.json` defines the mixin set.
- `src/main/resources/assets/potatotech/` holds textures and other asset data.
- `src/main/resources/lang/potatotech/` holds language entries.

## Entry Point Order To Remember

The mod metadata uses these entrypoints:

- `main`
- `afterBlockInit`
- `afterItemInit`
- `recipesReady`
- `client`
- `beforeClientStart`
- `afterClientStart`
- `beforeGameStart`
- `afterGameStart`
- `server`
- `initModels`

When making changes, check whether the logic belongs in the main initializer or one of the Halplibe phases rather than moving everything into a single class.

## Editing Conventions

- Prefer small, targeted changes over broad refactors.
- Preserve the existing package layout and naming style.
- Use Halplibe helpers already present in the codebase for block, item, recipe, network, and config registration.
- When adding content, update the matching language key and texture path together.
- Keep the `PTItems.itemTextures` -> `PTModels.initItemModels` flow intact when adding or renaming items.
- If a change touches blocks or items, verify the ID allocation still comes from the config-backed starting ranges.
- Keep recipe namespace handling reload-safe by following `PTRecipes.resetGroups()` + `registerNamespaces()` before loading entries.
- If a change touches GUIs or networked actions, check both client and server sides.
- Mixins should stay minimal and should be justified by a real BTA limitation or hook point.

## Files To Inspect First

1. `src/main/java/goldenage/potatotech/PotatoTech.java`
2. `src/main/java/goldenage/potatotech/PTBlocks.java`
3. `src/main/java/goldenage/potatotech/PTItems.java`
4. `src/main/java/goldenage/potatotech/PTRecipes.java`
5. `src/main/java/goldenage/potatotech/PTModels.java`
6. `src/main/resources/fabric.mod.json`
7. `src/main/resources/potatotech.mixins.json`

## BTA 7.3 References

Use these docs first when you need implementation guidance:

- Better than Documentation landing page: https://better-than-documentation.readthedocs.io/en/latest
- 7.3 changelog and feature docs under the `7.3/` section
- Mixins, blocks, items, recipes, GUI, and mod configuration sections in the BTA docs

## Verification

Use the existing Gradle tasks when validating changes:

- `build`
- `runClient`
- `runServer`

If a change is client-only or server-only, validate in the matching runtime rather than relying only on a full build.
