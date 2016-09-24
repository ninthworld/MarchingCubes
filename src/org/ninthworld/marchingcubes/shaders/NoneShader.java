package org.ninthworld.marchingcubes.shaders;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class NoneShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/none.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/none.frag";

	private int location_colorTexture;

	public NoneShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colorTexture = super.getUniformLocation("colorTexture");
	}

	public void connectTextureUnits(){
		super.loadInteger(location_colorTexture, 0);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
