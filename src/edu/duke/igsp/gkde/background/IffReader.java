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
