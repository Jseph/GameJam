import java.awt.geom.Point2D;
import java.util.*;
public class Surface 
{
	Point2D start;
	Point2D end;
	boolean hydrophobic;
	public Surface(Point2D start, Point2D end, boolean hydrophobic)
	{
		this.start = start;
		this.end = end;
		this.hydrophobic = hydrophobic;
	}
}
