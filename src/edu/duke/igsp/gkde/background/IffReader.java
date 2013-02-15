/*****************************************************************************
  IffReader.java

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;

import edu.duke.igsp.gkde.background.WigChromosome;

public class IffReader {
	public static WigChromosome read(File file) throws IOException {
		
		FileInputStream fin = new FileInputStream(file);
		DataInputStream di = new DataInputStream(fin);
		char[] chromosome = new char[10];
		char temp;

		//Read in chromosome name (before !)
		temp = di.readChar();
		int cursor = 0;
		while(temp != '!') {
			chromosome[cursor] = temp;
			temp = di.readChar();
			cursor++;
		}
		
		//System.out.println(String.valueOf(chromosome));
		int startPos = di.readInt();
		//System.out.println(startPos);
		int length = di.readInt();
		//System.out.println(length);
		
		//BufferedReader br = new BufferedReader(new InputStreamReader(di));
		short[] temp_data = new short[length];
		
		short line;
		cursor = 0;
		for(int k = 0; k < length; ++k) {
			line = di.readShort();
//		while ((line = di.readShort()) != -1) {
//			int hiNibble = line >> 4;
//			int loNibble = line & 0x0f;
//			temp_data[cursor*2] = (short)hiNibble;
//			temp_data[cursor*2+1] = (short)loNibble;
			temp_data[cursor] = line;
			cursor++;
			//System.out.println(hiNibble + " " + loNibble);
		}
		//System.out.println(cursor);
		return new WigChromosome(String.valueOf(chromosome), temp_data, startPos);
	} 

}
