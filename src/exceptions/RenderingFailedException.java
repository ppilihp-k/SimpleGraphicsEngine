package exceptions;

import model.geometrie.Polygon;

/**
 * Created by PhilippKroll on 03.10.2016.
 */
public class RenderingFailedException extends Exception{
    private Polygon p;
    public RenderingFailedException(Polygon p){
        this.p = p;
    }
    public Polygon getContent(){
        return p;
    }
}
