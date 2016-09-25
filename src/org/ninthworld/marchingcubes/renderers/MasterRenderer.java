package org.ninthworld.marchingcubes.renderers;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.*;
import org.ninthworld.marchingcubes.helper.ProjectionMatrix;
import org.ninthworld.marchingcubes.helper.MatrixHelper;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.shaders.CuboidShader;
import org.ninthworld.marchingcubes.shaders.MainShader;
import org.ninthworld.marchingcubes.shaders.AbstractShader;

import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class MasterRenderer {

    private static final String texture0 = "/textures/texture0.png";
    private static final String texture1 = "/textures/texture1.png";

    private static final String normal0 = "/textures/normal0.png";
    private static final String normal1 = "/textures/normal1.png";

    // private static final Vector3f clearColor = new Vector3f(0.82f, 0.93f, 0.94f);
    private static final Vector3f clearColor = new Vector3f(0.05f, 0.05f, 0.05f);

    private MainShader mainShader;
    private CuboidShader cuboidShader;

    private SkyboxRenderer skyboxRenderer;

    private int texture0Id;
    private int texture1Id;

    private int normal0Id;
    private int normal1Id;

    public MasterRenderer(Loader loader){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        Matrix4f projectionMatrix = ProjectionMatrix.create();

        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);

        texture0Id = loader.loadTexture(texture0);
        texture1Id = loader.loadTexture(texture1);

        normal0Id = loader.loadTexture(normal0);
        normal1Id = loader.loadTexture(normal1);

        mainShader = new MainShader();
        mainShader.start();
        mainShader.loadProjectionMatrix(projectionMatrix);
        mainShader.stop();

        cuboidShader = new CuboidShader();
        cuboidShader.start();
        cuboidShader.loadProjectionMatrix(projectionMatrix);
        cuboidShader.stop();
    }

    public void renderScene(Map<RawModel, List<ModelEntity>> entities, List<AsteroidEntity> asteroidEntities, List<CuboidEntity> cuboidEntities, LightEntity light, CameraEntity camera){
        prepare();

        skyboxRenderer.renderSkybox(camera);

        GL11.glEnable(GL11.GL_POLYGON_MODE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glDisable(GL11.GL_CULL_FACE);

        cuboidShader.start();
        cuboidShader.loadLight(light);
        cuboidShader.loadViewMatrix(camera);
        renderCuboidEntities(cuboidEntities, cuboidShader);
        cuboidShader.stop();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_POLYGON_MODE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0Id);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture1Id);
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal0Id);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal1Id);

        mainShader.start();
        mainShader.connectTextureUnits();
        mainShader.loadLight(light);
        mainShader.loadViewMatrix(camera);
        renderEntities(entities, mainShader);
        renderAsteroidEntities(asteroidEntities, mainShader);
        mainShader.stop();
    }

    private void renderEntities(Map<RawModel, List<ModelEntity>> entities, AbstractShader shader){
        for(RawModel rawModel : entities.keySet()){
            prepareRawModel(rawModel);

            for(ModelEntity entity : entities.get(rawModel)){
                prepareEntity(entity, shader);
                GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            unbindRawModel();
        }
    }

    private void renderCuboidEntities(List<CuboidEntity> cuboidEntities, AbstractShader shader){
        for(CuboidEntity cuboidEntity : cuboidEntities){
            prepareRawModel(cuboidEntity.getRawModel());
            prepareEntity(cuboidEntity, shader);
            GL11.glDrawElements(GL11.GL_TRIANGLES, cuboidEntity.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindRawModel();
        }
    }

    private void renderAsteroidEntities(List<AsteroidEntity> asteroidEntities, AbstractShader shader){
       for(AsteroidEntity asteroidEntity : asteroidEntities){
            prepareRawModel(asteroidEntity.getRawModel());
            prepareEntity(asteroidEntity, shader);
            GL11.glDrawElements(GL11.GL_TRIANGLES, asteroidEntity.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindRawModel();
        }
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

    private void prepareEntity(ModelEntity modelEntity, AbstractShader shader) {
        Matrix4f transformationMatrix = MatrixHelper.createTransformationMatrix(modelEntity.getPosition(), modelEntity.getRotation(), modelEntity.getScale());

        if(shader instanceof MainShader){
            ((MainShader) shader).loadTransformationMatrix(transformationMatrix);
        }else if(shader instanceof CuboidShader){
            ((CuboidShader) shader).loadTransformationMatrix(transformationMatrix);
        }
    }

    public void cleanUp(){
        mainShader.cleanUp();
        cuboidShader.cleanUp();
        skyboxRenderer.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
    }
}
