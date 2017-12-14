package edu.ca.ualberta.ssrg.chaintracker.acceleo.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoConditional;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoExpression;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoFile;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoLoop;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoModule;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoNonOCLExpresion;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.AcceleoTemplate;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.ModelAccess;
import edu.ca.ualberta.ssrg.chaintracker.acceleo.vos.ModelContext;
import edu.ca.ualberta.ssrg.chaintracker.ecore.EcoreSolver;
import edu.ca.ualberta.ssrg.chaintracker.ecore.EcoreSolverException;
import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelElement;

/**
 * AcceleoTransformationParser parses Acceleo template files
 * and extracts information about model accesses in different
 * Acceleo expressions (ex. loops, conditionals, simple expressions).
 *
 * For each model access it stores information about its:
 *  1) model context type
 *  2) model context instance
 *  3) model attribute expression
 * where applicable.
 * 
 */
public class AcceleoTransformationParser {
	
	private static final String DEFAULT_METAMODEL = "./data/acceleotests/metamodels/phy2templates/Layout.ecore";
	private static final String DEFAULT_TEMPLATE = "./data/acceleotests/templates/phy2templates/generateLayout.mtl";
	
	private static final String COMMENT_OPEN_TAG = "[comment";
	private static final String FILE_OPEN_TAG  = "[file";
	private static final String TEMPLATE_OPEN_TAG  = "[template";
	private static final String TEMPLATE_CLOSE_TAG  = "[/template]";
	private static final String MODULE_OPEN_TAG = "[module";
	private static final String IF_OPEN_TAG = "[if";
	private static final String IF_CLOSE_TAG = "[/if]";
	private static final String ELSEIF_OPEN_TAG = "[elseif";
	private static final String ELSE_OPEN_TAG = "[else]";
	private static final String FOR_OPEN_TAG = "[for";
	private static final String FOR_CLOSE_TAG = "[/for]";
	
	private String templatePath;
	
	private String inMetamodelPath;
	
	private String modelName;
	
	private EcoreSolver ecoreSolver;
	private List<ModelElement> modelElements;
	private List<String> modelAttributes;
	private List<ModelElement> localContextModelElements;
	private ArrayList<ArrayList<ModelElement>> rootContextModelElements;
	
	private AcceleoRecursionBridge fileData;
	
	private Hashtable<Integer, String> comments;
	
	private Hashtable<Integer, AcceleoFile> files;
	
	private Hashtable<Integer, ArrayList<AcceleoTemplate>> templates;
	
	private Hashtable<Integer, ArrayList<AcceleoModule>> modules;
	
	private Hashtable<Integer, ArrayList<AcceleoLoop>> loops;
	
	private Hashtable<Integer, ArrayList<AcceleoConditional>> conditionals;
	
	private List<Integer> lineNumbersParentIf;
	
	private Hashtable<Integer, ArrayList<AcceleoNonOCLExpresion>> simpleExpressions;
	
	private ArrayList<String> modelContexts;
	private ArrayList<String> modelInstanceContexts;

	public void initialize(String inMetamodelPath, String templatePath) throws TransformationParserException{
		this.inMetamodelPath = inMetamodelPath;
		this.templatePath = templatePath;
		
		fileData = AcceleoRecursionBridge.getInstance();
		fileData.setFileLines(new Hashtable<Integer, String>());
		fileData.setLineNumber(0);
		
		comments = new Hashtable<>();
		files = new Hashtable<>();
		templates = new Hashtable<>();
		modules = new Hashtable<>();
		
		loops = new Hashtable<>();
		conditionals = new Hashtable<>();
		simpleExpressions = new Hashtable<>();
		
		lineNumbersParentIf = new ArrayList<Integer>();
		
		modelContexts = new ArrayList<String>();
		modelInstanceContexts = new ArrayList<String>();
		
		// Gets a list of module attributes from the ecore file
		// using the EcoreParser - this is used to help parse IF statements
		// as the parser needs to know which statements are just OCL methods
		// and which statements are model acceses 
		initializeModelAttributes();
	}
	
	/**
	 * Gets a list of model elements from the given Ecore metamodel.
	 * Also initializes the model name using the Ecore file name.
	 * 
	 * @throws TransformationParserException
	 */
	private void initializeModelAttributes() throws TransformationParserException {
		String[] fileNameParts = this.inMetamodelPath.split("/");
		this.modelName = fileNameParts[fileNameParts.length - 1].split("\\.")[0];
		
		this.ecoreSolver = new EcoreSolver();
		ecoreSolver.setModelFile(this.inMetamodelPath);
		try {
			modelAttributes = ecoreSolver.getAttributes(this.inMetamodelPath);
		} catch (EcoreSolverException e) {
			e.printStackTrace();
			throw new TransformationParserException(e.getMessage());
		}
		
		ecoreSolver.initialize();
		modelElements = ecoreSolver.getModelElements(this.inMetamodelPath);
		
		rootContextModelElements = new ArrayList<ArrayList<ModelElement>>();
		localContextModelElements = new ArrayList<ModelElement>();
	}
	
	/**
	 * Goes through the template file and parses each line 
	 * looking for Acceleo definitions and expressions.
	 */
	public void parse(){
		
		try {
			
			FileInputStream fs = new FileInputStream(templatePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			fileData.setLineNumber(1);
			String line = br.readLine();
			
			// Parse each file line
			while (line != null) {
				// Store the file line
				fileData.putLine(fileData.getLineNumber(), line);
			
				// Check for different Acceleo definitions and expressions
				checkForAcceleoDefinitionsAndExpressions(line, br);
				
				// Read next line
				line = br.readLine();
				fileData.setLineNumber(fileData.getLineNumber()+1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedReader checkForAcceleoDefinitionsAndExpressions(String line, BufferedReader br) {
		// Check for different Acceleo definitions and expressions
		this.checkForFileDefinition(line, fileData.getLineNumber());
		this.checkForComments(line, fileData.getLineNumber());
		this.checkForTemplateDefinition(line, fileData.getLineNumber());
		this.checkForTemplateCloseDefinition(line);
		this.checkForModuleDefinition(line, fileData.getLineNumber());
		
		this.checkForNonOCLExpressions(line, fileData.getLineNumber());
		
		br = this.checkForLoopDefinitions(line, fileData.getLineNumber(), br);
		
		br = this.checkForConditional(line, fileData.getLineNumber(), br);
		
		return br;
	}
	
	/**
	 *  Gets the body of an Acceleo tag expression. 
	 *  Ie. the expression without the Acceleo open/closing tags
	 *  
	 * @param expression
	 * @param openTag
	 * @return
	 */
	public String getExpressionBody(String expression, String openTag) {
		
		if(!expression.contains(openTag)) return null;

		// Get the expression in between the opening and closing braces
		int starForIndex = expression.indexOf(openTag);
		int endForIndex = expression.indexOf("/]"); // case for expressions that close in same line as open
		if (endForIndex < 0) {
			endForIndex = expression.indexOf("]"); // case for expressions that have separate open and close
		}
		
		if (starForIndex < 0 || endForIndex < 0) return null;
		
		return expression.substring(starForIndex + openTag.length(), endForIndex)
				.trim() 
				.replaceAll("^\\(|\\)$", "") // strip brackets at the start & end of expression 
				.trim(); 
	}
	
	/**
	 * Gets the comment body from an Acceleo comment tag
	 * and stores it in the comments hashtable using the line number
	 * as a key.
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public void checkForComments(String line, int lineNumber){
		
		if(!line.contains(COMMENT_OPEN_TAG)) return;
		
		String comment = getExpressionBody(line, COMMENT_OPEN_TAG);
		if (comment != null) {
			comments.put(lineNumber, comment);
		}
	}
	
	/**
	 * Gets the file name from an Acceleo file tag and
	 * stores it in the comments hashtable using the line number
	 * as a key.
	 * 
	 * The Acceleo file tag has other paramters besides file name 
	 * (ex. append mode, file charset), but we are only interested in the
	 * first paramter, which is the file name.
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public void checkForFileDefinition(String line, int lineNumber){
		
		if(!line.contains(FILE_OPEN_TAG)) return;
		
		// Get the expression body
		String body = getExpressionBody(line, FILE_OPEN_TAG);
		if (body == null) return;
		
		String []paramList = body.split(",");
		// file tag is made up of 3 parts: file name, append mode, charset 
		String fileName = paramList[0];
		String appendMode = paramList[1];
		String charset = "";
		if (paramList.length > 2) {
			charset = paramList[2];
		}
		
		// Strip the quotes from the file name & charset before storing
		fileName = stripQuotes(fileName); 
		charset = stripQuotes(charset);
		
		// Check for model context in filename
		String expModelContext = null;
		String expModelInstanceContext = null;
		ModelContext modelContext = getModelContext(fileName);
		if (modelContext!= null) {
			expModelContext = modelContext.getModelContextType();
			expModelInstanceContext = modelContext.getModelContextInstance();
			
			// Strip the model context access
			fileName = stripModelInstance(expModelInstanceContext, fileName);
		}
		
		if (fileName != null) {
			// Create AcceleoFile object
			AcceleoFile f = new AcceleoFile();
			
			f.setFileName(fileName);
			f.setAppendMode(appendMode.equals("true"));
			f.setCharset(charset);
			
			f.setModelContext(expModelContext);
			f.setModelInstanceContext(expModelInstanceContext);
			
			files.put(lineNumber, f);
		}
	}
	
	/**
	 * Gets the template information from an Acceleo template tag and
	 * stores it in the template hashtable using the line number
	 * as a key.
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public void checkForTemplateDefinition(String line, int lineNumber){
		
		if(!line.contains(TEMPLATE_OPEN_TAG)) return;

		String visibility = "";
		String templateName = "";
		String methodName = "";
		String modelParamID = "";
		String modelParamInstanceID = "";

		// Get the expression body
		String body = getExpressionBody(line, TEMPLATE_OPEN_TAG);
		if (body == null) return;
		
		// Format for template tag: public generateElement(aScoreRules : ScoreRules)
		// This Regex looks for the 4 words  that make up the template tag using the Regex group "(\\w+)": 
		// ex. visibility = public, methodName = generateElement, modelParamInstanceID = aScoreRules, modelParamID = ScoreRules
		Pattern templatePattern = Pattern.compile("(\\w+)\\s+((\\w+)\\s*\\((\\w+)\\s+:\\s+(\\w+))");
		Matcher m = templatePattern.matcher(body);
		if (m != null && m.find()) {
			int groupCount = m.groupCount();
			if (groupCount > 0) visibility = m.group(1);
			if (groupCount > 1) templateName = m.group(2);
			if (groupCount > 2) methodName = m.group(3);
			if (groupCount > 3) modelParamInstanceID = m.group(4);
			if (groupCount > 4) modelParamID = m.group(5);
		} else {
			return;
		}
		
		// Create AcceleoTemplate object and add it to the hashtable of
		// AcceleoTemplates using the current line number as the key
		AcceleoTemplate at  = new AcceleoTemplate();
		at.setVisibility(visibility);
		at.setMethodName(methodName);
		at.setModelParamID(modelParamID);
		at.setModelParamInstanceID(modelParamInstanceID);
		at.setTemplateName(templateName + ")");
		templatesPut(lineNumber, at);
		
		//Set context assuming module has only one parameter
		// add model context to list of contexts
		modelContexts.add(modelParamID);
		modelInstanceContexts.add(modelParamInstanceID);
		List<ModelElement> rootModelElement = ecoreSolver.getModelElementByModelType(modelContexts.get(0), modelElements);
		rootContextModelElements.add(new ArrayList<ModelElement>(rootModelElement));
	}
	
	
	/**
	 * Checks for a closing Acceleo template tag and removes
	 * the model context / model instance context added from
	 * the opening Acceleo template tag from the current list.
	 * 
	 * @param line
	 */
	public void checkForTemplateCloseDefinition(String line){
		if(!line.contains(TEMPLATE_CLOSE_TAG)) return;
		
		removeLast(modelContexts);
		removeLast(modelInstanceContexts);
		removeLast(rootContextModelElements);
	}
	
	/**
	 * Given a line, checks if its accesses a model context
	 * and returns the model context. 
	 * If it does not explicitly access any model context, 
	 * it checks if it is implicitly accessing a local context or
	 *  the root model context,
	 * 
	 * The tags that can create contexts are Acceleo template 
	 * and loop tags. Each time a template or loop tag is encountered, 
	 * a context is added to the list of model contexts 
	 * (one for the model context ex. 'AppearActivity',
	 * and one for the model instance context ex. 'a'). 
	 * 
	 * The root model comes from the model context created by 
	 * the Acceleo template tag, and this will be the first 
	 * model context in the list of model contexts. 
	 * 
	 * The local model context occurs in statements like:
	 * '[collisions->first().collisionAction.removeActorId/]'
	 * collisions->first() creates a local context when we are
	 * parsing 'collisionAction.removeActorId'
	 * 
	 * @param expression - ex. 'actor.isBall'
	 */
	public ModelContext getModelContext(String expression) {
		int index = -1; 
		
		if (expression == null) return null;
		if (modelContexts.size() < 1) return null;

		// ModelContext consists of a type and (potential) instance
		String contextType = null;
		String contextInstance = "";
		
		// Strip starting "." if present
		expression = expression.replaceAll("^\\.", "");
		// Split expression by . 
		List<String> parts = Arrays.asList(expression.split("\\."));
		
		// If there is a model context access, it will be in the first
		// part of the expression
		String modelAccess = parts.get(0);

		// Compare the model access with each model context instance.
		// Do this in reverse order as the last added model context
		// is the most inner scope model context
		for (int i = modelInstanceContexts.size() - 1; i >= 0; i --) {
			if (modelAccess.equals(modelInstanceContexts.get(i))) {
				index = i;
				break;
			}
		}
		
		if (index != -1) { // EXPLICIT model access occured
			contextType = modelContexts.get(index);
			contextInstance = modelInstanceContexts.get(index);
			return new ModelContext(contextType, contextInstance);
		}
		
		// else IMPLICIT model access 
		
		// Check if IMPLICITLY accessing a local model context
		ModelElement modelElem = this.ecoreSolver.getModelElementByMember(expression, localContextModelElements); 
		if (modelElem == null) {
			// Check if IMPLICITLY accessing the ROOT context
			modelElem = this.ecoreSolver.getModelElementByMember(expression, rootContextModelElements.get(rootContextModelElements.size() - 1));
		}
		if (modelElem != null) {
			contextType = modelElem.getModelElement();
		}
		
		if (contextType != null) {
			int instanceIndex = modelContexts.indexOf(contextType);
			if (instanceIndex != -1) {
				// In the case of accessing root context model, 
				// we actually have instance stored so get it!
				contextInstance = modelInstanceContexts.get(instanceIndex);
			}
			return new ModelContext(contextType, contextInstance);
		}
		
		return null;
	}
	
	/**
	 * Gets the module information from an Acceleo module tag and
	 * stores it in the module hashtable using the line number
	 * as a key.
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public void checkForModuleDefinition(String line, int lineNumber){
		
		if (!line.contains(MODULE_OPEN_TAG)) return;
				
		String moduleName = "";
		String modelURI = "";
		
		// Get the expression body
		String body = getExpressionBody(line, MODULE_OPEN_TAG);
		if (body == null) return;
		
		// Format for module tag: [module generateScoring('http://ualberta.edu.cs.ssrg.phy.scoring')]
		// This Regex looks for the 2 words  that make up the module tag using 2 Regex groups: 
		// ex. moduleName = generateScoring, moduleURI = http://ualberta.edu.cs.ssrg.phy.scoring
		Pattern modulePattern = Pattern.compile("(\\w+)\\W+([\\w://\\.]+)");
		Matcher m = modulePattern.matcher(body);
		if (m != null && m.find()) {
			int groupCount = m.groupCount();
			if (groupCount > 0) moduleName = m.group(1);
			if (groupCount > 1) modelURI = m.group(2);
		} else {
			return;
		}
				
		// Create AcceleoModule object and add it to the hashtable of
		// AcceleoModules using the current line number as the key
		AcceleoModule moduleT = new AcceleoModule();
		moduleT.setModelURI(modelURI);
		moduleT.setModuleName(moduleName);
		modulesPut(lineNumber, moduleT);
	}
	
	/**
	 * Gets the loop information from an Acceleo conditional tag and
	 * stores it in the conditional hashtable using the line number
	 * as a key.
	 * 
	 * Then calls processInnerBody to process the inner body of the conditinal.
	 * 
	 * @param line
	 * @param lineNumber
	 * @param br
	 * @return
	 */
	public BufferedReader checkForConditional(String line, int lineNumber, BufferedReader br) {

		if(!line.contains(IF_OPEN_TAG) 
				&& !line.contains(ELSEIF_OPEN_TAG)
				&& !line.contains(ELSE_OPEN_TAG)) return br;

		AcceleoConditional accConditional = null;
		
		boolean ifConditional = false;
		// Assumption that only one conditional tag per time
		if (line.contains(ELSE_OPEN_TAG)) {
			accConditional = new AcceleoConditional();
			accConditional.setType(AcceleoConditional.ConditionalType.Else);
			accConditional.getModelContext().add(getLast(modelContexts)); 
			accConditional.getModelInstanceContext().add(getLast(modelInstanceContexts));
			accConditional.getExpression().add("");
			conditionalsPut(fileData.getLineNumber(), accConditional);
		} else {
		
			ifConditional = true;
			String body = getExpressionBody(line, IF_OPEN_TAG);
			if (body == null) {
				ifConditional = false;
				body = getExpressionBody(line, ELSEIF_OPEN_TAG);
				
				if (body == null) return br;
			}
			
			// Split up expression body into parts by spaces and braces ( and )
			List<String> condParts = getExpressionParts(body);
			
			// Iterate through each expression part to find model dependencies
			List<ModelAccess> modelAccesses = processExpression(condParts);
			for (ModelAccess modelAccess : modelAccesses) {
				// create AcceleoConditional and add it to hashtable of AcceleoConditionals
				// using the line number as a key
				if (accConditional == null) {
					// if the conditional has not been created yet, create it
					accConditional = new AcceleoConditional();
					if(ifConditional) { 
						accConditional.setType(AcceleoConditional.ConditionalType.If);
						accConditional.setLineNumber_parent_if(fileData.getLineNumber());
						lineNumbersParentIf.add(fileData.getLineNumber());
					} else {
						accConditional.setType(AcceleoConditional.ConditionalType.Elseif);
					}
					conditionalsPut(fileData.getLineNumber(), accConditional);
				}
				
				// update the value lists in the conditional
				accConditional.getModelContext().add(modelAccess.getModelContext().getModelContextType()); 
				accConditional.getModelInstanceContext().add(modelAccess.getModelContext().getModelContextInstance());
				accConditional.getExpression().add(modelAccess.getModelAttributeExpression());
			}
		}
		
		if (accConditional == null) return br;
		
		br = processConditionalInnerBody(accConditional, lineNumber, br);
		
		if (accConditional.getEndLine() == null) {
			accConditional.setEndLine(fileData.getLineNumber());
		}
		
		Integer recentLineNumber = lineNumbersParentIf.size() - 1;
		if (recentLineNumber >= 0) {
			if (ifConditional) {
				lineNumbersParentIf.remove(recentLineNumber);
			} else {
				accConditional.setLineNumber_parent_if(lineNumbersParentIf.get(recentLineNumber));
			}
		}
		
		return br;
	}
	
	private BufferedReader processConditionalInnerBody(AcceleoConditional parentConditional, int lineNumber, BufferedReader br){
		
		try {
			String line = br.readLine();			
			fileData.setLineNumber(fileData.getLineNumber()+1);
			
			while (line != null) {
				
				fileData.putLine(fileData.getLineNumber(), line);
				
				//	exit case loop/conditional is over
				if(line.contains(IF_CLOSE_TAG)){
					return br;
				}
				else if (line.contains(ELSEIF_OPEN_TAG) || line.contains(ELSE_OPEN_TAG)){
					if (parentConditional.getEndLine() == null) parentConditional.setEndLine(fileData.getLineNumber());
					checkForConditional(line, fileData.getLineNumber(), br);
					return br;
				}

				// Check for different Acceleo definitions and expressions
				checkForAcceleoDefinitionsAndExpressions(line, br);
				
				line = br.readLine();
				fileData.setLineNumber(fileData.getLineNumber()+1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return br;
	}
	
	/**
	 * Gets the loop information from an Acceleo loop tag and
	 * stores it in the loop hashtable using the line number
	 * as a key.
	 * 
	 * Then calls processInnerBody to process the inner body of the loop.
	 * 
	 * @param line
	 * @param lineNumber
	 * @param br
	 * @return
	 */
	public BufferedReader checkForLoopDefinitions(String line, int lineNumber, BufferedReader br){
		
		if(!line.contains(FOR_OPEN_TAG)) return br;
			
		String body =  getExpressionBody(line, FOR_OPEN_TAG);
		if (body == null) return br;
		
		String iteratorID = "";
		String iteratedModel = "";
		String navigationRelation = "";
		
		// Format for loop tag: [for (timeRule: TimedRule | timed)]
		// This Regex looks for the 3 words  that make up the loop tag using the Regex group "(\\w+)": 
		// ex. iteratorID = timeRule, iteratedModel = TimedRule, navigationRelation = timed
		// the navigationRelation can have '.' (ex. 'app.screens'), so allow '.' in the last Regex group
		Pattern loopPattern = Pattern.compile("(\\w+)\\s*:\\s*(\\w+)\\s*\\|\\s*([\\w\\.]+)");
		Matcher m =loopPattern.matcher(body);
		if (m != null && m.find()) {
			int groupCount = m.groupCount();
			if (groupCount > 0) iteratorID = m.group(1);
			if (groupCount > 1) iteratedModel = m.group(2);
			if (groupCount > 2) navigationRelation = m.group(3);
		} else {
			return br;
		}
		
		// Get the model context of navigation relation
		String expModelContext = null;
		String expModelInstanceContext = null;
		ModelContext modelContext = getModelContext(navigationRelation);
		if (modelContext!= null) {
			expModelContext = modelContext.getModelContextType();
			expModelInstanceContext = modelContext.getModelContextInstance();
			
			// Strips model context access from navigation relation if applicable
			navigationRelation = stripModelInstance(expModelInstanceContext, navigationRelation);
		}
			
		// Create Acceleo loop and add to loop table
		AcceleoLoop al = new AcceleoLoop();
		al.setModelContext(expModelContext);
		al.setModelInstanceContext(expModelInstanceContext);
		al.setIteratedModel(iteratedModel);
		al.setNavigationRelation(navigationRelation);
		al.setIteratorID(iteratorID);
		loopsPut(lineNumber, al);
		
		// update the model contexts with the loops iterated model
		modelContexts.add(iteratedModel);
		modelInstanceContexts.add(iteratorID);
		
		// processes the inner body of the loop
		br = processLoopInnerBody(lineNumber, br);
		
		// set the ending line of the loop
		al.setEndLine(fileData.getLineNumber());
		
		return br;
	}
	
	private BufferedReader processLoopInnerBody(int lineNumber, BufferedReader br){
		
		try {
			String line = br.readLine();
			fileData.setLineNumber(fileData.getLineNumber()+1);
			
			while (line != null) {
				
				fileData.putLine(fileData.getLineNumber(), line);
				
				//	exit case loop/conditional is over
				if(line.contains(FOR_CLOSE_TAG)){
					removeLast(modelContexts);
					removeLast(modelInstanceContexts);
					return br;
				}
				// Check for different Acceleo definitions and expressions
				checkForAcceleoDefinitionsAndExpressions(line, br);
				
				line = br.readLine();
				fileData.setLineNumber(fileData.getLineNumber()+1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return br;
	}
	
	/**
	 * Gets information from all Acceleo simple expression tags 
	 * (those with just a [ and /] around the expression body)
	 * and adds it to simple expression hashtable using line
	 * number as key
	 * 
	 * @param line
	 * @param lineNumber
	 */
	public void checkForNonOCLExpressions(String line, int lineNumber){
		if (modelContexts.size() == 0) {
			//Not inside template so not simple expression
			return;
		}
		
		List<String> simpleExpressions = getAllSimpleExpressions(line);
		
		List<ModelAccess> modelAccesses = processExpression(simpleExpressions);
		for (ModelAccess modelAccess : modelAccesses) {
			// Create the AcceleoNonOCLExpresion and add to simple expressions table
			AcceleoNonOCLExpresion accsimple = new AcceleoNonOCLExpresion();
			accsimple.setModelContext(modelAccess.getModelContext().getModelContextType()); 
			accsimple.setModelInstanceContext(modelAccess.getModelContext().getModelContextInstance());
			accsimple.setExpression(modelAccess.getModelAttributeExpression());
			simpleExpressionsPut(lineNumber, accsimple);
		}
	}
	
	/**
	 *  Gets the body of all Acceleo simple expression tags in a line. 
	 *  Ie. the expressions without the Acceleo open/closing tags
	 *  
	 *  Also splits the body into multiple "simple expressions"
	 *  by using braces and spaces as seperators.
	 *  
	 *  Currently multiple simple expressions per line is expected,
	 *  but this case is not handled for the other types of expressions.
	 *  
	 * @param line
	 * @return bodies of all Acceleo simple expressions in given line
	 */
	private List<String> getAllSimpleExpressions(String line) {
		
		List<String> bodies = new ArrayList<String>();
		
		// Use Regex to find expressions of form : [body/]
		// body is group 1 in our Regex pattern
		// The [^\\'] makes sure the opening brace is not quoted
		Pattern expressionPattern = Pattern.compile("\\[([^\\[]+)/\\]");
		Matcher m = expressionPattern.matcher(line);
		
		// Keep calling Regex find until run out of matches
		while (m != null && m.find()) {
			String sExp = m.group(1).trim();
			// Split up expression into parts by spaces and braces ( and )
			List<String> sExpParts = getExpressionParts(sExp);
			bodies.addAll(sExpParts);
		}
		
		return bodies;
	}
	
	/**
	 * Given a list of expressions, returns a list of ModelAccess.
	 * 
	 * Used for Acceleo conditionals & simple expressions.
	 *  
	 * @param expressions
	 * @return returns a list of ModelAccesses that contain:
	 * (1) model context
	 * (2) model context instance
	 * (3) model attribute expression
	 */
	private List<ModelAccess> processExpression(List<String> expressions) {
		ArrayList<ModelAccess> result = new ArrayList<ModelAccess>();
		
		for (String exp : expressions) {
			
			// Split expression based on '.' seperator, brackets and -> from method application
			List<String> furtherParts = Arrays.asList(exp.split("[\\s\\(\\)-]"));
			for (String part : furtherParts) {
				// If expression is a special character (see method for hardcoded list)
				// , it is not a simple expression so skip
				if (isSpecialCharacter(part)) continue;
				
				// Get the model context of the expression
				String expModelContext = null;
				String expModelInstanceContext = null;
				ModelContext modelContext = getModelContext(part);
				String stripedExp = part;
				if (modelContext!= null) {
					expModelContext = modelContext.getModelContextType();
					expModelInstanceContext = modelContext.getModelContextInstance();
					
					// Strip the model context access in the expression 
					stripedExp = stripModelInstance(expModelInstanceContext, part); 
				}
				
				boolean hadModelAccess = (!stripedExp.equals(part));
				
				// Strip OCL expression
				// Ex. in background.cameraType.equals('none'), only background.cameraType
				stripedExp = stripOCLExpression(stripedExp);
				
				// If the expression is null, than there was no model accesses so just continue to next
				if (stripedExp == null && !hadModelAccess) continue;
				if (stripedExp == null) stripedExp = "";
				
				// Create the ModelAccess and add to result list
				ModelAccess modelAccess = new ModelAccess();
				modelAccess.setModelContext(new ModelContext(expModelContext, expModelInstanceContext));
				modelAccess.setModelAttributeExpression(stripedExp);
				result.add(modelAccess);
				
				/*
				 * The local model context occurs in statements like:
				 * '[collisions->first().collisionAction.removeActorId/]'
				 * collisions->first() creates a local context when we are
				 * parsing 'collisionAction.removeActorId'
				 */
				// add local model context
				List<String> attributeParts = Arrays.asList(stripedExp.split("\\."));
				String lastAttribute = null;
				if (attributeParts.size() > 0) {
					lastAttribute = getLast(attributeParts);
				}
				ModelElement modelElem = ecoreSolver.getModelElementByMember(lastAttribute, modelElements);
				if (modelElem != null) {
					List<ModelElement> test = ecoreSolver.getModelElementByModelType(modelElem.getType(), modelElements);
					localContextModelElements.addAll(test);
				}
			}
			
			// clear local context models
			localContextModelElements = new ArrayList<ModelElement>();
		}
		
		return result;
	}
	
	/**
	 * Hardcoded special characters that we need to NOT
	 * match our search for simple expressions.
	 * 
	 * Ex. [ '[' /] and [ ']' /] 
	 * The above expressions we do not want to parse
	 * because they are just necessary escaping for 
	 * square braces 
	 * 
	 * @param line
	 * @return
	 */
	private boolean isSpecialCharacter(String line){
		return (line.equals("'['") || line.equals("']'"));
	}
	
	/**
	 * Split up expression body into parts by spaces and braces ( and )
	 * 
	 * @param exp
	 * @return
	 */
	public List<String> getExpressionParts(String exp) {
		// split expressions by space
		ArrayList<String> result =  new ArrayList<String>(Arrays.asList(exp.split("[\\s]")));

		// find expressions within () 
		Pattern innerExpressionPattern = Pattern.compile("\\(([^\\)\\(]+)\\)");
		Matcher m = innerExpressionPattern.matcher(exp);
		ArrayList<String> innerExpressions = new ArrayList<String>(); 
		while (m != null && m.find()) {
			String innerExp = m.group(1);
			if (innerExp != null) {
				result.add(innerExp);
			}
			innerExpressions.add(innerExp);
		} 
		// replace all inner expressions we just captured with blank in the
		// original expression
		exp = m.replaceAll("");
		
		// split the original expression on space again now that we've removed inner expressions
		result = new ArrayList<String>(Arrays.asList(exp.split("[\\s]")));
		// add inner expressions
		for (String part : innerExpressions) {
			result.add(part);
		}
		
		ArrayList<String> result2 = new ArrayList<String>(); 
		for (String part : result) {
			result2.add(part.replaceAll("^\\(|\\)$", "")); // strip brackets at the start & end of expression 
		}
		
		return result2;
	}
	
	/**
	 * Strips surrounding quotes from a string,
	 * if applicable.
	 * 
	 * @param s
	 * @return
	 */
	private String stripQuotes(String s) {
		s = s.trim();
		
		Pattern quotesPattern = Pattern.compile("^\\'(.*)\\'$");
		Matcher m = quotesPattern.matcher(s);
		if (m != null && m.find()) {
			s = m.group(1);
		} 
		
		return s.trim();
	}
	
	/**
	 * Strips the specified model instance access from a string
	 * and returns it.
	 * 
	 * @param modelInstanceContext
	 * @param exp
	 * @return
	 */
	private String stripModelInstance(String modelInstanceContext, String exp) {
		// access model instance only, no additional attributes
		if (exp.equals(modelInstanceContext)) return "";
		
		// Check for access to the modelInstanceContext at the start of the expression
		Pattern rootInstancePattern = Pattern.compile("^" + Pattern.quote(modelInstanceContext) + "\\.");
		Matcher rootMatch = rootInstancePattern.matcher(exp);
		if (rootMatch.find()) {
			// If find model access, replace with empty
			exp = rootMatch.replaceAll("");
		}
		
		return exp;
	}
	
	/**
	 * Strips OCLExpression from expression:
	 * 
	 * ex: name.toUpper() => name
	 * 
	 * @param exp
	 * @return
	 */
	private String stripOCLExpression(String exp) {
		String stripedExpression = null;

		// Strip starting "." if present
		exp = exp.replaceAll("^\\.", "");

		// Split expression part into further parts using . 
		// We need to find out where the model access ends
		// Ex. in background.cameraType.equals('none'), only background.cameraType
		// is a model access
		List<String> parts = Arrays.asList(exp.split("\\."));
		
		for (String p : parts) {
			
			// check if the list of model attributes (obtained from the Ecore file)
			// contains value of p => if it does not, that means model access is done so break
			if (!modelAttributesContains(p)) break;
			
			// p /is/ a model access so add it to the expression
			if (stripedExpression == null) {
				stripedExpression = "";
			} else {
				stripedExpression += ".";
			}
			stripedExpression += p;
		}
		
		return stripedExpression;
	}
	
	/**
	 * Check if the provided attribute is contained in the list 
	 * of model attributes. Also does a check if it is an escaped attribute
	 * (uses _).
	 * 
	 * @param att
	 * @return
	 */
	private boolean modelAttributesContains(String att) {
		if (modelAttributes == null) return false;
		
		return (modelAttributes.contains(att) || modelAttributes.contains(att.replaceAll("^_", "") ) );
	}
	
	//********************************************************************
	// Helpers
	//********************************************************************
	/**
	 * @return last element in a list, else returns null
	 */
	private <T> T getLast(List<T> list) {
		int last = list.size() - 1;
		if (last < 0) return null;
		
		return list.get(last);
	}
	
	/**
	 * Removes last element of list if applicable
	 * @param list
	 * @return
	 */
	private <T> void removeLast(ArrayList<T> list) {
		int last = list.size() - 1;
		if (last < 0) return;
		
		list.remove(last);
	}
	
	/**
	 *  Adds an AcceleoConditional to the hash of conditionals using
	 *  the line number as the key.
	 *  
	 * @param lineNumber
	 * @param ex
	 */
	private void conditionalsPut(int lineNumber, AcceleoConditional ex){
		
		ArrayList<AcceleoConditional> pre = conditionals.get(lineNumber);
		//FirstTime
		if(pre == null){
			pre = new ArrayList<>();
			pre.add(ex);
			conditionals.put(lineNumber, pre);
		}
		else{
			pre.add(ex);
			conditionals.put(lineNumber, pre);
		}
	}
	
	/**
	 *  Adds an AcceleoNonOCLExpresion to the hash of simple
	 *  expressions using the line number as the key.
	 *  
	 * @param lineNumber
	 * @param ex
	 */
	private void simpleExpressionsPut(int lineNumber, AcceleoNonOCLExpresion ex){
		ex.setEndLine(lineNumber);
		
		ArrayList<AcceleoNonOCLExpresion> pre = simpleExpressions.get(lineNumber);
		//FirstTime
		if(pre == null){
			pre = new ArrayList<>();
			pre.add(ex);
			simpleExpressions.put(lineNumber, pre);
		}
		else{
			pre.add(ex);
			simpleExpressions.put(lineNumber, pre);
		}
	}
	
	/**
	 *  Adds an AcceleoModule to the hash of modules
	 *  using the line number as the key.
	 *  
	 * @param lineNumber
	 * @param ex
	 */
	private void modulesPut(int lineNumber, AcceleoModule ex){
		
		ArrayList<AcceleoModule> pre = modules.get(lineNumber);
		//FirstTime
		if(pre == null){
			pre = new ArrayList<>();
			pre.add(ex);
			modules.put(lineNumber, pre);
		}
		else{
			//Add to existing list
			pre.add(ex);
			modules.put(lineNumber, pre);
		}
		
	}
	
	/**
	 *  Adds an AcceleoTemplate to the hash of templates
	 *  using the line number as the key.
	 *  
	 * @param lineNumber
	 * @param ex
	 */
	private void templatesPut(int lineNumber, AcceleoTemplate ex){
		
		ArrayList<AcceleoTemplate> pre = templates.get(lineNumber);
		//FirstTime
		if(pre == null){
			pre = new ArrayList<>();
			pre.add(ex);
			templates.put(lineNumber, pre);
		}
		else{
			//Add to existing list
			pre.add(ex);
			templates.put(lineNumber, pre);
		}
		
	}
	
	/**
	 *  Adds an AcceleoLoop to the hash of loops
	 *  using the line number as the key.
	 *  
	 * @param lineNumber
	 * @param ex
	 */
	private void loopsPut(int lineNumber, AcceleoLoop ex){
		
		ArrayList<AcceleoLoop> pre = loops.get(lineNumber);
		//FirstTime
		if(pre == null){
			pre = new ArrayList<>();
			pre.add(ex);
			loops.put(lineNumber, pre);
		}
		else{
			//Add to existing list
			pre.add(ex);
			loops.put(lineNumber, pre);
		}
	}	

	//********************************************************************
	// Pretty Printers
	//********************************************************************
	
	public void printSimpleExpressions(){
		
		Iterator<Integer> lineKeys = simpleExpressions.keySet().iterator();
		while(lineKeys.hasNext()){
			int currentLine=lineKeys.next();
			ArrayList<AcceleoNonOCLExpresion> exps = simpleExpressions.get(currentLine);
			for(AcceleoNonOCLExpresion t: exps){
				System.out.println(currentLine+" simple -"+ t.getExpression());
			}
			
		}
	}
	
	public void printLoopExpressions(){
		
		Iterator<Integer> lineKeys = loops.keySet().iterator();
		while(lineKeys.hasNext()){
			int currentLine=lineKeys.next();
			ArrayList<AcceleoLoop> exps = loops.get(currentLine);
			for(AcceleoLoop t: exps){
				System.out.println(currentLine+" for -"+ t.getIteratedModel());
			}
			
		}
	}
	
	public void printConditionalExpressions(){
		
		Iterator<Integer> lineKeys = conditionals.keySet().iterator();
		while(lineKeys.hasNext()){
			int currentLine=lineKeys.next();
			ArrayList<AcceleoConditional> conds = conditionals.get(currentLine);
			for(AcceleoConditional t: conds){
				List<String> expressions = t.getExpression();
				System.out.println(currentLine+" conditional -");
				for (String e : expressions) {
					System.out.println("\t"+ e);
				}
			}
			
		}
	}
	
	private void printFileWithLines(){

		Iterator<Integer> lineKeys = fileData.getFileLines().keySet().iterator();
		while(lineKeys.hasNext()){
			int currentLine=lineKeys.next();
			System.out.println(currentLine+" -"+fileData.getFileLines().get(currentLine));
		}
	}

	//********************************************************************
	// Getters & Setters
	//********************************************************************
	
	public Hashtable<Integer, String> getComments() {
		return comments;
	}

	public Hashtable<Integer, AcceleoFile> getFiles() {
		return files;
	}

	public Hashtable<Integer, ArrayList<AcceleoTemplate>> getTemplates() {
		return templates;
	}
	
	/**
	 * Returns the template name for a given line number
	 * @param lineNumber
	 * @return
	 */
	public String getTemplateName(int lineNumber) {
		Set<Integer> keySet = getTemplates().keySet();
		List<Integer> keys = Arrays.asList(keySet.toArray(new Integer[keySet.size()]));
		Collections.sort(keys);
		
		int templateLine = keys.get(0);
		for (Integer i : keys) {
			if (i <= lineNumber) {
				templateLine = i;
				continue;
			}
			if (i > lineNumber) break;
		}
		
		// Reasonable assumption: only one template definition per line  
		return getTemplates().get(templateLine).get(0).getTemplateName();
	}

	public Hashtable<Integer, ArrayList<AcceleoModule>> getModules() {
		return modules;
	}

	public Hashtable<Integer, ArrayList<AcceleoLoop>> getLoops() {
		return loops;
	}

	public Hashtable<Integer, ArrayList<AcceleoConditional>> getConditionals() {
		return conditionals;
	}

	public Hashtable<Integer, ArrayList<AcceleoNonOCLExpresion>> getSimpleExpressions() {
		return simpleExpressions;
	}
	
	/**
	 * Gets all the AcceleoConditionals, AcceleoLoops, AcceleoNonOCLExpressions,
	 * in a list for a given line number as the generic AcceleoExpression.
	 * 
	 * @param lineNumber
	 * @return
	 */
	public List<AcceleoExpression> getAllExpressions(int lineNumber) {
		List<AcceleoExpression> allExpressions = new ArrayList<AcceleoExpression>();
		
		List<AcceleoLoop> loops = getLoops().get(lineNumber);
		List<AcceleoConditional> conds = getConditionals().get(lineNumber);
		List<AcceleoNonOCLExpresion> simpleExpressions = getSimpleExpressions().get(lineNumber);
		if (loops != null) allExpressions.addAll(loops);
		if (conds != null) allExpressions.addAll(conds);
		if (simpleExpressions != null) allExpressions.addAll(simpleExpressions);
		
		return allExpressions;
	}

	public AcceleoRecursionBridge getFileData() {
		return fileData;
	}

	public void setFileData(AcceleoRecursionBridge fileData) {
		this.fileData = fileData;
	}
	
	public String getTransformationFile() {
		return this.templatePath;
	}
	
	public void setTransformationFile(String transformationFile) {
		templatePath = transformationFile;	
	}	
	
	public void setInMetamodelPath(String ecoreFile) {
		this.inMetamodelPath = ecoreFile;	
	}	
	
	
	public String getInMetamodelPath() {
		return inMetamodelPath;	
	}	
	
	public String getModelName() {
		return modelName;
	}
	
	// Getters & Setters for Model Context for testing purposes
	public ArrayList<String> getModelContexts() {
		return modelContexts;
	}

	public void setModelContexts(ArrayList<String> modelContexts) {
		this.modelContexts = modelContexts;
	}

	public ArrayList<String> getModelInstanceContexts() {
		return modelInstanceContexts;
	}

	public void setModelInstanceContexts(ArrayList<String> modelInstanceContexts) {
		this.modelInstanceContexts = modelInstanceContexts;
	}

	//*****************************************************************************
	// Main
	//*****************************************************************************
	public static void main(String... args){
		AcceleoTransformationParser atp = new AcceleoTransformationParser();
		try {
			atp.initialize(DEFAULT_METAMODEL, DEFAULT_TEMPLATE);
		} catch (TransformationParserException e) {
			e.printStackTrace();
			return;
		}
		atp.parse();
		
		//printFileWithLines();
		atp.printSimpleExpressions();
		atp.printLoopExpressions();
	}
}
