package model.utils;

import geometricCalculus.model.Vector;
import geometricCalculus.GeometricCalculator;
import geometricCalculus.model.Plane;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PhilippKroll on 16.08.2016.
 * this class represents the view-frustum of the user, sitting in front of the screen.
 * the users eyeview is represented by cameraOrigin, together with cameraDirection and cameraUp and cameraRight,
 * these four vectors define a coordinate-system, where cameraOrigin is the zero.
 * the frustum constructed around this system is used for computations, that belong to the categoration of interaction with the user
 */
public class ViewFrustum {

    private enum Normal {
        left,right,top,bot,near,far

    }

    private enum Vertex {
        vTopLeft,vTopRight,vBotRight,vBotLeft,wTopLeft,wTopRight,wBotRight,wBotLeft
    }

    /*the 8 vertices define a frustum*/
    /**
     * this vertex defines the nearest top left corner of the field of view
     */
    private Vector vTopLeft;
    /**
     * this vertex defines the nearest top right corner of the field of view
     */
    private Vector vTopRight;
    /**
     * this vertex defines the nearest bottom left corner of the field of view
     */
    private Vector vBotLeft;
    /**
     * this vertex defines the nearest bottom right corner of the field of view
     */
    private Vector vBotRight;
    /**
     * this vertex defines the most far away top left corner of the field of view
     */
    private Vector wTopLeft;
    /**
     * this vertex defines the most far away top right corner of the field of view
     */
    private Vector wTopRight;
    /**
     * this vertex defines the most far away bottom left corner of the field of view
     */
    private Vector wBotLeft;
    /**
     * this vertex defines the most far away bottom right corner of the field of view
     */
    private Vector wBotRight;

    /*
    * cameraOrigin, cameraDirection, cameraRight and cameraUp
    */
    /**
     * this defines the zero of the field of view coordinate-system
     */
    private Vector cameraOrigin;
    /**
     * defines the direction-axis
     */
    private Vector cameraDirection;
    /**
     * defines the up and down axis
     */
    private Vector cameraUp;
    /**
     * defines the left and right axis
     */
    private Vector cameraRight;

    /*
    * the coorinate-system is bounded by the following constants:
    */
    /**
     * defines the depth min range
     */
    private double distanceDmin;
    /**
     * defines the depth max range
     */
    private double distanceDmax;
    /**
     * defines the down range
     */
    private double distanceUmin;
    /**
     * defines the up range
     */
    private double distanceUmax;
    /**
     * defines the left range
     */
    private double distanceRmin;
    /**
     * defines the right range
     */
    private double distanceRmax;

    /**
     * contains the normalvectors of the 6 planes of this frustum, is indexed by the enum "Normal", see above.
     */
    private HashMap<Normal,Vector> normals;

    private GeometricCalculator gc;

    public ViewFrustum(int height, int width, int depth,GeometricCalculator gc){
        distanceRmin = -1*width/2d;
        distanceRmax = width/2d;
        distanceUmin = -1*height/2d;
        distanceUmax = height/2d;
        distanceDmin = 450;
        distanceDmax = depth;
        this.gc = gc;
        cameraOrigin = new Vector(3);
        cameraRight = new Vector(3);
        cameraRight.set(0,1);
        cameraUp = new Vector(3);
        cameraUp.set(1,1);
        cameraDirection = new Vector(3);
        cameraDirection.set(2,1);
        normals = new HashMap<>();
    }

    private void checkGeometricCalculator(){
        if(this.gc == null ){
            gc = new GeometricCalculator();
        }
    }

    public void setCameraOrigin(Vector cameraOrigin) {
        this.cameraOrigin = cameraOrigin;
        resetVertices();
    }

    public void setCameraDirection(Vector cameraDirection) {
        if(gc.length(cameraDirection) != 1){
            throw new IllegalArgumentException();
        }
        this.cameraDirection = cameraDirection;
        resetVertices();
    }

    public void setCameraUp(Vector cameraUp) {
        if(gc.length(cameraUp) != 1){
            throw new IllegalArgumentException();
        }
        this.cameraUp = cameraUp;
        resetVertices();
    }

    public void setCameraRight(Vector cameraRight) {
        if(gc.length(cameraRight) != 1){
            throw new IllegalArgumentException();
        }
        this.cameraRight = cameraRight;
        resetVertices();
    }

    public Vector getCameraOrigin() {
        return cameraOrigin;
    }

    public Vector getCameraDirection() {
        return cameraDirection;
    }

    public Vector getCameraUp() {
        return cameraUp;
    }

    public Vector getCameraRight() {
        return cameraRight;
    }

    public Plane getLeftPlane(){
        return new Plane(cameraOrigin,gc.subtract(getwBotLeft(),cameraOrigin),gc.subtract(getwTopLeft(),cameraOrigin));
    }

    public Plane getRightPlane(){
        return new Plane(cameraOrigin,gc.subtract(getwBotRight(),cameraOrigin),gc.subtract(getwTopRight(),cameraOrigin));
    }

    /**
     * returns the nearest vector top left:
     * vTopLeft = cameraOrigin + dMin * cameraDirection + uMax * cameraUp + rMin * cameraRight
     * @return vTopLeft, representing the nearest top left vertex of the viewport
     */
    public Vector getvTopLeft(){
        checkGeometricCalculator();
        if(vTopLeft == null){
            /*1.go minD + minR/2 + minUp/2*/
            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector rMinR = cameraRight.clone();
            gc.scale(rMinR,distanceRmin);
            Vector uMinU = cameraUp.clone();
            gc.scale(uMinU,distanceUmax);

            vTopLeft = gc.add(cameraOrigin,gc.add(dMinD,gc.add(uMinU,rMinR)));
            vTopLeft.set(2,distanceDmin);
        }
        return vTopLeft;
    }

    /**
     * returns the nearest vector bottom left:
     * vTopLeft = cameraOrigin + dMin * cameraDirection + uMin * cameraUp + rMin * cameraRight
     * @return vBotLeft, representing the nearest bottom left vertex of the viewport
     */
    public Vector getvBotLeft(){
        checkGeometricCalculator();
        if(vBotLeft == null){

            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMinU = cameraUp.clone();
            gc.scale(uMinU,distanceUmin);
            Vector rMinR = cameraRight.clone();
            gc.scale(rMinR,distanceRmin);

            vBotLeft = gc.add(cameraOrigin,gc.add(dMinD,gc.add(uMinU,rMinR)));
            vBotLeft.set(2,distanceDmin);
        }
        return vBotLeft;
    }

    /**
     * returns the nearest vector bottom right:
     * vBotRight = cameraOrigin + dMin * cameraDirection + uMin * cameraUp + rMax * cameraRight
     * @return vBotRight, representing the nearest bottom right vertex of the viewport
     */
    public Vector getvBotRight(){
        checkGeometricCalculator();
        if(vBotRight == null){

            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMinU = cameraUp.clone();
            gc.scale(uMinU,distanceUmin);
            Vector rMaxR = cameraRight.clone();
            gc.scale(rMaxR,distanceRmax);

            vBotRight = gc.add(cameraOrigin,gc.add(dMinD,gc.add(uMinU,rMaxR)));
            vBotRight.set(2,distanceDmin);
        }
        return vBotRight;
    }

    /**
     * returns the nearest vector top right:
     * vTopRight = cameraOrigin + dMin * cameraDirection + uMax * cameraUp + rMax * cameraRight
     * @return vTopRight, representing the nearest top right vertex of the viewport
     */
    public Vector getvTopRight(){
        checkGeometricCalculator();
        if(vTopRight == null){

            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMaxU = cameraUp.clone();
            gc.scale(uMaxU,distanceUmax);
            Vector rMaxR = cameraRight.clone();
            gc.scale(rMaxR,distanceRmax);

            vTopRight = gc.add(cameraOrigin,gc.add(dMinD,gc.add(uMaxU,rMaxR)));
            vTopRight.set(2,distanceDmin);
        }
        return vTopRight;
    }

    /**
     * returns the farthest vector bottom left:
     * wBotLeft = cameraOrigin + dMax/dmin * ( dMin * cameraDirection + uMin * cameraUp + rMin * cameraRight )
     * @return wBotLeft, representing the farthest bot left vertex of the viewport
     */
    public Vector getwBotLeft(){
        checkGeometricCalculator();
        if(wBotLeft == null){

            float c = (float)(distanceDmax/distanceDmin);
            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMinU = cameraUp.clone();
            gc.scale(uMinU,distanceUmin);
            Vector rMinR = cameraRight.clone();
            gc.scale(rMinR,distanceRmin);
            Vector dur = gc.add(dMinD,gc.add(uMinU,rMinR));
            gc.scale(dur,c);

            wBotLeft = gc.add(cameraOrigin,dur);
            wBotLeft.set(2,distanceDmax);
        }
        return wBotLeft;
    }

    /**
     * returns the farthest vector top left:
     * wTopLeft = cameraOrigin + dMax/dMin * ( dMin * cameraDirection + uMax * cameraUp + rMin * cameraRight )
     * @return wTopLeft, representing the farthest bot left vertex of the viewport
     */
    public Vector getwTopLeft(){
        checkGeometricCalculator();
        if(wTopLeft == null){

            float c = (float)(distanceDmax/distanceDmin);
            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMaxU = cameraUp.clone();
            gc.scale(uMaxU,distanceUmax);
            Vector rMinR = cameraRight.clone();
            gc.scale(rMinR,distanceRmin);
            Vector dur = gc.add(dMinD,gc.add(uMaxU,rMinR));
            gc.scale(dur,c);

            wTopLeft = gc.add(cameraOrigin,dur);
            wTopLeft.set(2,distanceDmax);
        }
        return wTopLeft;
    }

    /**
     * returns the farthest vector bottom right:
     * wBotRight = cameraOrigin + dMax/dMin * ( dMin * cameraDirection + uMin * cameraUp + rMax * cameraRight )
     * @return wBotRight, representing the farthest bot left vertex of the viewport
     */
    public Vector getwBotRight(){
        checkGeometricCalculator();
        if(wBotRight == null){

            float c = (float)(distanceDmax/distanceDmin);
            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMinU = cameraUp.clone();
            gc.scale(uMinU,distanceUmin);
            Vector rMaxR = cameraRight.clone();
            gc.scale(rMaxR,distanceRmax);
            Vector dur = gc.add(dMinD,gc.add(uMinU,rMaxR));
            gc.scale(dur,c);

            wBotRight = gc.add(cameraOrigin,dur);
            wBotRight.set(2,distanceDmax);
        }
        return wBotRight;
    }

    /**
     * returns the farthest vector top right:
     * wTopRight = cameraOrigin + dMax/dMin * ( dMin * cameraDirection + uMax * cameraUp + rMax * cameraRight )
     * @return wTopRight, representing the farthest bot left vertex of the viewport
     */
    public Vector getwTopRight(){
        checkGeometricCalculator();
        if(wTopRight == null){

            float c = (float)(distanceDmax/distanceDmin);
            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMaxU = cameraUp.clone();
            gc.scale(uMaxU,distanceUmax);
            Vector rMaxR = cameraRight.clone();
            gc.scale(rMaxR,distanceRmax);
            Vector dur = gc.add(dMinD,gc.add(uMaxU,rMaxR));
            gc.scale(dur,c);

            wTopRight = gc.add(cameraOrigin,dur);
            wTopRight.set(2,distanceDmax);
        }
        return wTopRight;
    }

    /**
     * returns the near plane of this viewfrustum, all the vectors are new instances of the original ones, thus any manipulations don't affect the viewfrustums verrticies
     * @return a plane object, which equals the one from this frustum
     */
    public Plane getNearPlane(){
        return new Plane(cameraOrigin.clone(),gc.subtract(getvTopLeft(),getvBotLeft()),gc.subtract(getvBotRight(),getvBotLeft()));
    }

    /**
     * computes the near normal vector, a clone of the original one is returned, thus any manipulation affect this frustum!
     * @return an equivalent of this frustums near plane normal vector, pointing into the frustum
     */
    public Vector getNearNormal(){
        Vector n = normals.get(Normal.near);
        if(n == null) {
            n = cameraDirection.clone();
            toUnitLength(n);
        }
        return n.clone();
    }

    /**
     * computes the far normal vector, a clone of the original one is returned, thus any manipulation affect this frustum!
     * @return an equivalent of this frustums far plane normal vector, pointing into the frustum
     */
    public Vector getFarNormal(){
        Vector n = normals.get(Normal.far);
        if(n == null) {
            n = cameraDirection.clone();
            toUnitLength(n);
            gc.scale(n, -1);
            normals.put(Normal.far,n);
        }
        return n.clone();
    }

    /**
     * computes the plane normal of the lefthand side plan of the frustum, which is pointing into the frustum, this vector is a clone of the frustums one, thus any manipulation doesn't affect the frustums one
     * @return a vector equal to the lefthand side plane normal of this frustum
     */
    public Vector getLeftNormal(){
        Vector n = normals.get(Normal.left);
        if(n == null) {
            Vector r = cameraRight.clone();
            gc.scale(r, distanceDmin);
            Vector d = cameraDirection.clone();
            gc.scale(d, distanceRmin);
            n = gc.subtract(r, d);
            gc.scale(n, 1 / Math.sqrt(Math.pow(distanceDmin, 2) + Math.pow(distanceRmin, 2)));
            normals.put(Normal.left,n);
        }
        return n.clone();
    }

    /**
     * computes the plane normal of the right hand side of this frustum, pointing into the frustum, this vector is a clone of the frustums one, thus any manipulation doesn't affect the frustums one
     * @return a vector, equal to the plane normal of the right hand side of this frustum, pointing into the frustum
     */
    public Vector getRightNormal(){
        Vector n = normals.get(Normal.right);
        if(n == null){
            Vector r = cameraRight.clone();
            gc.scale(r,distanceRmin*-1);
            Vector d = cameraDirection.clone();
            gc.scale(d,distanceRmin);
            n = gc.add(r,d);
            gc.scale(n,1/Math.sqrt(Math.pow(distanceDmin,2)+Math.pow(distanceRmax,2)));
            normals.put(Normal.right,n);
        }
        return n.clone();
    }

    /**
     * computes the normal vector to the top plan of this viewfrustum, the normal vector is pointing into the frustum, this vector is a clone of the frustums one, thus any manipulation doesn't affect the frustums one
     * @return a normal vector of the top plane, which is pointing into the viewfrustum
     */
    public Vector getTopNormal(){
        Vector n = normals.get(Normal.top);
        if(n == null) {
            Vector u = cameraUp.clone();
            gc.scale(u, distanceDmin * -1);
            Vector d = cameraDirection.clone();
            gc.scale(d, distanceUmax);
            n = gc.subtract(u, d);
            gc.scale(n, 1 / Math.sqrt(Math.pow(distanceDmin, 2) + Math.pow(distanceUmax, 2)));
            normals.put(Normal.top,n);
        }
        return n.clone();
    }

    /**
     * computes the normal vector to the bottom plan of this viewfrustum, the normal vector is pointing into the frustum, this vector is a clone of the frustums one, thus any manipulation doesn't affect the frustums one
     * @return a normal vector of the bottom plane, which is pointing into the viewfrustum
     */
    public Vector getBottomNormal(){
        Vector n = normals.get(Normal.bot);
        if(n == null) {
            Vector u = cameraUp.clone();
            gc.scale(u, distanceDmin);
            Vector d = cameraDirection.clone();
            gc.scale(d, distanceUmin);
            n = gc.subtract(u, d);
            gc.scale(n, 1 / Math.sqrt(Math.pow(distanceDmin, 2) + Math.pow(distanceUmin, 2)));
            normals.put(Normal.bot,n);
        }
        return n.clone();
    }

    private void toUnitLength(Vector v){
        double c = 1/gc.length(v);
        for(int i= 0;i < v.length();i++){
            v.set(i,v.get(i)*c);
        }
    }

    public void setHeight(int height){
        distanceUmax = (height/2)-1;
        distanceUmin = -(height/2);
        resetVertices();
    }

    public int getHeight(){

        //return (int)(Math.abs(distanceUmin) + Math.abs(distanceUmax) + 1);
        return (int) (Math.abs(distanceUmin*2));
    }

    public void setWidth(int width){
        distanceRmin = -(width/2);
        distanceRmax = (width/2)-1;
        resetVertices();
    }

    public int getWidth(){
        //return (int)(Math.abs(distanceRmin) + Math.abs(distanceRmax) + 1);
        return (int) Math.abs(distanceRmin*2);
    }

    public void setDepth(int depth){
        distanceDmin = 0f;
        distanceDmax = depth;
        resetVertices();
    }

    public int getDepth(){
        return (int)distanceDmax;
    }

    public String toString(){
        String s = "ViewFrustum:\n";
        s += "origin:\n"+cameraOrigin;
        s += "direction:\n"+cameraDirection;
        s += "right:\n"+cameraRight;
        s += "up:\n"+cameraUp;
        s += "vTopLeft:\n"+getvTopLeft();
        s += "vTopRight:\n"+getvTopRight();
        s += "vBotRight:\n"+getvBotRight();
        s += "vBotLeft:\n"+getvBotLeft();
        s += "wTopLeft:\n"+getwTopLeft();
        s += "wTopRight:\n"+getwTopRight();
        s += "wBotRight:\n"+getwBotRight();
        s += "wBotLeft:\n"+getwBotLeft();
        return s;
    }

    private void resetVertices(){
        this.vBotLeft = null;
        this.vTopLeft = null;
        this.vBotRight = null;
        this.vTopRight = null;
        this.wBotLeft = null;
        this.wTopLeft = null;
        this.wBotRight = null;
        this.wTopRight = null;
        normals.clear();
    }

}
