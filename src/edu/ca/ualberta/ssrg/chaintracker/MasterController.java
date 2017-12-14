package edu.ca.ualberta.ssrg.chaintracker;

import java.util.ArrayList;
import java.util.List;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoAnalyzer;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.M2TTupleExtractor;
import edu.ca.ualberta.ssrg.chaintracker.atl.main.ATLTransformationParser;
import edu.ca.ualberta.ssrg.chaintracker.atl.main.M2MTupleExtractor;
import edu.ca.ualberta.ssrg.chaintracker.ecore.EcoreSolver;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2MTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2TTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelForVisualizationConvertor;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.TuplePrinter;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.M2MTransformationTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.M2TTransformationTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.T2CTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.StandardTupleConvertor;

/**
 * Master Controller for the execution of the ATLParser on model-to-model transformation chains
 * and the AcceleoAnalyzer on model-to-template and template-to-code transformations.
 *
 * @author Victor
 */
public class MasterController {

	/**
	 * Input objects
	 */
	private List<MasterControllerInput> inputs;

	/**
	 * ATL parser produces a collection of Transformation given an ATL file
	 */
	private ATLTransformationParser ATLParser;

	/**
	 * AcceleoAnalyzer produces tuples for M2T (model to template) & T2C (template to code) transformations.
	 */
	private AcceleoAnalyzer AcceleoParser;

	/**
	 * ECORE Solver for implicit reference checking
	 */
	private EcoreSolver ECORESolver;

	/**
	 * Tuple formatter and pretty printer
	 */
	private TuplePrinter printer;

	/**
	 * Creates standard tuples for all types (M2M, M2T, T2C)
	 */
	private StandardTupleConvertor standardTupleConvertor;

	public MasterController(){
		inputs = new ArrayList<>();
	}

	public void execute(){

		// Initialize
		ATLParser = new ATLTransformationParser();
		AcceleoParser = new AcceleoAnalyzer();
		ECORESolver = new EcoreSolver();
		printer = new TuplePrinter();
		standardTupleConvertor = new StandardTupleConvertor();

		// Process each input independently
		for (MasterControllerInput input: inputs) {
			System.out.println("---------------------------------------------");
			System.out.println(input.toString());
			System.out.println("---------------------------------------------");

			if (input instanceof M2MInput) {
				M2MInput mInput = (M2MInput) input;
				traceATL(mInput.getTransformationFile(), mInput.getSourceFile(), mInput.getTargetFile());
			} else if (input instanceof M2TInput) {
				M2TInput mInput = (M2TInput) input;
				traceAcceleo(mInput.getTemplateFile(), mInput.getMetamodelFile(), mInput.getModelInstanceFile());
			}
		}

		// Create models & traces files
		try{
			printer.printModelsToVisualize();
	        printer.printTracesToVisualize();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Controls parsing of a transformation and controls the id for relationship printing in chains
	 * @param transformationFile
	 * @param sourceModelFile
	 * @param targetModelFile
	 */
	public void traceATL(String transformationFile, String sourceModelFile, String targetModelFile){

		// Setting Input files and initializing the internal structures of the parser and the solver
		ATLParser.setTransformationFile(transformationFile);
		ATLParser.initialize();

		ECORESolver.setModelFile(sourceModelFile);
		ECORESolver.initialize();

		// Processing transformation rules and extracting transformation tuples
		ArrayList<M2MTransformation> parsedTransformations = ATLParser.processMatchedRules();
		ATLParser.printTransformations(parsedTransformations);

		M2MTupleExtractor extractor  = new M2MTupleExtractor(parsedTransformations, ATLParser, ECORESolver, transformationFile);
		ArrayList<M2MTransformationTuple> tuples = extractor.extractTuples();

		// Convert the extracted M2MTransformationTuples into StandardTuples and initialize the printer
		printer.initialize(standardTupleConvertor.getStandardTuplesForM2M(tuples));

		// Initializing source and target model index.
		// Based on the source Ecore file
		ArrayList<ModelElement> sourcemodel = ECORESolver.getModelElements(sourceModelFile);
		printer.createModelIDs(ModelForVisualizationConvertor.getModelsFromModelElement(sourcemodel));
		// Based on the target Ecore file
		ArrayList<ModelElement> targetmodel = ECORESolver.getModelElements(targetModelFile);
		printer.createModelIDs(ModelForVisualizationConvertor.getModelsFromModelElement(targetmodel));

		// Create traces with printer
		printer.createTracesIDs();
	}

	/**
	 * Create models & ids for M2M and T2C.
	 * @param transformationFile
	 * @param sourceModelFile
	 * @param sourceModelInstanceFile
	 */
	public void traceAcceleo(String transformationFile, String sourceModelFile, String sourceModelInstanceFile){

		// Setting Input files and initializing the internal structures of the parser and the solver
		ArrayList<M2TTransformation> parsedTemplates = AcceleoParser.analyzeModelToTemplate(sourceModelFile, transformationFile);
		ECORESolver.setModelFile(sourceModelFile);
		ECORESolver.initialize();

		// Get M2TTuples
		M2TTupleExtractor extractor = new M2TTupleExtractor(parsedTemplates, transformationFile, ECORESolver);
		ArrayList<M2TTransformationTuple> tuples = extractor.extractTuples();

		// Get the model elments
		ArrayList<ModelElement> sourcemodel = ECORESolver.getModelElements(sourceModelFile);

		// Convert the extracted M2TTransformationTuple into StandardTuples and initialize the printer
		printer.initialize(standardTupleConvertor.getStandardTuplesForM2T(tuples));

		//Initializing source and target model index.
		// Based on the Ecore file
		printer.createModelIDs(ModelForVisualizationConvertor.getModelsFromModelElement(sourcemodel));
		// Based on the template file
		printer.createModelIDs(ModelForVisualizationConvertor.getModelsFromM2TTuples(tuples));

		// Create traces with printer
		printer.createTracesIDs();

		// If model instance file provided, continue on to trace the Acceleo generated code
		if (sourceModelInstanceFile != null) {
			traceAcceleoCode(transformationFile, sourceModelFile, sourceModelInstanceFile);
		}
	}

	public void traceAcceleoCode(String transformationFile, String sourceModelFile, String sourceModelInstanceFile) {
		// Setting Input files and initializing the internal structures of the parser
		ArrayList<T2CTuple> parseT2CTuples = AcceleoParser.analyzeTemplateToCode(sourceModelFile, sourceModelInstanceFile, transformationFile);

		// Convert the extracted T2CTuple into StandardTuples and initialize the printer
		printer.initialize(standardTupleConvertor.getStandardTuplesForT2C(parseT2CTuples));

		//Initializing source and target model index.
		// Based on the template file & code file
		printer.createModelIDs(ModelForVisualizationConvertor.getModelsFromT2CTuples(parseT2CTuples));

		// Create traces with printer
		printer.createTracesIDs();
	}

	public List<MasterControllerInput> getInputs() {
		return inputs;
	}

	public void setInputs(List<MasterControllerInput> inputs) {
		this.inputs = inputs;
	}

	//For testing purposes only
	public static void main(String ... args){

		MasterController mc = new MasterController();
		ArrayList<MasterControllerInput> inputs = new ArrayList<MasterControllerInput>();
	}

}
