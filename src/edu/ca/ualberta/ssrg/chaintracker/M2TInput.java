package edu.ca.ualberta.ssrg.chaintracker;

public class M2TInput implements MasterControllerInput {

	/**
	 * Metamodel file
	 * ex: Dynamics.ecore
	 */
	private String metamodelFile;

	/** Template file
	 *  ex: generateDynamics.mtl
	 */
	private String templateFile;

	/**
	 * Model instance file
	 * ex. dynamics.xmi
	 */
	private String modelInstanceFile;
	
	public M2TInput(String sourceFile, String targetFile) {
		this.metamodelFile = sourceFile;
		this.templateFile = targetFile;
	}
	
	public M2TInput(String metamodelFile, String templateFile, String modelSourceFile) {
		this.metamodelFile = metamodelFile;
		this.templateFile = templateFile;
		this.modelInstanceFile = modelSourceFile;
	}

	public String getMetamodelFile() {
		return metamodelFile;
	}

	public void setMetamodelFile(String metamodelFile) {
		this.metamodelFile = metamodelFile;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public String getModelInstanceFile() {
		return modelInstanceFile;
	}

	public void setModelInstanceFile(String modelInstanceFile) {
		this.modelInstanceFile = modelInstanceFile;
	}

	public String toString() {
		String s = "";
		s += "Source Metamodel=" + metamodelFile + "\n";
		s += "Source Template=" + templateFile  + "\n";
		if (modelInstanceFile != null) {
			s += "Source Model Instance=" + modelInstanceFile  + "\n";
		}
		return s;
	}

}
