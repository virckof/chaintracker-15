package edu.ca.ualberta.ssrg.chaintracker.vos;

public class TargetTextualElement {

	/**
	 * name of the template e.g. generateElement(aDynamics : Dynamics)
	 */
	private String templateName;
	
	/**
	 * Injection LOC in the template
	 */
	private String targetLine;
	
	/**
	 * Type of the expression (Conditional/Loop/Simple)
	 */
	private String targetExpressionType;

	

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTargetLine() {
		return targetLine;
	}

	public void setTargetLine(String targetLine) {
		this.targetLine = targetLine;
	}

	public String getTargetExpressionType() {
		return targetExpressionType;
	}

	public void setTargetExpressionType(String targetExpressionType) {
		this.targetExpressionType = targetExpressionType;
	}
	
	public String toReadableString(){
		String s = "[\n     Target Line : " + targetLine + "\n"+
				"     Target Expression : " + targetExpressionType;
				s += "\n]";
				
		return s;
	}
	
}
