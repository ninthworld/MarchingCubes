package org.ninthworld.marchingcubes.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.ninthworld.marchingcubes.shaders.NoneShader;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class NoneEffect {

    private ImageRenderer renderer;
    private NoneShader noneShader;

    public NoneEffect() {
        noneShader = new NoneShader();
        renderer = new ImageRenderer();
    }

    public void render(int colorTexture){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);

        noneShader.start();
        noneShader.connectTextureUnits();
        renderer.renderQuad();
        noneShader.stop();
    }

    public void cleanUp(){
        noneShader.cleanUp();
        renderer.cleanUp();
    }
}
