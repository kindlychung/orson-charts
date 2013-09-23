/**
 * (C)opyright 2013, by Object Refinery Limited
 */
package com.orsoncharts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import com.orsoncharts.axis.Axis3D;
import com.orsoncharts.axis.TextAnchor;
import com.orsoncharts.axis.TextUtils;
import com.orsoncharts.event.Chart3DChangeEvent;
import com.orsoncharts.event.Chart3DChangeListener;
import com.orsoncharts.plot.PiePlot3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.graphics3d.Dimension3D;
import com.orsoncharts.graphics3d.Face;
import com.orsoncharts.graphics3d.Object3D;
import com.orsoncharts.graphics3d.Tools2D;
import com.orsoncharts.graphics3d.World;
import com.orsoncharts.graphics3d.swing.Panel3D;
import com.orsoncharts.plot.CategoryPlot3D;

/**
 * A panel designed to display a Chart3D.  The panel will manage:
 * 
 * - the chart title;
 * - the chart viewing area;
 * - mouse interaction (drag to rotate, mouse-wheel to zoom in and out);
 * - viewing controls (zoom in/out/best-fit, buttons for rotations)
 * - export to PNG, SVG and PDF.
 */
public class ChartPanel3D extends Panel3D implements Chart3DChangeListener {

  /** The chart being rendered. */
  private Chart3D chart;

  /** The chart box (a frame of reference for the chart). */
  private ChartBox3D chartBox;

  /**
   * Creates a new chart panel to display the specified chart.
   *
   * @param chart  the chart.
   */
  public ChartPanel3D(Chart3D chart) {
    super(new World());
    this.chart = chart;
    this.chart.addChangeListener(this);
    setWorld(renderWorld());
  }
  
  // listen for dataset changes and update the world
  // then trigger a repaint
  private World renderWorld() {
    World world = new World();  // TODO: when we re-render the chart, should we
        // create a new world, or recycle the existing one?
    
    Dimension3D dim = this.chart.getPlot().getDimensions();
    double w = dim.getWidth();
    double h = dim.getHeight();
    double d = dim.getDepth();
    if (!(this.chart.getPlot() instanceof PiePlot3D)) {
      this.chartBox = new ChartBox3D(w, h, d, -w / 2, -h / 2, -d / 2, Color.WHITE);
      world.add(chartBox.getObject3D());    
    }

    this.chart.getPlot().composeToWorld(world, -w / 2, -h / 2, -d / 2);
    return world;
  }

  /**
   * Draws the chart to the specified output target.
   * 
   * @param g2  the output target. 
   */
  @Override
  public void drawContent(Graphics2D g2) {
    super.drawContent(g2); 
    Dimension dim = getSize();
    AffineTransform saved = g2.getTransform();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    
    g2.translate(dim.width / 2, dim.height / 2);

    Dimension3D dim3D = this.chart.getPlot().getDimensions();
    double w = dim3D.getWidth();
    double h = dim3D.getHeight();
    double depth = dim3D.getDepth();
    
    // if a PiePlot3D then there will be an overlay to track the pie label positions
    if (this.chart.getPlot() instanceof PiePlot3D) {
      PiePlot3D p = (PiePlot3D) this.chart.getPlot();
      World labelOverlay = new World();
      List<Object3D> objs = p.getLabelFaces(-w / 2, -h / 2, -depth / 2);
      for (Object3D obj : objs) {
        labelOverlay.add(obj);
      }
      Point2D[] pts = labelOverlay.calculateProjectedPoints(getViewPoint(),
           1000f);
      for (int i = 0; i < p.getDataset().getItemCount() * 2; i++) {
        Face f = labelOverlay.getFaces().get(i);
        if (Tools2D.area2(pts[f.getVertexIndex(0)], pts[f.getVertexIndex(1)], pts[f.getVertexIndex(2)]) > 0) {
          Comparable key = p.getDataset().getKey(i / 2);
          g2.setColor(Color.BLACK);
          g2.setFont(p.getDefaultSectionFont());
          Point2D pt = Tools2D.centrePoint(pts[f.getVertexIndex(0)], pts[f.getVertexIndex(1)], pts[f.getVertexIndex(2)], pts[f.getVertexIndex(3)]);
          TextUtils.drawAlignedString(key.toString(), g2, (float) pt.getX(), (float) pt.getY(), TextAnchor.CENTER);
         }
      }      
    }
    
    // if a CategoryPlot3D then there will be a ChartBox overlay

    // if an XYZPlot then there will be a ChartBox overlay
    if (this.chart.getPlot() instanceof XYZPlot || this.chart.getPlot() instanceof CategoryPlot3D) {
      World labelOverlay = new World();
      ChartBox3D cb = new ChartBox3D(w, h, depth, -w / 2, -h / 2, -depth / 2, new Color(0, 0, 255, 200));
      labelOverlay.add(cb.getObject3D());
      Point2D[] axisPts2D = labelOverlay.calculateProjectedPoints(getViewPoint(),
           1000f);
//      Point2D axisPt = axisPts2D[0];
//      g2.setPaint(Color.YELLOW);
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));
//
//      g2.setPaint(Color.RED);
//      axisPt = axisPts2D[1];
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));
//      axisPt = axisPts2D[2];
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));
//      axisPt = axisPts2D[3];
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));
//
//      g2.setPaint(Color.GREEN);
//      axisPt = axisPts2D[4];
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));
//      axisPt = axisPts2D[5];
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));
//      axisPt = axisPts2D[6];
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));
//      axisPt = axisPts2D[7];
//      g2.fill(new Rectangle2D.Double(axisPt.getX()-2.0, axisPt.getY()-2.0, 4.0, 4.0));

      // vertices
      Point2D v0 = axisPts2D[0];
      Point2D v1 = axisPts2D[1];
      Point2D v2 = axisPts2D[2];
      Point2D v3 = axisPts2D[3];
      Point2D v4 = axisPts2D[4];
      Point2D v5 = axisPts2D[5];
      Point2D v6 = axisPts2D[6];
      Point2D v7 = axisPts2D[7];

      // faces
      boolean a = this.chartBox.faceA().isRendered();
      boolean b = this.chartBox.faceB().isRendered();
      boolean c = this.chartBox.faceC().isRendered();
      boolean d = this.chartBox.faceD().isRendered();
      boolean e = this.chartBox.faceE().isRendered();
      boolean f = this.chartBox.faceF().isRendered();

      Axis3D xAxis = null, yAxis = null, zAxis = null;
      if (this.chart.getPlot() instanceof XYZPlot) {
          XYZPlot plot = (XYZPlot) this.chart.getPlot();
          xAxis = plot.getXAxis();
          yAxis = plot.getYAxis();
          zAxis = plot.getZAxis();
      } else if (this.chart.getPlot() instanceof CategoryPlot3D) {
          CategoryPlot3D plot = (CategoryPlot3D) this.chart.getPlot();
          xAxis = plot.getColumnAxis();
          yAxis = plot.getValueAxis();
          zAxis = plot.getRowAxis();
      }
      double ab = (count(a, b) == 1 ? v0.distance(v1) : 0.0);
      double bc = (count(b, c) == 1 ? v3.distance(v2) : 0.0);
      double cd = (count(c, d) == 1 ? v4.distance(v7) : 0.0);
      double da = (count(d, a) == 1 ? v5.distance(v6) : 0.0);
      double be = (count(b, e) == 1 ? v0.distance(v3) : 0.0);
      double bf = (count(b, f) == 1 ? v1.distance(v2) : 0.0);
      double df = (count(d, f) == 1 ? v6.distance(v7) : 0.0);
      double de = (count(d, e) == 1 ? v5.distance(v4) : 0.0);
      double ae = (count(a, e) == 1 ? v0.distance(v5) : 0.0);
      double af = (count(a, f) == 1 ? v1.distance(v6) : 0.0);
      double cf = (count(c, f) == 1 ? v2.distance(v7) : 0.0);
      double ce = (count(c, e) == 1 ? v3.distance(v4) : 0.0);

      if (count(a, b) == 1 && longest(ab, bc, cd, da)) {
        xAxis.render(g2, v0, v1, v7, true);
      }
      if (count(b, c) == 1 && longest(bc, ab, cd, da)) {
        xAxis.render(g2, v3, v2, v6, true);
      }
      if (count(c, d) == 1 && longest(cd, ab, bc, da)) {
        xAxis.render(g2, v4, v7, v1, true);
      }
      if (count(d, a) == 1 && longest(da, ab, bc, cd)) {
        xAxis.render(g2, v5, v6, v3, true);
      }

      if (count(b, e) == 1 && longest(be, bf, df, de)) {
        yAxis.render(g2, v0, v3, v7, true);
      }
      if (count(b, f) == 1 && longest(bf, be, df, de)) {
        yAxis.render(g2, v1, v2, v4, true);
      }
      if (count(d, f) == 1 && longest(df, be, bf, de)) {
        yAxis.render(g2, v6, v7, v0, true);
      }
      if (count(d, e) == 1 && longest(de, be, bf, df)) {
        yAxis.render(g2, v5, v4, v1, true);
      }

      if (count(a, e) == 1 && longest(ae, af, cf, ce)) {
        zAxis.render(g2, v0, v5, v2, true);
      }
      if (count(a, f) == 1 && longest(af, ae, cf, ce)) {
        zAxis.render(g2, v1, v6, v3, true);
      }
      if (count(c, f) == 1 && longest(cf, ae, af, ce)) {
        zAxis.render(g2, v2, v7, v5, true);
      }
      if (count(c, e) == 1 && longest(ce, ae, af, cf)) {
        zAxis.render(g2, v3, v4, v6, true);
      }
    }
    g2.setTransform(saved);
  }

  private boolean longest(double x, double a, double b, double c) {
    return x >= a && x >= b && x >= c;
  }

  private int count(boolean a, boolean b) {
    int result = 0;
    if (a) {
      result++;
    }
    if (b) {
      result++;
    }
    return result;
  }

    @Override
    public void chartChanged(Chart3DChangeEvent event) {
      World world = renderWorld();
      setWorld(world);
    }


}
