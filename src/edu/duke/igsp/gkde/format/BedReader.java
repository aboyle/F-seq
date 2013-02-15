/*****************************************************************************
  BedReader.java

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.duke.igsp.gkde.KDEChromosome;

/**
 * Bed files are not necessarily sorted. Must load everything before sort.
 * Assumes all come from one chromosome
 * 
 * @author jhg9
 * 
 * Modified to keep strand information
 * @author apb9
 * 
 */
public class BedReader {

  public static KDEChromosome[] read(File[] files) throws IOException {

    HashMap<String, ArrayList<KDEChromosome.Sequence>> chrMap = new HashMap<String, ArrayList<KDEChromosome.Sequence>>();

    String currentChr = null;
    ArrayList<KDEChromosome.Sequence> currentCuts = null;
    boolean lengthSet = false;
    int sequenceLength = 0;
    
    for (int i = 0; i < files.length; ++i) {

      BufferedReader br = new BufferedReader(new FileReader(files[i]));
      
      String line;

      while ((line = br.readLine()) != null) {
        String[] arr = line.split("\t");

        if (arr.length < 6)
          badFile(files[i]);

        if (arr[0] != currentChr) {
          if (!chrMap.containsKey(arr[0])) {
            chrMap.put(arr[0], new ArrayList<KDEChromosome.Sequence>());
          }
          currentChr = arr[0];
          currentCuts = chrMap.get(arr[0]);
        }
        
        KDEChromosome.Sequence seq = new KDEChromosome.Sequence();
        
        try {
          long s = Long.parseLong(arr[1]);
          long e = Long.parseLong(arr[2]);
          long diff = e - s;
          if(!lengthSet) {
        	  sequenceLength = (int)diff;
        	  lengthSet = true;
          }
          if(Math.abs(diff) > 1){
            //long mid = (long)Math.floor(diff / 2.0);
            if(arr[5].equals("+")) {
            	seq = new KDEChromosome.Sequence(s, arr[5].equals("+"));
            } else {
            	seq = new KDEChromosome.Sequence(e, arr[5].equals("+"));
            }
            currentCuts.add(seq);
          }else{
            seq = new KDEChromosome.Sequence(s, arr[5].equals("+"));
            currentCuts.add(seq);
          }
        } catch (NumberFormatException e) {
          badFile(files[i]);
        }
      }
    }

    currentCuts = null;

    KDEChromosome[] chrs = new KDEChromosome[chrMap.size()];
    int i = 0;
    String[] chrnames = chrMap.keySet().toArray(new String[0]);
    for (String chr : chrnames) {
      currentCuts = chrMap.remove(chr);
      KDEChromosome.Sequence[] cuts = new KDEChromosome.Sequence[currentCuts.size()];
      for (int j = 0; j < cuts.length; ++j)
        cuts[j] = currentCuts.get(j);

      Arrays.sort(cuts);
      chrs[i++] = new KDEChromosome(chr, cuts, sequenceLength);
    }
    return chrs;
  }

  private static void badFile(File f) throws IOException {
    throw new IOException("Bad '.bed' format for file " + f.getAbsolutePath());
  }
}
