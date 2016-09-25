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
import org.ninthworld.marchingcubes.shaders.PlaneShader;

import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class MasterRenderer {

    // private static final Vector3f clearColor = new Vector3f(0.82f, 0.93f, 0.94f);
    private static final Vector3f clearColor = new Vector3f(0.05f, 0.05f, 0.05f);

    private MainShader mainShader;
    private CuboidShader cuboidShader;
    private PlaneShader planeShader;

    private SkyboxRenderer skyboxRenderer;
    private AsteroidRenderer asteroidRenderer;

    public MasterRenderer(Loader loader){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        Matrix4f projectionMatrix = ProjectionMatrix.create();

        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        asteroidRenderer = new AsteroidRenderer(loader, projectionMatrix);

//        mainShader = new MainShader();
//        mainShader.start();
//        mainShader.loadProjectionMatrix(projectionMatrix);
//        mainShader.stop();

        cuboidShader = new CuboidShader();
        cuboidShader.start();
        cuboidShader.loadProjectionMatrix(projectionMatrix);
        cuboidShader.stop();

        planeShader = new PlaneShader();
        planeShader.start();
        planeShader.loadProjectionMatrix(projectionMatrix);
        planeShader.stop();
    }

    public void renderScene(Map<RawModel, List<ModelEntity>> entities, /*List<AsteroidEntity> asteroidEntities,*/ List<CuboidEntity> cuboidEntities, List<PlaneEntity> planeEntities, LightEntity light, CameraEntity camera){
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

        planeShader.start();
        planeShader.loadLight(light);
        planeShader.loadViewMatrix(camera);
        renderPlaneEntities(planeEntities, planeShader);
        planeShader.stop();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_POLYGON_MODE);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        //asteroidRenderer.renderAsteroids(asteroidEntities, light, camera);

//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0Id);
//        GL13.glActiveTexture(GL13.GL_TEXTURE1);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture1Id);
//        GL13.glActiveTexture(GL13.GL_TEXTURE2);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal0Id);
//        GL13.glActiveTexture(GL13.GL_TEXTURE3);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal1Id);
//
//        mainShader.start();
//        mainShader.connectTextureUnits();
//        mainShader.loadLight(light);
//        mainShader.loadViewMatrix(camera);
//        renderEntities(entities, mainShader);
//        renderAsteroidEntities(asteroidEntities, mainShader);
//        mainShader.stop();
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

    private void renderPlaneEntities(List<PlaneEntity> planeEntities, AbstractShader shader){
        for(PlaneEntity planeEntity : planeEntities){
            prepareRawModel(planeEntity.getRawModel());
            prepareEntity(planeEntity, shader);
            GL11.glDrawElements(GL11.GL_TRIANGLES, planeEntity.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
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
        }else if(shader instanceof PlaneShader){
            ((PlaneShader) shader).loadTransformationMatrix(transformationMatrix);
        }
    }

    public void cleanUp(){
        //mainShader.cleanUp();
        cuboidShader.cleanUp();
        skyboxRenderer.cleanUp();
        asteroidRenderer.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, 0);
    }
}
