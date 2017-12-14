package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.util.ArrayList;

import org.apache.tools.ant.types.resources.comparators.Size;

import edu.ca.ualberta.ssrg.chaintracker.ecore.EcoreSolver;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2TTransformation;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetTextualElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.IndirectSourceConcept;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.M2MTransformationTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.M2TTransformationTuple;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.ModelConcept;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.TemplateConcept;

/**
 * M2TTupleExtractor converts M2TTransformations into
 * M2TTuples. M2TTuples have knowledge of indirect model accesses.
 *
 */
public class M2TTupleExtractor {
	
	
	private ArrayList<M2TTransformation> transformations;
	
	private ArrayList<M2TTransformationTuple> tuples;
	
	private EcoreSolver ECORESolver;

	private String transformationFile;
	
	public M2TTupleExtractor (ArrayList<M2TTransformation> transformationsP, String transformationFileP, EcoreSolver solver){
		transformations = transformationsP;
		tuples = new ArrayList<>();
		ECORESolver = solver;
		transformationFile=transformationFileP;
	}
	
	public ArrayList<M2TTransformationTuple> extractTuples(){
		
		for(M2TTransformation t : transformations){
			
			//Some control structures such as else, and loops do not have enough context information
			// for the current ecore parser to solve the origins. We check for the ones that do, and proceed.
			if(!t.getSourceElement().getAttributes().get(0).getImplicitBindings().equals("."))
			{
				TargetTextualElement te = t.getTargetElement();
				SourceElement se = t.getSourceElement();
				SourceAttribute sa = se.getAttributes().get(0); //Precond M2TTransformations only one SA.
				
				M2TTransformationTuple tuple = new M2TTransformationTuple();
				ModelConcept sc = new ModelConcept();
				sc.setModelName(se.getSourceModel());
				sc.setElementID(se.getSourceElementType());
				sc.setAttributeID(sa.getImplicitBindings());
				sc.setModelURI(se.getModelURI());
				
				TemplateConcept ttc = new TemplateConcept();
				ttc.setTemplateExpressionType(te.getTargetExpressionType());
				ttc.setTemplateLine(te.getTargetLine());
				ttc.setTemplateName(te.getTemplateName());
				ttc.setFilePath(transformationFile);
				
				
				String seq[] = sa.getImplicitBindings().split("\\.");
					
				
				String[] seqReverse= new String[seq.length] ;
				
				
				int j=1;
				seqReverse[0]="";
				for(int i=seq.length-1; i>0; i--){
					
					seqReverse[j] = seq[i];
					j++;
				}
				
			
				
				//System.out.println("--->" +sa.getCompositeName());
				
				ArrayList<IndirectSourceConcept> iscs = new ArrayList<>();
				
				if(seq.length > 2){ // That means it has a sequence/ navigation call
	
					ECORESolver.tokenizeCallSequence(seqReverse, se.getSourceElementType(), 
							se.getSourceModel(), se.getModelURI(), iscs);
				}
				
				tuple.setSource(sc);
				tuple.setTarget(ttc);
				tuple.setIndirectSourceConcept(iscs);
				tuples.add(tuple);
			}
			
			
			
		}
		return tuples;

	}
	

	public void printTuples(){
		for(M2TTransformationTuple t : tuples){
			System.out.println(t.toString());
		}
	}
	
	
	
}
