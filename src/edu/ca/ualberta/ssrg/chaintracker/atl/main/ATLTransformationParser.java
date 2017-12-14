package edu.ca.ualberta.ssrg.chaintracker.atl.main;


import java.io.File;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.ca.ualberta.ssrg.chaintracker.vos.IndirectSource;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.SourceElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetAttribute;
import edu.ca.ualberta.ssrg.chaintracker.vos.TargetElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.M2MTransformation;

import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
 
public class ATLTransformationParser {
 
    final static String ATL_ELEMENTS_TAG = "elements";
    final static String MODULE_TAG = "atl:Module";
    final static String TYPE_TAG = "type";
    final static String BINDINGS_TAG = "bindings";
    final static String HELPER_FEATURE_TAG = "feature";
    final static String HELPER_CONTEXT_TAG = "context_";
    final static String CONTEXT_INSTANCE_NAME = "self";
    
    final static String TYPE_ATTRIBUTE = "xsi:type";
    final static String MATCHED_RULE_TYPE = "atl:MatchedRule";
    final static String MATCHED_RULE_IN_PATTERN_TYPE = "atl:SimpleInPatternElement";
    final static String MATCHED_RULE_OUT_PATTERN_TYPE = "atl:SimpleOutPatternElement";
    final static String HELPER_TYPE = "atl:Helper";
    final static String MODEL_NAVIGATION_TYPE = "ocl:NavigationOrAttributeCallExp";
    final static String OPERATION_CALL_TYPE = "ocl:OperationCallExp";
    final static String VARIABLE_TYPE = "ocl:VariableExp";
    
    final static String NAME_ATTRIBUTE = "name";
    final static String OPERATION_NAME_ATTRIBUTE = "operationName";
    final static String VARIABLE_REF_ATTRIBUTE = "referredVariable";
    
    final static String MODEL_ATTRIBUTE = "model";
    final static String VAR_NAME_ATTRIBUTE = "varName";
    
    private NodeList masterNodeList;
    
    private Document doc;
    
    private String transformationFile;
    
    private Hashtable<String, IndirectSource> indirectSources;
    
    /**
     * Initialize the master node list from the previously set
     * transformation file.
     */
	public void initialize(){
		
		try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(new File(transformationFile));
            doc.getDocumentElement().normalize();
            masterNodeList = doc.getChildNodes();
            
            indirectSources = new Hashtable<String, IndirectSource>();
            
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
 
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
 
        } catch (Throwable t) {
            t.printStackTrace();
        }
    	
    }
    
	/**
	 * Parse ATL matched rules to get M2MTransformations.
	 * 
	 * @return
	 */
    public ArrayList<M2MTransformation> processMatchedRules(){
    	
    	ArrayList<M2MTransformation> matchedRules = new ArrayList<>();
    	M2MTransformation matchedTemp = new M2MTransformation();
    	
    	// Getting the main module
    	Element moduleElement = getModule();
    	
		String module = "";
		if (moduleElement != null) {
			module = moduleElement.getAttribute(NAME_ATTRIBUTE);
		}
		
		// Get all matched rule nodes
		ArrayList<Node> matchedRuleNodes =  new ArrayList<>();
		XMLHelper.getNodeByTypeAttribute(doc.getElementsByTagName(ATL_ELEMENTS_TAG), MATCHED_RULE_TYPE, matchedRuleNodes);
        
    	// Navigating through all the elements of the transformation
        for(int i = 0; i < matchedRuleNodes.size(); i++){
        	Element matchedRuleElement = (Element) matchedRuleNodes.get(i);
        			
			// -> Initialize the Transformation VO (and friends)
			matchedTemp = new M2MTransformation();
			matchedTemp.setTransformationFilename(transformationFile);
			matchedTemp.setOwnerModule(module);
 			ArrayList<TargetElement> targetElementsTemp = new ArrayList<>(); 
 			SourceElement sourceElementTemp = new SourceElement();
        			
			// Name of the matched rule
			//System.out.println(matchedRuleElement.getAttribute(NAME_ATTRIBUTE));
			matchedTemp.setName(matchedRuleElement.getAttribute(NAME_ATTRIBUTE));
        			
			NodeList matchedRuleNodeChildren = matchedRuleElement.getChildNodes();
        			
			// -----> Processing the source elements (we know is only one)
			sourceElementTemp = getMatchedRuleSourceElement(matchedRuleNodeChildren);
			matchedTemp.setSourceElement(sourceElementTemp);
			
			// -----> Processing the target elements and matching sources
			targetElementsTemp = getMatchedRuleTargetElements(matchedRuleNodeChildren);
			matchedTemp.setTargetElements(targetElementsTemp);
			
			matchedRules.add(matchedTemp);
        }
        
        return matchedRules;
    }
    
    /**
     * Get ATL module
     * 
     * @return 
     */
    private Element getModule() {
    	// Getting the main module 
    	NodeList modules = doc.getElementsByTagName(MODULE_TAG);
		if (modules.getLength() == 0) return null;
		
		// Assuming one module per file
		Element moduleElement = (Element) modules.item(0);
		return moduleElement;
    }

    /**
     * Get SourceElement for a matched rule.
     * 
     * @param matchedRuleNodeChildren
     * @return
     */
    private SourceElement getMatchedRuleSourceElement(NodeList matchedRuleNodeChildren) {
    	// -----> Processing the source elements (we know is only one)
    	ArrayList<Node> elements = new ArrayList<>();
		XMLHelper.getNodeByTagName(matchedRuleNodeChildren, ATL_ELEMENTS_TAG, elements, true);
		ArrayList<Node> inPatternElements = new ArrayList<>();
		XMLHelper.getNodeByTypeAttribute(elements, MATCHED_RULE_IN_PATTERN_TYPE, inPatternElements);
		if (inPatternElements.size() == 0) return null;
		
		Element inPatternElement = (Element) inPatternElements.get(0);
		
		//Name of the source element variable 
		//System.out.println("Source Model Variable:" + inPatternElement.getAttribute("varName"));
		
		NodeList inPatternNodeChildren = inPatternElement.getChildNodes();
		// Just one level down to check the type of the source model element
		// We know it is only one level down!
		ArrayList<Node> sourceModelElementType = new ArrayList<>();
		XMLHelper.getNodeByTagName(inPatternNodeChildren, TYPE_TAG, sourceModelElementType, true);
		Element sourceModelNode = (Element) sourceModelElementType.get(0);
		//System.out.println("Source Model Element:" + sourceModelNode.getAttribute("name"));
		
		// -> Initialize the Source Element VO
		SourceElement sourceElement = new SourceElement();
		sourceElement.setSourceModelVariable(inPatternElement.getAttribute(VAR_NAME_ATTRIBUTE));
		sourceElement.setSourceElementType(sourceModelNode.getAttribute(NAME_ATTRIBUTE));
		sourceElement.setSourceModel(solveReferenceOCLType(sourceModelNode.getAttribute(MODEL_ATTRIBUTE), NAME_ATTRIBUTE));
	
		return sourceElement;
    }
    
    /**
     * Get TargetElements for a matched rule.
     * 
     * @param matchedRuleNodeChildren
     * @return
     */
    private ArrayList<TargetElement> getMatchedRuleTargetElements(NodeList matchedRuleNodeChildren) {
    	// -----> Processing the target elements and matching sources
		ArrayList<TargetElement> targetElementsTemp = new ArrayList<>(); 
		
		ArrayList<Node> elements = new ArrayList<>();
		XMLHelper.getNodeByTagName(matchedRuleNodeChildren, ATL_ELEMENTS_TAG, elements, true);
		ArrayList<Node> outPatternElements = new ArrayList<>();
		XMLHelper.getNodeByTypeAttribute(elements, MATCHED_RULE_OUT_PATTERN_TYPE, outPatternElements);
		if (outPatternElements.size() == 0) return targetElementsTemp;
	
		// Navigating the output elements
		for(int i = 0; i < outPatternElements.size(); i++){
			Element outputElement = (Element) outPatternElements.get(i);
    			
			//Name of the target element variable 
			//System.out.println("Target Model Variable:" + outputElement.getAttribute("varName"));
			
			NodeList outputElementChildren = outputElement.getChildNodes();
			
			// Just one level down to check the type of the target model element
			// We know it is only one level down!
			ArrayList<Node> modelElementType = new ArrayList<>();
			XMLHelper.getNodeByTagName(outputElementChildren, TYPE_TAG, modelElementType, true);
			Element targetModelNode = (Element) modelElementType.get(0);
			//System.out.println("Target Model Element:" + targetModelNode.getAttribute("name"));
			
			// -> Initialize the Target Element VO
			TargetElement targetElementTemp = new TargetElement();
			targetElementTemp.setTargetModellVariable(outputElement.getAttribute(VAR_NAME_ATTRIBUTE));
			targetElementTemp.setTargetElementType(targetModelNode.getAttribute(NAME_ATTRIBUTE));
			targetElementTemp.setTargetModel(solveReferenceOCLType(targetModelNode.getAttribute(MODEL_ATTRIBUTE), NAME_ATTRIBUTE));		
			ArrayList<TargetAttribute> targetAttributesTemp = new ArrayList<>();

			// Get the source attribute bindings of that specific target element
			ArrayList<Node> matchedRuleSourceAtt = new ArrayList<>();
			XMLHelper.getNodeByTagName(outputElementChildren, BINDINGS_TAG, matchedRuleSourceAtt, true);
			
			// Navigating the target attributes to extract the source attributes
			for(int j = 0; j < matchedRuleSourceAtt.size(); j++){
				
				Element sourceAttElement = (Element) matchedRuleSourceAtt.get(j);
				//System.out.println("++++++++++"+sourceAttElement.getAttribute("propertyName"));
				
				// -> Initialize the Target Attribute VO 
				TargetAttribute targetAttTemp = new TargetAttribute();
				targetAttTemp.setName(sourceAttElement.getAttribute("propertyName"));
				targetAttTemp.setOwner(targetElementTemp);
	
				NodeList attributeChildNodes = sourceAttElement.getChildNodes();
				ArrayList<Node> sourceAttributesByValue = new ArrayList<>();
				XMLHelper.getNodeByTagName(attributeChildNodes, "value", sourceAttributesByValue, true);
				// Will be one value node
				Element valueElement = (Element)sourceAttributesByValue.get(0);
				
				// Get bindings
				ArrayList<SourceAttribute> bindings = new ArrayList<SourceAttribute>();
				getBindings(valueElement, bindings, null);
				
				// Get function bindings
				ArrayList<IndirectSource> functionBindings = getFunctionBindings(valueElement);
				
				targetAttTemp.setBindings(bindings);
				targetAttTemp.setFunctionBindings(functionBindings);
    			targetAttTemp.setOwner(targetElementTemp);
    			targetAttributesTemp.add(targetAttTemp);
			}
			
			targetElementTemp.setAttributes(targetAttributesTemp);
			targetElementsTemp.add(targetElementTemp);	
		}
		
		return targetElementsTemp;
    }
    
    /**
     * Parse bindings (SourceAttribute) for a node.
     * 
     * @param element
     * @param compositeName
     * @param bindings
     * @param contextElement
     */
    private void getBindings(Element element, String compositeName, ArrayList<SourceAttribute> bindings, SourceElement contextElement) {
    	SourceElement sourceElement = null;

    	if(element.getAttribute(TYPE_ATTRIBUTE).equals(MODEL_NAVIGATION_TYPE)){
    		compositeName = compositeName + "/" + element.getAttribute(NAME_ATTRIBUTE);
		}  else if (element.getAttribute(TYPE_ATTRIBUTE).equals(VARIABLE_TYPE)) {
			sourceElement = solveVariableReference(element.getAttribute(VARIABLE_REF_ATTRIBUTE), contextElement);
		} else if (element.getAttribute(TYPE_ATTRIBUTE).equals("ocl:OclModelElement")){
			sourceElement = getSourceElementFromNode((Element)element.getParentNode(), null);
		}
    	
    	// in the event the source element was resolved from an iterator statement,
		// there may be additional binding determined from solving the variable reference
		if (sourceElement != null 
			&& sourceElement.getAttributes() != null && sourceElement.getAttributes().size() != 0) {
			compositeName = compositeName + sourceElement.getAttributes().get(0).getImplicitBindings();
		}
    	
    	NodeList children = XMLHelper.getElementChildNode(element);
    	if (children.getLength() == 0) {
    		
    		if (!compositeName.isEmpty()) {
    			SourceAttribute sourceAttTemp = new SourceAttribute();
    			sourceAttTemp.setImplicitBindings(compositeName);
    			
    			if (sourceElement != null) {
    				sourceAttTemp.setOwner(sourceElement); 
    				
    				addWithoutDuplicates(bindings, sourceAttTemp);
    			} else {
    				// do not add SourceAttribute which doesn't have
	    			// a SourceElement. While the SourceAttribute may be valid,
	    			// if we are unable to parse its SourceElement, we cannot
	    			// meaningfully use the SourceAttribute
    					System.out.println("Cannot parse SourceElement for binding " + compositeName);
    				}
    		}
    				
    		return;
    	}
    	
    	Element childElement = (Element) children.item(0);
    	getBindings(childElement, compositeName, bindings, contextElement);
    	
    	// clear out composite name for additional bindings
    	for (int i = 1; i < children.getLength(); i++) {
    		childElement = (Element) children.item(i);
    		getBindings(childElement, bindings, contextElement);
    	}
    }
    
    private void getBindings(Element element, ArrayList<SourceAttribute> bindings, SourceElement contextElement) {
    	getBindings(element, "", bindings, contextElement);
    }
    
    /**
     * Given an XML element, parses out a source attribute from it. 
     * 
     * @param element
     * @param compositeName
     * @param contextElement
     * @return
     */
    private SourceAttribute getSourceAttribute(Element element, String compositeName, SourceElement contextElement) {
    	SourceElement sourceElement = null;

    	if(element.getAttribute(TYPE_ATTRIBUTE).equals("ocl:NavigationOrAttributeCallExp")){
    		compositeName = compositeName + "/" + element.getAttribute(NAME_ATTRIBUTE);
    		
		}  else if (element.getAttribute(TYPE_ATTRIBUTE).equals(VARIABLE_TYPE)) {
			sourceElement = solveVariableReference(element.getAttribute("referredVariable"), contextElement);
			
		} else if (element.getAttribute(TYPE_ATTRIBUTE).equals("ocl:OclModelElement")){
			sourceElement = getSourceElementFromNode((Element)element.getParentNode(), null);
		}
    	
    	// in the event the source element was resolved from an iterator statement,
		// there may be additional binding determined from solving the variable reference
		if (sourceElement != null 
			&& sourceElement.getAttributes() != null && sourceElement.getAttributes().size() != 0) {
			compositeName = compositeName + sourceElement.getAttributes().get(0).getImplicitBindings();
		}
    	
    	NodeList children = XMLHelper.getElementChildNode(element);
    	if (children.getLength() == 0) {
    			SourceAttribute sourceAtt = new SourceAttribute();
    			sourceAtt.setImplicitBindings(compositeName);
    			
    			if (sourceElement != null) {
    				sourceAtt.setOwner(sourceElement); 
    				return sourceAtt;
    			} else {
    				// do not add SourceAttribute which doesn't have
	    			// a SourceElement. While the SourceAttribute may be valid,
	    			// if we are unable to parse its SourceElement, we cannot
	    			// meaningfully use the SourceAttribute
    				if (!compositeName.isEmpty()) {
    					System.out.println("Cannot parse SourceElement for binding " + compositeName);
    				}
    				
	    			return null;
    			}
    	}
    	
    	Element childElement = (Element) children.item(0);
    	return getSourceAttribute(childElement, compositeName, contextElement);
    }
    
    /**
     * Parse function bindings (IndirectSource) for a node.
     * 
     * @param elementNode
     * @return
     */
    private ArrayList<IndirectSource> getFunctionBindings(Element elementNode) {
    	ArrayList<IndirectSource> functionBindings = new ArrayList<IndirectSource>();
    	
    	IndirectSource helper = null;
    	// if element is an operation call, try and get the helper for the call
    	if(elementNode.getAttribute(TYPE_ATTRIBUTE).equals(OPERATION_CALL_TYPE)){
    		String helperName = elementNode.getAttribute(OPERATION_NAME_ATTRIBUTE);
    		helper = getHelperByName(helperName);
		}
		
    	// if operation call was a helper call, add it to function bindings
		if (helper != null) {
			addWithoutDuplicates(functionBindings, helper);
		}
		
		// recursively check for function bindings for element's children
		NodeList children = XMLHelper.getElementChildNode(elementNode);
		for (int l = 0; l < children.getLength();l++) {
			functionBindings.addAll(getFunctionBindings((Element)(children.item(l))));
		}
		
		return functionBindings;
    }

    /**
     * Parses a helper in the transformation file with the given name
     * and returns an IndirectSource for it
     * 
     * @param name
     * @return
     */
    private IndirectSource getHelperByName(String name) {
    	if (indirectSources.containsKey(name)) {
    		// if we've already parsed this helper, just return it
    		return indirectSources.get(name);
    	}
    	
    	// get the helper by its feature node
    	NodeList nodeList = doc.getElementsByTagName(HELPER_FEATURE_TAG);
    	ArrayList<Node> helperNodes = new ArrayList<Node>();
    	XMLHelper.getNodeByAttribute(nodeList, NAME_ATTRIBUTE, name, helperNodes);
    	if (helperNodes.size() == 0) {
    		return null;
    	}
    	//Should only be one match
    	Element helper = (Element)helperNodes.get(0);
    	
    	// Initialize the IndirectSource for the helper
    	IndirectSource indirectSource = new IndirectSource();
    	indirectSource.setType(IndirectSource.Type.Helper);
    	indirectSource.setHelperName(name);
    	indirectSources.put(name, indirectSource);
    	
    	// Set the context for the helper
    	SourceElement context = getHelperContext(helper);
		if (context != null) {
			indirectSource.setContext(context);
		}
		
		// Set the parameters for the helper
		indirectSource.setParameters(getHelperParameters(helper));
		
    	// Get bindings
    	Element bodyElement = (Element)helper.getElementsByTagName("body").item(0);
		ArrayList<SourceAttribute> dependencies = new ArrayList<SourceAttribute>();
		getBindings(bodyElement, "", dependencies, context);
		indirectSource.setDependencies(dependencies);
    	
		// Get function bindings
		ArrayList<IndirectSource> functionDependencies = getFunctionBindings(bodyElement);
		for(int i = functionDependencies.size() - 1; i >= 0; i--) {
			if (functionDependencies.get(i).getHelperName() == indirectSource.getHelperName()) {
				functionDependencies.remove(i);
			}
		}
		indirectSource.setFunctionDependencies(functionDependencies);
		
    	return indirectSource;
    }
    
    private void addWithoutDuplicates(ArrayList<SourceAttribute> sourceAttributes, SourceAttribute sourceAttribute) {
    	for(int i = sourceAttributes.size() - 1; i >= 0; i--) {
			if (sourceAttributes.get(i).getImplicitBindings().equals(sourceAttribute.getImplicitBindings())
					&& sourceAttributes.get(i).getOwner().equals(sourceAttribute.getOwner())) {
				return;
			}
		}
    	
    	sourceAttributes.add(sourceAttribute);
    }
    
    private void addWithoutDuplicates(ArrayList<IndirectSource> indirectSources, IndirectSource indirectSource) {
    	for(int i = indirectSources.size() - 1; i >= 0; i--) {
			if (indirectSources.get(i).getHelperName() == indirectSource.getHelperName()) {
				return;
			}
		}
    	
    	indirectSources.add(indirectSource);
    }
    
    /**
     * Parse the context from ATL Helper feature node. 
     * 
     * ex:
     
     <definition location="66:8-70:21">
        <feature xsi:type="ocl:Operation" location="66:38-70:21" name="getCollisionAID">
          ...
        </feature>
        <context_ location="66:8-66:32">
          <context_ xsi:type="ocl:OclModelElement" location="66:16-66:32" name="ScoreRule" model="/21"/>
        </context_>
      </definition>
     
     * @param helperFeatureElement
     * @return
     */
    private SourceElement getHelperContext(Element helperFeatureElement) {
    	// get context element from helper element
    	NodeList helperNodes = XMLHelper.getElementChildNode(helperFeatureElement.getParentNode());
    	if (helperNodes.getLength() <= 1) return null;
    	Element helperContextElement = (Element)helperNodes.item(1);
    	
     	// parse out the SourceElement using
    	// method getSourceElementFromNode()
    	SourceElement sourceElement = getSourceElementFromNode(helperContextElement, null);
    	if (sourceElement != null) {
    		// set variable name as 'self'
    		sourceElement.setSourceModelVariable(CONTEXT_INSTANCE_NAME);
    	}
    	
    	return sourceElement;
    }
    
    /**
     * Parse the parameters from ATL Helper feature node. 
     * 
     * ex: 
     * 
     	<feature xsi:type="ocl:Operation" location="79:13-79:107" name="getCellID">
        <parameters location="79:23-79:40" varName="cell" variableExp="/0/@elements.5/@definition/@feature/@body/@source/@source/@source/@source /0/@elements.5/@definition/@feature/@body/@arguments.0/@source/@source">
            <type xsi:type="ocl:OclModelElement" location="79:29-79:40" name="Cell" model="/19"/>
          </parameters>
          ...
        </feature>
     * 
     * @param helperFeatureElement
     * @return
     */
    private ArrayList<SourceElement> getHelperParameters(Element helperFeatureElement) {
    	// Get all parameter nodes from helper element
    	ArrayList<SourceElement> parameterSourceElements = new ArrayList<SourceElement>();
    	NodeList featureChildren = XMLHelper.getElementChildNode(helperFeatureElement);
    	ArrayList<Node> parameterNodes = new ArrayList<Node>();
    	XMLHelper.getNodeByTagName(featureChildren, "parameters", parameterNodes, false);
    	
    	// For each parameter node, parse out the SourceElement using
    	// method getSourceElementFromNode()
    	for (int i = 0; i < parameterNodes.size(); i++) {
    		Element parameterNode = (Element) parameterNodes.get(i);
    		
    		SourceElement sourceElement = getSourceElementFromNode(parameterNode, null);
        	if (sourceElement != null) {
        		parameterSourceElements.add(sourceElement);
        	}
    	}
    	
    	return parameterSourceElements;
    }
    
    /**
     * Solves a reference to a node of OCL type (OclModel or VariableDeclaration). 
     * 
     * ex: in
     * 
     	<arguments xsi:type="ocl:OclModelElement" location="41:28-41:40" name="Actor" model="/10"/>
     * 
     * the /10 refers to this element, which we return:
     * 
     	<ocl:OclModel location="41:28-41:34" name="Phydsl" elements="/0/@elements.2/@inPattern/@filter/@arguments.0"/>
     *	
     * @param ref
     * @return 
     */
    private Element solveReferenceOCLType(String ref){
    	
    	//The reference comes in the format /<id> so we get rid of the format and get the proper index
    	String indexA[] = ref.split("/");
    	if (indexA.length < 2) {
    		return null;
    	}
    	
    	int index = Integer.parseInt(indexA[1]);
    	
    	//A list of OCL models and variables is at the end of .atl.xmi file. The index in
    	//the reference refers to the reference's location in this list.
    	String queryTypes [] = new String [2];
    	queryTypes[0] = "ocl:OclModel";
    	queryTypes[1] = "ocl:VariableDeclaration";
    	
    	ArrayList<Node> oclTypes = new ArrayList<>();
    	XMLHelper.getNodeByMultipleTagNames(masterNodeList, queryTypes, oclTypes, true);
    	
    	Element oclElem = (Element) oclTypes.get(index-1); // -1 given the model array starts in 0 but the XMI refs in "/1"
    	return oclElem;
    }
    
    /**
     * Shortcut for calling method solveReferenceOCLType and getting the
     * value of an attribute on it
     * 
     * @param ref
     * @param attributeQuery
     * @return
     */
    private String solveReferenceOCLType(String ref, String attributeQuery){
    	Element refElement = solveReferenceOCLType(ref);
    	return (refElement != null) ? refElement.getAttribute(attributeQuery) : null;
    }
    
    /**
     * Solves a variable reference for a SourceElement 
     * 
     * ex: 
     * 
     	<source xsi:type="ocl:VariableExp" location="33:11-33:12" referredVariable="/0/@elements.1/@inPattern/@elements.0"/>
     * 
     * The value of the referredVariable provides a path we can navigate to get to the element that
     * we can call method getSourceElementFromNode() on to get the SourceElement from.
     * 
     * In the above example, we need to navigate to the 2nd node of tag name 'elements' (since the counting begins at 0),
     * then to its child node of tag name 'inPattern', and then to the 'inPattern' node's 1st child node
     * of tag name 'elements'.
     * 
		<source xsi:type="ocl:VariableExp" location="48:10-48:20" referredVariable="/12"/>
	 *	
     * In this example, the referredVariable refers to a model directly, so we can just solve it using
     * method solveReferenceOCLType()
     * 
     * @param ref - referredVariable attribute value
     * @param contextElement - optional context SourceElement in the event the reference is to a helper context
     * @return
     */
    public SourceElement solveVariableReference(String ref, SourceElement contextElement) {
    	if (ref.matches("/(\\d*)")) {
    		Element modelRefElement = solveReferenceOCLType(ref);
    		if (modelRefElement.getAttribute(VAR_NAME_ATTRIBUTE).equals(CONTEXT_INSTANCE_NAME)) {
    			// model is a reference to "self" so return the provided context SourceElement
    			return contextElement;
    		} else {
    			// since we do can't figure out the SourceElement type, 
    			// SourceElement will be invalid and so we just return null
        		return null;
    		}
    	}
    	
    	// Strip starting '/0' which refers to the module tag
    	ref = ref.replaceAll("^/0/@", "");
    	ArrayList<String> referenceNav = new ArrayList<String>(Arrays.asList(ref.split("/@")));
    	
    	ArrayList<Node> nodeList = new ArrayList<Node>();
    	Element node = null; 
    	for (int i = 0; i < referenceNav.size(); i ++) {
    		if (node == null) {
    			// initialize navigation at the module tag
    			node = getModule();
    		}
    		
    		// ex. elements.1 => elements is tag name, 1 is index 
    		// ex. inPattern => inPattern is tag name, no index so default index 0
    		String[] referenceNavParts = referenceNav.get(i).split("\\.");
    		if (referenceNavParts.length == 0) continue;
    		String tagName = referenceNavParts[0];
    		int index = 0;
    		if (referenceNavParts.length > 1) {
    			index = Integer.parseInt(referenceNavParts[1]);
    		}
    		
    		// find all direct children of current node who match the above tag name
			nodeList = new ArrayList<Node>();
			XMLHelper.getNodeByTagName(XMLHelper.getElementChildNode(node), tagName, nodeList, false);
    		
			// get the next node in the reference navigation
    		if (nodeList.size() > index) { 
    			node = (Element)nodeList.get(index);
    		} 
    	}

    	// iterator node => resolve using another function 
    	if (node.getTagName().equals("iterators"))
    		return getSourceElementFromIteratorNode(node, contextElement);
    	
    	// last node we navigated to is the one describing the SourceElement,
    	// so call method getSourceElementFromNode to get the SourceElement
    	return getSourceElementFromNode(node, contextElement);
    }
    
    /**
     * Parse SourceElement from the provided XML element node.
     * 
     * ex: of what sourceRefElement could look like:
     *    
     	<elements xsi:type="atl:SimpleInPatternElement" location="38:3-38:24" varName="sr" variableExp="/0/@elements.2/@inPattern/@filter/@source/@source/@source /0/@elements.2/@outPattern/@elements.0/@bindings.0/@value/@source /0/@elements.2/@outPattern/@elements.0/@bindings.1/@value/@source /0/@elements.2/@outPattern/@elements.0/@bindings.2/@value/@source/@source">
        	<type xsi:type="ocl:OclModelElement" location="38:8-38:24" name="ScoreRule" model="/12"/>
        </elements>
          
     * 
     * We need to parse out the instance name (sr), the type (ScoreRule) and model (/12 which
     * we use method solveReferenceOCLType() to get 'PhyDsl')
     * 
     * @param sourceRefElement
     * @param contextElement - optional context SourceElement in the event the element node refers to helper context
     * @return
     */
    private SourceElement getSourceElementFromNode(Element sourceRefElement, SourceElement contextElement) {
    	if (sourceRefElement == null) return null;
    	
    	SourceElement sourceElement = new SourceElement();
    	
    	NodeList refChildNodes = XMLHelper.getElementChildNode(sourceRefElement);
    	if (refChildNodes.getLength() == 0) return null;
    	Element typeElement = (Element)refChildNodes.item(0);
    
    	String instanceName = sourceRefElement.getAttribute(VAR_NAME_ATTRIBUTE);
    	sourceElement.setSourceModelVariable(instanceName);
    		
    	String sourceType = typeElement.getAttribute(NAME_ATTRIBUTE);
    	if (sourceType == null || sourceType.isEmpty()) {
    		// If there is no source type then the sourceRefElement
    		// is of a format we aren't handling -
    		// return null as without a type SourceElement is invalid/meaningless
    		return null;
    	}
    	sourceElement.setSourceElementType(sourceType);
    		
    	//Determine model it refers to from M2M transformation
    	Element modelRefElement = solveReferenceOCLType(typeElement.getAttribute(MODEL_ATTRIBUTE));
    	if (modelRefElement == null) {
    		return null;
    	}
    	if (modelRefElement.getAttribute(VAR_NAME_ATTRIBUTE).equals(CONTEXT_INSTANCE_NAME)) {
    		// model is a reference to "self" so return the provided context SourceElement
			return contextElement;
		} else {
			sourceElement.setSourceModel(modelRefElement.getAttribute(NAME_ATTRIBUTE));
    		return sourceElement;
		}
    }
    
    /**
     * Parse SourceElement from the provided XML iterator element node.
     * 
     * Format of an iterator call:
     * 
     *  <value xsi:type="ocl:IteratorExp" location="13:18-13:124" name="select">
              <source xsi:type="ocl:OperationCallExp" location="13:18-13:49" operationName="allInstances">
                <source xsi:type="ocl:OclModelElement" location="13:18-13:34" name="ScoreRule" model="/5"/>
              </source>
              <body xsi:type="ocl:OperationCallExp" location="13:62-13:123" operationName="oclIsKindOf">
                <source xsi:type="ocl:NavigationOrAttributeCallExp" location="13:62-13:90" name="definition">
                  <source xsi:type="ocl:NavigationOrAttributeCallExp" location="13:62-13:79" name="scoreDefinition">
                    <source xsi:type="ocl:VariableExp" location="13:62-13:63" referredVariable="/0/@elements.0/@outPattern/@elements.0/@bindings.0/@value/@iterators.0"/>
                  </source>
                </source>
                <arguments xsi:type="ocl:OclModelElement" location="13:103-13:122" name="ContactScore" model="/6"/>
              </body>
              <iterators location="13:58-13:59" varName="e" variableExp="/0/@elements.0/@outPattern/@elements.0/@bindings.0/@value/@body/@source/@source/@source"/>
            </value>
     * 
     * high level view:
     * 
     * <node>
     *  <source></source> <- what we need to parse to get the SourceELement
     *  <body></body>
     *  <iterators/> <- what we get as an argument
     * <node>
     * 
     * @param iteratorNode
     * @return
     */
    private SourceElement getSourceElementFromIteratorNode(Element iteratorNode, SourceElement contextElement) {
    	NodeList iteratorParentChildren = XMLHelper.getElementChildNode(iteratorNode.getParentNode());

    	ArrayList<Node> sourceNodes = new ArrayList<Node>();
    	XMLHelper.getNodeByTagName(iteratorParentChildren, "source", sourceNodes, false);
    	
    	if (sourceNodes.size() == 0)
    		return null;
    	
    	Element sourceNode = (Element)sourceNodes.get(0);
    	
    	SourceElement src = null;
    	
    	// solve the attribute binding from the source node
    	SourceAttribute srcAttr = getSourceAttribute(sourceNode, "", contextElement);
    	if (srcAttr != null) {
    		src = srcAttr.getOwner();
    		src.addSourceAttribute(srcAttr);
    	}
    	
    	return src;
    }
    
    /**
     * At the beginning of .atl file we add the following comments, for example:
     * 
     * -- @path Phydsl=/PhyDSLChain/metamodels/Phydsl.ecore
     * -- @path Dynamics=/PhyDSLChain/metamodels/Dynamics.ecore
     * 
     * to identify the file path for each model. This function returns the
     * file path for a given model.
     *  
     * @param modelName
     * @return
     */
    public String solveModeURI(String modelName){
    	
    	String uri="";
    	ArrayList<Node> commentsURI = new ArrayList<>();
    	XMLHelper.getNodeByTagName(masterNodeList, "commentsBefore", commentsURI, true);
    	
    	for(Node n : commentsURI){
    		Element oclElem = (Element) n;
    		String content = oclElem.getTextContent();
    		if(content.contains("@path")&&content.contains(modelName)){
    			String splitC []= content.split("=");
    			if(splitC.length>1){
    				uri = splitC[1];
    			}
    		}
    	}
    	return uri;
    }
    
    /**
     * Print a list of given M2MTransformations
     * 
     * @param transformations
     */
    public void printTransformations(ArrayList<M2MTransformation> transformations){

    	for(M2MTransformation t : transformations){
    		System.out.println("MODULE: " + t.getOwnerModule());
    		System.out.println(t.toReadableString());
    	}
    }

    public String getTransformationFile() {
		return transformationFile;
	}

	public void setTransformationFile(String transformationFile) {
		this.transformationFile = transformationFile;
	}
    
}