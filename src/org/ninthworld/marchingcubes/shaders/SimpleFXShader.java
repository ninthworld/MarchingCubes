package org.ninthworld.marchingcubes.shaders;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class SimpleFXShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/simple/simple.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/simple/simple.frag";

	private int location_colorTexture1;
    private int location_depthTexture1;
	private int location_colorTexture2;
    private int location_depthTexture2;

	private int location_effectIndex;

	public SimpleFXShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colorTexture1 = super.getUniformLocation("colorTexture1");
        location_depthTexture1 = super.getUniformLocation("depthTexture1");
		location_colorTexture2 = super.getUniformLocation("colorTexture2");
        location_depthTexture2 = super.getUniformLocation("depthTexture2");

		location_effectIndex = super.getUniformLocation("effectIndex");
	}

	public void connectTextureUnits(){
		super.loadInteger(location_colorTexture1, 0);
		super.loadInteger(location_depthTexture1, 1);
        super.loadInteger(location_colorTexture2, 2);
        super.loadInteger(location_depthTexture2, 3);
	}

	public void loadEffect(int effectIndex){
		super.loadInteger(location_effectIndex, effectIndex);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
