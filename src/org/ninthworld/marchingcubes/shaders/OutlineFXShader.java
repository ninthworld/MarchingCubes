package org.ninthworld.marchingcubes.shaders;

import org.lwjgl.util.vector.Vector2f;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class OutlineFXShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/outline/outline.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/outline/outline.frag";

	private int location_screenSize;

	private int location_colorTexture;
	private int location_depthTexture;

	public OutlineFXShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_screenSize = super.getUniformLocation("screenSize");

		location_colorTexture = super.getUniformLocation("colorTexture");
		location_depthTexture = super.getUniformLocation("depthTexture");
	}

	public void connectTextureUnits(){
		super.loadInteger(location_colorTexture, 0);
		super.loadInteger(location_depthTexture, 1);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadScreenSize(Vector2f screenSize){
		super.loadVector2f(location_screenSize, screenSize);
	}
}
