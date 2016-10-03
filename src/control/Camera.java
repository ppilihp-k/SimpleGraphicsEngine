package view;

import geometricCalculus.model.Matrix;
import java.util.Observer;
import java.util.Observable;
import geometricCalculus.model.Vector;
import geometricCalculus.GeometricCalculator;
import model.geometrie.Polygon;
import model.utils.Renderer;
import model.utils.ViewFrustumControler;
import model.utils.PictureBufferState;
import model.utils.ViewFrustum;
import model.geometrie.Triangle;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class Camera implements Observer{

    /**
     * helps out with geometric calculations
     */
    private GeometricCalculator gc;

    /**
     * contains the zbuffer and a "canvas" to paint the polygons on it, which are seen by the camera
     */
    private PictureBufferState pictureBuffer;

    /**
     * the cameras position in the virtual world
     */
    private Vector worldLocation;

    /**
     * the view-frustum represents the frustum of sight of the user
     */
    private ViewFrustum viewPort;

    private ViewFrustumControler viewFrustumControler;

    private Renderer renderer;

    public Camera(int height,int width,int depth){
        gc = new GeometricCalculator();
        pictureBuffer = new PictureBufferState(height,width);
        worldLocation = new Vector(3);
        worldLocation.addObserver(this);
        viewFrustumControler = new ViewFrustumControler(gc);
        renderer = new Renderer(gc);
        worldLocation = new Vector(3);
        initViewFrustum(height,width,depth);
    }

    /**
     * inits the view-frustum
     * the view-frustum is a lieing pyramid without the peak, this represents the view cone of the user
     */
    private void initViewFrustum(int height, int width,int depth){

        Vector cameraOrigin = new Vector(3);
        cameraOrigin.addObserver(this);
        Vector cameraUp = new Vector(3);
        cameraUp.set(1,1);
        cameraUp.addObserver(this);
        Vector cameraRight = new Vector(3);
        cameraRight.set(0,1);
        cameraRight.addObserver(this);;
        Vector cameraDirection = new Vector(3);
        cameraDirection.set(2,1);
        cameraDirection.addObserver(this);
        if (gc == null) {
            gc = new GeometricCalculator();
        }
        viewPort = new ViewFrustum(height,width,depth,cameraOrigin,cameraDirection,cameraUp,cameraRight,gc);

    }

    /**
     * returns the current world location.
     * keep in mind, that the vector is returned by reference, so any changes on this
     * vector will influence the hole camerasystem.
     * to avoid errors use the provided methods for manipulation.
     * @return the current worldlocation of this camera
     */
    public Vector getWolrdLocation(){
        return worldLocation;
    }

    /**
     * adds the vector v to the current worldlocation of this camera
     * @param v the difference between the current worldlocation and the new worldlocation
     */
    public void moveCamera(Vector v){
        worldLocation = gc.add(worldLocation,v);
    }


    public int getWidth(){
        return viewPort.getWidth();
    }

    public void setWidth(int width){
        viewPort.setWidth(width);
        onWidthChange();
    }

    public int getHeight(){
        return viewPort.getHeight();
    }

    public void setHeight(int height){
        viewPort.setHeight(height);
        onHeightChange();
    }

    public int getDepth(){
        return viewPort.getDepth();
    }

    public void setDepth(int depth){
        viewPort.setDepth(depth);
        onDepthChange();
    }

    /**
     * renders the given polynom, if the polynom is inside of the viewfrustum of this camera
     * @param p the polygon to draw
     */
    public void renderPolygon(Polygon p){
        Vector[] vectors = new Vector[p.getVertices().length];
        int i = 0;
        for (Vector v: p.getVertices()) {
            vectors[i] = gc.subtract(v,worldLocation);
            if(!viewFrustumControler.isInside(vectors[i])){
                System.out.println("polygon ist nicht im sichtfeld!");
                return;
            }
            i++;
        }
        renderer.scanline(pictureBuffer,vectors);
    }

    public Vector cull(Vector v){
        return null;
    }

    /**
     * this forces a cullingoperation on the triangle, which is not fully displayable on the screen
     * @param t the triangle, that is not displayable
     * @return a new fully displayable triangle
     */
    public Triangle forceCull(Triangle t){
        /*compute a twodimensional representation of the object and check if any of its points are out of bounds*/
        return null;
    }

    private void onWidthChange(){

    }

    private void onHeightChange(){

    }

    private void onDepthChange(){

    }

    private void onCameraChanged(){

    }

    public void update(Observable observable, Object object){

    }

}
