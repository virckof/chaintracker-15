package edu.ca.ualberta.ssrg.chaintracker.atl.main;

import java.util.ArrayList;

import edu.ca.ualberta.ssrg.chaintracker.ecore.EcoreSolver;
import edu.ca.ualberta.ssrg.chaintracker.vos.IndirectSource;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2MTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.IndirectSourceConcept;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.M2MTransformationTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.ModelConcept;


public class M2MTupleExtractor {
	
	ArrayList<M2MTransformation> transformations;
	
	ArrayList<M2MTransformationTuple> tuples;
	
	ATLTransformationParser parser;
	
	EcoreSolver solver;
	
	String transformationFile;
	
	public M2MTupleExtractor (ArrayList<M2MTransformation>  transformationsP, ATLTransformationParser parserP, EcoreSolver solverP, String transformationFile){
		transformations = transformationsP;
		this.transformationFile = transformationFile;
		tuples = new ArrayList<>();
		parser = parserP;
		solver = solverP;
	}
	
	
	public ArrayList<M2MTransformationTuple> extractTuples(){
		
		for(M2MTransformation t : transformations){
			
			//System.out.println(t.toReadableString());
					
			ArrayList<TargetElement> tElems = t.getTargetElements();
			
			for(TargetElement tEl : tElems){
				
				ArrayList<TargetAttribute> tAtts = tEl.getAttributes();
				
				for(TargetAttribute ta : tAtts){
					
					for (SourceAttribute sa : ta.getBindings()) {
						M2MTransformationTuple tuple = getTuple(t, tEl, ta, sa);
						
						if (tuple != null) {
							tuples.add(tuple);	
						}	
					}
					
					for (IndirectSource indirectSource : ta.getFunctionBindings()) {
						
						tuples.addAll(getTuplesFromIndirectSource(indirectSource, t, tEl, ta));
						
					}
				}
			}
		}
		return tuples;
	}
	
	private ArrayList<M2MTransformationTuple> getTuplesFromIndirectSource(IndirectSource indirectSource, M2MTransformation t, TargetElement tEl, TargetAttribute ta) {
		ArrayList<M2MTransformationTuple> tuples = new ArrayList<>();
		
		for (SourceAttribute sa : indirectSource.getDependencies()) {
			M2MTransformationTuple tuple = getTuple(t, tEl, ta, sa);
			
			if (tuple != null) {
				tuple.setImplicitReferenceName(indirectSource.getHelperName());
				tuples.add(tuple);
			}
		}
		
		
		//TODO: how to deal with implicit reference name for the indirect source's function bindings
		for (IndirectSource ids : indirectSource.getFunctionDependencies()) {
			ArrayList<M2MTransformationTuple> tuplesFrom = getTuplesFromIndirectSource(ids, t, tEl, ta);
		
			tuples.addAll(tuplesFrom);	
		}
		
		return tuples;
	}
	
	private M2MTransformationTuple getTuple(M2MTransformation t, TargetElement tEl, TargetAttribute ta, SourceAttribute sa) {
		M2MTransformationTuple tuple = new M2MTransformationTuple();
		tuple.setTransformationFile(transformationFile);
		tuple.setTansformationURI(t.getTransformationFilename());
		tuple.setTansformationID(t.getOwnerModule());
		tuple.setRuleID(t.getName());
		
		ModelConcept tc = new ModelConcept();
		tc.setModelName(tEl.getTargetModel());
		tc.setModelURI(parser.solveModeURI(tEl.getTargetModel()));
		tc.setElementID(ta.getOwner().getTargetElementType());
		tc.setAttributeID(ta.getName());
	
		
		ModelConcept sc = new ModelConcept();
		SourceElement sourceElement = sa.getOwner();
		sc.setModelName(sourceElement.getSourceModel());
		sc.setModelURI(parser.solveModeURI(sourceElement.getSourceModel()));
		sc.setElementID(sourceElement.getSourceElementType());
		sc.setAttributeID(sa.getImplicitBindings());
		
		// aren't handling cases where the target attribute is binded to another attribute of itself
		if (tEl.getTargetModel().equals(sourceElement.getSourceModel())) {
			return null;
		}
		
		String seq[] = sa.getImplicitBindings().split("/");
		/*System.out.println("--->" +sa.getImplicitBindings());
		for(String s: seq){
			System.out.println("seq: " +s);
		}*/
		ArrayList<IndirectSourceConcept> iscs = new ArrayList<>();
		
		if(seq.length > 2){ // That means it has a sequence/ navigation call
			/*System.out.println("---------------------------------");
			System.out.println("SET:" +sourceElement.getSourceElementType());
			System.out.println("SM:" + sourceElement.getSourceModel());
			System.out.println("URI:" + parser.solveModeURI(sourceElement.getSourceModel()));
			System.out.println("---------------------------------");
			
			System.out.println(solver.getModelFile());*/
			solver.tokenizeCallSequence(seq, sourceElement.getSourceElementType(), sourceElement.getSourceModel(),
					parser.solveModeURI(sourceElement.getSourceModel()), iscs);
		}
		
		tuple.setSource(sc);
		tuple.setTarget(tc);
		tuple.setIndirectSourceConcept(iscs);
		
		return tuple;
	}
	
	public void printTuples(){
		for(M2MTransformationTuple t : tuples){
			System.out.println(t.toString());
		}
	}
}
