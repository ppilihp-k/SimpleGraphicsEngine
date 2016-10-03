package control;

import geometricCalculus.GeometricCalculator;
import geometricCalculus.model.Vector;
import model.geometrie.Line;
import model.geometrie.Polygon;
import model.geometrie.SceneGraph;
import model.geometrie.Triangle;
import model.utils.*;
import model.utils.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Created by PhilippKroll on 25.09.2016.
 */
public class GraficEngine extends Canvas{

    private JFrame screen;
    private Camera camera;
    private LinkedList<SceneGraph> scenes;

    /**
     * contains the zbuffer and a "canvas" to paint the polygons on it, which are seen by the camera
     */
    private PictureBufferState pictureBuffer;

    private GeometricCalculator gc;

    private Renderer renderer;

    private Filter filter;

    private PolygonManipulationTool pmt;

    public GraficEngine(int height,int width,int depth){

        gc = new GeometricCalculator();
        renderer = new Renderer(gc);
        pictureBuffer = new PictureBufferState(height,width);
        screen = new JFrame();
        Dimension d = new Dimension();
        d.setSize(width,height);
        this.setSize(d);
        this.setMinimumSize(d);
        this.setPreferredSize(d);
        this.setMaximumSize(d);
        //screen.setPreferredSize(d);
        //screen.setMinimumSize(d);
        //screen.setMaximumSize(d);
        screen.add(this);
        screen.pack();
        screen.setLocationRelativeTo(null);
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //screen.setUndecorated(true);
        screen.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        screen.setResizable(false);
        screen.setVisible(true);
        camera = new Camera(height,width,depth);
        scenes = new LinkedList<SceneGraph>();
        gc = new GeometricCalculator();
        filter = new Filter();
        pmt = new PolygonManipulationTool(gc);
    }

    public void rotate(Polygon p,float angle,PolygonManipulationTool.Axis axis){
        try{
            pmt.rotate(axis,angle,null,p);
        } catch (Exception e){
            System.out.println("Fehler");
        }
    }

    public void translate(Polygon p,Vector v){
        try{
            pmt.translate(v,p);
        } catch (Exception e){
            System.out.println("Fehler");
        }
    }

    public void scale(Polygon p,Vector v){
        pmt.scale((float)v.get(0),(float)v.get(1),(float)v.get(2),p);
    }

    public void renderNextScene(){
        System.out.println("renderNextScene");
        for (SceneGraph sg: scenes) {
            for (Polygon p : sg.getPolygonsFromScene()) {
                renderPolygon(p);
            }
        }
        filter.filter(pictureBuffer);
        //filter.filter(pictureBuffer);
        //filter.filter(pictureBuffer);
        BufferedImage img = getNextImage();
        //screen.getContentPane().getGraphics().setColor(new Color(100,100,155));
        //screen.getContentPane().getGraphics().fillRect(0,0,camera.getWidth() , camera.getHeight());
        //screen.revalidate();
        //screen.getContentPane()
        super.getGraphics().drawImage(img,(screen.getRootPane().getWidth()-camera.getWidth())/2,(screen.getRootPane().getHeight()-camera.getHeight())/2,camera.getWidth(),camera.getHeight(),null);
        //System.out.println("camera height:"+camera.getHeight()+" width:"+camera.getWidth());
        //System.out.println("screen height:"+screen.getHeight()+" width:"+screen.getWidth());
    }

    /**
     * renders the given polynom, if the polynom is inside of the viewfrustum of this camera
     * @param p the polygon to draw
     */
    public void renderPolygon(Polygon p){
        System.out.println("renderPolygon");
        Vector[] vectors = new Vector[p.getVertices().length];
        int i = 0;
        for (Vector v: p.getVertices()) {
            vectors[i] = gc.subtract(v,camera.getWolrdLocation());
            if(!camera.canSee(vectors[i])){
                System.out.println("polygon ist nicht im sichtfeld!");
                return;
            }
            System.out.println("polygon ist im sichtfeld!");
            //System.out.println(vectors[i]);
            i++;
        }

        if(p instanceof Line){
            renderer.scanTwo(vectors[0].subVector(0,2),vectors[1].subVector(0,2),2,p.getTextureInfo(),pictureBuffer);
        } else if(p instanceof Triangle){
            //System.out.println("draw triangle");
            renderer.scanThree(vectors[0].subVector(0,2),vectors[1].subVector(0,2),vectors[2].subVector(0,2),p.getTextureInfo(),pictureBuffer);
            //printPictureBufferToConsole(pictureBuffer);
        }
    }

    public void printPictureBufferToConsole(PictureBufferState pbs){
        int row = 0;
        int col;
        while(row < pbs.gethHeight()){
            col = 0;
            while(col < pbs.getWidth()){
                System.out.print(pbs.getColor(row,col)+"\t\t");
                col++;
            }
            System.out.println();
            row++;
        }
    }

    public BufferedImage getNextImage(){
        System.out.println("getNextImage()");
        BufferedImage img = new BufferedImage(pictureBuffer.getWidth(),pictureBuffer.gethHeight(),BufferedImage.TYPE_4BYTE_ABGR);
        System.out.println("img width:"+img.getWidth()+" height:"+img.getHeight());
        for (int y = 0;y < img.getHeight();y++){
            for (int x = 0;x < img.getWidth();x++){
                //System.out.print(pictureBuffer.getColor(y,x)+"\t");
                img.setRGB(x,y,pictureBuffer.getColor(y,x));
                // System.out.print(img.getRGB(x,y)+"\t");
            }
            //System.out.println();
        }
        pictureBuffer.clear();
        return img;
    }

    public Dimension getScreenSize(){
        return screen.getSize();
    }

    public void addPolygon(SceneGraph sg){
        scenes.add(sg);
    }

    public void addScene(SceneGraph sg){

    }

    public void removeScene(String name){

    }

    public void removeScene(SceneGraph sg){

    }
}
