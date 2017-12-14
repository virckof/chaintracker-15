package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Element {
	
	private int xOrigin;
	private int yOrigin;
	private int xDest;
	private int yDest;
	private int height;
	private Model parentModel;
	private String name;
	private ArrayList<Attribute> attList = new ArrayList<Attribute>();
	private Color colour;
	private String type;
	private Annotation annotation;
	
	private ArrayList<Element> connectionsF = null;
	private ArrayList<Element> connectionsB = null;
	public static int RECTWIDTH = 20;

	public Element(String name, Model parentMod){
		setName(name);
		setColour(Color.BLUE);
		setParentModel(parentMod);
		setAnnotation(null);
	}
	
	/**
	 * This function takes an x and y coordinate and returns
	 * whether or not that point is located on the element.
	 */
	public boolean ClickOn(int xPt, int yPt){
		if ((xPt <= this.getXDest()) && (xPt >= this.getXOrigin())){
			if ((yPt <= this.getYDest()) && (yPt >= this.getYOrigin())){
				return true;
			}
		} 
		return false;
	}
	
	/**
	 * This function returns an ArrayList of the elements that are 
	 * connected to the element provided
	 */
	public ArrayList<Element> connected(ArrayList<Trace> traces){
		connectionsF = new ArrayList<Element>();
		connectionsF.add(this);
		connectionsB = new ArrayList<Element>();
		connectionsB.add(this);
		
		//Forward Connections
		for (int position = 0; position < connectionsF.size(); position++){
			for (Trace trace: traces){
				if (trace.getOrigin().getParentEle().equals(connectionsF.get(position))){
					arrayAddEle(connectionsF, trace.getDestination().getParentEle());
				}
			}
		}
		
		//Backward Connections
		for (int position = 0; position < connectionsB.size(); position++){
			for (Trace trace: traces){
				if (trace.getDestination().getParentEle().equals(connectionsB.get(position))){
					arrayAddEle(connectionsB, trace.getOrigin().getParentEle());
				}
			}
		}
		
		for (Element e : connectionsF){
			if (connectionsB.contains(e)){
				//Do nothing
			} else {
				connectionsB.add(e);
			}
		}
		return connectionsB;
		
		
	}
	
	/**
	 * This function adds an element to an array list iff it is not
	 * already present in the list.
	 */
	public void arrayAddEle(ArrayList<Element> list, Element item){
		if (list.contains(item)){
			//Do nothing
		} else {
			list.add(item);
		}
	}
	
	public void draw (Graphics2D graphics, ArrayList<Model> path, FontMetrics fm){
		Composite originalComposite;
		float TRANSPARANCY = 0.5f;
		AlphaComposite alphC;
		String eleName;
		originalComposite = graphics.getComposite();
        alphC = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARANCY); 
        
        //Draw element box
    	graphics.setColor(colour);
    	graphics.setComposite(alphC);
    	graphics.fillRect(xOrigin, yOrigin, Element.RECTWIDTH, height);
    	
    	//Draw font
    	graphics.setColor(Color.BLACK);
    	graphics.setComposite(originalComposite);
    	int fontWidth = fm.stringWidth(name);
    	//Ensure a max of 20 characters
    	if (name.length() > 20){
    		eleName = name.substring(0, 20);
    	} else {
    		eleName = name;
    	}
    	//Determine which side of the model the name should be written on
    	if (parentModel.equals(path.get(path.size() - 1))){
    		graphics.drawString(eleName, (xDest + FrontendData.SPACE_BETWEEN_ELEMENTS/2), (yOrigin + (height/2) + (fm.getHeight()/4)));	
    		//If element type is a template add that information to the string
        	if (getType().equals(Model.TYPE_TEMPLATE)) {
        		graphics.drawString("(Template)", (xDest + FrontendData.SPACE_BETWEEN_ELEMENTS/2), (yOrigin + (height/2) + (fm.getHeight()/4)) + fm.getHeight());	
        	}
    	} else {
    		graphics.drawString(eleName, (xOrigin - fontWidth - FrontendData.SPACE_BETWEEN_ELEMENTS), (yOrigin + (height/2) + (fm.getHeight()/4)));
    		//If element type is a template add that information to the string
        	if (getType().equals(Model.TYPE_TEMPLATE)) {
        		graphics.drawString("(Template)", (xOrigin - fontWidth - FrontendData.SPACE_BETWEEN_ELEMENTS), (yOrigin + (height/2) + (fm.getHeight()/4)) + fm.getHeight());
        	}
    	}
	}

	public int getXOrigin() {
		return xOrigin;
	}

	public void setXOrigin(int x) {
		this.xOrigin = (x - (RECTWIDTH/2));
		setXDest(xOrigin);
	}

	public int getYOrigin() {
		return yOrigin;
	}

	public void setYOrigin(int y) {
		this.yOrigin = y;
		setYDest(yOrigin);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public int getXDest() {
		return xDest;
	}

	private void setXDest(int x) {
		this.xDest = x + RECTWIDTH;
	}
	
	public int getYDest() {
		return yDest;
	}

	private void setYDest(int y) {
		this.yDest = y + this.height;
	}
	
	public ArrayList<Attribute> getAttList() {
		return attList;
	}

	public void addAttList(Attribute attribute) {
		this.attList.add(attribute);
	}
	
	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

	public Model getParentModel() {
		return parentModel;
	}

	private void setParentModel(Model parentModel) {
		this.parentModel = parentModel;
	}
	
	public ArrayList<Element> getConnectionsF() {
		return this.connectionsF;
	}
	
	public ArrayList<Element> getConnectionsB() {
		return this.connectionsB;
	}
	
	@Override
	public boolean equals(Object other){
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof Element)){

			return false;
		}
		Element otherElement = (Element) other;
		if(otherElement.getName().equals(this.name)&&otherElement.getParentModel().equals(this.parentModel)) {
			return true;
		}
		else return false;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}
}
