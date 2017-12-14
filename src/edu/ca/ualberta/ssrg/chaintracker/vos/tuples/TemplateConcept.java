package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

public class TemplateConcept extends Concept {
	
	//The name of the template module i.e. generateElement(aScoreRules:ScoreRules)
	private String templateName;
	
	private String templateLine;
	
	private String templateExpressionType;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateLine() {
		return templateLine;
	}

	public void setTemplateLine(String templateLine) {
		this.templateLine = templateLine;
	}

	public String getTemplateExpressionType() {
		return templateExpressionType;
	}

	public void setTemplateExpressionType(String templateExpressionType) {
		this.templateExpressionType = templateExpressionType;
	}
	
	@Override
	public String getUniqueID() {
		return getFilePath() + DELIMINATOR + getTemplateLine();
	}
	
	public String toString(){
		return getFilePath() + " - LOC:" + getTemplateLine() + " - Type:" + getTemplateExpressionType();
	}
	
	@Override
	public String getTypeName() {
		return "TEMPLATE";
	}
	
	@Override
	public String getTypeCode() {
		return "T";
	}
}
