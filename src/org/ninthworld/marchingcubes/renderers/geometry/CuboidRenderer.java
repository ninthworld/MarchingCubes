package org.ninthworld.marchingcubes.renderers.geometry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.ninthworld.marchingcubes.entities.*;
import org.ninthworld.marchingcubes.helper.MatrixHelper;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.shaders.CuboidShader;
import org.ninthworld.marchingcubes.shaders.PlaneShader;

import java.util.List;

/**
 * Created by NinthWorld on 9/25/2016.
 */
public class CuboidRenderer {

    private CuboidShader cuboidShader;

    public CuboidRenderer(Loader loader, Matrix4f projectionMatrix){
        cuboidShader = new CuboidShader();
        cuboidShader.start();
        cuboidShader.loadProjectionMatrix(projectionMatrix);
        cuboidShader.stop();
    }

    public void cleanUp(){
        cuboidShader.cleanUp();
    }

    public void render(List<CuboidEntity> cuboidEntities, LightEntity light, CameraEntity camera){
        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0, 0, 0, 0);

        cuboidShader.start();
        cuboidShader.loadLight(light);
        cuboidShader.loadViewMatrix(camera);
        renderEntities(cuboidEntities, cuboidShader);
        cuboidShader.stop();

    }

    private void renderEntities(List<CuboidEntity> cuboidEntities, CuboidShader shader){
        for(CuboidEntity cuboidEntity : cuboidEntities){
            prepareRawModel(cuboidEntity.getRawModel());
            prepareEntity(cuboidEntity, shader);
            GL11.glDrawElements(GL11.GL_LINE_LOOP, cuboidEntity.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindRawModel();
        }
    }

    private void prepareEntity(ModelEntity modelEntity, CuboidShader shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(modelEntity.getPosition(), modelEntity.getRotation(), modelEntity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }

    private void prepareRawModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    private void unbindRawModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
}
