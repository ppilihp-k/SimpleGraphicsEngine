package model.utils;

import geometricCalculus.model.Vector;
import geometricCalculus.GeometricCalculator;
import geometricCalculus.model.Matrix;

/**
 * Created by PhilippKroll on 16.08.2016.
 * this class represents the view-frustum of the user, sitting in front of the screen.
 * the users eyeview is represented by cameraOrigin, together with cameraDirection and cameraUp and cameraRight,
 * these four vectors define a coordinate-system, where cameraOrigin is the zero.
 * the frustum constructed around this system is used for computations, that belong to the categoration of interaction with the user
 */
public class ViewFrustum {

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

    private GeometricCalculator gc;

    public ViewFrustum(int height,int width,int depth,Vector cameraOrigin,Vector cameraDirection,Vector cameraUp,Vector cameraRight,GeometricCalculator gc){
        this.gc = gc;
        checkGeometricCalculator();
        if(gc.length(cameraOrigin) != 1 || gc.length(cameraDirection) != 1 || gc.length(cameraRight) != 1 || gc.length(cameraUp) != 1){
            throw new IllegalArgumentException();
        }
        this.cameraOrigin = cameraOrigin;
        this.cameraDirection = cameraDirection;
        this.cameraRight = cameraRight;
        this.cameraUp = cameraUp;
        setDepth(depth);
        setHeight(height);
        setWidth(width);
    }

    private void checkGeometricCalculator(){
        if(this.gc == null ){
            gc = new GeometricCalculator();
        }
    }

    /**
     * returns the nearest vector top left:
     * vTopLeft = cameraOrigin + dMin * cameraDirection + uMax * cameraUp + rMin * cameraRight
     * @return vTopLeft, representing the nearest top left vertex of the viewport
     */
    public Vector getvTopLeft(){
        checkGeometricCalculator();
        if(vTopLeft == null){

            Vector dMinD = cameraDirection.clone();
            gc.scale(dMinD,distanceDmin);
            Vector uMaxU = cameraUp.clone();
            gc.scale(uMaxU,distanceUmax);
            Vector rMinR = cameraRight.clone();
            gc.scale(rMinR,distanceRmin);

            vTopLeft = gc.add(cameraOrigin,gc.add(dMinD,gc.add(uMaxU,rMinR)));
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
            gc.scale(uMinU,distanceUmax);
            Vector rMinR = cameraRight.clone();
            gc.scale(rMinR,distanceRmax);
            Vector dur = gc.add(dMinD,gc.add(uMinU,rMinR));
            gc.scale(dur,c);

            wBotLeft = gc.add(cameraOrigin,dur);
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
            gc.scale(rMinR,distanceRmax);
            Vector dur = gc.add(dMinD,gc.add(uMaxU,rMinR));
            gc.scale(dur,c);

            wTopLeft = gc.add(cameraOrigin,dur);
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
            gc.scale(uMinU,distanceUmax);
            Vector rMaxR = cameraRight.clone();
            gc.scale(rMaxR,distanceRmax);
            Vector dur = gc.add(dMinD,gc.add(uMinU,rMaxR));
            gc.scale(dur,c);

            wBotRight = gc.add(cameraOrigin,dur);
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
        }
        return wTopRight;
    }

    public void setHeight(int height){
        distanceUmax = (height/2)-1;
        distanceUmin = -(height/2);
        resetVertices();
    }

    public float getHeight(){
        return (float)(Math.abs(distanceUmin) + Math.abs(distanceUmax) + 1);
    }

    public void setWidth(int width){
        distanceRmin = -(width/2);
        distanceRmax = (width/2)-1;
        resetVertices();
    }

    public float getWidth(){
        return (float)(Math.abs(distanceRmin) + Math.abs(distanceRmax) + 1);
    }

    public void setDepth(int depth){
        distanceDmin = 0f;
        distanceDmax = depth;
        resetVertices();
    }

    public float getDepth(){
        return (float)distanceDmax;
    }

    /**
     * routine that is called if either on of the following objects change:
     * cameraOrigin, cameraRight, cameraUp, cameraDirection
     * -> resets/refreshes the attributes of the viewport
     */
    private void onCameraChanged(){

    }

    /**
     *
     * @param newOrigin
     */
    public void translate(Vector newOrigin){

    }

    /**
     *
     * @param rotMat
     */
    public void rotateAroundDirection(Matrix rotMat){
        if(rotMat.length() != 3){
            throw new IllegalArgumentException();
        }
        checkGeometricCalculator();
        cameraRight = gc.multiply(rotMat,cameraRight);
        cameraUp = gc.multiply(rotMat,cameraUp);
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
    }

}
