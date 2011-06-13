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