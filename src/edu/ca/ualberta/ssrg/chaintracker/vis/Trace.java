package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import edu.ca.ualberta.ssrg.chaintracker.vos.printer.TraceForVisualization;

public class Trace {
	
	public enum TraceType {
		Implicit,
		Explicit
	}
	
	private Attribute origin;
	private Attribute destination;
	private String name;
	private String id;
	private TraceType type; 
	private Color colour;
	private String path;
	private String file;
	private boolean selected;
	private String natureType;
	
	// if type "Implicit," optional implicit reference name
	// ex. name of a helper function with a M2M function binding 
	private String implicitReferenceName; 
	
	//Variables for click calculation
	private int ERRMARGIN = 10;
	private Line2D line;
	private int boxX;
	private int boxY;

	public Trace(Attribute Start, Attribute End, String Name, String ID, String Type, String path, String natureType, String implicitReferenceName){
		setOrigin(Start);
		setDestination(End);
		setName(Name);
		setId(ID);
		if (Type.equals(TraceForVisualization.ImplicitType)){
			setType(TraceType.Implicit);
		} else if (Type.equals(TraceForVisualization.ExplicitType)){
			setType(TraceType.Explicit);
		} else {
			//default
			setType(TraceType.Implicit);
		}
		setColour();
		setPath(path);
		setFile(path);
		setSelected(false);
		setNatureType(natureType);
		if (!implicitReferenceName.equals(TraceForVisualization.NoImplicitReference)) {
			setImplicitReferenceName(implicitReferenceName);
		}
	}

	/**
	 * This function takes an x and y coordinate and returns
	 * whether or not that point is located near the trace.
	 * Near is defined as within the ERRMARGIN.
	 */
	public boolean ClickOn(int xPt, int yPt){
		//Create a line that follows the trace and a box where the user clicked with errMargin
		line = new Line2D.Double(this.getOrigin().getX(), this.getOrigin().getY(), this.getDestination().getX(), this.getDestination().getY());
		boxX = (xPt - (ERRMARGIN/2));
		boxY = (yPt - (ERRMARGIN/2));
		
		//Check for intersection between the errMargin box and the trace line
		if(line.intersects(boxX, boxY, ERRMARGIN, ERRMARGIN)){
			return true;
		} else {
			return false;
		}
	}
	
	public void draw(Graphics2D graphics){
		//Draw line
		graphics.setColor(colour);
    	graphics.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY()); 
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Trace))return false;
	    Trace otherMyClass = (Trace) other;
	    if (otherMyClass.getId().equals(this.getId())) {
	    	return true;
	    } else {
	    	return false;
	    }
	}

	public Attribute getOrigin() {
		return origin;
	}

	public void setOrigin(Attribute origin) {
		this.origin = origin;
	}

	public Attribute getDestination() {
		return destination;
	}

	public void setDestination(Attribute destination) {
		this.destination = destination;
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

	public TraceType getType() {
		return type;
	}

	private void setType(TraceType type) {
		this.type = type;
	}
	
	public void setColour() {
		if (this.selected){
			this.colour = Color.BLACK;
		} else {
			if (this.type.equals(TraceType.Implicit)){
				this.colour = Color.RED;
			} else if (this.type.equals(TraceType.Explicit)){
				this.colour = Color.GREEN;
			}
		}
	}
	
	public Color getColour(){
		return this.colour;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String file) {
		this.path = file;
	}
	
	//This function removes only the file name from the entire path string
	private void setFile(String path) {
		
		int index = path.lastIndexOf("\\");
		if (index == -1) {
			file = path;
		} else {
			file = path.substring(index + 1);
		}
	}
	
	public String getFile() {
		return file;
	}
	
	public String getNatureType() {
		return natureType;
	}

	public void setNatureType(String natureType) {
		this.natureType = natureType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getImplicitReferenceName() {
		return implicitReferenceName;
	}

	public void setImplicitReferenceName(String implicitReferenceName) {
		this.implicitReferenceName = implicitReferenceName;
	}
}