package edu.ca.ualberta.ssrg.chaintracker.acceleo.vos;

public class AcceleoFile {

	private String modelContext;
	private String modelInstanceContext;
	private String fileName;
	private boolean appendMode; 
	private String charset;
	
	
	public String getModelContext() {
		return modelContext;
	}
	public void setModelContext(String modelContext) {
		this.modelContext = modelContext;
	}
	
	
	public String getModelInstanceContext() {
		return modelInstanceContext;
	}
	
	public void setModelInstanceContext(String modelInstanceContext) {
		this.modelInstanceContext = modelInstanceContext;
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	public boolean isAppendMode() {
		return appendMode;
	}
	
	public void setAppendMode(boolean appendMode) {
		this.appendMode = appendMode;
	}
	
	
	public String getCharset() {
		return charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
