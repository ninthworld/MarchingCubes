package org.ninthworld.marchingcubes.renderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.*;
import org.ninthworld.marchingcubes.helper.MatrixHelper;
import org.ninthworld.marchingcubes.helper.ProjectionMatrix;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.shaders.*;

import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 9/25/2016.
 */
public class AsteroidRenderer {

    private static final String texture0 = "/textures/texture1.png";
    private static final String texture1 = "/textures/texture2.png";

    private static final String normal0 = "/textures/normal1.png";
    private static final String normal1 = "/textures/normal1.png";

    private AsteroidShader asteroidShader;

    private int texture0Id;
    private int texture1Id;

    private int normal0Id;
    private int normal1Id;

    public AsteroidRenderer(Loader loader, Matrix4f projectionMatrix){
        texture0Id = loader.loadTexture(texture0);
        texture1Id = loader.loadTexture(texture1);

        normal0Id = loader.loadTexture(normal0);
        normal1Id = loader.loadTexture(normal1);

        asteroidShader = new AsteroidShader();
        asteroidShader.start();
        asteroidShader.loadProjectionMatrix(projectionMatrix);
        asteroidShader.stop();
    }

    public void cleanUp(){
        asteroidShader.cleanUp();
    }

    public void render(List<AsteroidEntity> asteroidEntities, LightEntity light, CameraEntity camera){
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0, 0, 0, 0);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT_FACE);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0Id);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture1Id);
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal0Id);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal1Id);

        asteroidShader.start();
        asteroidShader.connectTextureUnits();
        asteroidShader.loadLight(light);
        asteroidShader.loadViewMatrix(camera);
        renderEntities(asteroidEntities, asteroidShader);
        asteroidShader.stop();
    }

    private void renderEntities(List<AsteroidEntity> asteroidEntities, AsteroidShader shader){
       for(AsteroidEntity asteroidEntity : asteroidEntities){
            prepareRawModel(asteroidEntity.getRawModel());
            prepareEntity(asteroidEntity, shader);
            GL11.glDrawElements(GL11.GL_TRIANGLES, asteroidEntity.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindRawModel();
        }
    }

    private void prepareEntity(AsteroidEntity asteroidEntity, AsteroidShader shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(asteroidEntity.getDrawPosition(), asteroidEntity.getRotation(), asteroidEntity.getScale());
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
