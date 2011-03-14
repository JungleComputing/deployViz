import java.awt.Color;
import java.util.LinkedList;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Polyline;
import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {

		// create a WorldWind main object
		WorldWindowGLCanvas worldWindCanvas = new WorldWindowGLCanvas();
		worldWindCanvas.setModel(new BasicModel());

		// build Java swing interface
		JFrame frame = new JFrame("World Wind");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(worldWindCanvas);
		frame.setSize(800, 600);
		frame.setVisible(true);

		// create a layer and add Polyline
		RenderableLayer layer = new RenderableLayer();
		
		// create some "Position" to build a polyline
		LinkedList<Position> list = new LinkedList<Position>();
		double lat, longit;
		for(int j=0; j <= 10; j++){
			lat = (Math.random()*1000) % 180 - 90;
			longit = (Math.random()*1000) % 180 - 90;
			LatLon pos1 = LatLon.fromDegrees(lat, longit);
			
			lat = (Math.random()*1000) % 180 - 90;
			longit = (Math.random()*1000) % 180 - 90;
			LatLon pos2 = LatLon.fromDegrees(lat, longit);
			
			LatLon pos3 = LatLon.interpolate(0.5, pos1, pos2);
			
			double dist = LatLon.greatCircleDistance(pos1, pos2).degrees * 10000;
			
			list.add(new Position(pos1, 0));
			list.add(new Position(pos3, dist));
			list.add(new Position(pos2, 0));
			
			Polyline polyline = new Polyline(list);
			polyline.setColor(new Color(255,0,0));
			polyline.setLineWidth(3.0);

			list.clear();
			layer.addRenderable(polyline);
		}
//		for (int j = 0; j < 360; j+=1) {
//			for (int i = 0; i < 90; i+=5) {
//				// in this case, points are in geographic coordinates.
//				// If you are using cartesian coordinates, you have to convert
//				// them to geographic coordinates.
//				// Maybe, there are some functions doing that in WWJ API...
//				list.add(Position.fromDegrees(i, j, 0));
//			}
//
//			// create "Polyline" with list of "Position" and set color /
//			// thickness
//			Polyline polyline = new Polyline(list);
//			polyline.setColor(new Color(255,0,0));
//			polyline.setLineWidth(3.0);
//
//			list.clear();
//			layer.addRenderable(polyline);
//		}

		// add layer to WorldWind
		worldWindCanvas.getModel().getLayers().add(layer);
	}

}
