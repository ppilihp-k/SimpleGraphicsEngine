package model.utils;

import exceptions.NothingDoneException;
import geometricCalculus.GeometricCalculator;
import geometricCalculus.model.Matrix;
import geometricCalculus.model.Vector;
import model.geometrie.Polygon;

import java.awt.image.BufferedImage;

/**
 * Created by PhilippKroll on 02.10.2016.
 * this class provides basic methods to manipulate polygons, such as translation, rotation or scale<p>
 * it should be used as some kind of controller for polygons!<p>
 * this class has no attributes, only a geometric calculator where mathematical computation are sourced out to<p>
 */
public class PolygonManipulationTool {

    public static enum Axis{
        X,Y,Z
    }

    private GeometricCalculator gc;

    public PolygonManipulationTool(GeometricCalculator gc){
        if(gc == null){
            this.gc = new GeometricCalculator();
        } else {
            this.gc = gc;
        }
    }

    public PolygonManipulationTool(){
        gc = new GeometricCalculator();
    }

    /**
     * this method translates all vertecies of the polygon p with the given vector<p>
     * this method manipulates the original vertecies of p!<p>
     * @param translate the translation vector
     * @param p the polygon to be translated
     * @throws NothingDoneException if the polygon equals null
     */
    public void translate(Vector translate, Polygon p) throws NothingDoneException{
        if(translate == null || p == null){
            throw new NothingDoneException();
        }
        Vector[] verts = p.getVertices();
        for (Vector v: verts) {
            Vector tmp = gc.add(v,translate);
            for (int i = 0;i < v.length();i++){
                v.set(i,tmp.get(i));
            }
        }
    }

    /**
     * rotates the polygon around the given axis and the given origin.<p>
     * if the relativeTo vector equals null, the rotation is performed relative to (0,0,0)<p>
     * this method manipulates the original vertecies of p!<p>
     * @param axis the axis to rotate around
     * @param angle the angle to rotate in degree
     * @param relativeTo the vertex to which is rotated relative to
     * @param p the polygon to rotate
     * @throws NothingDoneException
     */
    public void rotate(Axis axis,float angle,Vector relativeTo,Polygon p) throws NothingDoneException{
        if(p == null){
            throw new NothingDoneException();
        }

        //if(relativeTo == null){
            /*rotate relative to (0,0,0)*/
        //    return;
        //}
        /*rotate relative to the given relativTo vector*/
        Matrix m = createRotationMatrix(axis,angle);
        Vector[] verts = p.getVertices();
        for (Vector v:verts) {
            Vector cp;
            if (relativeTo != null){
                cp = gc.subtract(v,relativeTo);
            } else {
                cp = v;
            }
            Vector tmp = gc.multiply(m,cp);
            if(relativeTo != null){
                cp = gc.add(tmp,relativeTo);
            } else {
                cp = tmp;
            }
            //BufferedImage img = new BufferedImage(p.getTexture().getWidth(),p.getTexture().getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
            for (int i = 0;i < v.length();i++){
                v.set(i,cp.get(i));
            }
        }
    }

    public void rotateImage(Matrix rotationMatrix,BufferedImage img){

    }

    /**
     * this method creates a rotation matrix for the desired axis
     * @param axis either x,y or z axis
     * @param angle the angle for rotation
     * @return a 3x3 matrix containing the rotation information
     */
    public Matrix createRotationMatrix(Axis axis,float angle){
        angle = (float)Math.toRadians(angle);
        Matrix m = new Matrix(3);
        switch (axis){
            case X:
                m.set(0,0,1);
                m.set(1,1,Math.cos(angle));
                m.set(1,2,-1*Math.sin(angle));
                m.set(2,1,Math.sin(angle));
                m.set(2,2,Math.cos(angle));
                return m;
            case Y:
                m.set(0,0,Math.cos(angle));
                m.set(0,2,Math.sin(angle));
                m.set(1,1,1);
                m.set(2,0,-1*Math.sin(angle));
                m.set(2,2,Math.cos(angle));
                return m;
            case Z:
                m.set(0,0,Math.cos(angle));
                m.set(0,1,-1*Math.sin(angle));
                m.set(1,0,Math.sin(angle));
                m.set(1,1,Math.cos(angle));
                m.set(2,2,1);
                return m;
            default:
                m.set(0,0,1);
                m.set(1,1,1);
                m.set(2,2,1);
                return m;
        }
    }

    /**
     * scales the polygon p with the desired values for each axis
     * this means this scales every vertex of p
     * this method manipulates the original vertecies of p!
     * @param x the scale in direction x
     * @param y the scale in direction y
     * @param z the scale in direction z
     * @param p the polygon to be manipulated
     */
    public void scale(float x, float y, float z, Polygon p){
        Matrix m = createScalingMatrix(x,y,z);
        Vector[] verts = p.getVertices();
        for (Vector v: verts) {
            Vector tmp = gc.multiply(m,v);
            for (int i = 0;i < v.length();i++){
                v.set(i,tmp.get(i));
            }
        }
    }

    /**
     * creates a scalingmatrix with the desired values
     * @param x the scale in direction x
     * @param y the scale in direction y
     * @param z the scale in direction z
     * @return a 3x3 matrix containing the scaling information
     */
    public Matrix createScalingMatrix(float x,float y, float z){
        Matrix m = new Matrix(3);
        m.set(0,0,x);
        m.set(1,1,y);
        m.set(2,2,z);
        return m;
    }
}
