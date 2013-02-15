/*****************************************************************************
  WiggleDensityWriter.java

  (c) 2008-2013 - Alan Boyle
  Department of Genetics
  Stanford University
  aboyle@gmail.com

  Licensed under the GNU General Public License 3.0 license.

  This file is part of F-seq.

  F-seq is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  F-seq is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with F-seq.  If not, see <http://www.gnu.org/licenses/>.

******************************************************************************/

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
