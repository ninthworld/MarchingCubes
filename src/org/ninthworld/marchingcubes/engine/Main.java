package org.ninthworld.marchingcubes.engine;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.CameraEntity;
import org.ninthworld.marchingcubes.entities.LightEntity;
import org.ninthworld.marchingcubes.entities.ModelEntity;
import org.ninthworld.marchingcubes.entities.VoxelEntity;
import org.ninthworld.marchingcubes.fbo.Fbo;
import org.ninthworld.marchingcubes.fbo.PostProcessing;
import org.ninthworld.marchingcubes.helper.VoxelData;
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
        VoxelData voxelData = new VoxelData(1, 1, 1);
        voxelData.setVoxelDataAt(0, 0, 0, 1);
        VoxelEntity voxelEntity = new VoxelEntity(loader, voxelData);

        List<ModelEntity> voxelEntityList = new ArrayList<>();
        voxelEntityList.add(voxelEntity);

        modelEntities.put(voxelEntity.getRawModel(), voxelEntityList);

        loop();
    }

    private void loop(){
        while(!Display.isCloseRequested()){
            camera.move();

            //multisampleFbo.bindFrameBuffer();
            outputFbo.bindFrameBuffer();
            masterRenderer.renderScene(modelEntities, light, camera);
            outputFbo.unbindFrameBuffer();
            //multisampleFbo.unbindFrameBuffer();

            //multisampleFbo.resolveToFbo(outputFbo);
            PostProcessing.doPostProcessing(outputFbo.getColorTexture());

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