package edu.ca.ualberta.ssrg.chaintracker.vos.printer.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.ecore.EcoreSolver;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelForVisualization;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelForVisualizationConvertor;

public class ModelForVisualizationConvertorTests {

	private static final String XML_ECORE =  "examples/ATLExperiments/OWL2XML/XML.ecore";
	private static final String OWL_ECORE =  "examples/ATLExperiments/OWL2XML/OWL.ecore";
	
	@Test
	public void ResolveSuperTypes_1LevelDeep() {
		EcoreSolver solver = new EcoreSolver();
		ArrayList<ModelElement> modelElements = solver.getModelElements(XML_ECORE);
		
		List<ModelForVisualization> models = ModelForVisualizationConvertor.getModelsFromModelElement(modelElements);
		
		// Root : Element - children
		ModelForVisualization m = getModelByModelName("XML;Root;children", models);
		assertTrue(m != null);
	}
	
	@Test
	public void ResolveSuperTypes_MultipleLevelsDeep() {
		EcoreSolver solver = new EcoreSolver();
		ArrayList<ModelElement> modelElements = solver.getModelElements(OWL_ECORE);
		
		List<ModelForVisualization> models = ModelForVisualizationConvertor.getModelsFromModelElement(modelElements);
		
		// FunctionalProperty : Property
		// Property : RDFProperty, OWLUniverse
		// RDFProperty : RDFSResource - isDefinedBy
		ModelForVisualization m = getModelByModelName("OWL;FunctionalProperty;isDefinedBy", models);
		assertTrue(m != null);
	}
	
	private ModelForVisualization getModelByModelName(String model, List<ModelForVisualization> models) {
		for (ModelForVisualization m : models) {
			if (m.getModel().equals(model)) {
				return m;
			}
		}
		
		return null;
	}
}
