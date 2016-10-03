package model.utils;

/**
 * Created by PhilippKroll on 01.10.2016.
 */
public class Filter {

    public static enum AAMULTIPLY{
        X2,X4,X8,X16,X32
    }

    public static enum AAMETHOD{
        STDRDAVG
    }

    private PaintBox pb;
    private AAMULTIPLY aamultiplyer;
    private AAMETHOD aamethod;

    public Filter(){
        aamultiplyer = AAMULTIPLY.X2;
        aamethod = AAMETHOD.STDRDAVG;
        pb = new PaintBox();
    }

    public void filter(PictureBufferState pbs){
        int row = 1;
        int height = pbs.gethHeight()-1;
        int width = pbs.getWidth()-1;
        while(row < height-1){
            int col = 1;
            while (col < width-1){
                int avg = standardAverage(pbs,col-1,row-1,3,3);
                int a = pbs.getColor(row,col);
                //System.out.println((a >> 24)|0xff);
                pbs.setColor(row,col,avg);
                col++;
            }
            row++;
        }
    }

    private int getMultiplier(AAMULTIPLY m){
        switch (m){
            case X2: return 2;
            case X4: return 4;
            case X8: return 8;
            case X16: return 16;
            case X32: return 32;
            default: return 2;
        }
    }

    public int standardAverage(PictureBufferState pbs,int xStart,int yStart, int width,int height){
        if(!isValidCutout(xStart,yStart,width,height,pbs)){
            throw new IllegalArgumentException();
        }
        float avgGreen = 0f;
        float avgBlue = 0f;
        float avgRed = 0f;
        float avgAlpha = 0f;
        int yEnd = yStart + height;
        while(yStart < yEnd){
            int x = xStart;
            int xEnd = x + width;
            while (x < xEnd){
                int c = pbs.getColor(yStart,x);
                avgAlpha += pb.getAlpha(c);
                //int a = ((c >> 24) & 0xff)/255;
                avgGreen += (pb.getGreen(c));
                avgBlue += (pb.getBlue(c));
                avgRed += (pb.getRed(c));
                x++;
            }
            yStart++;
        }
        int numPixels = width * height;
        avgAlpha /= numPixels;
        avgGreen /= numPixels;
        avgBlue /= numPixels;
        avgRed /= numPixels;
        return pb.createColor((int)avgAlpha,(int)avgGreen,(int)avgBlue,(int)avgRed);
    }

    private boolean isValidCutout(int x,int y,int width,int height,PictureBufferState pbs){
        if(x < 0 || x + width > pbs.getWidth() || y < 0 || y + height > pbs.gethHeight()){
            return false;
        }
        return true;
    }
}
