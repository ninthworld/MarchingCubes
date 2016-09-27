package org.ninthworld.marchingcubes.helper;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class VectorHelper {

    public static Vector3f scalar(float scalar, Vector3f v1){
        return new Vector3f(v1.x * scalar, v1.y * scalar, v1.z * scalar);
    }

    public static Vector3f invert(Vector3f v1){
        return new Vector3f(-v1.x, -v1.y, -v1.z);
    }

    public static Vector3f floor(Vector3f v1){
        return new Vector3f((float) Math.floor(v1.x), (float) Math.floor(v1.y), (float) Math.floor(v1.z));
    }
}
