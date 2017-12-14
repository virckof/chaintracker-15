package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.ca.ualberta.ssrg.chaintracker.gui.MainVis;


/**
 * Graphical Panel for the main visualization (parallel coordinates)
 * Manages visualization events
 *
 */
@SuppressWarnings("serial")
public class GraphPanel extends JPanel implements MouseListener{

	//Min panel size
	private int minX;
	private int minY;

	//View Modes
	public static String OVERVIEW = "OVERVIEW";
	public static String BRANCH = "BRANCH";

	//Main Window instance
	private MainVis mainWindow;

	public static final String DEF_CANVAS = "./gui/backgroud.png";

	private BranchCanvas canvasB;
	private OverviewCanvas canvasO;

	private int currentWidth;
	private int currentHeight;

	// Data hashtable constructor
	private FrontendData hashData;

	//Boolean to determine if we are in high level overview or branch view
	private String viewMode = OVERVIEW;

	//For Double Click detection
	private boolean firstClick = true;


	private ArrayList<Element> eleList;
	private boolean isClicked;
	private Point clickLocation;
	private boolean prevDrawHoverInfo = false;
	private Point hoverLocation;

	public GraphPanel(MainVis main){
		mainWindow = main;

		currentHeight = mainWindow.getVisHeight();
		currentWidth = mainWindow.getVisWidth();

		minX = main.getVisWidth();
		minY = main.getVisHeight();

		hashData = new FrontendData();

		canvasB = new BranchCanvas(this, main, hashData);
		canvasO = new OverviewCanvas(this, main, hashData);

		this.add(canvasO);

		addMouseListener(this);
    	addMouseMotionListener(new MouseMotionListener(){
    		@Override
    		public void mouseMoved(MouseEvent mEvent){
    			if (viewMode.equals(BRANCH)){
    				checkForHover(mEvent);
    			}
    		}

    		@Override
    		public void mouseDragged(MouseEvent mEvent){
    			if (viewMode.equals(BRANCH)){
    				checkForHover(mEvent);
    			}
    		}
    	});
	}



	public void refresh(){
		minX = mainWindow.getVisWidth();
		minY = mainWindow.getVisHeight();

		if ((mainWindow.getVisHeight() > currentHeight) || (mainWindow.getVisWidth() > currentWidth)){
			currentHeight = mainWindow.getVisHeight();
			currentWidth = mainWindow.getVisWidth();
		}

		if (viewMode.equals(OVERVIEW)){
			canvasO.spaceModels();
		} else {
			canvasB.spaceModels();
		}

		this.setSize(currentWidth, currentHeight);

	}

	/**
     * Checks to see if the user is hovering over one of the
     * round branch buttons
     */
    public void checkForHover(MouseEvent mEvent){
    	boolean isOn;
    	hoverLocation = screenShiftAdjustment(mEvent.getPoint(), -4);
    	//hoverLocation = mEvent.getPoint();

    	if (canvasB.getBranchButtons() != null){
    		for (BranchButton button : canvasB.getBranchButtons()){
    			isOn = button.ClickOn(hoverLocation.x, hoverLocation.y);
    			if (isOn){
    				canvasB.setButtonInfo(button);
    				canvasB.setDrawHoverInfo(true);
    				if(!prevDrawHoverInfo){

    					repaint();
    					prevDrawHoverInfo = canvasB.isDrawHoverInfo();
    				}
    				break;
    			} else {
    				canvasB.setDrawHoverInfo(false);
    				if(prevDrawHoverInfo){

    					repaint();
    					prevDrawHoverInfo = canvasB.isDrawHoverInfo();
    				}
    			}
    		}
    	}
    }

	/**
	 * Called when filter button is pressed and subsequently calls the
	 * DrawingCanvas, telling it to update the visualization
	 */
	public void filter(){
		if (viewMode.equals(BRANCH)){
			canvasB.filterVis();
		}
	}

	/**
	 * Called when the user wants to filter by the currently selected trace forward.
	 */
	public void filterTraceForward(){
		if (viewMode.equals(BRANCH)){
			canvasB.filterTraceForward();
			canvasB.setFilteringTrace(canvasB.getSelectedTrace());
		}
	}

	/**
	 * Called when the user wants to filter by the currently selected trace backward.
	 */
	public void filterTraceBackward(){
		if (viewMode.equals(BRANCH)){
			canvasB.filterTraceBackward();
			canvasB.setFilteringTrace(canvasB.getSelectedTrace());
		}
	}

	public void filterAllEle(){
		if (viewMode.equals(BRANCH)){
			canvasB.toggleEleFilter(true);
			refresh();
		}
	}

	public void filterTraceEle(){
		if (viewMode.equals(BRANCH)){
			canvasB.toggleEleFilter(false);
			refresh();
		}
	}

	public void loadOverview(){
		if (viewMode.equals(BRANCH)){
			canvasB.setSelectedEle(null);
			this.remove(canvasB);
			this.add(canvasO);
			canvasO.getSelectedBranch().setSelected(false);
			canvasO.setSelectedBranch(null);
			canvasO.setDisplayInfo(false);
		}
		viewMode = OVERVIEW;
		canvasO.setClickedModel(null);
		refresh();
		canvasO.repaint();
	}

	public void loadBranch(){
		if (!(canvasO.getSelectedBranch() == null)){
			ModelTrace t = canvasO.getSelectedBranch();

			if (viewMode.equals(OVERVIEW)){
				this.remove(canvasO);
				this.add(canvasB);
			}
			viewMode = BRANCH;
			canvasB.loadBranchPath(t);
			refresh();

		}
	}

	/**
	 * Called when the clear button is pressed and subsequently calls
	 * the DrawingCanvas, telling it to reset the path and update
	 * the visualization. Also flushes the tables.
	 */
	public void clear(){
		if (viewMode.equals(BRANCH)){
			canvasB.clearVis();
			mainWindow.updateDetailsTable(null);
		}
	}


    /**
     * Returns the elements that are currently highlighted in the canvas
     */
    public ArrayList<Element> getSelectedElements(){
    	return canvasB.getSelectedElements();
    }

    /**
     * Returns all inbound traces in the canvas given an element
     */
    public ArrayList<Trace> getInboundTraces(Element ele){

    	return canvasB.getInboundTraces(ele);
    }

    /**
     * Returns all outbound traces in the canvas given an element
     */
    public ArrayList<Trace> getOutboundTraces(Element ele){

    	return canvasB.getOutboundTraces(ele);
    }

    /**
     * Returns the selected trace from the canvas
     */
    public Trace getSelectedTrace(){

    	return canvasB.getSelectedTrace();
    }


	public void setBufferPolicy() {
	//	canvas.createBufferStrategy(4);

	}

	public void load() {

		if (viewMode.equals(BRANCH)) {
			clear();
			loadOverview();
			repaint();
		}

		hashData.load();
		canvasO.load();

	}

	/**
	 * Adjusts for the shift in location between GraphPanel
	 * and its canvases
	 */
	private Point screenShiftAdjustment(Point click, int adjustment){
		int x = (int) (click.getX() + adjustment);
		int y = (int) (click.getY() + adjustment);
		return new Point(x, y);
	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public FrontendData getHashData() {
		return hashData;
	}

	/**
	 * Methods in MouseListener
	 */
	@Override
	public void mouseClicked(MouseEvent mEvent) {
		boolean clickDetermined = false;
		clickLocation = screenShiftAdjustment(mEvent.getPoint(), -4);

		if (viewMode.equals(BRANCH)) {
			//Handle mouse click in branch view
			if (SwingUtilities.isLeftMouseButton(mEvent)) {
				//Handle left mouse click
				branchLeftClick(clickDetermined);
			} else if (SwingUtilities.isRightMouseButton(mEvent)) {
				//Handle right mouse click
				branchRightClick(clickDetermined);
			}
		} else if (viewMode.equals(OVERVIEW)){
			//Handle mouse click in overview
			overviewLeftClick(clickDetermined);
		}
	}

	private void branchRightClick(boolean clickDetermined) {
		eleList = canvasB.getFilterEle();

		//Determine if element was clicked
		for (Element ele : eleList){
			isClicked = ele.ClickOn(clickLocation.x, clickLocation.y);
			if (isClicked){
				ele.setColour(Color.ORANGE);
				canvasB.setFilterHold(true);
				canvasB.setDisplayMenu(true);
				canvasB.setMenuEle(ele);
				clickDetermined = true;
				firstClick = false;
				repaint();
			}
		}
		if (!clickDetermined){
			canvasB.setMenuEle(null);
			canvasB.setDisplayMenu(false);
			canvasB.destroyMenuButtons();
		}
	}

	private void overviewLeftClick(boolean clickDetermined) {

		//Determine if branch button was clicked
		if (!clickDetermined) {
			isClicked = canvasO.getBranchButton().ClickOn(clickLocation.x, clickLocation.y);
			if (isClicked){
				clickDetermined = true;
				firstClick = true;
				loadBranch();
				repaint();
			}
		}

		//Determine if code view button was clicked
		if (!clickDetermined) {
			isClicked = canvasO.getCodeView().ClickOn(clickLocation.x, clickLocation.y);
			if (isClicked){
				clickDetermined = true;
				firstClick = true;
				mainWindow.openFile(MainVis.VIEW_FILE);
			}
		}

		//Determine if model was clicked
		if(!clickDetermined){
			for (ModelButton button : canvasO.getModelButtons()){
				isClicked = button.ClickOn(clickLocation.x, clickLocation.y);
				if(isClicked){
					//Show pie charts
					mainWindow.updateDetailsTable(button.getModel());
					clickDetermined = true;
					canvasO.setDisplayInfo(false);
					canvasO.setClickedModel(button.getModel());
					canvasO.repaint();
					break;
				}
			}
			if (!clickDetermined){
				canvasO.setClickedModel(null);
			}
		}

		//Determine if ModelTrace was clicked
		if (!clickDetermined){
			for (ModelTrace t : canvasO.getModelOverviewTraces()){
				isClicked = t.ClickOn(clickLocation.x, clickLocation.y);
				if(isClicked){
					canvasO.setSelectedBranch(t);
					clickDetermined = true;
					t.setSelected(true);
					canvasO.setDisplayInfo(true);
				} else {
					t.setSelected(false);
				}
			}
			canvasO.repaint();
			if (!clickDetermined){
				canvasO.setSelectedBranch(null);
				canvasO.setDisplayInfo(false);
			}
		}

	}

	private void branchLeftClick(boolean clickDetermined) {
		eleList = canvasB.getFilterEle();

		//Determine if aspect button was clicked
		if (!clickDetermined) {
			if (canvasB.getAspectsBtn() != null) {
				isClicked = canvasB.getAspectsBtn().ClickOn(clickLocation.x, clickLocation.y);
				if (isClicked){
					canvasB.setAnnSticky(true);
					clickDetermined = true;
					repaint();
				}
			}
		}

		//Determine if annotation button was clicked
		if (!clickDetermined) {
			if (canvasB.getAnnotateBtn() != null) {
				isClicked = canvasB.getAnnotateBtn().ClickOn(clickLocation.x, clickLocation.y);
				if (isClicked){
					clickDetermined = true;
					canvasB.setSelectedEle(canvasB.getMenuEle());
					mainWindow.getAnnDialog().showDialog(canvasB.getSelectedEle());
				}
			}
		}

		//Determine if filter button was clicked
		if (!clickDetermined) {
			if (canvasB.getFilterBtn() != null) {
				isClicked = canvasB.getFilterBtn().ClickOn(clickLocation.x, clickLocation.y);
				if (isClicked){
					canvasB.resetFilter();
					Element filEle = canvasB.getMenuEle();
					canvasB.setFilterHold(true);
					canvasB.setSelectedEle(filEle);
					canvasB.addFilteringEle(filEle);
					clickDetermined = true;
					canvasB.filterVis();
					repaint();
					mainWindow.updateDetailsTable(filEle);
				}
			}
		}

		//No menu button was clicked
		canvasB.destroyMenuButtons();

		//Determine if element was clicked
		for (Element ele : eleList){
			isClicked = ele.ClickOn(clickLocation.x, clickLocation.y);
			if (isClicked){
				canvasB.setFilterHold(true);
				canvasB.setSelectedEle(ele);
				ele.setColour(Color.ORANGE);
				if (canvasB.getFilteringEle().contains(ele)){
					//Do nothing
				} else {
					canvasB.addFilteringEle(ele);
				}
				clickDetermined = true;
				firstClick = true;
				repaint();
				mainWindow.updateDetailsTable(ele);
				break;
			}
		}

		//Determine if trace was clicked
		if (!clickDetermined){
			for (Trace t : canvasB.getFilterTrace()){
				isClicked = t.ClickOn(clickLocation.x, clickLocation.y);
				if(isClicked){
					canvasB.resetTraceFilter();
					t.setSelected(true);
					t.setColour();
					canvasB.setSelectedTrace(t);
					clickDetermined = true;
					firstClick = true;
					repaint();
					mainWindow.updateDetailsTable(t);
					break;
				}
			}
		}

		//Determine if round branch button was clicked
		if (!clickDetermined){
			for (BranchButton button : canvasB.getBranchButtons()){
				isClicked = button.ClickOn(clickLocation.x, clickLocation.y);
				if (isClicked){
					clickDetermined = true;
					firstClick = true;
					canvasB.clearVis();
					canvasB.setPath(button.getFrom(), button.getTo(), BRANCH);
					repaint();
					break;
				}
			}
		}

		//Determine if back button was clicked
		if(!clickDetermined) {
			isClicked = canvasB.getBackButton().ClickOn(clickLocation.x, clickLocation.y);
			if (isClicked){
				clickDetermined = true;
				clear();
				firstClick = true;
				loadOverview();
				repaint();
			}
		}

		//If click was not determined then must be first of double click
		if(!clickDetermined){
			if (!firstClick){
				//Clear selection
				clear();
				firstClick = true;
			} else {
				firstClick = false;
			}
		}
	}


	@Override
	public void update( Graphics g )
	{
	      paint( g );
	}

	@Override
	public void mouseEntered(MouseEvent mEvent) {
		checkForHover(mEvent);
	}

	@Override
	public void mouseExited(MouseEvent mEvent) {
		checkForHover(mEvent);
	}

	@Override
	public void mousePressed(MouseEvent mEvent) {
		//Do Nothing
	}

	@Override
	public void mouseReleased(MouseEvent mEvent) {
		//Do Nothing
	}

	public void save(){
		hashData.save();
	}

	public BranchCanvas getCanvasB(){
		return canvasB;
	}

	public OverviewCanvas getCanvasO(){
		return canvasO;
	}


	public int getMinX() {
		return minX;
	}



	public void setMinX(int minX) {
		this.minX = minX;
	}



	public int getMinY() {
		return minY;
	}



	public void setMinY(int minY) {
		this.minY = minY;
	}

}
