package edu.ca.ualberta.ssrg.chaintracker.acceleo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoTransformationParser;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.M2TTransformationBuilder;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.M2TTransformationBuilder.M2TType;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TransformationParserException;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2TTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetTextualElement;

public class M2TTransformationBuilderTest {
	private static final String SCORING_MULTI_TEMPLATE = "./data/acceleotests/templates/generateMultiTemplate.mtl";
	private static final String SCORING_SIMPLE_TEMPLATE = "./data/acceleotests/templates/generateSimple.mtl";
	private static final String SCORING_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Scoring.ecore";
	private static final String DYNAMICS_TEMPLATE =  "./data/acceleotests/templates/phy2templates/generateDynamics.mtl";
	private static final String DYNAMICS_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Dynamics.ecore";
	private static final String SCREENFLOW_STATIC_TEMPLATE =  "./data/acceleotests/templates/screenflow/generateStatic.mtl";
	private static final String SCREENFLOW_STATIC_TEST_TEMPLATE =  "./data/acceleotests/templates/generateStatic.mtl";
	private static final String SCREENFLOW_STATIC_METAMODEL = "./data/acceleotests/metamodels/screenflow/ScreenSimple.ecore";
	private static final String LAYOUT_TEMPLATE =  "./data/acceleotests/templates/phy2templates/generateLayout.mtl";
	private static final String LAYOUT_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Layout.ecore";
	
	private M2TTransformationBuilder builder;
	
	@Before
	public void setup() {
		builder = new M2TTransformationBuilder();
	}
	
	/* M2T - General ----------------------------------------------- */
	
	@Test
	public void test_getM2TTransformations_globalVariables() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp);
		for (M2TTransformation t : transformations) {
			assertEquals("Scoring", t.getSourceElement().getSourceModel());
			assertEquals(SCORING_SIMPLE_TEMPLATE, t.getOwnerTemplate());
		}
		
	}
	
	@Test
	public void test_getM2TTransformations_modelURI() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp);
		for (M2TTransformation t : transformations) {
			//assertEquals("http://ualberta.edu.cs.ssrg.phy.scoring", t.getSourceElement().getModelURI());
			assertEquals(SCORING_METAMODEL, t.getSourceElement().getModelURI());
		}
		
	}
	
	@Test
	public void test_getM2TTransformations_templateName() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp);
		for (M2TTransformation t : transformations) {
			assertEquals("generateElement(aScoreRules : ScoreRules)", t.getTargetElement().getTemplateName());
		}
	}
	
	@Test
	public void test_getM2TTransformations_templateName_multiTemplates() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_MULTI_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp);
		M2TTransformation t = getTransformationByTargetLine(transformations, 10);
		assertEquals("generateElement(aScoreRules : ScoreRules)", t.getTargetElement().getTemplateName());
		
		 t = getTransformationByTargetLine(transformations, 36);
		assertEquals("generateElement(bScoreRules : ScoreRules)", t.getTargetElement().getTemplateName());
	}
	
	@Test
	public void test_getM2TTransformations_isControl() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Loop);
		for (M2TTransformation t : transformations) {
			assertTrue(t.isControl());
		}
		
		transformations = builder.getM2TTransformations(atp, M2TType.Conditional);
		for (M2TTransformation t : transformations) {
			assertTrue(t.isControl());
		}
		
		transformations = builder.getM2TTransformations(atp, M2TType.Simple);
		for (M2TTransformation t : transformations) {
			assertTrue(!t.isControl());
		}
		
	}
	
	/* M2T - LOOPS ----------------------------------------------- */
	
	@Test
	public void test_getM2TTransformations_loop() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Loop);
		M2TTransformation t = transformations.get(0);
		SourceElement source = t.getSourceElement();
		assertEquals("ScoreRules", source.getSourceElementType());
		assertEquals(null, source.getSourceModelVariable());
		assertEquals(".touches", source.getAttributes().get(0).getImplicitBindings());
		
		TargetTextualElement target = t.getTargetElement();
		assertEquals("10", target.getTargetLine());
		assertEquals(M2TType.Loop.toString(), target.getTargetExpressionType());
	}
	
	@Test
	public void test_getM2TTransformations_loop2() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_METAMODEL, SCREENFLOW_STATIC_TEST_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Loop);
		M2TTransformation t = getTransformationByTargetLine(transformations, 8);
		SourceElement source = t.getSourceElement();
		assertEquals("Application", source.getSourceElementType());
		assertEquals(null, source.getSourceModelVariable());
		assertEquals(".screens", source.getAttributes().get(0).getImplicitBindings());
		
		TargetTextualElement target = t.getTargetElement();
		assertEquals("8", target.getTargetLine());
		assertEquals(M2TType.Loop.toString(), target.getTargetExpressionType());
	}
	
	/* M2T - CONDITIONALS ----------------------------------------------- */
	
	@Test
	public void test_getM2TTransformations_conditional() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Conditional);
		M2TTransformation t = transformations.get(0);
		SourceElement source = t.getSourceElement();
		assertEquals("TouchRule", source.getSourceElementType());
		assertEquals("touchRule", source.getSourceModelVariable());
		assertEquals(".touchAction.removeActorId", source.getAttributes().get(0).getImplicitBindings());
		
		TargetTextualElement target = t.getTargetElement();
		assertEquals("17", target.getTargetLine());
		assertEquals(M2TType.Conditional.toString(), target.getTargetExpressionType());
		
		// 2nd
		
		t = transformations.get(1);
		source = t.getSourceElement();
		assertEquals("TouchRule", source.getSourceElementType());
		assertEquals("touchRule", source.getSourceModelVariable());
		assertEquals(".actorId", source.getAttributes().get(0).getImplicitBindings());
		
		target = t.getTargetElement();
		assertEquals("17", target.getTargetLine());
		assertEquals(M2TType.Conditional.toString(), target.getTargetExpressionType());
	}
	
	@Test
	public void test_getM2TTransformations_conditional_explicit_root() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_METAMODEL, SCREENFLOW_STATIC_TEST_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Conditional);
		M2TTransformation t = getTransformationByTargetLine(transformations, 21);;
		SourceElement source = t.getSourceElement();
		assertEquals("Application", source.getSourceElementType());
		assertEquals("app", source.getSourceModelVariable());
		assertEquals("._package", source.getAttributes().get(0).getImplicitBindings());
		
		TargetTextualElement target = t.getTargetElement();
		assertEquals("21", target.getTargetLine());
		assertEquals(M2TType.Conditional.toString(), target.getTargetExpressionType());
	}
	
	
	@Test
	public void test_getM2TTransformations_conditional_doubleNested() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Conditional);
		M2TTransformation t = getTransformationByTargetLine(transformations, 275);;
		SourceElement source = t.getSourceElement();
		assertEquals("Actor", source.getSourceElementType());
		assertEquals("actor", source.getSourceModelVariable());
		assertEquals(".isBall", source.getAttributes().get(0).getImplicitBindings());
		
		TargetTextualElement target = t.getTargetElement();
		assertEquals("275", target.getTargetLine());
		assertEquals(M2TType.Conditional.toString(), target.getTargetExpressionType());
	}
	
	
	/* M2T - SIMPLE ----------------------------------------------- */
	
	@Test
	public void test_getM2TTransformations_simple_nested() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_SIMPLE_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Simple);
		M2TTransformation t = getTransformationByTargetLine(transformations, 11);
		SourceElement source = t.getSourceElement();
		assertEquals("TouchRule", source.getSourceElementType());
		assertEquals("touchRule", source.getSourceModelVariable());
		assertEquals(".actorId", source.getAttributes().get(0).getImplicitBindings());
		
		TargetTextualElement target = t.getTargetElement();
		assertEquals("11", target.getTargetLine());
		assertEquals(M2TType.Simple.toString(), target.getTargetExpressionType());
		
		// 2nd
		
		t = getTransformationByTargetLine(transformations, 14);
		source = t.getSourceElement();
		assertEquals("TouchRule", source.getSourceElementType());
		assertEquals("touchRule", source.getSourceModelVariable());
		assertEquals(".touchAction.points", source.getAttributes().get(0).getImplicitBindings());
		
		target = t.getTargetElement();
		assertEquals("14", target.getTargetLine());
		assertEquals(M2TType.Simple.toString(), target.getTargetExpressionType());
		
		// 3rd
		
		t = getTransformationByTargetLine(transformations, 15);
		source = t.getSourceElement();
		assertEquals("TouchRule", source.getSourceElementType());
		assertEquals("touchRule", source.getSourceModelVariable());
		assertEquals(".touchAction.userLoses", source.getAttributes().get(0).getImplicitBindings());
		
		target = t.getTargetElement();
		assertEquals("15", target.getTargetLine());
		assertEquals(M2TType.Simple.toString(), target.getTargetExpressionType());
		
		// 4th
		
		t = getTransformationByTargetLine(transformations, 16);
		source = t.getSourceElement();
		assertEquals("TouchRule", source.getSourceElementType());
		assertEquals("touchRule", source.getSourceModelVariable());
		assertEquals(".touchAction.gameEnds", source.getAttributes().get(0).getImplicitBindings());
		
		target = t.getTargetElement();
		assertEquals("16", target.getTargetLine());
		assertEquals(M2TType.Simple.toString(), target.getTargetExpressionType());
	}
	
	@Test
	public void test_getM2TTransformations_simple_root() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(DYNAMICS_METAMODEL, DYNAMICS_TEMPLATE);
		atp.parse();
		
		List<M2TTransformation> transformations = builder.getM2TTransformations(atp, M2TType.Simple);
		M2TTransformation t =  getTransformationByTargetLine(transformations, 148);
		SourceElement source = t.getSourceElement();
		assertEquals("Dynamics", source.getSourceElementType());
		assertEquals("aDynamics", source.getSourceModelVariable());
		assertEquals(".environment.gravity", source.getAttributes().get(0).getImplicitBindings());
		
		TargetTextualElement target = t.getTargetElement();
		assertEquals("148", target.getTargetLine());
		assertEquals(M2TType.Simple.toString(), target.getTargetExpressionType());
	}
	
	private M2TTransformation getTransformationByTargetLine(List<M2TTransformation> transformations, int targetLine) {
		for (M2TTransformation t : transformations) {
			if (t.getTargetElement().getTargetLine().equals(targetLine + "")) {
				return t;
			}
		}
		
		return null;
	}
}
