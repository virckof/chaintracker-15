package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

public class ModelAccess {

	private ModelContext modelContext;
	private String modelAttributeExpression;
	
	public ModelContext getModelContext() {
		return modelContext;
	}
	
	public void setModelContext(ModelContext modelContext) {
		this.modelContext = modelContext;
	}
	
	
	public String getModelAttributeExpression() {
		return modelAttributeExpression;
	}
	
	public void setModelAttributeExpression(String modelAttributeExpression) {
		this.modelAttributeExpression = modelAttributeExpression;
	}
}
