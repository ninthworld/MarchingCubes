package org.ninthworld.marchingcubes.shaders;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class LinearizeDepthFXShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/linearizeDepth/linearizeDepth.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/linearizeDepth/linearizeDepth.frag";

	private int location_depthTexture;

	public LinearizeDepthFXShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_depthTexture = super.getUniformLocation("depthTexture");
	}

	public void connectTextureUnits(){
		super.loadInteger(location_depthTexture, 0);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
