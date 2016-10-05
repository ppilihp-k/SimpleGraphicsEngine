package model.utils.render;

import geometricCalculus.GeometricCalculator;
import geometricCalculus.model.Vector;
import model.elementstate.MaterialLightState;
import model.elementstate.MaterialTextureState;
import model.utils.PictureBufferState;

/**
 * Created by PhilippKroll on 19.08.2016.
 */
public class Renderer {

    private GeometricCalculator gc;

    private boolean gridMode;

    private int gridColor;

    public Renderer(GeometricCalculator gc){
        gridMode = false;
        gridColor = 255 << 24 | 255;
        this.gc = gc;
    }

    /**
     * getter for the gridmodeflag
     * the gridmode draws every polygon with a 1 pixel border. the border is drawn inside the polygons real borders!
     * @return true, if gridmode is enabled and false otherwise
     */
    public boolean isGridMode(){
        return gridMode;
    }

    /**
     * setter for the gridmode
     * the gridmode draws every polygon with a 1 pixel border. the border is drawn inside the polygons real borders!
     * @param b
     */
    public void setGridMode(boolean b){
        gridMode = b;
    }

    /**
     * if gridmode is enabled, the grid is displayed in its gridcolor.
     * this method sets the grids color.
     * the grid colors alpha value is not adjustable, thus it is set to 255, if it is not.
     * the color is used in argb type.
     * @param c
     */
    public void setGridColor(int c){
        if(c >> 24 != 255){
            c = c | (255 << 24);
        }
        gridColor = c;
    }

    /**
     * returns the grids color, which is used in gridmode to display the grid.
     * @return an integer, representing the actual grid color in argb standard
     */
    public int getGridColor(){
        return gridColor;
    }

    /**
     * draws the given polygon to the imagebuffer of this renderer.
     * the vectors have to contain the two dimensional representation of the polygon, plus the depth infortmation for the zbuffer!
     * so make sure, the input is already a two dimensional representation of polygon, that should be painted, and the third vector position contains the depth information
     * @param vectors the vertices, which define a polygon
     * @throws IllegalArgumentException the vertices have to define either a point, a segment, a triangle or a rectangle, otherwise a exception is thrown
     */
    public void scanline(PictureBufferState pictureBuffer, Vector... vectors){
        if(vectors.length < 0 || vectors.length > 4){
            throw new IllegalArgumentException();
        }
        if(vectors.length == 2){
            System.out.println("Renderer.scanline() -> scanTwo()");
            //scanTwo(vectors[0].subVector(0,2),vectors[1].subVector(0,2),5,,null,pictureBuffer);
        }
    }

    private void scanOne(Vector v1, MaterialTextureState txs, MaterialLightState mls,PictureBufferState pictureBuffer){
        if(pictureBuffer.getDepth((int)v1.get(1),(int)v1.get(0)) >= v1.get(2)){
            pictureBuffer.setDepth((int)v1.get(1),(int)v1.get(0),(float)v1.get(2));
            pictureBuffer.setColor((int)v1.get(1),(int)v1.get(0),txs.getColor());
        }
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
    public void scanTwo(Vector v1, Vector v2,int thickness,MaterialTextureState texture,PictureBufferState pictureBuffer){
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
                if(gridMode && (yStart == y1 || yStart == y2)){
                    for (int xi = 0;xi < thickness;xi++){
                        pictureBuffer.setColor(pictureBuffer.getHeight()-yStart-1,(int)x1-(thickness/2)+xi,gridColor);
                    }
                } else {
                    for (int xi = 0;xi < thickness;xi++){
                        if(gridMode && (xi == 0 || xi == thickness-1)){
                            pictureBuffer.setColor(pictureBuffer.getHeight()-yStart-1,(int)x1-(thickness/2)+xi,gridColor);
                        } else {
                            int c = texture.getColor();
                            pictureBuffer.setColor(pictureBuffer.getHeight()-yStart-1,(int)x1-(thickness/2)+xi,c);
                        }
                    }
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
                    if(gridMode && (yi == 0 || yi == thickness-1)){
                        pictureBuffer.setColor(pictureBuffer.getHeight()-(int)n-(thickness/2)+yi-1,xStart,gridColor);
                    } else{
                        int c = texture.getColor();
                        pictureBuffer.setColor(pictureBuffer.getHeight()-(int)n-(thickness/2)+yi-1,xStart,c);
                    }
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
                    if(gridMode && (x == 0 || x == thickness-1)){
                        pictureBuffer.setColor(pictureBuffer.getHeight()-y-1,x,gridColor);
                    } else {
                        int c = texture.getColor();
                        pictureBuffer.setColor(pictureBuffer.getHeight()-y-1,i,c);
                    }
                }
            }
            x++;
        }

    }

    private float linearFunction(int x, float m,float n){
        return (x*m)+n;
    }

    private float yAxisIntersection(float x,float y, float m){
        return y - (m * x);
    }

    public void scanThree(Vector v1,Vector v2,Vector v3,MaterialTextureState texture,PictureBufferState pictureBufferState){
        //System.out.println("scanTrhee");
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
                fillRow(y,xMin,xMax,texture,pictureBufferState);
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
                fillRow(y,xMin,xMax,texture,pictureBufferState);
                y++;
            }
        }
        //System.out.println("fertig!");
    }

    /**
     * computes the x value of the linear function given by v1 and v2 at the f(x) value 'row'
     * this is a simple change of the equation f(x)=mx+n to (f(x)-n) / m = x
     * @param v1 a vector on the segment determined by v1 and v2
     * @param v2 a vector on the segment determined by v1 and v2
     * @param row the y or f(x) value, which is necessary to compute x from this segment
     * @return returns the x value, as float, from the segment, represented by v1 and v2, at the y value of row
     */
    private float getX(Vector v1,Vector v2,float row){
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
     * fills a column with the color information of the texture, if the texture has no image, the backgroundcolor is filled in
     * the row and col values have to be in the range of 0 <= rowStart,rowEnd < pbs.getHeight() and 0 <= col < pbs.getWidth() otherwise
     * nothing is filled into pbs
     * @param col the column which is painted
     * @param rowStart the y start value
     * @param rowEnd the y end value
     * @param t the texture to fill in
     * @param pbs the picturebuffer to paint in
     */
    private void fillCol(int col,int rowStart,int rowEnd,MaterialTextureState t,PictureBufferState pbs){
        int h = pbs.getHeight();
        while(rowStart <= rowEnd){
            pbs.setColor(h - rowStart,col,t.getColor());
            rowStart++;
        }
    }

    /**
     * fills a row with the color information of the texture, if the texture has no image, the backgroundcolor is filled in
     * the row and col values have to be in the range of 0 <= row < pbs.getHeight() and 0 <= colStart,colEnd < pbs.getWidth() otherwise
     * nothing is filled into pbs
     * @param row the row which is painted
     * @param colStart the x start value
     * @param colEnd the x end value
     * @param t the texture to fill in
     * @param pbs the picturebuffer to paint in
     */
    private void fillRow(int row,int colStart,int colEnd,MaterialTextureState t,PictureBufferState pbs){
        int h = pbs.getHeight();
        int w = pbs.getWidth();
        //System.out.println("row:"+row+" minX:"+colStart+" maxX:"+colEnd);
        while(row >= 0 && row < h && colStart >= 0 && colStart <= colEnd && colStart < w){
            //System.out.println((t.getColor() >> 24) & 0xff);
            //System.out.println("(x:"+colStart+",y:"+(h - row - 1)+")");
            pbs.setColor(h - row - 1,colStart,t.getColor());
            colStart++;
        }
    }

    /**
     * fills a row with the color information
     * the row and col values have to be in the range of 0 <= row < pbs.getHeight() and 0 <= colStart,colEnd < pbs.getWidth() otherwise
     * nothing is filled into pbs
     * @param row the row which is painted
     * @param colStart the x start value
     * @param colEnd the x end value
     * @param c the color to fill in
     * @param pbs the picturebuffer to paint in
     */
    private void fillRow(int row,int colStart,int colEnd,int c,PictureBufferState pbs){
        int h = pbs.getHeight();
        int w = pbs.getWidth();
        //System.out.println("row:"+row+" minX:"+colStart+" maxX:"+colEnd);
        while(row >= 0 && row < h && colStart >= 0 && colStart <= colEnd && colStart < w){
            //System.out.println((t.getColor() >> 24) & 0xff);
            //System.out.println("(x:"+colStart+",y:"+(h - row - 1)+")");
            pbs.setColor(h - row - 1,colStart,c);
            colStart++;
        }
    }

    /**
     * returns the vector with the highest value at index 'index'
     * @param index
     * @param V
     * @return
     */
    private float getHighest(int index,Vector ... V){
        if(index < 0 || index >= V[0].length()){
            throw new IllegalArgumentException();
        }
        float x = (float)V[0].get(index);
        for (Vector v : V) {
            x = (float)v.get(index) > x ? (float)v.get(index) : x;
        }
        return x;
    }

    /**
     * returns the vector with the lowest value at index 'index'
     * @param index
     * @param V
     * @return
     */
    private float getLowest(int index,Vector ... V){
        if(index < 0 || index >= V[0].length()){
            throw new IllegalArgumentException();
        }
        float x = (float)V[0].get(index);
        for (Vector v : V) {
            x = (float)v.get(index) < x ? (float)v.get(index) : x;
        }
        return x;
    }

    /**
     * returns an integer in ABGR java standard, all values have to be in range of 0 - 255 otherwise the methodcall will have a unpredictable outcome
     * @param r the red value
     * @param g the green value
     * @param b the blue values
     * @param a the alpha value
     * @return an integer representing the desired color
     */
    public int processARGB(int r,int g,int b, int a){
        return (a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
    }

    /**
     * computes the red value of the color c
     * @param c the color
     * @return the red value
     */
    public int getRed(int c){
        return 0xff & c;
    }
    /**
     * computes the green value of the color c
     * @param c the color
     * @return the green value
     */
    public int getGreen(int c){
        return 0xff & (c >> 8);
    }
    /**
     * computes the blue value of the color c
     * @param c the color
     * @return the blue value
     */
    public int getBlue(int c){
        return 0xff & (c >> 16);
    }
    /**
     * computes the aplha value of the color c
     * @param c the color
     * @return the alpha value
     */
    public int getAlpha(int c){
        return 0xff & (c >> 24);
    }

    private void checkGeometricCalculator(){
        if(this.gc == null){
            gc = new GeometricCalculator();
        }
    }
}
