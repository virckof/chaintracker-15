package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;

public class ModelButton extends RoundButton{
	private Model model;

	public ModelButton(int x, int y, Color colour, Color textC, String text,
			int length, int height, Model model) {
		super(x, y, colour, textC, text, length, height);
		setModel(model);
		
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
