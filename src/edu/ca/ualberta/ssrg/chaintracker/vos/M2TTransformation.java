package edu.ca.ualberta.ssrg.chaintracker.vos;


/**
 * Represents a transformation injection
 * @author Victor
 */
public class M2TTransformation {
	
	/**
	 * File path to the template
	 */
	private String ownerTemplate;
	
	/**
	 * Whether the transformation is a control transformation (ex. loop, conditional).
	 */
	private boolean isControl;
	
	/**
	 * Source element from the input model
	 */
	private SourceElement sourceElement;
	
	/**
	 * Target textual element (injection statement)
	 */
	private TargetTextualElement targetElement;

	public String getOwnerTemplate() {
		return ownerTemplate;
	}

	public void setOwnerTemplate(String ownerTemplate) {
		this.ownerTemplate = ownerTemplate;
	}

	public SourceElement getSourceElement() {
		return sourceElement;
	}

	public void setSourceElement(SourceElement sourceElement) {
		this.sourceElement = sourceElement;
	}

	public TargetTextualElement getTargetElement() {
		return targetElement;
	}

	public void setTargetElement(TargetTextualElement targetElement) {
		this.targetElement = targetElement;
	}
	
	public boolean isControl() {
		return isControl;
	}

	public void setControl(boolean isControl) {
		this.isControl = isControl;
	}

	public String toReadableString(){
		String s = "\nOwner Template :\n" + ownerTemplate
				+  "\nIs Control? : " + (isControl? "yes" : "no")
				+ "\nSource Element :\n" + sourceElement.toReadableString() 
				+ "\nTarget Element :\n" + targetElement.toReadableString();
		
		return s;
	}

}
