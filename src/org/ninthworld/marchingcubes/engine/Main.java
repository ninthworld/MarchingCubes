package org.ninthworld.marchingcubes.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.*;
import org.ninthworld.marchingcubes.fbo.Fbo;
import org.ninthworld.marchingcubes.fbo.PostProcessing;
import org.ninthworld.marchingcubes.helper.SimplexNoise;
import org.ninthworld.marchingcubes.helper.VoxelData;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.renderers.*;

import java.io.File;
import java.util.*;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class Main {

    private Loader loader;

    private MasterRenderer masterRenderer;

    private LightEntity light;
    private CameraEntity camera;

    private Fbo multisampleFbo;
    private Fbo outputFbo;

    private Map<RawModel, List<ModelEntity>> modelEntities;
    private List<AsteroidEntity> asteroidEntities;
    private List<CuboidEntity> cuboidEntities;

    public Main(){
        loader = new Loader();

        DisplayManager.createDisplay();
        PostProcessing.init(loader);

        light = new LightEntity(new Vector3f(1, 1, 1), new Vector3f(1f, 0.8f, 0.4f));
        light.setAmbient(new Vector3f(0.1f, 0.1f, 0.2f));

        camera = new CameraEntity(new Vector3f(5, 5, 5));
        camera.setRotation(new Vector3f((float) Math.PI/6f, (float) -Math.PI/6f, 0f));

        modelEntities = new HashMap<>();
        asteroidEntities = new ArrayList<>();
        cuboidEntities = new ArrayList<>();

        masterRenderer = new MasterRenderer(loader);

        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

        setup();
    }

    private void setup(){

        AsteroidEntity asteroidEntity = new AsteroidEntity(loader, new Vector3f(0, 0, 0), 64, 16, 32, 128, 0.5, 12345);
        asteroidEntities.add(asteroidEntity);

        AsteroidEntity asteroidEntity1 = new AsteroidEntity(loader, new Vector3f(-64, 64, 64), 32, 8, 16, 64, 0.5, 54321);
        asteroidEntities.add(asteroidEntity1);

        float cubicLength = asteroidEntity.getVoxelData().getVoxelData().length*asteroidEntity.getScale();
        CuboidEntity cuboidEntity = new CuboidEntity(loader, asteroidEntity.getCenterPosition(), new Vector3f(cubicLength, cubicLength, cubicLength));
        cuboidEntities.add(cuboidEntity);

        cubicLength = asteroidEntity1.getVoxelData().getVoxelData().length*asteroidEntity1.getScale();
        CuboidEntity cuboidEntity1 = new CuboidEntity(loader, asteroidEntity1.getCenterPosition(), new Vector3f(cubicLength, cubicLength, cubicLength));
        cuboidEntities.add(cuboidEntity1);

        loop();
    }

    float angle = 0;
    private void loop(){
        while(!Display.isCloseRequested()){
            camera.move();
            light.setPosition(new Vector3f((float) Math.cos(angle)*10f, 5f, (float) Math.sin(angle)*10f));

            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                angle += Math.PI/200f;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                angle -= Math.PI/200f;
            }

            multisampleFbo.bindFrameBuffer();
            masterRenderer.renderScene(modelEntities, asteroidEntities, cuboidEntities, light, camera);
            multisampleFbo.unbindFrameBuffer();

            multisampleFbo.resolveToFbo(outputFbo);
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
        //System.setProperty("org.lwjgl.librarypath", new File("lib/native/windows").getAbsolutePath());
        new Main();
    }
}