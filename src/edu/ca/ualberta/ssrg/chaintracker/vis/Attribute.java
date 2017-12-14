package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Graphics2D;

public class Attribute {
	
	private int x;
	private int y;
	private String name;
	private String id;
	private Element parentEle;
	private Model parentModel;
	private String type;

	public Attribute(String atName, String ID, Model parentMod, String type){
		setName(atName);
		setId(ID);
		setParentModel(parentMod);
		setType(type);
	}
	
	public void draw(Graphics2D graphics, int size){
		graphics.fillOval(x - (size/2), y - (size/2), size, size);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public Element getParentEle() {
		return parentEle;
	}

	public void setParentEle(Element parentEle) {
		this.parentEle = parentEle;
	}

	public Model getParentModel() {
		return parentModel;
	}

	private void setParentModel(Model parentModel) {
		this.parentModel = parentModel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
