package model.utils;

import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class Bitmap extends Observable{

    private int[][] bitmap;

    public Bitmap(int height, int width){
        bitmap = new int[height][width];
    }

    public int getHeight(){
        return bitmap.length;
    }

    public int getWidth(){
        return bitmap[0].length;
    }

    public void setColor(int c,int row, int col){
        if(c < 0){
            throw new IllegalArgumentException();
        }
        if(!checkBounds(row,col)){
            throw new IndexOutOfBoundsException();
        }
        super.setChanged();
        bitmap[row][col] = c;
    }

    public int getColor(int row, int col){
        if(!checkBounds(row,col)){
            throw new IndexOutOfBoundsException();
        }
        return bitmap[row][col];
    }

    public boolean checkBounds(int y, int x){
        if( x < 0 || x >= bitmap[0].length || y < 0 || y >= bitmap.length){
            return false;
        }
        return true;
    }

    public void setChanged(){
        super.setChanged();
    }

    public void clearChanged(){
        super.clearChanged();
    }
}
