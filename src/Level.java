import java.util.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;

public class Level 
{
	ArrayList<Surface> surfaces;
	ArrayList<Actor> actors;
	Point2D startPoint;
	Rectangle2D endBox;
	PhysicsEngine pe;
	public Level(int number)
	{
		String levelPath = "level"+number+".dat";
		surfaces = new ArrayList<Surface>();
		actors = new ArrayList<Actor>();
		try
		{
			Scanner scan = new Scanner(new File(levelPath));
			double startX = scan.nextDouble();
			double startY = scan.nextDouble();
			startPoint = new Point2D.Double(startX,startY);
			double endX1 = scan.nextDouble();
			double endY1 = scan.nextDouble();
			double endX2 = scan.nextDouble();
			double endY2 = scan.nextDouble();
			endBox = new Rectangle2D.Double(endX1, endY1, endX2, endY2);
			int numSurfaces = scan.nextInt();
			for(int i=0; i<numSurfaces; i++)
			{
				double sx = scan.nextDouble();
				double sy = scan.nextDouble();
				double ex = scan.nextDouble();
				double ey = scan.nextDouble();
				surfaces.add(new Surface(new Point2D.Double(sx, sy), new Point2D.Double(ex, ey), scan.nextBoolean()));
			}
			int numActors = scan.nextInt();
			for(int i=0; i<numActors; i++)
				Actor.getActor(scan);
		}catch (Exception e){System.out.println("Something went wrong reading input file "+levelPath+ "\n" + e);}
	}
}
