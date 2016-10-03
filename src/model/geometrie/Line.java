package model.geometrie;

import geometricCalculus.model.Vector;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class Segment extends Polygon{

    public Segment(Vector v1, Vector v2){
        super(3,v1,v2);
    }

    public String toString(){
        return "line";
    }
}
