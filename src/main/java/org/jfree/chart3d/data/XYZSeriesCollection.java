/**
 * (C)opyright 2013, by Object Refinery Limited
 */
package org.jfree.chart3d.data;

import java.util.ArrayList;
import java.util.List;
import org.jfree.graphics3d.ArgChecks;

/**
 * A collection of XYZSeries objects.
 */
public class XYZSeriesCollection extends AbstractDataset3D implements XYZDataset {

  private List<XYZSeries> series;

  public XYZSeriesCollection() {
    this.series = new ArrayList<XYZSeries>();
  }

  @Override
  public int getSeriesCount() {
    return this.series.size();
  }

  public void add(XYZSeries series) {
    ArgChecks.nullNotPermitted(series, "series");
    this.series.add(series);
  }

  @Override
  public int getItemCount(int seriesIndex) {
    ArgChecks.nullNotPermitted(this, null);
    XYZSeries s = this.series.get(seriesIndex);
    return s.getItemCount();
  }

  @Override
  public double getX(int seriesIndex, int itemIndex) {
    XYZSeries s = this.series.get(seriesIndex);
    return s.getXValue(itemIndex);
  }

  @Override
  public double getY(int seriesIndex, int itemIndex) {
    XYZSeries s = this.series.get(seriesIndex);
    return s.getYValue(itemIndex);
  }

  @Override
  public double getZ(int seriesIndex, int itemIndex) {
    XYZSeries s = this.series.get(seriesIndex);
    return s.getZValue(itemIndex);
  }
}
