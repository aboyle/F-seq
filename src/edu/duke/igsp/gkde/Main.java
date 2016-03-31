/*****************************************************************************
  Main.java

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

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import edu.duke.igsp.gkde.KDEChromosome.Settings;
import edu.duke.igsp.gkde.KDEChromosome;
import edu.duke.igsp.gkde.format.BedDensityWriter;
import edu.duke.igsp.gkde.format.NpfDensityWriter;
import edu.duke.igsp.gkde.format.BedReader;
import edu.duke.igsp.gkde.format.DensityWriter;
import edu.duke.igsp.gkde.format.WiggleDensityWriter;
import edu.duke.igsp.gkde.background.*;

public class Main {
  
  public static void main(String[] argv) throws Exception {
   
    
    Options opts = new Options();
    opts.addOption("s",true,"wiggle track step (default=1)");
    opts.addOption("l",true, "feature length (default=600)");
    opts.addOption("f",true, "fragment size (default=estimated from data)");
//    opts.addOption("b", true, "bandwidth (default=200)");
//    opts.addOption("w", true, "window (default=3800");
    opts.addOption("wg", true, "wg threshold set (default = calculated)");
    opts.addOption("c", true, "genomic count of sequence reads (default = calculated)");
    opts.addOption("h", false, "print usage");
    opts.addOption(OptionBuilder.withArgName( "input dir" )
        .hasArg()
        .withDescription( "input directory (default=current directory)" )
        .isRequired(false)
        .create( "d" ));
    opts.addOption(OptionBuilder.withArgName( "output dir" )
        .hasArg()
        .withDescription( "output directory (default=current directory)" )
        .isRequired(false)
        .create( "o" ));
    opts.addOption(OptionBuilder.withArgName( "background dir" )
            .hasArg()
            .withDescription( "background directory (default=none)" )
            .isRequired(false)
            .create( "b" ));
    opts.addOption(OptionBuilder.withArgName( "ploidy dir" )
            .hasArg()
            .withDescription( "ploidy/input directory (default=none)" )
            .isRequired(false)
            .create( "p" ));
    opts.addOption(OptionBuilder.withArgName( "wig | bed | npf" )
      .hasArg()
      .withDescription(  "output format (default wig)" )
      .isRequired(false)
      .create( "of" ));
    opts.addOption("t", true, "threshold (standard deviations) (default=4.0)");
//    opts.addOption("r", true, "background ratio (default=2.0)");
    opts.addOption("v", false, "verbose output");
    
    CommandLineParser parser = new GnuParser();
    int fragment_size = -1;
    int fragment_offset = 0;
    long featureLength = 600l;
//    float thresh = 2;
    float threshold = KDEChromosome.Settings.DEFAULT_THRESHOLD;
    int step = 1;
    boolean showHelp = false;
    boolean verbose = false;
    String inputDirectory = null;
    String backgroundDirectory = null;
    String ploidyDirectory = null;
    String[] files = null;
    String[] bgfiles = {};
    String[] ipfiles = {};
    String outputFormat = "wig";
    File outputDirectory = new File(System.getProperty("user.dir"));
    
    long bandwidth = 0l;
    long window = 0l;
  	long ncuts = 0l;
	float temp_threshold = 0f;

    System.out.println("F-Seq Version 1.85");
    
    try{
      CommandLine cmd = parser.parse(opts, argv);
      showHelp = (cmd.hasOption("h"));
      verbose = (cmd.hasOption("v"));
      if(cmd.hasOption("s"))
        step = Integer.parseInt(cmd.getOptionValue("s"));
      if(cmd.hasOption("f"))
    	fragment_size = Integer.parseInt(cmd.getOptionValue("f"));
      if(cmd.hasOption("d")) //input directory
        inputDirectory = cmd.getOptionValue("d");
      if(cmd.hasOption("b")) //background directory
        backgroundDirectory = cmd.getOptionValue("b");
      if(cmd.hasOption("p")) //ploidy|input directory
        ploidyDirectory = cmd.getOptionValue("p");
      if(cmd.hasOption("l")) // bandwidth
        featureLength = Long.parseLong(cmd.getOptionValue("l"));
      if(cmd.hasOption("of")){ // output format
        outputFormat = cmd.getOptionValue("of");
        if(!outputFormat.equals("wig") && !outputFormat.equals("bed") && !outputFormat.equals("npf")){
          System.out.println("Parameter error: output format must be 'wig' or 'bed'.");
          showHelp = true;
        }
      }
      if(cmd.hasOption("t")){ // threshold (standard deviations)
        threshold = Float.parseFloat(cmd.getOptionValue("t"));
      }
      if(cmd.hasOption("o")){ // output directory
        String out = cmd.getOptionValue("o");
        outputDirectory = new File(out);
        if(!outputDirectory.exists() && !outputDirectory.isDirectory()){
          System.out.println("Output directory '" + out + "' is not a valid directory.");
          showHelp = true;
        }
      }
      
		if(cmd.hasOption("wg"))
			temp_threshold = Float.parseFloat(cmd.getOptionValue("wg"));
		if(cmd.hasOption("c"))
			ncuts = Long.parseLong(cmd.getOptionValue("c"));

      // TESTING ONLY
   //   if(cmd.hasOption("w")) // window
   //     window = Long.parseLong(cmd.getOptionValue("w"));
      //if(cmd.hasOption("b")) // window
      //  bandwidth = Long.parseLong(cmd.getOptionValue("b"));
      
      files = cmd.getArgs(); // input files
      //bgfiles = cmd.getArgs(); // background files
    }catch(Exception e){
      System.out.println("Error parsing arguments: " + e.getMessage());
      e.printStackTrace();
      showHelp = true;
    }
    
    if(showHelp || (inputDirectory == null && files.length==0)){
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "fseq [options]... [file(s)]...", opts );
      System.exit(1);
    }
    
    
    File[] pfiles = getFiles(inputDirectory, files);
    File[] background_files = getFiles(backgroundDirectory, bgfiles);
    File[] ploidy_files = getFiles(ploidyDirectory, ipfiles);
    
    KDEChromosome[] chrs = BedReader.read(pfiles);
    //KDEChromosome[] input = BedReader.read(ifiles);
    
    //compute fragment offset
    if(fragment_size == -1) {
    	fragment_size = wgShiftCalc(chrs);
    }
    fragment_offset = (int)(fragment_size/2);
    
	if(ncuts == 0l) {
	  	for(int i = 0; i < chrs.length; ++i){
 	 		ncuts += (long)chrs[i].getLength();
	  	}
    }

    KDEChromosome.Settings settings = null;
    if(bandwidth > 0 || window > 0){
      settings = new KDEChromosome.Settings(bandwidth,window,threshold,fragment_offset, ncuts);
    }else{
      settings = new KDEChromosome.Settings(featureLength, threshold, fragment_offset, ncuts);
    }

	float wg_threshold = wgThreshold(settings, chrs);    
	if(temp_threshold != 0f) {
		wg_threshold = temp_threshold;
	}
    //KDEChromosome.Settings bg_settings = null;
    //bg_settings = new KDEChromosome.Settings(featureLength*2, threshold, fragment_offset);
    

    //int background_size = 0;
    //int input_size = 0;
    //float bg_ratio = 0;
    //float sd = 0;

    if(verbose){
        System.out.println("Settings: ");
        System.out.println("\twindow=" + (settings.window * 2));
        System.out.println("\tbandwidth=" + (settings.bandwidth));
        //System.out.println("\tfragment offset=" + (settings.offset));
    	System.out.println("\tthreshold = " + wg_threshold);
    	System.out.println("\test. fragment size = " + fragment_size);
    	System.out.println("\tsequence length = " + chrs[0].getSequenceLength());
    }
    
//    if(backgroundDirectory != null) {
//	    for(int i = 0; i < input.length; ++i) {
//	    	background_size += input[i].getLength();
//	    }
//	    for(int i = 0; i < chrs.length; ++i) {
//	    	input_size += chrs[i].getLength();
//	    }
//	    bg_ratio = (float)input_size/(float)background_size;
//	    sd = computeSD(bg_settings, input);
//	    //System.out.println("Sample Ratio: " + bg_ratio);
//	    //System.out.println("Input Size: " + input_size);
//	    //System.out.println("Background Size: " + background_size);
//	    //System.out.println("Input standard deviation: " + (settings.threshold * (float)Math.sqrt((double)bg_ratio * (double)sd * (double)sd)));
//	    //System.out.println("Data standard deviation: " + settings.threshold * computeSD(settings, chrs));
//    }
    
    for(int i = 0; i < chrs.length; ++i){
      if(chrs[i].getFirstPos() == chrs[i].getLastPos()){
        System.out.println("Warning: " + chrs[i].getChromosome() + " has size zero.  Skipping.");
        continue;
      }
      File ofile = Util.makeUniqueFileWithExtension(outputDirectory, chrs[i].getChromosome(), outputFormat);
      
      DensityWriter dw = null;
      if(outputFormat.equals("wig")){
        dw = new WiggleDensityWriter(ofile, chrs[i].getChromosome(), chrs[i].getFirstPos(), step);
      }else{
    	if(outputFormat.equals("npf")) {
    		dw = new NpfDensityWriter(ofile, chrs[i].getChromosome(), chrs[i].getFirstPos(), step);	
    	} else {
    		dw = new BedDensityWriter(ofile, chrs[i].getChromosome(), chrs[i].getFirstPos(), step);
    	}
      }

      //Function takes all? or new function for each?
//      if(backgroundDirectory != null) {
//    	  boolean hit = false;
//    	  for(int j = 0; j < background_files.length; ++j) {
//    		  if(background_files[j].getName().equals(chrs[i].getChromosome() + ".bff")) {
//    			  System.out.println("Running background on Chromosome " + chrs[i].getChromosome());
//    			  chrs[i].runBG(settings, dw, verbose, wg_threshold, background_files[j]);
//    			  hit = true;
//    		  }
//    	  }
//    	  if(!hit)
//    		  System.out.println("No background for Chromosome " + chrs[i].getChromosome());
//      } else {
//    	  if(ploidyDirectory !=)
//    	  chrs[i].run(settings, dw, verbose, wg_threshold);
//      }
      chrs[i].run(settings, dw, verbose, wg_threshold, background_files, ploidy_files);
      dw.close();
    }
    
    //kde.showGraph();
  }
 
  private static float computeSD(Settings settings, KDEChromosome[] chrs){
	  	Random r = new Random();

	  	double size = 0;
	  	double ncuts = 0;
	  
	  	for(int i = 0; i < chrs.length; ++i){
	  		size += (int)Math.abs(chrs[i].getLastPos() - chrs[i].getFirstPos());
	  		ncuts += chrs[i].getLength();
	  	}

	    int totalWindow = 1 + (int)(settings.window * 2);	  	
	    int cutDensity = (int)((ncuts / size) * totalWindow);
	    int thresholdIterations = 10000;
	  	double[] densities = new double[thresholdIterations];
	    long[] cuts = new long[cutDensity];
	  	
	    for(int i = 0; i < thresholdIterations; ++i){
	      for(int j = 0; j < cuts.length; ++j)
	        cuts[j] = r.nextInt(totalWindow);
	      Arrays.sort(cuts);
	      densities[i] = density(settings, (long)settings.window, (int)(cuts.length/2.0), cuts);
	    }
	    
	    double std = Util.std(densities);
	    
	    return (float)(std);
}
  
  private static float wgThreshold(Settings settings, KDEChromosome[] chrs){
	  	Random r = new Random();

	  	double size = 0;
	  	double ncuts = 0;
	  
	  	
	  	for(int i = 0; i < chrs.length; ++i){
	  		size += (int)Math.abs(chrs[i].getLastPos() - chrs[i].getFirstPos());
	  		ncuts += chrs[i].getLength();
	  	}
	  	
	    int totalWindow = 1 + (int)(settings.window * 2);	  	
	    int cutDensity = (int)((ncuts / size) * totalWindow);
	    int thresholdIterations = 10000;
	  	double[] densities = new double[thresholdIterations];
	    long[] cuts = new long[cutDensity];
	  	
	    for(int i = 0; i < thresholdIterations; ++i){
	      for(int j = 0; j < cuts.length; ++j)
	        cuts[j] = r.nextInt(totalWindow);
	      Arrays.sort(cuts);
	      densities[i] = density(settings, (long)settings.window, (int)(cuts.length/2.0), cuts);
	    }
	    
	    double mean = Util.mean(densities);
	    double std = Util.std(densities);
	    
	    return (float)(mean + settings.threshold * std);
  } 
  
  private static int wgShiftCalc(KDEChromosome chrs[]){
//	  for(int i = 0; i < chrs.length; ++i) {
		//These fragments are usually < 200bp so we can assume that the average is < 500
		
//	  }
	  //Doing Wilcoxon-Mann-Whitney Rank Sum Test
	  //Can compute confidence interval if necessary
	  
	  //get biggest chr for estimate
	  int max_chr = 0;
	  long max_chr_length = chrs[0].getLength();
	  for(int i = 0; i < chrs.length; ++i){
	  	if(chrs[i].getLength() > max_chr_length) {
	  		max_chr = i;
	  		max_chr_length = chrs[i].getLength(); //This is actually the size of the array - poor name
	  	}
	  }

	  KDEChromosome.Sequence[] seqs = chrs[max_chr].getCuts();
	  long cur_pos;
	  long last_pos;
	  ArrayList<Integer> d_temp = new ArrayList<Integer>();
	  int k;
	  
	  for(k = 0; k < max_chr_length; ++k) {
		  if(seqs[k].getStrand()) {
			  break;
		  }
	  }
	  
	  int max_range = 500; //max search range for sequences
	  int max = 1000000; //1 million is a good estimate
	  int prior = 150;
	  if(max > seqs.length - max_range-k) {
		  max = seqs.length-max_range-k;
	  }
	  
	  for(int j = k; j < k+max; ++j) {
	  	if(j > max_chr_length) {
	  		break;
	  	}
		  if(seqs[j].getStrand()) {
			  last_pos = seqs[j].getPosition();
			  int back_check = j - 10000;
			  if(j < 10000) {
				  back_check = j;
			  }
			  for(int i = back_check; i < j+10000; ++i) {
				  if(i >= max) 
					  break;
				  if(!seqs[i].getStrand()) {
					  if(Math.abs(seqs[i].getPosition()-last_pos-prior) < max_range) {
						  Integer temp = new Integer((int)(seqs[i].getPosition()-last_pos));
						  d_temp.add(temp);
					  }
				  }
			  }
		  }
	  }
	  
      int[] d = new int[d_temp.size()];
      for (int j = 0; j < d.length; ++j)
        d[j] = d_temp.get(j).intValue();
      
      Arrays.sort(d);
	  int median;
	  if(d.length % 2 == 0) { //even
		  median = (int)((double)(d[(int)Math.floor((double)d.length/2.0)] + d[(int)Math.ceil((double)d.length/2.0)])/2.0);
	  } else {
		  median = d[d.length/2];
	  }
	 // System.out.println("median " + median);
	 // for(int m = 0; m < d.length; ++m) {
	//	  System.out.println(d[m]);
	 // }
	  return median;
  }
  
  private static float density(Settings settings, long chromPos, int cutIdx, long[] cuts){
	    
	    long minPos = chromPos - settings.window;
	    long maxPos = chromPos + settings.window;
	    
	    double[] PRECOMPUTE = settings.precompute;
	    
	    double sum = 0.0;
	    for(int i = cutIdx-1; i > -1; --i){
	      if (cuts[i] < minPos) 
	        break;
	      int d = Math.abs((int)(cuts[i] - chromPos));
	      sum += settings.precompute[d];
	    }
	    
	    for(int i = cutIdx; i < cuts.length; ++i){
	      if (cuts[i] > maxPos) break;
	      
	      int d = Math.abs((int)(cuts[i] - chromPos));
	      
	      //System.out.println(d);
	      if(d > PRECOMPUTE.length-1)
	        throw new IllegalStateException();
	      sum += PRECOMPUTE[d];
	    }
	    
	    return (float)(sum / (double)settings.bandwidth);
  }
  
  private static File[] getFiles(String directory, String[] files){
  
    File[] inputFiles = null;
    
    if(directory != null){
      File dir = new File(directory);
      if (!dir.exists() || !dir.isDirectory()){
        System.out.println("Directory parameter " + directory + " does not exist or is not a directory.");
        System.exit(1);
      }
      if(files.length == 0){
        inputFiles = dir.listFiles(new FileFilter(){
          public boolean accept(File f) {
            return !f.isHidden() && f.isFile();
          }
        });
      }
    }else{
      directory = System.getProperty("user.dir");
    }
    
    if(inputFiles == null){
      inputFiles = new File[files.length];
      for(int i = 0; i < files.length; ++i){
        File f = new File(files[i]);
        boolean exists = false;
        if(!(exists = f.exists())){
          f = new File(directory, files[i]);
          exists = f.exists();
        }
        if(!exists){
          System.out.println("Input file " + files[i] + " does not exist.");
          System.exit(1);
        }
        inputFiles[i] = f;
      }
    }
   
    return inputFiles;
  }
}
