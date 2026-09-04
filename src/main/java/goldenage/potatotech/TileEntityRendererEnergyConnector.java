package goldenage.potatotech;

import goldenage.potatotech.blocks.entities.TileEntityEnergyConnector;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TileEntityRendererEnergyConnector extends TileEntityRenderer<TileEntityEnergyConnector> {

	@Override
	public void onWorldChanged(World world) {
		super.onWorldChanged(world);
	}

	public void doRender(TessellatorGeneral tessellator, TileEntityEnergyConnector tileEntity, double x, double y, double z, float g) {
		if (tileEntity.connections.isEmpty()) return;

		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.COLOR_WORLD);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);
		GLRenderer.globalSetLightEnabled(false);
		GLRenderer.modelM4f().translate((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);

		for (TileEntityEnergyConnector.Connection c : tileEntity.connections){
			double x2 = c.x - tileEntity.tilePos.x;
			double y2 = c.y - tileEntity.tilePos.y;
			double z2 = c.z - tileEntity.tilePos.z;

			if (x2 > 0 || x2 == 0 && y2 > 0 || x2 == 0 && y2 == 0 && z2 > 0) continue;

			double dist = Math.sqrt(x2*x2 + y2*y2 + z2*z2);
			double yOff = Math.log(dist + 0.15);

			double t_increment = 0.25 / dist;
			List<Vector3f> points = new ArrayList<>();
			for (double t = 0.0; t <= 1.0; t += t_increment) {
				double clampedT = Math.min(t, 1.0);
				double tx = clampedT * x2;
				double ty = clampedT * y2;
				double tz = clampedT * z2;

				double yOffset = (-0.8 * ((clampedT - 0.5) * (clampedT - 0.5)) + 0.2f) * yOff;
				points.add(new Vector3f((float) tx, (float) (ty - yOffset), (float) tz));
			}
			if (points.get(points.size() - 1).distanceSquared((float) x2, (float) (y2 - (-0.8 * 0.25 + 0.2f) * yOff), (float) z2) > 0.0001f) {
				points.add(new Vector3f((float) x2, (float) (y2 - (-0.8 * 0.25 + 0.2f) * yOff), (float) z2));
			}
			float[] colors = c.wireType.getColors();
			Util.draw3dTube(tessellator, 0.05, points, colors[0], colors[1], colors[2], colors[3], colors[4], colors[5]);
		}

		GLRenderer.popFrame();
	}
}
