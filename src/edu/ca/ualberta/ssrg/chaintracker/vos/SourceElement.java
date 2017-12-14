package edu.ca.ualberta.ssrg.chaintracker.vos;

import java.util.ArrayList;

public class SourceElement {
	
	/**
	 * Name of the variable "instance" of the model element navigating
	 */
	private String sourceModelVariable;
	
	/**
	 * element type according to the input model
	 */
	private String sourceElementType;
	
	/**
	 * Source model name (i.e. Scoring for Scoring.ecore)
	 */
	private String sourceModel;
	
	/**
	 * Source attributes used in the injection, in the case of M2T transformation this is always one.
	 */
	private ArrayList<SourceAttribute> attributes;
	
	/**
	 * Source model URI (i.e. http://ualberta.edu.cs.ssrg.phy.scoring for Scoring.ecore)
	 */
	private String modelURI;
	
	public SourceElement() {
		attributes = new ArrayList<SourceAttribute>();
	}

	public String getSourceModelVariable() {
		return sourceModelVariable;
	}

	public void setSourceModelVariable(String sourceModelVariable) {
		this.sourceModelVariable = sourceModelVariable;
	}

	public String getSourceElementType() {
		return sourceElementType;
	}

	public void setSourceElementType(String sourceElementType) {
		this.sourceElementType = sourceElementType;
	}


	public ArrayList<SourceAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<SourceAttribute> attributes) {
		this.attributes = attributes;
	}
	
	
	public String getSourceModel() {
		return sourceModel;
	}

	public void setSourceModel(String sourceModel) {
		this.sourceModel = sourceModel;
	}

	
	public String getModelURI() {
		return modelURI;
	}

	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}
	

	public void addSourceAttribute(SourceAttribute e){
		if(attributes!=null){
			attributes.add(e);
		}
		else{
			attributes = new ArrayList<>();
			attributes.add(e);
		}
	}
	
	
	public String toReadableString(){
		return toReadableString("");
	}
	
	public String toReadableString(String indent) {
		if (indent == null) {
			indent = "";
		}
		
		String str = indent + "[\n";
		str += indent + "    Source Variable: " + sourceModelVariable + "\n";
		str += indent + "    Model: " + sourceModel + "\n";
		str += indent + "    Type: " + sourceElementType + "\n";
		
		if (attributes.size() > 0) {
			str += indent + "    Source Attributes: [\n";
			for(SourceAttribute sa: attributes){
				str += sa.toReadableString(indent + "       ");
			}
			str += indent + "    ]\n";
		}
		
		str += indent + "]\n";
		
		return str;
	}
	
	public boolean equals(SourceElement other) {
		return (this.getSourceModel().equals(other.getSourceModel())
				&& this.getSourceElementType().equals(other.getSourceElementType()));
	}
}
