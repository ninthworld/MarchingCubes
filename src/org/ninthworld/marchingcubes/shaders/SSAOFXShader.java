package org.ninthworld.marchingcubes.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class SSAOFXShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/ssao/ssao.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/ssao/ssao.frag";

	private int location_colorTexture;
	private int location_depthTexture;
    private int location_normalTexture;

	private int[] location_samples;
	private int location_invProjectionMatrix;

	private int location_numSamples;
	private int location_kRadius;
	private int location_kDistanceThreshold;

	public SSAOFXShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colorTexture = super.getUniformLocation("colorTexture");
		location_depthTexture = super.getUniformLocation("depthTexture");
        location_normalTexture = super.getUniformLocation("normalTexture");

		location_invProjectionMatrix = super.getUniformLocation("invProjectionMatrix");
		location_samples = new int[16];
		for(int i=0; i<location_samples.length; i++) {
			location_samples[i] = super.getUniformLocation("samples[" + i + "]");
		}

		location_numSamples = super.getUniformLocation("numSamples");
		location_kRadius = super.getUniformLocation("kRadius");
		location_kDistanceThreshold = super.getUniformLocation("kDistanceThreshold");
	}

	public void loadNumSamples(int numSamples){
        super.loadInteger(location_numSamples, numSamples);
    }

    public void loadKRadius(float kRadius){
        super.loadFloat(location_kRadius, kRadius);
    }

    public void loadKDistanceThreshold(float kDistanceThreshold){
        super.loadFloat(location_kDistanceThreshold, kDistanceThreshold);
    }

	public void connectTextureUnits(){
		super.loadInteger(location_colorTexture, 0);
		super.loadInteger(location_depthTexture, 1);
        super.loadInteger(location_normalTexture, 2);
	}

	public void loadSamples(Vector2f[] samples){
		for(int i=0; i<samples.length; i++) {
            super.loadVector2f(location_samples[i], samples[i]);
		}
	}

	public void loadInvProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_invProjectionMatrix, projection);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
