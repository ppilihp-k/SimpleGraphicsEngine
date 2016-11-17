package model.utils.render;

import exceptions.InternalErrorException;
import exceptions.NoPropperDimensionException;
import model.geometrie.Line;
import model.geometrie.Polygon;
import model.geometrie.Triangle;
import model.utils.render.strategys.MyRenderStrategy;
import model.utils.render.strategys.RenderStrategy;
import model.utils.render.strategys.ScanLineTwo;
import singlethreadedGeometrie.geometricCalc.GeometricCalculator;
import singlethreadedGeometrie.geometricCalc.model.Segment;
import singlethreadedGeometrie.geometricCalc.model.Vector;
import model.elementstate.MaterialTextureState;
import model.utils.PaintBox;
import model.utils.PictureBufferState;
import utils.Tupel;
import exceptions.*;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by PhilippKroll on 19.08.2016.
 */
public class Renderer {

    private GeometricCalculator gc;

    private boolean gridMode;

    private int gridColor;

    private Line x,y,z;

    private ScanLineTwo scanLineTwo;

    private RenderStrategy strategy;

    private PaintBox pb;

    private RenderMonitor monitor;

    public Renderer(GeometricCalculator gc){
        gridMode = false;
        gridColor = 255 << 24 | 255 << 16 | 255 << 8 | 255;
        this.gc = gc;
        strategy = new MyRenderStrategy(gc);
        scanLineTwo = new ScanLineTwo();
        pb = new PaintBox();
        //monitor = new RenderMonitor();
    }

    public void submit(Polygon p,PictureBufferState pbs){
        //RenderTask rt = new RenderTask(p,pbs);
        //monitor.submit(rt);
        scanline(pbs,p);
    }

    /**
     * sets a new strategy for the renderer
     * @param r
     */
    public void setRenderStrategy(RenderStrategy r){
        if(r != null){
            this.strategy = r;
        }
    }

    /**
     * getter for the render strategy
     * @return
     */
    public RenderStrategy getRenderStrategy(){
        return strategy;
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
     * this method is used to determine the linesegments of the polygon p are inside the quadrat, defined by the 4 vectors
     * @param p
     * @param vertecies
     * @return
     * @throws NoPropperDimensionException
     */
    public LinkedList<Vector> clip(Polygon p, Vector ... vertecies) throws NoPropperDimensionException,NoResultException,InfiniteResultsException,InternalErrorException{
        /*interprese the vertecies as a polygon -> imagine, pairwise edges between the vertecies*/
        if(p.getDimension() != 2){
            NoPropperDimensionException npe = new NoPropperDimensionException();
            npe.getMessage().concat("\n\"clipping is constructed only for two dimensional polygons");
            throw npe;
        }
        LinkedList<Vector> output  = p.getVertexList();
        LinkedList<Vector> input = new LinkedList<>();
        LinkedList<Tupel<Vector,Vector>> clipEdges = toEdges(vertecies);

        /*clip against the the edges of the polygon, defined by vertecies*/
        /*traverse all edges of the clip polygon*/
        for (Tupel<Vector,Vector> clipEdge: clipEdges) {
            input.addAll(output);
            output.clear();
            Vector S = input.getLast();
            for (Vector E: input) {
                boolean eCw = isClockwise(E,clipEdge);
                if(eCw){
                    /*E/clipEdge.frist is located clockwise from clipEdge.second/clipEdge.frist*/
                    boolean sCw = isClockwise(S,clipEdge);
                    if(!sCw){
                        Segment s1 = new Segment(E,gc.subtract(E,S));
                        Segment s2 = new Segment(clipEdge.first,gc.subtract(clipEdge.second,clipEdge.first));
                        try{
                            output.add(gc.intersect(s1,s2));
                        } catch (Exception e){
                            throw e;
                        }
                    }
                    output.add(E);
                } else if(isClockwise(S,clipEdge)){
                    Segment s1 = new Segment(E,gc.subtract(E,S));
                    Segment s2 = new Segment(clipEdge.first,gc.subtract(clipEdge.second,clipEdge.first));
                    try{
                        output.add(gc.intersect(s1,s2));
                    } catch (Exception e){
                        throw e;
                    }
                }
                S = E;
            }
        }
        /*create the new polygons*/
        LinkedList<Polygon> outputPolygons = new LinkedList<>();
        if(true){
            return output;
        }
        if(output.size() == 2){
            outputPolygons.add(new Line(output.poll(),output.poll()));
        } else if(output.size() % 2 == 0){
            /*the number of vertecies is even, it is possible to construct |output| / 2 triangles*/

        } else {
            /*form triangles -> the number ob vertecies is odd*/
        }
        return null;
    }

    private boolean isClockwise(Vector v,Tupel<Vector,Vector> t){
        double sp = gc.scalarproduct(gc.subtract(v,t.first),gc.subtract(t.second,t.first));
        System.out.println(sp >= 0);
        return sp >= 0;
    }

    /**
     * creates a list of edges from a set of vectors
     * @param verts the vectors, which form a polygon
     * @return a list with edges
     */
    private LinkedList<Tupel<Vector,Vector>> toEdges(Vector ... verts){
        LinkedList<Tupel<Vector,Vector>> output = new LinkedList<>();
        for (int i = 0;i < verts.length;i++){
            Tupel<Vector,Vector> t = new Tupel<>();
            t.first = verts[i];
            t.second = verts[(i+1)%verts.length];
            output.add(t);
        }
        return output;
    }

    /**
     * draws the given polygon to the imagebuffer of this renderer.
     * the vectors have to contain the two dimensional representation of the polygon, plus the depth infortmation for the zbuffer!
     * so make sure, the input is already a two dimensional representation of polygon, that should be painted, and the third vector position contains the depth information
     * single-threaded method call!
     * @param p a polygon
     * @throws IllegalArgumentException the vertices have to define either a point, a segment, a triangle or a rectangle, otherwise a exception is thrown
     */
    public void scanline(PictureBufferState pictureBuffer, Polygon p){
        if(gridMode){
            if(p instanceof Line){
                Vector[] verts = p.getVertices();
                if(verts[0].length() != 2 || verts[1].length() != 2){
                    throw new IllegalArgumentException();
                }
                MaterialTextureState gridTexture = new MaterialTextureState(null);
                gridTexture.setBackgroundColor(gridColor);
                scanLineTwo.scanTwo(verts[0],verts[1],2,gridTexture,pictureBuffer);
            } else if(p instanceof Triangle){
                Vector[] verts = p.getVertices();
                MaterialTextureState gridTexture = new MaterialTextureState(null);
                gridTexture.setBackgroundColor(gridColor);
                scanLineTwo.scanTwo(verts[0],verts[1],2,gridTexture,pictureBuffer);
                scanLineTwo.scanTwo(verts[0],verts[2],2,gridTexture,pictureBuffer);
                scanLineTwo.scanTwo(verts[1],verts[2],2,gridTexture,pictureBuffer);
            } else {
                Set<Triangle> set = p.toTriangles();
                for (Triangle t:set){
                    Vector[] verts = t.getVertices();
                    if(verts[0].length() != 2 || verts[1].length() != 2 || verts[2].length() != 2){
                        throw new IllegalArgumentException();
                    }
                    MaterialTextureState gridTexture = new MaterialTextureState(null);
                    gridTexture.setBackgroundColor(gridColor);
                    scanLineTwo.scanTwo(verts[0],verts[1],2,gridTexture,pictureBuffer);
                    scanLineTwo.scanTwo(verts[0],verts[2],2,gridTexture,pictureBuffer);
                    scanLineTwo.scanTwo(verts[1],verts[2],2,gridTexture,pictureBuffer);
                }
            }
        } else {
            strategy.render(pictureBuffer,p);
        }
    }

    public void clear(PictureBufferState pbs){
        if(gridMode){
            int c = (pbs.getBackgroundcolor().getAlpha() << 24) | pbs.getBackgroundcolor().getRGB();
            pbs.setBackgroundcolor(pb.processARGB(255,0,0,0));
            pbs.clear();
            pbs.setBackgroundcolor(c);
        } else {
            pbs.clear();
        }
    }

    private void checkGeometricCalculator(){
        if(this.gc == null){
            gc = new GeometricCalculator();
        }
    }
}
