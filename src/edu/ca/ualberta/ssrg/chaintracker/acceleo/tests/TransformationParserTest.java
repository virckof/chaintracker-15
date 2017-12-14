package edu.ca.ualberta.ssrg.chaintracker.acceleo.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.AcceleoTransformationParser;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.main.TransformationParserException;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoConditional;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoFile;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoLoop;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoModule;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoNonOCLExpresion;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoTemplate;

public class TransformationParserTest {

	// PhyDsl Test files
	private static final String LAYOUT_TEMPLATE =  "./data/acceleotests/templates/phy2templates/generateLayout.mtl";
	private static final String LAYOUT_TEST_TEMPLATE =  "./data/acceleotests/templates/generateLayoutTest.mtl";
	private static final String LAYOUT_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Layout.ecore";
	private static final String CONDITIONAL_TEST_TEMPLATE =  "./data/acceleotests/templates/generateCondTest.mtl";
	private static final String IMPLICIT_MODEL_TEMPLATE =  "./data/acceleotests/templates/implicitModelTest.mtl";
	private static final String SCORING_TEMPLATE =  "./data/acceleotests/templates/phy2templates/generateScoring.mtl";
	private static final String SCORING_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Scoring.ecore";
	private static final String DYNAMICS_TEMPLATE =  "./data/acceleotests/templates/phy2templates/generateDynamics.mtl";
	private static final String DYNAMICS_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Dynamics.ecore";
	
	// ScreenFlow Test files
	private static final String SCREENFLOW_STATIC_TEMPLATE =  "./data/acceleotests/templates/screenflow/generateStatic.mtl";
	private static final String SCREENFLOW_STATIC_TEST_TEMPLATE =  "./data/acceleotests/templates/generateStatic.mtl";
	private static final String SCREENFLOW_STATIC_METAMODEL = "./data/acceleotests/metamodels/screenflow/ScreenSimple.ecore";
	
	// Acceleo Example Test files
	private static final String ANDROID_METAMODEL = "./data/acceleotests/acceleo-examples/metamodels/android.ecore";
	private static final String DBADAPTER_TEMPLATE =  "./data/acceleotests/acceleo-examples/templates/dbadapter.mtl";
	private static final String EDITXML_TEMPLATE =  "./data/acceleotests/acceleo-examples/templates/editXML.mtl";
	private static final String WORKFLOW_TEMPLATE =  "./data/acceleotests/acceleo-examples/templates/workflow.mtl";
	
	/* General ----------------------------------------------- */
	
	@Test
	public void test_checkForModelName() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		assertEquals("Scoring", atp.getModelName());
	}
	
	/* Parsing FILE tags ----------------------------------------------- */
	
	@Test
	public void test_checkForFile_staticName() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		// line: [file ('ScoringManager.java', false, 'UTF-8')]
		AcceleoFile f = atp.getFiles().get(7);
		assertEquals("ScoringManager.java", f.getFileName());
	}
	
	@Test
	public void test_checkForFile_otherProperties() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		// line: [file ('ScoringManager.java', false, 'UTF-8')]
		AcceleoFile f = atp.getFiles().get(7);
		assertFalse(f.isAppendMode());
		assertEquals("UTF-8", f.getCharset());
	}
	
	@Test
	public void test_checkForFile_dynamicName_nested() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_METAMODEL, SCREENFLOW_STATIC_TEMPLATE);
		atp.parse();
		
		//line 74: [for (s : Screen | app.screens)]
		//line 75: [file (s.name.concat('.xml'), false, 'UTF-8')]
		AcceleoFile f = atp.getFiles().get(75);
		assertEquals("name.concat('.xml')", f.getFileName());
		assertEquals("Screen", f.getModelContext());
		assertEquals("s", f.getModelInstanceContext());
	}
	
	@Test
	public void test_checkForFile_dynamicName2() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, DBADAPTER_TEMPLATE);
		atp.parse();
		
		//line : [file ('/src/android/activities/'.concat(a.name.concat('DbAdapter.java')), false, 'Cp1252')]
		AcceleoFile f = atp.getFiles().get(15);
		assertEquals("'/src/android/activities/'.concat(a.name.concat('DbAdapter.java'))", f.getFileName());
		assertEquals("Activity", f.getModelContext());
		assertEquals("a", f.getModelInstanceContext());
	}
	
	/* Parsing MODULE tags ----------------------------------------------- */
	
	@Test
	public void test_checkForModuleDefinition() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEMPLATE);
		atp.checkForModuleDefinition("[module generateLayout('http://ualberta.edu.cs.ssrg.phy.layout')]", 1);
		
		AcceleoModule m = (AcceleoModule) atp.getModules().get(1).get(0);
		assertEquals("http://ualberta.edu.cs.ssrg.phy.layout", m.getModelURI());
		assertEquals("generateLayout", m.getModuleName());
	}
	
	/* Parsing TEMPLATE tags ----------------------------------------------- */
	
	@Test
	public void test_checkForTemplateDefinition() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEMPLATE);
		atp.checkForTemplateDefinition("[template public generateElement(aScoreRules : ScoreRules)]", 1);
		
		AcceleoTemplate t = (AcceleoTemplate) atp.getTemplates().get(1).get(0);
		assertEquals("public", t.getVisibility());
		assertEquals("generateElement", t.getMethodName());
		assertEquals("ScoreRules", t.getModelParamID());
		assertEquals("aScoreRules", t.getModelParamInstanceID());
		assertEquals("generateElement(aScoreRules : ScoreRules)", t.getTemplateName());
	}
	
	@Test
	public void test_checkForTemplateDefinition2() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, DBADAPTER_TEMPLATE);
		atp.checkForTemplateDefinition("[template public allWidgets (a : Activity) ]", 1);
		
		AcceleoTemplate t = (AcceleoTemplate) atp.getTemplates().get(1).get(0);
		assertEquals("public", t.getVisibility());
		assertEquals("allWidgets", t.getMethodName());
		assertEquals("Activity", t.getModelParamID());
		assertEquals("a", t.getModelParamInstanceID());
		assertEquals("allWidgets (a : Activity)", t.getTemplateName());
	}
	
	/* Parsing COMMENT tags ----------------------------------------------- */
	
	@Test
	public void test_checkForSingleLineComment() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEMPLATE);
		atp.checkForComments("[comment encoding = UTF-8 /]", 1);
		
		String comment = atp.getComments().get(1);
		assertEquals("encoding = UTF-8", comment);
	}
	
	@Test
	public void test_checkForMultiLineComment() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, DBADAPTER_TEMPLATE);
		atp.parse();
		
		atp.checkForComments("[comment encoding = UTF-8 /]", 1);
		
		String comment = atp.getComments().get(3);
		assertEquals("Copyright (c) 2010 Obeo", comment);
		
		comment = atp.getComments().get(7);
		assertEquals("Any license can be applied to the files generated with this template", comment);
	}
	
	/* Parsing SIMPLE EXPRESSION tags ----------------------------------------------- */
	
	// Simple sanity check that parser gets right number of expressions
	// Other tests will verify the actual contents of the results 
	@Test
	public void test_checkForSimpleExpression() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(DYNAMICS_METAMODEL, DYNAMICS_TEMPLATE);
		atp.parse();
		
		assertEquals(11, atp.getSimpleExpressions().size());
		
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		assertEquals(19, atp.getSimpleExpressions().size());
	}
	
	@Test
	public void test_checkForRootSimpleExpression() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(DYNAMICS_METAMODEL, DYNAMICS_TEMPLATE);
		atp.parse();
		
		//line: Vec2 grav = new Vec2(0.0f, [environment.gravity/]f);
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(148).get(0);
		assertEquals("Dynamics", exp.getModelContext());
		assertEquals("aDynamics", exp.getModelInstanceContext());
		assertEquals("environment.gravity", exp.getExpression());
	}
	
	@Test
	public void test_checkForRootSimpleExpression_explicitAccess() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_METAMODEL, SCREENFLOW_STATIC_TEMPLATE);
		atp.parse();
		
		//line:  package="[app._package/]"
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(12).get(0);
		assertEquals("Application", exp.getModelContext());
		assertEquals("app", exp.getModelInstanceContext());
		assertEquals("_package", exp.getExpression());
	}
	
	@Test
	public void test_checkForRootSimpleExpression_notCalledInRootEval() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_METAMODEL, SCREENFLOW_STATIC_TEMPLATE);
		atp.parse();
		
		//line: tools:context="[app._package/].[s.name/]Activity" >
		AcceleoNonOCLExpresion exp1 = atp.getSimpleExpressions().get(80).get(0);
		AcceleoNonOCLExpresion exp2 = atp.getSimpleExpressions().get(80).get(1);
		
		assertEquals("Application", exp1.getModelContext());
		assertEquals("Screen", exp2.getModelContext());
		
		assertEquals("app", exp1.getModelInstanceContext());
		assertEquals("s", exp2.getModelInstanceContext());
		
		assertEquals("_package", exp1.getExpression());
		assertEquals("name", exp2.getExpression());
	}
	
	@Test
	public void test_checkForNestedSimpleExpression() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(DYNAMICS_METAMODEL, DYNAMICS_TEMPLATE);
		atp.parse();
		
		//line 175: [it.activityId/].setType(PhysicsView.BODY_TYPE_CIRCLE);
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(175).get(0);
		assertEquals("AppearActivity", exp.getModelContext());
		assertEquals("it", exp.getModelInstanceContext());
		assertEquals("activityId", exp.getExpression()); 
	}
	
	@Test
	public void test_checkForMultipleSimpleExpression() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(DYNAMICS_METAMODEL, DYNAMICS_TEMPLATE);
		atp.parse();
		
		//line: [it.activityId/].setObjectId(PhysicsView.[it.actorId/]Id);
		AcceleoNonOCLExpresion exp1 = atp.getSimpleExpressions().get(171).get(0);
		AcceleoNonOCLExpresion exp2 = atp.getSimpleExpressions().get(171).get(1);
		assertEquals("AppearActivity", exp1.getModelContext());
		assertEquals("it", exp1.getModelInstanceContext());
		assertEquals("AppearActivity", exp2.getModelContext());
		assertEquals("it", exp2.getModelInstanceContext());
		
		assertTrue((exp1.getExpression().equals("actorId") && exp2.getExpression().equals("activityId"))
				|| (exp2.getExpression().equals("actorId") && exp1.getExpression().equals("activityId")));
	}
	
	@Test
	public void test_checkSimpleExpression_withOCLExpression() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, DBADAPTER_TEMPLATE);
		atp.parse();
		
		//line : public class [a.name.toUpperFirst()/]DbAdapter {
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(36).get(0);
		assertEquals("Activity", exp.getModelContext());
		assertEquals("a", exp.getModelInstanceContext());
		assertEquals("name", exp.getExpression()); 
	}
	
	@Test
	public void test_checkSimpleExpression_complex() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, EDITXML_TEMPLATE);
		atp.parse();
		
		//line : [28 + 40 * (a.widgets->indexOf(w) - 1)/]
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(31).get(0);
		assertEquals("Activity", exp.getModelContext());
		assertEquals("a", exp.getModelInstanceContext());
		assertEquals("widgets", exp.getExpression());
		
		exp = atp.getSimpleExpressions().get(31).get(1);
		assertEquals("Widget", exp.getModelContext());
		assertEquals("w", exp.getModelInstanceContext());
		assertEquals("", exp.getExpression()); 
	}
	
	@Test
	public void test_checkSimpleExpression_complex2() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, DBADAPTER_TEMPLATE);
		atp.parse();
		
		//line : [a.widgets->select(not oclIsKindOf(Button)).oneWidget()->sep(',')/]
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(186).get(0);
		assertEquals("Activity", exp.getModelContext());
		assertEquals("a", exp.getModelInstanceContext());
		assertEquals("widgets", exp.getExpression());
	}
	
	@Test
	public void test_checkSimpleExpression_isNotSimpleExp() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, WORKFLOW_TEMPLATE);
		atp.parse();
		
		//[androidmanifestXML()/]
		assertEquals(null, atp.getSimpleExpressions().get(27));
	}
	
	@Test
	public void test_checkSimpleExpression_implictModelAccess() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, IMPLICIT_MODEL_TEMPLATE);
		atp.parse();
		
		//line : [collisions->first().collisionAction.removeActorId/]
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(10).get(0);
		assertEquals("ScoreRules", exp.getModelContext());
		assertEquals("aScoreRules", exp.getModelInstanceContext());
		assertEquals("collisions", exp.getExpression());
		
		exp = atp.getSimpleExpressions().get(10).get(1);
		assertEquals("CollisionRule", exp.getModelContext());
		assertEquals("", exp.getModelInstanceContext()); 
		assertEquals("collisionAction.removeActorId", exp.getExpression()); 
	}
	
	@Test
	public void test_endingLines_forSimple() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(DYNAMICS_METAMODEL, DYNAMICS_TEMPLATE);
		atp.parse();
		
		AcceleoNonOCLExpresion exp = atp.getSimpleExpressions().get(172).get(0);
		assertEquals(172, exp.getEndLine().intValue());
	}
	
	/* Parsing CONDITIONAL tags ----------------------------------------------- */
	
	@Test
	public void test_checkForConditional_usingBooleanAttribute() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEST_TEMPLATE);
		atp.parse();
		
		//[if isDragEnabled]
		AcceleoConditional cond1 = atp.getConditionals().get(10).get(0);
		assertTrue(cond1.getModelContext().contains("Layout"));
		assertTrue(cond1.getModelInstanceContext().contains("aLayout"));
		assertTrue(cond1.getExpression().contains("isDragEnabled"));
	}
	
	@Test
	public void test_checkForConditional_usingBooleanAttribute_withBrackets() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEST_TEMPLATE);
		atp.parse();
		
		//[if (isDragEnabled)]
		AcceleoConditional cond1 = atp.getConditionals().get(14).get(0);
		assertTrue(cond1.getModelContext().contains("Layout"));
		assertTrue(cond1.getModelInstanceContext().contains("aLayout"));
		assertTrue(cond1.getExpression().contains("isDragEnabled"));
	}
	
	@Test
	public void test_checkForConditional_usingNestedBooleanAttribute() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEST_TEMPLATE);
		atp.parse();
		
		//[for (actor : Actor | actors)]
		//		[for (cell : Cell | actor.cell)]
		//		[if (actor.isBall)]	
		AcceleoConditional cond1 = atp.getConditionals().get(20).get(0);
		assertTrue(cond1.getModelContext().contains("Actor"));
		assertTrue(cond1.getModelInstanceContext().contains("actor"));
		assertTrue(cond1.getExpression().contains("isBall"));
	}
	
	@Test
	public void test_checkForConditional_usingEqualsIgnoreCase() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEST_TEMPLATE);
		atp.parse();
		
		//[if not background.cameraType.equalsIgnoreCase('none')]
		AcceleoConditional cond1 = atp.getConditionals().get(28).get(0);
		assertTrue(cond1.getModelContext().contains("Layout"));
		assertTrue(cond1.getModelInstanceContext().contains("aLayout"));
		assertTrue(cond1.getExpression().contains("background.cameraType"));
	}
	
	@Test
	public void test_checkForConditional_multipleAttributes() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		//[if (collisionRule.collisionAction.removeActorId.equalsIgnoreCase(collisionRule.actorAId))]
		AcceleoConditional cond1 = atp.getConditionals().get(111).get(0);
		assertTrue(cond1.getModelContext().contains("CollisionRule"));
		assertTrue(cond1.getModelInstanceContext().contains("collisionRule"));
		assertTrue(cond1.getExpression().contains("collisionAction.removeActorId"));
		
		assertTrue(cond1.getModelContext().contains("CollisionRule"));
		assertTrue(cond1.getModelInstanceContext().contains("collisionRule"));
		assertTrue(cond1.getExpression().contains("actorAId"));
	}
	
	@Test
	public void test_checkForConditional_insideForLoopAttributes() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		AcceleoConditional cond1 = atp.getConditionals().get(90).get(0);
		assertTrue(cond1.getModelInstanceContext().contains("touchRule"));
		assertTrue(cond1.getExpression().contains("touchAction.removeActorId"));
		assertTrue(cond1.getExpression().contains("actorId"));
		
		AcceleoConditional cond2 = atp.getConditionals().get(111).get(0);
		assertTrue(cond2.getModelInstanceContext().contains("collisionRule"));
		assertTrue(cond2.getExpression().contains("collisionAction.removeActorId"));
		assertTrue(cond2.getExpression().contains("actorAId"));
		
		AcceleoConditional cond3 = atp.getConditionals().get(118).get(0);
		assertTrue(cond3.getModelInstanceContext().contains("collisionRule"));
		assertTrue(cond3.getExpression().contains("collisionAction.removeActorId"));
		assertTrue(cond3.getExpression().contains("actorBId"));
	}
	
	@Test
	public void test_checkForConditional_insideDoubleForLoop() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEMPLATE);
		atp.parse();
		
		AcceleoConditional cond = atp.getConditionals().get(275).get(0);
		assertEquals("Actor", cond.getModelContext().get(0));
		assertEquals("actor", cond.getModelInstanceContext().get(0));
		assertEquals("isBall", cond.getExpression().get(0));
	}
	
	@Test
	public void test_endingLines_forConditional() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		AcceleoConditional cond = atp.getConditionals().get(118).get(0);
		assertEquals(124, cond.getEndLine().intValue());
	}
	
	@Test
	public void test_checkForConditional_test_if_and_else() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, CONDITIONAL_TEST_TEMPLATE);
		atp.parse();
		
		AcceleoConditional if_cond = atp.getConditionals().get(13).get(0);
		assertEquals(AcceleoConditional.ConditionalType.If, if_cond.getType());
		assertEquals(19, if_cond.getEndLine().intValue());
		
		AcceleoConditional else_cond = atp.getConditionals().get(19).get(0);
		assertEquals(AcceleoConditional.ConditionalType.Else, else_cond.getType());
		assertEquals(21, else_cond.getEndLine().intValue());
	}
	
	@Test
	public void test_checkForConditional_test_if_and_elseif() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, CONDITIONAL_TEST_TEMPLATE);
		atp.parse();
		
		AcceleoConditional if_cond = atp.getConditionals().get(28).get(0);
		assertEquals(AcceleoConditional.ConditionalType.If, if_cond.getType());
		assertEquals(34, if_cond.getEndLine().intValue());
		
		AcceleoConditional elseif_cond = atp.getConditionals().get(34).get(0);
		assertEquals(AcceleoConditional.ConditionalType.Elseif, elseif_cond.getType());
		assertEquals(40, elseif_cond.getEndLine().intValue());
	}
	
	@Test
	public void test_checkForConditional_test_if_elsif_else() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, CONDITIONAL_TEST_TEMPLATE);
		atp.parse();
		
		
		 AcceleoConditional if_cond = atp.getConditionals().get(47).get(0);
		assertEquals(AcceleoConditional.ConditionalType.If, if_cond.getType());
		assertEquals(53, if_cond.getEndLine().intValue());
		
		AcceleoConditional elseif_cond = atp.getConditionals().get(53).get(0);
		assertEquals(AcceleoConditional.ConditionalType.Elseif, elseif_cond.getType());
		assertEquals(59, elseif_cond.getEndLine().intValue());
		
		AcceleoConditional else_cond = atp.getConditionals().get(59).get(0);
		assertEquals(AcceleoConditional.ConditionalType.Else, else_cond.getType());
		assertEquals(61, else_cond.getEndLine().intValue());
	}
	
	@Test
	public void test_checkForConditional_lineNumber_parent_if() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, CONDITIONAL_TEST_TEMPLATE);
		atp.parse();
		
		
		AcceleoConditional if_cond = atp.getConditionals().get(47).get(0);
		assertEquals(47, if_cond.getLineNumber_parent_if().intValue());
		
		AcceleoConditional elseif_cond = atp.getConditionals().get(53).get(0);
		assertEquals(47, elseif_cond.getLineNumber_parent_if().intValue());
		
		AcceleoConditional else_cond = atp.getConditionals().get(59).get(0);
		assertEquals(47, else_cond.getLineNumber_parent_if().intValue());
	}
	
	@Test
	public void test_endingLines_forLoopContainingIf() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, CONDITIONAL_TEST_TEMPLATE);
		atp.parse();
		
		AcceleoLoop loop = atp.getLoops().get(11).get(0);
		assertEquals(23, loop.getEndLine().intValue());
	}
	
	@Test
	public void test_checkForConditional_rootExpression() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_METAMODEL, SCREENFLOW_STATIC_TEST_TEMPLATE);
		atp.parse();
		
		AcceleoConditional cond = atp.getConditionals().get(21).get(0);
		assertEquals("app", cond.getModelInstanceContext().get(0));
		assertEquals("Application", cond.getModelContext().get(0));
		assertEquals("_package", cond.getExpression().get(0));
	}
	
	@Test
	public void test_checkForConditional_withNoExpression() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(ANDROID_METAMODEL, DBADAPTER_TEMPLATE);
		atp.parse();
		
		//line: [if (w.oclIsKindOf(Text))]
		AcceleoConditional cond = atp.getConditionals().get(43).get(0);
		assertEquals("w", cond.getModelInstanceContext().get(0));
		assertEquals("Widget", cond.getModelContext().get(0));
		assertEquals("", cond.getExpression().get(0));
	}
	
	@Test
	public void test_checkConditional_implictModelAccess() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, IMPLICIT_MODEL_TEMPLATE);
		atp.parse();
		
		//line : [if (collisions->first().collisionAction.removeActorId.equalsIgnoreCase('emerald') )]
		AcceleoConditional cond = atp.getConditionals().get(12).get(0);
		assertTrue(cond.getModelContext().contains("ScoreRules"));
		assertTrue(cond.getModelInstanceContext().contains("aScoreRules"));
		assertTrue(cond.getExpression().contains("collisions"));

		cond = atp.getConditionals().get(12).get(0);
		assertTrue(cond.getModelContext().contains("CollisionRule"));
		assertTrue(cond.getModelInstanceContext().contains("")); 
		assertTrue(cond.getExpression().contains("collisionAction.removeActorId")); 
	}
	
	/* Parsing FOR tags ----------------------------------------------- */
	
	@Test
	public void test_checkForLoop() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		AcceleoLoop loop = atp.getLoops().get(172).get(0);
		assertEquals("ScoreRules", loop.getModelContext());
		assertEquals("aScoreRules", loop.getModelInstanceContext());
		assertEquals("TimedRule", loop.getIteratedModel());
		assertEquals("timeRule", loop.getIteratorID());
		assertEquals("timed", loop.getNavigationRelation());
	}
	
	@Test
	public void test_checkForLoop_explicitRoot() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCREENFLOW_STATIC_METAMODEL, SCREENFLOW_STATIC_TEST_TEMPLATE);
		atp.parse();
		
		AcceleoLoop loop = atp.getLoops().get(8).get(0);
		assertEquals("Screen", loop.getIteratedModel());
		assertEquals("s", loop.getIteratorID());
		assertEquals("screens", loop.getNavigationRelation());
	}
	
	@Test
	public void test_checkForLoop_nested() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(LAYOUT_METAMODEL, LAYOUT_TEMPLATE);
		atp.parse();
		
		AcceleoLoop loop = atp.getLoops().get(274).get(0);
		assertEquals("Actor", loop.getModelContext());
		assertEquals("actor", loop.getModelInstanceContext());
		assertEquals("Cell", loop.getIteratedModel());
		assertEquals("cell", loop.getIteratorID());
		assertEquals("cell", loop.getNavigationRelation());
	}
	
	@Test
	public void test_endingLines_forLoop() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		atp.initialize(SCORING_METAMODEL, SCORING_TEMPLATE);
		atp.parse();
		
		AcceleoLoop loop = atp.getLoops().get(103).get(0);
		assertEquals(127, loop.getEndLine().intValue());
	}
	
	@Test
	public void test_getExpressionParts() throws TransformationParserException {
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		List<String> result = atp.getExpressionParts("collisions->first().collisionAction.removeActorId");
		System.out.println(result);
		//assertEquals("collisions->first().collisionAction.removeActorId", result.get(0));
		
		result = atp.getExpressionParts("not background.cameraType.equalsIgnoreCase('none')('none2')");
		System.out.println(result);
		//assertEquals("background.cameraType.equalsIgnoreCase('none')", result.get(1));
		
		result = atp.getExpressionParts("collisionRule.collisionAction.removeActorId.equalsIgnoreCase(collisionRule.actorAId)");
		System.out.println(result);
		
		result = atp.getExpressionParts("28 + 40 * (a.widgets->indexOf(w) - 1)");
		System.out.println(result);
	}
}
