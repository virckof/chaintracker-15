package edu.ca.ualberta.ssrg.chaintracker.atl.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.ca.ualberta.ssrg.chaintracker.atl.main.ATLTransformationParser;
import edu.ca.ualberta.ssrg.chaintracker.vos.IndirectSource;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2MTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetElement;

public class ATLParserTests {
	// PhyDsl files
	private static final String GAME2LAYOUT_ATL =  "./examples/M2M/Game2Layout.atl.xmi";
	private static final String GAME2SCORING_ATL =  "./examples/M2M/Game2Scoring.atl.xmi";
	
	// PhyDsl Test files (modification of PhyDsl to test certain scenarios)
	private static final String GAME2LAYOUT_TEST_ATL = "./examples/M2M/ATLParserTests/Game2Layout.atl.xmi";
	
	private static final String CLONEDR2CODECLONE_ATL = "examples/ATLExperiments/VisualRepCodeClone/CloneDr2CodeClone/CloneDr2CodeClone.atl.xmi";
	
	private static final String BOOK2PUBLICATION_ATL = "examples/ATLExperiments/Book2Publication/transformations/Book2Publication.atl.xmi";
	
	
	/* Matched Rules Tests ----------------------------------------------- */
	/* type: SimpleOutPatternElement  ----------------------------------------------- */
	
	@Test
	public void SimpleMatchedRule_NoOCLOperations() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2LAYOUT_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "locationActor2Cell");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("Layout", target.getTargetModel());
		assertEquals("Cell", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "actor");
		assertEquals("Location", attribute.getBindings().get(0).getOwner().getSourceElementType());
		assertEquals("/element", attribute.getBindings().get(0).getImplicitBindings());
		
		attribute = TestUtils.GetAttributeByName(attributes, "x");
		assertEquals("Location", attribute.getBindings().get(0).getOwner().getSourceElementType());
		assertEquals("/x/cordinate", attribute.getBindings().get(0).getImplicitBindings());
	}
	
	@Test
	public void SimpleMatchedRule_WithOCLOperations() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2LAYOUT_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "main");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("Layout", target.getTargetModel());
		assertEquals("Layout", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "height");
		assertEquals("Model", attribute.getBindings().get(0).getOwner().getSourceElementType());
		assertEquals("/y/cordinate/grid/layoutSection/game", attribute.getBindings().get(0).getImplicitBindings());
		
		attribute = TestUtils.GetAttributeByName(attributes, "cells");
		assertEquals("Model", attribute.getBindings().get(0).getOwner().getSourceElementType());
		assertEquals("/locations/layoutSection/game", attribute.getBindings().get(0).getImplicitBindings());
	}
	
	@Test
	public void SimpleMatchedRule_WithIterator() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2SCORING_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "main");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("Scoring", target.getTargetModel());
		assertEquals("ScoreRules", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "collisions");
		assertEquals("ScoreRule", attribute.getBindings().get(0).getOwner().getSourceElementType());
		assertEquals("/definition/scoreDefinition", attribute.getBindings().get(0).getImplicitBindings());
		
		attribute = TestUtils.GetAttributeByName(attributes, "timed");
		assertEquals("ScoreRule", attribute.getBindings().get(0).getOwner().getSourceElementType());
		assertEquals("/definition/scoreDefinition", attribute.getBindings().get(0).getImplicitBindings());
	}
	
	@Test
	public void SimpleMatchedRule_WithComplexIterator() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(CLONEDR2CODECLONE_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "CDUnit2CloneUnit");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("CodeClone", target.getTargetModel());
		assertEquals("CloneUnit", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "file");

		ArrayList<SourceAttribute> dependencies = attribute.getFunctionBindings().get(0).getDependencies();
		
		assertEquals("CDUnit", dependencies.get(1).getOwner().getSourceElementType());
		assertEquals("/fileName", dependencies.get(1).getImplicitBindings());

		assertEquals("File", dependencies.get(0).getOwner().getSourceElementType());
		assertEquals("/fileName", dependencies.get(0).getImplicitBindings());
	}
	
	@Test
	public void Iterator_WithHelperContext() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(BOOK2PUBLICATION_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "Book2Publication");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("Publication", target.getTargetModel());
		assertEquals("Publication", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "nbPages");

		ArrayList<SourceAttribute> dependencies = attribute.getFunctionBindings().get(0).getDependencies();
		
		assertEquals("Book", dependencies.get(0).getOwner().getSourceElementType());
		assertEquals("/chapters", dependencies.get(0).getImplicitBindings());
		
		assertEquals("Book", dependencies.get(1).getOwner().getSourceElementType());
		assertEquals("/nbPages/chapters", dependencies.get(1).getImplicitBindings());
	}
	
	@Test
	public void SimpleMatchedRule_With2SourceAttributes() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2LAYOUT_TEST_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "locationActor2Cell");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("Layout", target.getTargetModel());
		assertEquals("Cell", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "id");
		assertEquals("Location", attribute.getBindings().get(0).getOwner().getSourceElementType());
		assertEquals("/x/cordinate", attribute.getBindings().get(0).getImplicitBindings());
		assertEquals("Location", attribute.getBindings().get(1).getOwner().getSourceElementType());
		assertEquals("/y/cordinate", attribute.getBindings().get(1).getImplicitBindings());
	}
	
	/* Helper Tests ----------------------------------------------- */
	
	@Test
	public void Helepr_WithParameter() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2LAYOUT_TEST_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "locationTarget2Cell");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("Layout", target.getTargetModel());
		assertEquals("Cell", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "id");
		IndirectSource functionBinding = attribute.getFunctionBindings().get(0);
		assertEquals("getCellID", functionBinding.getHelperName());
		assertEquals("Location", functionBinding.getParameters().get(0).getSourceElementType());
		
		ArrayList<SourceAttribute> dependencies = functionBinding.getDependencies();
		
		assertEquals("Location", dependencies.get(0).getOwner().getSourceElementType());
		assertEquals("/x/cordinate", dependencies.get(0).getImplicitBindings());

		assertEquals("Location", dependencies.get(1).getOwner().getSourceElementType());
		assertEquals("/y/cordinate", dependencies.get(1).getImplicitBindings());	
	}
	
	@Test
	public void Helper_WithLetExp() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2SCORING_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "ScoreRule2CollisionRule");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		assertEquals("Scoring", target.getTargetModel());
		assertEquals("CollisionRule", target.getTargetElementType());
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "actorAID");
		assertEquals("getCollisionAID", attribute.getFunctionBindings().get(0).getHelperName());
		
		ArrayList<SourceAttribute> dependencies = attribute.getFunctionBindings().get(0).getDependencies();
		
		assertEquals("ScoreRule", dependencies.get(0).getOwner().getSourceElementType());
		assertEquals("/definition/scoreDefinition", dependencies.get(0).getImplicitBindings());
		
		assertEquals("ContactScore", dependencies.get(1).getOwner().getSourceElementType());
		assertEquals("/name/elementA", dependencies.get(1).getImplicitBindings());
	}
	
	@Test
	public void TestSolveVariableReference() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2LAYOUT_TEST_ATL);
		parser.initialize();
		
		SourceElement elemnt = parser.solveVariableReference("/0/@elements.5/@definition/@feature/@parameters.0", null);
		assertEquals("Location", elemnt.getSourceElementType());
		assertEquals("loc", elemnt.getSourceModelVariable());
		assertEquals("Phydsl", elemnt.getSourceModel());
	}
	
	@Test
	public void TestIndirectSource_DontReferenceItself() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2SCORING_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "Effect2Action");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "points");
		IndirectSource indirectSrc = attribute.getFunctionBindings().get(0);
		assertEquals(0, indirectSrc.getFunctionDependencies().size());
	}
	
	@Test
	public void Test_DontAddDuplicateIndirectSourceDependencies() {
		ATLTransformationParser parser = new ATLTransformationParser();
		parser.setTransformationFile(GAME2SCORING_ATL);
		parser.initialize();
		ArrayList<M2MTransformation> transformations = parser.processMatchedRules();
		
		M2MTransformation transformation = TestUtils.GetTransformationByName(transformations, "Effect2Action");
		
		SourceElement source = transformation.getSourceElement();
		TargetElement target = transformation.getTargetElements().get(0);
		
		ArrayList<TargetAttribute> attributes = target.getAttributes();
		
		TargetAttribute attribute = TestUtils.GetAttributeByName(attributes, "points");
		IndirectSource indirectSrc = attribute.getFunctionBindings().get(0);
		assertEquals(2, indirectSrc.getDependencies().size());
	}
}
