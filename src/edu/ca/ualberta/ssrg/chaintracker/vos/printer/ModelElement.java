package edu.ca.ualberta.ssrg.chaintracker.vos.printer;

public class ModelElement {
	
	private String metamodel;
	
	private String modelElement;
	
	private String member;
	
	private String type;
	
	private boolean isRelationship;
	
	private String supertype;
	
	private boolean isEmpty;

	
	public boolean isEmpty() {
		return isEmpty;
	}


	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}


	public String getSupertype() {
		return supertype;
	}


	public void setSupertype(String supertype) {
		this.supertype = supertype;
	}


	public String getMetamodel() {
		return metamodel;
	}


	public void setMetamodel(String metamodel) {
		this.metamodel = metamodel;
	}


	public String getModelElement() {
		return modelElement;
	}


	public void setModelElement(String modelElement) {
		this.modelElement = modelElement;
	}


	public String getMember() {
		return member;
	}


	public void setMember(String member) {
		this.member = member;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}



	public boolean isRelationship() {
		return isRelationship;
	}


	public void setRelationship(boolean isRelationship) {
		this.isRelationship = isRelationship;
	}


	public String getSimpleID(){
		return getMetamodel()+";"+getModelElement()+";"+getMember();
	}
	
	public String getConceptType() {
		return "MODEL";
	}

}
