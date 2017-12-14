package edu.ca.ualberta.ssrg.chaintracker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * RSyntaxTextArea demo with highlighting capabilities
 * @author Victor Guana
 *
 */
public class CodeViewer extends JPanel{
	
	 private RSyntaxTextArea textArea;

	 private String filePath;
	 
	 private RTextScrollPane sp;
	 
	 public CodeViewer(MainVis main, String filePath) {
		 
		 int width = (int) main.getCodeViewWidth() - 5;
		 int height = (int) main.getCodeViewHeight() - 33;
		 
		 this.filePath = filePath;


	      textArea = new RSyntaxTextArea();
	     
	      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
	      textArea.setCodeFoldingEnabled(true);
	      textArea.setEditable(false);
	      sp = new RTextScrollPane(textArea);
	      sp.setPreferredSize(new Dimension(width,height));
	      this.add(sp);
	      this.setPreferredSize(new Dimension(width,height));
	      this.loadFile();


	  }
	 
	 
	 /**
	  * Empty code viewer 
	  */
	 public CodeViewer(MainVis main){
		  int width = (int) main.getCodeViewWidth() - 5;
		  int height = (int) main.getCodeViewHeight() - 33;
		  textArea = new RSyntaxTextArea();
	      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
	      textArea.setCodeFoldingEnabled(true);
	      textArea.setEditable(false);
	      sp = new RTextScrollPane(textArea);
	      sp.setPreferredSize(new Dimension(width,height));
	      this.add(sp);
	      this.setPreferredSize(new Dimension(width, height));
	 }
	 
	 public void setScrollPaneSize(Dimension d){
		 sp.setSize(d);
	 }
	 
	 /**
	  * Sets the size of the codeViewer when it is resized in mainVis
	 * @return 
	  */
	 public void setPreferredSize(MainVis main){
		 int width = (int) main.getCodeViewWidth() - 5;
		 int height = (int) main.getCodeViewHeight() - 33;
		 sp.setPreferredSize(new Dimension(width,height));
		 this.setPreferredSize(new Dimension(width,height));
	 }
	 
	   /**
	    * Load an input file 
	    */
	   public void loadFile(){
		   
		   FileInputStream fs;
		   try {
			   fs = new FileInputStream(filePath);
			   BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			   String line = br.readLine();
			   while (line != null) {
				   textArea.append(line+"\n");
				   line = br.readLine();
			   }
			   
			} catch (Exception e) {
				e.printStackTrace();
			}
		   
	   }
	   
	   public String getFile() {
		   
		   if(filePath == null){
			   return "empty";
		   }
		   return filePath;
	   }

	   
	   /**
	    * Highlights a line 
	    * @param lineNumber
	    * @param color
	    */
	   public void highlightLine(int lineNumber, Color color){
 
		   try {
			   int startIndex = textArea.getLineStartOffset(lineNumber-1);
	           int endIndex = textArea.getLineEndOffset(lineNumber-1);
	           
	           DefaultHighlighter h = (DefaultHighlighter) textArea.getHighlighter();
			   h.setDrawsLayeredHighlights(false);
			   
			   h.addHighlight(startIndex , endIndex  , new DefaultHighlighter.DefaultHighlightPainter(color));
			
		   } catch (BadLocationException e) {
				e.printStackTrace();
			}
		   moveFocusToLine(lineNumber);
	   }
	   
	   /**
	    * Highlights the first occurrence of a word in the text area
	    * @param search
	    * @param color
	    */
	   public void highlightSearchFirst(String search, Color color){
		   
		   int startIndex = textArea.getText().indexOf(search);
		   int endIndex = startIndex+ search.length();
		   
		   try {
	           
			   DefaultHighlighter h = (DefaultHighlighter) textArea.getHighlighter();
			   h.setDrawsLayeredHighlights(false);
			   
			   h.addHighlight(startIndex , endIndex  , new DefaultHighlighter.DefaultHighlightPainter(color));
			
		   } catch (BadLocationException e) {
				// Fails when the search value is not in the document and this is expected
		   }
		   
		   moveFocusToPosition(startIndex);
	   }
	   
	   /**
	    * Highlights all the occurrences of a word in the text area
	    * @param search
	    * @param color
	    */
	   public void highlightSearchAll(String search, Color color){
		   
		   boolean found=false;
		   int startIndex = textArea.getText().indexOf(search);
		   int endIndex = startIndex+ search.length();
		   DefaultHighlighter  h = (DefaultHighlighter )textArea.getHighlighter();
		   h.setDrawsLayeredHighlights(false);
		   
		   if(startIndex>-1){
			   found=true;
			   while(found){
				   try {
			           
					   h.addHighlight(startIndex , endIndex  , new DefaultHighlighter.DefaultHighlightPainter(color));
					   
					   startIndex = textArea.getText().indexOf(search, endIndex);
					   endIndex = startIndex+ search.length();
					   if(startIndex<0){
						   found = false;
					   }
					
				   } catch (BadLocationException e) {
						e.printStackTrace();
				   }
			   }
		   }
		   
	   }
	   
	   /**
	    * Highlights the search given a zone id, and the zone limit stings i.e. zone id = 'methodName' limits '{' and '}'
	    * @param zoneID
	    * @param zoneLimiterStart
	    * @param zoneLimitersEnd
	    * @param search
	    * @param color
	    */
	   public void highlightSearchInZoneFirst(String zoneID, String zoneLimiterStart, String zoneLimitersEnd, String search, Color color){
		   
		   int zoneIDS = textArea.getText().indexOf(zoneID);
		   int startZone = textArea.getText().indexOf(zoneLimiterStart, zoneIDS)+zoneLimiterStart.length();
		   int endZone = textArea.getText().indexOf(zoneLimitersEnd, zoneIDS);
		   String zoneText = textArea.getText().substring(startZone, endZone);

		   int start = 0; //textArea.getText().indexOf(search, startZone);
		   
		   String searchPatternStr = search + "[^a-zA-Z]"; // don't match the search if a letter follows immediately after
		   Pattern pattern = Pattern.compile(searchPatternStr);
		   Matcher matcher = pattern.matcher(zoneText);
		   if (matcher.find()){
		    	start = matcher.start() + startZone;
		    }
		    
		   int end = start + search.length();
		   
		   DefaultHighlighter h = (DefaultHighlighter) textArea.getHighlighter();
		   h.setDrawsLayeredHighlights(false);
		   
		   if(startZone>-1 && start>=startZone && end <= endZone){
			   
			   try{
				   h.addHighlight(start , end-1 , new DefaultHighlighter.DefaultHighlightPainter(color));
			   }
			   catch(Exception e){
				   //Silent
			   }
		   }
		   	   
		   moveFocusToPosition(endZone);
	   }
	   
	   /**
	    * Highlights a zone given an id suffix, and identifiers useful for methods and functions with open and key characters
	    * @param zoneID
	    * @param zoneLimiterStart
	    * @param zoneLimitersEnd
	    * @param color
	    */
	   public void highlightZone(String zoneID, String zoneLimiterStart, String zoneLimitersEnd, Color color){
		   
		   int zoneIDS = textArea.getText().indexOf(zoneID);
		   int startZone = textArea.getText().indexOf(zoneLimiterStart, zoneIDS);
		   int endZone = textArea.getText().indexOf(zoneLimitersEnd, zoneIDS);
		   
		   DefaultHighlighter h = (DefaultHighlighter) textArea.getHighlighter();
		   h.setDrawsLayeredHighlights(false);
		   try{
			   h.addHighlight(zoneIDS , endZone , new DefaultHighlighter.DefaultHighlightPainter(color));
		   }
		   catch(Exception e){
			   //Silent
		    }
	   }
	   
	   
	   public void moveFocusToPosition(int pos){
		   
		   textArea.setCaretPosition(pos);
           
	   }
	   
	   public void moveFocusToLine(int line){
		   textArea.setCaretPosition(textArea.getDocument()  
                   .getDefaultRootElement().getElement(line-1)  
                   .getStartOffset()); 
	   }
	   
	   /**
	    * Clears all the highlights in the text area
	    */
	   public void cleanHighlights(){
		   textArea.getHighlighter().removeAllHighlights();
	   }

	   
	   //Getters and setters
	   public RSyntaxTextArea getTextArea() {
			return textArea;
		}


		public void setTextArea(RSyntaxTextArea textArea) {
			this.textArea = textArea;
		}


		public RTextScrollPane getSp() {
			return sp;
		}


		public void setSp(RTextScrollPane sp) {
			this.sp = sp;
		}

	   
}
