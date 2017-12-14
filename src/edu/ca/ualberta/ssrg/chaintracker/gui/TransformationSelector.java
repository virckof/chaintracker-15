package edu.ca.ualberta.ssrg.chaintracker.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import edu.ca.ualberta.ssrg.chaintracker.*;

public class TransformationSelector extends JFrame {

	public final static String ADD = "ADD_TRANS";
	public final static String PROCESS = "PROCESS";
	public final static String LOAD_WS = "LOAD_WS";
	
	private JButton addTransBtn;
	private JButton processBtn;
	private JButton workspaceBtn;
	private JPanel mainPanel;
	private JScrollPane scrollPanel;
	private int transformationNumer;
	private ArrayList<TransformationSelectorPanel> panels;

	
	public TransformationSelector(MainVis main){
		
		this.setVisible(false);
		
		panels= new ArrayList<>();
		
		this.setTitle("ChainTracker Loader");
        this.setLayout(new FlowLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        scrollPanel = new JScrollPane(mainPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
     
        addTransBtn= new JButton("Add  Transformation");
        addTransBtn.setActionCommand(ADD);
        addTransBtn.addActionListener(main);
        addTransBtn.setPreferredSize(new Dimension(165,30));
        
        processBtn= new JButton("Load Transformations");
        processBtn.setActionCommand(PROCESS);
        processBtn.addActionListener(main);
        processBtn.setPreferredSize(new Dimension(165,30));
        
        workspaceBtn = new JButton("Load Current Workspace");
        workspaceBtn.setActionCommand(LOAD_WS);
        workspaceBtn.addActionListener(main);
        workspaceBtn.setPreferredSize(new Dimension(165,30));
        
        addNewTransformationPanel (true);
        addNewTransformationPanel (true);
		

        this.setSize(540,425);
		this.setResizable(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.add(addTransBtn);
		this.add(processBtn);
		this.add(workspaceBtn);
		
		
		this.add(scrollPanel);
	
		setLocationRelativeTo(main);
		this.setWindowIcon();
	}
	
	public void addNewTransformationPanel (boolean initializing){
		
		TransformationSelectorPanel tspTemp = new TransformationSelectorPanel(transformationNumer);
		mainPanel.add(tspTemp);
		
		mainPanel.setPreferredSize(new Dimension(520, (170*(transformationNumer+1))));
		scrollPanel.setPreferredSize(new Dimension(520,345));
		
		this.setSize(540,425);
		if(!initializing){
			this.setVisible(true);
		}
		transformationNumer++;
		panels.add(tspTemp);
		
	}
	
	private void setWindowIcon(){
		ImageIcon img = new ImageIcon("./gui/icon.png");
		this.setIconImage(img.getImage());
	}
	
	public ArrayList<TransformationSelectorPanel> getPanels() {
		return panels;
	}

	public void setPanels(ArrayList<TransformationSelectorPanel> panels) {
		this.panels = panels;
	}

}
