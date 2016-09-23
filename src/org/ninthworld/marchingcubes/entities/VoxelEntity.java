package org.ninthworld.marchingcubes.entities;

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
        int indicesPointer = 0;

        byte[][][] neighborData = voxelData.getNeighborData();
        for(int bx=0; bx<neighborData.length; bx++){
            for(int by=0; by<neighborData[bx].length; by++){
                for(int bz=0; bz<neighborData[bx][by].length; bz++){
                    float x = (float) bx, y = (float) by, z = (float) bz;
                    byte cube = neighborData[bx][by][bz];

                    if(cube == 0x01){
                        verticesList.addAll(Arrays.asList(new Float[]{x, y, z + 0.5f, x + 0.5f, y, z, x, y + 0.5f, z}));
                        colorsList.addAll(Arrays.asList(new Float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f}));
                        normalsList.addAll(Arrays.asList(new Float[]{1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f}));
                        indicesList.addAll(Arrays.asList(new Integer[]{indicesPointer, indicesPointer + 1, indicesPointer + 2}));
                        indicesPointer += 3;
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
