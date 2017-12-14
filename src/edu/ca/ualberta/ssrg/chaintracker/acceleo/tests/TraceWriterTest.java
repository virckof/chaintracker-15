package edu.ca.ualberta.ssrg.chaintracker.acceleo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoTransformationParser;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TraceWriter;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TraceWriterException;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TransformationParserException;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoConditional;

public class TraceWriterTest {
	private static final String SCORING_SIMPLE_TRANS =  "./data/acceleotests/templates/generateSimple.mtl";
	private static final String CONDITIONAL_TEST_TEMPLATE =  "./data/acceleotests/templates/generateCondTest.mtl";
	private static final String SCORING_ECORE = "./data/acceleotests/metamodels/phy2templates/Scoring.ecore";
	
	private static final String SCREENFLOW_STATIC_TRANS =  "./data/acceleotests/templates/generateStatic.mtl";
	private static final String SCREENFLOW_STATIC_ECORE = "./data/acceleotests/metamodels/screenflow/ScreenSimple.ecore";
	
	private static final String ACCELEO_LISTROW_TRANS =  "./data/acceleotests/acceleo-examples-simplified/templates/listrowXML.mtl";
	private static final String ACCELEO_EX_ECORE = "./data/acceleotests/acceleo-examples-simplified/metamodels/android.ecore";
	
	private TraceWriter writer;
	
	@Before
	public void setup() {
		writer = new TraceWriter();
		writer.setDebugMode(true);
	}
	
	@Test
	public void test_genLines() throws TransformationParserException, TraceWriterException, IOException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_ECORE, SCORING_SIMPLE_TRANS);
		atp.parse();
		
		writer.writeTraces(atp, "./data/acceleotests/traceWriterTests/gen/generateSimple.mtl");
		
		List<String> actualFileLines = writer.getFileLines();
		List<String> expectedFileLines =  Files.readLines(new File("./data/acceleotests/traceWriterTests/generateSimple_Expected.mtl"), Charset.defaultCharset());
		for (int i = 0; i < actualFileLines.size(); i ++) {
			String actual = actualFileLines.get(i).trim();
			String expect = expectedFileLines.get(i).trim();
			assertEquals(expect, actual);
		}
	}
	
	@Test
	public void test_genLines_withCorrectIndents() throws TransformationParserException, TraceWriterException, IOException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_ECORE, SCREENFLOW_STATIC_TRANS);
		atp.parse();
		
		writer.writeTraces(atp, "./data/acceleotests/traceWriterTests/gen/generateStatic.mtl");
		
		List<String> actualFileLines = writer.getFileLines();
		List<String> expectedFileLines =  Files.readLines(new File("./data/acceleotests/traceWriterTests/generateStatic_Expected.mtl"), Charset.defaultCharset());
		for (int i = 0; i < actualFileLines.size(); i ++) {
			String actual = actualFileLines.get(i);
			String expect = expectedFileLines.get(i);
			assertEquals(expect, actual);
		}
	}
	
	@Test
	public void test_genLines_conditionals() throws TransformationParserException, TraceWriterException, IOException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_ECORE, CONDITIONAL_TEST_TEMPLATE);
		atp.parse();
		
		writer.writeTraces(atp, "./data/acceleotests/traceWriterTests/gen/generateCondTest.mtl");
		
		List<String> actualFileLines = writer.getFileLines();
		List<String> expectedFileLines =  Files.readLines(new File("./data/acceleotests/traceWriterTests/generateCondTest_Expected.mtl"), Charset.defaultCharset());
		for (int i = 0; i < actualFileLines.size(); i ++) {
			String actual = actualFileLines.get(i).trim();
			String expect = expectedFileLines.get(i).trim();
			assertEquals(expect, actual);
		}
	}
	
	@Test
	public void test_genLines_loop() throws TransformationParserException, TraceWriterException, IOException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ACCELEO_EX_ECORE, ACCELEO_LISTROW_TRANS);
		atp.parse();
		
		writer.writeTraces(atp, "./data/acceleotests/traceWriterTests/gen/listrowXML.mtl");
		
		List<String> actualFileLines = writer.getFileLines();
		List<String> expectedFileLines =  Files.readLines(new File("./data/acceleotests/traceWriterTests/listrowXML_Expected.mtl"), Charset.defaultCharset());
		for (int i = 0; i < actualFileLines.size(); i ++) {
			String actual = actualFileLines.get(i).trim();
			String expect = expectedFileLines.get(i).trim();
			assertEquals(expect, actual);
		}
	}
	
	@Test
	public void test_getSpacer() {
		String spacer = writer.getFrontEndSpace("  hello");
		assertEquals("  ", spacer);
	}
}
