package org.ninthworld.marchingcubes.renderers;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.LightEntity;
import org.ninthworld.marchingcubes.entities.ModelEntity;
import org.ninthworld.marchingcubes.helper.ProjectionMatrix;
import org.ninthworld.marchingcubes.entities.CameraEntity;
import org.ninthworld.marchingcubes.helper.MatrixHelper;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.shaders.MainShader;
import org.ninthworld.marchingcubes.shaders.AbstractShader;

import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 6/6/2016.
 */
public class MasterRenderer {

    // private static final Vector3f clearColor = new Vector3f(0.82f, 0.93f, 0.94f);
    private static final Vector3f clearColor = new Vector3f(0.0f, 0.0f, 0.0f);

    private MainShader mainShader;

    public MasterRenderer(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        //GL11.glShadeModel(GL11.GL_FLAT);

        mainShader = new MainShader();
        mainShader.start();
        mainShader.loadProjectionMatrix(ProjectionMatrix.create());
        mainShader.stop();
    }

    public void renderScene(Map<RawModel, List<ModelEntity>> entities, LightEntity light, CameraEntity camera){
        prepare();

        mainShader.start();
        mainShader.loadLight(light);
        mainShader.loadViewMatrix(camera);
        //mainShader.loadViewMatrix(new CameraEntity(new Vector3f(1000, 100, 1000), new Vector3f(0, 213, 0)));
        renderEntities(entities, mainShader);
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
        }
    }

    public void cleanUp(){
        mainShader.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
    }
}
