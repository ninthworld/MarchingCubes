package org.ninthworld.marchingcubes.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.ninthworld.marchingcubes.entities.*;
import org.ninthworld.marchingcubes.fbo.Fbo;
import org.ninthworld.marchingcubes.fbo.PostProcessing;
import org.ninthworld.marchingcubes.helper.ProjectionMatrix;
import org.ninthworld.marchingcubes.helper.SimplexNoise;
import org.ninthworld.marchingcubes.helper.VoxelData;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.renderers.*;
import org.ninthworld.marchingcubes.renderers.geometry.NormalRenderer;

import java.io.File;
import java.util.*;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class Main {

    private Loader loader;

    private MasterRenderer masterRenderer;
    private SkyboxRenderer skyboxRenderer;
    private AsteroidRenderer asteroidRenderer;
    private NormalRenderer normalRenderer;

    private LightEntity light;
    private CameraEntity camera;

    private Fbo multisampleFbo;
    private Fbo masterFbo;
    private Fbo skyboxFbo;
    private Fbo normalFbo;
    private Fbo asteroidFbo;

    private Fbo ssaoFXFbo;
    private Fbo simpleFXFbo1;
    private Fbo simpleFXFbo2;
    private Fbo simpleFXFbo3;

    private Map<RawModel, List<ModelEntity>> modelEntities;
    private List<AsteroidEntity> asteroidEntities;
    private List<CuboidEntity> cuboidEntities;
    private List<PlaneEntity> planeEntities;

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
        planeEntities = new ArrayList<>();

        Matrix4f projectionMatrix = ProjectionMatrix.create();
        masterRenderer = new MasterRenderer(loader);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        asteroidRenderer = new AsteroidRenderer(loader, projectionMatrix);
        normalRenderer = new NormalRenderer(loader, projectionMatrix);

        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        masterFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        skyboxFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        asteroidFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        normalFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        ssaoFXFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        simpleFXFbo1 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        simpleFXFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        simpleFXFbo3 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

        setup();
    }

    private void setup(){

        AsteroidEntity asteroidEntity = new AsteroidEntity(loader, new Vector3f(0, 0, 0), 64, 16, 32, 128, 0.5, 12345);
        asteroidEntities.add(asteroidEntity);

        AsteroidEntity asteroidEntity1 = new AsteroidEntity(loader, new Vector3f(-64, 64, 64), 32, 8, 16, 64, 0.5, 54321);
        asteroidEntities.add(asteroidEntity1);

//        float cubicLength = asteroidEntity.getVoxelData().getVoxelData().length*asteroidEntity.getScale();
//        CuboidEntity cuboidEntity = new CuboidEntity(loader, asteroidEntity.getCenterPosition(), new Vector3f(cubicLength, cubicLength, cubicLength));
//        cuboidEntities.add(cuboidEntity);
//
//        cubicLength = asteroidEntity1.getVoxelData().getVoxelData().length*asteroidEntity1.getScale();
//        CuboidEntity cuboidEntity1 = new CuboidEntity(loader, asteroidEntity1.getCenterPosition(), new Vector3f(cubicLength, cubicLength, cubicLength));
//        cuboidEntities.add(cuboidEntity1);

        PlaneEntity planeEntity = new PlaneEntity(loader, new Vector3f(0, 0, 0), new Vector3f(1, 0, 1));
        float scale = 64;
        for(int i=-8; i<8; i++){
            for(int j=-8; j<8; j++){
                PlaneEntity grid = new PlaneEntity(planeEntity.getRawModel(), new Vector3f(i, 0, j));
                grid.setScale(scale);
                planeEntities.add(grid);
            }
        }

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
            masterRenderer.renderScene(modelEntities, cuboidEntities, planeEntities, light, camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(masterFbo);

            multisampleFbo.bindFrameBuffer();
            skyboxRenderer.renderSkybox(camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(skyboxFbo);

            multisampleFbo.bindFrameBuffer();
            asteroidRenderer.renderAsteroids(asteroidEntities, light, camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(asteroidFbo);

            List<ModelEntity> models = new ArrayList<>();
            models.addAll(asteroidEntities);

            normalFbo.bindFrameBuffer();
            normalRenderer.render(models, camera);
            normalFbo.unbindFrameBuffer();

            ssaoFXFbo.bindFrameBuffer();
            PostProcessing.doPostProcessingSSAO(asteroidFbo.getColorTexture(), asteroidFbo.getDepthTexture(), normalFbo.getColorTexture(), 15, 0.006f, 4.0f);
            ssaoFXFbo.unbindFrameBuffer();

            simpleFXFbo1.bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(masterFbo.getColorTexture(), masterFbo.getDepthTexture(), new Vector4f(0.1f, 0.6f, 0.8f, 0.4f), 2, 0.003f);
            simpleFXFbo1.unbindFrameBuffer();

            simpleFXFbo2.bindFrameBuffer();
            PostProcessing.doPostProcessingSimpleAlpha(simpleFXFbo1.getColorTexture(), skyboxFbo.getColorTexture());
            simpleFXFbo2.unbindFrameBuffer();

            simpleFXFbo3.bindFrameBuffer();
            PostProcessing.doPostProcessingSimpleAdd(ssaoFXFbo.getColorTexture(), asteroidFbo.getDepthTexture(), simpleFXFbo2.getColorTexture(), skyboxFbo.getDepthTexture());
            simpleFXFbo3.unbindFrameBuffer();

//            ppFbo1.bindFrameBuffer();
//            PostProcessing.doPostProcessingSimpleAdd(outputFbo1.getColorTexture(), outputFbo1.getDepthTexture(), outputFbo2.getColorTexture(), outputFbo2.getDepthTexture());
//            ppFbo1.unbindFrameBuffer();

            //ppFbo2.bindFrameBuffer();
            PostProcessing.doPostProcessingOutline(simpleFXFbo3.getColorTexture(), asteroidFbo.getDepthTexture(), new Vector4f(0.8f, 0.4f, 0.1f, 0.4f), 2, 0.0015f);
            //ppFbo2.unbindFrameBuffer();

            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    private void cleanUp(){
        masterRenderer.cleanUp();
        skyboxRenderer.cleanUp();
        asteroidRenderer.cleanUp();
        normalRenderer.cleanUp();

        multisampleFbo.cleanUp();
        masterFbo.cleanUp();
        skyboxFbo.cleanUp();
        asteroidFbo.cleanUp();
        normalFbo.cleanUp();
        ssaoFXFbo.cleanUp();
        simpleFXFbo1.cleanUp();
        simpleFXFbo2.cleanUp();
        simpleFXFbo3.cleanUp();

        loader.cleanUp();

        PostProcessing.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args){
        //System.setProperty("org.lwjgl.librarypath", new File("lib/native/windows").getAbsolutePath());
        new Main();
    }
}