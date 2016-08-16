package model.utils;
import java.util.Observable;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class PictureBufferState extends Observable{

    private int[][] colorBuffer;
    private float[][] depthBuffer;

    public PictureBufferState(int height, int width){
        colorBuffer = new int[height][width];
        depthBuffer = new float[height][width];
    }

    public void clear(){
        int width = colorBuffer[0].length;
        int height = colorBuffer.length;
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                colorBuffer[y][x] = 0;
                depthBuffer[y][x] = 0;
            }
        }
    }

    public void setChanged(){
        super.setChanged();
    }

    public void clearChanged(){
        super.clearChanged();
    }

    private boolean checkBounds(int y,int x){
        int width = colorBuffer[0].length;
        int height = colorBuffer.length;
        if( x < 0 || x >= width || y < 0 || y >= height){
            return false;
        }
        return true;
    }

    public void setColor(int row,int col,int color){

    }

    public void setDepth(int row,int col,float depth){

    }

    public int getColor(int row, int col){
        return 0;
    }

    public float getDepth(int row, int col){
        return 0f;
    }
}
