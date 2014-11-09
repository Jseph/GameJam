import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.ArrayList;

public abstract class FilledObject 
{
	BufferedImage bi;
	Path2D p;
	double zoomLevel;
	public BufferedImage getImage()
	{
		if (bi!= null) return bi;
		bi = new BufferedImage((int)(p.getBounds2D().getWidth()*zoomLevel), (int)(p.getBounds2D().getHeight()*zoomLevel), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		PathIterator pi = p.getPathIterator(AffineTransform.getTranslateInstance(-p.getBounds().getX(), -p.getBounds2D().getY()));
		ArrayList<Point2D> pts = new ArrayList<Point2D>();
		double[] ipts = new double[2];
		while(!pi.isDone())
		{
			pi.currentSegment(ipts);
			pts.add(new Point2D.Double(ipts[0]*zoomLevel,ipts[1]*zoomLevel));
		}
		int [] xs = new int[pts.size()-2];
		int [] ys = new int[pts.size()-2];
		
		//Polygon pShifted = new Polygon(arg0, arg1, arg2)
		
		
		return bi;
	}
	public abstract Paint getFill();
}
