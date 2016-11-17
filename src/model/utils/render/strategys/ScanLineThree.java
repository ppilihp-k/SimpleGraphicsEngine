package model.utils.render.strategys;

import model.elementstate.MaterialLightState;
import model.elementstate.MaterialTextureState;
import model.utils.PictureBufferState;
import singlethreadedGeometrie.geometricCalc.GeometricCalculator;
import singlethreadedGeometrie.geometricCalc.model.Vector;

/**
 * Created by PhilippKroll on 09.11.2016.
 */
public class ScanLineThree extends ScanLine{

    private GeometricCalculator gc;

    public ScanLineThree(GeometricCalculator gc){
        super();
        this.gc = gc;
    }

    public ScanLineThree(){
        super();
        this.gc = new GeometricCalculator();
    }

    public void scanThree(Vector v1, Vector v2, Vector v3, MaterialTextureState texture, MaterialLightState l, PictureBufferState pictureBufferState){
        //System.out.println("scanTrhee: "+l.getColor());
        /*test, if the vectors satisfy the requirements*/
        if(v1.length() != 2 || v2.length() != 2 || v3.length() != 2){
            throw new IllegalArgumentException();
        }
        int width = pictureBufferState.getWidth();
        int height = pictureBufferState.getHeight();
        if(v1.get(0) < 0 || v1.get(0) >= width || v2.get(0) < 0 || v2.get(0) >= width || v3.get(0) < 0 || v3.get(0) >= width || v1.get(1) < 0 || v1.get(1) >= height || v2.get(1) < 0 || v2.get(1) >= height || v3.get(1) < 0 || v3.get(1) >= height){
            throw new IllegalArgumentException("v1: x="+v1.get(0)+",y="+v1.get(1)+"\n"+"v2: x="+v2.get(0)+",y="+v2.get(1)+"\n"+"v3: x="+v3.get(0)+",y="+v3.get(1));
        }

        /*go through the single lines and determine the min and max values*/
        /*determine y-mid, y-max and y-min*/
        //sortByY(v1,v2,v3);

        Vector tmp;
        if(v3.get(1) > v1.get(1)){
            //System.out.println("v3 grüßer v1");
            tmp = v1;
            v1 = v3;
            v3 = tmp;
        }
        if(v1.get(1) < v2.get(1)){
            //System.out.println("v2 größer v1");
            tmp = v1;
            v1 = v2;
            v2 = tmp;
        }
        if(v2.get(1) < v3.get(1)){
            //System.out.println("v2 größer v3");
            tmp = v3;
            v3 = v2;
            v2 = tmp;
        }

        /*check, if v2 is on the left or on the right side of vector v1v3*/
        //System.out.println(gc);
        Vector v1v3 = gc.subtract(v3,v1);
        Vector v1v2 = gc.subtract(v2,v1);
        float scalarprodukt = (float)v1v3.get(0)*(float)v1v2.get(1) - (float)v1v3.get(1)*(float)v1v2.get(0);

        if(scalarprodukt < 0){
            //System.out.println("links");
            /*the vector v2 is located on the left side of v1v3. this means, that the minboarder is the vector v1v3 and the maxborder are the vectors v1v2 and v2v3*/
            int y = (int)v3.get(1);
            while(y < (int)v1.get(1)){
                int xMax = (int)getX(v1,v3,y);
                int xMin;
                if(y <= (int)v2.get(1)){
                    xMin = (int)getX(v2,v3,y);
                } else {
                    xMin = (int)getX(v1,v2,y);
                }
                //System.out.println("(x:"+xMin+",y:"+y+")");
                fillRow(y,xMin,xMax,texture,l,pictureBufferState);
                y++;
            }
        } else {
            //System.out.println("rechts");
            /*the vector v2 is located on the right side of v1v3. this means, that the minboarder are the vectors v1v2 and v2v3 and the maxborder is the vector v1v3*/
            int y = (int)v3.get(1);
            int yMax = (int)v1.get(1);
            while(y <= yMax){
                int xMin = (int)getX(v1,v3,y);
                int xMax;
                if(y <= (int)v2.get(1)){
                    xMax = (int)getX(v2,v3,y);
                } else {
                    xMax = (int)getX(v1,v2,y);
                }
                //System.out.println(y);
                //System.out.println("(x:"+xMin+",y:"+y+")");
                fillRow(y,xMin,xMax,texture,l,pictureBufferState);
                y++;
            }
        }
        //System.out.println("fertig!");
    }
}
