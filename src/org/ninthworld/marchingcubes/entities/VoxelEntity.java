package org.ninthworld.marchingcubes.entities;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.helper.VoxelData;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class VoxelEntity extends ModelEntity {


    private VoxelData voxelData;

    public VoxelEntity(Loader loader, VoxelData voxelData) {
        super(null);
        this.voxelData = voxelData;
        this.setRawModel(createVoxelEntity(loader, this.voxelData));
    }

    private static RawModel createVoxelEntity(Loader loader, VoxelData voxelData){
        List<Float> verticesList = new ArrayList<>();
        List<Float> colorsList = new ArrayList<>();
        List<Float> normalsList = new ArrayList<>();
        List<Integer> indicesList = new ArrayList<>();
        Integer indicesPointer = 0;

        byte[][][] neighborData = voxelData.getNeighborData();
        for(int bx=0; bx<neighborData.length; bx++){
            for(int by=0; by<neighborData[bx].length; by++){
                for(int bz=0; bz<neighborData[bx][by].length; bz++){
                    float x = (float) bx, y = (float) by, z = (float) bz;
                    byte cube = neighborData[bx][by][bz];

                    if(cube == 0x01){
                        generateTriangle(new Vector3f(x - 0.5f, y - 0.5f, z), new Vector3f(x, y - 0.5f, z - 0.5f), new Vector3f(x - 0.5f, y, z - 0.5f),
                                verticesList, colorsList, normalsList, indicesList, indicesPointer);
                    }
                    if(cube == 0x02){
                        generateTriangle(new Vector3f(x, y - 0.5f, z - 0.5f), new Vector3f(x + 0.5f, y - 0.5f, z), new Vector3f(x + 0.5f, y, z - 0.5f),
                                verticesList, colorsList, normalsList, indicesList, indicesPointer);
                    }
                }
            }
        }

        float[] vertices = getFloatArray(verticesList);
        float[] colors = getFloatArray(colorsList);
        float[] normals = getFloatArray(normalsList);
        int[] indices  = getIntArray(indicesList);

        return loader.loadToVao(vertices, colors, normals, indices);
    }

    private static void generateTriangle(Vector3f p1, Vector3f p2, Vector3f p3, List<Float> verticesList, List<Float> colorsList, List<Float> normalsList, List<Integer> indicesList, Integer indicesPointer){
        Vector3f normal = new Vector3f();
        Vector3f.cross(Vector3f.sub(p2, p1, null), Vector3f.sub(p3, p1, null), normal);
        normal.normalise();

        verticesList.add(p1.x);
        verticesList.add(p1.y);
        verticesList.add(p1.z);
        verticesList.add(p2.x);
        verticesList.add(p2.y);
        verticesList.add(p2.z);
        verticesList.add(p3.x);
        verticesList.add(p3.y);
        verticesList.add(p3.z);

        for(int i=0; i<9; i++){
            colorsList.add(1f);
        }
        for(int i=0; i<3; i++){
            normalsList.add(normal.x);
            normalsList.add(normal.y);
            normalsList.add(normal.z);
            indicesList.add(indicesPointer++);
        }
    }

    private static float[] getFloatArray(List<Float> list){
        float[] array = new float[list.size()];
        for(int i=0; i<array.length; i++){
            array[i] = list.get(i);
        }
        return array;
    }

    private static int[] getIntArray(List<Integer> list){
        int[] array = new int[list.size()];
        for(int i=0; i<array.length; i++){
            array[i] = list.get(i);
        }
        return array;
    }
}
