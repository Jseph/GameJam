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
		return VelocityX*Math.cos(orientation)-VelocityY*Math.sin(orientation);
	}
	//This is defined as velocity toward the surface.
	public double NormalVelocity()
	{
		return -VelocityX*Math.sin(orientation)-VelocityY*Math.cos(orientation);
	}
	public void setVelocityTanNorm(double tan, double norm)
	{
		VelocityX = tan*Math.cos(orientation) - norm*Math.sin(orientation);
		VelocityY = -tan*Math.sin(orientation) - norm*Math.cos(orientation);
	}
	public void freeFromSurface()
	{
		aspectratio = 1;
		stuckSurface = null;
		setVelocityTanNorm(TangentalVelocity(), NormalVelocity()*0.8);
	}
	public void move(double dt)
	{
		center.setLocation(center.getX()+dt*VelocityX, center.getY()+dt*VelocityY);
	}
}