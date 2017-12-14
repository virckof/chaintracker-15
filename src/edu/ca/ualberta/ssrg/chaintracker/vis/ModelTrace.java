package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ModelTrace extends Line2D{
	
	private Model modelFrom;
	private Model modelTo; 
	private String file;
	private String path;

	private Point2D P1;
	private Point2D P2;
	private double X1;
	private double X2;
	private double Y1;
	private double Y2;
	private Color color;
	private boolean selected;

	//Variables for click calculation
	private int ERRMARGIN = 8;
	private int boxX;
	private int boxY;
	

	public ModelTrace(Model modelFrom, Model modelTo, String file, String path){
		setModelFrom(modelFrom);
		setModelTo(modelTo);
		setColor();
		setSelected(false);
		setFile(file);
		setPath(path);
		
	}
	
	/**
	 * This function takes an x and y coordinate and returns
	 * whether or not that point is located near the trace.
	 * Near is defined as within the ERRMARGIN.
	 */
	public boolean ClickOn(int xPt, int yPt){

		//Create a line that follows the trace and a box where the user clicked with errMargin
		boxX = (xPt - (ERRMARGIN/2));
		boxY = (yPt - (ERRMARGIN/2));
		
		//Check for intersection between the errMargin box and the trace line
		if(this.intersects(boxX, boxY, ERRMARGIN, ERRMARGIN)){
			return true;
		} else {
			return false;
		}
	}
	
	public void draw(Graphics2D graphics){
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(2));
		graphics.drawLine((int)X1, (int)Y1, (int)X2, (int)Y2);
	}
	
	public Model getModelFrom() {
		return modelFrom;
	}

	public void setModelFrom(Model modelFrom) {
		this.modelFrom = modelFrom;
	}
	
	public Model getModelTo() {
		return modelTo;
	}

	public void setModelTo(Model modelTo) {
		this.modelTo = modelTo;
	}

	@Override
	public Rectangle2D getBounds2D() {
		return null;
	}

	@Override
	public Point2D getP1() {
		return P1;
	}

	@Override
	public Point2D getP2() {
		return P2;
	}

	@Override
	public double getX1() {
		return X1;
	}
	
	public void setX1(double x) {
		X1 = x;
	}

	@Override
	public double getX2() {
		return X2;
	}
	
	public void setX2(double x) {
		X2 = x;
	}

	@Override
	public double getY1() {
		return Y1;
	}
	
	public void setY1(double y) {
		Y1 = y;
	}

	@Override
	public double getY2() {
		return Y2;
	}
	
	public void setY2(double y) {
		Y2 = y;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor() {
		if (this.selected){
			this.color = Color.ORANGE;
		} else {
			this.color = Color.BLACK;
		}
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		setColor();
	}
	

	@Override
	public void setLine(double xFrom, double yFrom, double xTo, double yTo) {
		setX1(xFrom);
		setX2(xTo);
		setY1(yFrom);
		setY2(yTo);
		
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
