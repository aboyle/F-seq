package edu.duke.igsp.gkde.format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

public class WiggleDensityWriter implements DensityWriter {
  
  private BufferedWriter bw;
  private int _step;
  private NumberFormat nf;
  private int _step_cursor;
  
  public WiggleDensityWriter(File f, String chr, long start, int step) throws IOException {
    bw = new BufferedWriter(new FileWriter(f));
    start++; // Wig is 1 based
    bw.write("fixedStep chrom=" + chr + " start=" + start + " step=" + step + "\n");
    this._step = step;
	this._step_cursor = (int)start;
    nf = NumberFormat.getNumberInstance();
    nf.setGroupingUsed(false);
    nf.setMaximumFractionDigits(4);
    nf.setMinimumFractionDigits(4);
  }
  
  public void writeDensity(float[] batch, int start, int length) throws IOException {
    int end = start + length;
    // Include step function option:
    // if(i % step == 0) { print }
    for(int i = start; i < end; ++i) {
    	if(_step_cursor % _step == 0) {
    		bw.write(nf.format(batch[i]) + "\n");
			_step_cursor = 0;
    	}
		_step_cursor++;
  	}
  }
  
  public void close() throws IOException {
    bw.close();
  }

  public void setThreshold(float threshold) {}
}
