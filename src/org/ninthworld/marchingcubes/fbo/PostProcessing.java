package org.ninthworld.marchingcubes.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.marchingcubes.helper.ProjectionMatrix;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.models.Loader;

/**
 * Created by NinthWorld on 9/25/2016.
 */
public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;

	private static SimpleFXRenderer simpleFXRenderer;
    private static OutlineFXRenderer outlineFXRenderer;
	private static SSAOFXRenderer ssaoFXRenderer;
	private static CombineFXRenderer combineFXRenderer;
	private static LinearizeDepthFXRenderer linearizeDepthFXRenderer;

	public static void init(Loader loader){
		quad = loader.loadToVao(POSITIONS, 2);
        simpleFXRenderer = new SimpleFXRenderer();
        outlineFXRenderer = new OutlineFXRenderer();
		ssaoFXRenderer = new SSAOFXRenderer(ProjectionMatrix.create());
		combineFXRenderer = new CombineFXRenderer();
		linearizeDepthFXRenderer = new LinearizeDepthFXRenderer();
	}

	public static void doPostProcessingLinearizeDepth(int depthTexture){
		start();
		linearizeDepthFXRenderer.render(depthTexture);
		end();
	}

	public static void doPostProcessingCombine(int ... texture){
		int[] colorTextures = new int[texture.length/2];
		int[] depthTextures = new int[texture.length/2];

		for(int i=0; i<texture.length; i++){
			if(i%2 == 0) {
				colorTextures[(int) Math.floor(i/2)] = texture[i];
			}else{
				depthTextures[(int) Math.floor(i/2)] = texture[i];
			}
		}

		start();
		combineFXRenderer.render(colorTextures, depthTextures);
		end();
	}

	public static void doPostProcessingSimpleAlpha(int colorTexture1, int colorTexture2) {
        start();
        simpleFXRenderer.renderAddAlpha(colorTexture1, colorTexture2);
        end();
    }

    public static void doPostProcessingSimpleAdd(int colorTexture1, int depthTexture1, int colorTexture2, int depthTexture2) {
        start();
        simpleFXRenderer.renderAddDepth(colorTexture1, depthTexture1, colorTexture2, depthTexture2);
        end();
    }

    public static void doPostProcessingSimpleRenderDepth(int depthTexture1){
		start();
		simpleFXRenderer.renderDepthTexture(depthTexture1);
		end();
	}

    public static void doPostProcessingOutline(int colorTexture, int depthTexture, Vector4f borderColor, int borderSize, float borderThreshold) {
        start();
        outlineFXRenderer.render(colorTexture, depthTexture, borderColor, borderSize, borderThreshold);
        end();
    }

    public static void doPostProcessingSSAO(int colorTexture, int depthTexture, int normalTexture, int numSamples, float kRadius, float kDistanceThreshold){
		start();
		ssaoFXRenderer.render(colorTexture, depthTexture, normalTexture, numSamples, kRadius, kDistanceThreshold);
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
