package goldenage.potatotech;

import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public abstract class BlockColorBlockPipeStack extends BlockColor {
		public BlockColorBlockPipeStack() {
		}

		@Override
		public int getFallbackColor(int var1, int var2) {
			int color = var1 + var2;
			return color;
		}

		@Override
		public int getWorldColor(@NotNull WorldSource var1, @NotNull TilePosc var2, int var3) {
			int color = 0;
			color |= 0xff;
			color |= 0xff << 8;
			color |= 0xff << 16;
			color |= 0xff << 24;
			return color;
		}

}
