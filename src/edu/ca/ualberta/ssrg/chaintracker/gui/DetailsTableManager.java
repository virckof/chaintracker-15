package edu.ca.ualberta.ssrg.chaintracker.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import edu.ca.ualberta.ssrg.chaintracker.vis.Attribute;
import edu.ca.ualberta.ssrg.chaintracker.vis.Model;
import edu.ca.ualberta.ssrg.chaintracker.vis.Trace;
import edu.ca.ualberta.ssrg.chaintracker.vis.Element;
import edu.ca.ualberta.ssrg.chaintracker.vis.Trace.TraceType;

public class DetailsTableManager {
	
	//Column Minimum Widths
	
	private Vector<String> columnNames;
	
	private Vector<Vector> rowData;
	
	private int MIN_NUM_ROWS = 5;
	
	public DefaultTableModel initialize(MainVis main, String col1, String col2){
		
		columnNames = new Vector<String>();
		
	    columnNames.addElement(col1);
	    columnNames.addElement(col2);
	    
	    Vector<String> rowOne = new Vector<String>();
	    Vector<String> rowTwo = new Vector<String>();
	    Vector<String> rowThree = new Vector<String>();
	    Vector<String> rowFour = new Vector<String>();
	    Vector<String> rowFive = new Vector<String>();

	    rowData = new Vector<Vector>();
	    rowData.addElement(rowOne);
	    rowData.addElement(rowTwo);
	    rowData.addElement(rowThree);
	    rowData.addElement(rowFour);
	    rowData.addElement(rowFive);
	    
	    main.setTableType(main.ELETABLE);
	    
	    return new DefaultTableModel(rowData, columnNames);
	   
	}
	
	/**
	 * Update table to display provided model information
	 */
	public void updateModelTable(MainVis main, Model model){
		String none = ("<html><b><FONT COLOR=\"800080\">None</FONT></html>");
		
		DefaultTableModel table1 = main.getDetailsTable1();
		DefaultTableModel table2 = main.getDetailsTable2();
		DefaultTableModel table3 = main.getDetailsTable3();
		
		ArrayList<DefaultTableModel> tables = new ArrayList<DefaultTableModel>();
		tables.add(table1);
		tables.add(table2);
		tables.add(table3);
		
		//Remove all rows currently in the tables
		for (DefaultTableModel table : tables){
			flushTables(main, table);
		}
		
		//Obtain required data for model
		//Elements and their attribute count
		ArrayList<String> eleListName = new ArrayList<String>();
		ArrayList<Integer> numAttributes = new ArrayList<Integer>();
		String eleStr;
		Integer attCount;
		for (Element ele : model.getEleList()){
			eleStr = ele.getName();
			eleListName.add(eleStr);
			attCount = ele.getAttList().size();
			numAttributes.add(attCount);
		}
		
		//Inbound Bindings
		ArrayList<String> inboundListName = new ArrayList<String>();
		ArrayList<String> inboundListSource = new ArrayList<String>();
		ArrayList<String> inboundListTarget = new ArrayList<String>();
		String inStr;
		for (Element ele : model.getEleList()){
			if (main.getGraphPanel().getInboundTraces(ele)!=null){
				for (Trace t : main.getGraphPanel().getInboundTraces(ele)){
					//Obtain trace Colour
					String colour;
					if (t.getType().equals(TraceType.Implicit)){
						colour = "FF0000";
					} else {
						colour = "008000";
					}
					
					inStr = t.getName();
					inboundListName.add(inStr);
					inStr = ("<html><b><FONT COLOR=\"" + colour + "\">" + t.getOrigin().getParentModel().getName() + "." + t.getOrigin().getParentEle().getName() + "." + t.getOrigin().getName() + "</FONT></html>");
					inboundListSource.add(inStr);
					inStr = ("<html><b><FONT COLOR=\"0000FF\">" + t.getDestination().getParentModel().getName() + "." + t.getDestination().getParentEle().getName() + "." + t.getDestination().getName() + "</FONT></html>");
					inboundListTarget.add(inStr);
				}
			} else {
				inboundListName.add(none);
				inboundListSource.add(none);
				inboundListTarget.add(none);
			}
		}
		
		//OutBound Bindings
		ArrayList<String> outboundListName = new ArrayList<String>();
		ArrayList<String> outboundListSource = new ArrayList<String>();
		ArrayList<String> outboundListTarget = new ArrayList<String>();
		String outStr;
		for (Element ele : model.getEleList()){
			if (main.getGraphPanel().getOutboundTraces(ele)!=null){
				for (Trace t : main.getGraphPanel().getOutboundTraces(ele)){
					//Obtain trace Colour
					String colour;
					if (t.getType().equals(TraceType.Implicit)){
						colour = "FF0000";
					} else {
						colour = "008000";
					}
					
					outStr = t.getName();
					outboundListName.add(outStr);
					outStr = ("<html><b><FONT COLOR=\"" + colour + "\">" + t.getOrigin().getParentModel().getName() + "." + t.getOrigin().getParentEle().getName() + "." + t.getOrigin().getName() + "</FONT></html>");
					outboundListSource.add(outStr);
					outStr = ("<html><b><FONT COLOR=\"0000FF\">" + t.getDestination().getParentModel().getName() + "." + t.getDestination().getParentEle().getName() + "." + t.getDestination().getName() + "</FONT></html>");
					outboundListTarget.add(outStr);
				}
			} else {
				outboundListName.add(none);
				outboundListSource.add(none);
				outboundListTarget.add(none);
			}
		}
		
		//Find out how many rows to add
		int numRowsT1, numRowsT2, numRowsT3;
		numRowsT1 = (eleListName.size() > MIN_NUM_ROWS) ? eleListName.size() : MIN_NUM_ROWS;
		numRowsT2 = (inboundListName.size() > MIN_NUM_ROWS) ? inboundListName.size() : MIN_NUM_ROWS;
		numRowsT3 = (outboundListName.size() > MIN_NUM_ROWS) ? outboundListName.size() : MIN_NUM_ROWS;
		table1.setRowCount(numRowsT1);
		table2.setRowCount(numRowsT2);
		table3.setRowCount(numRowsT3);
		
		for (int i = 0; i < table1.getRowCount(); i++){
			//Add attribute if available
			if (eleListName.size() > i){
				table1.setValueAt(eleListName.get(i), i, 0);
				table1.setValueAt(numAttributes.get(i), i, 1);
			}
		}
		
		for (int i = 0; i < table2.getRowCount(); i++){
			//Add inbound map if available
			if (inboundListName.size() > i){
				table2.setValueAt(inboundListName.get(i), i, 0);
				table2.setValueAt(inboundListSource.get(i), i, 1);
				table2.setValueAt(inboundListTarget.get(i), i, 2);
			}
		}
			
		for (int i = 0; i < table3.getRowCount(); i++){
			//Add outbound map if available
			if (outboundListName.size() > i){
				table3.setValueAt(outboundListName.get(i), i, 0);
				table3.setValueAt(outboundListSource.get(i), i, 1);
				table3.setValueAt(outboundListTarget.get(i), i, 2);
			}
		}
		
		//Alter table
		main.setDetailsTable1(table1);
		main.setDetailsTable2(table2);
		main.setDetailsTable3(table3);
	}

	/**
	 * Update table to display provided element information
	 */
	public void updateElementTable(MainVis main, Element ele){
		
		String none = ("<html><b><FONT COLOR=\"800080\">None</FONT></html>");
		
		DefaultTableModel table1 = main.getDetailsTable1();
		DefaultTableModel table2 = main.getDetailsTable2();
		DefaultTableModel table3 = main.getDetailsTable3();
		
		ArrayList<DefaultTableModel> tables = new ArrayList<DefaultTableModel>();
		tables.add(table1);
		tables.add(table2);
		tables.add(table3);
		
		//Remove all rows currently in the tables
		for (DefaultTableModel table : tables){
			flushTables(main, table);
		}
		
		//Obtain required data for element
		//Attributes
		ArrayList<String> attListName = new ArrayList<String>();
		ArrayList<String> attListType = new ArrayList<String>();
		String attStr;
		for (Attribute att : ele.getAttList()){
			attStr = att.getName();
			attListName.add(attStr);
			attStr = att.getType();
			attListType.add(attStr);
		}
		
		//Inbound Bindings
		ArrayList<String> inboundListName = new ArrayList<String>();
		ArrayList<String> inboundListSource = new ArrayList<String>();
		ArrayList<String> inboundListTarget = new ArrayList<String>();
		String inStr;
		if (main.getGraphPanel().getInboundTraces(ele)!=null){
			for (Trace t : main.getGraphPanel().getInboundTraces(ele)){
				//Obtain trace Colour
				String colour;
				if (t.getType().equals(TraceType.Implicit)){
					colour = "FF0000";
				} else {
					colour = "008000";
				}
				
				inStr = t.getName();
				inboundListName.add(inStr);
				inStr = ("<html><b><FONT COLOR=\"" + colour + "\">" + t.getOrigin().getParentModel().getName() + "." + t.getOrigin().getParentEle().getName() + "." + t.getOrigin().getName() + "</FONT></html>");
				inboundListSource.add(inStr);
				inStr = ("<html><b><FONT COLOR=\"0000FF\">" + t.getDestination().getParentModel().getName() + "." + t.getDestination().getParentEle().getName() + "." + t.getDestination().getName() + "</FONT></html>");
				inboundListTarget.add(inStr);
			}
		} else {
			inboundListName.add(none);
			inboundListSource.add(none);
			inboundListTarget.add(none);
		}
		
		//OutBound Bindings
		ArrayList<String> outboundListName = new ArrayList<String>();
		ArrayList<String> outboundListSource = new ArrayList<String>();
		ArrayList<String> outboundListTarget = new ArrayList<String>();
		String outStr;
		if (main.getGraphPanel().getOutboundTraces(ele)!=null){
			for (Trace t : main.getGraphPanel().getOutboundTraces(ele)){
				//Obtain trace Colour
				String colour;
				if (t.getType().equals(TraceType.Implicit)){
					colour = "FF0000";
				} else {
					colour = "008000";
				}
				
				outStr = t.getName();
				outboundListName.add(outStr);
				outStr = ("<html><b><FONT COLOR=\"" + colour + "\">" + t.getOrigin().getParentModel().getName() + "." + t.getOrigin().getParentEle().getName() + "." + t.getOrigin().getName() + "</FONT></html>");
				outboundListSource.add(outStr);
				outStr = ("<html><b><FONT COLOR=\"0000FF\">" + t.getDestination().getParentModel().getName() + "." + t.getDestination().getParentEle().getName() + "." + t.getDestination().getName() + "</FONT></html>");
				outboundListTarget.add(outStr);
			}
		} else {
			outboundListName.add(none);
			outboundListSource.add(none);
			outboundListTarget.add(none);
		}
		
		//Find out how many rows to add
		int numRowsT1, numRowsT2, numRowsT3;
		numRowsT1 = (attListName.size() > MIN_NUM_ROWS) ? attListName.size() : MIN_NUM_ROWS;
		numRowsT2 = (inboundListName.size() > MIN_NUM_ROWS) ? inboundListName.size() : MIN_NUM_ROWS;
		numRowsT3 = (outboundListName.size() > MIN_NUM_ROWS) ? outboundListName.size() : MIN_NUM_ROWS;
		table1.setRowCount(numRowsT1);
		table2.setRowCount(numRowsT2);
		table3.setRowCount(numRowsT3);
		
		for (int i = 0; i < table1.getRowCount(); i++){
			//Add attribute if available
			if (attListName.size() > i){
				table1.setValueAt(attListName.get(i), i, 0);
				table1.setValueAt(attListType.get(i), i, 1);
			}
		}
		
		for (int i = 0; i < table2.getRowCount(); i++){
			//Add inbound map if available
			if (inboundListName.size() > i){
				table2.setValueAt(inboundListName.get(i), i, 0);
				table2.setValueAt(inboundListSource.get(i), i, 1);
				table2.setValueAt(inboundListTarget.get(i), i, 2);
			}
		}
			
		for (int i = 0; i < table3.getRowCount(); i++){
			//Add outbound map if available
			if (outboundListName.size() > i){
				table3.setValueAt(outboundListName.get(i), i, 0);
				table3.setValueAt(outboundListSource.get(i), i, 1);
				table3.setValueAt(outboundListTarget.get(i), i, 2);
			}
		}
		
		//Alter table
		main.setDetailsTable1(table1);
		main.setDetailsTable2(table2);
		main.setDetailsTable3(table3);
		
	}
	
	/**
	 * Update table to display provided trace information
	 */
	public void updateTraceTable(MainVis main, Trace t){
		
		DefaultTableModel table1 = main.getDetailsTable1();
		DefaultTableModel table2 = main.getDetailsTable2();
		DefaultTableModel table3 = main.getDetailsTable3();
		
		ArrayList<DefaultTableModel> tables = new ArrayList<DefaultTableModel>();
		tables.add(table1);
		tables.add(table2);
		tables.add(table3);
		
		//Remove all rows currently in the tables
		for (DefaultTableModel table : tables){
			flushTables(main, table);
		}
		
		//Obtain trace Colour
		String colour;
		if (t.getType().equals(TraceType.Implicit)){
			colour = "FF0000";
		} else {
			colour = "008000";
		}
		
		//Obtain required data for element
		//Name
		//String name = t.getName();
		String name = t.getName();
		
		//Nature Type
		String Ntype = t.getNatureType(); 
		
		//Type
		String type = ("<html><b><FONT COLOR=\"" + colour + "\">" + t.getType().toString().toLowerCase() + "</FONT></html>");
		
		//Source Attribute Name
		String srcAttName = t.getOrigin().getName();
		
		//Source Attribute Parent Element
		String srcAttParent = t.getOrigin().getParentEle().getName();
		
		//Source Attribute Type
		String srcAttType = t.getOrigin().getType();
		
		//Dest Attribute Name
		String destAttName = t.getDestination().getName();
		
		//Dest Attribute Parent Element
		String destAttParent = t.getDestination().getParentEle().getName();
				
		//Dest Attribute Type
		String destAttType = t.getDestination().getType();
				
		//Find out how many rows to add
		for (DefaultTableModel table : tables){
			table.setRowCount(MIN_NUM_ROWS);
		}
		
		//Update Data
		table1.setValueAt(name, 0, 0);
		table1.setValueAt(Ntype, 0, 1);
		table1.setValueAt(type, 0, 2);
		table2.setValueAt(srcAttParent, 0, 0);
		table2.setValueAt(srcAttName, 0, 1);
		table2.setValueAt(srcAttType, 0, 2);
		table3.setValueAt(destAttParent, 0, 0);
		table3.setValueAt(destAttName, 0, 1);
		table3.setValueAt(destAttType, 0, 2);
		
		//Alter table
		main.setDetailsTable1(table1);
		main.setDetailsTable2(table2);
		main.setDetailsTable3(table3);
	   
	}
	
	//Clear all table contents
	public void flushTables(MainVis main, DefaultTableModel table) {
		
		table.setNumRows(0);
		table.setNumRows(MIN_NUM_ROWS);
	
	}
	
	/**
	 * Adjusts the size of the table cells to accommodate all information
	 */
	 public int getLargestContent(MainVis main, JTable table, int column, int margin) {
	        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
	        TableColumn col = colModel.getColumn(column);
	        int width, twoColWidth, threeColWidth;
	        
	        twoColWidth = (int) ((Math.round(main.getTableWidth()/2)) - 1);
			threeColWidth = (int) (Math.round(main.getTableWidth()/3));

	        TableCellRenderer renderer = col.getHeaderRenderer();
	        if (renderer == null) {
	            renderer = table.getTableHeader().getDefaultRenderer();
	        }
	        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
	        width = comp.getPreferredSize().width;

	        for (int r = 0; r < table.getRowCount(); r++) {
	            renderer = table.getCellRenderer(r, column);
	            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
	            int currentWidth = comp.getPreferredSize().width;
	            width = Math.max(width, currentWidth);
	        }

	        width += 2 * margin;
	        
	        int minColWidth = (table.getColumnCount()==2) ? twoColWidth : threeColWidth;
	        
	        width = (minColWidth > width) ? minColWidth : width;
	        
	        return width;

	    }
	

}