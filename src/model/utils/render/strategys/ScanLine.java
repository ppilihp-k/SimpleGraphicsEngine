package model.utils.render.strategys;

import model.elementstate.AffineTextureMappingStrategy;
import model.elementstate.MaterialLightState;
import model.elementstate.MaterialTextureState;
import model.elementstate.TextureInterpolationStrategy;
import model.utils.PaintBox;
import model.utils.PictureBufferState;
import singlethreadedGeometrie.geometricCalc.model.Vector;

/**
 * Created by PhilippKroll on 09.11.2016.
 */
public class ScanLine {

    private PaintBox pb;

    private TextureInterpolationStrategy strategy;

    public ScanLine(){
        strategy = new AffineTextureMappingStrategy();
        pb = new PaintBox();
    }

    /**
     * computes the x value of the linear function given by v1 and v2 at the f(x) value 'row'
     * this is a simple change of the equation f(x)=mx+n to (f(x)-n) / m = x
     * @param v1 a vector on the segment determined by v1 and v2
     * @param v2 a vector on the segment determined by v1 and v2
     * @param row the y or f(x) value, which is necessary to compute x from this segment
     * @return returns the x value, as float, from the segment, represented by v1 and v2, at the y value of row
     */
    protected float getX(Vector v1, Vector v2, float row){
        float nom = (float)v2.get(1) - (float)v1.get(1);
        if(nom == 0){
            return (float)v1.get(1);
        }
        float denom = (float)v2.get(0) - (float)v1.get(0);
        if(denom == 0){
            return (float)v1.get(0);
        }
        float m = nom / denom;
        float n = (float)v1.get(1) - (m * (float)v1.get(0));
        return (row - n) / m;
    }

    /**
     * fills a row with the color information of the texture, if the texture has no image, the backgroundcolor is filled in
     * the row and col values have to be in the range of 0 <= row < pbs.getHeight() and 0 <= colStart,colEnd < pbs.getWidth() otherwise
     * nothing is filled into pbs,
     * also takes the alpha value into account!
     * @param row the row which is painted
     * @param colStart the x start value
     * @param colEnd the x end value
     * @param t the texture to fill in
     * @param pbs the picturebuffer to paint in
     */
    protected void fillRow(int row, int colStart, int colEnd, MaterialTextureState t, MaterialLightState l, PictureBufferState pbs){
        int h = pbs.getHeight();
        int w = pbs.getWidth();
        while(row >= 0 && row < h && colStart >= 0 && colStart <= colEnd && colStart < w){
            int c = 0;
            if(t != null && t.getTexture() != null){
                //both states are not null, apply lighting to the texture
                //c = t.getColor(row%t.getTexture().getHeight(),colStart%t.getTexture().getWidth());

                //System.out.println("test texture mapping");
                Vector v0 = new Vector(2);
                v0.set(0,colStart);
                v0.set(1,row);
                Vector v1 = new Vector(2);
                v1.set(0,colEnd);
                v1.set(1,row);
                c = strategy.interpolate(t.getTexture(),v0,v1,colStart/colEnd);

            } else {
                if(l != null){
                    //materualstate is null. render with the backgroundcolor and apply the ligthinginformation
                    int light = (int)l.getColor();
                    c = pb.addColors(t.getBackgroundColor(),light);
                    //c = t.getBackgroundColor();
                    //System.out.println("Scanline.fillrow: black, white and grey value implementation!");
                    //System.out.println("alpha:"+pb.getAlpha(c)+" red:"+pb.getRed(c)+" green:"+pb.getGreen(c)+" blue:"+pb.getBlue(c));
                } else {
                    //materialstate and lightingstate are both null! simply render with the babkgroundcolor
                    c = t.getBackgroundColor();
                }
            }

            int pbsRow = h - row - 1;
            if(pb.getAlpha(c) < 255){
                int cPbs = pbs.getColor(pbsRow,colStart);
                int af = ((c >> 24) & 0xFF) / 255;
                int r = af * pb.getRed(c) + pb.getRed(cPbs);
                int g = af * pb.getGreen(c) + pb.getGreen(cPbs);
                int b = af * pb.getBlue(c) + pb.getGreen(cPbs);
                c = pb.processARGB(255,r,g,b);
            }
            pbs.setColor(pbsRow,colStart,c);
            colStart++;
        }
    }

    protected float linearFunction(int x, float m,float n){
        return (x*m)+n;
    }

    protected float yAxisIntersection(float x,float y, float m){
        return y - (m * x);
    }
}
