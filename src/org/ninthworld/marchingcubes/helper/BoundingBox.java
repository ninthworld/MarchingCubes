package org.ninthworld.marchingcubes.helper;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 9/27/2016.
 */
public class BoundingBox {

    private static Vector3f centerPos;
    private static Vector3f dimensions;

    public BoundingBox(Vector3f centerPos, Vector3f dimensions){
        this.centerPos = centerPos;
        this.dimensions = dimensions;
    }

    public boolean isPointInBB(Vector3f point){
        return (point.x >= centerPos.x - dimensions.x/2f && point.y >= centerPos.y - dimensions.y/2f && point.z >= centerPos.z - dimensions.z/2f &&
                point.x <= centerPos.x + dimensions.x/2f && point.y <= centerPos.y + dimensions.y/2f && point.z <= centerPos.z + dimensions.z/2f);
    }

}
