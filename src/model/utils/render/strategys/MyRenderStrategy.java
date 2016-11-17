package model.utils.render.strategys;

import model.geometrie.Polygon;
import model.geometrie.Triangle;
import model.utils.PaintBox;
import model.utils.PictureBufferState;
import singlethreadedGeometrie.geometricCalc.GeometricCalculator;
import singlethreadedGeometrie.geometricCalc.model.Vector;

import java.util.Set;

/**
 * Created by PhilippKroll on 09.11.2016.
 */
public class MyRenderStrategy extends RenderStrategy {

    private ScanLineThree scanLineThree;
    private ScanLineTwo scanLineTwo;

    public MyRenderStrategy(){
        scanLineThree = new ScanLineThree(new GeometricCalculator());
        scanLineTwo = new ScanLineTwo();
    }

    public MyRenderStrategy(GeometricCalculator gc){
        scanLineThree = new ScanLineThree(gc);
        scanLineTwo = new ScanLineTwo();
    }

    public void render(PictureBufferState pbs, Polygon p) {
        scanline(pbs,p);
    }

    /**
     * draws the given polygon to the imagebuffer of this renderer.
     * the vectors have to contain the two dimensional representation of the polygon, plus the depth infortmation for the zbuffer!
     * so make sure, the input is already a two dimensional representation of polygon, that should be painted, and the third vector position contains the depth information
     * @param p a polygon
     * @throws IllegalArgumentException the vertices have to define either a point, a segment, a triangle or a rectangle, otherwise a exception is thrown
     */
    public void scanline(PictureBufferState pictureBuffer, Polygon p){
        if(p.getVertices().length < 0){
            throw new IllegalArgumentException();
        }
        Vector[] vertecies = p.getVertices();
        switch (vertecies.length){
            case 1:
                if(vertecies[0].length() != 2){
                    throw new IllegalArgumentException();
                }
                pictureBuffer.setColor((int)vertecies[0].get(1),(int)vertecies[0].get(0),p.getTextureInfo().getBackgroundColor());
            case 2:
                if(vertecies[0].length() != 2 || vertecies[1].length() != 2){
                    throw new IllegalArgumentException();
                }
                scanLineTwo.scanTwo(vertecies[0],vertecies[1],2,p.getTextureInfo(),pictureBuffer);
                break;
            case 3:
                if(vertecies[0].length() != 2 || vertecies[1].length() != 2 || vertecies[1].length() != 2){
                    throw new IllegalArgumentException();
                }
                scanLineThree.scanThree(vertecies[0],vertecies[1],vertecies[2],p.getTextureInfo(),p.getLightningInfo(),pictureBuffer);
                break;
            default:
                Set<Triangle> set = p.toTriangles();
                for (Triangle t:set) {
                    if(vertecies[0].length() != 2 || vertecies[1].length() != 2 || vertecies[1].length() != 2){
                        throw new IllegalArgumentException();
                    }
                    Vector[] verts = t.getVertices();
                    scanLineThree.scanThree(verts[0],verts[1],verts[2],p.getTextureInfo(),p.getLightningInfo(),pictureBuffer);
                }
                break;
        }
    }

}
