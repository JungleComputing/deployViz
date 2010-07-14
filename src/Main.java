import helpers.VizUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edgeBundles.*;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class Main {

	private static Visualization vis;
	private static Tree tree = null;

	public static final String GRAPH = "graph";
	public static final String NODES = "graph.nodes";
	public static final String EDGES = "graph.edges";
	public static final String AGGR = "aggregates";

	private JFrame frame;
	private BundledEdgeRenderer edgeRenderer;
	private JSlider slider;
	private JCheckBox cbox;
	private JColorChooser chooser = null;
	private JDialog colorDialog = null;
	private JLabel currentLabel = null, labelStart = null, labelStop = null;

	public Main() {
	}

	public static void main(String arg[]) {
		Main visualization = new Main();
		visualization.initGUI();
	}

	public void initGUI() {
		Graph graph = null;
		try {
			graph = new GraphMLReader().readGraph("assets/das3.xml");
		} catch (DataIOException e) {
			e.printStackTrace();
			System.err.println("Error loading graph.");
			System.exit(1);
		}

		// add the graph to the visualization as the data group "graph"
		// nodes and edges are accessible as "graph.nodes" and "graph.edges"
		vis = new BundledEdgeVisualization();
		vis.add("graph", graph);

		TupleSet ts = vis.getGroup(GRAPH);
		if (ts instanceof Graph) {
			tree = ((Graph) ts).getSpanningTree();
		}

		// draw the "name" label for NodeItems
		LabelRenderer nodeRenderer = new LabelRenderer("name");
		nodeRenderer.setRoundedCorner(8, 8); // round the corners
		nodeRenderer.setRenderType(LabelRenderer.RENDER_TYPE_DRAW_AND_FILL);

		edgeRenderer = new BundledEdgeRenderer(
		// Constants.EDGE_TYPE_CURVE, tree);
				VizUtils.BSPLINE_EDGE_TYPE, tree);

		Renderer aggregateRenderer = new PolygonRenderer(
				Constants.POLY_TYPE_CURVE);
		((PolygonRenderer) aggregateRenderer).setCurveSlack(0.15f);

		// create a new default renderer factory and initialize it
		DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.setDefaultRenderer(nodeRenderer);
		drf.setDefaultEdgeRenderer(edgeRenderer);

		// add the renderer factory to the visualization
		vis.setRendererFactory(drf);

		ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
		nStroke.setDefaultColor(ColorLib.gray(100));
		// nStroke.add("_hover", ColorLib.gray(50));

		// ColorAction filling = new ColorAction(NODES, VisualItem.FILLCOLOR,
		// ColorLib.rgba(200, 200, 255, 150));

		// use black for node text
		ColorAction text = new ColorAction(NODES, VisualItem.TEXTCOLOR,
				ColorLib.gray(50));
		// use light grey for edges
		ColorAction edges = new ColorAction(EDGES, VisualItem.STROKECOLOR,
				ColorLib.gray(100));

		// create an action list containing all color assignments
		ActionList color = new ActionList();
		color.add(nStroke);
		color.add(text);
		color.add(edges);

		// add the action list to the visualization
		vis.putAction("color", color);

		// set the fillColor for the nodes according to the cluster they belong
		// to
		assignNodeColours();

		// ActionList circleLayout = new ActionList();
		// circleLayout.add(new CircleLayout("graph.nodes"));
		// circleLayout.add(new RepaintAction());
		// vis.putAction("layout", circleLayout);

		// create the tree layout action
		RadialTreeLayout treeLayout = new RadialTreeLayout(GRAPH);
		vis.putAction("treeLayout", treeLayout);

		// create a new Display that pull from our Visualization
		Display display = new Display(vis);
		display.setSize(700, 500); // set display size
		display.addControlListener(new DragControl(true)); // drag items around
		display.addControlListener(new PanControl()); // pan
		display.addControlListener(new ZoomControl()); // zoom
		display.setHighQuality(true);
		display.setDamageRedraw(false);

		// create a new window to hold the visualization
		frame = new JFrame("Edge bundles");

		// ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout lmanager = new BorderLayout();
		frame.setLayout(lmanager);

		JPanel topPanel = new JPanel();
		BoxLayout blayout = new BoxLayout(topPanel, BoxLayout.PAGE_AXIS);
		topPanel.setLayout(blayout);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Remove shared ancestor"));
		cbox = new JCheckBox();
		cbox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				edgeRenderer.setRemoveSharedAncestor(cbox.isSelected());
				vis.repaint();
			}
		});
		panel.add(cbox);
		
		panel.add(new JLabel("Change bundling factor"));
		slider = new JSlider(0, 20,
				(int) (20 * VizUtils.INITIAL_BUNDLING_FACTOR));
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {
				edgeRenderer.setBundlingFactor(slider.getValue() / 20.0);
				vis.repaint();
			}
		});
		panel.add(slider);
		topPanel.add(panel);

		panel = new JPanel();
		panel.add(new JLabel("Start color:"));
		labelStart = new JLabel("      ");
		labelStart.setBackground(Color.green);
		labelStart.setOpaque(true);
		labelStart.addMouseListener(new LabelMouseListener());
		panel.add(labelStart);

		panel.add(new JLabel("Stop color:"));
		labelStop = new JLabel("      ");
		labelStop.setBackground(Color.red);
		labelStop.setOpaque(true);
		labelStop.addMouseListener(new LabelMouseListener());
		panel.add(labelStop);
		topPanel.add(panel);

		frame.add(topPanel, BorderLayout.NORTH);

		frame.add(display, BorderLayout.CENTER);
		frame.pack(); // layout components in window
		frame.setVisible(true); // show the window

		vis.run("color"); // assign the colors
		vis.run("treeLayout"); // start up the tree layout

		updateEdges();
	}

	private void showColorChooser(Color color) {
		if (chooser == null && colorDialog == null) {
			chooser = new JColorChooser();
			colorDialog = JColorChooser.createDialog(frame, "Choose color",
					true, chooser, new DialogActionlistener(), null);
		}

		chooser.setColor(color);
		colorDialog.setVisible(true);
	}

	private void updateEdges() {
		Iterator<EdgeItem> edgeIter = vis.visibleItems(EDGES);
		BSplineEdgeItem edge;
		while (edgeIter.hasNext()) {
			edge = (BSplineEdgeItem) edgeIter.next();
			edge.computeControlPoints(false, 1, edge, tree);
		}
		VizUtils.computeAlphas(vis);
		vis.repaint();
	}

	private void assignNodeColours() {
		NodeItem item, parent;

		String nextColor;
		Color color;

		ArrayList<VisualItem> mainNodes = new ArrayList<VisualItem>();

		// add nodes to aggregates
		Iterator<NodeItem> nodes = vis.visibleItems(NODES);

		while (nodes.hasNext()) {
			item = nodes.next();
			if (item.getString("type").equals(VizUtils.HEAD_NODE)) {
				if (!mainNodes.contains(item)) {
					mainNodes.add(item);
					nextColor = VizUtils.getRandomColor();
					color = Color.decode(nextColor).brighter();
					item.setFillColor(ColorLib.color(color));
				}
			} else {
				parent = (NodeItem) item.getParent();
				if (parent != null
						&& parent.getString("type").equals(VizUtils.HEAD_NODE)) {
					int idx = mainNodes.indexOf(parent);
					if (idx < 0) { // the parent is not in the list
						mainNodes.add(parent);
						nextColor = VizUtils.getRandomColor();
						color = Color.decode(nextColor).brighter();
						item.setFillColor(ColorLib.color(color));
					}
					item.setFillColor(parent.getFillColor());
				} else { // the cluster node
					// TODO find a more appropriate color for the main node,
					// maybe draw it differently
					item.setFillColor(ColorLib.hex("#00FFFF"));
				}
			}
		}
	}

	private class LabelMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent event) {
			currentLabel = (JLabel) event.getSource();
			showColorChooser(Color.red);
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	private class DialogActionlistener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Color newcolor = chooser.getColor();
			currentLabel.setBackground(newcolor);
			if(currentLabel == labelStart){
				edgeRenderer.setStartColor(newcolor);
			} else {
				edgeRenderer.setStopColor(newcolor);
			}
			vis.repaint();
		}
	}
}