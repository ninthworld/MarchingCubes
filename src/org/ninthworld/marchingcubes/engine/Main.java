package org.ninthworld.marchingcubes.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.*;
import org.ninthworld.marchingcubes.fbo.Fbo;
import org.ninthworld.marchingcubes.fbo.PostProcessing;
import org.ninthworld.marchingcubes.helper.ProjectionMatrix;
import org.ninthworld.marchingcubes.helper.VectorHelper;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;
import org.ninthworld.marchingcubes.renderers.*;
import org.ninthworld.marchingcubes.renderers.geometry.CuboidRenderer;
import org.ninthworld.marchingcubes.renderers.geometry.NormalRenderer;
import org.ninthworld.marchingcubes.renderers.geometry.PlaneRenderer;

import java.security.Key;
import java.util.*;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class Main {

    private Loader loader;

    private PlaneRenderer planeRenderer;
    private CuboidRenderer cuboidRenderer;
    private SkyboxRenderer skyboxRenderer;
    private AsteroidRenderer asteroidRenderer;
    private NormalRenderer normalRenderer;

    private LightEntity light;
    private CameraEntity camera;

    private Fbo multisampleFbo;
    private Fbo skyboxFbo;
    private Fbo gridFbo;
    private Fbo cuboidFbo;
    private Fbo asteroidFbo;
    private Fbo asteroidNormalFbo;

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
        planeRenderer = new PlaneRenderer(loader, projectionMatrix);
        cuboidRenderer = new CuboidRenderer(loader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        asteroidRenderer = new AsteroidRenderer(loader, projectionMatrix);
        normalRenderer = new NormalRenderer(loader, projectionMatrix);

        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        skyboxFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        gridFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        cuboidFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        asteroidFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        asteroidNormalFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

        setup();
    }

    private void cleanUp(){
        planeRenderer.cleanUp();
        cuboidRenderer.cleanUp();
        skyboxRenderer.cleanUp();
        asteroidRenderer.cleanUp();
        normalRenderer.cleanUp();

        multisampleFbo.cleanUp();
        skyboxFbo.cleanUp();
        gridFbo.cleanUp();
        cuboidFbo.cleanUp();
        asteroidFbo.cleanUp();
        asteroidNormalFbo.cleanUp();

        loader.cleanUp();

        PostProcessing.cleanUp();
        DisplayManager.closeDisplay();
    }

    CuboidEntity cuboidEntity;

    private void setup(){

        AsteroidEntity asteroidEntity = new AsteroidEntity(loader, new Vector3f(0, 0, 0), 64, 16, 32, 128, 0.5, 12345);
        asteroidEntities.add(asteroidEntity);

        AsteroidEntity asteroidEntity1 = new AsteroidEntity(loader, new Vector3f(-64, 64, 64), 32, 8, 16, 64, 0.5, 54321);
        asteroidEntities.add(asteroidEntity1);

        float cubicLength = 1; //asteroidEntity.getVoxelData().getVoxelData().length*asteroidEntity.getScale();
        cuboidEntity = new CuboidEntity(loader, asteroidEntity.getCenterPosition(), new Vector3f(cubicLength, cubicLength, cubicLength));
        cuboidEntities.add(cuboidEntity);
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

    long time = 0;
    int frames = 0;

    float angle = 0;
    boolean drawCuboid = false;
    boolean rightMouse = false;
    boolean updateAsteroid = false;
    private void loop(){
        time = System.nanoTime();
        while(!Display.isCloseRequested()){
            if(System.nanoTime() - time < 1000000000L){
                frames++;
            }else{
                Display.setTitle("Marching Cubes - FPS: " + frames);
                time = System.nanoTime();
                frames = 0;

                if(updateAsteroid){
                    loader.cleanRawModel(asteroidEntities.get(0).getRawModel());
                    asteroidEntities.get(0).generateRawModel(loader);
                    updateAsteroid = false;
                    // ADD A REAL RAW MODEL UPDATE QUEUE
                    // ADD A REAL RAW MODEL UPDATE QUEUE
                    // ADD A REAL RAW MODEL UPDATE QUEUE
                }
            }

            camera.move();
            light.setPosition(new Vector3f((float) Math.cos(angle)*10f, 5f, (float) Math.sin(angle)*10f));

            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                angle += Math.PI/200f;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                angle -= Math.PI/200f;
            }

            if(Mouse.isButtonDown(1)){
                rightMouse = true;
            }else{
                rightMouse = false;
            }

            drawCuboid = false;
            for(AsteroidEntity asteroidEntity : asteroidEntities){
                for(int i=0; i<100; i++){
                    Vector3f point = Vector3f.add(camera.getPosition(), VectorHelper.scalar((float) i, camera.getDirectionVector()), null);
                    if(point.x >= asteroidEntity.getPosition().x && point.x <= asteroidEntity.getPosition().x + asteroidEntity.getVoxelData().getVoxelData().length &&
                       point.y >= asteroidEntity.getPosition().y && point.y <= asteroidEntity.getPosition().y + asteroidEntity.getVoxelData().getVoxelData()[0].length &&
                       point.z >= asteroidEntity.getPosition().z && point.z <= asteroidEntity.getPosition().z + asteroidEntity.getVoxelData().getVoxelData()[0][0].length){
                        Vector3f voxelPoint = Vector3f.sub(point, asteroidEntity.getPosition(), null);
                        voxelPoint = new Vector3f((float) Math.floor(voxelPoint.x), (float) Math.floor(voxelPoint.y), (float) Math.floor(voxelPoint.z));

                        if(asteroidEntity.getVoxelData().getVoxelDataAt((int) voxelPoint.x, (int) voxelPoint.y, (int) voxelPoint.z) > 0){
                            drawCuboid = true;
                            cuboidEntity.setPosition(Vector3f.add(Vector3f.add(voxelPoint, asteroidEntity.getPosition(), null), new Vector3f(0.5f, 0.5f, 0.5f), null));

                            if(rightMouse){
                                asteroidEntity.getVoxelData().setVoxelDataAt((int) voxelPoint.x, (int) voxelPoint.y, (int) voxelPoint.z, 0);
                                updateAsteroid = true;

                                // ADD A REAL RAW MODEL UPDATE QUEUE
                                // ADD A REAL RAW MODEL UPDATE QUEUE
                                // ADD A REAL RAW MODEL UPDATE QUEUE

//                                loader.cleanRawModel(asteroidEntity.getRawModel());
//                                asteroidEntity.generateRawModel(loader);
                            }
                            break;
                        }
                    }
                }
            }

            multisampleFbo.bindFrameBuffer();
            skyboxRenderer.renderSkybox(camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(skyboxFbo);

            multisampleFbo.bindFrameBuffer();
            planeRenderer.render(planeEntities, light, camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(gridFbo);

            multisampleFbo.bindFrameBuffer();
            cuboidRenderer.render((drawCuboid ? cuboidEntities : new ArrayList<CuboidEntity>()), light, camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(cuboidFbo);

            multisampleFbo.bindFrameBuffer();
            asteroidRenderer.render(asteroidEntities, light, camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(asteroidFbo);

            List<ModelEntity> models = new ArrayList<>();
            models.addAll(asteroidEntities);
            multisampleFbo.bindFrameBuffer();
            normalRenderer.render(models, camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFbo(asteroidNormalFbo);

            PostProcessing.doPostProcessing(
                    skyboxFbo.getColorTexture(), skyboxFbo.getDepthTexture(),
                    gridFbo.getColorTexture(), gridFbo.getDepthTexture(),
                    cuboidFbo.getColorTexture(), cuboidFbo.getDepthTexture(),
                    asteroidFbo.getColorTexture(), asteroidFbo.getDepthTexture(), asteroidNormalFbo.getColorTexture()
            );

            DisplayManager.updateDisplay();
        }

        cleanUp();
    }

    public static void main(String[] args){
        //System.setProperty("org.lwjgl.librarypath", new File("lib/native/windows").getAbsolutePath());
        new Main();
    }
}