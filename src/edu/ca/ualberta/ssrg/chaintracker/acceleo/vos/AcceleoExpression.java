package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

public abstract class AcceleoExpression {
	protected Integer endLine;
	
	public abstract String getExpressionString();
	
	public abstract  String getTypeString();
	
	public Integer getEndLine() {
		return endLine;
	}
	
	public void setEndLine(Integer endLine) {
		this.endLine = endLine;
	}
}
