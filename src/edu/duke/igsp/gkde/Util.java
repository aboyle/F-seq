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
