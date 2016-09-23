package org.ninthworld.marchingcubes.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.marchingcubes.entities.CameraEntity;
import org.ninthworld.marchingcubes.entities.LightEntity;
import org.ninthworld.marchingcubes.helper.MatrixHelper;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class MainShader extends AbstractShader {

    private static final String VERTEX_FILE = "/shaders/main/main.vs";
    private static final String FRAGMENT_FILE = "/shaders/main/main.fs";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    private int location_lightPosition;
    private int location_lightColor;

    public MainShader(){
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes(){
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "color");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");

        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColor = super.getUniformLocation("lightColor");
    }

    public void loadLight(LightEntity light){
        super.loadVector3f(location_lightPosition, light.getPosition());
        super.loadVector3f(location_lightColor, light.getColor());
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadViewMatrix(CameraEntity camera){
        Matrix4f viewMatrix = MatrixHelper.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
