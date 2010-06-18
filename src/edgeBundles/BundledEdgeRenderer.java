package edgeBundles;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;


import prefuse.Constants;
import prefuse.data.Tree;
import prefuse.render.EdgeRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class BundledEdgeRenderer extends EdgeRenderer {
    public static final int BSPLINE = 100;
    private Tree tree;

    public BundledEdgeRenderer(int edgeType, Tree tree) {
        super(edgeType);
        this.tree = tree;
    }

    @Override
    public void render(Graphics2D g, VisualItem item) {
        if (m_edgeType == BSPLINE) {
            computeControlPoints(false, 1, (EdgeItem) item);
            drawCubicBSpline(g, (EdgeItem) item);
        } else {
            Shape shape = getShape(item);
            if (shape != null)
                drawShape(g, item, shape);
        }
    }

    @Override
    protected Shape getRawShape(VisualItem item) {
        EdgeItem edge = (EdgeItem) item;
        VisualItem item1 = edge.getSourceItem();
        VisualItem item2 = edge.getTargetItem();

        int type = m_edgeType;

        getAlignedPoint(m_tmpPoints[0], item1.getBounds(), m_xAlign1, m_yAlign1);
        getAlignedPoint(m_tmpPoints[1], item2.getBounds(), m_xAlign2, m_yAlign2);
        m_curWidth = (float) (m_width * getLineWidth(item));

        // create the arrow head, if needed
        EdgeItem e = (EdgeItem) item;
        if (e.isDirected() && m_edgeArrow != Constants.EDGE_ARROW_NONE) {
            // get starting and ending edge endpoints
            boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
            Point2D start = null, end = null;
            start = m_tmpPoints[forward ? 0 : 1];
            end = m_tmpPoints[forward ? 1 : 0];

            // compute the intersection with the target bounding box
            VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
            int i = GraphicsLib.intersectLineRectangle(start, end, dest
                    .getBounds(), m_isctPoints);
            if (i > 0)
                end = m_isctPoints[0];

            // create the arrow head shape
            AffineTransform at = getArrowTrans(start, end, m_curWidth);
            m_curArrow = at.createTransformedShape(m_arrowHead);

            // update the endpoints for the edge shape
            // need to bias this by arrow head size
            Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
            lineEnd.setLocation(0, -m_arrowHeight);
            at.transform(lineEnd, lineEnd);
        } else {
            m_curArrow = null;
        }

        // create the edge shape
        Shape shape = null;
        double n1x = m_tmpPoints[0].getX();
        double n1y = m_tmpPoints[0].getY();
        double n2x = m_tmpPoints[1].getX();
        double n2y = m_tmpPoints[1].getY();
        switch (type) {
        case Constants.EDGE_TYPE_LINE:
            m_line.setLine(n1x, n1y, n2x, n2y);
            shape = m_line;
            break;
        case Constants.EDGE_TYPE_CURVE:
            getCurveControlPoints(edge, m_ctrlPoints, n1x, n1y, n2x, n2y);
            m_cubic.setCurve(n1x, n1y, m_ctrlPoints[0].getX(), m_ctrlPoints[0]
                    .getY(), m_ctrlPoints[1].getX(), m_ctrlPoints[1].getY(),
                    n2x, n2y);
            shape = m_cubic;
            break;
        case BSPLINE:
            // computeBSplineControlPoints(false, 1, edge);
            // see if you can use a different type of curve here
             getCurveControlPoints(edge, m_ctrlPoints, n1x, n1y, n2x, n2y);
             m_cubic.setCurve(n1x, n1y, m_ctrlPoints[0].getX(),
             m_ctrlPoints[0]
             .getY(), m_ctrlPoints[1].getX(), m_ctrlPoints[1].getY(),
             n2x, n2y);
             shape = m_cubic;
            break;
        default:
            throw new IllegalStateException("Unknown edge type");
        }

        // return the edge shape
        return shape;
    }

    @Override
    public void setBounds(VisualItem item) {
//        if (!m_manageBounds)
//            return;
//        if (m_edgeType == BSPLINE) {
//            Rectangle2D.Double r = new Rectangle2D.Double();
//            ArrayList<Point2D.Double> controlPoints = ((BSplineEdgeItem) item)
//                    .getControlPoints();
//            if (controlPoints != null) {
//                for (int i = 0; i < controlPoints.size(); i++) {
//                    r.add(controlPoints.get(i));
//                }
//                GraphicsLib.setBounds(item, r, getStroke(item));
//                if ( m_curArrow != null ) {
//                    Rectangle2D bbox = (Rectangle2D)item.get(VisualItem.BOUNDS);
//                    Rectangle2D.union(bbox, m_curArrow.getBounds2D(), bbox);
//                }
//            } else {
//                item.setBounds(item.getX(), item.getY(), 0, 0);
//            }
//        } else {
//            super.setBounds(item);
//        }
        
        super.setBounds(item);
    }

    // draw uniform cubic B-spline
    void drawCubicBSpline(Graphics g, EdgeItem item) {
        int nSteps = 10;
        double xA, yA, xB, yB, xC, yC, xD, yD;
        double a0, a1, a2, a3, b0, b1, b2, b3;
        double x = 0, y = 0, previousX, previousY;

        ArrayList<Point2D.Double> controlPoints = ((BSplineEdgeItem) item)
                .getControlPoints();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.gray);
        BasicStroke bs = new BasicStroke(1);
        g2d.setStroke(bs);

        if (controlPoints.size() <= 2) {
            g2d.drawLine((int) item.getSourceItem().getX(), (int) item
                    .getSourceItem().getY(), (int) item.getTargetItem().getX(),
                    (int) item.getTargetItem().getY());
        }

        for (int i = 1; i < controlPoints.size() - 2; i++) {
            xA = controlPoints.get(i - 1).getX();
            yA = controlPoints.get(i - 1).getY();

            xB = controlPoints.get(i).getX();
            yB = controlPoints.get(i).getY();

            xC = controlPoints.get(i + 1).getX();
            yC = controlPoints.get(i + 1).getY();

            xD = controlPoints.get(i + 2).getX();
            yD = controlPoints.get(i + 2).getY();

            /*
             * Apply this matrix to the three points and obtain line-matrix: |-1
             * 3 -3 1| | 3 -6 0 3| | -3 0 3 0| | 1 4 1 0| * 1/6
             */

            a3 = (-xA + 3 * (xB - xC) + xD) / 6;
            b3 = (-yA + 3 * (yB - yC) + yD) / 6;

            a2 = (xA - 2 * xB + xC) / 2;
            b2 = (yA - 2 * yB + yC) / 2;

            a1 = (-xA + xC) / 2;
            b1 = (-yA + yC) / 2;

            a0 = (xA + 4 * xB + xC) / 6;
            b0 = (yA + 4 * yB + yC) / 6;

            previousX = a0;
            previousY = b0;

            for (int j = 1; j <= nSteps; j++) {

                double t = (double) j / (double) nSteps;
                x = (a3 * t * t + a2 * t + a1) * t + a0;
                y = (b3 * t * t + b2 * t + b1) * t + b0;

                g2d
                        .drawLine((int) previousX, (int) previousY, (int) x,
                                (int) y);

                previousX = x;
                previousY = y;
            }
        }
    }

    private void computeControlPoints(boolean removeSharedAncestor,
            double bundling, EdgeItem edge) {
        NodeItem source, target, parent1 = null, parent2 = null;

        Point2D.Double temp;

        ArrayList<Point2D.Double> p1 = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> p2 = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> p = new ArrayList<Point2D.Double>();

        int d1, d2, i, N;
        double ux, uy, dx, dy;

        source = edge.getSourceItem();
        target = edge.getTargetItem();

        d1 = tree.getDepth(source.getRow());
        parent1 = source;

        d2 = tree.getDepth(target.getRow());
        parent2 = target;

        p1.add(new Point2D.Double(source.getX(), source.getY()));
        p1.add(new Point2D.Double(source.getX(), source.getY()));
        p1.add(new Point2D.Double(source.getX(), source.getY()));

        p2.add(new Point2D.Double(target.getX(), target.getY()));
        p2.add(new Point2D.Double(target.getX(), target.getY()));
        p2.add(new Point2D.Double(target.getX(), target.getY()));

        while (d1 > d2) {
            parent1 = (NodeItem) tree.getParent(source);
            d1--;
            p1.add(new Point2D.Double(parent1.getX(), parent1.getY()));
            source = parent1;
        }

        while (d2 > d1) {
            parent2 = (NodeItem) tree.getParent(target);
            d2--;
            p2.add(new Point2D.Double(parent2.getX(), parent2.getY()));
            target = parent2;
        }

        while (parent1 != parent2) {
            parent1 = (NodeItem) tree.getParent(parent1);
            parent2 = (NodeItem) tree.getParent(parent2);

            p1.add(new Point2D.Double(parent1.getX(), parent1.getY()));
            p2.add(new Point2D.Double(parent2.getX(), parent2.getY()));
        }

        d1 = p1.size();
        d2 = p2.size();

        if (d1 + d2 == 4 && d1 > 1 && d2 > 1) { // shared parent
            p.add(p1.get(1));
        } else {
            int offset = removeSharedAncestor ? 1 : 0;
            for (i = 0; i < p1.size() - offset; ++i) {
                p.add(p1.get(i));
            }
            for (i = p2.size() - 1; --i >= 0;) {
                p.add(p2.get(i));
            }

            double b = bundling, ib = 1 - bundling;
            N = p.size();

            if (b < 1) {
                NodeItem o = edge.getSourceItem();
                ux = o.getX();
                uy = o.getY();

                o = edge.getTargetItem();
                dx = o.getX();
                dy = o.getY();

                dx = (dx - ux) / (N + 2);
                dy = (dy - uy) / (N + 2);

                for (i = 0; i < N; i++) {
                    temp = p.get(i);
                    temp.setLocation(
                            b * temp.getX() + ib * (ux + (i + 2) * dx), b
                                    * temp.getY() + ib * (uy + (i + 2) * dy));
                    p.set(i, temp);
                }
            }
        }

        ((BSplineEdgeItem) edge).setControlPoints(p);
    }
    
    /**
     * Sets the type of the drawn edge. This must be one of
     * {@link prefuse.Constants#EDGE_TYPE_LINE} or
     * {@link prefuse.Constants#EDGE_TYPE_CURVE}.
     * 
     * @param type
     *            the new edge type
     */
    @Override
    public void setEdgeType(int type) {
        if (type < 0 || (type != BSPLINE && type >= Constants.EDGE_TYPE_COUNT))
            throw new IllegalArgumentException("Unrecognized edge curve type: "
                    + type);
        m_edgeType = type;
    }
}
