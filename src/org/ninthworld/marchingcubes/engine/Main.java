package org.ninthworld.marchingcubes.engine;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.CameraEntity;
import org.ninthworld.marchingcubes.entities.LightEntity;
import org.ninthworld.marchingcubes.entities.ModelEntity;
import org.ninthworld.marchingcubes.fbo.Fbo;
import org.ninthworld.marchingcubes.fbo.PostProcessing;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.renderers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class Main {

    private Loader loader;

    private MasterRenderer masterRenderer;

    private LightEntity light;
    private CameraEntity camera;
    private Map<String, RawModel> rawModels;
    private Map<RawModel, List<ModelEntity>> modelEntities;

    private Fbo multisampleFbo;
    private Fbo outputFbo;

    public Main(){
        loader = new Loader();

        DisplayManager.createDisplay();
        PostProcessing.init(loader);

        light = new LightEntity(new Vector3f(1, 1, 1), new Vector3f(1, 1, 1));
        camera = new CameraEntity(new Vector3f(5, 5, 5));
        camera.setRotation(new Vector3f((float) Math.PI/3f, (float) -Math.PI/3f, 0f));

        rawModels = new HashMap<>();
        modelEntities = new HashMap<>();

        masterRenderer = new MasterRenderer();

        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

        setup();
    }

    private void setup(){

        float[] vertices = {
                0, 0, 0,
                1, 0, 0,
                1, 0, 1
        };

        float[] colors = {
                1, 1, 1,
                1, 1, 1,
                1, 1, 1
        };

        float[] normals = {
                0, 1, 0,
                0, 1, 0,
                0, 1, 0
        };

        int[] indices = {
                0, 1, 2
        };

        // Add Raw Model
        RawModel rawModel1 = loader.loadToVao(vertices, colors, normals, indices);

        // Add Raw Model to entities list
        ModelEntity modelEntity1 = new ModelEntity(rawModel1);

        List<ModelEntity> rawModel1EntityList = new ArrayList<>();
        rawModel1EntityList.add(modelEntity1);

        modelEntities.put(rawModel1, rawModel1EntityList);

        loop();
    }

    private void loop(){
        while(!Display.isCloseRequested()){
            camera.move();

            //multisampleFbo.bindFrameBuffer();
            masterRenderer.renderScene(modelEntities, light, camera);
            //multisampleFbo.unbindFrameBuffer();

            //multisampleFbo.resolveToFbo(outputFbo);
            //PostProcessing.doPostProcessing(outputFbo.getColorTexture());

            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    private void cleanUp(){
        masterRenderer.cleanUp();

        multisampleFbo.cleanUp();
        outputFbo.cleanUp();

        loader.cleanUp();

        PostProcessing.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args){
        System.setProperty("org.lwjgl.librarypath", new File("lib/native/windows").getAbsolutePath());
        new Main();
    }
}