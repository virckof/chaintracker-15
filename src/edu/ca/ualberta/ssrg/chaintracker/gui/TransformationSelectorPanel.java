package edu.ca.ualberta.ssrg.chaintracker.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class TransformationSelectorPanel extends JPanel implements ActionListener{

	public final static String OPEN_CMD_TRANS = "OPEN_CMD_TRANS";
	public final static String OPEN_CMD_SMETA = "OPEN_CMD_SMETA";
	public final static String OPEN_CMD_TMETA = "OPEN_CMD_TMETA";
	public final static String OPEN_CMD_SMODEL = "OPEN_CMD_SMODEL";
	private JButton openBtnTrans;
	private JButton openBtnSourceMetamodel;
	private JButton openBtnTargetMetamodel;
	private JButton openSrcModel;
	private JFileChooser chooserJFC;
	private JTextField transTxt;
	private JTextField sourceMetamodelTxt;
	private JTextField targetMetamodelTxt;
	private JTextField sourceModelTxt;
	private boolean isM2T;
	
	public TransformationSelectorPanel(int id){
		
		TitledBorder titled = new TitledBorder("Transformation "+id);
		this.setBorder(titled);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.setPreferredSize(new Dimension(490, 165));
		
		JLabel transLab = new JLabel(" Transformation: ");
		transLab.setPreferredSize(new Dimension(100, 20));
		transTxt = new JTextField();
		transTxt.setPreferredSize(new Dimension(300, 20));
		
		JLabel sourceModelLab = new JLabel(" Source Metamodel: ");
		sourceModelLab.setPreferredSize(new Dimension(100, 20));
		sourceMetamodelTxt = new JTextField();
		sourceMetamodelTxt.setPreferredSize(new Dimension(300, 20));

		JLabel targetModelLab = new JLabel(" Target Metamodel: ");
		targetModelLab.setPreferredSize(new Dimension(100, 20));	
		targetMetamodelTxt = new JTextField();
		targetMetamodelTxt.setPreferredSize(new Dimension(300, 20));
		
		JLabel srcMode = new JLabel(" Source Model: ");
		srcMode.setPreferredSize(new Dimension(100, 20));	
		sourceModelTxt = new JTextField();
		sourceModelTxt.setPreferredSize(new Dimension(300, 20));
		
		
		openBtnTrans = new JButton();
		openBtnTrans.addActionListener(this);
		openBtnTrans.setActionCommand(OPEN_CMD_TRANS);
		openBtnTrans.setText("Open");
		
		openBtnSourceMetamodel = new JButton();
		openBtnSourceMetamodel.addActionListener(this);
		openBtnSourceMetamodel.setActionCommand(OPEN_CMD_SMETA);
		openBtnSourceMetamodel.setText("Open");
		
		openBtnTargetMetamodel = new JButton();
		openBtnTargetMetamodel.addActionListener(this);
		openBtnTargetMetamodel.setActionCommand(OPEN_CMD_TMETA);
		openBtnTargetMetamodel.setText("Open");
		
		openSrcModel = new JButton();
		openSrcModel.addActionListener(this);
		openSrcModel.setActionCommand(OPEN_CMD_SMODEL);
		openSrcModel.setText("Open");
		
		JRadioButton m2mBtn = new JRadioButton("M2M", true);
		m2mBtn.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int isChecked = e.getStateChange();
				if (isChecked == 1) {
					isM2T(false);
				}
			}           
	      });
		isM2T(false);
		
		JRadioButton m2tBtn = new JRadioButton("M2T");
		m2tBtn.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int isChecked = e.getStateChange();
				if (isChecked == 1) {
					isM2T(true);
				}
			}           
	      });
		
		ButtonGroup group = new ButtonGroup();
		group.add(m2mBtn);
		group.add(m2tBtn);
		
		this.add(transLab);
		this.add(transTxt);
		this.add(openBtnTrans);
		
		this.add(sourceModelLab);
		this.add(sourceMetamodelTxt);
		this.add(openBtnSourceMetamodel);
		
		this.add(targetModelLab);
		this.add(targetMetamodelTxt);
		this.add(openBtnTargetMetamodel);
		
		this.add(srcMode);
		this.add(sourceModelTxt);
		this.add(openSrcModel);
		
		this.add(m2mBtn);
		this.add(m2tBtn);
		
		chooserJFC = new JFileChooser();
		chooserJFC.setCurrentDirectory(new java.io.File("."));
	}
	
	// This method sets the text field and open button for Source Mode
	private void isM2T(boolean visible) {
		sourceModelTxt.setEnabled(visible);
		openSrcModel.setEnabled(visible);
		targetMetamodelTxt.setEnabled(!visible);
		openBtnTargetMetamodel.setEnabled(!visible);
		isM2T=visible;
	}

	public String getSourceMetamodelTxt() {
		return sourceMetamodelTxt.getText();
	}

	public String getTargetMetamodelTxt() {
		return targetMetamodelTxt.getText();
	}

	public String getSourceModelTxt() {
		return sourceModelTxt.getText();
	}
	
	public String getTransformationText(){
		return transTxt.getText();
	}

	public boolean isM2T() {
		return isM2T;
	}

	public void setM2T(boolean isM2T) {
		this.isM2T = isM2T;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		if(cmd.equals(OPEN_CMD_TRANS)){
			transTxt.setText(openFileChooser());
		}
		else if(cmd.equals(OPEN_CMD_SMETA)){
			sourceMetamodelTxt.setText(openFileChooser());
		}
		else if(cmd.equals(OPEN_CMD_TMETA)){
			targetMetamodelTxt.setText(openFileChooser());
		}
		else if(cmd.equals(OPEN_CMD_SMODEL)){
			sourceModelTxt.setText(openFileChooser());
		}
		
	}
	
	private String openFileChooser(){
		int result = chooserJFC.showOpenDialog(this);
		
		if(result == JFileChooser.APPROVE_OPTION){
			System.out.println("File: "+  chooserJFC.getSelectedFile());
			return chooserJFC.getSelectedFile().getAbsolutePath();
		}
		return "";
	}
	
	

}
