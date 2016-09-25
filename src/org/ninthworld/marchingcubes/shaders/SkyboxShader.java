package org.ninthworld.marchingcubes.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.marchingcubes.entities.CameraEntity;
import org.ninthworld.marchingcubes.helper.MatrixHelper;

/**
 * Created by NinthWorld on 9/24/2016.
 */
public class SkyboxShader extends AbstractShader {

    private static final String VERTEX_FILE = "/shaders/skybox/skybox.vert";
    private static final String FRAGMENT_FILE = "/shaders/skybox/skybox.frag";

    private int location_projectionMatrix;
    private int location_viewMatrix;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(CameraEntity camera){
        Matrix4f matrix = MatrixHelper.createViewMatrix(camera);
        matrix.m30 = matrix.m31 = matrix.m32 = 0;
        super.loadMatrix(location_viewMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
