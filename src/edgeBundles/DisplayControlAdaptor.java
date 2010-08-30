package edgeBundles;

import helpers.VizUtils;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.util.ColorLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class DisplayControlAdaptor extends ControlAdapter {

	private NodeItem lastSelectedNode = null;
	private Visualization vis;

	public DisplayControlAdaptor(Visualization vis) {
		this.vis = vis;
	}

	public void itemClicked(VisualItem item, MouseEvent e) {
		highlightSelection(item);
	}

	public void itemDragged(VisualItem item, MouseEvent e) {
		highlightSelection(item);
	}

	private void highlightSelection(VisualItem item) {
		BSplineEdgeItem edge;

		if (item instanceof NodeItem) {
			NodeItem node = (NodeItem) item;
			Iterator<EdgeItem> edgeIter = node.edges();

			resetPreviousSelection(node);

			// temporarily store old color here
			if (node.getFillColor() != VizUtils.SELECTED_FILL_COLOR)
				node.setStartFillColor(node.getFillColor());
			node.setFillColor(VizUtils.SELECTED_FILL_COLOR);
			node.setTextColor(VizUtils.SELECTED_TEXT_COLOR);

			while (edgeIter.hasNext()) {
				edge = (BSplineEdgeItem) edgeIter.next();
				edge.setStartFillColor(0x0000ff);
				edge.setEndFillColor(0x0000ff);
				edge.setSelected(true);
				edge.setHighlighted(true);

				NodeItem nitem = edge.getAdjacentItem(node);
				// temporarily store the old fill color in here until a new
				// selection happens
				if (nitem.getFillColor() != VizUtils.SELECTED_FILL_COLOR)
					nitem.setStartFillColor(nitem.getFillColor());

				// change color of the adjacent nodes
				nitem.setFillColor(VizUtils.SELECTED_FILL_COLOR);
				nitem.setTextColor(VizUtils.SELECTED_TEXT_COLOR);
			}

			VizUtils.forceEdgeUpdate(vis);

		} else if (lastSelectedNode != null) {
			resetPreviousSelection(null);
		}
		vis.repaint();
	}

	private void resetPreviousSelection(NodeItem newNode) {
		if (lastSelectedNode != null) {
			Iterator<EdgeItem> edgeIter = lastSelectedNode.edges();

			lastSelectedNode.setFillColor(lastSelectedNode.getStartFillColor());
			lastSelectedNode.setTextColor(VizUtils.DEFAULT_TEXT_COLOR);

			while (edgeIter.hasNext()) {
				BSplineEdgeItem edge = (BSplineEdgeItem) edgeIter.next();
				edge.setSelected(false);
				edge.setHighlighted(false);

				NodeItem nitem = edge.getAdjacentItem(lastSelectedNode);
				nitem.setFillColor(nitem.getStartFillColor());
				nitem.setTextColor(VizUtils.DEFAULT_TEXT_COLOR);
			}
		}
		lastSelectedNode = newNode;
	}
}
