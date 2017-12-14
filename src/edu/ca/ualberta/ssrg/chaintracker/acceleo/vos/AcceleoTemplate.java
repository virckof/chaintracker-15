package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

public class AcceleoTemplate {
	
	private String visibility;
	private String methodName;
	private String modelParamID;
	private String modelParamInstanceID;
	private String templateName;
	
	public String getVisibility() {
		return visibility;
	}
	
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getModelParamID() {
		return modelParamID;
	}
	
	public void setModelParamID(String modelParamID) {
		this.modelParamID = modelParamID;
	}
	
	public String getModelParamInstanceID() {
		return modelParamInstanceID;
	}
	
	public void setModelParamInstanceID(String modelParamInstanceID) {
		this.modelParamInstanceID = modelParamInstanceID;
	}
	
	public String getTemplateName() {
		return templateName;
	}
	
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
