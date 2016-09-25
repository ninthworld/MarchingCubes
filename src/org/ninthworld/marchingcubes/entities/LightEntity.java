package org.ninthworld.marchingcubes.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class LightEntity extends Entity {

    private Vector3f color;
    private Vector3f ambient;

    public LightEntity(Vector3f position, Vector3f color){
        super(position, new Vector3f());
        this.color = color;
        this.ambient = new Vector3f(0, 0, 0);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }
}
