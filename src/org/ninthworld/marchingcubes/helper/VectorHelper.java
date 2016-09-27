package org.ninthworld.marchingcubes.helper;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 9/26/2016.
 */
public class VectorHelper {

    public static Vector3f scalar(float scalar, Vector3f v1){
        return new Vector3f(v1.x * scalar, v1.y * scalar, v1.z * scalar);
    }
}
