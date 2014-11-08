import java.awt.geom.Point2D;


public class Blob
{
	public double orientation;//0 is vertical (on top of a horizontal plane)
	public double aspectratio;//really, this is strain.  it is (unstressed height / current height) and (stressed width / unstressed width)
	public double unstressedsize;//diameter
	public Surface stuckSurface;
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
		return VelocityX*Math.cos(orientation)+VelocityY*Math.sin(orientation);
	}
	public double NormalVelocity()
	{
		return VelocityX*Math.sin(orientation)-VelocityY*Math.cos(orientation);
	}
}