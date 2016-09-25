package org.ninthworld.marchingcubes.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.ninthworld.marchingcubes.shaders.SimpleFXShader;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class SimpleFXRenderer {

    private ImageRenderer renderer;
    private SimpleFXShader simpleFXShader;

    public SimpleFXRenderer() {
        simpleFXShader = new SimpleFXShader();
        renderer = new ImageRenderer();
    }

    public void renderNone(int colorTexture1){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture1);

        simpleFXShader.start();
        simpleFXShader.loadEffect(0);
        simpleFXShader.connectTextureUnits();
        renderer.renderQuad();
        simpleFXShader.stop();
    }

    public void renderAdd(int colorTexture1, int depthTexture1, int colorTexture2, int depthTexture2){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture2);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture2);

        simpleFXShader.start();
        simpleFXShader.loadEffect(1);
        simpleFXShader.connectTextureUnits();
        renderer.renderQuad();
        simpleFXShader.stop();
    }

    public void cleanUp(){
        simpleFXShader.cleanUp();
        renderer.cleanUp();
    }
}
