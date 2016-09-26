package org.ninthworld.marchingcubes.fbo;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.marchingcubes.shaders.OutlineFXShader;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class OutlineFXRenderer {

    private ImageRenderer renderer;
    private OutlineFXShader outlineFXShader;

    public OutlineFXRenderer() {
        outlineFXShader = new OutlineFXShader();
        renderer = new ImageRenderer();

        outlineFXShader.start();
        outlineFXShader.loadScreenSize(new Vector2f(Display.getWidth(), Display.getHeight()));
        outlineFXShader.stop();
    }

    public void render(int colorTexture, int depthTexture, Vector4f borderColor, int borderSize, float borderThreshold){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);

        outlineFXShader.start();
        outlineFXShader.connectTextureUnits();
        outlineFXShader.loadBorderColor(borderColor);
        outlineFXShader.loadBorderSize(borderSize);
        outlineFXShader.loadBorderThreshold(borderThreshold);
        renderer.renderQuad();
        outlineFXShader.stop();
    }

    public void cleanUp(){
        outlineFXShader.cleanUp();
        renderer.cleanUp();
    }
}
