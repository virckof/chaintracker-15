package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

public class CodeConcept extends Concept {
	
	//The line in the code file
	private String codeLine;
	
	//Whether the line in the code file is empty
	private boolean isEmptyLine;

	public String getCodeLine() {
		return codeLine;
	}

	public void setCodeLine(String codeLine) {
		this.codeLine = codeLine;
	}

	public boolean isEmptyLine() {
		return isEmptyLine;
	}

	public void setEmptyLine(boolean isEmptyLine) {
		this.isEmptyLine = isEmptyLine;
	}
	
	@Override
	public String getUniqueID() {
		return getFilePath() + DELIMINATOR + getCodeLine();
	}

	public String toString(){
		return filePath + " - LOC:" + codeLine + (isEmptyLine ? " (empty)" : "");
	}

	@Override
	public String getTypeName() {
		return "CODE";
	}
	
	@Override
	public String getTypeCode() {
		return "C";
	}
}
