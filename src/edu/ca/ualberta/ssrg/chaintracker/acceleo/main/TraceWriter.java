package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoConditional;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoExpression;

/**
 * TraceWriter annotates trace information in an Acceleo template file
 * using information from Acceleo Transformation Parser.
 * 
 * It writes trace information for when a model access was made
 * and when it was finished.
 * 
 * @see AcceleoTransformationParser
 *
 */
public class TraceWriter {
	public static final String GEN_OPEN = "//@gen < ";
	public static final String GEN_CLOSE = "// > @gen ";
	
	private static final Pattern frontSpacePattern = Pattern.compile("^[\\s]+");
	
	private BufferedWriter writer;
	private List<Integer> genCloseLineNumbers = new ArrayList<Integer>();
	private List<Integer> genCloseLineNumbersAfterCodeLine = new ArrayList<Integer>();
	private List<Integer> genCloseLineNumbersBeforeCodeLine = new ArrayList<Integer>();
	private List<Integer> genOpenLineNumbers = new ArrayList<Integer>();
	
	private boolean debugMode = false;
	private List<String> debugFileLines = new ArrayList<String>();
	
	private static final String CREATE_OUTPUT_FILE_ERROR = "Could not open file for writing traces";
	private static final String PARSER_NOT_INIT_ERROR = "Could not get file lines from AcceleoTransformationParser";
	private static final String WRITE_OUTPUT_FILE_ERROR = "Error writing traces to output file";

	/** Goes through all the file lines in the AcceleoTransformationParser and
	 * prints out a "trace" (line info, model info, etc) for any loops, conditionals, 
	 * simple expressions that occur on that line by printing the 
	 * GEN_OPEN string with the trace, before printing the file line to the output file.
	 * 
	 * @param atp - AcceleoTransformationParser that has already initialized & parsed
	 * @param fileName - file to output commented file to
	 */
	public void writeTraces(AcceleoTransformationParser atp, String fileName) throws TraceWriterException {
		// Create a file from the provided filename and open it for writing
		initFileWriter(fileName);

		// Gets the file lines from the AceeleoTransformationParser
		AcceleoRecursionBridge fileData = atp.getFileData();
		if (fileData == null) {
			throw new TraceWriterException(PARSER_NOT_INIT_ERROR);
		}
		Hashtable<Integer, String> file = fileData.getFileLines();
		if (file == null) {
			throw new TraceWriterException(PARSER_NOT_INIT_ERROR);
		}

		// Iterate through each file line
		int size = file.size();
		try {
			for (int i = 1; i < size + 1; i ++) {
				
				// Get get file line
				String templateLine = file.get(i);
				// Get space in front of the line
				String spacer =  getFrontEndSpace(templateLine);
				
				// From the conditionals, loops and expressions from the AcceleoTransformationParser
				// get a string off all the model attribute dependencies
				// The first string is the model dependency to be printed before the code line
				// 2nd string is the model dependency to be printed after the code line
				List<String> modelAttributeDependency = getModelAttributeDependency(atp, i);
				if (modelAttributeDependency != null && !(modelAttributeDependency.get(0)).isEmpty()) {
					writeLine(GEN_OPEN + i + modelAttributeDependency.get(0), spacer);
				}
				
				// if this line is contained in the list of lines that
				// should be closed before the code line, close it
				handleCloseGen(genCloseLineNumbersBeforeCodeLine, i, spacer);
				
				// write the actual line from the template
				writeLine(templateLine, "");
				
				// Write 2nd model dependency string
				if (modelAttributeDependency != null && !(modelAttributeDependency.get(1)).isEmpty()) {
					writeLine(GEN_OPEN + i + modelAttributeDependency.get(1), spacer);
				}
				
				// if this line is contained in the list of lines that
				// should be closed after the code line, close it
				handleCloseGen(genCloseLineNumbersAfterCodeLine, i, spacer);

			}
		} catch (IOException e) {
			e.printStackTrace();
			closeWriter();
			throw new TraceWriterException(WRITE_OUTPUT_FILE_ERROR);
		}
		
		closeWriter();
	}
	
	/**
	 * Checks if the current line number is in the given list of closing line numbers,
	 * then prints a gen close statement and removes the opening line number if applicable
	 * (in case of expressions with bodies like conditionals and loops)
	 * 
	 * @param closeLineNumbers
	 * @param lineNumber
	 * @param spacer
	 * @throws IOException
	 */
	private void handleCloseGen(List<Integer> closeLineNumbersFiltered, int lineNumber, String spacer) throws IOException {
		while (closeLineNumbersFiltered.contains(lineNumber)) {
			
			int index = genCloseLineNumbers.indexOf((Integer)lineNumber);
			
			Integer open_i = genOpenLineNumbers.get(index);
			if (open_i != null) {
				writeLine(GEN_CLOSE + open_i, spacer);
				genOpenLineNumbers.remove(index);
			} else {
				// Should not occur
			}
			
			closeLineNumbersFiltered.remove((Integer)lineNumber);
			genCloseLineNumbers.remove((Integer)lineNumber);
		}
	}
	
	/** Gets all the acceleo expressions from the AcceleoTransformationParser
	 * at the given line number, and returns a string with all the acceleo expressions
	 * (ie. a model attribute dependency string that will be used to print the trace).
	 * 
	 * First string is the model dependency string to be printed before the code line.
	 * Second string is the model dependency string to be printed after the code line.
	 * 
	 * AcceleoLoop, AcceleoConditional, AcceleoNonOCLExpression all inherit from
	 * AcceleoExpression which has an abstract method "getExpressionString"
	 * 
	 * @param atp
	 * @param lineNumber
	 * @return
	 */
	private List<String> getModelAttributeDependency(AcceleoTransformationParser atp, int lineNumber) {
		List<String> moduleAttributeDependency = new ArrayList<String>();
		String openGenBeforeCodeLineModel = ""; // model dependency to be written before code line
		String openGenAfterCodeLineModel = ""; // model dependency to be written after code line
		
		// Get all Acceleo expressions from the AcceleoTransformationParser
		// (loops, conditionals, simple expressions), and add their expression
		// string to the module attribute string
		// If applicable, adds their closing line to the list of lines that need to close
		List<AcceleoExpression> expressions = atp.getAllExpressions(lineNumber);
		
		boolean closeGenForSimple = false; 
		// TODO: this loop is only expecting to have : (1) loop, (1) conditional OR (1+) simple expressions
		// right now cannot handle for instance conditional & simple expressions on same line => TransformationParser
		// needs to first be able to handle this, and then TraceWriter can be updated
		for (AcceleoExpression exp : expressions) {
			boolean writeGenInsideCodeBlock = exp instanceof AcceleoConditional;
				
			if (writeGenInsideCodeBlock) {
				openGenAfterCodeLineModel += " " + exp.getExpressionString();
			} else {
				openGenBeforeCodeLineModel += " " + exp.getExpressionString();
			}
			
			// add the expressions closing line to the list of closing 
			// lines so we know to close it when we reach that line
			if (exp.getEndLine() != null) {
				if (exp.getEndLine() == lineNumber) { // case for simple expression since no body
					closeGenForSimple = true;
				} else {
					// add the line number to open lines so we can identify
					// which opening line the closing lines go with
					genOpenLineNumbers.add(lineNumber);
					genCloseLineNumbers.add(exp.getEndLine());
					if (writeGenInsideCodeBlock) {
						genCloseLineNumbersBeforeCodeLine.add(exp.getEndLine());
					} else {
						genCloseLineNumbersAfterCodeLine.add(exp.getEndLine());
					}
				}
			}
		}
		
		// add the closing for this line if encountered simple expression
		if (closeGenForSimple) {
			genOpenLineNumbers.add(lineNumber);
			genCloseLineNumbers.add(lineNumber);
			genCloseLineNumbersAfterCodeLine.add(lineNumber);
		}
		
		moduleAttributeDependency.add(openGenBeforeCodeLineModel);
		moduleAttributeDependency.add(openGenAfterCodeLineModel);
		return moduleAttributeDependency; 
	}
	
	/** Closes the BufferedWriter used to write to
	 * the trace output file.
	 * 
	 */
	private void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Given a file name, initializes the BufferedWriter 
	 *  to write to the file. 
	 * 
	 * @param fileName
	 * @throws TraceWriterException 
	 */
	private void initFileWriter(String fileName) throws TraceWriterException {
		File myFile = new File(fileName);
		myFile.getParentFile().mkdirs();
		try {
			writer = new BufferedWriter(new FileWriter(myFile));
		} catch (IOException e) {
			e.printStackTrace();
			throw new TraceWriterException(CREATE_OUTPUT_FILE_ERROR + ": " + fileName);
		}
	}

	/** Prints a line to a file with with an optional spacer to append to front of line.
	 *  If debugMode = true, it will add file lines to a list for easier testing.
	 *  
	 * @param line - string to be written to file
	 * @param spacer - string to appended to the line before written
	 */
	private void writeLine(String line, String spacer) throws IOException {
		if (line == null) return;

		if (writer == null) {
			System.err.println("Do not call this method directly. Use method 'parse' which will initialize the writer based on given output file.");
			return;
		}

		if (spacer == null) spacer = "";
		line = spacer + line;

		if (debugMode) {
			//System.out.println(line);
			debugFileLines.add(line);
		}

		writer.write(line + "\n");
	}

	/** Given a line, returns the leading whitespace of that line.
	 * 
	 *  Uses Regex expression to find the leading whitespace.
	 *  
	 * @param line
	 */
	public String getFrontEndSpace(String line) {
		Matcher m = frontSpacePattern.matcher(line);

		String spacer =  "";
		if (m != null && m.find()) {
			spacer = m.group(0);
		}

		return spacer;
	}

	/** Sets debug mode. With debugMode = true, the TraceWriter will 
	 *  store the file lines into a list for easier testing.
	 *  
	 * @param debugMode 
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	public List<String> getFileLines() {
		return this.debugFileLines;
	}
}
