package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

import edu.ca.ualberta.ssrg.chaintracker.vos.printer.TraceForVisualization;

public class StandardTuple {
	public boolean explicitReference;
	public String implicitReferenceName;
	public String file;
	public String name;
	public String sourceID;
	public String targetID;
	public String sourceName;
	public String targetName;
	
	public String sourceTypeCode;
	public String targetTypeCode;
	
	public String getTupleType() {
		return sourceTypeCode + "2" + targetTypeCode;
	}
}
