package goldenage.potatotech.blocks.models;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

public class BlockModelConnector<T extends BlockLogic> extends BlockModelGeneric<T> {
	private final float red;
	private final float green;
	private final float blue;

	public BlockModelConnector(Block<T> block) {
		this(block, 0.41f, 0.23f, 0.18f);
	}

	public BlockModelConnector(Block<T> block, float red, float green, float blue) {
		super(block, BlockModelDispatcher.loadDataModel("potatotech:block/energy_connector").asModel());
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public boolean renderAttached(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos, boolean cullFaces, @Nullable IconCoordinate overrideTexture) {
		int meta = worldSource.getBlockData(tilePos);
		Direction direction = Direction.fromId(meta & 7);

		StaticBlockModel model = this.getModel(worldSource, tilePos);
		// The 8.0 connector texture is a white mask. Apply the legacy connector tint.
		tessellator.setColorOpaque3f(red, green, blue);

		// The JSON model extends along +Z (South) from z=0 to z=9.
		// The base (z=0) must point toward the block this connector was placed on.
		// Metadata stores side.direction.id, but the model is oriented so we need the
		// opposite rotation to make the connector point toward the block it was placed on.
		//   UP(1)    -> placed on top    -> extend UP    (+Y) -> -90° X -> 3,0,0
		//   DOWN(0)  -> placed on bottom -> extend DOWN  (-Y) -> +90° X -> 1,0,0
		//   NORTH(2) -> placed on north  -> extend NORTH (-Z) -> 180° Y -> 0,2,0
		//   SOUTH(3) -> placed on south  -> extend SOUTH (+Z) ->   0°   -> 0,0,0
		//   WEST(4)  -> placed on west   -> extend WEST  (-X) -> -90° Y -> 0,3,0
		//   EAST(5)  -> placed on east   -> extend EAST  (+X) -> +90° Y -> 0,1,0
		switch (direction) {
			case UP -> model.renderAttached(this, tessellator, worldSource, tilePos, 3, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
			case DOWN -> model.renderAttached(this, tessellator, worldSource, tilePos, 1, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
			case NORTH -> model.renderAttached(this, tessellator, worldSource, tilePos, 0, 2, 0, 0, 0, 0, false, cullFaces, overrideTexture);
			case SOUTH -> model.renderAttached(this, tessellator, worldSource, tilePos, 0, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
			case WEST -> model.renderAttached(this, tessellator, worldSource, tilePos, 0, 3, 0, 0, 0, 0, false, cullFaces, overrideTexture);
			case EAST -> model.renderAttached(this, tessellator, worldSource, tilePos, 0, 1, 0, 0, 0, 0, false, cullFaces, overrideTexture);
			default -> model.renderAttached(this, tessellator, worldSource, tilePos, 0, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
		}
		tessellator.setColorOpaque3f(1.0f, 1.0f, 1.0f);

		return true;
	}
}
