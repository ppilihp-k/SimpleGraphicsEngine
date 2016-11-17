package model.utils.render;

import model.geometrie.Line;
import model.geometrie.Polygon;
import model.geometrie.Triangle;
import model.utils.PictureBufferState;
import model.utils.render.strategys.ScanLineThree;
import model.utils.render.strategys.ScanLineTwo;

import java.util.Observable;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by PhilippKroll on 09.11.2016.
 */
public class RenderTask implements Callable<RenderTask>{

    private Polygon p;

    private PictureBufferState pbs;

    private boolean finished;

    public RenderTask(Polygon p, PictureBufferState pbs){
        this.p = p;
        this.pbs = pbs;
        finished = false;
    }

    public RenderTask call(){
        //begin to render this polygon!
        if(p instanceof Line){
            (new ScanLineTwo()).scanTwo(p.getVertices()[0],p.getVertices()[1],2,p.getTextureInfo(),pbs);
        } else if(p instanceof Triangle){
            (new ScanLineThree()).scanThree(p.getVertices()[0],p.getVertices()[1],p.getVertices()[2],p.getTextureInfo(),p.getLightningInfo(),pbs);
        } else {
            Set<Triangle> set = p.toTriangles();
            for (Triangle t: set) {
                (new ScanLineThree()).scanThree(t.getVertices()[0],t.getVertices()[1],t.getVertices()[2],p.getTextureInfo(),p.getLightningInfo(),pbs);
            }
        }
        setFinished();
        return this;
    }

    public boolean hasFinished(){
        return finished;
    }

    public void setFinished(){
        finished = true;
    }

    public Polygon getPolygon(){
        return p;
    }
}
