package model.utils.render;

/**
 * Created by PhilippKroll on 10.11.2016.
 */
public class Range {
    public int x;
    public int y;
    public int width;
    public int height;
    public Range(int x,int y,int width,int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public void setX(int x){
        if(x < 0){
            throw new IllegalArgumentException();
        }
        this.x = x;
    }
    public void setY(int y){
        if(y < 0){
            throw new IllegalArgumentException();
        }
        this.y = y;
    }
    public void setWidth(int width){
        if(width < 0){
            throw new IllegalArgumentException();
        }
        this.width = width;
    }
    public void setHeight(int height){
        if(height < 0){
            throw new IllegalArgumentException();
        }
        this.height = height;
    }
}
