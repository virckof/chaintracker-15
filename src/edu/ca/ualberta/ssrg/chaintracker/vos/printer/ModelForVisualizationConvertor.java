package edu.ca.ualberta.ssrg.chaintracker.vos.printer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.CodeConcept;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.M2TTransformationTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.T2CTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.TemplateConcept;

/**
 * ModelForVisualizationConvertor converts Concept objects and Model Elements into
 * a standard ModelForVisualization (to be used to display traces in GUI).
 *
 */
public class ModelForVisualizationConvertor {

	/**
	 * Create ModelForVisualizations from ModelElements
	 * @param elems
	 * @return
	 */
	public static List<ModelForVisualization> getModelsFromModelElement(ArrayList<ModelElement> elems){
		ArrayList<ModelForVisualization> modelsForVis = new ArrayList<ModelForVisualization>();
		
		for(ModelElement e : elems){
			
			ModelForVisualization model = getModelFromElement(e);
			if (model != null) {
				modelsForVis.add(model);
			}
		}
		
		modelsForVis.addAll(solveSupertypesID(elems));
		
		return formatFileNames(modelsForVis);
	}
	
	
	/**
	 * Solves ids for source elements that have super types.
	 * @throws Exception
	 */
	private static List<ModelForVisualization> solveSupertypesID(ArrayList<ModelElement> elems){
		ArrayList<ModelForVisualization> modelsForVis = new ArrayList<ModelForVisualization>();
		
		for(ModelElement e : elems){	
			String superType = e.getSupertype();
			
			solveSupertypesID(elems, e, superType, modelsForVis);
		}
		
		return formatFileNames(modelsForVis);
	}
	
	private static void solveSupertypesID(ArrayList<ModelElement> elems,ModelElement element, String superType, ArrayList<ModelForVisualization> modelsForVis){
		if (superType == null) {
			return;
		}
		
		ArrayList<ModelElement>  superElems = getElement(elems, superType);
		if (superElems.size() == 0) {
			return;
		}
		
		for(ModelElement superE : superElems){
			if (superE.getMember() == null) continue;
			
			String newInheritedElemID = superE.getMetamodel()+";"+element.getModelElement()+";"+superE.getMember();
			ModelForVisualization model = new ModelForVisualization();
			model.setModel(newInheritedElemID);
			model.setModelSourceType(element.getConceptType()); 
			model.setModelType(superE.getType());
			
			modelsForVis.add(model);
		}
		
		superType = superElems.get(0).getSupertype();
		solveSupertypesID(elems, element, superType, modelsForVis);
	}
	
	/**
	 * Sequential search of a model element given the model element's name
	 * @param elems
	 * @param name
	 * @return
	 */
	private static ArrayList<ModelElement> getElement(ArrayList<ModelElement> elems, String name){
		ArrayList<ModelElement> superElems= new ArrayList<>();
		
		for(ModelElement model : elems){
			if(model.getModelElement().equals(name)){
				superElems.add(model);
			}
		}
		return superElems;
	}

	/**
	 * Create ModelForVisualization from M2T Tuple targets.
	 * (ModelForVisualization for M2T source are added using ModelElement)
	 * 
	 * @param tuples
	 * @return
	 */
	public static List<ModelForVisualization> getModelsFromM2TTuples(ArrayList<M2TTransformationTuple> tuples) {
		ArrayList<ModelForVisualization> modelsForVis = new ArrayList<ModelForVisualization>();

		for (M2TTransformationTuple tuple : tuples) {
			modelsForVis.add(getModelFromConcept(tuple.getTarget()));
		}

		return modelsForVis;
	}

	/**
	 * Create ModelForVisualization from T2C Tuple sources and targets.
	 * 
	 * @param tuples
	 * @return
	 */
	public static List<ModelForVisualization> getModelsFromT2CTuples(ArrayList<T2CTuple> tuples) {
		ArrayList<ModelForVisualization> modelsForVis = new ArrayList<ModelForVisualization>();

		for (T2CTuple tuple : tuples) {
			modelsForVis.add(getModelFromConcept(tuple.getSource()));
			modelsForVis.add(getModelFromConcept(tuple.getTarget()));
		}

		return modelsForVis;
	}

	//********************************************************************
	// ModelForVisualization convertors
	//********************************************************************

	/**
	 * Create ModelForVisualization for a ModelElement
	 * @param modelElement
	 * @return
	 */
	private static ModelForVisualization getModelFromElement(ModelElement modelElement) {
		if (modelElement.isEmpty()) return null;

		ModelForVisualization model = new ModelForVisualization();
		model.setModel(modelElement.getSimpleID());
		model.setModelSourceType(modelElement.getConceptType()); 
		model.setModelType(modelElement.getType());

		return formatFileNames(model);
	}

	/**
	 * Create ModelForVisualization for a TemplateConcept
	 * @param modelElement
	 * @return
	 */
	private static ModelForVisualization getModelFromConcept(TemplateConcept templateConcept) {
		ModelForVisualization model = new ModelForVisualization();
		model.setModel(templateConcept.getUniqueID());
		model.setModelSourceType(templateConcept.getTypeName()); 
		model.setModelType(templateConcept.getTemplateExpressionType());

		return formatFileNames(model);
	}

	/**
	 * Create ModelForVisualization for a CodeConcept
	 * @param modelElement
	 * @return
	 */
	private static ModelForVisualization getModelFromConcept(CodeConcept codeConcept) {
		ModelForVisualization model = new ModelForVisualization();
		model.setModel(codeConcept.getUniqueID());
		model.setModelSourceType(codeConcept.getTypeName()); 
		model.setModelType("code"); //hardcoded

		return formatFileNames(model);
	}


	//********************************************************************
	// File name format helpers
	//********************************************************************
	//TODO should handle file name striping at the source, not last resort here!
	
	private static List<ModelForVisualization>  formatFileNames(List<ModelForVisualization> models) {
		for (ModelForVisualization model : models) {
			model = formatFileNames(model);
		}

		return models;
	}

	private static ModelForVisualization formatFileNames(ModelForVisualization model) {
		if (model == null || model.getModel() == null) return model;
		
		model.setModel(stripFilePath(model.getModel()));

		return model;
	}

	private static String stripFilePath(String fileName) {
		File f = new File(fileName);
		String name = f.getName();
		
		return name;
	}

}
