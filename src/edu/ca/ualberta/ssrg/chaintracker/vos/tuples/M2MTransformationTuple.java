package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

import java.util.ArrayList;


public class M2MTransformationTuple {
	
	private String tansformationURI;
	
	private String tansformationID;
	
	private String ruleID;
	
	private ModelConcept source;
	
	private ModelConcept target;
	
	// ex. name of Helper if any
	private String implicitReferenceName;
	
	private String transformationFile;
	
	private ArrayList<IndirectSourceConcept> indirectSourceConcept = new ArrayList<>();

	public String getTansformationID() {
		return tansformationID;
	}

	public void setTansformationID(String tansformationID) {
		this.tansformationID = tansformationID;
	}

	public String getRuleID() {
		return ruleID;
	}

	public void setRuleID(String ruleID) {
		this.ruleID = ruleID;
	}

	public String getTansformationURI() {
		return tansformationURI;
	}

	public void setTansformationURI(String tansformationURI) {
		this.tansformationURI = tansformationURI;
	}

	public ModelConcept getSource() {
		return source;
	}

	public void setSource(ModelConcept source) {
		this.source = source;
	}

	public ModelConcept getTarget() {
		return target;
	}

	public void setTarget(ModelConcept target) {
		this.target = target;
	}
	
	public String getImplicitReferenceName() {
		return implicitReferenceName;
	}

	public void setImplicitReferenceName(String implicitReferenceName) {
		this.implicitReferenceName = implicitReferenceName;
	}

	public String getTransformationFile() {
		return transformationFile;
	}

	public void setTransformationFile(String transformationFile) {
		this.transformationFile = transformationFile;
	}

	public ArrayList<IndirectSourceConcept> getIndirectSourceConcept() {
		return indirectSourceConcept;
	}

	public void setIndirectSourceConcept(
			ArrayList<IndirectSourceConcept> indirectSourceConcept) {
		this.indirectSourceConcept = indirectSourceConcept;
	}

	public String toString(){
		String tuplS = "<<< " + ruleID + ">>> \n" +  "["+source.toString()+" ----> "+target.toString()+"] \n";
		
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
	
	public String toStringHash(){
		String tuplS = ruleID + ", " + source.hashCode() + ", " + target.hashCode() +"\n";
		
		for(IndirectSourceConcept isc : indirectSourceConcept){
			
			tuplS = tuplS+ ruleID + "IND, " + isc.hashCode() + ", " + target.hashCode()+"\n";
		}
		return tuplS;
	}
	

}
