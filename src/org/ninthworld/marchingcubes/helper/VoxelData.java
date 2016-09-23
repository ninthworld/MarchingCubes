package org.ninthworld.marchingcubes.helper;

/**
 * Created by NinthWorld on 9/22/2016.
 */
public class VoxelData {

    private int[][][] voxelData;

    public VoxelData(int xLength, int yLength, int zLength){
        this.voxelData = new int[xLength][yLength][zLength];
    }

    public int[][][] getVoxelData() {
        return voxelData;
    }

    public void setVoxelData(int[][][] voxelData) {
        this.voxelData = voxelData;
    }

    public int getVoxelDataAt(int x, int y, int z){
        if(inBounds(x, y, z)){
            return voxelData[x][y][z];
        }else{
            return 0;
        }
    }

    public void setVoxelDataAt(int x, int y, int z, int data){
        if(inBounds(x, y, z)){
            this.voxelData[x][y][z] = data;
        }
    }

    private boolean inBounds(int x, int y, int z){
        return (x >= 0 && x < voxelData.length && y >= 0 && y < voxelData[0].length && z >= 0 && z < voxelData[0][0].length);
    }

    public byte[][][] getNeighborData(){
        byte[][][] neighbors = new byte[voxelData.length + 2][voxelData[0].length + 2][voxelData[0][0].length + 2];

        for(int bx=0; bx<neighbors.length; bx++){
            for(int by=0; by<neighbors[bx].length; by++){
                for(int bz=0; bz<neighbors[bx][by].length; bz++){
                    int x = bx - 1, y = by - 1, z = bz - 1;
                    byte cube = 0x00;

                    if(getVoxelDataAt(x, y, z) > 0){
                        cube |= 0x01;
                    }

                    if(getVoxelDataAt(x + 1, y, z) > 0){
                        cube |= 0x02;
                    }

                    if(getVoxelDataAt(x, y + 1, z) > 0){
                        cube |= 0x04;
                    }

                    if(getVoxelDataAt(x + 1, y + 1, z) > 0){
                        cube |= 0x08;
                    }

                    if(getVoxelDataAt(x, y, z + 1) > 0){
                        cube |= 0x10;
                    }

                    if(getVoxelDataAt(x + 1, y, z + 1) > 0){
                        cube |= 0x20;
                    }

                    if(getVoxelDataAt(x, y + 1, z + 1) > 0){
                        cube |= 0x40;
                    }

                    if(getVoxelDataAt(x + 1, y + 1, z + 1) > 0){
                        cube |= 0x80;
                    }

                    neighbors[bx][by][bz] = cube;
                }
            }
        }

        return neighbors;
    }
}
