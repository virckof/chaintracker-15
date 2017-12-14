package edu.ca.ualberta.ssrg.chaintracker.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SplashScreen extends JFrame{
	
	public SplashScreen (MainVis main){
		setResizable(false);
		setUndecorated(true);
		setWindowIcon();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize(new Dimension(790, 230));
		JLabel background=new JLabel(new ImageIcon("./gui/backgroud.png"));
		background.setPreferredSize(new Dimension(790, 230));
		setContentPane(background);
		setVisible(true);
		
		setLocationRelativeTo(main);
	}
	
	public void load(){
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.dispose();
	}
	
	
	private void setWindowIcon(){
		ImageIcon img = new ImageIcon("./gui/icon.png");
		this.setIconImage(img.getImage());
	}
	
	

}
