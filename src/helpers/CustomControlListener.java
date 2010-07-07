package helpers;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;

import edgeBundles.BSplineEdgeItem;

import prefuse.Visualization;
import prefuse.controls.Control;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class CustomControlListener implements Control {

	private Visualization vis;
	private double minAlpha = 0.22, maxAlpha = 0.8;

	public CustomControlListener(Visualization vis) {
		this.vis = vis;
	}

	public void computeAlphas() {
		Iterator<EdgeItem> edgeIter = vis.visibleItems("graph.edges");
		BSplineEdgeItem edge;
		int minlength = Integer.MAX_VALUE, maxlength = Integer.MIN_VALUE, tsize;

		while (edgeIter.hasNext()) {
			edge = (BSplineEdgeItem) edgeIter.next();
			tsize = edge.getControlPoints().size();
			if (tsize > maxlength) {
				maxlength = tsize;
			}
			if (tsize < minlength) {
				minlength = tsize;
			}
		}

		edgeIter = vis.visibleItems("graph.edges");
		while (edgeIter.hasNext()) {
			edge = (BSplineEdgeItem) edgeIter.next();
			edge.setAlpha(fromIntervalToInterval(
					edge.getControlPoints().size(), minlength, maxlength,
					minAlpha, maxAlpha));
		}
	}

	public float fromIntervalToInterval(double x, double minx, double maxx,
			double miny, double maxy) {
		if(maxx == minx){
			return 1;
		}
		return (float) (maxy - (maxy - miny) * (x - minx) / (maxx - minx));
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void itemClicked(VisualItem item, MouseEvent e) {
	}

	@Override
	public void itemDragged(VisualItem item, MouseEvent e) {
		computeAlphas();
	}

	@Override
	public void itemEntered(VisualItem item, MouseEvent e) {
	}

	@Override
	public void itemExited(VisualItem item, MouseEvent e) {
	}

	@Override
	public void itemKeyPressed(VisualItem item, KeyEvent e) {
	}

	@Override
	public void itemKeyReleased(VisualItem item, KeyEvent e) {
	}

	@Override
	public void itemKeyTyped(VisualItem item, KeyEvent e) {
	}

	@Override
	public void itemMoved(VisualItem item, MouseEvent e) {
	}

	@Override
	public void itemPressed(VisualItem item, MouseEvent e) {
	}

	@Override
	public void itemReleased(VisualItem item, MouseEvent e) {
	}

	@Override
	public void itemWheelMoved(VisualItem item, MouseWheelEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void setEnabled(boolean enabled) {
	}

}
