package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;

public class PieSlice {
	  private double value;
	  private Color color;

	  public PieSlice(double value, Color color) {
	    this.setValue(value);
	    this.setColor(color);
	  }

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
