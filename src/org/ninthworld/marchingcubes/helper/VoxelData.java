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

    public int[][][][] getNeighborData(){
        int[][][][] neighbors = new int[voxelData.length + 1][voxelData[0].length + 1][voxelData[0][0].length + 1][8];

        for(int bx=0; bx<neighbors.length; bx++){
            for(int by=0; by<neighbors[bx].length; by++){
                for(int bz=0; bz<neighbors[bx][by].length; bz++){
                    for(int y=by-1, i=0; y<=by; y++){
                        for(int x=bx-1; x<=bx; x++, i++){
                            neighbors[bx][by][bz][i] = getVoxelDataAt(x, y, bz-1);
                        }
                        for(int x=bx; x>=bx-1; x--, i++){
                            neighbors[bx][by][bz][i] = getVoxelDataAt(x, y, bz);
                        }
                    }
                }
            }
        }

        return neighbors;
    }
}
