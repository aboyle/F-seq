package edu.duke.igsp.gkde.format;

import java.io.IOException;

public interface DensityWriter {
  public void writeDensity(float[] batch, int start, int length) throws IOException;
  
  public void close() throws IOException;
  
  public void setThreshold(float threshold);
}
