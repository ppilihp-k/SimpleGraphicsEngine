package model.utils;

/**
 * Created by PhilippKroll on 01.10.2016.
 */
public class PaintBox {

    public int createColor(int alpha,int red,int green,int blue){
        return ((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
    }

    public int setAlpha(int alpha,int c){
        return ((alpha & 0xff) << 24) | (c & 0xffffff);
    }

    public int getRed(int c){
        return c & 0xff;
    }

    public int getBlue(int c){
        return (c >> 8) & 0xff;
    }

    public int getGreen(int c){
        return (c >> 16) & 0xff;
    }

    public int getAlpha(int c){
        return (c >> 24) & 0xff;
    }
}
