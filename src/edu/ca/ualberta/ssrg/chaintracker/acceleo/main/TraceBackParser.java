package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TraceBackParser traces an annotated generated code file back to the
 * template that generated it. For each line in the code file,
 * provides a list of template lines that the code line was dependent
 * on for generation.
 * 
 * The annotated generated code file is a file annotated by
 * TraceWriter.
 * 
 * @see TraceWriter
 *
 */
public class TraceBackParser {
	// store table of code lines (of the non annotated file)
	// to the template lines that generated them
	private Hashtable<Integer, List<Integer>> tracebacks;
	
	// store code lines that are not gen trace comments
	private ArrayList<String> codeFileLines;
	
	private BufferedReader reader;
	private boolean insideGeneratedCode = false;
	private List<Integer> openGenerationCode = new ArrayList<Integer>();
	private int numberOfGenComments = 0;

	// Error messages
	private static final String READ_FILE_ERROR = "Could not open generated code file for parsing";
	private static final String PARSE_LINE_ERROR = "Error parsing line in generated code";
	
	/**
	 * Given a file that has been generated with comments written by TraceWriter
	 * parses it to determine the lines in the template that the 
	 * generated code depends on
	 * 
	 * @param fileName - .mtl template file with comments written by TraceWriter
	 * @throws TraceBackParserException
	 */
	public void traceBack(String fileName) throws TraceBackParserException {
		resetParser();
		
		// Open the template file for reading 
		FileInputStream fs;
		try {
			fs = new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(fs));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new TraceBackParserException(READ_FILE_ERROR);
		}
		
		int lineNumber = 1;
		try {
			String line;
			line = reader.readLine();
			
			// Read each line in the template file
			while (line != null) {
				if (line.contains(TraceWriter.GEN_OPEN)) {
					numberOfGenComments++; // increment # of gen comments we've encountered
					
					// If line contains a TraceWriter generation open comment, add the line
					// to the current open blocks
					insideGeneratedCode = true;
					Integer templateLine = parseGenLine(line);
					openGenerationCode.add(templateLine);
				} else if (line.contains(TraceWriter.GEN_CLOSE)) {
					numberOfGenComments++; // increment # of gen comments we've encountered
					
					// If line contains a TraceWriter generation close comment,
					// remove the most recent line from the current open blocks
					openGenerationCode.remove(openGenerationCode.size() - 1);
					if (openGenerationCode.size() == 0) {
						// if no more open blocks, we begin parsing coding that is
						// NOT generated code
						insideGeneratedCode = false;
					}
				} else {
					// add non-gen comment line to code file lines
					codeFileLines.add(line);
					
					if (insideGeneratedCode) {
						// If generated code, add the list of currently open geneartion blocks
						// to this line of code's trace back
						
						// Get the code line number in a file that has no gen comments
						int codeLineNum = getCodeLineNumber(lineNumber);
						tracebacks.put(codeLineNum, new ArrayList<Integer>(openGenerationCode));
					}
				}
				
				// read next line
				line = reader.readLine();
				lineNumber ++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			closeReader();
			throw new TraceBackParserException(PARSE_LINE_ERROR + ": line " + lineNumber);
		}
		
		closeReader();
	}
	
	private void resetParser() {
		tracebacks = new Hashtable<Integer, List<Integer>>();
		codeFileLines = new ArrayList<String>(); 
		insideGeneratedCode = false;
		openGenerationCode = new ArrayList<Integer>();
		numberOfGenComments = 0;
		// Since files start at line 1, add a null for index 0 of codeFileLines array
		codeFileLines.add(null);
	}
	
	/**
	 * Closes the current file reader.
	 */
	private void closeReader() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a generation open line written by TraceWriter
	 * parses out the template # in the line
	 * 
	 * @param genOpenComment
	 * @return
	 */
	public Integer parseGenLine(String genOpenComment) {
		Pattern p = Pattern.compile(TraceWriter.GEN_OPEN + "(\\d+)");
		Matcher m = p.matcher(genOpenComment);
		if (m != null && m.find()) {
			String num = m.group(1);
			return Integer.parseInt(num);
		}
		
		return null;
	}
	
	/**
	 * Given the code line number in the "polluted" file
	 * (file with gen comments) - returns the code line
	 * it would be in a file with NO gen comments
	 * 
	 * @param pollutedCodeLine
	 * @return
	 */
	private int getCodeLineNumber(int pollutedCodeLine) {
		return pollutedCodeLine - numberOfGenComments;
	}
	
	public Hashtable<Integer, List<Integer>> getTracebacks() {
		return tracebacks;
	}

	public ArrayList<String> getCodeFileLines() {
		return codeFileLines;
	}

	//********************************************************************
	// Pretty Printers
	//********************************************************************

	public void printTracebacks() {
		for (Entry<Integer, List<Integer>> entry : getTracebacks().entrySet()) {
			System.out.println("Code line " + entry.getKey() + " depends on:");
			String templateLines = null;
			for (Integer i : entry.getValue()) {
				if (templateLines == null) {
					templateLines = "";
				} else {
					templateLines += ", ";
				}
				
				templateLines += i;
			}
			System.out.println("\t Template Lines:" + templateLines);
		}
	}
}
