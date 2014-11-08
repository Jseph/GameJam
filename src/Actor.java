import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Scanner;


public abstract  class Actor 
{
	public abstract Polygon getHazardBounds();
	public abstract void drawSelf(Graphics2D a, DrawingState b);
	public abstract void updatePosition();
	public static Actor getActor(Scanner scan)
	{
		String type = scan.next();
		switch(type)
		{
		
		}
		return null;
	}
}
