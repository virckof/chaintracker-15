package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import edu.ca.ualberta.ssrg.chaintracker.vis.data.DataManager;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.TuplePrinter;

public class FrontendData {
	
	//Input lines for models
	private ArrayList<String> models;
	private ArrayList<String> traces;
		
	//Data manager for input reading
	private DataManager dataManager;
	
	//Data for traces
	private ArrayList<Trace> tracesArray;
	
	//Path length 
	private int pathLengthMax;

	//Data for hashtable and data storage
	private ArrayList<Model> modelsOrder;
	private Hashtable<Element, ArrayList<Attribute>> elementHT;
	private Hashtable<Model, Hashtable<Element, ArrayList<Attribute>>> modelHT;
	private Hashtable<String, Attribute> attributeIDHT;
	private Hashtable<Model, ArrayList<Model>> pathsForward;
	private Hashtable<Model, ArrayList<Model>> pathsBackward;
	private Hashtable<Element, ArrayList<Trace>> elementTracePathsForward;
	private Hashtable<Element, ArrayList<Trace>> elementTracePathsBackward;
	
	//Data for coordinate determination
	public static int MARGINX_LEFT = 145;
	public static int MARGINX_RIGHT = 210;
	public static int MARGINY = 60;
	public static int MODELSPACE = 295;
	public static int SPACE_BETWEEN_ELEMENTS = 10;
	public static int SPACE_BETWEEN_ATTRIBUTES = 10;
	public static int MODEL_HEIGHT_MIN = 450;
	public int modelHeight;
	private ArrayList<Element> eleList;
	
	//Stores read annotations that are deleted
	ArrayList<Annotation> deletedAnnotations = new ArrayList<Annotation>();
	
	public FrontendData(){
		//Obtain data from files into arrays
		dataManager = new DataManager();
		models = new ArrayList<>();
		traces = new ArrayList<>();
		
		//Create necessary hashtables
		modelHT = new Hashtable<Model, Hashtable<Element, ArrayList<Attribute>>>();
		elementHT = new Hashtable<Element, ArrayList<Attribute>>();
		attributeIDHT = new Hashtable<String, Attribute>();
		pathsForward = new Hashtable<Model, ArrayList<Model>>();
		pathsBackward = new Hashtable<Model, ArrayList<Model>>();
		elementTracePathsForward = new Hashtable<Element, ArrayList<Trace>>();
		elementTracePathsBackward = new Hashtable<Element, ArrayList<Trace>>();
	
	}
	
	public void load(){
		models = dataManager.read(TuplePrinter.DATA_EXCHANGE_MODELS);
		traces = dataManager.read(TuplePrinter.DATA_EXCHANGE_TRACES);
		
		//Create necessary hashtables
		modelHT = new Hashtable<Model, Hashtable<Element, ArrayList<Attribute>>>();
		elementHT = new Hashtable<Element, ArrayList<Attribute>>();
		attributeIDHT = new Hashtable<String, Attribute>();
		pathsForward = new Hashtable<Model, ArrayList<Model>>();
		pathsBackward = new Hashtable<Model, ArrayList<Model>>();
		elementTracePathsForward = new Hashtable<Element, ArrayList<Trace>>();
		elementTracePathsBackward = new Hashtable<Element, ArrayList<Trace>>();
		
		//Read models file and creates necessary objects
		ReadModels();
		
		//Read in annotations
		insertAnnotations();
				
		//Populate modelHT
		PopHTData();
		
		//Determine MODELHEIGHT
		//CalcModelHeight(modelHT.keySet(), null);
		
		//Fill HT relating Attributes to their IDs
		PopIDHT();
		
		//Fill in the x and y attribute coordinates
		FillCoordinates();
		
		//Make array list containing all traces
		makeTraces();
		
		//Populate paths Forward
		popPathsForward();
		
		//Populate paths backward
		popPathsBackward();
		
		//Find max path length
		maxPathLength();
	}
	
	public ArrayList<Trace> getInboundTraces(Element e){
		if (elementTracePathsBackward.keySet().contains(e)){
			return elementTracePathsBackward.get(e);
		} else {
			return null;
		}
	}
	
	public ArrayList<Trace> getOutboundTraces(Element e){
		if (elementTracePathsForward.keySet().contains(e)){
			return elementTracePathsForward.get(e);
		} else {
			return null;
		}
	}
	
	/**
	 * Determines the required model height
	 * @param  
	 */
	public void CalcModelHeight(Set<Model> models, ArrayList<Element> elements){
		int thisModelHeight;
		int numAttributes;
		Set<Element> elementKeys;
		modelHeight = MODEL_HEIGHT_MIN;
		
		for (Model model : models){
			thisModelHeight = SPACE_BETWEEN_ELEMENTS;
			elementKeys = new HashSet<Element>();
			for (Element ele : elements){
				if (ele.getParentModel().equals(model)){
					elementKeys.add(ele);
				}
			}
    		for (Element ele : elementKeys){
    			elementHT = modelHT.get(model);
    			numAttributes = elementHT.get(ele).size();
				thisModelHeight = thisModelHeight + SPACE_BETWEEN_ELEMENTS + ((numAttributes + 1) * SPACE_BETWEEN_ATTRIBUTES);
			}
			modelHeight = (thisModelHeight > modelHeight) ? thisModelHeight : modelHeight;
		}
		
		
	}
	
	/**
	 * Populates the model and element nested hashtables with the data
	 * found in the models file.
	 */
	private void PopHTData(){
		//Populate hashtables
		ArrayList<Element> elementList = new ArrayList<Element>();
		
		for (Model m : modelsOrder){
			elementHT = new Hashtable<Element, ArrayList<Attribute>>();
			elementList = m.getEleList();
			for (Element e : elementList){
				elementHT.put(e, e.getAttList());
			}
			
			modelHT.put(m, elementHT);
		}
	}
	
	/** 
     * Determines the x and y coordinates of each attribute
     * located in the hashtables and assigns them.
     * Also responsible for population a hashtable of element arrays
     * that can be looked up by the model name. 
     */
	public void FillCoordinates(){
		int numAttributes;
		int xpos;
		int ypos;
		
		for (int position = 0; position < modelsOrder.size(); position++){
			ypos = SPACE_BETWEEN_ELEMENTS + MARGINY;
			Model m = modelsOrder.get(position);
			//Set x position for the elements and attributes on the current model
			if (position == 0){
    			xpos = MARGINX_LEFT;
    		} else {
    			xpos = MARGINX_LEFT + (position * MODELSPACE);
    		}
			
			//Set element xpos, ypos, and height
			for (Element e : modelHT.get(m).keySet()){
				numAttributes = e.getAttList().size();
				e.setHeight((numAttributes + 1) * SPACE_BETWEEN_ATTRIBUTES);
				e.setXOrigin(xpos);
				e.setYOrigin(ypos);
				
				ypos = ypos + SPACE_BETWEEN_ATTRIBUTES;
				
				//Set attribute xpos and ypos
				for (Attribute att : modelHT.get(m).get(e)){
					att.setX(xpos);
					att.setY(ypos);
					
					//update ypos for next attribues
					ypos = ypos + SPACE_BETWEEN_ATTRIBUTES;
				}
				
				//update ypos for next element
				ypos = ypos + SPACE_BETWEEN_ELEMENTS;
			}
		}	
	}
	
	/** 
     * Reads all data from models file and creates all Models, Elements, and
     * Attributes. These parts are also related to one another by the list associated
     * with each. Also, the modelsOrder ArrayList is created to show the models in 
     * the order they appear in the file.
     */
	private void ReadModels(){
		String modelName = null;
		String elementName = null;
		Model model = null;
		Element ele = null;
		ArrayList<String> allModelNames = new ArrayList<String>();
		modelsOrder = new ArrayList<Model>();
		
		for (int i = 0; i < models.size(); i++){
			//Populate modelsOrder with all unique models
			String[] parts = models.get(i).split(";");
			
			//Find the model name and element name based on model type
			/**
			 * This switch case is responsible for identifying what type of model 
			 * each line in the models file represents. The model can be one of three 
			 * types: MODEL, TEMPLATE, or CODE
			 * 
			 * When each line is split into their respective parts their content is as follows:
			 * MODEL (ID#, model type, model name, element name, attribute name, attribute type)
			 * TEMPLATE (ID#, model type, element/file name, line number, structure type)
			 * CODE (ID#, model type, element/file name, line number, structure type)
			 */
			int eleIndex = -1;
			int typeIndex = -1;
			int attNameOrLineNum = -1;
			String modelType = parts[1];
			switch(modelType) {
			case(Model.TYPE_MODEL):
				modelName = parts[2];
				elementName = parts[3];
				eleIndex = 3;
				typeIndex = 5;
				attNameOrLineNum = 4;
				break;
			case(Model.TYPE_TEMPLATE):
				modelName = "CodeTemplate";
				elementName = parts[2];
				eleIndex = 2;
				typeIndex = 4;
				attNameOrLineNum = 3;
				break;
			case(Model.TYPE_CODE):
				modelName = "GeneratedCode";
				elementName = parts[2];
				eleIndex = 2;
				typeIndex = 4;
				attNameOrLineNum = 3;
				break;
			}
			
			if (allModelNames.contains(modelName)){
				for (Model m : modelsOrder){
					if (m.getName().equals(modelName)){
						model = m;
					}
				}
			} else {
				allModelNames.add(modelName);
				model = new Model(modelName, modelType);
				modelsOrder.add(model);
			} 
			
			//Create the elements and add the element to the model's list of elements
			if (model.getEleListNames().contains(elementName)){
				for (Element e : model.getEleList()){
					if (e.getName().equals(elementName)){
						ele = e;
					}
				}
			} else {
				ele = new Element(parts[eleIndex], model);
				ele.setType(modelType);
				model.addEleList(ele);
			}
			
			//Create all the attributes and add them to the Element's list of attributes
			Attribute att = new Attribute(parts[attNameOrLineNum], parts[0], model, parts[typeIndex]);
			ele.addAttList(att);
			att.setParentEle(ele);
			
		}
	}
	
	/**
	 * Inserts the annotations read from the annotations.ct file into the data structure
	 */
	private void insertAnnotations(){
		Element parentEle;
		ArrayList<Annotation> readInAnnotations = dataManager.readAnnotaitons();
		for (Annotation ann : readInAnnotations){
			parentEle = validateAnnotation(ann.getID());
			if (!(parentEle == null)){
				//Insert the annotation into the appropriate element
				parentEle.setAnnotation(ann);
			} else {
				//Add the annotation to the list for deletion
				deletedAnnotations.add(ann);
			}
		}
	}
	
	/**
	 * Checks the validity of all annotations read in from the anotations.ct file.
	 * If the annotation is valid (i.e. the parent element still exists) then the 
	 * element is returned, else a null is returned.
	 */
	private Element validateAnnotation(String annID){
		for (Model model : modelsOrder){
			for (Element ele : model.getEleList()){
				String tempID = ele.getParentModel().getName() + ":" + ele.getName() + ":" + ele.getAttList().get(0).getId() + ":" + ele.getAttList().get(ele.getAttList().size() - 1).getId();
				if (tempID.equals(annID)){
					return ele;
				} 
			}
		}
		//Match not found
		return null;
	}
	
	/** 
     * populates a new hashtable so a user can look up the attribute 
     * details by providing the attribute's ID.
     */
	private void PopIDHT(){
		for (Model m : modelsOrder){
			for (Element e : m.getEleList()){
				for (Attribute att : e.getAttList()){
					attributeIDHT.put(att.getId(), att);
				}
			}	
		}
	}
	
	/**
	 * Populates a hashtable used to connect each model to a list
	 * of other models it is directly (forward) connected to
	 */
	private void popPathsForward(){
		Attribute origin;
		Attribute destination;
		Model modelOrigin;
		Model modelDest;
		
		for (Trace t : tracesArray){
    		origin = t.getOrigin();
    		destination = t.getDestination();
    		modelOrigin = origin.getParentModel();
    		modelDest = destination.getParentModel();
    		
    		//Populating Models HT
    		if (pathsForward.containsKey(modelOrigin)){
    			//Model already in HT therefore check the destination model is added
    			if (pathsForward.get(modelOrigin).contains(modelDest)){
    				//Do nothing
    			} else {
    				pathsForward.get(modelOrigin).add(modelDest);
    			}
    			
    		} else {
    			//Add the model to the HT
    			pathsForward.put(modelOrigin, new ArrayList<Model>());
    			pathsForward.get(modelOrigin).add(modelDest);
    		}
    		
    		//Populating Element Trace HT
    		if (elementTracePathsForward.containsKey(origin.getParentEle())){
    			//Element already in HT therefore check the destination model is added
    			if (elementTracePathsForward.get(origin.getParentEle()).contains(t)){
    				//Do nothing
    			} else {
    				//Add the trace to the HT
    				elementTracePathsForward.get(origin.getParentEle()).add(t);
    			}
    			
    		} else {
    			//Add the element and trace to the HT
    			elementTracePathsForward.put(origin.getParentEle(), new ArrayList<Trace>());
    			elementTracePathsForward.get(origin.getParentEle()).add(t);
    		}
		}
	}
		
		/**
		 * Populates a hashtable used to connect each model to a list
		 * of other models it is directly (backward) connected to
		 */
	private void popPathsBackward(){
		Attribute origin;
		Attribute destination;
		Model modelOrigin;
		Model modelDest;
			
		for (Trace t : tracesArray){
			origin = t.getOrigin();
    		destination = t.getDestination();
    		modelOrigin = origin.getParentModel();
    		modelDest = destination.getParentModel();
	    	
	   		//Populating models HT
	   		if (pathsBackward.containsKey(modelDest)){
	   			//Model already in HT therefore check the destination model is added
	   			if (pathsBackward.get(modelDest).contains(modelOrigin)){
	   				//Do nothing
	   			} else {
	   				pathsBackward.get(modelDest).add(modelOrigin);
	   			}
	   			
	   		} else {
	   			//Add the model to the HT
	   			pathsBackward.put(modelDest, new ArrayList<Model>());
	   			pathsBackward.get(modelDest).add(modelOrigin);
	   		}
	   		
	    	//Populating Element Trace HT
    		if (elementTracePathsBackward.containsKey(destination.getParentEle())){
    			//Element already in HT therefore check the destination model is added
    			if (elementTracePathsBackward.get(destination.getParentEle()).contains(t)){
    				//Do nothing
    			} else {
    				//Add the trace to the HT
    				elementTracePathsBackward.get(destination.getParentEle()).add(t);
    			}
    			
    		} else {
    			//Add the element and trace to the HT
    			elementTracePathsBackward.put(destination.getParentEle(), new ArrayList<Trace>());
    			elementTracePathsBackward.get(destination.getParentEle()).add(t);
    		}
		}
	}
	
	private boolean hasPathForward(Model next){
		if (pathsForward.containsKey(next)){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Determines the longest path length in the graph so that 
	 * the graph can be displayed in the overview mode
	 */
	public void maxPathLength(){
		ArrayList<Model> toSearch = new ArrayList<Model>();
		ArrayList<Model> visited = new ArrayList<Model>();
		ArrayList<ArrayList<Model>> roots = new ArrayList<ArrayList<Model>>();
		int currentPathLength;
		pathLengthMax = 0;
		Model next;
		boolean isNext;
		boolean incCount;
		
		//Find all root nodes. All root Models are stored in level then level is added to roots.
		ArrayList<Model> level = new ArrayList<Model>();
		for (Model m : modelsOrder){
			if (!pathsBackward.containsKey(m)){
				level.add(m);
			}
		}
		roots.add(level);
		level = new ArrayList<Model>();
		
		//Determine longest path using depth first search
		for (Model root : roots.get(0)){
			toSearch.clear();
			visited.clear();
			toSearch.add(root);
			currentPathLength = 1;
			//System.out.println("----------------------------------------------------------------");
			//System.out.println("Looking at root: " + root.getName());
			
			while (!toSearch.isEmpty()){
				incCount = false;
				next = toSearch.get(toSearch.size() - 1);
				isNext = hasPathForward(next);
				
				if (isNext && !visited.contains(next)){
					visited.add(next);
					toSearch.remove(toSearch.size() - 1);
					for (Model item : pathsForward.get(next)){
						if (!visited.contains(item) && !toSearch.contains(item)){
							incCount = true;
							toSearch.add(item);
							level.add(item);
						}
					}
					if (incCount) {
						currentPathLength++;
						roots.add(level);
						level = new ArrayList<Model>();
					}
				// TODO there is an issue here when either
					//A. There are multiple roots or
					//B. There is a single graph that has multiple branches that do not converge
					//and the longest branch is not the root branch.
				} else {
					// Update the max length if the count was larger
					if (currentPathLength > pathLengthMax){
						pathLengthMax = currentPathLength;
					}
					toSearch.remove(toSearch.size() - 1);
					if(!isNext && !toSearch.isEmpty()){
						for (int i=0; i < roots.size(); i++){
							if (roots.get(i).contains(toSearch.get(toSearch.size() - 1))){
								currentPathLength = i + 1;
							}
						}
					}
				}
			
			}
		}
	}

	/**
	 * Changes the x positions of the given elements and their attributes
	 * based on the path the user has filtered
	 */
	public void updateXPos(ArrayList<Model> path) {
		int i = 0;
		int x;
		for (Model m : path){
			if (i == 0){
				x = MARGINX_LEFT;
			} else {
				x = MARGINX_LEFT + (i * MODELSPACE);
			}
			eleList = m.getEleList();
			for (Element ele : eleList){
				ele.setXOrigin(x);
				for (Attribute att : ele.getAttList()){
					att.setX(x);
				}
			}
			i++;
		}
		
	}
	
	/**
	 * Changes the y position of the given elements and their attributes based
	 * on the path the user had filtered
	 */
	public void updateYPos(ArrayList<Model> path, ArrayList<Element> eles){
		
		for (Model m : path){
			int ypos = MARGINY + SPACE_BETWEEN_ELEMENTS;
			for (Element ele : m.getEleList()){
				if (eles.contains(ele)){
					ele.setYOrigin(ypos);
					ypos = ypos + SPACE_BETWEEN_ATTRIBUTES;
					for (Attribute att : ele.getAttList()){
						att.setY(ypos);
						ypos = ypos + SPACE_BETWEEN_ATTRIBUTES;
					}
					ypos = ypos + SPACE_BETWEEN_ELEMENTS;
				}
			}
		}
	}
	
	/** 
     * creates all traces and stores them in an array
     */
    private void makeTraces(){
    	tracesArray = new ArrayList<Trace>();
    	
    	Attribute origin;
    	Attribute destination;
    	String name;
    	String ID;
    	String type;
    	String file;
    	String natureTrace;
    	String implicitReferenceName;
	
    	//Create an array of Traces
    	for (int position = 0; position < traces.size(); position++){
    		String[] parts = traces.get(position).split(";");
    		
    		origin = attributeIDHT.get(parts[6]);
    		destination = attributeIDHT.get(parts[7]);
		
    		file = parts[2];
    		name = parts[3];
    		ID = parts[0];
    		type = parts[4];
    		natureTrace = parts[1];
    		implicitReferenceName = parts[5];
    		Trace trace = new Trace(origin, destination, name, ID, type, file, natureTrace, implicitReferenceName);
    		tracesArray.add(trace);
    	}
    }
    
    /**
	 * Returns an array list of all the annotations in all elements
	 */
	private ArrayList<Annotation> getAllAnnotations(){
		//Iterate over all elements
		ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>();
		for (Model model : getModelHT().keySet()){
			for (Element ele : getModelHT().get(model).keySet()){
				if (!(ele.getAnnotation() == null)){
					allAnnotations.add(ele.getAnnotation());
				}
			}
		}
		return allAnnotations;
	}
	
	public void save(){
		dataManager.saveAnnotations(getAllAnnotations());
	}
    
    public ArrayList<Trace> getTracesArray() {
		return tracesArray;
	}
    
    public ArrayList<Model> getModelsOrder() {
		return modelsOrder;
	}
    
    public Hashtable<Model, Hashtable<Element, ArrayList<Attribute>>> getModelHT() {
		return modelHT;
	}
	
	public Hashtable<String, Attribute> getAttributeIDHT() {
		return attributeIDHT;
	}
	
	public Hashtable<Model, ArrayList<Model>> getPathsForward() {
		return pathsForward;
	}

	public Hashtable<Model, ArrayList<Model>> getPathsBackward() {
		return pathsBackward;
	}

	public Hashtable<Element, ArrayList<Trace>> getElementTracePathsForward() {
		return elementTracePathsForward;
	}

	public Hashtable<Element, ArrayList<Trace>> getElementTracePathsBackward() {
		return elementTracePathsBackward;
	}

	public int getPathLengthMax() {
		return pathLengthMax;
	}
	public int getModelHeight(){
		return modelHeight;
	}

}