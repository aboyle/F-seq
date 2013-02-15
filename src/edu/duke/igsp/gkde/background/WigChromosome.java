/*****************************************************************************
  WigChromosome.java

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

package edu.duke.igsp.gkde.background;
public class WigChromosome {
		 
	  private int _startPos;
	  private short[] _values;
	  private String _chromosome;
	  
	  public WigChromosome(String chromosome, short[] values, int startPos){
	    _chromosome = chromosome;
	    _values = values;
	    _startPos = startPos;
	  }
	  
	  public short[] getValues() {
		  	return _values;
	  }
	  
	  public String getChromosome(){
	    return _chromosome;
	  }
	  
	  public int getStart(){
	    return _startPos;
	  }
	  
	  public long getLength(){
		    return _values.length;
	  }
}
