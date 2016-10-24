package model.utils;

import exceptions.InfiniteResultsException;
import exceptions.InternalErrorException;
import exceptions.NoResultException;
import geometricCalculus.GeometricCalculator;
import geometricCalculus.model.Matrix;
import geometricCalculus.model.Plane;
import geometricCalculus.model.Segment;
import geometricCalculus.model.Vector;
import model.geometrie.Line;
import model.geometrie.Polygon;
import model.geometrie.Triangle;

import javax.swing.text.View;
import java.util.LinkedList;

/**
 * Created by PhilippKroll on 25.09.2016.
 */
public class ViewFrustumControler {

    public static enum ROTATION_AXIS {DIRECTION,RIGHT,UP};
    public GeometricCalculator gc;

    public ViewFrustumControler(GeometricCalculator gc){
        this.gc = gc;
    }

    /**
     * rotates the viewfrustum around the desired axis
     * @param vf the viewfrustum, which should be rotated
     * @param angle the angles in degree
     * @param axis the axis to rotate around
     */
    public void rotateFrustum(ViewFrustum vf,float angle,ROTATION_AXIS axis){
        angle = angle % 360;
        Matrix m;
        switch (axis){
            case RIGHT:
                m = getRotationMatrix(ROTATION_AXIS.RIGHT,angle);
                vf.setCameraUp(gc.multiply(m,vf.getCameraUp()));
                vf.setCameraDirection(gc.multiply(m,vf.getCameraDirection()));
                break;
            case DIRECTION:
                m = getRotationMatrix(ROTATION_AXIS.DIRECTION,angle);
                vf.setCameraRight(gc.multiply(m,vf.getCameraRight()));
                vf.setCameraUp(gc.multiply(m,vf.getCameraUp()));
                break;
            case UP:
                m = getRotationMatrix(ROTATION_AXIS.UP,angle);
                vf.setCameraRight(gc.multiply(m,vf.getCameraRight()));
                vf.setCameraDirection(gc.multiply(m,vf.getCameraDirection()));
                break;
        }
    }

    /**
     * the method creates a 3d-matrix which represents a rotation around x,y or z axis for
     * the given angle "angle"
     * @param axis name of the rotation axis
     * @param angle the angle of the rotation
     * @return a new matrix, which represents the specified rotation
     */
    private Matrix getRotationMatrix(ROTATION_AXIS axis, float angle){
        Matrix m = new Matrix(3);
        switch (axis){
            case UP:
                m.set(0,0,Math.cos(angle));
                m.set(0,2,Math.sin(angle));
                m.set(1,1,1);
                m.set(2,0,-Math.sin(angle));
                m.set(2,2,Math.cos(angle));
                break;
            case RIGHT:
                m.set(1,0,1);
                m.set(1,1,Math.cos(angle));
                m.set(1,2,-Math.sin(angle));
                m.set(2,1,Math.sin(angle));
                m.set(2,2,Math.cos(angle));
                break;
            case DIRECTION:
                m.set(0,0,Math.cos(angle));
                m.set(0,1,-Math.sin(angle));
                m.set(1,0,Math.sin(angle));
                m.set(1,1,Math.cos(angle));
                m.set(2,2,1);
                break;
        }
        return m;
    }

    /**
     * translates the viewfrustum
     * @param vf the viewfrustum
     * @param v the vector, which is applied to the frustum
     */
    public void moveWolrdPosition(ViewFrustum vf,Vector v){
        Vector vo = vf.getCameraOrigin();
        gc.translate(vo,v);
        vf.setCameraOrigin(vo);
    }

    /**
     * tests, if the vector v is in between near and far, left and right, bottom and top planes
     * @param v the vector to test
     * @param vf the viewfrustum
     * @return true, if v is inside the frustum, false otherwise
     */
    public boolean isInside(Vector v,ViewFrustum vf){
        float n = testPlanePointNormalForm(v,vf.getNearNormal(),vf.getCameraOrigin());
        float f = testPlanePointNormalForm(v,vf.getFarNormal(),vf.getCameraOrigin());
        float l = testPlanePointNormalForm(v,vf.getLeftNormal(),vf.getCameraOrigin());
        float r = testPlanePointNormalForm(v,vf.getRightNormal(),vf.getCameraOrigin());
        float t = testPlanePointNormalForm(v,vf.getTopNormal(),vf.getCameraOrigin());
        float b = testPlanePointNormalForm(v,vf.getBottomNormal(),vf.getCameraOrigin());
        /*
        System.out.println(vf);
        System.out.println(v);
        System.out.println("n "+n);
        System.out.println("f "+f);
        System.out.println("l "+l);
        System.out.println("r "+r);
        System.out.println("t "+t);
        System.out.println("b "+b);
*/

        return n >= 0 && f <= 0 && l >= 0 && r <= 0 && t <= 0 && b >= 0;
    }

    /**
     * computes the value from: normal * ( v - planeOrigin )
     * @param v the vector that is tested
     * @param normal the normal representing the plane where planeOrigin is the origin
     * @param planeOrigin the plane origin
     * @return the outcome of the normal-point equasion
     */
    private float testPlanePointNormalForm(Vector v, Vector normal, Vector planeOrigin){
        /*point-normal form: normal * (v - planeOrigin) = 0 */
        //System.out.println(v);
        if(gc.length(normal) != 1){
            gc.toUnitLength(normal);
        }
        //System.out.println((float)gc.scalarproduct(normal,gc.subtract(v,planeOrigin)));
        return (float)gc.scalarproduct(normal,gc.subtract(v,planeOrigin));
    }

    /**
     * the method constructs a line between the eye-point of the viewfrustum and each of the vertecies of the polygon.
     * the calculates the intersection with the near plane of the frustum and returns a new two dimensional polygon, which can be drawn be the renderer
     * @param p the polygon to be drawn
     * @param vf the viewfrustum, to test the polygon
     * @return a new 2 dimensional polygon, which hase 0 <= x < viewfrustums nearplane width und 0 <= y < viewfrustums near plane height
     * @throws Exception in case of an error, such as: the polygon hase a vertex, which is not visible to the user.
     */
    public Polygon toScreenPolygon(Polygon p,ViewFrustum vf) throws Exception{
        Vector[] verts = p.getVertices();
        Vector[] sverts = new Vector[verts.length];

        for (int i = 0; i < sverts.length; i++){
            sverts[i] = toScreenVector(p.getVertices()[i],vf.getCameraOrigin(),vf.getNearPlane());
            float t = (float)(Math.abs(sverts[i].get(0)+(vf.getWidth()/2))/vf.getWidth());
            sverts[i].set(0,t*vf.getWidth());
            t = (float)(Math.abs(sverts[i].get(1)+(vf.getHeight()/2))/vf.getHeight());
            sverts[i].set(1,t*vf.getHeight());
            /*problem with negative positioning!*/
        }
        if(p instanceof Line){
            Line l = new Line(sverts[0],sverts[1]);
            l.setTextureInfo(p.getTextureInfo());
            return l;
        }
        if(p instanceof Triangle){
            Triangle t = new Triangle(sverts[0],sverts[1],sverts[2]);
            t.setTextureInfo(p.getTextureInfo());
            return t;
        }
        return null;
    }

    /**
     *  calculates an equivalent vertex in 2d
     * @param v the vertex
     * @param origin the eye-point
     * @param screen the plane, describing the screen
     * @return a new 2dimensional vector, with exact screen coordinates
     * @throws Exception normally there is no case an exception can be throw, but if so its a fatal error
     */
    private Vector toScreenVector(Vector v,Vector origin,Plane screen) throws NoResultException, InfiniteResultsException, InternalErrorException {
        Segment ov = new Segment(origin.clone(),gc.subtract(v,origin));
        //System.out.println("line!");
        //System.out.println(origin.clone());
        //System.out.println(gc.subtract(v,origin));
        Vector n = gc.crossprodukt(screen.getDirectionalVectorOne(),screen.getDirectionalVectorTwo());
        return gc.intersect(ov,screen).subVector(0,2);
    }

}
