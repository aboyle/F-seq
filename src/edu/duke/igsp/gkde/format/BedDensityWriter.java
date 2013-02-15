/*****************************************************************************
  BedDensityWriter.java

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

public class BedDensityWriter implements DensityWriter{

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
  
  public BedDensityWriter(File f, String chr, long chromStart, int step) throws IOException {
    bw = new BufferedWriter(new FileWriter(f));
    this.chromPos = chromStart;
    this.chr = chr;
    this.step = step;
    
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
        }
      }else{ // aboveThreshold
        if(batch[i] > _threshold){
          _currentMax = Math.max(_currentMax, batch[i]);
        }else{
          _aboveThreshold = false;
          doWrite();
        }
      }
    }
  }
  
  private void doWrite() throws IOException {
    bw.write(chr + "\t" + _startPeakPos + "\t" + _currentPos + "\t" + (chr + "." + _counter++) + "\t" + _currentMax + "\n");
    _currentMax = 0.0f;
    _startPeakPos = 0l;
  }

}
