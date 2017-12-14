package edu.ca.ualberta.ssrg.chaintracker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;

import edu.ca.ualberta.ssrg.chaintracker.vis.Annotation;
import edu.ca.ualberta.ssrg.chaintracker.vis.Element;

public class AnnotationDialog extends JFrame {
	
	public final static String SAVE_ANNOTATION = "SAVE_ANNOTATION";
	
	private final int maxNumberOfCharacters = 500;

	private JButton enterAnnBtn;
	private JPanel textPanel;
	private JPanel radioPanel;
	private JScrollPane scrollPanel;
	private JTextPane textPane;
	private TitledBorder elementTitle;
	private TitledBorder radioTitle;
	private JRadioButton deleteRBtn;
	private JRadioButton modifyRBtn;
	private JRadioButton otherRBtn;
	private ButtonGroup bgroup;
	
	//Dimensions
	private Dimension dialogDim = new Dimension(460,290);
	private Dimension textDim = new Dimension(450,160);
	private Dimension btnDim = new Dimension(435,30);
	private Dimension radioDim = new Dimension(450, 50);
	
	public AnnotationDialog(MainVis main){
		
		this.setVisible(false);
		
		this.setTitle("Create/Modify Annotation");
        this.setLayout(new FlowLayout());
        
        elementTitle = new TitledBorder("title");
        radioTitle = new TitledBorder("Annotation Type");
        
        //Create text input area in a scroll pane
        textPanel = new JPanel();
        textPanel.setPreferredSize(textDim);
        textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        textPanel.setBackground(Color.WHITE);
        
        textPane = new JTextPane(new DefaultStyledDocument() {
        	@Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if ((getLength() + str.length()) <= maxNumberOfCharacters) {
                    super.insertString(offs, str, a);
                }
                else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        textPanel.add(textPane);
        
        scrollPanel = new JScrollPane(textPanel);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setPreferredSize(textDim);
        scrollPanel.setViewportView(textPane);
        
        scrollPanel.setBorder(elementTitle);
        
        //Create radio buttons
        deleteRBtn = new JRadioButton("Delete", false);
        modifyRBtn = new JRadioButton("Modify", false);
        otherRBtn = new JRadioButton("Other", true);
        bgroup = new ButtonGroup();
        bgroup.add(deleteRBtn);
        bgroup.add(modifyRBtn);
        bgroup.add(otherRBtn);
        radioPanel = new JPanel();
        radioPanel.setPreferredSize(radioDim);
        radioPanel.add(deleteRBtn);
        radioPanel.add(modifyRBtn);
        radioPanel.add(otherRBtn);
        radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setBorder(radioTitle);
        
        //Create save button
        enterAnnBtn= new JButton("Save Annotation");
        enterAnnBtn.setActionCommand(SAVE_ANNOTATION);
        enterAnnBtn.addActionListener(main);
        enterAnnBtn.setPreferredSize(btnDim);

        //Layout dialog
		this.setPreferredSize(dialogDim);
		this.setResizable(false);
		this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.add(scrollPanel);
		this.add(radioPanel);
		this.add(enterAnnBtn);
	
		setLocationRelativeTo(main);
		this.setWindowIcon();
	}
	
	public String getAnnotationText(){
		return textPane.getText();
	}
	
	public void showDialog(Element ele){
		elementTitle.setTitle(ele.getName());
		if (!(ele.getAnnotation() == null)){
			textPane.setText(ele.getAnnotation().getContent());
			switch(ele.getAnnotation().getType()){
			case 1:
				deleteRBtn.setSelected(true);
				break;
			case 2:
				modifyRBtn.setSelected(true);
				break;
			case 3:
				otherRBtn.setSelected(true);
				break;
			}
		} else {
			textPane.setText("");
			otherRBtn.setSelected(true);
		}
		
		this.setVisible(true);
	}
	
	private void setWindowIcon(){
		ImageIcon img = new ImageIcon("./gui/icon.png");
		this.setIconImage(img.getImage());
	}
	
	public int getSelectedRadio(){
		if(deleteRBtn.isSelected()){
			return Annotation.DELETE;
		} else if(modifyRBtn.isSelected()){
			return Annotation.MODIFY;
		} else if (otherRBtn.isSelected()){
			return Annotation.OTHER;
		}
		return 0;
	}
	
}
