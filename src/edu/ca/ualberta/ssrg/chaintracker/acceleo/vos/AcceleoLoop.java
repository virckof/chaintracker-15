package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

public class AcceleoLoop extends AcceleoExpression {

	private String modelContext;
	private String modelInstanceContext;
	private String iteratorID;
	private String iteratedModel;
	private String navigationRelation;
	
	public String getModelContext() {
		return modelContext;
	}
	
	public void setModelContext(String modelContex) {
		this.modelContext = modelContex;
	}
	
	public String getModelInstanceContext() {
		return modelInstanceContext;
	}
	
	public void setModelInstanceContext(String modelInstanceContex) {
		this.modelInstanceContext = modelInstanceContex;
	}
	
	public String getIteratedModel() {
		return iteratedModel;
	}
	
	public void setIteratedModel(String iteratedModel) {
		this.iteratedModel = iteratedModel;
	}
	
	public String getNavigationRelation() {
		return navigationRelation;
	}
	
	public void setNavigationRelation(String navigationRelation) {
		this.navigationRelation = navigationRelation;
	}
	
	public String getIteratorID() {
		return iteratorID;
	}
	
	public void setIteratorID(String iteratorID) {
		this.iteratorID = iteratorID;
	}

	@Override
	public String getExpressionString() {
		return getIteratedModel() + " " + getIteratorID() + " (" + getTypeString() + ")";
	}
	
	@Override
	public String getTypeString() {
		return "Loop";
	}
}
