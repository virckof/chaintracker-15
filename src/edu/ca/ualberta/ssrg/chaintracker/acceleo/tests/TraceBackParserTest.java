package edu.ca.ualberta.ssrg.chaintracker.acceleo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TraceBackParser;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TraceBackParserException;

public class TraceBackParserTest {
	
	private static final String SCORING_GEN_CODE = "./data/acceleotests/code/ScoringManager.java";
	private static final String ACCELEO_EX_LISTROW_GEN_CODE = "./data/acceleotests/traceBackParserTests/listrow_annotated.xml";
	
	@Test
	public void test_parseGenLine() {
		TraceBackParser parser = new TraceBackParser();
		Integer result = parser.parseGenLine("//@gen < 10 TouchRule");
		assertEquals(10, result, 0);
	}
	
	@Test
	public void test_getTracebacks() throws IOException, TraceBackParserException {
		TraceBackParser parser = new TraceBackParser();
		parser.traceBack(SCORING_GEN_CODE);
		
		Hashtable<Integer, List<Integer>> tracebacks = parser.getTracebacks();
		
		assertTrue(tracebacks.get(1) == null);
		
		assertTrue(tracebacks.get(4).contains(10));
		assertTrue(tracebacks.get(4).contains(11));
		
		assertTrue(tracebacks.get(7).contains(10));
		assertTrue(tracebacks.get(7).contains(14));
		
		assertTrue(tracebacks.get(8).contains(10));
		assertTrue(tracebacks.get(8).contains(15));
		
		assertTrue(tracebacks.get(9).contains(10));
		assertTrue(tracebacks.get(9).contains(16));
		
		assertTrue(tracebacks.get(10).contains(10));
		assertTrue(tracebacks.get(10).contains(17));
		
		assertTrue(tracebacks.get(11).contains(10));
	}
	
	@Test
	public void test_getTracebacks_xml() throws IOException, TraceBackParserException {
		TraceBackParser parser = new TraceBackParser();
		parser.traceBack(ACCELEO_EX_LISTROW_GEN_CODE);
		
		Hashtable<Integer, List<Integer>> tracebacks = parser.getTracebacks();
		
		
		// lines depending on for loop
		assertTrue(tracebacks.get(4).contains(20));
		assertTrue(tracebacks.get(5).contains(20));
		assertTrue(tracebacks.get(6).contains(20));
		assertTrue(tracebacks.get(7).contains(20));
		assertTrue(tracebacks.get(8).contains(20));
		assertTrue(tracebacks.get(9).contains(20));
		assertTrue(tracebacks.get(10).contains(20));

		// lines depending on if
		assertTrue(tracebacks.get(4).contains(21));
		assertTrue(tracebacks.get(5).contains(21));
		assertTrue(tracebacks.get(6).contains(21));
		assertTrue(tracebacks.get(7).contains(21));
		
		// lines depending on else if
		assertTrue(tracebacks.get(8).contains(26));
		assertTrue(tracebacks.get(9).contains(26));
		assertTrue(tracebacks.get(10).contains(26));
		
		// lines depending on simple
		assertTrue(tracebacks.get(4).contains(22));
		assertTrue(tracebacks.get(8).contains(27));
	}
}
