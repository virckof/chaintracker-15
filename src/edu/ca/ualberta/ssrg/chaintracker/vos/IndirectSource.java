package edu.ca.ualberta.ssrg.chaintracker.vos;

import java.util.ArrayList;

public class IndirectSource {

	public enum Type {
		ActionBlock,
		Helper
	}
	
	/**
	 * Type of the IndirectSource (ActionBlock, Helper)
	 */
	private Type type;
	
	/**
	 * If the IndirectSource is a Helper, their name
	 */
	private String helperName;
	
	/**
	 * If the IndirectSource is a Helper and has a context, their SourceElement context
	 */
	private SourceElement context;
	
	/**
	 * If the IndirectSource is a Helper and has parameters, their SourceElement parameters
	 */
	private ArrayList<SourceElement> parameters;
	
	/**
	 * SourceAttributes that are used by this IndirectSource
	 */
	private ArrayList<SourceAttribute> dependencies;
	
	/**
	 * Other IndirectSources that are used by this IndirectSource
	 */
	private ArrayList<IndirectSource> functionDependencies;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getHelperName() {
		return helperName;
	}

	public void setHelperName(String helperName) {
		this.helperName = helperName;
	}

	public SourceElement getContext() {
		return context;
	}

	public void setContext(SourceElement context) {
		this.context = context;
	}

	public ArrayList<SourceElement> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<SourceElement> parameters) {
		this.parameters = parameters;
	}

	public ArrayList<SourceAttribute> getDependencies() {
		return dependencies;
	}

	public void setDependencies(ArrayList<SourceAttribute> dependencies) {
		this.dependencies = dependencies;
	}

	public ArrayList<IndirectSource> getFunctionDependencies() {
		return functionDependencies;
	}

	public void setFunctionDependencies(
			ArrayList<IndirectSource> functionDependencies) {
		this.functionDependencies = functionDependencies;
	}
	
	public String getDisplayName() {
		if (getType() == Type.Helper) {
			return "Helper " + getHelperName();
		} else {
			return "Action Block";
		}
	}
	
	public String toReadableString(){
		return toReadableString("");
	}
	
	public String toReadableString(String indent){
		String str = indent + "Indirect Source: " + getDisplayName() + "\n";
		String seperator = "";
		
		if (getType() == Type.Helper) {
			if (getContext() != null) {
				str += indent + "    Context:\n" + getContext().toReadableString(indent + "        ") + "\n";
			}
			
			if (getParameters() != null && getParameters().size() > 0) {
				str += indent + "    Parameters:\n";
				
				seperator = "";
				for (SourceElement src : getParameters()) {
					str += seperator;
					str += src.toReadableString(indent + "        ");
					seperator = "\n";
				}
				
				str += "\n";
			}
		}
		
		
		if (getDependencies() != null && getDependencies().size() > 0) {
			str += indent + "    Dependencies: \n";
			
			for (SourceAttribute src : getDependencies()) {
				str += src.toReadableString(indent + "        ");
			}
			
			str += indent + "    \n";
		}
		
		if (getFunctionDependencies() != null && getFunctionDependencies().size() > 0) {
			str += indent + "    Function Dependencies:\n";
			
			seperator = "";
			for (IndirectSource src : getFunctionDependencies()) {
				str += seperator;
				
				if (src.getHelperName() != this.getHelperName()) {
					str += src.toReadableString(indent + "        ");
				} else {
					str += indent + "        " + src.getDisplayName();
				}
				
				seperator = "\n";
			}
			
			str += "\n";
		}

		return str;
	}
}
