package edu.ca.ualberta.ssrg.chaintracker.vos;

import java.util.ArrayList;

/**
 * Represents a transformation rule
 * @author Victor
 */
public class M2MTransformation {
	
	private String ownerModule;
	
	private String transformationFilename;
	
	private String name;
	
	private ArrayList<TargetElement> targetElements;
	
	private SourceElement sourceElement;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<TargetElement> getTargetElements() {
		return targetElements;
	}

	public void setTargetElements(ArrayList<TargetElement> targetElements) {
		this.targetElements = targetElements;
	}

	public SourceElement getSourceElement() {
		return sourceElement;
	}

	public void setSourceElement(SourceElement sourceElement) {
		this.sourceElement = sourceElement;
	}
	
		
	public String getTransformationFilename() {
		return transformationFilename;
	}

	public void setTransformationFilename(String transformationFilename) {
		this.transformationFilename = transformationFilename;
	}
	

	public String getOwnerModule() {
		return ownerModule;
	}

	public void setOwnerModule(String ownerModule) {
		this.ownerModule = ownerModule;
	}

	public String toReadableString(){
		String t = "Transformation: "+ name + " {\n" +
				    "    Source Element:\n" + sourceElement.toReadableString("    ") + "\n" +
				    "    Target Elements: [\n" ;
					for(TargetElement te : targetElements){
						t += te.toReadableString("        ") +"\n" ;
					}
				    t += "    ]\n";
				    t += "}";
		
		return t;
				   
	}

}
