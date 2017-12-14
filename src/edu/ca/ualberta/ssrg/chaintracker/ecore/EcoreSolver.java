package edu.ca.ualberta.ssrg.chaintracker.ecore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.omg.CORBA.portable.IndirectionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.ca.ualberta.ssrg.chaintracker.vos.printer.ModelElement;
import edu.ca.ualberta.ssrg.chaintracker.vos.tuples.IndirectSourceConcept;

/**
 * EcoreSolver is involved in extracting information from the Ecore
 * metamodel file. Information such as:
 * - getting a list of models from the metamodel file
 * - determining indirect model acceses from a model attribute expression
 *
 */
public class EcoreSolver {

	// Error messages
	private static final String FILE_OPEN_ERROR = "Could not open Ecore file";
	private static final String FILE_READ_ERROR = "Problem reading Ecore file";

	private Document doc;

	private String modelFile;

	public EcoreSolver(){
		modelFile = "";
	}

	public void initialize(){
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			System.out.println(modelFile);
			doc = docBuilder.parse(new File(modelFile));
			doc.getDocumentElement().normalize();


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

	public  ArrayList<IndirectSourceConcept> tokenizeCallSequence(String sequence[], String classRoot, String modelName, String modelURI, ArrayList<IndirectSourceConcept> indirectBabies){

		NodeList nList = doc.getElementsByTagName("eClassifiers");
		ArrayList<Node> eAttributes = new ArrayList<>();
		ArrayList<Node> eReference= new ArrayList<>();

		if(sequence.length >0){
			getClassAttributesAndReferences(nList, classRoot, eAttributes, eReference);
		}

		String currentShackle = sequence[sequence.length-1];
		boolean found=false;

		for(Node n : eAttributes){
			Element e = (Element) n;
			//System.out.println("-->" +e.getAttribute("name"));
			//System.out.println("?"+currentShackle);
			if(e.getAttribute("name").equals(currentShackle)){


				found = true;

				IndirectSourceConcept isc = new IndirectSourceConcept();
				isc.setType(IndirectSourceConcept.MODEL_ELEMENT_ATTRIBUTE);
				isc.setElementID(classRoot);
				isc.setAttributeID(e.getAttribute("name"));
				isc.setModelName(modelName);
				isc.setModelURI(modelURI);
				indirectBabies.add(isc);

				break;
			}
		}

		if(!found){
			for(Node n : eReference){
				Element e = (Element) n;
				if(e.getAttribute("name").equals(currentShackle)){

					String referenceClass = getTypeAttribute(e, "eType");
					//System.out.println("<"+e.getAttribute("name")+">Reference to->"+referenceClass);

					IndirectSourceConcept isc = new IndirectSourceConcept();
					isc.setType(IndirectSourceConcept.MODEL_ELEMENT_REFERENCE);
					isc.setElementID(classRoot);
					isc.setRelationName(e.getAttribute("name"));
					isc.setModelName(modelName);
					isc.setModelURI(modelURI);
					indirectBabies.add(isc);

					String newSequence[] = removeLastElement(sequence);
					tokenizeCallSequence(newSequence, referenceClass, modelName, modelURI, indirectBabies);

					found = true;
					break;
				}
			}
		}

		return indirectBabies;

	}

	private Element getClassNode(NodeList nodeList, String className) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element classNode = (Element)nodeList.item(i);

			if(classNode.getAttribute("xsi:type").equals("ecore:EClass")
					&& classNode.getAttribute("name").equals(className)){
				return classNode;
			}
		}

		return null;
	}

	private void getClassAttributesAndReferences(NodeList nodeList, String classRoot, ArrayList<Node> attributesAccumulate, ArrayList<Node> referencesAccumulate) {
		Element classNode = getClassNode(nodeList, classRoot);
		if (classNode == null) {
			return;
		}

		getNodeTypeValue(classNode.getChildNodes(), "eStructuralFeatures", "xsi:type", "ecore:EAttribute", attributesAccumulate);
		getNodeTypeValue(classNode.getChildNodes(), "eStructuralFeatures", "xsi:type", "ecore:EReference", referencesAccumulate);

		String superType = getTypeAttribute(classNode, "eSuperTypes");
		if (superType != null) {
			getClassAttributesAndReferences(nodeList, superType, attributesAccumulate, referencesAccumulate);
		}
	}

	public static String[] removeLastElement(String[] input) {
		String result [] = new String [input.length-1];

		for(int i =0; i<input.length-1 ; i++){
			result[i] = input[i];
		}
		return result;
	}

	private static void getNodeType(NodeList nodeList, String type, ArrayList<Node> accumulation) {

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if(tempNode.getNodeName().equals(type)){

					accumulation.add(tempNode);
				}
				if (tempNode.hasChildNodes()) {

					// loop again if has child nodes
					getNodeType(tempNode.getChildNodes(), type, accumulation);
				}
			}
		}
	}

	private static void getNodeTypeValue(NodeList nodeList, String type, String attribute, String value, ArrayList<Node> accumulation) {

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if(tempNode.getNodeName().equals(type)){

					Element e = (Element) tempNode;
					if(e.getAttribute(attribute).equals(value)){
						accumulation.add(tempNode);
					}
				}
				if (tempNode.hasChildNodes()) {

					// loop again if has child nodes
					getNodeType(tempNode.getChildNodes(), type, accumulation);
				}
			}
		}
	}


	public ArrayList<ModelElement> getModelElements(String modelFile){

		ArrayList<ModelElement> elements = new ArrayList<>();

		//Points the parser to the model to parse.
		setModelFile(modelFile);

		// Loads the documents
		initialize();

		String metamodel = extractMetamodelName(modelFile);

		NodeList nList = doc.getElementsByTagName("eClassifiers");
		ArrayList<Node> eAttributes = new ArrayList<>();
		ArrayList<Node> eReference= new ArrayList<>();

		for (int i = 0; i < nList.getLength(); i++) {
			Element classNode = (Element)nList.item(i);

			if (!classNode.getAttribute("xsi:type").equals("ecore:EClass")) {
				continue;
			}

			String modelElementType = classNode.getAttribute("name");
			String superType = getTypeAttribute(classNode, "eSuperTypes");

			eAttributes = new ArrayList<>();
			eReference= new ArrayList<>();

			getNodeTypeValue(classNode.getChildNodes(), "eStructuralFeatures", "xsi:type", "ecore:EAttribute", eAttributes);
			getNodeTypeValue(classNode.getChildNodes(), "eStructuralFeatures", "xsi:type", "ecore:EReference", eReference);

			//Case where the Class is empty, probably inherits from some other class
			if(eAttributes.isEmpty() && eReference.isEmpty()){
				ModelElement empty = new ModelElement();
				empty.setMetamodel(metamodel);
				empty.setModelElement(modelElementType);
				empty.setSupertype(superType);
				empty.setEmpty(true);

				elements.add(empty);
			}

			//Create a model element for each of the class' attributes
			for(Node attributeNode : eAttributes){
				ModelElement elem = createModelElement((Element)attributeNode, metamodel, modelElementType, superType);
				elem.setRelationship(false);

				elements.add(elem);
			}

			//Create a model element for each of the class' references
			for(Node referenceNode : eReference){
				ModelElement elem = createModelElement((Element)referenceNode, metamodel, modelElementType, superType);
				elem.setRelationship(true);

				elements.add(elem);
			}
		}

		return elements;
	}

	/**
	 * Creates a ModelElement from metamodel class'
	 * attribute or reference attribute.
	 */
	private ModelElement createModelElement(Element classAttribute, String metamodel, String modelElementType, String superType) {
		ModelElement modelElement = new ModelElement();
		modelElement.setMetamodel(metamodel);
		modelElement.setModelElement(modelElementType);
		modelElement.setSupertype(superType);

		modelElement.setMember(classAttribute.getAttribute("name"));

		String[] type = classAttribute.getAttribute("eType").split("#//");
		if(type.length > 1){
			modelElement.setType(type[1]);
		} else {
			// Example:  <eStructuralFeatures xsi:type="ecore:EAttribute" name="startLine" ordered="false" eType="/0/Integer"/>
			// want to parse out the type "Integer"
			type = classAttribute.getAttribute("eType").split("/");
			if(type.length > 2){
				modelElement.setType(type[2]);
			}
		}

		return modelElement;
	}

	/**
	 * Solves super type for model.
	 *
	 * Example 1: model XML element:
	 *
	 * <eClassifiers xsi:type="ecore:EClass" name="DataType"
	 * 		eSuperTypes="platform:/resource/ualberta.edu.cs.ssrg.phy.phydsl/src-gen/ualberta/edu/cs/ssrg/phy/Phydsl.ecore#//AbstractElement">
	 *
	 * want to get "AbstractElement" as the super type for this model
	 *
	 * Example 2: model XML element:
	 * <eClassifiers xsi:type="ecore:EClass" name="Root" eSuperTypes="/1/Element"/>
	 *
	 * want to get "Element" as the super type for this model
	 *
	 * TODO: not handling multiple super types
	 * Example:
	 *     <eClassifiers xsi:type="ecore:EClass" name="OWLAnnotationProperty" eSuperTypes="/0/RDFProperty /1/OWLUniverse"/>
	 *
	 * currently will only parse out RDFProperty as the super type for this model
	 *
	 * @param modelElement
	 * @return
	 */
	private String getTypeAttribute(Element modelElement, String typeAttribute) {
		// Get the name after the last slash, ex "/1/Element" => "Element"
		Pattern templatePattern = Pattern.compile("/([^/]*)$");

		// In the case of multiple types, use the first type for now until we support multi-type
		String type = modelElement.getAttribute(typeAttribute).split(" ")[0];
		Matcher m = templatePattern.matcher(type);
		if (m != null && m.find()) {
			return m.group(1);
		}

		return null;
	}

	/**
	 * Get metamodel name from meta model file name.
	 *
	 * Example:
	 * "examples/ATLExperiments/OWL2XML/XML.ecore"
	 *
	 * metamodel name is "XML"
	 *
	 * @param modelFile
	 * @return
	 */
	private String extractMetamodelName(String modelFile) {
		File f = new File(modelFile);
		String name = f.getName();

		String metamodel = name.split("\\.")[0];
		return metamodel;
	}

	/**
	 * Given an Ecore file, returns a list of all the Ecore
	 * metamodel attribute names.
	 *
	 * @param fileName - name of Ecore file
	 * @return
	 * @throws EcoreSolverException
	 */
	public List<String> getAttributes(String fileName) throws EcoreSolverException {
		List<String> attributes = new ArrayList<String>();

		FileInputStream fs;
		try {
			fs = new FileInputStream(fileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw new EcoreSolverException(FILE_OPEN_ERROR);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		String line;
		try {
			line = br.readLine();

			while (line != null) {
				line = br.readLine();
				if (line == null) break;

				// HACK. Find the line with nsURI, and skip.
				// This line contains the name of the Ecore model
				// which we don't want to return
				Pattern pEcoreName = Pattern.compile("nsURI");
				Matcher m = pEcoreName.matcher(line);
				if (m.find()) continue;
				//Likewise for Eclass names
				Pattern pClassName = Pattern.compile("ecore:EClass");
				m = pClassName.matcher(line);
				if (m.find()) continue;

				// Search for all other "name" fields
				Pattern p = Pattern.compile("name=\"([^\"]+)\"");
				m = p.matcher(line);
				if (m != null && m.find()) {
					attributes.add(m.group(1));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new EcoreSolverException(FILE_READ_ERROR);
		}

		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return attributes;
	}

	/**
	 * Given a model member access expression, returns the corresponding Model Element
	 * from the list of given Model Elements.
	 *
	 * TODO/Limitation: If there is more than one Model Element which matches, just returns the first one.
	 *
	 * @param attribute
	 * @param modelElements
	 * @return
	 */
	public ModelElement getModelElementByMember(String attribute, List<ModelElement> modelElements) {
		List<String> parts = Arrays.asList(attribute.split("\\."));
		attribute = parts.get(0);

		for(ModelElement modelElement : modelElements) {
			if (modelElement != null && modelElement.getMember() != null && modelElement.getMember().equals(attribute)) {
				return modelElement;
			}
		}

		return null;
	}

	/**
	 * Given a model type, returns all corresponding Model Elements
	 * from the list of given Model Elements.
	 *
	 * @param type
	 * @param modelElements
	 * @return
	 */
	public List<ModelElement> getModelElementByModelType(String type, List<ModelElement> modelElements) {
		List<ModelElement> elements = new ArrayList<ModelElement>();

		for(ModelElement modelElement : modelElements) {
			if (modelElement.getModelElement().equals(type)) {
				elements.add(modelElement);
			}
		}

		return elements;
	}

	public String getModelFile() {
		return modelFile;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}
}
