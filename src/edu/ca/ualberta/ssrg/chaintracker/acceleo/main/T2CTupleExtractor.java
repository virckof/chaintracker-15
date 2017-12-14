package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoConditional;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoLoop;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoNonOCLExpresion;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.TemplateConcept;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.T2CTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.CodeConcept;

/**
 * Creates T2CTuples from information from AcceleoTransformationParser
 * and TraceBackParser.
 * 
 * The source concept comes from the template, the target concept comes
 * from the generated code file.
 * 
 * @see AcceleoTransformationParser
 * @see TraceBackParser
 *
 */
public class T2CTupleExtractor {
	
	private ArrayList<T2CTuple> tuples;
	
	private AcceleoTransformationParser acceleoParser;
	
	private TraceBackParser traceParser;
	
	private String codeFile;
	
	private String templatePath;
	
	/**
	 * Create a T2CParser by passing in the TraceBackParser, AcceleoTransformationParser and the code file path.
	 * 
	 * @param traceParser - already called traceBacks() on it to init TraceBackParser's traceBacks 
	 * @param acceleoParser - already called parse() on it to init Acceleo expression info 
	 * @param codeFile
	 */
	public T2CTupleExtractor (TraceBackParser traceParser, AcceleoTransformationParser acceleoParser, String codeFile){
		tuples = new ArrayList<>();
		this.traceParser = traceParser;
		this.acceleoParser = acceleoParser;
		this.codeFile = codeFile;
		this.templatePath = acceleoParser.getTransformationFile();
	}
	
	/**
	 * Returns a list of T2CTuple from the tracebacks provided by the TraceBackParser given
	 * at creation.
	 * 
	 * @return
	 */
	public ArrayList<T2CTuple> extractTuples(){
		for (Entry<Integer, List<Integer>> entry : traceParser.getTracebacks().entrySet()) {
			
			Integer codeLine = entry.getKey();
			
			// Create a T2CTuple for each template line & code line pair
			for (Integer templateLine : entry.getValue()) {
				T2CTuple tuple = new T2CTuple();
				
				// Create source concept
				TemplateConcept source = new TemplateConcept();
				source.setFilePath(templatePath);
				source.setTemplateLine(templateLine.toString());
				source.setTemplateName(acceleoParser.getTemplateName(templateLine));
				source.setTemplateExpressionType(getExpressionType(templateLine));
				tuple.setSource(source);
				
				// Create target concept
				CodeConcept target = new CodeConcept();
				target.setFilePath(codeFile);
				target.setCodeLine(codeLine.toString());
				if (traceParser.getCodeFileLines().get(codeLine).matches("^\\s*$")) {
					target.setEmptyLine(true);
				}
				tuple.setTarget(target);
				
				tuples.add(tuple);
			}
		}
		
		return tuples;
	}
	
	/**
	 * Given a line number of the TEMPLATE returns the
	 * expression type.
	 * 
	 * @param lineNumber
	 * @return
	 */
	private String getExpressionType(int lineNumber) {
		String type = "";
		
		// Get loops, conditionals, simple expressions for given line
		List<AcceleoLoop> loops = acceleoParser.getLoops().get(lineNumber);
		List<AcceleoConditional> conds = acceleoParser.getConditionals().get(lineNumber);
		List<AcceleoNonOCLExpresion> simpleExpressions = acceleoParser.getSimpleExpressions().get(lineNumber);
		
		// If there are loops, conditionals or simple expressions for that line
		// , add that type to the type string
		if (loops != null && !loops.isEmpty()) {
			type += "Loop";
		}
		if (conds != null && !conds.isEmpty()) {
			if (!type.isEmpty()) type += ", ";
			type += "Conditional";
		}
		if (simpleExpressions != null && !simpleExpressions.isEmpty()) {
			if (!type.isEmpty()) type += ", ";
			type += "Simple";
		}
		
		return type;
	}
	
	public void printTuples(){
		for(T2CTuple t : tuples){
			System.out.println(t.toString());
		}
	}
}
