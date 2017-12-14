package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.swing.JPanel;

import edu.ca.ualberta.ssrg.chaintracker.gui.MainVis;

/**
 * Drawing Canvas for the parallel coordinates
 */
@SuppressWarnings("serial")
public class BranchCanvas extends JPanel{

	//Path status
	public static final String FIRST_RUN = "FIRST_RUN";
	public static final String BRANCH = "BRANCH";
	public static final String LOAD_BRANCH = "LOAD_BRANCH";

	// Size notification
	private GraphPanel container;

	//HashData
	private FrontendData hashData;

	//Graphics2D
	private Graphics2D graphics;
	private Image backBuffer;

	//Variables used to designate line points and labels
	private static int FONTSIZE = 12;
	private int fontWidth;
	private FontMetrics fm;
	private int x1;
	private int y1;
	private int y2;

	//Data for spacing
	private int canvasY;
	private int canvasX;
	private int numModels;
	private int counter1;

	//Color Pallete
	private Color purple = new Color(135, 26, 189);

	//Boolean to determine if we are showing all elements or only those connected to others
	private boolean showAllEle = false;

	//Boolean to determine if a menu should be shown for an element
	private boolean displayMenu = false;

	//Boolean to determine if annotation sticky should be shown
	private boolean annSticky = false;

	//Stores the most recently selected element
	private Element selectedEle = null;
	private Element menuEle = null;

	//Data for traces
	private ArrayList<Trace> allTraces;

	//Data for attribute points
	private static int PTSIZE = 4;

	//Buttons
	private static int BUTTONMARGIN = 10;
	private static int BUTTONSPACE = 10;
	private static int DIAMETER = 20;
	private int ypos;
	private int buttonID;
	private int countBranchKeys;
	private String buttonName;
	private ArrayList<BranchButton> branchButtons;
	private RectButton backButton = new RectButton(Colors.GREY_BUTTON, Color.WHITE, "Back", 80, 25);
	private RectButton filterBtn = null;
	private RectButton annotateBtn = null;
	private RectButton seeAnnBtn = null;

	//Data for click recognition/filtering
	private boolean filteringByTraceForward = false;
	private boolean filteringByTraceBackward = false;
	private boolean filterHold = false;
	private Trace selectedTrace;
	private Trace filteringTrace;
	private ArrayList<Element> filterEle = new ArrayList<Element>();
	private ArrayList<Element> filteringEle = new ArrayList<Element>();
	private ArrayList<Element> allEle = new ArrayList<Element>();
	private ArrayList<Trace> filterTrace = new ArrayList<Trace>();
	private ArrayList<String> ids = new ArrayList<String>();

	//Data for multiple pathsForward
	private ArrayList<Model> path = new ArrayList<Model>();
	private int position;
	private int numToRemove;
	private boolean isNext;

	//Data for the hover event and drawing branch information
	private boolean drawHoverInfo;
	private BranchButton buttonInfo;
	private int stickyMaxSize = 200;

    public BranchCanvas(GraphPanel containerP, MainVis main, FrontendData hashData){

    	this.hashData = hashData;
    	this.container = containerP;
    	this.setSize(new Dimension (500, 500));

    }

    public void load (){
    	Model root = null;

    	//Set default path
    	if (hashData.getModelsOrder().size() >= 2){
    		//find first root node
    		for (Model m : hashData.getModelsOrder()){
    			if (!hashData.getPathsBackward().containsKey(m)){
    				root = m;
    				break;
    			}
    		}

    		setPath(root, hashData.getPathsForward().get(root).get(0), FIRST_RUN);
    	}

    	repaint();

    }

    /**
     * Stores the information about the path the user is currently viewing.
     * It takes a Model modelFrom which indicates the model branching from
     * and modelTo which indicates the model the modelFrom branches to.
     */
    public void setPath(Model modelFrom, Model modelTo, String status){
    	boolean forward = false;
    	boolean backward = false;
    	boolean rootChange = false;
    	int directions;

    	canvasY = ((2 * FrontendData.MARGINY) + hashData.modelHeight);
    	//Find the location of "model" in the path array
    	if (status.equals(FIRST_RUN)){
    		//first run
    		position = 0;
    		path.add(modelFrom);
    		forward = true;
    	} else if (status.equals(BRANCH) && path.contains(modelTo)){
    		//backward path was selected
    		if (path.indexOf(modelTo) == 1){
    			//root is changing
    			rootChange = true;
    			path.set(0, modelFrom);
    		}

    		position = path.indexOf(modelTo) - 1;
    		backward = true;
    	} else if (status.equals(BRANCH) && path.contains(modelFrom)){
    		//forward path was selected
    		position = path.indexOf(modelFrom);
    		forward = true;
    	} else if (status.equals(LOAD_BRANCH)){
    		position = 0;
    		forward = true;
    	}

    	if (!rootChange) {
	    	//Set modelFrom and modelTo position and clear the rest of the path
	    	if (forward && status.equals(BRANCH)){
	    		numToRemove = path.size() - (position + 2);
	    	}
	    	if (backward && status.equals(BRANCH)){
	    		numToRemove = position;
	    	}
	    	path.set(position, modelFrom);
	    	if (path.size() > 1){
	    		for (int i = 0; i < numToRemove; i++){
	    			if (forward){
	    				path.remove(position + 2);
	    			}
	    			if (backward){
	    				path.remove(0);
	    			}
	    		}
	    		if (forward){
	    			path.set((position + 1), modelTo);
	    		}
	    	}  else {
	    		//First run
	    		path.add(modelTo);
	    	}

	    	//Check if there are more nodes in the path
	    	if (status.equals(LOAD_BRANCH)){
	    		directions = 2;
	    	} else {
	    		directions = 1;
	    	}
	    	isNext = modelConnects(modelTo, (forward) ? "forward" : "backward");
			pathFill(directions, forward, backward, modelTo, modelFrom);
    	}

    	//Change models and traces to reflect only what is in the path
    	container.refresh();
    	checkTraces();
    	makeEle();

    	//Update Element and Attribute x locations
    	hashData.updateXPos(path);
    	hashData.updateYPos(path, filterEle);

    	//Create the appropriate branch buttons
    	createBranchButtons();

    	//Re-calculate model height
    	Set<Model> models = new HashSet<Model>(path);
    	hashData.CalcModelHeight(models, allEle);

    }

    /**
     * Fills the remainder of the path once the new path has been
     * set up in setPath.
     */
    private void pathFill(int numRuns, boolean forward, boolean backward, Model modelTo, Model modelFrom){
    	//Update remainder of path to defaults
    	Hashtable<Model, ArrayList<Model>> paths = new Hashtable<Model, ArrayList<Model>>();
    	Model model = null;
    	for (int i =0; i < numRuns; i++){
	    	if (forward){
	    		position = position + 2;
	    		model = modelTo;
	    		paths = hashData.getPathsForward();
	    	}
	    	if (backward){
	    		position = 0;
	    		model = modelFrom;
	    		paths = hashData.getPathsBackward();
	    	}
	    	while (isNext){
	    		model = paths.get(model).get(0);
	    		path.add(position, model);
	    		if (forward){
	    			position++;
	    		}
	    		isNext = modelConnects(model, (forward) ? "forward" : "backward");
	    	}
	    	forward = !forward;
	    	backward = !backward;
	    	isNext = modelConnects(modelFrom, (forward) ? "forward" : "backward");
    	}
    }

    /**
     * Checks if there are models connected to the provided model
     */
    public boolean modelConnects(Model modelTo, String direction){
    	Hashtable<Model, ArrayList<Model>> paths = new Hashtable<Model, ArrayList<Model>>();
    	if (direction.equals("forward")){
    		paths = hashData.getPathsForward();
    	}
    	if (direction.equals("backward")){
    		paths = hashData.getPathsBackward();
    	}
    	if (paths.containsKey(modelTo)){
   			return true;
   		} else {
   			return false;
   		}
    }

    /**
     * Creates an ArrayList of all elements
     * This could mean all elements in these models or all
     * elements in these models that are connected to other models
     * in the path via a trace. (depends on showAllEle boolean)
     */
    public void makeEle(){
    	allEle.clear();

    	//Iterate over all elements
		for (Model model : hashData.getModelHT().keySet()){
        	for (Element ele : hashData.getModelHT().get(model).keySet()){
        		//Check if user wants to see all elements or only those connected to traces
        		if (showAllEle){
        			//Add all elements in path to allEle
            		if (path.contains(ele.getParentModel())){
            			allEle.add(ele);
            		}
        		} else {
        			//Add only the elements in the path and connected to traces to allEle
        			for (Trace t : allTraces){
	        			if (path.contains(ele.getParentModel()) && (t.getOrigin().getParentEle().equals(ele) || t.getDestination().getParentEle().equals(ele))){
	        				//Only add the element if it is not already in the allEle list
	        				if (!allEle.contains(ele)){
	        					allEle.add(ele);
	        				}
	        			}
        			}
        		}
        	}
    	}
		filterEle.clear();
		FillEleFilter();
    }

    /**
     * creates layout for models from input data and spaces them correctly
     * on the canvas
     */
    public void spaceModels(){

    	//Determine Spacing based on Models
    	numModels = path.size();
    	canvasX = (FrontendData.MARGINX_LEFT + FrontendData.MARGINX_RIGHT) + (FrontendData.MODELSPACE * (numModels - 1));
    	if(canvasX < container.getMinX()){
    		canvasX = container.getMinX();
    	}
    	canvasY = ((2 * FrontendData.MARGINY) + hashData.modelHeight);
    	if(canvasY < container.getMinY()){
    		canvasY = container.getMinY();
    	}
    	if(canvasY < container.getMinY()){
    		canvasY = container.getMinY();
    	}
    	this.setSize(canvasX, canvasY);


    }

    /**
     * Creates the branch buttons for the displayed path
     */
    private void createBranchButtons(){
    	//Create forward branch buttons
    	int xposTo = FrontendData.MARGINX_LEFT + BUTTONSPACE;
    	int xposFrom = FrontendData.MARGINX_LEFT - BUTTONSPACE - DIAMETER;
    	String modelName;
    	String branchName;
    	ypos = 4;
    	branchButtons = new ArrayList<BranchButton>();
    	int count = 1;
    	countBranchKeys = 1;
    	for (Model model : path){
    		if (hashData.getPathsForward().containsKey(model)){
    			for (Model branch : hashData.getPathsForward().get(model)){
    				if (path.contains(model) && path.contains(branch)){
    					//Do nothing
    				} else {
    					//Ensure 10 character limit on model names
    					if (model.getName().length() > 10){
    						modelName = model.getName().substring(0, 10);
    					} else {
    						modelName = model.getName();
    					}
    					if (branch.getName().length() > 10){
    						branchName = branch.getName().substring(0, 10);
    					} else {
    						branchName = branch.getName();
    					}
    					buttonName = count + ". " + modelName + " to " + branchName;
    					BranchButton newButton = new BranchButton(xposTo + (DIAMETER/2), ypos + (DIAMETER/2), Colors.BRANCH_BUTTON_PURPLE, Color.WHITE, buttonName, "forward", getBranchButtonID(), branch, model, DIAMETER, DIAMETER);
    					branchButtons.add(newButton);

    					xposTo = xposTo + DIAMETER + BUTTONSPACE;
    					count++;
    				}
    			}
    		}
			xposTo = FrontendData.MARGINX_LEFT + BUTTONSPACE + (FrontendData.MODELSPACE * countBranchKeys);
			countBranchKeys++;
    	}

    	//Create backward branch buttons
    	countBranchKeys = 1;
    	for (Model model : path){
    		if (hashData.getPathsBackward().containsKey(model)){
    			for (Model branch : hashData.getPathsBackward().get(model)){
    				if (path.contains(model) && path.contains(branch)){
    					//Do nothing
    				} else {
    					//Ensure 10 character limit on model names
    					if (model.getName().length() > 10){
    						modelName = model.getName().substring(0, 10);
    					} else {
    						modelName = model.getName();
    					}
    					if (branch.getName().length() > 10){
    						branchName = branch.getName().substring(0, 10);
    					} else {
    						branchName = branch.getName();
    					}
    					buttonName = count + ". " + modelName + " from " + branchName;
    					BranchButton newButton = new BranchButton(xposFrom + (DIAMETER/2), ypos + (DIAMETER/2), Colors.BRANCH_BUTTON_PURPLE, Color.WHITE, buttonName, "backward", getBranchButtonID(), model, branch, DIAMETER, DIAMETER);
    					branchButtons.add(newButton);

    					xposFrom = xposFrom - DIAMETER - BUTTONSPACE;
    					count++;
    				}
    			}
    		}
			xposFrom = FrontendData.MARGINX_LEFT - BUTTONSPACE - DIAMETER + (FrontendData.MODELSPACE * countBranchKeys);
			countBranchKeys++;
    	}
    }


    /**
     * Check that the traces are all in path
     */
    private void checkTraces(){
    	allTraces = new ArrayList<Trace>();
    	 //Check that the trace is connecting two models in the path
    	for (Trace t : hashData.getTracesArray()){
			if (path.contains(t.getOrigin().getParentModel()) && path.contains(t.getDestination().getParentModel())){
				allTraces.add(t);
			}
    	}

    	//Remove traces that do not connect the length of the whole path
    	//Get all traces that originate in Code Template
    	Hashtable <Element, ArrayList<Trace>> templateHT = new Hashtable <Element, ArrayList<Trace>>();
    	Element originEle;
    	for (Trace t : allTraces) {
    		originEle = t.getOrigin().getParentEle();
    		if (t.getOrigin().getParentModel().getType().equals(Model.TYPE_TEMPLATE)) {
    			if (!templateHT.keySet().contains(originEle)) {
    				ArrayList<Trace> traces = new ArrayList<Trace>();
    				traces.add(t);
    				templateHT.put(originEle, traces);
    			} else {
    				templateHT.get(originEle).add(t);
    			}
    		}
    	}

    	//Check if any traces in allTraces end in that element
    	Element destEle;
    	boolean eleValid;
    	for (Element e : templateHT.keySet()) {
    		eleValid = false;
    		for (Trace t : allTraces) {
        		destEle = t.getDestination().getParentEle();
        		if (destEle.equals(e)) {
        			eleValid = true;
        			break;
        		}
        	}
    		if (!eleValid) {
    			ArrayList<Trace> remove = templateHT.get(e);
    			for (Trace t : remove) {
    				allTraces.remove(t);
    			}
    		}

    	}
    }

    /**
     * Returns a unique branch button ID
     */
    public int getBranchButtonID(){
    	buttonID++;
    	return buttonID - 1;
    }

    /**
     * Check if filterEle list is empty, if so add all elements
     */
    private void FillEleFilter(){
        if (filterEle.isEmpty()){
           	for (Element ele : allEle){
           		ele.setColour(Color.BLUE);
           		filterEle.add(ele);
            }
        }
    }

    @Override
    public void paintComponent(Graphics graph){

    	if( backBuffer == null )
        {
            backBuffer = createImage( getWidth(), getHeight() );
            graphics = (Graphics2D) backBuffer.getGraphics();
        }

    	graphics.setColor(Color.WHITE);
    	graphics.fillRect(0, 0, getWidth(), getHeight());

    	graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    	//Graphics setup colour and title font
        graphics.setColor(Color.BLACK);
        fm = graphics.getFontMetrics(new Font("Ariel", Font.BOLD, FONTSIZE));
        graphics.setFont(new Font("Ariel", Font.BOLD, FONTSIZE));

        //Reset button IDs
        buttonID = 0;

        //Draw and label each model axis
        counter1 = 0;
        for (Model m : path){
        	if (counter1 == 0){
        	x1 = FrontendData.MARGINX_LEFT;
        	} else {
        		x1 = FrontendData.MARGINX_LEFT + (counter1 * FrontendData.MODELSPACE);
        	}
        	y1 = FrontendData.MARGINY;
        	y2 = FrontendData.MARGINY + hashData.getModelHeight();
        	graphics.drawLine(x1, y1, x1, y2);
        	fontWidth = fm.stringWidth(m.getName());
        	graphics.drawString(m.getName(), (x1 - (fontWidth/2)), (y1 - FONTSIZE/2));
        	counter1++;
        }

        //Reset font to regular
        fm = graphics.getFontMetrics(new Font("Ariel", Font.PLAIN, FONTSIZE));
        graphics.setFont(new Font("Ariel", Font.PLAIN, FONTSIZE));

        //Determine if it is a regular draw or trace filter draw
        if (filteringByTraceForward || filteringByTraceBackward) {
        	//TODO
        	// Drawing the filter by trace
        	Trace curTrace = filteringTrace;
        	filterEle.clear();
        	filterTrace.clear();
        	filterTrace.add(curTrace);

        	// Need to know for boundary case
        	Model originModel = path.get(0);
        	Model secondLastModel = path.get(path.size() - 2);
        	boolean frontBoundary = false;
        	boolean backBoundary = false;
        	if (filteringTrace.getOrigin().getParentModel().equals(secondLastModel)) {
        		backBoundary = true;
        	}
        	if (filteringTrace.getOrigin().getParentModel().equals(originModel)) {
        		frontBoundary = true;
        	}


        	// Find the two elements connected by the trace and add them to the collection to filter
        	Attribute destination = curTrace.getDestination();
        	Attribute origin = curTrace.getOrigin();
        	filterEle.add(destination.getParentEle());
        	filterEle.add(origin.getParentEle());

        	// Trace forward from destination and backward from origin
        	Queue<Trace> toCheck = new LinkedList<Trace>();
        	toCheck.add(curTrace);
        	ArrayList<Trace> allTracesToCheck = allTraces;
        	while (!toCheck.isEmpty()) {
        		curTrace = toCheck.remove();
        		origin = curTrace.getOrigin();
        		destination = curTrace.getDestination();
        		allTracesToCheck.remove(curTrace);
        		// Check all traces against current trace
	        	for (Trace t : allTracesToCheck) {
	        		if(filteringByTraceForward) {
	        			// Filtering forward
		        		if ((t.getOrigin().equals(destination)) || ((backBoundary) && (t.getOrigin().equals(filteringTrace.getOrigin())))) {
		        			if(!filterTrace.contains(t)) {
		        				filterTrace.add(t);
			        			toCheck.add(t);
		        			}
		        			if (!filterEle.contains(t.getDestination().getParentEle())) {
		        				filterEle.add(t.getDestination().getParentEle());
		        			}
		        		}
	        		} else {
	        			// Filtering backward
		        		if((t.getDestination().equals(origin)) || ((frontBoundary) && (t.getDestination().equals(filteringTrace.getDestination())))) {
		        			if(!filterTrace.contains(t)) {
		        				filterTrace.add(t);
			        			toCheck.add(t);
		        			}
		        			if (!filterEle.contains(t.getOrigin().getParentEle())) {
		        				filterEle.add(t.getOrigin().getParentEle());
		        			}
		        		}
	        		}
	        	}

	        	allTracesToCheck.add(curTrace);
        	}

        } else {
	        FillEleFilter();

	        //Ensure filterTrace lists only the Traces relevant to elements showing
	        ids = new ArrayList<String>();
	        filterTrace.clear();
	        for (Element ele : filterEle){
	        	for (Attribute att : ele.getAttList()){
	        		ids.add(att.getId());
	        	}
	        }
	        for (Trace t : allTraces){
	        	if ((ids.contains(t.getOrigin().getId())) && (ids.contains(t.getDestination().getId()))){
	        		if (((path.indexOf(t.getOrigin().getParentModel()) - 1) == (path.indexOf(t.getDestination().getParentModel()))) ||
	        				((path.indexOf(t.getDestination().getParentModel()) - 1) == (path.indexOf(t.getOrigin().getParentModel())))){
	        			filterTrace.add(t);
	        		}
	        	}
	        }
        }

        //Draw back button
		backButton.draw(graphics, 15, 5, 17, 17);

        //Draw box for each element on each model
        for (Element ele : filterEle){
        	ele.draw(graphics, path, fm);
        	if (!(ele.getAnnotation() == null) && !(ele.getAnnotation().equals(""))){
        		graphics.setColor(Colors.LAST_CHANCE_ORANGE);
        		int[] xCoords = new int[3];
        		int[] yCoords = new int[3];
        		xCoords[0] = (ele.getXOrigin());
        		xCoords[1] = ((ele.getXOrigin() + (Element.RECTWIDTH/2)));
        		xCoords[2] = (ele.getXOrigin());
        		yCoords[0] = (ele.getYOrigin());
        		yCoords[1] = (ele.getYOrigin());
        		yCoords[2] = ((ele.getYOrigin() + (Element.RECTWIDTH/2)));
        		graphics.fillPolygon(xCoords, yCoords, 3);
        	}
        }

        //Draw line for each trace
		for (Trace t : filterTrace){
			t.draw(graphics);
		}

		//Draw point for each Attribute
		graphics.setColor(Color.BLACK);
		for (Element ele : filterEle){
			for (Attribute att : ele.getAttList()){
					att.draw(graphics, PTSIZE);
			}
		}

    	//Draw Round Branch Buttons
    	int xposTo = FrontendData.MARGINX_LEFT + BUTTONSPACE;
    	int xposFrom = FrontendData.MARGINX_LEFT - BUTTONSPACE - DIAMETER;
    	int count = 1;
    	String modelName;
    	boolean forward = false;
    	boolean backward = false;
    	for (Model model : path){
    		if (model.getName().length() > 10){
    			modelName = model.getName().substring(0, 10);
    		} else {
    			modelName = model.getName();
    		}
    		int startXTo = xposTo;
    		int startXFrom = xposFrom;
    		for (BranchButton button : branchButtons){
    			String part0 = button.getText().substring(0, button.getText().indexOf(" "));
    			String part1 = null;
    			if (button.getText().contains("from")) {
        			part1 = button.getText().substring(button.getText().indexOf(" ") + 1, button.getText().indexOf(" from"));
    			} else {
    				part1 = button.getText().substring(button.getText().indexOf(" ") + 1, button.getText().indexOf(" to"));
    			}
    			if (part1.equals(modelName)){

    				if (button.getDirection().equals("forward")){
    					button.draw(graphics, part0.substring(0, 1), (fm.stringWidth(part0.substring(0, 1))/2), (fm.getHeight()/3));
    					xposTo = xposTo + BUTTONSPACE + DIAMETER;
    					forward = true;
    				} else {
    					button.draw(graphics, part0.substring(0, 1), (fm.stringWidth(part0.substring(0, 1))/2), (fm.getHeight()/3));
    					xposFrom = xposFrom - BUTTONSPACE - DIAMETER;
    					backward = true;
    				}

    			}
    		}
    		//Draw arrows
    		graphics.setColor(Colors.LIGHT_BLUE);
    		if (forward){
    			Point fromPoint = new Point(startXTo, ypos + (DIAMETER + BUTTONSPACE));
    			Point toPoint = new Point(xposTo, ypos + (DIAMETER + BUTTONSPACE));
    			graphics.fill(createArrowShape(fromPoint, toPoint));
    		}
    		if (backward){
    			Point fromPoint = new Point(startXFrom + DIAMETER, ypos + (DIAMETER + BUTTONSPACE));
    			Point toPoint = new Point(xposFrom + DIAMETER, ypos + (DIAMETER + BUTTONSPACE));
    			graphics.fill(createArrowShape(fromPoint, toPoint));
    		}

    		//Reset Values
    		xposTo = FrontendData.MARGINX_LEFT + BUTTONSPACE + (FrontendData.MODELSPACE * count);
    		xposFrom = FrontendData.MARGINX_LEFT - BUTTONSPACE - DIAMETER + (FrontendData.MODELSPACE * count);
    		count++;
    		forward = false;
    		backward = false;

    	}

    	//Draw info for hover
    	if (drawHoverInfo){
    		graphics.setColor(purple);
        	graphics.fillRect(buttonInfo.getX() + buttonInfo.getLength() + BUTTONSPACE, buttonInfo.getY(), fm.stringWidth(buttonInfo.getText()) + (2 * BUTTONMARGIN), fm.getHeight() + BUTTONMARGIN);
        	graphics.setColor(Color.WHITE);
        	graphics.drawString(buttonInfo.getText(), buttonInfo.getX() + buttonInfo.getLength() + BUTTONSPACE + BUTTONMARGIN, buttonInfo.getY() + BUTTONMARGIN + fm.getHeight()/2);
    	}

    	//Draw menu information if necessary
		if (displayMenu){
			filterBtn = new RectButton(Colors.GREY_BUTTON, Color.WHITE, "Filter Selection", 100, 25);
			annotateBtn = new RectButton(Colors.GREY_BUTTON, Color.WHITE, "Annotate", 100, 25);
			seeAnnBtn = new RectButton(Colors.GREY_BUTTON, Color.WHITE, "See Annotation", 100, 25);
			int list_margin = 5;
			int listWidth = ((2*list_margin) + filterBtn.getLength());
			int listHeight = ((4*list_margin) + (3*filterBtn.getHeight()));
			//Draw outer box
			int xpos = (int)Math.round(menuEle.getXOrigin() + (menuEle.getXDest() - menuEle.getXOrigin())/2);
			int ypos = (int)Math.round(menuEle.getYOrigin() + (menuEle.getYDest() - menuEle.getYOrigin())/2);
			Rectangle2D infoRect = new Rectangle2D.Double(xpos, ypos, listWidth, listHeight);
			graphics.setColor(Colors.LIGHT_GREY);
			graphics.fill(infoRect);
			graphics.setStroke(new BasicStroke(1.0f));
			graphics.setColor(Color.BLACK);
			graphics.draw(infoRect);
			//Draw filter button, annotate button, and see annotation button
    		filterBtn.draw(graphics, 40, 5, xpos + list_margin, ypos + list_margin);
    		ypos = ypos + filterBtn.getHeight() + list_margin;
    		annotateBtn.draw(graphics, 25, 5, xpos + list_margin, ypos + list_margin);
    		ypos = ypos + filterBtn.getHeight() + list_margin;
    		seeAnnBtn.draw(graphics, 40, 5, xpos + list_margin, ypos + list_margin);
    		setDisplayMenu(false);
    		menuEle.setColour(Color.BLUE);
		}

		//Draw annotations sticky if necessary
		if (annSticky){
			//Determine location for information
			int xpos = (int)Math.round(menuEle.getXOrigin() + (menuEle.getXDest() - menuEle.getXOrigin())/2);
			int ypos = (int)Math.round(menuEle.getYOrigin() + (menuEle.getYDest() - menuEle.getYOrigin())/2);

			if (!(menuEle.getAnnotation() == null)) {
				int list_margin = 5;
				String ann = menuEle.getAnnotation().getContent();

				//Obtain wrapped list from string
				List<String> lines = StringUtils.wrap(ann, fm, stickyMaxSize);

				//Set list height and width
				int listWidth = ((2*list_margin) + stickyMaxSize);
				int listHeight = ((2*list_margin) + ((lines.size())*fm.getHeight()));

				//Draw outer box and string
				Rectangle2D infoRect = new Rectangle2D.Double(xpos, ypos, listWidth, listHeight);
				graphics.setColor(Colors.LIGHT_YELLOW);
				graphics.fill(infoRect);
				graphics.setStroke(new BasicStroke(1.0f));
				graphics.setColor(Color.BLACK);
				graphics.draw(infoRect);

				fm = graphics.getFontMetrics(new Font("Ariel", Font.PLAIN, 11));
				graphics.setFont(new Font("Ariel", Font.PLAIN, 11));
				for (String line: lines) {
					ypos = ypos + fm.getHeight();
					graphics.drawString(line, xpos, ypos);
				}
			} else {
				//Draw toast when there are no annotations to show
				Point p = new Point(xpos, ypos);
				String message = "There are no annotations to show";
				long duration = 3000;
				Toast.showToast(container, message, p, duration);
			}

			annSticky = false;
		}

    	//Clear filter if hold is off
    	if (filterHold){
    		//Do nothing
    	} else {
    		resetFilter();
    		FillEleFilter();
    		filterHold = true;
    	}

    	graph.drawImage(backBuffer, 0, 0, this );
   	 	backBuffer = null;
    }

	/**
	 * Reset filter
	 */
    public void resetFilter(){
    	filteringEle.clear();
    	filterEle.clear();
    	filteringByTraceForward = false;
    	filteringByTraceBackward = false;
    	filterHold = false;
    }

    /**
     * Reset trace filter
     */
    public void resetTraceFilter(){
    	filterTrace.clear();
    	if (selectedTrace!=null){
    		selectedTrace.setSelected(false);
    		selectedTrace.setColour();
    		selectedTrace = null;
    	}
    }

    /**
     * Called when the filter button is pressed.
     */
    public void filterVis(){
		ArrayList<Element> temp = new ArrayList<Element>();
		if (selectedEle == null) {
			clearVis();
		}
		filterEle.clear();
		for (Element ele : filteringEle){
			ele.setColour(Color.BLUE);
			temp = ele.connected(allTraces);
			for (Element E : temp){
				if (filterEle.contains(E)){
					//Do nothing
				} else {
					filterEle.add(E);
				}
			}
		}
		resetTraceFilter();
		filteringEle.clear();
		hashData.updateYPos(path, filterEle);
		repaint();
		selectedEle = null;
    }

    /**
     * Called when the user selects the filter by trace destination option
     */
    public void filterTraceForward(){
    	//Check that a trace has been selected
    	if (getSelectedTrace() != null) {
    		filteringByTraceForward = true;
    		repaint();
    	}
    }

    /**
     * Called when the user selects the filter by trace source option
     */
    public void filterTraceBackward(){
    	//Check that a trace has been selected
    	if (getSelectedTrace() != null) {
    		filteringByTraceBackward = true;
    		repaint();
    	}
    }

    /**
     * Called when the clear button is pressed
     */
    public void clearVis(){
    	selectedEle = null;
    	resetFilter();
    	resetTraceFilter();
    	FillEleFilter();
    	hashData.updateYPos(path, filterEle);
    	repaint();
    }

    /**
     * This method is called when transferring from overview to branch view
     * It is responsible for setting the path the user is looking for
     */
    public void loadBranchPath(ModelTrace t){
		path.clear();
		path.add(t.getModelFrom());
		setPath(t.getModelFrom(), t.getModelTo(), LOAD_BRANCH);
		Set<Model> models = new HashSet<Model>(path);
    	hashData.CalcModelHeight(models, allEle);
		repaint();
    }

    /**
     * Used to create arrows for paint to draw
     */
    public static Shape createArrowShape(Point fromPt, Point toPt) {
        Polygon arrowPolygon = new Polygon();
        arrowPolygon.addPoint(-6,1);
        arrowPolygon.addPoint(3,1);
        arrowPolygon.addPoint(3,3);
        arrowPolygon.addPoint(6,0);
        arrowPolygon.addPoint(3,-3);
        arrowPolygon.addPoint(3,-1);
        arrowPolygon.addPoint(-6,-1);


        Point midPoint = midpoint(fromPt, toPt);

        double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

        AffineTransform transform = new AffineTransform();
        transform.translate(midPoint.x, midPoint.y);
        double ptDistance = fromPt.distance(toPt);
        double scale = ptDistance / 12.0; // 12 because it's the length of the arrow polygon.
        transform.scale(scale, 1.2); // 1.2 so they're always skinny
        transform.rotate(rotate);

        return transform.createTransformedShape(arrowPolygon);
    }

    /**
     * Calculates arrow midpoint
     */
    private static Point midpoint(Point p1, Point p2) {
        return new Point((int)((p1.x + p2.x)/2.0),
                         (int)((p1.y + p2.y)/2.0));
    }

    /**
     * Scroll listener. This method is called when the scroll bars on the graph panel change
     */
    public void scrollListener(){
    	repaint();
	}

    /**
     * Toggles the boolean showAllEle for filtering
     * and updates the view
     */
    public void toggleEleFilter(boolean showAll){
    	if (showAll){
    		showAllEle = true;
    	} else {
    		showAllEle = false;
    	}
    	makeEle();
    	Set<Model> models = new HashSet<Model>(path);
    	hashData.CalcModelHeight(models, allEle);
    	clearVis();
    	repaint();
    }

    /**
     * Returns the elements that are currently highlighted
     */
    public ArrayList<Element> getSelectedElements(){
    	return filteringEle;
    }

    /**
     * Returns all inbound traces given an element
     */
    public ArrayList<Trace> getInboundTraces(Element ele){

    	return hashData.getInboundTraces(ele);
    }

    /**
     * Returns all outbound traces given an element
     */
	public ArrayList<Trace> getOutboundTraces(Element ele) {

		return hashData.getOutboundTraces(ele);
	}

	public Trace getSelectedTrace() {
		return selectedTrace;
	}

	public ArrayList<BranchButton> getBranchButtons() {
		return branchButtons;
	}

	public boolean isDrawHoverInfo() {
		return drawHoverInfo;
	}

	public void setDrawHoverInfo(boolean drawHoverInfo) {
		this.drawHoverInfo = drawHoverInfo;
	}

	public BranchButton getButtonInfo() {
		return buttonInfo;
	}

	public void setButtonInfo(BranchButton buttonInfo) {
		this.buttonInfo = buttonInfo;
	}

	public boolean isFilterHold() {
		return filterHold;
	}

	public void setFilterHold(boolean filterHold) {
		this.filterHold = filterHold;
	}

	public ArrayList<Element> getFilterEle() {
		return filterEle;
	}

	public void setFilterEle(ArrayList<Element> filterEle) {
		this.filterEle = filterEle;
	}

	public ArrayList<Element> getFilteringEle() {
		return filteringEle;
	}

	public void addFilteringEle(Element ele) {
		filteringEle.add(ele);
	}

	public ArrayList<Trace> getFilterTrace() {
		return filterTrace;
	}

	public void setFilterTrace(ArrayList<Trace> filterTrace) {
		this.filterTrace = filterTrace;
	}

	public void setSelectedTrace(Trace selectedTrace) {
		this.selectedTrace = selectedTrace;
	}

	public void setFilteringTrace(Trace t) {
		this.filteringTrace = t;
	}

	public RectButton getBackButton() {
		return backButton;
	}

    // Needed so the JFrame knows what size the canvas is
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvasX, canvasY);
    }

	public Element getSelectedEle() {
		return selectedEle;
	}

	public void setSelectedEle(Element selectedEle) {
		this.selectedEle = selectedEle;
	}

	public Element getMenuEle() {
		return menuEle;
	}

	public void setMenuEle(Element selectedEle) {
		this.menuEle = selectedEle;
	}

	public boolean isDisplayMenu() {
		return displayMenu;
	}

	public void setDisplayMenu(boolean displayMenu) {
		this.displayMenu = displayMenu;
	}

	public RectButton getFilterBtn() {
		return filterBtn;
	}

	public RectButton getAnnotateBtn() {
		return annotateBtn;
	}

	public RectButton getAspectsBtn() {
		return seeAnnBtn;
	}

	public boolean getAnnSticky() {
		return annSticky;
	}

	public void setAnnSticky(boolean annSticky) {
		this.annSticky = annSticky;
	}

	public void destroyMenuButtons() {
		this.filterBtn = null;
		this.annotateBtn = null;
		this.seeAnnBtn = null;
	}

}
