package org.ninthworld.marchingcubes.entities;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.marchingcubes.helper.VertexAttribData;
import org.ninthworld.marchingcubes.models.Loader;
import org.ninthworld.marchingcubes.models.RawModel;

/**
 * Created by NinthWorld on 9/24/2016.
 */
public class PlaneEntity extends ModelEntity {

    private Vector3f dimensions;

    public PlaneEntity(Loader loader, Vector3f position, Vector3f dimensions){
        super(null);
        this.setPosition(position);
        this.dimensions = dimensions;
        this.setRawModel(createPlaneEntity(loader, dimensions));
    }

    public PlaneEntity(RawModel rawModel, Vector3f position){
        super(rawModel);
        this.setPosition(position);
    }

    public static RawModel createPlaneEntity(Loader loader, Vector3f dimensions){
        VertexAttribData vertexAttribData = new VertexAttribData();

        float x = dimensions.x/2f;
        float y = dimensions.y/2f;
        float z = dimensions.z/2f;

        float[][][] vertices = new float[][][]{
                {{-x, -y, -z}, {x, -y, -z}, {x, y, z}},
                {{x, y, z}, {-x, y, z}, {-x, -y, -z}}
        };

        Vector3f color = new Vector3f(1, 1, 1);
        for(int i=0; i<vertices.length; i++){
            Vector3f[] verts = new Vector3f[3];
            for(int j=0; j<vertices[i].length; j++){
                verts[j] = new Vector3f(vertices[i][j][0], vertices[i][j][1], vertices[i][j][2]);
            }

            vertexAttribData.addTriangle(verts[0], verts[1], verts[2], color);
        }

        return vertexAttribData.loadToVaoColor(loader);
    }

}
