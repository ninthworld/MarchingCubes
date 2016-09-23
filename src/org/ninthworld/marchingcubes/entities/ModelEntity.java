package org.ninthworld.marchingcubes.entities;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.models.RawModel;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class ModelEntity extends Entity {

    private float scale;
    private RawModel rawModel;

    public ModelEntity(RawModel rawModel){
        super();
        this.rawModel = rawModel;
        this.scale = 1.0f;
    }

    public ModelEntity(RawModel rawModel, Vector3f position, Vector3f rotation){
        super(position, rotation);
        this.rawModel = rawModel;
        this.scale = 1.0f;
    }

    public ModelEntity(RawModel rawModel, Vector3f position, Vector3f rotation, float scale){
        super(position, rotation);
        this.rawModel = rawModel;
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public void setRawModel(RawModel rawModel) {
        this.rawModel = rawModel;
    }
}
