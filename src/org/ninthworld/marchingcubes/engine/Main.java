package org.ninthworld.marchingcubes.engine;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.entities.CameraEntity;
import org.ninthworld.marchingcubes.entities.LightEntity;
import org.ninthworld.marchingcubes.entities.ModelEntity;
import org.ninthworld.marchingcubes.entities.VoxelEntity;
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
    private Map<String, RawModel> rawModels;
    private Map<RawModel, List<ModelEntity>> modelEntities;

    private Fbo multisampleFbo;
    private Fbo outputFbo;

    public Main(){
        loader = new Loader();

        DisplayManager.createDisplay();
        PostProcessing.init(loader);

        light = new LightEntity(new Vector3f(1, 1, 1), new Vector3f(1, 1, 1));
        camera = new CameraEntity(new Vector3f(15, 15, 15));
        camera.setRotation(new Vector3f((float) Math.PI/6f, (float) -Math.PI/6f, 0f));

        rawModels = new HashMap<>();
        modelEntities = new HashMap<>();

        masterRenderer = new MasterRenderer();

        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

        setup();
    }

    private void setup(){
        VoxelData voxelData = new VoxelData(64, 64, 64);

        float radius = 16;
        float noiseAmp = radius*2;
        SimplexNoise simplexNoise = new SimplexNoise((int)noiseAmp*4, 0.5, (int)(Math.random()*1000));

        int width = voxelData.getVoxelData().length;
        int height = voxelData.getVoxelData()[0].length;
        int depth = voxelData.getVoxelData()[0][0].length;

        Vector3f volumeCenter = new Vector3f(width/2f, height/2f, depth/2f);

        for(int x=0; x<width; x++){
            for(int y=0; y<height; y++){
                for(int z=0; z<depth; z++){
                    Vector3f currentPos = new Vector3f((float) x, (float) y, (float) z);

                    float distToCenter = Vector3f.sub(volumeCenter, currentPos, null).length();
                    float surfaceDist = radius + (float) simplexNoise.getNoise(x, y, z)*noiseAmp;

                    if(distToCenter < surfaceDist){
                        voxelData.setVoxelDataAt(x, y, z, (distToCenter < (radius+noiseAmp)/3f ? 1 : (distToCenter >= (radius+noiseAmp)/3f && distToCenter < 2f*(radius+noiseAmp)/3f ? 2 : 3)));
                    }
                }
            }
        }

        VoxelEntity voxelEntity = new VoxelEntity(loader, voxelData);
        voxelEntity.setScale(0.25f);

        List<ModelEntity> voxelEntityList = new ArrayList<>();
        voxelEntityList.add(voxelEntity);

        modelEntities.put(voxelEntity.getRawModel(), voxelEntityList);

        loop();
    }

    private void loop(){
        while(!Display.isCloseRequested()){
            camera.move();

            multisampleFbo.bindFrameBuffer();
            masterRenderer.renderScene(modelEntities, light, camera);
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