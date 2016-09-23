package org.ninthworld.marchingcubes.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class Entity {

    private Vector3f position;
    private Vector3f rotation;

    public Entity(){
        this.position = new Vector3f();
        this.rotation = new Vector3f();
    }

    public Entity(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void increasePosition(Vector3f delta) {
        position = Vector3f.add(position, delta, null);
    }

    public void increasePosition(float x, float y, float z){
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public void increaseRotation(Vector3f delta) {
        rotation = Vector3f.add(rotation, delta, null);
    }

    public void increaseRotation(float pitch, float yaw, float roll){
        rotation.x += pitch;
        rotation.y += yaw;
        rotation.z += roll;
    }
}
