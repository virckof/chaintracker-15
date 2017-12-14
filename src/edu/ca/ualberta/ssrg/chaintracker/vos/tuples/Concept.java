package edu.ca.ualberta.ssrg.chaintracker.vos.tuples;

public abstract class Concept {
	protected static final String DELIMINATOR = ";";
	
	protected String filePath;
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public abstract String getUniqueID();
	
	public abstract String getTypeName();
	
	public abstract String getTypeCode();
}
