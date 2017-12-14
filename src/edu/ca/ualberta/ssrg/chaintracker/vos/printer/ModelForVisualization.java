package edu.ca.ualberta.ssrg.chaintracker.vos.printer;

public class ModelForVisualization implements Comparable<ModelForVisualization>{
	private int modelID;
	
	private String modelSourceType;
	
	private String model;
	
	private String modelType;

	public int getModelID() {
		return modelID;
	}

	public void setModelID(int modelID) {
		this.modelID = modelID;
	}

	public String getModelSourceType() {
		return modelSourceType;
	}

	public void setModelSourceType(String modelSourceType) {
		this.modelSourceType = modelSourceType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	
	public String toString() {
		return modelID + ";" + modelSourceType + ";"+ model + ";" + modelType;
	}

	@Override
	public int compareTo(ModelForVisualization other) {
		if (this.modelID == other.modelID) return 0;
		
		return (this.modelID > other.modelID) ? 1 : -1;
	}
}
