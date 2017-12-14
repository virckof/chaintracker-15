package edu.ca.ualberta.ssrg.chaintracker.vos;

public class SourceAttribute {
	
	/**
	 * Navigation statement or name of the attribute
	 */
	private String implicitBindings;
	
	/**
	 * Owner source element of this attribute
	 */
	private SourceElement owner;

	public String getImplicitBindings() {
		return implicitBindings;
	}

	public void setImplicitBindings(String implicitBindings) {
		this.implicitBindings = implicitBindings;
	}

	public SourceElement getOwner() {
		return owner;
	}

	public void setOwner(SourceElement owner) {
		this.owner = owner;
	}
	

	public String toReadableString(){
		return toReadableString("");
	}
	
	public String toReadableString(String indent){
		if (indent == null) {
			indent = "";
		}
		
		String elementType = (owner != null) ? owner.getSourceElementType() : "";
		String str =  indent + "[" + elementType + " --> " + implicitBindings  + "]" +"\n";
		
		return str;
	}

}
