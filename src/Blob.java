import java.awt.geom.Point2D;


public class Blob
{
	public double orientation;//0 is vertical (on top of a horizontal plane)
	public double aspectratio;
	public double unstressedsize;
	public Point2D center;
	public double VelocityX;
	public double VelocityY;
	public Blob(double size, Point2D location)
	{
		orientation = 2*Math.PI;
		unstressedsize = size;
		center = location;
		aspectratio = 1;
		VelocityX = 0;
		VelocityY = 0;
	}
	public double TangentalVelocity()
	{
		return VelocityX*Math.cos
	}
}
