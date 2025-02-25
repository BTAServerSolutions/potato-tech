package goldenage.potatotech;

import net.minecraft.client.render.tessellator.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Util {
    public static double[] crossProduct(double[] v0, double[] v1) {
        double[] crossProduct = new double[3];
        crossProduct[0] = v0[1] * v1[2] - v0[2] * v1[1];
        crossProduct[1] = v0[2] * v1[0] - v0[0] * v1[2];
        crossProduct[2] = v0[0] * v1[1] - v0[1] * v1[0];
        return crossProduct;
    }

    public static void normalize(double[] v) {
        double len = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2] * v[2]);
        v[0] /= len;
        v[1] /= len;
        v[2] /= len;
    }

    public static void draw3dLine(double width, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b) {

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Tessellator tessellator = Tessellator.instance;
        double l = Math.sqrt(x2*x2 + y2*y2 + z2*x2);

        Vector3f norm = new Vector3f((float)(x2 - x1), (float) (y2 - y1), (float)(z2 - z1));
        norm.normalise(norm);

        Vector3f perp = new Vector3f(1, 0, 0);
        if (Math.abs(norm.x) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 0.0f;
            perp.z = 1.0f;
        } else if (Math.abs(norm.z) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 1.0f;
            perp.z = 0.0f;
        }

        Vector3f up = new Vector3f(0, 0, 0) ;
        Vector3f.cross(norm, perp, up);
        up.normalise(up);

        Vector3f right = new Vector3f();
        Vector3f.cross(norm, up, right);

        up.x *= (float) (width * 0.5);
        up.y *= (float) (width * 0.5);
        up.z *= (float) (width * 0.5);

        right.x *= (float) (width * 0.5);
        right.y *= (float) (width * 0.5);
        right.z *= (float) (width * 0.5);

        tessellator.startDrawing(GL11.GL_QUADS);

        GL11.glColor4f(r, g, b, 1);

        tessellator.setNormal(-right.x, -right.y, -right.z);
        tessellator.addVertex(x1 - up.x - right.x, y1 - up.y - right.y, z1 - up.z - right.z);
        tessellator.addVertex(x1 + up.x - right.x, y1 + up.y - right.y, z1 + up.z - right.z);
        tessellator.addVertex(x2 + up.x - right.x, y2 + up.y - right.y, z2 + up.z - right.z);
        tessellator.addVertex(x2 - up.x - right.x, y2 - up.y - right.y, z2 - up.z - right.z);

        tessellator.setNormal(right.x, right.y, right.z);
        tessellator.addVertex(x1 - up.x + right.x, y1 - up.y + right.y, z1 - up.z + right.z);
        tessellator.addVertex(x2 - up.x + right.x, y2 - up.y + right.y, z2 - up.z + right.z);
        tessellator.addVertex(x2 + up.x + right.x, y2 + up.y + right.y, z2 + up.z + right.z);
        tessellator.addVertex(x1 + up.x + right.x, y1 + up.y + right.y, z1 + up.z + right.z);

        tessellator.setNormal(up.x, up.y, up.z);
        tessellator.addVertex(x1 - right.x + up.x, y1 - right.y + up.y, z1 - right.z + up.z);
        tessellator.addVertex(x1 + right.x + up.x, y1 + right.y + up.y, z1 + right.z + up.z);
        tessellator.addVertex(x2 + right.x + up.x, y2 + right.y + up.y, z2 + right.z + up.z);
        tessellator.addVertex(x2 - right.x + up.x, y2 - right.y + up.y, z2 - right.z + up.z);

        tessellator.setNormal(-up.x, -up.y, -up.z);
        tessellator.addVertex(x1 - right.x - up.x, y1 - right.y - up.y, z1 - right.z - up.z);
        tessellator.addVertex(x2 - right.x - up.x, y2 - right.y - up.y, z2 - right.z - up.z);
        tessellator.addVertex(x2 + right.x - up.x, y2 + right.y - up.y, z2 + right.z - up.z);
        tessellator.addVertex(x1 + right.x - up.x, y1 + right.y - up.y, z1 + right.z - up.z);

        tessellator.draw();

        GL11.glPopAttrib();
    }
    public static void draw3dLineWithTexture(int textureId, double width, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b) {

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, 1);

        Tessellator tessellator = Tessellator.instance;
        double l = Math.sqrt(x2*x2 + y2*y2 + z2*x2);

        Vector3f norm = new Vector3f((float)(x2 - x1), (float) (y2 - y1), (float)(z2 - z1));
        norm.normalise(norm);

        Vector3f perp = new Vector3f(1, 0, 0);
        if (Math.abs(norm.x) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 0.0f;
            perp.z = 1.0f;
        } else if (Math.abs(norm.z) > 0.9f) {
            perp.x = 0.0f;
            perp.y = 1.0f;
            perp.z = 0.0f;
        }

        Vector3f up = new Vector3f(0, 0, 0) ;
        Vector3f.cross(norm, perp, up);
        up.normalise(up);

        Vector3f right = new Vector3f();
        Vector3f.cross(norm, up, right);

        up.x *= (float) (width * 0.5);
        up.y *= (float) (width * 0.5);
        up.z *= (float) (width * 0.5);

        right.x *= (float) (width * 0.5);
        right.y *= (float) (width * 0.5);
        right.z *= (float) (width * 0.5);

        tessellator.startDrawing(GL11.GL_QUADS);

        tessellator.addVertex(x1 - up.x - right.x, y1 - up.y - right.y, z1 - up.z - right.z);
        tessellator.addVertex(x1 + up.x - right.x, y1 + up.y - right.y, z1 + up.z - right.z);
        tessellator.addVertex(x2 + up.x - right.x, y2 + up.y - right.y, z2 + up.z - right.z);
        tessellator.addVertex(x2 - up.x - right.x, y2 - up.y - right.y, z2 - up.z - right.z);

        tessellator.addVertex(x1 - up.x  + right.x, y1 - up.y + right.y, z1 - up.z + right.z);
        tessellator.addVertex(x2 - up.x  + right.x, y2 - up.y + right.y, z2 - up.z + right.z);
        tessellator.addVertex(x2 + up.x  + right.x, y2 + up.y + right.y, z2 + up.z + right.z);
        tessellator.addVertex(x1 + up.x  + right.x, y1 + up.y + right.y, z1 + up.z + right.z);

        tessellator.addVertex(x1 - right.x + up.x, y1 - right.y + up.y, z1 - right.z + up.z);
        tessellator.addVertex(x1 + right.x + up.x, y1 + right.y + up.y, z1 + right.z + up.z);
        tessellator.addVertex(x2 + right.x + up.x, y2 + right.y + up.y, z2 + right.z + up.z);
        tessellator.addVertex(x2 - right.x + up.x, y2 - right.y + up.y, z2 - right.z + up.z);

        tessellator.addVertex(x1 - right.x - up.x, y1 - right.y - up.y, z1 - right.z - up.z);
        tessellator.addVertex(x2 - right.x - up.x, y2 - right.y - up.y, z2 - right.z - up.z);
        tessellator.addVertex(x2 + right.x - up.x, y2 + right.y - up.y, z2 + right.z - up.z);
        tessellator.addVertex(x1 + right.x - up.x, y1 + right.y - up.y, z1 + right.z - up.z);

        tessellator.draw();

        GL11.glPopAttrib();
    }
}
