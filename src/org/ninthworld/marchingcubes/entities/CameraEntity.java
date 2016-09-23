package org.ninthworld.marchingcubes.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class CameraEntity extends Entity {

    private static final float maxLook = 85;
    private static final float mouseSensitivity = 0.08f;

    public CameraEntity(Vector3f position){
        super(position, new Vector3f());
    }

    public CameraEntity(Vector3f position, Vector3f rotation){
        super(position, rotation);
    }

    public void move(){

        float sinYaw = (float) Math.sin(getRotation().getY());
        float cosYaw = (float) Math.cos(getRotation().getY());

        float speed = 0.8f;
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            increasePosition(sinYaw * speed, 0, -cosYaw * speed);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            increasePosition(-sinYaw * speed, 0, cosYaw * speed);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            increasePosition(cosYaw * speed, 0, -sinYaw * speed);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            increasePosition(-cosYaw * speed, 0, sinYaw * speed);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            increasePosition(0, speed, 0);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            increasePosition(0, -speed, 0);
        }

        if(Mouse.isInsideWindow() && Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            Mouse.setGrabbed(false);
        }

        if(Mouse.isGrabbed()) {
            float mouseDX = Mouse.getDX();
            float mouseDY = -Mouse.getDY();
            increaseRotation((float) Math.toRadians(mouseDY * mouseSensitivity), (float) Math.toRadians(mouseDX * mouseSensitivity), 0);
            getRotation().setX((float) Math.max(-maxLook, Math.min(maxLook, getRotation().getX())));
        }
    }
}