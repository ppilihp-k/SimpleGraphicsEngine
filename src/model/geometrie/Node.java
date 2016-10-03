package model.geometrie;

import geometricCalculus.model.Matrix;
import geometricCalculus.model.Vector;

import java.util.LinkedList;

/**
 * Created by PhilippKroll on 19.08.2016.
 */
public class Node {
    private static long GLOBALHASHNUMBER;
    private long hash;
    private Node parent;
    private LinkedList<Node> children;
    private LinkedList<Polygon> polygons;

    public Node(Node parent) throws Exception {
        hash = GLOBALHASHNUMBER++;
        if(GLOBALHASHNUMBER == Long.MAX_VALUE){
            throw new Exception();
        }
        this.parent = parent;
        children = new LinkedList<>();
        this.polygons = new LinkedList<>();
    }

    /**
     * returns a number, every node has a unique number.
     * @return
     */
    public long getHash(){
        return hash;
    }

    /**
     * adds a parent to this node
     * @param n
     */
    public void addParent(Node n){
        this.parent = n;
    }

    /**
     * returns the parent of this node
     * @return the nodes parentnode
     */
    public Node getParent(){
        return this.parent;
    }

    /**
     * a node in this tree is a leaf, if it has no children
     * @return true, if the node is a leaf, false otherwise
     */
    public boolean isLeaf(){
        return children.size() == 0;
    }

    /**
     * if a node has no parent, it is the root node of this tree
     * @return true, if the actual node is the root
     */
    public boolean isRoot(){
        return parent == null;
    }

    /**
     *  the method adds the given polynoms to this node
     * @param polygons the polygons to be added
     */
    public void addPolygon(Polygon... polygons){
        for (Polygon p:polygons) {
            this.polygons.add(p);
        }
    }

    /**
     * this method add a child to this node.
     * @param child the node, which should be added to the "tree"
     * @return true, if the node was added, false otherwise
     */
    public void addChild(Node child){
        child.addParent(this);
        this.addChild(child);
    }

    /**
     * the method looks for the node with the hash "hash"
     * @param hash hash of the searched node
     * @return the node with hash "hash"
     */
    public Node find(long hash){
        if(this.hash == hash){
            return this;
        } else if(isLeaf()){
            return null;
        } else {
            for (Node n:children) {
                Node f = n.find(hash);
                if(f != null){
                    return f;
                }
            }
            return null;
        }
    }

    /**
     * the method applies either a rotation, if o is a matrix or a translation, if o is a vector, on the all nodes, including the node
     * with has nodesHash
     * @param o the manipulation
     * @return true, if something was changed during the method call, false otherweise
     */
    public boolean manipulate(Object o){
        if(!(o instanceof Matrix) || !(o instanceof Vector)){
            return false;
        }
        for (Polygon p:polygons) {
            if(o instanceof Matrix){
                //p.rotate((Matrix)o);
            } else if(o instanceof Vector){

                //p.translate((Vector)o);
            }
        }
        if(!isLeaf()){
            for (Node n: children) {
                n.manipulate(o);
            }
            return true;
        }
        return false;
    }

    /**
     * this method collects all nodes in a single list
     * @return a list, which contains all nodes of node this method is called on
     */
    public LinkedList<Node> toList(){
        LinkedList<Node> list = new LinkedList<>();
        if(isLeaf()){
            list.add(this);
            return list;
        }
        for (Node n:children) {
            LinkedList<Node> l = n.toList();
            for (Node nl:l) {
                list.add(nl);
            }
        }
        return list;
    }
}
