import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.util.LinkedList;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.FrameFactory;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.Polyline;

import javax.media.opengl.GL;
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

		// // create some "Position" to build a polyline
		// LinkedList<Position> list = new LinkedList<Position>();
		// double lat, longit;
		// for (int j = 0; j <= 10; j++) {
		// lat = (Math.random() * 1000) % 180 - 90;
		// longit = (Math.random() * 1000) % 180 - 90;
		// LatLon pos1 = LatLon.fromDegrees(lat, longit);
		//
		// lat = (Math.random() * 1000) % 180 - 90;
		// longit = (Math.random() * 1000) % 180 - 90;
		// LatLon pos2 = LatLon.fromDegrees(lat, longit);
		//
		// LatLon pos3 = LatLon.interpolate(0.5, pos1, pos2);
		//
		// double dist = LatLon.greatCircleDistance(pos1, pos2).degrees * 10000;
		//
		// list.add(new Position(pos1, 0));
		// list.add(new Position(pos3, dist));
		// list.add(new Position(pos2, 0));
		//
		// Polyline polyline = new Polyline(list);
		// polyline.setColor(new Color(255, 0, 0));
		// polyline.setLineWidth(3.0);
		//
		// list.clear();
		// layer.addRenderable(polyline);
		// }
		// for (int j = 0; j < 360; j+=1) {
		// for (int i = 0; i < 90; i+=5) {
		// // in this case, points are in geographic coordinates.
		// // If you are using cartesian coordinates, you have to convert
		// // them to geographic coordinates.
		// // Maybe, there are some functions doing that in WWJ API...
		// list.add(Position.fromDegrees(i, j, 0));
		// }
		//
		// // create "Polyline" with list of "Position" and set color /
		// // thickness
		// Polyline polyline = new Polyline(list);
		// polyline.setColor(new Color(255,0,0));
		// polyline.setLineWidth(3.0);
		//
		// list.clear();
		// layer.addRenderable(polyline);
		// }

		// add layer to WorldWind
		worldWindCanvas.getModel().getLayers().add(layer);

		Experiment experiment = new Experiment();
		worldWindCanvas.getModel().getLayers().add(experiment.buildDotLayer());
	}

	private void generateRandomCoordinates() {
		LinkedList<Position> list = new LinkedList<Position>();
		double lat, longit;
		for (int j = 0; j <= 10; j++) {
			lat = (Math.random() * 1000) % 180 - 90;
			longit = (Math.random() * 1000) % 360 - 180;
			LatLon pos1 = LatLon.fromDegrees(lat, longit);

			lat = (Math.random() * 1000) % 180 - 90;
			longit = (Math.random() * 1000) % 360 - 180;
			LatLon pos2 = LatLon.fromDegrees(lat, longit);

			LatLon pos3 = LatLon.interpolate(0.5, pos1, pos2);

			double dist = LatLon.greatCircleDistance(pos1, pos2).degrees * 10000;

			list.add(new Position(pos1, 0));
			list.add(new Position(pos3, dist));
			list.add(new Position(pos2, 0));
		}
	}

}

class Experiment {
	public Experiment() {

	}

	private RenderableLayer dotLayer;

	private BufferedImage shapesList[] = {
			PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f,
					Color.RED),
			PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f,
					Color.ORANGE),
			PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f,
					Color.YELLOW),
			PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f,
					Color.GREEN),
			PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f,
					Color.BLUE),
			PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f,
					Color.GRAY),
			PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f,
					Color.BLACK), };
	private Color colorList[] = { Color.RED, Color.ORANGE, Color.YELLOW,
			Color.GREEN, Color.BLUE, Color.GRAY, Color.BLACK, };

	private static int NPOSITIONS = 20;

	public RenderableLayer buildDotLayer() {
		Position pos;
		CircleAnnotation annotation;
		int i;
		RenderableLayer layer = new RenderableLayer();
		layer.setName("Locations");

		AnnotationAttributes dotAttributes = new AnnotationAttributes();
		dotAttributes.setLeader(FrameFactory.LEADER_NONE);
		dotAttributes.setDrawOffset(new Point(0, -16));
		dotAttributes.setSize(new Dimension(15, 15));
		dotAttributes.setBorderWidth(0);
		dotAttributes.setCornerRadius(0);
		dotAttributes.setBackgroundColor(new Color(0, 0, 0, 0));

		LinkedList<Position> positionList = generatePositionList(NPOSITIONS);

		for (i = 0; i < NPOSITIONS; i++) {
			pos = positionList.get(i);

			annotation = new CircleAnnotation(pos, dotAttributes);
			annotation.getAttributes().setImageSource(shapesList[i % 7]);
			annotation.getAttributes().setTextColor(colorList[i % 7]);

			layer.addRenderable(annotation);
		}

		generateRandomConnections(positionList, layer);
		
		return layer;
	}

	private void generateRandomConnections(LinkedList<Position> positionList,
			RenderableLayer layer) {

		int i, j;
		LinkedList<Position> polylineList = new LinkedList<Position>();
		Position pos1, pos2, pos3;
		Polyline polyline;

		for (i = 0; i < positionList.size(); i++) {
			for (j = i + 1; j < positionList.size(); j++) {
				if (Math.random() > 0.5) {
					polylineList.clear();
					pos1 = positionList.get(i);
					pos2 = positionList.get(j);
					
					double dist = LatLon.greatCircleDistance(pos1,
					 pos2).degrees * 10000;
					
					pos3 = Position.interpolateGreatCircle(0.5, pos1, pos2);
					pos3 = new Position(pos3.latitude, pos3.longitude, dist);	
					
//					Angle greatCircleDistance = Position.greatCircleDistance(pos1, pos2);
//					greatCircleDistance = Angle.fromRadians(greatCircleDistance.radians / 2);
//					
//					pos3 = new Position(Position.greatCircleEndPosition(pos1, 0.0, greatCircleDistance.getRadians()), 10000);
					

					polylineList.add(pos1);
					polylineList.add(pos3); // this is the point obtained by
											// interpolation
					polylineList.add(pos2);

					polyline = new Polyline(polylineList);
					polyline.setColor(new Color(255, 0, 0));
					polyline.setLineWidth(3.0);

					layer.addRenderable(polyline);
				}
			}
		}
	}

	private LinkedList<Position> generatePositionList(int nPoints) {
		LinkedList<Position> positionList = new LinkedList<Position>();
		for (int i = 0; i < nPoints; i++) {
			positionList.add(generateRandomPosition());
		}

		return positionList;
	}

	private Position generateRandomPosition() {
		double lat, longit;
		lat = (Math.random() * 1000) % 180 - 90;
		longit = (Math.random() * 1000) % 360 - 180;
		LatLon pos1 = LatLon.fromDegrees(lat, longit);

		return new Position(pos1, 0);
	}
}

// TODO - circle size according to location size
class CircleAnnotation extends GlobeAnnotation {
	public CircleAnnotation(Position position, AnnotationAttributes defaults) {
		super("", position, defaults);
	}

	protected void applyScreenTransform(DrawContext dc, int x, int y,
			int width, int height, double scale) {
		double finalScale = scale * this.computeScale(dc);

		GL gl = dc.getGL();
		gl.glTranslated(x, y, 0);
		gl.glScaled(finalScale, finalScale, 1);
	}

	// Override annotation drawing for a simple circle
	private DoubleBuffer shapeBuffer;

	protected void doDraw(DrawContext dc, int width, int height,
			double opacity, Position pickPosition) {
		// Draw colored circle around screen point - use annotation's text color
		if (dc.isPickingMode()) {
			this.bindPickableObject(dc, pickPosition);
		}

		this.applyColor(dc, this.getAttributes().getTextColor(), 0.6 * opacity,
				true);

		// Draw 32x32 shape from its bottom left corner
		int size = 32;
		if (this.shapeBuffer == null)
			this.shapeBuffer = FrameFactory.createShapeBuffer(
					FrameFactory.SHAPE_ELLIPSE, size, size, 0, null);
		dc.getGL().glTranslated(-size / 2, -size / 2, 0);
		FrameFactory.drawBuffer(dc, GL.GL_TRIANGLE_FAN, this.shapeBuffer);
	}
}
