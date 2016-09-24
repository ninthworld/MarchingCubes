package org.ninthworld.marchingcubes.helper;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NinthWorld on 9/23/2016.
 */
public class VertexAttribData {

    private List<Float> verticesList;
    private List<Float> materialsList;
    private List<Float> normalsList;
    private List<Integer> indicesList;
    private int indicesPointer;

    public Vector3f centerPos;

    public VertexAttribData(){
        this.verticesList = new ArrayList<>();
        this.materialsList = new ArrayList<>();
        this.normalsList = new ArrayList<>();
        this.indicesList = new ArrayList<>();
        this.indicesPointer = 0;
        this.centerPos = new Vector3f();
    }

    public void addTriangle(Vector3f v1, Vector3f v2, Vector3f v3, int material) {
        Vector3f normal = new Vector3f();
        Vector3f.cross(Vector3f.sub(v2, v1, null), Vector3f.sub(v3, v1, null), normal);
        normal.normalise();

        verticesList.add(v1.x);
        verticesList.add(v1.y);
        verticesList.add(v1.z);
        verticesList.add(v2.x);
        verticesList.add(v2.y);
        verticesList.add(v2.z);
        verticesList.add(v3.x);
        verticesList.add(v3.y);
        verticesList.add(v3.z);

        for (int i = 0; i < 3; i++) {
            normalsList.add(normal.x);
            normalsList.add(normal.y);
            normalsList.add(normal.z);
            indicesList.add(indicesPointer++);

            materialsList.add((float) material);
        }
    }

    public RawModel loadToVao(Loader loader) {
        float[] vertices = getFloatArray(verticesList);
        float[] materials = getFloatArray(materialsList);
        float[] normals = getFloatArray(normalsList);
        int[] indices  = getIntArray(indicesList);

        return loader.loadToVaoMaterial(vertices, materials, normals, indices);
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
