package model.geometrie;
import java.util.LinkedList;
import geometricCalculus.model.Matrix;
import geometricCalculus.model.Vector;

/**
 * Created by PhilippKroll on 16.08.2016.
 */
public class SceneGraph {

    /**
     * the rootnode of this graph
     */
    private Node root;
    /**
     * the name of this scene
     */
    public String name;

    public LinkedList<SceneGraph> scenes;

    public LinkedList<Polygon> polygons;

    public SceneGraph(String name) {
        try {
            root = new Node(null);
        } catch (Exception e){

        }
        this.name = name;
        polygons = new LinkedList<>();
    }

    public void addScene(){

    }

    public void addPolygon(Polygon p){
        polygons.add(p);
    }

    public LinkedList<Polygon> getPolygonsFromScene(){
        return polygons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * return all nodes contained by this scenegraph
     * @return a list with all nodes
     */
    public LinkedList<Node> toList(){
        return root.toList();
    }

    /**
     * methodwrapper for the translation of a subgraph
     * @param v the translationvector
     * @param n the subgraph
     * @return true, if the translation was successful, false otherwise
     */
    public boolean translate(Vector v,Node n){
        return manipulate(n,v);
    }

    /**
     * methodwrapper for the rotation of a subgraph
     * @param m the rotationmatrix
     * @param n the subgraph
     * @return true, if the rotation was successful, false otherwise
     */
    public boolean rotate(Matrix m,Node n){
        return manipulate(n,m);
    }

    /**
     * the method encapsulates a searchoperation and the manipulation that should be
     * applied
     * @param n the node, where to apply the manipulation
     * @param o the manipulation object
     * @return true, if the subgraph n was manipulated, false otherwise
     */
    private boolean manipulate(Node n,Object o){
        Node node = root.find(n.getHash());
        if(node != null){
            return n.manipulate(o);
        }
        return false;
    }

}
