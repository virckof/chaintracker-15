package edu.ca.ualberta.ssrg.chaintracker.vis.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.ca.ualberta.ssrg.chaintracker.vis.Annotation;

/**
 * Read files line by line according to the input file standards
 * @author Victor
 *
 */
public class DataManager {
	
	public final static String ANNOTATIONS_FILE = "./exchange/annotations.ct";

	/**
	 * Returns a list of the lines contained in the file
	 * @param file
	 * @return
	 */
	public ArrayList<String> read(String file){	
		ArrayList<String> result = new ArrayList<>();
		try{
		  FileInputStream fstream = new FileInputStream(file);
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;
		  
		  //Read File Line By Line
		  while ((strLine = br.readLine()) != null)   {
			  result.add(strLine);
		  }
		  in.close();
		}catch (Exception e){
			 System.err.println("Error: " + e.getMessage());
		}
		return result;
	}
	
	public void saveAnnotations(ArrayList<Annotation> allAnnotations){
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ANNOTATIONS_FILE));
			out.writeObject(allAnnotations);
			out.flush();
			out.close();
			
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public ArrayList<Annotation> readAnnotaitons(){
		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(ANNOTATIONS_FILE));
			allAnnotations = (ArrayList<Annotation>) in.readObject();
			in.close();
			
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		
		return allAnnotations;
	}
}


