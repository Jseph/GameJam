import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
public class Surface 
{
	Point2D start;
	Point2D end;
	Line2D lineRep;
	boolean hydrophobic;
	public Surface(Point2D start, Point2D end, boolean hydrophobic)
	{
		this.start = start;
		this.end = end;
		this.hydrophobic = hydrophobic;
		this.lineRep = new Line2D.Double(start,end);
	}
	public void drawSelf(Graphics2D myGraphics, DrawingState ds) 
	{
		Point2D ms = ds.mapPoint(start);
		Point2D me = ds.mapPoint(end);
		//Draw the active part of the surface, for now it has a width of 3 px...
		//Don't think that's going to end up mattering.
		int length = (int) ms.distance(me);
		myGraphics.translate((int)ms.getX(), (int)ms.getY());
		double theta = Math.atan2(me.getY()-ms.getY(), me.getX()-ms.getX());
		myGraphics.rotate(theta);
		//Draw the line, color represents the type of surface for now
		//The grey represents the inactive surface.
		//The actual surface is along the interface of the color;
		if(hydrophobic)
			myGraphics.setColor(Color.red);
		else
			myGraphics.setColor(Color.green);
		myGraphics.fillRect(0, 0, length, 3);
		myGraphics.setColor(Color.gray);
		myGraphics.fillRect(0, 3, length, 3);
		myGraphics.rotate(-theta);
		myGraphics.translate(-(int)ms.getX(), -(int)ms.getY());
	}
	//returns orientation in units consistant with the rest of this
	//terrible unit system. This corresponds to the orientation of the
	//blob if it is sitting on this object
	public double getOrientation()
	{
		return  Math.atan2(-end.getY()+start.getY(), end.getX()-start.getX());
	}
}
