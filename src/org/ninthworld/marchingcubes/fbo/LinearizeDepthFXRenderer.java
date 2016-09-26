package org.ninthworld.marchingcubes.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.ninthworld.marchingcubes.shaders.CombineFXShader;
import org.ninthworld.marchingcubes.shaders.LinearizeDepthFXShader;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class LinearizeDepthFXRenderer {

    private ImageRenderer renderer;
    private LinearizeDepthFXShader linearizeDepthFXShader;

    public LinearizeDepthFXRenderer() {
        linearizeDepthFXShader = new LinearizeDepthFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int depthTexture){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);

        linearizeDepthFXShader.start();
        linearizeDepthFXShader.connectTextureUnits();
        renderer.renderQuad();
        linearizeDepthFXShader.stop();
    }

    public void cleanUp(){
        linearizeDepthFXShader.cleanUp();
        renderer.cleanUp();
    }
}
