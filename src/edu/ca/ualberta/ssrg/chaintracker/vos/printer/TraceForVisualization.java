package edu.ca.ualberta.ssrg.chaintracker.vos.printer;

public class TraceForVisualization {
	public static String ImplicitType = "implicit";
	public static String ExplicitType = "explicit";
	public static String NoImplicitReference = "none";
	
	private int traceId;
	
	private String traceType;
	
	private String file;
	
	private String value; //TODO better name
	
	private boolean explicitReference; // else implicit reference
	
	private String implicitReferenceName;
	
	private int sourceId;
	
	private int targetId;
	
	public TraceForVisualization() {
		implicitReferenceName = "";
	}
	
	public int getTraceId() {
		return traceId;
	}

	public void setTraceId(int traceId) {
		this.traceId = traceId;
	}

	public String getTraceType() {
		return traceType;
	}

	public void setTraceType(String traceType) {
		this.traceType = traceType;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isExplicitReference() {
		return explicitReference;
	}

	public void setExplicitReference(boolean explicitReference) {
		this.explicitReference = explicitReference;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	
	public String getImplicitReferenceName() {
		return implicitReferenceName;
	}

	public void setImplicitReferenceName(String implicitReferenceName) {
		this.implicitReferenceName = implicitReferenceName;
	}

	public String toString() {
		String s = traceId +  ";" + traceType + ";" + file + ";" + value + ";";
		if (explicitReference) {
			s += ExplicitType;
		} else {
			s += ImplicitType;
		}
		
		s += ";" + ((implicitReferenceName!= null) ? implicitReferenceName : NoImplicitReference);
		
		s += ";" + sourceId + ";" + targetId;
		
		return s;
	}
}
