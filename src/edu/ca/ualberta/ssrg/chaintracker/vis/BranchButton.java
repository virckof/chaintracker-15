package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;

public class BranchButton extends RoundButton{
	
	private String direction;
	private int ID;
	private Model to;
	private Model from;

	public BranchButton(int x, int y, Color colour, Color textC, String text, String direction, int ID, Model to, Model from, int length, int height) {
		super(x, y, colour, textC, text, length, height);
		setDirection(direction);
		setID(ID);
		setTo(to);
		setFrom(from);
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Model getTo() {
		return to;
	}

	public void setTo(Model to) {
		this.to = to;
	}

	public Model getFrom() {
		return from;
	}

	public void setFrom(Model from) {
		this.from = from;
	}

}
