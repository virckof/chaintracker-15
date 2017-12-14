package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;
import java.awt.Graphics2D;

public class RoundButton {
	
	private int xCenter;
	private int yCenter;
	private int length;
	private int height;
	private String text;
	private Color colour;
	private Color textColour;
	private int semiMajor;
	private int semiMinor;
	
	public RoundButton(int xCenter, int yCenter, Color colour, Color textC, String text, int length, int height){
		setX(xCenter);
		setY(yCenter);
		setText(text);
		setColour(colour);
		setTextColour(textC);
		setLength(length);
		setHeight(height);
		semiMajor = (int) Math.round(length/2);
		semiMinor = (int) Math.round(height/2);
	}
	
	/**
	 * This function takes an x and y coordinate and returns
	 * whether or not that point is located on the button.
	 */
	public boolean ClickOn(int xPt, int yPt){
		double dx = xPt - xCenter;
		double dy = yPt - yCenter;
		
		if ((((dx*dx)/(semiMajor*semiMajor)) + ((dy*dy)/(semiMinor*semiMinor))) <= 1){
			return true;
		} 
		return false;
	}
	
	public void draw(Graphics2D graphics, String label, int fontOffsetX, int fontOffsetY){
		graphics.setColor(colour);
		graphics.fillOval(xCenter - (length/2), yCenter - (height/2), length, height);
		graphics.setColor(textColour);
		graphics.drawString(label, xCenter - fontOffsetX, yCenter + fontOffsetY);
	}

	public int getX() {
		return xCenter;
	}

	private void setX(int x) {
		this.xCenter = x;
	}

	public int getY() {
		return yCenter;
	}

	public void setY(int y) {
		this.yCenter = y;
	}
	
	public String getText() {
		return text;
	}

	private void setText(String text) {
		if (text.length() > 29){
			text = text.substring(0, 29);
		}
		this.text = text;
	}
	
	public Color getColour() {
		return colour;
	}

	private void setColour(Color col) {
		this.colour = col;
	}
	
	public Color getTextColour() {
		return textColour;
	}

	private void setTextColour(Color col) {
		this.textColour = col;
	}

	public int getLength() {
		return length;
	}

	private void setLength(int length) {
		this.length = length;
	}

	public int getHeight() {
		return height;
	}

	private void setHeight(int height) {
		this.height = height;
	}
	public int getSemiMajor(){
		return semiMajor;
	}
	public int getSemiMinor(){
		return semiMinor;
	}
}
