import java.awt.geom.Point2D;

public class DrawingState 
{
	int width;
	int height;
	double zoomLevel;
	//Maybe topLeft should be implemented as a point...
	double topLeftX;
	double topLeftY;
	public DrawingState()
	{
		width = 800;
		height = 600;
		zoomLevel = 1.0;
		topLeftX = 0;
		topLeftY = 0;
	}
	public DrawingState(int w, int h, double z, double tlx, double tly)
	{
		width = w;
		height = h;
		zoomLevel = z;
		topLeftX = tlx;
		topLeftY = tly;
	}
	public Point2D mapPoint(Point2D from)//This is in pixels
	{
		double fx = from.getX() - topLeftX;
		double fy = from.getY() - topLeftY;
		return new Point2D.Double(fx*zoomLevel, fy*zoomLevel);
	}
	public Point2D getBottomRight()//This is in the normalized coordinate system
	{
		return new Point2D.Double(topLeftX + width/zoomLevel, topLeftY + height/zoomLevel);
	}
	//This will ensure that this point is within the middle 50% of the x and y directions
	//Something should be added to keep this from going beyond the edge of the level...
	public void keepInMiddleBox(Point2D center)
	{
		Point2D br = getBottomRight();
		double lx = topLeftX;
		double rx = br.getX();
		double ty = topLeftY;
		double by = br.getY();
		if(lx + (rx-lx)/4 > center.getX())
			topLeftX = center.getX() - (rx-lx)/4;
		if(lx + (rx-lx)*3/4 < center.getX())
			topLeftX = center.getX() + (rx-lx)/4;
		if(ty + (by - ty)/4 > center.getY());
			topLeftY = center.getY() - (by-ty)/4;
		if(ty + (by-ty)*3/4 < center.getY())
			topLeftY = center.getY() + (by-ty)/4;
	}
}
