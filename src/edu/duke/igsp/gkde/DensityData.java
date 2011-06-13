package edu.duke.igsp.gkde;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.xy.XYDataset;


public class DensityData  extends AbstractSeriesDataset implements XYDataset {

  private double _start;
  private float[] _density;
  
  public DensityData(float[] f, long startPos){
    _start = startPos;
    _density = f;
  }
  
  /**
   * Returns the order of the domain (X) values.
   * 
   * @return The domain order.
   */
  public DomainOrder getDomainOrder() {
      return DomainOrder.ASCENDING;
  }
  
  /**
   * Returns the x-value (as a double primitive) for an item within a series.
   * 
   * @param series  the series index (zero-based).
   * @param item  the item index (zero-based).
   * 
   * @return The value.
   */
  public double getXValue(int series, int item) {
      return (double)(_start + item);
  }

  /**
   * Returns the y-value (as a double primitive) for an item within a series.
   * 
   * @param series  the series index (zero-based).
   * @param item  the item index (zero-based).
   * 
   * @return The value.
   */
  public double getYValue(int series, int item) {
      return (double)_density[item];
  }

  @Override
  public int getSeriesCount() {
    return 1;
  }

  @Override
  public Comparable getSeriesKey(int series) {
    return "chrom";
  }

  public int getItemCount(int series) {
    return _density.length;
  }

  public Number getX(int series, int item) {
    return new Double(getXValue(series, item));
  }

  public Number getY(int series, int item) {
    return new Double(getYValue(series, item));
  }
}
