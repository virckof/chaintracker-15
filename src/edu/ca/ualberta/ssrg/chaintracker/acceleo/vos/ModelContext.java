package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

public class ModelContext {
	private String modelContextType;
	private String modelContextInstance;
	
	public ModelContext() {
		
	}
	
	public ModelContext(String type, String instance)
	{
		this.modelContextType = type; 
		this.modelContextInstance = instance;
	}
	
	
	public String getModelContextType() {
		return modelContextType;
	}
	public void setModelContextType(String modelContextType) {
		this.modelContextType = modelContextType;
	}
	
	
	public String getModelContextInstance() {
		return modelContextInstance;
	}
	
	public void setModelContextInstance(String modelContextInstance) {
		this.modelContextInstance = modelContextInstance;
	}
}
