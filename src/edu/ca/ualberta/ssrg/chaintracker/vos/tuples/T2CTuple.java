package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

public class T2CTuple {
	
	//The source template concept
	private TemplateConcept source;
	
	//The target code concept
	private CodeConcept target;

	public TemplateConcept getSource() {
		return source;
	}

	public void setSource(TemplateConcept source) {
		this.source = source;
	}

	public CodeConcept getTarget() {
		return target;
	}

	public void setTarget(CodeConcept target) {
		this.target = target;
	}
	
	public String toString() {
			String s =  "\nTemplate File :\n" + source.getFilePath()
					+ "\nCode File :\n" + target.getFilePath()
					+ "\nSource Concept :\n" + source.toString() 
					+ "\nTarget Concept :\n" + target.toString();
			
			return s;
	}
}
