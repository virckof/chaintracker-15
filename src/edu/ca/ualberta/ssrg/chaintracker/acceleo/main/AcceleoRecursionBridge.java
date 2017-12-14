package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.util.Hashtable;

public class AcceleoRecursionBridge {
	
	private Hashtable<Integer, String> fileLines;
	private int lineNumber;
	
	private static AcceleoRecursionBridge instance = null;
   
	protected AcceleoRecursionBridge() {
	   fileLines = new Hashtable<>();
	   lineNumber = 0;
    }
    public static AcceleoRecursionBridge getInstance() {
       if(instance == null) {
          instance = new AcceleoRecursionBridge();
       }
       return instance;
    }
    
    public void putLine(int lineNumber, String line){
    	String a =fileLines.put(lineNumber, line);
    	if(a != null){
    		System.out.println("Collision");
    		System.out.println(line);
    	}
    }
   
	public Hashtable<Integer, String> getFileLines() {
		return fileLines;
	}
	public void setFileLines(Hashtable<Integer, String> fileLines) {
		this.fileLines = fileLines;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	 
}
