package edu.ca.ualberta.ssrg.chaintracker.vos;

import java.util.ArrayList;

public class TargetElement {
	
	private String targetModellVariable;
	
	private String targetElementType;
	
	private String targetModel;
	
	private String modelURI;
	
	private ArrayList<TargetAttribute> attributes;

	String getTargetModellVariable() {
		return targetModellVariable;
	}

	public void setTargetModellVariable(String targetModellVariable) {
		this.targetModellVariable = targetModellVariable;
	}

	public String getTargetElementType() {
		return targetElementType;
	}

	public void setTargetElementType(String targetElementType) {
		this.targetElementType = targetElementType;
	}


	public ArrayList<TargetAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<TargetAttribute> attributes) {
		this.attributes = attributes;
	}

	public String getTargetModel() {
		return targetModel;
	}

	public void setTargetModel(String targetModel) {
		this.targetModel = targetModel;
	}
	
	public String getModelURI() {
		return modelURI;
	}

	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}

	public String toReadableString(){
		return toReadableString("");
	}
	
	public String toReadableString(String indent) {
		if (indent == null) {
			indent = "";
		}
		
		String str = indent + "[\n";
		str += indent + "    Target Variable: " + targetModellVariable + "\n";
		str += indent + "    Model: " + targetModel + "\n";
		str += indent + "    Type: " + targetElementType + "\n";
		
		if (attributes.size() > 0) {
			str += indent + "    Source Attributes: [\n";
			for(TargetAttribute ta: attributes){
				str += ta.toReadableString(indent + "       ");
			}
		
			str += indent + "    ]\n";
		}
		
		str += indent + "]\n";
		
		return str;
		
	}
}
