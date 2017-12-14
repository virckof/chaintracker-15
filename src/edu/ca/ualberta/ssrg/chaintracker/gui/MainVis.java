package edu.ca.ualberta.ssrg.chaintracker.gui;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import edu.ca.ualberta.ssrg.chaintracker.M2MInput;
import edu.ca.ualberta.ssrg.chaintracker.M2TInput;
import edu.ca.ualberta.ssrg.chaintracker.MasterController;
import edu.ca.ualberta.ssrg.chaintracker.MasterControllerInput;
import edu.ca.ualberta.ssrg.chaintracker.vis.Annotation;
import edu.ca.ualberta.ssrg.chaintracker.vis.Colors;
import edu.ca.ualberta.ssrg.chaintracker.vis.Element;
import edu.ca.ualberta.ssrg.chaintracker.vis.GraphPanel;
import edu.ca.ualberta.ssrg.chaintracker.vis.Model;
import edu.ca.ualberta.ssrg.chaintracker.vis.Trace;
import edu.ca.ualberta.ssrg.chaintracker.vis.Trace.TraceType;

/**
 * Main container of the application
 * @author Victor
 *
 */
@SuppressWarnings("serial")
public class MainVis extends JFrame implements ComponentListener, ActionListener{
	
	// Tracks table type being displayed
	public int ELETABLE = 0;
	public int TRACETABLE = 1;
	public int ELETABLE_MODEL = 0;
	public int TRACETABLE_M2M = 2;
	public int TRACETABLE_M2T = 3;
	public int TRACETABLE_T2C = 4;
	public int MODELTABLE = 5;
	private int tableType;
	private int subTableType;
	private int tableWidth;
	private int codeViewWidth;
	private int codeViewHeight;
	private int splitPaneWidth;
	private int splitPaneHeight;
	private JTable table1;
	private JTable table2;
	private JTable table3;
	
	// Event Constants
	public final static String CONFIG_MENU = "CONFIG_MENU";
	
	public final static String FILTER_MENU = "FILTER_MENU";
	
	public final static String FILTER_TRACE_FORWARD = "FILTER_TRACE_FORWARD";
	
	public final static String FILTER_TRACE_BACKWARD = "FILTER_TRACE_BACKWARD";
	
	public final static String FILTER_ALL_ELE = "FILTER_ALL_ELE";
	
	public final static String FILTER_TRACE_ELE = "FILTER_TRACE_ELE";
	
	public final static String OVERVIEW = "OVERVIEW";
	
	public final static String BRANCH_VIEW = "BRANCH_VIEW";
	
	public final static String CLEAN_MENU = "CLEAN_MENU";

	public final static String CODE_MENU = "CODE_MENU";
	
	public final static String SAVE = "SAVE";
	
	public final static String VIEW_FILE = "VIEW_FILE";
	
	//Column Constants
	public final static String SOURCE = "<html><b>Source</b></html>";
	
	public final static String TARGET = "<html><b>Target</b></html>";
	
	public final static String MAPPING_NAME = "<html><b>Transformation</b></html>";
	
	public final static String TYPE = "<html><b>Type</b></html>";
	
	public final static String MAPPING = "<html><b>Binding</b></html>";
	
	public final static String SOURCE_ATT_NAME = "<html><b>Source Attribute</b></html>";
	
	public final static String SOURCE_ATT_PARENT = "<html><b>Source Element</b></html>";
	
	public final static String SOURCE_ATT_TYPE = "<html><b>Source Attribute Type</b></html>";
	
	public final static String TARGET_ATT_NAME = "<html><b>Target Attribute</b></html>";
	
	public final static String TARGET_ATT_PARENT = "<html><b>Target Element</b></html>";
	
	public final static String TARGET_ATT_TYPE = "<html><b>Target Attribute Type</b></html>";
	
	public final static String TEMPLATE_FILE = "<html><b>Template File</b></html>";
	
	public final static String TEMPLATE_LINE = "<html><b>Template Line</b></html>";
	
	public final static String TEMPLATE_TYPE = "<html><b>Template Type</b></html>";
	
	public final static String CODE_FILE = "<html><b>Code File</b></html>";
	
	public final static String CODE_LINE = "<html><b>Code Line</b></html>";
	
	public final static String CODE_TYPE = "<html><b>Code Type</b></html>";
	
	public final static String ATT_NAME =  "<html><b>Attribute Name</b></html>";
	
	public final static String ATT_TYPE =  "<html><b>Attribute Type</b></html>";
	
	public final static String LINE_ID =  "<html><b>Line Number</b></html>";
	
	public final static String LINE_TYPE =  "<html><b>Expression Type</b></html>";
	
	public final static String INBOUND_MAP_NAME = "<html><b>Forward Bindings</b></html>";
	
	public final static String OUTBOUND_MAP_NAME = "<html><b>Backward Bindings</b></html>";
	
	public final static String TEMPLATE_MAPPING_TYPE = "<html><b>Template Binding Type</b></html>";
	
	public final static String ELEMENT_NAME = "<html><b>Model Elements</b></html>";
	
	public final static String NUM_ATTRIBUTES = "<html><b>Number of Attributes</b></html>";

	public final static String VIEWER_VIEW_ELEMENT_FORWARD = "VIEWER_VIEW_ELEMENT_FORWARD";
	
	public final static String VIEWER_VIEW_ELEMENT_BACKWARDS = "VIEWER_VIEW_ELEMENT_BACKWARDS";
	
	public final static String VIEWER_CLOSE_CURRENT = "VIEWER_CLOSE_CURRENT";
	
	public final static String VIEWER_VIEW_TRACE = "VIEWER_VIEW_TRACE";
	
	public final static String ANNOTATION = "ANNOTATION";
	
	public final static String EMPTY_FILE = "EMPTY_FILE";
	
	// Constants
	public final static int VERTICAL_SCROLL_BAR_SIZE = 13;
	
	public final static int HORIZONTAL_SCROLL_BAR_SIZE = 35;
	
	// Canvas container
	private GraphPanel graphPanel;
	
	private JMenuBar menubar;
	
	private TransformationSelector transSelector;
	
	private AnnotationDialog annDialog;
	
	private JTabbedPane codeViewers;
	
	private JScrollPane graphScroll;

	private JSplitPane splitPane;
	
	private JScrollPane tableScrollPane1, tableScrollPane2, tableScrollPane3;
	
	private DefaultTableModel detailsTable1;
	
	private DefaultTableModel detailsTable2;
	
	private DefaultTableModel detailsTable3;
	
	private MasterController controller;
	
	private Hashtable<String, CodeViewer> viewers;
	
	private int codeViewerCount;
	
	private DetailsTableManager detailsTblMgr;
	
	//Variables for resizing
	int visWidth, visHeight, tableHeight, twoColWidth, threeColWidth;
	
	// Track which zones have been highlighted already
	private ArrayList<String> zoneIds;
	
	public MainVis(){
		
		viewers = new Hashtable<>();
		detailsTblMgr = new DetailsTableManager();
		initializeMenuBar();
		initializeCodeViewers();
		
		zoneIds = new ArrayList<String>();
			
		transSelector = new TransformationSelector(this);
		annDialog = new AnnotationDialog(this);
		
		// Used so all the components calculate their initial size given an initial x and y.
		Toolkit tk = Toolkit.getDefaultToolkit();  
		int xSize = ((int) tk.getScreenSize().getWidth());  
		int ySize = ((int) tk.getScreenSize().getHeight());  
		this.setSize(xSize,ySize);
		
		// By default the window is maximized
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
	
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("ChainTracker - Accesible MDE");
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		visWidth = (int) ((Math.round(this.getSize().getWidth())/16) * 9);
		visHeight = (int) ((Math.round(this.getSize().getHeight())/10) * 8);
		codeViewWidth = (int) (((Math.round(this.getSize().getWidth())/16) * 7) - 28);
		codeViewHeight = (int) (((Math.round(this.getSize().getHeight())/10) * 8));
		
		graphPanel = new GraphPanel(this);
		graphScroll = new JScrollPane(graphPanel);
		graphScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		graphScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphScroll, codeViewers);
		splitPane.setPreferredSize(new Dimension(visWidth + codeViewWidth, visHeight));
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(visWidth);
		
		//Listener for divider sliding in split pane
		PropertyChangeListener listener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {

		            @Override
		            public void run() {
		                componentResized(null);
		            }
		        });
			}
		};
		
		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, listener);
	    
		this.add(splitPane);
		addEmptyCodeViewer();
		
		initializeDetailsTable(); // the addition happens inside because of the scroll
		
		setWindowIcon();
		
		//create custom close operation
		this.addWindowListener(new WindowAdapter()
		{
		      public void windowClosing(WindowEvent e)
		      {
		          graphPanel.save();
		      }
		});
		
		
	}

	private void initializeCodeViewers() {
		codeViewers = new JTabbedPane();
		codeViewers.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
	}


	private void initializeMenuBar(){
		menubar = new JMenuBar();

		// Configuration Menu
		JMenu configuration = new JMenu("Chain Configuration");
		configuration.setMnemonic(KeyEvent.VK_C);
	    
		//Load Icon and Item
		ImageIcon loadIcon = new ImageIcon("./gui/load.png");
		Image imgLoad = loadIcon.getImage();  
		Image newimgLoad = imgLoad.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		loadIcon = new ImageIcon(newimgLoad);  
		
	    JMenuItem load = new JMenuItem("Load Transformations", loadIcon);
	    load.addActionListener(this);
	    load.setActionCommand(CONFIG_MENU);
	    configuration.add(load);
        
	    JMenuItem save = new JMenuItem("Save", loadIcon);
	    save.addActionListener(this);
	    save.setActionCommand(SAVE);
	    configuration.add(save);
	    menubar.add(configuration);
	    
	    // Filter Menu
	    JMenu visualizations = new JMenu("Visualizations");
	    visualizations.setMnemonic(KeyEvent.VK_V);
		
		//Change to overview
	    ImageIcon overviewIcon = new ImageIcon("./gui/overview.png");
		Image imgOverview = overviewIcon.getImage();  
		Image newimgOverview = imgOverview.getScaledInstance(18, 18,  java.awt.Image.SCALE_SMOOTH);  
		overviewIcon = new ImageIcon(newimgOverview);

		JMenuItem overview = new JMenuItem("See Overview Visualization", overviewIcon);
	    overview.addActionListener(this);
	    overview.setActionCommand(OVERVIEW);
	    visualizations.add(overview);
	    
	    //Change to overview
	    ImageIcon branchIcon = new ImageIcon("./gui/branch.png");
		Image imgBranch = branchIcon.getImage();  
		Image newimgBranch = imgBranch.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		branchIcon = new ImageIcon(newimgBranch); 
	    
  		JMenuItem branch = new JMenuItem("See Branch Visualization", branchIcon);
  		branch.addActionListener(this);
  		branch.setActionCommand(BRANCH_VIEW);
  	    visualizations.add(branch);
  	    
  	    menubar.add(visualizations);
  	    
	    // Filter Menu
	    JMenu filtering = new JMenu("Filter Elements");
		filtering.setMnemonic(KeyEvent.VK_F);
		
		//Filter Icon and Item
		ImageIcon filterIcon = new ImageIcon("./gui/filter.png");
		Image imgFilter = filterIcon.getImage();  
		Image newimgFilter = imgFilter.getScaledInstance(18, 18,  java.awt.Image.SCALE_SMOOTH);  
		loadIcon = new ImageIcon(newimgFilter); 
		
		//Clean Icon and Item
	    ImageIcon cleanIcon = new ImageIcon("./gui/clean.png");
		Image imgClean = cleanIcon.getImage();  
		Image newimgClean = imgClean.getScaledInstance(18, 18,  java.awt.Image.SCALE_SMOOTH);  
		cleanIcon = new ImageIcon(newimgClean);
	    
	    // Show all elements icon		
		JMenuItem filterAllEle = new JMenuItem("Show All Chain Elements", loadIcon);
	    filterAllEle.addActionListener(this);
	    filterAllEle.setActionCommand(FILTER_ALL_ELE);
	    filtering.add(filterAllEle);
	    
	    // Show branch elements only
	  
	    JMenuItem filterTraceEle = new JMenuItem("Show Branch Elements", loadIcon);
	    filterTraceEle.addActionListener(this);
	    filterTraceEle.setActionCommand(FILTER_TRACE_ELE);
	    filtering.add(filterTraceEle);
	    
	    //Filter Model Selection
	    JMenuItem filter = new JMenuItem("Filter Model Element", loadIcon);
	    filter.addActionListener(this);
	    filter.setActionCommand(FILTER_MENU);
	    filtering.add(filter);
	    
	    //Filter Trace Selection from destination
	    JMenuItem filterTraceF = new JMenuItem("Filter Binding Target", loadIcon);
	    filterTraceF.addActionListener(this);
	    filterTraceF.setActionCommand(FILTER_TRACE_FORWARD);
	    filtering.add(filterTraceF);

	    //Filter Trace Selection from source
	    JMenuItem filterTraceB = new JMenuItem("Filter Binding Source", loadIcon);
	    filterTraceB.addActionListener(this);
	    filterTraceB.setActionCommand(FILTER_TRACE_BACKWARD);
	    filtering.add(filterTraceB);
	    
		//Clean Selection
	    JMenuItem clean = new JMenuItem("Clean Selection", cleanIcon);
	    clean.addActionListener(this);
	    clean.setActionCommand(CLEAN_MENU);
	    filtering.add(clean);
	    
	    menubar.add(filtering);

	    
	    // Code Menu
	    JMenu codeOperations = new JMenu("Code Projections");
		filtering.setMnemonic(KeyEvent.VK_O);
		
		  //Close Code Icon and Item
	    ImageIcon closeCodeIcon = new ImageIcon("./gui/close.png");
  		Image imgCloseCode = closeCodeIcon.getImage();  
  		Image newimgCloseCode= imgCloseCode.getScaledInstance(18, 18,  java.awt.Image.SCALE_SMOOTH);  
  		closeCodeIcon = new ImageIcon(newimgCloseCode);  

  	    JMenuItem closeCode = new JMenuItem("Close Current Viewer", closeCodeIcon);
  	    closeCode.addActionListener(this);
  	    closeCode.setActionCommand(VIEWER_CLOSE_CURRENT);
  	    codeOperations.add(closeCode);
		
	    //View Forward Bindings from Element Icon and Item
	    ImageIcon forwardBindingsIcon = new ImageIcon("./gui/forward.png");
		Image imgForwardMap = forwardBindingsIcon.getImage();  
		Image newimgForwardMap= imgForwardMap.getScaledInstance(17, 17,  java.awt.Image.SCALE_SMOOTH);  
		forwardBindingsIcon = new ImageIcon(newimgForwardMap);  

	    JMenuItem codeForward = new JMenuItem("Elements Forward Bindings", forwardBindingsIcon);
	    codeForward.addActionListener(this);
	    codeForward.setActionCommand(VIEWER_VIEW_ELEMENT_FORWARD);
	    codeOperations.add(codeForward);
	    
	    //View Backward Bindings from Element Icon and Item
	    ImageIcon backwardBindingsIcon = new ImageIcon("./gui/backward.png");
		Image imgBackwardMap = backwardBindingsIcon.getImage();  
		Image newimgBackwardMap= imgBackwardMap.getScaledInstance(17, 17,  java.awt.Image.SCALE_SMOOTH);  
		backwardBindingsIcon = new ImageIcon(newimgBackwardMap);  

	    JMenuItem codeBackward = new JMenuItem("Elements Backward Bindings", backwardBindingsIcon);
	    codeBackward.addActionListener(this);
	    codeBackward.setActionCommand(VIEWER_VIEW_ELEMENT_BACKWARDS);
	    codeOperations.add(codeBackward);
	    
	    //View Trace Icon and Item
	    ImageIcon codeTraceIcon = new ImageIcon("./gui/codeY.png"); 
  		Image imgCodeTrace = codeTraceIcon.getImage();  
  		Image newimgCodeTrace= imgCodeTrace.getScaledInstance(18, 18,  java.awt.Image.SCALE_SMOOTH);  
  		codeTraceIcon = new ImageIcon(newimgCodeTrace);  

  	    JMenuItem codeTrace = new JMenuItem("Single Binding", codeTraceIcon);
  	    codeTrace.addActionListener(this);
  	    codeTrace.setActionCommand(VIEWER_VIEW_TRACE);
  	    codeOperations.add(codeTrace);
	    
	    menubar.add(codeOperations);
	    
	    // Filter Menu
	    JMenu annotations = new JMenu("Annotations");
	    annotations.setMnemonic(KeyEvent.VK_V);
	    
	    //See annotations
	    ImageIcon annIcon = new ImageIcon("./gui/annotation.png");
		Image imgAnn = annIcon.getImage();  
		Image newimgAnn = imgAnn.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
		annIcon = new ImageIcon(newimgAnn); 
	    
  		JMenuItem annotation = new JMenuItem("Create/Modify Annotation", annIcon);
  		annotation.addActionListener(this);
  		annotation.setActionCommand(ANNOTATION);
  		annotations.add(annotation);
  	    
  	    menubar.add(annotations);

        this.setJMenuBar(menubar);	

	}

	
	public void setBufferPolicy(){
		graphPanel.setBufferPolicy();
	}
	
	public void addResizeListener(){
		addComponentListener(this);
	}
	
	private void showSplash(){
		SplashScreen sc = new SplashScreen(this);
		sc.load();
	}
	
	private void initializeController(){
		controller = new MasterController();
		ArrayList<MasterControllerInput> inputs = new ArrayList<MasterControllerInput>();
		
		for(TransformationSelectorPanel tsp : transSelector.getPanels()){
			
			//M2M Information Case
			if(!tsp.isM2T()){
				
				String M2Mtransformation = tsp.getTransformationText();
				String sourceMetamodel = tsp.getSourceMetamodelTxt();
				String targetMetamodel = tsp.getTargetMetamodelTxt();
				
				if(!(M2Mtransformation.isEmpty() || sourceMetamodel.isEmpty() || targetMetamodel.isEmpty())){
					M2MInput input = new M2MInput(sourceMetamodel, targetMetamodel, M2Mtransformation);
					inputs.add(input);
				}
			}
			else{
				String M2Ttransformation = tsp.getTransformationText();
				String sourceMetamodel = tsp.getSourceMetamodelTxt();
				String souceModel = tsp.getSourceModelTxt(); //Optional will run the T2C analysis
				
				if(!(M2Ttransformation.isEmpty() || sourceMetamodel.isEmpty() || souceModel.isEmpty())){ //case full M2T AND T2C
					M2TInput input = new M2TInput(sourceMetamodel, M2Ttransformation, souceModel);
					inputs.add(input);
				}
				else if(!(M2Ttransformation.isEmpty() || sourceMetamodel.isEmpty())){ // case only M2T
					M2TInput input = new M2TInput(sourceMetamodel, M2Ttransformation);
					inputs.add(input);
				}
			}

		}
		controller.setInputs(inputs);		
	}
	
	private void setWindowIcon(){
		ImageIcon img = new ImageIcon("./gui/icon.png");
		this.setIconImage(img.getImage());
	}
	
	
	private boolean validateData()
	{
		boolean one = false;
		for(TransformationSelectorPanel tsp : transSelector.getPanels()){
			
			String transformation = tsp.getTransformationText();
			String sourceMetamodel = tsp.getSourceMetamodelTxt();
			String targetMetamodel = tsp.getTargetMetamodelTxt();
			
			if(!tsp.isM2T()){
				if(!(transformation.isEmpty() || sourceMetamodel.isEmpty() || targetMetamodel.isEmpty())){
					one = true;
				}
			}
			else{
				if(!(transformation.isEmpty() || sourceMetamodel.isEmpty() )){
					one = true;
				}
			}
	
		}
		return one;
	}
	
	public void initializeDetailsTable(){
		//Find table dimensions
		int tableHeight;
		int twoColWidth;
		int threeColWidth;
		tableWidth = (int) ((Math.round(this.getSize().getWidth()/3) - 11));
		tableHeight = (int) (Math.round(this.getSize().getHeight())/10);
		twoColWidth = (int) ((Math.round(tableWidth/2)) - 1);
		threeColWidth = (int) (Math.round(tableWidth/3));
		
		detailsTable1 = detailsTblMgr.initialize(this, ATT_NAME, ATT_TYPE);
		detailsTable2 = detailsTblMgr.initialize(this, OUTBOUND_MAP_NAME, SOURCE);
		
		detailsTable2.addColumn(TARGET);
		detailsTable3 = detailsTblMgr.initialize(this, INBOUND_MAP_NAME, SOURCE);
		detailsTable3.addColumn(TARGET);
		
		table1  = new JTable(detailsTable1);
		table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table1.getColumnModel().getColumn(0).setPreferredWidth(twoColWidth);
		table1.getColumnModel().getColumn(1).setPreferredWidth(twoColWidth);
		
		table2 = new JTable(detailsTable2);
		table2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table2.getColumnModel().getColumn(0).setPreferredWidth(threeColWidth);
		table2.getColumnModel().getColumn(1).setPreferredWidth(threeColWidth);
		table2.getColumnModel().getColumn(2).setPreferredWidth(threeColWidth);
		
		table3  = new JTable(detailsTable3);
		table3.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table3.getColumnModel().getColumn(0).setPreferredWidth(threeColWidth);
		table3.getColumnModel().getColumn(1).setPreferredWidth(threeColWidth);
		table3.getColumnModel().getColumn(2).setPreferredWidth(threeColWidth);
		
		tableScrollPane1 = new JScrollPane(table1);
	    tableScrollPane1.setPreferredSize(new Dimension(tableWidth,tableHeight));
	    
	    tableScrollPane2 = new JScrollPane(table2);
	    tableScrollPane2.setPreferredSize(new Dimension(tableWidth,tableHeight));
	    tableScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    
	    tableScrollPane3 = new JScrollPane(table3);
	    tableScrollPane3.setPreferredSize(new Dimension(tableWidth,tableHeight));
	    tableScrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
	    
	    
	    tableScrollPane1.getViewport().setViewPosition(new Point(0,0));
	    tableScrollPane2.getViewport().setViewPosition(new Point(0,0));
	    tableScrollPane3.getViewport().setViewPosition(new Point(0,0));
	   
	    this.add(tableScrollPane1);
	    this.add(tableScrollPane2);
	    this.add(tableScrollPane3);
	    
	}
	
	private void resizeTables(){
		for (int i=0; i < table1.getColumnCount(); i++){
			table1.getColumnModel().getColumn(i).setPreferredWidth(detailsTblMgr.getLargestContent(this, table1, i, 3));
		}
		for (int i=0; i < table2.getColumnCount(); i++){
			table2.getColumnModel().getColumn(i).setPreferredWidth(detailsTblMgr.getLargestContent(this, table2, i, 3));
		}
		for (int i=0; i < table3.getColumnCount(); i++){
			table3.getColumnModel().getColumn(i).setPreferredWidth(detailsTblMgr.getLargestContent(this, table3, i, 3));
		}
	}
	
	public void windowClosing(WindowEvent e){
		
	}
	
	// View selected file from overview
	public void openFile(String cmd) {
		if (cmd.equals(VIEW_FILE)) {
			String file = graphPanel.getCanvasO().getSelectedBranch().getFile();
			String path = graphPanel.getCanvasO().getSelectedBranch().getPath();
			String nonXMIPath = path;
			
			//Check if path is a .atl.xmi (if so trim it)
			boolean trim = path.contains(".atl.xmi");
			if (trim) {
				nonXMIPath = path.substring(0, (path.length() - 4));
			}
			CodeViewer cv =getCodeViewer(nonXMIPath);
			
			if(cv==null){
				String title = getFileNameForDisplay(file);
				cv = addCodeViewer(title, nonXMIPath);
			}
			codeViewers.setSelectedComponent(cv);
		}
	}
	
	public static String getFileNameForDisplay(String path) {
		File f = new File(path);
		String name = f.getName();
		
		//strip .xmi from .atl.xmi files
		name = name.replaceAll("atl\\.xmi", "atl");
		
		return name;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		if(cmd.equals(CONFIG_MENU)){
			
			transSelector.setVisible(true);
		}
		else if (cmd.equals(SAVE)){
			graphPanel.save();
		}
		else if(cmd.equals(TransformationSelector.ADD)){
			transSelector.addNewTransformationPanel(false);
		}
		else if(cmd.equals(TransformationSelector.PROCESS)){
			if(validateData()){
				initializeController();
				controller.execute();
				transSelector.setVisible(false);			
				loadNewWorkSpace();
			}
			else{
				JOptionPane.showMessageDialog(this, "You have to select at least one transformation and model to process");
			}
			
		}
		else if(cmd.equals(TransformationSelector.LOAD_WS)){
			transSelector.setVisible(false);			
			loadNewWorkSpace();
		}
		else if(cmd.equals(CLEAN_MENU)){
			graphPanel.clear(); 
		}
		else if(cmd.equals(FILTER_MENU)){
			graphPanel.filter();
		}
		else if (cmd.equalsIgnoreCase(FILTER_TRACE_FORWARD)){
			graphPanel.filterTraceForward();
		}
		else if (cmd.equalsIgnoreCase(FILTER_TRACE_BACKWARD)){
			graphPanel.filterTraceBackward();
		}
		else if (cmd.equals(FILTER_ALL_ELE)){
			graphPanel.filterAllEle();
		}
		else if (cmd.equals(FILTER_TRACE_ELE)){
			graphPanel.filterTraceEle();
		}
		else if (cmd.equals(OVERVIEW)){
			graphPanel.loadOverview();
		}
		else if(cmd.equals(VIEWER_VIEW_ELEMENT_FORWARD)){
			
			highlightCodeElementForwardBindings();
			
		}
		else if(cmd.equals(VIEWER_VIEW_ELEMENT_BACKWARDS)){
			
			highlightCodeElemenBackwardBindings();
			
		}	
		else if(cmd.equals(VIEWER_VIEW_TRACE)){
			highlightCodeTrace();
		}
		
		else if(cmd.equals(VIEWER_CLOSE_CURRENT)){
			removeCurrentCodeViewer();
		}
		else if(cmd.equals(BRANCH_VIEW)){
			graphPanel.loadBranch();
		} 
		else if (cmd.equals(ANNOTATION)){
			//If an element is selected show it's annotation dialog
			if (!(graphPanel.getCanvasB().getSelectedEle() == null)){
				annDialog.showDialog(graphPanel.getCanvasB().getSelectedEle());
			}
		}
		else if (cmd.equals(AnnotationDialog.SAVE_ANNOTATION)){
			int type = annDialog.getSelectedRadio();
			Element ele = graphPanel.getCanvasB().getSelectedEle();
			String annText = annDialog.getAnnotationText();
			
			if (annText.equals("")){
				//Remove annotation
				if (!(ele.getAnnotation() == null)){
					ele.setAnnotation(null);
				}
			} else if (ele.getAnnotation() == null){
				//Create
				Annotation newAnnotation = new Annotation(annText, type, ele);
				ele.setAnnotation(newAnnotation);
			} else {
				//Modify
				ele.getAnnotation().setContent(annText);
				ele.getAnnotation().setType(type);
			}
			
			annDialog.setVisible(false);
			graphPanel.getCanvasB().repaint();
		}
	}
	

	


	public void updateDetailsTable(Object obj){
		
		Trace trace = new Trace(null, null, "", "", "", "", "", "");
		Model model = new Model(null, null);
		int objTableType;
		int objSubTableType;
		
		//Decide what type of table the user is looking for
		if (obj == null) {
			//Flush tables
			detailsTblMgr.flushTables(this, detailsTable1);
			detailsTblMgr.flushTables(this, detailsTable2);
			detailsTblMgr.flushTables(this, detailsTable3);
		} else if (obj.getClass().equals(model.getClass())){
			objTableType = MODELTABLE;
			objSubTableType = MODELTABLE;
			checkTableChange(objSubTableType, objTableType, obj);
		} else if (obj.getClass().equals(trace.getClass())){
			objTableType = TRACETABLE;
			Trace t = (Trace) obj;
			switch (t.getNatureType()){
			case "M2M":
				objSubTableType = TRACETABLE_M2M;
				break;
			case "M2T":
				objSubTableType = TRACETABLE_M2T;
				break;
			default:
				objSubTableType = TRACETABLE_T2C;
				break;
			}
			checkTableChange(objSubTableType, objTableType, obj);
		} else {
			objTableType = ELETABLE;
			objSubTableType = ELETABLE_MODEL;
			checkTableChange(objSubTableType, objTableType, obj);
		}
		
		resizeTables();
	}
	
	
	private void checkTableChange(int objSubTableType, int objTableType, Object obj) {
		//Check if the type of table is changed
		if (this.getSubTableType() == objSubTableType){
			//Table type has not changed, only rows need updating
			if (objTableType == ELETABLE){
				detailsTblMgr.updateElementTable(this, (Element) obj);
			} else if (objTableType == MODELTABLE){
				detailsTblMgr.updateModelTable(this, (Model) obj);
			} else {
				detailsTblMgr.updateTraceTable(this, (Trace) obj);
			}
		} else {
			detailsTable1.setColumnCount(0);
			detailsTable2.setColumnCount(0);
			detailsTable3.setColumnCount(0);
			//Table type has changed
			if (objTableType == ELETABLE){
				
				detailsTable1.addColumn(ATT_NAME);
				detailsTable1.addColumn(ATT_TYPE);
				detailsTable2.addColumn(OUTBOUND_MAP_NAME);
				detailsTable2.addColumn(SOURCE);
				detailsTable2.addColumn(TARGET);		
				detailsTable3.addColumn(INBOUND_MAP_NAME);
				detailsTable3.addColumn(SOURCE);
				detailsTable3.addColumn(TARGET);
				this.setSubTableType(ELETABLE_MODEL);
				
				this.setTableType(ELETABLE);
				detailsTblMgr.updateElementTable(this, (Element) obj);
				
			} else if (objTableType == MODELTABLE){
				detailsTable1.addColumn(ELEMENT_NAME);
				detailsTable1.addColumn(NUM_ATTRIBUTES);
				detailsTable2.addColumn(OUTBOUND_MAP_NAME);
				detailsTable2.addColumn(SOURCE);
				detailsTable2.addColumn(TARGET);		
				detailsTable3.addColumn(INBOUND_MAP_NAME);
				detailsTable3.addColumn(SOURCE);
				detailsTable3.addColumn(TARGET);
				
				this.setTableType(MODELTABLE);
				this.setSubTableType(MODELTABLE);
				detailsTblMgr.updateModelTable(this, (Model) obj);
			} else {
				
				if (objSubTableType == TRACETABLE_M2M){
					detailsTable1.addColumn(MAPPING_NAME);
					detailsTable1.addColumn(TYPE);
					detailsTable1.addColumn(MAPPING);
					detailsTable2.addColumn(SOURCE_ATT_PARENT);
					detailsTable2.addColumn(SOURCE_ATT_NAME);
					detailsTable2.addColumn(SOURCE_ATT_TYPE);
					detailsTable3.addColumn(TARGET_ATT_PARENT);
					detailsTable3.addColumn(TARGET_ATT_NAME);
					detailsTable3.addColumn(TARGET_ATT_TYPE);
					this.setSubTableType(TRACETABLE_M2M);
				} else if (objSubTableType == TRACETABLE_M2T){
					detailsTable1.addColumn(MAPPING_NAME);
					detailsTable1.addColumn(TYPE);
					detailsTable1.addColumn(MAPPING);
					detailsTable2.addColumn(SOURCE_ATT_PARENT);
					detailsTable2.addColumn(SOURCE_ATT_NAME);
					detailsTable2.addColumn(SOURCE_ATT_TYPE);
					detailsTable3.addColumn(TEMPLATE_FILE);
					detailsTable3.addColumn(TEMPLATE_LINE);
					detailsTable3.addColumn(TEMPLATE_TYPE);
					this.setSubTableType(TRACETABLE_M2T);
				} else {
					detailsTable1.addColumn(MAPPING_NAME);
					detailsTable1.addColumn(TYPE);
					detailsTable1.addColumn(MAPPING);
					detailsTable2.addColumn(TEMPLATE_FILE);
					detailsTable2.addColumn(TEMPLATE_LINE);
					detailsTable2.addColumn(TEMPLATE_TYPE);
					detailsTable3.addColumn(CODE_FILE);
					detailsTable3.addColumn(CODE_LINE);
					detailsTable3.addColumn(CODE_TYPE);
					this.setSubTableType(TRACETABLE_T2C);
				}
				
				this.setTableType(TRACETABLE);
				detailsTblMgr.updateTraceTable(this, (Trace) obj);
				
			}
		}
	}
	
	
	private void loadNewWorkSpace(){
		// Close all code viewers
		while(codeViewerCount > 0) {
			removeCurrentCodeViewer();
		}
		
		graphPanel.load();
		graphPanel.repaint();
		this.setVisible(true);
		
		zoneIds = new ArrayList<String>();
	}
	
	public AnnotationDialog getAnnDialog() {
		return annDialog;
	}

	public int getTableType() {
		return tableType;
	}
	
	public int getGraphWidth(){
		return this.graphScroll.getWidth();
	}
	
	public int getGraphHeight(){
		return this.graphScroll.getHeight();
	}

	public void setTableType(int tableType) {
		this.tableType = tableType;
	}
	
	public DefaultTableModel getDetailsTable1(){
		return detailsTable1;
	}
	
	public void setDetailsTable1(DefaultTableModel table){
		this.detailsTable1 = table;
	}
	
	public DefaultTableModel getDetailsTable2(){
		return detailsTable2;
	}
	
	public void setDetailsTable2(DefaultTableModel table){
		this.detailsTable2 = table;
	}
	
	public DefaultTableModel getDetailsTable3(){
		return detailsTable3;
	}
	
	public void setDetailsTable3(DefaultTableModel table){
		this.detailsTable3 = table;
	}
	
	public GraphPanel getGraphPanel(){
		return graphPanel;
	}

	public int getSubTableType() {
		return subTableType;
	}

	public void setSubTableType(int subTableType) {
		this.subTableType = subTableType;
	}
	
	public int getTableWidth() {
		return tableWidth;
	}
	
	public int getCodeViewHeight() {
		return codeViewHeight;
	}
	
	public int getCodeViewWidth() {
		return codeViewWidth;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent event) {
		//Takes care of resizing when window is maximized
		componentResized(event);
	}

	/**
	 * Called when the user resizes the mainVis.
	 * This function resizes the parts in mainVis to 
	 * accommodate the new window size.
	 */
	@Override
	public void componentResized(ComponentEvent event) {
		//Inital resize calculations
		visHeight = (int) ((Math.round(this.getSize().getHeight())/16) * 13);
		codeViewHeight = visHeight;
		tableWidth = (int) ((Math.round(this.getSize().getWidth()/3) - 11));
		tableHeight = (int) (((Math.round(this.getSize().getHeight())/16) * 2) - 20);
		twoColWidth = (int) (Math.round(tableWidth/2));
		threeColWidth = (int) (Math.round(tableWidth/3));
		
		//Resize table columns
		table1.getColumnModel().getColumn(0).setPreferredWidth(twoColWidth);
		table1.getColumnModel().getColumn(1).setPreferredWidth(twoColWidth);
		
		table2.getColumnModel().getColumn(0).setPreferredWidth(threeColWidth);
		table2.getColumnModel().getColumn(1).setPreferredWidth(threeColWidth);
		table2.getColumnModel().getColumn(2).setPreferredWidth(threeColWidth);
		
		table3.getColumnModel().getColumn(0).setPreferredWidth(threeColWidth);
		table3.getColumnModel().getColumn(1).setPreferredWidth(threeColWidth);
		table3.getColumnModel().getColumn(2).setPreferredWidth(threeColWidth);
		
		//Resize split pane
		splitPaneWidth = (int) this.getSize().getWidth()- 21;
		splitPaneHeight = visHeight;
		splitPane.getDividerLocation();
		
		splitPane.setPreferredSize(new Dimension(splitPaneWidth, splitPaneHeight));
		tableScrollPane1.setPreferredSize(new Dimension(tableWidth,tableHeight));
		tableScrollPane2.setPreferredSize(new Dimension(tableWidth,tableHeight));
		tableScrollPane3.setPreferredSize(new Dimension(tableWidth,tableHeight));	
		
		
		resizeCodeViews();
		
		graphPanel.refresh();
		
	}
	
	// Ensures that all views fit in the current window size
	private void resizeCodeViews() {
		Dimension preferredSize = new Dimension(codeViewers.getWidth() - VERTICAL_SCROLL_BAR_SIZE, 
				codeViewers.getHeight() - HORIZONTAL_SCROLL_BAR_SIZE);
		for (String str : viewers.keySet()){
			
			viewers.get(str).setScrollPaneSize(preferredSize);
		
			viewers.get(str).getSp().setPreferredSize(preferredSize);
			
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// Auto-generated method stub
		
	}
	
	public JScrollPane getGraphScroll() {
		return graphScroll;
	}
	
	public int getVisHeight() {
		return visHeight;
	}

	public void setVisHeight(int visHeight) {
		this.visHeight = visHeight;
	}

	public int getVisWidth() {
		return visWidth;
	}

	public void setVisWidth(int visWidth) {
		this.visWidth = visWidth;
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// CODE VIEWER MANAGEMENT CODE
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds a new code viewer to the gui for a given file path.
	 * @param title
	 * @param file
	 * @return
	 */
	private CodeViewer addCodeViewer(String title, String file){
		removeEmptyCodeViewer();
		CodeViewer viewer = new CodeViewer(this, file);
		codeViewers.add(title, viewer);
		codeViewers.setSelectedComponent(viewer);
		viewers.put(file, viewer);
		codeViewerCount++;
		resizeCodeViews();
		return viewer;
	}
	
	/**
	 * Verifies if there a code viewer open for a file path. 
	 * @param file (fully qualified name, this is complete path and file name)
	 * @return null if there is no code viewer available; the code viewer if found.
	 */
	public CodeViewer getCodeViewer(String file){
		
		if(viewers.keySet().contains(file)){
			return viewers.get(file);
		}
		else{

			return null;
		}
	}

	/**
	 * Closes the code viewer that is currently open.
	 */
	private void removeCurrentCodeViewer(){
	
		int current = codeViewers.getSelectedIndex();
		CodeViewer currentComponent = (CodeViewer)codeViewers.getSelectedComponent();
		
		viewers.remove(currentComponent.getFile());
		
		codeViewers.remove(current);
		codeViewerCount --;		
		if(codeViewerCount<=0){
			addEmptyCodeViewer();
			codeViewerCount=0;
		}
	}
	
	/**
	 * Check if there is only one code viewer (empty) and deletes it
	 * @return true if it was the case
	 */
	private boolean removeEmptyCodeViewer(){
		
		if(codeViewerCount == 0){
			codeViewers.remove(viewers.get(EMPTY_FILE));
			
			return true;
		}
		return false;
	}
	
	/**
	 * Adds an empty code Viewer. Used when initializing a new GUI or when the users closes all the viwers.
	 * The idea is to always have a component in that part of the layout, even if it is empty.
	 */
	private void addEmptyCodeViewer(){
		CodeViewer viewer = new CodeViewer(this);
		viewers.put(EMPTY_FILE, viewer);
		codeViewers.add("Code Viewer", viewer);
		resizeCodeViews();

	}

	/**
	 * EVENT HANDLER HELPER: captures the event to "highlight an individual mapping", 
	 * delegates to highlightCodeTraceM2M and highlightCodeTraceM2TandT2C depending of the type of mapping to highlight.
	 */
	public void highlightCodeTrace(){
		
		Trace trace = 	graphPanel.getSelectedTrace();
		
		if (trace == null) {
			return;
		}
		
		System.out.println(trace.getNatureType());
		
		if(trace.getNatureType().equals("M2M")){
			highlightCodeTraceM2M(trace, true);
		}
		else if(trace.getNatureType().equals("M2T") || trace.getNatureType().equals("T2C")){
			highlightCodeTraceM2TandT2C(trace);
		}
		
	}
	
	/**
	 * Opens and highlights an individual trace in an .mtl file
	 * If the file has a viewer assigned this method will use it.
	 * @param trace
	 */
	private void highlightCodeTraceM2TandT2C(Trace trace){
		String traceFile = trace.getPath();
		String line = trace.getDestination().getName();
		
		CodeViewer cv =getCodeViewer(traceFile);
		
		if(cv==null){
			cv = addCodeViewer(getSimpleFileName(traceFile), traceFile);
		}
		
		codeViewers.setSelectedComponent(cv);
		
		cleanHighlights(cv);
		
		if (trace.getType().equals(TraceType.Implicit)){
			cv.highlightLine(Integer.parseInt(line), Colors.LIGHT_RED);
		}
		else if (trace.getType().equals(TraceType.Explicit)){
			cv.highlightLine(Integer.parseInt(line), Colors.LIGHT_GREEN);
		}
	}
	
	/**
	 * Opens and highlights all the lines impacted by a M2M trace in an .atl file
	 * If the file has a viewer assigned this method will use it.
	 * @param trace
	 */
	private void highlightCodeTraceM2M(Trace trace, boolean cleanHighlights){
		// Cleaning the .xmi tail ATL files should line in the same folder than their parsed xmi versions
		String traceFile = trace.getPath().replaceAll(".xmi", "");
		String sourceAttribute = trace.getOrigin().getName();
		String targetAttribute = trace.getDestination().getName();
		String traceName = trace.getName();
		
		CodeViewer cv =getCodeViewer(traceFile);
		
		if(cv==null){
			cv = addCodeViewer(getSimpleFileName(traceFile), traceFile);
		}
		
		codeViewers.setSelectedComponent(cv);
		codeViewers.setSelectedComponent(cv);
		
		if (cleanHighlights) {
			cleanHighlights(cv);
			cv.highlightZone("rule "+traceName, "{", "}", Colors.LIGHT_YELLOW);
		}
		
		cv.highlightSearchAll("rule "+traceName, Colors.LIGHT_CYAN);
		cv.highlightSearchInZoneFirst(traceName, "{", "}", targetAttribute, Colors.LIGHT_BLUE);
		
		if (trace.getType().equals(TraceType.Implicit)){
			cv.highlightSearchInZoneFirst(traceName, targetAttribute, "}", "."+sourceAttribute, Colors.LIGHT_RED);
			cv.highlightSearchInZoneFirst(traceName, targetAttribute, "}", "."+trace.getImplicitReferenceName(), Colors.LIGHT_RED);
		}
		else if (trace.getType().equals(TraceType.Explicit)){
			cv.highlightSearchInZoneFirst(traceName, targetAttribute, "}", "."+sourceAttribute, Colors.LIGHT_GREEN);
		}		
		
		highlightCodeTraceM2MForHelper(cv, trace, targetAttribute, sourceAttribute);
	}
	
	private void highlightCodeTraceM2MForHelper(CodeViewer cv, Trace trace, String targetAttribute, String sourceAttribute) {
		String implicitRefName = trace.getImplicitReferenceName();
		if (implicitRefName != null && implicitRefName.length() > 0) {
			String zoneId = " " + implicitRefName;
			if (!zoneIds.contains(zoneId)) {
				zoneIds.add(zoneId);
				
				cv.highlightSearchAll(zoneId, Colors.LIGHT_CYAN);
				cv.highlightZone(zoneId, ":", ";", Colors.LIGHT_YELLOW);
				cv.highlightSearchAll(zoneId, Colors.LIGHT_CYAN);
				cv.highlightSearchInZoneFirst(zoneId, ":", ";", targetAttribute, Colors.LIGHT_BLUE);
			}
			
			if (trace.getType().equals(TraceType.Implicit)){
				cv.highlightSearchInZoneFirst(zoneId, implicitRefName, ";", "."+sourceAttribute, Colors.LIGHT_RED);
			}
			else if (trace.getType().equals(TraceType.Explicit)){
				cv.highlightSearchInZoneFirst(zoneId, implicitRefName, ";", "."+sourceAttribute, Colors.LIGHT_GREEN);
			}	
		}
	}
	
	/**
	 * EVENT HANDLER HELPER: captures the "highlight backward bindings" event, finds the respective traces,
	 * and delegates the highlightMultipleCodeTrace method to affect the code viewers as necessary.
	 */
	public void highlightCodeElemenBackwardBindings(){
		
		boolean newGroup = true;
		ArrayList<Element> elems = 	graphPanel.getSelectedElements();
		
		if(elems.isEmpty()){
			JOptionPane.showMessageDialog(this, "You have to select at least one model element");
		}
		
		for(Element s : elems){
			
			ArrayList<Trace> tsi =  graphPanel.getInboundTraces(s);
			
			if(tsi!=null){
				highlightMultipleCodeTrace(tsi, newGroup);
				newGroup = false;
			}
			else{
				JOptionPane.showMessageDialog(this, s.getName() + " has no backward traces to see.");
			}
		}

		
	}
	
	/**
	 * EVENT HANDLER HELPER: captures the "highlight forward bindings" event, finds the respective traces,
	 * and delegates the highlightMultipleCodeTrace method to affect the code viewers as necessary.
	 */
	public void highlightCodeElementForwardBindings(){
		boolean newGroup = true;
		ArrayList<Element> elems = 	graphPanel.getSelectedElements();		
		
		if(elems.isEmpty()){
			JOptionPane.showMessageDialog(this, "You have to select at least one model element");
		}
		
		for(Element s : elems){
		
			ArrayList<Trace> tso =  graphPanel.getOutboundTraces(s);
			
			if(tso!=null){
				highlightMultipleCodeTrace(tso, newGroup);
				newGroup = false;
			}
			else{
				JOptionPane.showMessageDialog(this, s.getName() + " has no forward traces to see.");
			}
		}
	}
	
	/**
	 * Opens and highlights all the lines impacted by a collection of traces both M2M and M2T
	 * Uses highlightMultipleCodeTraceM2M and highlightMultipleCodeTraceM2T methods
	 * If the file has a viewer assigned this method will use it.
	 * This method is called when a user wants to project the code forward or backward traces of a model element or template.
	 * @param trace
	 */
	public void highlightMultipleCodeTrace(ArrayList<Trace> traces, boolean flush){
		
		if(traces!=null){
			
			if(traces.get(0).getNatureType().equals("M2M")){
				highlightMultipleCodeTraceM2M(traces, flush);
			}
			else if (traces.get(0).getNatureType().equals("M2T") || traces.get(0).getNatureType().equals("T2C")){
				highlightMultipleCodeTraceM2T(traces, flush);
			}
		}
		
	}
	
	/**
	 * Opens and highlights all the lines impacted by a collection of M2M traces
	 * Makes sure that the highlights for a file do not overlap with flush.
	 * Works with the CodeViewer to format the highlight of M2M ATL rules.
	 * @param traces
	 * @param flush
	 */
	private void highlightMultipleCodeTraceM2M(ArrayList<Trace> traces, boolean flush){
		//First highlight the rules with the background yellow (as a result of highlighting precedence)
		boolean firstTime = true;
		for(Trace trace : traces){
			String traceFile = trace.getPath().replaceAll(".xmi", "");
			String traceName = trace.getName();
			
			CodeViewer cv =getCodeViewer(traceFile);
			
			if(cv==null){
				cv = addCodeViewer(getSimpleFileName(traceFile), traceFile);
			}
			
			if(firstTime && flush){
				
				cleanHighlights(cv);
				firstTime=false;
			}
			codeViewers.setSelectedComponent(cv);
			
			String zoneId = "rule "+traceName; 
			if (!zoneIds.contains(zoneId)) {
				zoneIds.add(zoneId);
				cv.highlightZone(zoneId, "{", "}", Colors.LIGHT_YELLOW);
			}
		}
				
		for(Trace trace : traces){
			highlightCodeTraceM2M(trace, false);
		}
	}
	
	/**
	 * Opens and highlights all the lines impacted by a collection of M2T traces
	 * Makes sure that the highlights for a file do not overlap with flush.
	 * Works with the CodeViewer to format the highlight of M2M Acceleo rules.
	 * @param traces
	 * @param flush
	 */
	private void highlightMultipleCodeTraceM2T(ArrayList<Trace> traces, boolean flush){
		//First highlight the rules with the background yellow (as a result of highlighting precedence)
		boolean firstTime = true;
		for(Trace trace : traces){
			String traceFile = trace.getPath();
			String line = trace.getDestination().getName();
			
			CodeViewer cv =getCodeViewer(traceFile);
			
			if(cv==null){
				cv = addCodeViewer(getSimpleFileName(traceFile), traceFile);
			}
			
			if(firstTime && flush){
				
				cleanHighlights(cv);
				firstTime=false;
			}
			codeViewers.setSelectedComponent(cv);
			
			if (trace.getType().equals(TraceType.Implicit)){
				cv.highlightLine(Integer.parseInt(line), Colors.LIGHT_RED);
			}
			else if (trace.getType().equals(TraceType.Explicit)){
				cv.highlightLine(Integer.parseInt(line), Colors.LIGHT_GREEN);
			}
			
		}
		
	}
	
	/**
	 * Utility method that takes the fully qualified name of a file, 
	 * and extracts the simple file name (i.e. removes the path, leaves the file name with the extension)
	 * @param file
	 * @return
	 */
	private String getSimpleFileName(String file){
		
		if(file.contains("/")){
			String [] path = file.split("/");
			return path[path.length-1];
		}
		else if (file.contains("\\")){
			String [] path = file.split("\\\\");
			return path[path.length-1];
		}
		return file;
	}
	
	private void cleanHighlights(CodeViewer cv) {
		cv.cleanHighlights();
		zoneIds = new ArrayList<String>();
	}
		
	//////////////////////////////////////////////////////////////////////////
	// MASTER MAIN
	//////////////////////////////////////////////////////////////////////////
	
	public static void main (String... args){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		
		// Constructor receives true or false to indicate test mode.
		// Test mode load the current files located at exchange.
		MainVis mv = new MainVis();
		mv.setVisible(true);
		mv.setBufferPolicy();
		mv.addResizeListener();
		
	}

	
}
