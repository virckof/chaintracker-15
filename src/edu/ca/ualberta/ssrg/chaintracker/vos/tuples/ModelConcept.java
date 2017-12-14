package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

public class ModelConcept extends Concept {
	
	private String modelURI;
	
	private String modelName;
	
	protected String elementID;
	
	protected String attributeID;
	
	
	public String getModelURI() {
		return modelURI;
	}

	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getElementID() {
		return elementID;
	}

	public void setElementID(String elementID) {
		this.elementID = elementID;
	}

	public String getAttributeID() {
		return attributeID;
	}

	public void setAttributeID(String attributeID) {
		this.attributeID = attributeID;
	}
	
	public String toString(){
		return modelName+":"+elementID+":"+attributeID;
	}
	
	@Override
	public String getUniqueID(){
		String stripedAttributeID = attributeID.replaceAll("^\\.", "");
		return sanitazeID(getModelName()+ DELIMINATOR + getElementID() + DELIMINATOR +stripedAttributeID);
	}
	
	private String sanitazeID(String id){
		String sID = id.replace("/", "");
		return sID;
	}
	
	@Override
	public String getTypeName() {
		return "MODEL";
	}
	
	@Override
	public String getTypeCode() {
		return "M";
	}
}
