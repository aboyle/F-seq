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
