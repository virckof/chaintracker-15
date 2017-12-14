package edu.ca.ualberta.ssrg.chaintracker;

public class M2MInput implements MasterControllerInput {

	/**
	 * Source model file
	 * ex: PhyDsl.ecore
	 */
	private String sourceFile;

	/**
	 * Target model file
	 * ex: Dynamics.ecore
	 */
	private String targetFile;

	/**
	 * ATL transformation file
	 * ex: Dynamics.atl.xmi
	 */
	private String transformationFile;

	public M2MInput(String sourceFile, String targetFile, String transformationFile) {
		this.sourceFile = sourceFile;
		this.targetFile = targetFile;
		this.transformationFile = transformationFile;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}

	public String getTransformationFile() {
		return transformationFile;
	}

	public void setTransformationFile(String transformationFile) {
		this.transformationFile = transformationFile;
	}
	
	public String toString() {
		String s = "";
		s += "Transformation="+ transformationFile + "\n";
		s += "Source Model=" + sourceFile + "\n";
		s+= "Target Model=" + targetFile  + "\n";
		
		return s;
	}
}
