package goldenage.potatotech.blocks.models;

import goldenage.potatotech.blocks.entities.TileEntityPipe;
import goldenage.potatotech.blocks.entities.TileEntityChute;
import goldenage.potatotech.compat.catalyst.CatalystItemIoCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.renderer.DrawMode;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tessellator.RenderBuffer;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

public class BlockModelPipe<T extends BlockLogic> extends BlockModelGeneric<T> {

	public final StaticBlockModel arm;
	public final StaticBlockModel armInsert;
	public final StaticBlockModel armExtract;

	private static final int[][] OFFSETS = {
		{ 0, -1,  0},
		{ 0,  1,  0},
		{ 0,  0, -1},
		{ 0,  0,  1},
		{-1,  0,  0},
		{ 1,  0,  0},
	};

	private static final float[][] COLORS = {
		{1.0f, 1.0f, 1.0f},
		{0.05f, 0.05f, 0.05f},
		{1.0f, 0.0f, 0.0f},
		{0.1f, 0.8f, 0.1f},
		{0.4f, 0.2f, 0.1f},
		{0.0f, 0.0f, 1.0f},
		{0.7f, 0.0f, 1.0f},
		{0.0f, 0.8f, 1.0f},
		{0.7f, 0.7f, 0.7f},
		{0.2f, 0.2f, 0.2f},
		{1.2f, 0.7f, 0.7f},
		{0.2f, 1.5f, 0.2f},
		{1.0f, 1.5f, 0.0f},
		{0.5f, 0.5f, 1.0f},
		{1.0f, 0.2f, 1.0f},
		{1.0f, 0.7f, 0.0f},
		{5.0f, 5.0f, 5.0f},
		{0.79f, 0.19f, 0.29f},
		{0.43f, 0.12f, 0.11f},
		{0.64f, 0.73f, 0.66f},
		{0.56f, 0.55f, 0.17f},
		{0.78f, 0.52f, 0.25f},
		{1.00f, 0.76f, 0.46f},
		{0.38f, 0.88f, 0.76f},
		{0.92f, 0.89f, 0.56f},
		{0.31f, 0.24f, 0.84f},
		{0.82f, 0.99f, 0.17f},
		{0.76f, 0.41f, 0.20f},
		{0.18f, 0.15f, 0.51f},
		{0.45f, 0.18f, 0.54f},
		{0.24f, 0.54f, 0.44f}
	};
	public BlockModelPipe(Block<T> block, String corePath, String armPath, String armInsertPath, String armExtractPath) {
		super(block, BlockModelDispatcher.loadDataModel(corePath).asModel());
		this.arm = BlockModelDispatcher.loadDataModel(armPath).asModel();
		this.armInsert = BlockModelDispatcher.loadDataModel(armInsertPath).asModel();
		this.armExtract = BlockModelDispatcher.loadDataModel(armExtractPath).asModel();
	}

	@Override
	public boolean renderAttached(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos, boolean cullFaces, @Nullable IconCoordinate overrideTexture) {
		TileEntity tileEntity = worldSource.getTileEntity(tilePos);

		if (tileEntity instanceof TileEntityPipe pipe) {
			this.getModel(worldSource, tilePos).renderAttached(this, tessellator, worldSource, tilePos, 0, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);

			for (int i = 0; i < 6; i++) {
				if (pipe.modeBySide[i] == 3) continue;

				int nx = tilePos.x() + OFFSETS[i][0];
				int ny = tilePos.y() + OFFSETS[i][1];
				int nz = tilePos.z() + OFFSETS[i][2];

				boolean shouldConnect = false;
				TileEntity neighborTe = worldSource.getTileEntity(nx, ny, nz);
				if (neighborTe != null) {
					if (neighborTe instanceof TileEntityPipe neighborPipe) {
						int opposite = Direction.fromId(i).opposite().id;
						if (neighborPipe.modeBySide[opposite] != 3) {
							shouldConnect = true;
						}
					} else if (neighborTe instanceof TileEntityChute) {
						shouldConnect = true;
					} else if (neighborTe instanceof Container) {
						if (FabricLoader.getInstance().isModLoaded("catalyst-core") && CatalystItemIoCompat.isItemIo(neighborTe)) {
							shouldConnect = CatalystItemIoCompat.hasConfiguredSide(neighborTe, Direction.fromId(i));
						} else {
							shouldConnect = true;
						}
					}
				}

				if (!shouldConnect) continue;

				StaticBlockModel modelToRender;
				if (pipe.modeBySide[i] == 1) {
					modelToRender = armInsert;
				} else if (pipe.modeBySide[i] == 2) {
					modelToRender = armExtract;
				} else {
					modelToRender = arm;
				}

				float[] color = COLORS[pipe.colorBySide[i]];
				TessellatorGeneral tintedTessellator = new DyeTintTessellator(tessellator, color[0], color[1], color[2]);
				switch (i) {
					case 0 -> modelToRender.renderAttached(this, tintedTessellator, worldSource, tilePos, 1, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					case 1 -> modelToRender.renderAttached(this, tintedTessellator, worldSource, tilePos, 3, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					case 2 -> modelToRender.renderAttached(this, tintedTessellator, worldSource, tilePos, 0, 2, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					case 3 -> modelToRender.renderAttached(this, tintedTessellator, worldSource, tilePos, 0, 0, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					case 4 -> modelToRender.renderAttached(this, tintedTessellator, worldSource, tilePos, 0, 3, 0, 0, 0, 0, false, cullFaces, overrideTexture);
					case 5 -> modelToRender.renderAttached(this, tintedTessellator, worldSource, tilePos, 0, 1, 0, 0, 0, 0, false, cullFaces, overrideTexture);
				}
			}
		}

		return true;
	}

	private record DyeTintTessellator(TessellatorGeneral delegate, float red, float green, float blue) implements TessellatorGeneral {
		@Override
		public void startDrawing(DrawMode drawMode) {
			delegate.startDrawing(drawMode);
		}

		@Override
		public void addVertex(double x, double y, double z) {
			delegate.addVertex(x, y, z);
		}

		@Override
		public void setTranslation(double x, double y, double z) {
			delegate.setTranslation(x, y, z);
		}

		@Override
		public void offsetTranslation(double x, double y, double z) {
			delegate.offsetTranslation(x, y, z);
		}

		@Override
		public void draw() {
			delegate.draw();
		}

		@Override
		public RenderBuffer record(int drawMode, int vertexCount) {
			return delegate.record(drawMode, vertexCount);
		}

		@Override
		public void setColorOpaque3f(float red, float green, float blue) {
			delegate.setColorOpaque3f(red * this.red, green * this.green, blue * this.blue);
		}

		@Override
		public void setColor1i(int color) {
			delegate.setColor1i(color);
		}

		@Override
		public void lockColor() {
			delegate.lockColor();
		}

		@Override
		public void setTextureUV(double u, double v) {
			delegate.setTextureUV(u, v);
		}

		@Override
		public void setLightmapCoord1i(int lightmapCoordinate) {
			delegate.setLightmapCoord1i(lightmapCoordinate);
		}

		@Override
		public void setNormal(float x, float y, float z) {
			delegate.setNormal(x, y, z);
		}

		@Override
		public void setShade1i(int shade) {
			delegate.setShade1i(shade);
		}
	}
}
