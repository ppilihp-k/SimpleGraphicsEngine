package view;

import model.utils.Bitmap;

import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.util.Observer;
import java.util.Observable;
import javax.swing.JFrame;
import geometricCalculus.model.Vector;
import geometricCalculus.GeometricCalculator;
import model.utils.ViewFrustum;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class Camera extends Canvas implements Observer{

    GeometricCalculator gc = new GeometricCalculator();

    /**
     * camera origin
     */
    private Vector cameraOrigin;
    /**
     * the direction, the camera points to
     */
    private Vector cameraDirection;
    /**
     * the direction, that represents up (up = 1, and down = -1), relative to the origin
     */
    private Vector cameraUp;
    /**
     * the direction, that represents right (right = 1, and left = -1), relative to the origin
     */
    private Vector cameraRight;

    /**
     * the view-frustum represents the frustum of sight of the user
     */
    private ViewFrustum viewPort;

    private BufferedImage canvas;
    private JFrame screen;

    public Camera(int height,int width,int depth){

        initOrigins(height,width);
        initViewFrustum(height,width,depth);

        canvas = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
        screen = new JFrame();
        screen.setSize(width,height);
        screen.pack();
        screen.setLocationRelativeTo(null);
        screen.setVisible(true);
    }

    /**
     * inits the view-frustum
     * the view-frustum is a lieing pyramid without the peak, this represents the view cone of the user
     */
    private void initViewFrustum(int height, int width,int depth){



        viewPort = new ViewFrustum(vbl,vtl,vbr,vtr,wbl,wtl,wbr,wtr);

    }

    /**
     * init of the point of view.
     * the vector cameraorigin represents a zero
     * the other vectors represent the coordinate system around the cameraorigin
     */
    private void initOrigins(int height,int width){

        cameraOrigin = new Vector(3);
        cameraOrigin.set(0,0);
        cameraOrigin.set(1,0);
        cameraOrigin.set(2,0);
        cameraDirection = new Vector(3);
        cameraDirection.set(0,0);
        cameraDirection.set(1,0);
        cameraDirection.set(2,-1);
        cameraRight = new Vector(3);
        cameraRight.set(0,1);
        cameraRight.set(1,0);
        cameraRight.set(2,0);
        cameraUp = new Vector(3);
        cameraUp.set(0,0);
        cameraUp.set(1,1);
        cameraUp.set(2,0);
    }

    private void swapBuffers(){
    }

    public void clear(){
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        for (int x = 0;x < width; x++){
            for (int y = 0;y < height; y++){
                canvas.setRGB(x,y,0);
            }
        }
    }

    public void update(Observable observable, Object object){

    }

}
