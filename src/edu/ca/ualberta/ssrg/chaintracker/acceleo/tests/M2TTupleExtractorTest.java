package edu.ca.ualberta.ssrg.chaintracker.acceleo.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoTransformationParser;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.M2TTransformationBuilder;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.M2TTupleExtractor;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TransformationParserException;
import edu.ca.ualberta.ssrg.chaintracker.ecore.EcoreSolver;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2TTransformation;

public class M2TTupleExtractorTest {
	private static final String SCORING_SIMPLE_TEMPLATE = "./data/acceleotests/templates/generateSimple.mtl";
	private static final String SCORING_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Scoring.ecore";
	
	private static final String LAYOUT_TEMPLATE = "./data/acceleotests/templates/phy2templates/generateLayout.mtl";
	private static final String LAYOUT_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Layout.ecore";
	
	@Test
	public void test__getM2TTuple() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		M2TTransformationBuilder builder = new M2TTransformationBuilder();
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp);
		
		EcoreSolver solver = new EcoreSolver();
		solver.setModelFile(SCORING_METAMODEL);
		solver.initialize();
		
		M2TTupleExtractor ex = new M2TTupleExtractor(new ArrayList(transformations), SCORING_SIMPLE_TEMPLATE, solver);
		ex.extractTuples();
		ex.printTuples();
	}
	
	@Test
	public void test_getM2TTuple_nestedLoop() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEMPLATE);
		atp.parse();
		
		M2TTransformationBuilder builder = new M2TTransformationBuilder();
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp);
		
		EcoreSolver solver = new EcoreSolver();
		solver.setModelFile(LAYOUT_METAMODEL);
		solver.initialize();
		
		M2TTupleExtractor ex = new M2TTupleExtractor(new ArrayList(transformations), LAYOUT_TEMPLATE, solver);
		ex.extractTuples();
		ex.printTuples();
	}
}
