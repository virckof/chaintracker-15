package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

public class AcceleoNonOCLExpresion extends AcceleoExpression {
	
	private String modelContext;
	private String modelInstanceContext;
	private String expression;
	
	public String getModelContext() {
		return modelContext;
	}
	
	public void setModelContext(String modelContex) {
		this.modelContext = modelContex;
	}
	
	public String getModelInstanceContext() {
		return modelInstanceContext;
	}
	
	public void setModelInstanceContext(String modelInstanceContex) {
		this.modelInstanceContext = modelInstanceContex;
	} 
	
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String getExpressionString() {
		return getCompositeName() + " (" + getTypeString() + ")";
	}
	
	@Override
	public String getTypeString() {
		return "Simple";
	}

	/**
	 * @return a composite string of the
	 * model instance context and the expression.
	 */
	public String getCompositeName() {
		String exp = "";
		
		if (getModelInstanceContext() != null) {
			exp += getModelInstanceContext() + ".";
		}
		
		if (getExpression() != null) {
			exp += getExpression();
		}
		
		return exp;
	}

}
