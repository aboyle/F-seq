/*****************************************************************************
  NpfDensityWriter.java

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
import java.util.Arrays;
import java.util.Random;
import java.text.NumberFormat;

import edu.duke.igsp.gkde.KDEChromosome;
import edu.duke.igsp.gkde.Util;
import edu.duke.igsp.gkde.KDEChromosome.Settings;
import edu.duke.igsp.gkde.background.BffReader;
import edu.duke.igsp.gkde.background.IffReader;
import edu.duke.igsp.gkde.background.WigChromosome;

public class NpfDensityWriter implements DensityWriter{

  private BufferedWriter bw;
  private final long chromPos;
  private final int step;
  private final String chr;
  
  private float _threshold = 0.0f;
  private long _currentPos;
  private long _counter = 1;
  
  private long _startPeakPos = 0;
  private boolean _aboveThreshold = false;
  private float _currentMax = 0.0f;
  private long _currentMaxPos = 0;
  
  private NumberFormat nf;
  
  public NpfDensityWriter(File f, String chr, long chromStart, int step) throws IOException {
	    bw = new BufferedWriter(new FileWriter(f));
	    this.chromPos = chromStart;
	    this.chr = chr;
	    this.step = step;
	    nf = NumberFormat.getNumberInstance();
	    nf.setGroupingUsed(false);
	    nf.setMaximumFractionDigits(8);
	    nf.setMinimumFractionDigits(8);
	    
	    _currentPos = chromPos;
	  }
	  
	  public void setThreshold(float threshold){
	    _threshold = threshold;
	  }
	  
	  public void close() throws IOException {
	    if(_aboveThreshold)
	      doWrite();
	    bw.close();
	  }

	  public void writeDensity(float[] batch, int start, int length)
	      throws IOException {
	    int end = start + length;
	    for(int i = start; i < end; ++i, ++_currentPos){
	      if(!_aboveThreshold){
	        if(batch[i] > _threshold){
	          _aboveThreshold = true;
	          _startPeakPos = _currentPos;
	          _currentMax = batch[i];
	          _currentMaxPos = _currentPos;
	        }
	      }else{ // aboveThreshold
	        if(batch[i] > _threshold){
	          _currentMax = Math.max(_currentMax, batch[i]);
	          if(_currentMax == batch[i])
	        	  _currentMaxPos = _currentPos;
	        }else{
	          _aboveThreshold = false;
	          doWrite();
	        }
	      }
	    }
	  }
	  
	  private void doWrite() throws IOException {
		long centerPos = (long)_currentMaxPos - _startPeakPos;
	    bw.write(chr + "\t" + _startPeakPos + "\t" + (_currentPos-1) + "\t" + (chr + "." + _counter++) + "\t" + "0" + "\t" + "." + "\t" + nf.format(_currentMax) + "\t" + "-1" + "\t" + "-1" + "\t" + centerPos + "\n");
	    _currentMax = 0.0f;
	    _currentMaxPos = 0;
	    _startPeakPos = 0l;
	  }

}
