package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

import java.util.ArrayList;

import edu.ca.ualberta.ssrg.chaintracker.vos.M2TTransformation;

public class M2TTransformationTuple {

	//The source model concept
	private ModelConcept source;
	
	//The target template concept (line id and expression of the template)
	private TemplateConcept target;
	
	//Possible indirect source concepts relationships or other navigated elements
	private ArrayList<IndirectSourceConcept> indirectSourceConcept = new ArrayList<>();

	public ModelConcept getSource() {
		return source;
	}

	public void setSource(ModelConcept source) {
		this.source = source;
	}

	public TemplateConcept getTarget() {
		return target;
	}

	public void setTarget(TemplateConcept target) {
		this.target = target;
	}

	public ArrayList<IndirectSourceConcept> getIndirectSourceConcept() {
		return indirectSourceConcept;
	}

	public void setIndirectSourceConcept(
			ArrayList<IndirectSourceConcept> indirectSourceConcept) {
		this.indirectSourceConcept = indirectSourceConcept;
	}
	
	public String toString(){
		
		String tuplS = "<<< File: "+ target.getFilePath() + " -Template Module: " + target.getTemplateName() + ">>>" + "\n" 
		+"["+source.toString()+" ----> "+target.toString()+"] \n";
		for(IndirectSourceConcept isc : indirectSourceConcept){
			
			if(isc.getType().equals(IndirectSourceConcept.MODEL_ELEMENT_ATTRIBUTE)){
				tuplS = tuplS + "   [INDIRECT: <"+ isc.getType() +">"+ isc.toString() +" ----> "+target.toString()+ "] \n";
			}
			else if (isc.getType().equals(IndirectSourceConcept.MODEL_ELEMENT_REFERENCE)){
				tuplS = tuplS + "   [INDIRECT: <"+ isc.getType() +">"+ isc.toStringReference() +" ----> "+target.toString()+ "] \n";
			} 
		}
		return tuplS;
	}
	
}
