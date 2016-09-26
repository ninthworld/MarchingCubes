package org.ninthworld.marchingcubes.shaders;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class OutlineFXShader extends AbstractShader {

	private static final String VERTEX_FILE = "/shaders/postprocessing/outline/outline.vert";
	private static final String FRAGMENT_FILE = "/shaders/postprocessing/outline/outline.frag";

	private int location_screenSize;

	private int location_colorTexture;
	private int location_depthTexture;

	private int location_borderColor;
	private int location_borderSize;
	private int location_borderThreshold;

	public OutlineFXShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_screenSize = super.getUniformLocation("screenSize");

		location_colorTexture = super.getUniformLocation("colorTexture");
		location_depthTexture = super.getUniformLocation("depthTexture");

		location_borderColor = super.getUniformLocation("borderColor");
		location_borderSize = super.getUniformLocation("borderSize");
		location_borderThreshold = super.getUniformLocation("borderThreshold");
	}

	public void connectTextureUnits(){
		super.loadInteger(location_colorTexture, 0);
		super.loadInteger(location_depthTexture, 1);
	}

	public void loadBorderColor(Vector4f borderColor){
		super.loadVector4f(location_borderColor, borderColor);
	}

	public void loadBorderSize(int borderSize){
		super.loadInteger(location_borderSize, borderSize);
	}

	public void loadBorderThreshold(float borderThreshold){
		super.loadFloat(location_borderThreshold, borderThreshold);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadScreenSize(Vector2f screenSize){
		super.loadVector2f(location_screenSize, screenSize);
	}
}
