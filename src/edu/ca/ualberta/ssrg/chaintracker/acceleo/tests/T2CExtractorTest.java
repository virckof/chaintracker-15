package edu.ca.ualberta.ssrg.chaintracker.acceleo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoTransformationParser;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.T2CTupleExtractor;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TraceBackParser;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TraceBackParserException;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TransformationParserException;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.TemplateConcept;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.T2CTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.CodeConcept;

public class T2CExtractorTest {
	private static final String SCORING_TEMPLATE =  "./data/acceleotests/templates/generateSimple.mtl";
	private static final String SCORING_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Scoring.ecore";
	private static final String SCORING_GEN_ANNOTATED_CODE = "./data/acceleotests/code/ScoringManager.java";
	private static final String SCORING_GEN_ORIGINAL_CODE = "test path to not annotated code"; 
	
	private AcceleoTransformationParser atp;
	private TraceBackParser traceParser;
	
	private void setupScoringTest() throws TransformationParserException, TraceBackParserException {
		atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		traceParser = new TraceBackParser();
		traceParser.traceBack(SCORING_GEN_ANNOTATED_CODE);
	}
	
	@Test
	public void test_getT2CTuples_globalVariables() throws IOException, TraceBackParserException, TransformationParserException {
		setupScoringTest();
		
		T2CTupleExtractor ex = new T2CTupleExtractor(traceParser, atp, SCORING_GEN_ORIGINAL_CODE);
		ArrayList<T2CTuple> tuples = ex.extractTuples();
		
		for (T2CTuple t : tuples) {
			assertEquals(SCORING_TEMPLATE, t.getSource().getFilePath());
			assertEquals(SCORING_GEN_ORIGINAL_CODE, t.getTarget().getFilePath());
		}
		
		ex.printTuples();
	}
	
	@Test
	public void test_getT2CTuples_sourceConcept() throws IOException, TraceBackParserException, TransformationParserException {
		setupScoringTest();
		
		T2CTupleExtractor ex = new T2CTupleExtractor(traceParser, atp, SCORING_GEN_ORIGINAL_CODE);
		ArrayList<T2CTuple> tuples = ex.extractTuples();
		
		TemplateConcept src = getFirstTupleByTemplateLine(tuples, 10).getSource();
		assertEquals("10", src.getTemplateLine());
		assertEquals("generateElement(aScoreRules : ScoreRules)", src.getTemplateName());
		assertEquals(SCORING_TEMPLATE, src.getFilePath());
	}
	
	@Test
	public void test_getT2CTuples_sourceConcept_loop() throws IOException, TraceBackParserException, TransformationParserException {
		setupScoringTest();
		
		T2CTupleExtractor ex = new T2CTupleExtractor(traceParser, atp, SCORING_GEN_ORIGINAL_CODE);
		ArrayList<T2CTuple> tuples = ex.extractTuples();
		
		TemplateConcept src = getFirstTupleByTemplateLine(tuples, 10).getSource();
		assertEquals("Loop", src.getTemplateExpressionType());
	}
	
	@Test
	public void test_getT2CTuples_sourceConcept_conditional() throws IOException, TraceBackParserException, TransformationParserException {
		setupScoringTest();
		
		T2CTupleExtractor ex = new T2CTupleExtractor(traceParser, atp, SCORING_GEN_ORIGINAL_CODE);
		ArrayList<T2CTuple> tuples = ex.extractTuples();
		
		TemplateConcept src = getFirstTupleByTemplateLine(tuples, 17).getSource();
		assertEquals("Conditional", src.getTemplateExpressionType());
	}

	@Test
	public void test_getT2CTuples_sourceConcept_simple() throws IOException, TraceBackParserException, TransformationParserException {
		setupScoringTest();
		
		T2CTupleExtractor ex = new T2CTupleExtractor(traceParser, atp, SCORING_GEN_ORIGINAL_CODE);
		ArrayList<T2CTuple> tuples = ex.extractTuples();
		
		TemplateConcept src = getFirstTupleByTemplateLine(tuples, 15).getSource();
		assertEquals("Simple", src.getTemplateExpressionType());
	}
	
	@Test
	public void test_getT2CTuples_targetConcept() throws IOException, TraceBackParserException, TransformationParserException {
		setupScoringTest();
		
		T2CTupleExtractor ex = new T2CTupleExtractor(traceParser, atp, SCORING_GEN_ORIGINAL_CODE);
		ArrayList<T2CTuple> tuples = ex.extractTuples();
		
		CodeConcept target = getFirstTupleByTemplateLine(tuples, 14).getTarget();
		assertEquals("7", target.getCodeLine());
	}
	
	@Test
	public void test_getT2CTuples_targetConcept_isEmptyLine() throws IOException, TraceBackParserException, TransformationParserException {
		setupScoringTest();
		
		T2CTupleExtractor ex = new T2CTupleExtractor(traceParser, atp, SCORING_GEN_ORIGINAL_CODE);
		ArrayList<T2CTuple> tuples = ex.extractTuples();
		
		CodeConcept target = getFirstTupleByCodeLine(tuples, 6).getTarget();
		assertTrue(target.isEmptyLine());
		
		target = getFirstTupleByCodeLine(tuples, 10).getTarget();
		assertFalse(target.isEmptyLine());
		
		target = getFirstTupleByCodeLine(tuples, 11).getTarget();
		assertTrue(target.isEmptyLine());
		
		target = getFirstTupleByCodeLine(tuples, 12).getTarget();
		assertTrue(target.isEmptyLine());
	}
	
	private T2CTuple getFirstTupleByTemplateLine(List<T2CTuple> tuples, int templateLine) {
		for (T2CTuple t : tuples) {
			if (t.getSource().getTemplateLine().equals(templateLine + "")) {
				return t;
			}
		}
		
		return null;
	}
	
	private T2CTuple getFirstTupleByCodeLine(List<T2CTuple> tuples, int codeLine) {
		for (T2CTuple t : tuples) {
			if (t.getTarget().getCodeLine().equals(codeLine + "")) {
				return t;
			}
		}
		
		return null;
	}
}
