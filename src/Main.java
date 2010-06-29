import javax.swing.JFrame;

import edgeBundles.BundledEdgeRenderer;
import edgeBundles.BundledEdgeVisualization;

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
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class Main {

    private static Visualization vis;
    private static Tree tree = null;

    public Main() {
    }

    public static void main(String arg[]) {
        Graph graph = null;
        try {
            graph = new GraphMLReader().readGraph("assets/relatii.xml");
        } catch (DataIOException e) {
            e.printStackTrace();
            System.err.println("Error loading graph.");
            System.exit(1);
        }

        // add the graph to the visualization as the data group "graph"
        // nodes and edges are accessible as "graph.nodes" and "graph.edges"
        vis = new BundledEdgeVisualization();
        vis.add("graph", graph);

        TupleSet ts = vis.getGroup("graph");
        if (ts instanceof Graph) {
            tree = ((Graph) ts).getSpanningTree();
        }

        // draw the "name" label for NodeItems
        LabelRenderer r = new LabelRenderer("firstName");
        r.setRoundedCorner(8, 8); // round the corners

        BundledEdgeRenderer edgeRenderer = new BundledEdgeRenderer(
        // Constants.EDGE_TYPE_CURVE, tree);
                BundledEdgeRenderer.BSPLINE, tree);

        // create a new default renderer factory
        // return our name label renderer as the default for all non-EdgeItems
        // includes straight line edges for EdgeItems by default
        vis.setRendererFactory(new DefaultRendererFactory(r, edgeRenderer));

        // // create our nominal color palette
        // // pink for females, baby blue for males
        // int[] palette = new int[] { ColorLib.rgb(255, 180, 180),
        // ColorLib.rgb(190, 190, 255) };
        // // map nominal data values to colors using our provided palette
        // DataColorAction fill = new DataColorAction("graph.nodes", "gender",
        // Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        // use black for node text
        ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR,
                ColorLib.gray(0));
        // use light grey for edges
        ColorAction edges = new ColorAction("graph.edges",
                VisualItem.STROKECOLOR, ColorLib.gray(200));

        // create an action list containing all color assignments
        ActionList color = new ActionList();
        // color.add(fill);
        color.add(text);
        color.add(edges);
        vis.putAction("color", color);

        // ActionList circleLayout = new ActionList();
        // circleLayout.add(new CircleLayout("graph.nodes"));
        // circleLayout.add(new RepaintAction());
        // vis.putAction("layout", circleLayout);

        // ActionList radialLayout = new ActionList();
        // radialLayout.add(new CircleLayout("graph.nodes"));
        // radialLayout.add(new RepaintAction());
        // vis.putAction("layout", radialLayout);

        // create the tree layout action
        RadialTreeLayout treeLayout = new RadialTreeLayout("graph");
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
        JFrame frame = new JFrame("Bundled edges");
        // ensure application exits when window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(display);
        frame.pack(); // layout components in window
        frame.setVisible(true); // show the window

        vis.run("color"); // assign the colors
        vis.run("treeLayout"); // start up the tree layout
    }
}