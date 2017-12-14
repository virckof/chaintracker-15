package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.JPanel;

import edu.ca.ualberta.ssrg.chaintracker.gui.MainVis;

@SuppressWarnings("serial")
public class OverviewCanvas extends JPanel {

	// Data for spacing
	private int canvasY;
	private int canvasX;
	private int numModels;
	private int MARGINX_LEFT = 170;
	private int MODEL_SPACE = 250;
	private int TOP_MARGIN = 160;
	int MODEL_SPACE_Y = 100;

	// Size notification
	private GraphPanel container;

	// Attributes for composites
	private Composite originalComposite;

	// HashData
	private FrontendData hashData;

	// Graphics2D
	private Graphics2D graphics;
	private Image backBuffer;

	// Variables used to designate line points and labels
	private static int STANDARD_FONTSIZE = 12;
	private static int LABEL_FONTSIZE = 10;
	private FontMetrics fm;

	// Data for traces
	private ArrayList<ModelTrace> modelOverviewTraces;
	private Hashtable<Integer, ArrayList<Model>> modelTraceLevels;

	// Data for click recognition
	private static int DIAMETER = 20;
	private ArrayList<ModelButton> modelButtons;

	// Path length
	private int maxPathLength;

	// Selected branch
	private ModelTrace selectedBranch;

	// Data for pie chart
	public static int CHART_SIZE = 50;
	public static int STRING_MARGIN = 5;

	// Data for models
	private Model clickedModel = null;
	int ovalWidth = 130;
	int ovalHeight = 50;

	// Data for branch info
	private boolean displayInfo = false;
	private RectButton branchButton = new RectButton(Colors.GREY_BUTTON, Color.WHITE, "See Branch", 80, 25);
	private RectButton codeView = new RectButton(Colors.GREY_BUTTON, Color.WHITE, "Open File", 80, 25);

	public OverviewCanvas(GraphPanel containerP, MainVis main, FrontendData hashData){

		this.hashData = hashData;
		this.container = containerP;
		this.setSize(new Dimension(500, 500));
		canvasY = container.getMinY();

		maxPathLength = hashData.getPathLengthMax();

	}

	/**
	 * Loads the data from the files to the canvas representation
	 */
	public void load() {

		displayInfo = false;

		maxPathLength = hashData.getPathLengthMax();

		// Put all modelTraces into the array for overview
		popModelTraces();

		// Create all model buttons
		createModelButtons();

		container.refresh();

		repaint();
	}

	/**
     * creates layout for models from input data and spaces them correctly
     * on the canvas
	 */
	public void spaceModels() {

		// Determine Spacing based on Models
		numModels = maxPathLength;
    	canvasX = (MARGINX_LEFT + FrontendData.MARGINX_RIGHT) + (MODEL_SPACE * (numModels - 1));
		if (canvasX < container.getMinX()) {
			canvasX = container.getMinX();
		}
		if (canvasY < container.getMinY()) {
			canvasY = container.getMinY();
		}
		this.setSize(canvasX, canvasY);

    	
	}

	/**
     * This function populates the modelOverviewTraces array 
     * so the program has access to a web of models that are
     * interconnected for the high level overview
	 */
	private void popModelTraces() {
		modelOverviewTraces = new ArrayList<ModelTrace>();
		Model origin;
		Model dest;
		String file, path;
		boolean created;
		for (Trace t : hashData.getTracesArray()) {
			origin = t.getOrigin().getParentModel();
			dest = t.getDestination().getParentModel();
			file = t.getFile();
			path = t.getPath();
			created = false;
			for (ModelTrace mt : modelOverviewTraces) {
    			if (mt.getModelFrom().equals(origin) && mt.getModelTo().equals(dest)){
					created = true;
				}
			}
			if (created == false) {
				ModelTrace newMT = new ModelTrace(origin, dest, file, path);
				modelOverviewTraces.add(newMT);
			}
		}

		// organize models into their "levels"
		modelTraceLevels = new Hashtable<Integer, ArrayList<Model>>();
		boolean start, end;
		// Create empty hashtable
		for (int i = 0; i < maxPathLength; i++) {
			modelTraceLevels.put(i, new ArrayList<Model>());
		}
		// Populate start and end in hashtable
		for (Model m : hashData.getModelsOrder()) {
			start = true;
			end = true;
			for (ModelTrace mt : modelOverviewTraces) {
				if (mt.getModelTo().equals(m)) {
					start = false;
				}
				if (mt.getModelFrom().equals(m)) {
					end = false;
				}
			}
			if (start == true) {
				modelTraceLevels.get(0).add(m);
			} else if (end == true) {
				modelTraceLevels.get(maxPathLength - 1).add(m);
			}
		}

		// Populate middle of hashtable
		for (int i = 0; i < maxPathLength - 1; i++) {
			for (Model m : modelTraceLevels.get(i)) {
				for (ModelTrace mt : modelOverviewTraces) {
    				if (mt.getModelFrom().equals(m) && !modelTraceLevels.get(i + 1).contains(mt.getModelTo())){
						modelTraceLevels.get(i + 1).add(mt.getModelTo());
					}
				}
			}
		}

		// Assign ModelTrace coordinates
		int xpos = MARGINX_LEFT;
		int ypos, y;
		for (int i = 0; i < maxPathLength; i++) {
			ypos = TOP_MARGIN;
			for (Model m : modelTraceLevels.get(i)) {
				for (ModelTrace mt : modelOverviewTraces) {
					if (mt.getModelFrom().equals(m)) {
	    				y = (((modelTraceLevels.get(i + 1).indexOf(mt.getModelTo())) * MODEL_SPACE_Y) + TOP_MARGIN);
						mt.setLine(xpos, ypos, xpos + MODEL_SPACE, y);
					}
				}
				ypos = ypos + MODEL_SPACE_Y;
			}
			xpos = xpos + MODEL_SPACE;
		}
	}

	/**
	 * This method is responsible for creating a roundButton for each model
	 * available so the user can click them to get more information
	 */
	private void createModelButtons() {
		// Create button for each model
		String displayText;
		modelButtons = new ArrayList<ModelButton>();

		int temp;
		int ypos;
		int xpos = MARGINX_LEFT;
		for (int i = 0; i < maxPathLength; i++) {
			ypos = TOP_MARGIN;
			for (Model m : modelTraceLevels.get(i)) {
				displayText = m.getName();
	    		ModelButton newModelButton = new ModelButton(xpos, ypos, Colors.LIGHT_BLUE, Color.BLACK, displayText, ovalWidth, ovalHeight, m);
				modelButtons.add(newModelButton);
				ypos = ypos + MODEL_SPACE_Y;
			}
			xpos = xpos + MODEL_SPACE;
			temp = ypos + TOP_MARGIN;
			if (temp > canvasY) {
				canvasY = temp;
			}
		}

		if (canvasY < container.getMinY()) {
			canvasY = container.getMinY();
		}
		this.setSize(canvasX, canvasY);
	}

	@Override
	public void paintComponent(Graphics graph) {

		if (modelOverviewTraces != null) {

    		if( backBuffer == null )  
            {  
				backBuffer = createImage(getWidth(), getHeight());
				graphics = (Graphics2D) backBuffer.getGraphics();
			}

			// Set composites
			originalComposite = graphics.getComposite();

			graphics.setComposite(originalComposite);
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, getWidth(), getHeight());

        	graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Graphics setup colour and title font
			graphics.setColor(Color.BLACK);
            fm = graphics.getFontMetrics(new Font("Ariel", Font.BOLD, STANDARD_FONTSIZE));
			graphics.setFont(new Font("Ariel", Font.BOLD, STANDARD_FONTSIZE));
			Stroke defaultStroke = graphics.getStroke();

			// Draw ModelTrace connecting all Models
			for (ModelTrace mt : modelOverviewTraces) {
				mt.draw(graphics);
			}

			// Draw model buttons
			for (RoundButton button : modelButtons) {
    			button.draw(graphics, button.getText(), (fm.stringWidth(button.getText())/2), (fm.getHeight()/3));
			}

			// Draw traces information if necessary
			if (displayInfo) {
				fm = graphics.getFontMetrics(new Font("Ariel", Font.PLAIN, 11));
				graphics.setFont(new Font("Ariel", Font.PLAIN, 11));
				ArrayList<String> traceNames = new ArrayList<String>();
				int list_margin = 5;
				Integer count = 0;
				int maxCountWidth;
				int listWidth;
				int listHeight = (2 * list_margin);
				String file = null;
				// Obtain all relevant trace names
				for (Trace trace : hashData.getTracesArray()) {
    				if (selectedBranch.getModelFrom().equals(trace.getOrigin().getParentModel()) && selectedBranch.getModelTo().equals(trace.getDestination().getParentModel())
							&& !traceNames.contains(trace.getName())) {
						traceNames.add(trace.getName());
						count++;
						if (file == null) {
							file = trace.getFile();
						}
					}
				}
				// Sort the names alphabetically ignoring case sensitivity
				Collections.sort(traceNames, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareToIgnoreCase(o2);
					}
				});
    			maxCountWidth = fm.stringWidth(fm.stringWidth(count.toString()) + "     " + ". ");
				listWidth = (2 * list_margin) + maxCountWidth;
				// Calculate list dimensions
				for (String name : traceNames) {
    				listWidth = ((fm.stringWidth(name) + (2*list_margin) + maxCountWidth) > listWidth) ? (fm.stringWidth(name) + (2*list_margin) + maxCountWidth) : listWidth;
					listHeight = listHeight + fm.getHeight();
				}
				// Add height for file name
				listHeight = listHeight + fm.getHeight();
				// Ensure buttons fit in width
    			int buttonWidth = codeView.getLength() + (3*list_margin) + branchButton.getLength();
				if (listWidth < buttonWidth) {
					listWidth = buttonWidth;
				}
				listHeight = listHeight + (branchButton.getHeight() / 2);
				// Draw outer box
    			int xpos = (int)Math.round(selectedBranch.getX1() + (selectedBranch.getX2() - selectedBranch.getX1())/2);
    			int ypos = (int)Math.round(selectedBranch.getY1() + (selectedBranch.getY2() - selectedBranch.getY1())/2 + (DIAMETER/2) + STRING_MARGIN);
    			Rectangle2D infoRect = new Rectangle2D.Double(xpos, ypos, listWidth, listHeight);
				graphics.setColor(Colors.LIGHT_YELLOW);
				graphics.fill(infoRect);
				graphics.setStroke(new BasicStroke(9.0f));
				graphics.setColor(Color.BLACK);
				graphics.setStroke(defaultStroke);
				graphics.draw(infoRect);
				// Draw list
				xpos = xpos + list_margin;
				count = 0;
				fm = graphics.getFontMetrics(new Font("Ariel", Font.BOLD, 11));
				graphics.setFont(new Font("Ariel", Font.BOLD, 11));
				String displayName = MainVis.getFileNameForDisplay(file);
				ypos = ypos + fm.getHeight();
				graphics.drawString(displayName, xpos, ypos);
				fm = graphics.getFontMetrics(new Font("Ariel", Font.PLAIN, 11));
				graphics.setFont(new Font("Ariel", Font.PLAIN, 11));
				for (String name : traceNames) {
					count++;
					displayName = "     " + count + ". " + name;
					ypos = ypos + fm.getHeight();
					graphics.drawString(displayName, xpos, ypos);
				}
				// Draw branch button and code button
				ypos = ypos + fm.getHeight();
				branchButton.draw(graphics, 30, 5, xpos, ypos - list_margin);
				xpos = xpos + branchButton.getLength() + list_margin;
				codeView.draw(graphics, 25, 5, xpos, ypos - list_margin);
			}

			// Draw Pie Charts if necessary and labels
    		if ((!(clickedModel == null)) && (clickedModel.getType().equals(Model.TYPE_MODEL))){
				ArrayList<PieSlice> allSlices = new ArrayList<PieSlice>();
				ArrayList<PieSlice> inSlices = new ArrayList<PieSlice>();
				ArrayList<PieSlice> outSlices = new ArrayList<PieSlice>();
				double totalEle = 0;
				double inbound = 0;
				double outbound = 0;
				double unused = 0;
				double bidirectional = 0;
				for (Element ele : clickedModel.getEleList()) {
					boolean in = false;
					boolean out = false;
					totalEle++;
					if (!(hashData.getInboundTraces(ele) == null)) {
						if (!(hashData.getOutboundTraces(ele) == null)) {
							bidirectional++;
						} else {
							inbound++;
						}
						in = true;
					}
					if (!in && !(hashData.getOutboundTraces(ele) == null)) {
						outbound++;
						out = true;
					}
					if (!in && !out) {
						unused++;
					}

				}
    			allSlices.add(new PieSlice(Math.round(((inbound + outbound + bidirectional)/totalEle)*100), Colors.CHART_GREEN));
    			allSlices.add(new PieSlice(Math.round((unused/totalEle)*100), Colors.CHART_RED));
    			inSlices.add(new PieSlice(Math.round(((inbound + bidirectional)/totalEle)*100), Colors.CHART_GREEN));
    			inSlices.add(new PieSlice(Math.round(((outbound + unused)/totalEle)*100), Colors.CHART_RED));
    			outSlices.add(new PieSlice(Math.round(((outbound + bidirectional)/totalEle)*100), Colors.CHART_GREEN));
    			outSlices.add(new PieSlice(Math.round(((inbound + unused)/totalEle)*100), Colors.CHART_RED));
				// Create Labels
				String inCover = "In Coverage";
				String outCover = "Out Coverage";
				String allCover = "Total Coverage";
				// Draw outer box
				int infoWidth = fm.stringWidth(allCover) + (STRING_MARGIN * 2);
    			int infoHeight = (CHART_SIZE*3) + (STRING_MARGIN*20) + (fm.getHeight()*3);
				int infoX = 0;
				int infoY = 0;
				for (ModelButton button : modelButtons) {
					if (clickedModel.equals(button.getModel())) {
    					infoX = button.getX() - button.getLength()/2 - STRING_MARGIN*2 - infoWidth;
						infoY = button.getY() - infoHeight / 2;
						break;
					}
				}
    			Rectangle2D infoRect = new Rectangle2D.Double(infoX, infoY, infoWidth, infoHeight);
				graphics.setColor(Color.WHITE);
				graphics.fill(infoRect);
				graphics.setStroke(new BasicStroke(9.0f));
				graphics.setColor(Color.BLACK);
				graphics.setStroke(defaultStroke);
				graphics.draw(infoRect);
				// Draw Number of elements
				String numEle = "Elements: " + clickedModel.getEleList().size();
    			graphics.drawString(numEle, (infoX + (infoWidth/2)) - (fm.stringWidth(numEle)/2) + STRING_MARGIN, infoY + STRING_MARGIN*3);
				// Draw Pies
    			PieChart totalPie = new PieChart((infoX + infoWidth/2 - CHART_SIZE/2), (infoY + STRING_MARGIN*2 + fm.getHeight()), CHART_SIZE, allSlices);
    			PieChart inPie = new PieChart((infoX + infoWidth/2 - CHART_SIZE/2), (totalPie.getY() + CHART_SIZE + STRING_MARGIN*2 + fm.getHeight()), CHART_SIZE, inSlices);
    			PieChart outPie = new PieChart((infoX + infoWidth/2 - CHART_SIZE/2), (inPie.getY() + CHART_SIZE + STRING_MARGIN*2 + fm.getHeight()), CHART_SIZE, outSlices);
				totalPie.drawPie(graphics);
				inPie.drawPie(graphics);
				outPie.drawPie(graphics);
				// Draw Labels
				graphics.setColor(Color.BLACK);
				fm = graphics.getFontMetrics(new Font("Ariel", Font.PLAIN, 11));
				graphics.setFont(new Font("Ariel", Font.BOLD, LABEL_FONTSIZE));
    			graphics.drawString(allCover, (infoX + (infoWidth/2)) - (fm.stringWidth(allCover)/2), totalPie.getY() + CHART_SIZE + (STRING_MARGIN*2));
    			graphics.drawString(inCover, (infoX + (infoWidth/2)) - (fm.stringWidth(inCover)/2), inPie.getY() + CHART_SIZE + (STRING_MARGIN*2));
    			graphics.drawString(outCover, (infoX + (infoWidth/2)) - (fm.stringWidth(outCover)/2), outPie.getY() + CHART_SIZE + (STRING_MARGIN*2));
				// Draw legend
				int indicationRect = fm.getHeight();
				graphics.setColor(Colors.CHART_GREEN);
    			graphics.fillRect((infoX + (infoWidth/2)) - (fm.stringWidth(outCover)/2), outPie.getY() + CHART_SIZE + (STRING_MARGIN) + fm.getHeight(), indicationRect, indicationRect);
				graphics.setColor(Colors.CHART_RED);
    			graphics.fillRect((infoX + (infoWidth/2)) - (fm.stringWidth(outCover)/2), outPie.getY() + CHART_SIZE + (STRING_MARGIN*2) + (fm.getHeight()*2), indicationRect, indicationRect);
				graphics.setColor(Color.BLACK);
    			graphics.drawString("Covered", (outPie.getX()+(CHART_SIZE/2)) - (fm.stringWidth(outCover)/2) + STRING_MARGIN + fm.getHeight(), outPie.getY() + CHART_SIZE + (STRING_MARGIN*3) + fm.getHeight());
    			graphics.drawString("Uncovered", (outPie.getX()+(CHART_SIZE/2)) - (fm.stringWidth(outCover)/2) + STRING_MARGIN + fm.getHeight(), outPie.getY() + CHART_SIZE + (STRING_MARGIN*4) + (fm.getHeight()*2));
			}

			graph.drawImage(backBuffer, 0, 0, this);
			backBuffer = null;

		}

	}

	// Needed so the JFrame knows what size the canvas is
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(canvasX, canvasY);
	}

	public ArrayList<ModelTrace> getModelOverviewTraces() {
		return modelOverviewTraces;
	}

	public ModelTrace getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(ModelTrace selectedBranch) {
		this.selectedBranch = selectedBranch;
	}
	public ArrayList<ModelButton> getModelButtons() {
		return modelButtons;
	}

	public Model getClickedModel() {
		return clickedModel;
	}

	public void setClickedModel(Model clickedModel) {
		this.clickedModel = clickedModel;
	}

	public boolean getDisplayInfo() {
		return displayInfo;
	}

	public void setDisplayInfo(boolean displayInfo) {
		this.displayInfo = displayInfo;
	}

	public RectButton getBranchButton() {
		return branchButton;
	}

	public RectButton getCodeView() {
		return codeView;
	}

}
