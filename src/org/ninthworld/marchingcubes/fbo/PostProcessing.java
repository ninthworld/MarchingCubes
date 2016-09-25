package org.ninthworld.marchingcubes.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.models.Loader;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;

	private static SimpleFXRenderer simpleFXRenderer;
    private static OutlineFXRenderer outlineFXRenderer;

	public static void init(Loader loader){
		quad = loader.loadToVao(POSITIONS, 2);
        simpleFXRenderer = new SimpleFXRenderer();
        outlineFXRenderer = new OutlineFXRenderer();
	}
	
	public static void doPostProcessingSimpleNone(int colorTexture) {
        start();
        simpleFXRenderer.renderNone(colorTexture);
        end();
    }

    public static void doPostProcessingSimpleAdd(int colorTexture1, int depthTexture1, int colorTexture2, int depthTexture2) {
        start();
        simpleFXRenderer.renderAdd(colorTexture1, depthTexture1, colorTexture2, depthTexture2);
        end();
    }

    public static void doPostProcessingOutline(int colorTexture, int depthTexture) {
        start();
        outlineFXRenderer.render(colorTexture, depthTexture);
        end();
    }
	
	public static void cleanUp(){
        simpleFXRenderer.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}



}
