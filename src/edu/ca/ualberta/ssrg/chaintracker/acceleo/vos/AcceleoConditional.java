package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

import java.util.ArrayList;
import java.util.List;

public class AcceleoConditional extends AcceleoExpression {
	public enum ConditionalType {
		NotSet,
		If,
		Elseif,
		Else
	}
	
	private ConditionalType type;
	private Integer lineNumber_parent_if; //line number of the opening IF statement 
	
	private List<String> modelContext;
	private List<String> modelInstanceContext;
	private List<String> expression;
	
	public AcceleoConditional() {
		type = ConditionalType.NotSet; // Default
		modelContext = new ArrayList<String>();
		modelInstanceContext = new ArrayList<String>();
		expression = new ArrayList<String>();
	}
	
	public void setType(ConditionalType type) {
		this.type = type;
	}
	
	public ConditionalType getType() {
		return this.type;
	}
	
	public Integer getLineNumber_parent_if() {
		return lineNumber_parent_if;
	}
	
	public void setLineNumber_parent_if(Integer parentLine) {
		this.lineNumber_parent_if = parentLine;
	}
	
	public List<String> getModelContext() {
		return modelContext;
	}
	
	public void setModelContext(List<String> modelContext) {
		this.modelContext = modelContext;
	}
	
	public List<String> getModelInstanceContext() {
		return modelInstanceContext;
	}
	
	public void setModelInstanceContext(List<String> modelInstanceContex) {
		this.modelInstanceContext = modelInstanceContex;
	}
	
	public List<String> getExpression() {
		return expression;
	}
	
	public void setExpression(List<String> expression) {
		this.expression = expression;
	}
	
	@Override
	public String getExpressionString() {
		String s = null;
		
		for (int i = 0; i < getExpression().size(); i++) {
			if (s == null) {
				s = ""; 
			} else {
				s += ", ";
			}
			
			if (getModelInstanceContext().get(i) != null) {
				s += getModelInstanceContext().get(i) + ".";
			}
			
			if (getExpression().get(i) != null) {
				s += getExpression().get(i);
			}
		}
		
		return s + " (" + getTypeString() + " - " + this.type.toString() + " )";
	}

	@Override
	public String getTypeString() {
		return "Conditional";
	}
}
