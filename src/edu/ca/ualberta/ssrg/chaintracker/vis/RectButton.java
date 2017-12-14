package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class RectButton {
	
	private int XOrigin;
	private int YOrigin;
	private int length;
	private int height;
	private String text;
	private Color colour;
	private Color textColour;
	
	public RectButton(Color colour, Color textC, String text, int length, int height){
		
		setText(text);
		setColour(colour);
		setTextColour(textC);
		setLength(length);
		setHeight(height);
	}
	
	/**
	 * This function takes an x and y coordinate and returns
	 * whether or not that point is located on the button.
	 */
	public boolean ClickOn(int xPt, int yPt){
		if ((xPt <= (this.getX() + this.getLength())) && (xPt >= this.getX())){
			if ((yPt <= (this.getY() + this.getHeight())) && (yPt >= this.getY())){
				return true;
			}
		} 
		return false;
	}
	
	public void draw(Graphics2D graphics, int fontOffsetX, int fontOffsetY, int xOrigin, int yOrigin){
		setX(xOrigin);
		setY(yOrigin);
		graphics.setFont(new Font("Ariel", Font.BOLD, 11));
		graphics.setColor(colour);
		graphics.fillRect(XOrigin, YOrigin, length, height);
		graphics.setColor(textColour);
		graphics.drawString(text, XOrigin + (this.getLength()/2) - fontOffsetX, YOrigin + (this.getHeight()/2) + fontOffsetY);
		graphics.setFont(new Font("Ariel", Font.PLAIN, 11));
	}

	public int getX() {
		return XOrigin;
	}

	public void setX(int x) {
		this.XOrigin = x;
	}

	public int getY() {
		return YOrigin;
	}

	public void setY(int y) {
		this.YOrigin = y;
	}
	
	public String getText() {
		return text;
	}

	private void setText(String text) {
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
}
