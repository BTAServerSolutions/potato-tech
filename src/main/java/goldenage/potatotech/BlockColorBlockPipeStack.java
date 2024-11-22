package goldenage.potatotech;

import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.client.render.colorizer.Colorizer;
import net.minecraft.core.world.WorldSource;

public class BlockColorBlockPipeStack extends BlockColor {


		public BlockColorBlockPipeStack() {
		}

		@Override
		public int getFallbackColor(int meta) {
			int color = meta;
			return color;
		}

		@Override
		public int getWorldColor(WorldSource world, int x, int y, int z) {
			int color = 0;
			color |= 0xff;
			color |= 0xff << 8;
			color |= 0xff << 16;
			color |= 0xff << 24;
			return color;
		}
}
