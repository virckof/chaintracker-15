package edu.ca.ualberta.ssrg.chaintracker.vis;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class PieChart {
	
	private static int CHART_PERCENT_FONT_SIZE = 9;
	
	private int x, y, diameter;
	private ArrayList<PieSlice> slices;
	private Rectangle area;
	
	public PieChart(int x, int y, int diameter, ArrayList<PieSlice> slices){
		setX(x);
		setY(y);
		setDiameter(diameter);
		setSlices(slices);
		Rectangle rec = new Rectangle(x, y, diameter, diameter);
		setArea(rec);
	}
	
	/**
     * Figures out the offset for placing the percentage labels on the pie charts
     */
    private Point calculateOffset(double angle, double hypotenuse){
    	double radiansAngle = Math.toRadians(angle);
    	int dx = (int) (Math.cos(radiansAngle)*(hypotenuse));
    	int dy = (int) (Math.sin(radiansAngle)*(hypotenuse));
    	
    	if (dy < 0){
			dy = dy * (-1);
		}
		if (dx < 0){
			dx = dx * (-1);
		}
		
		return new Point(dx, dy);
    }
    
    /**
     * draw the pie chart on graphics g
     */
    public void drawPie(Graphics2D g) {
        double total = 0.0D;
        for (int i = 0; i < slices.size(); i++) {
          total += slices.get(i).getValue();
        }

        double curValue = 0.0D;
        int startAngle = 0;
        for (int i = 0; i < slices.size(); i++) {
          startAngle = (int) (curValue * 360 / total);
          int arcAngle = (int) (slices.get(i).getValue() * 360 / total);

          g.setColor(slices.get(i).getColor());
          g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
          curValue += slices.get(i).getValue();
          
          //Draw percent label
          g.setColor(Color.BLACK);
          g.setFont(new Font("Ariel", Font.BOLD, CHART_PERCENT_FONT_SIZE));
          FontMetrics fm = g.getFontMetrics(new Font("Ariel", Font.BOLD, CHART_PERCENT_FONT_SIZE));
          
          String percent = (int) slices.get(i).getValue() + "%";
          double radius = area.height/2;
          double endAngle;
          if (startAngle == 0){
        	  endAngle = arcAngle;
          } else {
        	  endAngle = 360;
          }
          double midAngle = (endAngle - startAngle)/2;
          double midAngleSuplement = 180 - midAngle;
          double xCenter = area.x + radius - (fm.stringWidth(percent)/2);
          double yCenter = area.y + radius + (fm.getHeight()/3);
          Point offset;
         
          if (slices.get(i).getValue() == 100){
        	  g.drawString(percent, (int) Math.round(xCenter), (int) Math.round(yCenter));
          } else if (slices.get(i).getValue() == 0){
        	  //Do nothing
          } else if (slices.get(i).getValue() == 50){
        	  if (startAngle == 0){
        		  g.drawString(percent, (int) Math.round(xCenter), (int) Math.round(yCenter - (radius/2)));
        	  } else {
        		  g.drawString(percent, (int) Math.round(xCenter), (int) Math.round(yCenter + (radius/2)));
        	  }
          } else if (slices.get(i).getValue() >= 25){
        	  if (slices.get(i).getValue() > 50){
        		  offset = calculateOffset(midAngleSuplement, radius/2);
        		  if (startAngle == 0){
        			  g.drawString(percent, (int) Math.round(xCenter - offset.x), (int) Math.round(yCenter - offset.y));
        		  } else {
        			  g.drawString(percent, (int) Math.round(xCenter - offset.x), (int) Math.round(yCenter + offset.y));
        		  }
        	  } else {
        		  offset = calculateOffset(midAngle, radius/2);
        		  if (startAngle == 0){
        			  g.drawString(percent, (int) Math.round(xCenter + offset.x), (int) Math.round(yCenter - offset.y));
        		  } else {
        			  g.drawString(percent, (int) Math.round(xCenter + offset.x), (int) Math.round(yCenter + offset.y));
        		  }
        	  }
        	  
          } else {
        	//Less than 25%
        	  offset = calculateOffset(midAngle, 3*(radius/2));
        	  if (startAngle == 0){
        		  g.drawString(percent, (int) Math.round(xCenter + offset.x), (int) Math.round(yCenter - offset.y));
    		  } else {
    			  g.drawString(percent, (int) Math.round(xCenter + offset.x), (int) Math.round(yCenter + offset.y));
    		  }
          }
      }
    }

	public ArrayList<PieSlice> getSlices() {
		return slices;
	}

	public void setSlices(ArrayList<PieSlice> slices) {
		this.slices = slices;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDiameter() {
		return diameter;
	}

	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}

	public Rectangle getArea() {
		return area;
	}

	public void setArea(Rectangle area) {
		this.area = area;
	}

}
