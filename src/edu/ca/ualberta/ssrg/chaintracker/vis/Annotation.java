package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.io.Serializable;

public class Annotation implements Serializable{
	
	public static int DELETE = 1;
	public static int MODIFY = 2;
	public static int OTHER = 3;
	
	private String content;
	private int type;
	private String ID;

	public Annotation(String content, int type, Element parentEle){
		setContent(content);
		setType(type);
		setID(parentEle);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getID() {
		return ID;
	}

	public void setID(Element ele) {
		String IDt = ele.getParentModel().getName() + ":" + ele.getName() + ":" + ele.getAttList().get(0).getId() + ":" + ele.getAttList().get(ele.getAttList().size() - 1).getId();
		this.ID = IDt;
	}

}
