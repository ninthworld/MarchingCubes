package org.ninthworld.marchingcubes.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.ninthworld.marchingcubes.shaders.CombineFXShader;
import org.ninthworld.marchingcubes.shaders.SimpleFXShader;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class CombineFXRenderer {

    private ImageRenderer renderer;
    private CombineFXShader combineFXShader;

    public CombineFXRenderer() {
        combineFXShader = new CombineFXShader();
        renderer = new ImageRenderer();
    }

    public void render(int[] colorTextures, int[] depthTextures){
        int numTextures = colorTextures.length;
        for(int i=0; i<colorTextures.length; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextures[i]);
        }

        for(int i=0; i<combineFXShader.textureCount - colorTextures.length; i++){
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + colorTextures.length + i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextures[colorTextures.length - 1]);
        }

        for(int i=0; i<colorTextures.length; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i + combineFXShader.textureCount);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextures[i]);
        }

        for(int i=0; i<combineFXShader.textureCount - depthTextures.length; i++){
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + depthTextures.length + i + combineFXShader.textureCount);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextures[depthTextures.length - 1]);
        }

        combineFXShader.start();
        combineFXShader.loadNumTextures(numTextures);
        combineFXShader.connectTextureUnits();
        renderer.renderQuad();
        combineFXShader.stop();
    }

    public void cleanUp(){
        combineFXShader.cleanUp();
        renderer.cleanUp();
    }
}
