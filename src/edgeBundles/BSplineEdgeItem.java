package edgeBundles;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import prefuse.visual.NodeItem;
import prefuse.visual.tuple.TableEdgeItem;


public class BSplineEdgeItem extends TableEdgeItem{
   public BSplineEdgeItem(){
       super();
   }
   
   private ArrayList<Point2D.Double> controlPoints;
   
   public void setControlPoints(ArrayList<Point2D.Double> list){
       controlPoints = list;
   }
   
   public ArrayList<Point2D.Double> getControlPoints(){
       return controlPoints;
   }
}
