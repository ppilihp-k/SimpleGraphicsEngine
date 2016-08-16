package model.geometrie;

import geometricCalculus.model.Matrix;
import geometricCalculus.model.Vector;
import model.elementstate.MaterialLightState;
import model.elementstate.MaterialTextureState;

/**
 * Created by PhilippKroll on 16.08.2016.
 * this class represents a polygon, which contains multiple vertices a rotation matrix and information about color, texture and lightning
 */
public abstract class Polygon {

    private Vector[] vertices;
    private Matrix rotationMatrix;

    private MaterialLightState lightningInfo;
    private MaterialTextureState textureInfo;


    public Polygon(Vector[] vertices, int dimension){
        for (Vector v:vertices) {
            if(v.length() != dimension){
                throw new IllegalArgumentException();
            }
        }
        this.vertices = vertices;
        rotationMatrix = new Matrix(dimension);
        lightningInfo = new MaterialLightState();
        textureInfo = new MaterialTextureState();
    }

    public Vector[] getVertices() {
        return vertices;
    }

    public void setRotationMatrix(Matrix rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    public MaterialLightState getLightningInfo() {
        return lightningInfo;
    }

    public void setLightningInfo(MaterialLightState lightningInfo) {
        this.lightningInfo = lightningInfo;
    }

    public MaterialTextureState getTextureInfo() {
        return textureInfo;
    }

    public void setTextureInfo(MaterialTextureState textureInfo) {
        this.textureInfo = textureInfo;
    }

    public void setVertices(Vector[] vertices) {

        this.vertices = vertices;
    }

    public Matrix getRotationMatrix() {

        return rotationMatrix;
    }

}
