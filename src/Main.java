import helpers.VizUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edgeBundles.*;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.activity.Activity;
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

	public static final String TREE_LAYOUT = "treeLayout";
	public static final String RADIAL_TREE_LAYOUT = "radialTreeLayout";

	private JFrame frame;
	private BundledEdgeRenderer edgeRenderer;
	private JSlider slider;
	private JCheckBox cbox;
	private JColorChooser chooser = null;
	private JDialog colorDialog = null;
	private JButton lastSelectedButton = null, buttonStart = null,
			buttonStop = null;

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

		// ColorAction filling = new ColorAction(NODES, VisualItem.FILLCOLOR,
		// ColorLib.rgba(200, 200, 255, 150));

		// use black for node text
		ColorAction text = new ColorAction(NODES, VisualItem.TEXTCOLOR,
				ColorLib.gray(50));
		
		// use light grey for edges
//		ColorAction edges = new ColorAction(EDGES, VisualItem.STROKECOLOR,
//				ColorLib.gray(100));
		
//		ColorAction fill = new ColorAction(NODES, 
//                VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
//        fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
//        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));

		// create an action list containing all color assignments
		ActionList color = new ActionList();
		color.add(nStroke);
		color.add(text);
		//color.add(edges);
		//color.add(fill);

		// add the action list to the visualization
		vis.putAction("color", color);

		// set the fillColor for the nodes according to the cluster they belong
		// to
		assignNodeColours();

		// create the radial tree layout action
		RadialTreeLayout radialTreeLayout = new RadialTreeLayout(GRAPH);
		vis.putAction(RADIAL_TREE_LAYOUT, radialTreeLayout);

		// create the tree layout action
		NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(GRAPH,
				Constants.ORIENT_TOP_BOTTOM, 200, 3, 20);
		vis.putAction(TREE_LAYOUT, treeLayout);

		// create a new Display that pull from our Visualization
		Display display = new Display(vis);
		display.setSize(700, 500); // set display size
		display.addControlListener(new DragControl(true)); // drag items around
		display.addControlListener(new PanControl()); // pan
		display.addControlListener(new ZoomControl()); // zoom
		display.setHighQuality(true);
		display.setDamageRedraw(false);
		display.addControlListener(new DisplayControlAdaptor(vis));

		// create a new window to hold the visualization
		frame = new JFrame("Edge bundles");

		// ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout lmanager = new BorderLayout();
		frame.setLayout(lmanager);

		JPanel topPanel = new JPanel();
		BoxLayout blayout = new BoxLayout(topPanel, BoxLayout.LINE_AXIS);
		topPanel.setLayout(blayout);
		topPanel.add(Box.createRigidArea(new Dimension(30, 30)));

		JPanel verticalpaJPanel = new JPanel();
		BoxLayout verticalLayout = new BoxLayout(verticalpaJPanel,
				BoxLayout.PAGE_AXIS);
		verticalpaJPanel.setLayout(verticalLayout);

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Remove shared ancestor:"));
		cbox = new JCheckBox();
		cbox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				edgeRenderer.setRemoveSharedAncestor(cbox.isSelected());
				VizUtils.forceEdgeUpdate(vis);
				vis.repaint();
			}
		});
		panel.add(cbox);
		verticalpaJPanel.add(panel);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Start color:"));
		buttonStart = new JButton("  ");
		buttonStart.setBackground(VizUtils.DEFAULT_START_COLOR);
		buttonStart.setFocusPainted(false);
		buttonStart.addActionListener(new ButtonActionListener());
		buttonStart.setToolTipText("Click to select color");
		panel.add(buttonStart);

		panel.add(new JLabel("Stop color:"));
		buttonStop = new JButton("  ");
		buttonStop.setBackground(VizUtils.DEFAULT_STOP_COLOR);
		buttonStop.setFocusPainted(false);
		buttonStop.addActionListener(new ButtonActionListener());
		buttonStop.setToolTipText("Click to select color");
		panel.add(buttonStop);

		verticalpaJPanel.add(panel);
		topPanel.add(verticalpaJPanel);

		topPanel.add(new JSeparator(JSeparator.VERTICAL));

		verticalpaJPanel = new JPanel();
		verticalLayout = new BoxLayout(verticalpaJPanel, BoxLayout.PAGE_AXIS);
		verticalpaJPanel.setLayout(verticalLayout);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Change bundling factor:"));
		slider = new JSlider(0, 20,
				(int) (20 * VizUtils.INITIAL_BUNDLING_FACTOR));
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {
				edgeRenderer.setBundlingFactor(slider.getValue() / 20.0);
				VizUtils.forceEdgeUpdate(vis);
				vis.repaint();
			}
		});
		panel.add(slider);

		verticalpaJPanel.add(panel);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Layout:"));

		ButtonGroup radioGroup = new ButtonGroup();
		JRadioButton circleRadio = new JRadioButton("Radial tree");
		radioGroup.add(circleRadio);
		circleRadio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				vis.run(RADIAL_TREE_LAYOUT);
				VizUtils.forceEdgeUpdate(vis);
				vis.repaint();
			}
		});
		circleRadio.setSelected(true);
		panel.add(circleRadio);

		JRadioButton treeRadio = new JRadioButton("Tree");
		radioGroup.add(treeRadio);
		treeRadio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				vis.run(TREE_LAYOUT);
				VizUtils.forceEdgeUpdate(vis);
				vis.repaint();
			}
		});
		panel.add(treeRadio);
		verticalpaJPanel.add(panel);
		topPanel.add(verticalpaJPanel);

		frame.add(topPanel, BorderLayout.NORTH);

		frame.add(display, BorderLayout.CENTER);
		frame
				.add(
						new JLabel(
								"Use the left mouse button to pan and right mouse button to zoom"),
						BorderLayout.SOUTH);

		vis.run("color"); // assign the colors
		vis.run(RADIAL_TREE_LAYOUT); // start up the tree layout

		VizUtils.computeAlphas(vis, tree);
		
		frame.pack(); // layout components in window
		frame.setVisible(true); // show the window
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

	private class ButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			lastSelectedButton = (JButton) event.getSource();
			if (lastSelectedButton == buttonStart) {
				showColorChooser(edgeRenderer.getStartColor());
			} else {
				showColorChooser(edgeRenderer.getStopColor());
			}
		}
	}

	private class DialogActionlistener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Color newcolor = chooser.getColor();
			lastSelectedButton.setBackground(newcolor);
			if (lastSelectedButton == buttonStart) {
				edgeRenderer.setStartColor(newcolor);
			} else {
				edgeRenderer.setStopColor(newcolor);
			}
			vis.repaint();
		}
	}
}