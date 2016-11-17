package model.utils.render.strategys;

import model.elementstate.MaterialTextureState;
import model.utils.PictureBufferState;
import singlethreadedGeometrie.geometricCalc.model.Vector;

/**
 * Created by PhilippKroll on 09.11.2016.
 */
public class ScanLineTwo extends ScanLine{

    public ScanLineTwo(){
        super();
    }

    /**
     * renders a line between the two vectors v1 and v2 with the desired thickness into the given picturebuffer.
     * this method uses the fact, that v1 and v2 can be interpreted as a segment. so the pixels to set are computed by a
     * linear function like f(x)=mx+n ,where m is the gradient and n is the intersection with the y axis.
     * @param v1
     * @param v2
     * @param thickness
     * @param pictureBuffer
     */
    public void scanTwo(Vector v1, Vector v2, int thickness, MaterialTextureState texture, PictureBufferState pictureBuffer){
        if(v1.length() != 2 || v2.length() != 2){
            throw new IllegalArgumentException();
        }
        if(v1.get(0) < 0 || v1.get(0) >= pictureBuffer.getWidth() || v1.get(1) < 0 || v1.get(1) >= pictureBuffer.getHeight() ||
                v2.get(0) < 0 || v2.get(0) >= pictureBuffer.getWidth() || v2.get(1) < 0 || v2.get(1) >= pictureBuffer.getHeight()){
            throw new IllegalArgumentException();
        }
        //System.out.println("scanTwo");
        float x1 = (float)v1.get(0);
        float y1 = (float)v1.get(1);

        float x2 = (float)v2.get(0);
        float y2 = (float)v2.get(1);

        float denom = (x2 - x1);
        float nom = (y2 - y1);

        if(denom == 0){
            /*there is no gradient! so the segment hat to be parallel to the y axis*/
            int yStart = 0;
            int yEnd = 0;
            if(y1 < y2){
                yStart = (int)y1;
                yEnd = (int)y2;
            } else {
                yStart = (int)y2;
                yEnd = (int)y1;
            }
            while(yStart <= yEnd){
                for (int xi = 0;xi < thickness;xi++){
                    int c;
                    if(texture != null && texture.getTexture() != null){
                        c = texture.getColor((int)v1.get(0),yStart%texture.getTexture().getHeight());
                    } else {
                        c = texture.getBackgroundColor();
                    }
                    pictureBuffer.setColor(pictureBuffer.getHeight()-yStart-1,(int)x1-(thickness/2)+xi,c);
                }
                yStart++;
            }
            return;
        }

        if(nom == 0){
            /*if the gradient of the segment equals 0, then the segment has to be parallel to the x-axis*/
            int xStart,xEnd;
            if(x1 < x2){
                xStart = (int)x1;
                xEnd = (int)x2;
            } else {
                xStart = (int)x2;
                xEnd = (int)x1;
            }
            float n = yAxisIntersection(x1,y1,0);
            while(xStart <= xEnd){
                for (int yi = 0;yi < thickness;yi++){
                    int c;
                    if (texture != null && texture.getTexture() != null){
                        c = texture.getColor(xStart%texture.getTexture().getWidth(),(int)v1.get(1));
                    } else {
                        c = texture.getBackgroundColor();
                    }
                    pictureBuffer.setColor(pictureBuffer.getHeight()-(int)n-(thickness/2)+yi-1,xStart,c);
                }
                xStart++;
            }
            return;
        }

        float m = nom / denom;
        float n = yAxisIntersection(x1,y1,m);

        int x;
        int xEnd;
        if(x1 < x2){
            x = (int)x1;
            xEnd = (int)x2;
        } else {
            x = (int)x2;
            xEnd = (int)x2;
        }
        while(x < xEnd){
            int y = (int)linearFunction(x,m,n);
            for(int i = x-thickness/2;i < x+thickness/2;i++) {
                if(i >= 0 && i < pictureBuffer.getWidth() && y >= 0 && y <= pictureBuffer.getHeight()){
                    int c = 0;
                    if(texture != null && texture.getTexture() != null){
                        c = texture.getColor(x%texture.getTexture().getWidth(),y%texture.getTexture().getHeight());
                    } else {
                        c = texture.getBackgroundColor();
                    }
                    pictureBuffer.setColor(pictureBuffer.getHeight()-y-1,i,c);
                }
            }
            x++;
        }

    }

}
