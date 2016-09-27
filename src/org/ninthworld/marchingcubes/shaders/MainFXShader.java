package org.ninthworld.marchingcubes.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class MainFXShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/mainfx.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/mainfx.frag";

	private int location_screenSize;

	private static final int textureCount = 16;
	private int[] location_textures;

	private int[] location_samples;
	private int location_invProjectionMatrix;

	public MainFXShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_screenSize = super.getUniformLocation("screenSize");

		location_textures = new int[textureCount];
		for(int i=0; i<location_textures.length; i++){
			location_textures[i] = super.getUniformLocation("textures[" + i + "]");
		}

		location_invProjectionMatrix = super.getUniformLocation("invProjectionMatrix");
		location_samples = new int[16];
		for(int i=0; i<location_samples.length; i++) {
			location_samples[i] = super.getUniformLocation("samples[" + i + "]");
		}
	}

	public void connectTextureUnits(){
		for(int i=0; i<location_textures.length; i++) {
			super.loadInteger(location_textures[i], i);
		}
	}

	public void loadSamples(Vector2f[] samples){
		for(int i=0; i<samples.length; i++) {
            super.loadVector2f(location_samples[i], samples[i]);
		}
	}

	public void loadInvProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_invProjectionMatrix, projection);
	}

	public void loadScreenSize(Vector2f screenSize){
		super.loadVector2f(location_screenSize, screenSize);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
