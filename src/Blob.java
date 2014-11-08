import java.awt.geom.Point2D;


public class Blob
{
	public double orientation;
	public double width;
	public double height;
	public double unstressedsize;
	public Point2D center;
	
	public Blob(double size, Point2D location)
	{
		orientation = 2*Math.PI;
		width = size;
		height = size;
		unstressedsize = size;
		center = location;
	}
}
