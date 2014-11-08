import java.awt.geom.Point2D;


public class Blob
{
	public double orientation;
	public double width;
	public double height;
	public double unstressedsize;
	public Point2D center;
	public double VelocityX;
	public double VelocityY;
	public Blob(double size, Point2D location)
	{
		orientation = 2*Math.PI;
		width = size;
		height = size;
		unstressedsize = size;
		center = location;
		VelocityX = 0;
		VelocityY = 0;
	}
}
