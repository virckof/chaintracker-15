package edu.ca.ualberta.ssrg.chaintracker.atl.main;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides helper functions to navigate an XML document. 
 *
 */
public class XMLHelper {
	 final static String COMMENTS_BEFORE = "commentsBefore";
	 final static String COMMENTS_AFTER = "commentsAfter";
	 
	 final static String TYPE_ATTRIBUTE = "xsi:type";
	 
	/**
     * Get all nodes of a given tag name.
     * 
     * Ex:
     * 
     * <bindings location="14:4-14:81" propertyName="environment">
     *... </bindings>
     *
     * has a tag name of "bindings"
     * 
     * @param nodeList - list of nodes to search
     * @param tagName - node tag name to find
     * @param accumulation - list of nodes to add the found nodes to
     * @boolean recursive - whether to search recursively through nodes children
     */
    public static void getNodeByTagName(NodeList nodeList, String tagName, ArrayList<Node> accumulation, boolean recursive) {
        getNodeByMultipleTagNames(nodeList, new String[] {tagName}, accumulation, recursive);
    }
    
    /**
     * Get all nodes of given tag names.
     * 
     * @param nodeList - list of nodes to search
     * @param tagNames - list of node tag names to find
     * @param accumulation - list of nodes to add the found nodes to
     * @boolean recursive - whether to search recursively through nodes children
     */
    public static void getNodeByMultipleTagNames(NodeList nodeList, String[]tagNames, ArrayList<Node> accumulation, boolean recursive) {
    	
        for (int i = 0; i < nodeList.getLength(); i++) {
     
	    	Node tempNode = nodeList.item(i);
	     
	    	// make sure it's element node.
	    	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
	    		for(String type : tagNames){
	    			if(tempNode.getNodeName().equals(type)){
		    			accumulation.add(tempNode);
	    			}
	    		}
	    		
	    		if (tempNode.hasChildNodes() && recursive) {		     
        			// loop again if has child nodes
	    			getNodeByMultipleTagNames(tempNode.getChildNodes(), tagNames, accumulation, recursive);
         
        		}
	    	}   
        }
    }
    
    /**
     * Given a list of node, find nodes with type attribute
     * equivalent to the desired type attribute value.
     * 
     * Ex: 
     * 
     * <value xsi:type="ocl:NavigationOrAttributeCallExp" location="13:18-13:69" name="activities">
     * ...</value>
     * 
     * has a type attribute value of "ocl:NavigationOrAttributeCallExp"
     * 
     * @param nodeList - list of nodes to search
     * @param typeAttributeValue
     * @param accumulation - list of nodes to add the found nodes to
     * @param recursive- whether to look inside child nodes
     */
    public static void getNodeByTypeAttribute(ArrayList<Node> nodeList, String typeAttributeValue, ArrayList<Node> accumulation) {

    	for(int i=0; i < nodeList.size(); i++){
			Node node = nodeList.get(i);
			Element element = (Element) node;
			String elementType = element.getAttribute(TYPE_ATTRIBUTE);
			
			if(elementType.equals(typeAttributeValue)){
				accumulation.add(node);
			}
			
			if (node.hasChildNodes()) {		     
    			// loop again if has child nodes
				getNodeByTypeAttribute(node.getChildNodes(), typeAttributeValue, accumulation);
     
    		}
		}
    }
    
    public static void getNodeByTypeAttribute(NodeList nodeList, String typeAttributeValue, ArrayList<Node> accumulation) {

    	for(int i = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element element = (Element) node;
			
			String elementType = element.getAttribute(TYPE_ATTRIBUTE);
			
			if(elementType.equals(typeAttributeValue)){
				accumulation.add(node);
			}
		}
    }
    
    public static void getNodeByAttribute(NodeList nodeList, String attribute, String attributeValue, ArrayList<Node> accumulation) {

    	for(int i = 0; i < nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element element = (Element) node;
			
			String elementType = element.getAttribute(attribute);
			
			if(elementType.equals(attributeValue)){
				accumulation.add(node);
			}
		}
    }
    
    public static NodeList getElementChildNode(Node node) {
		NodeList children = node.getChildNodes();
		
		for (int i = children.getLength() - 1; i >= 0; i--) {
	    	Node childNode = children.item(i);
	    	
	    	
	    	
	    	// Remove any children nodes that are non-element or comment elements
	    	if (childNode.getNodeType() != Node.ELEMENT_NODE
	    			|| childNode.getNodeName() == COMMENTS_BEFORE
	    			|| childNode.getNodeName() == COMMENTS_AFTER) {
	    		childNode.getParentNode().removeChild(childNode);
	    	};
		}
		
		return children;
    }
}
