package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.util.ArrayList;

public class Model {
	
	public static final String TYPE_MODEL = "MODEL";
	public static final String TYPE_TEMPLATE = "TEMPLATE";
	public static final String TYPE_CODE = "CODE";

	private String name;
	private String type;
	private ArrayList<Element> eleList = new ArrayList<Element>();
	
	public Model(String name, String modType){
		setName(name);
		setType(modType);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<Element> getEleList() {
		return eleList;
	}
	
	public ArrayList<String> getEleListNames() {
		ArrayList<String> eleNames = new ArrayList<String>();
		
		for (Element e : getEleList()){
			eleNames.add(e.getName());
		}
		
		return eleNames;
	}

	public void addEleList(Element element) {
		eleList.add(element);
	}
}
