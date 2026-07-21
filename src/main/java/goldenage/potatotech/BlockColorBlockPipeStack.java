package goldenage.potatotech;

import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockColorBlockPipeStack extends BlockColor {
		public BlockColorBlockPipeStack() {
		}

	@Override
	public int getFallbackColor(int i, int i1) {
		int color = i;
		return color;
	}

	@Override
	public int getWorldColor(@NotNull WorldSource worldSource, @NotNull TilePosc tilePosc, int i) {
		int color = 0;
		color |= 0xff;
		color |= 0xff << 8;
		color |= 0xff << 16;
		color |= 0xff << 24;
		return color;
	}
}
