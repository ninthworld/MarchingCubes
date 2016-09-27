package org.ninthworld.marchingcubes.entities;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.helper.VoxelData;
import org.ninthworld.marchingcubes.models.Loader;

/**
 * Created by NinthWorld on 9/27/2016.
 */
public class SpaceShipEntity extends VoxelEntity {

    public SpaceShipEntity(Loader loader, Vector3f position) {
        super(new VoxelData(8, 8, 8));
        this.setPosition(position);
        this.getVoxelData().setVoxelDataAt(4, 4, 4, 1);

        this.generateRawModel(loader);
    }
}
