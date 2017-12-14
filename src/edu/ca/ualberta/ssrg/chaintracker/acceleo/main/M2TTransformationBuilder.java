package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoConditional;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoLoop;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoModule;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoNonOCLExpresion;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2TTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetTextualElement;

/**
 * M2TTransformationBuilder uses information from AcceleoTransformationParser
 * to create M2TTransformations from model accesses by Acceleo loops,
 * conditional and simple expressions.
 * 
 * The source element comes from the model, and the target element comes from the template.
 * 
 * @see AcceleoTransformationParser
 */
public class M2TTransformationBuilder {
	
	public enum M2TType {
		Loop,
		Conditional,
		Simple
	}
			
	/**
	 * Given an already parsed AcceleoTransformationParser, turns all the information
	 * about the Acceleo loops, conditionals and simple expressions from the parser into
	 * M2TTransformation objects.
	 * 
	 * @param atp -  AcceleoTransformationParser that has already initialized & parsed
	 * @return list of M2TTransformation objects
	 */
	public List<M2TTransformation> getM2TTransformations(AcceleoTransformationParser atp) {
		
		List<M2TTransformation> transformations = new ArrayList<M2TTransformation>();
		
		// Get each type of M2TTransformation objects: LOOP, CONDITIONAL, SIMPLE
		for (M2TType type : M2TType.values()) {
			transformations.addAll(getM2TTransformations(atp, type));
		}
		
		return transformations;
	}
	
	/**
	 * Given an already parsed AcceleoTransformationParser, turns all the information
	 * about the specified type (loops, conditionals or simple expressions) 
	 * from the parser into M2TTransformation objects.
	 * 
	 * @param atp -  AcceleoTransformationParser that has already initialized & parsed
	 * @param type
	 * @return
	 */
	public List<M2TTransformation> getM2TTransformations(AcceleoTransformationParser atp, M2TType type) {
		
		List<M2TTransformation> transformations = new ArrayList<M2TTransformation>();
		
		// Get transformation depending on the type
		switch(type) {
			case Loop :
				transformations = getLoopTransformations(atp.getLoops());
				break;
			case Conditional :
				transformations = getConditionalTransformations(atp.getConditionals());
				break;
			case Simple :
				transformations = getSimpleTransformations(atp.getSimpleExpressions());
				break;
			default :
				break;
		}
		
		// Set information that is the same for all the transformations
		for (M2TTransformation t : transformations) {
			t.setOwnerTemplate(atp.getTransformationFile());
			t.getSourceElement().setSourceModel(atp.getModelName());
			
			// Assumption one module
			AcceleoModule m = (AcceleoModule) ((List<AcceleoModule>)(atp.getModules().values().toArray()[0])).get(0);
			t.getSourceElement().setModelURI(atp.getInMetamodelPath());
			
			int lineNumber = Integer.parseInt(t.getTargetElement().getTargetLine());
			String templateName = atp.getTemplateName(lineNumber);
			t.getTargetElement().setTemplateName(templateName);
		}
		
		return transformations;
	}

	/**
	 * Given a table of AcceleoLoops, turns each AcceleoLoop into a
	 * M2TTransformation object.
	 * 
	 * @param loopsTable
	 * @return
	 */
	private List<M2TTransformation> getLoopTransformations(Hashtable<Integer, ArrayList<AcceleoLoop>> loopsTable) {
		
		List<M2TTransformation> transformations = new ArrayList<M2TTransformation>();
		
		for (Entry<Integer, ArrayList<AcceleoLoop>> entry : loopsTable.entrySet()) {
			ArrayList<AcceleoLoop> loops = entry.getValue();
			
			// Create 1 M2TTransformation object per AcceleoLoop
			for (AcceleoLoop loop : loops) {
				M2TTransformation t = new M2TTransformation();
				t.setControl(true);

				SourceElement sourceElement = new SourceElement();
				// element type is the AcceleoLoop's model context
				sourceElement.setSourceElementType(loop.getModelContext());
				// no SourceModel variable for loops
				
				// attribute is the AcceleoLoop's navigation relation
				SourceAttribute attribute = new SourceAttribute();
				attribute.setOwner(sourceElement);
				attribute.setImplicitBindings("." + loop.getNavigationRelation());
				sourceElement.addSourceAttribute(attribute);

				TargetTextualElement targetElement = new TargetTextualElement();
				targetElement.setTargetExpressionType(M2TType.Loop.toString());
				// key is the template line -> set as target line
				targetElement.setTargetLine(entry.getKey().toString());
				
				t.setSourceElement(sourceElement);
				t.setTargetElement(targetElement);
				transformations.add(t);
			}
		}
		
		return transformations;
	}
	
	/**
	 * Given a table of AcceleoConditional, turns each AcceleoConditional into a
	 * M2TTransformation object.
	 * 
	 * @param conditionalsTable
	 * @return
	 */
	private List<M2TTransformation> getConditionalTransformations(Hashtable<Integer, ArrayList<AcceleoConditional>> conditionalsTable) {

		List<M2TTransformation> transformations = new ArrayList<M2TTransformation>();
		
		for (Entry<Integer, ArrayList<AcceleoConditional>> entry : conditionalsTable.entrySet()) {
			ArrayList<AcceleoConditional> conditionals = entry.getValue();
			
			for (AcceleoConditional cond : conditionals) {
				// For each model dependency in the conditional, create 1 M2TTransformation object
				// Ex. The following Acceleo conditional: 
				// 		[if (collisionRule.collisionAction.removeActorId.equalsIgnoreCase(collisionRule.actorBId))]
				// has 2 model dependencies: collisionRule.collisionAction.removeActorId & collisionRule.actorBId)
				for (int i = 0; i < cond.getExpression().size(); i++) {
					
					M2TTransformation t = new M2TTransformation();
					t.setControl(true);
					
					SourceElement sourceElement = new SourceElement();
					// element type is the AcceleoConditional's model context for model dependency i
					sourceElement.setSourceElementType(cond.getModelContext().get(i));
					// model variable is the AcceleoConditional's model context instance
					sourceElement.setSourceModelVariable(cond.getModelInstanceContext().get(i));
					
					// attribute is the AcceleoConditional's expression for model dependency i
					SourceAttribute attribute = new SourceAttribute();
					attribute.setOwner(sourceElement);
					attribute.setImplicitBindings("." + cond.getExpression().get(i));
					sourceElement.addSourceAttribute(attribute);
	
					TargetTextualElement targetElement = new TargetTextualElement();
					targetElement.setTargetExpressionType(M2TType.Conditional.toString());
					// key is the template line -> set as target line
					targetElement.setTargetLine(entry.getKey().toString());
					
					t.setSourceElement(sourceElement);
					t.setTargetElement(targetElement);
					transformations.add(t);
				}
			}
		}
		
		return transformations;
	}

	/**
	 * Given a table of AcceleoNonOCLExpresion, turns each AcceleoNonOCLExpresion into a
	 * M2TTransformation object.
	 * 
	 * @param simpleExpressionsTable
	 * @return
	 */
	private List<M2TTransformation> getSimpleTransformations(Hashtable<Integer, ArrayList<AcceleoNonOCLExpresion>> simpleExpressionsTable) {
		
		List<M2TTransformation> transformations = new ArrayList<M2TTransformation>();
		
		for (Entry<Integer, ArrayList<AcceleoNonOCLExpresion>> entry : simpleExpressionsTable.entrySet()) {
			ArrayList<AcceleoNonOCLExpresion> expressions = entry.getValue();
			
			// Create 1 M2TTransformation object per AcceleoNonOCLExpresion
			for (AcceleoNonOCLExpresion exp : expressions) {
				M2TTransformation t = new M2TTransformation();
				t.setControl(false);
				
				SourceElement sourceElement = new SourceElement();
				// element type is the AcceleoNonOCLExpresion's model context
				sourceElement.setSourceElementType(exp.getModelContext());
				// model variable type is the AcceleoNonOCLExpresion's model context instance
				sourceElement.setSourceModelVariable(exp.getModelInstanceContext());
				
				// attribute is the AcceleoNonOCLExpresion's expression 
				SourceAttribute attribute = new SourceAttribute();
				attribute.setOwner(sourceElement);
				attribute.setImplicitBindings("." + exp.getExpression());
				sourceElement.addSourceAttribute(attribute);

				TargetTextualElement targetElement = new TargetTextualElement();
				targetElement.setTargetExpressionType(M2TType.Simple.toString());
				// key is the template line -> set as target line
				targetElement.setTargetLine(entry.getKey().toString());
				
				t.setSourceElement(sourceElement);
				t.setTargetElement(targetElement);
				transformations.add(t);
			}
		}
		
		return transformations;
	}
}
