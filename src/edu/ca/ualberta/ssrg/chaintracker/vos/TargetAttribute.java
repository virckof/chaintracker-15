package edu.ca.ualberta.ssrg.chaintracker.vos;

import java.util.ArrayList;

public class TargetAttribute {
	
	private String name;
	
	private TargetElement owner;
	
	private ArrayList<SourceAttribute> bindings;

	private ArrayList<IndirectSource> functionBindings;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TargetElement getOwner() {
		return owner;
	}

	public void setOwner(TargetElement owner) {
		this.owner = owner;
	}

	public ArrayList<SourceAttribute> getBindings() {
		return bindings;
	}

	public void setBindings(ArrayList<SourceAttribute> bindings) {
		this.bindings = bindings;
	}
	
	public ArrayList<IndirectSource> getFunctionBindings() {
		return functionBindings;
	}

	public void setFunctionBindings(ArrayList<IndirectSource> functionBindings) {
		this.functionBindings = functionBindings;
	}

	public String toReadableString(){
		return toReadableString("");
	}

	public String toReadableString(String indent) {
		if (indent == null) {
			indent = "";
		}
		
		String seperator = "";
		
		String str = indent + "[" + name + " From:";
		
		if (getBindings() != null && getBindings().size() > 0) {
			str += "\n" + indent + "    Bindings\n";
			for(SourceAttribute binding : bindings) {
				str += seperator;
				str += binding.toReadableString(indent + "        ");
				seperator = "\n";
			}
		}
		
		seperator = "";
		if (getFunctionBindings() != null && getFunctionBindings().size() > 0) {
			str += "\n" + indent + "    Function Bindings\n";
			for(IndirectSource binding : getFunctionBindings()) {
				str += seperator;
				str += binding.toReadableString(indent + "        ") ;
				seperator = "\n";
			}
		}
		
		str += indent + "]\n";
		
		return str;
	}
}
