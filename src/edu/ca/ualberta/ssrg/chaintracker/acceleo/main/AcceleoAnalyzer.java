package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.ca.ualberta.ssrg.chaintracker.vos.M2TTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.T2CTuple;

/**
 * Acceleo Analyzer is used to generate M2T and T2C tuples.
 * 
 * It takes in an Ecore metamodel file, Acceleo template file, and
 * a (for T2C tuples) model instance. 
 *
 */
public class AcceleoAnalyzer {
	private static final String M2T_ANALYSIS = "M2T";
	private static final String T2C_ANALYSIS = "T2C";
	
	// paths for generated templates/code
	private static final String ANNOTATED_TEMPLATE_PATH = "gen/annotatedTemplates/"; 
	private static final String ANNOTATED_CODE_PATH = "gen/annotatedCode/";
	private static final String CODE_PATH = "gen/code/"; 
	
	/**
	 * Given a metamodel and template path, returns a list of M2TTransformation objects.
	 * 
	 * Does this by:
	 * 1. using the AcceleoTrasformationParser to parse information about 
	 * loops, conditionals, and simple expressions in the provided template.
	 * 2. then using the M2TTransformationBuilder to translate the parsed
	 * information into M2TTransformation objects
	 * 
	 * @param inMetamodelPath
	 * @param templatePath
	 * @return
	 */
	public ArrayList<M2TTransformation> analyzeModelToTemplate(String inMetamodelPath, String templatePath) {
		// Parse the .mtl file for Acceleo expression information
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		try {
			atp.initialize(inMetamodelPath, templatePath);
		} catch (TransformationParserException e2) {
			e2.printStackTrace();
			return null;
		}
		atp.parse();
		
		// Create M2TTransformation objects from the parser's information
		M2TTransformationBuilder builder = new M2TTransformationBuilder();
		ArrayList<M2TTransformation> transformations = new ArrayList <M2TTransformation>(builder.getM2TTransformations(atp));
		
		for (M2TTransformation t : transformations) {
			System.out.println(t.toReadableString());
		}
		
		return transformations;
	}
	
	/**
	 * Given a meta model path, model path and template path, 
	 * generates the code with and without annotations.
	 * The annotated code is than used to return a list of T2CTuples.
	 *  
	 * Does this by:
	 * 1. using the AcceleoTrasformationParser to parse information about 
	 * loops, conditionals, and simple expressions in the provided template.
	 * 2. using the TraceWriter to write an annotated template file 
	 * by adding trace information from the AcceleoTransformationParser
	 * (template line number, model dependency, etc)
	 * 3. using the AcceleoLauncher to generate the code with and
	 * without anotations
	 * 4. using the TraceBackParser to trace back the annotated
	 * generated code to link code lines with template lines
	 * 5. using the T2CTupleExtractor to translate the information
	 * from the TraceBackParser into T2CTuples
	 * 
	 * @param inMetamodelPath
	 * @param inModelPath
	 * @param templatePath
	 * @return
	 */
	public ArrayList<T2CTuple> analyzeTemplateToCode(String inMetamodelPath, String inModelPath, String templatePath) {
		// Parse the .mtl file for Acceleo expression information
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		try {
			atp.initialize(inMetamodelPath, templatePath);
		} catch (TransformationParserException e2) {
			e2.printStackTrace();
			return null;
		}
		atp.parse();
				
		// Create an annoated MTL file that has information about
		// what template line and models are associated with each Acceleo expression
		String outputAnnotatedMtlFile = ANNOTATED_TEMPLATE_PATH + stripFilePath(templatePath);
		try {
			createAnnotatedMTLFile(atp, outputAnnotatedMtlFile);
		} catch (TraceWriterException e1) {
			e1.printStackTrace();
			return null;
		}
		
		// Create a GeneratedFilesWatcher in order to determine what (annotated) files are generated 
		GeneratedFilesWatcher w = new GeneratedFilesWatcher(ANNOTATED_CODE_PATH);
		
		// Generate the code with both original and annotated templates
		try {
			generateCode(templatePath, inModelPath, inMetamodelPath, CODE_PATH);
			generateCode(outputAnnotatedMtlFile, inModelPath, inMetamodelPath, ANNOTATED_CODE_PATH);
		} catch (AcceleoLauncherException e) {
			e.printStackTrace();
			return null;
		}
		
		// Get the list of generated annotated code files from the watcher
		List<String> genFiles = w.getGeneratedFiles();
		
		// Traceback the generated annotated code files to determine which template lines
		// each line of code is generated from and return a list of T2C tuples
		try {
			return new ArrayList<T2CTuple>(tracebackAnnotatedCode(atp, genFiles));
		} catch (TraceBackParserException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Create an annoated MTL file that has information about
	 * what template line and models are associated with each Acceleo expression 
	 * 
	 * @param atp
	 * @param outputAnnotatedMtlFile
	 * @throws TraceWriterException
	 */
	private static void createAnnotatedMTLFile(AcceleoTransformationParser atp, String outputAnnotatedMtlFile) throws TraceWriterException {	
		TraceWriter writer = new TraceWriter();
		writer.writeTraces(atp, outputAnnotatedMtlFile);
	}
	
	/**
	 * Generate code based on the metamodel, model and template
	 * 
	 * @param outputAnnotatedMtlFile
	 * @param modelFile
	 * @param ecoreFile
	 * @param moduleURI
	 * @param genCodeDir
	 * @throws AcceleoLauncherException
	 */
	private static void generateCode(String outputAnnotatedMtlFile, String modelFile, String ecoreFile, String genCodeDir) throws AcceleoLauncherException {
		AcceleoLauncher acceleoLauncher = new AcceleoLauncher();
		acceleoLauncher.launch(ecoreFile, modelFile, outputAnnotatedMtlFile, genCodeDir);
	}
	
	/**
	 * Traceback the given list of generated code files and determine
	 * what lines from the template each line of code depends on
	 * 
	 * @param atp 
	 * @param codeFiles
	 * @throws TraceBackParserException
	 */
	private List<T2CTuple> tracebackAnnotatedCode(AcceleoTransformationParser atp, List<String> codeFiles) throws TraceBackParserException {
		TraceBackParser parser = new TraceBackParser();
		
		List<T2CTuple> tuples = new ArrayList<T2CTuple>();
		
		File annotatedCodeDir = new File(ANNOTATED_CODE_PATH);
		File codeDir = new File(CODE_PATH);
		
		for (String file : codeFiles) {
			// Parse for trace backs
			parser.traceBack(file);
			
			System.out.println(file + " -------------------");
			//parser.printTracebacks();
			
			// Get the file name of not annotated code file
			// as TraceBackParser will give tracebacks between the 
			// original (not annotated) template file and the generated
			// not annotated code file to be stored in T2CTuple
			String codeFile = file.replaceAll(Pattern.quote(annotatedCodeDir.getPath()), "");
			codeFile = codeDir.getAbsolutePath() + codeFile;
			
			// Get the T2C tuples
			T2CTupleExtractor tupleExtractor = new T2CTupleExtractor(parser, atp, codeFile);
			tuples.addAll(tupleExtractor.extractTuples());
			//tupleExtractor.printTuples();
		}
		
		return tuples;
	}
	
	/**
	 * From a string representing file path, returns
	 * the last part, ie. just the file's name.
	 * 
	 * @param fileName
	 * @return
	 */
	private String stripFilePath(String fileName) {
		File f = new File(fileName);
		String name = f.getName();
		
		return name;
	}
	
	//*****************************************************************************
	// Main
	//*****************************************************************************
	/**
	 * Analyzer does the following:
	 * 1. Parse a .mtl file to analyze the loops, conditionals, simple Acceleo expressions
	 * using AcceleoTransformationParser
	 * 
	 * Then depending on the analysis type:
	 * 
	 * M2T Analysis:
	 * 2. Uses M2TTransformationBuilder to translate the information from
	 * the parser into M2TTransformation objects 
	 * 
	 * T2C Analysis:
	 * 2. Uses TraceWriter to write traces in the .mtl for Acceleo expressions
	 * that links the template line with the model dependencies
	 * 3. Uses AcceleoLauncher to generate code
	 * 4. Uses TraceBackParser to use the traces from TraceWriter
	 * to link the generated code lines to the template lines that generated them 
	 * 
	 * The main function takes 4 arguments:
	 * 	1. metamodel file (.ecore)
	 *  2. model file (.xmi)
	 *  3. template file (.mtl)
	 *  4. type of analysis("M2T" or "T2C", optional - default is M2T)
	 * 
	 * @param args
	 */
	public static void main(String... args){
		
		if (args.length < 4) {
			System.err.println("4 arguments required: "
					+ "\n\t1. metamodel file (.ecore)"
					+ "\n\t2. model file (.xmi)"
					+ "\n\t3. template file (.mtl)"
					+ "\n\t4. type of analysis to perform (M2T or T2C); if no type provided, defaults to M2T");
			return;
		}
		
		// Get arguments
		String inMetamodelPath = args[0];
		String inModelPath = args[1];
		String templatePath = args[2]; 
		String analysisType = (args.length > 3) ? args[3] : M2T_ANALYSIS;
		AcceleoAnalyzer analyzer = new AcceleoAnalyzer();
		
		if (analysisType.equalsIgnoreCase(T2C_ANALYSIS)) {
			analyzer.analyzeTemplateToCode(inMetamodelPath, inModelPath, templatePath);
		} else {
			analyzer.analyzeModelToTemplate(inMetamodelPath, templatePath);
		}
	}
	
}
