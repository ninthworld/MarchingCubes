package org.ninthworld.marchingcubes.shaders;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class CombineFXShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/combine/combine.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/combine/combine.frag";

	public static final int textureCount = 8;

	private int location_numTextures;
	private int[] location_colorTextures;
	private int[] location_depthTextures;

	public CombineFXShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numTextures = super.getUniformLocation("numTextures");

		location_colorTextures = new int[textureCount];
		for(int i=0; i<location_colorTextures.length; i++) {
			location_colorTextures[i] = super.getUniformLocation("colorTextures[" + i + "]");
		}

		location_depthTextures = new int[textureCount];
		for(int i=0; i<location_depthTextures.length; i++) {
			location_depthTextures[i] = super.getUniformLocation("depthTextures[" + i + "]");
		}
	}

	public void connectTextureUnits(){
		for(int i=0; i<location_colorTextures.length; i++) {
			super.loadInteger(location_colorTextures[i], i);
		}

		for(int i=0; i<location_depthTextures.length; i++) {
			super.loadInteger(location_depthTextures[i], location_colorTextures.length + i);
		}
	}

	public void loadNumTextures(int numTextures){
		super.loadInteger(location_numTextures, numTextures);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
