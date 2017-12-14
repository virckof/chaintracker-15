package edu.ca.ualberta.ssrg.chaintracker.vos.printer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.IndirectSourceConcept;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.M2MTransformationTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.StandardTuple;

/**
 * TuplePrinter, given a list of tuples, creates ModelForVisualization
 * and TracesForVisualization which can be used to display
 * traces between models in the GUI.
 *
 */
public class TuplePrinter {

	public final static String DATA_EXCHANGE_MODELS = "./exchange/models.ct";
	public final static String DATA_EXCHANGE_TRACES = "./exchange/traces.ct";
	
	private Hashtable<String, ArrayList<StandardTuple>> sourceElementTable;
	
	private Hashtable<String, ArrayList<StandardTuple>> targetElementTable;
	
	private Hashtable<String, ModelForVisualization> models;
	
	private ArrayList<TraceForVisualization> traces;
	
	private int nextAssignedModelId = 0;
	
	private int nextAssignedTraceId = 0;
	
	public TuplePrinter (){
		models = new Hashtable<String, ModelForVisualization>();
		traces = new ArrayList<TraceForVisualization>();
		sourceElementTable = new Hashtable<>();
		targetElementTable = new Hashtable<>();
	}
	
	/**
	 * For each StandardTuple, create a source element and a target element.
	 * @param tuples
	 */
	public void initialize(ArrayList<StandardTuple> tuples) {
		sourceElementTable = new Hashtable<>();
		targetElementTable = new Hashtable<>();
		for(StandardTuple t : tuples){
			updateElementTable(sourceElementTable, t, t.sourceName);
			updateElementTable(targetElementTable, t, t.targetName);
		}
	}
	
	private void updateElementTable(Hashtable<String, ArrayList<StandardTuple>> table, StandardTuple tuple, String key)
	{
		ArrayList<StandardTuple> tuplesTemp;
		if(!table.containsKey(key)){
			tuplesTemp = new ArrayList<>();
		}
		else{
			tuplesTemp = table.get(key);
		}	
		
		tuplesTemp.add(tuple);
		table.put(key, tuplesTemp);
	}
	
	/**
	 * Creates numeric ids for all the model elements
	 * @param givenModels
	 */
	public void createModelIDs(List<ModelForVisualization> givenModels){
		for(ModelForVisualization model : givenModels){
			// If already exists, skip
			if (models.containsKey(model.getModel())) continue;
			
			model.setModel(model.getModel());
			model.setModelID(nextAssignedModelId);
			nextAssignedModelId++;
			
			models.put(model.getModel(), model);
			
		}
	}
	
	/**
	 * Creates numeric ids for all traces
	 */
	public void createTracesIDs(){
		for(ArrayList<StandardTuple> tuples : sourceElementTable.values()) {
		
			for(int i=0; i< tuples.size(); i++){
				StandardTuple tuple = tuples.get(i);

				String sourceUniqueID = tuple.sourceID;
				String targetUniqueID = tuple.targetID;
//				System.out.println("Source: " + sourceUniqueID);
//				System.out.println("Target: " + targetUniqueID);

				int numericSourceID= 0;
				int numericTargetID= 0;
				boolean failed = false;
				
				try{
					numericSourceID = models.get(sourceUniqueID).getModelID();
					numericTargetID = models.get(targetUniqueID).getModelID();
				}
				catch(Exception e){
					failed = true;
					System.out.println("Source: " + sourceUniqueID);
					System.out.println("Target: " + targetUniqueID);
					System.out.println("Trace call not supported");
				}
				
				if(!failed){
					TraceForVisualization trace = new TraceForVisualization();
					trace.setTraceId(nextAssignedTraceId);
					trace.setTraceType(tuple.getTupleType());
					trace.setFile(tuple.file);
					trace.setValue(tuple.name.replace(" ", ""));
					trace.setExplicitReference(tuple.explicitReference);
					trace.setImplicitReferenceName(tuple.implicitReferenceName);
					trace.setSourceId(numericSourceID);
					trace.setTargetId(numericTargetID);
					traces.add(trace);
				
					nextAssignedTraceId++;
				}
			}
		}
	}
	
	//********************************************************************
	// Printers
	//********************************************************************
	
	/**
	 * Prints models to modesl.ct
	 * @throws Exception
	 */
	public void printModelsToVisualize() throws Exception{
		
		PrintWriter writer = new PrintWriter(DATA_EXCHANGE_MODELS, "UTF-8");
		
		ModelForVisualization[] modelList = models.values().toArray(new ModelForVisualization[models.size()]);
		Arrays.sort(modelList);
		boolean firstModel = true;
		for (ModelForVisualization m : modelList) {
			if (!firstModel) {
				writer.print(System.lineSeparator());
			} else {
				firstModel = false;
			}
			writer.print(m.toString());
		}
		
		writer.close();
	}
	
	/**
	 * Prints traces to traces.ct
	 * @throws Exception
	 */
	public void printTracesToVisualize() throws Exception{
		
		PrintWriter writer = new PrintWriter(DATA_EXCHANGE_TRACES, "UTF-8");
		boolean firstTrace = true;
		for(TraceForVisualization trace : traces) {
			if (!firstTrace) {
				writer.print(System.lineSeparator());
			} else {
				firstTrace = false;
			}
			writer.print(trace.toString());
		}
		writer.close();
	
	}
	
	/**
	 * Prints models (models.ct) to console for testing purposes
	 * @throws Exception
	 */
	public void printModelsToConsole() throws Exception{
		
		ModelForVisualization[] modelList = models.values().toArray(new ModelForVisualization[models.size()]);
		Arrays.sort(modelList);
		for (ModelForVisualization m : modelList) {
			System.out.println(m.toString());
		}

	}

}
