/*****************************************************************************
  Util.java

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

package edu.duke.igsp.gkde;

import java.io.File;

public class Util {
  public static File makeUniqueFileWithExtension(File outDir, String name, String ext){
    
    int i = 0;
    File nfile = new File(outDir, name + "." + ext);
    while(nfile.exists()){
      nfile = new File(outDir, name +  "-" + ++i + "." + ext);
    }
    return nfile;
  }
  
  public static int computeBandwidth(int featureLength, double sd){
    return (int)((featureLength / 2.0) / (3.0 * sd));
  }
  
  public static double mean(double[] arr){
    double sum = 0.0;
    for(int i = 0; i < arr.length; ++i)
      sum += arr[i];
    return sum / arr.length;
  }
  
  public static double std(double[] arr){
    double m = mean(arr);
    double sum = 0.0;
    for(int i= 0; i < arr.length; ++i){
      sum += Math.pow(arr[i] - m, 2);
    }
    return Math.sqrt(sum / arr.length);
  }
}
